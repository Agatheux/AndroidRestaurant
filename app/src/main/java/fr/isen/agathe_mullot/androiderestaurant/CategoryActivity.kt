package fr.isen.agathe_mullot.androiderestaurant

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class CategoryActivity : ComponentActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryId = intent.getIntExtra("categoryId", -1)
        val categoryName = getCategoryNameById(categoryId)
        val menuItems = getMenuItemsByCategoryId(categoryId, resources)

        navController = NavHostController(this)
        setContent {
            CategoryContent(categoryName, menuItems, navController)
        }
    }

    private fun getCategoryNameById(categoryId: Int): String {
        return when (categoryId) {
            1 -> "Entrées"
            2 -> "Plats"
            3 -> "Desserts"
            else -> "Autres"
        }
    }

    private fun getMenuItemsByCategoryId(categoryId: Int, resources: Resources): List<MenuItem> {
        val menuItems = mutableListOf<MenuItem>()
        val titlesArray = when (categoryId) {
            1 -> resources.getStringArray(R.array.entries_titles)
            2 -> resources.getStringArray(R.array.main_course_titles)
            3 -> resources.getStringArray(R.array.dessert_titles)
            else -> emptyArray()
        }

        titlesArray.forEachIndexed { index, title ->
            menuItems.add(MenuItem(index, title))
        }

        return menuItems
    }
}


@Composable
fun CategoryContent(categoryName: String, menuItems: List<MenuItem>, navController: NavController) {
    Column {
        Text(text = "Category: $categoryName")
        MenuList(menuItems, navController)
    }
}

@Composable
fun MenuList(menuItems: List<MenuItem>, navController: NavController) {
    LazyColumn {
        items(menuItems) { menuItem ->
            Text(
                text = menuItem.title,
                modifier = Modifier.clickable {
                    // Pass the selected menu item to the detail activity
                    val intent = Intent(navController.context, DetailActivity::class.java)
                    intent.putExtra("menuItemTitle", menuItem.title)
                    navController.context.startActivity(intent)

                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewCategoryContent() {
    val navController = rememberNavController()
    val menuItems = listOf(
        MenuItem(1, "Item 1"),
        MenuItem(2, "Item 2"),
        MenuItem(3, "Item 3")
    )
    CategoryContent("Category", menuItems, navController)
}

data class MenuItem(val id: Int, val title: String)
