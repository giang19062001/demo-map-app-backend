package com.vietq.demo_map_app_backend.service

import com.vietq.demo_map_app_backend.dto.MartLocationFromOrderResponseDto
import com.vietq.demo_map_app_backend.dto.MartRequestDto
import com.vietq.demo_map_app_backend.dto.MartResponseDto
import com.vietq.demo_map_app_backend.repository.MartRepository
import org.springframework.stereotype.Service

@Service
class MartService(
    private val martRepository: MartRepository,

) {
    private val radius: Double = 5.0 // km
    private  val limit: Int = 10

    fun getNearestMarts(dto: MartRequestDto): List<MartResponseDto> {
        return martRepository.findNearestMarts(dto.lat, dto.lon, radius, limit)
    }
    fun getMartLocationFromOrder(orderId: Long): MartLocationFromOrderResponseDto? {
        return martRepository.getMartLocationFromOrder(orderId)
    }
}
