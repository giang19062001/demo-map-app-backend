package com.vietq.demo_map_app_backend.dto

import io.swagger.v3.oas.annotations.media.Schema

data class MartResponseDto(
    val id: Long,
    val name: String,
    val image: String,
    val lat: Double,
    val lon: Double,
    val address: String?,
    val distance: Double
)


data class MartRequestDto(
    @field:Schema(
        example = "10.78296781157542"
    ) val lat: Double,

    @field:Schema(
        example = "106.70442680576058"
    ) val lon: Double,
)

data class MartLocationFromOrderResponseDto(
    val lat: Double,
    val lon: Double,
    val orderId: Long,
    val shipperId: Long,
    val martId: Long,
)