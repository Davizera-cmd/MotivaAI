package com.example.motivaai.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.motivaai.MotivAiApplication
import com.example.motivaai.data.db.UserPreferences
import com.example.motivaai.services.GeminiService
import com.example.motivaai.utils.NotificationHelper
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

/**
 * Worker do WorkManager responsável por executar a tarefa diária de
 * gerar e enviar a notificação motivacional (RF02, RF04, RNF05).
 */
class MotivationalWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    // ID único para esta tarefa de trabalho
    companion object {
        const val WORK_NAME = "MotivationalWorker"
    }

    override suspend fun doWork(): Result {
        // --- 1. Obter as dependências (Repositório, Serviços) ---
        // Como não podemos usar injeção de dependência normal,
        // pegamos o repositório da nossa classe Application.
        val application = appContext as MotivAiApplication
        val repository = application.repository

        // Instancia os serviços que o worker precisa
        val geminiService = GeminiService()
        val notificationHelper = NotificationHelper(appContext)

        // Cria o canal de notificação (seguro chamar múltiplas vezes)
        notificationHelper.createNotificationChannel()

        // --- 2. Buscar os dados do usuário ---
        val userPrefs = repository.getUserPreferences().firstOrNull() // Pega o valor mais recente do Flow

        // Se não houver preferências (ex: usuário desinstalou), falha o worker
        if (userPrefs == null) {
            println("Worker falhou: UserPreferences é nulo.")
            return Result.failure()
        }

        // Se o onboarding não estiver completo, não faz sentido rodar.
        if (!userPrefs.onboardingCompleted) {
            println("Worker pulou: Onboarding não completo.")
            return Result.success() // Sucesso, mas não fez nada
        }

        // --- 3. Calcular os dados para a API ---
        val daysAbstinent = calculateDaysAbstinent(userPrefs.abstinenceStartDateInMillis)

        // --- 4. Chamar a API Gemini para gerar a mensagem ---
        val message = geminiService.generateMotivationalMessage(
            addictionType = userPrefs.addictionType,
            aiPrompt = userPrefs.aiPrompt,
            daysAbstinent = daysAbstinent
        )

        // --- 5. Exibir a notificação ---
        notificationHelper.showNotification(
            title = "Sua motivação diária chegou!",
            message = message
        )

        println("Worker completou com sucesso.")
        return Result.success() // Tarefa concluída!
    }

    /**
     * Calcula a diferença de dias entre a data de início e agora.
     */
    private fun calculateDaysAbstinent(startDateInMillis: Long): Long {
        val now = System.currentTimeMillis()
        val differenceInMillis = now - startDateInMillis

        // Converte milissegundos para dias
        return TimeUnit.MILLISECONDS.toDays(differenceInMillis)
    }
}