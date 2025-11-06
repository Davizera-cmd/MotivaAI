package com.example.motivaai.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para o banco de dados do MotivAI.
 * Define todas as operações de banco de dados (ler, escrever, atualizar).
 */
@Dao
interface MotivAiDao {

    // --- Operações para UserPreferences (RF01, RF02, RF04) ---

    /**
     * Insere ou Atualiza as preferências do usuário.
     * Usamos @Upsert (Update + Insert) para sempre sobrescrever a linha única (id=1).
     */
    @Upsert
    suspend fun upsertUserPreferences(preferences: UserPreferences)

    /**
     * Busca as preferências do usuário.
     * Retorna um Flow, que é um fluxo de dados reativo. A UI será
     * notificada automaticamente se os dados mudarem.
     * Usamos o ID fixo que definimos na entidade.
     */
    @Query("SELECT * FROM user_preferences WHERE id = ${UserPreferences.SINGLE_ROW_ID}")
    fun getUserPreferences(): Flow<UserPreferences?> // O '?' indica que pode ser nulo (no primeiro uso)


    // --- Operações para DiaryEntry (RF05, RF07) ---

    /**
     * Insere um novo relato no diário.
     */
    @Insert
    suspend fun insertDiaryEntry(entry: DiaryEntry)

    /**
     * Busca todos os relatos do diário, ordenados do mais recente para o mais antigo.
     * Retorna um Flow para que a lista do diário na UI se atualize sozinha.
     */
    @Query("SELECT * FROM diary_entries ORDER BY dateInMillis DESC")
    fun getAllDiaryEntries(): Flow<List<DiaryEntry>>

    /**
     * Busca todos os relatos (NÃO reativo) para exportação de PDF (RF07).
     * Esta função não usa Flow porque a exportação é um evento único.
     */
    @Query("SELECT * FROM diary_entries ORDER BY dateInMillis ASC") // PDF geralmente é na ordem cronológica
    suspend fun getAllEntriesForExport(): List<DiaryEntry>
}