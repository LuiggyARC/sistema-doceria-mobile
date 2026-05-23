package com.doceriadaduda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "funcionarios")
data class Funcionario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyId: Int,
    val nome: String,
    val cargo: String,
    val email: String,
    val ativo: Boolean = true
)
