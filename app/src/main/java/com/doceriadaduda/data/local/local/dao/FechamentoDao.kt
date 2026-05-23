package com.doceriadaduda.data.local.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.doceriadaduda.model.Fechamento
import kotlinx.coroutines.flow.Flow

@Dao
interface FechamentoDao {
    @Insert
    suspend fun insert(fechamento: Fechamento)

    @Query("SELECT * FROM fechamentos WHERE companyId = :companyId AND data = :dataStr")
    suspend fun getFechamentoByData(dataStr: String, companyId: Int): Fechamento?

    @Query("SELECT data, qtdVendas, faturamento, despesas, saldo FROM fechamentos WHERE companyId = :companyId ORDER BY data DESC LIMIT 10")
    fun getFechamentosRecentes(companyId: Int): Flow<List<FechamentoResumo>>
}

data class FechamentoResumo(val data: String, val qtdVendas: Int, val faturamento: Double, val despesas: Double, val saldo: Double)
