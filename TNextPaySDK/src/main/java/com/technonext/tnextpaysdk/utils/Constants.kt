package com.technonext.tnextpaysdk.utils
internal object Constants {
    internal val BASE_URL: String = "https://api-stage.tnextpay.com/checkout/"

    // WebView settings
    internal const val WEBVIEW_TIMEOUT_MS = 30_000L
    internal const val PAYMENT_EXTRA_KEY = "payment_key"
    internal const val PAYMENT_RESULT_CODE = 1001

    // JavaScript interface name (how JS calls back into Android)
    internal const val JS_BRIDGE_NAME = "TNestPayBridge"

    // URL patterns to detect transaction result
    internal const val SUCCESS_URL_PATTERN = "/success/"
    internal const val FAILURE_URL_PATTERN = "/failure/"
    internal const val CANCEL_URL_PATTERN  = "/cancel/"
    internal const val PENDING_URL_PATTERN = "/pending/"
}