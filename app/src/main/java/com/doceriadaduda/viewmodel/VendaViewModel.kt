package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.remote.ApiService
import com.doceriadaduda.data.remote.PedidoApi
import com.doceriadaduda.data.repository.ProdutoRepository
import com.doceriadaduda.data.repository.VendaRepository
import com.doceriadaduda.model.Venda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VendaViewModel(private val produtoRepository: ProdutoRepository,
                       private val vendaRepository: VendaRepository,
                       private val apiService: ApiService,
                       private val sharedViewModel: SharedViewModel) : ViewModel() {

    private val TAXA_CREDITO = 4.39 // percentual

    private val _produtosAtivos = MutableStateFlow<List<String>>(emptyList())
    val produtosAtivos: StateFlow<List<String>> = _produtosAtivos.asStateFlow()

    private val _vendasHoje = MutableStateFlow<List<Venda>>(emptyList())
    val vendasHoje: StateFlow<List<Venda>> = _vendasHoje.asStateFlow()

    private val _mensagemStatus = MutableStateFlow("")
    val mensagemStatus: StateFlow<String> = _mensagemStatus.asStateFlow()

    private val _mensagemStatusColor = MutableStateFlow(0xFF000000) // Default black
    val mensagemStatusColor: StateFlow<Long> = _mensagemStatusColor.asStateFlow()

    init {
        loadProdutosAtivos()
        loadVendasHoje()
    }

    private fun loadProdutosAtivos() {
        viewModelScope.launch {
            produtoRepository.getProdutosAtivos().collect {
                _produtosAtivos.value = it.map { prod -> prod.nome }
            }
        }
    }

    private fun loadVendasHoje() {
        viewModelScope.launch {
            val today = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
            vendaRepository.getVendasHoje(today).collect {
                _vendasHoje.value = it
            }
        }
    }

    fun registrarVenda(produtoNome: String?, quantidadeStr: String?, formaPagamento: String?) {
        if (produtoNome.isNullOrBlank() || quantidadeStr.isNullOrBlank()) {
            _mensagemStatus.value = "Preencha todos os campos!"
            _mensagemStatusColor.value = 0xFFE53935 // RED
            return
        }

        viewModelScope.launch {
            try {
                val quantidade = quantidadeStr.toInt()
                if (quantidade <= 0) {
                    _mensagemStatus.value = "Quantidade deve ser maior que zero!"
                    _mensagemStatusColor.value = 0xFFE53935 // RED
                    return@launch
                }

                val produto = produtoRepository.getProdutoByNome(produtoNome)
                if (produto == null) {
                    _mensagemStatus.value = "Produto nao encontrado!"
                    _mensagemStatusColor.value = 0xFFE53935 // RED
                    return@launch
                }

                if (produto.quantidadeEstoque < quantidade) {
                    _mensagemStatus.value = "Estoque insuficiente! Disponivel: ${produto.quantidadeEstoque}"
                    _mensagemStatusColor.value = 0xFFE53935 // RED
                    return@launch
                }

                val total = quantidade * produto.precoVenda
                var taxa = 0.0
                var valorLiq = total

                // Envio para a API Django
                try {
                    val pedidoApi = PedidoApi(produtoNome, quantidade, produto.precoVenda)
                    val respostaApi = apiService.criarPedido(pedidoApi)
                    println("Pedido criado na API: $respostaApi")
                } catch (e: Exception) {
                    println("Erro ao enviar para API: ${e.message}")
                }

                // Aplicar taxa de 4,39% no cartao de credito
                if (formaPagamento == "Cartao Credito") {
                    taxa = total * (TAXA_CREDITO / 100)
                    valorLiq = total - taxa
                }

                val venda = Venda(
                    produtoId = produto.id,
                    quantidade = quantidade,
                    precoUnitarioHistorico = produto.precoVenda,
                    valorTotal = total,
                    formaPagamento = formaPagamento,
                    taxaCartao = taxa,
                    valorLiquido = valorLiq,
                    dataVenda = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                vendaRepository.insert(venda)

                val produtoAtualizado = produto.copy(quantidadeEstoque = produto.quantidadeEstoque - quantidade)
                produtoRepository.update(produtoAtualizado)

                if (formaPagamento == "Cartao Credito") {
                    _mensagemStatus.value = "Venda realizada! ${produto.nome} x$quantidade = ${sharedViewModel.fmtReal(total)}\n" +
                            "Taxa cartao (${TAXA_CREDITO}%): -${sharedViewModel.fmtReal(taxa)}\n" +
                            "Valor liquido: ${sharedViewModel.fmtReal(valorLiq)}"
                } else {
                    _mensagemStatus.value = "Venda realizada! ${produto.nome} x$quantidade = ${sharedViewModel.fmtReal(total)}"
                }
                _mensagemStatusColor.value = 0xFF4CAF50 // GREEN

            } catch (e: NumberFormatException) {
                _mensagemStatus.value = "Quantidade invalida!"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }
}
