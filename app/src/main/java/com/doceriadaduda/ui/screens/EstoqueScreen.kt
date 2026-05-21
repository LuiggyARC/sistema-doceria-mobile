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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.doceriadaduda.model.Produto
import com.doceriadaduda.ui.theme.Blue
import com.doceriadaduda.ui.theme.Green
import com.doceriadaduda.ui.theme.Orange
import com.doceriadaduda.ui.theme.Primary
import com.doceriadaduda.ui.theme.Red
import com.doceriadaduda.ui.theme.Secondary
import com.doceriadaduda.ui.theme.TextColor
import com.doceriadaduda.viewmodel.EstoqueViewModel
import com.doceriadaduda.viewmodel.SharedViewModel

@Composable
fun EstoqueScreen(estoqueViewModel: EstoqueViewModel = AppModule.estoqueViewModel,
                  sharedViewModel: SharedViewModel = AppModule.sharedViewModel) {

    val produtosAtivos by estoqueViewModel.produtosAtivos.collectAsState()
    val mensagemStatus by estoqueViewModel.mensagemStatus.collectAsState()
    val mensagemStatusColor by estoqueViewModel.mensagemStatusColor.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Estoque / Cardápio",
                fontSize = 24.sp,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            Button(
                onClick = { showAddProductDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Produto", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Novo Produto", color = Color.White, fontSize = 16.sp)
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

        if (produtosAtivos.isEmpty()) {
            item {
                Text("Nenhum produto cadastrado.", fontSize = 13.sp, color = TextColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        } else {
            items(produtosAtivos) {
                ProdutoItem(it, estoqueViewModel, sharedViewModel)
            }
        }
    }

    if (showAddProductDialog) {
        AddProdutoDialog(
            onDismiss = { showAddProductDialog = false },
            estoqueViewModel = estoqueViewModel
        )
    }
}

@Composable
fun ProdutoItem(produto: Produto, estoqueViewModel: EstoqueViewModel, sharedViewModel: SharedViewModel) {
    var showEditPanel by remember { mutableStateOf(false) }
    var showWasteDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var qtdRepor by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, Primary)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(produto.nome, fontSize = 15.sp, color = TextColor, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Preço: ${sharedViewModel.fmtReal(produto.precoVenda)} | ${produto.categoria ?: "Geral"}",
                        fontSize = 11.sp,
                        color = TextColor
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Estoque",
                            tint = if (produto.quantidadeEstoque <= produto.estoqueMinimo) Red else Green,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Estoque: ${produto.quantidadeEstoque}",
                            fontSize = 12.sp,
                            color = if (produto.quantidadeEstoque <= produto.estoqueMinimo) Red else Green,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = { showWasteDialog = true }) {
                    Icon(Icons.Default.RestoreFromTrash, contentDescription = "Desperdício", tint = Orange)
                }
                IconButton(onClick = { showEditPanel = !showEditPanel }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Secondary)
                }
                IconButton(onClick = { showConfirmDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Red)
                }
            }

            if (showEditPanel) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Secondary.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                var novoNome by remember { mutableStateOf(produto.nome) }
                var novoPreco by remember { mutableStateOf(produto.precoVenda.toString()) }
                var novaCategoria by remember { mutableStateOf(produto.categoria ?: "Geral") }

                OutlinedTextField(
                    value = novoNome,
                    onValueChange = { novoNome = it },
                    label = { Text("Novo nome") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = novoPreco,
                    onValueChange = { novoPreco = it },
                    label = { Text("Novo preço") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = novaCategoria,
                    onValueChange = { novaCategoria = it },
                    label = { Text("Categoria") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { estoqueViewModel.salvarEdicaoProduto(produto.id, produto.nome, novoNome, novoPreco, novaCategoria) },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvar Alterações")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Reposição de Estoque", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = qtdRepor,
                        onValueChange = { qtdRepor = it },
                        label = { Text("Qtd") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { estoqueViewModel.reporEstoque(produto.id, produto.nome, qtdRepor) },
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Repor")
                    }
                }
            }
        }
    }

    if (showWasteDialog) {
        var qtdWaste by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showWasteDialog = false },
            title = { Text("Registrar Desperdício") },
            text = {
                Column {
                    Text("Produto: ${produto.nome}")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = qtdWaste,
                        onValueChange = { qtdWaste = it },
                        label = { Text("Quantidade perdida") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val qtd = qtdWaste.toIntOrNull() ?: 0
                    if (qtd > 0) {
                        estoqueViewModel.registrarDesperdicio(produto.id, produto.nome, qtd)
                        showWasteDialog = false
                    }
                }) {
                    Text("Confirmar", color = Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWasteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showConfirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            title = { Text("Confirmar exclusão") },
            text = { Text("Deseja remover \'${produto.nome}\' do cardápio?") },
            confirmButton = {
                TextButton(onClick = { estoqueViewModel.excluirProduto(produto.id, produto.nome); showConfirmDeleteDialog = false }) {
                    Text("Excluir", color = Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AddProdutoDialog(onDismiss: () -> Unit, estoqueViewModel: EstoqueViewModel) {
    var nome by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var estoqueMinimo by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Produto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome do Produto") })
                OutlinedTextField(
                    value = preco, 
                    onValueChange = { preco = it }, 
                    label = { Text("Preço de Venda") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoria (ex: Bolos)") })
                OutlinedTextField(value = estoqueMinimo, onValueChange = { estoqueMinimo = it }, label = { Text("Aviso de Estoque Baixo (Qtd)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(onClick = {
                estoqueViewModel.adicionarProduto(nome, preco, categoria, estoqueMinimo)
                onDismiss()
            }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
