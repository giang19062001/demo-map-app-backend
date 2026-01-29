package com.vietq.demo_map_app_backend.service

import com.vietq.demo_map_app_backend.dto.UserResponseDto
import com.vietq.demo_map_app_backend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun getUser(userId: Long): UserResponseDto? {
        return userRepository.getUser(userId)
    }
}