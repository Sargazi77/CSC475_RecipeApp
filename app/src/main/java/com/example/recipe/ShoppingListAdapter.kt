package com.example.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView

class ShoppingListAdapter(
    private var items: MutableList<String>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    inner class ShoppingListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItem: TextView = view.findViewById(R.id.tvItem)
        val btnDelete: AppCompatImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val item = items[position]
        holder.tvItem.text = item

        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
            removeItem(position)
        }
    }

    // Method to update the list of items
    fun updateList(newItems: List<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Helper method to remove an item from the list
    private fun removeItem(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }
}