package com.example.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurantes")
data class Restaurante(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val categoria: String,
    val cidade: String,
    val descricao: String
)
