package com.vietq.demo_map_app_backend.repository

import com.study.jooq.tables.Banner.Companion.BANNER
import com.vietq.demo_map_app_backend.dto.BannerResponseDto
import com.vietq.demo_map_app_backend.mapper.BannerMapper
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BannerRepository(
    private val dsl: DSLContext,
    private val bannerMapper: BannerMapper
) {
    fun getBanners(): List<BannerResponseDto> {
        return dsl
            .select(BANNER.ID, BANNER.IMAGE)
            .from(BANNER)
            .fetch { r ->
                bannerMapper.toResponse(r)
            }
    }
}