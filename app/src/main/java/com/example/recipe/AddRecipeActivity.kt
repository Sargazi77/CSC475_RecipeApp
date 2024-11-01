package com.example.recipe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.recipe.databinding.ActivityAddRecipeBinding

/**
 * Activity to add a new recipe. Allows the user to input recipe details,
 * select an image, and save the recipe to the database.
 */
class AddRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecipeBinding // View binding to access UI elements
    private var selectedImageUri: Uri? = null // Variable to store selected image URI
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent> // Launcher for picking an image
    private lateinit var databaseHelper: DatabaseHelper // Helper to interact with the database
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String> // Launcher for requesting permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database helper
        databaseHelper = DatabaseHelper(this)

        // Initialize the image picker launcher to handle image selection result
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                // Successfully selected an image
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    // Display the selected image in the ImageView
                    binding.ivRecipeImage.setImageURI(it)
                }
            } else {
                // Handle case when image selection was cancelled
                Toast.makeText(this, "Image selection cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize the permission request launcher for storage permissions
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed to pick an image
                pickImageFromGallery()
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        // Set a click listener to trigger permission check and image selection
        binding.btnUploadImage.setOnClickListener {
            checkAndRequestPermission()
        }

        // Set a click listener for saving the recipe to the database
        binding.btnSaveRecipe.setOnClickListener {
            saveRecipe()
        }
    }

    /**
     * Checks and requests necessary permissions based on Android version.
     */
    private fun checkAndRequestPermission() {
        // Determine the permission based on Android version
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES // Newer permission for media images
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE // Legacy permission for external storage
        }

        when {
            // Check if permission is already granted
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted, proceed to pick an image
                pickImageFromGallery()
            }
            // Show rationale if permission was denied previously
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(this, "Permission required to access images", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(permission) // Request permission again
            }
            else -> {
                // Directly request permission if it's the first time
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    /**
     * Launches the system's image picker for the user to select an image.
     */
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE) // Allows the user to select a file that is openable
            type = "image/*" // Limits selection to images
        }
        imagePickerLauncher.launch(intent) // Start the image picker activity
    }

    /**
     * Saves the recipe details entered by the user to the database.
     */
    private fun saveRecipe() {
        // Get user input for recipe name, ingredients, and notes
        val recipeName = binding.etRecipeName.text.toString().trim()
        val ingredients = binding.etIngredients.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        // Validate that all fields are filled
        if (recipeName.isEmpty() || ingredients.isEmpty() || notes.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return // Exit the function if any field is empty
        }

        // Ensure that an image has been selected
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return // Exit the function if no image is selected
        }

        // Create a new Recipe object with the entered details
        val newRecipe = Recipe(
            id = 0, // ID will be auto-generated by the database
            name = recipeName,
            ingredients = ingredients,
            notes = notes,
            imageUri = selectedImageUri.toString(), // Save the URI of the selected image
            isFavorite = false // Default the recipe as non-favorite
        )

        // Insert the new recipe into the database
        val success = databaseHelper.insertRecipe(newRecipe)

        if (success) {
            // Display success message and close the activity
            Toast.makeText(this, "Recipe added successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // Display failure message if insertion fails
            Toast.makeText(this, "Failed to add recipe", Toast.LENGTH_SHORT).show()
        }
    }
}