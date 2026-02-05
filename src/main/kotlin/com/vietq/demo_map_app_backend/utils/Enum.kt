package com.vietq.demo_map_app_backend.utils
import com.study.jooq.enums.OrderPaymentstatus

enum class EpayMerchantCodeEnum(val code: String, val description: String, ) {
    MERCHANT_FAIL("-1", "Transaction not exists"),
    UNKNOWN("UNKNOWN", "Unknown transaction status");
    companion object {
        fun fromCode(code: String?): EpayMerchantCodeEnum {
            return EpayMerchantCodeEnum.entries.find { it.code == code } ?: EpayMerchantCodeEnum.UNKNOWN
        }
        fun isFailed(code: String?): Boolean {
            return EpayMerchantCodeEnum.Companion.fromCode(code) == MERCHANT_FAIL
        }
    }
}
enum class EpayTransactionResultCodeEnum(val code: String, val description: String, ) {
    // SUCCESS
    SUCCESS("00_000", "Transaction successfully"),
    PENDING("99", "Transaction is pending"),

    // PENDING - USER HASN'T PAID YET (DCODE)
    DCODE_MAPPED_NOT_PAID("00_005", "Transaction has been mapped to Dcode account, but user hasn't paid yet",),

    // CONNECTION ERRORS
    CONNECTION_ERROR("FL_900", "Connection error",),
    SOCKET_CONNECTION_ERROR("FL_901", "Socket connection error",),
    PROCESSING_ERROR("FL_902", "Error happened while processing"),
    SOCKET_TIMEOUT("FL_903", "Socket timeout exception"),

    // MERCHANT/ORDER VALIDATION ERRORS
    INVALID_MERCHANT_ID("OR_101", "MerID is invalid or Merchant is not registered. Contact to Customer Service Center for further information."),
    PAYMENT_TYPE_INACTIVE("OR_102", "This Payment type doesn’t exist or currently not activated. Contact Customer Service for further information."),
    CURRENCY_UNDEFINED("OR_103", "Currency Code was undefined. Please check your request parameter and make sure [currencyCode] is defined"),
    BUYER_CITY_UNDEFINED("OR_104", "buyerCity was undefined. Please check your request parameter and make sure [buyerCity] is defined",),
    INVOICE_WAS_UNDEFINED("OR_105", "InvoiceNo was undefined. Please check your request parameter and make sure [invoiceNo] is defined",),
    GOODS_NAME_UNDEFINED("OR_106", "Goods Name was undefined or wrong format. Please check your request parameter and make sure [goodsNm] is defined",),
    BUYER_NAME_UNDEFINED("OR_107", "Buyer first name or last name was undefined. Please check your request parameter and make sure[buyerFirstNm] and [buyerLastNm] is defined.",),
    BUYER_PHONE_UNDEFINED("OR_108", "Buyer Phone Number was undefined. Please check your request parameter and make sure[buyerPhone] is defined."),
    BUYER_EMAIL_UNDEFINED("OR_109", "Buyer Email Address was undefined. Please check your request parameter and make sure[buyerEmail] is defined correctly.",),
    CALLBACK_URL_UNDEFINED("OR_110", "Callback URL was undefined. Please check your request parameter and make sure [callbackUrl] isdefined."),
    NOTIFICATION_URL_UNDEFINED("OR_111", "Notification URL was undefined. Please check your request parameter and make sure [notiUrl] is defined"),
    INVALID_AMOUNT_FORMAT("OR_112", "Payment Amount is invalid. Amount should only number and do not include decimal."),
    INVALID_MERCHANT_TOKEN("OR_113", "Invalid Merchant Token. Contact Customer Service for further information."),
    AMOUNT_ZERO_OR_NEGATIVE("OR_114", "Payment Amount has to be greater than 0. Please check your request parameter and make sure[amount] is defined"),
    DUPLICATE_ORDER_FLAG_NULL("OR_115", "The flag field which was used to identify if a merchant will be checked duplicate order no is null."),
    DUPLICATE_INVOICE_NO("OR_116", "Invoice no is duplicated"),
    DUPLICATE_MERCHANT_TRX_ID("OR_117", "Duplicate Merchant transaction ID (merTrxId)",),
    REQUEST_DOMAIN_ERROR("OR_118", "Request domain was undefined, MerchantId sent by merchant is null, MerchantId is mismatch (Inquiry transaction function"),
    MERCHANT_NOT_ACTIVE1("OR_120", "Merchant’s status error (Merchant is not active)"),
    MERCHANT_TRX_ID_EMPTY("OR_122", "Merchant Transaction ID is empty or wrong format",),
    MERCHANT_NOT_DEFINED("OR_123", "Error of merchant is not defined in the system."),
    MERCHANT_NOT_ACTIVE2("OR_124", "Merchant’s status error (Merchant is not active)"),
    MERCHANT_NOT_REGISTERED("OR_125", "Merchant is not registered this payment method or Cybersource settle time is not defined."),
    MERCHANT_PG_NOT_SET("OR_126", "PG Type is not set"),
    MERCHANT_LIMIT_ERROR("OR_127", "Check Merchant’s applied limit error."),
    OVER_LIMIT_ACCOUNT("OR_128", "Over limit amount error."),
    MERCHANT_TYPE_NOT_DEFINED("OR_130", "The field used to identify merchant is online or offline is not defined. Please check your request parameter and make sure [merType] is defined"),
    MERCHANT_ON_TYPE_NOT_ACTIVE("OR_131", "This Online Merchant type is currently not activated."),
    MERCHANT_OFF_TYPE_NOT_ACTIVE("OR_132", "This Offline Merchant type is currently not activated."),
    CONTRACT_INFO_NOT_DEFINED("OR_133", "Contract’s information is not defined."),
    INVALID_AMOUNT_OR("OR_134", "Invalid amount"),

