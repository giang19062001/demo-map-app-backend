package com.vietq.demo_map_app_backend.controller

import PaymentCallbackDto
import com.study.jooq.enums.OrderPaymentstatus
import com.vietq.demo_map_app_backend.dto.CreateOrderDto
import com.vietq.demo_map_app_backend.dto.CreateOrderResponseDto
import com.vietq.demo_map_app_backend.service.PaymentService
import com.vietq.demo_map_app_backend.utils.SuccessResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.vietq.demo_map_app_backend.utils.toSuccessResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/api/payment")
class PaymentController(private val paymentService: PaymentService) {
    @Operation(
        summary = "API EPAY for creating Link and QRcode",
        description = """
        ================== Testing card list ==================

        1. ATM with card number
        --------------------------------------------------
        Bank           : NAPAS - SaigonBank
        Card number    : 9704 0000 0000 0018
        Tên chủ thẻ    : NGUYEN VAN A
        Ngày phát hành : 03/07
        OTP/CVV        : otp

        2. ATM with Account number
        --------------------------------------------------
        Bank                   : NAPAS - Any Bank
        Acount number          : 01040001
        Account name           : NGUYEN VAN A
        Identity Card/Passport : 01040001
        OTP/CVV                : otp

        3. Visa/Master/JCB/AMEX
        --------------------------------------------------
        ▸ VISA
        - 445653 00 0000 1005
        - 400000 00 0000 1000
        - 400000 00 0000 2701
        Name           : NGUYEN VAN A
        Expired        : 11/28
        CVV            : 123
        3DS Password   : 1234

        ▸ MASTER CARD
        - 520000 00 0000 1005
        - 520000 00 0000 2235
        Name           : NGUYEN VAN A
        Expired        : 11/28
        CVV            : 123
        3DS Password   : 1234

        ▸ JCB
        - 333700 00 0000 0008
        - 3550 9986 5013 1033
        - 333800 00 0000 0296
        Name           : NGUYEN VAN A
        Expired        : 11/28
        CVV            : 123
        3DS Password   : 1234

        ▸ AMERICAN EXPRESS (AMEX)
        - 340000 00 0001 007
        - 340000 00 0002 708
        Name           : NGUYEN VAN A
        Expired        : 11/28
        CVV            : 123
        3DS Password   : 1234
        ====================================================================
    """
    )
    @PostMapping("/createOrder")
    fun createOrder(@Valid @RequestBody dto: CreateOrderDto, httpRequest: HttpServletRequest): ResponseEntity<SuccessResponse<CreateOrderResponseDto>> {
        val clientIp = httpRequest.remoteAddr
        val orderResponse = paymentService.createOrder(dto, clientIp)
        return ResponseEntity.ok(orderResponse.toSuccessResponse("Create order successfully"))
    }

    @Operation(
        summary = "invoiceNo and orderCode are same",
        description = """
        data:  NULL || ENUM("NOT_YET", "PENDING", "PAYMENT_SUCCESS", "PAYMENT_FAIL")
        """
    )
    @GetMapping("/checkPayment/{invoiceNo}")
    fun checkPayment(@PathVariable invoiceNo: String): ResponseEntity<SuccessResponse<OrderPaymentstatus>> {
        val (paymentStatus, msg) = paymentService.checkPayment(invoiceNo)
        return ResponseEntity.ok(
            paymentStatus.toSuccessResponse(msg)
        )
    }

    @Operation(summary = "API for EPAY callback")
    @PostMapping("/ipnNotiUrl")
    fun callbackIpn(@RequestBody body: PaymentCallbackDto): ResponseEntity<Void> {
        paymentService.callbackIpn(body)
        return ResponseEntity.ok().build()
    }

}