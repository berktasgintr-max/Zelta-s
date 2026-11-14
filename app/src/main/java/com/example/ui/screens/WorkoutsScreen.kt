package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram
import com.example.ui.components.ExerciseFormVisualizer
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.MintVital
import com.example.ui.theme.WarmGold
import kotlinx.coroutines.delay

enum class WorkoutFilter {
    ALL,
    GYM,
    HOME,
    YOUTH,
    PRO;

    fun label(strings: AppStrings): String = when (this) {
        ALL -> strings.filterAll
        GYM -> strings.filterGym
        HOME -> strings.filterHome
        YOUTH -> strings.filterYouth
        PRO -> strings.filterPro
    }
}

fun workoutPreviewDrawable(workout: WorkoutProgram): Int {
    val focus = workout.focusArea.lowercase()
    val title = workout.title.lowercase()
    return when (workout.category) {
        WorkoutCategory.HOME -> when {
            focus == "core" || title.contains("karın") || title.contains("core") ||
                title.contains("esneme") || title.contains("mobilite") ->
                R.drawable.img_preview_home_core_stretch
            focus == "lower" || title.contains("bacak") || title.contains("kalça") ||
                title.contains("hiit") || title.contains("tabata") ||
                title.contains("kardiyo") || title.contains("yağ yakım") ->
                R.drawable.img_preview_home_legs_cardio
            focus == "upper" || title.contains("şınav") || title.contains("göğüs") ||
                title.contains("kalistenik") || title.contains("kol") ||
                title.contains("omuz") || title.contains("duruş") ->
                R.drawable.img_preview_home_pushups
            else -> R.drawable.img_home_workout_banner
        }
        WorkoutCategory.YOUTH -> when {
            focus == "core" || focus == "upper" || title.contains("omurga") ||
                title.contains("duruş") || title.contains("core") ->
                R.drawable.img_preview_home_core_stretch
            else -> R.drawable.img_preview_youth_athletic
        }
        WorkoutCategory.GYM -> when {
            focus == "lower" || title.contains("bacak") || title.contains("squat") ||
                title.contains("quadriceps") || title.contains("deadlift") ->
                R.drawable.img_preview_lower_legs
            focus == "core" || title.contains("karın") || title.contains("core") ->
                R.drawable.img_preview_core_abs
            title.contains("sırt") || title.contains("pull") || title.contains("v-taper") ||
                title.contains("trapez") || title.contains("kol") || title.contains("biceps") ->
                R.drawable.img_preview_gym_back_arms
            focus == "upper" || title.contains("göğüs") || title.contains("push") ||
                title.contains("bench") || title.contains("omuz") ->
                R.drawable.img_preview_upper_chest
            else -> R.drawable.img_gym_workout_banner
        }
    }
}