    TRANSACTION_NOT_EXIST("OR_140", "Transaction does not exist"),
    BUYER_ADDRESS_REQUIRED("OR_141", "Buyer address is required",),
    BUYER_STATE_REQUIRED("OR_142", "Buyer state is required for US/CA",),
    BUYER_COUNTRY_REQUIRED("OR_143", "Buyer country is required",),
    INVALID_DESCRIPTION("OR_147", "Description is invalid",),
    INVALID_TIMESTAMP("OR_148", "timeStamp is not allowed to be empty or invalid", ),
    SYSTEM_NOT_SUPPORT_TOKEN("OR_150", "The system doesn’t support tokenization for this payment method", ),
    INVALID_OR_EMPTY_USERID("OR_151", "userId is not allowed to be empty or is invalid", ),
    INVALID_USERID("OR_152", "userId is invalid or empty", ),
    INVALID_PAY_OPTION("OR_153", "payOption is invalid", ),
    NOT_SUPPORT_3DS("OR_154", "Not support merchant ON 3DS", ),
    ONLY_SUPPORT_PAYMENT_INTERNATIONAL_CARD("OR_155", "Only support payment type is International card", ),
    ONLY_SUPPORT_MERCHANT_INTERNATIONAL_CARD("OR_156", "Only support merchant using token with International card", ),
    NOT_SUPPORT_PAYMENT_INTERNATIONAL_CARD("OR_157", "Not support International Card payment", ),
    NOT_SUPPORT_PAYMENT_ATM_CARD("OR_158", "Not support ATM Card payment", ),
    NOT_SUPPORT_EWALLET_CARD("OR_159", "Not support eWallet payment", ),
    TRANSACTION_TIMEOUT("OR_160", "Transaction timeout",),

