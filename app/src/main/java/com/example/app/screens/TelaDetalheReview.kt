package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.app.components.TituloSecao
import com.example.app.components.dinheiro
import com.example.app.data.FoodLogViewModel
import com.example.app.ui.theme.LaranjaFoodLog

@Composable
fun TelaDetalheReview(foodLog: FoodLogViewModel, id: Int, onRestaurante: (Int) -> Unit, onVoltar: () -> Unit) {
    LaunchedEffect(id) { foodLog.carregarDetalheReview(id); foodLog.carregarDados() }

    var editando by remember(id) { mutableStateOf(false) }
    var pratoEdicao by remember(id) { mutableStateOf("") }
    var notaEdicao by remember(id) { mutableIntStateOf(0) }
    var precoEdicao by remember(id) { mutableStateOf("") }
    var comentarioEdicao by remember(id) { mutableStateOf("") }
    var erroEdicao by remember(id) { mutableStateOf("") }
    var salvando by remember(id) { mutableStateOf(false) }

    val review = foodLog.reviewDetalhe?.takeIf { it.id == id }
    if (foodLog.carregandoReview) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (review == null) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Avaliação não encontrada no banco.")
            Button(onClick = onVoltar) { Text("Voltar") }
        }
        return
    }
    val restaurante = foodLog.restaurantePorId(review.restauranteId)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TituloSecao(review.prato, "Avaliação #${review.id}")
        Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Restaurante", style = MaterialTheme.typography.labelLarge)
                Text(restaurante?.nome ?: "Restaurante não encontrado", style = MaterialTheme.typography.titleMedium)
                HorizontalDivider()
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { i ->
                        Icon(
                            Icons.Default.Star, contentDescription = null,
                            tint = if (i < review.nota) LaranjaFoodLog else androidx.compose.ui.graphics.Color.LightGray
                        )
                    }
                }
                Text("Nota: ${review.nota}/5", fontWeight = FontWeight.Bold)
                Text("Preço pago: ${dinheiro(review.preco)}", fontWeight = FontWeight.Medium)
                HorizontalDivider()
                Text("Meu comentário", style = MaterialTheme.typography.labelLarge)
                Text(review.comentario)
            }
        }
        OutlinedButton(onClick = {
            pratoEdicao = review.prato
            notaEdicao = review.nota
            precoEdicao = review.preco.toString()
            comentarioEdicao = review.comentario
            erroEdicao = ""
            editando = true
        }, modifier = Modifier.fillMaxWidth()) { Text("Editar avaliação") }
        if (restaurante != null) {
            Button(onClick = { onRestaurante(restaurante.id) }, modifier = Modifier.fillMaxWidth()) {
                Text("Ver detalhes do restaurante")
            }
        }
    }

    if (editando) {
        AlertDialog(
            onDismissRequest = { if (!salvando) editando = false },
            title = { Text("Editar avaliação") },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 430.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(pratoEdicao, { pratoEdicao = it }, label = { Text("Prato") })
                    Text("Nota")
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        (1..5).forEach { estrela ->
                            IconButton(onClick = { notaEdicao = estrela }, modifier = Modifier.size(44.dp)) {
                                Icon(Icons.Default.Star, contentDescription = "$estrela estrelas", tint = if (estrela <= notaEdicao) LaranjaFoodLog else androidx.compose.ui.graphics.Color.LightGray)
                            }
                        }
                    }
                    OutlinedTextField(
                        precoEdicao, { precoEdicao = it }, label = { Text("Preço") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(comentarioEdicao, { comentarioEdicao = it }, label = { Text("Comentário") }, minLines = 2)
                    if (erroEdicao.isNotEmpty()) Text(erroEdicao, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(enabled = !salvando, onClick = {
                    val valor = precoEdicao.replace(',', '.').toDoubleOrNull()
                    erroEdicao = when {
                        pratoEdicao.isBlank() -> "Informe o prato."
                        notaEdicao !in 1..5 -> "Selecione uma nota de 1 a 5."
                        valor == null || !valor.isFinite() || valor <= 0 -> "Informe um preço válido."
                        comentarioEdicao.isBlank() -> "Informe o comentário."
                        else -> ""
                    }
                    if (erroEdicao.isEmpty() && valor != null) {
                        salvando = true
                        foodLog.atualizarReview(id, pratoEdicao, notaEdicao, valor, comentarioEdicao) { sucesso ->
                            salvando = false
                            if (sucesso) editando = false
                            else erroEdicao = "Não foi possível atualizar no banco."
                        }
                    }
                }) { Text(if (salvando) "Salvando..." else "Salvar alterações") }
            },
            dismissButton = { TextButton(onClick = { editando = false }, enabled = !salvando) { Text("Cancelar") } }
        )
    }
}
