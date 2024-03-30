package fr.isen.agathe_mullot.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    Box(
        modifier = Modifier
            .background(Color(26, 0, 48)) // Définir la couleur de fond
            .fillMaxSize() // Remplir toute la taille disponible
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        TextWithImage(text = "Bienvenue chez TakeawayApp", imageResId = R.drawable.logo)

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.entree),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            ClickableTextWithNavigation(
                text = "Entrées",
                categoryId = 1,
                onClick = { categoryId -> launchCategoryActivity(categoryId) },

            )
            Image(
                painter = painterResource(id = R.drawable.entree),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(modifier = Modifier.padding(start = 100.dp, end = 100.dp))
        Spacer(modifier = Modifier.height(50.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.plats),
                contentDescription = null, // Ajoutez une description si nécessaire
                modifier = Modifier.size(30.dp) // Taille de l'image
            )
            ClickableTextWithNavigation(
                text = "Plats",
                categoryId = 2,
                onClick = { categoryId -> launchCategoryActivity(categoryId) }
            )
            Image(
                painter = painterResource(id = R.drawable.plats),
                contentDescription = null, // Ajoutez une description si nécessaire
                modifier = Modifier.size(30.dp) // Taille de l'image
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(modifier = Modifier.padding(start = 100.dp, end = 100.dp))
        Spacer(modifier = Modifier.height(50.dp))

        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.desserts),
                contentDescription = null, // Ajoutez une description si nécessaire
                modifier = Modifier.size(30.dp) // Taille de l'image
            )
            ClickableTextWithNavigation(
                text = "Desserts",
                categoryId = 3,
                onClick = { categoryId -> launchCategoryActivity(categoryId) }
            )
            Image(
                painter = painterResource(id = R.drawable.desserts),
                contentDescription = null, // Ajoutez une description si nécessaire
                modifier = Modifier.size(30.dp) // Taille de l'image
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(modifier = Modifier.padding(start = 100.dp, end = 100.dp))
    }
}

@Composable
fun ClickableTextWithNavigation(text: String, categoryId: Int, onClick: (Int) -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF23D7CB)),
        modifier = Modifier.clickable {
            // Appeler la fonction onClick avec l'ID de catégorie
            onClick(categoryId)
        }
    )
}

@Composable
fun TextWithImage(text: String, imageResId: Int, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Top, // Aligner le contenu en haut
        horizontalAlignment = Alignment.CenterHorizontally, // Aligner le contenu au centre horizontalement
        modifier = modifier
            .fillMaxWidth() // Prendre toute la largeur disponible
            .padding(horizontal = 16.dp, vertical = 16.dp) // Ajouter une marge
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF23D7CB), // Couleur orange
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null, // Provide a content description if needed
            modifier = Modifier
                .fillMaxWidth() // Remplir toute la largeur de l'écran
                .aspectRatio(1f) // Ratio d'aspect 16:9 pour l'image
                .height(240.dp) // Hauteur fixe de l'image
        )
    }
}
