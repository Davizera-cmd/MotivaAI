package com.example.motivaai.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.motivaai.data.MotivAiRepository

/**
 * Factory (fábrica) para criar nossos ViewModels.
 * É necessária porque nossos ViewModels têm dependências (como o MotivAiRepository)
 * que precisam ser injetadas manualmente em seus construtores.
 */
class ViewModelFactory(
    private val repository: MotivAiRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe que o sistema está pedindo é o MainViewModel
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // Se for, cria e retorna uma instância dele, passando o repositório
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }

        // Se for um ViewModel que não conhecemos, lança um erro
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}