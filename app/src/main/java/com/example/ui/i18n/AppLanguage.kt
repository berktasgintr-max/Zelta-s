package com.example.ui.i18n

import java.util.Locale

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val regionTag: String
) {
    EN("en", "English", "English", "Europe / Global"),
    TR("tr", "Türkçe", "Turkish", "Türkiye"),
    DE("de", "Deutsch", "German", "Deutschland / AT / CH"),
    FR("fr", "Français", "French", "France / BE / CH"),
    ES("es", "Español", "Spanish", "España"),
    IT("it", "Italiano", "Italian", "Italia"),
    RU("ru", "Русский", "Russian", "Россия / Europe"),
    NL("nl", "Nederlands", "Dutch", "Nederland / België"),
    PL("pl", "Polski", "Polish", "Polska"),
    PT("pt", "Português", "Portuguese", "Portugal / Brasil");

    fun aiPromptLanguageName(): String = when (this) {
        EN -> "English"
        TR -> "Turkish (Türkçe)"
        DE -> "German (Deutsch)"
        FR -> "French (Français)"
        ES -> "Spanish (Español)"
        IT -> "Italian (Italiano)"
        RU -> "Russian (Русский)"
        NL -> "Dutch (Nederlands)"
        PL -> "Polish (Polski)"
        PT -> "Portuguese (Português)"
    }

    companion object {
        fun systemDefaultCode(): String =
            if (Locale.getDefault().language.equals("tr", ignoreCase = true)) TR.code else EN.code

        fun fromCode(code: String?): AppLanguage {
            val normalized = code
                ?.trim()
                ?.substringBefore('-')
                ?.substringBefore('_')
                ?.lowercase()
            return entries.firstOrNull { it.code == normalized } ?: EN
        }
    }
}
