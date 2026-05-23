package com.doceriadaduda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doceriadaduda.di.AppModule
import com.doceriadaduda.model.Empresa
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    val empresaRepository = AppModule.empresaRepository
    val empresas by empresaRepository.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Painel Administrativo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("Gerenciamento de Empresas e Clientes", fontSize = 14.sp)
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(empresas) { empresa ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (empresa.ativa) MaterialTheme.colorScheme.surface else Color.LightGray.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(empresa.nome, fontWeight = FontWeight.Bold)
                            Text(empresa.email, fontSize = 12.sp)
                        }
                        
                        Switch(
                            checked = empresa.ativa,
                            onCheckedChange = { isChecked ->
                                scope.launch {
                                    empresaRepository.update(empresa.copy(ativa = isChecked))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
