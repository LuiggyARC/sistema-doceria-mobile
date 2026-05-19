package com.doceriadaduda.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.model.Produto
import com.doceriadaduda.viewmodel.EstoqueViewModel
import com.doceriadaduda.viewmodel.SharedViewModel

@Composable
fun AddProdutoDialog(
    onDismiss: () -> Unit,
    estoqueViewModel: EstoqueViewModel = AppModule.estoqueViewModel,
    sharedViewModel: SharedViewModel = AppModule.sharedViewModel
) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Geral") }
    var precoVenda by remember { mutableStateOf("") }
    var quantidadeEstoque by remember { mutableStateOf("") }
    var estoqueMinimo by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Novo Produto") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Produto") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoria") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = precoVenda,
                    onValueChange = { precoVenda = it },
                    label = { Text("Preco de Venda (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantidadeEstoque,
                    onValueChange = { quantidadeEstoque = it },
                    label = { Text("Quantidade em Estoque") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = estoqueMinimo,
                    onValueChange = { estoqueMinimo = it },
                    label = { Text("Estoque Minimo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val newProduto = Produto(
                    nome = nome,
                    categoria = categoria,
                    precoVenda = sharedViewModel.parseValor(precoVenda),
                    quantidadeEstoque = quantidadeEstoque.toIntOrNull() ?: 0,
                    estoqueMinimo = estoqueMinimo.toIntOrNull() ?: 5,
                    ativo = true
                )
                estoqueViewModel.addProduto(newProduto)
                onDismiss()
            }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
