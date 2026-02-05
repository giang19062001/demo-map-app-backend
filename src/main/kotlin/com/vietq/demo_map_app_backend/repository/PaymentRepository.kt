package com.vietq.demo_map_app_backend.repository

import UpsertPaymentDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import com.study.jooq.tables.OrderEpay.Companion.ORDER_EPAY
import com.vietq.demo_map_app_backend.dto.PaymentEpayResponseDto
import com.vietq.demo_map_app_backend.mapper.PaymentMapper
import org.aspectj.apache.bcel.generic.RET

@Repository
class PaymentRepository(
    private val dsl: DSLContext,
    private val paymentMapper: PaymentMapper
) {

    fun upsertPayment(dto: UpsertPaymentDto): Int {
        val affected = dsl.insertInto(ORDER_EPAY)
            .set(ORDER_EPAY.ORDERCODE, dto.orderCode)
            .set(ORDER_EPAY.TRXID, dto.trxId)
            .set(ORDER_EPAY.MERTRXID, dto.merTrxId)
            .set(ORDER_EPAY.GOODSNM, dto.goodsNm)
            .set(ORDER_EPAY.BUYERFIRSTNM, dto.buyerFirstNm)
            .set(ORDER_EPAY.BUYERLASTNM, dto.buyerLastNm)
            .set(ORDER_EPAY.AMOUNT, dto.amount)
            .set(ORDER_EPAY.REMAINAMOUNT, dto.remainAmount)
            .set(ORDER_EPAY.PAYTYPE, dto.payType)
            .set(ORDER_EPAY.PAYOPTION, dto.payOption)
            .set(ORDER_EPAY.BANKID, dto.bankId)
            .set(ORDER_EPAY.BANKCODE, dto.bankCode)
            .set(ORDER_EPAY.CARDNO, dto.cardNo)
            .set(ORDER_EPAY.CARDTYPE, dto.cardType)
            .set(ORDER_EPAY.CARDTYPEVALUE, dto.cardTypeValue)
            .set(ORDER_EPAY.STATUS, dto.status)
            .set(ORDER_EPAY.RESULTCD, dto.resultCd)
            .set(ORDER_EPAY.RESULTMSG, dto.resultMsg)
            .onDuplicateKeyUpdate()
            .set(ORDER_EPAY.TRXID, dto.trxId)
            .set(ORDER_EPAY.MERTRXID, dto.merTrxId)
            .set(ORDER_EPAY.GOODSNM, dto.goodsNm)
            .set(ORDER_EPAY.BUYERFIRSTNM, dto.buyerFirstNm)
            .set(ORDER_EPAY.BUYERLASTNM, dto.buyerLastNm)
            .set(ORDER_EPAY.AMOUNT, dto.amount)
            .set(ORDER_EPAY.REMAINAMOUNT, dto.remainAmount)
            .set(ORDER_EPAY.PAYTYPE, dto.payType)
            .set(ORDER_EPAY.PAYOPTION, dto.payOption)
            .set(ORDER_EPAY.BANKID, dto.bankId)
            .set(ORDER_EPAY.BANKCODE, dto.bankCode)
            .set(ORDER_EPAY.CARDNO, dto.cardNo)
            .set(ORDER_EPAY.CARDTYPE, dto.cardType)
            .set(ORDER_EPAY.CARDTYPEVALUE, dto.cardTypeValue)
            .set(ORDER_EPAY.STATUS, dto.status)
            .set(ORDER_EPAY.RESULTCD, dto.resultCd)
            .set(ORDER_EPAY.RESULTMSG, dto.resultMsg)
            .execute()

        if (affected == 0) {
            throw IllegalStateException("Upsert payment failed")
        }

        return affected
    }

    fun getPaymentByOrderCode(orderCode: String): PaymentEpayResponseDto? {
        return dsl
            .selectFrom(ORDER_EPAY)
            .where(ORDER_EPAY.ORDERCODE.eq(orderCode))
            .fetchOne { r ->
                paymentMapper.toPaymentResponseDto(r)
            } ?: return null
    }

}