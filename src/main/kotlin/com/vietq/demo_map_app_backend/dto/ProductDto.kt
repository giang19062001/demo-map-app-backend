package com.vietq.demo_map_app_backend.dto


data class ProductResponseDto(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val name: String,
    val price: Long,
    val image: String
)
