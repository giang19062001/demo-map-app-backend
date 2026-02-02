package com.vietq.demo_map_app_backend.service

import PaymentCallbackDto
import PaymentCheckInvoiceDataResponseDto
import PaymentCheckInvoiceResponseDto
import PaymentCheckTransactionDataResponseDto
import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderPaymentstatus
import com.vietq.demo_map_app_backend.component.CallApiComponent
import com.vietq.demo_map_app_backend.config.EpayConfig
import com.vietq.demo_map_app_backend.config.TimezoneConfig
import com.vietq.demo_map_app_backend.dto.CreateOrderDto
import com.vietq.demo_map_app_backend.dto.OrderDeliveryInfoDto
import com.vietq.demo_map_app_backend.dto.CreateOrderResponseDto
import com.vietq.demo_map_app_backend.mapper.PaymentMapper
import com.vietq.demo_map_app_backend.repository.PaymentRepository
import com.vietq.demo_map_app_backend.utils.EpayMerchantCodeEnum
import com.vietq.demo_map_app_backend.utils.EpayTransactionResultCodeEnum
import com.vietq.demo_map_app_backend.utils.EpayTransactionStatuEnum
import com.vietq.demo_map_app_backend.utils.decrypt3DES
import com.vietq.demo_map_app_backend.utils.isSha256Equal
import com.vietq.demo_map_app_backend.utils.sha256
import org.apache.coyote.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.util.LinkedMultiValueMap
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.text.isNullOrBlank

