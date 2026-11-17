package com.example.ui.components

import android.content.Context
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.size.Precision
import com.example.R
import com.example.data.catalog.ExerciseMediaCatalog
import com.example.data.catalog.MuscleGroupZone
import com.example.data.model.ExerciseDemoType
import com.example.data.model.ExerciseItem
import com.example.ui.i18n.strRes
import kotlinx.coroutines.delay

/**
 * Shared singleton ImageLoader across the entire app to avoid allocating
 * duplicate Coil memory/disk caches and decoder threads per composable.
 */
object AppImageLoader {
    @Volatile
    private var instance: ImageLoader? = null

    fun get(context: Context): ImageLoader {
        return instance ?: synchronized(this) {
            val appCtx = context.applicationContext
            instance ?: ImageLoader.Builder(appCtx)
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .memoryCache {
                    MemoryCache.Builder(appCtx)
                        .maxSizePercent(0.18)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(appCtx.cacheDir.resolve("coil_image_cache"))
                        .maxSizeBytes(32L * 1024L * 1024L)
                        .build()
                }
                .allowHardware(true)
                .crossfade(false)
                .respectCacheHeaders(false)
                .build()
                .also { instance = it }
        }
    }
}

@Composable
fun rememberGifEnabledImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember(context) { AppImageLoader.get(context) }
}

private val StudioBottomVignetteBrush = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color(0xE010131A))
)

