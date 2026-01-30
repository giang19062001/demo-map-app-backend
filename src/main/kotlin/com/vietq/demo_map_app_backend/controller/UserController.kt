package com.vietq.demo_map_app_backend.controller

import com.vietq.demo_map_app_backend.dto.UserResponseDto
import com.vietq.demo_map_app_backend.service.UserService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class UserController (
    private val userService: UserService
){
    @Operation(
        summary = "Get user list",
    )
    @GetMapping("/getUsers")
    fun getUsers(): List<UserResponseDto> {
        return userService.getUsers()
    }
}