@Service
class PaymentService(
    private val epayConfig: EpayConfig,
    private val orderService: OrderService,
    private val paymentRepository: PaymentRepository,
    private val paymentMapper: PaymentMapper,
    private val userService: UserService,
    private val timezoneConfig: TimezoneConfig,
    private val callApiComponent: CallApiComponent

) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    companion object {
        const val WINDOW_COLOR = "#1A1819"
        const val DESCRIPTION_PAYMENT = "Pay for order"
        const val LANGUAGE = "EN"
        const val PAY_TYPE_DEFAULT = "NO"
        const val CURRENCY = "VND"
        const val EXPIRED_TIME = 15L // Expired in 15 minutes
    }

    // GET URL AND QRCODE OF EPAY WEBVIEW
    fun createOrder(
        dto: CreateOrderDto,
        clientIp: String
    ): CreateOrderResponseDto {
        val body = mutableMapOf<String, String>()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val timeStamp = System.currentTimeMillis().toString()

        // GET INFO USER
        val userInfo = userService.getUser(dto.userId) ?: throw BadRequestException("User not found")
        val userDeliveryInfo = OrderDeliveryInfoDto(
            shipperId = dto.userId,  // hardcode shipper
            ordererName = userInfo.name,
            ordererPhone = userInfo.phone,
            ordererAddress = userInfo.address,
        )

        val uniqueHardCodeNumber = System.currentTimeMillis()
        val orderCode = "ORD00$uniqueHardCodeNumber" // hardcode
        body["merId"] = epayConfig.merId
        body["amount"] = dto.amount.toString()
        body["currency"] = CURRENCY
        body["goodsNm"] = "cartData-${orderCode}"
        body["invoiceNo"] = orderCode
        body["payType"] = PAY_TYPE_DEFAULT
        body["payOption"] = ""
        body["bankCode"] = ""
        body["buyerFirstNm"] = userInfo.name
        body["buyerLastNm"] = userInfo.name
        body["buyerEmail"] = userInfo.email
        body["callBackUrl"] = epayConfig.callBackUrl
        body["notiUrl"] = epayConfig.notiUrl
        body["reqDomain"] = epayConfig.reqDomain
        body["descriptions"] = DESCRIPTION_PAYMENT // ! require

        val linkExptime =
            ZonedDateTime.now(ZoneId.of(timezoneConfig.timezone)).plusMinutes(EXPIRED_TIME).format(formatter)
        val merchantTokenRaw =
            timeStamp + orderCode + epayConfig.merId + dto.amount.toString() + linkExptime + epayConfig.encodeKey
        val merchantToken = sha256(merchantTokenRaw)
        val hashRaw = orderCode + dto.amount.toString() + epayConfig.encodeKey
        val hash = sha256(hashRaw)


        body["merchantToken"] = merchantToken
        body["userIP"] = clientIp
        body["userLanguage"] = LANGUAGE
        body["timeStamp"] = timeStamp
        body["linkExptime"] = linkExptime
        body["windowColor"] = WINDOW_COLOR
        body["hash"] = hash

        val createLinkUrl = epayConfig.createLinkUrl
        log.info("epayConfig.createLinkUrl --- body={}", body)

        // CALL API EPAY
        val res = callApiComponent.postJson(
            url = createLinkUrl,
            body = body,
            responseType = PaymentCreateLinkResponseDto::class.java
        )
        log.info("epayConfig.createLinkUrl --- res={}", res)

        if (res == null || res.resultCd != EpayTransactionResultCodeEnum.SUCCESS.code) {
            throw BadRequestException("Orders cannot be executed at the moment, please try again.")
        }

        // DECRYPTED 'paymentLink' FROM EXTERNAL API
        val decryptedPaymentLink = res.paymentLink
            ?.takeIf { it.isNotBlank() }
            ?.let { decrypt3DES(it, epayConfig.encodeKey) }
            ?: ""

        // INSERT ORDER
        val insertId = orderService.insertOrder(dto, orderCode, userDeliveryInfo)
        log.info("Write Order with insertId={}", insertId)

        // RETURN
        return paymentMapper.toCreateLinkResponse(
            res = res,
            decryptedPaymentLink = decryptedPaymentLink
        )

    }

    // ONLY CASE USER FINISH ALL STEPS OF PAYMENT (FAIL & SUCCESS)
    fun callbackUrl(dto: PaymentCallbackDto) {
        // GET INFO FROM DB
        val paymentInfo = paymentRepository.getPaymentByOrderCode(dto.invoiceNo)
        // ONLY UPDATE STATUS WHEN 'paymentInfo' HAVENOT INSERTED YET or (STATUS ON DATABASE IS FAILED BUT EPAY IS SUCCESS)
        if (paymentInfo == null || (EpayTransactionResultCodeEnum.isFailed(paymentInfo.resultCd) && EpayTransactionResultCodeEnum.isSuccess(
                dto.resultCd
            ))
        ) {
            handleCallback(dto, "REDIRECT_URL")
        }
    }

    // ONLY CASE USER FINISH ALL STEPS OF PAYMENT (ONLY SUCCESS) -- EPAY JUST CALLBACK THIS API ONLY IN CASE OF TRANSACTION SUCCESSFULLY
    fun callbackIpn(dto: PaymentCallbackDto) {
        // GET INFO FROM DB
        val paymentInfo = paymentRepository.getPaymentByOrderCode(dto.invoiceNo)
        // ONLY UPDATE STATUS WHEN 'paymentInfo' HAVENOT INSERTED YET or (STATUS ON DATABASE IS FAILED BUT EPAY IS SUCCESS)
        if (paymentInfo == null || (EpayTransactionResultCodeEnum.isFailed(paymentInfo.resultCd) && EpayTransactionResultCodeEnum.isSuccess(
                dto.resultCd
            ))
        ) {
            handleCallback(dto, "IPN")
        }
    }

    @Transactional
    fun checkPayment(invoiceNo: String): Pair<OrderPaymentstatus, String> {
        // GET INFO FROM DB
        val paymentInfo = paymentRepository.getPaymentByOrderCode(invoiceNo)

        // STATUS DEFAULT
        var paymentStatus =
            if (paymentInfo != null) EpayTransactionResultCodeEnum.toPaymentStatus(paymentInfo.resultCd) else OrderPaymentstatus.NOT_YET
        var resultMsg = paymentInfo?.resultMsg ?: ""


        // CHECKING ORDERCODE
        orderService.getOrderByCode(invoiceNo) ?: throw BadRequestException("Order not found")
        val timeStamp = System.currentTimeMillis().toString()
        val merchantTokenInvoiceRaw = timeStamp + invoiceNo + epayConfig.merId + epayConfig.encodeKey
        val merchantTokenInvoice = sha256(merchantTokenInvoiceRaw)

        //  CALL API EPAY TO GET 'merTrxId'
        val dataByInvoice = getPropertyForTransaction(invoiceNo, merchantTokenInvoice, timeStamp)

        // !!!! DONT HAVE 'merTrxId' MEANS THE TRANSACTION NOT EXISTS
        if (EpayMerchantCodeEnum.isFailed(dataByInvoice.merTrxId)) {
            // NOT YET
            paymentStatus = OrderPaymentstatus.NOT_YET
            resultMsg = EpayMerchantCodeEnum.MERCHANT_FAIL.description
            orderService.changePaymentStatusOrder(
                invoiceNo,
                paymentStatus,
                OrderOrderstatus.WAITING
            )
            return Pair(paymentStatus, resultMsg)
        }

        // CALL API EPAY TO CHECK TRANSACTION STATUS
        val dataByTransaction = getTransactionStatus(dataByInvoice.merTrxId, timeStamp)
        // ONLY UPDATE STATUS WHEN 'paymentInfo' HAVENOT INSERTED YET or (STATUS ON DATABASE IS FAILED BUT EPAY IS SUCCESS)
        if (paymentInfo == null || (EpayTransactionResultCodeEnum.isFailed(paymentInfo.resultCd) && EpayTransactionResultCodeEnum.isSuccess(dataByTransaction.resultCd))) {
            // UPDATE/INSERT PAYMENT
            val body = paymentMapper.toUpsertPaymentDto(dataByTransaction)
            paymentRepository.upsertPayment(body)

            // CHECK CONDITION TO UPDATE ORDER
            paymentStatus = updateOrderPaymentStatus(invoiceNo, dataByTransaction.resultCd, dataByTransaction.status)
            return Pair(paymentStatus, dataByTransaction.resultMsg)
        } else {
            // IF THERE ARE AVAILABLE DATA -> RETURN DIRECTLY
            return Pair(paymentStatus, resultMsg)
        }
    }

    // PRIVATE FUNCTION
    private fun handleCallback(dto: PaymentCallbackDto, nameCallback: String) {
        log.info("EPAY_CALLBACK_RECEIVED_{}: dto={}", nameCallback, dto)

        if (dto.merchantToken.isBlank() || dto.merTrxId.isBlank()) {
            // CANNOT VERIFY TOKEN || CANNOT GET FINAL TRANSACTION STAUTS (MISSING 'merTrxId')  -> FORCE FAILED
            paymentRepository.upsertPayment(paymentMapper.toUpsertPaymentDto(dto))
            updateOrderPaymentStatus(dto.invoiceNo, dto.resultCd, dto.status)
        } else {
            // Verify token only for valid transaction
            val (dataDto, isSuccess) = if (!verifyMerchantToken(dto)) {
                // CALL API EPAY TO CHECK FINAL TRANSACTION STATUS
                getTransactionStatus(dto.merTrxId, dto.timeStamp).let {
                    it to EpayTransactionResultCodeEnum.isSuccess(it.resultCd)
                }
            } else {
                dto to EpayTransactionResultCodeEnum.isSuccess(dto.resultCd)
            }

            // UPSERT PAYMENT
            // UPDATE ORDER STATUS
            when (dataDto) {
                is PaymentCallbackDto -> {
                    paymentRepository.upsertPayment(paymentMapper.toUpsertPaymentDto(dataDto))
                    updateOrderPaymentStatus(dataDto.invoiceNo, dataDto.resultCd, dataDto.status)
                }

                is PaymentCheckTransactionDataResponseDto -> {
                    paymentRepository.upsertPayment(paymentMapper.toUpsertPaymentDto(dataDto))
                    updateOrderPaymentStatus(dataDto.invoiceNo, dataDto.resultCd, dataDto.status)
                }

                else -> throw IllegalStateException("Unknown data type")
            }
        }

    }

    //  CALL API EPAY TO GET 'merTrxId'
    private fun getPropertyForTransaction(
        invoiceNo: String,
        merchantTokenInvoice: String,
        timeStamp: String
    ): PaymentCheckInvoiceDataResponseDto {
        // 'merTrxId' can be generated again each time the user clicks or scans the code one more
        val bodyByInvoice = LinkedMultiValueMap<String, String>().apply {
            add("merId", epayConfig.merId)
            add("invoiceNo", invoiceNo)
            add("merchantToken", merchantTokenInvoice)
            add("timeStamp", timeStamp)
        }
        log.info(" bodyByInvoice --- invoiceNo={}", invoiceNo)

        val responseByInvoice = callApiComponent.postForm(
            url = epayConfig.inquiryNoStatus,
            body = bodyByInvoice,
            responseType = PaymentCheckInvoiceResponseDto::class.java
        ) ?: throw BadRequestException("Call external API to get 'merTrxId' by invoice failed")

        log.info(" responseByInvoice ---> {}", responseByInvoice)

        val dataByInvoice =
            responseByInvoice.data ?: throw BadRequestException("Call external API to get 'merTrxId' by invoice failed")

        return dataByInvoice
    }

    // CALL API EPAY TO CHECK TRANSACTION STATUS
    private fun getTransactionStatus(merTrxId: String, timeStamp: String): PaymentCheckTransactionDataResponseDto {
        val merchantTokenTransactionRaw = timeStamp + merTrxId + epayConfig.merId + epayConfig.encodeKey
        val merchantTokenTransaction = sha256(merchantTokenTransactionRaw)
        val bodyByTransaction = LinkedMultiValueMap<String, String>().apply {
            add("merId", epayConfig.merId)
            add("merTrxId", merTrxId)
            add("merchantToken", merchantTokenTransaction)
            add("timeStamp", timeStamp)
        }
        log.info(" bodyByMer --- merTrxId={}", merTrxId)

        val responseByTransaction = callApiComponent.postForm(
            url = epayConfig.inquiryStatus,
            body = bodyByTransaction,
            responseType = PaymentCheckTransactionResponseDto::class.java
        ) ?: throw BadRequestException("Call external API to checking transaction status by 'merTrxId' failed")

        log.info(" responseByTransaction ---> {}", responseByTransaction)

        val dataByTransaction = responseByTransaction.data
            ?: throw BadRequestException("Call external API to checking transaction status by 'merTrxId' failed")

        return dataByTransaction
    }

    // UPDATE PAYMENT_STATUS ON DB
    private fun updateOrderPaymentStatus(invoiceNo: String, resultCd: String, status: String): OrderPaymentstatus {
        var paymentStatus = OrderPaymentstatus.NOT_YET

        when {
            // SUCCESS
            EpayTransactionResultCodeEnum.isSuccess(resultCd) && EpayTransactionStatuEnum.isSuccess(status) -> {
                paymentStatus = OrderPaymentstatus.PAYMENT_SUCCESS
                orderService.changePaymentStatusOrder(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.APPROVED
                )
            }

            // PENDING
            EpayTransactionResultCodeEnum.isPending(resultCd) -> {
                paymentStatus = OrderPaymentstatus.PENDING
                orderService.changePaymentStatusOrder(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.WAITING
                )
            }

            // NOT YET
            EpayTransactionResultCodeEnum.isNotYet(resultCd) -> {
                paymentStatus = OrderPaymentstatus.NOT_YET

                orderService.changePaymentStatusOrder(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.WAITING
                )
            }

            // FAIL
            else -> {
                paymentStatus = OrderPaymentstatus.PAYMENT_FAIL
                orderService.changePaymentStatusOrder(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.REFUSE
                )
            }
        }
        return paymentStatus
    }

    // COMMON VERIFY LOGIC FOR CALLBACK & IPN
    private fun verifyMerchantToken(dto: PaymentCallbackDto): Boolean {
        // !!! VERIFY merchantToken
        val merchantTokenCallbackRaw =
            dto.resultCd + dto.timeStamp +
                    dto.merTrxId + dto.trxId +
                    dto.merId + dto.amount +
                    epayConfig.encodeKey
        val merchantTokenCallback = sha256(merchantTokenCallbackRaw)

        // COMPARE
        if (!isSha256Equal(merchantTokenCallback, dto.merchantToken)) {
            log.error(
                "VERIFY_MERCHANT_TOKEN_FAILED: invoiceNo={}, expected={}, received={}",
                dto.invoiceNo, merchantTokenCallback, dto.merchantToken
            )

            return false
        }

        log.info(
            "VERIFY_MERCHANT_TOKEN_SUCCESS: invoiceNo={}, token={}",
            dto.invoiceNo, merchantTokenCallback
        )
        return true
    }

}
