package com.vietq.demo_map_app_backend.controller

import com.vietq.demo_map_app_backend.dto.ProductResponseDto
import com.vietq.demo_map_app_backend.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/product")
class ProductController(
    private val productService: ProductService
) {


    @Operation(summary = "Get product list by category",)
    @GetMapping("/getProducts")
    fun getNearbyMarts(
        @Parameter(description = "ID of category: 0 is get product list by all categories, category ID > 0 is filtering product by specific category")
        @RequestParam(defaultValue = "0") categoryId: Long
    ): List<ProductResponseDto> {
        return productService.getProducts(categoryId)
    }

}