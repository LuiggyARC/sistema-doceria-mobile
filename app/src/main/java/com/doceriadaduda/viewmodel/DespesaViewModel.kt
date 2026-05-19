package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.local.local.dao.DespesaResumo
import com.doceriadaduda.data.repository.DespesaRepository
import com.doceriadaduda.model.Despesa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DespesaViewModel(private val despesaRepository: DespesaRepository,
                         private val sharedViewModel: SharedViewModel) : ViewModel() {

    private val _ultimasDespesas = MutableStateFlow<List<DespesaResumo>>(emptyList())
    val ultimasDespesas: StateFlow<List<DespesaResumo>> = _ultimasDespesas.asStateFlow()

    private val _mensagemStatus = MutableStateFlow("")
    val mensagemStatus: StateFlow<String> = _mensagemStatus.asStateFlow()

    private val _mensagemStatusColor = MutableStateFlow(0xFF000000) // Default black
    val mensagemStatusColor: StateFlow<Long> = _mensagemStatusColor.asStateFlow()

    init {
        loadUltimasDespesas()
    }

    fun loadUltimasDespesas() {
        viewModelScope.launch {
            despesaRepository.getUltimasDespesas().collect {
                _ultimasDespesas.value = it
            }
        }
    }

    fun registrarDespesa(descricao: String?, categoria: String?, valorStr: String?) {
        if (descricao.isNullOrBlank() || valorStr.isNullOrBlank()) {
            _mensagemStatus.value = "Preencha descricao e valor!"
            _mensagemStatusColor.value = 0xFFE53935 // RED
            return
        }

        viewModelScope.launch {
            try {
                val valorDesp = sharedViewModel.parseValor(valorStr)
                if (valorDesp <= 0) {
                    _mensagemStatus.value = "Valor deve ser maior que zero!"
                    _mensagemStatusColor.value = 0xFFE53935 // RED
                    return@launch
                }

                val despesa = Despesa(
                    descricao = descricao.trim(),
                    categoria = categoria,
                    tipo = "Variavel", // Hardcoded as in Python version
                    valor = valorDesp,
                    data = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                )
                despesaRepository.insert(despesa)
                _mensagemStatus.value = "Despesa \'${descricao}\' registrada!"
                _mensagemStatusColor.value = 0xFF4CAF50 // GREEN
                loadUltimasDespesas()
            } catch (e: NumberFormatException) {
                _mensagemStatus.value = "Valor invalido! Use virgula ou ponto."
                _mensagemStatusColor.value = 0xFFE53935 // RED
            } catch (e: Exception) {
                _mensagemStatus.value = "Erro: ${e.message}"
                _mensagemStatusColor.value = 0xFFE53935 // RED
            }
        }
    }
}
