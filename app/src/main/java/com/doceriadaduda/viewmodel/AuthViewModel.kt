package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doceriadaduda.data.local.SessionManager
import com.doceriadaduda.data.repository.EmpresaRepository
import com.doceriadaduda.model.Empresa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val empresaRepository: EmpresaRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(email: String, senha: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val empresa = empresaRepository.getByEmail(email)
                if (empresa != null && empresa.senhaHash == senha) { // Simples para exemplo, usar hash real
                    sessionManager.companyId = empresa.id
                    sessionManager.companyName = empresa.nome
                    sessionManager.isAdmin = empresa.isAdmin
                    onSuccess(empresa.nome)
                } else {
                    _error.value = "Email ou senha incorretos"
                }
            } catch (e: Exception) {
                _error.value = "Erro ao fazer login: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(nome: String, email: String, senha: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                if (empresaRepository.getByEmail(email) != null) {
                    _error.value = "Este email já está cadastrado"
                    return@launch
                }

                val novaEmpresa = Empresa(
                    nome = nome,
                    email = email,
                    senhaHash = senha, // Simples para exemplo
                    isAdmin = false
                )
                val id = empresaRepository.insert(novaEmpresa)
                sessionManager.companyId = id.toInt()
                sessionManager.companyName = nome
                onSuccess(nome)
            } catch (e: Exception) {
                _error.value = "Erro ao cadastrar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
