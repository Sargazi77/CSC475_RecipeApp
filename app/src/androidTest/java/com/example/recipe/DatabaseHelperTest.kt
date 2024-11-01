package com.example.recipe

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {

    private lateinit var dbHelper: DatabaseHelper

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = DatabaseHelper(context, ":memory:")
        dbHelper.writableDatabase // Trigger onCreate to ensure tables are set up
        insertPermanentRecipes()
    }

    private fun insertPermanentRecipes() {
        val recipes = listOf(
            Recipe(
                id = 0,
                name = "Barbecue Chicken",
                ingredients = "4 skin-on, bone-in chicken breasts, 1/2 cup apple cider, 1/4 cup ketchup, 2 tbsp Worcestershire sauce, etc.",
                notes = "Position a rack in the upper third of the oven; preheat to 425 degrees F. Season the chicken...",
                imageUri = "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2014/2/7/1/FNM_030114-Roasted-Chicken-With-Succotash-Recipe-h_s4x3.jpg.rend.hgtvcom.826.620.suffix/1391877822515.webp",
                isFavorite = false
            ),
            Recipe(
                id = 0,
                name = "Instant Pot Salmon",
                ingredients = "1 1/4 pounds small red-skinned potatoes, 4 tbsp unsalted butter, Four 5- to 6-ounce salmon fillets, etc.",
                notes = "Put the potatoes in the bottom of an Instant Pot. Add 1 cup water, 2 tablespoons of the butter...",
                imageUri = "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2017/10/3/0/FNM_110117-Instant-Pot-Salmon-with-Garlic-Potatoes_s4x3.jpg.rend.hgtvcom.1280.720.suffix/1507047931718.webp",
                isFavorite = false
            ),
            Recipe(
                id = 0,
                name = "Cauliflower Stir-Fry",
                ingredients = "1 cup jasmine rice, 1 head cauliflower, 3 tbsp vegetable oil, etc.",
                notes = "Preheat the broiler. Cook the rice as the label directs. Meanwhile, toss the cauliflower...",
                imageUri = "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2016/12/17/4/FNM010117_Cauliflower-Star-Fry-with-Toasted-Peanuts-Recipe_s4x3.jpg.rend.hgtvcom.1280.720.suffix/1482181364458.webp",
                isFavorite = false
            ),
            Recipe(
                id = 0,
                name = "Stuffed Bell Peppers",
                ingredients = "6 bell peppers, any color, 4 tbsp olive oil, 8 ounces lean ground beef, etc.",
                notes = "Preheat the oven to 350 degrees F. Cut the tops off the peppers. Remove and discard the stems...",
                imageUri = "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2016/2/26/2/WU1307H_stuffed-peppers_s4x3.jpg.rend.hgtvcom.1280.720.suffix/1463506005081.webp",
                isFavorite = false
            )
        )
        recipes.forEach { dbHelper.insertRecipe(it) }
    }

    @After
    fun tearDown() {
        dbHelper.clearDatabase()
        dbHelper.close()
    }

    @Test
    fun testPermanentRecipesExist() {
        val allRecipes = dbHelper.getAllRecipes()
        assertEquals("Expected 4 permanent recipes", 4, allRecipes.size)

        val expectedRecipeNames = listOf(
            "Barbecue Chicken",
            "Instant Pot Salmon",
            "Cauliflower Stir-Fry",
            "Stuffed Bell Peppers"
        )
        val actualRecipeNames = allRecipes.map { it.name }
        assertEquals("Permanent recipes are missing or incorrect", expectedRecipeNames.sorted(), actualRecipeNames.sorted())
    }

    @Test
    fun testInsertRecipe() {
        val newRecipe = Recipe(
            id = 0,
            name = "Test Recipe",
            ingredients = "Test Ingredients",
            notes = "Test Notes",
            imageUri = "",
            isFavorite = false
        )
        val insertResult = dbHelper.insertRecipe(newRecipe)
        assertTrue("Recipe insertion failed", insertResult)

        val allRecipes = dbHelper.getAllRecipes()
        assertEquals("Expected 5 recipes after insertion", 5, allRecipes.size)
    }

    @Test
    fun testGetRecipeById() {
        val allRecipes = dbHelper.getAllRecipes()
        val barbecueChicken = allRecipes.find { it.name == "Barbecue Chicken" }
        assertNotNull("Barbecue Chicken recipe not found", barbecueChicken)

        val fetchedRecipe = dbHelper.getRecipeById(barbecueChicken!!.id)
        assertNotNull("Failed to fetch recipe by ID", fetchedRecipe)
        assertEquals("Barbecue Chicken", fetchedRecipe?.name)
    }

    @Test
    fun testUpdateNonPermanentRecipe() {
        val newRecipe = Recipe(
            id = 0,
            name = "Temporary Recipe",
            ingredients = "Some Ingredients",
            notes = "Some Notes",
            imageUri = "",
            isFavorite = false
        )
        dbHelper.insertRecipe(newRecipe)

        val insertedRecipe = dbHelper.getAllRecipes().find { it.name == "Temporary Recipe" }
        assertNotNull("Temporary Recipe not found after insertion", insertedRecipe)

        val updatedRecipe = insertedRecipe!!.copy(name = "Updated Recipe")
        val updateResult = dbHelper.updateRecipe(updatedRecipe)
        assertTrue("Recipe update failed", updateResult)

        val fetchedRecipe = dbHelper.getRecipeById(updatedRecipe.id)
        assertEquals("Updated Recipe", fetchedRecipe?.name)
    }

    @Test
    fun testDeleteNonPermanentRecipe() {
        val newRecipe = Recipe(
            id = 0,
            name = "Temporary Recipe",
            ingredients = "Some Ingredients",
            notes = "Some Notes",
            imageUri = "",
            isFavorite = false
        )
        dbHelper.insertRecipe(newRecipe)

        val allRecipesBeforeDeletion = dbHelper.getAllRecipes()
        assertEquals("Expected 5 recipes before deletion", 5, allRecipesBeforeDeletion.size)

        val insertedRecipe = allRecipesBeforeDeletion.find { it.name == "Temporary Recipe" }
        assertNotNull("Temporary Recipe not found for deletion", insertedRecipe)

        val deleteResult = dbHelper.deleteRecipe(insertedRecipe!!.id)
        assertTrue("Recipe deletion failed", deleteResult)

        val allRecipesAfterDeletion = dbHelper.getAllRecipes()
        assertEquals("Expected 4 recipes after deletion", 4, allRecipesAfterDeletion.size)
    }

    @Test
    fun testFavoriteStatusToggle() {
        val allRecipes = dbHelper.getAllRecipes()
        assertTrue("No recipes found in database", allRecipes.isNotEmpty())

        val recipe = allRecipes[0]
        val initialFavoriteStatus = recipe.isFavorite

        val toggleResult = dbHelper.updateRecipeFavoriteStatus(recipe.id, !initialFavoriteStatus)
        assertTrue("Favorite status toggle failed", toggleResult)

        val updatedRecipe = dbHelper.getRecipeById(recipe.id)
        assertNotNull("Failed to fetch recipe by ID after favorite toggle", updatedRecipe)
        assertEquals("Favorite status not toggled", !initialFavoriteStatus, updatedRecipe?.isFavorite)
    }
}