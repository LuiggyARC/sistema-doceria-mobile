package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.EmpresaDao
import com.doceriadaduda.model.Empresa
import kotlinx.coroutines.flow.Flow

class EmpresaRepository(private val empresaDao: EmpresaDao) {
    suspend fun getByEmail(email: String) = empresaDao.getEmpresaByEmail(email)
    suspend fun getById(id: Int) = empresaDao.getEmpresaById(id)
    fun getAll(): Flow<List<Empresa>> = empresaDao.getAllEmpresas()
    suspend fun insert(empresa: Empresa) = empresaDao.insert(empresa)
    suspend fun update(empresa: Empresa) = empresaDao.update(empresa)
    suspend fun delete(empresa: Empresa) = empresaDao.delete(empresa)
}
