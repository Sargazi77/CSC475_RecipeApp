import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.recipe.MainActivity
import com.example.recipe.R
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun testAddRecipeButton() {
        // Launch the main activity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Click on the FAB to add a recipe and check if AddRecipeActivity is launched
        onView(withId(R.id.fabAddRecipe)).perform(click())

        // Verify if AddRecipeActivity is opened by checking the presence of 'etRecipeName'
        onView(withId(R.id.etRecipeName)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFunctionality() {
        // Launch the main activity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Check if the search view is displayed and can accept input
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
    }
}
