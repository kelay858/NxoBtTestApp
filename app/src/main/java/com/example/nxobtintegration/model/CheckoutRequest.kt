package com.example.nxobtintegration.model

import com.braintreepayments.api.PayPalNativeCheckoutPaymentIntent
import com.braintreepayments.api.PostalAddress

data class CheckoutRequest(
    val address: PostalAddress? = null,
    val displayName: String? = null,
    val shouldOfferPPC: Boolean = false,
    val isAddressEditable: Boolean = false,
    val intentType: String = PayPalNativeCheckoutPaymentIntent.AUTHORIZE,
    val payMode: PayMode = PayMode.PAY_NOW,
)

enum class PayMode {
    CONTINUE,
    PAY_NOW;

    fun toList(): List<String> {
        return values().map { it.toString() }
    }
}
