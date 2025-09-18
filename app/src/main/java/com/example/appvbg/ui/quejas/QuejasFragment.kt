package com.example.appvbg.ui.quejas

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class QuejasFragment : Fragment(R.layout.fragment_quejas) {
    private lateinit var viewModel: QuejaViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var tvTotalQuejas: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quejas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Inicializar vistas
            tvTotalQuejas = view.findViewById(R.id.tvTotalQuejas)
            recyclerView = view.findViewById(R.id.recyclerView)

            // Configurar RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Inicializar adapter
            adapter = ItemAdapter(
                mutableListOf(),
                onDetailsClicked = { item ->
                    try {
                        val itemJson = item.json?.toString() ?: "{}"
                        val action = QuejasFragmentDirections.actionQuejasFragmentToDetallesQueja(itemJson)
                        findNavController().navigate(action)
                    } catch (e: Exception) {
                        Log.e("QuejasFragment", "Error al navegar a detalles", e)
                        Toast.makeText(requireContext(), "Error al abrir detalles", Toast.LENGTH_SHORT).show()
                    }
                },
                onDeleteClicked = { item ->
                    viewModel.removeItem(item)
                }
            )
            recyclerView.adapter = adapter

            // Configurar ViewModel
            viewModel = ViewModelProvider(this)[QuejaViewModel::class.java]

            // Observadores
            viewModel.items.observe(viewLifecycleOwner) { itemList ->
                adapter.updateItems(itemList)
                tvTotalQuejas.text = "Total: ${itemList.size} quejas"
            }

            viewModel.filtros.observe(viewLifecycleOwner) { filtro ->
                adapter.updateItems(viewModel.items.value ?: emptyList())
            }

            viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
                errorMsg?.let {
                    Log.e("QuejaViewModel", it)
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                }
            }

            // Configurar FAB
            val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
            fab?.setOnClickListener {
                try {
                    val actionCrearQueja = QuejasFragmentDirections.actionQuejasFragmentToCrearQueja()
                    findNavController().navigate(actionCrearQueja)
                } catch (e: Exception) {
                    Log.e("QuejasFragment", "Error al navegar a crear queja", e)
                    Toast.makeText(requireContext(), "Error al crear queja", Toast.LENGTH_SHORT).show()
                }
            }

            // Configurar fragmento de filtros
            if (childFragmentManager.findFragmentById(R.id.filter_container) == null) {
                try {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.filter_container, FilterQuejasFragment())
                        .commit()
                } catch (e: Exception) {
                    Log.e("QuejasFragment", "Error al cargar filtros", e)
                }
            }

        } catch (e: Exception) {
            Log.e("QuejasFragment", "Error en onViewCreated", e)
            Toast.makeText(requireContext(), "Error al cargar la vista", Toast.LENGTH_LONG).show()
        }
    }

    data class Item(
        val id: Int,
        val nombre: String,
        val sede: String,
        val codigo: String,
        val tipo_de_acompanamiento: String,
        val fecha: String,
        val estado: String,
        val detalles: String,
        val facultad: String?,
        val unidad: String?,
        val json: JSONObject?
    )

    private class ItemAdapter(
        private var items: MutableList<Item>,
        private val onDetailsClicked: (Item) -> Unit,
        private val onDeleteClicked: (Item) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.queja_item_layout, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = items.getOrNull(position)
            item?.let {
                holder.bind(it, onDetailsClicked, onDeleteClicked)
            }
        }

        fun updateItems(newItems: List<Item>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = items.size

        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
            private val idTextView: TextView = itemView.findViewById(R.id.idTextView)
            private val codeTextView: TextView = itemView.findViewById(R.id.codeTextView)
            private val facultadTextView: TextView = itemView.findViewById(R.id.facultadTextView)
            private val sedeTextView: TextView = itemView.findViewById(R.id.sedeTextView)
            private val estadoTextView: TextView = itemView.findViewById(R.id.estadoTextView)
            private val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
            private val detailsButton: Button = itemView.findViewById(R.id.detailsButton)
            private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

            fun bind(item: Item, onDetailsClicked: (Item) -> Unit, onDeleteClicked: (Item) -> Unit) {
                nameTextView.text = item.nombre
                idTextView.text = "ID: ${item.id}"
                codeTextView.text = "Código: ${item.codigo}"
                facultadTextView.text = "Facultad: ${item.facultad ?: "No especificado"}"
                sedeTextView.text = "Sede: ${item.sede}"
                estadoTextView.text = item.estado
                fechaTextView.text = "Fecha: ${item.fecha}"

                detailsButton.setOnClickListener { onDetailsClicked(item) }
                deleteButton.setOnClickListener { onDeleteClicked(item) }
            }
        }
    }
}