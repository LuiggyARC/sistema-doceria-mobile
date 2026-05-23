package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.local.SessionManager
import com.doceriadaduda.data.repository.FuncionarioRepository
import com.doceriadaduda.model.Funcionario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FuncionarioViewModel(
    private val funcionarioRepository: FuncionarioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val companyId get() = sessionManager.companyId

    private val _funcionarios = MutableStateFlow<List<Funcionario>>(emptyList())
    val funcionarios: StateFlow<List<Funcionario>> = _funcionarios.asStateFlow()

    init {
        loadFuncionarios()
    }

    fun loadFuncionarios() {
        viewModelScope.launch {
            funcionarioRepository.getFuncionarios(companyId).collect {
                _funcionarios.value = it
            }
        }
    }

    fun adicionarFuncionario(nome: String, email: String, cargo: String) {
        viewModelScope.launch {
            val novo = Funcionario(
                companyId = companyId,
                nome = nome,
                email = email,
                cargo = cargo
            )
            funcionarioRepository.insert(novo)
        }
    }

    fun desativarFuncionario(id: Int) {
        viewModelScope.launch {
            funcionarioRepository.desativar(id, companyId)
        }
    }
}
