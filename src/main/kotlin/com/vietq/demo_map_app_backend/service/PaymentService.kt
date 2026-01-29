package com.vietq.demo_map_app_backend.service

import UpsertPaymentDto
import PaymentCallbackIpnDto
import PaymentCallbackUrlDto
import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderPaymentstatus
import com.vietq.demo_map_app_backend.component.CallApiComponent
import com.vietq.demo_map_app_backend.config.EpayConfig
import com.vietq.demo_map_app_backend.dto.CreateOrderDto
import com.vietq.demo_map_app_backend.dto.CreateOrderDeliveryDto
import com.vietq.demo_map_app_backend.dto.CreateOrderResponseDto
import com.vietq.demo_map_app_backend.mapper.PaymentMapper
import com.vietq.demo_map_app_backend.repository.PaymentRepository
import com.vietq.demo_map_app_backend.utils.decrypt3DES
import com.vietq.demo_map_app_backend.utils.sha256
import org.apache.coyote.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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
    private val callApiComponent: CallApiComponent

) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    companion object {
        const val WINDOW_COLOR = "#1A1819"
        const val DESCRIPTION_PAYMENT = "Pay for order"
        const val LANGUAGE = "vn"
        const val PAY_TYPE_DEFAULT = "NO"
        const val CURRENCY = "VND"
    }


    fun createOrder(
        dto: CreateOrderDto,
        clientIp: String
    ): CreateOrderResponseDto {
        val body = mutableMapOf<String, String>()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val timeStamp = System.currentTimeMillis().toString()

        // get info user
        val userInfo = userService.getUser(dto.userId) ?: throw BadRequestException("User not found")
        val userDeliveryInfo = CreateOrderDeliveryDto(
            shipperId = dto.userId,  // hardcode shipper
            ordererName = userInfo.name,
            ordererPhone = userInfo.phone,
            ordererAddress = userInfo.address,
        )

        val uniqueHardCodeNumber = System.currentTimeMillis()
        val orderCode = "ORDVIETQ00$uniqueHardCodeNumber" // hardcode
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

        val linkExptime = ZonedDateTime
            .now(ZoneId.of("Asia/Ho_Chi_Minh"))  // ! pay attention
            .plusMinutes(15).format(formatter) // Expired in 15 minutes
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

        // DECRYPTED 'paymentLink' FROM EXTERNAL API
        val decryptedPaymentLink = res?.paymentLink
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

    private fun updateOrderStatus(invoiceNo: String, resultCd: String, status: String) {
        if (resultCd == "00_000" && status == "0") {
            orderService.changePaymentStatusOrder(
                invoiceNo,
                OrderPaymentstatus.PAYMENT_SUCCESS,
                OrderOrderstatus.APPROVED
            )
        } else {
            orderService.changePaymentStatusOrder(
                invoiceNo,
                OrderPaymentstatus.PAYMENT_FAIL,
                OrderOrderstatus.REFUSE
            )
        }
    }

    // ONLY CASE USER FINISH ALL STEPS AND REDIRECT (FAIL & SUCCESS)
    fun callbackUrl(dto: PaymentCallbackUrlDto) {
        log.info("EPAY_URL_RECEIVED: dto={}", dto)

        // !!! Nhớ compare merchantToken trước khi thực thi những logic khác sau này
        val body = UpsertPaymentDto(
            orderCode = dto.invoiceNo,
            merTrxId = dto.merTrxId,
            trxId = dto.trxId
        )
        paymentRepository.upsertPayment(body)

        // UPDATE STATUS ORDER
        updateOrderStatus(dto.invoiceNo, dto.resultCd, dto.status)
    }

    // ONLY CASE USER FINISH ALL STEPS AND REDIRECT (ONLY SUCCESS)
    fun callbackIpn(dto: PaymentCallbackIpnDto) {
        log.info("EPAY_IPN_RECEIVED: dto={}", dto)

        // !!! Nhớ compare merchantToken trước khi thực thi những logic khác sau này

        val body = UpsertPaymentDto(
            orderCode = dto.invoiceNo,
            merTrxId = dto.merTrxId,
            trxId = dto.trxId
        )
        paymentRepository.upsertPayment(body)


        // UPDATE STATUS ORDER
        updateOrderStatus(dto.invoiceNo, dto.resultCd, dto.status)
    }

    fun checkPayment(invoiceNo: String): OrderPaymentstatus {
        // STATUS DEFAULT
        var paymentStatus = OrderPaymentstatus.NOT_YET

        // CHECKING ORDERCODE
        orderService.getOrderByCode(invoiceNo) ?: throw BadRequestException("Order not found")
        val timeStamp = System.currentTimeMillis().toString()
        val merchantTokenInvoiceRaw = timeStamp + invoiceNo + epayConfig.merId + epayConfig.encodeKey
        val merchantTokenInvoice = sha256(merchantTokenInvoiceRaw)

        //  CALL API EPAY TO GET 'merTrxId'
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
            responseType = PaymentCheckStatusResponseDto::class.java
        ) ?: throw BadRequestException("Call external API to get 'merTrxId' by invoice failed")

        log.info(" responseByInvoice ---> {}", responseByInvoice)

        val dataByInvoice =
            responseByInvoice.data ?: throw BadRequestException("Call external API to get 'merTrxId' by invoice failed")

        // DONT HAVE 'merTrxId' MEANS THE USER HAVE PAID NOT YET
        if(dataByInvoice.merTrxId == "-1"){
            // NOT YET
            paymentStatus = OrderPaymentstatus.NOT_YET
            orderService.changePaymentStatusOrder(
                invoiceNo,
                paymentStatus,
                OrderOrderstatus.WAITING
            )
            return paymentStatus
        }

        // CALL API EPAY TO CHECK TRANSACTION STATUS
        val merchantTokenTransactionRaw = timeStamp + dataByInvoice.merTrxId + epayConfig.merId + epayConfig.encodeKey
        val merchantTokenTransaction = sha256(merchantTokenTransactionRaw)
        val bodyByTransaction = LinkedMultiValueMap<String, String>().apply {
            add("merId", epayConfig.merId)
            add("merTrxId", dataByInvoice.merTrxId)
            add("merchantToken", merchantTokenTransaction)
            add("timeStamp", timeStamp)
        }
        log.info(" bodyByMer --- merTrxId={}", dataByInvoice.merTrxId)

        val responseByTransaction = callApiComponent.postForm(
            url = epayConfig.inquiryStatus,
            body = bodyByTransaction,
            responseType = PaymentCheckStatusResponseDto::class.java
        ) ?: throw BadRequestException("Call external API to checking transaction status by 'merTrxId' failed")

        log.info(" responseByTransaction ---> {}", responseByTransaction)


        val dataByTransaction = responseByTransaction.data
            ?: throw BadRequestException("Call external API to checking transaction status by 'merTrxId' failed")


        when {
            dataByTransaction.resultCd == "00_000" && dataByTransaction.status >= "0" -> {
                // SUCCESS
                paymentStatus = OrderPaymentstatus.PAYMENT_SUCCESS
                orderService.changePaymentStatusOrder(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.APPROVED
                )
            }

            dataByTransaction.resultCd == "99" -> {
                // PENDING
                paymentStatus = OrderPaymentstatus.PENDING
                orderService.changePaymentStatusOrder(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.WAITING
                )

            }
            dataByTransaction.resultCd == "OR_140" -> {
                // NOT YET
                paymentStatus = OrderPaymentstatus.NOT_YET
                orderService.changePaymentStatusOrder(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.WAITING
                )

            }

            else -> {
                // FAIL
                paymentStatus = OrderPaymentstatus.PAYMENT_FAIL
                orderService.changePaymentStatusOrder(
                    invoiceNo,
                    OrderPaymentstatus.PAYMENT_FAIL,
                    OrderOrderstatus.REFUSE
                )
            }
        }
        return paymentStatus
    }

}
