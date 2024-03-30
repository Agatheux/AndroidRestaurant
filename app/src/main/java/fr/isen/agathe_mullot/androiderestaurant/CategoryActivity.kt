package fr.isen.agathe_mullot.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import kotlin.text.isNotBlank
import java.io.Serializable

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryName = intent.getStringExtra("categoryName") ?: ""

        // Chargement des éléments de menu depuis l'API
        loadMenuItems(categoryName)
    }

    private fun loadMenuItems(categoryName: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = "http://test.api.catering.bluecodegames.com/menu"

        val requestBody = JSONObject().apply {
            put("id_shop", 1)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            Response.Listener { response ->
                Log.d("DEBUG", "Response JSON: $response")
                val menuItems = parseMenuItems(response, categoryName)
                setContent {
                    CategoryScreenContent(categoryName, menuItems)
                }
            },
            Response.ErrorListener { error: VolleyError? ->
                val errorMessage = error?.message ?: "Unknown error"
                Log.e("API_ERROR", "Error: $errorMessage")
                val menuItems = listOf(MenuItem(-1, "Error: Unable to load menu items", emptyList(), emptyList(), 12.2))
                setContent {
                    CategoryScreenContent(categoryName, menuItems)
                }
            })

        requestQueue.add(jsonObjectRequest)
    }


    private fun parseMenuItems(response: JSONObject, categoryName: String): List<MenuItem> {
        val menuItems = mutableListOf<MenuItem>()

        if (response.has("data")) {
            val dataArray = response.getJSONArray("data")
            val gson = Gson()

            for (i in 0 until dataArray.length()) {
                val categoryObject = dataArray.getJSONObject(i)
                val category = gson.fromJson(categoryObject.toString(), Category::class.java)

                if (category.name_fr == categoryName) {
                    category.items.forEach { item ->
                        val ingredientsList = item.ingredients.map {
                            Ingredient(it.id.toInt(), it.name_fr)
                        }
                        val imageUrls = item.images.filter { imageUrl -> !imageUrl.isNullOrEmpty() }

                        // Recherche du prix correspondant à l'item
                        Log.d("DEBUG", "Category Object: $categoryObject")
                        val price = findPriceForItem(item, categoryObject)


                        Log.d("DEBUG", "Item: $item, Price: $price")
                        val menuItem = MenuItem(
                            item.id.toInt(),
                            item.name_fr,
                            ingredientsList,
                            imageUrls,
                            price
                        )
                        menuItems.add(menuItem)
                    }
                    break
                }
            }
        }

        return menuItems
    }


    // Fonction pour rechercher le prix correspondant à l'item
    private fun findPriceForItem(item: Item, categoryObject: JSONObject): Double {
        val itemId = item.id.toInt()

        if (categoryObject.has("items")) {
            val itemsArray = categoryObject.getJSONArray("items")
            for (i in 0 until itemsArray.length()) {
                val itemObject = itemsArray.getJSONObject(i)
                val itemIdFromResponse = itemObject.getString("id")
                if (itemIdFromResponse == item.id && itemObject.has("prices")) {
                    val pricesArray = itemObject.getJSONArray("prices")
                    for (j in 0 until pricesArray.length()) {
                        val priceObject = pricesArray.getJSONObject(j)
                        return priceObject.getDouble("price")
                    }
                }
            }
        }
        return 0.0 // Retourne 0 si aucun prix correspondant n'est trouvé
    }



}

data class MenuItem(
    val id: Int,
    val name_fr: String,
    val ingredients: List<Ingredient>,
    val imageUrls: List<String>,
    val price: Double // Ajout de la propriété pour le prix
) : Serializable

data class Category(val name_fr: String, val items: List<Item>) : Serializable
data class Item(val id: String, val name_fr: String, val ingredients: List<Ingredient>, val images: List<String>) : Serializable
data class Ingredient(val id: Int, val name_fr: String) : Serializable


@Composable
fun CategoryScreenContent(categoryName: String, menuItems: List<MenuItem>) {
    val context = LocalContext.current

    val imageResId = when (categoryName) {
        "Entrées" -> R.drawable.entree // Remplacez "entree_image" par le nom de votre image pour les entrées
        "Plats" -> R.drawable.plats // Remplacez "plat_image" par le nom de votre image pour les plats
        "Desserts" -> R.drawable.desserts // Remplacez "dessert_image" par le nom de votre image pour les desserts
        else -> R.drawable.logo // Image par défaut si la catégorie n'est pas reconnue
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1a0030))
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null, // Description du contenu de l'image (peut être null si non pertinent)
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp) // Taille de l'image
        )

        // Affichage des éléments du menu
        menuItems.forEach { menuItem ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateToDetailActivity(context, menuItem)
                    }
            ) {
                Text(
                    text = menuItem.name_fr,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "${menuItem.price} €",
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}



private fun navigateToDetailActivity(context: Context, menuItem: MenuItem) {
    val intent = Intent(context, DetailActivity::class.java).apply {
        putExtra("menuItem", menuItem)
    }
    context.startActivity(intent)
}

@Preview
@Composable
fun PreviewCategoryContent() {
    val menuItems = listOf(
        MenuItem(1, "Item 1", emptyList(), emptyList(), 12.2),
        MenuItem(2, "Item 2", emptyList(), emptyList(), 12.2),
        MenuItem(3, "Item 3", emptyList(), emptyList(), 12.2)
    )
    CategoryScreenContent("Category", menuItems)
}
