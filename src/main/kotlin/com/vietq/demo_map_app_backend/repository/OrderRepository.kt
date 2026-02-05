package com.vietq.demo_map_app_backend.repository

import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderPaymentstatus
import com.study.jooq.enums.OrderRefundstatus
import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import com.study.jooq.tables.Order.Companion.ORDER
import com.study.jooq.tables.OrderCartItems.Companion.ORDER_CART_ITEMS
import com.study.jooq.tables.OrderCartItemsCancel.Companion.ORDER_CART_ITEMS_CANCEL
import com.vietq.demo_map_app_backend.dto.CreateOrderDto
import com.vietq.demo_map_app_backend.dto.OrderAdminResponseDto
import com.vietq.demo_map_app_backend.dto.OrderDeliveryInfoDto
import com.vietq.demo_map_app_backend.mapper.OrderMapper
import org.jooq.impl.DSL
import java.time.LocalDateTime


@Repository
class OrderRepository(
    private val dsl: DSLContext,
    private val orderMapper: OrderMapper
) {
    fun insertOrder(dto: CreateOrderDto, orderCode: String, userDeliveryInfo: OrderDeliveryInfoDto): Long {

        return dsl.transactionResult { config ->

            val ctx = DSL.using(config)

            // Insert ORDER
            val orderId = ctx.insertInto(ORDER)
                .set(ORDER.MARTID, dto.martId)
                .set(ORDER.USERID, dto.userId)
                .set(ORDER.SHIPPERID, userDeliveryInfo.shipperId)
                .set(ORDER.ORDERCODE, orderCode)
                .set(ORDER.ORDERERNAME, userDeliveryInfo.ordererName)
                .set(ORDER.ORDERERPHONE, userDeliveryInfo.ordererPhone)
                .set(ORDER.ORDERERADDRESS, userDeliveryInfo.ordererAddress)
                .set(ORDER.ORDERSTATUS, OrderOrderstatus.WAITING)
                .set(ORDER.PAYMENTSTATUS, OrderPaymentstatus.NOT_YET)
                .set(ORDER.CARTTOTAL, dto.cartTotal)
                .set(ORDER.DISCOUNT, dto.discount)
                .set(ORDER.DELIVERYFEE, dto.deliveryFee)
                .set(ORDER.COUPONVOLUME, dto.couponVolume)
                .set(ORDER.POINTVOLUME, dto.pointVolume)
                .set(ORDER.POINTACCUMULATE, dto.pointAccumulate)
                .set(ORDER.AMOUNT, dto.amount)
                .set(ORDER.CREATEDAT, LocalDateTime.now())
                .returning(ORDER.ID)
                .fetchOne()
                ?.get(ORDER.ID)
                ?: throw IllegalStateException("Insert order failed")

            // Insert cart items
            dto.cartData.forEach { item ->

                ctx.insertInto(ORDER_CART_ITEMS)
                    .set(ORDER_CART_ITEMS.ORDERCODE, orderCode)
                    .set(ORDER_CART_ITEMS.PRODUCTID, item.id)
                    .set(ORDER_CART_ITEMS.NAME, item.name)
                    .set(ORDER_CART_ITEMS.IMAGE, item.image)
                    .set(ORDER_CART_ITEMS.PRICE, item.price)
                    .set(ORDER_CART_ITEMS.QUANTITY, item.quantity)
                    .set(ORDER_CART_ITEMS.CATEGORYID, item.categoryId)
                    .set(ORDER_CART_ITEMS.CATEGORYNAME, item.categoryName)
                    .set(ORDER_CART_ITEMS.CREATEDAT, LocalDateTime.now())
                    .execute()
            }

            orderId
        }
    }


    fun getOrders(userId: Long): List<OrderResponseDto> {

        val orders = dsl
            .selectFrom(ORDER)
            .where(ORDER.USERID.eq(userId))
            .orderBy(ORDER.CREATEDAT.desc())
            .fetch { r -> orderMapper.toOrderResponse(r) }

        val orderCodes = orders.map { it.orderCode }

        val cartMap = dsl
            .selectFrom(ORDER_CART_ITEMS)
            .where(ORDER_CART_ITEMS.ORDERCODE.`in`(orderCodes))
            .fetchGroups(
                ORDER_CART_ITEMS.ORDERCODE
            ) { r ->
                orderMapper.toCartItemResponse(r)
            }

        return orders.map { order ->
            order.copy(cartData = (cartMap[order.orderCode] ?: emptyList()))
        }
    }

    fun getOrderById(orderId: Long): OrderResponseDto? {

        val order = dsl
            .selectFrom(ORDER)
            .where(ORDER.ID.eq(orderId))
            .fetchOne { r ->
                orderMapper.toOrderResponse(r)
            } ?: return null

        val orderCode = order.orderCode

        // GET CART
        val carts = dsl
            .selectFrom(ORDER_CART_ITEMS)
            .where(ORDER_CART_ITEMS.ORDERCODE.eq(orderCode))
            .fetch { r -> orderMapper.toCartItemResponse(r) }

        // GET CART ITEM CANCEL
        val cartCancels = dsl
            .selectFrom(ORDER_CART_ITEMS_CANCEL)
            .where(ORDER_CART_ITEMS_CANCEL.ORDERCODE.eq(orderCode))
            .fetch { r -> orderMapper.toCartItemCancelResponse(r) }

        return order.copy(
            cartData = carts,
            cartDataCancel = cartCancels
        )
    }

    fun getOrderByCode(orderCode: String): OrderResponseDto? {

        val order = dsl
            .selectFrom(ORDER)
            .where(ORDER.ORDERCODE.eq(orderCode))
            .fetchOne { r ->
                orderMapper.toOrderResponse(r)
            } ?: return null

        val cartItems = dsl
            .selectFrom(ORDER_CART_ITEMS)
            .where(ORDER_CART_ITEMS.ORDERCODE.eq(orderCode))
            .fetch { r ->
                orderMapper.toCartItemResponse(r)
            }

        return order.copy(cartData = cartItems)
    }

    /**
     * the purpose is update 'orderStatus' and 'paymentStatus'
     */
    fun changeOrderAndPaymentStatus(orderCode: String, paymentStatus: OrderPaymentstatus, orderStatus: OrderOrderstatus): Boolean {
        return dsl
            .update(ORDER)
            .set(ORDER.PAYMENTSTATUS, paymentStatus)
            .set(ORDER.ORDERSTATUS, orderStatus)
            .where(
                ORDER.ORDERCODE.eq(orderCode)
            )
            .execute() > 0
    }

    /**
     * the purpose is mark delivery success when shipper arrived ( SIMULATOR )
     */
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


}