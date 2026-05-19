package com.doceriadaduda.data.local.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.doceriadaduda.model.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produtos WHERE ativo = 1 ORDER BY nome ASC")
    fun getProdutosAtivos(): Flow<List<Produto>>

    @Query("SELECT * FROM produtos WHERE id = :produtoId")
    suspend fun getProdutoById(produtoId: Int): Produto?

    @Query("SELECT * FROM produtos WHERE nome = :nomeProduto AND ativo = 1")
    suspend fun getProdutoByNome(nomeProduto: String): Produto?

    @Insert
    suspend fun insert(produto: Produto)

    @Update
    suspend fun update(produto: Produto)

    @Query("UPDATE produtos SET ativo = 0 WHERE id = :produtoId")
    suspend fun desativarProduto(produtoId: Int)

    @Query("SELECT COUNT(*) FROM produtos WHERE quantidadeEstoque <= estoqueMinimo AND ativo = 1")
    fun getEstoqueBaixoCount(): Flow<Int>
}
