package com.example.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.model.Restaurante
import com.example.app.model.Review
import com.example.app.ui.theme.LaranjaFoodLog
import com.example.app.ui.theme.RosaSuaveFoodLog
import com.example.app.ui.theme.TextoSecundarioFoodLog
import java.util.Locale

fun dinheiro(valor: Double): String = "R$ %.2f".format(Locale("pt", "BR"), valor)
fun notaTexto(valor: Double): String = "%.1f".format(Locale("pt", "BR"), valor)

@Composable
fun TituloSecao(titulo: String, subtitulo: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        if (subtitulo != null) Text(subtitulo, color = TextoSecundarioFoodLog, fontSize = 13.sp)
    }
}

@Composable
fun RestauranteCard(
    restaurante: Restaurante,
    media: Double?,
    quantidade: Int,
    onAbrir: () -> Unit,
    onRemover: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onAbrir),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(RosaSuaveFoodLog),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Restaurant, contentDescription = null, tint = LaranjaFoodLog, modifier = Modifier.size(30.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(restaurante.nome, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${restaurante.categoria} • ${restaurante.cidade}", fontSize = 12.sp, color = TextoSecundarioFoodLog, maxLines = 1)
                Text(if (media == null) "Sem avaliações" else "★ ${notaTexto(media)} • $quantidade avaliações", color = LaranjaFoodLog, fontSize = 13.sp)
            }
            if (onRemover != null) {
                IconButton(onClick = onRemover) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover ${restaurante.nome}", tint = Color(0xFFB94A40))
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    restauranteNome: String,
    onAbrir: () -> Unit,
    onRemover: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onAbrir),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(review.prato, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(restauranteNome, color = LaranjaFoodLog, fontSize = 13.sp)
                }
                if (onRemover != null) {
                    IconButton(onClick = onRemover) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover review de ${review.prato}", tint = Color(0xFFB94A40))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("★ ${review.nota}/5", color = LaranjaFoodLog, fontWeight = FontWeight.SemiBold)
                Text(dinheiro(review.preco), fontWeight = FontWeight.Medium)
            }
            Text(review.comentario, fontSize = 13.sp, color = TextoSecundarioFoodLog, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Text("Toque para ver detalhes", color = LaranjaFoodLog, fontSize = 12.sp)
        }
    }
}
