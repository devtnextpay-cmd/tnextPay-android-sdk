package com.technonext.tnextpaysdk.config

import com.technonext.tnextpaysdk.utils.Constants

/**
 * Internal configuration object for the SDK.
 * Holds runtime config derived from consumer input and build config.
 */
internal data class SDKConfig(
    val merchantKey: String,
    val baseUrl: String = Constants.BASE_URL,
    val timeoutMs: Long = Constants.WEBVIEW_TIMEOUT_MS
) {
    init {
        require(merchantKey.isNotBlank()) {
            "Merchant key cannot be blank"
        }
    }

    // Full URL ready for WebView
    val fullPaymentUrl: String
        get() = if (baseUrl.endsWith("/")) {
            "$baseUrl$merchantKey"
        } else {
            "$baseUrl/$merchantKey"
        }
}