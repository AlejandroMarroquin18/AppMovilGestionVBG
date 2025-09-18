package com.example.appvbg.ui.talleres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentTalleresMainBinding

class TalleresMainFragment : Fragment() {

    private var _binding: FragmentTalleresMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTalleresMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnVerTalleres.setOnClickListener {
            // Navegar a la lista de talleres
            findNavController().navigate(R.id.verTalleresFragment)
        }

        binding.btnCrearTaller.setOnClickListener {
            // Navegar a crear nuevo taller
            findNavController().navigate(R.id.crearTallerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}