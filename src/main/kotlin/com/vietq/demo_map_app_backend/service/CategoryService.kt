package com.vietq.demo_map_app_backend.service

import com.vietq.demo_map_app_backend.dto.CateResponseDto
import com.vietq.demo_map_app_backend.repository.CategoryRepository
import org.springframework.stereotype.Service


@Service
class CategoryService (
    private val categoryRepository: CategoryRepository,

    ){
    fun getCategories(): List<CateResponseDto> {
        return categoryRepository.getCategories()
    }
}