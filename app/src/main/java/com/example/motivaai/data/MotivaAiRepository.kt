package com.example.motivaai.data

import com.example.motivaai.data.db.DiaryEntry
import com.example.motivaai.data.db.MotivAiDao
import com.example.motivaai.data.db.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repositório. É a única fonte de verdade para os dados do app.
 * Abstrai a origem dos dados (no caso, o Room DAO) do resto do app (ViewModels).
 */
class MotivAiRepository(
    // O Repositório "pede" o DAO para funcionar.
    // Isso é chamado de Injeção de Dependência.
    private val motivAiDao: MotivAiDao
) {

    // --- Funções para UserPreferences ---

    /**
     * Expõe o Flow de preferências do DAO.
     */
    fun getUserPreferences(): Flow<UserPreferences?> {
        return motivAiDao.getUserPreferences()
    }

    /**
     * Encapsula a lógica de salvar as preferências.
     */
    suspend fun upsertUserPreferences(preferences: UserPreferences) {
        motivAiDao.upsertUserPreferences(preferences)
    }

    // --- Funções para DiaryEntry ---

    /**
     * Expõe o Flow da lista de relatos do DAO.
     */
    fun getAllDiaryEntries(): Flow<List<DiaryEntry>> {
        return motivAiDao.getAllDiaryEntries()
    }

    /**
     * Encapsula a lógica de inserir um novo relato.
     */
    suspend fun insertDiaryEntry(entry: DiaryEntry) {
        motivAiDao.insertDiaryEntry(entry)
    }

    /**
     * Encapsula a lógica de buscar os dados para exportação (RF07).
     */
    suspend fun getAllEntriesForExport(): List<DiaryEntry> {
        return motivAiDao.getAllEntriesForExport()
    }
}