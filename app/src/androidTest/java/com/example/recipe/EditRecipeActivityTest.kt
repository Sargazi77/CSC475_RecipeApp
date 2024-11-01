package com.example.recipe

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditRecipeActivityTest {

    private lateinit var scenario: ActivityScenario<EditRecipeActivity>

    @Before
    fun setup() {
        // Launching EditRecipeActivity with the required intent
        val intent = Intent(ApplicationProvider.getApplicationContext(), EditRecipeActivity::class.java).apply {
            putExtra("RECIPE_ID", 1) // Assuming 1 is an existing recipe ID in the test database
        }
        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testLoadRecipeDetails() {
        // Check if the EditRecipeActivity loaded the recipe details correctly
        onView(withId(R.id.etRecipeName)).check(matches(isDisplayed()))
        onView(withId(R.id.etIngredients)).check(matches(isDisplayed()))
        onView(withId(R.id.etNotes)).check(matches(isDisplayed()))
    }

    @Test
    fun testOpenImagePicker() {
        // Click the image view to attempt to open the image picker
        onView(withId(R.id.ivRecipeImage)).perform(click())
        // Note: We won’t validate the intent here as we focus on view interaction
    }

    @Test
    fun testSaveRecipeChanges() {
        // Enter text into the name, ingredients, and notes fields
        onView(withId(R.id.etRecipeName)).perform(replaceText("New Recipe Name"), closeSoftKeyboard())
        onView(withId(R.id.etIngredients)).perform(replaceText("New Ingredients"), closeSoftKeyboard())
        onView(withId(R.id.etNotes)).perform(replaceText("New Notes"), closeSoftKeyboard())

        // Click the save button
        onView(withId(R.id.btnSave)).perform(click())

        // Verify that the activity finishes after save (expecting activity to finish)
        scenario.onActivity {
            assert(it.isFinishing)
        }
    }
}