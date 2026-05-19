package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.repository.ProdutoRepository
import com.doceriadaduda.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EstoqueViewModel(private val produtoRepository: ProdutoRepository,
                         private val sharedViewModel: SharedViewModel) : ViewModel() {

    private val _produtosAtivos = MutableStateFlow<List<Produto>>(emptyList())
    val produtosAtivos: StateFlow<List<Produto>> = _produtosAtivos.asStateFlow()

    private val _mensagemStatus = MutableStateFlow("")
    val mensagemStatus: StateFlow<String> = _mensagemStatus.asStateFlow()

    private val _mensagemStatusColor = MutableStateFlow(0xFF000000) // Default black
    val mensagemStatusColor: StateFlow<Long> = _mensagemStatusColor.asStateFlow()

    init {
        loadProdutosAtivos()
    }

    fun loadProdutosAtivos() {
        viewModelScope.launch {
            produtoRepository.getProdutosAtivos().collect {
                _produtosAtivos.value = it
            }
        }
    }

    fun reporEstoque(produtoId: Int, nomeProduto: String, quantidadeStr: String) {
        if (quantidadeStr.isBlank()) {
            _mensagemStatus.value = "Digite a quantidade a repor!"
            _mensagemStatusColor.value = 0xFFE53935 // RED
            return
        }
        viewModelScope.launch {
            try {
                val qtdRepor = quantidadeStr.toInt()
                if (qtdRepor <= 0) {
                    _mensagemStatus.value = "Quantidade deve ser maior que zero!"
                    _mensagemStatusColor.value = 0xFFE53935 // RED
                    return@launch
                }
                val produto = produtoRepository.getProdutoById(produtoId)
                if (produto != null) {
                    val produtoAtualizado = produto.copy(quantidadeEstoque = produto.quantidadeEstoque + qtdRepor)
                    produtoRepository.update(produtoAtualizado)
                    _mensagemStatus.value = "Estoque de \'${nomeProduto}\' reposto com +${qtdRepor} unidades!"
                    _mensagemStatusColor.value = 0xFF4CAF50 // GREEN
                    loadProdutosAtivos() // Recarregar a lista para atualizar a UI
                }
            } catch (e: NumberFormatException) {
                _mensagemStatus.value = "Quantidade invalida!"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }

    fun salvarEdicaoProduto(produtoId: Int, nomeAtual: String, novoNome: String, novoPrecoStr: String, novaCategoria: String?) {
        if (novoNome.isBlank() || novoPrecoStr.isBlank()) {
            _mensagemStatus.value = "Nome e preco sao obrigatorios!"
            _mensagemStatusColor.value = 0xFFE53935 // RED
            return
        }
        viewModelScope.launch {
            try {
                val novoPreco = sharedViewModel.parseValor(novoPrecoStr)
                if (novoPreco <= 0) {
                    _mensagemStatus.value = "Preco deve ser maior que zero!"
                    _mensagemStatusColor.value = 0xFFE53935 // RED
                    return@launch
                }

                val produtoExistenteComNome = produtoRepository.getProdutoByNome(novoNome)
                if (produtoExistenteComNome != null && produtoExistenteComNome.id != produtoId) {
                    _mensagemStatus.value = "Ja existe um produto com esse nome!"
                    _mensagemStatusColor.value = 0xFFE53935 // RED
                    return@launch
                }

                val produto = produtoRepository.getProdutoById(produtoId)
                if (produto != null) {
                    val produtoAtualizado = produto.copy(
                        nome = novoNome.trim(),
                        categoria = novaCategoria?.trim() ?: "Geral",
                        precoVenda = novoPreco
                    )
                    produtoRepository.update(produtoAtualizado)
                    _mensagemStatus.value = "Produto atualizado com sucesso!"
                    _mensagemStatusColor.value = 0xFF4CAF50 // GREEN
                    loadProdutosAtivos()
                }
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }

    fun addProduto(produto: Produto) {
        viewModelScope.launch {
            try {
                produtoRepository.insert(produto)
                _mensagemStatus.value = "Produto \'${produto.nome}\' adicionado com sucesso!"
                _mensagemStatusColor.value = 0xFF4CAF50 // GREEN
                loadProdutosAtivos()
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro ao adicionar produto: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }

    fun excluirProduto(produtoId: Int, nomeProduto: String) {
        viewModelScope.launch {
            try {
                produtoRepository.desativarProduto(produtoId)
                _mensagemStatus.value = "Produto \'${nomeProduto}\' removido!"
                _mensagemStatusColor.value = 0xFF4CAF50 // GREEN
                loadProdutosAtivos()
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro ao excluir: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }
}
