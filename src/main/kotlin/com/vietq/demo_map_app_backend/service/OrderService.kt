package com.vietq.demo_map_app_backend.service

import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderPaymentstatus
import com.study.jooq.enums.OrderRefundstatus
import com.vietq.demo_map_app_backend.dto.CartItemDto
import com.vietq.demo_map_app_backend.dto.OrderDeliveryInfoDto
import com.vietq.demo_map_app_backend.dto.CreateOrderDto
import com.vietq.demo_map_app_backend.dto.OrderResponseDto
import com.vietq.demo_map_app_backend.repository.OrderRepository
import com.vietq.demo_map_app_backend.utils.EpayMerchantCodeEnum
import com.vietq.demo_map_app_backend.utils.EpayTransactionResultCodeEnum
import com.vietq.demo_map_app_backend.utils.EpayTransactionStatuEnum
import com.vietq.demo_map_app_backend.utils.calculateCartData
import com.vietq.demo_map_app_backend.utils.groupCartItems
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    private val log = LoggerFactory.getLogger(PaymentService::class.java)
    private  val logbase = "OrderService"

    /**
     * Prevent errors when merchants is not exist --> Mark as 'paymentStatus' is 'NOT_YET'
     * @return: <PaymentStatus: enum(NOT_YET | PENDING | PAYMENT_SUCCESS | PAYMENT_FAIL), Message: String>
     */
    fun fallbackOrderMerchant(merTrxId: String, invoiceNo: String, paymentStatus: OrderPaymentstatus, resultMsg: String): Triple<OrderPaymentstatus, String, Boolean> {
        var finalPaymentStatus = paymentStatus
        var finalResultMsg = resultMsg

        // 'merTrxId' = -1 --> ERROR (TRANSACTION HAVE NOT PAID YET)
        if (EpayMerchantCodeEnum.isFailed(merTrxId)) {
            finalPaymentStatus = OrderPaymentstatus.NOT_YET
            finalResultMsg = EpayMerchantCodeEnum.MERCHANT_FAIL.description

            // UPDATE PAYMENT STATUS -> WAITING
            orderRepository.changeOrderAndPaymentStatus(invoiceNo, finalPaymentStatus, OrderOrderstatus.WAITING)
            return Triple(finalPaymentStatus, finalResultMsg, false)
        }

        return Triple(finalPaymentStatus, finalResultMsg, true)
    }

    /**
     * Update 'paymentStatus' on 'order' table
     * @return: enum(NOT_YET | PENDING | PAYMENT_SUCCESS | PAYMENT_FAIL)
     */
    public fun updateOrderPaymentStatus(invoiceNo: String, resultCd: String, status: String): OrderPaymentstatus {
        log.info("logbase={} --- updateOrderPaymentStatus: resultCd={}, status={}",logbase, resultCd, status)

        var paymentStatus = OrderPaymentstatus.NOT_YET

        when {
            // SUCCESS
            EpayTransactionResultCodeEnum.isSuccess(resultCd) && EpayTransactionStatuEnum.isSuccess(status) -> {
                paymentStatus = OrderPaymentstatus.PAYMENT_SUCCESS
                orderRepository.changeOrderAndPaymentStatus(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.APPROVED
                )
            }
            // SUCCESS BUT REFUND
            EpayTransactionResultCodeEnum.isSuccess(resultCd) && EpayTransactionStatuEnum.isRefund(status) -> {
                paymentStatus = OrderPaymentstatus.PAYMENT_SUCCESS
                orderRepository.changeOrderAndPaymentStatus(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.APPROVED
                )
            }

            // PENDING
            EpayTransactionResultCodeEnum.isPending(resultCd) -> {
                paymentStatus = OrderPaymentstatus.PENDING
                orderRepository.changeOrderAndPaymentStatus(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.WAITING
                )
            }

            // NOT YET
            EpayTransactionResultCodeEnum.isNotYet(resultCd) -> {
                paymentStatus = OrderPaymentstatus.NOT_YET

                orderRepository.changeOrderAndPaymentStatus(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.WAITING
                )
            }

            // FAIL
            else -> {
                paymentStatus = OrderPaymentstatus.PAYMENT_FAIL
                orderRepository.changeOrderAndPaymentStatus(
                    invoiceNo,
                    paymentStatus,
                    OrderOrderstatus.REFUSE
                )
            }
        }
        return paymentStatus
    }

    /**
     * Get order info and re-calculate cart data with items cart was cancelled
     * @return: OrderResponseDto
     */
    fun getOrderById(orderId: Long): OrderResponseDto? {
        val order =  orderRepository.getOrderById(orderId)
        return order?.let {
            var cartDataResult: List<CartItemDto> = emptyList()
            var cartDataCancelResult: List<CartItemDto> = emptyList()

            if(order.refundStatus == OrderRefundstatus.ENTIRE) {
                cartDataResult = emptyList()
                cartDataCancelResult = order.cartData
            }else if(order.refundStatus == OrderRefundstatus.PARTIAL) {
                // cartDataResult = cartData - cartDataCancel
                cartDataResult = calculateCartData(it.cartData, it.cartDataCancel)

                // Group cartDataCancelResult by id
                cartDataCancelResult = groupCartItems(it.cartDataCancel)
            }
            else if(order.refundStatus == OrderRefundstatus.NONE) {
                cartDataResult = order.cartData
                cartDataCancelResult =  emptyList()
            }

            it.copy(
                cartData = cartDataResult,
                cartDataCancel = cartDataCancelResult
            )
        }
    }

    fun getOrders(userId: Long): List<OrderResponseDto> {
        return orderRepository.getOrders(userId)
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

}