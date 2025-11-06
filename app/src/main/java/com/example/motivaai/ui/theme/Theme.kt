package com.example.motivaai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esta é a paleta de cores do Modo Claro, usando as cores que definimos.
private val LightColorScheme = lightColorScheme(
    primary = MotivYellow,          // Cor principal (botões, etc.)
    onPrimary = MotivTextBlack,     // Cor do texto/ícones em cima da primária
    primaryContainer = MotivYellow,
    onPrimaryContainer = MotivTextBlack,
    secondary = MotivYellowDark,      // Cor secundária
    onSecondary = MotivTextBlack,   // Texto/ícones em cima da secundária
    background = MotivBackground,     // Cor de fundo principal das telas
    onBackground = MotivTextBlack,  // Cor do texto em cima do fundo
    surface = MotivBackground,        // Cor de "superfícies" (cards, etc.)
    onSurface = MotivTextBlack,     // Cor do texto em cima de superfícies
    error = Color(0xFFB00020),
    onError = White
    // ... outras cores podem ser definidas
)

// (Poderíamos definir um DarkColorScheme aqui, mas por enquanto usaremos o claro)

@Composable
fun MotivAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Detecta o modo do sistema
    content: @Composable () -> Unit
) {
    // Por enquanto, nosso app só tem o tema claro (LightColorScheme)
    // Se darkTheme for true, ele ainda usará o tema claro.
    val colorScheme = LightColorScheme

    // Este bloco gerencia as cores da barra de status do sistema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Seta a cor da barra de status (topo)
            window.statusBarColor = colorScheme.primaryContainer.toArgb()
            // Seta a cor da barra de navegação (baixo)
            window.navigationBarColor = colorScheme.background.toArgb()

            // Define se os ícones (bateria, relógio) são claros ou escuros
            // 'true' = ícones escuros (para fundos claros)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    // Aplica o tema Material 3 com nossas cores e fontes
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}