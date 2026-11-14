package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.WarmGold
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal val AthleteSkin = Color(0xFFF6B773)
internal val AthleteSkinShadow = Color(0xFFE09E56)
internal val MaleShirtBlue = Color(0xFF0C94F5)
internal val MaleShortsBlack = Color(0xFF1E242B)
internal val MaleShoeRed = Color(0xFFD91E2A)
internal val MaleHairDark = Color(0xFF1A2530)
internal val FemaleTopPink = Color(0xFFFF1E8E)
internal val FemaleLeggingsNavy = Color(0xFF213F6B)
internal val FemaleHairBrown = Color(0xFF3E1F10)

internal enum class LimbRole {
    AUTO,
    UPPER_ARM,
    FOREARM,
    FULL_ARM,
    THIGH,
    CALF,
    FULL_LEG,
    SPINE
}

internal data class AthletePalette(
    val primaryLimb: Color,
    val secondaryLimb: Color,
    val torsoFill: Color,
    val jointCore: Color,
    val muscleGlow: Color,
    val equipMetal: Color,
    val equipPad: Color,
    val guideLine: Color,
    val isFemale: Boolean = false,
    val hairColor: Color = MaleHairDark,
    val outfitAccent: Color = MaleShirtBlue
)

private fun isSameRgb(a: Color, b: Color): Boolean {
    return abs(a.red - b.red) < 0.04f &&
        abs(a.green - b.green) < 0.04f &&
        abs(a.blue - b.blue) < 0.04f
}

