package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.FechamentoDao
import com.doceriadaduda.data.local.local.dao.FechamentoResumo
import com.doceriadaduda.model.Fechamento
import kotlinx.coroutines.flow.Flow

class FechamentoRepository(private val fechamentoDao: FechamentoDao) {

    suspend fun insert(fechamento: Fechamento) = fechamentoDao.insert(fechamento)

    suspend fun getFechamentoByData(dataStr: String, companyId: Int): Fechamento? = fechamentoDao.getFechamentoByData(dataStr, companyId)

    fun getFechamentosRecentes(companyId: Int): Flow<List<FechamentoResumo>> = fechamentoDao.getFechamentosRecentes(companyId)
}