    // BANK/PAYMENT METHOD ERRORS
    BANK_MAINTENANCE("OR_161", "Bank maintenance"),
    NOT_SUPPORT_INSTALLMENT("OR_162", "Not support installment payment"),
    INVALID_TYPE("OR_163", "[type] is Invalid or NULL"),
    ORDER_ALREADY_PROCESSED("OR_164", "Order has already been processed successfully, please make another order",),
    ORDER_UNDER_PROCESSING("OR_165", "Order is under processing. Please try again after 30 minutes in maximum",),
    NO_URL_CONFIGURE("OR_166", "No url configured",),
    CARD_WAS_DELETED("OR_167", "Card information has been deleted because there has been no transaction for a long time. Please re-enter the previously saved card information",),
    CARD_WAS_EXPIRED("OR_168", "Card you have saved previously has expired. Please return to the purchase page, delete the saved card and pay with a new card. (User Information >> Payment Management >> Select Card to Delete >> Delete Card)",),
    CARD_NOT_MATCH("OR_169", "The card number you just entered does not match the card number registered with the merchant. Please enter the card number registered",),
    PAYMENT_LINK_EXPIRED("OR_170", "Payment link does not exist or has expired"),
    PAYMENT_LINK_PROCESSING("OR_171", "Payment link is being processed"),
    INVALID_WINDOW_TYPE("OR_172", "WindowType is invalid"),
    INVALID_LANGUAGE("OR_173", "User Language is invalid"),
    INVALID_MERCHANT_INSTALLMENT("OR_174", "Invalid Merchant installment declaration"),
    INVALID_FIELD("OR_175", "Invalid [_Field_] field length. Please check again"),

