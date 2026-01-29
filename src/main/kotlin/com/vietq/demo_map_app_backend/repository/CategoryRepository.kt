package com.vietq.demo_map_app_backend.repository

import com.study.jooq.tables.Category.Companion.CATEGORY
import com.vietq.demo_map_app_backend.dto.BannerResponseDto
import com.vietq.demo_map_app_backend.dto.CateResponseDto
import com.vietq.demo_map_app_backend.mapper.BannerMapper
import com.vietq.demo_map_app_backend.mapper.CategoryMapper
import org.jooq.DSLContext
import org.springframework.stereotype.Repository


@Repository
class CategoryRepository(
    private val dsl: DSLContext,
    private val categoryMapper: CategoryMapper
) {
    fun getCategories(): List<CateResponseDto> {
        return dsl
            .select(
                CATEGORY.ID,
                CATEGORY.NAME,
                CATEGORY.IMAGE,
            )
            .from(CATEGORY)
            .fetch { r ->
                categoryMapper.toResponse(r)
            }
    }
}