@Composable
fun ExerciseAnimationPlayer(
    exercise: ExerciseItem,
    localizedName: String,
    localizedTargetMuscle: String,
    modifier: Modifier = Modifier,
    isFullScreenHero: Boolean = false,
    playerHeight: Dp = if (isFullScreenHero) 268.dp else 216.dp
) {
    val media = remember(exercise.id) { ExerciseMediaCatalog.mediaFor(exercise.id) }
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val imageLoader = rememberGifEnabledImageLoader()

    var isPlaying by remember(exercise.id) { mutableStateOf(!isPreview) }
    var isSlowMotion by remember(exercise.id) { mutableStateOf(false) }
    var activeFrame by remember(exercise.id) { mutableIntStateOf(0) }
    var startFrameReady by remember(exercise.id) { mutableStateOf(false) }
    var peakFrameReady by remember(exercise.id) { mutableStateOf(false) }

    val cycleMs = if (isSlowMotion) 1700L else 950L

    LaunchedEffect(exercise.id, isPlaying, isSlowMotion, isPreview) {
        if (!isPlaying || isPreview) return@LaunchedEffect
        while (true) {
            delay(cycleMs)
            activeFrame = if (activeFrame == 0) 1 else 0
        }
    }

    val startRequest = remember(media.startFrameUrl) {
        ImageRequest.Builder(context)
            .data(media.startFrameUrl)
            .size(560, 380)
            .precision(Precision.INEXACT)
            .crossfade(false)
            .build()
    }
    val peakRequest = remember(media.peakFrameUrl) {
        ImageRequest.Builder(context)
            .data(media.peakFrameUrl)
            .size(560, 380)
            .precision(Precision.INEXACT)
            .crossfade(false)
            .build()
    }

    val anyPhotoLoaded = startFrameReady || peakFrameReady
    val showPeak = peakFrameReady && (activeFrame == 1 || !startFrameReady)
    val peakAlpha by animateFloatAsState(
        targetValue = if (showPeak) 1f else 0f,
        animationSpec = tween(durationMillis = if (isSlowMotion) 320 else 180),
        label = "peak_frame_alpha"
    )

    val phaseText = strRes(if (activeFrame == 0) R.string.ex_phase_start else R.string.ex_phase_peak)
    val playToggleDesc = strRes(if (isPlaying) R.string.ex_anim_pause else R.string.ex_anim_play)
    val speedLabel = strRes(if (isSlowMotion) R.string.ex_speed_slow else R.string.ex_speed_1x)
    val studioBackdrop = Color(0xFF10131A)
    val studioTop = Color(0xFF1C2230)
    val studioGlow = MaterialTheme.colorScheme.primary
    val cornerShape = RoundedCornerShape(if (isFullScreenHero) 22.dp else 16.dp)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("exercise_animation_player_${exercise.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(playerHeight)
                .clip(cornerShape)
                .drawWithCache {
                    val backdropBrush = Brush.verticalGradient(listOf(studioTop, studioBackdrop))
                    val spotlightBrush = Brush.radialGradient(
                        colors = listOf(studioGlow.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.42f),
                        radius = size.maxDimension * 0.50f
                    )
                    val gridStep = 28.dp.toPx()
                    val gridPath = Path().apply {
                        var x = gridStep
                        while (x < size.width) {
                            moveTo(x, 0f)
                            lineTo(x, size.height)
                            x += gridStep
                        }
                        var y = gridStep
                        while (y < size.height) {
                            moveTo(0f, y)
                            lineTo(size.width, y)
                            y += gridStep
                        }
                    }
                    val gridColor = Color.White.copy(alpha = 0.045f)
                    onDrawBehind {
                        drawRect(brush = backdropBrush)
                        drawPath(gridPath, color = gridColor, style = Stroke(width = 1f))
                        drawRect(brush = spotlightBrush)
                    }
                }
                .border(
                    BorderStroke(
                        width = if (isFullScreenHero) 1.5.dp else 1.dp,
                        color = studioGlow.copy(alpha = if (isFullScreenHero) 0.42f else 0.22f)
                    ),
                    shape = cornerShape
                )
        ) {
            // Only draw fallback anatomical visualizer before exercise photos load
            if (!anyPhotoLoaded) {
                MuscleZonePulseVisualizer(
                    muscleZone = media.muscleZone,
                    demoType = exercise.demoType,
                    isSlowMotion = isSlowMotion,
                    isPeakPhase = activeFrame == 1,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (!isPreview) {
                val imageScale = if (isFullScreenHero) ContentScale.Fit else ContentScale.Crop
                AsyncImage(
                    model = startRequest,
                    imageLoader = imageLoader,
                    contentDescription = localizedName,
                    contentScale = imageScale,
                    onSuccess = { startFrameReady = true },
                    onError = { startFrameReady = false },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = if (startFrameReady) 1f - peakAlpha else 0f }
                )

                AsyncImage(
                    model = peakRequest,
                    imageLoader = imageLoader,
                    contentDescription = "$localizedName - $phaseText",
                    contentScale = imageScale,
                    onSuccess = { peakFrameReady = true },
                    onError = { peakFrameReady = false },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = if (peakFrameReady) peakAlpha else 0f }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .align(Alignment.BottomCenter)
                    .background(StudioBottomVignetteBrush)
            )

            // Top-left Form/Phase live pill
            Surface(
                color = studioBackdrop.copy(alpha = 0.86f),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (activeFrame == 1) MaterialTheme.colorScheme.primary
                                else Color.White.copy(alpha = 0.72f)
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = phaseText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Top-right Play/Pause & Speed Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(width = 64.dp, height = 48.dp)
                        .clip(CircleShape)
                        .clickable { isSlowMotion = !isSlowMotion }
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        shape = CircleShape
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isSlowMotion) Icons.Rounded.SlowMotionVideo else Icons.Rounded.Speed,
                                contentDescription = speedLabel,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = speedLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { isPlaying = !isPlaying }
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = playToggleDesc,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Bottom overlay: Target Muscle Badge + Interactive Frame Switcher
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Surface(
                    color = studioBackdrop.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = localizedTargetMuscle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (anyPhotoLoaded) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0, 1).forEach { idx ->
                            Surface(
                                color = if (activeFrame == idx) MaterialTheme.colorScheme.primary
                                else studioBackdrop.copy(alpha = 0.88f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        isPlaying = false
                                        activeFrame = idx
                                    }
                            ) {
                                Text(
                                    text = "${idx + 1}/2",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (activeFrame == idx) MaterialTheme.colorScheme.onPrimary
                                    else Color.White.copy(alpha = 0.82f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MuscleZonePulseVisualizer(
    muscleZone: MuscleGroupZone,
    demoType: ExerciseDemoType,
    isSlowMotion: Boolean,
    isPeakPhase: Boolean,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val mutedBody = Color.White.copy(alpha = 0.24f)
    val surfaceTrack = Color.White.copy(alpha = 0.16f)

    // Driven cleanly by active phase transitions instead of an independent 60fps infinite loop
    val pulse by animateFloatAsState(
        targetValue = if (isPeakPhase) 1f else 0.25f,
        animationSpec = tween(durationMillis = if (isSlowMotion) 420 else 240),
        label = "muscle_phase_pulse"
    )

    Canvas(modifier = modifier.padding(18.dp)) {
        val w = size.width
        val h = size.height
        val cx = w * 0.36f
        val bodyScale = (h / 145f).coerceIn(0.85f, 1.65f)

        val activeRingRadius = 28f * bodyScale + (14f * bodyScale * pulse)
        drawCircle(
            color = primary.copy(alpha = 0.12f + 0.14f * pulse),
            radius = activeRingRadius * 1.45f,
            center = Offset(cx, h * 0.48f)
        )

        fun zoneColor(zone: MuscleGroupZone): Color {
            val isTarget = muscleZone == zone || muscleZone == MuscleGroupZone.FULL_BODY
            return if (isTarget) primary.copy(alpha = 0.55f + 0.45f * pulse) else mutedBody
        }

        drawCircle(
            color = mutedBody,
            radius = 12f * bodyScale,
            center = Offset(cx, h * 0.16f)
        )

        drawRoundRect(
            color = zoneColor(MuscleGroupZone.SHOULDERS),
            topLeft = Offset(cx - 34f * bodyScale, h * 0.26f),
            size = Size(68f * bodyScale, 14f * bodyScale),
            cornerRadius = CornerRadius(7f * bodyScale, 7f * bodyScale)
        )

        val torsoColor = when (muscleZone) {
            MuscleGroupZone.CHEST -> zoneColor(MuscleGroupZone.CHEST)
            MuscleGroupZone.BACK -> primary.copy(alpha = 0.55f + 0.45f * pulse)
            MuscleGroupZone.FULL_BODY -> primary.copy(alpha = 0.5f + 0.45f * pulse)
            else -> mutedBody
        }
        drawRoundRect(
            color = torsoColor,
            topLeft = Offset(cx - 24f * bodyScale, h * 0.35f),
            size = Size(48f * bodyScale, 24f * bodyScale),
            cornerRadius = CornerRadius(8f * bodyScale, 8f * bodyScale)
        )

        drawRoundRect(
            color = zoneColor(MuscleGroupZone.CORE),
            topLeft = Offset(cx - 19f * bodyScale, h * 0.51f),
            size = Size(38f * bodyScale, 22f * bodyScale),
            cornerRadius = CornerRadius(6f * bodyScale, 6f * bodyScale)
        )

        val armShiftY = if (demoType == ExerciseDemoType.PRESS || demoType == ExerciseDemoType.PUSHUP || demoType == ExerciseDemoType.CURL) {
            -6f * bodyScale * pulse
        } else 0f
        drawRoundRect(
            color = zoneColor(MuscleGroupZone.ARMS),
            topLeft = Offset(cx - 42f * bodyScale, h * 0.29f + armShiftY),
            size = Size(11f * bodyScale, 42f * bodyScale),
            cornerRadius = CornerRadius(5f * bodyScale, 5f * bodyScale)
        )
        drawRoundRect(
            color = zoneColor(MuscleGroupZone.ARMS),
            topLeft = Offset(cx + 31f * bodyScale, h * 0.29f + armShiftY),
            size = Size(11f * bodyScale, 42f * bodyScale),
            cornerRadius = CornerRadius(5f * bodyScale, 5f * bodyScale)
        )

        val legShiftY = if (demoType == ExerciseDemoType.SQUAT || demoType == ExerciseDemoType.LUNGE || demoType == ExerciseDemoType.JUMP) {
            5f * bodyScale * pulse
        } else 0f
        drawRoundRect(
            color = zoneColor(MuscleGroupZone.LEGS_GLUTES),
            topLeft = Offset(cx - 20f * bodyScale, h * 0.67f + legShiftY),
            size = Size(16f * bodyScale, 44f * bodyScale),
            cornerRadius = CornerRadius(7f * bodyScale, 7f * bodyScale)
        )
        drawRoundRect(
            color = zoneColor(MuscleGroupZone.LEGS_GLUTES),
            topLeft = Offset(cx + 4f * bodyScale, h * 0.67f + legShiftY),
            size = Size(16f * bodyScale, 44f * bodyScale),
            cornerRadius = CornerRadius(7f * bodyScale, 7f * bodyScale)
        )

        val gaugeX = w * 0.72f
        val gaugeY = h * 0.48f
        val gaugeRadius = 30f * bodyScale
        val arcTopLeft = Offset(gaugeX - gaugeRadius, gaugeY - gaugeRadius)
        val arcSize = Size(gaugeRadius * 2f, gaugeRadius * 2f)
        val arcStroke = Stroke(width = 7f * bodyScale, cap = StrokeCap.Round)
        drawArc(
            color = surfaceTrack,
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = arcStroke
        )
        drawArc(
            color = primary,
            startAngle = 135f,
            sweepAngle = 270f * pulse.coerceIn(0.08f, 1f),
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = arcStroke
        )
        drawCircle(
            color = primary.copy(alpha = 0.25f + 0.55f * pulse),
            radius = 7f * bodyScale,
            center = Offset(gaugeX, gaugeY)
        )
    }
}
