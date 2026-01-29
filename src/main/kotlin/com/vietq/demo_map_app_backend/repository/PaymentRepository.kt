package com.vietq.demo_map_app_backend.repository

import UpsertPaymentDto
import PaymentDbResponseDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import com.study.jooq.tables.OrderPaymentEpay.Companion.ORDER_PAYMENT_EPAY

@Repository
class PaymentRepository(
    private val dsl: DSLContext
) {

    fun upsertPayment(dto: UpsertPaymentDto): Long {
        val record = dsl.insertInto(ORDER_PAYMENT_EPAY)
            .set(ORDER_PAYMENT_EPAY.ORDERCODE, dto.orderCode)
            .set(ORDER_PAYMENT_EPAY.TRXID, dto.trxId)
            .set(ORDER_PAYMENT_EPAY.MERTRXID, dto.merTrxId)
            .onDuplicateKeyUpdate()
            .set(ORDER_PAYMENT_EPAY.TRXID, dto.trxId)
            .set(ORDER_PAYMENT_EPAY.MERTRXID, dto.merTrxId)
            .returning(ORDER_PAYMENT_EPAY.ID)
            .fetchOne()

        return record?.get(ORDER_PAYMENT_EPAY.ID)
            ?: throw IllegalStateException("Upsert payment failed")
    }


    fun updatePayment(orderCode: String, trxId: String): Int {
        return dsl.update(ORDER_PAYMENT_EPAY)
            .set(ORDER_PAYMENT_EPAY.TRXID, trxId)
            .where(ORDER_PAYMENT_EPAY.ORDERCODE.eq(orderCode))
            .execute()
    }


    fun getPaymentByOrderCode(orderCode: String): PaymentDbResponseDto? {
        return dsl
            .selectFrom(ORDER_PAYMENT_EPAY)
            .where(ORDER_PAYMENT_EPAY.ORDERCODE.eq(orderCode))
            .fetchOne { r ->
                PaymentDbResponseDto(
                    id = r[ORDER_PAYMENT_EPAY.ID]!!,
                    orderCode = r[ORDER_PAYMENT_EPAY.ORDERCODE]!!,
                    trxId = r[ORDER_PAYMENT_EPAY.TRXID]!!,
                    merTrxId = r[ORDER_PAYMENT_EPAY.MERTRXID]!!,
                )
            }
    }
}