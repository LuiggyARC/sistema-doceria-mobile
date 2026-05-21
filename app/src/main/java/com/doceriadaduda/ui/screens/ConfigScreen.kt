package com.doceriadaduda.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Bluetooth
import androidx.core.content.ContextCompat
import com.doceriadaduda.data.payment.PaymentProvider
import com.doceriadaduda.di.AppModule

@Composable
fun ConfigScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val paymentManager = remember { AppModule.paymentManager }
    
    var mpPublicKey by remember { 
        mutableStateOf(prefs.getString("mp_public_key", "") ?: "") 
    }
    
    var statusMessage by remember { mutableStateOf("") }
    var selectedProvider by remember { 
        val savedProvider = prefs.getString("preferred_payment_provider", PaymentProvider.MERCADO_PAGO.name)
        mutableStateOf(try { PaymentProvider.valueOf(savedProvider!!) } catch(e: Exception) { PaymentProvider.MERCADO_PAGO })
    }

    // Launcher para ativar Bluetooth
    val bluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            statusMessage = "Bluetooth ativado! Iniciando busca..."
            paymentManager.iniciarPareamentoBluetooth(context, selectedProvider)
        } else {
            statusMessage = "O Bluetooth precisa estar ativado para conectar."
        }
    }

    // Launcher para permissões
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // Se as permissões foram dadas, tenta ativar o Bluetooth se necessário
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothLauncher.launch(enableBtIntent)
            } else {
                paymentManager.iniciarPareamentoBluetooth(context, selectedProvider)
            }
        } else {
            statusMessage = "Permissões de Bluetooth/Localização negadas."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configurações",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Seção de Seleção de Maquininha
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Provedor de Pagamento",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ProviderButton("MP", selectedProvider == PaymentProvider.MERCADO_PAGO) { 
                        selectedProvider = PaymentProvider.MERCADO_PAGO 
                    }
                    ProviderButton("Caixa", selectedProvider == PaymentProvider.CAIXA_AZULZINHA) { 
                        selectedProvider = PaymentProvider.CAIXA_AZULZINHA 
                    }
                    ProviderButton("Stone", selectedProvider == PaymentProvider.STONE) { 
                        selectedProvider = PaymentProvider.STONE 
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (selectedProvider != PaymentProvider.CAIXA_AZULZINHA) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Conexão Bluetooth",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Conecte seu app com a maquininha via Bluetooth.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                arrayOf(
                                    Manifest.permission.BLUETOOTH_SCAN,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                )
                            } else {
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            }

                            val hasPermissions = permissions.all {
                                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                            }

                            if (hasPermissions) {
                                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                                if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
                                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                    bluetoothLauncher.launch(enableBtIntent)
                                } else {
                                    paymentManager.iniciarPareamentoBluetooth(context, selectedProvider)
                                    statusMessage = "Buscando maquininha ${selectedProvider.name}..."
                                }
                            } else {
                                permissionLauncher.launch(permissions)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Bluetooth, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Conectar via Bluetooth")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Conexão Caixa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A maquininha Azulzinha da Caixa não requer conexão Bluetooth direta.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Integração Mercado Pago",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Insira sua Public Key para ativar a maquininha.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = mpPublicKey,
                    onValueChange = { mpPublicKey = it },
                    label = { Text("Public Key (TEST-...)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                    singleLine = true
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                prefs.edit()
                    .putString("mp_public_key", mpPublicKey)
                    .putString("preferred_payment_provider", selectedProvider.name)
                    .apply()
                statusMessage = "Configurações salvas!"
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Salvar Configurações")
        }
        
        if (statusMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = statusMessage, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProviderButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(label, color = if (isSelected) Color.White else Color.Black)
    }
}
