package com.example.motivaai

import android.app.Application
import com.example.motivaai.data.MotivAiRepository
import com.example.motivaai.data.db.AppDatabase

/**
 * Classe Application personalizada.
 * O objetivo dela é criar e prover uma instância ÚNICA (Singleton)
 * do nosso Banco de Dados e do nosso Repositório para todo o app.
 * Isso é uma forma de Injeção de Dependência manual.
 */
class MotivAiApplication : Application() {

    // Usamos "lazy" para que o banco e o repositório só sejam
    // criados quando forem realmente acessados pela primeira vez.

    // Cria a instância única do banco de dados
    private val database by lazy { AppDatabase.getInstance(this) }

    // Cria a instância única do repositório, passando o DAO do banco
    val repository by lazy { MotivAiRepository(database.motivAiDao()) }
}