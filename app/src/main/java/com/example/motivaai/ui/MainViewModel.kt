package com.example.motivaai.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motivaai.data.MotivAiRepository
import com.example.motivaai.data.db.DiaryEntry
import com.example.motivaai.data.db.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val LOADING_PREFERENCES = UserPreferences(
    id = -1,
    abstinenceStartDateInMillis = 0L,
    notificationTimeHour = 0,
    notificationTimeMinute = 0,
    addictionType = "loading",
    aiPrompt = "loading",
    onboardingCompleted = false
)

/**
 * ViewModel principal do app.
 * Ele gerencia o estado da UI e se comunica com o Repositório.
 */
class MainViewModel(private val repository: MotivAiRepository) : ViewModel() {

    // --- Leitura de Dados (Expondo Flows do Repositório como StateFlow) ---

    /**
     * Expõe as preferências do usuário (contador, IA, etc.) como um StateFlow.
     * A UI (Compose) vai "observar" este fluxo e se atualizar automaticamente.
     * stateIn: Converte um Flow normal em um StateFlow, que guarda o último valor.
     */
    val userPreferences: StateFlow<UserPreferences?> = repository.getUserPreferences()
        .stateIn(
            scope = viewModelScope, // O "tempo de vida" do ViewModel
            started = SharingStarted.WhileSubscribed(5000L), // Começa a "ouvir" quando a UI está visível
            initialValue = LOADING_PREFERENCES // Valor inicial antes do banco carregar
        )

    /**
     * Expõe a lista de relatos do diário como um StateFlow.
     */
    val diaryEntries: StateFlow<List<DiaryEntry>> = repository.getAllDiaryEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList() // Valor inicial é uma lista vazia
        )

    // --- Escrita de Dados (Ações que a UI pode chamar) ---

    /**
     * Salva as preferências iniciais do onboarding (RF06).
     * Usa viewModelScope.launch para rodar a operação do banco em background.
     */
    fun saveOnboardingPreferences(prefs: UserPreferences) {
        viewModelScope.launch {
            repository.upsertUserPreferences(prefs)
        }
    }

    /**
     * Salva um novo relato no diário (RF05).
     */
    fun addDiaryEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.insertDiaryEntry(entry)
        }
    }

    /**
     * Reinicia o contador de abstinência (RF03).
     * Ele basicamente salva um novo UserPreferences com a data de início atualizada.
     */
    fun resetCounter(newStartDate: Long) {
        // Pega as preferências atuais
        val currentPrefs = userPreferences.value
        if (currentPrefs != null) {
            // Cria uma cópia atualizando apenas a data de início
            val updatedPrefs = currentPrefs.copy(abstinenceStartDateInMillis = newStartDate)
            viewModelScope.launch {
                repository.upsertUserPreferences(updatedPrefs)
            }
        }
    }
}