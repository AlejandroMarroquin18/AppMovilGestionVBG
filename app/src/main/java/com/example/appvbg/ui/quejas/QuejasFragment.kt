package com.example.appvbg.ui.quejas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
//import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appvbg.R
import com.example.appvbg.ui.agenda.crear_queja.CrearQueja
import com.example.appvbg.ui.quejas.detalles.DetallesQueja
import org.json.JSONObject



import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuejasFragment : Fragment(R.layout.fragment_quejas) {
    private lateinit var viewModel: QuejaViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val items = mutableListOf<Item>() // Replace with your data source

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quejas, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (childFragmentManager.findFragmentById(R.id.filter_container) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.filter_container, FilterQuejasFragment())
                .commit()
        }
        recyclerView = view.findViewById(R.id.recyclerView) // Assuming you have a RecyclerView with this ID in your layout
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize your data (replace with your actual data loading)
        //items.addAll(generateDummyItems())

        adapter = ItemAdapter(
            mutableListOf(),
            onDetailsClicked = { item ->
                // Handle details button click, e.g., navigate to details fragment
                // You'll need to create a details fragment and implement navigation
                val itemJson = item.json.toString()


                // Dentro de QuejasFragment, cuando hagas clic en un ítem o algo que navegue a DetallesQuejaFragment
                val action = QuejasFragmentDirections.actionQuejasFragmentToDetallesQueja(itemJson)
                findNavController().navigate(action)



            },
            onDeleteClicked = { item ->
                viewModel.removeItem(item)
            }
        )
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[QuejaViewModel::class.java]

        viewModel.items.observe(viewLifecycleOwner) { itemList ->
            adapter.updateItems(itemList)
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
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        //fab?.setImageResource(R.drawable.tu_icono_deseado)
        fab?.setOnClickListener {
            // Acción específica para este fragmento
            val actionCrearQueja = QuejasFragmentDirections.actionQuejasFragmentToCrearQueja()
            findNavController().navigate(actionCrearQueja)


        }


    }
    private fun aplicarFiltros(filtros: FiltroData) {}



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
                .inflate(R.layout.queja_item_layout, parent, false) // Create an item layout (item_layout.xml)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = items[position]
            holder.bind(item, onDetailsClicked, onDeleteClicked)
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
            private val detailsButton: Button = itemView.findViewById(R.id.detailsButton)
            private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

            fun bind(item: Item, onDetailsClicked: (Item) -> Unit, onDeleteClicked: (Item) -> Unit) {
                nameTextView.text = item.nombre
                idTextView.text = "ID: ${item.id}"
                codeTextView.text = "Código: ${item.codigo}"
                facultadTextView.text = "Facultad: ${item.facultad}"
                sedeTextView.text = "Sede: ${item.sede}"

                detailsButton.setOnClickListener { onDetailsClicked(item) }
                deleteButton.setOnClickListener { onDeleteClicked(item) }
            }
        }
    }
}