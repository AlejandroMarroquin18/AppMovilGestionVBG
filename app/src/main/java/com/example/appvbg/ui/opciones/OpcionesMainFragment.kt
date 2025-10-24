package com.example.appvbg.ui.opciones

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appvbg.APIConstant
import com.example.appvbg.R
import com.example.appvbg.api.PrefsHelper
import com.example.appvbg.api.makeRequest
import com.example.appvbg.databinding.FragmentOpcionesMainBinding
import com.example.appvbg.splashactivity.SplashActivity
import com.example.appvbg.ui.quejas.detalles.DetallesQuejaDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OpcionesMainFragment : Fragment() {

    private var _binding: FragmentOpcionesMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpcionesMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {

        binding.cerrarSesionButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try{/*
                    val resp = makeRequest(
                        "${APIConstant.BACKEND_URL}api/logout/",
                        "POST",
                        PrefsHelper.getDRFToken(requireContext())?:""
                    )*/
                    val resp="sas"
                    withContext(Dispatchers.Main) {
                        if (resp == "error") {
                            Toast.makeText(requireContext(), "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            val sharedPref = requireContext().getSharedPreferences("AppPrefs", MODE_PRIVATE)
                            sharedPref.edit().clear().apply()
                            // Redirigir al SplashActivity
                            val intent = Intent(requireContext(), SplashActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }

                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al cerrar sesion", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Borrar todos los datos guardados en AppPrefs


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}