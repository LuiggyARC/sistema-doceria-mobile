package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.ProdutoDao
import com.doceriadaduda.model.Produto
import kotlinx.coroutines.flow.Flow

class ProdutoRepository(private val produtoDao: ProdutoDao) {

    fun getProdutosAtivos(): Flow<List<Produto>> = produtoDao.getProdutosAtivos()

    suspend fun getProdutoById(produtoId: Int): Produto? = produtoDao.getProdutoById(produtoId)

    suspend fun getProdutoByNome(nomeProduto: String): Produto? = produtoDao.getProdutoByNome(nomeProduto)

    suspend fun insert(produto: Produto) = produtoDao.insert(produto)

    suspend fun update(produto: Produto) = produtoDao.update(produto)

    suspend fun desativarProduto(produtoId: Int) = produtoDao.desativarProduto(produtoId)

    fun getEstoqueBaixoCount(): Flow<Int> = produtoDao.getEstoqueBaixoCount()
}
