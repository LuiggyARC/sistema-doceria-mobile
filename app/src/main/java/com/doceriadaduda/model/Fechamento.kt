package com.doceriadaduda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fechamentos")
data class Fechamento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyId: Int = 1,
    val data: String, // Usar String para DATE ou converter para LocalDate
    val qtdVendas: Int,
    val faturamento: Double,
    val despesas: Double,
    val saldo: Double,
    val horaFechamento: String?, // Usar String para TIMESTAMP ou converter para LocalDateTime
    val observacao: String?
)
