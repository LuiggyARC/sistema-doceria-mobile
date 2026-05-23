package com.doceriadaduda.data.local.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.doceriadaduda.model.Venda
import kotlinx.coroutines.flow.Flow

@Dao
interface VendaDao {
    @Insert
    suspend fun insert(venda: Venda)

    @Query("SELECT * FROM vendas WHERE companyId = :companyId AND DATE(dataVenda) = :today ORDER BY id DESC LIMIT 10")
    fun getVendasHoje(today: String, companyId: Int): Flow<List<Venda>>

    @Query("SELECT SUM(valorTotal) FROM vendas WHERE companyId = :companyId AND DATE(dataVenda) = :today")
    fun getVendasTotalHoje(today: String, companyId: Int): Flow<Double?>

    @Query("SELECT SUM(valorTotal) FROM vendas WHERE companyId = :companyId AND STRFTIME(\"%Y-%m\", dataVenda) = :month")
    fun getVendasTotalMes(month: String, companyId: Int): Flow<Double?>

    @Query("SELECT COUNT(*) FROM vendas WHERE companyId = :companyId AND DATE(dataVenda) = :today")
    fun getQtdVendasHoje(today: String, companyId: Int): Flow<Int>

    @Query("SELECT SUM(taxaCartao) FROM vendas WHERE companyId = :companyId AND DATE(dataVenda) = :today")
    fun getTaxaCartaoHoje(today: String, companyId: Int): Flow<Double?>

    @Query("SELECT p.nome, SUM(v.quantidade) as total FROM vendas v JOIN produtos p ON v.produtoId = p.id WHERE v.companyId = :companyId AND DATE(v.dataVenda) = :today GROUP BY p.nome ORDER BY total DESC LIMIT 5")
    fun getTopVendidosDia(today: String, companyId: Int): Flow<List<TopVendidoDia>>

    @Query("SELECT formaPagamento, SUM(valorTotal) as total FROM vendas WHERE companyId = :companyId AND STRFTIME(\"%Y-%m\", dataVenda) = :month GROUP BY formaPagamento ORDER BY total DESC")
    fun getVendasPorFormaPagamentoMes(month: String, companyId: Int): Flow<List<VendasPorFormaPagamento>>

    @Query("SELECT p.nome, SUM(v.quantidade) as total, SUM(v.valorTotal) as receita FROM vendas v JOIN produtos p ON v.produtoId = p.id WHERE v.companyId = :companyId AND STRFTIME(\"%Y-%m\", v.dataVenda) = :month GROUP BY p.nome ORDER BY receita DESC LIMIT 5")
    fun getTopVendidosMes(month: String, companyId: Int): Flow<List<TopVendidoMes>>

    @Query("SELECT STRFTIME(\"%w\", dataVenda) as diaSemana, COALESCE(SUM(valorTotal), 0) as total, COUNT(DISTINCT DATE(dataVenda)) as diasDistintos FROM vendas WHERE companyId = :companyId AND DATE(dataVenda) >= DATE(\'now\', \'-60 days\') GROUP BY STRFTIME(\"%w\", dataVenda)")
    fun getMediaVendasPorDiaSemana(companyId: Int): Flow<List<MediaVendasDiaSemana>>

    @Query("SELECT COALESCE(SUM(valorTotal),0) as faturamento, COUNT(*) as quantidade FROM vendas WHERE companyId = :companyId AND STRFTIME(\"%Y-%m\", dataVenda) = :month")
    fun getFaturamentoQtdMes(month: String, companyId: Int): Flow<FaturamentoQtdMes>

    @Query("SELECT COALESCE(SUM(taxaCartao),0) FROM vendas WHERE companyId = :companyId AND STRFTIME(\"%Y-%m\", dataVenda) = :month")
    fun getTaxaCartaoMes(month: String, companyId: Int): Flow<Double?>

    @Query("SELECT COALESCE(SUM(valorTotal),0) as faturamento, COUNT(*) as quantidade FROM vendas WHERE companyId = :companyId AND STRFTIME(\"%Y-%m\", dataVenda) = :previousMonth")
    fun getFaturamentoQtdMesAnterior(previousMonth: String, companyId: Int): Flow<FaturamentoQtdMes>

    @Query("SELECT p.categoria, SUM(v.valorTotal) as total FROM vendas v JOIN produtos p ON v.produtoId = p.id WHERE v.companyId = :companyId AND STRFTIME(\"%Y-%m\", v.dataVenda) = :month GROUP BY p.categoria ORDER BY total DESC")
    fun getVendasPorCategoriaMes(month: String, companyId: Int): Flow<List<VendasPorCategoria>>

    @Query("SELECT SUM(v.valorTotal - (p.precoCusto * v.quantidade)) FROM vendas v JOIN produtos p ON v.produtoId = p.id WHERE v.companyId = :companyId AND STRFTIME(\"%Y-%m\", v.dataVenda) = :month")
    fun getLucroEstimadoMes(month: String, companyId: Int): Flow<Double?>

    @Query("SELECT SUM(v.valorTotal - (p.precoCusto * v.quantidade)) FROM vendas v JOIN produtos p ON v.produtoId = p.id WHERE v.companyId = :companyId AND DATE(v.dataVenda) = :today")
    fun getLucroEstimadoHoje(today: String, companyId: Int): Flow<Double?>
}

data class TopVendidoDia(val nome: String, val total: Int)
data class VendasPorFormaPagamento(val formaPagamento: String, val total: Double)
data class TopVendidoMes(val nome: String, val total: Int, val receita: Double)
data class MediaVendasDiaSemana(val diaSemana: String, val total: Double, val diasDistintos: Int)
data class FaturamentoQtdMes(val faturamento: Double, val quantidade: Int)
data class VendasPorCategoria(val categoria: String?, val total: Double)
