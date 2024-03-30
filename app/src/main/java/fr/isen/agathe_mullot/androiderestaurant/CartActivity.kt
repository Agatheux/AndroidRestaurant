package fr.isen.agathe_mullot.androiderestaurant

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import java.io.File

class CartActivity : ComponentActivity() {
    private val cartItemsState = mutableStateOf(emptyList<CartItem>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val initialCartItems = readCartItems(context)
            cartItemsState.value = initialCartItems
            CartScreenContent(cartItemsState.value, context, ::updateCartItems)
        }
    }

    private fun updateCartItems(updatedItems: List<CartItem>) {
        cartItemsState.value = updatedItems
    }
}

@Composable
fun CartScreenContent(
    cartItems: List<CartItem>,
    context: Context,
    updateCartItems: (List<CartItem>) -> Unit
) {
    CartScreen(cartItems, context, updateCartItems)
    updateCartItems(cartItems) // Appel de la fonction pour mettre à jour le nombre d'articles dans le panier
}


// Dans CartScreen
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    context: Context,
    updateCartItems: (List<CartItem>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) } // Variable d'état pour afficher l'AlertDialog

    // Grouper les articles par titre
    val groupedCartItems = cartItems.groupBy { it.menuItem }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(26, 0, 48))
    ) {
        LazyColumn {
            groupedCartItems.forEach { (_, items) ->
                item {
                    CartItemRow(
                        cartItem = items.first(),
                        quantity = items.sumBy { it.quantity },
                        onRemoveItemClick = { item ->
                            removeItemFromCart(item, context, updateCartItems)
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                clearCart(context)
                updateCartItems(emptyList())
                showDialog = true // Afficher l'AlertDialog
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF23D7CB))
        ) {
            Text("Passer la commande", color = Color.White) // Texte en blanc
        }
    }

    // Affichage de l'AlertDialog si showDialog est vrai
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Information") },
            text = { Text("Commande en cours de livraison") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    quantity: Int,
    onRemoveItemClick: (CartItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${cartItem.menuItem.name_fr} x $quantity", // Afficher le titre avec la quantité
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = Color.White
        )
        Button(
            onClick = { onRemoveItemClick(cartItem) },
            modifier = Modifier.padding(start = 16.dp),

        ) {
            Text("Supprimer")
        }
    }
}

fun readCartItems(context: Context): List<CartItem> {
    val cartFile = File(context.filesDir, "cart.json")
    val cartItems = mutableListOf<CartItem>()
    try {
        Log.d("CartActivity", "Lecture du fichier JSON...")
        cartFile.forEachLine { line ->
            val cartItem = Gson().fromJson(line, CartItem::class.java)
            cartItems.add(cartItem) // Ajouter l'élément à la liste
        }
        Log.d("CartActivity", "Elements du panier : $cartItems")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return cartItems
}

fun removeItemFromCart(
    cartItem: CartItem,
    context: Context,
    updateCartItems: (List<CartItem>) -> Unit
) {
    val cartFile = File(context.filesDir, "cart.json")
    val cartItems = readCartItems(context)
    val updatedCartItems = cartItems.filterNot { it == cartItem } // Filtrer tous les éléments sauf celui à supprimer

    // Mise à jour du fichier JSON
    cartFile.writeText("")
    updatedCartItems.forEach { item ->
        cartFile.appendText(item.toJson() + "\n")
    }

    // Mettre à jour le nombre d'articles dans le panier
    saveCartItemCount(context, updatedCartItems.sumBy { it.quantity })

    // Mettre à jour l'interface utilisateur
    updateCartItems(updatedCartItems)
}

fun clearCart(context: Context) {
    // Effacer le contenu du fichier cart.json
    val cartFile = File(context.filesDir, "cart.json")
    cartFile.writeText("")

    // Mettre à jour le nombre d'articles dans les préférences utilisateur
    saveCartItemCount(context, 0)
}


@Preview
@Composable
fun PreviewCartScreen() {
    val context = LocalContext.current
    val cartItems = listOf(
        CartItem(
            MenuItem(
                id = 1,
                name_fr = "Nom de l'article",
                ingredients = emptyList(),
                imageUrls = emptyList(),
                price = 10.0
            ),
            quantity = 2
        )
    )
    CartScreenContent(cartItems, context) {}
}
