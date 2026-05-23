package com.doceriadaduda.di

import android.content.Context
import com.doceriadaduda.data.local.local.AppDatabase
import com.doceriadaduda.data.remote.ApiService
import com.doceriadaduda.data.payment.PaymentManager
import com.doceriadaduda.data.local.SessionManager
import com.doceriadaduda.data.repository.DespesaRepository
import com.doceriadaduda.data.repository.EmpresaRepository
import com.doceriadaduda.data.repository.FechamentoRepository
import com.doceriadaduda.data.repository.FuncionarioRepository
import com.doceriadaduda.data.repository.ProdutoRepository
import com.doceriadaduda.data.repository.VendaRepository
import com.doceriadaduda.viewmodel.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {

    lateinit var applicationContext: Context

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
    private val empresaDao by lazy { database.empresaDao() }
    private val funcionarioDao by lazy { database.funcionarioDao() }

    val sessionManager: SessionManager by lazy { SessionManager(applicationContext) }

    val produtoRepository: ProdutoRepository by lazy { ProdutoRepository(produtoDao) }
    val vendaRepository: VendaRepository by lazy { VendaRepository(vendaDao) }
    val despesaRepository: DespesaRepository by lazy { DespesaRepository(despesaDao) }
    val fechamentoRepository: FechamentoRepository by lazy { FechamentoRepository(fechamentoDao) }
    val empresaRepository: EmpresaRepository by lazy { EmpresaRepository(empresaDao) }
    val funcionarioRepository: FuncionarioRepository by lazy { FuncionarioRepository(funcionarioDao) }

    val paymentManager: PaymentManager by lazy { PaymentManager() }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8010/") // Base URL da sua API Django
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

    val authViewModel: AuthViewModel by lazy {
        AuthViewModel(empresaRepository, sessionManager)
    }

    val funcionarioViewModel: FuncionarioViewModel by lazy {
        FuncionarioViewModel(funcionarioRepository, sessionManager)
    }

    val syncViewModel: SyncViewModel by lazy {
        SyncViewModel(apiService, produtoRepository, vendaRepository, despesaRepository, sessionManager)
    }

    val sharedViewModel: SharedViewModel by lazy { SharedViewModel() }

    val dashboardViewModel: DashboardViewModel by lazy {
        DashboardViewModel(produtoRepository, vendaRepository, despesaRepository, fechamentoRepository, sessionManager)
    }

    val vendaViewModel: VendaViewModel by lazy {
        VendaViewModel(produtoRepository, vendaRepository, apiService, sessionManager, paymentManager)
    }

    val estoqueViewModel: EstoqueViewModel by lazy {
        EstoqueViewModel(produtoRepository, sessionManager)
    }

    val despesaViewModel: DespesaViewModel by lazy {
        DespesaViewModel(despesaRepository, sessionManager, sharedViewModel)
    }

    val relatoriosViewModel: RelatoriosViewModel by lazy {
        RelatoriosViewModel(vendaRepository, despesaRepository, fechamentoRepository, produtoRepository, sessionManager, sharedViewModel)
    }
}
