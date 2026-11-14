package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ExerciseItem
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.WarmGold
import kotlin.math.cos

@Composable
fun ExerciseFormVisualizer(
    exercise: ExerciseItem,
    gender: String = "male",
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val spec = remember(exercise.id, exercise.demoType) { resolveExerciseMotion(exercise) }
    var isPaused by remember(exercise.id) { mutableStateOf(false) }
    var slowMotion by remember(exercise.id) { mutableStateOf(false) }
    var previewGender by remember(exercise.id, gender) { mutableStateOf(gender) }
    var rawTimeSec by remember(exercise.id) { mutableFloatStateOf(0f) }

    LaunchedEffect(exercise.id, isPaused, slowMotion) {
        if (isPaused) return@LaunchedEffect
        var lastMs = 0L
        while (true) {
            withFrameMillis { frameMs ->
                if (lastMs != 0L) {
                    val dt = ((frameMs - lastMs).coerceIn(0L, 64L)) / 1000f
                    val speedFactor = if (slowMotion) 0.5f else 1f
                    rawTimeSec += dt * speedFactor
                }
                lastMs = frameMs
            }
        }
    }

    val periodSec = (spec.cycleMs / 1000f).coerceAtLeast(0.8f)
    val cyclePos = (rawTimeSec % (periodSec * 2f)) / periodSec
    val linearPhase = if (cyclePos <= 1f) cyclePos else (2f - cyclePos)
    val phase = 0.5f - 0.5f * cos(linearPhase * Math.PI).toFloat()
    val goingForward = cyclePos <= 1f

    val isFemale = previewGender == "female"
    val ext = LocalExtendedColors.current
    val primary = MaterialTheme.colorScheme.onSurface
    val accent = MaterialTheme.colorScheme.primary
    val secondary = if (isFemale) FemaleTopPink else MaleShirtBlue
    val surface = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline

    val palette = remember(primary, secondary, outline, accent, isFemale) {
        AthletePalette(
            primaryLimb = primary,
            secondaryLimb = secondary,
            torsoFill = primary.copy(alpha = 0.92f),
            jointCore = WarmGold,
            muscleGlow = accent,
            equipMetal = outline.copy(alpha = 0.95f),
            equipPad = accent.copy(alpha = 0.35f),
            guideLine = ElectricTeal,
            isFemale = isFemale,
            outfitAccent = if (isFemale) FemaleTopPink else MaleShirtBlue
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.14f),
                        surface,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .border(1.dp, outline.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .padding(10.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val floorY = h * 0.87f

            drawStudioBackdrop(w, h, floorY, outline, accent)

            when (spec.kind) {
                ExerciseMotionKind.BENCH_PRESS_FLAT,
                ExerciseMotionKind.BENCH_PRESS_INCLINE,
                ExerciseMotionKind.FLOOR_PRESS,
                ExerciseMotionKind.SEATED_CHEST_PRESS,
                ExerciseMotionKind.CHEST_FLY,
                ExerciseMotionKind.OVERHEAD_PRESS,
                ExerciseMotionKind.LATERAL_RAISE,
                ExerciseMotionKind.SKULL_CRUSHER,
                ExerciseMotionKind.CABLE_PUSHDOWN,
                ExerciseMotionKind.DIPS,
                ExerciseMotionKind.CHAIR_DIPS,
                ExerciseMotionKind.LAT_PULLDOWN,
                ExerciseMotionKind.CABLE_ROW,
                ExerciseMotionKind.FACE_PULL,
                ExerciseMotionKind.BENT_OVER_ROW,
                ExerciseMotionKind.SHRUGS,
                ExerciseMotionKind.PULL_UP,
                ExerciseMotionKind.AUSTRALIAN_ROW,
                ExerciseMotionKind.DEAD_HANG,
                ExerciseMotionKind.BICEPS_CURL,
                ExerciseMotionKind.INCLINE_CURL,
                ExerciseMotionKind.PUSHUP_STANDARD,
                ExerciseMotionKind.PUSHUP_DECLINE,
                ExerciseMotionKind.PUSHUP_INCLINE,
                ExerciseMotionKind.PIKE_PUSHUP -> drawUpperBodyMotion(spec.kind, phase, w, h, floorY, palette)

                else -> drawLowerCoreCardioMotion(spec.kind, phase, w, h, floorY, palette)
            }
        }

        // Top-left: Target Muscle Focus Badge
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = exercise.targetMuscle,
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Top-right: Male/Female Switcher, Slow Motion & Play/Pause Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { previewGender = if (isFemale) "male" else "female" }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isFemale) Icons.Rounded.Female else Icons.Rounded.Male,
                        contentDescription = if (isEn) "Switch Athlete" else "Sporcu Değiştir",
                        tint = if (isFemale) FemaleTopPink else MaleShirtBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isFemale) (if (isEn) "Female" else "Kadın") else (if (isEn) "Male" else "Erkek"),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                color = if (slowMotion) ext.tealContainer else MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { slowMotion = !slowMotion }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Speed,
                        contentDescription = if (isEn) "Speed" else "Hız",
                        tint = if (slowMotion) ext.tealText else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (slowMotion) "0.5x" else "1x",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (slowMotion) ext.tealText else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                shape = CircleShape,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { isPaused = !isPaused }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                        contentDescription = if (isPaused) (if (isEn) "Play" else "Oynat") else (if (isEn) "Pause" else "Duraklat"),
                        tint = ext.orangeText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Bottom-left: Dynamic movement phase & breathing cue
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text = if (goingForward) spec.phaseA else spec.phaseB,
                style = MaterialTheme.typography.labelMedium,
                color = ext.tealText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
