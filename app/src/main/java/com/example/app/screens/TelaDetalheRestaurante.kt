package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app.components.ReviewCard
import com.example.app.components.TituloSecao
import com.example.app.components.notaTexto
import com.example.app.data.FoodLogViewModel
import com.example.app.ui.theme.LaranjaFoodLog

@Composable
fun TelaDetalheRestaurante(
    foodLog: FoodLogViewModel, id: Int,
    onNovaReview: () -> Unit, onReview: (Int) -> Unit, onVoltar: () -> Unit
) {
    LaunchedEffect(id) {
        foodLog.carregarDetalheRestaurante(id)
        foodLog.carregarDados()
    }
    var editando by remember(id) { mutableStateOf(false) }
    var nomeEdicao by remember(id) { mutableStateOf("") }
    var categoriaEdicao by remember(id) { mutableStateOf("") }
    var cidadeEdicao by remember(id) { mutableStateOf("") }
    var descricaoEdicao by remember(id) { mutableStateOf("") }
    var erroEdicao by remember(id) { mutableStateOf("") }
    var salvando by remember(id) { mutableStateOf(false) }

    val restaurante = foodLog.restauranteDetalhe?.takeIf { it.id == id }
    if (foodLog.carregandoRestaurante) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (restaurante == null) {
        Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Restaurante não encontrado no banco.")
            Button(onClick = onVoltar) { Text("Voltar") }
        }
        return
    }

    val avaliacoes = foodLog.reviewsDoRestaurante(id)
    val media = foodLog.mediaDoRestaurante(id)
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { TituloSecao(restaurante.nome, "${restaurante.categoria} • ${restaurante.cidade}") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(restaurante.descricao)
                    HorizontalDivider()
                    Text("Média calculada a partir das reviews", style = MaterialTheme.typography.bodySmall)
                    Text(
                        if (media == null) "Ainda sem avaliações" else "★ ${notaTexto(media)} de 5",
                        style = MaterialTheme.typography.headlineSmall,
                        color = LaranjaFoodLog,
                        fontWeight = FontWeight.Bold
                    )
                    Text("${avaliacoes.size} avaliação(ões) vinculada(s)")
                }
            }
        }
        item {
            OutlinedButton(onClick = {
                nomeEdicao = restaurante.nome
                categoriaEdicao = restaurante.categoria
                cidadeEdicao = restaurante.cidade
                descricaoEdicao = restaurante.descricao
                erroEdicao = ""
                editando = true
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Editar restaurante")
            }
        }
        item { Button(onClick = onNovaReview, modifier = Modifier.fillMaxWidth()) { Text("Avaliar este restaurante") } }
        item { TituloSecao("Avaliações deste restaurante") }
        if (avaliacoes.isEmpty()) {
            item { Text("Seja a primeira pessoa a avaliar este restaurante.") }
        } else {
            items(avaliacoes.asReversed(), key = { it.id }) { review ->
                ReviewCard(review, restaurante.nome, onAbrir = { onReview(review.id) })
            }
        }
    }

    if (editando) {
        AlertDialog(
            onDismissRequest = { if (!salvando) editando = false },
            title = { Text("Editar restaurante") },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(nomeEdicao, { nomeEdicao = it }, label = { Text("Nome") })
                    OutlinedTextField(categoriaEdicao, { categoriaEdicao = it }, label = { Text("Categoria") })
                    OutlinedTextField(cidadeEdicao, { cidadeEdicao = it }, label = { Text("Cidade") })
                    OutlinedTextField(descricaoEdicao, { descricaoEdicao = it }, label = { Text("Descrição") }, minLines = 2)
                    if (erroEdicao.isNotEmpty()) Text(erroEdicao, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !salvando,
                    onClick = {
                        if (listOf(nomeEdicao, categoriaEdicao, cidadeEdicao, descricaoEdicao).any { it.isBlank() }) {
                            erroEdicao = "Preencha todos os campos."
                        } else {
                            salvando = true
                            foodLog.atualizarRestaurante(
                                id, nomeEdicao, categoriaEdicao, cidadeEdicao, descricaoEdicao
                            ) { sucesso ->
                                salvando = false
                                if (sucesso) editando = false
                                else erroEdicao = "Não foi possível atualizar no banco."
                            }
                        }
                    }
                ) { Text(if (salvando) "Salvando..." else "Salvar alterações") }
            },
            dismissButton = {
                TextButton(onClick = { editando = false }, enabled = !salvando) { Text("Cancelar") }
            }
        )
    }
}
