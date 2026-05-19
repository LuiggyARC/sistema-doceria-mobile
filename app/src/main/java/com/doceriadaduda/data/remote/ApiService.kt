package com.doceriadaduda.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/pedidos/")
    suspend fun criarPedido(@Body pedido: PedidoApi): Map<String, String>
}
