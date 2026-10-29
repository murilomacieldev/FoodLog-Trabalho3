package com.example.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.app.data.FoodLogViewModel
import com.example.app.screens.*
import com.example.app.ui.theme.FundoFoodLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(foodLog: FoodLogViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route
    val principais = setOf(Rotas.HOME, Rotas.BUSCA, Rotas.NOVO_LOG_COM_RESTAURANTE, Rotas.PERFIL)

    fun navegarPrincipal(rota: String) {
        navController.navigate(rota) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = FundoFoodLog,
        topBar = {
            val titulo = when (rotaAtual) {
                Rotas.HISTORICO -> "Histórico de Reviews"
                Rotas.DETALHE_RESTAURANTE -> "Restaurante"
                Rotas.DETALHE_REVIEW -> "Detalhes da Review"
                else -> null
            }
            if (titulo != null) {
                TopAppBar(
                    title = { Text(titulo) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = FundoFoodLog)
                )
            }
        },
        bottomBar = {
            if (rotaAtual in principais) {
                NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
                    listOf(
                        Triple(Rotas.HOME, "Home", Icons.Default.Home),
                        Triple(Rotas.BUSCA, "Busca", Icons.Default.Search),
                        Triple(Rotas.NOVO_LOG, "Novo Log", Icons.Default.AddCircle),
                        Triple(Rotas.PERFIL, "Perfil", Icons.Default.Person)
                    ).forEach { (rota, titulo, icone) ->
                        NavigationBarItem(
                            selected = rotaAtual == rota || (rota == Rotas.NOVO_LOG && rotaAtual == Rotas.NOVO_LOG_COM_RESTAURANTE),
                            onClick = { navegarPrincipal(rota) },
                            icon = { Icon(icone, contentDescription = titulo) },
                            label = { Text(titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rotas.HOME) {
                TelaHome(
                    foodLog = foodLog,
                    onBusca = { navegarPrincipal(Rotas.BUSCA) },
                    onNovoLog = { navegarPrincipal(Rotas.NOVO_LOG) },
                    onReview = { navController.navigate(Rotas.review(it)) },
                    onRestaurante = { navController.navigate(Rotas.restaurante(it)) }
                )
            }
            composable(Rotas.BUSCA) {
                TelaBusca(foodLog = foodLog, onRestaurante = { navController.navigate(Rotas.restaurante(it)) })
            }
            composable(
                route = Rotas.NOVO_LOG_COM_RESTAURANTE,
                arguments = listOf(navArgument("restauranteId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { entry ->
                TelaNovoLog(
                    foodLog = foodLog,
                    restauranteInicial = entry.arguments?.getInt("restauranteId") ?: -1,
                    onBuscarRestaurante = { navegarPrincipal(Rotas.BUSCA) },
                    onPublicado = { navController.navigate(Rotas.HISTORICO) }
                )
            }
            composable(Rotas.PERFIL) {
                TelaPerfil(
                    foodLog = foodLog,
                    onHistorico = { navController.navigate(Rotas.HISTORICO) },
                    onNovoLog = { navegarPrincipal(Rotas.NOVO_LOG) }
                )
            }
            composable(Rotas.HISTORICO) {
                TelaHistorico(
                    foodLog = foodLog,
                    onNovaReview = { navegarPrincipal(Rotas.NOVO_LOG) },
                    onReview = { navController.navigate(Rotas.review(it)) }
                )
            }
            composable(
                route = Rotas.DETALHE_RESTAURANTE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("id") ?: -1
                TelaDetalheRestaurante(
                    foodLog = foodLog,
                    id = id,
                    onNovaReview = { navController.navigate(Rotas.novoLogDoRestaurante(id)) },
                    onReview = { navController.navigate(Rotas.review(it)) },
                    onVoltar = { navController.popBackStack() }
                )
            }
            composable(
                route = Rotas.DETALHE_REVIEW,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                TelaDetalheReview(
                    foodLog = foodLog,
                    id = entry.arguments?.getInt("id") ?: -1,
                    onRestaurante = { navController.navigate(Rotas.restaurante(it)) },
                    onVoltar = { navController.popBackStack() }
                )
            }
        }
    }
}
