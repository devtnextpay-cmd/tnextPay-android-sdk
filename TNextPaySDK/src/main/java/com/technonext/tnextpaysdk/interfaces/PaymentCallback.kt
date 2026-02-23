package com.technonext.tnextpaysdk.interfaces

import com.technonext.tnextpaysdk.model.PaymentResponse

/**
 * Callback interface for payment transaction results
 */
interface PaymentCallback {
    /**
     * Called when payment is completed (success, failure, or cancelled)
     */
    fun onPaymentComplete(response: PaymentResponse)

    /**
     * Called when an error occurs during payment process
     */
    fun onPaymentError(error: String)
}