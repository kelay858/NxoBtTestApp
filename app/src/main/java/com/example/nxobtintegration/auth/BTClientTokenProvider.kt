package com.example.nxobtintegration.auth

import com.braintreepayments.api.ClientTokenCallback
import com.braintreepayments.api.ClientTokenProvider
import java.lang.Exception

class BTClientTokenProvider: ClientTokenProvider {
    override fun getClientToken(callback: ClientTokenCallback) {
        if (BTConfig.clientTokenizationKey != null) {
            callback.onSuccess(BTConfig.clientTokenizationKey)
        } else {
            callback.onFailure(Exception("No Client Token specified"))
        }
    }
}