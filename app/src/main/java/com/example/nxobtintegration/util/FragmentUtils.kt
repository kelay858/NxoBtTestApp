package com.example.nxobtintegration.util

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.marginLeft
import com.example.nxobtintegration.R
import com.example.nxobtintegration.model.CheckoutRequest
import com.example.nxobtintegration.model.PayMode

object FragmentUtils {

    fun showDialog(
        context: Context,
        onComplete: (amount: String, request: CheckoutRequest) -> Unit
    ) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.set_params)
            val linearLayout = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
            val amountEditText = EditText(context) .apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                setHint(R.string.set_amount)
            }
            val shippingEditable = CheckBox(context).apply {
                setText(R.string.set_address_editable)
                visibility = View.GONE

            }
            val shippingOverride = CheckBox(context).apply {
                setText(R.string.set_address_override)
                setOnCheckedChangeListener { _, checked ->
                    shippingEditable.visibility = if (checked) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
            val spinnerText = TextView(context).apply {
                setText(R.string.pay_mode)
            }
            val spinner = Spinner(context).apply {
                adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, PayMode.values().toList())
            }

            val spinnerLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(spinnerText)
                addView(spinner)
            }
            linearLayout.apply {
                addView(amountEditText)
                addView(shippingOverride)
                addView(shippingEditable)
                addView(spinnerLayout)
            }
            setView(linearLayout)
            setPositiveButton(android.R.string.ok) { _, _ ->
                amountEditText.text.toString()
                val request = if (shippingOverride.isChecked) {
                    CheckoutRequest(
                        MockDataProvider.createAddress(),
                        null,
                        shouldOfferPPC = false,
                        isAddressEditable = shippingEditable.isChecked,
                        intentType = "",
                        payMode = PayMode.valueOf(spinner.selectedItem.toString())
                    )
                } else {
                    CheckoutRequest(
                        null,
                        null,
                        shouldOfferPPC = false,
                        isAddressEditable = false,
                        intentType = "",
                        payMode = PayMode.valueOf(spinner.selectedItem.toString())
                    )
                }
                onComplete(amountEditText.text.toString(), request)
            }
            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }
}