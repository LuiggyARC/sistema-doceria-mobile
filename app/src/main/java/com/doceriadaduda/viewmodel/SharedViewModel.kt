package com.doceriadaduda.viewmodel

import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.util.Locale

class SharedViewModel : ViewModel() {

    fun parseValor(texto: String?): Double {
        if (texto.isNullOrBlank()) {
            return 0.0
        }
        var cleanedText = texto.trim().replace("R$", "").replace(" ", "")
        cleanedText = cleanedText.replace(".", "").replace(",", ".")
        return cleanedText.toDoubleOrNull() ?: 0.0
    }

    fun fmtReal(valor: Double?): String {
        if (valor == null) return "R$ 0,00"
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            format.format(valor)
        } catch (e: Exception) {
            "R$ 0,00"
        }
    }
}
