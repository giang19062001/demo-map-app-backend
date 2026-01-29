package com.vietq.demo_map_app_backend.mapper
import com.study.jooq.tables.Mart.Companion.MART
import com.study.jooq.tables.User.Companion.USER
import com.vietq.demo_map_app_backend.dto.MartResponseDto
import com.vietq.demo_map_app_backend.dto.UserResponseDto
import org.jooq.Field
import org.jooq.Record
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toResponse(r: Record): UserResponseDto =
        UserResponseDto(
            id = r[USER.ID]!!,
            name = r[USER.NAME]!!,
            phone = r[USER.PHONE]!!,
            email = r[USER.EMAIL]!!,
            address = r[USER.ADDRESS]!!,
        )
}