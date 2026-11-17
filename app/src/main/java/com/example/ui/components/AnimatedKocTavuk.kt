package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.example.R
import kotlinx.coroutines.delay

enum class CoachAnimationMood {
    IDLE,
    COACHING,
    CHEERING
}

/**
 * Lightweight FitChicken mascot rendered with downsampled async Coil decoding
 * (avoids synchronous main-thread 1024x1024 RGBA bitmap decoding) and GPU-only
 * graphicsLayer spring feedback on tap/trigger updates.
 */
@Composable
fun AnimatedKocTavuk(
    modifier: Modifier = Modifier,
    size: Dp = 128.dp,
    mood: CoachAnimationMood = CoachAnimationMood.IDLE,
    triggerKey: Any = Unit,
    loopDemo: Boolean = false,
    @Suppress("UNUSED_PARAMETER") showAura: Boolean = false,
    contentDescription: String = "FitChicken"
) {
    val defaultFrame = when (mood) {
        CoachAnimationMood.IDLE -> 0
        CoachAnimationMood.COACHING -> 1
        CoachAnimationMood.CHEERING -> 2
    }

    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    val imageLoader = remember(context) { AppImageLoader.get(context) }
    val targetPx = with(LocalDensity.current) { size.roundToPx().coerceIn(96, 360) }

    var activeFrame by remember(mood) { mutableIntStateOf(defaultFrame) }
    var bounceActive by remember { mutableStateOf(false) }

    LaunchedEffect(triggerKey, isPreview) {
        if (triggerKey != Unit && !loopDemo && !isPreview) {
            bounceActive = true
            val sequence = listOf(1, 2, defaultFrame)
            for (frame in sequence) {
                activeFrame = frame
                delay(220L)
            }
            bounceActive = false
        }
    }

    LaunchedEffect(loopDemo, mood, isPreview) {
        if (loopDemo && !isPreview) {
            val cycle = when (mood) {
                CoachAnimationMood.CHEERING -> listOf(2, 1, 2, 0)
                CoachAnimationMood.COACHING -> listOf(1, 0, 1, 2)
                CoachAnimationMood.IDLE -> listOf(0, 1, 0, 2)
            }
            var idx = 0
            while (true) {
                activeFrame = cycle[idx % cycle.size]
                idx++
                delay(1500L)
            }
        } else {
            activeFrame = defaultFrame
        }
    }

    val popScale by animateFloatAsState(
        targetValue = if (bounceActive) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "pop_scale"
    )

    val frameRes = when (activeFrame) {
        1 -> R.drawable.img_koc_tavuk_1
        2 -> R.drawable.img_koc_tavuk_2
        else -> R.drawable.img_koc_tavuk_0
    }

    val request = remember(frameRes, targetPx) {
        ImageRequest.Builder(context)
            .data(frameRes)
            .size(targetPx, targetPx)
            .precision(Precision.INEXACT)
            .crossfade(false)
            .build()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        AsyncImage(
            model = request,
            imageLoader = imageLoader,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = popScale
                    scaleY = popScale
                    translationY = if (bounceActive) -6f else 0f
                }
        )
    }
}
