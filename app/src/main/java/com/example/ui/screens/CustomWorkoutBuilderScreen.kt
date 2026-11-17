package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.AzExerciseDatabase
import com.example.data.catalog.AzExerciseEntry
import com.example.data.catalog.MuscleGroupZone
import com.example.data.catalog.localizedBreathingCue
import com.example.data.catalog.localizedFormCue
import com.example.data.catalog.localizedName
import com.example.data.catalog.localizedSecondaryMuscles
import com.example.data.catalog.localizedTargetMuscle
import com.example.data.model.ExerciseItem
import com.example.data.model.WorkoutCategory
import com.example.ui.components.CleanTextField
import com.example.ui.components.ExerciseAnimationPlayer
import com.example.ui.i18n.LocalAppLanguageCode
import com.example.ui.i18n.LocalAppLocale
import com.example.ui.i18n.LocalLocalizedContext
import com.example.ui.i18n.strRes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWorkoutBuilderScreen(
    onBack: () -> Unit,
    onSaveWorkout: (
        title: String,
        category: WorkoutCategory,
        difficulty: String,
        exercises: List<ExerciseItem>,
        scheduleDateKey: String?
    ) -> Unit
) {
    BackHandler(onBack = onBack)

    val localizedCtx = LocalLocalizedContext.current
    val langCode = LocalAppLanguageCode.current
    val locale = LocalAppLocale.current

    var workoutName by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(WorkoutCategory.HOME) }
    var selectedDifficulty by rememberSaveable { mutableStateOf("Intermediate") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf<MuscleGroupZone?>(null) }

    val selectedExercises = remember { mutableStateListOf<ExerciseItem>() }
    var inspectedEntry by remember { mutableStateOf<AzExerciseEntry?>(null) }
    var showSchedulePicker by remember { mutableStateOf(false) }

    val filteredAzEntries = remember(searchQuery, selectedZone, langCode) {
        AzExerciseDatabase.search(searchQuery, selectedZone)
            .sortedBy { it.item.localizedName(localizedCtx).uppercase(locale) }
    }

    val groupedByLetter = remember(filteredAzEntries, langCode) {
        filteredAzEntries.groupBy { entry ->
            entry.item.localizedName(localizedCtx)
                .trim()
                .firstOrNull()
                ?.uppercaseChar()
                ?: '#'
        }
    }

    val upcomingDays = remember(langCode) {
        val list = mutableListOf<Pair<String, String>>()
        val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val labelFmt = SimpleDateFormat("EEE, d MMM", locale)
        val cal = Calendar.getInstance()
        repeat(14) {
            list.add(isoFmt.format(cal.time) to labelFmt.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    inspectedEntry?.let { entry ->
        MusclePreviewSheet(
            entry = entry,
            onDismiss = { inspectedEntry = null },
            onAddToRoutine = { customizedExercise ->
                selectedExercises.add(customizedExercise)
                inspectedEntry = null
            }
        )
    }

    if (showSchedulePicker) {
        AlertDialog(
            onDismissRequest = { showSchedulePicker = false },
            title = { Text(strRes(R.string.builder_schedule_dialog_title)) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(320.dp)
                ) {
                    items(upcomingDays) { (iso, label) ->
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSaveWorkout(
                                        workoutName,
                                        selectedCategory,
                                        selectedDifficulty,
                                        selectedExercises.toList(),
                                        iso
                                    )
                                    showSchedulePicker = false
                                    onBack()
                                }
                                .testTag("builder_schedule_day_$iso")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(iso, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSchedulePicker = false }) {
                    Text(strRes(R.string.common_cancel))
                }
            }
        )
    }

    val selectedIds = remember(selectedExercises.size) {
        selectedExercises.map { it.id }.toSet()
    }
    val flatElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val surfaceCardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val subtleOutline = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column {
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
                                .testTag("builder_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = strRes(R.string.common_cancel),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strRes(R.string.builder_screen_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.32f))
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.32f))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showSchedulePicker = true },
                            enabled = selectedExercises.isNotEmpty(),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("save_and_schedule_custom_button")
                        ) {
                            Icon(Icons.Rounded.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strRes(R.string.builder_save_schedule_btn),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                onSaveWorkout(
                                    workoutName,
                                    selectedCategory,
                                    selectedDifficulty,
                                    selectedExercises.toList(),
                                    null
                                )
                                onBack()
                            },
                            enabled = selectedExercises.isNotEmpty(),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("save_custom_workout_button")
                        ) {
                            Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strRes(R.string.builder_save_btn),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Metadata Configuration Card
            item(key = "builder_metadata", contentType = "metadata") {
                Card(
                    colors = surfaceCardColors,
                    shape = MaterialTheme.shapes.large,
                    border = subtleOutline,
                    elevation = flatElevation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CleanTextField(
                            value = workoutName,
                            onValueChange = { workoutName = it.take(60) },
                            label = strRes(R.string.builder_name_label),
                            placeholder = strRes(R.string.builder_name_hint),
                            inputModifier = Modifier.testTag("custom_workout_name_input")
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(WorkoutCategory.entries) { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category },
                                    label = { Text(strRes(category.labelRes)) }
                                )
                            }
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val diffs = listOf(
                                "Beginner" to R.string.diff_beginner,
                                "Intermediate" to R.string.diff_intermediate,
                                "Advanced" to R.string.diff_advanced
                            )
                            items(diffs) { (diffKey, resId) ->
                                FilterChip(
                                    selected = selectedDifficulty == diffKey,
                                    onClick = { selectedDifficulty = diffKey },
                                    label = { Text(strRes(resId)) }
                                )
                            }
                        }
                    }
                }
            }

            // 2. Current Selected Routine Tray
            item(key = "builder_routine_tray", contentType = "routine_tray") {
                Card(
                    colors = surfaceCardColors,
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
                    elevation = flatElevation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = strRes(R.string.builder_routine_title, selectedExercises.size),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (selectedExercises.isNotEmpty()) {
                                val estMin = (selectedExercises.size * 7 + 5).coerceIn(15, 90)
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "~$estMin ${strRes(R.string.unit_min)}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        if (selectedExercises.isEmpty()) {
                            Text(
                                text = strRes(R.string.builder_routine_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                selectedExercises.forEachIndexed { idx, ex ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(start = 12.dp, top = 6.dp, end = 4.dp, bottom = 6.dp)
                                        ) {
                                            Text(
                                                text = "${idx + 1}.",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = ex.localizedName(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${ex.localizedTargetMuscle()} • ${ex.setsReps}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            IconButton(
                                                onClick = { selectedExercises.removeAt(idx) },
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Close,
                                                    contentDescription = strRes(R.string.common_delete),
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. A-to-Z Search & Muscle Filter Header
            item(key = "builder_az_search_header", contentType = "search_header") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CleanTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = strRes(R.string.az_search_hint),
                        inputModifier = Modifier.testTag("az_exercise_search_input")
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val zoneChips = listOf(
                            null to R.string.zone_all_az,
                            MuscleGroupZone.CHEST to R.string.zone_chest,
                            MuscleGroupZone.BACK to R.string.zone_back,
                            MuscleGroupZone.SHOULDERS to R.string.zone_shoulders,
                            MuscleGroupZone.ARMS to R.string.zone_arms,
                            MuscleGroupZone.CORE to R.string.zone_core,
                            MuscleGroupZone.LEGS_GLUTES to R.string.zone_legs,
                            MuscleGroupZone.FULL_BODY to R.string.zone_full
                        )
                        items(zoneChips) { (zone, resId) ->
                            FilterChip(
                                selected = selectedZone == zone,
                                onClick = { selectedZone = zone },
                                label = { Text(strRes(resId)) }
                            )
                        }
                    }
                }
            }

            // 4. Alphabetical A-Z Exercise Directory
            groupedByLetter.forEach { (letter, entries) ->
                item(key = "header_$letter", contentType = "az_header") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = letter.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.28f))
                        )
                    }
                }

                items(
                    items = entries,
                    key = { it.item.id },
                    contentType = { "az_row" }
                ) { entry ->
                    val alreadyAdded = selectedIds.contains(entry.item.id)
                    Card(
                        colors = surfaceCardColors,
                        shape = RoundedCornerShape(14.dp),
                        border = if (alreadyAdded) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                        } else subtleOutline,
                        elevation = flatElevation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { inspectedEntry = entry }
                            .testTag("az_exercise_row_${entry.item.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.FitnessCenter,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = entry.item.localizedName(),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = entry.item.localizedTargetMuscle(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                color = if (alreadyAdded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (alreadyAdded) Icons.Rounded.Check else Icons.Rounded.Add,
                                        contentDescription = strRes(R.string.preview_add_to_routine),
                                        tint = if (alreadyAdded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MusclePreviewSheet(
    entry: AzExerciseEntry,
    onDismiss: () -> Unit,
    onAddToRoutine: (ExerciseItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val ex = entry.item
    val localizedName = ex.localizedName()
    val primaryMuscle = ex.localizedTargetMuscle()
    val secondaryMuscles = ex.localizedSecondaryMuscles()
    val volumePresets = remember(ex.setsReps) {
        listOf(ex.setsReps, "3 x 10", "3 x 12", "4 x 8", "4 x 12", "3 x 45s").distinct()
    }
    var selectedVolume by remember(ex.id) { mutableStateOf(ex.setsReps) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("muscle_preview_dialog")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = localizedName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${strRes(R.string.preview_primary_muscle)}: $primaryMuscle",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Rounded.Close, contentDescription = strRes(R.string.common_close))
                }
            }

            // Highlighted Muscle Group Breakdown Card
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text(
                        text = strRes(R.string.preview_primary_muscle),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = primaryMuscle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (secondaryMuscles.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${strRes(R.string.preview_secondary_muscles)}: $secondaryMuscles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            ExerciseAnimationPlayer(
                exercise = ex,
                localizedName = localizedName,
                localizedTargetMuscle = primaryMuscle,
                isFullScreenHero = false,
                playerHeight = 192.dp
            )

            // Quick Coach Cues
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "${strRes(R.string.form_tip_prefix)}: ${ex.localizedFormCue()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${strRes(R.string.breathing_prefix)}: ${ex.localizedBreathingCue()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sets & Reps Picker
            Text(
                text = strRes(R.string.preview_sets_reps_label),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(volumePresets) { preset ->
                    FilterChip(
                        selected = selectedVolume == preset,
                        onClick = { selectedVolume = preset },
                        label = { Text(preset) }
                    )
                }
            }

            Button(
                onClick = { onAddToRoutine(ex.copy(setsReps = selectedVolume)) },
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("confirm_add_exercise_button")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strRes(R.string.preview_add_to_routine))
            }
        }
    }
}
