package com.doceriadaduda.data.payment

import kotlinx.coroutines.delay

// Nota: Estes imports podem aparecer como erro até que o Gradle seja sincronizado
// import com.mercadopago.android.sdk.point.PointSdk
// import com.mercadopago.android.sdk.point.PaymentTool
// import com.mercadopago.android.sdk.point.PaymentMethod

enum class PaymentProvider { STONE, MERCADO_PAGO, CAIXA_AZULZINHA }

interface PaymentCallback {
    fun onSucesso(idTransacao: String)
    fun onError(mensagem: String)
    fun onStatus(mensagem: String)
}

class PaymentManager {
    
    fun isDispositivoConectado(context: android.content.Context, provider: PaymentProvider): Boolean {
        if (provider == PaymentProvider.CAIXA_AZULZINHA) return true

        val bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            android.util.Log.d("PaymentManager", "Bluetooth nulo ou desligado")
            return false
        }

        return try {
            val bondedDevices = bluetoothAdapter.bondedDevices
            if (bondedDevices.isNullOrEmpty()) {
                android.util.Log.d("PaymentManager", "Nenhum dispositivo pareado")
                return false
            }

            // Filtro rigoroso: apenas nomes conhecidos de terminais de pagamento
            val keywords = listOf("PAX", "MP-", "D150", "D175", "D180", "STONE", "GERTEC", "BTPOS", "PAGS")
            
            var maquininhaAtiva = false
            for (device in bondedDevices) {
                val name = device.name?.uppercase() ?: ""
                android.util.Log.d("PaymentManager", "Verificando dispositivo: $name")
                if (keywords.any { name.contains(it) }) {
                    android.util.Log.d("PaymentManager", "Dispositivo $name compatível. Checando conexão real...")
                    // Tenta verificar se o dispositivo está REALMENTE conectado agora
                    try {
                        val isConnectedMethod = device.javaClass.getMethod("isConnected")
                        val isConnected = isConnectedMethod.invoke(device) as Boolean
                        android.util.Log.d("PaymentManager", "Conexão real com $name: $isConnected")
                        if (isConnected) {
                            maquininhaAtiva = true
                            break
                        }
                    } catch (e: Exception) {
                        android.util.Log.d("PaymentManager", "Erro ao checar conexão real com $name. Usando fallback pareado.")
                        // Fallback: se não puder verificar conexão real, aceita o pareamento
                        maquininhaAtiva = true
                        break
                    }
                }
            }
            
            maquininhaAtiva
        } catch (e: SecurityException) {
            android.util.Log.e("PaymentManager", "Erro de segurança ao checar bluetooth", e)
            false
        }
    }

    suspend fun processarPagamento(
        provider: PaymentProvider,
        valor: Double,
        metodo: String,
        callback: PaymentCallback
    ) {
        when (provider) {
            PaymentProvider.MERCADO_PAGO -> {
                callback.onStatus("Conectando ao Point Mercado Pago...")
                delay(3000)
                callback.onSucesso("MP_${System.currentTimeMillis()}")
            }
            PaymentProvider.CAIXA_AZULZINHA -> {
                callback.onStatus("Conectando à Azulzinha da Caixa...")
                // Simulação da integração com SDK da Caixa (Bin/Fiserv ou PagSeguro)
                delay(3000)
                callback.onSucesso("CAIXA_${System.currentTimeMillis()}")
            }
            PaymentProvider.STONE -> {
                callback.onStatus("Iniciando Stone SDK...")
                delay(3000)
                callback.onSucesso("STONE_${System.currentTimeMillis()}")
            }
        }
    }

    fun iniciarPareamentoBluetooth(context: android.content.Context, provider: PaymentProvider) {
        if (provider == PaymentProvider.CAIXA_AZULZINHA) {
            android.widget.Toast.makeText(context, "A maquininha da Caixa não utiliza Bluetooth.", android.widget.Toast.LENGTH_LONG).show()
            return
        }

        val bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            android.widget.Toast.makeText(context, "Este dispositivo não suporta Bluetooth.", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            android.widget.Toast.makeText(context, "O Bluetooth precisa ser ativado primeiro.", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        // Aqui chamamos o fluxo de pareamento de cada SDK
        when (provider) {
            PaymentProvider.MERCADO_PAGO -> {
                // Ao integrar o SDK real, aqui chamaremos PointSdk.getInstance().device().openBluetoothSettings()
                // Por enquanto, abrimos as configurações de Bluetooth do sistema para simular a "busca"
                val intent = android.content.Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                android.widget.Toast.makeText(context, "Abrindo busca de dispositivos...", android.widget.Toast.LENGTH_SHORT).show()
            }
            PaymentProvider.STONE -> {
                android.widget.Toast.makeText(context, "Iniciando descoberta de leitores Stone...", android.widget.Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
}
