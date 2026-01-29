package com.vietq.demo_map_app_backend.dto

import java.math.BigDecimal

data class ProductResponseDto(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val name: String,
    val price: BigDecimal,
    val image: String
)
