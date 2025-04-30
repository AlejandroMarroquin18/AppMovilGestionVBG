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

class FilterQuejasFragment : Fragment(R.layout.fragment_filter_quejas) {

    private lateinit var viewModel: QuejaViewModel
    private lateinit var sedeSpinner: Spinner
    private lateinit var tipoSpinner: Spinner
    private lateinit var facultadSpinner: Spinner
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_filter_quejas, container, false)

        // Inicializar vistas usando el "view"
        sedeSpinner = view.findViewById(R.id.sedeQuejaSpinner)
        tipoSpinner = view.findViewById(R.id.tipoQuejaSpinner)
        facultadSpinner = view.findViewById(R.id.facultadQuejaSpinner)
        searchEditText = view.findViewById(R.id.searchQuejaText)
        searchButton = view.findViewById(R.id.searchQuejaButton)

        // Configurar los spinners
        val sedes = listOf("Todos","Melendez","San Fernando","Santander de Quilichao","Buenaventura","Buga","Zarzal","Otra")
        val tipos = listOf("Todos", "Acompañamiento integral", "Acompañamiento psicológico")
        val facultades = listOf("Todos","Artes Integradas","Ciencias Naturales y Exactas","Ciencias de la Administración",
            "Salud","Ciencias Sociales y Económicas","Humanidades","Ingeniería","Educacion y Pedagogía","Psicología")

        sedeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sedes)
        tipoSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tipos)
        facultadSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, facultades)

        // Acción del botón de búsqueda
        searchButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            val sede = sedeSpinner.selectedItem.toString()
            val tipo = tipoSpinner.selectedItem.toString()
            val facultad = facultadSpinner.selectedItem.toString()

            Toast.makeText(requireContext(), "Buscando: $searchQuery, $sede, $tipo, $facultad", Toast.LENGTH_SHORT).show()
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireParentFragment())[QuejaViewModel::class.java]

        // En el botón de búsqueda
        searchButton.setOnClickListener {
            val filtros = FiltroData(
                codigo = searchEditText.text.toString(),
                sede = sedeSpinner.selectedItem.toString(),
                tipo = tipoSpinner.selectedItem.toString(),
                facultad = facultadSpinner.selectedItem.toString(),
                id = 2
            )


            viewModel.actualizarFiltros(filtros)
        }
    }


}
