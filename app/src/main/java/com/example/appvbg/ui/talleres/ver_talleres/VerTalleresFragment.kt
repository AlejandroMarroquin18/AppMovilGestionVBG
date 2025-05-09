package com.example.appvbg.ui.talleres.ver_talleres

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appvbg.R
import com.example.appvbg.ui.quejas.FilterVerTalleresFragment
import org.json.JSONObject

class VerTalleresFragment: Fragment(R.layout.fragment_vertalleres) {
    private lateinit var viewModel: VerTalleresViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val items = mutableListOf<ItemTalleres>() // Replace with your data source

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
        //items.addAll(generateDummyItems())

        adapter = ItemAdapter(items,
            onDetailsClicked = { item ->
                // Handle details button click, e.g., navigate to details fragment
                // You'll need to create a details fragment and implement navigation
                val itemJson = item.json.toString()


                // Dentro de QuejasFragment, cuando hagas clic en un ítem o algo que navegue a DetallesQuejaFragment
                val action = VerTalleresFragmentDirections.actionVerTalleresToDetallesTaller(itemJson)
                //val action = VerTalleresFragmentDirections.actionVertalleresFragmentToDetallesTallerFragment(itemJson)

                findNavController().navigate(action)
            },
            onDeleteClicked = { item ->
                // Handle delete button click
                viewModel.removeItem(item) // Or use a more efficient way to update the adapter
            }
        )
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[VerTalleresViewModel::class.java]

        viewModel.items.observe(viewLifecycleOwner) { itemList ->
            adapter.updateItems(itemList)
        }

        

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Log.e("QuejaViewModel", it)
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun aplicarFiltros(filtros: com.example.appvbg.ui.talleres.ver_talleres.FiltroData) {}

    data class ItemTalleres(
                    val id:Int?,
                    val nombre: String,
                    val fechaInicio: String,
                    val horaInicio: String,
                    val horaFin: String,
                    val ubicacion: String,
                    val modalidad: String,
                    val beneficiarios: String,
                    val talleristas: String,
                    val descripcion: String,
                    val estado: String,
                    val json: JSONObject?
    )

    private class ItemAdapter(
        private val items: MutableList<ItemTalleres>,
        private val onDetailsClicked: (ItemTalleres) -> Unit,
        private val onDeleteClicked: (ItemTalleres) -> Unit
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

        fun updateItems(newItems: List<ItemTalleres>) {

            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
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


            fun bind(item: ItemTalleres, onDetailsClicked: (ItemTalleres) -> Unit, onDeleteClicked: (ItemTalleres) -> Unit) {
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