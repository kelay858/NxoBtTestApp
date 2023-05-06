package com.example.nxobtintegration.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nxobtintegration.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.launchBa.setOnClickListener {
//            var token: String = "EC-4ME94791UM2024107"
//            Auth(requireContext(), token).invoke()
            val token = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getString("auth", "EMPTY")

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra("auth", token)
            intent.data =
                Uri.parse("app_name_lower_case://reminder")

            requireContext().startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}