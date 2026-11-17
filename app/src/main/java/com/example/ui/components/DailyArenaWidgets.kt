package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.localizedName
import com.example.data.catalog.localizedSetsReps
import com.example.data.catalog.localizedTargetMuscle
import com.example.data.catalog.localizedTitle
import com.example.data.local.MealCalorieEntity
import com.example.data.model.ExerciseItem
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutProgram
import com.example.ui.i18n.strRes
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.WheelSpinOutcome
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RankAndXpHeroCard(
    profile: UserProfile,
    onClaimCheckIn: () -> Unit,
    onOpenQuests: (() -> Unit)? = null,
    onOpenWheel: (() -> Unit)? = null,
    onOpenQuestsAndWheel: (() -> Unit)? = null
) {
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val claimedToday = profile.lastCheckInDate == todayKey
    val bestStreak = maxOf(profile.bestStreakDays, profile.streakDays)

    val animatedXpProgress by animateFloatAsState(
        targetValue = (profile.xpInCurrentLevel / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "animated_xp_progress"
    )
    val heroPrimary = MaterialTheme.colorScheme.primary
    val heroSurface = MaterialTheme.colorScheme.surface
    val heroTertiary = MaterialTheme.colorScheme.tertiary

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = heroSurface),
        border = BorderStroke(1.5.dp, heroPrimary.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rank_xp_hero_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithCache {
                    val bgBrush = Brush.linearGradient(
                        colors = listOf(
                            heroPrimary.copy(alpha = 0.16f),
                            heroSurface,
                            heroTertiary.copy(alpha = 0.10f)
                        )
                    )
                    onDrawBehind {
                        drawRect(brush = bgBrush)
                    }
                }
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Top Row: Prominent Rank Badge + Title + Daily Claim CTA
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = strRes(profile.chickenRankTitleRes()),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "${strRes(R.string.level_prefix)}${profile.chickenLevel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "${profile.totalXp} XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = strRes(profile.chickenRankTitleRes()),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (claimedToday) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                            modifier = Modifier.heightIn(min = 40.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strRes(R.string.claimed_today),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onClaimCheckIn,
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("claim_daily_checkin_button")
                        ) {
                            Icon(Icons.Rounded.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strRes(R.string.claim_daily_xp),
                                maxLines = 1,
                                softWrap = false,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Streak Flame Banner
                Surface(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.82f),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocalFireDepartment,
                                    contentDescription = strRes(R.string.daily_streak_suffix),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${profile.streakDays} ${strRes(R.string.daily_streak_suffix)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.EmojiEvents,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${strRes(R.string.best_streak)}: $bestStreak",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Animated XP Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strRes(R.string.next_rank_progress),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${profile.xpInCurrentLevel} / 100 XP",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        if (animatedXpProgress > 0.005f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedXpProgress.coerceAtLeast(0.03f))
                                    .height(12.dp)
                                    .clip(CircleShape)
                                    .background(heroPrimary)
                            )
                        }
                    }
                }

                if (onOpenQuests != null || onOpenWheel != null || onOpenQuestsAndWheel != null) {
                    val doneQuests = listOf(
                        profile.questCoachDone,
                        profile.questChallengeDone,
                        profile.questHydrationDone
                    ).count { it }
                    val spinsUsed = if (profile.lastWheelSpinDate == todayKey) profile.dailySpinCount else 0
                    val spinsLeft = (2 - spinsUsed).coerceAtLeast(0)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                onOpenQuests?.invoke() ?: onOpenQuestsAndWheel?.invoke()
                            },
                            shape = CircleShape,
                            border = BorderStroke(1.dp, heroPrimary.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp)
                                .testTag("home_open_quests_modal")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = heroPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Quests ($doneQuests/3)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        val infiniteTransition = rememberInfiniteTransition(label = "wheel_spin_loop")
                        val wheelRotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 3500, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "wheel_spin_angle"
                        )

                        Button(
                            onClick = {
                                onOpenWheel?.invoke() ?: onOpenQuestsAndWheel?.invoke()
                            },
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp)
                                .testTag("home_open_wheel_modal")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .graphicsLayer { rotationZ = wheelRotation },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🎡",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PRO Wheel ($spinsLeft)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyStreakAndLevelCard(
    profile: UserProfile,
    onClaimCheckIn: () -> Unit = {},
    selectedMood: String = "balanced",
    onSelectMood: (String) -> Unit = {}
) {
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val claimedToday = profile.lastCheckInDate == todayKey

    val todayWeekIdx = remember {
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 6
        }
    }
    val bestStreak = maxOf(profile.bestStreakDays, profile.streakDays)

    val statusBadge = when (selectedMood) {
        "gentle" -> strRes(R.string.activity_path_gentle_cue)
        "energized" -> strRes(R.string.activity_path_go_for_it)
        else -> strRes(R.string.activity_path_sweet_spot)
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Activity Path & Mood Readiness
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strRes(R.string.activity_path_title),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = statusBadge,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    val bandColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                    val curveColor = MaterialTheme.colorScheme.primary
                    val nodeSurface = MaterialTheme.colorScheme.surface
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .drawWithCache {
                                val w = size.width
                                val h = size.height
                                val bandTop = Path().apply {
                                    moveTo(0f, h * 0.42f)
                                    cubicTo(w * 0.25f, h * 0.18f, w * 0.55f, h * 0.55f, w, h * 0.24f)
                                    lineTo(w, h * 0.68f)
                                    cubicTo(w * 0.55f, h * 0.92f, w * 0.25f, h * 0.54f, 0f, h * 0.78f)
                                    close()
                                }
                                val trendY = when (selectedMood) {
                                    "gentle" -> 0.56f
                                    "energized" -> 0.34f
                                    else -> 0.45f
                                }
                                val pathLine = Path().apply {
                                    moveTo(w * 0.04f, h * 0.62f)
                                    cubicTo(w * 0.28f, h * 0.38f, w * 0.56f, h * 0.64f, w * 0.90f, h * trendY)
                                }
                                val curveStroke = Stroke(width = 6f, cap = StrokeCap.Round)
                                val dotCenter = Offset(w * 0.90f, h * trendY)
                                val haloColor = curveColor.copy(alpha = 0.26f)
                                onDrawBehind {
                                    drawPath(bandTop, color = bandColor)
                                    drawPath(path = pathLine, color = curveColor, style = curveStroke)
                                    drawCircle(color = haloColor, radius = 16f, center = dotCenter)
                                    drawCircle(color = nodeSurface, radius = 9f, center = dotCenter)
                                    drawCircle(color = curveColor, radius = 6f, center = dotCenter)
                                }
                            }
                    )

                    Text(
                        text = strRes(R.string.daily_mood_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "energized" to strRes(R.string.mood_energized),
                            "balanced" to strRes(R.string.mood_balanced),
                            "gentle" to strRes(R.string.mood_gentle)
                        ).forEach { (key, label) ->
                            val isSelected = selectedMood == key
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                shape = CircleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .clip(CircleShape)
                                    .clickable { onSelectMood(key) }
                                    .testTag("mood_chip_$key")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Weekly Streak Tracker Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strRes(R.string.weekly_streak_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${strRes(R.string.best_streak)}: $bestStreak",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Connected 7-Day Weekday Streak Nodes
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp)) {
                    val lastCompletedIdx = if (claimedToday) todayWeekIdx else todayWeekIdx - 1
                    val firstLitIdx = if (profile.streakDays > 0 && lastCompletedIdx >= 0) {
                        (lastCompletedIdx - profile.streakDays + 1).coerceAtLeast(0)
                    } else {
                        Int.MAX_VALUE
                    }

                    val weekdaysRaw = strRes(R.string.weekdays_short)
                    val weekdaysShort = remember(weekdaysRaw) {
                        weekdaysRaw.split(",").map { it.trim() }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        weekdaysShort.forEachIndexed { idx, dayName ->
                            val isLit = idx in firstLitIdx..lastCompletedIdx
                            val isToday = idx == todayWeekIdx
                            val isSundayMilestone = idx == 6

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(3.dp)
                                                .background(
                                                    when {
                                                        idx == 0 -> Color.Transparent
                                                        isLit && (idx - 1) in firstLitIdx..lastCompletedIdx -> MaterialTheme.colorScheme.primary
                                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                                    }
                                                )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(3.dp)
                                                .background(
                                                    when {
                                                        idx == 6 -> Color.Transparent
                                                        isLit && (idx + 1) in firstLitIdx..lastCompletedIdx -> MaterialTheme.colorScheme.primary
                                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                                    }
                                                )
                                        )
                                    }

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isLit -> MaterialTheme.colorScheme.primary
                                                    isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                                    else -> MaterialTheme.colorScheme.surface
                                                }
                                            )
                                    ) {
                                        when {
                                            isSundayMilestone -> Icon(
                                                imageVector = Icons.Rounded.EmojiEvents,
                                                contentDescription = dayName,
                                                tint = if (isLit) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            isLit -> Icon(
                                                imageVector = Icons.Rounded.LocalFireDepartment,
                                                contentDescription = dayName,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            isToday -> Icon(
                                                imageVector = Icons.Rounded.Bolt,
                                                contentDescription = dayName,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            else -> Icon(
                                                imageVector = Icons.Rounded.RadioButtonUnchecked,
                                                contentDescription = dayName,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isToday || isLit) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isLit || isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = strRes(R.string.streak_shield_hint),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DailyQuestsAndChallengeCard(
    profile: UserProfile,
    onTapCoachQuest: () -> Unit,
    onCompleteChallengeQuest: () -> Unit,
    onCompleteHydrationQuest: () -> Unit
) {
    var challengeRunning by remember { mutableStateOf(false) }
    var challengeSeconds by remember { mutableIntStateOf(30) }

    LaunchedEffect(challengeRunning, challengeSeconds) {
        if (challengeRunning && challengeSeconds > 0) {
            delay(1000L)
            challengeSeconds--
            if (challengeSeconds == 0) {
                challengeRunning = false
                onCompleteChallengeQuest()
            }
        }
    }

    val doneCount = listOf(profile.questCoachDone, profile.questChallengeDone, profile.questHydrationDone).count { it }
    val doneLabel = strRes(R.string.common_done)

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${strRes(R.string.todays_quests)} ($doneCount/3)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strRes(if (doneCount == 3) R.string.quests_combo_done else R.string.quests_combo_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (doneCount == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            QuestRowItem(
                title = strRes(R.string.quest_coach_title),
                rewardLabel = "+25 XP",
                done = profile.questCoachDone,
                doneLabel = doneLabel,
                onClick = onTapCoachQuest
            )

            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (profile.questChallengeDone) Icons.Rounded.CheckCircle else Icons.Rounded.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strRes(R.string.quest_challenge_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strRes(if (profile.questChallengeDone) R.string.quest_challenge_done else R.string.quest_challenge_pending),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (profile.questChallengeDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!profile.questChallengeDone && !challengeRunning) {
                            Button(
                                onClick = {
                                    challengeSeconds = 30
                                    challengeRunning = true
                                },
                                modifier = Modifier.testTag("start_daily_challenge_button")
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strRes(R.string.common_start))
                            }
                        }
                    }
                    if (challengeRunning) {
                        LinearProgressIndicator(
                            progress = { (30 - challengeSeconds) / 30f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${strRes(R.string.remaining_sec)}: $challengeSeconds s",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            OutlinedButton(onClick = {
                                challengeRunning = false
                                onCompleteChallengeQuest()
                            }) {
                                Text(strRes(R.string.finish_label))
                            }
                        }
                    }
                }
            }

            QuestRowItem(
                title = strRes(R.string.quest_hydration_title),
                rewardLabel = "+35 XP",
                done = profile.questHydrationDone,
                doneLabel = doneLabel,
                icon = Icons.Rounded.WaterDrop,
                onClick = onCompleteHydrationQuest
            )
        }
    }
}

@Composable
private fun QuestRowItem(
    title: String,
    rewardLabel: String,
    done: Boolean,
    doneLabel: String,
    icon: ImageVector = Icons.Rounded.Bolt,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = !done, onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = if (done) Icons.Rounded.CheckCircle else icon,
                contentDescription = null,
                tint = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (done) doneLabel else rewardLabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DailyXpProWheelCard(
    profile: UserProfile,
    latestOutcome: WheelSpinOutcome?,
    onSpinWheel: (Boolean) -> WheelSpinOutcome?,
    onSpinAnimationFinished: (WheelSpinOutcome) -> Unit
) {
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val spinCount = if (profile.lastWheelSpinDate == todayKey) profile.dailySpinCount else 0
    val maxSpins = 2
    val spinsExhausted = spinCount >= maxSpins

    val wheelRotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var activeSpinOutcome by remember { mutableStateOf(latestOutcome) }
    val scope = rememberCoroutineScope()

    val slices = MainViewModel.WHEEL_SLICES
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Casino, contentDescription = null, tint = primaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strRes(R.string.wheel_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = onSurfaceColor
                )
            }

            val slicePaint = remember {
                android.graphics.Paint().apply {
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            }
            val hubPaint = remember(onSurfaceColor) {
                android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = onSurfaceColor.toArgb()
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 24f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            }
            val pointerPath = remember { Path() }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(228.dp)
                    .padding(vertical = 4.dp)
            ) {
                Canvas(modifier = Modifier.size(216.dp)) {
                    val radius = size.minDimension / 2f - 6f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val topLeft = Offset(center.x - radius, center.y - radius)
                    val arcSize = Size(radius * 2f, radius * 2f)
                    val sweep = 360f / slices.size.toFloat()

                    rotate(degrees = wheelRotation.value, pivot = center) {
                        slices.forEachIndexed { idx, slice ->
                            val startAngle = -90f - sweep / 2f + idx * sweep
                            val sliceColor = when {
                                slice.isProJackpot -> primaryColor
                                idx % 2 == 0 -> primaryColor.copy(alpha = 0.72f)
                                else -> bgColor
                            }
                            drawArc(
                                color = sliceColor,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = true,
                                topLeft = topLeft,
                                size = arcSize
                            )
                            drawArc(
                                color = outlineColor,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = true,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = 2.5f)
                            )

                            drawContext.canvas.nativeCanvas.apply {
                                save()
                                rotate(idx * sweep, center.x, center.y)
                                slicePaint.color = when {
                                    slice.isProJackpot -> onPrimaryColor.toArgb()
                                    idx % 2 == 0 -> onPrimaryColor.toArgb()
                                    else -> onSurfaceColor.toArgb()
                                }
                                slicePaint.textSize = if (slice.isProJackpot) 23f else 24f
                                val displayLabel = if (slice.isProJackpot) "PRO ★" else slice.label
                                drawText(displayLabel, center.x, center.y - radius * 0.62f, slicePaint)
                                restore()
                            }
                        }

                        drawCircle(
                            color = primaryColor,
                            radius = radius,
                            center = center,
                            style = Stroke(width = 6f)
                        )
                    }

                    drawCircle(color = surfaceColor, radius = radius * 0.24f, center = center)
                    drawCircle(color = primaryColor, radius = radius * 0.24f, center = center, style = Stroke(width = 4f))
                    drawContext.canvas.nativeCanvas.drawText("XP", center.x, center.y + 8f, hubPaint)

                    pointerPath.reset()
                    pointerPath.moveTo(center.x, center.y - radius + 16f)
                    pointerPath.lineTo(center.x - 14f, center.y - radius - 10f)
                    pointerPath.lineTo(center.x + 14f, center.y - radius - 10f)
                    pointerPath.close()
                    drawPath(pointerPath, color = primaryColor)
                    drawPath(pointerPath, color = onPrimaryColor, style = Stroke(width = 2f))
                }
            }

            AnimatedVisibility(visible = activeSpinOutcome != null && !isSpinning) {
                activeSpinOutcome?.let { res ->
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (res.wonProJackpot) {
                                strRes(R.string.wheel_jackpot_won)
                            } else {
                                strRes(R.string.wheel_xp_won, res.xpEarned)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = primaryColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (!isSpinning && !spinsExhausted) {
                        val isExtra = spinCount >= 1
                        val outcome = onSpinWheel(isExtra)
                        if (outcome != null) {
                            isSpinning = true
                            activeSpinOutcome = outcome
                            scope.launch {
                                val sweep = 360f / slices.size.toFloat()
                                val currentMod = ((wheelRotation.value % 360f) + 360f) % 360f
                                val desiredMod = (360f - outcome.sliceIndex * sweep) % 360f
                                val deltaMod = (desiredMod - currentMod + 360f) % 360f
                                val target = wheelRotation.value + 360f * 6f + deltaMod
                                wheelRotation.animateTo(
                                    targetValue = target,
                                    animationSpec = tween(
                                        durationMillis = 3200,
                                        easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
                                    )
                                )
                                isSpinning = false
                                onSpinAnimationFinished(outcome)
                            }
                        }
                    }
                },
                enabled = !isSpinning && !spinsExhausted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (spinsExhausted) MaterialTheme.colorScheme.background else primaryColor,
                    contentColor = if (spinsExhausted) MaterialTheme.colorScheme.onSurfaceVariant else onPrimaryColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("spin_lucky_workout_button")
            ) {
                Icon(Icons.Rounded.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isSpinning -> strRes(R.string.wheel_spinning)
                        spinsExhausted -> strRes(R.string.wheel_spins_exhausted_today)
                        spinCount == 1 -> strRes(R.string.wheel_extra_spin_ad_btn)
                        else -> strRes(R.string.wheel_spin_button)
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (spinsExhausted && !isSpinning) {
                Text(
                    text = strRes(R.string.wheel_come_back_tomorrow),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DailyNutritionSummaryCard(
    profile: UserProfile,
    calorieLogs: List<MealCalorieEntity>,
    onOpenCalorieScanner: () -> Unit
) {
    val todayLogs = remember(calorieLogs) {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        calorieLogs.filter { it.loggedAtMillis >= startOfDay }
    }
    val consumedKcal = todayLogs.sumOf { it.calories }
    val targetKcal = profile.dailyCalorieTarget.coerceAtLeast(1200)
    val consumedProtein = todayLogs.sumOf { it.proteinGrams }
    val consumedCarbs = todayLogs.sumOf { it.carbsGrams }
    val consumedFat = todayLogs.sumOf { it.fatGrams }

    val targetProtein = com.example.data.catalog.FitnessFoodDatabase.dailyProteinTargetGrams(profile)
    val targetCarbs = ((targetKcal * 0.45) / 4.0).toInt().coerceAtLeast(100)
    val targetFat = ((targetKcal * 0.25) / 9.0).toInt().coerceAtLeast(40)

    val animatedProgress by animateFloatAsState(
        targetValue = (consumedKcal.toFloat() / targetKcal.toFloat()).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 550),
        label = "nutrition_progress"
    )

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_nutrition_card")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strRes(R.string.nutrition_card_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    shape = CircleShape,
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onOpenCalorieScanner)
                        .testTag("dashboard_nutrition_scan_cta")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Restaurant,
                            contentDescription = strRes(R.string.nutrition_scan_cta),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strRes(R.string.nutrition_scan_cta),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strRes(R.string.nutrition_consumed_fmt, consumedKcal),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strRes(R.string.nutrition_target_fmt, targetKcal),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.background
            )

            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                ) {
                    MacroNutrientStat(
                        title = strRes(R.string.macro_protein),
                        current = "${consumedProtein}g",
                        target = strRes(R.string.macro_target_fmt, targetProtein)
                    )
                    MacroNutrientStat(
                        title = strRes(R.string.macro_carbs),
                        current = "${consumedCarbs}g",
                        target = strRes(R.string.macro_target_fmt, targetCarbs)
                    )
                    MacroNutrientStat(
                        title = strRes(R.string.macro_fat),
                        current = "${consumedFat}g",
                        target = strRes(R.string.macro_target_fmt, targetFat)
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroNutrientStat(title: String, current: String, target: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = current,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = target,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun DailyWorkoutChecklistCard(
    workout: WorkoutProgram,
    checkedIds: Set<String>,
    onToggleExercise: (String) -> Unit,
    onInspectExercise: (ExerciseItem) -> Unit,
    onStartWorkout: () -> Unit
) {
    val totalCount = workout.exercises.size.coerceAtLeast(1)
    val doneCount = workout.exercises.count { checkedIds.contains(it.id) }
    val allChecked = doneCount >= workout.exercises.size && workout.exercises.isNotEmpty()

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_workout_checklist_card")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strRes(R.string.checklist_pro_tailored_badge),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = strRes(R.string.checklist_card_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = workout.localizedTitle(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    shape = CircleShape
                ) {
                    Text(
                        text = strRes(R.string.checklist_progress_fmt, doneCount, totalCount),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            workout.exercises.forEach { exercise ->
                val isChecked = checkedIds.contains(exercise.id)
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onInspectExercise(exercise) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 6.dp, top = 8.dp, bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exercise.localizedName(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${exercise.localizedSetsReps()} • ${exercise.localizedTargetMuscle()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onToggleExercise(exercise.id) },
                            modifier = Modifier.testTag("dashboard_exercise_checkbox_${exercise.id}")
                        )
                    }
                }
            }

            if (allChecked) {
                Text(
                    text = strRes(R.string.checklist_all_done),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onStartWorkout,
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("dashboard_checklist_start_cta")
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strRes(R.string.checklist_start_session),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

