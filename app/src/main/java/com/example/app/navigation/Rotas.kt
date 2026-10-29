package com.example.app.navigation

object Rotas {
    const val HOME = "home"
    const val BUSCA = "busca"
    const val NOVO_LOG = "novo_log"
    const val PERFIL = "perfil"
    const val HISTORICO = "historico"
    const val DETALHE_RESTAURANTE = "detalhe_restaurante/{id}"
    const val DETALHE_REVIEW = "detalhe_review/{id}"


    const val NOVO_LOG_COM_RESTAURANTE = "novo_log?restauranteId={restauranteId}"

    fun restaurante(id: Int) = "detalhe_restaurante/$id"
    fun review(id: Int) = "detalhe_review/$id"
    fun novoLogDoRestaurante(id: Int) = "novo_log?restauranteId=$id"
}
