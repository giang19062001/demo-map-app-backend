package com.vietq.demo_map_app_backend.mapper
import com.study.jooq.tables.Banner.Companion.BANNER
import com.vietq.demo_map_app_backend.dto.BannerResponseDto
import org.springframework.stereotype.Component
import org.jooq.Record


@Component
class BannerMapper {
    fun toResponse(r: Record): BannerResponseDto =
        BannerResponseDto(
            id = r[BANNER.ID]!!,
            image = r[BANNER.IMAGE]!!
        )
}