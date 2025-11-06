package com.example.motivaai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Configuração padrão de tipografia (fontes) do Material 3
// Pode personalizar isso mais tarde se quisermos.
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Outros estilos de texto como titleLarge, labelSmall, etc.
       podem ser definidos aqui. Por enquanto, o padrão é suficiente.
    */
)