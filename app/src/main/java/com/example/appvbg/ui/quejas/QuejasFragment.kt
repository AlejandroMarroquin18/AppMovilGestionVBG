package com.example.appvbg.ui.quejas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
//import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appvbg.R

class QuejasFragment : Fragment(R.layout.fragment_quejas) {

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

        recyclerView = view.findViewById(R.id.recyclerView) // Assuming you have a RecyclerView with this ID in your layout
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
    }


    private fun generateDummyItems(): List<Item> {
        // Replace this with your actual data loading logic
        return (1..10).map {
            Item(
                id = it,
                name = "Item $it",
                code = "$it",
                facultad = " $it",
                sede = "Sede $it"
            )
        }
    }


    data class Item(val id: Int, val name: String, val code: String, val facultad: String, val sede: String)


    private class ItemAdapter(
        private val items: List<Item>,
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
                nameTextView.text = item.name
                idTextView.text = "ID: ${item.id}"
                codeTextView.text = "Code: ${item.code}"
                facultadTextView.text = "School: ${item.facultad}"
                sedeTextView.text = "Sede: ${item.sede}"

                detailsButton.setOnClickListener { onDetailsClicked(item) }
                deleteButton.setOnClickListener { onDeleteClicked(item) }
            }
        }
    }
}