package com.example.data.catalog

import android.content.Context
import androidx.compose.runtime.Composable
import com.example.R
import com.example.data.model.ExerciseItem
import com.example.data.model.WorkoutProgram
import com.example.ui.i18n.LocalLocalizedContext

private fun isTurkish(context: Context): Boolean {
    val lang = context.resources.configuration.locales.get(0)?.language ?: ""
    return lang.equals("tr", ignoreCase = true)
}

private fun lookupString(context: Context, resName: String, fallback: String): String {
    val resId = context.resources.getIdentifier(resName, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else fallback
}

private fun lookupStringArray(context: Context, resName: String, fallback: List<String>): List<String> {
    val resId = context.resources.getIdentifier(resName, "array", context.packageName)
    if (resId == 0) return fallback
    val arr = context.resources.getStringArray(resId)
    return if (arr.isNotEmpty()) arr.toList() else fallback
}

fun WorkoutProgram.localizedTitle(context: Context): String {
    val fallback = if (isTurkish(context) && titleTr.isNotBlank()) titleTr else title
    return lookupString(context, "w_${id}_title", fallback)
}

fun WorkoutProgram.localizedSubtitle(context: Context): String {
    val fallback = if (isTurkish(context) && subtitleTr.isNotBlank()) subtitleTr else subtitle
    return lookupString(context, "w_${id}_sub", fallback)
}

fun WorkoutProgram.localizedDifficulty(context: Context): String = when (difficulty.trim()) {
    "Başlangıç", "Beginner" -> context.getString(R.string.diff_beginner)
    "Orta", "Intermediate" -> context.getString(R.string.diff_intermediate)
    "Zor", "Advanced" -> context.getString(R.string.diff_advanced)
    "İleri", "PRO", "Pro" -> context.getString(R.string.diff_pro)
    else -> difficulty
}

fun ExerciseItem.localizedName(context: Context): String {
    val fallback = if (isTurkish(context) && nameTr.isNotBlank()) nameTr else name
    return lookupString(context, "ex_${id}_name", fallback)
}

fun ExerciseItem.localizedTargetMuscle(context: Context): String {
    val fallback = if (isTurkish(context) && targetMuscleTr.isNotBlank()) targetMuscleTr else targetMuscle
    return lookupString(context, "ex_${id}_muscle", fallback)
}

fun ExerciseItem.localizedSecondaryMuscles(context: Context): String {
    return if (isTurkish(context) && secondaryMusclesTr.isNotBlank()) secondaryMusclesTr else secondaryMuscles
}

fun ExerciseItem.localizedSetsReps(context: Context): String =
    lookupString(context, "ex_${id}_sets", setsReps)

fun ExerciseItem.localizedSteps(context: Context): List<String> {
    val fallback = if (isTurkish(context) && stepsTr.isNotEmpty()) stepsTr else steps
    return lookupStringArray(context, "ex_${id}_steps", fallback)
}

fun ExerciseItem.localizedFormCue(context: Context): String {
    val fallback = if (isTurkish(context) && formCueTr.isNotBlank()) formCueTr else formCue
    return lookupString(context, "ex_${id}_cue", fallback)
}

fun ExerciseItem.localizedBreathingCue(context: Context): String {
    val fallback = if (isTurkish(context) && breathingCueTr.isNotBlank()) breathingCueTr else breathingCue
    return lookupString(context, "ex_${id}_breath", fallback)
}

fun ExerciseItem.localizedCommonMistake(context: Context): String {
    val fallback = if (isTurkish(context) && commonMistakeTr.isNotBlank()) commonMistakeTr else commonMistake
    return lookupString(context, "ex_${id}_mistake", fallback)
}

@Composable
fun WorkoutProgram.localizedTitle(): String = localizedTitle(LocalLocalizedContext.current)

@Composable
fun WorkoutProgram.localizedSubtitle(): String = localizedSubtitle(LocalLocalizedContext.current)

@Composable
fun WorkoutProgram.localizedDifficulty(): String = localizedDifficulty(LocalLocalizedContext.current)

@Composable
fun ExerciseItem.localizedName(): String = localizedName(LocalLocalizedContext.current)

@Composable
fun ExerciseItem.localizedTargetMuscle(): String = localizedTargetMuscle(LocalLocalizedContext.current)

@Composable
fun ExerciseItem.localizedSecondaryMuscles(): String = localizedSecondaryMuscles(LocalLocalizedContext.current)

@Composable
fun ExerciseItem.localizedSetsReps(): String = localizedSetsReps(LocalLocalizedContext.current)

@Composable
fun ExerciseItem.localizedSteps(): List<String> = localizedSteps(LocalLocalizedContext.current)

@Composable
fun ExerciseItem.localizedFormCue(): String = localizedFormCue(LocalLocalizedContext.current)

@Composable
fun ExerciseItem.localizedBreathingCue(): String = localizedBreathingCue(LocalLocalizedContext.current)

@Composable
fun ExerciseItem.localizedCommonMistake(): String = localizedCommonMistake(LocalLocalizedContext.current)