@Composable
fun WorkoutsScreen(
    activeFilter: WorkoutFilter,
    onSelectFilter: (WorkoutFilter) -> Unit,
    gender: String,
    isProActive: Boolean,
    isWorkoutAccessible: (WorkoutProgram) -> Boolean,
    adsWatchedForWorkout: (String) -> Int,
    onWatchAdForWorkout: (WorkoutProgram) -> Unit,
    onUpgradePro: () -> Unit,
    onScheduleToday: (WorkoutProgram) -> Unit,
    onCompleteWorkout: (WorkoutProgram) -> Unit,
    selectedWorkout: WorkoutProgram?,
    onSelectWorkout: (WorkoutProgram?) -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current

    val filtered = remember(activeFilter) {
        val all = WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
        when (activeFilter) {
            WorkoutFilter.ALL -> all
            WorkoutFilter.GYM -> all.filter { it.category == WorkoutCategory.GYM }
            WorkoutFilter.HOME -> all.filter { it.category == WorkoutCategory.HOME }
            WorkoutFilter.YOUTH -> all.filter { it.isRecommendedForYouth }
            WorkoutFilter.PRO -> all.filter { it.isProSpecial }
        }
    }

    if (selectedWorkout != null) {
        WorkoutDetailView(
            workout = selectedWorkout,
            gender = gender,
            onBack = { onSelectWorkout(null) },
            onScheduleToday = { onScheduleToday(selectedWorkout) },
            onCompleteWorkout = { onCompleteWorkout(selectedWorkout) }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Category Hero Banner Image
        val bannerRes = when (activeFilter) {
            WorkoutFilter.HOME -> R.drawable.img_home_workout_banner
            WorkoutFilter.YOUTH -> R.drawable.img_preview_youth_athletic
            WorkoutFilter.GYM -> R.drawable.img_gym_workout_banner
            else -> R.drawable.img_workouts_hero_arena
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(MaterialTheme.shapes.large)
        ) {
            Image(
                painter = painterResource(id = bannerRes),
                contentDescription = strings.workoutsBannerTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.78f)
                            )
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = strings.workoutsBannerTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.workoutsBannerSub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            items(WorkoutFilter.entries) { filter ->
                FilterChip(
                    selected = activeFilter == filter,
                    onClick = { onSelectFilter(filter) },
                    label = { Text(filter.label(strings), fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("workout_filter_${filter.name.lowercase()}")
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered, key = { it.id }) { workout ->
                val accessible = isWorkoutAccessible(workout)
                val adsWatched = adsWatchedForWorkout(workout.id)
                val previewRes = workoutPreviewDrawable(workout)

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            when {
                                !accessible -> ext.goldText.copy(alpha = 0.55f)
                                workout.isRecommendedForYouth -> ext.purpleText.copy(alpha = 0.6f)
                                else -> MaterialTheme.colorScheme.outline
                            },
                            MaterialTheme.shapes.large
                        )
                        .clickable {
                            if (accessible) onSelectWorkout(workout) else onWatchAdForWorkout(workout)
                        }
                        .testTag("workout_card_${workout.id}")
                ) {
                    Column {
                        // Pre-workout Preview Image Banner on every card before opening
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(126.dp)
                        ) {
                            Image(
                                painter = painterResource(id = previewRes),
                                contentDescription = workout.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.2f),
                                                Color.Black.copy(alpha = 0.65f)
                                            )
                                        )
                                    )
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .padding(10.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = if (workout.category == WorkoutCategory.GYM) FlameOrange else ElectricTeal,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = workout.category.label(strings.langCode),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    if (workout.isProSpecial) {
                                        Surface(color = WarmGold, shape = RoundedCornerShape(8.dp)) {
                                            Text(
                                                text = if (isProActive) strings.proOpenBadge else strings.proBadge,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color(0xFF1A1000),
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                                if (!accessible) {
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.65f),
                                        shape = CircleShape
                                    ) {
                                        Icon(
                                            Icons.Rounded.Lock,
                                            contentDescription = if (isEn) "Locked" else "Kilitli",
                                            tint = WarmGold,
                                            modifier = Modifier.padding(6.dp).size(18.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${workout.durationMinutes} ${if (isEn) "min" else "dk"} • ~${workout.estimatedCalories} kcal • ${workout.exercises.size} ${if (isEn) "Exercises" else "Egzersiz"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            )
                        }

                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (workout.isRecommendedForYouth) {
                                Surface(color = ext.purpleContainer, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = strings.youthRecommendedBadge,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ext.purpleText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(text = workout.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(
                                text = workout.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = workout.difficulty,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            if (!accessible) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = { onWatchAdForWorkout(workout) },
                                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("watch_ad_unlock_${workout.id}")
                                    ) {
                                        Icon(Icons.Rounded.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${strings.watchAdsPrefix} ($adsWatched/3)")
                                    }
                                    OutlinedButton(onClick = onUpgradePro) {
                                        Text(strings.unlockWithPro)
                                    }
                                }
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = { onSelectWorkout(workout) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Rounded.FitnessCenter, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.viewAndStart)
                                    }
                                    OutlinedButton(onClick = { onScheduleToday(workout) }) {
                                        Text(strings.addToCalendar)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutDetailView(
    workout: WorkoutProgram,
    gender: String,
    onBack: () -> Unit,
    onScheduleToday: () -> Unit,
    onCompleteWorkout: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current
    var restSecondsLeft by remember { mutableIntStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning, restSecondsLeft) {
        if (timerRunning && restSecondsLeft > 0) {
            delay(1000L)
            restSecondsLeft--
            if (restSecondsLeft == 0) timerRunning = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("workout_detail_back")) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = if (isEn) "Back" else "Geri")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = workout.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = "${workout.durationMinutes} ${if (isEn) "min" else "dk"} • ${workout.exercises.size} ${if (isEn) "Exercises" else "Egzersiz"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(onClick = onScheduleToday) {
                Text(strings.scheduleToday)
            }
        }

        if (restSecondsLeft > 0) {
            Surface(
                color = ext.tealContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Timer, contentDescription = null, tint = ext.tealText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEn) "Rest Timer: ${restSecondsLeft}s" else "Dinlenme Sayacı: $restSecondsLeft sn",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ext.tealText
                        )
                    }
                    OutlinedButton(onClick = { timerRunning = false; restSecondsLeft = 0 }) {
                        Text(if (isEn) "Reset" else "Sıfırla")
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (workout.isRecommendedForYouth) {
                Surface(
                    color = ext.purpleContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEn) {
                            "${strings.youthRecommendedBadge} • Safe, posture-building sequence for growing athletes."
                        } else {
                            "${strings.youthRecommendedBadge} • Büyüme çağındaki gençler için güvenli ve postür destekleyici egzersiz dizisi."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ext.purpleText,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            workout.exercises.forEachIndexed { index, ex ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${index + 1}. ${ex.name}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = ex.setsReps,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ext.orangeText,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isEn) "Target Muscle: ${ex.targetMuscle}" else "Hedef Kas: ${ex.targetMuscle}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = ext.goldText
                        )

                        // Sculpted Male / Female Animated Exercise Demonstration
                        ExerciseFormVisualizer(exercise = ex, gender = gender)

                        Text(
                            text = if (isEn) "How to Perform (Step by Step)" else "Nasıl Yapılır? (Adım Adım)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        ex.steps.forEachIndexed { stepIdx, stepText ->
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(FlameOrange.copy(alpha = 0.2f))
                                ) {
                                    Text("${stepIdx + 1}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ext.orangeText)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stepText, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            }
                        }

                        Surface(color = ext.mintContainer, shape = RoundedCornerShape(10.dp)) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = ext.mintText, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${if (isEn) "Form Tip" else "Form İpucu"}: ${ex.formCue}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Surface(color = ext.tealContainer, shape = RoundedCornerShape(10.dp)) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Air, contentDescription = null, tint = ext.tealText, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${if (isEn) "Breathing" else "Nefes"}: ${ex.breathingCue}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Surface(color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${if (isEn) "Avoid Mistake" else "Kaçınılacak Hata"}: ${ex.commonMistake}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        if (ex.restSeconds > 0) {
                            Button(
                                onClick = {
                                    restSecondsLeft = ex.restSeconds
                                    timerRunning = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isEn) "Set Done • Start ${ex.restSeconds}s Rest"
                                    else "Set Tamamlandı • ${ex.restSeconds} sn Dinlenme Başlat"
                                )
                            }
                        }
                    }
                }
            }
            Button(
                onClick = onCompleteWorkout,
                colors = ButtonDefaults.buttonColors(containerColor = MintVital, contentColor = Color(0xFF0A140F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("complete_workout_xp_button")
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEn) "Complete Workout (+60 XP & Streak!)" else "Antrenmanı Tamamla (+60 XP & Seri!)",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
