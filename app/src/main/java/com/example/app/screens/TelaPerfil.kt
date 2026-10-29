package com.example.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app.components.TituloSecao
import com.example.app.components.notaTexto
import com.example.app.data.FoodLogViewModel
import com.example.app.ui.theme.LaranjaFoodLog
import com.example.app.ui.theme.RosaSuaveFoodLog

@Composable
fun TelaPerfil(foodLog: FoodLogViewModel, onHistorico: () -> Unit, onNovoLog: () -> Unit) {
    LaunchedEffect(Unit) { foodLog.carregarDados() }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        TituloSecao("Meu Perfil", "Resumo das suas experiências")
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(70.dp).clip(CircleShape).background(RosaSuaveFoodLog), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = LaranjaFoodLog, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Explorador gastronômico", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Perfil local de demonstração", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Estatistica("Reviews", foodLog.reviews.size.toString(), Modifier.weight(1f))
            Estatistica("Restaurantes", foodLog.restaurantes.size.toString(), Modifier.weight(1f))
        }
        Estatistica("Minha nota média", if (foodLog.reviews.isEmpty()) "—" else "★ ${notaTexto(foodLog.reviews.map { it.nota }.average())}", Modifier.fillMaxWidth())
        TituloSecao("Suas atividades", "Acompanhe os pratos que você já avaliou")
        Button(onClick = onHistorico, modifier = Modifier.fillMaxWidth()) { Text("Ver histórico de reviews") }
        OutlinedButton(onClick = onNovoLog, modifier = Modifier.fillMaxWidth()) { Text("Criar nova avaliação") }
        Text("Restaurantes e avaliações são salvos localmente com Room.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
private fun Estatistica(titulo: String, valor: String, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = LaranjaFoodLog)
            Text(titulo, style = MaterialTheme.typography.bodySmall)
        }
    }
}
