package com.vietq.demo_map_app_backend.service

import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderPaymentstatus
import com.vietq.demo_map_app_backend.dto.OrderDeliveryInfoDto
import com.vietq.demo_map_app_backend.dto.CreateOrderDto
import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import com.vietq.demo_map_app_backend.repository.OrderRepository
import org.springframework.stereotype.Service


@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {

    fun getOrders(userId: Long): List<OrderResponseDto> {
        return orderRepository.getOrders(userId)
    }
    fun getOrderById(orderId: Long): OrderResponseDto? {
        return orderRepository.getOrderById(orderId)
    }
    fun getOrderByCode(orderCode: String): OrderResponseDto? {
        return orderRepository.getOrderByCode(orderCode)
    }

    fun markAsCompleteOrder(orderId: Long): Boolean {
        return orderRepository.markAsCompleteOrder(orderId)
    }
    fun insertOrder(dto: CreateOrderDto, orderCode: String, userDeliveryInfo: OrderDeliveryInfoDto): Long {
        return orderRepository.insertOrder(dto, orderCode, userDeliveryInfo)
    }

    fun changePaymentStatusOrder(orderCode: String, paymentStatus: OrderPaymentstatus, orderStatus : OrderOrderstatus): Boolean {
        return orderRepository.changePaymentStatusOrder(orderCode, paymentStatus, orderStatus)
    }
}