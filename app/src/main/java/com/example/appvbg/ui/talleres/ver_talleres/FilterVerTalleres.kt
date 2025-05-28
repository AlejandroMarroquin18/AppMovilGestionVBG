package com.example.appvbg.ui.quejas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appvbg.R
import com.example.appvbg.ui.talleres.ver_talleres.FiltroData
import com.example.appvbg.ui.talleres.ver_talleres.VerTalleresViewModel
import android.widget.AdapterView
import android.text.TextWatcher
import android.text.Editable

class FilterVerTalleresFragment : Fragment(R.layout.fragment_filter_ver_talleres) {

    private lateinit var viewModel: VerTalleresViewModel
    private lateinit var fechaSpinner: Spinner
    private lateinit var modalidadSpinner: Spinner
    private lateinit var estadoSpinner: Spinner
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireParentFragment())[VerTalleresViewModel::class.java]


        // Inicializar vistas usando el "view"
        fechaSpinner = view.findViewById(R.id.fechaTallerSpinner)
        modalidadSpinner = view.findViewById(R.id.modalidadTallerSpinner)
        estadoSpinner = view.findViewById(R.id.estadoTallerSpinner)
        searchEditText = view.findViewById(R.id.searchTallerText)
        searchButton = view.findViewById(R.id.searchTallerButton)

        // Configurar los spinners
        val fechas = listOf("Todas")
        val modalidades = listOf("Todas","Presencial", "Virtual")
        val estados = listOf("Todos","Pendiente", "Realizado")



        fechaSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fechas)
        modalidadSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            modalidades
        )
        estadoSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, estados)

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        fechaSpinner.onItemSelectedListener = spinnerListener
        modalidadSpinner.onItemSelectedListener = spinnerListener
        estadoSpinner.onItemSelectedListener = spinnerListener

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                applyFilters()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        searchButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            val fecha = fechaSpinner.selectedItem.toString()
            val modalidad = modalidadSpinner.selectedItem.toString()
            val estado = estadoSpinner.selectedItem.toString()

            Toast.makeText(requireContext(), "Buscando: $searchQuery, $fecha, $modalidad, $estado", Toast.LENGTH_SHORT).show()
        }


        // En el botón de búsqueda
        searchButton.setOnClickListener {
            applyFilters()
            Toast.makeText(requireContext(), "Filtrando...", Toast.LENGTH_SHORT).show()
        }
    }


        private fun applyFilters() {
            val filtros = FiltroData(
                nombre = searchEditText.text.toString(),
                fechaInicio = fechaSpinner.selectedItem.toString(),
                modalidad = modalidadSpinner.selectedItem.toString(),
                estado = estadoSpinner.selectedItem.toString()
            )
            viewModel.actualizarFiltros(filtros)
        }

}
