package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.MintVital
import com.example.ui.theme.WarmGold
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.drawUpperBodyMotion(
    kind: ExerciseMotionKind,
    p: Float,
    w: Float,
    h: Float,
    floorY: Float,
    pal: AthletePalette
) {
    val cx = w * 0.5f
    val headR = h * 0.068f

    when (kind) {
        ExerciseMotionKind.BENCH_PRESS_FLAT,
        ExerciseMotionKind.FLOOR_PRESS -> {
            val isFloor = kind == ExerciseMotionKind.FLOOR_PRESS
            val bodyY = if (isFloor) floorY - 14f else floorY - h * 0.24f
            if (isFloor) {
                drawMat(cx - w * 0.30f, cx + w * 0.28f, floorY, pal.equipPad)
            } else {
                drawFlatBench(cx - w * 0.26f, cx + w * 0.22f, bodyY + 6f, floorY, pal.equipPad, pal.equipMetal)
            }
            val head = Offset(cx - w * 0.18f, bodyY - 4f)
            val shoulder = Offset(cx - w * 0.08f, bodyY)
            val hip = Offset(cx + w * 0.10f, bodyY)
            val knee = Offset(cx + w * 0.19f, if (isFloor) bodyY - h * 0.14f else bodyY + h * 0.04f)
            val foot = Offset(cx + w * 0.22f, floorY)

            val pressY = bodyY - h * 0.12f - p * h * 0.26f
            val elbow = Offset(shoulder.x - w * 0.03f, (shoulder.y + pressY) * 0.5f + 8f)
            val hand = Offset(shoulder.x + w * 0.01f, pressY)

            drawMuscleHighlight(Offset(shoulder.x + 8f, shoulder.y - 6f), 17f, p, FlameOrange)
            drawMotionArrow(Offset(hand.x + w * 0.10f, bodyY - h * 0.12f), Offset(hand.x + w * 0.10f, bodyY - h * 0.38f), ElectricTeal)

            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 9f, LimbRole.CALF)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            if (isFloor) {
                drawDumbbell(hand, WarmGold)
            } else {
                drawBarbell(hand, halfWidth = 28f, plateRadius = 15f, barColor = pal.equipMetal, plateColor = FlameOrange)
            }
        }

        ExerciseMotionKind.BENCH_PRESS_INCLINE -> {
            val seatX = cx + w * 0.05f
            val seatY = floorY - h * 0.16f
            val backTop = Offset(cx - w * 0.14f, floorY - h * 0.42f)
            drawLine(pal.equipMetal, Offset(cx - w * 0.10f, backTop.y + 20f), Offset(cx - w * 0.10f, floorY), 7f, StrokeCap.Round)
            drawLine(pal.equipMetal, Offset(seatX + 10f, seatY), Offset(seatX + 10f, floorY), 7f, StrokeCap.Round)
            drawLine(pal.equipPad, backTop, Offset(seatX, seatY), 13f, StrokeCap.Round)
            drawLine(pal.equipPad, Offset(seatX, seatY), Offset(seatX + w * 0.12f, seatY), 13f, StrokeCap.Round)

            val hip = Offset(seatX - 4f, seatY - 7f)
            val shoulder = Offset(backTop.x + 14f, backTop.y + 8f)
            val head = Offset(backTop.x - 4f, backTop.y - 6f)
            val knee = Offset(seatX + w * 0.12f, seatY - 4f)
            val foot = Offset(seatX + w * 0.14f, floorY)

            val hand = Offset(shoulder.x + w * 0.04f, shoulder.y - h * 0.10f - p * h * 0.25f)
            val elbow = Offset(shoulder.x - w * 0.02f, (shoulder.y + hand.y) * 0.5f + 6f)

            drawMuscleHighlight(Offset(shoulder.x + 10f, shoulder.y - 6f), 17f, p, FlameOrange)
            drawMotionArrow(Offset(hand.x + w * 0.10f, shoulder.y - h * 0.10f), Offset(hand.x + w * 0.10f, shoulder.y - h * 0.35f), ElectricTeal)

            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 9f, LimbRole.CALF)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            drawDumbbell(hand, WarmGold)
        }

        ExerciseMotionKind.SEATED_CHEST_PRESS -> {
            drawLine(pal.equipMetal, Offset(cx - w * 0.18f, h * 0.20f), Offset(cx - w * 0.18f, floorY), 8f, StrokeCap.Round)
            drawLine(pal.equipPad, Offset(cx - w * 0.12f, floorY - h * 0.44f), Offset(cx - w * 0.12f, floorY - h * 0.20f), 12f, StrokeCap.Round)
            drawLine(pal.equipPad, Offset(cx - w * 0.12f, floorY - h * 0.20f), Offset(cx + w * 0.04f, floorY - h * 0.20f), 12f, StrokeCap.Round)

            val hip = Offset(cx - w * 0.08f, floorY - h * 0.24f)
            val shoulder = Offset(cx - w * 0.09f, floorY - h * 0.48f)
            val head = Offset(shoulder.x, shoulder.y - headR * 1.45f)
            val knee = Offset(cx + w * 0.07f, hip.y + 2f)
            val foot = Offset(cx + w * 0.09f, floorY)

            val hand = Offset(shoulder.x + w * 0.08f + p * w * 0.16f, shoulder.y + 4f)
            val elbow = Offset(shoulder.x + w * 0.01f + p * w * 0.07f, shoulder.y + 12f)

            drawMuscleHighlight(Offset(shoulder.x + 12f, shoulder.y + 6f), 16f, p, FlameOrange)
            drawMotionArrow(Offset(shoulder.x + w * 0.08f, shoulder.y - 16f), Offset(shoulder.x + w * 0.24f, shoulder.y - 16f), ElectricTeal)

            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 9f, LimbRole.CALF)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            drawCircle(FlameOrange, 8f, hand)
        }

        ExerciseMotionKind.CHEST_FLY -> {
            drawFlatBench(cx - w * 0.18f, cx + w * 0.18f, floorY - h * 0.14f, floorY, pal.equipPad, pal.equipMetal)
            val head = Offset(cx, floorY - h * 0.56f)
            val shoulderL = Offset(cx - w * 0.07f, floorY - h * 0.43f)
            val shoulderR = Offset(cx + w * 0.07f, floorY - h * 0.43f)
            val hip = Offset(cx, floorY - h * 0.18f)

            val spread = (1f - p) * w * 0.19f + w * 0.04f
            val armLift = p * h * 0.08f
            val handL = Offset(cx - spread, shoulderL.y - armLift)
            val handR = Offset(cx + spread, shoulderR.y - armLift)

            drawMuscleHighlight(Offset(cx, shoulderL.y + 4f), 18f, p, FlameOrange)
            drawMotionArrow(Offset(cx - w * 0.21f, shoulderL.y - 18f), Offset(cx - w * 0.06f, shoulderL.y - 28f), ElectricTeal)
            drawMotionArrow(Offset(cx + w * 0.21f, shoulderR.y - 18f), Offset(cx + w * 0.06f, shoulderR.y - 28f), ElectricTeal)

            drawLimbSegment(hip, Offset(cx - w * 0.08f, floorY), pal.secondaryLimb, pal, 10f)
            drawLimbSegment(hip, Offset(cx + w * 0.08f, floorY), pal.secondaryLimb, pal, 10f)
            drawTorso(hip, Offset(cx, shoulderL.y), pal.torsoFill, 18f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulderL, handL, WarmGold, pal, 9f)
            drawLimbSegment(shoulderR, handR, WarmGold, pal, 9f)
            drawDumbbell(handL, WarmGold, 0.85f)
            drawDumbbell(handR, WarmGold, 0.85f)
        }

        ExerciseMotionKind.OVERHEAD_PRESS -> {
            val hip = Offset(cx, floorY - h * 0.36f)
            val shoulder = Offset(cx, floorY - h * 0.60f)
            val head = Offset(cx, shoulder.y - headR * 1.45f)
            val footL = Offset(cx - w * 0.06f, floorY)
            val footR = Offset(cx + w * 0.06f, floorY)

            val lift = p * h * 0.23f
            val elbowL = Offset(cx - w * 0.12f, shoulder.y - h * 0.03f - lift * 0.6f)
            val elbowR = Offset(cx + w * 0.12f, shoulder.y - h * 0.03f - lift * 0.6f)
            val handL = Offset(cx - w * 0.09f, shoulder.y - h * 0.11f - lift)
            val handR = Offset(cx + w * 0.09f, shoulder.y - h * 0.11f - lift)

            drawMuscleHighlight(Offset(cx - w * 0.06f, shoulder.y), 12f, p, FlameOrange)
            drawMuscleHighlight(Offset(cx + w * 0.06f, shoulder.y), 12f, p, FlameOrange)
            drawMotionArrow(Offset(cx + w * 0.17f, shoulder.y - h * 0.08f), Offset(cx + w * 0.17f, shoulder.y - h * 0.32f), ElectricTeal)

            drawLimbSegment(hip, footL, pal.secondaryLimb, pal, 10f)
            drawLimbSegment(hip, footR, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 17f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbowL, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbowL, handL, WarmGold, pal, 9f)
            drawLimbSegment(shoulder, elbowR, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbowR, handR, WarmGold, pal, 9f)
            drawDumbbell(handL, WarmGold, 0.85f)
            drawDumbbell(handR, WarmGold, 0.85f)
        }

        ExerciseMotionKind.LATERAL_RAISE -> {
            val hip = Offset(cx, floorY - h * 0.36f)
            val shoulder = Offset(cx, floorY - h * 0.60f)
            val head = Offset(cx, shoulder.y - headR * 1.45f)
            val footL = Offset(cx - w * 0.05f, floorY)
            val footR = Offset(cx + w * 0.05f, floorY)

            val angleDeg = 78f - p * 75f
            val armLen = w * 0.19f
            val handL = Offset(cx - w * 0.05f - cos(Math.toRadians(angleDeg.toDouble())).toFloat() * armLen, shoulder.y + sin(Math.toRadians(angleDeg.toDouble())).toFloat() * armLen)
            val handR = Offset(cx + w * 0.05f + cos(Math.toRadians(angleDeg.toDouble())).toFloat() * armLen, shoulder.y + sin(Math.toRadians(angleDeg.toDouble())).toFloat() * armLen)

            drawMuscleHighlight(Offset(cx - w * 0.06f, shoulder.y), 12f, p, FlameOrange)
            drawMuscleHighlight(Offset(cx + w * 0.06f, shoulder.y), 12f, p, FlameOrange)
            drawMotionArrow(Offset(cx + w * 0.14f, shoulder.y + h * 0.18f), Offset(cx + w * 0.24f, shoulder.y - 4f), ElectricTeal)

            drawLimbSegment(hip, footL, pal.secondaryLimb, pal, 10f)
            drawLimbSegment(hip, footR, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 17f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(Offset(cx - w * 0.04f, shoulder.y), handL, WarmGold, pal, 9f)
            drawLimbSegment(Offset(cx + w * 0.04f, shoulder.y), handR, WarmGold, pal, 9f)
            drawDumbbell(handL, WarmGold, 0.8f)
            drawDumbbell(handR, WarmGold, 0.8f)
        }

        ExerciseMotionKind.SKULL_CRUSHER -> {
            val benchY = floorY - h * 0.24f
            drawFlatBench(cx - w * 0.26f, cx + w * 0.22f, benchY + 6f, floorY, pal.equipPad, pal.equipMetal)
            val head = Offset(cx - w * 0.18f, benchY - 4f)
            val shoulder = Offset(cx - w * 0.08f, benchY)
            val hip = Offset(cx + w * 0.10f, benchY)
            val knee = Offset(cx + w * 0.19f, benchY + h * 0.04f)
            val foot = Offset(cx + w * 0.22f, floorY)

            // Upper arm locked vertical, only forearm hinges backward toward forehead
            val elbow = Offset(shoulder.x, shoulder.y - h * 0.22f)
            val arcAngle = -155f + p * 65f // bends back near forehead (-155) to straight up (-90)
            val hand = drawAnglePoint(elbow, h * 0.20f, arcAngle)

            drawMuscleHighlight(Offset(shoulder.x, (shoulder.y + elbow.y) * 0.5f), 13f, p, FlameOrange)
            drawMotionArrow(Offset(elbow.x - w * 0.10f, elbow.y - 4f), Offset(elbow.x - 4f, elbow.y - h * 0.20f), ElectricTeal)

            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 9f, LimbRole.CALF)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            drawBarbell(hand, halfWidth = 20f, plateRadius = 11f, barColor = pal.equipMetal, plateColor = WarmGold)
        }

        ExerciseMotionKind.CABLE_PUSHDOWN -> {
            val towerX = cx + w * 0.20f
            drawLine(pal.equipMetal, Offset(towerX, h * 0.08f), Offset(towerX, floorY), 8f, StrokeCap.Round)
            drawLine(pal.equipMetal, Offset(towerX, h * 0.10f), Offset(cx + w * 0.05f, h * 0.10f), 6f, StrokeCap.Round)

            val hip = Offset(cx - w * 0.08f, floorY - h * 0.36f)
            val shoulder = Offset(cx - w * 0.06f, floorY - h * 0.60f)
            val head = Offset(shoulder.x, shoulder.y - headR * 1.4f)
            val foot = Offset(cx - w * 0.10f, floorY)

            val elbow = Offset(shoulder.x + w * 0.02f, shoulder.y + h * 0.14f)
            val forearmAngle = -30f + p * 85f // bent up (-30) -> pushed straight down (+55)
            val hand = drawAnglePoint(elbow, h * 0.18f, forearmAngle)

            drawLine(ElectricTeal, Offset(cx + w * 0.06f, h * 0.10f), hand, 3f)
            drawMuscleHighlight(Offset(shoulder.x + 3f, (shoulder.y + elbow.y) * 0.5f), 12f, p, FlameOrange)
            drawMotionArrow(Offset(hand.x + 24f, elbow.y - 10f), Offset(hand.x + 24f, elbow.y + h * 0.18f), ElectricTeal)

            drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            drawCircle(FlameOrange, 7f, hand)
        }

        ExerciseMotionKind.DIPS,
        ExerciseMotionKind.CHAIR_DIPS -> {
            val isChair = kind == ExerciseMotionKind.CHAIR_DIPS
            val barY = floorY - h * 0.38f
            if (isChair) {
                drawRoundRect(pal.equipPad, Offset(cx - w * 0.24f, barY), Size(w * 0.16f, 10f), CornerRadius(4f, 4f))
                drawLine(pal.equipMetal, Offset(cx - w * 0.22f, barY + 10f), Offset(cx - w * 0.22f, floorY), 6f)
                drawLine(pal.equipMetal, Offset(cx - w * 0.10f, barY + 10f), Offset(cx - w * 0.10f, floorY), 6f)
                val drop = p * h * 0.15f
                val hand = Offset(cx - w * 0.10f, barY)
                val shoulder = Offset(cx - w * 0.03f, barY - h * 0.12f + drop)
                val hip = Offset(cx + w * 0.04f, barY + drop)
                val foot = Offset(cx + w * 0.24f, floorY)
                val elbow = Offset(shoulder.x - w * 0.06f, shoulder.y + h * 0.04f)
                drawMuscleHighlight(Offset(shoulder.x - 4f, shoulder.y + 12f), 13f, p, FlameOrange)
                drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f)
                drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                drawAthleteHead(Offset(shoulder.x + 4f, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
                drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            } else {
                drawLine(pal.equipMetal, Offset(cx - w * 0.16f, barY), Offset(cx + w * 0.16f, barY), 7f, StrokeCap.Round)
                drawLine(pal.equipMetal, Offset(cx - w * 0.14f, barY), Offset(cx - w * 0.14f, floorY), 7f)
                drawLine(pal.equipMetal, Offset(cx + w * 0.14f, barY), Offset(cx + w * 0.14f, floorY), 7f)
                val drop = p * h * 0.17f
                val hand = Offset(cx + w * 0.04f, barY)
                val shoulder = Offset(cx + w * 0.03f, barY - h * 0.20f + drop)
                val hip = Offset(cx - w * 0.02f, shoulder.y + h * 0.23f)
                val knee = Offset(cx - w * 0.05f, hip.y + h * 0.18f)
                val elbow = Offset(cx - w * 0.04f, shoulder.y + h * 0.08f)
                drawMuscleHighlight(Offset(shoulder.x, shoulder.y + 8f), 15f, p, FlameOrange)
                drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
                drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                drawAthleteHead(Offset(shoulder.x + 2f, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
                drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            }
        }

        ExerciseMotionKind.LAT_PULLDOWN -> {
            val pulleyTop = Offset(cx + w * 0.02f, h * 0.08f)
            drawLine(pal.equipMetal, Offset(cx - w * 0.20f, h * 0.08f), Offset(cx + w * 0.14f, h * 0.08f), 7f, StrokeCap.Round)
            drawLine(pal.equipMetal, Offset(cx + w * 0.14f, h * 0.08f), Offset(cx + w * 0.14f, floorY), 7f)
            drawFlatBench(cx - w * 0.18f, cx + w * 0.06f, floorY - h * 0.22f, floorY, pal.equipPad, pal.equipMetal)

            val hip = Offset(cx - w * 0.06f, floorY - h * 0.25f)
            val shoulder = Offset(cx - w * 0.08f, floorY - h * 0.50f)
            val head = Offset(shoulder.x, shoulder.y - headR * 1.45f)
            val knee = Offset(cx + w * 0.08f, hip.y + 2f)
            val foot = Offset(cx + w * 0.09f, floorY)

            val barY = h * 0.18f + p * (shoulder.y - h * 0.14f)
            val hand = Offset(cx + w * 0.02f, barY)
            val elbow = Offset(shoulder.x + w * 0.08f, (shoulder.y + barY) * 0.5f + p * 16f)

            drawLine(ElectricTeal, pulleyTop, hand, 3.5f)
            drawBarbell(hand, halfWidth = 26f, plateRadius = 4f, barColor = WarmGold, plateColor = WarmGold)
            drawMuscleHighlight(Offset(shoulder.x - 4f, shoulder.y + 16f), 16f, p, MintVital)
            drawMotionArrow(Offset(hand.x + w * 0.15f, h * 0.20f), Offset(hand.x + w * 0.15f, shoulder.y), ElectricTeal)

            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 9f, LimbRole.CALF)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
        }

        ExerciseMotionKind.CABLE_ROW,
        ExerciseMotionKind.FACE_PULL -> {
            val isFacePull = kind == ExerciseMotionKind.FACE_PULL
            val towerX = cx + w * 0.25f
            drawLine(pal.equipMetal, Offset(towerX, h * 0.10f), Offset(towerX, floorY), 8f, StrokeCap.Round)
            if (!isFacePull) {
                drawFlatBench(cx - w * 0.24f, cx + w * 0.06f, floorY - h * 0.16f, floorY, pal.equipPad, pal.equipMetal)
            }
            val hip = if (isFacePull) Offset(cx - w * 0.10f, floorY - h * 0.36f) else Offset(cx - w * 0.12f, floorY - h * 0.20f)
            val shoulder = Offset(hip.x - p * w * 0.02f, hip.y - h * 0.25f)
            val head = Offset(shoulder.x, shoulder.y - headR * 1.4f)
            val foot = if (isFacePull) Offset(cx - w * 0.12f, floorY) else Offset(cx + w * 0.10f, floorY - h * 0.18f)

            val pulleyOrigin = Offset(towerX, if (isFacePull) shoulder.y - 8f else hip.y - 12f)
            val hand = Offset(shoulder.x + w * 0.20f - p * w * 0.14f, if (isFacePull) head.y + 4f else hip.y - h * 0.09f)
            val elbow = Offset(shoulder.x + w * 0.04f - p * w * 0.10f, hand.y + 4f)

            drawLine(ElectricTeal, pulleyOrigin, hand, 3.5f)
            drawMuscleHighlight(Offset(shoulder.x - 4f, shoulder.y + 10f), 15f, p, MintVital)
            drawMotionArrow(Offset(hand.x + 14f, hand.y - 22f), Offset(shoulder.x + 4f, hand.y - 22f), ElectricTeal)

            drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            drawCircle(FlameOrange, 7f, hand)
        }

        ExerciseMotionKind.BENT_OVER_ROW -> {
            val hip = Offset(cx - w * 0.08f, floorY - h * 0.35f)
            val shoulder = Offset(cx + w * 0.10f, floorY - h * 0.48f)
            val head = Offset(shoulder.x + w * 0.05f, shoulder.y - headR * 1.15f)
            val knee = Offset(cx - w * 0.04f, floorY - h * 0.18f)
            val foot = Offset(cx - w * 0.06f, floorY)

            val pullY = shoulder.y + h * 0.28f - p * h * 0.20f
            val hand = Offset(shoulder.x + w * 0.02f, pullY)
            val elbow = Offset(shoulder.x - w * 0.05f, shoulder.y + h * 0.06f - p * h * 0.10f)

            drawMuscleHighlight(Offset(shoulder.x - 10f, shoulder.y + 10f), 16f, p, MintVital)
            drawMotionArrow(Offset(hand.x + w * 0.12f, shoulder.y + h * 0.26f), Offset(hand.x + w * 0.12f, shoulder.y + h * 0.06f), ElectricTeal)

            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 9f, LimbRole.CALF)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            drawBarbell(hand, halfWidth = 24f, plateRadius = 13f, barColor = pal.equipMetal, plateColor = FlameOrange)
        }

        ExerciseMotionKind.SHRUGS -> {
            val hip = Offset(cx, floorY - h * 0.36f)
            val lift = p * h * 0.06f
            val shoulder = Offset(cx, floorY - h * 0.60f - lift)
            val head = Offset(cx, floorY - h * 0.60f - headR * 1.45f)
            val footL = Offset(cx - w * 0.05f, floorY)
            val footR = Offset(cx + w * 0.05f, floorY)
            val handL = Offset(cx - w * 0.09f, hip.y + h * 0.06f - lift)
            val handR = Offset(cx + w * 0.09f, hip.y + h * 0.06f - lift)

            drawMuscleHighlight(Offset(cx, shoulder.y - 4f), 15f, p, FlameOrange)
            drawMotionArrow(Offset(cx + w * 0.16f, shoulder.y + 18f), Offset(cx + w * 0.16f, shoulder.y - 16f), ElectricTeal)

            drawLimbSegment(hip, footL, pal.secondaryLimb, pal, 10f)
            drawLimbSegment(hip, footR, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 17f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(Offset(cx - w * 0.05f, shoulder.y), handL, WarmGold, pal, 9f)
            drawLimbSegment(Offset(cx + w * 0.05f, shoulder.y), handR, WarmGold, pal, 9f)
            drawDumbbell(handL, WarmGold, 0.85f)
            drawDumbbell(handR, WarmGold, 0.85f)
        }

        ExerciseMotionKind.PULL_UP,
        ExerciseMotionKind.DEAD_HANG -> {
            val barY = h * 0.13f
            drawLine(pal.equipMetal, Offset(w * 0.24f, barY), Offset(w * 0.76f, barY), 7f, StrokeCap.Round)
            val isDeadHang = kind == ExerciseMotionKind.DEAD_HANG
            val pullOffset = if (isDeadHang) p * h * 0.025f else p * h * 0.18f

            val shoulder = Offset(cx, barY + h * 0.26f - pullOffset)
            val head = Offset(cx, shoulder.y - headR * 1.35f)
            val hip = Offset(cx, shoulder.y + h * 0.22f)
            val knee = Offset(cx - w * 0.03f, hip.y + h * 0.17f)
            val foot = Offset(cx - w * 0.09f, knee.y + h * 0.12f)
            val handL = Offset(cx - w * 0.09f, barY)
            val handR = Offset(cx + w * 0.09f, barY)

            if (isDeadHang) {
                drawMuscleHighlight(Offset(cx, (shoulder.y + hip.y) * 0.5f), 18f, p, ElectricTeal)
                drawMotionArrow(Offset(cx + w * 0.14f, shoulder.y), Offset(cx + w * 0.14f, hip.y + 10f), MintVital)
            } else {
                drawMuscleHighlight(Offset(cx, shoulder.y + 12f), 16f, p, FlameOrange)
                drawMotionArrow(Offset(cx + w * 0.16f, barY + h * 0.26f), Offset(cx + w * 0.16f, barY + h * 0.08f), ElectricTeal)
            }

            drawLimbSegment(handL, shoulder, WarmGold, pal, 9f)
            drawLimbSegment(handR, shoulder, WarmGold, pal, 9f)
            drawTorso(hip, shoulder, pal.torsoFill, 17f, pal)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 9f, LimbRole.CALF)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
        }

        ExerciseMotionKind.AUSTRALIAN_ROW -> {
            val barPos = Offset(cx + w * 0.12f, floorY - h * 0.44f)
            drawLine(pal.equipMetal, Offset(barPos.x, barPos.y), Offset(barPos.x, floorY), 7f, StrokeCap.Round)
            drawCircle(WarmGold, 7f, barPos)

            val foot = Offset(cx - w * 0.22f, floorY - 4f)
            val lift = p * h * 0.16f
            val shoulder = Offset(cx + w * 0.10f, floorY - h * 0.28f - lift)
            val hip = Offset((foot.x + shoulder.x) * 0.5f, (foot.y + shoulder.y) * 0.5f)
            val head = Offset(shoulder.x + w * 0.05f, shoulder.y - headR * 1.15f)
            val elbow = Offset(shoulder.x - w * 0.06f, shoulder.y + h * 0.06f)

            drawMuscleHighlight(Offset(shoulder.x - 8f, shoulder.y + 6f), 15f, p, MintVital)
            drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, barPos, WarmGold, pal, 9f)
        }

        ExerciseMotionKind.BICEPS_CURL,
        ExerciseMotionKind.INCLINE_CURL -> {
            val isIncline = kind == ExerciseMotionKind.INCLINE_CURL
            if (isIncline) {
                drawLine(pal.equipPad, Offset(cx - w * 0.12f, floorY - h * 0.52f), Offset(cx - w * 0.02f, floorY - h * 0.18f), 12f, StrokeCap.Round)
                drawLine(pal.equipMetal, Offset(cx - w * 0.10f, floorY - h * 0.42f), Offset(cx - w * 0.10f, floorY), 6f)
            }
            val hip = if (isIncline) Offset(cx - w * 0.02f, floorY - h * 0.22f) else Offset(cx - w * 0.04f, floorY - h * 0.36f)
            val shoulder = if (isIncline) Offset(cx - w * 0.09f, floorY - h * 0.46f) else Offset(cx - w * 0.04f, floorY - h * 0.60f)
            val head = Offset(shoulder.x, shoulder.y - headR * 1.45f)
            val foot = Offset(cx + if (isIncline) w * 0.08f else -w * 0.04f, floorY)

            val elbow = Offset(shoulder.x + w * 0.02f, shoulder.y + h * 0.16f)
            val angleDeg = 82f - p * 130f // hanging down (+82°) -> curled up (-48°)
            val hand = drawAnglePoint(elbow, h * 0.16f, angleDeg)

            drawMuscleHighlight(Offset(shoulder.x + 4f, (shoulder.y + elbow.y) * 0.5f), 13f, p, FlameOrange)
            drawMotionArrow(Offset(elbow.x + w * 0.14f, elbow.y + h * 0.10f), Offset(elbow.x + w * 0.12f, elbow.y - h * 0.12f), ElectricTeal)

            drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
            drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            drawDumbbell(hand, WarmGold)
        }

        ExerciseMotionKind.PUSHUP_STANDARD,
        ExerciseMotionKind.PUSHUP_DECLINE,
        ExerciseMotionKind.PUSHUP_INCLINE,
        ExerciseMotionKind.PIKE_PUSHUP -> {
            drawMat(cx - w * 0.32f, cx + w * 0.30f, floorY, pal.equipPad)
            when (kind) {
                ExerciseMotionKind.PUSHUP_DECLINE -> {
                    // Elevated box under feet
                    drawRoundRect(pal.equipPad, Offset(cx - w * 0.32f, floorY - h * 0.18f), Size(w * 0.12f, h * 0.18f), CornerRadius(5f, 5f))
                    val dip = p * h * 0.14f
                    val foot = Offset(cx - w * 0.24f, floorY - h * 0.18f)
                    val hand = Offset(cx + w * 0.20f, floorY - 6f)
                    val shoulder = Offset(cx + w * 0.17f, floorY - h * 0.24f + dip)
                    val hip = Offset((foot.x + shoulder.x) * 0.5f, (foot.y + shoulder.y) * 0.5f)
                    val elbow = Offset(shoulder.x - w * 0.03f, (shoulder.y + hand.y) * 0.5f)
                    drawMuscleHighlight(Offset(shoulder.x - 4f, shoulder.y + 8f), 15f, p, FlameOrange)
                    drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
                    drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                    drawAthleteHead(Offset(shoulder.x + w * 0.05f, shoulder.y - headR * 0.9f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                    drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
                    drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
                }
                ExerciseMotionKind.PUSHUP_INCLINE -> {
                    val benchTop = floorY - h * 0.22f
                    drawRoundRect(pal.equipPad, Offset(cx + w * 0.14f, benchTop), Size(w * 0.15f, h * 0.22f), CornerRadius(6f, 6f))
                    val dip = p * h * 0.12f
                    val foot = Offset(cx - w * 0.24f, floorY - 6f)
                    val hand = Offset(cx + w * 0.18f, benchTop)
                    val shoulder = Offset(cx + w * 0.13f, benchTop - h * 0.18f + dip)
                    val hip = Offset((foot.x + shoulder.x) * 0.5f, (foot.y + shoulder.y) * 0.5f)
                    val elbow = Offset(shoulder.x - w * 0.02f, (shoulder.y + hand.y) * 0.5f)
                    drawMuscleHighlight(Offset(shoulder.x - 4f, shoulder.y + 6f), 14f, p, FlameOrange)
                    drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
                    drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                    drawAthleteHead(Offset(shoulder.x + w * 0.05f, shoulder.y - headR * 0.9f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                    drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
                    drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
                }
                ExerciseMotionKind.PIKE_PUSHUP -> {
                    val dip = p * h * 0.14f
                    val foot = Offset(cx - w * 0.22f, floorY - 6f)
                    val hip = Offset(cx - w * 0.02f, floorY - h * 0.48f)
                    val hand = Offset(cx + w * 0.18f, floorY - 6f)
                    val shoulder = Offset(cx + w * 0.12f, floorY - h * 0.26f + dip)
                    val head = Offset(shoulder.x + w * 0.04f, shoulder.y + headR * 1.1f)
                    drawMuscleHighlight(shoulder, 15f, p, FlameOrange)
                    drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
                    drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                    drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                    drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
                }
                else -> {
                    val dip = p * h * 0.16f
                    val foot = Offset(cx - w * 0.24f, floorY - 8f)
                    val hand = Offset(cx + w * 0.20f, floorY - 6f)
                    val shoulder = Offset(cx + w * 0.17f, floorY - h * 0.26f + dip)
                    val hip = Offset((foot.x + shoulder.x) * 0.5f, (foot.y + shoulder.y) * 0.5f)
                    val elbow = Offset(shoulder.x - w * 0.03f, (shoulder.y + hand.y) * 0.5f)
                    drawMuscleHighlight(Offset(shoulder.x - 4f, shoulder.y + 8f), 15f, p, FlameOrange)
                    drawMotionArrow(Offset(cx + w * 0.28f, floorY - h * 0.26f), Offset(cx + w * 0.28f, floorY - h * 0.10f), ElectricTeal)
                    drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
                    drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                    drawAthleteHead(Offset(shoulder.x + w * 0.05f, shoulder.y - headR * 0.9f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                    drawLimbSegment(shoulder, elbow, pal.primaryLimb, pal, 9f)
                    drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
                }
            }
        }

        else -> {}
    }
}
