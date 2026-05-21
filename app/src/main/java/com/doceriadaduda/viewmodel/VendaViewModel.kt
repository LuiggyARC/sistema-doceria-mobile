package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.payment.PaymentCallback
import com.doceriadaduda.data.payment.PaymentManager
import com.doceriadaduda.data.payment.PaymentProvider
import com.doceriadaduda.data.remote.ApiService
import com.doceriadaduda.data.remote.PedidoApi
import com.doceriadaduda.data.repository.ProdutoRepository
import com.doceriadaduda.data.repository.VendaRepository
import com.doceriadaduda.model.Venda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VendaViewModel(private val produtoRepository: ProdutoRepository,
                       private val vendaRepository: VendaRepository,
                       private val apiService: ApiService,
                       private val sharedViewModel: SharedViewModel,
                       private val paymentManager: PaymentManager) : ViewModel() {

    private val TAXA_CREDITO = 4.39 // percentual

    private val _produtosCompletos = MutableStateFlow<List<com.doceriadaduda.model.Produto>>(emptyList())
    val produtosCompletos: StateFlow<List<com.doceriadaduda.model.Produto>> = _produtosCompletos.asStateFlow()

    private val _produtosAtivos = MutableStateFlow<List<String>>(emptyList())
    val produtosAtivos: StateFlow<List<String>> = _produtosAtivos.asStateFlow()

    private val _vendasHoje = MutableStateFlow<List<Venda>>(emptyList())
    val vendasHoje: StateFlow<List<Venda>> = _vendasHoje.asStateFlow()

    private val _mensagemStatus = MutableStateFlow("")
    val mensagemStatus: StateFlow<String> = _mensagemStatus.asStateFlow()

    private val _mensagemStatusColor = MutableStateFlow(0xFF000000) // Default black
    val mensagemStatusColor: StateFlow<Long> = _mensagemStatusColor.asStateFlow()

    private val _isAguardandoPagamento = MutableStateFlow(false)
    val isAguardandoPagamento: StateFlow<Boolean> = _isAguardandoPagamento.asStateFlow()

    private val _preferredProvider = MutableStateFlow(PaymentProvider.MERCADO_PAGO)
    val preferredProvider: StateFlow<PaymentProvider> = _preferredProvider.asStateFlow()

    private val _showNoDeviceDialog = MutableStateFlow(false)
    val showNoDeviceDialog: StateFlow<Boolean> = _showNoDeviceDialog.asStateFlow()

    private var pendingVendaData: Triple<com.doceriadaduda.model.Produto, Int, String>? = null

    init {
        loadProdutosAtivos()
        loadVendasHoje()
        loadPreferredProvider()
    }

    private fun loadPreferredProvider() {
        val context = com.doceriadaduda.di.AppModule.applicationContext
        val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val providerName = prefs.getString("preferred_payment_provider", PaymentProvider.MERCADO_PAGO.name)
        _preferredProvider.value = try {
            PaymentProvider.valueOf(providerName ?: PaymentProvider.MERCADO_PAGO.name)
        } catch (e: Exception) {
            PaymentProvider.MERCADO_PAGO
        }
    }

    fun setPreferredProvider(provider: PaymentProvider) {
        _preferredProvider.value = provider
        val context = com.doceriadaduda.di.AppModule.applicationContext
        val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putString("preferred_payment_provider", provider.name).apply()
    }

    private fun loadProdutosAtivos() {
        viewModelScope.launch {
            produtoRepository.getProdutosAtivos().collect {
                _produtosCompletos.value = it
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

    fun registrarVenda(produtoNome: String?, quantidadeStr: String?, formaPagamento: String?, onComplete: () -> Unit = {}) {
        if (produtoNome.isNullOrBlank() || quantidadeStr.isNullOrBlank()) {
            _mensagemStatus.value = "Preencha todos os campos!"
            _mensagemStatusColor.value = 0xFFE53935 // RED
            return
        }

        viewModelScope.launch {
            try {
                val quantidade = quantidadeStr.toInt()
                val produto = produtoRepository.getProdutoByNome(produtoNome) ?: return@launch

                if (formaPagamento == "Dinheiro") {
                    // Dinheiro salva direto
                    finalizarRegistroVenda(produto, quantidade, formaPagamento, onComplete)
                } else {
                    // Cartão ou PIX (via maquininha)
                    val context = com.doceriadaduda.di.AppModule.applicationContext
                    val provider = _preferredProvider.value

                    android.util.Log.d("VendaViewModel", "Iniciando checagem de dispositivo para provider: $provider")
                    val isConnected = paymentManager.isDispositivoConectado(context, provider)
                    android.util.Log.d("VendaViewModel", "Dispositivo conectado? $isConnected")

                    if (!isConnected) {
                        pendingVendaData = Triple(produto, quantidade, formaPagamento!!)
                        _showNoDeviceDialog.value = true
                    } else {
                        executarFluxoPagamento(produto, quantidade, formaPagamento!!, provider, onComplete)
                    }
                }
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }

    fun registrarVendaManual(onComplete: () -> Unit = {}) {
        val data = pendingVendaData ?: return
        _showNoDeviceDialog.value = false
        viewModelScope.launch {
            finalizarRegistroVenda(data.first, data.second, data.third, onComplete)
        }
    }

    fun cancelarVendaManual() {
        _showNoDeviceDialog.value = false
        pendingVendaData = null
    }

    private fun executarFluxoPagamento(
        produto: com.doceriadaduda.model.Produto,
        quantidade: Int,
        formaPagamento: String,
        provider: PaymentProvider,
        onComplete: () -> Unit
    ) {
        _isAguardandoPagamento.value = true
        viewModelScope.launch {
            paymentManager.processarPagamento(
                provider = provider,
                valor = quantidade * produto.precoVenda,
                metodo = formaPagamento,
                callback = object : PaymentCallback {
                    override fun onSucesso(idTransacao: String) {
                        viewModelScope.launch {
                            finalizarRegistroVenda(produto, quantidade, formaPagamento, onComplete)
                            _isAguardandoPagamento.value = false
                        }
                    }

                    override fun onError(mensagem: String) {
                        _mensagemStatus.value = "Erro no pagamento: $mensagem"
                        _mensagemStatusColor.value = 0xFFE53935 // RED
                        _isAguardandoPagamento.value = false
                    }

                    override fun onStatus(mensagem: String) {
                        _mensagemStatus.value = mensagem
                        _mensagemStatusColor.value = 0xFF000000
                    }
                }
            )
        }
    }

    private suspend fun finalizarRegistroVenda(
        produto: com.doceriadaduda.model.Produto, 
        quantidade: Int, 
        formaPagamento: String?, 
        onComplete: () -> Unit
    ) {
        val total = quantidade * produto.precoVenda
        var taxa = 0.0
        var valorLiq = total

        if (formaPagamento == "Cartao Credito") {
            taxa = total * (TAXA_CREDITO / 100)
            valorLiq = total - taxa
        }

        val venda = Venda(
            produtoId = produto.id,
            quantidade = quantidade,
            precoUnitarioHistorico = produto.precoVenda,
            valorTotal = total,
            formaPagamento = formaPagamento ?: "Dinheiro",
            taxaCartao = taxa,
            valorLiquido = valorLiq,
            dataVenda = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        vendaRepository.insert(venda)

        val produtoAtualizado = produto.copy(quantidadeEstoque = produto.quantidadeEstoque - quantidade)
        produtoRepository.update(produtoAtualizado)
        
        _mensagemStatus.value = "Venda de ${produto.nome} finalizada!"
        _mensagemStatusColor.value = 0xFF4CAF50 // GREEN
        onComplete()
    }
}
