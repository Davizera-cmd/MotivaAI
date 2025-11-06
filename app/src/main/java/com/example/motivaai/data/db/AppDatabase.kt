package com.example.motivaai.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * A classe principal do banco de dados do Room.
 * Ela une as Entidades (tabelas) e o DAO (operações).
 * (Atende RNF02: Armazenamento Local)
 */
@Database(
    entities = [UserPreferences::class, DiaryEntry::class], // Nossas duas tabelas
    version = 1, // Mudar a versão se alterar a estrutura das tabelas no futuro
    exportSchema = false // Não precisamos exportar o schema do DB por enquanto
)
abstract class AppDatabase : RoomDatabase() {

    // O banco de dados precisa saber sobre o DAO
    abstract fun motivAiDao(): MotivAiDao

    /**
     * Companion object para implementar o padrão Singleton.
     * Isso garante que tenhamos apenas UMA instância do banco de dados.
     */
    companion object {

        // @Volatile garante que o valor de 'INSTANCE' esteja sempre atualizado
        // para todas as threads do aplicativo.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // 'synchronized' garante que apenas uma thread por vez
            // possa executar este bloco de código, evitando a criação
            // de duas instâncias do banco ao mesmo tempo.
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "motivai_database" // Nome do arquivo do banco de dados
                    )
                        // (Aqui poderíamos adicionar migrações no futuro)
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}