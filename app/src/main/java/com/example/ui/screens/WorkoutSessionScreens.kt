package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.localizedBreathingCue
import com.example.data.catalog.localizedCommonMistake
import com.example.data.catalog.localizedDifficulty
import com.example.data.catalog.localizedFormCue
import com.example.data.catalog.localizedName
import com.example.data.catalog.localizedSetsReps
import com.example.data.catalog.localizedSteps
import com.example.data.catalog.localizedSubtitle
import com.example.data.catalog.localizedTargetMuscle
import com.example.data.catalog.localizedTitle
import com.example.data.model.ExerciseItem
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram
import com.example.ui.components.AnimatedKocTavuk
import com.example.ui.components.CoachAnimationMood
import com.example.ui.components.ExerciseAnimationPlayer
import com.example.ui.components.ExerciseDetailBottomSheet
import com.example.ui.i18n.strRes
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val KG_TO_LBS = 2.20462

private data class SetPrescriptionDefaults(
    val totalSets: Int,
    val defaultReps: Int,
    val defaultWeightKg: Double
)

private data class LoggedSetRecord(
    val exerciseId: String,
    val setNumber: Int,
    val reps: Int,
    val weightKg: Double
)

private fun parsePrescriptionDefaults(
    exercise: ExerciseItem,
    category: WorkoutCategory
): SetPrescriptionDefaults {
    val nums = Regex("\\d+").findAll(exercise.setsReps).mapNotNull { it.value.toIntOrNull() }.toList()
    val sets = (nums.getOrNull(0) ?: 3).coerceIn(1, 6)
    val reps = (nums.getOrNull(1) ?: 12).coerceIn(1, 99)
    val lowerName = exercise.name.lowercase()
    val defaultWeight = when {
        category != WorkoutCategory.GYM -> 0.0
        lowerName.contains("deadlift") || lowerName.contains("squat") || lowerName.contains("leg press") -> 40.0
        lowerName.contains("bench") || lowerName.contains("row") || lowerName.contains("pulldown") -> 25.0
        lowerName.contains("plank") || lowerName.contains("dip") || lowerName.contains("crunch") ||
            lowerName.contains("leg raise") || lowerName.contains("pull-up") || lowerName.contains("barfiks") -> 0.0
        else -> 15.0
    }
    return SetPrescriptionDefaults(
        totalSets = sets,
        defaultReps = reps,
        defaultWeightKg = defaultWeight
    )
}

private fun formatWeightVolume(weightKg: Double, weightUnit: String): String {
    val isLbs = weightUnit.equals("lbs", ignoreCase = true)
    val converted = if (isLbs) weightKg * KG_TO_LBS else weightKg
    val suffix = if (isLbs) "lbs" else "kg"
    val rounded = (converted * 10.0).roundToInt() / 10.0
    return if (rounded % 1.0 == 0.0) {
        "${rounded.toInt()} $suffix"
    } else {
        "$rounded $suffix"
    }
}

