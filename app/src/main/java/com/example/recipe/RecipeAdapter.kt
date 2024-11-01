package com.example.recipe

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipe.databinding.ItemRecipeBinding

class RecipeAdapter(
    private val context: Context,
    private var recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit,
    private val onEditClick: (Recipe) -> Unit,
    private val onDeleteClick: (Recipe) -> Unit,
    private val onFavoriteClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.tvRecipeName.text = recipe.name

            try {
                Glide.with(context)
                    .load(Uri.parse(recipe.imageUri))
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(binding.ivRecipeImage)
            } catch (e: Exception) {
                Log.e("RecipeAdapter", "Error loading image URI: ${recipe.imageUri}", e)
                binding.ivRecipeImage.setImageResource(R.drawable.ic_no_image)
            }

            // Update heart icon based on favorite status
            val favoriteIcon = if (recipe.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            binding.btnFavorite.setImageResource(favoriteIcon)

            // Set click listeners
            binding.root.setOnClickListener { onRecipeClick(recipe) }
            binding.btnEdit.setOnClickListener { onEditClick(recipe) }
            binding.btnDelete.setOnClickListener { onDeleteClick(recipe) }

            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun updateList(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}