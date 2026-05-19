package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.repository.DespesaRepository
import com.doceriadaduda.data.repository.FechamentoRepository
import com.doceriadaduda.data.repository.ProdutoRepository
import com.doceriadaduda.data.repository.VendaRepository
import com.doceriadaduda.data.local.local.dao.TopVendidoDia
import com.doceriadaduda.model.Fechamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DashboardViewModel(
    private val produtoRepository: ProdutoRepository,
    private val vendaRepository: VendaRepository,
    private val despesaRepository: DespesaRepository,
    private val fechamentoRepository: FechamentoRepository
) : ViewModel() {

    private val _vendasHoje = MutableStateFlow(0.0)
    val vendasHoje: StateFlow<Double> = _vendasHoje

    private val _vendasMes = MutableStateFlow(0.0)
    val vendasMes: StateFlow<Double> = _vendasMes

    private val _estoqueBaixo = MutableStateFlow(0)
    val estoqueBaixo: StateFlow<Int> = _estoqueBaixo

    private val _despesasHoje = MutableStateFlow(0.0)
    val despesasHoje: StateFlow<Double> = _despesasHoje

    private val _qtdVendasHoje = MutableStateFlow(0)
    val qtdVendasHoje: StateFlow<Int> = _qtdVendasHoje

    private val _taxaCartaoHoje = MutableStateFlow(0.0)
    val taxaCartaoHoje: StateFlow<Double> = _taxaCartaoHoje

    private val _topVendidosDia = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val topVendidosDia: StateFlow<List<Pair<String, Int>>> = _topVendidosDia

    private val _caixaFechadoHoje = MutableStateFlow(false)
    val caixaFechadoHoje: StateFlow<Boolean> = _caixaFechadoHoje

    init {
        loadDashboardStats()
    }

    private fun loadDashboardStats() {
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
            val month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

            val flowList = listOf(
                vendaRepository.getVendasTotalHoje(today),
                vendaRepository.getVendasTotalMes(month),
                produtoRepository.getEstoqueBaixoCount(),
                despesaRepository.getDespesasTotalHoje(today),
                vendaRepository.getQtdVendasHoje(today),
                vendaRepository.getTaxaCartaoHoje(today),
                vendaRepository.getTopVendidosDia(today),
                flow { emit(fechamentoRepository.getFechamentoByData(today)) }
            )

            combine(flowList) { array ->
                _vendasHoje.value = array[0] as Double? ?: 0.0
                _vendasMes.value = array[1] as Double? ?: 0.0
                _estoqueBaixo.value = array[2] as Int
                _despesasHoje.value = array[3] as Double? ?: 0.0
                _qtdVendasHoje.value = array[4] as Int
                _taxaCartaoHoje.value = array[5] as Double? ?: 0.0
                _topVendidosDia.value = (array[6] as List<TopVendidoDia>).map { Pair(it.nome, it.total) }
                _caixaFechadoHoje.value = (array[7] as Fechamento?) != null
            }.collect { }
        }
    }
}
