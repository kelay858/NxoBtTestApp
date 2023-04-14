package com.example.nxobtintegration.auth

import android.content.Context
import android.view.ContextMenu
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.ClientTokenCallback
import com.braintreepayments.api.ClientTokenProvider
import com.braintreepayments.api.PayPalNativeCheckoutClient
import com.braintreepayments.api.PayPalNativeCheckoutListener

class PayPalNativeCheckoutClientProvider(
    context: Context,
    clientTokenProvider: ClientTokenProvider
) {
    private val braintreeClient: BraintreeClient = BraintreeClient(context, clientTokenProvider)
    private lateinit var payPalNativeCheckoutClient: PayPalNativeCheckoutClient

    fun setup(listener: PayPalNativeCheckoutListener) {
        payPalNativeCheckoutClient = PayPalNativeCheckoutClient(braintreeClient)
        payPalNativeCheckoutClient.setListener(listener)
    }

    fun getClient(): PayPalNativeCheckoutClient {
        return payPalNativeCheckoutClient
    }
}
