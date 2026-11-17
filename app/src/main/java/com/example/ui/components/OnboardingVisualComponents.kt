package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.SuccessStory
import com.example.ui.i18n.strRes
import com.example.ui.theme.AppThemeOption
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun OnboardingCircularHeader(
    step: Int,
    totalSteps: Int,
    activeTheme: AppThemeOption,
    onBack: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val progress = ((step + 1f) / (totalSteps + 1f)).coerceIn(0.05f, 1f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (step > 0) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("onboarding_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = strRes(R.string.common_back),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(44.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    strokeWidth = 4.dp,
                    trackColor = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "${step + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            ) {
                Text(
                    text = strRes(R.string.onb_header_progress, step + 1, totalSteps + 1),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .testTag("onboarding_theme_toggle")
        ) {
            Icon(
                imageVector = if (activeTheme.isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                contentDescription = strRes(R.string.theme_studio_title),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun OnboardingStepTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OnboardingMetricPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun OnboardingRulerPicker(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    stepUnit: Float = 1f,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tickColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
    var dragAccum by remember { mutableFloatStateOf(0f) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(valueRange, stepUnit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    dragAccum -= dragAmount
                    val threshold = 16f
                    if (kotlin.math.abs(dragAccum) >= threshold) {
                        val steps = (dragAccum / threshold).toInt()
                        dragAccum -= steps * threshold
                        val next = (value + steps * stepUnit)
                            .coerceIn(valueRange.start, valueRange.endInclusive)
                        onValueChange(next)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
            val midX = size.width / 2f
            val spacing = 14.dp.toPx()
            val currentInt = (value / stepUnit).roundToInt()
            for (offset in -14..14) {
                val tickVal = currentInt + offset
                val raw = tickVal * stepUnit
                if (raw < valueRange.start || raw > valueRange.endInclusive) continue
                val x = midX + offset * spacing
                if (x < 0f || x > size.width) continue
                val isMajor = tickVal % 5 == 0
                val lineH = if (isMajor) size.height * 0.48f else size.height * 0.28f
                drawLine(
                    color = if (offset == 0) primary else tickColor,
                    start = Offset(x, size.height - lineH),
                    end = Offset(x, size.height - 6.dp.toPx()),
                    strokeWidth = if (offset == 0) 4f else if (isMajor) 2.5f else 1.5f,
                    cap = StrokeCap.Round
                )
            }
            drawLine(
                color = primary,
                start = Offset(midX, 8.dp.toPx()),
                end = Offset(midX, size.height - 4.dp.toPx()),
                strokeWidth = 4.5f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun OnboardingOptionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
        label = "opt_bg"
    )
    Surface(
        color = bgColor,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (selected) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun OnboardingSelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = if (!selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun OnboardingFocusGrid(
    selectedAreas: Set<String>,
    onToggleArea: (String) -> Unit
) {
    val areas = listOf(
        "abs" to R.string.focus_abs,
        "chest" to R.string.focus_chest,
        "arms" to R.string.focus_arms,
        "shoulders" to R.string.focus_shoulders,
        "back" to R.string.focus_back,
        "legs" to R.string.focus_legs
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        areas.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                pair.forEach { (key, labelRes) ->
                    val isSel = selectedAreas.contains(key)
                    Surface(
                        color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            if (isSel) 1.5.dp else 1.dp,
                            if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleArea(key) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = strRes(labelRes),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSel) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingSocialAuthorityCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnimatedKocTavuk(size = 76.dp, mood = CoachAnimationMood.CHEERING, loopDemo = true, showAura = false)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                            Text(
                                text = strRes(R.string.onb_rating_badge),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(color = MaterialTheme.colorScheme.background, shape = CircleShape) {
                            Text(
                                text = strRes(R.string.onb_users_badge),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Text(strRes(R.string.onb_social_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            Text(strRes(R.string.onb_social_sub), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun OnboardingProgressCurveChart(
    currentWeightKg: Float,
    targetWeightKg: Float,
    month1WeightKg: Float,
    targetDateIso: String
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.tertiary
    val gridColor = MaterialTheme.colorScheme.outline
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(16.dp)) {
            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), shape = CircleShape) {
                Text(
                    text = strRes(R.string.onb_target_date_fmt, targetDateIso),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(14.dp)
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithCache {
                            val values = listOf(currentWeightKg, month1WeightKg, targetWeightKg, targetWeightKg)
                            val minV = (values.minOrNull() ?: 50f) - 4f
                            val maxV = (values.maxOrNull() ?: 90f) + 4f
                            val span = (maxV - minV).coerceAtLeast(2f)
                            fun pt(idx: Int, v: Float): Offset {
                                val x = (idx / 3f) * size.width
                                val y = size.height - ((v - minV) / span) * size.height
                                return Offset(x, y)
                            }
                            val pts = values.mapIndexed { i, v -> pt(i, v) }
                            val strokePath = Path().apply {
                                moveTo(pts[0].x, pts[0].y)
                                for (i in 0 until pts.lastIndex) {
                                    val midX = (pts[i].x + pts[i + 1].x) / 2f
                                    cubicTo(midX, pts[i].y, midX, pts[i + 1].y, pts[i + 1].x, pts[i + 1].y)
                                }
                            }
                            val fillPath = Path().apply {
                                addPath(strokePath)
                                lineTo(pts.last().x, size.height)
                                lineTo(pts.first().x, size.height)
                                close()
                            }
                            val fillBrush = Brush.verticalGradient(listOf(primary.copy(alpha = 0.30f), Color.Transparent))
                            val strokeBrush = Brush.horizontalGradient(listOf(secondary, primary))
                            val strokeStyle = Stroke(width = 7f, cap = StrokeCap.Round)
                            val gridLineColor = gridColor.copy(alpha = 0.45f)
                            onDrawBehind {
                                drawLine(gridLineColor, Offset(0f, size.height * 0.5f), Offset(size.width, size.height * 0.5f), 2f)
                                drawPath(fillPath, brush = fillBrush)
                                drawPath(strokePath, brush = strokeBrush, style = strokeStyle)
                                pts.forEachIndexed { idx, p ->
                                    drawCircle(color = if (idx >= 2) primary else secondary, radius = 9f, center = p)
                                }
                            }
                        }
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = strRes(R.string.onb_month_1_label, String.format(Locale.US, "%.1f", month1WeightKg)),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = strRes(R.string.onb_year_1_label, String.format(Locale.US, "%.1f", targetWeightKg)),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HoldToCommitButton(
    isCommitted: Boolean,
    onCommitted: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val holdProgress = remember { Animatable(if (isCommitted) 1f else 0f) }
    var isHolding by remember { mutableStateOf(false) }
    val primary = MaterialTheme.colorScheme.primary

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = CircleShape,
        border = BorderStroke(2.dp, primary),
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clip(CircleShape)
            .pointerInput(isCommitted) {
                if (isCommitted) return@pointerInput
                detectTapGestures(
                    onPress = {
                        isHolding = true
                        val job = scope.launch {
                            holdProgress.animateTo(1f, tween(durationMillis = 1050, easing = LinearEasing))
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onCommitted()
                        }
                        tryAwaitRelease()
                        isHolding = false
                        if (!isCommitted && holdProgress.value < 1f) {
                            job.cancel()
                            holdProgress.animateTo(0f, tween(200))
                        }
                    }
                )
            }
            .testTag("onboarding_hold_commit_button")
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(holdProgress.value.coerceIn(0f, 1f))
                        .height(62.dp)
                        .background(primary)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCommitted) Icons.Rounded.CheckCircle else Icons.Rounded.Fingerprint,
                    contentDescription = null,
                    tint = if (holdProgress.value > 0.52f || isCommitted) MaterialTheme.colorScheme.onPrimary else primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isCommitted -> strRes(R.string.onb_committed_btn)
                        isHolding -> strRes(R.string.onb_holding_btn, (holdProgress.value * 100).roundToInt())
                        else -> strRes(R.string.onb_hold_btn)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (holdProgress.value > 0.52f || isCommitted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun OnboardingAnalysisLoader(onFinished: () -> Unit) {
    val bars = remember { List(4) { Animatable(0f) } }
    val labels = listOf(
        strRes(R.string.onb_analyzing_bar1),
        strRes(R.string.onb_analyzing_bar2),
        strRes(R.string.onb_analyzing_bar3),
        strRes(R.string.onb_analyzing_bar4)
    )
    LaunchedEffect(Unit) {
        for (bar in bars) {
            bar.animateTo(1f, tween(durationMillis = 560, easing = EaseInOutCubic))
        }
        delay(220L)
        onFinished()
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AnimatedKocTavuk(size = 84.dp, mood = CoachAnimationMood.COACHING, loopDemo = true, showAura = false)
                Column(modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(strRes(R.string.onb_analyzing_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            bars.forEachIndexed { idx, anim ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(labels[idx], style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        Text("${(anim.value * 100).roundToInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    LinearProgressIndicator(
                        progress = { anim.value },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    }
}

@Composable
fun FreeVsProComparisonTable() {
    val rows = listOf(
        Triple(strRes(R.string.onb_compare_custom_plan), true, true),
        Triple(strRes(R.string.onb_compare_ai_coach), true, true),
        Triple(strRes(R.string.onb_compare_calorie_scan), true, true),
        Triple(strRes(R.string.onb_compare_ad_free), false, true)
    )
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(strRes(R.string.onb_compare_feature), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                Text(strRes(R.string.plan_free), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(0.75f))
                Text(strRes(R.string.plan_pro), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, modifier = Modifier.weight(0.75f))
            }
            rows.forEach { (label, freeIncluded, proIncluded) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.5.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(0.75f)) {
                        if (freeIncluded) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                        } else {
                            Icon(Icons.Rounded.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(13.dp))
                        }
                    }
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(0.75f)) {
                        if (proIncluded) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingStoryCard(story: SuccessStory) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("${story.name} (${story.age})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp)) {
                    Text(story.badge, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { Icon(Icons.Rounded.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp)) }
                Spacer(modifier = Modifier.width(6.dp))
                Text(story.resultSummary, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            Text("\"${story.quote}\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
