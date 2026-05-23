package com.doceriadaduda.data.repository

import com.doceriadaduda.data.local.local.dao.ProdutoDao
import com.doceriadaduda.model.Produto
import kotlinx.coroutines.flow.Flow

class ProdutoRepository(private val produtoDao: ProdutoDao) {

    fun getProdutosAtivos(companyId: Int): Flow<List<Produto>> = produtoDao.getProdutosAtivos(companyId)

    suspend fun getProdutoById(produtoId: Int, companyId: Int): Produto? = produtoDao.getProdutoById(produtoId, companyId)

    suspend fun getProdutoByNome(nomeProduto: String, companyId: Int): Produto? = produtoDao.getProdutoByNome(nomeProduto, companyId)

    suspend fun insert(produto: Produto) = produtoDao.insert(produto)

    suspend fun update(produto: Produto) = produtoDao.update(produto)

    suspend fun desativarProduto(produtoId: Int, companyId: Int) = produtoDao.desativarProduto(produtoId, companyId)

    fun getEstoqueBaixoCount(companyId: Int): Flow<Int> = produtoDao.getEstoqueBaixoCount(companyId)
}
