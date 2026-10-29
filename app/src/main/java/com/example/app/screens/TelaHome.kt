package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.components.ReviewCard
import com.example.app.components.RestauranteCard
import com.example.app.components.TituloSecao
import com.example.app.data.FoodLogViewModel
import com.example.app.ui.theme.LaranjaFoodLog

@Composable
fun TelaHome(
    foodLog: FoodLogViewModel,
    onBusca: () -> Unit,
    onNovoLog: () -> Unit,
    onReview: (Int) -> Unit,
    onRestaurante: (Int) -> Unit
) {
    var filtro by remember { mutableStateOf("Tudo") }
    LaunchedEffect(Unit) { foodLog.carregarDados() }
    val categorias = listOf("Tudo", "Hamburgueria", "Japonês", "Italiana")
    val restaurantesFiltrados = foodLog.restaurantes.filter { filtro == "Tudo" || it.categoria == filtro }
    val reviewsFiltradas = foodLog.reviews.filter { review ->
        restaurantesFiltrados.any { it.id == review.restauranteId }
    }.asReversed()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("FoodLog", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LaranjaFoodLog, modifier = Modifier.weight(1f))
                IconButton(onClick = onBusca) { Icon(Icons.Default.Search, contentDescription = "Buscar restaurantes") }
            }
        }
        item { TituloSecao("Descubra e registre", "Suas experiências gastronômicas em um só lugar") }
        item {
            Button(onClick = onNovoLog, modifier = Modifier.fillMaxWidth()) { Text("+ Registrar nova experiência") }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                categorias.forEach { categoria ->
                    FilterChip(selected = filtro == categoria, onClick = { filtro = categoria }, label = { Text(categoria) })
                }
            }
        }
        item { TituloSecao("Reviews recentes") }
        if (reviewsFiltradas.isEmpty()) {
            item { Text("Ainda não há avaliações nessa categoria.") }
        } else {
            items(reviewsFiltradas, key = { "review_${it.id}" }) { review ->
                ReviewCard(review, foodLog.restaurantePorId(review.restauranteId)?.nome ?: "Restaurante removido", onAbrir = { onReview(review.id) })
            }
        }
        item { TituloSecao("Restaurantes para descobrir") }
        if (restaurantesFiltrados.isEmpty()) {
            item { Text("Nenhum restaurante encontrado nessa categoria.") }
        } else {
            items(restaurantesFiltrados, key = { "rest_${it.id}" }) { restaurante ->
                RestauranteCard(restaurante, foodLog.mediaDoRestaurante(restaurante.id), foodLog.reviewsDoRestaurante(restaurante.id).size, onAbrir = { onRestaurante(restaurante.id) })
            }
        }
        item {
            OutlinedButton(onClick = onBusca, modifier = Modifier.fillMaxWidth()) { Text("Ver todos os restaurantes") }
        }
    }
}
