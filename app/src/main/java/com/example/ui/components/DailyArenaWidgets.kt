package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.WheelSpinOutcome
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DailyStreakAndLevelCard(
    profile: UserProfile,
    onClaimCheckIn: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val claimedToday = profile.lastCheckInDate == todayKey

    val pulseTransition = rememberInfiniteTransition(label = "streak_flame")
    val flameScale by pulseTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "flame_scale"
    )

    // Monday = 0 .. Sunday = 6
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
    val levelPrefix = if (isEn) "Lv." else "Sv."

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f), MaterialTheme.shapes.large)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .scale(flameScale)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocalFireDepartment,
                                contentDescription = strings.dailyStreakSuffix,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${profile.streakDays} ${strings.dailyStreakSuffix}!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$levelPrefix${profile.chickenLevel} • ${profile.chickenRankTitle(strings.langCode)} (${profile.totalXp} XP)",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (claimedToday) {
                        Surface(
                            color = ext.mintContainer,
                            shape = CircleShape
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = ext.mintText, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.claimedToday, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ext.mintText)
                            }
                        }
                    } else {
                        Button(
                            onClick = onClaimCheckIn,
                            modifier = Modifier.testTag("claim_daily_checkin_button")
                        ) {
                            Icon(Icons.Rounded.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.claimDailyXp)
                        }
                    }
                }

                // XP Level progress bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = strings.nextRankProgress,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${profile.xpInCurrentLevel} / 150 XP",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { profile.xpInCurrentLevel / 150f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                }

                // Weekly Streak Tracker Header + Best Streak Badge
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strings.weeklyStreakTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = ext.goldContainer,
                        shape = CircleShape,
                        modifier = Modifier.border(1.dp, ext.goldText.copy(alpha = 0.55f), CircleShape)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = ext.goldText, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${strings.bestStreak}: $bestStreak",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ext.goldText
                            )
                        }
                    }
                }

                // Connected 7-Day Weekday Streak Nodes (Pzt..Paz / Mon..Sun)
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)) {
                        val lastCompletedIdx = if (claimedToday) todayWeekIdx else todayWeekIdx - 1
                        val firstLitIdx = if (profile.streakDays > 0 && lastCompletedIdx >= 0) {
                            (lastCompletedIdx - profile.streakDays + 1).coerceAtLeast(0)
                        } else {
                            Int.MAX_VALUE
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            strings.weekdaysShort.forEachIndexed { idx, dayName ->
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
                                        // Connector rails left & right
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(3.dp)
                                                    .background(
                                                        when {
                                                            idx == 0 -> Color.Transparent
                                                            isLit && (idx - 1) in firstLitIdx..lastCompletedIdx -> MaterialTheme.colorScheme.primary
                                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
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
                                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                                        }
                                                    )
                                            )
                                        }

                                        // Day node circle
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isLit && isSundayMilestone -> WarmGold
                                                        isLit -> MaterialTheme.colorScheme.primary
                                                        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                                    }
                                                )
                                                .border(
                                                    width = if (isToday && !isLit) 2.dp else 1.dp,
                                                    color = when {
                                                        isLit && isSundayMilestone -> WarmGold
                                                        isLit || isToday -> MaterialTheme.colorScheme.primary
                                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                                    },
                                                    shape = CircleShape
                                                )
                                        ) {
                                            when {
                                                isSundayMilestone -> Icon(
                                                    imageVector = Icons.Rounded.EmojiEvents,
                                                    contentDescription = dayName,
                                                    tint = if (isLit) ext.onGoldButton else ext.goldText,
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
                                        color = when {
                                            isLit && isSundayMilestone -> ext.goldText
                                            isLit || isToday -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
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

@Composable
fun DailyQuestsAndChallengeCard(
    profile: UserProfile,
    onTapCoachQuest: () -> Unit,
    onCompleteChallengeQuest: () -> Unit,
    onCompleteHydrationQuest: () -> Unit
) {
    val strings = LocalAppStrings.current
    val ext = LocalExtendedColors.current
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

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f), MaterialTheme.shapes.large)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${strings.todaysQuests} ($doneCount/3)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (doneCount == 3) strings.questsComboDone else strings.questsComboHint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (doneCount == 3) ext.mintText else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }

            // Quest 1: Coach Tip (strictly once-per-day +XP)
            QuestRowItem(
                title = strings.questCoachTitle,
                rewardLabel = "+25 XP",
                done = profile.questCoachDone,
                doneLabel = strings.doneLabel,
                onClick = onTapCoachQuest
            )

            // Quest 2: Split-neutral 30s posture/breath/mobility opener (safe regardless of leg/push/pull day)
            Surface(
                color = if (profile.questChallengeDone) ext.mintContainer else MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (profile.questChallengeDone) Icons.Rounded.CheckCircle else Icons.Rounded.Timer,
                            contentDescription = null,
                            tint = if (profile.questChallengeDone) ext.mintText else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.questChallengeTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (profile.questChallengeDone) strings.questChallengeDone else strings.questChallengePending,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (profile.questChallengeDone) ext.mintText else MaterialTheme.colorScheme.secondary
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
                                Text(strings.startLabel)
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
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${strings.remainingSec}: $challengeSeconds s",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            OutlinedButton(onClick = {
                                challengeRunning = false
                                onCompleteChallengeQuest()
                            }) {
                                Text(strings.finishLabel)
                            }
                        }
                    }
                }
            }

            // Quest 3: Hydration / Nutrition Check
            QuestRowItem(
                title = strings.questHydrationTitle,
                rewardLabel = "+35 XP",
                done = profile.questHydrationDone,
                doneLabel = strings.doneLabel,
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
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Rounded.Bolt,
    onClick: () -> Unit
) {
    val ext = LocalExtendedColors.current
    Surface(
        color = if (done) ext.mintContainer else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = !done, onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = if (done) Icons.Rounded.CheckCircle else icon,
                contentDescription = null,
                tint = if (done) ext.mintText else MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (done) doneLabel else rewardLabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (done) ext.mintText else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DailyXpProWheelCard(
    profile: UserProfile,
    latestOutcome: WheelSpinOutcome?,
    onSpinWheel: () -> WheelSpinOutcome?,
    onSpinAnimationFinished: (WheelSpinOutcome) -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val spunToday = profile.lastWheelSpinDate == todayKey

    val wheelRotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var activeSpinOutcome by remember { mutableStateOf(latestOutcome) }
    val scope = rememberCoroutineScope()

    val slices = MainViewModel.WHEEL_SLICES
    val sliceColors = remember {
        listOf(
            Color(0xFFE05219),
            Color(0xFF009688),
            Color(0xFF3949AB),
            Color(0xFFFFB800), // Index 3: 1/1000 PRO Jackpot!
            Color(0xFF7B1FA2),
            Color(0xFF2E7D32),
            Color(0xFFC2185B),
            Color(0xFF0277BD)
        )
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, WarmGold.copy(alpha = 0.7f), MaterialTheme.shapes.large)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Casino, contentDescription = null, tint = ext.goldText)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.wheelTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Prominent 1-in-1000 PRO Chance Callout above the wheel
            Surface(
                color = ext.goldContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ext.goldText.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                    .testTag("wheel_pro_chance_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = ext.goldText, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.wheelProChanceBanner,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ext.goldText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Animated Canvas Spinning Wheel
            val outlineColor = MaterialTheme.colorScheme.outline
            val hubBgColor = MaterialTheme.colorScheme.surface
            val hubTextColor = MaterialTheme.colorScheme.onSurface
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

                    // Rotating slices + labels
                    rotate(degrees = wheelRotation.value, pivot = center) {
                        slices.forEachIndexed { idx, slice ->
                            val startAngle = -90f - sweep / 2f + idx * sweep
                            val sliceColor = sliceColors[idx % sliceColors.size]
                            drawArc(
                                color = sliceColor,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = true,
                                topLeft = topLeft,
                                size = arcSize
                            )
                            drawArc(
                                color = Color.White.copy(alpha = 0.35f),
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = true,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = 2.5f)
                            )

                            // Label inside slice along radial axis
                            drawContext.canvas.nativeCanvas.apply {
                                save()
                                rotate(idx * sweep, center.x, center.y)
                                val paint = android.graphics.Paint().apply {
                                    isAntiAlias = true
                                    color = if (slice.isProJackpot) Color(0xFF1A1000).toArgb() else Color.White.toArgb()
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    textSize = if (slice.isProJackpot) 23f else 24f
                                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                                }
                                val displayLabel = if (slice.isProJackpot) "PRO ★" else slice.label
                                drawText(displayLabel, center.x, center.y - radius * 0.62f, paint)
                                restore()
                            }
                        }

                        // Outer wheel ring
                        drawCircle(
                            color = WarmGold,
                            radius = radius,
                            center = center,
                            style = Stroke(width = 6f)
                        )
                    }

                    // Center hub
                    drawCircle(color = hubBgColor, radius = radius * 0.24f, center = center)
                    drawCircle(color = WarmGold, radius = radius * 0.24f, center = center, style = Stroke(width = 4f))
                    drawContext.canvas.nativeCanvas.apply {
                        val hubPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = hubTextColor.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 24f
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        drawText("XP", center.x, center.y + 8f, hubPaint)
                    }

                    // Stationary Top Pointer Triangle
                    val pointerPath = Path().apply {
                        moveTo(center.x, center.y - radius + 16f)
                        lineTo(center.x - 14f, center.y - radius - 10f)
                        lineTo(center.x + 14f, center.y - radius - 10f)
                        close()
                    }
                    drawPath(pointerPath, color = FlameOrange)
                    drawPath(pointerPath, color = outlineColor, style = Stroke(width = 2f))
                }
            }

            AnimatedVisibility(visible = activeSpinOutcome != null && !isSpinning) {
                activeSpinOutcome?.let { res ->
                    Surface(
                        color = if (res.wonProJackpot) ext.goldContainer else ext.mintContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (res.wonProJackpot) {
                                if (isEn) "JACKPOT! You won 1 Month of PRO Plan!"
                                else "BÜYÜK ÖDÜL! 1 Aylık PRO Plan Kazandın!"
                            } else {
                                if (isEn) "Reward Won: +${res.xpEarned} XP!"
                                else "Bugünkü Kazancın: +${res.xpEarned} XP!"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (res.wonProJackpot) ext.goldText else ext.mintText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (!isSpinning && !spunToday) {
                        val outcome = onSpinWheel()
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
                enabled = !isSpinning && !spunToday,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (spunToday) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
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
                        isSpinning -> strings.wheelSpinning
                        spunToday -> strings.wheelSpunToday
                        else -> strings.wheelSpinButton
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            if (spunToday && !isSpinning) {
                Text(
                    text = strings.wheelComeBackTomorrow,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
