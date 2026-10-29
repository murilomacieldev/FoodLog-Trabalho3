package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.components.RestauranteCard
import com.example.app.components.TituloSecao
import com.example.app.data.FoodLogViewModel

@Composable
fun TelaBusca(foodLog: FoodLogViewModel, onRestaurante: (Int) -> Unit) {
    var termo by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Tudo") }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }
    var excluirId by remember { mutableStateOf<Int?>(null) }
    var salvando by remember { mutableStateOf(false) }
    var excluindo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { foodLog.carregarDados() }


    val filtrados = foodLog.restaurantes.filter { restaurante ->
        (termo.isBlank() || listOf(restaurante.nome, restaurante.categoria, restaurante.cidade).any { it.contains(termo.trim(), ignoreCase = true) }) &&
            (filtro == "Tudo" || restaurante.categoria.equals(filtro, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TituloSecao("Buscar", "Explore e organize os restaurantes") }
        item {
            OutlinedTextField(
                value = termo, onValueChange = { termo = it },
                label = { Text("Restaurante, tipo ou cidade") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                listOf("Tudo", "Italiana", "Hamburgueria", "Japonês").forEach { opcao ->
                    FilterChip(selected = filtro == opcao, onClick = { filtro = opcao }, label = { Text(opcao) })
                }
            }
        }
        item {
            Button(onClick = { mostrarFormulario = !mostrarFormulario; erro = "" }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (mostrarFormulario) "Fechar formulário" else "Adicionar restaurante")
            }
        }
        if (mostrarFormulario) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Novo restaurante", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(nome, { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(categoria, { categoria = it }, label = { Text("Categoria (ex.: Italiana)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(cidade, { cidade = it }, label = { Text("Cidade") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(descricao, { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                        if (erro.isNotBlank()) Text(erro, color = MaterialTheme.colorScheme.error)
                        Button(onClick = {
                            if (listOf(nome, categoria, cidade, descricao).any { it.isBlank() }) {
                                erro = "Preencha todos os campos."
                            } else {
                                if (!salvando) {
                                    salvando = true
                                    foodLog.adicionarRestaurante(nome, categoria, cidade, descricao) { sucesso ->
                                        salvando = false
                                        if (sucesso) {
                                            nome = ""; categoria = ""; cidade = ""; descricao = ""; erro = ""
                                            mostrarFormulario = false
                                            termo = ""; filtro = "Tudo"
                                        } else erro = "Não foi possível salvar. Confira o Logcat."
                                    }
                                }
                            }
                        }, enabled = !salvando, modifier = Modifier.fillMaxWidth()) {
                            Text(if (salvando) "Salvando..." else "Salvar restaurante")
                        }
                    }
                }
            }
        }
        item { TituloSecao("Restaurantes (${filtrados.size})") }
        if (filtrados.isEmpty()) {
            item { Text("Nenhum restaurante encontrado. Experimente outra busca ou adicione um novo.") }
        } else {
            items(filtrados, key = { it.id }) { restaurante ->
                RestauranteCard(
                    restaurante = restaurante,
                    media = foodLog.mediaDoRestaurante(restaurante.id),
                    quantidade = foodLog.reviewsDoRestaurante(restaurante.id).size,
                    onAbrir = { onRestaurante(restaurante.id) },
                    onRemover = { excluirId = restaurante.id }
                )
            }
        }
    }
    if (erro.isNotEmpty() && !mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { erro = "" },
            title = { Text("Aviso") }, text = { Text(erro) },
            confirmButton = { TextButton(onClick = { erro = "" }) { Text("OK") } }
        )
    }
    val restauranteParaExcluir = excluirId?.let { foodLog.restaurantePorId(it) }
    if (restauranteParaExcluir != null) {
        AlertDialog(
            onDismissRequest = { excluirId = null },
            title = { Text("Remover restaurante?") },
            text = { Text("${restauranteParaExcluir.nome} e suas avaliações também serão removidos.") },
            confirmButton = {
                TextButton(onClick = {
                    if (!excluindo) {
                        excluindo = true
                        foodLog.removerRestaurante(restauranteParaExcluir.id) { sucesso ->
                            excluindo = false
                            if (sucesso) excluirId = null
                            else erro = "Não foi possível remover o restaurante."
                        }
                    }
                }, enabled = !excluindo) {
                    Text("Remover")
                }
            },
            dismissButton = { TextButton(onClick = { excluirId = null }) { Text("Cancelar") } }
        )
    }
}
