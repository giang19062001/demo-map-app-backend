package com.vietq.demo_map_app_backend.controller

import com.vietq.demo_map_app_backend.dto.CateResponseDto
import com.vietq.demo_map_app_backend.service.CategoryService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/category")
class CategoryController (
    private val categoryService: CategoryService

){
    @Operation(summary = "Get category list",)
    @GetMapping("/getCategories")
    fun getCategories(): List<CateResponseDto> {
        return categoryService.getCategories()
    }
}