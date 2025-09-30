package com.example.appvbg.ui.talleres.ver_talleres

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var tvTotalTalleres: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vertalleres, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTotalTalleres = view.findViewById(R.id.tvTotalTalleres)

        childFragmentManager.beginTransaction()
            .replace(R.id.filter_container, FilterVerTalleresFragment())
            .commit()

        recyclerView = view.findViewById(R.id.recyclerTallerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ItemAdapter(mutableListOf(),
            onDetailsClicked = { item ->
                val itemJson = item.json.toString()
                val action = VerTalleresFragmentDirections.actionVerTalleresToDetallesTaller(itemJson)
                findNavController().navigate(action)
            },
            onDeleteClicked = { item ->
                viewModel.removeItem(item)
            }
        )
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[VerTalleresViewModel::class.java]

        viewModel.filtros.observe(viewLifecycleOwner) { filtro ->
            adapter.updateItems(viewModel.items.value ?: emptyList())
        }

        viewModel.items.observe(viewLifecycleOwner) { itemList ->
            adapter.updateItems(itemList)
            tvTotalTalleres.text = "Total: ${itemList.size} talleres"
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Log.e("TallerViewModel", it)
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

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
        private var items: MutableList<ItemTalleres>,
        private val onDetailsClicked: (ItemTalleres) -> Unit,
        private val onDeleteClicked: (ItemTalleres) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.taller_item_layout, parent, false)
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
            private val estadoTallerTextView: TextView = itemView.findViewById(R.id.estadoTallerTextView)
            private val detailsButton: android.widget.Button = itemView.findViewById(R.id.detallesTallerTextView)
            private val deleteButton: android.widget.Button = itemView.findViewById(R.id.eliminarTallerTextView)

            fun bind(item: ItemTalleres, onDetailsClicked: (ItemTalleres) -> Unit, onDeleteClicked: (ItemTalleres) -> Unit) {
                nombreTallerTextView.text = item.nombre
                fechaTallerTextView.text = "📅 ${item.fechaInicio}"
                horaInicioTallerTextView.text = "⏰ Inicio: ${item.horaInicio}"
                horaFinTallerTextView.text = "⏰ Fin: ${item.horaFin}"
                ubicacionTallerTextView.text = "📍 ${item.ubicacion}"
                modalidadTallerTextView.text = "📱 ${item.modalidad}"
                estadoTallerTextView.text = item.estado

                // Personalizar color del estado
                val backgroundResource = when (item.estado.toLowerCase()) {
                    "pendiente" -> R.drawable.badge_pending
                    "realizado" -> R.drawable.badge_resolved
                    "cancelado" -> R.drawable.badge_in_progress
                    else -> R.drawable.badge_background
                }
                estadoTallerTextView.setBackgroundResource(backgroundResource)

                detailsButton.setOnClickListener { onDetailsClicked(item) }
                deleteButton.setOnClickListener { onDeleteClicked(item) }
            }
        }
    }
}