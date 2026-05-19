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

    @Query("SELECT descricao, categoria, valor, data FROM despesas ORDER BY data DESC LIMIT 10")
    fun getUltimasDespesas(): Flow<List<DespesaResumo>>

    @Query("SELECT SUM(valor) FROM despesas WHERE data = :today")
    fun getDespesasTotalHoje(today: String): Flow<Double?>

    @Query("SELECT SUM(valor) FROM despesas WHERE STRFTIME(\"%Y-%m\", data) = :month")
    fun getDespesasTotalMes(month: String): Flow<Double?>

    @Query("SELECT SUM(valor) FROM despesas WHERE data >= DATE(\'now\', \'-60 days\')")
    fun getDespesasTotalUltimos60Dias(): Flow<Double?>

    @Query("SELECT categoria, SUM(valor) as valor FROM despesas WHERE STRFTIME(\"%Y-%m\", data) = :month GROUP BY categoria ORDER BY valor DESC")
    fun getDespesasPorCategoriaMes(month: String): Flow<List<DespesaPorCategoria>>

    @Query("SELECT SUM(valor) FROM despesas WHERE STRFTIME(\"%Y-%m\", data) = :previousMonth")
    fun getDespesasTotalMesAnterior(previousMonth: String): Flow<Double?>
}

data class DespesaResumo(val descricao: String, val categoria: String?, val valor: Double, val data: String)
data class DespesaPorCategoria(val categoria: String, val valor: Double)
