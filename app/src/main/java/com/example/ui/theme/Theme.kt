package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class ExtendedColors(
    val isDark: Boolean,
    val goldText: Color,
    val goldContainer: Color,
    val mintText: Color,
    val mintContainer: Color,
    val tealText: Color,
    val tealContainer: Color,
    val purpleText: Color,
    val purpleContainer: Color,
    val orangeText: Color,
    val onGoldButton: Color = Color(0xFF1A1000)
)

private val DarkExtendedColors = ExtendedColors(
    isDark = true,
    goldText = WarmGold,
    goldContainer = WarmGold.copy(alpha = 0.18f),
    mintText = MintVital,
    mintContainer = MintVital.copy(alpha = 0.16f),
    tealText = ElectricTeal,
    tealContainer = ElectricTeal.copy(alpha = 0.16f),
    purpleText = Color(0xFFA78BFA),
    purpleContainer = YouthPurple.copy(alpha = 0.18f),
    orangeText = FlameOrange
)

private val LightExtendedColors = ExtendedColors(
    isDark = false,
    goldText = Color(0xFFB45309),
    goldContainer = Color(0xFFFEF3C7),
    mintText = Color(0xFF047857),
    mintContainer = Color(0xFFD1FAE5),
    tealText = Color(0xFF0F766E),
    tealContainer = Color(0xFFCCFBF1),
    purpleText = Color(0xFF5B21B6),
    purpleContainer = Color(0xFFEDE9FE),
    orangeText = Color(0xFFC2410C)
)

val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }

enum class AppThemeOption(
    val id: String,
    val titleTr: String,
    val titleEn: String,
    val subtitleTr: String,
    val subtitleEn: String,
    val isDark: Boolean,
    val previewPrimary: Color,
    val previewSecondary: Color,
    val previewBg: Color
) {
    FIRE_ARENA(
        id = "fire_arena",
        titleTr = "Ateşli Arena",
        titleEn = "Fire Arena",
        subtitleTr = "Klasik Koç Tavuk koyu teması",
        subtitleEn = "Classic Koç Tavuk dark theme",
        isDark = true,
        previewPrimary = FlameOrange,
        previewSecondary = WarmGold,
        previewBg = ObsidianBg
    ),
    PURE_WHITE(
        id = "pure_white",
        titleTr = "Saf Beyaz",
        titleEn = "Pure White",
        subtitleTr = "Kar beyazı arka plan & keskin mavi",
        subtitleEn = "Crisp snow white & vivid cobalt",
        isDark = false,
        previewPrimary = PureWhitePrimary,
        previewSecondary = PureWhiteSecondary,
        previewBg = PureWhiteBg
    ),
    MORNING_LIGHT(
        id = "morning_light",
        titleTr = "Sabah Güneşi",
        titleEn = "Morning Light",
        subtitleTr = "Sıcak, ferah gündüz spor modu",
        subtitleEn = "Warm daylight fitness mode",
        isDark = false,
        previewPrimary = FlameOrange,
        previewSecondary = ElectricTeal,
        previewBg = LightBg
    ),
    CYBER_NEON(
        id = "cyber_neon",
        titleTr = "Siber Neon",
        titleEn = "Cyber Neon",
        subtitleTr = "Elektrik cyan & neon gece modu",
        subtitleEn = "Electric cyan & neon midnight",
        isDark = true,
        previewPrimary = CyberCyan,
        previewSecondary = CyberMagenta,
        previewBg = CyberBg
    ),
    ROYAL_GOLD(
        id = "royal_gold",
        titleTr = "Altın Şampiyon",
        titleEn = "Royal Gold",
        subtitleTr = "Kraliyet altını & antrasit lüksü",
        subtitleEn = "Championship gold & onyx luxury",
        isDark = true,
        previewPrimary = RoyalGold,
        previewSecondary = RoyalAmber,
        previewBg = RoyalBg
    ),
    EMERALD_VITAL(
        id = "emerald_vital",
        titleTr = "Zümrüt Enerji",
        titleEn = "Emerald Bio",
        subtitleTr = "Doğal biyo-güç & taze nane",
        subtitleEn = "Fresh vitality & bio-emerald",
        isDark = true,
        previewPrimary = EmeraldPrimary,
        previewSecondary = EmeraldSecondary,
        previewBg = EmeraldBg
    ),
    CRIMSON_SAMURAI(
        id = "crimson_samurai",
        titleTr = "Kızıl Samuray",
        titleEn = "Crimson Arena",
        subtitleTr = "Yakut kırmızısı & karbon siyah",
        subtitleEn = "Ruby crimson & carbon black",
        isDark = true,
        previewPrimary = CrimsonPrimary,
        previewSecondary = CrimsonSecondary,
        previewBg = CrimsonBg
    ),
    ROYAL_VIOLET(
        id = "royal_violet",
        titleTr = "Mor Galaksi",
        titleEn = "Galaxy Violet",
        subtitleTr = "Derin mor & parlak buz mavisi",
        subtitleEn = "Deep violet & electric ice",
        isDark = true,
        previewPrimary = VioletPrimary,
        previewSecondary = VioletSecondary,
        previewBg = VioletBg
    );

    fun title(lang: String): String = if (lang == "en") titleEn else titleTr
    fun subtitle(lang: String): String = if (lang == "en") subtitleEn else subtitleTr

    companion object {
        fun fromId(id: String): AppThemeOption = entries.firstOrNull { it.id == id } ?: FIRE_ARENA
    }
}

