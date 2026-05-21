package com.doceriadaduda.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.ui.theme.Primary
import com.doceriadaduda.ui.theme.Secondary
import com.doceriadaduda.ui.theme.TextColor
import com.doceriadaduda.viewmodel.SharedViewModel
import com.doceriadaduda.viewmodel.VendaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendaScreen(vendaViewModel: VendaViewModel = AppModule.vendaViewModel,
                sharedViewModel: SharedViewModel = AppModule.sharedViewModel) {

    val vendasHoje by vendaViewModel.vendasHoje.collectAsState()
    val produtosCompletos by vendaViewModel.produtosCompletos.collectAsState()
    val mensagemStatus by vendaViewModel.mensagemStatus.collectAsState()
    val mensagemStatusColor by vendaViewModel.mensagemStatusColor.collectAsState()
    val isAguardandoPagamento by vendaViewModel.isAguardandoPagamento.collectAsState()
    val showNoDeviceDialog by vendaViewModel.showNoDeviceDialog.collectAsState()

    var showVendaDialog by remember { mutableStateOf(false) }

    if (showNoDeviceDialog) {
        AlertDialog(
            onDismissRequest = { vendaViewModel.cancelarVendaManual() },
            title = { Text("Dispositivo não encontrado") },
            text = { Text("Não foi encontrado nenhum dispositivo conectado. Deseja continuar e inserir manualmente?") },
            confirmButton = {
                Button(onClick = { 
                    vendaViewModel.registrarVendaManual {
                        showVendaDialog = false
                    }
                }) {
                    Text("Continuar Manual")
                }
            },
            dismissButton = {
                TextButton(onClick = { vendaViewModel.cancelarVendaManual() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Vendas",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showVendaDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nova Venda")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.primary)

        if (mensagemStatus.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(mensagemStatusColor).copy(alpha = 0.1f))
            ) {
                Text(
                    text = mensagemStatus,
                    color = Color(mensagemStatusColor),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = "Vendas de Hoje",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (vendasHoje.isEmpty()) {
                item {
                    Text("Nenhuma venda hoje.", fontSize = 13.sp, color = TextColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            } else {
                items(vendasHoje) { venda ->
                    val prodNome = produtosCompletos.find { it.id == venda.produtoId }?.nome ?: "Produto #${venda.produtoId}"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(prodNome, fontWeight = FontWeight.Bold, color = TextColor)
                                Text("x${venda.quantidade} | ${venda.formaPagamento}", fontSize = 12.sp, color = TextColor.copy(alpha = 0.7f))
                            }
                            Text(sharedViewModel.fmtReal(venda.valorTotal), fontWeight = FontWeight.Black, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }

    if (showVendaDialog) {
        QuickVendaDialog(
            produtos = produtosCompletos,
            isAguardandoPagamento = isAguardandoPagamento,
            onDismiss = { if (!isAguardandoPagamento) showVendaDialog = false },
            onConfirm = { produto, qtd, pagamento ->
                vendaViewModel.registrarVenda(produto.nome, qtd.toString(), pagamento) {
                    showVendaDialog = false
                }
            },
            sharedViewModel = sharedViewModel
        )
    }
}

@Composable
fun QuickVendaDialog(
    produtos: List<com.doceriadaduda.model.Produto>,
    isAguardandoPagamento: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (com.doceriadaduda.model.Produto, Int, String) -> Unit,
    sharedViewModel: SharedViewModel
) {
    var step by remember { mutableStateOf(1) }
    var selectedProduto by remember { mutableStateOf<com.doceriadaduda.model.Produto?>(null) }
    var quantidade by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                if (isAguardandoPagamento) "Aguardando Maquininha..." 
                else if (step == 1) "Selecione o Produto" 
                else "Finalizar Venda: ${selectedProduto?.nome}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp, max = 400.dp), contentAlignment = Alignment.Center) {
                if (isAguardandoPagamento) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Green)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Siga as instruções na maquininha", textAlign = TextAlign.Center)
                    }
                } else if (step == 1) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(produtos) { produto ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        selectedProduto = produto
                                        step = 2 
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(produto.nome, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1)
                                    Text(sharedViewModel.fmtReal(produto.precoVenda), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    selectedProduto?.let { prod ->
                        Column {
                            Text("Preço Unitário: ${sharedViewModel.fmtReal(prod.precoVenda)}", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton(onClick = { if (quantidade > 1) quantidade-- }) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = Red)
                                }
                                Text(
                                    text = quantidade.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                IconButton(onClick = { if (quantidade < prod.quantidadeEstoque) quantidade++ }) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Green)
                                }
                            }
                            Text(
                                "Estoque disponível: ${prod.quantidadeEstoque}",
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = if (prod.quantidadeEstoque < 5) Red else TextColor
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Total: ${sharedViewModel.fmtReal(prod.precoVenda * quantidade)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Green, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Forma de Pagamento:", fontWeight = FontWeight.Bold)
                            
                            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PaymentButton("Dinheiro", Modifier.weight(1f)) { onConfirm(prod, quantidade, "Dinheiro") }
                                PaymentButton("PIX", Modifier.weight(1f)) { onConfirm(prod, quantidade, "PIX") }
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PaymentButton("Débito", Modifier.weight(1f)) { onConfirm(prod, quantidade, "Cartao Debito") }
                                PaymentButton("Crédito", Modifier.weight(1f)) { onConfirm(prod, quantidade, "Cartao Credito") }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (step == 2 && !isAguardandoPagamento) {
                TextButton(onClick = { step = 1 }) { Text("Voltar") }
            }
        },
        dismissButton = {
            if (!isAguardandoPagamento) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        }
    )
}

@Composable
fun PaymentButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (label == "Dinheiro") Green else MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(label, fontSize = 12.sp, color = Color.White)
    }
}

val Green = Color(0xFF4CAF50)
val Red = Color(0xFFE53935)