internal fun DrawScope.drawStudioBackdrop(
    w: Float,
    h: Float,
    floorY: Float,
    gridColor: Color,
    accent: Color
) {
    val cols = 8
    for (i in 1 until cols) {
        val x = w * (i / cols.toFloat())
        drawLine(
            color = gridColor.copy(alpha = 0.08f),
            start = Offset(x, h * 0.08f),
            end = Offset(x, floorY),
            strokeWidth = 1.5f
        )
    }
    val rows = 4
    for (r in 1..rows) {
        val y = floorY * (r / (rows + 1f))
        drawLine(
            color = gridColor.copy(alpha = 0.08f),
            start = Offset(w * 0.06f, y),
            end = Offset(w * 0.94f, y),
            strokeWidth = 1.5f
        )
    }
    drawLine(
        color = accent.copy(alpha = 0.55f),
        start = Offset(w * 0.06f, floorY),
        end = Offset(w * 0.94f, floorY),
        strokeWidth = 5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = accent.copy(alpha = 0.18f),
        start = Offset(w * 0.12f, floorY + 6f),
        end = Offset(w * 0.88f, floorY + 6f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawTaperedSegment(
    a: Offset,
    b: Offset,
    color: Color,
    rStart: Float,
    rMid: Float,
    rEnd: Float
) {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val len = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
    val nx = -dy / len
    val ny = dx / len
    val mid = Offset((a.x + b.x) * 0.48f, (a.y + b.y) * 0.48f)

    val path = Path().apply {
        moveTo(a.x + nx * rStart, a.y + ny * rStart)
        lineTo(mid.x + nx * rMid, mid.y + ny * rMid)
        lineTo(b.x + nx * rEnd, b.y + ny * rEnd)
        lineTo(b.x - nx * rEnd, b.y - ny * rEnd)
        lineTo(mid.x - nx * rMid, mid.y - ny * rMid)
        lineTo(a.x - nx * rStart, a.y - ny * rStart)
        close()
    }
    drawPath(path = path, color = color)
    drawCircle(color = color, radius = rStart * 0.92f, center = a)
    drawCircle(color = color, radius = rEnd * 0.92f, center = b)
}

private fun DrawScope.drawAthleteFoot(
    ankle: Offset,
    dirX: Float,
    pal: AthletePalette,
    alpha: Float,
    scale: Float = 1f
) {
    val dir = if (dirX >= 0f) 1f else -1f
    if (pal.isFemale) {
        // Sleek bare/minimalist studio foot like reference female vector
        val footPath = Path().apply {
            moveTo(ankle.x - 4f * dir * scale, ankle.y - 3f * scale)
            lineTo(ankle.x + 15f * dir * scale, ankle.y + 3f * scale)
            lineTo(ankle.x - 5f * dir * scale, ankle.y + 4f * scale)
            close()
        }
        drawPath(footPath, color = AthleteSkin.copy(alpha = alpha))
    } else {
        // Bold red sneaker with crisp white sole & laces like reference male vector
        val shoeTop = Path().apply {
            moveTo(ankle.x - 7f * dir * scale, ankle.y - 5f * scale)
            lineTo(ankle.x + 5f * dir * scale, ankle.y - 5f * scale)
            lineTo(ankle.x + 16f * dir * scale, ankle.y + 2f * scale)
            lineTo(ankle.x - 7f * dir * scale, ankle.y + 2f * scale)
            close()
        }
        drawPath(shoeTop, color = MaleShoeRed.copy(alpha = alpha))
        drawLine(
            color = Color.White.copy(alpha = 0.92f * alpha),
            start = Offset(ankle.x - 7f * dir * scale, ankle.y + 2.5f * scale),
            end = Offset(ankle.x + 16f * dir * scale, ankle.y + 2.5f * scale),
            strokeWidth = 3f * scale,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = 0.85f * alpha),
            start = Offset(ankle.x + 2f * dir * scale, ankle.y - 3f * scale),
            end = Offset(ankle.x + 7f * dir * scale, ankle.y - 1f * scale),
            strokeWidth = 1.6f * scale,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Draws an anatomical, tapered limb segment (quads/calves/biceps/forearms) tailored to
 * the Male (blue sleeve, tan arms, black shorts, tan legs, red sneakers) or
 * Female (tan arms, full navy leggings, minimal foot) reference vectors.
 */
internal fun DrawScope.drawLimbSegment(
    a: Offset,
    b: Offset,
    color: Color,
    pal: AthletePalette,
    thickness: Float = 11f,
    role: LimbRole = LimbRole.AUTO
) {
    val alpha = color.alpha.coerceIn(0.25f, 1f)
    val scale = if (pal.isFemale) 0.92f else 1.12f
    val rStart = thickness * 0.62f * scale
    val rMid = thickness * 0.72f * scale
    val rEnd = thickness * 0.44f * scale

    val resolvedRole = if (role != LimbRole.AUTO) {
        role
    } else {
        when {
            isSameRgb(color, pal.secondaryLimb) -> {
                if (thickness >= 10.8f) LimbRole.THIGH else LimbRole.FULL_LEG
            }
            isSameRgb(color, pal.primaryLimb) -> LimbRole.UPPER_ARM
            isSameRgb(color, WarmGold) -> LimbRole.FOREARM
            isSameRgb(color, ElectricTeal) -> LimbRole.SPINE
            else -> LimbRole.FULL_LEG
        }
    }

    when (resolvedRole) {
        LimbRole.UPPER_ARM, LimbRole.FOREARM, LimbRole.FULL_ARM -> {
            // Base skin-toned arm
            drawTaperedSegment(a, b, AthleteSkin.copy(alpha = alpha), rStart, rMid, rEnd)
            // Male short-sleeve blue t-shirt cap on upper arm (top 40% from shoulder a)
            if (!pal.isFemale && resolvedRole == LimbRole.UPPER_ARM) {
                val sleeveEnd = Offset(a.x + (b.x - a.x) * 0.42f, a.y + (b.y - a.y) * 0.42f)
                drawTaperedSegment(a, sleeveEnd, MaleShirtBlue.copy(alpha = alpha), rStart * 1.06f, rStart * 1.02f, rMid * 0.92f)
            }
            // Subtle hand silhouette at distal point
            if (resolvedRole == LimbRole.FOREARM || resolvedRole == LimbRole.FULL_ARM) {
                drawCircle(color = AthleteSkin.copy(alpha = alpha), radius = rEnd * 1.05f, center = b)
            }
        }

        LimbRole.THIGH -> {
            if (pal.isFemale) {
                drawTaperedSegment(a, b, FemaleLeggingsNavy.copy(alpha = alpha), rStart, rMid, rEnd * 1.05f)
            } else {
                // Assume proximal (hip) end is `a` if role is explicitly THIGH, or higher endpoint if AUTO
                val hipEnd = if (role == LimbRole.THIGH) a else if (a.y <= b.y) a else b
                val kneeEnd = if (hipEnd == a) b else a
                val shortsEnd = Offset(
                    hipEnd.x + (kneeEnd.x - hipEnd.x) * 0.72f,
                    hipEnd.y + (kneeEnd.y - hipEnd.y) * 0.72f
                )
                drawTaperedSegment(hipEnd, kneeEnd, AthleteSkin.copy(alpha = alpha), rStart, rMid, rEnd)
                drawTaperedSegment(hipEnd, shortsEnd, MaleShortsBlack.copy(alpha = alpha), rStart * 1.12f, rMid * 1.08f, rMid * 0.96f)
            }
        }

        LimbRole.CALF -> {
            if (pal.isFemale) {
                drawTaperedSegment(a, b, FemaleLeggingsNavy.copy(alpha = alpha), rStart, rMid, rEnd)
                val footEnd = if (role == LimbRole.CALF) b else if (b.y >= a.y) b else a
                val kneeEnd = if (footEnd == b) a else b
                drawAthleteFoot(footEnd, (footEnd.x - kneeEnd.x).takeIf { abs(it) > 2f } ?: 1f, pal, alpha)
            } else {
                drawTaperedSegment(a, b, AthleteSkin.copy(alpha = alpha), rStart, rMid, rEnd)
                val footEnd = if (role == LimbRole.CALF) b else if (b.y >= a.y) b else a
                val kneeEnd = if (footEnd == b) a else b
                drawAthleteFoot(footEnd, (footEnd.x - kneeEnd.x).takeIf { abs(it) > 2f } ?: 1f, pal, alpha)
            }
        }

        LimbRole.FULL_LEG -> {
            val hipEnd = if (role == LimbRole.FULL_LEG) a else if (a.y <= b.y) a else b
            val footEnd = if (hipEnd == a) b else a
            if (pal.isFemale) {
                drawTaperedSegment(hipEnd, footEnd, FemaleLeggingsNavy.copy(alpha = alpha), rStart, rMid, rEnd)
            } else {
                val shortsEnd = Offset(
                    hipEnd.x + (footEnd.x - hipEnd.x) * 0.44f,
                    hipEnd.y + (footEnd.y - hipEnd.y) * 0.44f
                )
                drawTaperedSegment(hipEnd, footEnd, AthleteSkin.copy(alpha = alpha), rStart, rMid, rEnd)
                drawTaperedSegment(hipEnd, shortsEnd, MaleShortsBlack.copy(alpha = alpha), rStart * 1.12f, rMid * 1.06f, rMid * 0.92f)
            }
            drawAthleteFoot(footEnd, (footEnd.x - hipEnd.x).takeIf { abs(it) > 2f } ?: 1f, pal, alpha)
        }

        LimbRole.SPINE, LimbRole.AUTO -> {
            val spineColor = if (pal.isFemale) FemaleTopPink else MaleShirtBlue
            drawTaperedSegment(a, b, spineColor.copy(alpha = alpha), rStart, rMid, rEnd)
        }
    }
}

/**
 * Draws the sculpted Male (blue athletic top + black shorts waistband)
 * or Female (hot-pink crop top + exposed tan midriff + high-waist navy leggings) torso.
 */
internal fun DrawScope.drawTorso(
    hip: Offset,
    shoulder: Offset,
    color: Color,
    thickness: Float = 16f,
    pal: AthletePalette? = null
) {
    val isFemale = pal?.isFemale ?: false
    val dx = shoulder.x - hip.x
    val dy = shoulder.y - hip.y
    val len = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
    val nx = -dy / len
    val ny = dx / len

    val hipHalf = if (isFemale) thickness * 0.80f else thickness * 0.72f
    val waistHalf = if (isFemale) thickness * 0.56f else thickness * 0.68f
    val shoulderHalf = if (isFemale) thickness * 0.86f else thickness * 1.22f

    fun halfAt(t: Float): Float = when {
        t <= 0.36f -> hipHalf + (waistHalf - hipHalf) * (t / 0.36f)
        else -> waistHalf + (shoulderHalf - waistHalf) * ((t - 0.36f) / 0.64f)
    }

    fun drawBand(t0: Float, t1: Float, fill: Color) {
        val p0 = Offset(hip.x + dx * t0, hip.y + dy * t0)
        val p1 = Offset(hip.x + dx * t1, hip.y + dy * t1)
        val h0 = halfAt(t0)
        val h1 = halfAt(t1)
        val path = Path().apply {
            moveTo(p0.x - nx * h0, p0.y - ny * h0)
            lineTo(p1.x - nx * h1, p1.y - ny * h1)
            lineTo(p1.x + nx * h1, p1.y + ny * h1)
            lineTo(p0.x + nx * h0, p0.y + ny * h0)
            close()
        }
        drawPath(path = path, color = fill)
    }

    if (isFemale) {
        // Navy high-waist leggings hip -> exposed tan midriff -> hot-pink crop tank top
        drawCircle(color = FemaleLeggingsNavy, radius = hipHalf * 0.94f, center = hip)
        drawBand(0f, 0.28f, FemaleLeggingsNavy)
        drawBand(0.28f, 0.46f, AthleteSkin)
        drawBand(0.46f, 1.0f, FemaleTopPink)
        // Neck/shoulder skin triangle & smooth shoulder caps
        drawCircle(color = AthleteSkin, radius = thickness * 0.34f, center = shoulder)
        drawCircle(color = FemaleTopPink, radius = thickness * 0.36f, center = Offset(shoulder.x - nx * shoulderHalf * 0.45f, shoulder.y - ny * shoulderHalf * 0.45f))
        drawCircle(color = FemaleTopPink, radius = thickness * 0.36f, center = Offset(shoulder.x + nx * shoulderHalf * 0.45f, shoulder.y + ny * shoulderHalf * 0.45f))
    } else {
        // Black athletic shorts hip -> bright blue fitness top
        drawCircle(color = MaleShortsBlack, radius = hipHalf * 0.94f, center = hip)
        drawBand(0f, 0.34f, MaleShortsBlack)
        drawBand(0.34f, 1.0f, MaleShirtBlue)
        drawCircle(color = MaleShirtBlue, radius = thickness * 0.62f, center = shoulder)
    }
}

/**
 * Draws the Male (golden-tan head + swept dark navy hair) or
 * Female (golden-tan head + dark brown hair & high bun) head silhouette matching the user PNGs.
 */
internal fun DrawScope.drawAthleteHead(
    center: Offset,
    radius: Float,
    color: Color,
    headbandColor: Color = MaleShirtBlue,
    isFemale: Boolean = false
) {
    // Neck connector
    drawCircle(color = AthleteSkin, radius = radius * 0.44f, center = Offset(center.x, center.y + radius * 0.95f))

    if (isFemale) {
        // High round dark-brown hair bun behind top-back of head (like image_fbdc3bb1.png)
        drawCircle(
            color = FemaleHairBrown,
            radius = radius * 0.60f,
            center = Offset(center.x - radius * 0.62f, center.y - radius * 0.86f)
        )
        // Outer hair silhouette wrapping back of head
        drawCircle(
            color = FemaleHairBrown,
            radius = radius * 1.05f,
            center = Offset(center.x - radius * 0.14f, center.y - radius * 0.08f)
        )
        // Warm golden-tan face
        drawCircle(color = AthleteSkin, radius = radius * 0.94f, center = center)
        // Curved bangs / hairline across forehead
        drawArc(
            color = FemaleHairBrown,
            startAngle = 165f,
            sweepAngle = 195f,
            useCenter = true,
            topLeft = Offset(center.x - radius * 1.02f, center.y - radius * 1.08f),
            size = Size(radius * 2.04f, radius * 1.15f)
        )
    } else {
        // Warm golden-tan face
        drawCircle(color = AthleteSkin, radius = radius, center = center)
        // Swept dark navy-charcoal hair crown (like image_a75e9eaa.png)
        val hairPath = Path().apply {
            moveTo(center.x - radius * 0.95f, center.y + radius * 0.10f)
            quadraticTo(
                center.x - radius * 1.12f,
                center.y - radius * 1.15f,
                center.x + radius * 0.15f,
                center.y - radius * 1.18f
            )
            quadraticTo(
                center.x + radius * 1.18f,
                center.y - radius * 1.05f,
                center.x + radius * 1.02f,
                center.y - radius * 0.10f
            )
            quadraticTo(
                center.x + radius * 0.35f,
                center.y - radius * 0.48f,
                center.x - radius * 0.45f,
                center.y - radius * 0.25f
            )
            close()
        }
        drawPath(path = hairPath, color = MaleHairDark)
    }
}

internal fun DrawScope.drawMuscleHighlight(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color = FlameOrange
) {
    val pulse = 0.55f + 0.45f * sin(phase * 3.14159f)
    drawCircle(
        color = color.copy(alpha = 0.24f * pulse),
        radius = radius * (1.35f + 0.3f * pulse),
        center = center
    )
    drawCircle(
        color = color.copy(alpha = 0.52f * pulse),
        radius = radius * 0.70f,
        center = center
    )
}

internal fun DrawScope.drawMotionArrow(
    start: Offset,
    end: Offset,
    color: Color = ElectricTeal
) {
    drawLine(
        color = color.copy(alpha = 0.65f),
        start = start,
        end = end,
        strokeWidth = 3.5f,
        cap = StrokeCap.Round,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 7f), 0f)
    )
    val dx = end.x - start.x
    val dy = end.y - start.y
    val len = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
    val ux = dx / len
    val uy = dy / len
    val headLen = 11f
    val left = Offset(end.x - ux * headLen - uy * headLen * 0.6f, end.y - uy * headLen + ux * headLen * 0.6f)
    val right = Offset(end.x - ux * headLen + uy * headLen * 0.6f, end.y - uy * headLen - ux * headLen * 0.6f)
    drawLine(color.copy(alpha = 0.85f), end, left, strokeWidth = 3.5f, cap = StrokeCap.Round)
    drawLine(color.copy(alpha = 0.85f), end, right, strokeWidth = 3.5f, cap = StrokeCap.Round)
}

internal fun DrawScope.drawMat(
    leftX: Float,
    rightX: Float,
    floorY: Float,
    color: Color
) {
    drawRoundRect(
        color = color.copy(alpha = 0.65f),
        topLeft = Offset(leftX, floorY - 7f),
        size = Size(rightX - leftX, 7f),
        cornerRadius = CornerRadius(4f, 4f)
    )
}

internal fun DrawScope.drawFlatBench(
    leftX: Float,
    rightX: Float,
    benchTopY: Float,
    floorY: Float,
    padColor: Color,
    metalColor: Color
) {
    drawLine(metalColor, Offset(leftX + 18f, benchTopY + 10f), Offset(leftX + 18f, floorY), strokeWidth = 7f, cap = StrokeCap.Round)
    drawLine(metalColor, Offset(rightX - 18f, benchTopY + 10f), Offset(rightX - 18f, floorY), strokeWidth = 7f, cap = StrokeCap.Round)
    drawRoundRect(
        color = padColor,
        topLeft = Offset(leftX, benchTopY),
        size = Size(rightX - leftX, 13f),
        cornerRadius = CornerRadius(6f, 6f)
    )
}

internal fun DrawScope.drawBarbell(
    center: Offset,
    halfWidth: Float = 38f,
    plateRadius: Float = 15f,
    barColor: Color,
    plateColor: Color = FlameOrange
) {
    drawLine(
        color = barColor,
        start = Offset(center.x - halfWidth, center.y),
        end = Offset(center.x + halfWidth, center.y),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
    drawRoundRect(
        color = plateColor,
        topLeft = Offset(center.x - halfWidth - 4f, center.y - plateRadius),
        size = Size(9f, plateRadius * 2f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRoundRect(
        color = plateColor,
        topLeft = Offset(center.x + halfWidth - 5f, center.y - plateRadius),
        size = Size(9f, plateRadius * 2f),
        cornerRadius = CornerRadius(3f, 3f)
    )
}

internal fun DrawScope.drawDumbbell(
    center: Offset,
    color: Color = WarmGold,
    sizeScale: Float = 1f
) {
    val w = 22f * sizeScale
    val h = 13f * sizeScale
    drawRoundRect(
        color = color,
        topLeft = Offset(center.x - w / 2f, center.y - h / 2f),
        size = Size(w, h),
        cornerRadius = CornerRadius(4f, 4f)
    )
}

internal fun DrawScope.drawKettlebell(
    center: Offset,
    color: Color = WarmGold
) {
    drawCircle(
        color = color,
        radius = 9f,
        center = Offset(center.x, center.y - 8f),
        style = Stroke(width = 3.5f)
    )
    drawCircle(color = FlameOrange, radius = 12f, center = center)
}

internal fun DrawScope.drawSineRope(
    start: Offset,
    end: Offset,
    phase: Float,
    phaseShift: Float,
    color: Color
) {
    val path = Path()
    val segments = 24
    for (i in 0..segments) {
        val t = i / segments.toFloat()
        val x = start.x + (end.x - start.x) * t
        val amp = 18f * (1f - t * 0.45f)
        val wave = sin(t * 14f - phase * 6.283f + phaseShift) * amp
        val y = start.y + (end.y - start.y) * t + wave
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

internal fun DrawScope.drawAnglePoint(
    origin: Offset,
    length: Float,
    deg: Float
): Offset {
    val rad = Math.toRadians(deg.toDouble())
    return Offset(
        origin.x + (cos(rad) * length).toFloat(),
        origin.y + (sin(rad) * length).toFloat()
    )
}
