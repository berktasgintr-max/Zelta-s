package com.example.ui.theme

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.R

enum class AppThemeOption(
    val id: String,
    val isDark: Boolean,
    val previewPrimary: Color,
    val previewSecondary: Color,
    val previewBg: Color,
    @get:StringRes val titleRes: Int,
    @get:StringRes val subtitleRes: Int
) {
    LIGHT_ORANGE(
        id = "light_orange",
        isDark = false,
        previewPrimary = LightOrangePrimary,
        previewSecondary = LightOrangeSecondaryPreview,
        previewBg = LightOrangeBackground,
        titleRes = R.string.theme_light_orange_title,
        subtitleRes = R.string.theme_light_orange_sub
    ),
    DARK_ORANGE(
        id = "dark_orange",
        isDark = true,
        previewPrimary = DarkOrangePrimary,
        previewSecondary = DarkOrangeSecondaryPreview,
        previewBg = DarkOrangeBackground,
        titleRes = R.string.theme_dark_orange_title,
        subtitleRes = R.string.theme_dark_orange_sub
    );

    fun title(context: Context): String = context.getString(titleRes)

    fun subtitle(context: Context): String = context.getString(subtitleRes)

    companion object {
        fun fromId(id: String): AppThemeOption = when (id.lowercase()) {
            "dark_orange", "charcoal_orange" -> DARK_ORANGE
            else -> LIGHT_ORANGE
        }
    }
}

typealias AppThemeEnum = AppThemeOption

val LightOrangeColors = lightColorScheme(
    primary = LightOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = LightOrangePrimary.copy(alpha = 0.12f),
    onPrimaryContainer = LightOrangePrimary,
    secondary = LightOrangePrimary,
    onSecondary = Color.White,
    secondaryContainer = LightOrangePrimary.copy(alpha = 0.10f),
    onSecondaryContainer = LightOrangePrimary,
    tertiary = LightOrangeSecondaryPreview,
    onTertiary = Color.White,
    background = LightOrangeBackground,
    onBackground = LightOrangeOnSurface,
    surface = LightOrangeSurface,
    onSurface = LightOrangeOnSurface,
    surfaceVariant = LightOrangeSurface,
    onSurfaceVariant = LightOrangeMuted,
    outline = LightOrangeOutline,
    error = ErrorCrimson,
    onError = Color.White
)

val DarkOrangeColors = darkColorScheme(
    primary = DarkOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = DarkOrangePrimary.copy(alpha = 0.18f),
    onPrimaryContainer = DarkOrangePrimary,
    secondary = DarkOrangePrimary,
    onSecondary = Color.White,
    secondaryContainer = DarkOrangePrimary.copy(alpha = 0.14f),
    onSecondaryContainer = DarkOrangePrimary,
    tertiary = DarkOrangeSecondaryPreview,
    onTertiary = Color.White,
    background = DarkOrangeBackground,
    onBackground = DarkOrangeOnSurface,
    surface = DarkOrangeSurface,
    onSurface = DarkOrangeOnSurface,
    surfaceVariant = DarkOrangeSurface,
    onSurfaceVariant = DarkOrangeMuted,
    outline = DarkOrangeOutline,
    error = ErrorCrimson,
    onError = Color.White
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
private fun animatedColorScheme(target: ColorScheme): ColorScheme {
    val anim = tween<Color>(durationMillis = 300)
    val primary by animateColorAsState(target.primary, anim, label = "primary")
    val onPrimary by animateColorAsState(target.onPrimary, anim, label = "onPrimary")
    val primaryContainer by animateColorAsState(target.primaryContainer, anim, label = "primaryContainer")
    val onPrimaryContainer by animateColorAsState(target.onPrimaryContainer, anim, label = "onPrimaryContainer")
    val secondary by animateColorAsState(target.secondary, anim, label = "secondary")
    val onSecondary by animateColorAsState(target.onSecondary, anim, label = "onSecondary")
    val secondaryContainer by animateColorAsState(target.secondaryContainer, anim, label = "secondaryContainer")
    val onSecondaryContainer by animateColorAsState(target.onSecondaryContainer, anim, label = "onSecondaryContainer")
    val tertiary by animateColorAsState(target.tertiary, anim, label = "tertiary")
    val onTertiary by animateColorAsState(target.onTertiary, anim, label = "onTertiary")
    val background by animateColorAsState(target.background, anim, label = "background")
    val onBackground by animateColorAsState(target.onBackground, anim, label = "onBackground")
    val surface by animateColorAsState(target.surface, anim, label = "surface")
    val onSurface by animateColorAsState(target.onSurface, anim, label = "onSurface")
    val surfaceVariant by animateColorAsState(target.surfaceVariant, anim, label = "surfaceVariant")
    val onSurfaceVariant by animateColorAsState(target.onSurfaceVariant, anim, label = "onSurfaceVariant")
    val outline by animateColorAsState(target.outline, anim, label = "outline")

    return target.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline
    )
}

@Composable
fun KocTavukTheme(
    selectedTheme: AppThemeEnum = AppThemeEnum.LIGHT_ORANGE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (selectedTheme) {
        AppThemeEnum.LIGHT_ORANGE -> LightOrangeColors
        AppThemeEnum.DARK_ORANGE -> DarkOrangeColors
    }

    MaterialTheme(
        colorScheme = animatedColorScheme(colorScheme),
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

@Composable
fun TavukKocTheme(
    themeOption: AppThemeOption = AppThemeOption.LIGHT_ORANGE,
    content: @Composable () -> Unit
) {
    KocTavukTheme(selectedTheme = themeOption, content = content)
}
