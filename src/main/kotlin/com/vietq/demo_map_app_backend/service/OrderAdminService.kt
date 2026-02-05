package com.vietq.demo_map_app_backend.service

import com.study.jooq.enums.OrderOrderstatus
import com.study.jooq.enums.OrderRefundstatus
import com.vietq.demo_map_app_backend.dto.CartItemDto
import com.vietq.demo_map_app_backend.dto.InsertCancelEntireDto
import com.vietq.demo_map_app_backend.dto.InsertCancelPartialDto
import com.vietq.demo_map_app_backend.dto.OrderAdminResponseDto
import com.vietq.demo_map_app_backend.dto.OrderCancelPartialDto
import com.vietq.demo_map_app_backend.repository.OrderAdminRepository
import com.vietq.demo_map_app_backend.utils.EpayTransactionResultCodeEnum
import com.vietq.demo_map_app_backend.utils.calculateCartData
import com.vietq.demo_map_app_backend.utils.groupCartItems
import org.apache.coyote.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode


@Service
class OrderAdminService(
    private val orderAdminRepository: OrderAdminRepository,
    private val paymentService: PaymentService,

    ) {
    private  val logbase = "OrderAdminService"
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    fun getAdminOrders(userId: Long): List<OrderAdminResponseDto> {
        return orderAdminRepository.getAdminOrders(userId)
    }

    fun getAdminOrderByCode(orderCode: String): OrderAdminResponseDto? {
        val order = orderAdminRepository.getAdminOrderByCode(orderCode)
        return order?.let {
            var cartDataCurrentResult: List<CartItemDto> = emptyList()
            var cartDataCancelResult: List<CartItemDto> = emptyList()

            if(order.refundStatus == OrderRefundstatus.ENTIRE) {
                cartDataCurrentResult = emptyList()
                cartDataCancelResult = order.cartDataOriginal
            }else if(order.refundStatus == OrderRefundstatus.PARTIAL) {
                // cartDataResult = cartData - cartDataCancel
                cartDataCurrentResult = calculateCartData(it.cartDataOriginal, it.cartDataCancel)
                // Group cartDataCancelResult by id
                cartDataCancelResult = groupCartItems(it.cartDataCancel)
            }
            else if(order.refundStatus == OrderRefundstatus.NONE) {
                cartDataCurrentResult = order.cartDataOriginal
                cartDataCancelResult =  emptyList()
            }

            it.copy(
                cartDataCurrent = cartDataCurrentResult,
                cartDataCancel = cartDataCancelResult
            )
        }
    }

    /**
     * the purpose is cancel/refund all item on an order
     */
    fun cancelAdminEntireOrder(orderCode: String): Boolean {
        // GET TRANSACTION INFO FROM DATABASE
        val currentOrder = orderAdminRepository.getAdminOrderByCode(orderCode)
            ?: throw BadRequestException("Missing parameter - cannot cancel order.")
        if (currentOrder.trxId?.isBlank() == true || currentOrder.merTrxId?.isBlank() == true || currentOrder.amount.isBlank() || currentOrder.payType.toString()
                .isBlank()
        ) {
            throw BadRequestException("Missing parameter - cannot cancel order.")
        }

        // GET TRANSACTION INFO FROM EPAY
        val dataByTransaction = paymentService.getTransactionStatus(currentOrder.merTrxId!!)

        log.info("logbase={} ---cancelAdminEntireOrder: currentOrder={}", logbase, currentOrder)
        val trxId = currentOrder.trxId
        val amount = dataByTransaction.remainAmount
        val payType = currentOrder.payType

        //GENERATE
        val uniqueHardCodeNumber = System.currentTimeMillis()
        val cancelCode = "CAN00$uniqueHardCodeNumber" // hardcode

        // SEND CANCEL API
        val responseCancel = paymentService.cancelTransaction(trxId!!, amount!!, cancelCode, payType!!)
        if (responseCancel.cancelTrxId.isNullOrBlank() && EpayTransactionResultCodeEnum.isFailed(responseCancel.resultCd)) {
            throw BadRequestException("Cancel entire Error")
        } else {
            // INSERT CANCEL INFO INTO TABLE
            val dto = InsertCancelEntireDto(
                orderCode = orderCode,
                cancelCode = cancelCode,
                amountOriginal = currentOrder.amount.toLong(),
                amountCancel = dataByTransaction.remainAmount.toLong()
            )
            orderAdminRepository.insertCancelEntire(dto)

            // UPDATE REFUND STATUS
            orderAdminRepository.changeOrderRefundStatus(orderCode, OrderRefundstatus.ENTIRE)

            // UPDATE ORDER STATUS
            orderAdminRepository.changeOrderStatus(orderCode, OrderOrderstatus.CANCEL)

            // IPN WILL BE UPDATE 'remainAmount'
            return true
        }
    }

    /**
     * the purpose is cancel/refund each item on an order
     */
    fun cancelPartialOrder(orderCode: String, dto: OrderCancelPartialDto): Boolean {
        val currentOrder = orderAdminRepository.getAdminOrderByCode(orderCode)
            ?: throw BadRequestException("Missing parameter - cannot cancel order.")
        if (currentOrder.trxId?.isBlank() == true || currentOrder.amount.isBlank() || currentOrder.payType.toString()
                .isBlank()
        ) {
            throw BadRequestException("Missing parameter - cannot cancel order.")
        }


        // CHECK IS LAST ITEM
        val cartData = calculateCartData(currentOrder.cartDataOriginal, currentOrder.cartDataCancel)
        log.info("logbase={} ---cancelPartialOrder: cartData={}", logbase, cartData)

        val itemInCart = cartData.find { it.id == dto.productId }
        val isLastItem = if (itemInCart != null) {
            itemInCart.quantity == dto.quantity && cartData.size == 1
        } else {
            throw BadRequestException("Cannot find product - cannot cancel item.")
        }

        log.info("logbase={} ---cancelPartialOrder: productId={}, isLastItem={}",logbase, dto.productId, isLastItem)

        val totalAmount = currentOrder.amount.toLongOrNull() ?: 0L
        // GET REFUND AMOUNT TOTAL OF THIS ORDER
        val refundedAmount = orderAdminRepository.getRefundedSoFar(orderCode)

        // CALCULATE AMOUNT OF THIS ITEM
        val amountCancel =
            calculateItemPriceToCancel(dto.price, dto.quantity, totalAmount, currentOrder.cartTotal, refundedAmount, isLastItem)
        val amountStr = amountCancel.toString()
        log.info("logbase={} ---cancelPartialOrder: currentOrder={}, amount={}", logbase, currentOrder, amountCancel)

        val trxId = currentOrder.trxId
        val payType = currentOrder.payType


        //GENERATE
        val uniqueHardCodeNumber = System.currentTimeMillis()
        val cancelCode = "CAN00$uniqueHardCodeNumber" // hardcode

        // SEND CANCEL API
        val responseCancel = paymentService.cancelTransaction(trxId!!, amountStr, cancelCode, payType!!)
        if (responseCancel.cancelTrxId.isNullOrBlank() && EpayTransactionResultCodeEnum.isFailed(responseCancel.resultCd)) {
            throw BadRequestException("Cancel partial Error")
        } else {
            // INSERT CANCEL INFO INTO TABLE
            val insertDto = InsertCancelPartialDto(
                orderCode = orderCode,
                cancelCode = cancelCode,
                productId = dto.productId,
                quantity = dto.quantity,
                price = dto.price,
                name = dto.name,
                image = dto.image,
                categoryId = dto.categoryId,
                categoryName = dto.categoryName,
                amountActual = (dto.quantity * dto.price),
                amountCancel = amountCancel,
            )
            orderAdminRepository.insertCancelPartial(insertDto)

            if(isLastItem){
                // UPDATE REFUND STATUS -> ENTIRE
                orderAdminRepository.changeOrderRefundStatus(orderCode, OrderRefundstatus.ENTIRE)

                // UPDATE ORDER STATUS
                orderAdminRepository.changeOrderStatus(orderCode, OrderOrderstatus.CANCEL)
            }else{
                // UPDATE REFUND STATUS ->
                orderAdminRepository.changeOrderRefundStatus(orderCode, OrderRefundstatus.PARTIAL)
            }
            // IPN WILL BE UPDATE 'remainAmount'
            return true
        }
    }

    private fun calculateItemPriceToCancel(price: Long, quantity: Int, totalAmount: Long, cartTotal: Long, refundedAmount: Long, isLastItem: Boolean = false): Long {
        val remainingRefundable = totalAmount - refundedAmount

        if (remainingRefundable <= 0L) {
            return 0L
        }

        if (isLastItem) {
            return remainingRefundable
        }

        val itemTotal = BigDecimal.valueOf(price)
            .multiply(BigDecimal.valueOf(quantity.toLong()))

        val calculatedRefund = itemTotal
            .multiply(BigDecimal.valueOf(totalAmount))
            .divide(BigDecimal.valueOf(cartTotal), 0, RoundingMode.DOWN)
            .toLong()
        log.info("logbase={} ---calculateItemPriceToCancel: calculatedRefund={}, remainingRefundable={}", logbase, calculatedRefund, remainingRefundable)
        return minOf(calculatedRefund, remainingRefundable)
    }
}