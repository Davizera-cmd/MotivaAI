package com.example.motivaai.services

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

/**
 * Serviço para se comunicar com a API do Google Gemini (RF04).
 * Esta classe é responsável por montar o prompt e gerar a mensagem.
 */
class GeminiService {

    // (MVP) A chave da API fica hardcoded.

    // NÂO COLOCAR A CHAVE AQUI AO SUBIR PARA O GITHUB VIU?
    private val apiKey = "AIzaSyCholo-5XBj6yg-OpPiRi5O6o9VYTEYvrs" // << aqui coloca a chave

    // Configuração de segurança e geração
    private val generationConfig = generationConfig {
        // Evita que a IA gere respostas muito longas ou entre em loops
        maxOutputTokens = 150
        // Um pouco de criatividade, mas não muito (0.0 = 100% determinístico)
        temperature = 0.4f
    }

    /**
     * Gera uma mensagem motivacional com base nas preferências do usuário.
     */
    suspend fun generateMotivationalMessage(
        addictionType: String,
        aiPrompt: String,
        daysAbstinent: Long
    ): String {

        // 1. Inicializa o modelo
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash", // Um modelo rápido e eficiente
            apiKey = apiKey,
            generationConfig = generationConfig
        )

        // 2. Monta o prompt
        val prompt = """
            Você é um assistente motivacional amigável chamado MotivAI.
            Sua tarefa é escrever uma mensagem de apoio curta (1 ou 2 frases) para 
            um usuário que está tentando superar o vício em "$addictionType".
            O usuário está em abstinência há $daysAbstinent dias.
            O tom da mensagem deve ser: "$aiPrompt".
            
            Seja direto e inspirador. Não use markdown ou formatação especial.
            Não inclua saudações como "Olá!" ou "Oi!".
        """.trimIndent() // trimIndent() remove a formatação/espaços do código

        try {
            // 3. Chama a API
            val response = generativeModel.generateContent(prompt)

            // 4. Retorna o texto da resposta (ou uma msg de erro)
            return response.text ?: "Continue firme na sua jornada!"

        } catch (e: Exception) {
            // Em caso de erro de rede ou API, retorna uma mensagem padrão
            println("Erro na API Gemini: ${e.message}")
            return "Você é forte e capaz. Continue firme na sua jornada!"
        }
    }
}