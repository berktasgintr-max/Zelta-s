package com.example.ui.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.localizedTitle
import com.example.data.model.ExerciseItem
import com.example.data.model.WorkoutProgram
import com.example.ui.components.AnimatedKocTavuk
import com.example.ui.components.CoachAnimationMood
import com.example.ui.components.DailyQuestsAndChallengeCard
import com.example.ui.components.DailyStreakAndLevelCard
import com.example.ui.components.DailyWorkoutChecklistCard
import com.example.ui.components.DailyXpProWheelCard
import com.example.ui.components.ExerciseDetailBottomSheet
import com.example.ui.components.RankAndXpHeroCard
import com.example.ui.i18n.LocalAppLanguageCode
import com.example.ui.i18n.LocalAppLocale
import com.example.ui.i18n.strRes
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.WheelSpinOutcome
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: MainUiState,
    personalizedWorkouts: List<WorkoutProgram>,
    isWorkoutAccessible: (WorkoutProgram) -> Boolean,
    adsWatchedForWorkout: (String) -> Int,
    onTapChicken: () -> Unit,
    onClaimDailyCheckIn: () -> Unit,
    onCompleteQuest: (String) -> Unit,
    onSpinWheel: (Boolean) -> WheelSpinOutcome?,
    onSpinAnimationFinished: (WheelSpinOutcome) -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenWorkout: (WorkoutProgram) -> Unit,
    onWatchAdForWorkout: (WorkoutProgram) -> Unit,
    onOpenAiCoach: () -> Unit,
    onUpgradePro: () -> Unit = {}
) {
    val langCode = LocalAppLanguageCode.current
    val locale = LocalAppLocale.current
    val profile = uiState.profile
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val subtleBorder = remember(primaryColor) { BorderStroke(1.dp, primaryColor.copy(alpha = 0.28f)) }
    val flatElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val surfaceCardColors = CardDefaults.cardColors(containerColor = surfaceColor)

    var selectedMood by rememberSaveable { mutableStateOf("balanced") }
    var checkedExerciseIds by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var inspectedExercise by remember { mutableStateOf<ExerciseItem?>(null) }
    var showQuestsSheet by rememberSaveable { mutableStateOf(false) }
    var showWheelSheet by rememberSaveable { mutableStateOf(false) }

    val todayLabel = remember(langCode) {
        SimpleDateFormat("EEEE • d MMM", locale).format(Date()).replaceFirstChar { it.uppercase() }
    }
    val coachMood = when (selectedMood) {
        "energized" -> CoachAnimationMood.CHEERING
        "gentle" -> CoachAnimationMood.IDLE
        else -> CoachAnimationMood.COACHING
    }

    inspectedExercise?.let { exercise ->
        ExerciseDetailBottomSheet(
            exercise = exercise,
            onDismiss = { inspectedExercise = null }
        )
    }

    if (showQuestsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showQuestsSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.testTag("home_quests_sheet")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strRes(R.string.todays_quests),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { showQuestsSheet = false },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = strRes(R.string.common_close))
                    }
                }

                DailyQuestsAndChallengeCard(
                    profile = profile,
                    onTapCoachQuest = onTapChicken,
                    onCompleteChallengeQuest = { onCompleteQuest("challenge") },
                    onCompleteHydrationQuest = { onCompleteQuest("hydration") }
                )

                DailyStreakAndLevelCard(
                    profile = profile,
                    onClaimCheckIn = onClaimDailyCheckIn,
                    selectedMood = selectedMood,
                    onSelectMood = { selectedMood = it }
                )
            }
        }
    }

    if (showWheelSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showWheelSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.testTag("home_wheel_sheet")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strRes(R.string.wheel_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { showWheelSheet = false },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = strRes(R.string.common_close))
                    }
                }

                DailyXpProWheelCard(
                    profile = profile,
                    latestOutcome = uiState.latestWheelOutcome,
                    onSpinWheel = onSpinWheel,
                    onSpinAnimationFinished = onSpinAnimationFinished
                )
            }
        }
    }

    val activeWorkout = remember(personalizedWorkouts, profile.isProActive) {
        personalizedWorkouts.firstOrNull { isWorkoutAccessible(it) } ?: personalizedWorkouts.firstOrNull()
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Greeting / Header
        item(key = "home_greeting_header", contentType = "header") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = todayLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${strRes(R.string.hello)}, ${profile.displayName}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${strRes(R.string.target_label)}: ${profile.targetWeightKg.toInt()} kg • ${strRes(R.string.current_label)}: ${profile.currentWeightKg} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onOpenThemePicker,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .testTag("open_theme_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = strRes(R.string.theme_studio_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Surface(
                        color = if (profile.isProActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = CircleShape,
                        border = subtleBorder,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onUpgradePro)
                            .testTag("home_header_plan_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = if (profile.isProActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strRes(if (profile.isProActive) R.string.plan_pro else R.string.plan_free),
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                softWrap = false,
                                color = if (profile.isProActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2. Quick Coach Card
        item(key = "home_coach_hero", contentType = "coach_hero") {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = surfaceCardColors,
                border = BorderStroke(1.2.dp, primaryColor.copy(alpha = 0.36f)),
                elevation = flatElevation,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAiCoach)
                    .testTag("tappable_chicken_coach_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithCache {
                            val brush = Brush.linearGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.14f),
                                    surfaceColor,
                                    tertiaryColor.copy(alpha = 0.08f)
                                )
                            )
                            onDrawBehind {
                                drawRect(brush = brush)
                            }
                        }
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AnimatedKocTavuk(
                                size = 76.dp,
                                mood = coachMood,
                                triggerKey = "${uiState.tipTapCount}_$selectedMood",
                                showAura = false,
                                contentDescription = strRes(R.string.home_ai_card_title)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("home_ai_card_badge")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AutoAwesome,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = strRes(R.string.home_ai_card_badge),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Text(
                                    text = strRes(R.string.home_ai_card_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                val shownAdvice = if (selectedMood == "gentle" && uiState.tipTapCount == 0) {
                                    strRes(R.string.coach_gentle_fallback)
                                } else {
                                    uiState.currentCoachTip
                                }
                                Text(
                                    text = shownAdvice,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onOpenAiCoach,
                                shape = CircleShape,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("home_ai_card_open_cta")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strRes(R.string.home_ai_card_cta),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            OutlinedButton(
                                onClick = onTapChicken,
                                shape = CircleShape,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("home_ai_card_tip_action")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strRes(
                                        if (profile.questCoachDone) R.string.home_ai_card_tip_btn
                                        else R.string.coach_tap_claim_xp
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Streak / Rank Card (opens Quests & PRO Wheel modals)
        item(key = "home_rank_xp", contentType = "rank_xp") {
            RankAndXpHeroCard(
                profile = profile,
                onClaimCheckIn = onClaimDailyCheckIn,
                onOpenQuests = { showQuestsSheet = true },
                onOpenWheel = { showWheelSheet = true }
            )
        }

        // 4. Active Workout Checklist
        if (activeWorkout != null) {
            item(key = "home_active_checklist", contentType = "workout_checklist") {
                if (isWorkoutAccessible(activeWorkout)) {
                    DailyWorkoutChecklistCard(
                        workout = activeWorkout,
                        checkedIds = checkedExerciseIds.toSet(),
                        onToggleExercise = { exId ->
                            checkedExerciseIds = if (checkedExerciseIds.contains(exId)) {
                                checkedExerciseIds - exId
                            } else {
                                checkedExerciseIds + exId
                            }
                        },
                        onInspectExercise = { ex -> inspectedExercise = ex },
                        onStartWorkout = { onOpenWorkout(activeWorkout) }
                    )
                } else {
                    val adsWatched = adsWatchedForWorkout(activeWorkout.id)
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = surfaceCardColors,
                        border = subtleBorder,
                        elevation = flatElevation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_workout_checklist_card")
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Rounded.Lock, contentDescription = null, tint = primaryColor)
                                Text(
                                    text = activeWorkout.localizedTitle(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { onWatchAdForWorkout(activeWorkout) },
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                ) {
                                    Icon(Icons.Rounded.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${strRes(R.string.watch_ads_prefix)} ($adsWatched/3)",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                OutlinedButton(
                                    onClick = onUpgradePro,
                                    shape = CircleShape,
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text(
                                        text = strRes(R.string.unlock_with_pro),
                                        maxLines = 1,
                                        softWrap = false
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