    // DOMESTIC CARD ERRORS
    DC_NAVAS_ERROR("DC_101", "Error checking information with NAPAS",),
    DC_TRANSACTION_NOT_DEFINED("DC_102", "Transaction Id is not defined",),
    DC_TRANSACTION_EXISTS("DC_103", "Transaction already exists",),
    INVOICE_NOT_DEFINED("DC_104", "Invoice no is null. Please make sure that [invoiceNo] was defined already",),
    DATA_IS_NULL("DC_105", "Data is null error"),
    PAYMENT_TYPE_UNDEFINED("DC_110", "Payment type is undefined. Contact Megapay for further information."),
    ATM_TABLE_INSERT_UPDATE_ERROR("DC_112", "Error occurred while inserting or updating data in the tables related to ATM transaction."),
    EMAIL_TRANSACTION_UPDATE_ERROR("DC_113", "Error occurred while updating email transaction."),
    NOTIFY_TRANSACTION_TABLE_SAVE_ERROR("DC_114", "Error when saving data into notify transaction table."),
    TRANSACTION_INFO_NOT_REGISTERED("DC_117", "Transaction’s information haven’t registered yet. Please check again."),
    SERVER_BUSY("DC_119", "Server is busy. Please kindly try again in few minutes."),
    PAYMENT_SUCCESS_TOKEN_NOT_CREATED("DC_120", "Payment Success but token is not created."),
    PARTNER_TRANSACTION_ID_NULL("DC_122", "Partner transaction Id is null"),
    PARTNER_TRANSACTION_ID_INVALID("DC_123", "Partner transaction Id is invalid"),
    INVALID_AMOUNT_DC("DC_124", "Invalid amount"),
    INVALID_CURRENCY("DC_125", "Invalid currency"),
    WRONG_CVV("DC_126", "Wrong CVV"),
    TRANSACTION_OUT_OF_LIMIT("DC_127", "Transaction value is out of limit range set by your bank"),
    INVALID_EXPIRATION_DATE("DC_128", "Invalid expiration date"),
    INVALID_ISSUE_DATE("DC_129", "Invalid issue date"),
    BELOW_MINIMUM_LIMIT("DC_130", "value does not meet minimum limit set by bank"),
    EXPIRED_NAPAS_SESSION("DC_131", "Expired napas Session"),
    TRANSACTION_NOT_SUPPORTED("DC_132", "Transaction not supported"),
    CARD_NOT_IN_PROMOTION("DC_133", "The card inputted is not promotion program"),
    ORDER_EXPIRED("DC_134", "Order is expired"),
    AUTHENTICATION_FAILED_3DS("DC_135", "Authentication failed 3ds"),
    TRANSACTION_FAILED_CHECK_CARD("IC_101", "Transaction failed. Please check card information and try again."),
    TRANSACTION_ID_NOT_DEFINED("IC_102", "Transaction Id is not defined"),
    TRANSACTION_ALREADY_EXIST("IC_103", "Transaction already exist. Please make new transaction."),
    INVOICE_NO_UNDEFINED("IC_104", "Invoice no is undefined (null). Please check field named [invoiceNo] again"),
    MERCHANT_CARD_INFO_NOT_DEFINED("IC_105", "Merchant’s card information is not defined"),
    CONNECTION_CYBERSOURCE_ERROR("IC_107", "Error occurred while connecting to CyberSource"),
    PAYTYPE_OR_MERCHANT_ID_MISSING("IC_110", "Paytype or merchant id is missing"),
    CREDIT_CARD_TABLE_INSERT_ERROR("IC_112", "Error occurred while inserting data into tables related to credit card transaction"),
    EMAIL_TRANSACTION_TABLE_UPDATE_ERROR("IC_113", "Error occurred while updating email transaction table"),
    INVALID_MID("IC_115", "Invalid MID, Merchant is not registered. Contact Megapay for further information."),
    TRANSACTION_INFO_NOT_REGISTERED_IC("IC_117", "Transaction’s information is not registered"),
    MERCHANT_INACTIVE("IC_121", "Merchant is inactive"),
    PAYTOKEN_WRONG_FORMAT("IC_122", "payToken is in wrong format or empty"),
    TOKENIZATION_NOT_SUPPORTED("IC_123", "Merchant doesn’t support payment with Tokenization with international card"),
    TOKEN_NOT_FOUND("IC_124", "Token was not found"),
    TOKEN_LOCKED("IC_125", "Token locked"),
    TOKEN_DELETED("IC_126", "Token deleted"),
    INVALID_CARD_TYPE_VALUE("IC_127", "Card type (cardTypeValue) is invalid"),
    INVALID_SIGNATURE("IC_128", "Signature (cardTypeToken) is invalid"),
    CARD_TYPE_NOT_SUPPORTED("IC_129", "Card type (cardTypeValue) is not support"),
    FEE_NOT_CREATED("IC_130", "Merchant has not created fee"),
    INTERNATIONAL_CARD_DOMESTIC_NOT_ALLOWED("IC_131", "Not allowed to accept international card (domestic)"),
    INTERNATIONAL_CARD_OVERSEAS_NOT_ALLOWED("IC_132", "Not allow to accept international card (overseas)"),
    DECRYPTING_TOKEN_ERROR("IC_133", "Decrypting token error"),
    FRAUD_CARD("IC_134", "Fraud card"),
    DISCOUNT_CODE_INVALID("IC_136", "Discount Code is invalid or not applied for this Card"),
    CARD_NOT_SUPPORTED("IC_137", "The entered card is not supported. Please make the transaction again by another card"),
    DECLINE_STOLEN_OR_LOST_CARD("IC_138", "Decline - Stolen or lost card."),
    DECLINE_INVALID_CVN("IC_139", "Decline - Invalid Card Verification Number (CVN)."),
    DECLINE_PROCESSOR_FAILURE("IC_140", "Decline - Processor failure."),
    DECLINE_PINLESS_DEBIT_LIMIT_EXCEEDED("IC_141", "Decline - The Pinless Debit card use frequency or maximum amount per use has been exceeded"),
    INVALID_POSTAL_CODE("IC_142", "Invalid postal code"),
    INVALID_BUYER_COUNTRY("IC_143", "Invalid buyer country"),
    INVALID_SHIP_COUNTRY("IC_144", "Invalid ship country"),
    MISSING_BUYER_COUNTRY("IC_145", "Missing field buyer country"),
    NO_RESPONSE_FROM_CARDINAL("IC_146", "Not receiving response events from Cardinal."),
    CAPTURE_AUTH_REVERSAL_PROCESSING("IC_147", "Transation Capture/Authorization Reversal is processing or success."),
    MERCHANT_TOKEN_MISMATCH("IC_148", "MerchantToken is not match."),
    INACTIVE_OR_UNAUTHORIZED("IC_149", "Inactive || not authorized for online transactions"),
    VA_CONNECTION_FAILED("VA_101", "Connect to VA system is fail or transaction id is not defined"),
    VA_TRANSACTION_EXISTED("VA_102", "VA transaction has already existed"),
    MERCHANT_ID_MISSING("VA_103", "Merchant id information is missing"),
    VA_TRANS_TABLE_INSERT_ERROR("VA_104", "Error occurred while inserting data into VA trans table"),
    TRANS_RESULT_TABLE_INSERT_ERROR("VA_105", "Error occurred while inserting data into trans result table."),
    TRANSACTION_SEARCH_NOT_EXIST("VA_106", "Error occurred while searching transaction or transaction doesn’t exist"),
    MERCHANT_NOTIFICATION_TABLE_INSERT_ERROR("VA_107", "Error occurred while inserting data into merchant notification table"),
    RECEIVING_CONDITION_WRONG("VA_109", "Condition of receiving money is wrong (should equal to 03)"),
    EFFECTIVE_DATE_WRONG("VA_110", "Effective date is wrong"),
    EXPIRE_DATE_WRONG("VA_111", "Expire date is wrong"),
    TRANSACTION_INVALID("VA_112", "Transaction is invalid"),
    BANK_ISSUING_DEPOSIT_CODE_NOT_FOUND("VA_113", "Cannot find Bank issuing Deposit code"),
    TRANSACTION_ID_NOT_GENERATED("CC_101", "Transaction id is not generated."),
    INVALID_MID_OR_NOT_REGISTERED("CC_102", "Invalid MID or Merchant is not registered. Contact Customer Service for further information."),
    MERCHANT_NOT_ACTIVE("CC_109", "Merchant in the not active status"),
    TRANSACTION_NOT_REGISTERED("CC_110", "The transaction has not been registered"),
    INVALID_CANCEL_AMOUNT("CC_111", "Error occurred when canceling amount less than or equal 0 or canceling amount not equal transaction amount (In case of comprehensive cancelation)"),
    TRANSACTION_TO_CANCEL_NOT_FOUND("CC_112", "The transaction needs to be canceled is not found."),
    TRANSACTION_FULLY_CANCELED("CC_113", "The transaction has been canceled fully"),
    PAYMENT_METHOD_NOT_ACTIVATED("CC_114", "This payment method is currently not activated with merchant or insert notification data has been failed"),
    MERCHANT_TOKEN_INVALID("CC_115", "Merchant’s token is invalid"),
    CANCEL_AMOUNT_MISMATCH("CC_116", "Canceling amount must equal payment amount."),
    CANCEL_AMOUNT_INVALID_FORMAT("CC_117", "Amount of cancelation/refunding is invalid (Format number exception)"),
    ALREADY_CANCELED("CC_118", "It has been canceled."),
    CANCEL_AMOUNT_EXCEEDS_PAYMENT("CC_119", "The amount of money you entered is larger than the payment amount or the remaining payment amount is less than 0."),
    UPDATE_TRANSACTION_INFO_ERROR("CC_121", "Error occurred when updating transaction’s information"),
    INSERT_PARTIAL_CANCEL_INFO_ERROR("CC_122", "Error occurred when inserting partial cancelation information"),
    INSERT_DATA_AFTER_CANCEL_ERROR("CC_124", "Error occurred when inserting data after canceling"),
    TRANSACTION_RESULT_REGISTRATION_ERROR("CC_125", "Transaction result registration error."),
    QUERY_DATA_ERROR("CC_126", "Error occurred when querying data"),
    INVALID_PARTIAL_CANCEL_FLAG("CC_127", "Partial cancelation flag or status is invalid."),
    CANCEL_MESSAGE_NOT_DEFINED("CC_128", "Cancel message is not defined"),
    CANCEL_AMOUNT_SMALLER_THAN_PAYMENT("CC_130", "The amount of money you want to cancel is smaller than the transaction’s payment amount."),
    INSERT_CANCELATION_TRANSACTION_TABLE_ERROR("CC_131", "Error occurred when inserting data into cancelation transaction table"),
    UPDATE_TRANSACTION_HISTORY_ERROR("CC_132", "Update transaction history error."),
    BANK_CONNECTION_ERROR("CC_133", "Bank connection error."),
    CANCEL_PASSWORD_MISMATCHED("CC_135", "Cancel password is mismatched."),
    CANCELING_FUNCTION_UNAVAILABLE("CC_136", "Canceling function is unavailable with this merchant. Please Contact to Meagepay."),
    PARTIAL_REFUND_NOT_AVAILABLE_NOW("CC_141", "Currently cannot make a partial refund for this transaction, please wait until tomorrow"),
    MUST_REFUND_ALL("CC_143", "This transaction must refund all"),
    TRANSACTION_FAILED_GENERIC("PG_ER1", "Transaction Failed."),
    CARD_INFO_WRONG("PG_ER2", "Card’s information is wrong"),
    TRANSACTION_FAILED_TIMEOUT("PG_ER3", "Transaction is failed - Timeout"),
    TRANSACTION_FAILED_GENERIC_2("PG_ER4", "Transaction is failed"),
    CUSTOMER_CANCELED("PG_ER5", "Customer canceled transaction"),
    SYSTEM_ERROR_CONTACT_ADMIN("PG_ER6", "System error, please contact to Megapay’s Admin for supporting"),
    CARD_NUMBER_INVALID("PG_ER7", "Card number is invalid"),
    PUBLISH_EXPIRE_DATE_INVALID("PG_ER8", "Publish/Expire date is invalid"),
    BUYER_ADDRESS_WRONG("PG_ER10", "Buyer address is wrong"),
    PAYER_AUTH_NOT_CONFIGURED("PG_ER11", "Card has not been configured Payer Authentication yet"),
    BUYER_NAME_WRONG("PG_ER12", "Buyer last name or first name is wrong"),
    BUYER_CITY_STATE_WRONG("PG_ER13", "Buyer city/state is wrong"),
    OTP_WRONG("PG_ER16", "OTP is wrong"),
    CARD_INFO_NOT_APPROVED("PG_ER17", "Card information has not been approved yet, please contact to issuing bank to be supported"),
    CARD_EXPIRED_OR_LOCKED("PG_ER18", "Card expired or locked"),
    INSUFFICIENT_FUNDS("PG_ER19", "The amount of money is not enough to make a payment"),
    TRANSACTION_AMOUNT_OUT_OF_LIMIT("PG_ER20", "The amount of money of transaction is not within the allowed limit"),
    CARD_NOT_ACTIVATED_FOR_ONLINE("PG_ER21", "Card has not been activated or signed up for online payment yet"),
    CARD_HOLDER_NAME_WRONG("PG_ER22", "Card holder name is wrong"),
    TRANSACTION_DENIED_BY_BANK("PG_ER23", "Issuing bank denied the transaction"),
    DENIED_BY_FRAUD_SYSTEM("PG_ER25", "The transaction was denied by fraud management system"),
    DATA_INVALID_OR_EMPTY("PG_ER26", "Data is invalid or empty"),
    TRANSACTION_POSTPONED_BY_BANK("PG_ER28", "Issuing bank is postponing this transaction. Please try again later"),
    CUSTOMER_IN_BLACKLIST("PG_ER29", "Transaction failed because customer is in blacklist"),
    CUSTOMER_AUTHENTICATION_FAILED("PG_ER30", "Transaction failed – Cannot authenticate the customer"),
    EXCEEDS_DAILY_LIMIT("PG_ER31", "Transaction exceeds daily limit set by bank"),
    EXCEEDS_MAXIMUM_LIMIT("PG_ER32", "Transaction value exceeds maximum limit set by bank"),
    OTP_TIMEOUT("PG_ER42", "OTP time out (if you are charged, it will be refunded)"),
    ISSUING_BANK_BUSY("PG_ER43", "Issuing bank is busy. Please try again"),
    PENDING_WITH_ZALOPAY("ZL_1", "Pending with zalopay"),
    USER_ERROR_WITH_ZALOPAY("ZL_2", "User error with zalopay"),
    MERCHANT_ERROR_WITH_ZALOPAY("ZL_3", "Merchant error with zalopay"),
    SYSTEM_ERROR_WITH_ZALOPAY("ZL_4", "System error with zalopay"),
    UNKNOWN_ERROR_WITH_ZALOPAY("ZL_5", "Unknown error with zalopay"),
    USER_AND_SYSTEM_ERROR_WITH_ZALOPAY("ZL_6", "User error and system error with zalopay"),
    BANK_ERROR_WITH_ZALOPAY("ZL_7", "Bank error with zalopay"),
    OTHER_ERROR_WITH_ZALOPAY("ZL_8", "Other error with zalopay"),
    EMPTY_ACCESSKEY_OR_PARTNERCODE("MM_1", "Empty accessKey or partnerCode"),
    DUPLICATED_REQUESTID("MM_12", "Duplicated requestID"),
    PARTNER_NOT_ACTIVATED("MM_14", "Partner is not activated"),
    ORDERID_WRONG_FORMAT("MM_2", "OrderId is in wrong format"),
    SYSTEM_MAINTENANCE("MM_29", "System maintenance. Please try in few minutes"),
    TRANSACTION_PURCHASED("MM_32", "Transaction was purchased"),
    TRANSACTION_CANNOT_BE_REFUNDED("MM_33", "Transaction cannot be refunded"),
    TRANSACTION_REFUNDED("MM_34", "Transaction refunded"),
    EXPIRED_TRANSACTION("MM_36", "Expired transaction"),
    CAPSET_EXCEEDED("MM_37", "Capset exceeded"),
    INSUFFICIENT_FUNDS_MM("MM_38", "Insufficient funds"),
    AMOUNT_INVALID_RANGE("MM_4", "Amount is invalid, should be between 1,000VND and 20,000,000 VND"),
    SERVICE_NOT_SUPPORT_REQUEST("MM_44", "Service does not support your request"),
    ORDER_CANCELLED_BY_USER("MM_49", "Order cancelled by user"),
    SIGNATURE_WRONG("MM_5", "Signature is wrong. Check raw signature before signed"),
    TRANSACTION_DOES_NOT_EXIST("MM_58", "Transaction does not exist"),
    ERROR_PARSING_JSON("MM_59", "Error parsing body to Json object"),
    ORDERID_EXISTS("MM_6", "OrderId exists"),
    PAY_BY_BANK_SOURCE_FAILED("MM_63", "Pay by bank source failed"),
    PENDING_TRANSACTION("MM_7", "Pending transaction"),
    USER_FAILED_AUTHENTICATION("MM_80", "User failed authentication"),
    USER_NO_LINKED_BANK_ACCOUNT("MM_9043", "User does not link bank account"),
    ERROR_UNDEFINED("MM_99", "Error undefined"),
    BANK_OR_INSTALLMENT_TERM_NOT_SUPPORTED("IS_001", "Bank or term of installment not supported"),
    VIETTEL_TRANSACTION_FAIL_REFUND("VT_01", "Transaction fail from Viettel Money or Bank, if user has been deducted money, will be refunded within 7-15 days. Please contact CS center 18009000 for support"),
    WRONG_PASSWORD_PIN_OTP("VT_02", "Wrong password/PIN/OTP or expired OTP"),
    VIETTEL_ACCOUNT_INACTIVE("VT_03", "Viettel Money account has in inactivated/locked/cancelled. Please contact CS center 18009000 for support"),
    LINKED_CARD_INFO_NOT_FOUND("VT_04", "Cannot find the linked card information or card link has expired. Please unlink and re-link"),
    PHONE_NUMBER_MISMATCH("VT_05", "Phone number doesn't match / didn't registered with card number"),
    EXCEEDED_MONTHLY_LIMIT("VT_06", "Amount or count of transaction exceeded monthly limit"),
    MISSING_PHONE_OR_AMOUNT("VT_07", "Didn't input phone number or payment amount. Please contact CS center 18009000 for support"),
    SUBSCRIBER_INFO_NOT_FOUND("VT_08", "Cannot find subscriber information/account cardholder information/ID number or the information doesn't match. Please check again or contact CS center 18009000 for support"),
    OTP_SEND_ERROR("VT_09", "Cannot find phone number to send OTP or cannot send OTP. Please contact CS center 18009000 for support"),
    ACCOUNT_NOT_LINKED_AUTO_PAYMENT("VT_10", "Viettel Money account hasn’t been linked for automatic payment, please check again before implementing transaction again"),
    ACCOUNT_ALREADY_LINKED_AUTO_PAYMENT("VT_11", "Viettel Money account has been linked for automatic payment, please check again before implementing transaction again"),
    NOT_ELIGIBLE_FOR_MOBILE_MONEY("VT_12", "You are not eligible to use Mobile Money service. Please contact CS center 18009000 for support"),
    NEED_CHANGE_PIN("VT_13", "Transaction failed. Account needs to change PIN before making Online payment, please access Viettel Money App to check."),
    // UNKNOWN
    UNKNOWN("UNKNOWN", "Unknown transaction status");

