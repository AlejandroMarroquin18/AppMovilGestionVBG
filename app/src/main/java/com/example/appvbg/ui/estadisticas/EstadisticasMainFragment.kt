package com.example.appvbg.ui.estadisticas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentEstadisticasMainBinding

class EstadisticasMainFragment : Fragment() {

    private var _binding: FragmentEstadisticasMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstadisticasMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnEstadisticasQuejas.setOnClickListener {
            val action = EstadisticasMainFragmentDirections.actionEstadisticasMainToEstadisticasQuejas()
            findNavController().navigate(action)
        }

        binding.btnEstadisticasAgenda.setOnClickListener {
            val action = EstadisticasMainFragmentDirections.actionEstadisticasMainToEstadisticasAgenda()
            findNavController().navigate(action)
        }

        binding.btnEstadisticasTalleres.setOnClickListener {
            val action = EstadisticasMainFragmentDirections.actionEstadisticasMainToEstadisticasTalleres()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}