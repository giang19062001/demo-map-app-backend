package com.vietq.demo_map_app_backend.dto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDateTime


// FOR API 'createOrder'
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
        example = "[{\"id\": 1, \"name\": \"Product 1\", \"image\": \"/uploads/products/1.png\", \"price\": 20000, \"quantity\": 2, \"categoryId\": 4, \"categoryName\": \"Beverage\"}, {\"id\": 2, \"name\": \"Product 2\", \"image\": \"/uploads/products/2.png\", \"price\": 25000, \"quantity\": 2, \"categoryId\": 2, \"categoryName\": \"Meat\"}, {\"id\": 3, \"name\": \"Product 3\", \"image\": \"/uploads/products/3.png\", \"price\": 15000, \"quantity\": 2, \"categoryId\": 1, \"categoryName\": \"Fruit\"}]",
        required = true
    )
    @field:NotEmpty(message = "cartData must not be empty")
    @field:Size(min = 1, message = "cartData must contain at least 1 item")
    val cartData: List<CartItemDto>,

    @field:Schema(
        example = "10",
        required = true
    )
    val pointAccumulate: Long,

    @field:Schema(
        example = "120000",
        required = true
    )
    val cartTotal: Long,

    @field:Schema(
        example = "0",
        required = true
    )
    val discount: Long,

    @field:Schema(
        example = "10000",
        required = true
    )
    val deliveryFee: Long,

    @field:Schema(
        example = "5000",
        required = true
    )
    val couponVolume: Long,

    @field:Schema(
        example = "500",
        required = true
    )
    val pointVolume: Long,

    @field:Schema(
        description = "VND",
        example = "124500",
        required = true
    )
    @field:DecimalMin(
        value = "1",
        inclusive = true,
        message = "Amount must be greater than 0"
    )
    val amount: Long,
    )

// FOR API 'createOrder' (optional)
data class OrderDeliveryInfoDto(
    val shipperId: Long,
    val ordererName: String,
    val ordererPhone: String,
    val ordererAddress: String,
)

// FOR REPOSITORY 'insertCancelEntire'
data class InsertCancelEntireDto(
    val orderCode: String,
    val cancelCode: String,
    val amountOriginal: Long,
    val amountCancel: Long,
)

data class InsertCancelPartialDto(
    val orderCode: String,
    val cancelCode: String,
    val productId: Long,
    val name: String,
    val image: String,
    val quantity: Int,
    val price: Long,
    val categoryId: Long,
    val categoryName: String,
    val amountActual: Long,
    val amountCancel: Long
)


// FOR API '/admin/order/cancelPartialOrder'
data class OrderCancelPartialDto(
    val productId: Long,
    val name: String,
    val image: String,
    val quantity: Int,
    val price: Long,
    val categoryId: Long,
    val categoryName: String
)
