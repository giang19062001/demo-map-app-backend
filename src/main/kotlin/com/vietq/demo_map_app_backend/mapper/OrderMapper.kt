package com.vietq.demo_map_app_backend.mapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.study.jooq.tables.Mart.Companion.MART
import com.study.jooq.tables.Order.Companion.ORDER
import com.vietq.demo_map_app_backend.dto.CartDto
import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import org.jooq.Field
import org.jooq.Record
import org.springframework.stereotype.Component

@Component
class OrderMapper {

    private val objectMapper = jacksonObjectMapper()
    fun toResponse(
        r: Record
    ): OrderResponseDto {

        val cartItems: List<CartDto> =
            r[ORDER.CARTDATA]?.data()?.let { json ->
                objectMapper.readValue(
                    json,
                    object : TypeReference<List<CartDto>>() {}
                )
            } ?: emptyList()

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
            cartData = cartItems,
            cartDataCancel = emptyList(),
            cartTotal = r[ORDER.CARTTOTAL]!!,
            discount = r[ORDER.DISCOUNT]!!,
            deliveryFee = r[ORDER.DELIVERYFEE]!!,
            couponVolume = r[ORDER.COUPONVOLUME]!!,
            pointVolume = r[ORDER.POINTVOLUME]!!,
            pointAccumulate = r[ORDER.POINTACCUMULATE]!!,
            amount = r[ORDER.AMOUNT]!!,
            createdAt = r[ORDER.CREATEDAT]!!,
            updatedAt = r[ORDER.UPDATEDAT]
        )
    }
}
