package com.vietq.demo_map_app_backend.controller

import com.vietq.demo_map_app_backend.dto.ProductResponseDto
import com.vietq.demo_map_app_backend.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/product")
class ProductController(
    private val productService: ProductService
) {


    @Operation(
        summary = "Lấy danh sách product theo category",
    )
    @GetMapping("/getProducts")
    fun getNearbyMarts(
        @Parameter(
            description = "ID của category. 0 là lấy tất cả sản phẩm, category ID > 0 là lọc sản phẩm theo thể loại",
        )
        @RequestParam(defaultValue = "0") categoryId: Long
    ): List<ProductResponseDto> {
        return productService.getProducts(categoryId)
    }

}