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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.ui.theme.Background
import com.doceriadaduda.ui.theme.Primary
import com.doceriadaduda.ui.theme.Secondary
import com.doceriadaduda.ui.theme.TextColor
import com.doceriadaduda.viewmodel.SharedViewModel
import com.doceriadaduda.viewmodel.VendaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendaScreen(vendaViewModel: VendaViewModel = AppModule.vendaViewModel,
                sharedViewModel: SharedViewModel = AppModule.sharedViewModel) {

    val produtosAtivos by vendaViewModel.produtosAtivos.collectAsState()
    val vendasHoje by vendaViewModel.vendasHoje.collectAsState()
    val mensagemStatus by vendaViewModel.mensagemStatus.collectAsState()
    val mensagemStatusColor by vendaViewModel.mensagemStatusColor.collectAsState()

    var selectedProduto by remember { mutableStateOf<String?>(null) }
    var quantidade by remember { mutableStateOf("") }
    var expandedProduto by remember { mutableStateOf(false) }
    var expandedPagamento by remember { mutableStateOf(false) }
    val formasPagamento = listOf("Dinheiro", "PIX", "Cartao Debito", "Cartao Credito")
    var selectedFormaPagamento by remember { mutableStateOf(formasPagamento[0]) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        item {
            Text(
                text = "Registrar Venda",
                fontSize = 24.sp,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            ExposedDropdownMenuBox(
                expanded = expandedProduto,
                onExpandedChange = { expandedProduto = !expandedProduto },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedProduto ?: "Selecione um produto",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Produto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProduto) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Secondary,
                        unfocusedBorderColor = Primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedProduto,
                    onDismissRequest = { expandedProduto = false }
                ) {
                    produtosAtivos.forEach { produto ->
                        DropdownMenuItem(
                            text = { Text(produto) },
                            onClick = {
                                selectedProduto = produto
                                expandedProduto = false
                            }
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = quantidade,
                onValueChange = { quantidade = it },
                label = { Text("Quantidade") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Secondary,
                    unfocusedBorderColor = Primary
                )
            )
        }

        item {
            ExposedDropdownMenuBox(
                expanded = expandedPagamento,
                onExpandedChange = { expandedPagamento = !expandedPagamento },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedFormaPagamento,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Forma de Pagamento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPagamento) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Secondary,
                        unfocusedBorderColor = Primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedPagamento,
                    onDismissRequest = { expandedPagamento = false }
                ) {
                    formasPagamento.forEach { forma ->
                        DropdownMenuItem(
                            text = { Text(forma) },
                            onClick = {
                                selectedFormaPagamento = forma
                                expandedPagamento = false
                            }
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = { vendaViewModel.registrarVenda(selectedProduto, quantidade, selectedFormaPagamento) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Finalizar Venda", tint = Color.White)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Finalizar Venda", color = Color.White, fontSize = 16.sp)
            }
        }

        item {
            if (mensagemStatus.isNotBlank()) {
                Text(
                    text = mensagemStatus,
                    color = Color(mensagemStatusColor),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Vendas de Hoje",
                fontSize = 18.sp,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
        }

        if (vendasHoje.isEmpty()) {
            item {
                Text("Nenhuma venda hoje.", fontSize = 13.sp, color = TextColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        } else {
            items(vendasHoje) {
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
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Venda", tint = Secondary, modifier = Modifier.size(16.dp))
                        Column(
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(it.produtoId.toString(), fontSize = 14.sp, color = TextColor, fontWeight = FontWeight.Bold) // TODO: Get product name
                            Text("x${it.quantidade} | ${it.formaPagamento}", fontSize = 11.sp, color = TextColor)
                        }
                        Text(sharedViewModel.fmtReal(it.valorTotal), fontSize = 14.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
