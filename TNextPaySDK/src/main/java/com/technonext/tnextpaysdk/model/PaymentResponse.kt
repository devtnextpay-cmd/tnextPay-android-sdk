package com.technonext.tnextpaysdk.model

data class PaymentResponse(
    val status: TransactionStatus,
    val transactionId: String? = null,
    val amount: String? = null,
    val currency: String? = null,
    val message: String? = null,
    val rawData: Map<String, String>? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Check if transaction was successful
     */
    fun isSuccessful(): Boolean = status == TransactionStatus.SUCCESS

    /**
     * Check if transaction failed
     */
    fun isFailed(): Boolean = status == TransactionStatus.FAILURE

    /**
     * Check if transaction was cancelled by user
     */
    fun isCancelled(): Boolean = status == TransactionStatus.CANCELLED
}