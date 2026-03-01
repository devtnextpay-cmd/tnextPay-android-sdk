package com.technonext.tnextpaysdk
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.technonext.tnextpaysdk.config.SDKConfig
import com.technonext.tnextpaysdk.interfaces.PaymentCallback
import com.technonext.tnextpaysdk.model.PaymentResponse
import com.technonext.tnextpaysdk.model.TransactionStatus
import com.technonext.tnextpaysdk.utils.Constants

class TNestPaySDK private constructor(
    private val config: SDKConfig,
    private val callback: PaymentCallback?
) {

    companion object {
        private const val TAG = "TNestPaySDK"

        // Singleton instance for callback handling
        @Volatile
        private var callbackInstance: PaymentCallback? = null

        internal fun setCallback(callback: PaymentCallback?) {
            callbackInstance = callback
        }

        internal fun getCallback(): PaymentCallback? = callbackInstance

        internal fun clearCallback() {
            callbackInstance = null
        }
    }

    /**
     * Start payment flow from an Activity
     *
     * @param activity The activity to start payment from
     */
    fun startPayment(activity: Activity) {
        try {
            Log.d(TAG, "Starting payment with key: ${config.merchantKey}")

            // Store callback for result handling
            setCallback(callback)

            // Create intent to PaymentActivity
            val intent = Intent(activity, TNextPayActivity::class.java).apply {
                putExtra(Constants.PAYMENT_EXTRA_KEY, config.merchantKey)
            }

            activity.startActivityForResult(intent, Constants.PAYMENT_RESULT_CODE)

        } catch (e: Exception) {
            Log.e(TAG, "Error starting payment", e)
            callback?.onPaymentError("Failed to start payment: ${e.message}")
        }
    }

    /**
     * Start payment flow from a Fragment
     *
     * @param fragment The fragment to start payment from
     */
    fun startPayment(fragment: Fragment) {
        try {
            Log.d(TAG, "Starting payment from fragment with key: ${config.merchantKey}")

            // Store callback for result handling
            setCallback(callback)

            // Create intent to PaymentActivity
            val intent = Intent(fragment.requireContext(), TNextPayActivity::class.java).apply {
                putExtra(Constants.PAYMENT_EXTRA_KEY, config.merchantKey)
            }

            fragment.startActivityForResult(intent, Constants.PAYMENT_RESULT_CODE)

        } catch (e: Exception) {
            Log.e(TAG, "Error starting payment from fragment", e)
            callback?.onPaymentError("Failed to start payment: ${e.message}")
        }
    }

    /**
     * Get the full payment URL (for debugging purposes)
     */
    fun getPaymentUrl(): String = config.fullPaymentUrl

    /**
     * Builder class for TNestPaySDK
     */
    class Builder {
        private var sessionKey: String? = null
        private var callback: PaymentCallback? = null
        private var timeoutMs: Long = Constants.WEBVIEW_TIMEOUT_MS

        /**
         * Set the merchant key (required)
         * This key will be concatenated with the base URL
         */
        fun setMerchantKey(key: String): Builder {
            this.sessionKey = key
            return this
        }

        /**
         * Set payment callback (optional but recommended)
         */
        fun setCallback(callback: PaymentCallback): Builder {
            this.callback = callback
            return this
        }

        /**
         * Set WebView timeout in milliseconds (optional)
         * Default: 30000ms (30 seconds)
         */
        fun setTimeout(timeoutMs: Long): Builder {
            this.timeoutMs = timeoutMs
            return this
        }

        /**
         * Build the TNestPaySDK instance
         *
         * @throws IllegalArgumentException if merchant key is not set
         */
        fun build(): TNestPaySDK {
            val key = sessionKey ?: throw IllegalArgumentException(
                "Merchant key is required. Use setMerchantKey() to provide it."
            )

            val config = SDKConfig(
                merchantKey = key,
                timeoutMs = timeoutMs
            )

            return TNestPaySDK(config, callback)
        }
    }

    /**
     * Helper method to handle payment result in Activity.onActivityResult()
     *
     * Usage in Activity:
     * ```
     * override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
     *     super.onActivityResult(requestCode, resultCode, data)
     *     TNestPaySDK.handleActivityResult(requestCode, resultCode, data)
     * }
     * ```
     */
    object ResultHandler {
        fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
            if (requestCode != Constants.PAYMENT_RESULT_CODE) {
                return false
            }

            val callback = getCallback()
            if (callback == null) {
                Log.w(TAG, "No callback registered to handle payment result")
                return false
            }

            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Payment completed - extract response from intent
                    val response = extractPaymentResponse(data)
                    callback.onPaymentComplete(response)
                }
                Activity.RESULT_CANCELED -> {
                    // Payment cancelled by user
                    callback.onPaymentComplete(
                        PaymentResponse(
                            status = TransactionStatus.CANCELLED,
                            message = "Payment cancelled by user"
                        )
                    )
                }
                else -> {
                    // Unknown result
                    callback.onPaymentError("Unknown payment result code: $resultCode")
                }
            }

            // Clear callback after handling
            clearCallback()
            return true
        }

        private fun extractPaymentResponse(data: Intent?): PaymentResponse {
            // TODO: Extract actual response data from intent in next steps
            // For now, return a basic success response
            return PaymentResponse(
                status = TransactionStatus.SUCCESS,
                message = "Payment processed"
            )
        }
    }
}