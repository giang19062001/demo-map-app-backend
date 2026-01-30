package com.vietq.demo_map_app_backend.mapper

import PaymentCallbackDto
import PaymentCheckTransactionDataResponseDto
import PaymentCreateLinkResponseDto
import UpsertPaymentDto
import com.study.jooq.enums.OrderPaymentEpayPaytype
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

    fun toUpsertPaymentDto(
        dto: PaymentCallbackDto,
    ): UpsertPaymentDto {
        return UpsertPaymentDto(
            orderCode = dto.invoiceNo,
            merTrxId = dto.merTrxId,
            trxId = dto.trxId,
            goodsNm = dto.goodsNm,
            buyerFirstNm = dto.buyerFirstNm,
            buyerLastNm = dto.buyerLastNm,
            amount = dto.amount,
            remainAmount = dto.remainAmount ?: dto.amount,
            payType = dto.payType.let { OrderPaymentEpayPaytype.valueOf(it) },
            payOption = dto.payOption,
            bankId = dto.bankId,
            bankCode = dto.bankCode,
            cardNo = dto.cardNo,
            cardType = dto.cardType,
            cardTypeValue = dto.cardTypeValue,
            status = dto.status,
            resultCd = dto.resultCd,
            resultMsg = dto.resultMsg
        )
    }

    fun toUpsertPaymentDto(
        dto: PaymentCheckTransactionDataResponseDto,
    ): UpsertPaymentDto {
        return UpsertPaymentDto(
            orderCode = dto.invoiceNo,
            merTrxId = dto.merTrxId,
            trxId = dto.trxId,
            goodsNm = dto.goodsNm,
            buyerFirstNm = dto.buyerFirstNm,
            buyerLastNm = dto.buyerLastNm,
            amount = dto.amount,
            remainAmount = dto.remainAmount ?: dto.amount,
            payType = dto.payType.let { OrderPaymentEpayPaytype.valueOf(it) },
            payOption = dto.payOption,
            bankId = dto.bankId,
            bankCode = dto.bankCode,
            cardNo = dto.cardNo,
            cardType = dto.cardType,
            cardTypeValue = dto.cardTypeValue,
            status = dto.status,
            resultCd = dto.resultCd,
            resultMsg = dto.resultMsg
        )
    }
}
