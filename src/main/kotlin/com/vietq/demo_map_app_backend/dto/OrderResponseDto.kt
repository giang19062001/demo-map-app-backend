package com.vietq.demo_map_app_backend.dto

import com.study.jooq.enums.OrderEpayPaytype
import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderPaymentstatus
import com.study.jooq.enums.OrderRefundstatus
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime


// FOR API 'cart'
data class CartItemDto(
    val id: Long,
    val name: String,
    val image: String,
    val price: Long,
    val quantity: Int,
    val categoryId: Long,
    val categoryName: String
)

// FOR API '/order/getOrders'
data class OrderResponseDto(
    val id: Long,
    val martId: Long,
    val userId: Long,
    val shipperId: Long?,
    val orderCode: String,
    val ordererName: String,
    val ordererPhone: String,
    val ordererAddress: String,
    val orderStatus: OrderOrderstatus,
    val paymentStatus: OrderPaymentstatus,
    val refundStatus: OrderRefundstatus,
    val cancelStatus: String,
    val cartData: List<CartItemDto>,
    val cartDataCancel: List<CartItemDto>,
    val cartTotal: Long,
    val discount: Long,
    val deliveryFee: Long,
    val couponVolume: Long,
    val pointVolume: Long,
    val pointAccumulate: Long,
    val amount: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)

// FOR API 'createOrder'
data class CreateOrderResponseDto(
    val resultCd: String,
    val resultMsg: String,
    val paymentId: String,
    val merId: String,
    val invoiceNo: String,
    val amount: String,
    val currency: String,
    val timeStamp: String,
    val payType: String,
    val payOption: String,
    val linkExptime: String,
    val paymentLink: String,
    val qrCode: String,
    val merchantToken: String
)

// FOR API '/admin/order/getOrders'
data class OrderAdminResponseDto(
    val id: Long,
    val orderCode: String,
    val ordererName: String,
    val cartDataOriginal: List<CartItemDto>,
    val cartDataCurrent: List<CartItemDto>,
    val cartDataCancel: List<CartItemDto>,
    val discount: Long,
    val deliveryFee: Long,
    val couponVolume: Long,
    val pointVolume: Long,
    val cartTotal: Long,
    val amount: String,
    val remainAmount: String?,
    val createdAt: LocalDateTime,
    val trxId: String?,
    val merTrxId: String?,
    val status: String?,
    val payType: OrderEpayPaytype?,
    val bankId: String?,
    val cardNo: String?,
    val paymentStatus: OrderPaymentstatus,
    val refundStatus: OrderRefundstatus,
    val resultCd: String?,
    val resultMsg: String?
)