@Composable
fun WorkoutPreviewScreen(
    workout: WorkoutProgram,
    onBack: () -> Unit,
    onScheduleToday: () -> Unit,
    onStartWorkout: () -> Unit
) {
    var selectedPreviewExercise by remember(workout.id) { mutableStateOf<ExerciseItem?>(null) }

    selectedPreviewExercise?.let { previewEx ->
        ExerciseDetailBottomSheet(
            exercise = previewEx,
            onDismiss = { selectedPreviewExercise = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("workout_preview_screen")
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("workout_detail_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = strRes(R.string.common_back),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = workout.localizedTitle(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = onScheduleToday,
                shape = CircleShape,
                modifier = Modifier.height(42.dp)
            ) {
                Icon(Icons.Rounded.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strRes(R.string.add_to_calendar), maxLines = 1)
            }
        }

        // Scrollable Exercise Preview List
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item(key = "workout_preview_header", contentType = "header") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = strRes(workout.category.labelRes),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = workout.localizedDifficulty(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = workout.localizedSubtitle(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            PreviewMetaStat(
                                icon = Icons.Rounded.AccessTime,
                                label = "${workout.durationMinutes} ${strRes(R.string.unit_min)}"
                            )
                            PreviewMetaStat(
                                icon = Icons.Rounded.LocalFireDepartment,
                                label = "~${workout.estimatedCalories} kcal"
                            )
                            PreviewMetaStat(
                                icon = Icons.Rounded.FitnessCenter,
                                label = "${workout.exercises.size} ${strRes(R.string.exercises_count_suffix)}"
                            )
                        }
                    }
                }
            }

            item(key = "workout_preview_exercises_title", contentType = "section_header") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                ) {
                    Text(
                        text = strRes(R.string.workout_preview_exercises),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = strRes(R.string.workout_preview_tap_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            itemsIndexed(
                items = workout.exercises,
                key = { idx, ex -> "${ex.id}_$idx" },
                contentType = { _, _ -> "exercise_row" }
            ) { index, ex ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { selectedPreviewExercise = ex }
                        .testTag("preview_exercise_row_${ex.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = ex.localizedName(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ex.localizedTargetMuscle(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = ex.localizedSetsReps(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                            if (ex.restSeconds > 0) {
                                Text(
                                    text = "${ex.restSeconds}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Rounded.PlayCircleOutline,
                            contentDescription = ex.localizedName(),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        // Sticky Bottom CTA Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = onStartWorkout,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_active_workout_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strRes(R.string.start_workout),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewMetaStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    workout: WorkoutProgram,
    weightUnit: String = "kg",
    onWeightUnitChange: (String) -> Unit = {},
    onExitToPreview: () -> Unit,
    onCompleteWorkout: () -> Unit
) {
    val exercises = workout.exercises
    var exerciseIndex by rememberSaveable(workout.id) { mutableIntStateOf(0) }
    var currentSet by rememberSaveable(workout.id) { mutableIntStateOf(1) }
    var sessionCompleted by rememberSaveable(workout.id) { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }
    var showAllSteps by remember { mutableStateOf(false) }

    var restSecondsLeft by rememberSaveable(workout.id) { mutableIntStateOf(0) }
    var restRunning by rememberSaveable(workout.id) { mutableStateOf(false) }

    val loggedSets = remember(workout.id) { mutableStateListOf<LoggedSetRecord>() }

    val safeIndex = exerciseIndex.coerceIn(0, (exercises.size - 1).coerceAtLeast(0))
    val currentExercise = exercises.getOrNull(safeIndex)

    val defaults = remember(currentExercise?.id, workout.category) {
        if (currentExercise != null) parsePrescriptionDefaults(currentExercise, workout.category)
        else SetPrescriptionDefaults(3, 12, 0.0)
    }

    var repsInput by remember(safeIndex) { mutableIntStateOf(defaults.defaultReps) }
    var weightInputKg by remember(safeIndex) { mutableDoubleStateOf(defaults.defaultWeightKg) }

    LaunchedEffect(restRunning, restSecondsLeft) {
        if (restRunning && restSecondsLeft > 0) {
            delay(1000L)
            restSecondsLeft--
            if (restSecondsLeft == 0) {
                restRunning = false
            }
        }
    }

    BackHandler {
        if (sessionCompleted) {
            onCompleteWorkout()
        } else if (loggedSets.isNotEmpty()) {
            showExitConfirm = true
        } else {
            onExitToPreview()
        }
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text(strRes(R.string.exit_workout_title)) },
            text = { Text(strRes(R.string.exit_workout_body)) },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirm = false
                        onExitToPreview()
                    }
                ) {
                    Text(strRes(R.string.exit_workout_leave))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text(strRes(R.string.exit_workout_stay))
                }
            }
        )
    }

    if (sessionCompleted || currentExercise == null) {
        ActiveWorkoutSummaryView(
            workout = workout,
            totalSets = loggedSets.size.coerceAtLeast(exercises.size),
            totalReps = loggedSets.sumOf { it.reps }.coerceAtLeast(exercises.size * 10),
            totalVolumeKg = loggedSets.sumOf { it.reps * it.weightKg },
            weightUnit = weightUnit,
            onClaimAndFinish = onCompleteWorkout
        )
        return
    }

    val isLastExercise = safeIndex >= exercises.lastIndex
    val isLastSetOfExercise = currentSet >= defaults.totalSets
    val isVeryFinalSet = isLastExercise && isLastSetOfExercise
    val overallProgress = ((safeIndex.toFloat() + (currentSet - 1).toFloat() / defaults.totalSets.toFloat()) /
        exercises.size.coerceAtLeast(1).toFloat()).coerceIn(0.05f, 1f)

    val localizedName = currentExercise.localizedName()
    val localizedMuscle = currentExercise.localizedTargetMuscle()
    val localizedSteps = currentExercise.localizedSteps()
    val formCue = currentExercise.localizedFormCue()
    val breathingCue = currentExercise.localizedBreathingCue()
    val coachBullets = remember(currentExercise.id, formCue, localizedSteps, breathingCue) {
        buildList {
            add(formCue)
            localizedSteps.firstOrNull()?.let { add(it) }
            add(breathingCue)
        }.distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .testTag("active_workout_screen")
    ) {
        // Minimal Full-Screen Top Progress Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        if (loggedSets.isNotEmpty()) showExitConfirm = true else onExitToPreview()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("active_workout_exit_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = strRes(R.string.common_close),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strRes(R.string.active_exercise_fmt, safeIndex + 1, exercises.size),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = localizedName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = {
                        if (safeIndex > 0) {
                            exerciseIndex = safeIndex - 1
                            currentSet = 1
                            showAllSteps = false
                            restRunning = false
                            restSecondsLeft = 0
                        }
                    },
                    enabled = safeIndex > 0,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = strRes(R.string.common_back),
                        tint = if (safeIndex > 0) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    )
                }

                IconButton(
                    onClick = {
                        if (!isLastExercise) {
                            exerciseIndex = safeIndex + 1
                            currentSet = 1
                            showAllSteps = false
                            restRunning = false
                            restSecondsLeft = 0
                        }
                    },
                    enabled = !isLastExercise,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = strRes(R.string.common_next_step),
                        tint = if (!isLastExercise) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { overallProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(horizontal = 6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }

        // Upper/Middle Hero Visual + Short Bulleted Coach Cues
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val heroHeight = (maxHeight * 0.56f).coerceIn(210.dp, 310.dp)

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                ExerciseAnimationPlayer(
                    exercise = currentExercise,
                    localizedName = localizedName,
                    localizedTargetMuscle = localizedMuscle,
                    isFullScreenHero = true,
                    playerHeight = heroHeight
                )

                // Concise Bulleted Coach Tips
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = strRes(R.string.coach_cues_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = currentExercise.localizedSetsReps(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { showAllSteps = !showAllSteps }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = strRes(if (showAllSteps) R.string.hide_all_steps else R.string.show_all_steps),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = if (showAllSteps) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        coachBullets.forEach { bullet ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 7.dp)
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = bullet,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        AnimatedVisibility(visible = showAllSteps) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                localizedSteps.forEachIndexed { idx, stepText ->
                                    Text(
                                        text = "${idx + 1}. $stepText",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "• ${strRes(R.string.avoid_mistake_prefix)}: ${currentExercise.localizedCommonMistake()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        // Inline Rest Timer Banner (between sets, non-blocking)
        AnimatedVisibility(visible = restRunning && restSecondsLeft > 0) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strRes(R.string.rest_timer_fmt, restSecondsLeft),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(
                        onClick = {
                            restRunning = false
                            restSecondsLeft = 0
                        }
                    ) {
                        Text(
                            text = strRes(R.string.rest_skip_btn),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom Interactive Reps & Weight Input Dock + Complete Set CTA
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Set Counter + Completed Set Chips
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strRes(R.string.active_set_fmt, currentSet, defaults.totalSets),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (s in 1..defaults.totalSets) {
                            val isDone = loggedSets.any { it.exerciseId == currentExercise.id && it.setNumber == s }
                            val isCurrent = s == currentSet
                            Surface(
                                color = when {
                                    isDone -> MaterialTheme.colorScheme.primary
                                    isCurrent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                    else -> MaterialTheme.colorScheme.background
                                },
                                shape = CircleShape,
                                border = if (isCurrent && !isDone) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "$s",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Weight Unit Toggle (KG / LBS)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isLbs = weightUnit.equals("lbs", ignoreCase = true)
                    Text(
                        text = strRes(if (isLbs) R.string.active_weight_lbs_label else R.string.active_weight_kg_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = !isLbs,
                            onClick = { onWeightUnitChange("kg") },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("weight_unit_kg_btn")
                        ) {
                            Text(
                                text = strRes(R.string.weight_unit_kg),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        SegmentedButton(
                            selected = isLbs,
                            onClick = { onWeightUnitChange("lbs") },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("weight_unit_lbs_btn")
                        ) {
                            Text(
                                text = strRes(R.string.weight_unit_lbs),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Side-by-Side Reps & Weight Steppers (+/- and direct numeric input)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StepperInputCard(
                        label = strRes(R.string.reps_label),
                        rawNumericText = "$repsInput",
                        unitSuffix = null,
                        keyboardType = KeyboardType.Number,
                        onRawTextChange = { text ->
                            val clean = text.filter { it.isDigit() }.take(3)
                            val parsed = clean.toIntOrNull()
                            if (parsed != null) {
                                repsInput = parsed.coerceIn(1, 150)
                            }
                        },
                        onDecrement = { if (repsInput > 1) repsInput-- },
                        onIncrement = { if (repsInput < 150) repsInput++ },
                        minusTestTag = "reps_minus_button",
                        plusTestTag = "reps_plus_button",
                        valueTestTag = "reps_value_text",
                        modifier = Modifier.weight(1f)
                    )

                    val isLbs = weightUnit.equals("lbs", ignoreCase = true)
                    val displayedWeight = remember(weightInputKg, isLbs) {
                        if (isLbs) (weightInputKg * KG_TO_LBS * 2.0).roundToInt() / 2.0 else weightInputKg
                    }
                    val weightCleanStr = remember(displayedWeight) {
                        val rounded = (displayedWeight * 10.0).roundToInt() / 10.0
                        if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
                    }
                    StepperInputCard(
                        label = strRes(if (isLbs) R.string.active_weight_lbs_label else R.string.active_weight_kg_label),
                        rawNumericText = weightCleanStr,
                        unitSuffix = if (isLbs) "lbs" else "kg",
                        keyboardType = KeyboardType.Decimal,
                        onRawTextChange = { text ->
                            val normalized = text.replace(",", ".")
                            val parsed = normalized.toDoubleOrNull()
                            if (parsed != null) {
                                val inKg = if (isLbs) parsed / KG_TO_LBS else parsed
                                weightInputKg = inKg.coerceIn(0.0, 400.0)
                            }
                        },
                        onDecrement = {
                            weightInputKg = if (isLbs) {
                                val nextLbs = (displayedWeight - 5.0).coerceAtLeast(0.0)
                                nextLbs / KG_TO_LBS
                            } else {
                                (weightInputKg - 2.5).coerceAtLeast(0.0)
                            }
                        },
                        onIncrement = {
                            weightInputKg = if (isLbs) {
                                val nextLbs = (displayedWeight + 5.0).coerceAtMost(880.0)
                                nextLbs / KG_TO_LBS
                            } else {
                                (weightInputKg + 2.5).coerceAtMost(400.0)
                            }
                        },
                        minusTestTag = "weight_minus_button",
                        plusTestTag = "weight_plus_button",
                        valueTestTag = "weight_value_text",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Complete Set / Finish Workout Primary Orange Button
                Button(
                    onClick = {
                        loggedSets.add(
                            LoggedSetRecord(
                                exerciseId = currentExercise.id,
                                setNumber = currentSet,
                                reps = repsInput,
                                weightKg = weightInputKg
                            )
                        )
                        if (isVeryFinalSet) {
                            restRunning = false
                            restSecondsLeft = 0
                            sessionCompleted = true
                        } else if (isLastSetOfExercise) {
                            exerciseIndex = (safeIndex + 1).coerceAtMost(exercises.lastIndex)
                            currentSet = 1
                            showAllSteps = false
                            if (currentExercise.restSeconds > 0) {
                                restSecondsLeft = currentExercise.restSeconds
                                restRunning = true
                            }
                        } else {
                            currentSet++
                            if (currentExercise.restSeconds > 0) {
                                restSecondsLeft = currentExercise.restSeconds
                                restRunning = true
                            }
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("complete_set_button")
                ) {
                    Icon(
                        imageVector = if (isVeryFinalSet) Icons.Rounded.FitnessCenter else Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strRes(if (isVeryFinalSet) R.string.complete_final_set_btn else R.string.complete_set_btn),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StepperInputCard(
    label: String,
    rawNumericText: String,
    unitSuffix: String?,
    keyboardType: KeyboardType,
    onRawTextChange: (String) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    minusTestTag: String,
    plusTestTag: String,
    valueTestTag: String,
    modifier: Modifier = Modifier
) {
    var editBuffer by remember { mutableStateOf(rawNumericText) }
    LaunchedEffect(rawNumericText) {
        val bufNum = editBuffer.replace(",", ".").toDoubleOrNull()
        val rawNum = rawNumericText.replace(",", ".").toDoubleOrNull()
        if (bufNum != rawNum) {
            editBuffer = rawNumericText
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            onDecrement()
                        }
                        .testTag(minusTestTag)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Remove,
                            contentDescription = "-",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    BasicTextField(
                        value = editBuffer,
                        onValueChange = { next ->
                            val filtered = if (keyboardType == KeyboardType.Number) {
                                next.filter { it.isDigit() }.take(3)
                            } else {
                                next.filter { it.isDigit() || it == '.' || it == ',' }.take(5)
                            }
                            editBuffer = filtered
                            onRawTextChange(filtered)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(valueTestTag)
                    )
                    if (unitSuffix != null) {
                        Text(
                            text = unitSuffix,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            onIncrement()
                        }
                        .testTag(plusTestTag)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "+",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveWorkoutSummaryView(
    workout: WorkoutProgram,
    totalSets: Int,
    totalReps: Int,
    totalVolumeKg: Double,
    weightUnit: String,
    onClaimAndFinish: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("workout_summary_screen")
    ) {
        AnimatedKocTavuk(
            size = 164.dp,
            mood = CoachAnimationMood.CHEERING,
            loopDemo = true,
            contentDescription = strRes(R.string.app_name)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = strRes(R.string.workout_summary_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = workout.localizedTitle(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = strRes(R.string.workout_summary_sub),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SummaryStatTile(
                label = strRes(R.string.summary_sets_label),
                value = "$totalSets",
                modifier = Modifier.weight(1f)
            )
            SummaryStatTile(
                label = strRes(R.string.summary_reps_label),
                value = "$totalReps",
                modifier = Modifier.weight(1f)
            )
            SummaryStatTile(
                label = strRes(R.string.summary_volume_label),
                value = formatWeightVolume(totalVolumeKg, weightUnit),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onClaimAndFinish,
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("complete_workout_xp_button")
        ) {
            Icon(Icons.Rounded.FitnessCenter, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strRes(R.string.complete_workout_btn),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun SummaryStatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
