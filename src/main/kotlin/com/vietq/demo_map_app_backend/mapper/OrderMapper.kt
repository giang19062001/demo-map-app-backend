package com.vietq.demo_map_app_backend.mapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.study.jooq.tables.Mart.Companion.MART
import com.study.jooq.tables.Order.Companion.ORDER
import com.study.jooq.tables.OrderCartItems.Companion.ORDER_CART_ITEMS
import com.study.jooq.tables.OrderPaymentEpay.Companion.ORDER_PAYMENT_EPAY
import com.vietq.demo_map_app_backend.dto.CartItemDto
import com.vietq.demo_map_app_backend.dto.OrderAdminResponseDto
import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import org.jooq.Field
import org.jooq.Record
import org.springframework.stereotype.Component

@Component
class OrderMapper {

    private val objectMapper = jacksonObjectMapper()
    // ADMIN
    fun toOrderAdminResponse(r: Record): OrderAdminResponseDto {
        return OrderAdminResponseDto(
            id = r[ORDER.ID]!!,
            martId = r[ORDER.MARTID]!!,
            userId = r[ORDER.USERID]!!,
            shipperId = r[ORDER.SHIPPERID],
            orderCode = r[ORDER.ORDERCODE]!!,
            ordererName = r[ORDER.ORDERERNAME]!!,
            ordererPhone = r[ORDER.ORDERERPHONE]!!,
            ordererAddress = r[ORDER.ORDERERADDRESS]!!,
            orderStatus = r[ORDER.ORDERSTATUS]!!.name,
            paymentStatus = r[ORDER.PAYMENTSTATUS]!!.name,
            refundStatus = r[ORDER.REFUNDSTATUS]!!.name,
            cancelStatus = r[ORDER.CANCELSTATUS]!!.name,
            cartData = emptyList(),
            cartDataCancel = emptyList(),
            cartTotal = r[ORDER.CARTTOTAL]!!,
            discount = r[ORDER.DISCOUNT]!!,
            deliveryFee = r[ORDER.DELIVERYFEE]!!,
            couponVolume = r[ORDER.COUPONVOLUME]!!,
            pointVolume = r[ORDER.POINTVOLUME]!!,
            pointAccumulate = r[ORDER.POINTACCUMULATE]!!,
            amount = r[ORDER.AMOUNT]!!,
            createdAt = r[ORDER.CREATEDAT]!!,
            updatedAt = r[ORDER.UPDATEDAT],
            resultCd = r[ORDER_PAYMENT_EPAY.RESULTCD] ?:"",
            resultMsg = r[ORDER_PAYMENT_EPAY.RESULTMSG] ?:"",
        )
    }

    // APP
    fun toOrderResponse(r: Record): OrderResponseDto {
        return OrderResponseDto(
            id = r[ORDER.ID]!!,
            martId = r[ORDER.MARTID]!!,
            userId = r[ORDER.USERID]!!,
            shipperId = r[ORDER.SHIPPERID],
            orderCode = r[ORDER.ORDERCODE]!!,
            ordererName = r[ORDER.ORDERERNAME]!!,
            ordererPhone = r[ORDER.ORDERERPHONE]!!,
            ordererAddress = r[ORDER.ORDERERADDRESS]!!,
            orderStatus = r[ORDER.ORDERSTATUS]!!.name,
            paymentStatus = r[ORDER.PAYMENTSTATUS]!!.name,
            refundStatus = r[ORDER.REFUNDSTATUS]!!.name,
            cancelStatus = r[ORDER.CANCELSTATUS]!!.name,
            cartData = emptyList(),
            cartDataCancel = emptyList(),
            cartTotal = r[ORDER.CARTTOTAL]!!,
            discount = r[ORDER.DISCOUNT]!!,
            deliveryFee = r[ORDER.DELIVERYFEE]!!,
            couponVolume = r[ORDER.COUPONVOLUME]!!,
            pointVolume = r[ORDER.POINTVOLUME]!!,
            pointAccumulate = r[ORDER.POINTACCUMULATE]!!,
            amount = r[ORDER.AMOUNT]!!,
            createdAt = r[ORDER.CREATEDAT]!!,
            updatedAt = r[ORDER.UPDATEDAT],
        )
    }


    fun toCartItemResponse(r: Record): CartItemDto {
        return CartItemDto(
            id = r[ORDER_CART_ITEMS.PRODUCTID]!!,
            name = r[ORDER_CART_ITEMS.NAME]!!,
            image = r[ORDER_CART_ITEMS.IMAGE]!!,
            price = r[ORDER_CART_ITEMS.PRICE]!!,
            quantity = r[ORDER_CART_ITEMS.QUANTITY]!!,
            categoryId = r[ORDER_CART_ITEMS.CATEGORYID]!!,
            categoryName = r[ORDER_CART_ITEMS.CATEGORYNAME]!!,

        )
    }
}
