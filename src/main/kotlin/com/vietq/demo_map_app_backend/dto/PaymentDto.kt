
data class UpsertPaymentDto(
    val orderCode: String,
    val trxId: String?,
    val merTrxId: String,
)
data class PaymentCallbackUrlDto(
    val resultCd: String,
    val resultMsg: String,
    val invoiceNo: String,
    val status: String,
    val merTrxId: String,
    val trxId: String,
    val amount: String,
    val payType: String,
    val bankId: String,
    val merchantToken: String
)

data class PaymentCallbackIpnDto(
    val resultCd: String,
    val resultMsg: String,
    val invoiceNo: String,
    val status: String,
    val merTrxId: String,
    val trxId: String,
    val amount: String,
    val payType: String,
    val bankId: String,
    val merchantToken: String
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

data class PaymentCheckStatusResponseDto(
    val resultCd: String,
    val resultMsg: String?,
    val data: PaymentCheckStatusDataResponse?
)

data class PaymentCheckStatusDataResponse(
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
    // nullable
    val currency: String? = null,
    val invoiceNo: String? = null,
    val goodsNm: String? = null,
    val payType: String? = null,
    val buyerFirstNm: String? = null,
    val buyerLastNm: String? = null,
    val buyerEmail: String? = null,
    val bankId: String? = null,
    val cardNo: String? = null,
    val trxDt: String? = null,
    val trxTm: String? = null,
    val remainAmount: String? = null,
    val userFee: String? = null,
    val merchantFee: String? = null,
    val merFeeToken: String? = null,
    val holderNm: String? = null,
    val cardType: String? = null
)

data class PaymentDbResponseDto(
    val id: Long,
    val orderCode: String,
    val trxId: String,
    val merTrxId: String,
)

