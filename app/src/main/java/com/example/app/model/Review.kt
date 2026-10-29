package com.example.app.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    foreignKeys = [ForeignKey(
        entity = Restaurante::class,
        parentColumns = ["id"],
        childColumns = ["restauranteId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["restauranteId"])]
)
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restauranteId: Int,
    val prato: String,
    val nota: Int,
    val preco: Double,
    val comentario: String
)
