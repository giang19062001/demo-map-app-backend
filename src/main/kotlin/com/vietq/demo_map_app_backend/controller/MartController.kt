package com.vietq.demo_map_app_backend.controller

import com.vietq.demo_map_app_backend.dto.MartRequestDto
import com.vietq.demo_map_app_backend.dto.MartResponseDto
import com.vietq.demo_map_app_backend.service.MartService
import io.swagger.v3.oas.annotations.Operation
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/mart")
class MartController(
    private val martService: MartService
) {

    @Operation(summary = "Get list of Mars nearby User location")
    @GetMapping("/nearby")
    fun getNearbyMarts(@ParameterObject dto : MartRequestDto): List<MartResponseDto> {
        return martService.getNearestMarts(dto)
    }
}
