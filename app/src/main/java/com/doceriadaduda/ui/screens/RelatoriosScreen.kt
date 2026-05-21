package com.doceriadaduda.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.ui.theme.Blue
import com.doceriadaduda.ui.theme.Green
import com.doceriadaduda.ui.theme.Orange
import com.doceriadaduda.ui.theme.Primary
import com.doceriadaduda.ui.theme.Purple
import com.doceriadaduda.ui.theme.Red
import com.doceriadaduda.ui.theme.Secondary
import com.doceriadaduda.ui.theme.TextColor
import com.doceriadaduda.viewmodel.RelatoriosViewModel
import com.doceriadaduda.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun RelatoriosScreen(relatoriosViewModel: RelatoriosViewModel = AppModule.relatoriosViewModel,
                     sharedViewModel: SharedViewModel = AppModule.sharedViewModel) {

    val mesSelecionadoStr by relatoriosViewModel.mesSelecionado.collectAsState()
    val mesSelecionado = remember(mesSelecionadoStr) { YearMonth.parse(mesSelecionadoStr) }

    val vendasHoje by relatoriosViewModel.vendasHoje.collectAsState()
    val despesasHoje by relatoriosViewModel.despesasHoje.collectAsState()
    val qtdVendasHoje by relatoriosViewModel.qtdVendasHoje.collectAsState()
    val taxaCartaoHoje by relatoriosViewModel.taxaCartaoHoje.collectAsState()
    val saldoDia by relatoriosViewModel.saldoDia.collectAsState()

    val vendas7Dias by relatoriosViewModel.vendas7Dias.collectAsState()
    val pagamentosMes by relatoriosViewModel.pagamentosMes.collectAsState()
    val topMes by relatoriosViewModel.topMes.collectAsState()
    val despesasCategorias by relatoriosViewModel.despesasCategorias.collectAsState()

    val faturamentoMes by relatoriosViewModel.faturamentoMes.collectAsState()
    val despesasMes by relatoriosViewModel.despesasMes.collectAsState()
    val saldoMes by relatoriosViewModel.saldoMes.collectAsState()
    val qtdVendasMes by relatoriosViewModel.qtdVendasMes.collectAsState()
    val taxaCartaoMes by relatoriosViewModel.taxaCartaoMes.collectAsState()

    val faturamentoMesAnterior by relatoriosViewModel.faturamentoMesAnterior.collectAsState()
    val despesasMesAnterior by relatoriosViewModel.despesasMesAnterior.collectAsState()
    val saldoMesAnterior by relatoriosViewModel.saldoMesAnterior.collectAsState()
    val qtdVendasMesAnterior by relatoriosViewModel.qtdVendasMesAnterior.collectAsState()

    val ticketMedio by relatoriosViewModel.ticketMedio.collectAsState()
    val ticketMedioAnterior by relatoriosViewModel.ticketMedioAnterior.collectAsState()

    val vendasPorCategoria by relatoriosViewModel.vendasPorCategoria.collectAsState()

    val estoqueResumo by relatoriosViewModel.estoqueResumo.collectAsState()

    val fechamentosRecentes by relatoriosViewModel.fechamentosRecentes.collectAsState()

    val todayFmt = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val monthDisplayFmt = mesSelecionado.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Relatórios",
                    fontSize = 24.sp,
                    color = Secondary,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = { 
                        com.doceriadaduda.util.PdfReportExporter.exportMensal(
                            context = com.doceriadaduda.di.AppModule.applicationContext, // Access app context
                            mes = mesSelecionado.format(DateTimeFormatter.ofPattern("MM/yyyy")),
                            faturamento = faturamentoMes,
                            despesas = despesasMes,
                            saldo = saldoMes,
                            ticketMedio = ticketMedio,
                            vendasQtd = qtdVendasMes
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF", fontSize = 12.sp)
                }
            }
            HorizontalDivider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }

        // Seletor de Mês
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { 
                        val prev = mesSelecionado.minusMonths(1)
                        relatoriosViewModel.selecionarMes(prev.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mês Anterior", tint = Secondary)
                    }
                    
                    Text(
                        text = monthDisplayFmt.replaceFirstChar { it.uppercase() },
                        fontWeight = FontWeight.Bold,
                        color = Secondary,
                        fontSize = 16.sp
                    )
                    
                    IconButton(onClick = { 
                        val next = mesSelecionado.plusMonths(1)
                        relatoriosViewModel.selecionarMes(next.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo Mês", tint = Secondary)
                    }
                }
            }
        }

        // Resumo do Dia
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Summarize, contentDescription = "Resumo", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Resumo do Dia ($todayFmt)", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    ResumoItem("Vendas realizadas:", "$qtdVendasHoje", TextColor)
                    ResumoItem("Faturamento bruto:", sharedViewModel.fmtReal(vendasHoje), Green)
                    ResumoItem("Despesas:", sharedViewModel.fmtReal(despesasHoje), Red)
                    ResumoItem("Taxa Cartao:", sharedViewModel.fmtReal(taxaCartaoHoje), Purple)
                    ResumoItem("Saldo do dia:", sharedViewModel.fmtReal(saldoDia), if (saldoDia >= 0) Green else Red)
                }
            }
        }

        // Gráfico 1: Vendas dos últimos 7 dias
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShowChart, contentDescription = "Vendas 7 dias", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Vendas - Ultimos 7 dias", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    val maxVendaDia = vendas7Dias.maxOfOrNull { it.third } ?: 1.0
                    val coresDias = listOf(Primary, Secondary, Green, Orange, Purple, Blue, Red)

                    if (vendas7Dias.isEmpty()) {
                        Text("Nenhuma venda nos ultimos 7 dias.", fontSize = 12.sp, color = TextColor, fontStyle = FontStyle.Italic)
                    } else {
                        vendas7Dias.forEachIndexed { index, (diaNome, diaNum, valor) ->
                            val corBarra = coresDias[index % coresDias.size]
                            HorizontalBarChartItem(
                                label = "$diaNome\n$diaNum",
                                value = valor,
                                maxValue = maxVendaDia,
                                color = corBarra,
                                sharedViewModel = sharedViewModel
                            )
                        }
                        Text("(barras proporcionais ao maior dia)", fontSize = 10.sp, color = TextColor, fontStyle = FontStyle.Italic)
                    }
                }
            }
        }

        // Gráfico 2: Vendas por forma de pagamento (mês selecionado)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = "Formas de Pagamento", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Formas de Pagamento (Mes)", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    val totalPagto = pagamentosMes.sumOf { it.total }
                    val coresPagto = mapOf(
                        "Dinheiro" to Green,
                        "PIX" to Blue,
                        "Cartao Debito" to Orange,
                        "Cartao Credito" to Purple
                    )

                    if (pagamentosMes.isEmpty()) {
                        Text("Nenhuma venda no período.", fontSize = 12.sp, color = TextColor, fontStyle = FontStyle.Italic)
                    } else {
                        pagamentosMes.forEach { item ->
                            val pct = if (totalPagto > 0) (item.total / totalPagto * 100) else 0.0
                            val cor = coresPagto.getOrDefault(item.formaPagamento, Secondary)
                            PaymentMethodBarItem(
                                label = item.formaPagamento,
                                percentage = pct,
                                color = cor
                            )
                        }
                    }
                }
            }
        }

        // Gráfico 3: Top 5 produtos mais vendidos do mês selecionado
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, contentDescription = "Top Produtos Mes", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Top 5 Produtos Vendidos (Mes)", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    val maxReceitaTop = topMes.maxOfOrNull { it.receita } ?: 1.0
                    val coresTop = listOf(Secondary, Primary, Green, Blue, Orange)

                    if (topMes.isEmpty()) {
                        Text("Nenhuma venda no período.", fontSize = 12.sp, color = TextColor, fontStyle = FontStyle.Italic)
                    } else {
                        topMes.forEachIndexed { index, item ->
                            val corBarra = coresTop[index % coresTop.size]
                            HorizontalBarChartItem(
                                label = item.nome,
                                value = item.receita,
                                maxValue = maxReceitaTop,
                                color = corBarra,
                                sharedViewModel = sharedViewModel
                            )
                        }
                    }
                }
            }
        }

        // Gráfico 3.5: Vendas por Categoria (mês selecionado)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Summarize, contentDescription = "Vendas por Categoria", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Vendas por Categoria (Mes)", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    val maxVendaCat = vendasPorCategoria.maxOfOrNull { it.total } ?: 1.0
                    val coresCat = listOf(Orange, Purple, Blue, Green, Primary)

                    if (vendasPorCategoria.isEmpty()) {
                        Text("Nenhuma venda no período.", fontSize = 12.sp, color = TextColor, fontStyle = FontStyle.Italic)
                    } else {
                        vendasPorCategoria.forEachIndexed { index, item ->
                            val corBarra = coresCat[index % coresCat.size]
                            HorizontalBarChartItem(
                                label = item.categoria ?: "Sem Categoria",
                                value = item.total,
                                maxValue = maxVendaCat,
                                color = corBarra,
                                sharedViewModel = sharedViewModel
                            )
                        }
                    }
                }
            }
        }

        // Gráfico 4: Despesas por categoria (mês selecionado)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Despesas por Categoria", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Despesas por Categoria (Mes)", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    val maxDespesaCat = despesasCategorias.maxOfOrNull { it.valor } ?: 1.0
                    val coresDesp = listOf(Red, Orange, Purple, Blue, Green)

                    if (despesasCategorias.isEmpty()) {
                        Text("Nenhuma despesa no período.", fontSize = 12.sp, color = TextColor, fontStyle = FontStyle.Italic)
                    } else {
                        despesasCategorias.forEachIndexed { index, item ->
                            val corBarra = coresDesp[index % coresDesp.size]
                            HorizontalBarChartItem(
                                label = item.categoria,
                                value = item.valor,
                                maxValue = maxDespesaCat,
                                color = corBarra,
                                sharedViewModel = sharedViewModel
                            )
                        }
                    }
                }
            }
        }

        // Seção: Resumo de Estoque (Alerta de Reposição)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estoque", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Alertas de Estoque", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    val produtosBaixos = estoqueResumo.filter { it.quantidadeEstoque <= it.estoqueMinimo }
                    
                    if (produtosBaixos.isEmpty()) {
                        Text("Todos os produtos com estoque em dia.", fontSize = 12.sp, color = Green, fontWeight = FontWeight.Bold)
                    } else {
                        produtosBaixos.forEach { prod ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(prod.nome, fontSize = 13.sp, color = TextColor)
                                Text("${prod.quantidadeEstoque} un.", fontSize = 13.sp, color = Red, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("(Reponha estes itens em breve)", fontSize = 10.sp, color = TextColor, fontStyle = FontStyle.Italic)
                    }
                }
            }
        }

        // Resumo do Mês Selecionado
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Summarize, contentDescription = "Resumo Mes", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Resumo do Mes (${mesSelecionado.format(DateTimeFormatter.ofPattern("MM/yyyy"))})", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    ResumoItem("Vendas realizadas:", "$qtdVendasMes", TextColor)
                    ResumoItem("Faturamento bruto:", sharedViewModel.fmtReal(faturamentoMes), Green)
                    ResumoItem("Despesas:", sharedViewModel.fmtReal(despesasMes), Red)
                    ResumoItem("Taxa Cartao:", sharedViewModel.fmtReal(taxaCartaoMes), Purple)
                    ResumoItem("Lucro (Saldo):", sharedViewModel.fmtReal(saldoMes), if (saldoMes >= 0) Green else Red)
                    ResumoItem("Ticket Medio:", sharedViewModel.fmtReal(ticketMedio), TextColor)
                }
            }
        }

        // Comparativo com Mês Anterior
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Comparativo Mes Anterior", tint = Secondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Comparativo com Mes Anterior", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    ComparativoItem("Faturamento:", faturamentoMes, faturamentoMesAnterior, sharedViewModel)
                    ComparativoItem("Despesas:", despesasMes, despesasMesAnterior, sharedViewModel)
                    ComparativoItem("Saldo:", saldoMes, saldoMesAnterior, sharedViewModel)
                    ComparativoItem("Ticket Medio:", ticketMedio, ticketMedioAnterior, sharedViewModel)
                }
            }
        }

        // Fechamentos Recentes
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Fechamentos Recentes",
                fontSize = 18.sp,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
        }

        if (fechamentosRecentes.isEmpty()) {
            item {
                Text("Nenhum fechamento registrado.", fontSize = 13.sp, color = TextColor, fontStyle = FontStyle.Italic)
            }
        } else {
            items(fechamentosRecentes) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Primary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("Data: ${it.data}", fontSize = 14.sp, color = TextColor, fontWeight = FontWeight.Bold)
                            Text("Vendas: ${it.qtdVendas} | Faturamento: ${sharedViewModel.fmtReal(it.faturamento)}", fontSize = 11.sp, color = TextColor)
                            Text("Despesas: ${sharedViewModel.fmtReal(it.despesas)} | Saldo: ${sharedViewModel.fmtReal(it.saldo)}", fontSize = 11.sp, color = TextColor)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun HorizontalBarChartItem(label: String, value: Double, maxValue: Double, color: Color, sharedViewModel: SharedViewModel) {
    val proportion = if (maxValue > 0) (value / maxValue).toFloat() else 0f
    val barWidth = (proportion * 200).dp // Max width 200dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(label, fontSize = 11.sp, color = TextColor, modifier = Modifier.width(55.dp))
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(barWidth).height(22.dp).background(color, RoundedCornerShape(5.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(sharedViewModel.fmtReal(value), fontSize = 11.sp, color = TextColor, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun PaymentMethodBarItem(label: String, percentage: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(5.dp)))
        Text(label, fontSize = 11.sp, color = TextColor, modifier = Modifier.width(90.dp))
        val barWidth = (percentage / 100 * 160).dp // Max width 160dp
        Spacer(modifier = Modifier.width(barWidth).height(20.dp).background(color, RoundedCornerShape(5.dp)))
        Text("${String.format("%.0f", percentage)}%", fontSize = 11.sp, color = TextColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ComparativoItem(label: String, atual: Double, anterior: Double, sharedViewModel: SharedViewModel) {
    val variacao = if (anterior > 0) ((atual - anterior) / anterior) * 100 else if (atual > 0) 100.0 else 0.0
    val seta = if (variacao >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown
    val corVar = if (variacao >= 0) Green else Red
    val sinal = if (variacao >= 0) "+" else ""

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextColor, modifier = Modifier.weight(1f))
        Text(sharedViewModel.fmtReal(atual), fontSize = 13.sp, color = TextColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(seta, contentDescription = "Tendencia", tint = corVar, modifier = Modifier.size(16.dp))
        Text("$sinal${String.format("%.0f", variacao)}%", fontSize = 11.sp, color = corVar, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(2.dp))
}

@Composable
private fun ResumoItem(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = TextColor)
        Text(value, fontSize = 14.sp, color = valueColor, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(2.dp))
}
