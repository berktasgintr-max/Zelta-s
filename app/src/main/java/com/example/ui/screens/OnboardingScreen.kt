package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.catalog.CoachWisdomAndStories
import com.example.data.model.UserProfile
import com.example.ui.components.AnimatedKocTavuk
import com.example.ui.components.CleanTextField
import com.example.ui.components.CoachAnimationMood
import com.example.ui.components.FreeVsProComparisonTable
import com.example.ui.components.HoldToCommitButton
import com.example.ui.components.OnboardingAnalysisLoader
import com.example.ui.components.OnboardingCircularHeader
import com.example.ui.components.OnboardingFocusGrid
import com.example.ui.components.OnboardingMetricPill
import com.example.ui.components.OnboardingOptionCard
import com.example.ui.components.OnboardingProgressCurveChart
import com.example.ui.components.OnboardingRulerPicker
import com.example.ui.components.OnboardingSelectableChip
import com.example.ui.components.OnboardingSocialAuthorityCard
import com.example.ui.components.OnboardingStepTitle
import com.example.ui.components.OnboardingStoryCard
import com.example.ui.i18n.LocalAppLanguageCode
import com.example.ui.i18n.LocalAppLocale
import com.example.ui.i18n.LocalLocalizedContext
import com.example.ui.i18n.strRes
import com.example.ui.theme.AppThemeOption
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Suppress("UNUSED_PARAMETER")
@Composable
fun OnboardingScreen(
    initialProfile: UserProfile,
    activeTheme: AppThemeOption = AppThemeOption.LIGHT_ORANGE,
    onToggleTheme: () -> Unit = {},
    onSelectLanguage: (String) -> Unit = {},
    onComplete: (UserProfile, String) -> Unit
) {
    val context = LocalContext.current
    val localizedCtx = LocalLocalizedContext.current
    val langCode = LocalAppLanguageCode.current
    val locale = LocalAppLocale.current
    val totalSteps = 14
    var step by remember { mutableIntStateOf(0) }

    var name by remember {
        mutableStateOf(
            if (initialProfile.displayName == "Sporcu" || initialProfile.displayName == "Athlete") ""
            else initialProfile.displayName
        )
    }
    var gender by remember { mutableStateOf(initialProfile.gender) }
    var age by remember { mutableFloatStateOf(initialProfile.age.toFloat().coerceIn(10f, 75f)) }
    var heightCm by remember { mutableFloatStateOf(initialProfile.heightCm.toFloat().coerceIn(130f, 215f)) }
    var currentWeight by remember { mutableFloatStateOf(initialProfile.currentWeightKg.toFloat().coerceIn(35f, 160f)) }
    var targetWeight by remember { mutableFloatStateOf(initialProfile.targetWeightKg.toFloat().coerceIn(35f, 160f)) }
    var dailyCalories by remember { mutableFloatStateOf(initialProfile.dailyCalorieTarget.toFloat().coerceIn(1200f, 4000f)) }
    var bodyFatCurrent by remember { mutableFloatStateOf(initialProfile.bodyFatCurrentPct.toFloat().coerceIn(6f, 48f)) }
    var bodyFatTarget by remember { mutableFloatStateOf(initialProfile.bodyFatTargetPct.toFloat().coerceIn(6f, 38f)) }
    var goal by remember { mutableStateOf(initialProfile.goal) }
    val validFocusKeys = remember { setOf("abs", "chest", "arms", "shoulders", "back", "legs") }
    var focusAreas by remember {
        mutableStateOf(
            initialProfile.focusAreasCsv
                .split(",")
                .map { it.trim() }
                .filter { it in validFocusKeys }
                .toSet()
        )
    }
    var activityLevel by remember { mutableStateOf(initialProfile.activityLevel) }
    var experienceLevel by remember { mutableStateOf(initialProfile.experienceLevel) }
    var sleepQuality by remember { mutableStateOf(initialProfile.sleepQuality) }
    var nutritionHabit by remember { mutableStateOf(initialProfile.nutritionHabit) }
    var stressLevel by remember { mutableStateOf(initialProfile.stressLevel) }
    var badHabits by remember {
        mutableStateOf(initialProfile.badHabitsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet())
    }
    var squatCapacity by remember { mutableStateOf(initialProfile.squatCapacity) }
    var wantBetterSleep by remember { mutableStateOf(initialProfile.wantBetterSleep) }
    var wantLessStress by remember { mutableStateOf(initialProfile.wantLessStress) }
    var environment by remember { mutableStateOf(initialProfile.environment) }
    var sessionMinutes by remember { mutableIntStateOf(initialProfile.sessionMinutes) }
    var planPace by remember { mutableStateOf(initialProfile.planPace) }
    var isCommitted by remember { mutableStateOf(false) }
    var trainingDays by remember {
        mutableStateOf(
            initialProfile.trainingDaysCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                .ifEmpty { setOf("mon", "wed", "fri", "sat") }
        )
    }
    var notificationsEnabled by remember {
        mutableStateOf(
            initialProfile.notificationsEnabled ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        )
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsEnabled = granted
    }

    val computedTargetIso = remember(currentWeight, targetWeight, planPace) {
        val diff = abs(currentWeight - targetWeight)
        val weeklyRate = when (planPace) {
            "aggressive" -> 0.75f
            "steady" -> 0.35f
            else -> 0.55f
        }
        val weeks = (diff / weeklyRate).coerceIn(4f, 52f)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, (weeks * 7f).roundToInt())
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    fun buildProfile(): UserProfile {
        val primaryFocus = when {
            focusAreas.contains("legs") -> "lower"
            focusAreas.contains("abs") && focusAreas.size == 1 -> "core"
            focusAreas.any { it in setOf("chest", "arms", "shoulders", "back") } -> "upper"
            else -> "full_body"
        }
        return initialProfile.copy(
            displayName = name.trim().ifEmpty { localizedCtx.getString(R.string.common_default_champ) },
            gender = gender,
            appLanguage = langCode,
            isLanguageSelected = true,
            age = age.roundToInt(),
            heightCm = heightCm.roundToInt(),
            startWeightKg = currentWeight.roundToInt().toDouble(),
            currentWeightKg = currentWeight.roundToInt().toDouble(),
            targetWeightKg = targetWeight.roundToInt().toDouble(),
            goal = goal,
            activityLevel = activityLevel,
            experienceLevel = experienceLevel,
            environment = environment,
            weeklyDays = trainingDays.size.coerceIn(1, 7),
            sessionMinutes = sessionMinutes,
            sleepQuality = sleepQuality,
            nutritionHabit = nutritionHabit,
            focusArea = primaryFocus,
            focusAreasCsv = focusAreas.joinToString(","),
            bodyFatCurrentPct = bodyFatCurrent.roundToInt(),
            bodyFatTargetPct = bodyFatTarget.roundToInt(),
            dailyCalorieTarget = dailyCalories.roundToInt(),
            stressLevel = stressLevel,
            badHabitsCsv = badHabits.joinToString(","),
            squatCapacity = squatCapacity,
            wantBetterSleep = wantBetterSleep,
            wantLessStress = wantLessStress,
            planPace = planPace,
            trainingDaysCsv = trainingDays.joinToString(","),
            notificationsEnabled = notificationsEnabled,
            targetDateIso = computedTargetIso
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        OnboardingCircularHeader(
            step = step,
            totalSteps = totalSteps,
            activeTheme = activeTheme,
            onBack = { if (step > 0) step-- },
            onToggleTheme = onToggleTheme
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val forward = targetState > initialState
                    (slideInHorizontally(tween(240)) { if (forward) it / 3 else -it / 3 } + fadeIn(tween(220))) togetherWith
                        (slideOutHorizontally(tween(200)) { if (forward) -it / 3 else it / 3 } + fadeOut(tween(180)))
                },
                label = "onboarding_step"
            ) { currentStep ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (currentStep) {
                        0 -> {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.large,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                                    AnimatedKocTavuk(
                                        size = 140.dp,
                                        mood = CoachAnimationMood.COACHING,
                                        loopDemo = true,
                                        showAura = false,
                                        contentDescription = "FitChicken Mascot",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), shape = CircleShape) {
                                            Text(
                                                text = strRes(R.string.onb_coach_badge),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(strRes(R.string.onb_step0_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                        Text(strRes(R.string.onb_step0_sub), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it.take(30) },
                                label = { Text(strRes(R.string.onb_name_label)) },
                                placeholder = { Text(strRes(R.string.onb_name_placeholder)) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .testTag("onboarding_name_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFF6600),
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedLabelColor = Color(0xFFFF6600)
                                )
                            )
                            Text(strRes(R.string.onb_step1_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                GenderChipCard(
                                    title = strRes(R.string.male_label),
                                    icon = Icons.Rounded.Male,
                                    selected = gender == "male",
                                    onClick = { gender = "male" },
                                    modifier = Modifier.weight(1f).testTag("onboarding_gender_male")
                                )
                                GenderChipCard(
                                    title = strRes(R.string.female_label),
                                    icon = Icons.Rounded.Female,
                                    selected = gender == "female",
                                    onClick = { gender = "female" },
                                    modifier = Modifier.weight(1f).testTag("onboarding_gender_female")
                                )
                            }
                        }
                        1 -> {
                            OnboardingStepTitle(strRes(R.string.onb_step5_title), strRes(R.string.onb_step5_sub))
                            OnboardingOptionCard(strRes(R.string.onb_goal_muscle), strRes(R.string.onb_goal_muscle_sub), goal == "muscle", { goal = "muscle" })
                            OnboardingOptionCard(strRes(R.string.onb_goal_fat), strRes(R.string.onb_goal_fat_sub), goal == "fat_loss", { goal = "fat_loss" })
                            OnboardingOptionCard(strRes(R.string.onb_goal_fitness), strRes(R.string.onb_goal_fitness_sub), goal == "fitness", { goal = "fitness" })
                            OnboardingOptionCard(strRes(R.string.onb_goal_growth), strRes(R.string.onb_goal_growth_sub), goal == "growth_posture", { goal = "growth_posture" })
                            OnboardingSocialAuthorityCard()
                        }
                        2 -> {
                            val ageInt = age.roundToInt()
                            val hInt = heightCm.roundToInt()
                            OnboardingStepTitle(strRes(R.string.onb_step2_title), strRes(R.string.onb_step3_sub))
                            OnboardingMetricPill(strRes(R.string.onb_age_badge_fmt, ageInt))
                            Slider(
                                value = age,
                                onValueChange = { age = it },
                                valueRange = 10f..75f,
                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("onboarding_age_slider")
                            )
                            if (ageInt <= 18) {
                                Surface(color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(strRes(R.string.onb_youth_banner), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                            OnboardingMetricPill("$hInt cm")
                            OnboardingRulerPicker(value = heightCm, valueRange = 130f..215f, stepUnit = 1f, onValueChange = { heightCm = it })
                            Slider(
                                value = heightCm,
                                onValueChange = { heightCm = it },
                                valueRange = 130f..215f,
                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("onboarding_height_slider")
                            )
                        }
                        3 -> {
                            OnboardingStepTitle(strRes(R.string.onb_step4_title), strRes(R.string.onb_step4_sub))
                            OnboardingMetricPill(strRes(R.string.onb_current_weight_fmt, currentWeight.roundToInt()))
                            OnboardingRulerPicker(value = currentWeight, valueRange = 35f..160f, stepUnit = 1f, onValueChange = { currentWeight = it })
                            Slider(value = currentWeight, onValueChange = { currentWeight = it }, valueRange = 35f..160f)
                            OnboardingMetricPill(strRes(R.string.onb_target_weight_fmt, targetWeight.roundToInt()))
                            OnboardingRulerPicker(value = targetWeight, valueRange = 35f..160f, stepUnit = 1f, onValueChange = { targetWeight = it })
                            Slider(value = targetWeight, onValueChange = { targetWeight = it }, valueRange = 35f..160f)
                        }
                        4 -> {
                            val hMeters = heightCm / 100f
                            val bmiVal = if (hMeters > 0f) currentWeight / (hMeters * hMeters) else 22f
                            OnboardingStepTitle(strRes(R.string.onb_target_calories_title), strRes(R.string.onb_target_calories_sub))
                            OnboardingMetricPill(strRes(R.string.onb_calories_kcal_fmt, (dailyCalories / 50f).roundToInt() * 50))
                            Slider(value = dailyCalories, onValueChange = { dailyCalories = it }, valueRange = 1200f..4000f)
                            OnboardingMetricPill(strRes(R.string.onb_body_fat_current_fmt, bodyFatCurrent.roundToInt()))
                            Slider(value = bodyFatCurrent, onValueChange = { bodyFatCurrent = it }, valueRange = 6f..48f)
                            OnboardingMetricPill(strRes(R.string.onb_body_fat_target_fmt, bodyFatTarget.roundToInt()))
                            Slider(value = bodyFatTarget, onValueChange = { bodyFatTarget = it }, valueRange = 6f..38f)
                            Surface(color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = strRes(R.string.onb_bmi_live_fmt, String.format(Locale.US, "%.1f", bmiVal)),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                        5 -> {
                            OnboardingStepTitle(strRes(R.string.onb_focus_multi_title), strRes(R.string.onb_focus_multi_sub))
                            OnboardingFocusGrid(
                                selectedAreas = focusAreas,
                                onToggleArea = { area ->
                                    focusAreas = if (focusAreas.contains(area)) {
                                        focusAreas - area
                                    } else {
                                        focusAreas + area
                                    }
                                }
                            )
                            Text(
                                text = strRes(R.string.onb_focus_selected_fmt, focusAreas.size),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        6 -> {
                            OnboardingStepTitle(strRes(R.string.onb_step6_title), strRes(R.string.onb_step7_sub))
                            OnboardingOptionCard(strRes(R.string.onb_act_sedentary), strRes(R.string.onb_act_sedentary_sub), activityLevel == "sedentary", { activityLevel = "sedentary" })
                            OnboardingOptionCard(strRes(R.string.onb_act_light), strRes(R.string.onb_act_light_sub), activityLevel == "light", { activityLevel = "light" })
                            OnboardingOptionCard(strRes(R.string.onb_act_moderate), strRes(R.string.onb_act_moderate_sub), activityLevel == "moderate", { activityLevel = "moderate" })
                            OnboardingOptionCard(strRes(R.string.onb_act_active), strRes(R.string.onb_act_active_sub), activityLevel == "active", { activityLevel = "active" })
                            Text(strRes(R.string.onb_step7_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf("beginner" to R.string.diff_beginner, "occasional" to R.string.onb_exp_occasional, "regular" to R.string.onb_exp_regular, "advanced" to R.string.diff_advanced).forEach { (k, res) ->
                                    OnboardingSelectableChip(strRes(res), experienceLevel == k, { experienceLevel = k }, Modifier.weight(1f))
                                }
                            }
                        }
                        7 -> {
                            OnboardingStepTitle(strRes(R.string.onb_lifestyle_title), strRes(R.string.onb_lifestyle_sub))
                            Text(strRes(R.string.onb_stress_label), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OnboardingSelectableChip(strRes(R.string.onb_stress_low), stressLevel == "low", { stressLevel = "low" }, Modifier.weight(1f))
                                OnboardingSelectableChip(strRes(R.string.onb_stress_moderate), stressLevel == "moderate", { stressLevel = "moderate" }, Modifier.weight(1f))
                                OnboardingSelectableChip(strRes(R.string.onb_stress_high), stressLevel == "high", { stressLevel = "high" }, Modifier.weight(1f))
                            }
                            OnboardingOptionCard(strRes(R.string.onb_sleep_good), strRes(R.string.onb_sleep_good_sub), sleepQuality == "7-8 saat düzenli", { sleepQuality = "7-8 saat düzenli" })
                            OnboardingOptionCard(strRes(R.string.onb_sleep_low), strRes(R.string.onb_sleep_low_sub), sleepQuality != "7-8 saat düzenli", { sleepQuality = "5-6 saat düzensiz" })
                            OnboardingOptionCard(strRes(R.string.onb_nut_protein), strRes(R.string.onb_nut_protein_sub), nutritionHabit == "Protein odaklı", { nutritionHabit = "Protein odaklı" })
                            Text(strRes(R.string.onb_habits_label), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            listOf(
                                "late_snacking" to R.string.habit_late_snacking,
                                "sugary_drinks" to R.string.habit_sugary_drinks,
                                "skipping_meals" to R.string.habit_skipping_meals,
                                "low_water" to R.string.habit_low_water,
                                "long_sitting" to R.string.habit_long_sitting
                            ).forEach { (key, res) ->
                                val active = badHabits.contains(key)
                                OnboardingSelectableChip(
                                    label = (if (active) "✓ " else "") + strRes(res),
                                    selected = active,
                                    onClick = { badHabits = if (active) badHabits - key else badHabits + key },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        8 -> {
                            OnboardingStepTitle(strRes(R.string.onb_squat_title), strRes(R.string.onb_squat_sub))
                            OnboardingOptionCard(strRes(R.string.squat_0_10), "", squatCapacity == "0_10", { squatCapacity = "0_10" })
                            OnboardingOptionCard(strRes(R.string.squat_10_20), "", squatCapacity == "10_20", { squatCapacity = "10_20" })
                            OnboardingOptionCard(strRes(R.string.squat_20_35), "", squatCapacity == "20_35", { squatCapacity = "20_35" })
                            OnboardingOptionCard(strRes(R.string.squat_35_plus), "", squatCapacity == "35_plus", { squatCapacity = "35_plus" })
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { wantBetterSleep = !wantBetterSleep }) {
                                Checkbox(checked = wantBetterSleep, onCheckedChange = { wantBetterSleep = it })
                                Text(strRes(R.string.onb_micro_sleep), style = MaterialTheme.typography.bodyMedium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { wantLessStress = !wantLessStress }) {
                                Checkbox(checked = wantLessStress, onCheckedChange = { wantLessStress = it })
                                Text(strRes(R.string.onb_micro_stress), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        9 -> {
                            OnboardingStepTitle(strRes(R.string.onb_step9_title), strRes(R.string.onb_step9_sub))
                            OnboardingOptionCard(strRes(R.string.onb_env_gym), strRes(R.string.onb_env_gym_sub), environment == "gym", { environment = "gym" })
                            OnboardingOptionCard(strRes(R.string.onb_env_home), strRes(R.string.onb_env_home_sub), environment == "home", { environment = "home" })
                            OnboardingOptionCard(strRes(R.string.onb_env_outdoor), strRes(R.string.onb_env_outdoor_sub), environment == "outdoor", { environment = "outdoor" })
                            OnboardingOptionCard(strRes(R.string.onb_env_hybrid), strRes(R.string.onb_env_hybrid_sub), environment == "hybrid", { environment = "hybrid" })
                            Text(strRes(R.string.onb_session_len_fmt, sessionMinutes), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OnboardingSelectableChip(strRes(R.string.onb_dur_short), sessionMinutes == 15, { sessionMinutes = 15 }, Modifier.weight(1f))
                                OnboardingSelectableChip(strRes(R.string.onb_dur_medium), sessionMinutes == 25, { sessionMinutes = 25 }, Modifier.weight(1f))
                                OnboardingSelectableChip(strRes(R.string.onb_dur_long), sessionMinutes == 35 || sessionMinutes >= 40, { sessionMinutes = 35 }, Modifier.weight(1f))
                            }
                            Text(strRes(R.string.onb_pace_label), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OnboardingSelectableChip(strRes(R.string.pace_steady), planPace == "steady", { planPace = "steady" }, Modifier.weight(1f))
                                OnboardingSelectableChip(strRes(R.string.pace_balanced), planPace == "balanced", { planPace = "balanced" }, Modifier.weight(1f))
                                OnboardingSelectableChip(strRes(R.string.pace_aggressive), planPace == "aggressive", { planPace = "aggressive" }, Modifier.weight(1f))
                            }
                        }
                        10 -> {
                            val monthDelta = when (planPace) {
                                "aggressive" -> 3.0f
                                "steady" -> 1.4f
                                else -> 2.2f
                            }
                            val month1 = if (targetWeight < currentWeight) {
                                (currentWeight - monthDelta).coerceAtLeast(targetWeight)
                            } else {
                                (currentWeight + monthDelta).coerceAtMost(targetWeight)
                            }
                            OnboardingStepTitle(strRes(R.string.onb_curve_title), strRes(R.string.onb_curve_sub))
                            OnboardingProgressCurveChart(
                                currentWeightKg = currentWeight,
                                targetWeightKg = targetWeight,
                                month1WeightKg = month1,
                                targetDateIso = computedTargetIso
                            )
                            CoachWisdomAndStories.stories(localizedCtx).take(2).forEach { OnboardingStoryCard(it) }
                        }
                        11 -> {
                            OnboardingStepTitle(strRes(R.string.onb_commit_title), strRes(R.string.onb_commit_sub))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    AnimatedKocTavuk(
                                        size = 128.dp,
                                        mood = if (isCommitted) CoachAnimationMood.CHEERING else CoachAnimationMood.COACHING,
                                        loopDemo = true,
                                        showAura = false
                                    )
                                    Text(
                                        text = strRes(R.string.onb_commit_pledge),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                    HoldToCommitButton(isCommitted = isCommitted, onCommitted = { isCommitted = true })
                                }
                            }
                        }
                        12 -> {
                            OnboardingStepTitle(strRes(R.string.onb_weekdays_title), strRes(R.string.onb_weekdays_sub))
                            val dayTokens = listOf("mon" to Calendar.MONDAY, "tue" to Calendar.TUESDAY, "wed" to Calendar.WEDNESDAY, "thu" to Calendar.THURSDAY, "fri" to Calendar.FRIDAY, "sat" to Calendar.SATURDAY, "sun" to Calendar.SUNDAY)
                            val shortFmt = remember(locale) { SimpleDateFormat("EE", locale) }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                dayTokens.forEach { (token, calConst) ->
                                    val dayText = remember(locale, token) {
                                        val c = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, calConst) }
                                        shortFmt.format(c.time).take(3).uppercase(locale)
                                    }
                                    val selected = trainingDays.contains(token)
                                    OnboardingSelectableChip(
                                        label = dayText,
                                        selected = selected,
                                        onClick = {
                                            trainingDays = if (selected && trainingDays.size > 1) trainingDays - token else trainingDays + token
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Text(strRes(R.string.onb_days_selected_fmt, trainingDays.size), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(strRes(R.string.onb_notify_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Text(strRes(R.string.onb_notify_sub), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (notificationsEnabled) {
                                        Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), shape = CircleShape) {
                                            Text(strRes(R.string.onb_notify_enabled_badge), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                } else {
                                                    notificationsEnabled = true
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().height(48.dp)
                                        ) {
                                            Text(strRes(R.string.onb_notify_allow_btn))
                                        }
                                    }
                                }
                            }
                        }
                        13 -> {
                            OnboardingAnalysisLoader(onFinished = { step = totalSteps })
                        }
                        totalSteps -> {
                            OnboardingStepTitle(strRes(R.string.onb_step15_title), strRes(R.string.onb_step15_sub))
                            FreeVsProComparisonTable()
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strRes(R.string.onb_pro_benefits_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    listOf(R.string.onb_pro_b1, R.string.onb_pro_b3, R.string.onb_pro_b4, R.string.onb_pro_b5).forEach { res ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(strRes(res), style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Button(
                                        onClick = { onComplete(buildProfile(), "pro") },
                                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("select_pro_plan_button")
                                    ) {
                                        Icon(Icons.Rounded.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strRes(R.string.onb_start_pro), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.LockOpen, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strRes(R.string.onb_free_plan_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    }
                                    Text(strRes(R.string.onb_free_plan_bullets), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    OutlinedButton(
                                        onClick = { onComplete(buildProfile(), "free") },
                                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("select_free_plan_button")
                                    ) {
                                        Icon(Icons.Rounded.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strRes(R.string.onb_start_free), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(140.dp))
                        }
                    }
                }
            }
        }

        if (step < 13) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { step++ },
                enabled = step != 11 || isCommitted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_next_button")
            ) {
                Text(
                    text = strRes(if (step == 12) R.string.onb_see_plans else R.string.onb_continue),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun GenderChipCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
        modifier = modifier.clip(MaterialTheme.shapes.large).clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(44.dp).clip(CircleShape).background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
            ) {
                Icon(icon, contentDescription = title, tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
            }
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}
