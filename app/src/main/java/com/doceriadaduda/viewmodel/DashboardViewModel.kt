package com.doceriadaduda.viewmodel

import android.content.Context
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
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

    private val _despesasMes = MutableStateFlow(0.0)
    val despesasMes: StateFlow<Double> = _despesasMes.asStateFlow()

    private val _metaMes = MutableStateFlow(0.0)
    val metaMes: StateFlow<Double> = _metaMes.asStateFlow()

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

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        _metaMes.value = prefs.getFloat("meta_mes", 0.0f).toDouble()
        loadDashboardStats()
    }

    fun salvarMeta(context: Context, meta: Double) {
        _metaMes.value = meta
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putFloat("meta_mes", meta.toFloat()).apply()
    }

    fun fecharCaixa() {
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
            val now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            
            val fechamento = Fechamento(
                data = today,
                qtdVendas = _qtdVendasHoje.value,
                faturamento = _vendasHoje.value,
                despesas = _despesasHoje.value,
                saldo = _vendasHoje.value - _despesasHoje.value,
                horaFechamento = now,
                observacao = "Fechamento automático pelo Dashboard"
            )
            fechamentoRepository.insert(fechamento)
            _caixaFechadoHoje.value = true
        }
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
                flow { emit(fechamentoRepository.getFechamentoByData(today)) },
                despesaRepository.getDespesasTotalMes(month)
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
                _despesasMes.value = array[8] as Double? ?: 0.0
            }.collect { }
        }
    }
}
