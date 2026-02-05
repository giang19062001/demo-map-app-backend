import com.study.jooq.enums.OrderEpayPaytype



// REDIRECT_URL AND IPN
data class PaymentCallbackDto(
    var resultCd: String,
    val resultMsg: String,
    val merId: String,
    val timeStamp: String,
    val invoiceNo: String,
    var status: String,
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

// FOR INSERT/UPDATE DATA ON 'PAYMENT' TABLE
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
    val payType: OrderEpayPaytype?, // ENUM
    val payOption: String? = null,
    val bankId: String? = null,
    val bankCode: String? = null,
    val cardNo: String? = null,
    val cardType: String? = null,
    val cardTypeValue: String? = null,
)
