package fr.isen.agathe_mullot.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val menuItemTitle = intent.getStringExtra("menuItemTitle")

        // Affiche les détails du plat sélectionné
        setContent {
            DetailContent( menuItemTitle ?: "")
        }
    }
}


@Composable
fun DetailContent(menuItemTitle: String) {
    Column {
        Text(text = "Detail Activity Content")
        Text(text = "Menu Item Title: $menuItemTitle")
        // Ajoutez ici d'autres détails du plat si nécessaire
    }
}

