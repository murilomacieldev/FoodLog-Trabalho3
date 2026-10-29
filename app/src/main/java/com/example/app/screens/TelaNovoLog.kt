package com.example.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.components.TituloSecao
import com.example.app.data.FoodLogViewModel
import com.example.app.ui.theme.LaranjaFoodLog
import com.example.app.ui.theme.RosaSuaveFoodLog

@Composable
fun TelaNovoLog(
    foodLog: FoodLogViewModel,
    restauranteInicial: Int,
    onBuscarRestaurante: () -> Unit,
    onPublicado: () -> Unit
) {
    var prato by remember(restauranteInicial) { mutableStateOf("") }
    var restauranteId by remember(restauranteInicial) {
        mutableIntStateOf(if (foodLog.restaurantePorId(restauranteInicial) != null) restauranteInicial else foodLog.restaurantes.firstOrNull()?.id ?: -1)
    }
    var menuExpandido by remember { mutableStateOf(false) }
    var nota by remember { mutableIntStateOf(0) }
    var preco by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf("") }
    var salvando by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { foodLog.carregarDados() }
    LaunchedEffect(restauranteInicial, foodLog.restaurantes.size) {
        if (foodLog.restaurantePorId(restauranteInicial) != null) {
            restauranteId = restauranteInicial
        } else if (foodLog.restaurantePorId(restauranteId) == null) {
            restauranteId = foodLog.restaurantes.firstOrNull()?.id ?: -1
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TituloSecao("Novo Log", "Registre sua experiência gastronômica")
        Box(
            Modifier.fillMaxWidth().height(116.dp).clip(RoundedCornerShape(16.dp)).background(RosaSuaveFoodLog),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = LaranjaFoodLog, modifier = Modifier.size(40.dp))
                Text("Conte como foi sua refeição", color = LaranjaFoodLog)
            }
        }
        if (foodLog.restaurantes.isEmpty()) {
            Text("Cadastre um restaurante antes de publicar uma avaliação.")
            Button(onClick = onBuscarRestaurante, modifier = Modifier.fillMaxWidth()) { Text("Cadastrar restaurante") }
        } else {
            Text("Restaurante", style = MaterialTheme.typography.titleSmall)
            Box {
                OutlinedButton(onClick = { menuExpandido = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(foodLog.restaurantePorId(restauranteId)?.nome ?: "Escolha um restaurante")
                }
                DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }) {
                    foodLog.restaurantes.forEach { restaurante ->
                        DropdownMenuItem(
                            text = { Text(restaurante.nome) },
                            onClick = { restauranteId = restaurante.id; menuExpandido = false }
                        )
                    }
                }
            }
            OutlinedTextField(prato, { prato = it }, label = { Text("Nome do prato") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Text("Sua nota", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                (1..5).forEach { indice ->
                    IconButton(onClick = { nota = indice }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Star, contentDescription = "$indice estrelas", modifier = Modifier.size(32.dp), tint = if (indice <= nota) LaranjaFoodLog else androidx.compose.ui.graphics.Color.LightGray)
                    }
                }
            }
            OutlinedTextField(
                value = preco, onValueChange = { preco = it },
                label = { Text("Preço pago") }, prefix = { Text("R$ ") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = comentario, onValueChange = { comentario = it },
                label = { Text("Comentário") }, modifier = Modifier.fillMaxWidth(), minLines = 3
            )
            if (erro.isNotBlank()) Text(erro, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            Button(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                onClick = {
                    val precoNumero = preco.replace(',', '.').toDoubleOrNull()
                    erro = when {
                        foodLog.restaurantePorId(restauranteId) == null -> "Selecione um restaurante válido."
                        prato.isBlank() -> "Informe o nome do prato."
                        nota !in 1..5 -> "Escolha sua nota de 1 a 5 estrelas."
                        precoNumero == null || !precoNumero.isFinite() || precoNumero <= 0.0 -> "Informe um preço válido maior que zero."
                        comentario.isBlank() -> "Escreva um comentário."
                        else -> ""
                    }
                    if (erro.isEmpty() && precoNumero != null) {
                        if (!salvando) {
                            salvando = true
                            foodLog.adicionarReview(restauranteId, prato, nota, precoNumero, comentario) { sucesso ->
                                salvando = false
                                if (sucesso) {
                                    prato = ""; nota = 0; preco = ""; comentario = ""
                                    onPublicado()
                                } else erro = "Erro ao salvar a review no banco."
                            }
                        }
                    }
                },
                enabled = !salvando
            ) { Text(if (salvando) "Publicando..." else "Publicar Review") }
        }
    }
}
