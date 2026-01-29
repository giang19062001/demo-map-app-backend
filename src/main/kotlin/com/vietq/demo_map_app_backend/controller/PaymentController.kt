package com.vietq.demo_map_app_backend.controller

import PaymentCallbackIpnDto
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
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@RestController
@RequestMapping("/api/payment")
class PaymentController(private val paymentService: PaymentService) {
    @Operation(
        summary = "API EPAY cho nút đặt hàng",
        description = """
        Thông tin test thanh toán Epay
        Ngân hàng      : NAPAS-SaigonBank
        Số thẻ         : 9704000000000018
        Tên chủ thẻ    : NGUYEN VAN A
        Ngày phát hành : 03/07
        OTP  : otp
        """
    )
    @PostMapping("/create-order")
    fun createOrder(
        @RequestBody request: CreateOrderDto,
        httpRequest: HttpServletRequest
    ): ResponseEntity<SuccessResponse<CreateOrderResponseDto>> {

        val clientIp = httpRequest.remoteAddr

        val orderResponse = paymentService.createOrder(request, clientIp)
        return ResponseEntity.ok(orderResponse.toSuccessResponse("Create order successfully"))
    }

    @Operation(
        summary = "invoiceNo and orderCode are same",
        description = """
        data:  NULL || ENUM("NOT_YET", "PENDING", "PAYMENT_SUCCESS", "PAYMENT_FAIL")
        """
    )
    @GetMapping("/checkPayment/{invoiceNo}")
    fun checkPayment(
        @PathVariable invoiceNo: String
    ): ResponseEntity<SuccessResponse<OrderPaymentstatus>> {

        val paymentResponse = paymentService.checkPayment(invoiceNo)

        return ResponseEntity.ok(
            paymentResponse.toSuccessResponse("Get info successfully")
        )
    }

    // EPAY ONLY CALL THIS API WHEN THE PAYMENT SUCCESS ( NOT CALL THIS API IF THE PAYMENT FAILED)
    @Operation(summary = "API for EPAY callback")
    @PostMapping("/ipn-notiUrl")
    fun callbackIpn(
        @RequestBody body: PaymentCallbackIpnDto
    ): ResponseEntity<Void> {
        paymentService.callbackIpn(body)
        return ResponseEntity.ok().build()
    }

}