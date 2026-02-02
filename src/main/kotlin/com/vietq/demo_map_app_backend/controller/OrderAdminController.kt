package com.vietq.demo_map_app_backend.controller
import com.vietq.demo_map_app_backend.dto.OrderAdminResponseDto
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
@RequestMapping("/api/admin/order")
class OrderAdminController(
    private val orderService: OrderService
) {

    @Operation(
        summary = "Get order list by userId ( ADMIN )",
    )
    @GetMapping("/getOrders")
    fun getAdminOrders(
        @Parameter(
            description = "ID user: 1, 2, 3",
        )
        @RequestParam(defaultValue = "1") userId: Long
    ): List<OrderAdminResponseDto> {
        return orderService.getAdminOrders(userId)
    }
}