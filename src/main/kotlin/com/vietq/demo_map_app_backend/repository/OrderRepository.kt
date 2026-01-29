package com.vietq.demo_map_app_backend.repository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.study.jooq.enums.OrderCancelstatus
import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderPaymentstatus
import com.study.jooq.enums.OrderRefundstatus
import com.vietq.demo_map_app_backend.dto.CartDto
import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import com.study.jooq.tables.Order.Companion.ORDER
import com.study.jooq.tables.Mart.Companion.MART
import com.study.jooq.tables.OrderPaymentEpay.Companion.ORDER_PAYMENT_EPAY
import com.vietq.demo_map_app_backend.dto.CreateOrderDto
import com.vietq.demo_map_app_backend.dto.CreateOrderDeliveryDto
import com.vietq.demo_map_app_backend.mapper.OrderMapper
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jooq.JSON
import java.time.LocalDateTime


@Repository
class OrderRepository(
    private val dsl: DSLContext,
    private val orderMapper: OrderMapper
) {
    fun insertOrder(dto: CreateOrderDto, orderCode: String, userDeliveryInfo: CreateOrderDeliveryDto): Long {

        val objectMapper = jacksonObjectMapper()

        val cartDataJson = objectMapper.writeValueAsString(dto.cartData)

        val record = dsl.insertInto(ORDER)
            .set(ORDER.MARTID, dto.martId)
            .set(ORDER.USERID, dto.userId)
            .set(ORDER.SHIPPERID, userDeliveryInfo.shipperId)
            .set(ORDER.ORDERCODE, orderCode)
            .set(ORDER.ORDERERNAME, userDeliveryInfo.ordererName)
            .set(ORDER.ORDERERPHONE, userDeliveryInfo.ordererPhone)
            .set(ORDER.ORDERERADDRESS, userDeliveryInfo.ordererAddress)
            .set(ORDER.ORDERSTATUS, OrderOrderstatus.WAITING)
            .set(ORDER.PAYMENTSTATUS, OrderPaymentstatus.NOT_YET)
            .set(ORDER.REFUNDSTATUS, OrderRefundstatus.NONE)
            .set(ORDER.CANCELSTATUS, OrderCancelstatus.NONE)
            .set(ORDER.CARTDATA, JSON.valueOf(cartDataJson))
            .set(ORDER.CARTTOTAL, dto.cartTotal)
            .set(ORDER.DISCOUNT, dto.discount)
            .set(ORDER.DELIVERYFEE, dto.deliveryFee)
            .set(ORDER.COUPONVOLUME, dto.couponVolume)
            .set(ORDER.POINTVOLUME, dto.pointVolume)
            .set(ORDER.POINTACCUMULATE, dto.pointAccumulate)
            .set(ORDER.AMOUNT, dto.amount)
            .set(ORDER.CREATEDAT, LocalDateTime.now())
            .set(ORDER.UPDATEDAT, null as LocalDateTime?)
            .returning(ORDER.ID)
            .fetchOne()

        return record?.get(ORDER.ID)
            ?: throw IllegalStateException("Insert order failed")
    }

    fun getOrders(userId: Long): List<OrderResponseDto> {
        return dsl
            .selectFrom(ORDER)
            .where(ORDER.USERID.eq(userId))
            .orderBy(ORDER.CREATEDAT.desc())
            .fetch { r ->
                orderMapper.toResponse(r)
            }
    }

    fun getOrderById(orderId: Long): OrderResponseDto? {
        return dsl
            .selectFrom(ORDER)
            .where(ORDER.ID.eq(orderId))
            .fetchOne { r ->
                orderMapper.toResponse(r)
            }
    }


    fun getOrderByCode(orderCode: String): OrderResponseDto? {

        return dsl
            .selectFrom(ORDER)
            .where(ORDER.ORDERCODE.eq(orderCode))
            .fetchOne { r ->
                orderMapper.toResponse(r)

            }
    }
    fun markAsCompleteOrder(orderId: Long): Boolean {
        return dsl
            .update(ORDER)
            .set(ORDER.ORDERSTATUS, OrderOrderstatus.COMPLETE)
            .where(
                ORDER.ID.eq(orderId),
                ORDER.ORDERSTATUS.eq(OrderOrderstatus.DELIVERING)
            )
            .execute() > 0
    }

    fun changePaymentStatusOrder(orderCode: String, paymentStatus: OrderPaymentstatus, orderStatus: OrderOrderstatus): Boolean {
        return dsl
            .update(ORDER)
            .set(ORDER.PAYMENTSTATUS, paymentStatus)
            .set(ORDER.ORDERSTATUS, orderStatus)
            .where(
                ORDER.ORDERCODE.eq(orderCode)
            )
            .execute() > 0
    }



}