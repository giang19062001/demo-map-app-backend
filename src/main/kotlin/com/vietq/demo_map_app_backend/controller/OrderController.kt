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
    @Operation(
        summary = "Lấy danh sách order theo id của user",
    )
    @GetMapping("/getOrders")
    fun getOrders(
        @Parameter(
            description = "ID của user - hardcode phía App ( 1 Or 2)",
        )
        @RequestParam(defaultValue = "1") userId: Long
    ): List<OrderResponseDto> {
        return orderService.getOrders(userId)
    }

    @Operation(
        summary = "Lấy danh sách order chi tiết ",
    )
    @GetMapping("/getOrder/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<OrderResponseDto> {
        return orderService.getOrderById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @Operation(
        summary = "cập nhập order với trạng thái là hoàn thành",
    )
    @PutMapping("/markAsCompleteOrder/{id}")
    fun markAsCompleteOrder(@PathVariable id: Long): ResponseEntity<Boolean> {
        return orderService.markAsCompleteOrder(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

}