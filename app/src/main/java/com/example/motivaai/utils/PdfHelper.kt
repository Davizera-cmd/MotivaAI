package com.example.motivaai.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.motivaai.data.db.DiaryEntry
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Classe utilitária para gerar e salvar um arquivo PDF (RF07).
 */
class PdfHelper(private val context: Context) {

    // Tamanho A4 em pontos (aprox. 595 x 842)
    private val PAGE_WIDTH = 595
    private val PAGE_HEIGHT = 842

    // Margens
    private val MARGIN_X = 40
    private val MARGIN_Y = 60

    /**
     * Gera um PDF com a lista de entradas do diário e salva-o no sistema.
     */
    fun generatePdf(entries: List<DiaryEntry>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        // Configuração de Estilos de Texto
        val titlePaint = Paint().apply {
            textSize = 24f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
        }
        val contentPaint = Paint().apply {
            textSize = 12f
        }
        val datePaint = Paint().apply {
            textSize = 10f
            color = android.graphics.Color.GRAY
        }

        // Variáveis de layout
        var yPosition = MARGIN_Y.toFloat()
        val lineHeight = 18f

        // 1. Título do Documento
        canvas.drawText("Diário de Bordo MotivAI", MARGIN_X.toFloat(), yPosition, titlePaint)
        yPosition += lineHeight * 2

        // 2. Data de Geração
        val today = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Gerado em: $today", MARGIN_X.toFloat(), yPosition, datePaint)
        yPosition += lineHeight * 2

        // Verifica se há entradas
        if (entries.isEmpty()) {
            canvas.drawText("Nenhum relato encontrado.", MARGIN_X.toFloat(), yPosition, contentPaint)
        } else {
            // 3. Itera sobre as entradas
            entries.forEachIndexed { index, entry ->

                // Quebra de página se não houver espaço suficiente
                val requiredSpace = lineHeight * 5
                if (yPosition + requiredSpace > PAGE_HEIGHT - MARGIN_Y) {
                    document.finishPage(page)
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    yPosition = MARGIN_Y.toFloat() // Volta para o topo da nova página
                }

                // Data e Hora do Relato
                val entryDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(entry.dateInMillis))
                canvas.drawText("Relato ${entries.size - index} - $entryDate", MARGIN_X.toFloat(), yPosition, headerPaint)
                yPosition += lineHeight

                // Dificuldade
                canvas.drawText("Dificuldade: ${entry.difficulty}", MARGIN_X + 10f, yPosition, contentPaint)
                yPosition += lineHeight

                // Notas (quebra de linha simples, ignorando palavras longas por simplicidade)
                val notes = if (entry.notes.isBlank()) "(Sem anotações)" else entry.notes
                val lines = notes.split("\n") // Trata quebra de linha do usuário
                lines.forEach { line ->
                    canvas.drawText("Notas: $line", MARGIN_X + 10f, yPosition, contentPaint)
                    yPosition += lineHeight
                }

                // Espaçamento entre os relatos
                yPosition += lineHeight * 0.5f
            }
        }

        // 4. Finaliza a última página
        document.finishPage(page)

        // 5. Salva o documento no Armazenamento Compartilhado
        val fileName = "DiarioBordo_MotivAI_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.pdf"

        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API 29) e superior: Usa MediaStore (método moderno)
            savePdfUsingMediaStore(document, fileName)
        } else {
            // Android 9 (API 28) e inferior: Usa FileOutputStream (requer permissão WRITE_EXTERNAL_STORAGE)
            savePdfUsingFileStream(document, fileName)
        }

        // 6. Fecha o documento
        document.close()

        // 7. Notifica o usuário
        val message = if (result) {
            "PDF salvo com sucesso em Downloads!"
        } else {
            "Falha ao salvar o PDF. Verifique as permissões."
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Método moderno para salvar PDF em Android 10+
    private fun savePdfUsingMediaStore(document: PdfDocument, fileName: String): Boolean {
        return try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { os ->
                    document.writeTo(os)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Método legado para salvar PDF em Android 9-
    @Suppress("DEPRECATION")
    private fun savePdfUsingFileStream(document: PdfDocument, fileName: String): Boolean {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { os ->
                document.writeTo(os)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}