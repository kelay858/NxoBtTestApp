package com.example.nxobtintegration.util

import com.braintreepayments.api.PayPalNativeCheckoutRequest
import com.braintreepayments.api.PayPalNativeCheckoutVaultRequest
import com.braintreepayments.api.PostalAddress
import com.example.nxobtintegration.auth.BTConfig
import com.example.nxobtintegration.model.CheckoutRequest
import com.example.nxobtintegration.model.PayMode

class CreateRequestFactory(
    private val checkoutRequest: CheckoutRequest
) {
    fun createPayPalVaultRequest(): PayPalNativeCheckoutVaultRequest {
        val request = PayPalNativeCheckoutVaultRequest()

        if (checkoutRequest.displayName != null) {
            request.displayName = checkoutRequest.displayName
        }
        if (checkoutRequest.shouldOfferPPC) {
            request.shouldOfferCredit = true
        }

        if (checkoutRequest.isAddressEditable && checkoutRequest.address != null) {
            request.isShippingAddressEditable = true
            request.isShippingAddressRequired = true
            request.shippingAddressOverride = checkoutRequest.address
        }

        request.returnUrl = BTConfig.returnUrl
        return request
    }

    fun createPayPalCheckoutRequest(amount: String): PayPalNativeCheckoutRequest {
        val request = PayPalNativeCheckoutRequest(amount)
        if (checkoutRequest.displayName != null) {
            request.displayName = checkoutRequest.displayName
        }
        // check this
        request.intent = checkoutRequest.intentType

        if (checkoutRequest.payMode == PayMode.PAY_NOW) {
            request.userAction = PayPalNativeCheckoutRequest.USER_ACTION_COMMIT
        }

        if (checkoutRequest.address != null) {
            request.isShippingAddressEditable = checkoutRequest.isAddressEditable
            request.isShippingAddressRequired = true
            request.shippingAddressOverride = checkoutRequest.address
        }

        if (checkoutRequest.address == null && checkoutRequest.isAddressEditable) {
            request.isShippingAddressEditable = true
        }

        request.returnUrl = BTConfig.returnUrl
        return request
    }
}

object MockDataProvider {
    fun createAddress(): PostalAddress {
        return PostalAddress().apply {
            recipientName = "Brian Tree"
            streetAddress = "123 Fake Street"
            extendedAddress = "Floor A"
            postalCode = "94103"
            locality = "San Francisco"
            region = "CA"
            countryCodeAlpha2 = "US"
        }
    }
}