private val FireArenaScheme = darkColorScheme(
    primary = FlameOrange,
    onPrimary = CreamText,
    primaryContainer = FlameOrangeDeep,
    onPrimaryContainer = CreamText,
    secondary = WarmGold,
    onSecondary = ObsidianBg,
    secondaryContainer = EspressoElevated,
    onSecondaryContainer = WarmGold,
    tertiary = ElectricTeal,
    onTertiary = ObsidianBg,
    background = ObsidianBg,
    onBackground = CreamText,
    surface = EspressoSurface,
    onSurface = CreamText,
    surfaceVariant = EspressoCard,
    onSurfaceVariant = MutedSandText,
    outline = BorderWarm,
    error = ErrorCrimson,
    onError = CreamText
)

private val PureWhiteScheme = lightColorScheme(
    primary = PureWhitePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = PureWhiteSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF9A3412),
    tertiary = PureWhiteTertiary,
    onTertiary = Color.White,
    background = PureWhiteBg,
    onBackground = PureWhiteInk,
    surface = PureWhiteSurface,
    onSurface = PureWhiteInk,
    surfaceVariant = PureWhiteCard,
    onSurfaceVariant = PureWhiteMuted,
    outline = PureWhiteOutline,
    error = Color(0xFFDC2626),
    onError = Color.White
)

private val MorningLightScheme = lightColorScheme(
    primary = FlameOrange,
    onPrimary = LightSurface,
    primaryContainer = LightCard,
    onPrimaryContainer = FlameOrangeDeep,
    secondary = WarmGold,
    onSecondary = DarkInk,
    secondaryContainer = LightCard,
    onSecondaryContainer = DarkInk,
    tertiary = ElectricTeal,
    onTertiary = LightSurface,
    background = LightBg,
    onBackground = DarkInk,
    surface = LightSurface,
    onSurface = DarkInk,
    surfaceVariant = LightCard,
    onSurfaceVariant = MutedInk,
    outline = LightOutline,
    error = ErrorCrimson,
    onError = LightSurface
)

private val CyberNeonScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = CyberBg,
    primaryContainer = Color(0xFF004F66),
    onPrimaryContainer = CyberText,
    secondary = CyberMagenta,
    onSecondary = CyberText,
    secondaryContainer = Color(0xFF28173A),
    onSecondaryContainer = CyberMagenta,
    tertiary = CyberLime,
    onTertiary = CyberBg,
    background = CyberBg,
    onBackground = CyberText,
    surface = CyberSurface,
    onSurface = CyberText,
    surfaceVariant = CyberCard,
    onSurfaceVariant = CyberMuted,
    outline = CyberBorder,
    error = ErrorCrimson,
    onError = CyberText
)

