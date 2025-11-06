package com.example.motivaai.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade (tabela) que armazena as configurações globais do usuário.
 * Esta tabela terá sempre apenas UMA linha (com id = 1).
 * (Atende RF01, RF02, RF04, RF06)
 */
@Entity(tableName = "user_preferences")
data class UserPreferences(

    @PrimaryKey
    val id: Int = 1, // Chave primária fixa. Só existirá a linha com id 1

    // RF01: Contador de Abstinência
    val abstinenceStartDateInMillis: Long,

    // RF02: Agendamento de Notificações
    val notificationTimeHour: Int, // Hora (0-23)
    val notificationTimeMinute: Int, // Minuto (0-59)

    // RF04: Personalização da IA
    val addictionType: String, // Tipo de vício
    val aiPrompt: String, // Prompt de personalização (tom das mensagens)

    // RF06: Onboarding
    val onboardingCompleted: Boolean = false
) {
    companion object {
        // Constante para garantir que sempre acessemos a única linha
        const val SINGLE_ROW_ID = 1
    }
}