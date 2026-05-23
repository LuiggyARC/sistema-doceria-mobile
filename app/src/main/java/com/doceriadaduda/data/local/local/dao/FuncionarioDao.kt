package com.doceriadaduda.data.local.local.dao

import androidx.room.*
import com.doceriadaduda.model.Funcionario
import kotlinx.coroutines.flow.Flow

@Dao
interface FuncionarioDao {
    @Query("SELECT * FROM funcionarios WHERE companyId = :companyId AND ativo = 1")
    fun getFuncionariosByEmpresa(companyId: Int): Flow<List<Funcionario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(funcionario: Funcionario)

    @Update
    suspend fun update(funcionario: Funcionario)

    @Query("UPDATE funcionarios SET ativo = 0 WHERE id = :id AND companyId = :companyId")
    suspend fun desativarFuncionario(id: Int, companyId: Int)
}
