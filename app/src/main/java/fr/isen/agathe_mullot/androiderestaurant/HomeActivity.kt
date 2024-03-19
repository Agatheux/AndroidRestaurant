package fr.isen.agathe_mullot.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.isen.agathe_mullot.androiderestaurant.ui.theme.AndroidERestaurantTheme
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreenContent(navController = navController) { categoryId ->
                            val categoryName = getCategoryNameById(categoryId)
                            launchCategoryActivity(categoryId, categoryName)
                        }
                    }
                }
            }
        }
    }

    private fun launchCategoryActivity(categoryId: Int, categoryName: String) {
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra("categoryId", categoryId)
        intent.putExtra("categoryName", categoryName)
        startActivity(intent)
    }

    private fun getCategoryNameById(categoryId: Int): String {
        return when (categoryId) {
            1 -> "Entrées"
            2 -> "Plats"
            3 -> "Desserts"
            else -> "Unknown Category"
        }
    }
}


@Composable
fun HomeScreenContent(navController: NavController, launchCategoryActivity: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextWithImage(text = "Bienvenue", imageResId = R.drawable.androiiiiid)

        ClickableTextWithNavigation(
            text = "Entrées",
            categoryId = 1,
            onClick = { categoryId -> launchCategoryActivity(categoryId) }
        )
        Divider(modifier = Modifier.padding(start = 100.dp, end = 100.dp))
        Spacer(modifier = Modifier.height(50.dp))
        ClickableTextWithNavigation(
            text = "Plats",
            categoryId = 2,
            onClick = { categoryId -> launchCategoryActivity(categoryId) }
        )
        Divider(modifier = Modifier.padding(start = 100.dp, end = 100.dp))
        Spacer(modifier = Modifier.height(50.dp))
        ClickableTextWithNavigation(
            text = "Desserts",
            categoryId = 3,
            onClick = { categoryId -> launchCategoryActivity(categoryId) }
        )
        Divider(modifier = Modifier.padding(start = 100.dp, end = 100.dp))
    }
}

@Composable
fun ClickableTextWithNavigation(text: String, categoryId: Int, onClick: (Int) -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFFA500)),
        modifier = Modifier.clickable {
            // Appeler la fonction onClick avec l'ID de catégorie
            onClick(categoryId)
        }
    )
}

@Composable
fun TextWithImage(text: String, imageResId: Int, modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(50.dp))
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Column(
            horizontalAlignment = Alignment.End // Aligner le contenu à droite
        ) {
            Text(
                text = "Bienvenue",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFFFA500), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Text(
                text = "chez",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFFFA500), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Text(
                text = "DroidRestaurant",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF8B4513), fontStyle = FontStyle.Italic, fontSize = 20.sp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Image(
            painter = painterResource(id = R.drawable.androiiiiid),
            contentDescription = null // Provide a content description if needed
        )
        Spacer(modifier = Modifier.height(200.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AndroidERestaurantTheme {
        val navController = rememberNavController()
        HomeScreenContent(navController) { }
    }
}
