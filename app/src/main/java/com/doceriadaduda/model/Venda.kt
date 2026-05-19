package com.doceriadaduda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendas")
data class Venda(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val produtoId: Int,
    val quantidade: Int,
    val precoUnitarioHistorico: Double,
    val valorTotal: Double,
    val formaPagamento: String?,
    val taxaCartao: Double,
    val valorLiquido: Double,
    val dataVenda: String // Usar String para TIMESTAMP ou converter para LocalDateTime
)
