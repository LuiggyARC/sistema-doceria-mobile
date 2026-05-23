package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.FuncionarioDao
import com.doceriadaduda.model.Funcionario
import kotlinx.coroutines.flow.Flow

class FuncionarioRepository(private val funcionarioDao: FuncionarioDao) {
    fun getFuncionarios(companyId: Int): Flow<List<Funcionario>> = funcionarioDao.getFuncionariosByEmpresa(companyId)
    suspend fun insert(funcionario: Funcionario) = funcionarioDao.insert(funcionario)
    suspend fun update(funcionario: Funcionario) = funcionarioDao.update(funcionario)
    suspend fun desativar(id: Int, companyId: Int) = funcionarioDao.desativarFuncionario(id, companyId)
}
