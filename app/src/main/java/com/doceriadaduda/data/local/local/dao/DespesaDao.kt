package com.doceriadaduda.data.local.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.doceriadaduda.model.Despesa
import kotlinx.coroutines.flow.Flow

@Dao
interface DespesaDao {
    @Insert
    suspend fun insert(despesa: Despesa)

    @Query("SELECT descricao, categoria, valor, data FROM despesas WHERE companyId = :companyId ORDER BY data DESC LIMIT 10")
    fun getUltimasDespesas(companyId: Int): Flow<List<DespesaResumo>>

    @Query("SELECT SUM(valor) FROM despesas WHERE companyId = :companyId AND data = :today")
    fun getDespesasTotalHoje(today: String, companyId: Int): Flow<Double?>

    @Query("SELECT SUM(valor) FROM despesas WHERE companyId = :companyId AND STRFTIME(\"%Y-%m\", data) = :month")
    fun getDespesasTotalMes(month: String, companyId: Int): Flow<Double?>

    @Query("SELECT SUM(valor) FROM despesas WHERE companyId = :companyId AND data >= DATE(\'now\', \'-60 days\')")
    fun getDespesasTotalUltimos60Dias(companyId: Int): Flow<Double?>

    @Query("SELECT categoria, SUM(valor) as valor FROM despesas WHERE companyId = :companyId AND STRFTIME(\"%Y-%m\", data) = :month GROUP BY categoria ORDER BY valor DESC")
    fun getDespesasPorCategoriaMes(month: String, companyId: Int): Flow<List<DespesaPorCategoria>>

    @Query("SELECT SUM(valor) FROM despesas WHERE companyId = :companyId AND STRFTIME(\"%Y-%m\", data) = :previousMonth")
    fun getDespesasTotalMesAnterior(previousMonth: String, companyId: Int): Flow<Double?>

    @Query("SELECT * FROM despesas WHERE companyId = :companyId AND sincronizado = 0")
    suspend fun getDespesasNaoSincronizadas(companyId: Int): List<Despesa>

    @Query("UPDATE despesas SET sincronizado = 1 WHERE id IN (:ids)")
    suspend fun marcarComoSincronizado(ids: List<Int>)
}

data class DespesaResumo(val descricao: String, val categoria: String?, val valor: Double, val data: String)
data class DespesaPorCategoria(val categoria: String, val valor: Double)
