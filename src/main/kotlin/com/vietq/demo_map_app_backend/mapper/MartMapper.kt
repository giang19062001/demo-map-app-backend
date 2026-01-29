package com.vietq.demo_map_app_backend.mapper
import com.study.jooq.tables.Mart.Companion.MART
import com.vietq.demo_map_app_backend.dto.MartResponseDto
import org.jooq.Field
import org.jooq.Record
import org.springframework.stereotype.Component

@Component
class MartMapper {
    fun toResponse(r: Record, distanceField: Field<Double>): MartResponseDto =
        MartResponseDto(
            id = r[MART.ID]!!,
            name = r[MART.NAME]!!,
            lat = r[MART.LAT]!!,
            lon = r[MART.LON]!!,
            image = r[MART.IMAGE]!!,
            address = r[MART.ADDRESS],
            distance = r[distanceField]
        )
}