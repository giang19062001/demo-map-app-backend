package com.vietq.demo_map_app_backend.controllerView

import PaymentCallbackUrlDto
import com.vietq.demo_map_app_backend.service.PaymentService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam

@Controller
class PaymentViewController(
    private val paymentService: PaymentService
) {

    @GetMapping("/view/payment/callBackUrl")
    fun paymentCallback(
        @ModelAttribute dto: PaymentCallbackUrlDto,
        model: Model
    ): String {

        // For view
        model.addAttribute("resultCd", dto.resultCd)
        model.addAttribute("resultMsg", dto.resultMsg)
        model.addAttribute("invoiceNo", dto.invoiceNo)
        model.addAttribute("amount", dto.amount)

        // For all cases ( IPN will be in charge Success case)
        if (listOf(dto.invoiceNo, dto.status, dto.merTrxId, dto.trxId).all { it.isNotBlank() }) {
            paymentService.callbackUrl(dto)
        }

        return "result-payment"
    }

}