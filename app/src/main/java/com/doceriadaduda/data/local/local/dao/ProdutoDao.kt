package com.doceriadaduda.data.local.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.doceriadaduda.model.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produtos WHERE companyId = :companyId AND ativo = 1 ORDER BY nome ASC")
    fun getProdutosAtivos(companyId: Int): Flow<List<Produto>>

    @Query("SELECT * FROM produtos WHERE id = :produtoId AND companyId = :companyId")
    suspend fun getProdutoById(produtoId: Int, companyId: Int): Produto?

    @Query("SELECT * FROM produtos WHERE nome = :nomeProduto AND companyId = :companyId AND ativo = 1")
    suspend fun getProdutoByNome(nomeProduto: String, companyId: Int): Produto?

    @Insert
    suspend fun insert(produto: Produto)

    @Update
    suspend fun update(produto: Produto)

    @Query("UPDATE produtos SET ativo = 0 WHERE id = :produtoId AND companyId = :companyId")
    suspend fun desativarProduto(produtoId: Int, companyId: Int)

    @Query("SELECT COUNT(*) FROM produtos WHERE companyId = :companyId AND quantidadeEstoque <= estoqueMinimo AND ativo = 1")
    fun getEstoqueBaixoCount(companyId: Int): Flow<Int>
}
