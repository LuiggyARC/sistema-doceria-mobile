package com.doceriadaduda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "despesas")
data class Despesa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyId: Int = 1,
    val descricao: String,
    val categoria: String?,
    val tipo: String?,
    val valor: Double,
    val data: String, // Usar String para DATE ou converter para LocalDate
    val sincronizado: Boolean = false
)
