package com.example.motivaai.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motivaai.data.db.UserPreferences
import com.example.motivaai.ui.theme.MotivAITheme
import java.util.*

/**
 * Tela de Onboarding (RF06).
 * Coleta as informações iniciais do usuário.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: (UserPreferences) -> Unit // Função "callback" para salvar os dados
) {
    // 1. Estados para guardar os inputs do usuário
    var addictionType by remember { mutableStateOf("") }
    var aiPrompt by remember { mutableStateOf("") }

    // Para guardar a data e hora de início
    var startDate by remember { mutableStateOf<Calendar?>(null) }

    // Para guardar a hora da notificação
    var notificationTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // 2. Lógica para os Seletores de Data e Hora (Date/Time Pickers)

    // Seletor de Data de Início
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            // Abre o Seletor de Hora logo em seguida
            TimePickerDialog(
                context,
                { _, hour: Int, minute: Int ->
                    startDate = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute)
                    }
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true // 24h
            ).show()
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    // Seletor de Hora da Notificação
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            notificationTime = Pair(hour, minute)
        },
        9, 0, true // Padrão 09:00
    )

    // 3. Layout da UI (Compose)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuração Inicial") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Nosso Amarelo
                    titleContentColor = MaterialTheme.colorScheme.onPrimary // Nosso Preto
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Padding interno do Scaffold
                .padding(16.dp) // Nosso padding
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Bem-vindo ao MotivAI!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Vamos configurar sua jornada. Estes dados ficam apenas no seu celular.",
                textAlign = TextAlign.Center
            )

            // --- RF01: Data de Início ---
            Button(onClick = { datePickerDialog.show() }) {
                Text(
                    text = if (startDate == null) "1. Selecionar Data e Hora de Início"
                    else "Início: ${formatCalendar(startDate)}"
                )
            }

            // --- RF02: Hora da Notificação ---
            Button(onClick = { timePickerDialog.show() }) {
                Text(
                    text = if (notificationTime == null) "2. Definir Hora da Notificação Diária"
                    else "Notificação: ${formatTime(notificationTime)}"
                )
            }

            // --- RF04: Tipo de Vício ---
            OutlinedTextField(
                value = addictionType,
                onValueChange = { addictionType = it },
                label = { Text("3. Qual vício você quer combater?") },
                placeholder = { Text("Ex: Álcool, Tabaco, Redes Sociais") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- RF04: Tom da IA ---
            OutlinedTextField(
                value = aiPrompt,
                onValueChange = { aiPrompt = it },
                label = { Text("4. Tom das mensagens da IA") },
                placeholder = { Text("Ex: Gentil e acolhedor") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para baixo

            // --- Botão de Salvar ---
            val isFormValid = startDate != null &&
                    notificationTime != null &&
                    addictionType.isNotBlank() &&
                    aiPrompt.isNotBlank()

            Button(
                onClick = {
                    val finalPrefs = UserPreferences(
                        abstinenceStartDateInMillis = startDate!!.timeInMillis,
                        notificationTimeHour = notificationTime!!.first,
                        notificationTimeMinute = notificationTime!!.second,
                        addictionType = addictionType,
                        aiPrompt = aiPrompt,
                        onboardingCompleted = true // Marcamos como completo!
                    )
                    onOnboardingComplete(finalPrefs)
                },
                enabled = isFormValid, // Só habilita se tudo estiver preenchido
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Iniciar Minha Jornada")
            }
        }
    }
}

// Funções "Helper" para formatar data e hora
private fun formatCalendar(calendar: Calendar?): String {
    if (calendar == null) return ""
    return android.text.format.DateFormat.format("dd/MM/yy 'às' HH:mm", calendar).toString()
}

private fun formatTime(time: Pair<Int, Int>?): String {
    if (time == null) return ""
    return String.format(Locale.getDefault(), "%02d:%02d", time.first, time.second)
}

// --- Preview para o Android Studio ---
@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MotivAITheme {
        OnboardingScreen(onOnboardingComplete = {})
    }
}