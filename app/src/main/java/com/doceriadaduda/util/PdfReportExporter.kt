package com.doceriadaduda.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PdfReportExporter {

    fun exportMensal(
        context: Context,
        mes: String,
        faturamento: Double,
        despesas: Double,
        saldo: Double,
        ticketMedio: Double,
        vendasQtd: Int
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        var y = 40f

        // Header
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Pai D’égua Hub", 150f, y, paint)
        
        y += 25f
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Relatório Mensal: $mes", 150f, y, paint)

        y += 40f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 12f
        
        canvas.drawText("Faturamento Bruto: R$ ${String.format("%.2f", faturamento)}", 20f, y, paint)
        y += 25f
        canvas.drawText("Total Despesas: R$ ${String.format("%.2f", despesas)}", 20f, y, paint)
        y += 25f
        paint.isFakeBoldText = true
        canvas.drawText("Saldo Líquido: R$ ${String.format("%.2f", saldo)}", 20f, y, paint)
        paint.isFakeBoldText = false

        y += 40f
        canvas.drawText("Quantidade de Vendas: $vendasQtd", 20f, y, paint)
        y += 25f
        canvas.drawText("Ticket Médio: R$ ${String.format("%.2f", ticketMedio)}", 20f, y, paint)

        y += 60f
        paint.textSize = 10f
        paint.color = Color.GRAY
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        canvas.drawText("Gerado em: $now", 20f, y, paint)

        pdfDocument.finishPage(page)

        val fileName = "Relatorio_${mes.replace("/", "_")}_${System.currentTimeMillis()}.pdf"
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    Toast.makeText(context, "Relatório salvo em Downloads", Toast.LENGTH_LONG).show()
                }
            } else {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(context, "Relatório salvo em Downloads", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao gerar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }
}
