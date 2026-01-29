package com.vietq.demo_map_app_backend.repository

import com.study.jooq.tables.Category.Companion.CATEGORY
import com.study.jooq.tables.Product.Companion.PRODUCT
import com.vietq.demo_map_app_backend.dto.ProductResponseDto
import com.vietq.demo_map_app_backend.mapper.ProductMapper
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class ProductRepository(
    private val dsl: DSLContext,
    private val productMapper: ProductMapper
) {

    fun getProducts(categoryId: Long): List<ProductResponseDto> {

        val condition = if (categoryId == 0L) {
            DSL.noCondition() // GET ALL khi category = 0 (ALL)
        } else {
            PRODUCT.CATEGORYID.eq(categoryId)
        }
        return dsl
            .select(
                PRODUCT.ID,
                PRODUCT.CATEGORYID,
                CATEGORY.NAME.`as`("categoryName"),
                PRODUCT.NAME,
                PRODUCT.PRICE,
                PRODUCT.IMAGE
            )
            .from(PRODUCT)
            .join(CATEGORY)
            .on(PRODUCT.CATEGORYID.eq(CATEGORY.ID))
            .where(condition)
            .fetch { r ->
                productMapper.toResponse(r)
            }
    }
}
