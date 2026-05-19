package com.doceriadaduda.di

import android.content.Context
import com.doceriadaduda.data.local.local.AppDatabase
import com.doceriadaduda.data.remote.ApiService
import com.doceriadaduda.data.repository.DespesaRepository
import com.doceriadaduda.data.repository.FechamentoRepository
import com.doceriadaduda.data.repository.ProdutoRepository
import com.doceriadaduda.data.repository.VendaRepository
import com.doceriadaduda.viewmodel.DashboardViewModel
import com.doceriadaduda.viewmodel.DespesaViewModel
import com.doceriadaduda.viewmodel.EstoqueViewModel
import com.doceriadaduda.viewmodel.RelatoriosViewModel
import com.doceriadaduda.viewmodel.SharedViewModel
import com.doceriadaduda.viewmodel.VendaViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    private val produtoDao by lazy { database.produtoDao() }
    private val vendaDao by lazy { database.vendaDao() }
    private val despesaDao by lazy { database.despesaDao() }
    private val fechamentoDao by lazy { database.fechamentoDao() }

    val produtoRepository: ProdutoRepository by lazy { ProdutoRepository(produtoDao) }
    val vendaRepository: VendaRepository by lazy { VendaRepository(vendaDao) }
    val despesaRepository: DespesaRepository by lazy { DespesaRepository(despesaDao) }
    val fechamentoRepository: FechamentoRepository by lazy { FechamentoRepository(fechamentoDao) }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8010/") // Base URL da sua API Django
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

    val sharedViewModel: SharedViewModel by lazy { SharedViewModel() }

    val dashboardViewModel: DashboardViewModel by lazy {
        DashboardViewModel(produtoRepository, vendaRepository, despesaRepository, fechamentoRepository)
    }

    val vendaViewModel: VendaViewModel by lazy {
        VendaViewModel(produtoRepository, vendaRepository, apiService, sharedViewModel)
    }

    val estoqueViewModel: EstoqueViewModel by lazy {
        EstoqueViewModel(produtoRepository, sharedViewModel)
    }

    val despesaViewModel: DespesaViewModel by lazy {
        DespesaViewModel(despesaRepository, sharedViewModel)
    }

    val relatoriosViewModel: RelatoriosViewModel by lazy {
        RelatoriosViewModel(vendaRepository, despesaRepository, fechamentoRepository, sharedViewModel)
    }
}
