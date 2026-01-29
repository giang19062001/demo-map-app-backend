package com.vietq.demo_map_app_backend.controller

import com.vietq.demo_map_app_backend.dto.BannerResponseDto
import com.vietq.demo_map_app_backend.service.BannerService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/banner")
class BannerController (
    private val bannerService: BannerService
){
    @Operation(
        summary = "Get banner list",
    )
    @GetMapping("/getBanners")
    fun getBanners(): List<BannerResponseDto> {
        return bannerService.getBanners()
    }
}