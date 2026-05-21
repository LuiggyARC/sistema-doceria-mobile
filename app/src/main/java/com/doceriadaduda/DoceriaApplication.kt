package com.doceriadaduda

import android.app.Application
import android.content.Context
import com.doceriadaduda.di.AppModule

class DoceriaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
        
        // Tenta carregar a chave salva para inicializar o SDK
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val publicKey = prefs.getString("mp_public_key", null)
        
        if (!publicKey.isNullOrBlank()) {
            try {
                // MercadoPagoSDK.initialize(this, publicKey, "BR")
                println("Mercado Pago inicializado com a chave: $publicKey")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
