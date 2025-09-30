package com.example.appvbg.ui.quejas

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button // Cambiado de ImageButton a Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appvbg.R
import android.text.TextWatcher

class FilterQuejasFragment : Fragment(R.layout.fragment_filter_quejas) {

    private lateinit var viewModel: QuejaViewModel
    private lateinit var sedeSpinner: Spinner
    private lateinit var tipoSpinner: Spinner
    private lateinit var facultadSpinner: Spinner
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button // Cambiado de ImageButton a Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireParentFragment())[QuejaViewModel::class.java]

        sedeSpinner = view.findViewById(R.id.sedeQuejaSpinner)
        tipoSpinner = view.findViewById(R.id.tipoQuejaSpinner)
        facultadSpinner = view.findViewById(R.id.facultadQuejaSpinner)
        searchEditText = view.findViewById(R.id.searchQuejaText)
        searchButton = view.findViewById(R.id.searchQuejaButton) // Ahora esto es un Button

        val sedes = listOf("Todos","Melendez","San Fernando","Santander de Quilichao","Buenaventura","Buga","Zarzal","Otra")
        val tipos = listOf("Todos", "Acompañamiento integral", "Acompañamiento psicológico")
        val facultades = listOf("Todos","Artes Integradas","Ciencias Naturales y Exactas","Ciencias de la Administración",
            "Salud","Ciencias Sociales y Económicas","Humanidades","Ingeniería","Educacion y Pedagogía","Psicología")

        sedeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sedes)
        tipoSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tipos)
        facultadSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, facultades)

        // Un único listener para todos los spinners:
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }
        sedeSpinner.onItemSelectedListener = spinnerListener
        tipoSpinner.onItemSelectedListener = spinnerListener
        facultadSpinner.onItemSelectedListener = spinnerListener

        // Además, si quieres filtrar sobre la marcha al escribir:
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { applyFilters() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        // (Opcional) mantener el botón de búsqueda para el usuario:
        searchButton.setOnClickListener {
            applyFilters()
            Toast.makeText(requireContext(), "Filtrando...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFilters() {
        val filtros = FiltroData(
            id = 0, // si no lo usas puedes ignorar este campo
            codigo = searchEditText.text.toString(),
            sede = sedeSpinner.selectedItem.toString(),
            tipo = tipoSpinner.selectedItem.toString(),
            facultad = facultadSpinner.selectedItem.toString()
        )
        viewModel.actualizarFiltros(filtros)
    }
}