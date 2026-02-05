package com.vietq.demo_map_app_backend.utils

import com.vietq.demo_map_app_backend.dto.CartItemDto

public fun calculateCartData(cartDataOriginal: List<CartItemDto>, cartDataCancel: List<CartItemDto>): List<CartItemDto> {
    val cancelMap = cartDataCancel.groupBy { it.id }
        .mapValues { (_, items) -> items.sumOf { it.quantity } }

    val remainingItems = cartDataOriginal.mapNotNull { item ->
        val canceledQuantity = cancelMap[item.id] ?: 0
        val remainingQuantity = item.quantity - canceledQuantity

        if (remainingQuantity > 0) {
            item.copy(quantity = remainingQuantity)
        } else {
            null
        }
    }
    return remainingItems
}

public fun groupCartItems(items: List<CartItemDto>): List<CartItemDto> {
    return items.groupBy { it.id }
        .map { (_, groupedItems) ->
            val firstItem = groupedItems.first()
            val totalQuantity = groupedItems.sumOf { it.quantity }
            firstItem.copy(quantity = totalQuantity)
        }
}
