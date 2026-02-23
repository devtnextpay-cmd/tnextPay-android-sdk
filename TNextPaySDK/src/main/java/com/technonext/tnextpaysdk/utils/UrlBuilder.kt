package com.technonext.tnextpaysdk.utils


internal object UrlBuilder {

    private const val TAG = "TNestPay_UrlBuilder"


    internal fun buildPaymentUrl(key: String): String {
        val baseUrl = Constants.BASE_URL
        val sanitizedKey = key.trim()

        require(sanitizedKey.isNotEmpty()) {
            "Payment key must not be empty"
        }

        // Ensure no double slashes
        val normalizedBase = if (baseUrl.endsWith("/")) {
            baseUrl
        } else {
            "$baseUrl/"
        }

        return "$normalizedBase$sanitizedKey"
    }
}