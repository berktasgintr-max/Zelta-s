package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.WorkoutProgram
import com.example.firebase.GoogleSignInButton
import com.example.ui.components.DailyQuestsAndChallengeCard
import com.example.ui.components.DailyStreakAndLevelCard
import com.example.ui.components.DailyXpProWheelCard
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.WheelSpinOutcome
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    uiState: MainUiState,
    personalizedWorkouts: List<WorkoutProgram>,
    isWorkoutAccessible: (WorkoutProgram) -> Boolean,
    adsWatchedForWorkout: (String) -> Int,
    onTapChicken: () -> Unit,
    onClaimDailyCheckIn: () -> Unit,
    onCompleteQuest: (String) -> Unit,
    onSpinWheel: () -> WheelSpinOutcome?,
    onSpinAnimationFinished: (WheelSpinOutcome) -> Unit,
    onToggleLanguage: (String) -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenWorkout: (WorkoutProgram) -> Unit,
    onWatchAdForWorkout: (WorkoutProgram) -> Unit,
    onUpgradePro: () -> Unit,
    onOpenYouthCatalog: () -> Unit,
    onOpenAiCoach: () -> Unit,
    onOpenCalorieScanner: () -> Unit,
    onAuthMessage: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current
    val profile = uiState.profile
    var bounceTrigger by remember { mutableStateOf(false) }
    var armFrame by remember { mutableIntStateOf(0) }
    LaunchedEffect(uiState.tipTapCount) {
        if (uiState.tipTapCount > 0) {
            val sequence = listOf(1, 2, 1, 2, 1, 0)
            for (f in sequence) {
                armFrame = f
                delay(200L)
            }
        }
    }
    val chickenFrameRes = when (armFrame) {
        1 -> R.drawable.img_koc_tavuk_1
        2 -> R.drawable.img_koc_tavuk_2
        else -> R.drawable.img_koc_tavuk_0
    }
    val chickenScale by animateFloatAsState(
        targetValue = if (bounceTrigger) 1.09f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        finishedListener = { bounceTrigger = false },
        label = "chicken_bounce"
    )
    val idleTransition = rememberInfiniteTransition(label = "chicken_idle")
    val idlePulse by idleTransition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "idle_pulse"
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Greeting, Language Switcher, Theme Switcher & Plan Badge
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${strings.hello}, ${profile.displayName}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${strings.targetLabel}: ${profile.targetWeightKg.toInt()} kg • ${strings.currentLabel}: ${profile.currentWeightKg} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Instant TR | EN Switcher Pill
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .clip(CircleShape)
                            .clickable { onToggleLanguage(if (isEn) "tr" else "en") }
                            .testTag("home_language_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = strings.settingsLanguageTitle,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEn) "EN" else "TR",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenThemePicker,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("open_theme_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = strings.themeStudioTitle,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Surface(
                        color = if (profile.isProActive) ext.goldContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        modifier = Modifier.border(
                            1.dp,
                            if (profile.isProActive) ext.goldText else MaterialTheme.colorScheme.outline,
                            CircleShape
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = if (profile.isProActive) ext.goldText else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (profile.isProActive) strings.planPro else strings.planFree,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (profile.isProActive) ext.goldText else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Connected Weekly Streak & Chicken Level Card
        item {
            DailyStreakAndLevelCard(
                profile = profile,
                onClaimCheckIn = onClaimDailyCheckIn
            )
        }

        // Interactive Realistic Koç Tavuk Card with Real Speech Bubble + Tail & Accurate Once-Daily XP Badge
        item {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f), MaterialTheme.shapes.large)
                    .clickable {
                        bounceTrigger = true
                        onTapChicken()
                    }
                    .testTag("tappable_chicken_coach_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Frameless transparent Koç Tavuk with animated arm movement on advice tap
                        Image(
                            painter = painterResource(id = chickenFrameRes),
                            contentDescription = strings.coachAdviceHeader,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(width = 112.dp, height = 142.dp)
                                .scale(chickenScale * idlePulse)
                        )

                        // Pointer tail aimed at Koç Tavuk's mouth
                        val bubbleBg = MaterialTheme.colorScheme.surface
                        val bubbleBorder = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                        Canvas(
                            modifier = Modifier
                                .width(14.dp)
                                .height(22.dp)
                        ) {
                            val tailPath = Path().apply {
                                moveTo(size.width + 1f, 0f)
                                lineTo(0f, size.height * 0.5f)
                                lineTo(size.width + 1f, size.height)
                                close()
                            }
                            drawPath(tailPath, color = bubbleBg)
                        }

                        // Speech Bubble Box
                        Surface(
                            color = bubbleBg,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, bubbleBorder, RoundedCornerShape(18.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.TipsAndUpdates,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${strings.coachAdviceHeader} • ${(uiState.tipTapCount % 40) + 1}/40",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                AnimatedContent(
                                    targetState = uiState.currentCoachTip,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "coach_tip_text"
                                ) { tip ->
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = if (profile.questCoachDone) ext.mintContainer else MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (profile.questCoachDone) strings.coachTapNextOnly else strings.coachTapClaimXp,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (profile.questCoachDone) ext.mintText else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily 3 Quests + Split-Neutral 30s Opener Challenge
        item {
            DailyQuestsAndChallengeCard(
                profile = profile,
                onTapCoachQuest = {
                    bounceTrigger = true
                    onTapChicken()
                },
                onCompleteChallengeQuest = { onCompleteQuest("challenge") },
                onCompleteHydrationQuest = { onCompleteQuest("hydration") }
            )
        }

        // Daily XP & 1-in-1000 PRO Wheel
        item {
            DailyXpProWheelCard(
                profile = profile,
                latestOutcome = uiState.latestWheelOutcome,
                onSpinWheel = onSpinWheel,
                onSpinAnimationFinished = onSpinAnimationFinished
            )
        }

        // Youth 0-18 Mode Callout
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ext.purpleContainer),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ext.purpleText.copy(alpha = 0.55f), MaterialTheme.shapes.medium)
                    .clickable(onClick = onOpenYouthCatalog)
                    .testTag("youth_mode_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = ext.purpleText, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (profile.isYouthGrowthMode) {
                                "${strings.youthActiveTitle} (${profile.age})"
                            } else {
                                strings.youthAdultTitle
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = ext.purpleText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (profile.isYouthGrowthMode) strings.youthActiveDesc else strings.youthAdultDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Optional Cloud Sign-In Banner if not signed in
        if (uiState.currentUser == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f), MaterialTheme.shapes.medium)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.cloudSaveTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Text(
                            text = strings.cloudSaveDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        GoogleSignInButton(
                            label = strings.googleSignInButton,
                            modifier = Modifier.fillMaxWidth(),
                            onAuthSuccess = {
                                onAuthMessage(
                                    if (isEn) "Google account connected & synced!" else "Google hesabın bağlandı ve verilerin bulutla eşitlendi!"
                                )
                            },
                            onAuthError = { err -> onAuthMessage(err) }
                        )
                    }
                }
            }
        }

        // Personalized Program Section (Home-filtered for home users, Youth-first for 0-18)
        item {
            Column {
                Text(
                    text = strings.personalizedTitle,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = if (profile.isProActive) strings.personalizedProSub else strings.personalizedFreeSub,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(personalizedWorkouts, key = { it.id }) { workout ->
                    val unlocked = isWorkoutAccessible(workout)
                    val lockedForPersonalized = !unlocked
                    val adsWatched = adsWatchedForWorkout(workout.id)
                    val previewRes = workoutPreviewDrawable(workout)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .width(274.dp)
                            .border(
                                1.dp,
                                if (lockedForPersonalized) ext.goldText.copy(alpha = 0.55f) else ext.mintText.copy(alpha = 0.45f),
                                MaterialTheme.shapes.large
                            )
                            .clickable {
                                if (lockedForPersonalized) onWatchAdForWorkout(workout) else onOpenWorkout(workout)
                            }
                    ) {
                        Column {
                            // Pre-workout Preview Photo on personalized card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(108.dp)
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
                                                colors = listOf(Color.Black.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.65f))
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
                                    Surface(
                                        color = if (workout.isProSpecial) WarmGold else MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (workout.isProSpecial) strings.proBadge else workout.category.label(strings.langCode),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (workout.isProSpecial) ext.onGoldButton else MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    if (lockedForPersonalized) {
                                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = WarmGold, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (workout.isRecommendedForYouth) {
                                    Surface(color = ext.purpleContainer, shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            text = strings.youthRecommendedBadge,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ext.purpleText,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Text(text = workout.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${workout.durationMinutes} ${if (isEn) "min" else "dk"} • ${workout.exercises.size} ${if (isEn) "Exercises" else "Egzersiz"} • ${workout.difficulty}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (lockedForPersonalized) {
                                    Button(
                                        onClick = { onWatchAdForWorkout(workout) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Rounded.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${strings.watchAdsPrefix} ($adsWatched/3)")
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onOpenWorkout(workout) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Rounded.FitnessCenter, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.viewAndStart)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // PRO Photo Calorie AI Calculator Quick Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ext.goldText.copy(alpha = 0.55f), MaterialTheme.shapes.large)
                    .clickable(onClick = onOpenCalorieScanner)
                    .testTag("home_calorie_scanner_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ext.goldContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = strings.homeCalorieTitle,
                            tint = ext.goldText,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = strings.homeCalorieTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = ext.goldContainer, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = strings.proBadge,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ext.goldText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = strings.homeCalorieSub,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick AI Coach Callout
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAiCoach)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_koc_tavuk_0),
                        contentDescription = strings.askAiTitle,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(62.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.askAiTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (profile.isProActive) {
                                strings.askAiProSub
                            } else {
                                val rem = (3 - profile.aiQuestionsUsedToday).coerceAtLeast(0)
                                if (isEn) "Free Plan: $rem / 3 AI questions remaining today"
                                else "Ücretsiz Plan: Bugün kalan $rem / 3 soru hakkın var"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Rounded.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        // Free Plan Ad Banner (strictly hidden for Pro Plan users)
        if (!profile.isProActive) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.adBannerTag, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.adBannerBody,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onUpgradePro) {
                            Text(strings.adFreeProBtn)
                        }
                    }
                }
            }
        }
    }
}
