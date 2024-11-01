package com.example.recipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.recipe.databinding.ActivityViewRecipeBinding

class ViewRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRecipeBinding  // View binding for layout access
    private lateinit var dbHelper: DatabaseHelper  // Database helper instance
    private var recipeId: Int = 0  // ID of the recipe to view
    private var recipe: Recipe? = null  // Recipe object to display
    private var isFavorite: Boolean = false  // Track favorite status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        recipeId = intent.getIntExtra("RECIPE_ID", 0)

        loadRecipeData()

        binding.btnAddToFavorites.setOnClickListener { toggleFavorite() }
        binding.btnAddToShoppingList.setOnClickListener { addIngredientsToShoppingList() }
        binding.btnEditRecipe.setOnClickListener {
            val intent = Intent(this, EditRecipeActivity::class.java)
            intent.putExtra("RECIPE_ID", recipeId)
            startActivity(intent)
        }
    }

    private fun loadRecipeData() {
        recipe = dbHelper.getRecipeById(recipeId)
        recipe?.let {
            binding.tvRecipeName.text = it.name
            binding.tvIngredients.text = it.ingredients
            binding.tvNotes.text = it.notes
            isFavorite = it.isFavorite

            if (!it.imageUri.isNullOrEmpty()) {
                val uri = it.imageUri
                try {
                    // Use Glide to load the image from the URI or URL
                    Glide.with(this)
                        .load(uri) // Glide can handle both local URIs and remote URLs
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image) // Fallback if the image fails to load
                        .into(binding.ivRecipeImage)
                } catch (e: Exception) {
                    Log.e("ViewRecipeActivity", "Error loading image with Glide: ${e.message}")
                    binding.ivRecipeImage.setImageResource(R.drawable.ic_no_image)
                }
            } else {
                binding.ivRecipeImage.setImageResource(R.drawable.ic_no_image)
            }

            updateFavoriteIcon()
        } ?: run {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        dbHelper.updateRecipeFavoriteStatus(recipeId, isFavorite)
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        binding.btnAddToFavorites.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    private fun addIngredientsToShoppingList() {
        recipe?.ingredients?.split(",")?.map { it.trim() }?.forEach {
            dbHelper.insertShoppingListItem(it)
        }
        Toast.makeText(this, "Ingredients added to shopping list", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        loadRecipeData()  // Refresh the data when returning to this activity
    }
}