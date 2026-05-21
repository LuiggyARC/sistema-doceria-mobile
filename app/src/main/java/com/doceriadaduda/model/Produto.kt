package com.doceriadaduda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class Produto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val categoria: String?,
    val precoCusto: Double = 0.0,
    val precoVenda: Double,
    val quantidadeEstoque: Int,
    val estoqueMinimo: Int,
    val ativo: Boolean
)
