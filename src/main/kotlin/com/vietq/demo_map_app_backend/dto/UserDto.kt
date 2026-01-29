package com.vietq.demo_map_app_backend.dto

import java.math.BigDecimal


data class UserResponseDto(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
)
