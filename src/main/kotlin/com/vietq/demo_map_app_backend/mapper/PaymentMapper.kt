package com.vietq.demo_map_app_backend.mapper

import PaymentCallbackDto
import PaymentCheckTransactionDataResponseDto
import PaymentCreateLinkResponseDto
import PaymentEpayResponseDto
import UpsertPaymentDto
import com.study.jooq.enums.OrderPaymentEpayPaytype
import com.vietq.demo_map_app_backend.dto.CreateOrderResponseDto
import org.springframework.stereotype.Component
import org.jooq.Record
import com.study.jooq.tables.OrderPaymentEpay.Companion.ORDER_PAYMENT_EPAY

@Component
class PaymentMapper {

    fun toPaymentResponseDto(r: Record): PaymentEpayResponseDto =
        PaymentEpayResponseDto(
            orderCode = r[ORDER_PAYMENT_EPAY.ORDERCODE]!!,
            trxId = r[ORDER_PAYMENT_EPAY.TRXID],
            merTrxId = r[ORDER_PAYMENT_EPAY.MERTRXID],
            goodsNm =r[ORDER_PAYMENT_EPAY.GOODSNM],
            buyerFirstNm =r[ORDER_PAYMENT_EPAY.BUYERFIRSTNM],
            buyerLastNm = r[ORDER_PAYMENT_EPAY.BUYERLASTNM],
            amount = r[ORDER_PAYMENT_EPAY.AMOUNT],
            remainAmount = r[ORDER_PAYMENT_EPAY.REMAINAMOUNT],
            payType = r[ORDER_PAYMENT_EPAY.PAYTYPE],
            payOption =r[ORDER_PAYMENT_EPAY.PAYOPTION],
            bankId =r[ORDER_PAYMENT_EPAY.BANKID],
            bankCode = r[ORDER_PAYMENT_EPAY.BANKCODE],
            cardNo = r[ORDER_PAYMENT_EPAY.CARDNO],
            cardType = r[ORDER_PAYMENT_EPAY.CARDTYPE],
            cardTypeValue = r[ORDER_PAYMENT_EPAY.CARDTYPEVALUE],
            status = r[ORDER_PAYMENT_EPAY.STATUS],
            resultCd = r[ORDER_PAYMENT_EPAY.RESULTCD]!!,
            resultMsg =r[ORDER_PAYMENT_EPAY.RESULTMSG]!!
        )

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
