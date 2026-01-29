package com.vietq.demo_map_app_backend.service

import com.vietq.demo_map_app_backend.dto.ProductResponseDto
import com.vietq.demo_map_app_backend.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun getProducts(categoryId: Long): List<ProductResponseDto> {
        return productRepository.getProducts(categoryId)
    }
}