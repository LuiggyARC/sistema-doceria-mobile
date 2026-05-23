package com.doceriadaduda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empresas")
data class Empresa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val email: String,
    val senhaHash: String,
    val logoPath: String? = null,
    val isAdmin: Boolean = false,
    val ativa: Boolean = true
)
