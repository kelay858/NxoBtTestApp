package com.example.nxobtintegration.util

import android.content.Context
import lib.android.paypal.com.magnessdk.InvalidInputException
import lib.android.paypal.com.magnessdk.MagnesResult
import lib.android.paypal.com.magnessdk.MagnesSDK
import lib.android.paypal.com.magnessdk.MagnesSettings
import lib.android.paypal.com.magnessdk.MagnesSource
import java.util.Objects

class FoundationRiskConfig constructor(
    context: Context
) {
    private val context: Context
    private val appVersion: String
    private var sourceApp: MagnesSource
    private var magnesSettings: MagnesSettings? = null
    private var magnesResult: MagnesResult? = null
    private var riskInitialized: Boolean
    val riskPayload: String?
        get() = magnesResult!!.deviceInfo.toString()
    private var previousPairingId: String? = null
    fun getSourceApp(): MagnesSource {
        return sourceApp
    }

    /**
     * @param sourceApp
     */
    @Deprecated(
        """This method does not change the sourceApp as sourceApp can only be changed at the time of Magnes init method.
      If you are using this module, your sourceApp will be set as SourceApp.PAYPAL."""
    )
    fun setSourceApp(sourceApp: MagnesSource) {
        Objects.requireNonNull(sourceApp)
        this.sourceApp = sourceApp
    }

    val dysonPairingId: String
        get() = magnesResult!!.paypalClientMetaDataId
    val clientMetaDataId: String
        get() = magnesResult!!.paypalClientMetaDataId

    @Synchronized
    fun initRisk() {
        if (!riskInitialized) {
            Objects.requireNonNull(sourceApp)
            magnesSettings = MagnesSettings.Builder(context)
                .setMagnesSource(sourceApp)
                .build()
            MagnesSDK.getInstance().setUp(magnesSettings!!)
            magnesResult = MagnesSDK.getInstance().collect(context)
            riskInitialized = true
        }
    }

    /**
     * Triggers sending of Dyson payload.
     *
     * @param payerId the payerId which will be passed to RiskComponent as the pairingId
     */
    @Deprecated("use {@link #generatePairingIdAndNotifyDyson(String)}")
    fun sendRiskDataToDyson(payerId: String) {
        generatePairingIdAndNotifyDyson(payerId)
    }

    /**
     * Triggers sending of Dyson payload.
     *
     *
     * NB: This call actually triggers sending of Dyson request. It might not be evident on the code below
     * but the mere re-generation of the pairingID triggers a Dyson request. Thus, we only needed
     * to explicitly call RiskComponent::sendRiskPayload() IFF (if and only if) the there is no change
     * in the pairingId.
     *
     * @param payerId          the payerId which will be passed to RiskComponent as the pairingId
     * @param customParameters custom parameters to send with the payload
     */
    @Deprecated("use {@link #generatePairingIdAndNotifyDyson(String, Map)}")
    fun sendRiskDataToDyson(payerId: String?, customParameters: Map<String?, String?>?) {
        if (payerId == null) {
            //LOGGER.warning("payerId is null. Did not trigger sending risk payload");
            return
        }
        generatePairingIdAndNotifyDyson(payerId, customParameters)
    }

    /**
     * Generates a new pairingID for dyson using the given customID and asynchronously uploads the payload to dyson server.
     *
     * This method is idempotent, calling it multiple times with the same customID will only submit once.
     *
     * @param customID customID passed to RiskComponent to generate a new PairingId
     * For e.g. When app generates a paycode for a transaction that will be carried out in a web view(on merchant's site), client first generates the paycode and calls [.generatePairingIdAndNotifyDyson] and then pass the paycode to merchant site.
     * This allows risk to perform proper validation when merchant site tries use the paycode for a payment.
     */
    fun generatePairingIdAndNotifyDyson(customID: String) {
        //requireNonEmptyString(customID);
        generateNewPairingId(customID, null)
    }

    /**
     * Generates a new pairingID for dyson using the given customID and asynchronously uploads the payload to dyson server.
     *
     * @param customParameters custom parameters to send with the payload
     */
    @Deprecated(
        """This is deprecated in favour of generatePairingIdAndNotifyDyson(@NonNull String customID).
      Calling this will not submit anything to MagnesSDK as we need the pairing ID to be the ec-token.
     
      """
    )
    fun generatePairingIdAndNotifyDyson(customParameters: Map<String?, String?>?) {
        //requireNonEmptyMap(customParameters);
        generateNewPairingId("null", customParameters)
    }

    /**
     * Generates a new pairingID for dyson using the given customID and asynchronously uploads the payload to dyson server.
     *
     * @param customId         customID passed to RiskComponent to generate a new PairingId
     * For e.g. When app generates a paycode for a transaction that will be carried out in a web view(on merchant's site), client first generates the paycode and calls [.generatePairingIdAndNotifyDyson] and then pass the paycode to merchant site.
     * This allows risk to perform proper validation when merchant site tries use the paycode for a payment.
     * @param customParameters custom parameters to send with the payload
     */
    fun generatePairingIdAndNotifyDyson(
        customId: String,
        customParameters: Map<String?, String?>?
    ) {
        //requireNonEmptyString(customId);
        //requireNonEmptyMap(customParameters);
        generateNewPairingId(customId, customParameters)
    }

    private fun generateNewPairingId(customId: String, customParameters: Map<String?, String?>?) {
        //requireAny(customId);
        //requireAny(customParameters);
        var customParameters = customParameters
        if (customParameters == null) {
            customParameters = HashMap<String?, String?>()
        }
        //addGcmTokenToParamsIfAvailable(customParameters);
        if (customId != null && customId != previousPairingId) {
            try {
                magnesResult = MagnesSDK.getInstance().collectAndSubmit(
                    context,
                    customId,
                    customParameters as HashMap<String?, String?>?
                )
                previousPairingId = customId
            } catch (exception: InvalidInputException) {

            }
        }
    } /*
    private void addGcmTokenToParamsIfAvailable(Map<String, Object>  params) {
        requireNonNull(params);
        if (!TextUtils.isEmpty(DeviceState.getInstance().getGcmToken())) {
            params.put(RiskComponent.RISK_MANAGER_NOTIF_TOKEN, DeviceState.getInstance().getGcmToken());
        }
    }
    */

    companion object {
        private val TAG = FoundationRiskConfig::class.java.simpleName
    }

    init {
        Objects.requireNonNull(context)
        this.context = context
        sourceApp = MagnesSource.PAYPAL
        riskInitialized = false
        appVersion = "v1.0NativeXO"
        initRisk()
    }
}