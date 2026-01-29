package com.vietq.demo_map_app_backend.utils

sealed class ApiResponse<out T> {
    abstract val success: Boolean
    abstract val message: String
    abstract val statusCode: Number
}

data class SuccessResponse<T>(
    override val success: Boolean = true,
    val data: T,
    override val message: String = "Success",
    override val statusCode: Number = 200
) : ApiResponse<T>()

data class ErrorResponse(
    override val success: Boolean = false,
    val data: Nothing? = null,
    override val message: String,
    override val statusCode: Number = 400
) : ApiResponse<Nothing>()

fun <T> T.toSuccessResponse(message: String = "Success"): SuccessResponse<T> {
    return SuccessResponse(
        success = true,
        data = this,
        message = message,
        statusCode = 200
    )
}

