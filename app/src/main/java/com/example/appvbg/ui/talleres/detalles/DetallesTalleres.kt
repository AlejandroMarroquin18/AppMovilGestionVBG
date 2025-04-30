package com.example.appvbg.ui.talleres.detalles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appvbg.R

/**
 * OnViewCreated===
 *
 * val adapter = CasosAdapter(lista) { casoId ->
 *             // Cuando se pulsa el botón "Detalles"
 *             val fragment = DetallesTalleres.newInstance(casoId)
 *             parentFragmentManager.beginTransaction()
 *                 .replace(R.id.fragment_container, fragment)
 *                 .addToBackStack(null)
 *                 .commit()
 *         }
 * */
class DetallesTalleres : Fragment(R.layout.fragment_detalles_talleres) {
    private var tallerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tallerId = arguments?.getInt("TALLER_ID")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Aquí se hace el fetch al taller (?

    }
    companion object {
        fun newInstance(id: Int): DetallesTalleres {
            val fragment = DetallesTalleres()
            val args = Bundle()
            args.putInt("TALLER_ID", id)
            fragment.arguments = args
            return fragment
        }
    }


}