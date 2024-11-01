// FavoritesActivity.kt
package com.example.recipe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipe.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        recipeAdapter = RecipeAdapter(
            this,
            databaseHelper.getFavoriteRecipes(),
            onRecipeClick = { recipe -> /* Handle click */ },
            onEditClick = { recipe -> /* Handle edit */ },
            onDeleteClick = { recipe ->
                databaseHelper.deleteRecipe(recipe.id)
                loadFavoriteRecipes()
            },
            onFavoriteClick = { recipe ->
                val isFavorite = !recipe.isFavorite
                recipe.isFavorite = isFavorite
                databaseHelper.updateRecipeFavoriteStatus(recipe.id, isFavorite)
                loadFavoriteRecipes()
            }
        )

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoritesRecyclerView.adapter = recipeAdapter

        loadFavoriteRecipes()
    }

    private fun loadFavoriteRecipes() {
        val favorites = databaseHelper.getFavoriteRecipes()
        recipeAdapter.updateList(favorites)
    }
}