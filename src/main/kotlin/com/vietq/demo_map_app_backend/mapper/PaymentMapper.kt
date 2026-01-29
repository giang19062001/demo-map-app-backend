package com.vietq.demo_map_app_backend.mapper

import PaymentCreateLinkResponseDto
import com.vietq.demo_map_app_backend.dto.CreateOrderResponseDto
import org.springframework.stereotype.Component

@Component
class PaymentMapper {

    fun toCreateLinkResponse(
        res: PaymentCreateLinkResponseDto?,
        decryptedPaymentLink: String
    ): CreateOrderResponseDto {

        return CreateOrderResponseDto(
            resultCd = res?.resultCd.orEmpty(),
            resultMsg = res?.resultMsg.orEmpty(),
            paymentId = res?.paymentId.orEmpty(),
            merId = res?.merId.orEmpty(),
            invoiceNo = res?.invoiceNo.orEmpty(),
            amount = res?.amount.orEmpty(),
            currency = res?.currency.orEmpty(),
            timeStamp = res?.timeStamp.orEmpty(),
            payType = res?.payType.orEmpty(),
            payOption = res?.payOption.orEmpty(),
            linkExptime = res?.linkExptime.orEmpty(),
            paymentLink = decryptedPaymentLink,
            qrCode = res?.qrCode.orEmpty(),
            merchantToken = res?.merchantToken.orEmpty()
        )
    }
}
