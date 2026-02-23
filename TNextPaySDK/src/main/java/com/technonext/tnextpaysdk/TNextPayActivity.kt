package com.technonext.tnextpaysdk
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.technonext.tnextpaysdk.databinding.ActivityPaymentBinding
import com.technonext.tnextpaysdk.model.PaymentResponse
import com.technonext.tnextpaysdk.model.TransactionStatus
import com.technonext.tnextpaysdk.ui.PaymentWebViewClient
import com.technonext.tnextpaysdk.utils.Constants
import com.technonext.tnextpaysdk.utils.UrlBuilder
class TNextPayActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPaymentBinding
    private var merchantKey: String? = null
    private var paymentUrl: String? = null

    companion object {
        private const val TAG = "PaymentActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setupToolbar()
        extractMerchantKey()
        setupWebView()
        loadPaymentPage()
    }

    /**
     * Setup toolbar with back button
     */
//    private fun setupToolbar() {
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//
//        binding.toolbar.setNavigationOnClickListener {
//            onBackPressed()
//        }
//    }

    /**
     * Extract merchant key from intent
     */
    private fun extractMerchantKey() {
        merchantKey = intent.getStringExtra(Constants.PAYMENT_EXTRA_KEY)

        if (merchantKey.isNullOrBlank()) {
            Log.e(TAG, "Merchant key is missing!")
            showError("Invalid payment configuration")
            finishWithError("Merchant key is required")
            return
        }

        // Build full payment URL
        paymentUrl = try {
            UrlBuilder.buildPaymentUrl(merchantKey!!)
        } catch (e: Exception) {
            Log.e(TAG, "Error building payment URL", e)
            null
        }

        if (paymentUrl == null) {
            finishWithError("Failed to build payment URL")
        }
    }

    /**
     * Setup WebView with proper settings
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(binding.webView) {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(true)
                builtInZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_DEFAULT

                // Security settings
                allowFileAccess = false
                allowContentAccess = false

                // Modern WebView settings
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            // Set custom WebViewClient
            webViewClient = PaymentWebViewClient(
                onPageLoadingChanged = { isLoading ->
                    updateLoadingState(isLoading)
                },
                onTransactionComplete = { response ->
                    handleTransactionComplete(response)
                },
                onError = { error ->
                    showError(error)
                    finishWithError(error)
                }
            )

            // Enable progress tracking
            webChromeClient = object : android.webkit.WebChromeClient() {
                override fun onProgressChanged(view: android.webkit.WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                    binding.progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
                }
            }
        }
    }

    /**
     * Load payment page in WebView
     */
    private fun loadPaymentPage() {
        paymentUrl?.let { url ->
            Log.d(TAG, "Loading payment URL: $url")
            binding.webView.loadUrl(url)
        } ?: run {
            finishWithError("Payment URL is not available")
        }
    }

    /**
     * Update loading state UI
     */
    private fun updateLoadingState(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    /**
     * Handle transaction completion
     */
    private fun handleTransactionComplete(response: PaymentResponse) {
        Log.d(TAG, "Transaction complete: ${response.status}")

        // Return result to SDK
        setResult(Activity.RESULT_OK, intent.apply {
            putExtra("status", response.status.name)
            putExtra("transaction_id", response.transactionId)
            putExtra("amount", response.amount)
            putExtra("currency", response.currency)
            putExtra("message", response.message)
        })

        finish()
    }

    /**
     * Show error message to user
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Finish activity with error result
     */
    private fun finishWithError(error: String) {
        setResult(Activity.RESULT_CANCELED, intent.apply {
            putExtra("error", error)
        })
        finish()
    }

    /**
     * Handle back button press
     */
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            // User wants to cancel payment
            setResult(Activity.RESULT_CANCELED, intent.apply {
                putExtra("status", TransactionStatus.CANCELLED.name)
                putExtra("message", "Payment cancelled by user")
            })
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }
}