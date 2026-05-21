package com.doceriadaduda.ui.screens

import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.ui.theme.Background
import com.doceriadaduda.ui.theme.CardBackground
import com.doceriadaduda.ui.theme.Green
import com.doceriadaduda.ui.theme.LocalDynamicThemeState
import com.doceriadaduda.ui.theme.Orange
import com.doceriadaduda.ui.theme.Red
import com.doceriadaduda.ui.theme.TextColor
import com.doceriadaduda.viewmodel.DashboardViewModel
import com.doceriadaduda.viewmodel.SharedViewModel

@Composable
fun DashboardScreen(
    companyName: String = "Pai D’égua Hub",
    dashboardViewModel: DashboardViewModel = AppModule.dashboardViewModel,
    sharedViewModel: SharedViewModel = AppModule.sharedViewModel
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        dashboardViewModel.init(context)
    }

    val vendasHoje by dashboardViewModel.vendasHoje.collectAsState()
    val vendasMes by dashboardViewModel.vendasMes.collectAsState()
    val despesasMes by dashboardViewModel.despesasMes.collectAsState()
    val metaMes by dashboardViewModel.metaMes.collectAsState()
    val estoqueBaixo by dashboardViewModel.estoqueBaixo.collectAsState()
    val despesasHoje by dashboardViewModel.despesasHoje.collectAsState()
    val qtdVendasHoje by dashboardViewModel.qtdVendasHoje.collectAsState()
    val topVendidosDia by dashboardViewModel.topVendidosDia.collectAsState()
    val caixaFechadoHoje by dashboardViewModel.caixaFechadoHoje.collectAsState()

    val lucroMes = vendasMes - despesasMes
    val saldoDia = vendasHoje - despesasHoje
    val saldoCor = if (saldoDia >= 0) Green else Red
    
    val progressoMeta = if (metaMes > 0) (vendasMes / metaMes).toFloat().coerceIn(0f, 1f) else 0f
    
    var showMetaDialog by remember { mutableStateOf(false) }
    var metaInput by remember { mutableStateOf(metaMes.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = companyName,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Painel de Controle",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Vendas Mes",
                    value = sharedViewModel.fmtReal(vendasMes),
                    icon = Icons.Default.Payments,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Lucro do Mes",
                    value = sharedViewModel.fmtReal(lucroMes),
                    icon = Icons.Default.Savings,
                    color = if (lucroMes >= 0) Green else Red,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Meta de Vendas: ${sharedViewModel.fmtReal(metaMes)}", fontWeight = FontWeight.Bold, color = TextColor)
                        }
                        IconButton(onClick = { 
                            metaInput = metaMes.toString()
                            showMetaDialog = true 
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Meta", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progressoMeta },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                        color = if (progressoMeta >= 1f) Green else MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(progressoMeta * 100).toInt()}% da meta atingida",
                        fontSize = 12.sp,
                        color = TextColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
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
                    title = "Estoque Baixo",
                    value = "$estoqueBaixo itens",
                    icon = Icons.Default.WarningAmber,
                    color = if (estoqueBaixo > 0) Orange else Green,
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = if (caixaFechadoHoje) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Status Caixa",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (caixaFechadoHoje) "Caixa FECHADO hoje" else "Caixa ABERTO",
                            fontSize = 13.sp,
                            color = if (caixaFechadoHoje) Red else Green,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (!caixaFechadoHoje) {
                        Button(
                            onClick = { dashboardViewModel.fecharCaixa() },
                            colors = ButtonDefaults.buttonColors(containerColor = Red),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Fechar Caixa", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Summarize, contentDescription = "Resumo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Resumo do Dia", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                    ResumoItem("Vendas realizadas:", "$qtdVendasHoje", TextColor)
                    ResumoItem("Faturamento bruto:", sharedViewModel.fmtReal(vendasHoje), Green)
                    ResumoItem("Despesas:", sharedViewModel.fmtReal(despesasHoje), Red)
                    ResumoItem("Saldo do dia:", sharedViewModel.fmtReal(saldoDia), saldoCor)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Top 5 Produtos Vendidos Hoje", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
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

        item {
            val dynamicThemeState = LocalDynamicThemeState.current
            
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    dynamicThemeState.companyName = "Pai D’égua Hub"
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("company_name", "Pai D’égua Hub").apply()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
                border = BorderStroke(1.dp, Red),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voltar para Login / Trocar Nome")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showMetaDialog) {
        AlertDialog(
            onDismissRequest = { showMetaDialog = false },
            title = { Text("Definir Meta do Mes") },
            text = {
                OutlinedTextField(
                    value = metaInput,
                    onValueChange = { metaInput = it },
                    label = { Text("Valor da Meta (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val metaValue = metaInput.toDoubleOrNull() ?: 0.0
                    dashboardViewModel.salvarMeta(context, metaValue)
                    showMetaDialog = false
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMetaDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
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

@Composable
fun TopVendidoItem(nome: String, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Produto", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(nome, fontSize = 14.sp, color = TextColor, modifier = Modifier.weight(1f))
            Text("$total un.", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}
