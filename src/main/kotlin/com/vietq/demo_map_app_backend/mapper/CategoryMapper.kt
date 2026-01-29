package com.vietq.demo_map_app_backend.mapper
import com.study.jooq.tables.Category.Companion.CATEGORY
import com.vietq.demo_map_app_backend.dto.CateResponseDto
import org.jooq.Record
import org.springframework.stereotype.Component


@Component
class CategoryMapper {
    fun toResponse(r: Record): CateResponseDto =
        CateResponseDto(
            id = r[CATEGORY.ID]!!,
            name = r[CATEGORY.NAME]!!,
            image = r[CATEGORY.IMAGE]!!,
        )
}