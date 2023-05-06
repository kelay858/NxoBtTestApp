package com.example.nxobtintegration.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nxobtintegration.databinding.FragmentFirstBinding
import com.example.nxobtintegration.databinding.FragmentSecondBinding
import com.example.nxobtintegration.util.Auth
import java.lang.Exception

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.textviewAuth?.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}