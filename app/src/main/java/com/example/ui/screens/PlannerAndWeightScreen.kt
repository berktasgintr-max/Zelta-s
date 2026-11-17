package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import com.example.ui.components.CleanTextField
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.catalog.localizedTitle
import com.example.data.model.CalendarWorkoutEntry
import com.example.data.model.UserProfile
import com.example.data.model.WeightEntry
import com.example.data.model.WorkoutProgram
import com.example.ui.i18n.LocalAppLanguageCode
import com.example.ui.i18n.LocalAppLocale
import com.example.ui.i18n.strRes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
    fun PlannerAndWeightScreen(
    selectedSubTab: Int,
    onSelectSubTab: (Int) -> Unit,
    profile: UserProfile,
    weightLogs: List<WeightEntry>,
    calendarEntries: List<CalendarWorkoutEntry>,
    customWorkouts: List<WorkoutProgram> = emptyList(),
    isWorkoutAccessible: (WorkoutProgram) -> Boolean,
    onAddWeightLog: (Double, String) -> Unit,
    onDeleteWeightLog: (String) -> Unit,
    onScheduleWorkout: (String, WorkoutProgram) -> Unit,
    onAutoBuildProSchedule: () -> Unit,
    onToggleCalendarCompleted: (CalendarWorkoutEntry) -> Unit,
    onDeleteCalendarEntry: (String) -> Unit,
    onUpdateTrainingDays: (Set<String>) -> Unit = {},
    onUpgradePro: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            SegmentedButton(
                selected = selectedSubTab == 0,
                onClick = { onSelectSubTab(0) },
                shape = SegmentedButtonDefaults.itemShape(0, 2),
                icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_calendar")
            ) {
                Text(strRes(R.string.tab_calendar), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            SegmentedButton(
                selected = selectedSubTab != 0,
                onClick = { onSelectSubTab(1) },
                shape = SegmentedButtonDefaults.itemShape(1, 2),
                icon = { Icon(Icons.Rounded.MonitorWeight, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_weight")
            ) {
                Text(strRes(R.string.tab_weight), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        when (selectedSubTab) {
            0 -> CalendarPlannerTab(
                profile = profile,
                calendarEntries = calendarEntries,
                customWorkouts = customWorkouts,
                isWorkoutAccessible = isWorkoutAccessible,
                onScheduleWorkout = onScheduleWorkout,
                onAutoBuildProSchedule = onAutoBuildProSchedule,
                onToggleCalendarCompleted = onToggleCalendarCompleted,
                onDeleteCalendarEntry = onDeleteCalendarEntry,
                onUpdateTrainingDays = onUpdateTrainingDays,
                onUpgradePro = onUpgradePro
            )
            else -> WeightTrackerTab(
                profile = profile,
                weightLogs = weightLogs,
                onAddWeightLog = onAddWeightLog,
                onDeleteWeightLog = onDeleteWeightLog
            )
        }
    }
}

@Composable
private fun CalendarPlannerTab(
    profile: UserProfile,
    calendarEntries: List<CalendarWorkoutEntry>,
    customWorkouts: List<WorkoutProgram>,
    isWorkoutAccessible: (WorkoutProgram) -> Boolean,
    onScheduleWorkout: (String, WorkoutProgram) -> Unit,
    onAutoBuildProSchedule: () -> Unit,
    onToggleCalendarCompleted: (CalendarWorkoutEntry) -> Unit,
    onDeleteCalendarEntry: (String) -> Unit,
    onUpdateTrainingDays: (Set<String>) -> Unit,
    onUpgradePro: () -> Unit = {}
) {
    val langCode = LocalAppLanguageCode.current
    val locale = LocalAppLocale.current

    val upcomingDays = remember(langCode) {
        val list = mutableListOf<Pair<String, String>>()
        val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val labelFmt = SimpleDateFormat("dd MMM EEE", locale)
        val cal = Calendar.getInstance()
        repeat(14) {
            list.add(isoFmt.format(cal.time) to labelFmt.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    var selectedDateKey by remember { mutableStateOf(upcomingDays.first().first) }
    var showPickerDialog by remember { mutableStateOf(false) }

    val dayEntries = remember(calendarEntries, selectedDateKey) {
        calendarEntries.filter { it.dateKey == selectedDateKey }
    }
    val selectedDayTokens = remember(profile.trainingDaysCsv) {
        profile.trainingDaysCsv
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .toSet()
            .ifEmpty { setOf("mon", "wed", "fri", "sat") }
    }
    val weekDayLabels = remember(locale) {
        val tokens = listOf("mon" to Calendar.MONDAY, "tue" to Calendar.TUESDAY, "wed" to Calendar.WEDNESDAY, "thu" to Calendar.THURSDAY, "fri" to Calendar.FRIDAY, "sat" to Calendar.SATURDAY, "sun" to Calendar.SUNDAY)
        val shortFmt = SimpleDateFormat("EE", locale)
        tokens.map { (token, calConst) ->
            val cal = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, calConst) }
            token to shortFmt.format(cal.time).take(3).uppercase(locale)
        }
    }
    val completedTotal = remember(calendarEntries) { calendarEntries.count { it.completed } }

    val flatElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val surfaceCardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "planner_weekly_blueprint", contentType = "blueprint_card") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                elevation = flatElevation,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("planner_weekly_blueprint_card")
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strRes(R.string.planner_weekly_plan_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = strRes(R.string.planner_scheduled_days_fmt, selectedDayTokens.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = strRes(
                                    R.string.planner_done_count_fmt,
                                    completedTotal,
                                    calendarEntries.size.coerceAtLeast(selectedDayTokens.size)
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        weekDayLabels.forEach { (token, shortLabel) ->
                            val isActiveDay = selectedDayTokens.contains(token)
                            Surface(
                                color = if (isActiveDay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(10.dp),
                                border = if (!isActiveDay) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val newDays = if (isActiveDay && selectedDayTokens.size > 1) {
                                            selectedDayTokens - token
                                        } else {
                                            selectedDayTokens + token
                                        }
                                        onUpdateTrainingDays(newDays)
                                    }
                            ) {
                                Text(
                                    text = shortLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActiveDay) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "planner_add_header", contentType = "action_card") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = strRes(R.string.calendar_empty_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = strRes(R.string.calendar_empty_sub),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showPickerDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_calendar_workout_button")
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strRes(R.string.add_workout),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (profile.isProActive) {
                            OutlinedButton(
                                onClick = onAutoBuildProSchedule,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pro_auto_plan_button")
                            ) {
                                Icon(Icons.Rounded.AutoFixHigh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strRes(R.string.pro_auto_plan),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = onUpgradePro,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("locked_pro_auto_plan_button")
                            ) {
                                Icon(Icons.Rounded.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${strRes(R.string.pro_auto_plan)} 🔒",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "planner_date_strip", contentType = "date_strip") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(upcomingDays, key = { it.first }, contentType = { "day_chip" }) { (iso, label) ->
                    val selected = selectedDateKey == iso
                    val hasWorkout = calendarEntries.any { it.dateKey == iso }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { selectedDateKey = iso }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        if (hasWorkout) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }

        if (dayEntries.isEmpty()) {
            item(key = "planner_empty_day", contentType = "empty_state") {
                val isScheduledDayToken = remember(selectedDateKey, selectedDayTokens) {
                    runCatching {
                        val cal = Calendar.getInstance()
                        cal.time = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(selectedDateKey)!!
                        val token = when (cal.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.MONDAY -> "mon"
                            Calendar.TUESDAY -> "tue"
                            Calendar.WEDNESDAY -> "wed"
                            Calendar.THURSDAY -> "thu"
                            Calendar.FRIDAY -> "fri"
                            Calendar.SATURDAY -> "sat"
                            else -> "sun"
                        }
                        selectedDayTokens.contains(token)
                    }.getOrDefault(false)
                }
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Surface(
                            color = if (isScheduledDayToken) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.background,
                            shape = CircleShape
                        ) {
                            Text(
                                text = strRes(if (isScheduledDayToken) R.string.planner_training_day_tag else R.string.planner_rest_day_tag),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isScheduledDayToken) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = strRes(R.string.day_empty_schedule, selectedDateKey),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(
                items = dayEntries,
                key = { it.id },
                contentType = { "calendar_entry" }
            ) { entry ->
                Card(
                    colors = surfaceCardColors,
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        1.dp,
                        if (entry.completed) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.outline
                    ),
                    elevation = flatElevation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        IconButton(onClick = { onToggleCalendarCompleted(entry) }) {
                            Icon(
                                imageVector = if (entry.completed) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = strRes(R.string.toggle_complete),
                                tint = if (entry.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            val displayTitle = (customWorkouts + WorkoutCatalogHomeAndYouth.ALL_WORKOUTS)
                                .find { it.id == entry.workoutId }
                                ?.localizedTitle()
                                ?: entry.workoutTitle
                            Text(
                                text = displayTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (entry.completed) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = strRes(if (entry.completed) R.string.status_completed else R.string.planner_training_day_tag),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (entry.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "${entry.durationMinutes} ${strRes(R.string.unit_min)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = { onDeleteCalendarEntry(entry.id) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = strRes(R.string.common_delete), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showPickerDialog) {
        val available = remember(profile.isProActive, customWorkouts) {
            (customWorkouts + WorkoutCatalogHomeAndYouth.ALL_WORKOUTS).filter { isWorkoutAccessible(it) }
        }
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            title = {
                Text(strRes(R.string.choose_workout_for_date, selectedDateKey))
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(340.dp)
                ) {
                    items(available, key = { it.id }) { workout ->
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onScheduleWorkout(selectedDateKey, workout)
                                    showPickerDialog = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(workout.localizedTitle(), style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${strRes(workout.category.labelRes)} • ${workout.durationMinutes} ${strRes(R.string.unit_min)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPickerDialog = false }) {
                    Text(strRes(R.string.common_close))
                }
            }
        )
    }
}

@Composable
private fun WeightTrackerTab(
    profile: UserProfile,
    weightLogs: List<WeightEntry>,
    onAddWeightLog: (Double, String) -> Unit,
    onDeleteWeightLog: (String) -> Unit
) {
    val langCode = LocalAppLanguageCode.current
    val locale = LocalAppLocale.current
    var showAddDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf(profile.currentWeightKg.toString()) }
    var noteInput by remember { mutableStateOf("") }

    val delta = profile.currentWeightKg - profile.startWeightKg
    val deltaText = if (delta >= 0) "+${String.format(Locale.US, "%.1f", delta)} kg" else "${String.format(Locale.US, "%.1f", delta)} kg"

    val flatElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val surfaceCardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val subtleOutline = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "weight_stat_pills", contentType = "stat_pills") {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatPillCard(strRes(R.string.current_label), "${profile.currentWeightKg} kg", Modifier.weight(1f))
                StatPillCard(strRes(R.string.target_label), "${profile.targetWeightKg} kg", Modifier.weight(1f))
                StatPillCard(strRes(R.string.stat_change), deltaText, Modifier.weight(1f))
            }
        }

        item(key = "weight_chart_card", contentType = "weight_chart") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = subtleOutline,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(strRes(R.string.weight_chart_title), style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = "BMI: ${String.format(Locale.US, "%.1f", profile.bmi)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = {
                                weightInput = profile.currentWeightKg.toString()
                                noteInput = ""
                                showAddDialog = true
                            },
                            modifier = Modifier.testTag("log_weight_button")
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strRes(R.string.log_weight))
                        }
                    }

                    WeightChartCanvas(
                        entries = weightLogs,
                        targetWeight = profile.targetWeightKg,
                        emptyHint = strRes(R.string.weight_chart_empty)
                    )
                }
            }
        }

        items(
            items = weightLogs.reversed(),
            key = { it.id },
            contentType = { "weight_log" }
        ) { log ->
            val dateStr = remember(log.loggedAtMillis, langCode) {
                SimpleDateFormat("dd MMM yyyy, HH:mm", locale).format(Date(log.loggedAtMillis))
            }
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.medium,
                border = subtleOutline,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${log.weightKg} kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(dateStr, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (log.note.isNotBlank()) {
                            Text(log.note, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    IconButton(onClick = { onDeleteWeightLog(log.id) }) {
                        Icon(Icons.Rounded.Delete, contentDescription = strRes(R.string.common_delete), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(strRes(R.string.new_weight_entry)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CleanTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = strRes(R.string.weight_kg_label),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    CleanTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it.take(100) },
                        label = strRes(R.string.note_optional_label),
                        placeholder = strRes(R.string.note_placeholder),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val parsed = weightInput.replace(",", ".").toDoubleOrNull()
                    if (parsed != null && parsed in 25.0..300.0) {
                        onAddWeightLog(parsed, noteInput)
                        showAddDialog = false
                    }
                }) {
                    Text(strRes(R.string.common_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(strRes(R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
private fun StatPillCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun WeightChartCanvas(
    entries: List<WeightEntry>,
    targetWeight: Double,
    emptyHint: String
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val targetColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp)
    ) {
        if (entries.isEmpty()) {
            Text(
                text = emptyHint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val values = entries.map { it.weightKg } + targetWeight
                        val minV = (values.minOrNull() ?: 50.0) - 2.0
                        val maxV = (values.maxOrNull() ?: 90.0) + 2.0
                        val span = (maxV - minV).coerceAtLeast(1.0)
                        fun yFor(v: Double): Float {
                            val norm = ((v - minV) / span).toFloat()
                            return size.height - norm * size.height
                        }
                        val targetY = yFor(targetWeight)
                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                        val path = Path()
                        val points = mutableListOf<Offset>()
                        val stepX = if (entries.size <= 1) size.width / 2f else size.width / (entries.size - 1).toFloat()
                        entries.forEachIndexed { idx, item ->
                            val x = if (entries.size <= 1) size.width / 2f else idx * stepX
                            val y = yFor(item.weightKg)
                            points.add(Offset(x, y))
                            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        val lineStroke = Stroke(width = 6f, cap = StrokeCap.Round)
                        val targetStrokeColor = targetColor.copy(alpha = 0.7f)
                        val midGridColor = gridColor.copy(alpha = 0.4f)
                        onDrawBehind {
                            drawLine(
                                color = targetStrokeColor,
                                start = Offset(0f, targetY),
                                end = Offset(size.width, targetY),
                                strokeWidth = 3f,
                                pathEffect = dashEffect
                            )
                            drawLine(
                                color = midGridColor,
                                start = Offset(0f, size.height * 0.5f),
                                end = Offset(size.width, size.height * 0.5f),
                                strokeWidth = 2f
                            )
                            drawPath(path, color = lineColor, style = lineStroke)
                            points.forEach { pt ->
                                drawCircle(color = lineColor, radius = 9f, center = pt)
                            }
                        }
                    }
            )
        }
    }
}
