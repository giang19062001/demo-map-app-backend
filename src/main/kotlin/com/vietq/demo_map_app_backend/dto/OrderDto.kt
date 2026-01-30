package com.vietq.demo_map_app_backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOrderDto(
    @field:Schema(
        example = "1",
        required = true
    )
    val userId: Long,

    @field:Schema(
        example = "1",
        required = true
    )
    val martId: Long,

    @field:Schema(
        example = "[{\"id\": 1, \"name\": \"Product 1\", \"image\": \"/uploads/products/1.png\", \"price\": 15000, \"quantity\": 2, \"categoryId\": 4, \"categoryName\": \"Beverage\"}, {\"id\": 2, \"name\": \"Product 2\", \"image\": \"/uploads/products/2.png\", \"price\": 15000, \"quantity\": 2, \"categoryId\": 2, \"categoryName\": \"Meat\"}, {\"id\": 3, \"name\": \"Product 3\", \"image\": \"/uploads/products/3.png\", \"price\": 15000, \"quantity\": 2, \"categoryId\": 1, \"categoryName\": \"Fruit\"}]",
        required = true
    )
    @field:NotEmpty(message = "cartData must not be empty")
    @field:Size(min = 1, message = "cartData must contain at least 1 item")
    val cartData: List<CartItemDto>,

    @field:Schema(
        example = "10",
        required = true
    )
    val pointAccumulate: BigDecimal,

    @field:Schema(
        example = "90000",
        required = true
    )
    val cartTotal: BigDecimal,

    @field:Schema(
        example = "0",
        required = true
    )
    val discount: BigDecimal,

    @field:Schema(
        example = "5000",
        required = true
    )
    val deliveryFee: BigDecimal,

    @field:Schema(
        example = "5000",
        required = true
    )
    val couponVolume: BigDecimal,

    @field:Schema(
        example = "500",
        required = true
    )
    val pointVolume: BigDecimal,

    @field:Schema(
        description = "VND",
        example = "89500",
        required = true
    )
    @field:DecimalMin(
        value = "1",
        inclusive = true,
        message = "Amount must be greater than 0"
    )
    val amount: BigDecimal,
    )

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

data class OrderDeliveryInfoDto(
    val shipperId: Long,
    val ordererName: String,
    val ordererPhone: String,
    val ordererAddress: String,
)

data class CartItemDto(
    val id: Long,
    val name: String,
    val image: String,
    @field:DecimalMin("1")
    val price: BigDecimal,
    @field:Min(1)
    val quantity: Int,
    val categoryId: Long,
    val categoryName: String
)


data class OrderResponseDto(
    val id: Long,
    val martId: Long,
    val userId: Long,
    val shipperId: Long?,
    val orderCode: String,
    val ordererName: String,
    val ordererPhone: String,
    val ordererAddress: String,
    val orderStatus: String,
    val paymentStatus: String,
    val refundStatus: String,
    val cancelStatus: String,
    val cartData: List<CartItemDto>,
    val cartDataCancel: List<CartItemDto>,
    val cartTotal: BigDecimal,
    val discount: BigDecimal,
    val deliveryFee: BigDecimal,
    val couponVolume: BigDecimal,
    val pointVolume: BigDecimal,
    val pointAccumulate: BigDecimal,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