private val RoyalGoldScheme = darkColorScheme(
    primary = RoyalGold,
    onPrimary = RoyalBg,
    primaryContainer = Color(0xFF4F3B00),
    onPrimaryContainer = RoyalGold,
    secondary = RoyalAmber,
    onSecondary = RoyalBg,
    secondaryContainer = Color(0xFF33261A),
    onSecondaryContainer = RoyalGold,
    tertiary = RoyalMint,
    onTertiary = RoyalBg,
    background = RoyalBg,
    onBackground = RoyalText,
    surface = RoyalSurface,
    onSurface = RoyalText,
    surfaceVariant = RoyalCard,
    onSurfaceVariant = RoyalMuted,
    outline = RoyalBorder,
    error = ErrorCrimson,
    onError = RoyalText
)

private val EmeraldVitalScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldBg,
    primaryContainer = Color(0xFF004D28),
    onPrimaryContainer = EmeraldText,
    secondary = EmeraldSecondary,
    onSecondary = EmeraldBg,
    secondaryContainer = Color(0xFF1E3B2B),
    onSecondaryContainer = EmeraldSecondary,
    tertiary = EmeraldTertiary,
    onTertiary = EmeraldBg,
    background = EmeraldBg,
    onBackground = EmeraldText,
    surface = EmeraldSurface,
    onSurface = EmeraldText,
    surfaceVariant = EmeraldCard,
    onSurfaceVariant = EmeraldMuted,
    outline = EmeraldBorder,
    error = ErrorCrimson,
    onError = EmeraldText
)

private val CrimsonSamuraiScheme = darkColorScheme(
    primary = CrimsonPrimary,
    onPrimary = CrimsonText,
    primaryContainer = Color(0xFF5C0A1E),
    onPrimaryContainer = CrimsonText,
    secondary = CrimsonSecondary,
    onSecondary = CrimsonBg,
    secondaryContainer = Color(0xFF3A2510),
    onSecondaryContainer = CrimsonSecondary,
    tertiary = CrimsonTertiary,
    onTertiary = CrimsonBg,
    background = CrimsonBg,
    onBackground = CrimsonText,
    surface = CrimsonSurface,
    onSurface = CrimsonText,
    surfaceVariant = CrimsonCard,
    onSurfaceVariant = CrimsonMuted,
    outline = CrimsonBorder,
    error = ErrorCrimson,
    onError = CrimsonText
)

private val RoyalVioletScheme = darkColorScheme(
    primary = VioletPrimary,
    onPrimary = VioletBg,
    primaryContainer = Color(0xFF3F1D6B),
    onPrimaryContainer = VioletText,
    secondary = VioletSecondary,
    onSecondary = VioletBg,
    secondaryContainer = Color(0xFF143642),
    onSecondaryContainer = VioletSecondary,
    tertiary = VioletTertiary,
    onTertiary = VioletBg,
    background = VioletBg,
    onBackground = VioletText,
    surface = VioletSurface,
    onSurface = VioletText,
    surfaceVariant = VioletCard,
    onSurfaceVariant = VioletMuted,
    outline = VioletBorder,
    error = ErrorCrimson,
    onError = VioletText
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
private fun animatedColorScheme(target: ColorScheme): ColorScheme {
    val anim = tween<Color>(durationMillis = 360)
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
fun TavukKocTheme(
    themeOption: AppThemeOption = AppThemeOption.FIRE_ARENA,
    content: @Composable () -> Unit
) {
    val targetScheme = when (themeOption) {
        AppThemeOption.FIRE_ARENA -> FireArenaScheme
        AppThemeOption.PURE_WHITE -> PureWhiteScheme
        AppThemeOption.MORNING_LIGHT -> MorningLightScheme
        AppThemeOption.CYBER_NEON -> CyberNeonScheme
        AppThemeOption.ROYAL_GOLD -> RoyalGoldScheme
        AppThemeOption.EMERALD_VITAL -> EmeraldVitalScheme
        AppThemeOption.CRIMSON_SAMURAI -> CrimsonSamuraiScheme
        AppThemeOption.ROYAL_VIOLET -> RoyalVioletScheme
    }

    val extended = if (themeOption.isDark) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extended) {
        MaterialTheme(
            colorScheme = animatedColorScheme(targetScheme),
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}
