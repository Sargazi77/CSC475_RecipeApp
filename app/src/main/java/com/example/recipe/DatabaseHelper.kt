package com.example.recipe

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper is responsible for creating, managing, and interacting with the SQLite database.
 * It includes methods for adding, retrieving, updating, and deleting recipes and shopping list items.
 */
class DatabaseHelper(
    context: Context,
    databaseName: String = "recipes.db" // Defaults to "recipes.db", but allows in-memory for testing
) : SQLiteOpenHelper(context, databaseName, null, 2) {

    // This method is called the first time the database is created
    override fun onCreate(db: SQLiteDatabase) {
        // Create the "recipes" table to store all recipe data
        db.execSQL(
            """
            CREATE TABLE recipes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                ingredients TEXT,
                notes TEXT,
                imageUri TEXT,
                isFavorite INTEGER DEFAULT 0
            )
            """
        )

        // Create the "shopping_list" table to store ingredients the user adds to the shopping list
        db.execSQL(
            """
            CREATE TABLE shopping_list (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                item TEXT
            )
            """
        )

        // Insert permanent recipes when the database is first created
        insertPermanentRecipes(db)
    }

    /**
     * Inserts four permanent recipes that will always remain in the app.
     */
    private fun insertPermanentRecipes(db: SQLiteDatabase) {
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

        recipes.forEach { insertRecipe(db, it) }
    }

    /**
     * Inserts a recipe into the "recipes" table.
     * @param db SQLiteDatabase instance to interact with the database.
     * @param recipe Recipe object containing details to insert into the table.
     */
    private fun insertRecipe(db: SQLiteDatabase, recipe: Recipe) {
        val values = ContentValues().apply {
            put("name", recipe.name)
            put("ingredients", recipe.ingredients)
            put("notes", recipe.notes)
            put("imageUri", recipe.imageUri)
            put("isFavorite", if (recipe.isFavorite) 1 else 0)
        }
        db.insert("recipes", null, values) // Insert the recipe data into the "recipes" table
    }

    // Other database methods remain unchanged to ensure existing functionality
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS recipes") // Drop old "recipes" table
        db.execSQL("DROP TABLE IF EXISTS shopping_list") // Drop old "shopping_list" table
        onCreate(db) // Recreate the tables by calling onCreate
    }

    fun insertRecipe(recipe: Recipe): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", recipe.name)
            put("ingredients", recipe.ingredients)
            put("notes", recipe.notes)
            put("imageUri", recipe.imageUri)
            put("isFavorite", if (recipe.isFavorite) 1 else 0)
        }
        return db.insert("recipes", null, values) != -1L
    }

    fun getRecipeById(id: Int): Recipe? {
        val db = readableDatabase
        val cursor = db.query("recipes", null, "id = ?", arrayOf(id.toString()), null, null, null)

        return if (cursor.moveToFirst()) {
            val recipe = Recipe(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")) == 1
            )
            cursor.close()
            recipe
        } else {
            cursor.close()
            null
        }
    }

    fun getAllRecipes(): List<Recipe> {
        val db = readableDatabase
        val cursor = db.query("recipes", null, null, null, null, null, null)

        val recipes = mutableListOf<Recipe>()
        if (cursor.moveToFirst()) {
            do {
                recipes.add(
                    Recipe(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients")),
                        notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return recipes
    }

    fun getFavoriteRecipes(): List<Recipe> {
        val db = readableDatabase
        val cursor = db.query(
            "recipes",
            null,
            "isFavorite = ?",
            arrayOf("1"),
            null,
            null,
            null
        )

        val favoriteRecipes = mutableListOf<Recipe>()
        if (cursor.moveToFirst()) {
            do {
                favoriteRecipes.add(
                    Recipe(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients")),
                        notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return favoriteRecipes
    }

    fun updateRecipe(recipe: Recipe): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", recipe.name)
            put("ingredients", recipe.ingredients)
            put("notes", recipe.notes)
            put("imageUri", recipe.imageUri)
            put("isFavorite", if (recipe.isFavorite) 1 else 0)
        }
        return db.update("recipes", values, "id = ?", arrayOf(recipe.id.toString())) > 0
    }

    fun updateRecipeFavoriteStatus(recipeId: Int, isFavorite: Boolean): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("isFavorite", if (isFavorite) 1 else 0)
        }
        return db.update("recipes", values, "id = ?", arrayOf(recipeId.toString())) > 0
    }

    fun getShoppingListItems(): List<String> {
        val db = readableDatabase
        val cursor = db.query("shopping_list", null, null, null, null, null, null)

        val items = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                items.add(cursor.getString(cursor.getColumnIndexOrThrow("item")))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun insertShoppingListItem(item: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("item", item)
        }
        return db.insert("shopping_list", null, values) != -1L
    }

    fun deleteShoppingListItem(item: String): Boolean {
        val db = writableDatabase
        return db.delete("shopping_list", "item = ?", arrayOf(item)) > 0
    }

    fun deleteRecipe(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("recipes", "id = ?", arrayOf(id.toString()))
        return result > 0
    }
    fun clearDatabase() {
        writableDatabase.execSQL("DELETE FROM recipes")
        writableDatabase.execSQL("DELETE FROM shopping_list")
    }

}