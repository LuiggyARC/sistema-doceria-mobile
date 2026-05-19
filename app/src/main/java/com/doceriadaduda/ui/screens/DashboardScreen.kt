package com.doceriadaduda.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.ui.theme.Background
import com.doceriadaduda.ui.theme.CardBackground
import com.doceriadaduda.ui.theme.Green
import com.doceriadaduda.ui.theme.Orange
import com.doceriadaduda.ui.theme.Primary
import com.doceriadaduda.ui.theme.Purple
import com.doceriadaduda.ui.theme.Red
import com.doceriadaduda.ui.theme.Secondary
import com.doceriadaduda.ui.theme.TextColor
import com.doceriadaduda.viewmodel.DashboardViewModel
import com.doceriadaduda.viewmodel.SharedViewModel

@Composable
fun DashboardScreen(dashboardViewModel: DashboardViewModel = AppModule.dashboardViewModel,
                    sharedViewModel: SharedViewModel = AppModule.sharedViewModel) {

    val vendasHoje by dashboardViewModel.vendasHoje.collectAsState()
    val vendasMes by dashboardViewModel.vendasMes.collectAsState()
    val estoqueBaixo by dashboardViewModel.estoqueBaixo.collectAsState()
    val despesasHoje by dashboardViewModel.despesasHoje.collectAsState()
    val qtdVendasHoje by dashboardViewModel.qtdVendasHoje.collectAsState()
    val taxaCartaoHoje by dashboardViewModel.taxaCartaoHoje.collectAsState()
    val topVendidosDia by dashboardViewModel.topVendidosDia.collectAsState()
    val caixaFechadoHoje by dashboardViewModel.caixaFechadoHoje.collectAsState()

    val saldoDia = vendasHoje - despesasHoje
    val saldoCor = if (saldoDia >= 0) Green else Red

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Doceria da Duda",
                fontSize = 26.sp,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Painel de Controle",
                fontSize = 14.sp,
                color = TextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Vendas Hoje",
                    value = sharedViewModel.fmtReal(vendasHoje),
                    icon = Icons.Default.PointOfSale,
                    color = Green,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Vendas no Mes",
                    value = sharedViewModel.fmtReal(vendasMes),
                    icon = Icons.Default.CalendarMonth,
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Estoque Baixo",
                    value = "$estoqueBaixo itens",
                    icon = Icons.Default.WarningAmber,
                    color = if (estoqueBaixo > 0) Orange else Green,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Taxa Cartao",
                    value = sharedViewModel.fmtReal(taxaCartaoHoje),
                    icon = Icons.Default.CreditCard,
                    color = Purple,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(5.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (caixaFechadoHoje) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Status Caixa",
                        tint = Secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (caixaFechadoHoje) "Caixa FECHADO hoje" else "Caixa ABERTO",
                        fontSize = 13.sp,
                        color = if (caixaFechadoHoje) Red else Green,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }

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
                        Text("Resumo do Dia", fontSize = 16.sp, color = Secondary, fontWeight = FontWeight.Bold)
                    }
                    Divider(color = Primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    ResumoItem("Vendas realizadas:", "$qtdVendasHoje", TextColor)
                    ResumoItem("Faturamento bruto:", sharedViewModel.fmtReal(vendasHoje), Green)
                    ResumoItem("Despesas:", sharedViewModel.fmtReal(despesasHoje), Red)
                    ResumoItem("Saldo do dia:", sharedViewModel.fmtReal(saldoDia), saldoCor)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Top 5 Produtos Vendidos Hoje", fontSize = 18.sp, color = Secondary, fontWeight = FontWeight.Bold)
            Divider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
        }

        if (topVendidosDia.isEmpty()) {
            item {
                Text("Nenhuma venda registrada hoje.", fontSize = 13.sp, color = TextColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        } else {
            items(topVendidosDia) {
                TopVendidoItem(it.first, it.second)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.5.dp, color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            Text(title, fontSize = 11.sp, color = TextColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(value, fontSize = 14.sp, color = color, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ResumoItem(label: String, value: String, valueColor: Color) {
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

@Composable
fun TopVendidoItem(nome: String, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Produto", tint = Secondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(nome, fontSize = 14.sp, color = TextColor, modifier = Modifier.weight(1f))
            Text("$total un.", fontSize = 14.sp, color = Secondary, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}
