package com.example.motivaai.ui

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.motivaai.BuildConfig
import com.example.motivaai.workers.MotivationalWorker

/**
 * Componentes de UI visíveis apenas em modo Debug.
 * Fácil de remover/manter separado da lógica de produção.
 */
@Composable
fun DebugTestNotificationButton() {

    // Esta linha é a MÁGICA:
    // O "BuildConfig.DEBUG" é uma variável que o Android Studio
    // define como true quando clica em "Run" (Debug)
    // e como 'false' quando você gera o app "pra valer" (Release).
    // O compilador vai OTIMIZAR e remover este botão inteiramente no app final.
    if (BuildConfig.DEBUG) {
        val context = LocalContext.current

        Button(
            onClick = {
                triggerTestNotification(context)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(text = "TESTE: Gerar Notificação Agora")
        }
    }
}

/**
 * Dispara uma "OneTimeWorkRequest" para o nosso Worker (RF02/RF04)
 */
private fun triggerTestNotification(context: Context) {
    val workManager = WorkManager.getInstance(context)

    // Define as mesmas restrições (precisa de internet)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    // Cria uma requisição ÚNICA (OneTime)
    val testWorkRequest = OneTimeWorkRequest.Builder(MotivationalWorker::class.java)
        .setConstraints(constraints)
        .build()

    // Enfileira o trabalho. O WorkManager vai rodar assim que puder.
    workManager.enqueue(testWorkRequest)

    println("Pedido de TESTE de notificação enfileirado.")
}