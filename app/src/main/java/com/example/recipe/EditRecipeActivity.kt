package com.example.recipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipe.databinding.ActivityEditRecipeBinding

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditRecipeBinding  // View binding for layout access
    private lateinit var databaseHelper: DatabaseHelper  // Helper for interacting with the database
    private var recipeId: Int = -1  // ID of the recipe being edited
    private var imageUri: Uri? = null  // URI for the selected recipe image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        recipeId = intent.getIntExtra("RECIPE_ID", -1)
        loadRecipeDetails()

        binding.ivRecipeImage.setOnClickListener { openImagePicker() }
        binding.btnSave.setOnClickListener { saveRecipeChanges() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"  // Only allow images to be selected
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data  // Store the selected image URI
            imageUri?.let { uri ->
                try {
                    // Request persistable permission to use the URI across sessions
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    binding.ivRecipeImage.setImageURI(uri)  // Display the selected image
                } catch (e: SecurityException) {
                    Toast.makeText(this, "Permission denied for this image", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadRecipeDetails() {
        val recipe = databaseHelper.getRecipeById(recipeId)
        recipe?.let {
            binding.etRecipeName.setText(it.name)
            binding.etIngredients.setText(it.ingredients)
            binding.etNotes.setText(it.notes)

            if (!it.imageUri.isNullOrEmpty()) {
                val uri = Uri.parse(it.imageUri)
                try {
                    // Attempt to display the image URI
                    binding.ivRecipeImage.setImageURI(uri)
                } catch (e: SecurityException) {
                    // Handle if URI permission is denied
                    Toast.makeText(this, "Image access permission denied", Toast.LENGTH_SHORT).show()
                    binding.ivRecipeImage.setImageResource(R.drawable.ic_no_image)
                }
            } else {
                binding.ivRecipeImage.setImageResource(R.drawable.ic_no_image)
            }
        } ?: run {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveRecipeChanges() {
        val name = binding.etRecipeName.text.toString().trim()
        val ingredients = binding.etIngredients.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (name.isEmpty() || ingredients.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedRecipe = Recipe(
            id = recipeId,
            name = name,
            ingredients = ingredients,
            notes = notes,
            imageUri = imageUri?.toString() ?: "",
            isFavorite = false
        )

        if (databaseHelper.updateRecipe(updatedRecipe)) {
            Toast.makeText(this, "Recipe updated", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to update recipe", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 100  // Request code for image picker
    }
}