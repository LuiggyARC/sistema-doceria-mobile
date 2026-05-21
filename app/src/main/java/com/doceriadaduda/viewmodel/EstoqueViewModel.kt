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
                }
            } catch (e: NumberFormatException) {
                _mensagemStatus.value = "Quantidade inválida!"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }

    fun registrarDesperdicio(produtoId: Int, nomeProduto: String, quantidade: Int) {
        viewModelScope.launch {
            try {
                val produto = produtoRepository.getProdutoById(produtoId)
                if (produto != null) {
                    if (produto.quantidadeEstoque < quantidade) {
                        _mensagemStatus.value = "Estoque insuficiente para registrar perda!"
                        _mensagemStatusColor.value = 0xFFE53935 // RED
                        return@launch
                    }
                    val produtoAtualizado = produto.copy(quantidadeEstoque = produto.quantidadeEstoque - quantidade)
                    produtoRepository.update(produtoAtualizado)
                    
                    // TODO: Futuramente, registrar isso em uma tabela de Auditoria/Perdas
                    _mensagemStatus.value = "Desperdício de $quantidade un. de \'$nomeProduto\' registrado."
                    _mensagemStatusColor.value = 0xFFFFA000 // ORANGE
                }
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro ao registrar desperdício: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }

    fun adicionarProduto(nome: String, precoStr: String, categoria: String, estoqueMinStr: String) {
        if (nome.isBlank() || precoStr.isBlank()) {
            _mensagemStatus.value = "Preencha os campos obrigatórios!"
            _mensagemStatusColor.value = 0xFFE53935 // RED
            return
        }
        viewModelScope.launch {
            try {
                val preco = precoStr.toDoubleOrNull() ?: 0.0
                val estoqueMin = estoqueMinStr.toIntOrNull() ?: 5
                
                val novoProduto = Produto(
                    nome = nome.trim(),
                    categoria = if (categoria.isBlank()) "Geral" else categoria.trim(),
                    precoVenda = preco,
                    quantidadeEstoque = 0,
                    estoqueMinimo = estoqueMin,
                    ativo = true
                )
                produtoRepository.insert(novoProduto)
                _mensagemStatus.value = "Produto \'$nome\' cadastrado com sucesso!"
                _mensagemStatusColor.value = 0xFF4CAF50 // GREEN
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro ao salvar: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }

    fun salvarEdicaoProduto(produtoId: Int, nomeAtual: String, novoNome: String, novoPrecoStr: String, novaCategoria: String?) {
        if (novoNome.isBlank() || novoPrecoStr.isBlank()) {
            _mensagemStatus.value = "Nome e preço são obrigatórios!"
            _mensagemStatusColor.value = 0xFFE53935 // RED
            return
        }
        viewModelScope.launch {
            try {
                val novoPreco = novoPrecoStr.toDoubleOrNull() ?: 0.0
                if (novoPreco <= 0) {
                    _mensagemStatus.value = "Preço deve ser maior que zero!"
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
                }
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro: ${e.message}"
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
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro ao excluir: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }
}
