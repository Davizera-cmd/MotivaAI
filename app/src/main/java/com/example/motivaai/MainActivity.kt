package com.example.motivaai

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.example.motivaai.ui.MainViewModel
import com.example.motivaai.ui.ViewModelFactory
import com.example.motivaai.ui.screens.MainScreen
import com.example.motivaai.ui.screens.OnboardingScreen
import com.example.motivaai.ui.theme.MotivAITheme
import com.example.motivaai.workers.MotivationalWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as MotivAiApplication
        val repository = application.repository
        val viewModelFactory = ViewModelFactory(repository)

        setContent {
            MotivAITheme {
                val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
                AppNavigator(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AppNavigator(viewModel: MainViewModel) {

    val userPreferences by viewModel.userPreferences.collectAsState()

    // Pega o contexto atual (necessário para o WorkManager)
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            userPreferences?.id == -1 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            userPreferences == null -> {
                OnboardingScreen(
                    onOnboardingComplete = { prefs ->
                        // 1. Salva as preferências (como antes)
                        viewModel.saveOnboardingPreferences(prefs)

                        // 2. NOVO: Agenda o Worker
                        scheduleDailyWorker(
                            context = context,
                            notificationHour = prefs.notificationTimeHour,
                            notificationMinute = prefs.notificationTimeMinute
                        )
                    }
                )
            }

            userPreferences?.onboardingCompleted == true -> {
                MainScreen(viewModel = viewModel)
            }

            userPreferences?.onboardingCompleted == false -> {
                OnboardingScreen(
                    onOnboardingComplete = { prefs ->
                        // (Também agendamos aqui, caso o usuário
                        // tenha falhado o onboarding antes)
                        viewModel.saveOnboardingPreferences(prefs)

                        scheduleDailyWorker(
                            context = context,
                            notificationHour = prefs.notificationTimeHour,
                            notificationMinute = prefs.notificationTimeMinute
                        )
                    }
                )
            }
        }
    }
}

/**
 * Função para agendar o MotivationalWorker (RF02).
 */
private fun scheduleDailyWorker(
    context: Context,
    notificationHour: Int,
    notificationMinute: Int
) {
    val workManager = WorkManager.getInstance(context)

    // 1. Calcula o atraso (delay) inicial.
    // Queremos que a primeira notificação ocorra no horário
    // que o usuário escolheu (hoje ou amanhã).
    val now = Calendar.getInstance()
    val scheduledTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, notificationHour)
        set(Calendar.MINUTE, notificationMinute)
        set(Calendar.SECOND, 0)
        // Se o horário de hoje já passou, agenda para amanhã
        if (before(now)) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val initialDelay = scheduledTime.timeInMillis - now.timeInMillis

    // 2. Define restrições (ex: precisa de internet)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) // Só roda se tiver internet
        .build()

    // 3. Cria a requisição de trabalho PERIÓDICA (24h)
    val dailyWorkRequest = PeriodicWorkRequest.Builder(
        MotivationalWorker::class.java,
        24, TimeUnit.HOURS // Repete a cada 24 horas
    )
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // Define o atraso inicial
        .setConstraints(constraints)
        .build()

    // 4. Envia o trabalho para o WorkManager
    // Usamos "enqueueUniquePeriodicWork" para garantir que SÓ exista
    // UMA instância desse trabalho agendada por vez.
    workManager.enqueueUniquePeriodicWork(
        MotivationalWorker.WORK_NAME, // Um nome único para a tarefa
        ExistingPeriodicWorkPolicy.KEEP, // Mantém o agendamento antigo se já existir
        dailyWorkRequest
    )

    println("Trabalho diário agendado. Delay inicial: $initialDelay ms")
}