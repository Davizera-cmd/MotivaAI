package com.example.motivaai.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade (tabela) que representa um único relato diário do usuário.
 * (Atende ao RF05: Registro de Relato Diário)
 */
@Entity(tableName = "diary_entries")
data class DiaryEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Chave primária que se auto-incrementa

    val dateInMillis: Long, // Data do relato, salva em milissegundos
    val difficulty: String, // Nível de dificuldade (Fácil, Médio, Difícil)
    val notes: String // Campo de texto livre
)