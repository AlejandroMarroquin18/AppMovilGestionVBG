package com.example.appvbg.ui.talleres.ver_talleres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appvbg.R
import com.example.appvbg.ui.quejas.FilterVerTalleresFragment

class VerTalleresFragment: Fragment(R.layout.fragment_vertalleres) {
    private lateinit var viewModel: VerTalleresViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val items = mutableListOf<Item>() // Replace with your data source

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vertalleres, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.filter_container, FilterVerTalleresFragment())
            .commit()


        recyclerView = view.findViewById(R.id.recyclerTallerView) // Assuming you have a RecyclerView with this ID in your layout
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize your data (replace with your actual data loading)
        items.addAll(generateDummyItems())

        adapter = ItemAdapter(items,
            onDetailsClicked = { item ->
                // Handle details button click, e.g., navigate to details fragment
                // You'll need to create a details fragment and implement navigation
            },
            onDeleteClicked = { item ->
                // Handle delete button click
                items.remove(item)
                adapter.notifyDataSetChanged() // Or use a more efficient way to update the adapter
            }
        )
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[VerTalleresViewModel::class.java]

        viewModel.filtros.observe(viewLifecycleOwner) { filtros ->
            // Por ejemplo, actualizar RecyclerView
            aplicarFiltros(filtros)
        }

    }
    private fun aplicarFiltros(filtros: com.example.appvbg.ui.talleres.ver_talleres.FiltroData) {}


    private fun generateDummyItems(): List<Item> {
        // Replace this with your actual data loading logic
        return (1..10).map {
            Item(
                nombre = "$it",
                fechaInicio = "$it",
                horaInicio = "$it",
                horaFin = "$it",
                ubicacion = "$it",
                modalidad = "$it",
                beneficiarios = "$it",
                talleristas = "$it",
                descripcion = "$it",
                estado = "$it"
            )
        }
    }


    data class Item(val nombre: String, val fechaInicio: String, val horaInicio: String, val horaFin: String, val ubicacion: String,
                    val modalidad: String, val beneficiarios: String, val talleristas: String, val descripcion: String, val estado: String)


    private class ItemAdapter(
        private val items: List<Item>,
        private val onDetailsClicked: (Item) -> Unit,
        private val onDeleteClicked: (Item) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.taller_item_layout, parent, false) // Create an item layout (item_layout.xml)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = items[position]
            holder.bind(item, onDetailsClicked, onDeleteClicked)
        }

        override fun getItemCount(): Int = items.size

        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nombreTallerTextView: TextView = itemView.findViewById(R.id.nombreTallerTextView)
            private val fechaTallerTextView: TextView = itemView.findViewById(R.id.fechaTallerTextView)
            private val horaInicioTallerTextView: TextView = itemView.findViewById(R.id.horaInicioTallerTextView)
            private val horaFinTallerTextView: TextView = itemView.findViewById(R.id.horaFinTallerTextView)
            private val ubicacionTallerTextView: TextView = itemView.findViewById(R.id.ubicacionTallerTextView)
            private val modalidadTallerTextView: TextView = itemView.findViewById(R.id.modalidadTallerTextView)
            private val beneficiariosTallerTextView: TextView = itemView.findViewById(R.id.beneficiariosTallerTextView)
            private val talleristasTallerTextView: TextView = itemView.findViewById(R.id.talleristasTallerTextView)
            private val descripcionTallerTextView: TextView = itemView.findViewById(R.id.descripcionTallerTextView)
            private val estadoTallerTextView: TextView = itemView.findViewById(R.id.estadoTallerTextView)
            private val detailsButton: Button = itemView.findViewById(R.id.detallesTallerTextView)
            private val deleteButton: Button = itemView.findViewById(R.id.eliminarTallerTextView)


            fun bind(item: Item, onDetailsClicked: (Item) -> Unit, onDeleteClicked: (Item) -> Unit) {
                nombreTallerTextView.text= item.nombre
                fechaTallerTextView.text = "Fecha: ${item.fechaInicio}"
                horaInicioTallerTextView.text = "Hora Inicio: ${item.horaInicio}"
                horaFinTallerTextView.text = "Hora Fin: ${item.horaFin}"
                ubicacionTallerTextView.text = "Ubicación: ${item.ubicacion}"
                modalidadTallerTextView.text = "Modalidad: ${item.modalidad}"
                beneficiariosTallerTextView.text = "Beneficiarios: ${item.beneficiarios}"
                talleristasTallerTextView.text = "Talleristas: ${item.talleristas}"
                descripcionTallerTextView.text = "Descripción: ${item.descripcion}"
                estadoTallerTextView.text = "Estado: ${item.estado}"

                detailsButton.setOnClickListener { onDetailsClicked(item) }
                deleteButton.setOnClickListener { onDeleteClicked(item) }
            }
        }
    }


}