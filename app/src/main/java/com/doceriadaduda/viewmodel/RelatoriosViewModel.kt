package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.local.local.dao.DespesaPorCategoria
import com.doceriadaduda.data.local.local.dao.FechamentoResumo
import com.doceriadaduda.data.local.local.dao.FaturamentoQtdMes
import com.doceriadaduda.data.local.local.dao.TopVendidoMes
import com.doceriadaduda.data.local.local.dao.VendasPorCategoria
import com.doceriadaduda.data.local.local.dao.VendasPorFormaPagamento
import com.doceriadaduda.data.repository.DespesaRepository
import com.doceriadaduda.data.repository.FechamentoRepository
import com.doceriadaduda.data.repository.VendaRepository
import com.doceriadaduda.model.Fechamento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.doceriadaduda.data.local.SessionManager

class RelatoriosViewModel(private val vendaRepository: VendaRepository,
                          private val despesaRepository: DespesaRepository,
                          private val fechamentoRepository: FechamentoRepository,
                          private val produtoRepository: com.doceriadaduda.data.repository.ProdutoRepository,
                          private val sessionManager: SessionManager,
                          private val sharedViewModel: SharedViewModel) : ViewModel() {

    private val companyId get() = sessionManager.companyId

    private val _vendasHoje = MutableStateFlow(0.0)
    val vendasHoje: StateFlow<Double> = _vendasHoje.asStateFlow()

    private val _despesasHoje = MutableStateFlow(0.0)
    val despesasHoje: StateFlow<Double> = _despesasHoje.asStateFlow()

    private val _qtdVendasHoje = MutableStateFlow(0)
    val qtdVendasHoje: StateFlow<Int> = _qtdVendasHoje.asStateFlow()

    private val _taxaCartaoHoje = MutableStateFlow(0.0)
    val taxaCartaoHoje: StateFlow<Double> = _taxaCartaoHoje.asStateFlow()

    private val _saldoDia = MutableStateFlow(0.0)
    val saldoDia: StateFlow<Double> = _saldoDia.asStateFlow()

    private val _vendas7Dias = MutableStateFlow<List<Triple<String, String, Double>>>(emptyList())
    val vendas7Dias: StateFlow<List<Triple<String, String, Double>>> = _vendas7Dias.asStateFlow()

    private val _pagamentosMes = MutableStateFlow<List<VendasPorFormaPagamento>>(emptyList())
    val pagamentosMes: StateFlow<List<VendasPorFormaPagamento>> = _pagamentosMes.asStateFlow()

    private val _topMes = MutableStateFlow<List<TopVendidoMes>>(emptyList())
    val topMes: StateFlow<List<TopVendidoMes>> = _topMes.asStateFlow()

    private val _despesasCategorias = MutableStateFlow<List<DespesaPorCategoria>>(emptyList())
    val despesasCategorias: StateFlow<List<DespesaPorCategoria>> = _despesasCategorias.asStateFlow()

    private val _faturamentoMes = MutableStateFlow(0.0)
    val faturamentoMes: StateFlow<Double> = _faturamentoMes.asStateFlow()

    private val _despesasMes = MutableStateFlow(0.0)
    val despesasMes: StateFlow<Double> = _despesasMes.asStateFlow()

    private val _saldoMes = MutableStateFlow(0.0)
    val saldoMes: StateFlow<Double> = _saldoMes.asStateFlow()

    private val _qtdVendasMes = MutableStateFlow(0)
    val qtdVendasMes: StateFlow<Int> = _qtdVendasMes.asStateFlow()

    private val _taxaCartaoMes = MutableStateFlow(0.0)
    val taxaCartaoMes: StateFlow<Double> = _taxaCartaoMes.asStateFlow()

    private val _faturamentoMesAnterior = MutableStateFlow(0.0)
    val faturamentoMesAnterior: StateFlow<Double> = _faturamentoMesAnterior.asStateFlow()

    private val _despesasMesAnterior = MutableStateFlow(0.0)
    val despesasMesAnterior: StateFlow<Double> = _despesasMesAnterior.asStateFlow()

    private val _saldoMesAnterior = MutableStateFlow(0.0)
    val saldoMesAnterior: StateFlow<Double> = _saldoMesAnterior.asStateFlow()

    private val _qtdVendasMesAnterior = MutableStateFlow(0)
    val qtdVendasMesAnterior: StateFlow<Int> = _qtdVendasMesAnterior.asStateFlow()

    private val _ticketMedio = MutableStateFlow(0.0)
    val ticketMedio: StateFlow<Double> = _ticketMedio.asStateFlow()

    private val _ticketMedioAnterior = MutableStateFlow(0.0)
    val ticketMedioAnterior: StateFlow<Double> = _ticketMedioAnterior.asStateFlow()

    private val _fechamentosRecentes = MutableStateFlow<List<FechamentoResumo>>(emptyList())
    val fechamentosRecentes: StateFlow<List<FechamentoResumo>> = _fechamentosRecentes.asStateFlow()

    private val _estoqueResumo = MutableStateFlow<List<com.doceriadaduda.model.Produto>>(emptyList())
    val estoqueResumo: StateFlow<List<com.doceriadaduda.model.Produto>> = _estoqueResumo.asStateFlow()

    private val _vendasPorCategoria = MutableStateFlow<List<VendasPorCategoria>>(emptyList())
    val vendasPorCategoria: StateFlow<List<VendasPorCategoria>> = _vendasPorCategoria.asStateFlow()

    private val _mesSelecionado = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
    val mesSelecionado: StateFlow<String> = _mesSelecionado.asStateFlow()

    private val _mensagemStatus = MutableStateFlow("")
    val mensagemStatus: StateFlow<String> = _mensagemStatus.asStateFlow()

    private val _mensagemStatusColor = MutableStateFlow(0xFF000000) // Default black
    val mensagemStatusColor: StateFlow<Long> = _mensagemStatusColor.asStateFlow()

    init {
        loadRelatoriosData()
    }

    fun selecionarMes(mes: String) {
        _mesSelecionado.value = mes
        loadRelatoriosData()
    }

    fun loadRelatoriosData() {
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
            val month = _mesSelecionado.value
            val firstDayOfSelectedMonth = LocalDate.parse("$month-01")
            val previousMonth = firstDayOfSelectedMonth.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))

            val flowList = listOf(
                vendaRepository.getVendasTotalHoje(today, companyId),
                despesaRepository.getDespesasTotalHoje(today, companyId),
                vendaRepository.getQtdVendasHoje(today, companyId),
                vendaRepository.getTaxaCartaoHoje(today, companyId),
                getVendas7DiasFlow(),
                vendaRepository.getVendasPorFormaPagamentoMes(month, companyId),
                vendaRepository.getTopVendidosMes(month, companyId),
                despesaRepository.getDespesasPorCategoriaMes(month, companyId),
                vendaRepository.getFaturamentoQtdMes(month, companyId),
                despesaRepository.getDespesasTotalMes(month, companyId),
                vendaRepository.getTaxaCartaoMes(month, companyId),
                vendaRepository.getFaturamentoQtdMesAnterior(previousMonth, companyId),
                despesaRepository.getDespesasTotalMesAnterior(previousMonth, companyId),
                fechamentoRepository.getFechamentosRecentes(companyId),
                vendaRepository.getVendasPorCategoriaMes(month, companyId),
                produtoRepository.getProdutosAtivos(companyId)
            )

            combine(flowList) { array ->
                val vHoje = array[0] as Double? ?: 0.0
                val dHoje = array[1] as Double? ?: 0.0
                val qtdVendasHoje = array[2] as Int
                val taxaCartaoHoje = array[3] as Double? ?: 0.0
                val vendas7Dias = array[4] as List<Triple<String, String, Double>>
                val pagamentosMes = array[5] as List<VendasPorFormaPagamento>
                val topMes = array[6] as List<TopVendidoMes>
                val despCategorias = array[7] as List<DespesaPorCategoria>
                val fatQtdMes = array[8] as FaturamentoQtdMes
                val despMes = array[9] as Double? ?: 0.0
                val taxaMes = array[10] as Double? ?: 0.0
                val fatQtdMesAnt = array[11] as FaturamentoQtdMes
                val despMesAnt = array[12] as Double? ?: 0.0
                val fechamentosRecentes = array[13] as List<FechamentoResumo>
                val vendasPorCat = array[14] as List<VendasPorCategoria>
                val produtos = array[15] as List<com.doceriadaduda.model.Produto>

                _vendasHoje.value = vHoje
                _despesasHoje.value = dHoje
                _qtdVendasHoje.value = qtdVendasHoje
                _taxaCartaoHoje.value = taxaCartaoHoje
                _saldoDia.value = vHoje - dHoje
                _vendas7Dias.value = vendas7Dias
                _pagamentosMes.value = pagamentosMes
                _topMes.value = topMes
                _despesasCategorias.value = despCategorias

                _faturamentoMes.value = fatQtdMes.faturamento
                _qtdVendasMes.value = fatQtdMes.quantidade
                _despesasMes.value = despMes
                _taxaCartaoMes.value = taxaMes
                _saldoMes.value = fatQtdMes.faturamento - despMes

                _faturamentoMesAnterior.value = fatQtdMesAnt.faturamento
                _qtdVendasMesAnterior.value = fatQtdMesAnt.quantidade
                _despesasMesAnterior.value = despMesAnt
                _saldoMesAnterior.value = fatQtdMesAnt.faturamento - despMesAnt

                _ticketMedio.value = if (fatQtdMes.quantidade > 0) fatQtdMes.faturamento / fatQtdMes.quantidade else 0.0
                _ticketMedioAnterior.value = if (fatQtdMesAnt.quantidade > 0) fatQtdMesAnt.faturamento / fatQtdMesAnt.quantidade else 0.0

                _fechamentosRecentes.value = fechamentosRecentes
                _vendasPorCategoria.value = vendasPorCat
                _estoqueResumo.value = produtos
            }.collect { }
        }
    }

    private fun getVendas7DiasFlow(): Flow<List<Triple<String, String, Double>>> {
        val diasSemana = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab")
        val flows = (0..6).map { i ->
            val date = LocalDate.now().minusDays(i.toLong())
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            vendaRepository.getVendasTotalHoje(dateStr, companyId).map { value ->
                val diaNome = diasSemana[(date.dayOfWeek.value % 7)]
                val diaNum = date.format(DateTimeFormatter.ofPattern("dd/MM"))
                Triple(diaNome, diaNum, value ?: 0.0)
            }
        }
        return combine(flows) { it.toList().reversed() }
    }
}
