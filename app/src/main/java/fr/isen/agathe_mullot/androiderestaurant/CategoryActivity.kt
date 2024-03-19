package fr.isen.agathe_mullot.androiderestaurant

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryId = intent.getIntExtra("categoryId", -1)
        val categoryName = getCategoryNameById(categoryId)
        val menuItems = getMenuItemsByCategoryId(categoryId, resources)

        setContent {
            CategoryContent(categoryName, menuItems)
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

        titlesArray.forEach { title ->
            menuItems.add(MenuItem(title))
        }

        return menuItems
    }
}

@Composable
fun CategoryContent(categoryName: String, menuItems: List<MenuItem>) {
    Column {
        Text(text = "Category: $categoryName")
        MenuList(menuItems)
    }
}

@Composable
fun MenuList(menuItems: List<MenuItem>) {
    LazyColumn {
        items(menuItems) { menuItem ->
            Text(text = menuItem.title)
        }
    }
}

@Preview
@Composable
fun PreviewCategoryContent() {
    val menuItems = listOf(
        MenuItem("Item 1"),
        MenuItem("Item 2"),
        MenuItem("Item 3")
    )
    CategoryContent("Category", menuItems)
}

data class MenuItem(val title: String)
