import com.study.jooq.enums.OrderPaymentEpayPaytype

data class UpsertPaymentDto(
    val orderCode: String,
    val trxId: String,
    val merTrxId: String,
    val amount: String,
    val status: String,
    val resultCd: String,
    val resultMsg: String,
    val goodsNm: String? = null,
    val buyerFirstNm: String? = null,
    val buyerLastNm: String? = null,
    val remainAmount : String? = null,
    val payType: OrderPaymentEpayPaytype?, // ENUM
    val payOption: String? = null,
    val bankId: String? = null,
    val bankCode: String? = null,
    val cardNo: String? = null,
    val cardType: String? = null,
    val cardTypeValue: String? = null,
)
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
data class PaymentCallbackDto(
    val resultCd: String,
    val resultMsg: String,
    val merId: String,
    val timeStamp: String,
    val invoiceNo: String,
    val status: String,
    val merTrxId: String,
    val trxId: String,
    val amount: String,
    val payType: String,
    val payOption: String?,
    val bankId: String,
    val bankCode: String?,
    val merchantToken: String,
    val remainAmount: String?,
    val goodsNm: String?,
    val buyerFirstNm: String?,
    val buyerLastNm: String?,
    val cardNo: String?,
    val cardType: String?,
    val cardTypeValue: String?,
    )

data class PaymentCheckInvoiceDataResponseDto(
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

data class PaymentCheckTransactionDataResponseDto(
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

data class PaymentCheckInvoiceResponseDto(
    val resultCd: String,
    val resultMsg: String?,
    val data: PaymentCheckInvoiceDataResponseDto?
)

data class PaymentCheckTransactionResponseDto(
    val resultCd: String,
    val resultMsg: String?,
    val data: PaymentCheckTransactionDataResponseDto?
)

