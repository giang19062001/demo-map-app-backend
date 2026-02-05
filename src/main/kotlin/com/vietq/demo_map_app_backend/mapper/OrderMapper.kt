package com.vietq.demo_map_app_backend.mapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.study.jooq.enums.OrderEpayPaytype
import com.study.jooq.enums.OrderPaymentstatus
import com.study.jooq.tables.Order.Companion.ORDER
import com.study.jooq.tables.OrderCartItems.Companion.ORDER_CART_ITEMS
import com.study.jooq.tables.OrderCartItemsCancel.Companion.ORDER_CART_ITEMS_CANCEL
import com.study.jooq.tables.OrderEpay.Companion.ORDER_EPAY
import com.vietq.demo_map_app_backend.dto.CartItemDto
import com.vietq.demo_map_app_backend.dto.OrderAdminResponseDto
import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import org.jooq.Record
import org.springframework.stereotype.Component
import kotlin.collections.emptyList

@Component
class OrderMapper {

    private val objectMapper = jacksonObjectMapper()
    // ADMIN
    fun toOrderAdminResponse(r: Record): OrderAdminResponseDto {
        return OrderAdminResponseDto(
            id = r[ORDER.ID]!!,
            orderCode = r[ORDER.ORDERCODE]!!,
            ordererName = r[ORDER.ORDERERNAME]!!,
            cartDataOriginal = emptyList(),
            cartDataCurrent = emptyList(),
            cartDataCancel = emptyList(),
            amount = r[ORDER.AMOUNT]?.toString() ?: "0",
            remainAmount = r[ORDER_EPAY.REMAINAMOUNT]?.toString() ?: "-",
            discount = r[ORDER.DISCOUNT]!!,
            deliveryFee = r[ORDER.DELIVERYFEE]!!,
            couponVolume = r[ORDER.COUPONVOLUME]!!,
            pointVolume = r[ORDER.POINTVOLUME]!!,
            cartTotal = r[ORDER.CARTTOTAL]!!,
            createdAt = r[ORDER.CREATEDAT]!!,
            trxId = r[ORDER_EPAY.TRXID] ?: "",
            status = r[ORDER_EPAY.STATUS] ?: "",
            merTrxId = r[ORDER_EPAY.MERTRXID] ?: "",
            payType = r[ORDER_EPAY.PAYTYPE],
            bankId = r[ORDER_EPAY.BANKID] ?: "",
            cardNo = r[ORDER_EPAY.CARDNO] ?: "",
            resultCd = r[ORDER_EPAY.RESULTCD] ?: "",
            resultMsg = r[ORDER_EPAY.RESULTMSG] ?: "",
            paymentStatus = r[ORDER.PAYMENTSTATUS]!!,
            refundStatus = r[ORDER.REFUNDSTATUS]!!,
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
            orderStatus = r[ORDER.ORDERSTATUS]!!,
            paymentStatus = r[ORDER.PAYMENTSTATUS]!!,
            refundStatus = r[ORDER.REFUNDSTATUS]!!,
            cancelStatus = "", // optional
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
    fun toCartItemCancelResponse(r: Record): CartItemDto {
        return CartItemDto(
            id = r[ORDER_CART_ITEMS_CANCEL.PRODUCTID]!!,
            name = r[ORDER_CART_ITEMS_CANCEL.NAME]!!,
            image = r[ORDER_CART_ITEMS.IMAGE]!!,
            price = r[ORDER_CART_ITEMS_CANCEL.PRICE]!!,
            quantity = r[ORDER_CART_ITEMS_CANCEL.QUANTITY]!!,
            categoryId = r[ORDER_CART_ITEMS_CANCEL.CATEGORYID]!!,
            categoryName = r[ORDER_CART_ITEMS_CANCEL.CATEGORYNAME]!!,
            )
    }
}
