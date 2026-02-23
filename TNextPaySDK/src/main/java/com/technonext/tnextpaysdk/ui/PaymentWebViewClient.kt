package com.technonext.tnextpaysdk.ui

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.technonext.tnextpaysdk.model.PaymentResponse
import com.technonext.tnextpaysdk.model.TransactionStatus
import com.technonext.tnextpaysdk.utils.Constants

/**
 * Custom WebViewClient to track payment page navigation and detect transaction results
 */
internal class PaymentWebViewClient(
    private val onPageLoadingChanged: (Boolean) -> Unit,
    private val onTransactionComplete: (PaymentResponse) -> Unit,
    private val onError: (String) -> Unit
) : WebViewClient() {

    companion object {
        private const val TAG = "PaymentWebViewClient"
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Log.d(TAG,"Page started loading: $url")
        onPageLoadingChanged(true)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d(TAG,"Page finished loading: $url")
        onPageLoadingChanged(false)

        // Check if the current URL indicates transaction completion
        url?.let { checkTransactionStatus(it) }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString() ?: return false
        Log.d(TAG,"URL loading: $url")

        // Check if URL indicates transaction result
        if (isTransactionResultUrl(url)) {
            checkTransactionStatus(url)
            return true // Prevent navigation, we'll handle the result
        }

        return false // Allow normal navigation
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        val errorMessage = "WebView error: ${error?.description}"
        Log.e(TAG, errorMessage)
        onError(errorMessage)
    }

    /**
     * Check if URL contains transaction result indicators
     */
    private fun isTransactionResultUrl(url: String): Boolean {
        return url.contains(Constants.SUCCESS_URL_PATTERN, ignoreCase = true) ||
                url.contains(Constants.FAILURE_URL_PATTERN, ignoreCase = true) ||
                url.contains(Constants.CANCEL_URL_PATTERN, ignoreCase = true) ||
                url.contains(Constants.PENDING_URL_PATTERN, ignoreCase = true)
    }

    /**
     * Parse URL to determine transaction status and extract data
     */
    private fun checkTransactionStatus(url: String) {
        Log.d(TAG,"Checking transaction status for URL: $url")

        val status = when {
            url.contains(Constants.SUCCESS_URL_PATTERN, ignoreCase = true) -> TransactionStatus.SUCCESS
            url.contains(Constants.FAILURE_URL_PATTERN, ignoreCase = true) -> TransactionStatus.FAILURE
            url.contains(Constants.CANCEL_URL_PATTERN, ignoreCase = true) -> TransactionStatus.CANCELLED
            url.contains(Constants.PENDING_URL_PATTERN, ignoreCase = true) -> TransactionStatus.PENDING
            else -> return // Not a result URL
        }

        // Extract query parameters from URL
        val queryParams = extractQueryParameters(url)

        val response = PaymentResponse(
            status = status,
            transactionId = queryParams["transaction_id"] ?: queryParams["txn_id"],
            amount = queryParams["amount"],
            currency = queryParams["currency"],
            message = queryParams["message"] ?: getDefaultMessage(status),
            rawData = queryParams
        )

//        logDebug("Transaction detected: $status")
        onTransactionComplete(response)
    }

    /**
     * Extract query parameters from URL
     */
    private fun extractQueryParameters(url: String): Map<String, String> {
        val params = mutableMapOf<String, String>()

        try {
            val uri = android.net.Uri.parse(url)
            uri.queryParameterNames.forEach { key ->
                uri.getQueryParameter(key)?.let { value ->
                    params[key] = value
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting query parameters", e)
        }

        return params
    }

    /**
     * Get default message based on transaction status
     */
    private fun getDefaultMessage(status: TransactionStatus): String {
        return when (status) {
            TransactionStatus.SUCCESS -> "Payment completed successfully"
            TransactionStatus.FAILURE -> "Payment failed"
            TransactionStatus.CANCELLED -> "Payment cancelled by user"
            TransactionStatus.PENDING -> "Payment is pending"
            TransactionStatus.TIMEOUT -> "Payment timed out"
            TransactionStatus.ERROR -> "An error occurred"
        }
    }

//    private fun logDebug(message: String) {
//        if (BuildConfig.DEBUG) {
//            Log.d(TAG, message)
//        }
//    }
}