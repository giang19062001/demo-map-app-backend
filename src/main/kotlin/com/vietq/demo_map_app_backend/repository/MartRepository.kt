package com.vietq.demo_map_app_backend.repository

import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.tables.Mart.Companion.MART
import com.study.jooq.tables.Order.Companion.ORDER
import com.vietq.demo_map_app_backend.dto.MartLocationFromOrderResponseDto
import com.vietq.demo_map_app_backend.dto.MartResponseDto
import com.vietq.demo_map_app_backend.mapper.MartMapper
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class MartRepository(
    private val dsl: DSLContext,
    private val martMapper: MartMapper
) {
    fun getMartLocationFromOrder(orderId: Long): MartLocationFromOrderResponseDto? {
        return dsl
            .select(
                ORDER.ID,
                ORDER.MARTID,
                ORDER.SHIPPERID,
                MART.LAT,
                MART.LON
            )
            .from(ORDER)
            .join(MART)
            .on(ORDER.MARTID.eq(MART.ID))
            .where(
                ORDER.ID.eq(orderId), ORDER.ORDERSTATUS.eq(OrderOrderstatus.DELIVERING)
            )
            .fetchOne { r ->
                MartLocationFromOrderResponseDto(
                    orderId = r[ORDER.ID]!!,
                    martId = r[ORDER.MARTID]!!,
                    shipperId = r[ORDER.SHIPPERID]!!,
                    lat = r[MART.LAT]!!,
                    lon = r[MART.LON]!!
                )
            }
    }
    fun findNearestMarts(
        lat: Double,
        lon: Double,
        radiusKm: Double,
        limit: Int
    ): List<MartResponseDto> {

        // công thức tính distance
        val distanceField = DSL.field(
            """
            6371 * acos(
              cos(radians({0})) * cos(radians({1})) *
              cos(radians({2}) - radians({3})) +
              sin(radians({0})) * sin(radians({1}))
            )
            """,
            Double::class.java,
            DSL.inline(lat),
            MART.LAT,
            MART.LON,
            DSL.inline(lon)
        ).`as`("distance")

        return dsl
            .select(
                MART.ID,
                MART.NAME,
                MART.LAT,
                MART.LON,
                MART.IMAGE,
                MART.ADDRESS,
                distanceField
            )
            .from(MART)
            .where(MART.ACTIVE.eq(1))
            .and(MART.LON.between(lon - 0.1, lon + 0.1))
            .and(MART.LAT.between(lat - 0.1, lat + 0.1))
            .having(distanceField.le(radiusKm))
            .orderBy(distanceField.asc())
            .limit(limit)
            .fetch { r ->
                martMapper.toResponse(r, distanceField)
            }
    }
}