    companion object {
        fun fromCode(code: String?): EpayTransactionResultCodeEnum {
            return entries.find { it.code == code } ?: UNKNOWN
        }
        fun toPaymentStatus(code: String?): OrderPaymentstatus {
            return when {
                isSuccess(code) -> OrderPaymentstatus.PAYMENT_SUCCESS
                isPending(code) -> OrderPaymentstatus.PENDING
                isNotYet(code)  -> OrderPaymentstatus.NOT_YET
                else            -> OrderPaymentstatus.PAYMENT_FAIL
            }
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
                    result == PENDING_WITH_ZALOPAY ||
                    result == PENDING_TRANSACTION
        }

        fun isNotYet(code: String?): Boolean {
            val result = fromCode(code)
            return result == TRANSACTION_NOT_EXIST ||
                    result == TRANSACTION_SEARCH_NOT_EXIST
        }

        fun isFailed(code: String?): Boolean {
            return !isSuccess(code) &&
                    !isPending(code) &&
                    !isNotYet(code)
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
    UNKNOWN("UNKNOWN", "Unknown transaction status");

    companion object {

        fun fromCode(code: String?): EpayTransactionStatuEnum {
            return entries.find { it.code == code } ?: UNKNOWN
        }

        fun isSuccess(code: String?): Boolean {
            val result = fromCode(code)
            return result == SUCCESS_NOT_REFUND
        }
        fun isRefund(code: String?): Boolean {
            val result = fromCode(code)
            return result == SUCCESS_ALREADY_REFUND
        }
    }
}