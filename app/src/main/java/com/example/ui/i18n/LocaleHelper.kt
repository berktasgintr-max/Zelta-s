package com.example.ui.i18n

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

object LocaleHelper {
    fun localeFor(langCode: String): Locale =
        Locale.forLanguageTag(AppLanguage.fromCode(langCode).code)

    fun localizedContext(context: Context, langCode: String): Context {
        val locale = localeFor(langCode)
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
        return context.createConfigurationContext(config)
    }
}

val LocalLocalizedContext = staticCompositionLocalOf<Context> {
    error("LocalLocalizedContext must be provided at the root of the Compose tree")
}

val LocalAppLocale = staticCompositionLocalOf<Locale> {
    Locale.getDefault()
}

val LocalAppLanguageCode = staticCompositionLocalOf<String> {
    AppLanguage.EN.code
}

@Composable
@ReadOnlyComposable
fun strRes(@StringRes id: Int): String =
    LocalLocalizedContext.current.getString(id)

@Composable
@ReadOnlyComposable
fun strRes(@StringRes id: Int, vararg formatArgs: Any): String {
    val ctx = LocalLocalizedContext.current
    val locale = LocalAppLocale.current
    return String.format(locale, ctx.getString(id), *formatArgs)
}
