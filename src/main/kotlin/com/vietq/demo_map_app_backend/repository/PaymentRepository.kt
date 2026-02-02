package com.vietq.demo_map_app_backend.repository

import PaymentEpayResponseDto
import UpsertPaymentDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import com.study.jooq.tables.OrderPaymentEpay.Companion.ORDER_PAYMENT_EPAY
import com.vietq.demo_map_app_backend.mapper.PaymentMapper
import org.aspectj.apache.bcel.generic.RET

@Repository
class PaymentRepository(
    private val dsl: DSLContext,
    private val paymentMapper: PaymentMapper
) {

    fun upsertPayment(dto: UpsertPaymentDto): Int {
        val affected = dsl.insertInto(ORDER_PAYMENT_EPAY)
            .set(ORDER_PAYMENT_EPAY.ORDERCODE, dto.orderCode)
            .set(ORDER_PAYMENT_EPAY.TRXID, dto.trxId)
            .set(ORDER_PAYMENT_EPAY.MERTRXID, dto.merTrxId)
            .set(ORDER_PAYMENT_EPAY.GOODSNM, dto.goodsNm)
            .set(ORDER_PAYMENT_EPAY.BUYERFIRSTNM, dto.buyerFirstNm)
            .set(ORDER_PAYMENT_EPAY.BUYERLASTNM, dto.buyerLastNm)
            .set(ORDER_PAYMENT_EPAY.AMOUNT, dto.amount)
            .set(ORDER_PAYMENT_EPAY.REMAINAMOUNT, dto.remainAmount)
            .set(ORDER_PAYMENT_EPAY.PAYTYPE, dto.payType)
            .set(ORDER_PAYMENT_EPAY.PAYOPTION, dto.payOption)
            .set(ORDER_PAYMENT_EPAY.BANKID, dto.bankId)
            .set(ORDER_PAYMENT_EPAY.BANKCODE, dto.bankCode)
            .set(ORDER_PAYMENT_EPAY.CARDNO, dto.cardNo)
            .set(ORDER_PAYMENT_EPAY.CARDTYPE, dto.cardType)
            .set(ORDER_PAYMENT_EPAY.CARDTYPEVALUE, dto.cardTypeValue)
            .set(ORDER_PAYMENT_EPAY.STATUS, dto.status)
            .set(ORDER_PAYMENT_EPAY.RESULTCD, dto.resultCd)
            .set(ORDER_PAYMENT_EPAY.RESULTMSG, dto.resultMsg)
            .onDuplicateKeyUpdate()
            .set(ORDER_PAYMENT_EPAY.TRXID, dto.trxId)
            .set(ORDER_PAYMENT_EPAY.MERTRXID, dto.merTrxId)
            .set(ORDER_PAYMENT_EPAY.GOODSNM, dto.goodsNm)
            .set(ORDER_PAYMENT_EPAY.BUYERFIRSTNM, dto.buyerFirstNm)
            .set(ORDER_PAYMENT_EPAY.BUYERLASTNM, dto.buyerLastNm)
            .set(ORDER_PAYMENT_EPAY.AMOUNT, dto.amount)
            .set(ORDER_PAYMENT_EPAY.REMAINAMOUNT, dto.remainAmount)
            .set(ORDER_PAYMENT_EPAY.PAYTYPE, dto.payType)
            .set(ORDER_PAYMENT_EPAY.PAYOPTION, dto.payOption)
            .set(ORDER_PAYMENT_EPAY.BANKID, dto.bankId)
            .set(ORDER_PAYMENT_EPAY.BANKCODE, dto.bankCode)
            .set(ORDER_PAYMENT_EPAY.CARDNO, dto.cardNo)
            .set(ORDER_PAYMENT_EPAY.CARDTYPE, dto.cardType)
            .set(ORDER_PAYMENT_EPAY.CARDTYPEVALUE, dto.cardTypeValue)
            .set(ORDER_PAYMENT_EPAY.STATUS, dto.status)
            .set(ORDER_PAYMENT_EPAY.RESULTCD, dto.resultCd)
            .set(ORDER_PAYMENT_EPAY.RESULTMSG, dto.resultMsg)
            .execute()

        if (affected == 0) {
            throw IllegalStateException("Upsert payment failed")
        }

        return affected
    }

    fun getPaymentByOrderCode(orderCode: String): PaymentEpayResponseDto? {
        return dsl
            .selectFrom(ORDER_PAYMENT_EPAY)
            .where(ORDER_PAYMENT_EPAY.ORDERCODE.eq(orderCode))
            .fetchOne { r ->
                paymentMapper.toPaymentResponseDto(r)
            } ?: return null
    }

}