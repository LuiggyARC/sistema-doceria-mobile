package com.doceriadaduda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.viewmodel.VendaViewModel
import com.doceriadaduda.viewmodel.SharedViewModel

@Composable
fun HistoricoVendasScreen(
    vendaViewModel: VendaViewModel = AppModule.vendaViewModel,
    sharedViewModel: SharedViewModel = AppModule.sharedViewModel
) {
    val vendas by vendaViewModel.vendasHoje.collectAsState() // Por enquanto usando hoje, mas ViewModel pode ser expandido

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Histórico de Vendas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vendas) { venda ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Venda #${venda.id}", fontWeight = FontWeight.Bold)
                            Text(venda.dataVenda, fontSize = 12.sp, color = Color.Gray)
                        }
                        Text(
                            sharedViewModel.fmtReal(venda.valorTotal),
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
