package com.example.motivaai.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motivaai.data.db.UserPreferences
import com.example.motivaai.ui.theme.MotivAITheme
import java.util.Locale

/**
 * Tela de Configurações (RF02 / RF04).
 * Permite ao usuário alterar o horário da notificação e o tom da IA.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentPreferences: UserPreferences, // Recebe o estado atual
    onSettingsSaved: (newPrefs: UserPreferences, newHour: Int, newMinute: Int) -> Unit, // Callback
    onBack: () -> Unit // Callback para voltar
) {
    // Estados iniciais baseados nas preferências atuais
    var aiPrompt by remember { mutableStateOf(currentPreferences.aiPrompt) }
    var notificationHour by remember { mutableStateOf(currentPreferences.notificationTimeHour) }
    var notificationMinute by remember { mutableStateOf(currentPreferences.notificationTimeMinute) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Seletor de Hora da Notificação (idêntico ao onboarding)
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            notificationHour = hour
            notificationMinute = minute
        },
        notificationHour,
        notificationMinute,
        true // 24h
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações MotivAI") },
                navigationIcon = { // Botão de Voltar
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Exibir vício atual (não pode ser mudado aqui)
            Text(
                text = "Vício Atual: ${currentPreferences.addictionType}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            // --- RF02: Hora da Notificação ---
            Button(onClick = { timePickerDialog.show() }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Horário: ${String.format(Locale.getDefault(), "%02d:%02d", notificationHour, notificationMinute)}"
                )
            }

            // --- RF04: Tom da IA ---
            OutlinedTextField(
                value = aiPrompt,
                onValueChange = { aiPrompt = it },
                label = { Text("Novo Tom das Mensagens da IA") },
                placeholder = { Text("Ex: Firme, como um coach") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- Botão de Salvar ---
            Button(
                onClick = {
                    val newPrefs = currentPreferences.copy(
                        aiPrompt = aiPrompt,
                        notificationTimeHour = notificationHour,
                        notificationTimeMinute = notificationMinute
                    )
                    onSettingsSaved(newPrefs, notificationHour, notificationMinute)
                    onBack() // Volta para a tela principal
                    Toast.makeText(context, "Configurações salvas e reagendadas!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Salvar e Atualizar Agendamento")
            }
        }
    }
}