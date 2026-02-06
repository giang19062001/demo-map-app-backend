package com.vietq.demo_map_app_backend.repository

import com.study.jooq.tables.User.Companion.USER
import com.vietq.demo_map_app_backend.dto.UserResponseDto
import com.vietq.demo_map_app_backend.mapper.UserMapper
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val dsl: DSLContext,
    private val userMapper: UserMapper
) {

    fun getUsers(): List<UserResponseDto> {
        return dsl
            .select(USER.ID, USER.NAME, USER.PHONE, USER.EMAIL, USER.ADDRESS,)
            .from(USER)
            .fetch { r ->
                userMapper.toResponse(r)
            }
    }

    fun getUser(userId: Long): UserResponseDto? {
        return dsl
            .select(USER.ID, USER.NAME, USER.PHONE, USER.EMAIL, USER.ADDRESS,)
            .from(USER)
            .where(USER.ID.eq(userId))
            .fetchOne { r ->
                userMapper.toResponse(r)
            }
    }
}