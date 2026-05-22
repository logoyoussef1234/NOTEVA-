package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1B1F).copy(alpha = 0.85f), // semi-transparent Elegant Dark surface
            contentColor = Color(0xFFE6E1E5)
        ),
        border = BorderStroke(
            borderWidth,
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3E3C45).copy(alpha = 0.6f),
                    Color(0xFFD0BCFF).copy(alpha = 0.15f) // glowing lavender hint
                ),
                start = Offset(0f, 0f),
                end = Offset(100f, 200f)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun PremiumBackground(
    isDark: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF111111), // Elegant Dark solid background
                Color(0xFF161517), // subtle rich twilight dark transition
                Color(0xFF111111)  
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF4F6F9), // modern paper white
                Color(0xFFE3E8F0), // clean cool gray
                Color(0xFFFFFFFF)  // pure bottom highlights
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        // Subtle lavender radial glow circle decoration in background for dark theme
        if (isDark) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .offset(x = (-80).dp, y = (-50).dp)
                    .clip(RoundedCornerShape(140.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFD0BCFF).copy(alpha = 0.11f), // Elegant Lavender premium glow
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .align(androidx.compose.ui.Alignment.BottomEnd)
                    .offset(x = 100.dp, y = 100.dp)
                    .clip(RoundedCornerShape(160.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF381E72).copy(alpha = 0.14f), // Elegant Indigo backing glow
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        content()
    }
}
