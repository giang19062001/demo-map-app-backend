package com.vietq.demo_map_app_backend.utils

import com.vietq.demo_map_app_backend.utils.EpayResultCodeEnum.Companion.fromCode
import com.vietq.demo_map_app_backend.utils.EpayResultCodeEnum.SUCCESS

enum class EpayResultCodeEnum(
    val code: String,
    val description: String,
) {
    // SUCCESS
    SUCCESS("00_000", "Transaction successfully"),
    PENDING("99", "Transaction is pending"),


    // PENDING - USER HASN'T PAID YET (DCODE)
    DCODE_MAPPED_NOT_PAID("00_005", "Transaction has been mapped to Dcode account, but user hasn't paid yet",),

    // PENDING

    // CONNECTION ERRORS
    CONNECTION_ERROR("FL_900", "Connection error",),
    SOCKET_CONNECTION_ERROR("FL_901", "Socket connection error",),
    PROCESSING_ERROR("FL_902", "Error happened while processing"),
    SOCKET_TIMEOUT("FL_903", "Socket timeout exception"),

    // MERCHANT/ORDER VALIDATION ERRORS
    INVALID_MERCHANT_ID("OR_101", "Merchant ID is invalid or not registered"),
    PAYMENT_TYPE_INACTIVE("OR_102", "Payment type doesn't exist or not activated"),
    CURRENCY_UNDEFINED("OR_103", "Currency Code was undefined"),
    BUYER_CITY_UNDEFINED("OR_104", "Buyer city was undefined",),
    INVOICE_NO_UNDEFINED("OR_105", "InvoiceNo was undefined",),
    GOODS_NAME_UNDEFINED("OR_106", "Goods Name was undefined or wrong format",),
    BUYER_NAME_UNDEFINED("OR_107", "Buyer first name or last name was undefined",),
    BUYER_PHONE_UNDEFINED("OR_108", "Buyer Phone Number was undefined"),
    BUYER_EMAIL_UNDEFINED("OR_109", "Buyer Email Address was undefined",),
    CALLBACK_URL_UNDEFINED("OR_110", "Callback URL was undefined"),
    NOTIFICATION_URL_UNDEFINED("OR_111", "Notification URL was undefined"),
    INVALID_AMOUNT_FORMAT("OR_112", "Payment Amount is invalid"),
    INVALID_MERCHANT_TOKEN("OR_113", "Invalid Merchant Token"),
    AMOUNT_ZERO_OR_NEGATIVE("OR_114", "Payment Amount has to be greater than 0"),
    DUPLICATE_ORDER_FLAG_NULL("OR_115", "Duplicate order check flag is null"),
    DUPLICATE_INVOICE_NO("OR_116", "Invoice no is duplicated"),
    DUPLICATE_MERCHANT_TRX_ID("OR_117", "Duplicate Merchant transaction ID",),
    REQUEST_DOMAIN_ERROR("OR_118", "Request domain or MerchantId error"),
    MERCHANT_NOT_ACTIVE("OR_120", "Merchant is not active"),
    MERCHANT_TRX_ID_EMPTY("OR_122", "Merchant Transaction ID is empty or wrong format",),
    MERCHANT_NOT_DEFINED("OR_123", "Merchant is not defined in the system"),
    // PAYMENT NOT YET/EXPIRED
    TRANSACTION_NOT_EXIST("OR_140", "Transaction does not exist"),
    // CANCELLED
    TRANSACTION_CANCELLED("OR_141", "Transaction has been cancelled",),
    // VALIDATION CONTINUED
    BUYER_ADDRESS_REQUIRED("OR_141", "Buyer address is required",),
    BUYER_STATE_REQUIRED("OR_142", "Buyer state is required for US/CA",),
    BUYER_COUNTRY_REQUIRED("OR_143", "Buyer country is required",),
    INVALID_DESCRIPTION("OR_147", "Description is invalid",),
    INVALID_TIMESTAMP("OR_148", "Timestamp is invalid or empty", ),
    TRANSACTION_TIMEOUT("OR_160", "Transaction timeout",),
    // BANK/PAYMENT METHOD ERRORS
    BANK_MAINTENANCE("OR_161", "Bank maintenance"),
    ORDER_ALREADY_PROCESSED("OR_164", "Order has already been processed successfully",),
    ORDER_UNDER_PROCESSING("OR_165", "Order is under processing",),
    PAYMENT_LINK_EXPIRED("OR_170", "Payment link does not exist or has expired"),
    PAYMENT_LINK_PROCESSING("OR_171", "Payment link is being processed"),

    // DOMESTIC CARD ERRORS
    DC_NAVAS_ERROR("DC_101", "Error checking information with NAPAS",),
    DC_TRANSACTION_NOT_DEFINED("DC_102", "Transaction Id is not defined",),
    DC_TRANSACTION_EXISTS("DC_103", "Transaction already exists",),
    DC_SERVER_BUSY("DC_119", "Server is busy, try again",),
    DC_TRANSACTION_SUCCESS_NO_TOKEN("DC_121", "Payment Success but token is not created",),
    DC_WRONG_CVV("DC_126", "Wrong CVV",),
    DC_OUT_OF_LIMIT("DC_127", "Transaction value is out of limit range"),
    DC_EXPIRED_SESSION("DC_131", "Expired NAPAS session"),
    DC_AUTH_FAILED_3DS("DC_135", "Authentication failed 3ds",),

    // INTERNATIONAL CARD ERRORS
    IC_TRANSACTION_FAILED("IC_101", "Transaction failed, check card information",),
    IC_CONNECTION_ERROR("IC_107", "Error connecting to CyberSource"),
    IC_INVALID_MID("IC_115", "Invalid MID, Merchant not registered"),
    IC_TOKEN_NOT_FOUND("IC_124", "Token was not found",),
    IC_TOKEN_LOCKED("IC_125", "Token locked",),
    IC_TOKEN_DELETED("IC_126", "Token deleted",),
    IC_FRAUD_CARD("IC_134", "Fraud card",),
    IC_DECLINED_STOLEN_CARD("IC_138", "Decline - Stolen or lost card",),
    IC_DECLINED_INVALID_CVN("IC_139", "Decline - Invalid CVN",),
    IC_DECLINED_PROCESSOR_FAILURE("IC_140", "Decline - Processor failure",),

    // VIRTUAL ACCOUNT ERRORS
    VA_CONNECTION_FAILED("VA_101", "Connect to VA system failed",),
    VA_TRANSACTION_EXISTS("VA_102", "VA transaction already exists",),
    VA_TRANSACTION_NOT_FOUND("VA_106", "Transaction doesn't exist",),
    VA_INVALID_TRANSACTION("VA_112", "Transaction is invalid",),

    // CANCELLATION/REFUND ERRORS
    CC_INVALID_MID("CC_102", "Invalid MID or Merchant not registered",),
    CC_TRANSACTION_NOT_FOUND("CC_112", "Transaction to cancel not found",),
    CC_ALREADY_CANCELLED("CC_118", "Transaction has been cancelled", ),
    CC_BANK_CONNECTION_ERROR("CC_130", "Bank connection error",),

    // PAYMENT GATEWAY ERRORS
    PG_TRANSACTION_FAILED("PG_ER1", "Transaction Failed", ),
    PG_WRONG_CARD_INFO("PG_ER2", "Card information is wrong",),
    PG_TIMEOUT("PG_ER3", "Transaction failed - Timeout"),
    PG_CUSTOMER_CANCELLED("PG_ER5", "Customer canceled transaction",),
    PG_SYSTEM_ERROR("PG_ER6", "System error",),
    PG_INSUFFICIENT_FUNDS("PG_ER19", "Insufficient funds",),
    PG_EXCEEDS_LIMIT("PG_ER20", "Amount exceeds allowed limit",),
    PG_DENIED_BY_BANK("PG_ER23", "Issuing bank denied the transaction",),
    PG_DENIED_BY_FRAUD("PG_ER25", "Denied by fraud management system",),
    PG_BLACKLISTED("PG_ER29", "Customer is in blacklist",),
    PG_AUTHENTICATION_FAILED("PG_ER30", "Cannot authenticate the customer"),
    PG_OTP_TIMEOUT("PG_ER42", "OTP timeout",),
    PG_BANK_BUSY("PG_ER43", "Issuing bank is busy",),

    // ZALOPAY ERRORS
    ZL_PENDING("ZL_1", "Pending with ZaloPay"),
    ZL_USER_ERROR("ZL_2", "User error with ZaloPay"),
    ZL_MERCHANT_ERROR("ZL_3", "Merchant error with ZaloPay",),
    ZL_SYSTEM_ERROR("ZL_4", "System error with ZaloPay",),

    // MOMO ERRORS
    MM_EMPTY_ACCESS_KEY("MM_1", "Empty accessKey or partnerCode"),
    MM_DUPLICATED_REQUEST("MM_12", "Duplicated requestID",),
    MM_INACTIVE_PARTNER("MM_14", "Partner is not activated",),
    MM_TRANSACTION_PURCHASED("MM_32", "Transaction was purchased",),
    MM_INSUFFICIENT_FUNDS("MM_38", "Insufficient funds",),
    MM_INVALID_AMOUNT("MM_4", "Amount is invalid",),
    MM_WRONG_SIGNATURE("MM_5", "Signature is wrong", ),
    MM_ORDER_EXISTS("MM_6", "OrderId exists",),
    MM_PENDING_TRANSACTION("MM_7", "Pending transaction",),
    MM_USER_AUTH_FAILED("MM_80", "User failed authentication",),
    MM_ORDER_CANCELLED("MM_49", "Order cancelled by user"),

    // INSTALLMENT ERRORS
    IS_BANK_NOT_SUPPORTED("IS_001", "Bank or term of installment not supported"),

    // VIETTEL MONEY ERRORS
    VT_TRANSACTION_FAILED("VT_01", "Transaction fail from Viettel Money or Bank"),
    VT_WRONG_PASSWORD("VT_02", "Wrong password/PIN/OTP or expired OTP",),
    VT_ACCOUNT_INACTIVE("VT_03", "Viettel Money account is inactive/locked/cancelled"),
    VT_EXCEEDS_LIMIT("VT_06", "Amount exceeded monthly limit"),

    // UNKNOWN
    UNKNOWN("UNKNOWN", "Unknown transaction status"); //

    companion object {
        fun fromCode(code: String?): EpayResultCodeEnum {
            return entries.find { it.code == code } ?: UNKNOWN
        }
        fun isSuccess(code: String?): Boolean {
            return fromCode(code) == SUCCESS
        }

        fun isPending(code: String?): Boolean {
            val result = fromCode(code)
            return result == PENDING ||
                    result == DCODE_MAPPED_NOT_PAID ||
                    result == ORDER_UNDER_PROCESSING ||
                    result == PAYMENT_LINK_PROCESSING ||
                    result == ZL_PENDING ||
                    result == MM_PENDING_TRANSACTION
        }

        fun isNotYet(code: String?): Boolean {
            val result = fromCode(code)
            return result == TRANSACTION_NOT_EXIST ||
                    result == VA_TRANSACTION_NOT_FOUND
        }

    }
}

enum class EpayTransactionStatuEnum(val code: String, val description: String) {
    SUCCESS_NOT_REFUND("0", "Original transaction is successful and not refunded yet"),
    SUCCESS_INSTALLMENT_CANCEL("1", "Original transaction is successful and Installment conversion is canceled or rejected"),
    SUCCESS_ALREADY_REFUND("2", " Original transaction is successful, and refund is done"),
    DEPOSIT_ISSUE_BUT_NOT_DEPOSITED("5", " Deposit code is issued, but end user has not deposited money yet "),
    NOT_FOUND("-1", "Original transaction not found"),
    PENDING("-2", "Original transaction is pending"),
    FAILED("-3", "Original transaction is failed"),
    UNKNOWN("UNKNOWN", "Unknown transaction status"); //

    companion object {

        fun fromCode(code: String?): EpayTransactionStatuEnum {
            return EpayTransactionStatuEnum.entries.find { it.code == code } ?: EpayTransactionStatuEnum.UNKNOWN
        }

        fun isSuccess(code: String?): Boolean {
            val value = code?.toIntOrNull() ?: return false
            return value >= 0
        }
    }
}