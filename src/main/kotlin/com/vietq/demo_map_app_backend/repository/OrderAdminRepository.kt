package com.vietq.demo_map_app_backend.repository

import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderRefundstatus
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import com.study.jooq.tables.Order.Companion.ORDER
import com.study.jooq.tables.OrderCancel.Companion.ORDER_CANCEL
import com.study.jooq.tables.OrderCartItems.Companion.ORDER_CART_ITEMS
import com.study.jooq.tables.OrderCartItemsCancel.Companion.ORDER_CART_ITEMS_CANCEL
import com.study.jooq.tables.OrderEpay.Companion.ORDER_EPAY
import com.vietq.demo_map_app_backend.dto.InsertCancelEntireDto
import com.vietq.demo_map_app_backend.dto.InsertCancelPartialDto
import com.vietq.demo_map_app_backend.dto.OrderAdminResponseDto
import com.vietq.demo_map_app_backend.mapper.OrderMapper
import org.jooq.impl.DSL
import java.time.LocalDateTime

@Repository
class OrderAdminRepository(
    private val dsl: DSLContext,
    private val orderMapper: OrderMapper
) {
    /**
     * The purpose is to log the event of entire cancellation.
     */
    fun insertCancelEntire(dto: InsertCancelEntireDto): Long? {
        return dsl.transactionResult { config ->
            val ctx = DSL.using(config)

            val insertedRecord = ctx.insertInto(ORDER_CANCEL)
                .set(ORDER_CANCEL.ORDERCODE, dto.orderCode)
                .set(ORDER_CANCEL.CANCELCODE, dto.cancelCode)
                .set(ORDER_CANCEL.AMOUNTORIGINAL, dto.amountOriginal)
                .set(ORDER_CANCEL.AMOUNTCANCEL, dto.amountCancel)
                .set(ORDER_CANCEL.CREATEDAT, LocalDateTime.now())
                .returning(ORDER_CANCEL.ID)
                .fetchOne()
                ?: throw IllegalStateException("Insert cancel entire failed")

            insertedRecord.getValue(ORDER_CANCEL.ID)
        }
    }

    /**
     * the purpose is to log the events of partially canceling individual items
     */
    fun insertCancelPartial(dto: InsertCancelPartialDto): Long? {
        return dsl.transactionResult { config ->
            val ctx = DSL.using(config)

            val insertedRecord = ctx.insertInto(ORDER_CART_ITEMS_CANCEL)
                .set(ORDER_CART_ITEMS_CANCEL.ORDERCODE, dto.orderCode)
                .set(ORDER_CART_ITEMS_CANCEL.CANCELCODE, dto.cancelCode)
                .set(ORDER_CART_ITEMS_CANCEL.PRODUCTID, dto.productId)
                .set(ORDER_CART_ITEMS_CANCEL.NAME, dto.name)
                .set(ORDER_CART_ITEMS_CANCEL.IMAGE, dto.image)
                .set(ORDER_CART_ITEMS_CANCEL.PRICE, dto.price)
                .set(ORDER_CART_ITEMS_CANCEL.QUANTITY, dto.quantity)
                .set(ORDER_CART_ITEMS_CANCEL.CATEGORYID, dto.categoryId)
                .set(ORDER_CART_ITEMS_CANCEL.CATEGORYNAME, dto.categoryName)
                .set(ORDER_CART_ITEMS_CANCEL.AMOUNTACTUAL, dto.amountActual)
                .set(ORDER_CART_ITEMS_CANCEL.AMOUNTCANCEL, dto.amountCancel)
                .set(ORDER_CART_ITEMS_CANCEL.CREATEDAT, LocalDateTime.now())
                .returning(ORDER_CART_ITEMS_CANCEL.ID)
                .fetchOne()
                ?: throw IllegalStateException("Insert cancel partial failed")

            insertedRecord.getValue(ORDER_CART_ITEMS_CANCEL.ID)
        }
    }

    /**
     * the purpose is get refund total amount of one order
     */
    fun getRefundedSoFar(orderCode: String): Long {
        return dsl
            .select(
                DSL.coalesce(
                    DSL.sum(ORDER_CART_ITEMS_CANCEL.AMOUNTCANCEL),
                    DSL.inline(0L)
                )
            )
            .from(ORDER_CART_ITEMS_CANCEL)
            .where(
                ORDER_CART_ITEMS_CANCEL.ORDERCODE.eq(orderCode)
            )
            .fetchOne(0, Long::class.java) ?: 0L
    }

    /**
     * the purpose is update 'orderStatus'
     */
    fun changeOrderStatus(orderCode: String, orderStatus: OrderOrderstatus): Boolean {
        return dsl
            .update(ORDER)
            .set(ORDER.ORDERSTATUS, orderStatus)
            .where(
                ORDER.ORDERCODE.eq(orderCode)
            )
            .execute() > 0
    }

    /**
     * the purpose is update 'refundStatus'
     */
    fun changeOrderRefundStatus(orderCode: String, refundStatus: OrderRefundstatus): Boolean {
        return dsl
            .update(ORDER)
            .set(ORDER.REFUNDSTATUS, refundStatus)
            .where(
                ORDER.ORDERCODE.eq(orderCode)
            )
            .execute() > 0
    }


    /**
     * the purpose is get the list of orders by user ( For admin page )
     */
    fun getAdminOrders(userId: Long): List<OrderAdminResponseDto> {
        val orders = dsl
            .select(
                ORDER.ID, ORDER.ORDERCODE, ORDER.ORDERERNAME, ORDER.CREATEDAT,
                ORDER.PAYMENTSTATUS, ORDER.REFUNDSTATUS,
                ORDER.DISCOUNT, ORDER.DELIVERYFEE, ORDER.COUPONVOLUME, ORDER.POINTVOLUME, ORDER.CARTTOTAL,
                ORDER_EPAY.TRXID, ORDER_EPAY.MERTRXID, ORDER_EPAY.STATUS,
                ORDER_EPAY.PAYTYPE, ORDER_EPAY.BANKID, ORDER_EPAY.CARDNO,
                ORDER_EPAY.RESULTCD, ORDER_EPAY.RESULTMSG, ORDER.AMOUNT, ORDER_EPAY.REMAINAMOUNT
            )
            .from(ORDER)
            .leftJoin(ORDER_EPAY)
            .on(ORDER.ORDERCODE.eq(ORDER_EPAY.ORDERCODE))
            .where(ORDER.USERID.eq(userId))
            .orderBy(ORDER.CREATEDAT.desc())
            .fetch { r -> orderMapper.toOrderAdminResponse(r) }

        return orders.map { order ->
            order.copy(
                cartDataOriginal = emptyList(),
                cartDataCurrent = emptyList(),
                cartDataCancel = emptyList(),
            )
        }

    }

    /**
     * the purpose is get an order info by 'orderCode' ( For admin page )
     */
    fun getAdminOrderByCode(orderCode: String): OrderAdminResponseDto? {
        val order = dsl
            .select(
                ORDER.ID, ORDER.ORDERCODE, ORDER.ORDERERNAME, ORDER.CREATEDAT,
                ORDER.PAYMENTSTATUS, ORDER.PAYMENTSTATUS, ORDER.REFUNDSTATUS,
                ORDER.DISCOUNT, ORDER.DELIVERYFEE, ORDER.COUPONVOLUME, ORDER.POINTVOLUME, ORDER.CARTTOTAL,
                ORDER_EPAY.TRXID, ORDER_EPAY.MERTRXID, ORDER_EPAY.STATUS,
                ORDER_EPAY.PAYTYPE, ORDER_EPAY.BANKID, ORDER_EPAY.CARDNO,
                ORDER_EPAY.RESULTCD, ORDER_EPAY.RESULTMSG, ORDER.AMOUNT, ORDER_EPAY.REMAINAMOUNT
            )
            .from(ORDER)
            .leftJoin(ORDER_EPAY)
            .on(ORDER.ORDERCODE.eq(ORDER_EPAY.ORDERCODE))
            .where(ORDER.ORDERCODE.eq(orderCode))
            .orderBy(ORDER.CREATEDAT.desc())
            .fetchOne { r -> orderMapper.toOrderAdminResponse(r) }
            ?: return null

        // GET CART ITEMS
        val carts = dsl
            .selectFrom(ORDER_CART_ITEMS)
            .where(ORDER_CART_ITEMS.ORDERCODE.eq(orderCode))
            .fetch { r -> orderMapper.toCartItemResponse(r) }

        // GET CART ITEMS CANCEL
        val cartCancels = dsl
            .selectFrom(ORDER_CART_ITEMS_CANCEL)
            .where(ORDER_CART_ITEMS_CANCEL.ORDERCODE.eq(orderCode))
            .fetch { r -> orderMapper.toCartItemCancelResponse(r) }

        return order.copy(
            cartDataOriginal = carts,
            cartDataCurrent = emptyList(),
            cartDataCancel = cartCancels
        )
    }
}