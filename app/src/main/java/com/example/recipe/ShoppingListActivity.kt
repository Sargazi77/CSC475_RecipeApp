package com.example.recipe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipe.databinding.ActivityShoppingListBinding

class ShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var shoppingListAdapter: ShoppingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setupRecyclerView()

        binding.btnAddItem.setOnClickListener {
            val newItem = binding.etNewItem.text.toString().trim()
            if (newItem.isNotEmpty()) {
                dbHelper.insertShoppingListItem(newItem)
                binding.etNewItem.text.clear()
                loadShoppingListItems()
            } else {
                Toast.makeText(this, "Please enter an item", Toast.LENGTH_SHORT).show()
            }
        }

        loadShoppingListItems()
    }

    private fun setupRecyclerView() {
        shoppingListAdapter = ShoppingListAdapter(mutableListOf()) { item ->
            dbHelper.deleteShoppingListItem(item)
            loadShoppingListItems()
        }
        binding.shoppingListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.shoppingListRecyclerView.adapter = shoppingListAdapter
    }

    private fun loadShoppingListItems() {
        val items = dbHelper.getShoppingListItems()
        shoppingListAdapter.updateList(items)
    }
}