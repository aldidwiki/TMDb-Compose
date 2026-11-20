package com.aldiprahasta.tmdb.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aldiprahasta.tmdb.R

private val DiscoverPrimary = Color(0xFF3B4CCA)
val DiscoverRating = Color(0xFFF59E0B)

private val DiscoverColors = lightColorScheme(
        primary = DiscoverPrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF1F3FF),
        onPrimaryContainer = DiscoverPrimary,
        background = Color(0xFFF9F9FF),
        onBackground = Color(0xFF0F172A),
        surface = Color.White,
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFF1F3FF),
        onSurfaceVariant = Color(0xFF64748B),
        outline = Color(0xFFE2E6F4),
        outlineVariant = Color(0xFFE2E6F4),
        scrim = Color(0xFF0F172A)
)

private val PlusJakartaSans = FontFamily(
        Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
        Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
        Font(R.font.plus_jakarta_sans_semi_bold, FontWeight.SemiBold),
        Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
        Font(R.font.plus_jakarta_sans_extra_bold, FontWeight.ExtraBold)
)

private fun discoverText(size: Int, height: Int, weight: FontWeight) = TextStyle(
        fontFamily = PlusJakartaSans,
        fontSize = size.sp,
        lineHeight = height.sp,
        fontWeight = weight
)

private val DiscoverTypography = Typography(
        headlineSmall = discoverText(22, 28, FontWeight.Bold),
        titleLarge = discoverText(20, 28, FontWeight.Bold),
        titleMedium = discoverText(18, 24, FontWeight.Bold),
        titleSmall = discoverText(16, 22, FontWeight.Bold),
        bodyMedium = discoverText(14, 22, FontWeight.Normal),
        bodySmall = discoverText(12, 18, FontWeight.Normal),
        labelLarge = discoverText(13, 18, FontWeight.SemiBold),
        labelMedium = discoverText(11, 16, FontWeight.SemiBold),
        labelSmall = discoverText(10, 14, FontWeight.Medium)
)

@Composable
fun DiscoverTheme(content: @Composable () -> Unit) {
    MaterialTheme(
            colorScheme = DiscoverColors,
            typography = DiscoverTypography,
            content = content
    )
}
