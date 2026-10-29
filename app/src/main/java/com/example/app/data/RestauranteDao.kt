package com.example.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app.model.Restaurante

@Dao
interface RestauranteDao {
    @Insert
    suspend fun inserir(restaurante: Restaurante): Long

    @Query("SELECT * FROM restaurantes ORDER BY id DESC")
    suspend fun buscarTodos(): List<Restaurante>

    @Query("SELECT * FROM restaurantes WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Restaurante?

    @Update
    suspend fun atualizar(restaurante: Restaurante)

    @Delete
    suspend fun deletar(restaurante: Restaurante)
}
