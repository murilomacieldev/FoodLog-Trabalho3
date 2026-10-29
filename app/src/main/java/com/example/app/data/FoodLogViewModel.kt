package com.example.app.data

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.app.model.Restaurante
import com.example.app.model.Review
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class FoodLogViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val restauranteDao = database.restauranteDao()
    private val reviewDao = database.reviewDao()
    private val escopoBanco = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutexBanco = Mutex()

    val restaurantes = mutableStateListOf<Restaurante>()
    val reviews = mutableStateListOf<Review>()

    var restauranteDetalhe by mutableStateOf<Restaurante?>(null)
        private set
    var reviewDetalhe by mutableStateOf<Review?>(null)
        private set
    var carregandoRestaurante by mutableStateOf(false)
        private set
    var carregandoReview by mutableStateOf(false)
        private set
    var erroBanco by mutableStateOf<String?>(null)
        private set

    private var idDetalheRestaurante: Int? = null
    private var idDetalheReview: Int? = null

    init {
        carregarDados()
    }

    fun limparErro() {
        erroBanco = null
    }

    private suspend fun atualizarListasDoBanco() {
        // Chamado somente em Dispatchers.IO e protegido por mutexBanco.
        val restaurantesDoBanco = restauranteDao.buscarTodos()
        val reviewsDoBanco = reviewDao.buscarTodos()
        withContext(Dispatchers.Main) {
            restaurantes.clear()
            restaurantes.addAll(restaurantesDoBanco)
            reviews.clear()
            reviews.addAll(reviewsDoBanco)
            // Mantém detalhes abertos atualizados depois de uma edição ou remoção.
            idDetalheRestaurante?.let { id ->
                restauranteDetalhe = restaurantesDoBanco.firstOrNull { it.id == id }
            }
            idDetalheReview?.let { id ->
                reviewDetalhe = reviewsDoBanco.firstOrNull { it.id == id }
            }
        }
    }

    fun carregarDados() {
        escopoBanco.launch {
            mutexBanco.withLock {
                try {
                    atualizarListasDoBanco()
                } catch (e: Exception) {
                    registrarErro("Erro ao buscar dados", e)
                }
            }
        }
    }

    private suspend fun registrarErro(contexto: String, erro: Exception) {
        Log.e("FoodLogRoom", contexto, erro)
        withContext(Dispatchers.Main) {
            erroBanco = "$contexto. Confira o Logcat."
        }
    }

    private fun executar(
        descricao: String,
        onConcluido: (Boolean) -> Unit,
        operacao: suspend () -> Unit
    ) {
        escopoBanco.launch {
            mutexBanco.withLock {
                try {
                    operacao()
                    atualizarListasDoBanco()
                    withContext(Dispatchers.Main) {
                        erroBanco = null
                        onConcluido(true)
                    }
                } catch (e: Exception) {
                    registrarErro(descricao, e)
                    withContext(Dispatchers.Main) { onConcluido(false) }
                }
            }
        }
    }

    fun adicionarRestaurante(
        nome: String, categoria: String, cidade: String, descricao: String,
        onConcluido: (Boolean) -> Unit = {}
    ) = executar("Erro ao inserir restaurante", onConcluido) {
        restauranteDao.inserir(
            Restaurante(
                nome = nome.trim(), categoria = categoria.trim(),
                cidade = cidade.trim(), descricao = descricao.trim()
            )
        )
    }

    fun atualizarRestaurante(
        id: Int, nome: String, categoria: String, cidade: String, descricao: String,
        onConcluido: (Boolean) -> Unit = {}
    ) = executar("Erro ao editar restaurante", onConcluido) {
        val anterior = restauranteDao.buscarPorId(id)
            ?: error("Restaurante $id não encontrado")
        restauranteDao.atualizar(
            anterior.copy(
                nome = nome.trim(), categoria = categoria.trim(),
                cidade = cidade.trim(), descricao = descricao.trim()
            )
        )
    }

    fun removerRestaurante(id: Int, onConcluido: (Boolean) -> Unit = {}) =
        executar("Erro ao remover restaurante", onConcluido) {
            val anterior = restauranteDao.buscarPorId(id)
                ?: error("Restaurante $id não encontrado")
            // A chave estrangeira com CASCADE remove reviews ligadas ao restaurante.
            restauranteDao.deletar(anterior)
        }

    fun adicionarReview(
        restauranteId: Int, prato: String, nota: Int, preco: Double, comentario: String,
        onConcluido: (Boolean) -> Unit = {}
    ) = executar("Erro ao inserir avaliação", onConcluido) {
        check(restauranteDao.buscarPorId(restauranteId) != null) {
            "O restaurante da avaliação não existe"
        }
        reviewDao.inserir(
            Review(
                restauranteId = restauranteId, prato = prato.trim(), nota = nota,
                preco = preco, comentario = comentario.trim()
            )
        )
    }

    fun atualizarReview(
        id: Int, prato: String, nota: Int, preco: Double, comentario: String,
        onConcluido: (Boolean) -> Unit = {}
    ) = executar("Erro ao editar avaliação", onConcluido) {
        val anterior = reviewDao.buscarPorId(id)
            ?: error("Avaliação $id não encontrada")
        reviewDao.atualizar(
            anterior.copy(prato = prato.trim(), nota = nota, preco = preco, comentario = comentario.trim())
        )
    }

    fun removerReview(id: Int, onConcluido: (Boolean) -> Unit = {}) =
        executar("Erro ao remover avaliação", onConcluido) {
            val anterior = reviewDao.buscarPorId(id)
                ?: error("Avaliação $id não encontrada")
            reviewDao.deletar(anterior)
        }

    fun carregarDetalheRestaurante(id: Int) {
        idDetalheRestaurante = id
        restauranteDetalhe = null
        carregandoRestaurante = true
        escopoBanco.launch {
            mutexBanco.withLock {
                try {
                    val item = restauranteDao.buscarPorId(id)
                    withContext(Dispatchers.Main) {
                        if (idDetalheRestaurante == id) restauranteDetalhe = item
                    }
                } catch (e: Exception) {
                    registrarErro("Erro ao buscar restaurante por ID", e)
                } finally {
                    withContext(Dispatchers.Main) { if (idDetalheRestaurante == id) carregandoRestaurante = false }
                }
            }
        }
    }

    fun carregarDetalheReview(id: Int) {
        idDetalheReview = id
        reviewDetalhe = null
        carregandoReview = true
        escopoBanco.launch {
            mutexBanco.withLock {
                try {
                    val item = reviewDao.buscarPorId(id)
                    withContext(Dispatchers.Main) {
                        if (idDetalheReview == id) reviewDetalhe = item
                    }
                } catch (e: Exception) {
                    registrarErro("Erro ao buscar review por ID", e)
                } finally {
                    withContext(Dispatchers.Main) { if (idDetalheReview == id) carregandoReview = false }
                }
            }
        }
    }

    fun restaurantePorId(id: Int) = restaurantes.find { it.id == id }
    fun reviewPorId(id: Int) = reviews.find { it.id == id }
    fun reviewsDoRestaurante(id: Int) = reviews.filter { it.restauranteId == id }
    fun mediaDoRestaurante(id: Int): Double? =
        reviewsDoRestaurante(id).takeIf { it.isNotEmpty() }?.map { it.nota }?.average()

    override fun onCleared() {
        escopoBanco.cancel()
        super.onCleared()
    }
}
