package com.example.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app.model.Review

@Dao
interface ReviewDao {
    @Insert
    suspend fun inserir(review: Review): Long

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    suspend fun buscarTodos(): List<Review>

    @Query("SELECT * FROM reviews WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Review?

    @Query("SELECT * FROM reviews WHERE restauranteId = :restauranteId ORDER BY id DESC")
    suspend fun buscarPorRestaurante(restauranteId: Int): List<Review>

    @Update
    suspend fun atualizar(review: Review)

    @Delete
    suspend fun deletar(review: Review)
}
