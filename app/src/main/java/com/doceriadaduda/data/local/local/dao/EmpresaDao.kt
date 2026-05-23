package com.doceriadaduda.data.local.local.dao

import androidx.room.*
import com.doceriadaduda.model.Empresa
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpresaDao {
    @Query("SELECT * FROM empresas WHERE email = :email LIMIT 1")
    suspend fun getEmpresaByEmail(email: String): Empresa?

    @Query("SELECT * FROM empresas WHERE id = :id LIMIT 1")
    suspend fun getEmpresaById(id: Int): Empresa?

    @Query("SELECT * FROM empresas")
    fun getAllEmpresas(): Flow<List<Empresa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(empresa: Empresa): Long

    @Update
    suspend fun update(empresa: Empresa)

    @Delete
    suspend fun delete(empresa: Empresa)
}
