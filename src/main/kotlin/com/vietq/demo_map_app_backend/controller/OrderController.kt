package com.vietq.demo_map_app_backend.controller

import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import com.vietq.demo_map_app_backend.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/order")
class OrderController(
    private val orderService: OrderService
) {
    @Operation(summary = "Get order list by userId",)
    @GetMapping("/getOrders")
    fun getOrders(@Parameter(description = "ID user: 1, 2, 3",) @RequestParam(defaultValue = "1") userId: Long): List<OrderResponseDto> {
        return orderService.getOrders(userId)
    }

    @Operation(summary = "Get order detail by id",)
    @GetMapping("/getOrder/{orderId}")
    fun getOrderById(@PathVariable orderId: Long): ResponseEntity<OrderResponseDto> {
        return orderService.getOrderById(orderId)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @Operation(summary = "Update order delivery success (simulator)",)
    @PutMapping("/markAsCompleteOrder/{orderId}")
    fun markAsCompleteOrder(@PathVariable orderId: Long): ResponseEntity<Boolean> {
        return orderService.markAsCompleteOrder(orderId)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

}