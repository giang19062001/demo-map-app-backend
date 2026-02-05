package com.vietq.demo_map_app_backend.controller

import com.vietq.demo_map_app_backend.dto.OrderAdminResponseDto
import com.vietq.demo_map_app_backend.dto.OrderCancelPartialDto
import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import com.vietq.demo_map_app_backend.service.OrderAdminService
import com.vietq.demo_map_app_backend.utils.SuccessResponse
import com.vietq.demo_map_app_backend.utils.toSuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.apache.coyote.BadRequestException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/admin/order")
class OrderAdminController(
    private val orderAdminService: OrderAdminService
) {

    @Operation(summary = "Get order list by userId ( ADMIN )",)
    @GetMapping("/getOrders")
    fun getAdminOrders(@Parameter(description = "ID user: 1, 2, 3",) @RequestParam(defaultValue = "1") userId: Long): List<OrderAdminResponseDto> {
        return orderAdminService.getAdminOrders(userId)
    }

    @Operation(summary = "Get order by orderCode ( ADMIN )",)
    @GetMapping("/getOrderByCode/{orderCode}")
    fun getAdminOrderByCode(@PathVariable orderCode: String): OrderAdminResponseDto? {
        return orderAdminService.getAdminOrderByCode(orderCode)
    }


    @Operation(summary = "Cancel entire order (ADMIN)")
    @PutMapping("/cancelEntireOrder/{orderCode}")
    fun cancelAdminEntireOrder(@PathVariable orderCode: String, ): ResponseEntity<SuccessResponse<Boolean>> {
        val resut = orderAdminService.cancelAdminEntireOrder(orderCode)
        return ResponseEntity.ok(
            resut.toSuccessResponse("Cancel success")
        )
    }

    @Operation(summary = "Cancel partial order (ADMIN)")
    @PutMapping("/cancelPartialOrder/{orderCode}")
    fun cancelPartialOrder(@PathVariable orderCode: String, @RequestBody dto: OrderCancelPartialDto):  ResponseEntity<SuccessResponse<Boolean>> {
        val resut = orderAdminService.cancelPartialOrder(orderCode, dto)
        return ResponseEntity.ok(
            resut.toSuccessResponse("Cancel success")
        )
    }

}