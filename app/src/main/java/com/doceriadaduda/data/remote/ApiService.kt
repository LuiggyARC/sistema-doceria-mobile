package com.doceriadaduda.data.remote

import com.doceriadaduda.model.Despesa
import com.doceriadaduda.model.Empresa
import com.doceriadaduda.model.Produto
import com.doceriadaduda.model.Venda
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/pedidos/")
    suspend fun criarPedido(@Body pedido: PedidoApi): Map<String, String>

    @POST("api/backup/produtos/")
    suspend fun backupProdutos(@Body produtos: List<Produto>): Map<String, String>

    @POST("api/backup/vendas/")
    suspend fun backupVendas(@Body vendas: List<Venda>): Map<String, String>

    @POST("api/backup/despesas/")
    suspend fun backupDespesas(@Body despesas: List<Despesa>): Map<String, String>
}
