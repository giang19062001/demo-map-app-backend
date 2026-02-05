package com.vietq.demo_map_app_backend.service

import PaymentCallbackDto
import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderEpayPaytype
import com.study.jooq.enums.OrderPaymentstatus
import com.vietq.demo_map_app_backend.component.CallApiComponent
import com.vietq.demo_map_app_backend.config.EpayConfig
import com.vietq.demo_map_app_backend.config.TimezoneConfig
import com.vietq.demo_map_app_backend.dto.CreateOrderDto
import com.vietq.demo_map_app_backend.dto.OrderDeliveryInfoDto
import com.vietq.demo_map_app_backend.dto.CreateOrderResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentCancelResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentCreateLinkResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentEpayResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentGetMerchantDataResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentGetMerchantResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentGetTransactionDataResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentGetTransactionResponseDto
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
    private  val logbase = "PaymentService"

    companion object {
        const val WINDOW_COLOR = "#1A1819"
        const val DESCRIPTION_PAYMENT = "Pay for order"
        const val LANGUAGE = "EN" // VN, EN, KO
        const val PAY_TYPE_DEFAULT = "NO"
        const val CURRENCY = "VND"
        const val EXPIRED_TIME = 15L // Expired in 15 minutes
        const val CANCEL_MSG = "Testing"
        const val CALLBACK_REDIRECT = "REDIRECT_URL"
        const val CALLBACK_IPN = "IPM"

    }

    /**
     * The purpose is created transaction into Epay and insert transaction into Database
     * @return: Link and Qrcode for Epay webview
     */
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

        log.info("logbase={} --- getMerchantTransaction: body={}", logbase, body)
        val createLinkUrl = epayConfig.createLinkUrl
        // CALL API EPAY
        val res = callApiComponent.postJson(
            url = createLinkUrl,
            body = body,
            responseType = PaymentCreateLinkResponseDto::class.java
        )
        log.info("logbase={} --- createOrder: PaymentCreateLinkResponse={}", logbase, res)
        if (res == null || EpayTransactionResultCodeEnum.isFailed(res.resultCd)) {
            throw BadRequestException("Orders cannot be executed at the moment, please try again.")
        }

        // DECRYPTED 'paymentLink' FROM EXTERNAL API
        val decryptedPaymentLink = res.paymentLink
            ?.takeIf { it.isNotBlank() }
            ?.let { decrypt3DES(it, epayConfig.encodeKey) }
            ?: ""

        // INSERT ORDER
         orderService.insertOrder(dto, orderCode, userDeliveryInfo)

        // RETURN
        return paymentMapper.toCreateLinkResponse(
            res = res,
            decryptedPaymentLink = decryptedPaymentLink
        )

    }

    /**
     * Epay only call this API when transaction SUCCESS or FAILED (Required: The user must complete the steps until the webview closes automatically)
     */
    fun callbackUrl(dto: PaymentCallbackDto) {
        handleCallback(dto, CALLBACK_REDIRECT)
    }

    /**
     * Epay only call this API when transaction SUCCESS or REFUND
     */
    fun callbackIpn(dto: PaymentCallbackDto) {
        handleCallback(dto, CALLBACK_IPN)
    }

    /**
     * The purpose is compare transaction from Database with Epay, update it if the conditions are met (again) and then return exactly transaction information
     * @return: <PaymentStatus: enum(NOT_YET | PENDING | PAYMENT_SUCCESS | PAYMENT_FAIL), Message: String>
     */
    @Transactional
    fun checkPayment(invoiceNo: String): Pair<OrderPaymentstatus, String> {
        log.info("logbase={} --- checkPayment: invoiceNo={}", logbase, invoiceNo)

        // GET INFO FROM DB
        val paymentInfo = paymentRepository.getPaymentByOrderCode(invoiceNo)

        // STATUS DEFAULT
        var paymentStatus = if (paymentInfo != null) EpayTransactionResultCodeEnum.toPaymentStatus(paymentInfo.resultCd) else OrderPaymentstatus.NOT_YET
        var resultMsg = paymentInfo?.resultMsg ?: ""

        // CHECKING ORDERCODE
        orderService.getOrderByCode(invoiceNo) ?: throw BadRequestException("Order not found")
        val timeStamp = System.currentTimeMillis().toString()
        val merchantTokenInvoiceRaw = timeStamp + invoiceNo + epayConfig.merId + epayConfig.encodeKey
        val merchantTokenInvoice = sha256(merchantTokenInvoiceRaw)

        //  CALL API EPAY TO GET 'merTrxId'
        val dataByInvoice = getMerchantTransaction(invoiceNo, merchantTokenInvoice, timeStamp)

        // CHECKING MERCHANT ID
        val (fallbackPaymentStatus, fallbackResultMsg, isValid) = orderService.fallbackOrderMerchant(dataByInvoice.merTrxId, invoiceNo, paymentStatus, resultMsg)
        if (!isValid) {
            return Pair(fallbackPaymentStatus, fallbackResultMsg)
        }else{
            paymentStatus = fallbackPaymentStatus
            resultMsg = fallbackResultMsg
        }

        // CALL API EPAY TO CHECK TRANSACTION STATUS
        val dataByTransaction = getTransactionStatus(dataByInvoice.merTrxId)

       // CHECK CONDITION UPDATE
        if (shouldUpsertOrderPayment(paymentInfo, dataByTransaction)) {
            // UPDATE/INSERT PAYMENT
            val body = paymentMapper.toUpsertPaymentDto(dataByTransaction)
            paymentRepository.upsertPayment(body)

            // CHECK CONDITION TO UPDATE ORDER
            paymentStatus = orderService.updateOrderPaymentStatus(invoiceNo, dataByTransaction.resultCd, dataByTransaction.status)
            return Pair(paymentStatus, dataByTransaction.resultMsg)
        } else {
            // IF THERE ARE AVAILABLE DATA -> RETURN DIRECTLY
            return Pair(paymentStatus, resultMsg)
        }
    }

    /**
     * Check if payment should be upserted based on current DB state and Epay transaction data
     */
    private fun shouldUpsertOrderPayment(paymentInfo:  PaymentEpayResponseDto?, dataByTransaction: PaymentGetTransactionDataResponseDto
    ): Boolean {
        // UPSERT WHEN 'paymentInfo' HAVE NOT INSERTED YET
        // or 'RESULT_CODE ON DATABASE IS FAILED' BUT 'EPAY IS SUCCESS' -- User try to pay again
        // or REFUND
        log.info("logbase={} --- shouldUpsertOrderPayment: paymentInfo={}", logbase, paymentInfo)

        val result = paymentInfo == null
                || (EpayTransactionResultCodeEnum.isFailed(paymentInfo.resultCd) && EpayTransactionResultCodeEnum.isSuccess(dataByTransaction.resultCd))
                || (paymentInfo.remainAmount != dataByTransaction.remainAmount && EpayTransactionStatuEnum.isRefund(dataByTransaction.status))

        log.info("logbase={} --- shouldUpsertOrderPayment: result={}", logbase, result)
        return result
    }

    /**
     * Function use for callBackUrl and callBackIpn, it will compare transaction from Database with Epay, update it if the conditions are met
     */
    private fun handleCallback(dto: PaymentCallbackDto, nameCallback: String) {
        log.info("logbase={} --- handleCallback: nameCallback={},  dto={}", logbase, nameCallback, dto)

        when {
            // FORCE FAILED - nếu thiếu merchantToken hoặc merTrxId
            dto.merchantToken.isBlank() || dto.merTrxId.isBlank() -> {
                // UPSERT PAYMENT
                paymentRepository.upsertPayment(paymentMapper.toUpsertPaymentDto(dto))
                // UPDATE ORDER STATUS
                orderService.updateOrderPaymentStatus(dto.invoiceNo, dto.resultCd, dto.status)
            }

            // CHECK API AGAIN IF VERIFY FAILED
            !verifyMerchantToken(dto) -> {
                // GET INFO FROM DB
                val paymentInfo = paymentRepository.getPaymentByOrderCode(dto.invoiceNo)
                // CALL API EPAY TO CHECK FINAL TRANSACTION STATUS
                val dataByTransaction = getTransactionStatus(dto.merTrxId)
                // CHECK CONDITION UPDATE
                if (shouldUpsertOrderPayment(paymentInfo, dataByTransaction)) {
                    // UPSERT PAYMENT
                    paymentRepository.upsertPayment(paymentMapper.toUpsertPaymentDto(dataByTransaction))
                    // UPDATE ORDER STATUS
                    orderService.updateOrderPaymentStatus(dataByTransaction.invoiceNo, dataByTransaction.resultCd, dataByTransaction.status)
                }
            }

            // VERIFY SUCCESS
            else -> {
                // UPSERT PAYMENT
                paymentRepository.upsertPayment(paymentMapper.toUpsertPaymentDto(dto))
                // UPDATE ORDER STATUS
                orderService.updateOrderPaymentStatus(dto.invoiceNo, dto.resultCd, dto.status)
            }
        }
    }

    /**
     * The purpose is get info of Merchant ( Mainly: 'merTrxId')
     */
    private fun getMerchantTransaction(invoiceNo: String, merchantTokenInvoice: String, timeStamp: String): PaymentGetMerchantDataResponseDto {

        // 'merTrxId' can be generated again each time the user clicks or scans the code one more
        val bodyByInvoice = LinkedMultiValueMap<String, String>().apply {
            add("merId", epayConfig.merId)
            add("invoiceNo", invoiceNo)
            add("merchantToken", merchantTokenInvoice)
            add("timeStamp", timeStamp)
        }
        log.info("logbase={} --- getMerchantTransaction: bodyByInvoice={}", logbase, bodyByInvoice)

        val responseByInvoice = callApiComponent.postForm(
            url = epayConfig.inquiryNoStatusUrl,
            body = bodyByInvoice,
            responseType = PaymentGetMerchantResponseDto::class.java
        ) ?: throw BadRequestException("Call external API to get merchant failed")

        log.info("logbase={} --- getMerchantTransaction: responseByInvoice={}", logbase, responseByInvoice)

        val dataByInvoice =
            responseByInvoice.data ?: throw BadRequestException("Call external API to get merchant failed")

        return dataByInvoice
    }

    /**
     * The purpose is get last status of transaction
     */
    public fun getTransactionStatus(merTrxId: String): PaymentGetTransactionDataResponseDto {
        val timeStamp = System.currentTimeMillis().toString()
        val merchantTokenTransactionRaw = timeStamp + merTrxId + epayConfig.merId + epayConfig.encodeKey
        val merchantTokenTransaction = sha256(merchantTokenTransactionRaw)

        val bodyByTransaction = LinkedMultiValueMap<String, String>().apply {
            add("merId", epayConfig.merId)
            add("merTrxId", merTrxId)
            add("merchantToken", merchantTokenTransaction)
            add("timeStamp", timeStamp)
        }
        log.info("logbase={} --- getTransactionStatus: bodyByTransaction={}", logbase, bodyByTransaction)
        val responseByTransaction = callApiComponent.postForm(
            url = epayConfig.inquiryStatusUrl,
            body = bodyByTransaction,
            responseType = PaymentGetTransactionResponseDto::class.java
        ) ?: throw BadRequestException("Call external API to get transaction status failed")

        log.info("logbase={} --- getTransactionStatus: responseByTransaction={}", logbase, responseByTransaction)

        val dataByTransaction = responseByTransaction.data
            ?: throw BadRequestException("Call external API to get transaction status failed")

        return dataByTransaction
    }

    /**
     * The purpose is cancel and cancel partial transaction on Epay
     */
    public fun cancelTransaction(trxId: String, amount: String, cancelCode: String, payType: OrderEpayPaytype): PaymentCancelResponseDto {
        // HASH
        val timeStamp = System.currentTimeMillis().toString()
        val merchantTokenRaw = timeStamp + cancelCode + trxId + epayConfig.merId + amount + epayConfig.encodeKey
        val merchantToken = sha256(merchantTokenRaw)

        // BODY
        val bodyCancel = LinkedMultiValueMap<String, String>().apply {
            add("trxId", trxId)
            add("merId", epayConfig.merId)
            add("merTrxId", cancelCode)
            add("amount", amount)
            add("payType", payType.toString())
            add("cancelMsg", CANCEL_MSG)
            add("timeStamp", timeStamp)
            add("merchantToken", merchantToken)
            add("cancelPw", epayConfig.cancelPw)
        }
        log.info("logbase={} --- cancelTransaction: bodyCancel={}", logbase, bodyCancel)

        val responseCancel = callApiComponent.postForm(
            url = epayConfig.cancelUrl,
            body = bodyCancel,
            responseType = PaymentCancelResponseDto::class.java
        ) ?: throw BadRequestException("Call external API to cancel failed")

        log.info("logbase={} --- cancelTransaction: responseCancel={}", logbase, responseCancel)

        return responseCancel
    }

    /**
     * The purpose is Verify 'merchantToken' come from callbackUrl and callbackIpn
     * @return: true | false
     */
    private fun verifyMerchantToken(dto: PaymentCallbackDto): Boolean {
        // VERIFY merchantToken
        val merchantTokenCallbackRaw = dto.resultCd + dto.timeStamp + dto.merTrxId + dto.trxId + epayConfig.merId + dto.amount + epayConfig.encodeKey
        val merchantTokenCallback = sha256(merchantTokenCallbackRaw)

        // COMPARE
        if (!isSha256Equal(merchantTokenCallback, dto.merchantToken)) {
            log.info("logbase={} ---verifyMerchantToken(FAIL): invoiceNo={}, expected={}, received={}", logbase, dto.invoiceNo, merchantTokenCallback, dto.merchantToken)
            return false
        }
        log.info("logbase={} ---verifyMerchantToken(SUCCESS): invoiceNo={}, token{}", logbase, dto.invoiceNo, merchantTokenCallback)
        return true
    }

}
