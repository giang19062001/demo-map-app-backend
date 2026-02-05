package com.vietq.demo_map_app_backend.dto

import com.study.jooq.enums.OrderEpayPaytype


// FOR API 'createlink'
data class PaymentCreateLinkResponseDto(
    val resultCd: String?,
    val resultMsg: String?,
    val paymentId: String?,
    val merId: String?,
    val invoiceNo: String?,
    val amount: String?,
    val currency: String?,
    val timeStamp: String?,
    val payType: String?,
    val payOption: String?,
    val linkExptime: String?,
    val paymentLink: String?,
    val qrCode: String?,
    val merchantToken: String?
)

// FOR API 'paymentCancel'
data class PaymentCancelResponseDto(
    val trxId: String?,
    val merId: String?,
    val cancelTrxId: String?,
    val resultCd: String,
    val resultMsg: String,
    val trxDt: String?,
    val trxTm: String?,
    val timeStamp: String?,
    val description: String?,
    val amount: String?,
    val merTrxId: String?,
    val merchantToken: String?,
    val payMessage: String, )

// FOR GET DATA FROM 'PAYMENT' TABLE
data class PaymentEpayResponseDto(
    val orderCode: String,
    val trxId: String?,
    val merTrxId: String?,
    val goodsNm: String?,
    val buyerFirstNm: String?,
    val buyerLastNm: String?,
    val amount: String?,
    val remainAmount: String?,
    val payType:  OrderEpayPaytype?, // ENUM
    val payOption: String?,
    val bankId: String?,
    val bankCode: String?,
    val cardNo: String?,
    val cardType: String?,
    val cardTypeValue: String?,
    val status: String?,
    val resultCd: String,
    val resultMsg: String
)

// FOR API 'trxStatusInvoiceNo'
data class PaymentGetMerchantDataResponseDto(
    val trxId: String,
    val merId: String,
    val amount: String,
    val resultCd: String,
    val resultMsg: String,
    val status: String,
    val timeStamp: String,
    val merchantToken: String,
    val payMessage: String,
    val merTrxId: String,
)

data class PaymentGetMerchantResponseDto(
    val resultCd: String,
    val resultMsg: String?,
    val data: PaymentGetMerchantDataResponseDto?
)

// FOR API 'trxStatus'
data class PaymentGetTransactionDataResponseDto(
    val invoiceNo: String,
    val trxId: String,
    val merId: String,
    val amount: String,
    val resultCd: String,
    val resultMsg: String,
    val status: String,
    val timeStamp: String,
    val merchantToken: String,
    val payMessage: String,
    val merTrxId: String,
    val payType: String,
    val goodsNm: String,
    val payOption: String?,
    val buyerFirstNm: String?,
    val buyerLastNm: String?,
    val bankId: String?,
    val bankCode: String?,
    val cardNo: String?,
    val remainAmount: String?,
    val cardType: String?,
    val cardTypeValue: String?,
)

data class PaymentGetTransactionResponseDto(
    val resultCd: String,
    val resultMsg: String?,
    val data: PaymentGetTransactionDataResponseDto?
)

