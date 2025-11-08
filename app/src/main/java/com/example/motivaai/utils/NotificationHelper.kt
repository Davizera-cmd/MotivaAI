package com.example.motivaai.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.motivaai.R

/**
 * Classe utilitária para criar o canal de notificação
 * e exibir as notificações (RF02).
 */
class NotificationHelper(private val context: Context) {

    // IDs fixos para o canal e a notificação
    companion object {
        const val CHANNEL_ID = "motivai_channel_1"
        const val CHANNEL_NAME = "Motivações Diárias"
        const val CHANNEL_DESCRIPTION = "Canal para as notificações motivacionais diárias"
        const val NOTIFICATION_ID = 101
    }

    /**
     * Cria o Canal de Notificação.
     * Essencial para Android 8 (API 26) e superiores.
     */
    fun createNotificationChannel() {
        // A checagem 'Build.VERSION.SDK_INT' garante que isso
        // só rode em versões do Android que precisam.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            // Registra o canal no sistema
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Constrói e exibe a notificação.
     */
    fun showNotification(title: String, message: String) {

        // 1. Constrói a notificação
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Ícone do app (padrão)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Permite texto longo
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Remove a notificação ao ser tocada

        // 2. Exibe a notificação
        with(NotificationManagerCompat.from(context)) {
            // Checagem de permissão para Android 13+
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Se não tiver permissão, não faz nada.
                // (Lidaremos com o pedido de permissão na UI depois)
                println("Sem permissão para POST_NOTIFICATIONS")
                return
            }

            // Exibe a notificação
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}