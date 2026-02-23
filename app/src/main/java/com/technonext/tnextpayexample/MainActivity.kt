package com.technonext.tnextpayexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.technonext.tnextpaysdk.TNestPaySDK
import com.technonext.tnextpaysdk.interfaces.PaymentCallback
import com.technonext.tnextpaysdk.model.PaymentResponse
import android.widget.Button
import android.widget.TextView
import com.technonext.tnextpayexample.R

class MainActivity : AppCompatActivity() {

    private lateinit var etMerchantKey: TextInputEditText
    private lateinit var btnStartPayment: Button
    private lateinit var tvResult: TextView

    private lateinit var paymentSDK: TNestPaySDK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        etMerchantKey = findViewById(R.id.etMerchantKey)
        btnStartPayment = findViewById(R.id.btnStartPayment)
        tvResult = findViewById(R.id.tvResult)

        // Setup button click
        btnStartPayment.setOnClickListener {
            startPayment()
        }
    }

    private fun startPayment() {
        val merchantKey = etMerchantKey.text.toString().trim() //"01KJ4FC43HDPFX57RTN337G5W2"

        if (merchantKey.isEmpty()) {
            Toast.makeText(this, "Please enter merchant key", Toast.LENGTH_SHORT).show()
            return
        }

        // Clear previous result
        tvResult.text = "Processing payment..."

        try {
            // Initialize SDK with Builder pattern
            paymentSDK = TNestPaySDK.Builder()
                .setMerchantKey(merchantKey)
                .setTimeout(60000) // 60 seconds timeout
                .setCallback(object : PaymentCallback {
                    override fun onPaymentComplete(response: PaymentResponse) {
                        handlePaymentResult(response)
                    }

                    override fun onPaymentError(error: String) {
                        handlePaymentError(error)
                    }
                })
                .build()

            // Log the payment URL (for debugging)
            val paymentUrl = paymentSDK.getPaymentUrl()
            println("Payment URL: $paymentUrl")
            Toast.makeText(this, "Opening payment page...", Toast.LENGTH_SHORT).show()

            // Start payment
            paymentSDK.startPayment(this)

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            tvResult.text = "Error: ${e.message}"
            println("Web Error: ${e.message}")
        }
    }

    private fun handlePaymentResult(response: PaymentResponse) {
        val resultText = buildString {
            appendLine("━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("PAYMENT RESULT")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━")
            appendLine()
            appendLine("Status: ${response.status}")
            appendLine("Success: ${response.isSuccessful()}")
            appendLine()

            response.transactionId?.let {
                appendLine("Transaction ID: $it")
            }

            response.amount?.let {
                appendLine("Amount: $it")
            }

            response.currency?.let {
                appendLine("Currency: $it")
            }

            response.message?.let {
                appendLine("Message: $it")
            }

            appendLine()
            appendLine("Timestamp: ${response.timestamp}")

            response.rawData?.let { data ->
                if (data.isNotEmpty()) {
                    appendLine()
                    appendLine("Raw Data:")
                    data.forEach { (key, value) ->
                        appendLine("  $key: $value")
                    }
                }
            }

            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━━━━━━")
        }

        tvResult.text = resultText

        // Show toast based on result
        val toastMessage = when {
            response.isSuccessful() -> "✓ Payment Successful!"
            response.isFailed() -> "✗ Payment Failed"
            response.isCancelled() -> "Payment Cancelled"
            else -> "Payment ${response.status}"
        }

        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun handlePaymentError(error: String) {
        tvResult.text = "ERROR:\n$error"
        Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle SDK result
        TNestPaySDK.ResultHandler.handleActivityResult(requestCode, resultCode, data)
    }
}