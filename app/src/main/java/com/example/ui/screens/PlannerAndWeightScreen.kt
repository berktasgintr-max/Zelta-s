package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.local.MealCalorieEntity
import com.example.data.model.CalendarWorkoutEntry
import com.example.data.model.UserProfile
import com.example.data.model.WeightEntry
import com.example.data.model.WorkoutProgram
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.CalorieScanUiState
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
    calorieLogs: List<MealCalorieEntity>,
    calorieScanState: CalorieScanUiState,
    isWorkoutAccessible: (WorkoutProgram) -> Boolean,
    onAddWeightLog: (Double, String) -> Unit,
    onDeleteWeightLog: (String) -> Unit,
    onScheduleWorkout: (String, WorkoutProgram) -> Unit,
    onAutoBuildProSchedule: () -> Unit,
    onToggleCalendarCompleted: (CalendarWorkoutEntry) -> Unit,
    onDeleteCalendarEntry: (String) -> Unit,
    onAnalyzeMealPhoto: (Bitmap) -> Unit,
    onDeleteMealCalorieLog: (String) -> Unit,
    onClearCalorieScanState: () -> Unit,
    onUpgradePro: () -> Unit,
    onOpenInviteTab: () -> Unit
) {
    val strings = LocalAppStrings.current

    Column(modifier = Modifier.fillMaxSize()) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            SegmentedButton(
                selected = selectedSubTab == 0,
                onClick = { onSelectSubTab(0) },
                shape = SegmentedButtonDefaults.itemShape(0, 3),
                icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_calendar")
            ) {
                Text(strings.tabCalendar, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            SegmentedButton(
                selected = selectedSubTab == 1,
                onClick = { onSelectSubTab(1) },
                shape = SegmentedButtonDefaults.itemShape(1, 3),
                icon = { Icon(Icons.Rounded.MonitorWeight, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_weight")
            ) {
                Text(strings.tabWeight, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            SegmentedButton(
                selected = selectedSubTab == 2,
                onClick = { onSelectSubTab(2) },
                shape = SegmentedButtonDefaults.itemShape(2, 3),
                icon = { Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_calories")
            ) {
                Text(strings.tabCalories, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        when (selectedSubTab) {
            0 -> CalendarPlannerTab(
                profile = profile,
                calendarEntries = calendarEntries,
                isWorkoutAccessible = isWorkoutAccessible,
                onScheduleWorkout = onScheduleWorkout,
                onAutoBuildProSchedule = onAutoBuildProSchedule,
                onToggleCalendarCompleted = onToggleCalendarCompleted,
                onDeleteCalendarEntry = onDeleteCalendarEntry
            )
            1 -> WeightTrackerTab(
                profile = profile,
                weightLogs = weightLogs,
                onAddWeightLog = onAddWeightLog,
                onDeleteWeightLog = onDeleteWeightLog
            )
            else -> CalorieScannerTab(
                profile = profile,
                calorieLogs = calorieLogs,
                scanState = calorieScanState,
                onAnalyzePhoto = onAnalyzeMealPhoto,
                onDeleteLog = onDeleteMealCalorieLog,
                onClearScanState = onClearCalorieScanState,
                onUpgradePro = onUpgradePro,
                onOpenInviteTab = onOpenInviteTab
            )
        }
    }
}

@Composable
private fun CalendarPlannerTab(
    profile: UserProfile,
    calendarEntries: List<CalendarWorkoutEntry>,
    isWorkoutAccessible: (WorkoutProgram) -> Boolean,
    onScheduleWorkout: (String, WorkoutProgram) -> Unit,
    onAutoBuildProSchedule: () -> Unit,
    onToggleCalendarCompleted: (CalendarWorkoutEntry) -> Unit,
    onDeleteCalendarEntry: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"

    val upcomingDays = remember(isEn) {
        val list = mutableListOf<Pair<String, String>>()
        val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val locale = if (isEn) Locale.ENGLISH else Locale.forLanguageTag("tr")
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

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEn) "Build Your Workout Calendar" else "Antrenman Takvimi Oluştur",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = if (isEn) "Select a day, schedule workouts, and check them off as you finish."
                        else "Gün seçip istediğin antrenmanı planla veya tamamladıkça işaretle.",
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
                            Text(if (isEn) "Add Workout" else "Antrenman Ekle")
                        }
                        if (profile.isProActive) {
                            OutlinedButton(
                                onClick = onAutoBuildProSchedule,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Rounded.AutoFixHigh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isEn) "PRO Auto-Plan" else "PRO Otomatik")
                            }
                        }
                    }
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(upcomingDays) { (iso, label) ->
                    val selected = selectedDateKey == iso
                    val hasWorkout = calendarEntries.any { it.dateKey == iso }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(if (selected) FlameOrange else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                1.dp,
                                if (hasWorkout) WarmGold else MaterialTheme.colorScheme.outline,
                                MaterialTheme.shapes.medium
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
                                    .background(if (selected) MaterialTheme.colorScheme.onPrimary else WarmGold)
                            )
                        }
                    }
                }
            }
        }

        if (dayEntries.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEn) {
                            "No workouts scheduled for $selectedDateKey. Tap 'Add Workout' to fill your day!"
                        } else {
                            "$selectedDateKey tarihinde planlanmış antrenman yok. 'Antrenman Ekle' ile gününü doldurabilirsin!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(dayEntries, key = { it.id }) { entry ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        IconButton(onClick = { onToggleCalendarCompleted(entry) }) {
                            val ext = LocalExtendedColors.current
                            Icon(
                                imageVector = if (entry.completed) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = if (isEn) "Toggle complete" else "Tamamlandı işaretle",
                                tint = if (entry.completed) ext.mintText else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            val ext = LocalExtendedColors.current
                            Text(
                                text = entry.workoutTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isEn) {
                                    "${entry.durationMinutes} min • ${if (entry.completed) "Completed" else "Scheduled"}"
                                } else {
                                    "${entry.durationMinutes} dk • ${if (entry.completed) "Tamamlandı" else "Planlandı"}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (entry.completed) ext.mintText else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteCalendarEntry(entry.id) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = if (isEn) "Delete" else "Sil", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showPickerDialog) {
        val available = remember(profile.isProActive) {
            WorkoutCatalogHomeAndYouth.ALL_WORKOUTS.filter { isWorkoutAccessible(it) }
        }
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            title = {
                Text(if (isEn) "Choose Workout for $selectedDateKey" else "$selectedDateKey için Antrenman Seç")
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(340.dp)
                ) {
                    items(available, key = { it.id }) { workout ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onScheduleWorkout(selectedDateKey, workout)
                                    showPickerDialog = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(workout.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${workout.category.label(strings.langCode)} • ${workout.durationMinutes} ${if (isEn) "min" else "dk"}",
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
                    Text(if (isEn) "Close" else "Kapat")
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
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    var showAddDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf(profile.currentWeightKg.toString()) }
    var noteInput by remember { mutableStateOf("") }

    val delta = profile.currentWeightKg - profile.startWeightKg
    val deltaText = if (delta >= 0) "+${String.format(Locale.US, "%.1f", delta)} kg" else "${String.format(Locale.US, "%.1f", delta)} kg"

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            val ext = LocalExtendedColors.current
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatPillCard(strings.currentLabel, "${profile.currentWeightKg} kg", ext.orangeText, Modifier.weight(1f))
                StatPillCard(strings.targetLabel, "${profile.targetWeightKg} kg", ext.tealText, Modifier.weight(1f))
                StatPillCard(if (isEn) "Change" else "Değişim", deltaText, ext.goldText, Modifier.weight(1f))
            }
        }

        item {
            val ext = LocalExtendedColors.current
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
                        Column {
                            Text(if (isEn) "Weight Progress Chart" else "Kilo Değişim Grafiği", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(
                                text = "BMI: ${String.format(Locale.US, "%.1f", profile.bmi)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ext.tealText
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
                            Text(if (isEn) "Log Weight" else "Kilo Gir")
                        }
                    }

                    WeightChartCanvas(
                        entries = weightLogs,
                        targetWeight = profile.targetWeightKg,
                        emptyHint = if (isEn) "Add your first weight entry to plot the chart." else "Grafik için ilk kilo kaydını ekle."
                    )
                }
            }
        }

        items(weightLogs.reversed(), key = { it.id }) { log ->
            val dateStr = remember(log.loggedAtMillis, isEn) {
                val loc = if (isEn) Locale.ENGLISH else Locale.forLanguageTag("tr")
                SimpleDateFormat("dd MMM yyyy, HH:mm", loc).format(Date(log.loggedAtMillis))
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${log.weightKg} kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FlameOrange)
                        Text(dateStr, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (log.note.isNotBlank()) {
                            Text(log.note, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    IconButton(onClick = { onDeleteWeightLog(log.id) }) {
                        Icon(Icons.Rounded.Delete, contentDescription = if (isEn) "Delete" else "Sil", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (isEn) "New Weight Entry" else "Yeni Kilo Kaydı") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text(if (isEn) "Weight (kg)" else "Kilo (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it.take(100) },
                        label = { Text(if (isEn) "Note (optional)" else "Not (isteğe bağlı)") },
                        placeholder = { Text(if (isEn) "E.g. Morning fasted" else "Örn: Sabah aç karnına") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
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
                    Text(if (isEn) "Save" else "Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(if (isEn) "Cancel" else "İptal")
                }
            }
        )
    }
}

@Composable
private fun StatPillCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = accent, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WeightChartCanvas(
    entries: List<WeightEntry>,
    targetWeight: Double,
    emptyHint: String
) {
    val lineColor = FlameOrange
    val targetColor = ElectricTeal
    val gridColor = MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
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
            Canvas(modifier = Modifier.fillMaxSize()) {
                val values = entries.map { it.weightKg } + targetWeight
                val minV = (values.minOrNull() ?: 50.0) - 2.0
                val maxV = (values.maxOrNull() ?: 90.0) + 2.0
                val span = (maxV - minV).coerceAtLeast(1.0)

                fun yFor(v: Double): Float {
                    val norm = ((v - minV) / span).toFloat()
                    return size.height - norm * size.height
                }

                val targetY = yFor(targetWeight)
                drawLine(
                    color = targetColor.copy(alpha = 0.7f),
                    start = Offset(0f, targetY),
                    end = Offset(size.width, targetY),
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                )

                drawLine(
                    color = gridColor.copy(alpha = 0.4f),
                    start = Offset(0f, size.height * 0.5f),
                    end = Offset(size.width, size.height * 0.5f),
                    strokeWidth = 2f
                )

                val path = Path()
                val stepX = if (entries.size <= 1) size.width / 2f else size.width / (entries.size - 1).toFloat()
                entries.forEachIndexed { idx, item ->
                    val x = if (entries.size <= 1) size.width / 2f else idx * stepX
                    val y = yFor(item.weightKg)
                    if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color = lineColor, style = Stroke(width = 6f, cap = StrokeCap.Round))

                entries.forEachIndexed { idx, item ->
                    val x = if (entries.size <= 1) size.width / 2f else idx * stepX
                    val y = yFor(item.weightKg)
                    drawCircle(color = lineColor, radius = 9f, center = Offset(x, y))
                }
            }
        }
    }
}
