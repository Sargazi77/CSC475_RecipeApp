package com.example.recipe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipe.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding  // Binding to access layout views
    private lateinit var databaseHelper: DatabaseHelper  // Database helper instance
    private lateinit var recipeAdapter: RecipeAdapter  // Adapter to handle recipe list
    private var recipes: MutableList<Recipe> = mutableListOf()  // All recipes from the database
    private var filteredRecipes: MutableList<Recipe> = mutableListOf()  // Filtered recipe list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        // Load all recipes from the database
        recipes = databaseHelper.getAllRecipes().toMutableList()
        filteredRecipes.addAll(recipes)

        // Setup RecyclerView with the adapter
        recipeAdapter = RecipeAdapter(
            context = this,
            recipes = filteredRecipes,
            onRecipeClick = { recipe ->  // Handle recipe item click
                val intent = Intent(this, ViewRecipeActivity::class.java).apply {
                    putExtra("RECIPE_ID", recipe.id)  // Pass the recipe ID to view activity
                }
                startActivity(intent)
            },
            onEditClick = { recipe ->  // Handle edit button click
                val intent = Intent(this, EditRecipeActivity::class.java).apply {
                    putExtra("RECIPE_ID", recipe.id)  // Pass the recipe ID to edit activity
                }
                startActivity(intent)
            },
            onDeleteClick = { recipe ->  // Handle delete button click
                databaseHelper.deleteRecipe(recipe.id)
                refreshRecipeList()  // Refresh the list after deletion
            },
            onFavoriteClick = { recipe ->  // Handle favorite toggle click
                recipe.isFavorite = !recipe.isFavorite
                databaseHelper.updateRecipeFavoriteStatus(recipe.id, recipe.isFavorite)
                refreshRecipeList()
            }
        )

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recipeRecyclerView.adapter = recipeAdapter

        // Floating action button to add a new recipe
        binding.fabAddRecipe.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivity(intent)
        }

        // Search view to filter recipes by name
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterRecipes(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText.orEmpty())
                return true
            }
        })

        // Handle bottom navigation clicks
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_recipes -> true
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                R.id.nav_shopping_list -> {
                    startActivity(Intent(this, ShoppingListActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Filter recipes based on the search query
    private fun filterRecipes(query: String) {
        filteredRecipes.clear()
        if (query.isEmpty()) {
            filteredRecipes.addAll(recipes)
        } else {
            filteredRecipes.addAll(recipes.filter {
                it.name.contains(query, ignoreCase = true)
            })
        }
        recipeAdapter.notifyDataSetChanged()
    }

    // Refresh the recipe list from the database
    private fun refreshRecipeList() {
        recipes.clear()
        recipes.addAll(databaseHelper.getAllRecipes())
        filteredRecipes.clear()
        filteredRecipes.addAll(recipes)
        recipeAdapter.notifyDataSetChanged()
    }

    // Refresh the list when the activity resumes
    override fun onResume() {
        super.onResume()
        refreshRecipeList()
    }
}