package com.example.nxobtintegration.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.braintreepayments.api.PayPalNativeCheckoutAccountNonce
import com.braintreepayments.api.PayPalNativeCheckoutListener
import com.example.nxobtintegration.util.CreateRequestFactory
import com.example.nxobtintegration.util.MockDataProvider
import com.example.nxobtintegration.auth.PayPalNativeCheckoutClientProvider
import com.example.nxobtintegration.auth.BTClientTokenProvider
import com.example.nxobtintegration.databinding.FragmentFirstBinding
import com.example.nxobtintegration.model.CheckoutRequest
import com.example.nxobtintegration.util.FragmentUtils
import java.lang.Exception

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), PayPalNativeCheckoutListener {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var clientProvider: PayPalNativeCheckoutClientProvider

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        clientProvider = PayPalNativeCheckoutClientProvider(requireContext(), BTClientTokenProvider())
        clientProvider.setup(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.launchCheckout.setOnClickListener {
            tokenizePayPalAccountWithCheckout()
        }

        binding.launchBa.setOnClickListener {
            tokenizePayPalAccountWithBillingAgreement()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun tokenizePayPalAccountWithCheckout() {
        // One time checkout
        FragmentUtils.showDialog(requireContext()) { amount, request ->
            // Configure other values on `request` as needed.
            activity?.let {
                clientProvider.getClient().launchNativeCheckout(it, CreateRequestFactory(request).createPayPalCheckoutRequest(amount))
            }
        }
    }

    private fun tokenizePayPalAccountWithBillingAgreement() {
        // BA
        val factory = CreateRequestFactory(
            CheckoutRequest(
                null,
                null,
                shouldOfferPPC = true,
                isAddressEditable = true
            )
        )

        // Configure other values on `request` as needed.
        activity?.let {
            clientProvider.getClient().launchNativeCheckout(it, factory.createPayPalVaultRequest())
        }
    }

    override fun onPayPalSuccess(payPalAccountNonce: PayPalNativeCheckoutAccountNonce) {
        binding.textviewFirst.text =
            "Success:" +
            "\nFirst name: ${payPalAccountNonce.firstName} " +
            "\nLast name: ${payPalAccountNonce.lastName} " +
            "\nPayer ID: ${payPalAccountNonce.payerId} " +
            "\nShipping Address: ${payPalAccountNonce.shippingAddress} " +
            "\nClient Metadata ID: ${payPalAccountNonce.clientMetadataId} " +
            "\nEmail: ${payPalAccountNonce.email} "
    }

    override fun onPayPalFailure(error: Exception) {
        binding.textviewFirst.text = "${error.message}"
    }
}