package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.FaturamentoQtdMes
import com.doceriadaduda.data.local.local.dao.MediaVendasDiaSemana
import com.doceriadaduda.data.local.local.dao.TopVendidoDia
import com.doceriadaduda.data.local.local.dao.TopVendidoMes
import com.doceriadaduda.data.local.local.dao.VendaDao
import com.doceriadaduda.data.local.local.dao.VendasPorCategoria
import com.doceriadaduda.data.local.local.dao.VendasPorFormaPagamento
import com.doceriadaduda.model.Venda
import kotlinx.coroutines.flow.Flow

class VendaRepository(private val vendaDao: VendaDao) {

    suspend fun insert(venda: Venda) = vendaDao.insert(venda)

    fun getVendasHoje(today: String, companyId: Int): Flow<List<Venda>> = vendaDao.getVendasHoje(today, companyId)

    fun getVendasTotalHoje(today: String, companyId: Int): Flow<Double?> = vendaDao.getVendasTotalHoje(today, companyId)

    fun getVendasTotalMes(month: String, companyId: Int): Flow<Double?> = vendaDao.getVendasTotalMes(month, companyId)

    fun getQtdVendasHoje(today: String, companyId: Int): Flow<Int> = vendaDao.getQtdVendasHoje(today, companyId)

    fun getTaxaCartaoHoje(today: String, companyId: Int): Flow<Double?> = vendaDao.getTaxaCartaoHoje(today, companyId)

    fun getTopVendidosDia(today: String, companyId: Int): Flow<List<TopVendidoDia>> = vendaDao.getTopVendidosDia(today, companyId)

    fun getVendasPorFormaPagamentoMes(month: String, companyId: Int): Flow<List<VendasPorFormaPagamento>> = vendaDao.getVendasPorFormaPagamentoMes(month, companyId)

    fun getTopVendidosMes(month: String, companyId: Int): Flow<List<TopVendidoMes>> = vendaDao.getTopVendidosMes(month, companyId)

    fun getMediaVendasPorDiaSemana(companyId: Int): Flow<List<MediaVendasDiaSemana>> = vendaDao.getMediaVendasPorDiaSemana(companyId)

    fun getFaturamentoQtdMes(month: String, companyId: Int): Flow<FaturamentoQtdMes> = vendaDao.getFaturamentoQtdMes(month, companyId)

    fun getTaxaCartaoMes(month: String, companyId: Int): Flow<Double?> = vendaDao.getTaxaCartaoMes(month, companyId)

    fun getFaturamentoQtdMesAnterior(previousMonth: String, companyId: Int): Flow<FaturamentoQtdMes> = vendaDao.getFaturamentoQtdMesAnterior(previousMonth, companyId)

    fun getVendasPorCategoriaMes(month: String, companyId: Int): Flow<List<VendasPorCategoria>> = vendaDao.getVendasPorCategoriaMes(month, companyId)

    fun getLucroEstimadoMes(month: String, companyId: Int): Flow<Double?> = vendaDao.getLucroEstimadoMes(month, companyId)

    fun getLucroEstimadoHoje(today: String, companyId: Int): Flow<Double?> = vendaDao.getLucroEstimadoHoje(today, companyId)

    suspend fun getVendasNaoSincronizadas(companyId: Int) = vendaDao.getVendasNaoSincronizadas(companyId)

    suspend fun marcarComoSincronizado(ids: List<Int>) = vendaDao.marcarComoSincronizado(ids)
}
