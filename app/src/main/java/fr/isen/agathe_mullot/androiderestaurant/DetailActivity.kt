package fr.isen.agathe_mullot.androiderestaurant

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File


class DetailActivity : ComponentActivity() {
    private var cartItemCount by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val menuItem = intent.getSerializableExtra("menuItem") as MenuItem

        setContent {
            val context = LocalContext.current

            // Récupérer la valeur actuelle de cartItemCount
            cartItemCount = getCartItemCount(context)

            DetailScreenContent(menuItem, cartItemCount)
        }
    }

    override fun onResume() {
        super.onResume()

        // Mettre à jour la valeur de cartItemCount lors de la reprise de l'activité
        cartItemCount = getCartItemCount(this)
    }

    // Fonction pour mettre à jour le nombre d'articles dans le panier
    fun updateCartItemCount() {
        // Mettez à jour la valeur de cartItemCount
        cartItemCount = getCartItemCount(this)
    }
}


@Composable
fun DetailScreenContent(menuItem: MenuItem, cartItemCount: Int) {
    DetailScreen(menuItem = menuItem, cartItemCount = cartItemCount)
}

@Composable
fun DetailScreen(menuItem: MenuItem, cartItemCount: Int) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var quantity by remember { mutableStateOf(1) }
    var cartItemCountState by remember { mutableStateOf(cartItemCount) }

    LaunchedEffect(cartItemCount) {
        cartItemCountState = cartItemCount
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(26, 0, 48))

    ) {
        // Custom toolbar avec la pastille
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // Hauteur de la barre d'outils agrandie
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Détail",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = Color.White
            )
            IconButton(
                onClick = {
                    // Gérer le clic sur l'icône du chariot
                    context.startActivity(Intent(context, CartActivity::class.java))
                },
                modifier = Modifier.size(48.dp) // Taille du chariot agrandie
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Panier", tint = Color.White)
            }
            // Affichage de la pastille
            if (cartItemCountState > 0) {
                Text(
                    text = cartItemCountState.toString(),
                    color = Color.White,
                    fontSize = 14.sp, // Taille de la police augmentée
                    modifier = Modifier
                        .background(Color(0xFF23D7CB), CircleShape)
                        .padding(start = 4.dp)
                        .clickable {
                            context.startActivity(Intent(context, CartActivity::class.java))
                        }
                        .padding(4.dp)
                )
            }
        }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Afficher le carrousel horizontal des images valides
            val validImageUrls = menuItem.imageUrls.filter { url ->
                !url.contains("20lyonnaise.jpg") &&
                        !url.contains("grille.jpg") &&
                        !url.contains("burger-maison-1000x675.jpg")
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                itemsIndexed(validImageUrls) { index, imageUrl ->
                    Image(
                        painter = rememberImagePainter(imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(400.dp)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Afficher le nom de l'élément
            Text(
                text = menuItem.name_fr,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Afficher les ingrédients séparés par des virgules
            Text(
                text = menuItem.ingredients.joinToString { it.name_fr },
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(30.dp))

            // Sélecteur de quantité
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Quantité: ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Button(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("-")
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.White
                )
                Button(
                    onClick = { quantity++ },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("+")
                }
            }

            // Afficher le prix total
            val totalPrice = menuItem.price * quantity
            Text(
                text = "Prix total: $totalPrice €",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(50.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Enregistrer l'article dans le panier
                        saveToCart(context, CartItem(menuItem, quantity))

                        // Mettre à jour le nombre d'articles dans le panier
                        (context as? DetailActivity)?.updateCartItemCount()

                        // Afficher un message de confirmation
                        showAlertDialog(context, "L'article a été ajouté au panier.")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFF23D7CB))
            ) {
                Text("Ajouter au panier")
            }
        }
    }
}



fun showAlertDialog(context: Context, message: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Information")
    builder.setMessage(message)
    builder.setPositiveButton("OK") { dialog, which ->
        dialog.dismiss()
    }
    val dialog = builder.create()
    dialog.show()
}


data class CartItem(
    val menuItem: fr.isen.agathe_mullot.androiderestaurant.MenuItem,
    val quantity: Int
)

fun CartItem.toJson(): String {
    return Gson().toJson(this)
}

fun saveToCart(context: Context, cartItem: CartItem) {
    val cartFile = File(context.filesDir, "cart.json")
    cartFile.appendText(cartItem.toJson() + "\n")

    // Mettre à jour le nombre d'articles dans les préférences utilisateur
    val itemCount = getCartItemCount(context) + cartItem.quantity
    saveCartItemCount(context, itemCount)
}

fun saveCartItemCount(context: Context, count: Int) {
    val sharedPref = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putInt("cartItemCount", count)
        apply()
    }
}

fun getCartItemCount(context: Context): Int {
    val sharedPref = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
    return sharedPref.getInt("cartItemCount", 0)
}




@Preview
@Composable
fun PreviewDetailScreen() {
    val menuItem = MenuItem(
        1,
        "Item 1",
        emptyList(),
        emptyList(),
        12.2
    )
    val cartItemCount = 0
    DetailScreen(menuItem, cartItemCount)
}
