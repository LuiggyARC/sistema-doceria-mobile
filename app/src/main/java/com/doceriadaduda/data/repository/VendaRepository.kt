package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.FaturamentoQtdMes
import com.doceriadaduda.data.local.local.dao.MediaVendasDiaSemana
import com.doceriadaduda.data.local.local.dao.TopVendidoDia
import com.doceriadaduda.data.local.local.dao.TopVendidoMes
import com.doceriadaduda.data.local.local.dao.VendaDao
import com.doceriadaduda.data.local.local.dao.VendasPorFormaPagamento
import com.doceriadaduda.model.Venda
import kotlinx.coroutines.flow.Flow

class VendaRepository(private val vendaDao: VendaDao) {

    suspend fun insert(venda: Venda) = vendaDao.insert(venda)

    fun getVendasHoje(today: String): Flow<List<Venda>> = vendaDao.getVendasHoje(today)

    fun getVendasTotalHoje(today: String): Flow<Double?> = vendaDao.getVendasTotalHoje(today)

    fun getVendasTotalMes(month: String): Flow<Double?> = vendaDao.getVendasTotalMes(month)

    fun getQtdVendasHoje(today: String): Flow<Int> = vendaDao.getQtdVendasHoje(today)

    fun getTaxaCartaoHoje(today: String): Flow<Double?> = vendaDao.getTaxaCartaoHoje(today)

    fun getTopVendidosDia(today: String): Flow<List<TopVendidoDia>> = vendaDao.getTopVendidosDia(today)

    fun getVendasPorFormaPagamentoMes(month: String): Flow<List<VendasPorFormaPagamento>> = vendaDao.getVendasPorFormaPagamentoMes(month)

    fun getTopVendidosMes(month: String): Flow<List<TopVendidoMes>> = vendaDao.getTopVendidosMes(month)

    fun getMediaVendasPorDiaSemana(): Flow<List<MediaVendasDiaSemana>> = vendaDao.getMediaVendasPorDiaSemana()

    fun getFaturamentoQtdMes(month: String): Flow<FaturamentoQtdMes> = vendaDao.getFaturamentoQtdMes(month)

    fun getTaxaCartaoMes(month: String): Flow<Double?> = vendaDao.getTaxaCartaoMes(month)

    fun getFaturamentoQtdMesAnterior(previousMonth: String): Flow<FaturamentoQtdMes> = vendaDao.getFaturamentoQtdMesAnterior(previousMonth)
}
