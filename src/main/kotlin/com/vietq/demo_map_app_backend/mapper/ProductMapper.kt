package com.vietq.demo_map_app_backend.mapper
import com.study.jooq.tables.Product.Companion.PRODUCT
import com.vietq.demo_map_app_backend.dto.ProductResponseDto
import org.jooq.Record
import org.springframework.stereotype.Component

@Component
class ProductMapper {
    fun toResponse(r: Record): ProductResponseDto =
        ProductResponseDto(
            id = r[PRODUCT.ID]!!,
            categoryId = r[PRODUCT.CATEGORYID]!!,
            categoryName = r.get("categoryName", String::class.java)!!,
            name = r[PRODUCT.NAME]!!,
            price =  r[PRODUCT.PRICE]!!,
            image = r[PRODUCT.IMAGE]!!
        )
}