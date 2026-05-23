package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.DespesaDao
import com.doceriadaduda.data.local.local.dao.DespesaPorCategoria
import com.doceriadaduda.data.local.local.dao.DespesaResumo
import com.doceriadaduda.model.Despesa
import kotlinx.coroutines.flow.Flow

class DespesaRepository(private val despesaDao: DespesaDao) {

    suspend fun insert(despesa: Despesa) = despesaDao.insert(despesa)

    fun getUltimasDespesas(companyId: Int): Flow<List<DespesaResumo>> = despesaDao.getUltimasDespesas(companyId)

    fun getDespesasTotalHoje(today: String, companyId: Int): Flow<Double?> = despesaDao.getDespesasTotalHoje(today, companyId)

    fun getDespesasTotalMes(month: String, companyId: Int): Flow<Double?> = despesaDao.getDespesasTotalMes(month, companyId)

    fun getDespesasTotalUltimos60Dias(companyId: Int): Flow<Double?> = despesaDao.getDespesasTotalUltimos60Dias(companyId)

    fun getDespesasPorCategoriaMes(month: String, companyId: Int): Flow<List<DespesaPorCategoria>> = despesaDao.getDespesasPorCategoriaMes(month, companyId)

    fun getDespesasTotalMesAnterior(previousMonth: String, companyId: Int): Flow<Double?> = despesaDao.getDespesasTotalMesAnterior(previousMonth, companyId)
}
