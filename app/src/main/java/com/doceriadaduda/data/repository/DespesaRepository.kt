package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.DespesaDao
import com.doceriadaduda.data.local.local.dao.DespesaPorCategoria
import com.doceriadaduda.data.local.local.dao.DespesaResumo
import com.doceriadaduda.model.Despesa
import kotlinx.coroutines.flow.Flow

class DespesaRepository(private val despesaDao: DespesaDao) {

    suspend fun insert(despesa: Despesa) = despesaDao.insert(despesa)

    fun getUltimasDespesas(): Flow<List<DespesaResumo>> = despesaDao.getUltimasDespesas()

    fun getDespesasTotalHoje(today: String): Flow<Double?> = despesaDao.getDespesasTotalHoje(today)

    fun getDespesasTotalMes(month: String): Flow<Double?> = despesaDao.getDespesasTotalMes(month)

    fun getDespesasTotalUltimos60Dias(): Flow<Double?> = despesaDao.getDespesasTotalUltimos60Dias()

    fun getDespesasPorCategoriaMes(month: String): Flow<List<DespesaPorCategoria>> = despesaDao.getDespesasPorCategoriaMes(month)

    fun getDespesasTotalMesAnterior(previousMonth: String): Flow<Double?> = despesaDao.getDespesasTotalMesAnterior(previousMonth)
}
