package com.vietq.demo_map_app_backend.service

import com.vietq.demo_map_app_backend.dto.BannerResponseDto
import com.vietq.demo_map_app_backend.repository.BannerRepository
import org.springframework.stereotype.Service

@Service
class BannerService(
    private val bannerRepository: BannerRepository,
) {

    fun getBanners(): List<BannerResponseDto> {
        return bannerRepository.getBanners()
    }
}