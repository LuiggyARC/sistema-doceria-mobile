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
import androidx.compose.material.icons.filled.ReceiptLong
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
import com.doceriadaduda.ui.theme.Red
import com.doceriadaduda.ui.theme.Secondary
import com.doceriadaduda.ui.theme.TextColor
import com.doceriadaduda.viewmodel.DespesaViewModel
import com.doceriadaduda.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DespesaScreen(despesaViewModel: DespesaViewModel = AppModule.despesaViewModel,
                  sharedViewModel: SharedViewModel = AppModule.sharedViewModel) {

    val ultimasDespesas by despesaViewModel.ultimasDespesas.collectAsState()
    val mensagemStatus by despesaViewModel.mensagemStatus.collectAsState()
    val mensagemStatusColor by despesaViewModel.mensagemStatusColor.collectAsState()

    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categorias = listOf(
        "Ingredientes", "Embalagens", "Aluguel", "Contas de Consumo",
        "Salarios", "Marketing", "Manutencao", "Transporte", "Outros"
    )
    var selectedCategoria by remember { mutableStateOf(categorias[0]) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Registrar Despesa",
                fontSize = 24.sp,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descricao") },
                modifier = Modifier.fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Secondary,
                    unfocusedBorderColor = Primary
                )
            )
        }

        item {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Secondary,
                        unfocusedBorderColor = Primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                selectedCategoria = categoria
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Secondary,
                    unfocusedBorderColor = Primary
                )
            )
        }

        item {
            Button(
                onClick = { despesaViewModel.registrarDespesa(descricao, selectedCategoria, valor) },
                modifier = Modifier.fillMaxWidth().height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = "Registrar Despesa", tint = Color.White)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Registrar Despesa", color = Color.White, fontSize = 16.sp)
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
                text = "Ultimas Despesas",
                fontSize = 18.sp,
                color = Secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(color = Secondary, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
        }

        if (ultimasDespesas.isEmpty()) {
            item {
                Text("Nenhuma despesa registrada.", fontSize = 13.sp, color = TextColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        } else {
            items(ultimasDespesas) {
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
                            Text(it.descricao, fontSize = 14.sp, color = TextColor, fontWeight = FontWeight.Bold)
                            Text("${it.categoria ?: "Geral"} | ${it.data}", fontSize = 11.sp, color = TextColor)
                        }
                        Text(sharedViewModel.fmtReal(it.valor), fontSize = 14.sp, color = Red, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
