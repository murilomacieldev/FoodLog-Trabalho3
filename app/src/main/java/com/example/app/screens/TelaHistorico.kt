package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.components.ReviewCard
import com.example.app.components.TituloSecao
import com.example.app.data.FoodLogViewModel

@Composable
fun TelaHistorico(foodLog: FoodLogViewModel, onNovaReview: () -> Unit, onReview: (Int) -> Unit) {
    var excluirId by remember { mutableStateOf<Int?>(null) }
    var excluindo by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { foodLog.carregarDados() }
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { TituloSecao("Minhas avaliações (${foodLog.reviews.size})", "Veja, adicione ou remova suas experiências") }
        item {
            Button(onClick = onNovaReview, modifier = Modifier.fillMaxWidth()) { Text("+ Adicionar nova review") }
        }
        if (foodLog.reviews.isEmpty()) {
            item { Text("Você ainda não tem avaliações. Toque no botão acima para criar a primeira.") }
        } else {
            items(foodLog.reviews.asReversed(), key = { it.id }) { review ->
                ReviewCard(
                    review = review,
                    restauranteNome = foodLog.restaurantePorId(review.restauranteId)?.nome ?: "Restaurante removido",
                    onAbrir = { onReview(review.id) },
                    onRemover = { excluirId = review.id }
                )
            }
        }
    }
    val reviewExcluir = excluirId?.let { foodLog.reviewPorId(it) }
    if (reviewExcluir != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Remover avaliação?") },
            text = { Text("A review de ${reviewExcluir.prato} será removida da lista.") },
            confirmButton = { TextButton(onClick = {
                if (!excluindo) {
                    excluindo = true
                    foodLog.removerReview(reviewExcluir.id) { sucesso ->
                        excluindo = false
                        if (sucesso) excluirId = null
                    }
                }
            }, enabled = !excluindo) { Text("Remover") } },
            dismissButton = { TextButton(onClick = { excluirId = null }) { Text("Cancelar") } }
        )
    }
}
