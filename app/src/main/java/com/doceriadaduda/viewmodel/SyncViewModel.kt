package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.local.SessionManager
import com.doceriadaduda.data.remote.ApiService
import com.doceriadaduda.data.repository.DespesaRepository
import com.doceriadaduda.data.repository.ProdutoRepository
import com.doceriadaduda.data.repository.VendaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SyncViewModel(
    private val apiService: ApiService,
    private val produtoRepository: ProdutoRepository,
    private val vendaRepository: VendaRepository,
    private val despesaRepository: DespesaRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage = _syncMessage.asStateFlow()

    fun performBackup() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Iniciando backup..."
            try {
                val companyId = sessionManager.companyId
                
                // 1. Backup Produtos (Sincronização Total)
                val produtos = produtoRepository.getProdutosAtivos(companyId).first()
                apiService.backupProdutos(produtos)
                
                // 2. Backup Vendas (Sincronização Delta)
                val vendasPendente = vendaRepository.getVendasNaoSincronizadas(companyId)
                if (vendasPendente.isNotEmpty()) {
                    apiService.backupVendas(vendasPendente)
                    vendaRepository.marcarComoSincronizado(vendasPendente.map { it.id })
                }
                
                // 3. Backup Despesas (Sincronização Delta)
                val despesasPendente = despesaRepository.getDespesasNaoSincronizadas(companyId)
                if (despesasPendente.isNotEmpty()) {
                    apiService.backupDespesas(despesasPendente)
                    despesaRepository.marcarComoSincronizado(despesasPendente.map { it.id })
                }
                
                _syncMessage.value = "Backup concluído com sucesso!"
            } catch (e: Exception) {
                _syncMessage.value = "Erro no backup: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
