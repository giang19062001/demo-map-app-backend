package com.vietq.demo_map_app_backend.mapper

import PaymentCallbackDto
import UpsertPaymentDto
import com.study.jooq.enums.OrderEpayPaytype
import com.vietq.demo_map_app_backend.dto.CreateOrderResponseDto
import org.springframework.stereotype.Component
import org.jooq.Record
import com.study.jooq.tables.OrderEpay.Companion.ORDER_EPAY
import com.vietq.demo_map_app_backend.dto.PaymentCreateLinkResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentEpayResponseDto
import com.vietq.demo_map_app_backend.dto.PaymentGetTransactionDataResponseDto

@Component
class PaymentMapper {

    fun toPaymentResponseDto(r: Record): PaymentEpayResponseDto =
        PaymentEpayResponseDto(
            orderCode = r[ORDER_EPAY.ORDERCODE]!!,
            trxId = r[ORDER_EPAY.TRXID],
            merTrxId = r[ORDER_EPAY.MERTRXID],
            goodsNm =r[ORDER_EPAY.GOODSNM],
            buyerFirstNm =r[ORDER_EPAY.BUYERFIRSTNM],
            buyerLastNm = r[ORDER_EPAY.BUYERLASTNM],
            amount = r[ORDER_EPAY.AMOUNT],
            remainAmount = r[ORDER_EPAY.REMAINAMOUNT],
            payType = r[ORDER_EPAY.PAYTYPE],
            payOption =r[ORDER_EPAY.PAYOPTION],
            bankId =r[ORDER_EPAY.BANKID],
            bankCode = r[ORDER_EPAY.BANKCODE],
            cardNo = r[ORDER_EPAY.CARDNO],
            cardType = r[ORDER_EPAY.CARDTYPE],
            cardTypeValue = r[ORDER_EPAY.CARDTYPEVALUE],
            status = r[ORDER_EPAY.STATUS],
            resultCd = r[ORDER_EPAY.RESULTCD]!!,
            resultMsg =r[ORDER_EPAY.RESULTMSG]!!
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
            payType = dto.payType.let { OrderEpayPaytype.valueOf(it) },
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
        dto: PaymentGetTransactionDataResponseDto,
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
            payType = dto.payType.let { OrderEpayPaytype.valueOf(it) },
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
