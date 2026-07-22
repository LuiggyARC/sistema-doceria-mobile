package com.doceriadaduda

import android.app.Application
import android.content.Context
import com.doceriadaduda.di.AppModule

import com.doceriadaduda.util.SecurityUtils

class DoceriaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
        
        // Tenta carregar a chave salva de forma segura
        val securePrefs = SecurityUtils.getEncryptedPrefs(this)
        val publicKey = securePrefs.getString("mp_public_key", null)
        
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
