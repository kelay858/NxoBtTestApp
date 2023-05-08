package com.example.nxobtintegration.util

import android.content.Context
import com.example.nxobtintegration.ui.AuthRepo
import com.paypal.authcore.authentication.AuthenticationDelegate
import com.paypal.authcore.authentication.Authenticator
import com.paypal.authcore.authentication.RiskDelegate
import com.paypal.authcore.authentication.model.AuthClientConfig
import com.paypal.authcore.authentication.model.AuthClientConfigBuilder
import com.paypal.authcore.util.EnvironmentUtil
import com.paypal.openid.AuthorizationException
import com.paypal.openid.TokenResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Auth(private val context: Context, private val checkoutToken: String) {
    val foundationRiskConfig = FoundationRiskConfig(context)
    val authenticator by lazy { createAuthenticator(checkoutToken) }

    private val failureCallback = { error: Exception? ->
        println("Doing Error $error")
    }

    fun invoke(successCallback: (String?) -> Unit) {
        foundationRiskConfig.generatePairingIdAndNotifyDyson(
            checkoutToken
        )

        authenticator.authenticateForAccessTokenWithDelegate(
            getAuthDelegate(
                checkoutToken,
                successCallback,
                failureCallback
            ),
            context,
            ""
        )
    }

    private fun getAuthDelegate(trackingId: String, success: (String?) -> Unit, failure: (Exception?) -> Unit ): AuthenticationDelegate {
        val delegate = object : AuthenticationDelegate {
            override fun getTrackingID(): String {
                return trackingId
            }

            override fun completeWithSuccess(token: TokenResponse?) {
                success(token?.accessToken)
                context.getSharedPreferences("pref", Context.MODE_PRIVATE).edit().putString("auth", token?.accessToken).apply()
                println("Doing Auth ${token?.accessToken}")
            }

            override fun completeWithFailure(error: AuthorizationException?) {
                CoroutineScope(Dispatchers.IO).launch {
                    authenticator.logOutUser()
                    failure(error)
                    println("Doing Error $error")
                }
            }
        }
        return delegate
    }

    private fun createAuthenticator(checkoutToken: String): Authenticator {
        val returnUrl = "com.example.nxobtintegration://paypalpay"
        val environmentUtil = EnvironmentUtil("Sandbox")
        val tokenUrl = environmentUtil.tokenURL
        val authUrl = environmentUtil.authorizationURL

        // setup additional params required for auth flow.
        val additionalAuthParams: Map<String, String> = mapOf(
            "redirect_uri" to returnUrl,
            "signup_redirect_uri" to returnUrl,
            "flowName" to "nativeXO",
            "metadata_id" to checkoutToken,
            "prompt" to "login"
        )
        val authClientConfigBuilder = AuthClientConfigBuilder(
            "AT57x1M3ThTWCENZOQ6v__N4XiJYuyhMJ8GWs8jakW1XFl-gXagxFFB-gQS2IVAaMFRMDfAoCwcJyKes",
            returnUrl,
            "https://uri.paypal.com/web/experience/incontextxo",
            environmentUtil.tokenURL,
            environmentUtil.authorizationURL
        )
        authClientConfigBuilder.authorizationParam = additionalAuthParams

        val riskDelegate = RiskDelegate { foundationRiskConfig.riskPayload }

        val authClientConfig = AuthClientConfig(authClientConfigBuilder)
        return Authenticator(
            context,
            authClientConfig,
            riskDelegate
        )
    }
}