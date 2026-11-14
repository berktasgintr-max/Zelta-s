package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.MintVital
import com.example.ui.theme.WarmGold
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.drawLowerCoreCardioMotion(
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
        ExerciseMotionKind.BARBELL_SQUAT,
        ExerciseMotionKind.GOBLET_SQUAT,
        ExerciseMotionKind.BODYWEIGHT_SQUAT -> {
            val drop = p * h * 0.21f
            val foot = Offset(cx - w * 0.02f, floorY)
            val knee = Offset(cx + w * 0.08f, floorY - h * 0.19f + drop * 0.3f)
            val hip = Offset(cx - w * 0.06f - p * w * 0.04f, floorY - h * 0.37f + drop)
            val shoulder = Offset(hip.x + w * 0.05f + p * w * 0.03f, hip.y - h * 0.23f)
            val head = Offset(shoulder.x + 2f, shoulder.y - headR * 1.4f)

            drawMuscleHighlight(Offset((hip.x + knee.x) * 0.5f, (hip.y + knee.y) * 0.5f), 15f, p, FlameOrange)
            drawMotionArrow(Offset(cx - w * 0.20f, floorY - h * 0.58f), Offset(cx - w * 0.20f, floorY - h * 0.36f), ElectricTeal)

            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 10f, LimbRole.CALF)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 11f, LimbRole.THIGH)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)

            when (kind) {
                ExerciseMotionKind.BARBELL_SQUAT -> {
                    val hand = Offset(shoulder.x - 4f, shoulder.y - 4f)
                    drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
                    drawBarbell(hand, halfWidth = 30f, plateRadius = 15f, barColor = pal.equipMetal, plateColor = FlameOrange)
                }
                ExerciseMotionKind.GOBLET_SQUAT -> {
                    val hand = Offset(shoulder.x + w * 0.08f, shoulder.y + h * 0.07f)
                    drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
                    drawDumbbell(hand, WarmGold)
                }
                else -> {
                    val hand = Offset(shoulder.x + w * 0.14f, shoulder.y + h * 0.03f - p * h * 0.05f)
                    drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
                }
            }
        }

        ExerciseMotionKind.DEADLIFT_HINGE -> {
            // p=0 hinged down grabbing barbell near shins, p=1 stood tall locked out
            val foot = Offset(cx, floorY)
            val knee = Offset(cx + (1f - p) * w * 0.04f, floorY - h * 0.19f)
            val hip = Offset(cx - (1f - p) * w * 0.08f, floorY - h * 0.37f + (1f - p) * h * 0.05f)
            val shoulder = Offset(cx + (1f - p) * w * 0.11f, hip.y - h * 0.24f + (1f - p) * h * 0.12f)
            val head = Offset(shoulder.x + (1f - p) * 10f, shoulder.y - headR * 1.35f)
            val barPos = Offset(cx + w * 0.05f, floorY - h * 0.13f - p * h * 0.21f)

            drawMuscleHighlight(Offset(hip.x - 6f, hip.y + 4f), 16f, p, FlameOrange)
            drawMotionArrow(Offset(cx + w * 0.22f, floorY - h * 0.13f), Offset(cx + w * 0.22f, floorY - h * 0.35f), ElectricTeal)

            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 10f, LimbRole.CALF)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 11f, LimbRole.THIGH)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, barPos, WarmGold, pal, 9f)
            drawBarbell(barPos, halfWidth = 30f, plateRadius = 15f, barColor = pal.equipMetal, plateColor = FlameOrange)
        }

        ExerciseMotionKind.LEG_PRESS -> {
            drawLine(pal.equipMetal, Offset(cx - w * 0.22f, floorY - h * 0.12f), Offset(cx + w * 0.24f, floorY - h * 0.56f), 8f, StrokeCap.Round)
            val hip = Offset(cx - w * 0.14f, floorY - h * 0.16f)
            val shoulder = Offset(cx - w * 0.24f, floorY - h * 0.36f)
            val head = Offset(shoulder.x - 6f, shoulder.y - headR * 1.2f)

            val pushDist = p * w * 0.13f
            val foot = Offset(cx + w * 0.05f + pushDist, floorY - h * 0.34f - pushDist * 0.7f)
            val knee = Offset(cx - w * 0.06f + pushDist * 0.6f, floorY - h * 0.34f - pushDist * 0.25f)

            drawLine(pal.equipPad, Offset(foot.x - 10f, foot.y + 16f), Offset(foot.x + 12f, foot.y - 16f), 11f, StrokeCap.Round)
            drawMuscleHighlight(Offset((hip.x + knee.x) * 0.5f, (hip.y + knee.y) * 0.5f), 15f, p, FlameOrange)

            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 11f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 10f, LimbRole.CALF)
        }

        ExerciseMotionKind.LEG_EXTENSION,
        ExerciseMotionKind.LYING_LEG_CURL -> {
            if (kind == ExerciseMotionKind.LEG_EXTENSION) {
                drawFlatBench(cx - w * 0.18f, cx + w * 0.06f, floorY - h * 0.22f, floorY, pal.equipPad, pal.equipMetal)
                val hip = Offset(cx - w * 0.08f, floorY - h * 0.25f)
                val shoulder = Offset(cx - w * 0.11f, floorY - h * 0.50f)
                val knee = Offset(cx + w * 0.06f, hip.y)
                val angleDeg = 85f - p * 80f // vertical down (+85) -> kicked horizontal (+5)
                val foot = drawAnglePoint(knee, h * 0.21f, angleDeg)
                drawMuscleHighlight(Offset((hip.x + knee.x) * 0.5f, hip.y - 4f), 14f, p, FlameOrange)
                drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                drawAthleteHead(Offset(shoulder.x, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 11f, LimbRole.THIGH)
                drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 10f, LimbRole.CALF)
            } else {
                drawFlatBench(cx - w * 0.24f, cx + w * 0.18f, floorY - h * 0.20f, floorY, pal.equipPad, pal.equipMetal)
                val head = Offset(cx - w * 0.18f, floorY - h * 0.28f)
                val shoulder = Offset(cx - w * 0.10f, floorY - h * 0.23f)
                val knee = Offset(cx + w * 0.14f, floorY - h * 0.23f)
                val angleDeg = -5f - p * 95f // extended (-5) -> curled to glutes (-100)
                val foot = drawAnglePoint(knee, h * 0.20f, angleDeg)
                drawMuscleHighlight(Offset((shoulder.x + knee.x) * 0.65f, knee.y - 6f), 15f, p, FlameOrange)
                drawTorso(knee, shoulder, pal.torsoFill, 15f, pal)
                drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 10f, LimbRole.CALF)
            }
        }

        ExerciseMotionKind.HIP_THRUST,
        ExerciseMotionKind.GLUTE_BRIDGE -> {
            val isThrust = kind == ExerciseMotionKind.HIP_THRUST
            if (isThrust) {
                drawRoundRect(pal.equipPad, Offset(cx - w * 0.28f, floorY - h * 0.26f), Size(w * 0.14f, h * 0.26f), CornerRadius(6f, 6f))
            } else {
                drawMat(cx - w * 0.30f, cx + w * 0.28f, floorY, pal.equipPad)
            }
            val shoulder = if (isThrust) Offset(cx - w * 0.14f, floorY - h * 0.26f) else Offset(cx - w * 0.16f, floorY - 12f)
            val head = Offset(shoulder.x - w * 0.08f, shoulder.y - 4f)
            val lift = p * h * 0.18f
            val hip = Offset(cx + w * 0.02f, floorY - 12f - lift)
            val knee = Offset(cx + w * 0.14f, floorY - h * 0.22f - lift * 0.25f)
            val foot = Offset(cx + w * 0.18f, floorY - 6f)

            drawMuscleHighlight(hip, 16f, p, FlameOrange)
            drawMotionArrow(Offset(hip.x, floorY - h * 0.16f), Offset(hip.x, floorY - h * 0.36f), ElectricTeal)

            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 11f, LimbRole.THIGH)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 10f, LimbRole.CALF)
            if (isThrust) {
                drawBarbell(hip, halfWidth = 22f, plateRadius = 14f, barColor = pal.equipMetal, plateColor = WarmGold)
            }
        }

        ExerciseMotionKind.CALF_RAISE -> {
            // Step block
            drawRoundRect(pal.equipPad, Offset(cx - w * 0.10f, floorY - h * 0.07f), Size(w * 0.18f, h * 0.07f), CornerRadius(4f, 4f))
            val raise = p * h * 0.08f
            val toes = Offset(cx + w * 0.03f, floorY - h * 0.07f)
            val ankle = Offset(cx - w * 0.02f, floorY - h * 0.07f - raise)
            val hip = Offset(cx - w * 0.02f, ankle.y - h * 0.36f)
            val shoulder = Offset(cx - w * 0.02f, hip.y - h * 0.22f)
            val head = Offset(shoulder.x, shoulder.y - headR * 1.4f)

            drawMuscleHighlight(Offset(ankle.x - 4f, (ankle.y + hip.y) * 0.65f), 13f, p, FlameOrange)
            drawMotionArrow(Offset(cx + w * 0.14f, floorY - h * 0.08f), Offset(cx + w * 0.14f, floorY - h * 0.22f), ElectricTeal)

            drawLimbSegment(hip, ankle, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
        }

        ExerciseMotionKind.LUNGE_SPLIT,
        ExerciseMotionKind.BULGARIAN_SPLIT_SQUAT -> {
            val isBulgarian = kind == ExerciseMotionKind.BULGARIAN_SPLIT_SQUAT
            if (isBulgarian) {
                drawRoundRect(pal.equipPad, Offset(cx - w * 0.30f, floorY - h * 0.20f), Size(w * 0.13f, h * 0.20f), CornerRadius(5f, 5f))
            }
            val drop = p * h * 0.17f
            val hip = Offset(cx - w * 0.01f, floorY - h * 0.35f + drop)
            val shoulder = Offset(hip.x + w * 0.02f, hip.y - h * 0.23f)
            val head = Offset(shoulder.x, shoulder.y - headR * 1.4f)

            // Front leg bent 90°
            val frontFoot = Offset(cx + w * 0.15f, floorY)
            val frontKnee = Offset(cx + w * 0.14f, floorY - h * 0.19f + drop * 0.2f)
            // Back leg dropping toward floor (or on bench for Bulgarian)
            val backFoot = Offset(cx - w * 0.20f, if (isBulgarian) floorY - h * 0.20f else floorY)
            val backKnee = Offset(cx - w * 0.11f, floorY - h * 0.17f + drop * 0.65f)

            drawMuscleHighlight(Offset((hip.x + frontKnee.x) * 0.5f, (hip.y + frontKnee.y) * 0.5f), 14f, p, FlameOrange)
            drawLimbSegment(backKnee, backFoot, pal.secondaryLimb.copy(alpha = 0.65f), pal, 9f, LimbRole.CALF)
            drawLimbSegment(hip, backKnee, pal.secondaryLimb.copy(alpha = 0.65f), pal, 10f, LimbRole.THIGH)
            drawLimbSegment(frontKnee, frontFoot, pal.secondaryLimb, pal, 10f, LimbRole.CALF)
            drawLimbSegment(hip, frontKnee, pal.secondaryLimb, pal, 11f, LimbRole.THIGH)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, Offset(shoulder.x + 4f, hip.y + 8f), WarmGold, pal, 8f)
            drawDumbbell(Offset(shoulder.x + 4f, hip.y + 8f), WarmGold, 0.75f)
        }

        ExerciseMotionKind.PLANK_HOLD,
        ExerciseMotionKind.PLANK_DYNAMIC,
        ExerciseMotionKind.SIDE_PLANK -> {
            drawMat(cx - w * 0.32f, cx + w * 0.30f, floorY, pal.equipPad)
            val foot = Offset(cx - w * 0.25f, floorY - 6f)
            val hand = Offset(cx + w * 0.20f, floorY - 6f)
            val bounce = if (kind == ExerciseMotionKind.PLANK_DYNAMIC) sin(p * 6.283f) * h * 0.05f else 0f
            val shoulder = Offset(cx + w * 0.18f, floorY - h * 0.24f - bounce)
            val hip = Offset(cx - w * 0.03f, floorY - h * 0.22f - bounce * 0.4f)
            val head = Offset(shoulder.x + w * 0.06f, shoulder.y - headR * 0.7f)

            drawMuscleHighlight(hip, 16f, p, if (kind == ExerciseMotionKind.SIDE_PLANK) ElectricTeal else FlameOrange)
            drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            if (kind == ExerciseMotionKind.PLANK_DYNAMIC) {
                // Alternating shoulder tap motion
                val tapHand = Offset(shoulder.x + (1f - p) * 8f, if (p > 0.5f) shoulder.y + 4f else hand.y)
                drawLimbSegment(shoulder, tapHand, WarmGold, pal, 9f)
            } else {
                val elbow = Offset(shoulder.x + w * 0.01f, floorY - 8f)
                drawLimbSegment(shoulder, elbow, WarmGold, pal, 9f)
                drawLimbSegment(elbow, hand, WarmGold, pal, 9f)
            }
        }

        ExerciseMotionKind.HANGING_KNEE_RAISE -> {
            val barY = h * 0.12f
            drawLine(pal.equipMetal, Offset(w * 0.26f, barY), Offset(w * 0.74f, barY), 7f, StrokeCap.Round)
            val hand = Offset(cx, barY)
            val shoulder = Offset(cx, barY + h * 0.22f)
            val hip = Offset(cx, shoulder.y + h * 0.22f)
            val kneeLift = p * h * 0.21f
            val knee = Offset(cx + w * 0.08f + p * w * 0.06f, hip.y + h * 0.18f - kneeLift)
            val foot = Offset(knee.x - w * 0.03f, knee.y + h * 0.14f)
            drawMuscleHighlight(Offset(cx + 6f, hip.y - 6f), 15f, p, FlameOrange)
            drawLimbSegment(hand, shoulder, WarmGold, pal, 9f)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 9f)
            drawAthleteHead(Offset(cx, shoulder.y - headR * 1.2f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
        }

        ExerciseMotionKind.CABLE_CRUNCH -> {
            val towerX = cx + w * 0.22f
            drawLine(pal.equipMetal, Offset(towerX, h * 0.10f), Offset(towerX, floorY), 8f)
            val knee = Offset(cx - w * 0.06f, floorY - 6f)
            val hip = Offset(cx - w * 0.10f, floorY - h * 0.24f)
            val shoulder = Offset(hip.x + w * 0.08f + p * w * 0.07f, hip.y - h * 0.23f + p * h * 0.14f)
            val head = Offset(shoulder.x + 8f, shoulder.y - headR * 1.1f)
            val hand = Offset(shoulder.x + 12f, shoulder.y + 4f)
            drawLine(ElectricTeal, Offset(towerX, h * 0.12f), hand, 3.5f)
            drawMuscleHighlight(Offset((hip.x + shoulder.x) * 0.5f, (hip.y + shoulder.y) * 0.5f), 15f, p, FlameOrange)
            drawLimbSegment(knee, Offset(cx - w * 0.20f, floorY - 6f), pal.secondaryLimb, pal, 10f, LimbRole.CALF)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
        }

        ExerciseMotionKind.BICYCLE_CRUNCH,
        ExerciseMotionKind.LEG_RAISE,
        ExerciseMotionKind.V_UP,
        ExerciseMotionKind.HOLLOW_HOLD,
        ExerciseMotionKind.DEAD_BUG -> {
            drawMat(cx - w * 0.32f, cx + w * 0.30f, floorY, pal.equipPad)
            val hip = Offset(cx, floorY - 14f)
            val torsoLift = when (kind) {
                ExerciseMotionKind.V_UP -> p * h * 0.26f
                ExerciseMotionKind.HOLLOW_HOLD -> h * 0.10f + p * h * 0.03f
                ExerciseMotionKind.BICYCLE_CRUNCH -> h * 0.12f
                else -> 4f
            }
            val shoulder = Offset(cx - w * 0.19f + torsoLift * 0.25f, floorY - 14f - torsoLift)
            val head = Offset(shoulder.x - 10f, shoulder.y - headR * 0.9f)

            val legAngleDeg = when (kind) {
                ExerciseMotionKind.LEG_RAISE -> -10f - p * 75f
                ExerciseMotionKind.V_UP -> -8f - p * 60f
                ExerciseMotionKind.HOLLOW_HOLD -> -15f - p * 6f
                ExerciseMotionKind.BICYCLE_CRUNCH -> -20f - sin(p * 6.283f) * 35f
                ExerciseMotionKind.DEAD_BUG -> -75f + p * 60f
                else -> -25f
            }
            val foot = drawAnglePoint(hip, w * 0.24f, legAngleDeg)
            drawMuscleHighlight(Offset(hip.x - 8f, hip.y - 10f), 16f, p, FlameOrange)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(head, headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f)
            val armTarget = if (kind == ExerciseMotionKind.DEAD_BUG) {
                Offset(shoulder.x - p * w * 0.12f, shoulder.y - h * 0.18f)
            } else {
                Offset(shoulder.x + p * w * 0.12f, shoulder.y - h * 0.12f)
            }
            drawLimbSegment(shoulder, armTarget, WarmGold, pal, 9f)
        }

        ExerciseMotionKind.BIRD_DOG -> {
            drawMat(cx - w * 0.32f, cx + w * 0.30f, floorY, pal.equipPad)
            val hip = Offset(cx - w * 0.10f, floorY - h * 0.24f)
            val shoulder = Offset(cx + w * 0.12f, floorY - h * 0.26f)
            val knee = Offset(cx - w * 0.11f, floorY - 6f)
            val hand = Offset(cx + w * 0.13f, floorY - 6f)
            val extLeg = Offset(hip.x - w * 0.18f * p, hip.y - 4f * p)
            val extArm = Offset(shoulder.x + w * 0.18f * p, shoulder.y - 6f * p)
            drawMuscleHighlight(Offset((hip.x + shoulder.x) * 0.5f, hip.y), 14f, p, MintVital)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 9f, LimbRole.THIGH)
            drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
            drawLimbSegment(hip, extLeg, pal.secondaryLimb, pal, 9f, LimbRole.FULL_LEG)
            drawLimbSegment(shoulder, extArm, WarmGold, pal, 9f)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawAthleteHead(Offset(shoulder.x + 12f, shoulder.y - headR), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
        }

        ExerciseMotionKind.L_SIT_HOLD -> {
            drawMat(cx - w * 0.28f, cx + w * 0.28f, floorY, pal.equipPad)
            val hand = Offset(cx, floorY - 6f)
            val hover = p * 5f
            val hip = Offset(cx - w * 0.04f, floorY - h * 0.22f - hover)
            val shoulder = Offset(cx - w * 0.04f, hip.y - h * 0.24f)
            val knee = Offset(cx + w * 0.12f, hip.y - h * 0.05f)
            drawMuscleHighlight(hip, 16f, p, FlameOrange)
            drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f)
            drawAthleteHead(Offset(shoulder.x, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
        }

        ExerciseMotionKind.SUPERMAN_HOLD -> {
            drawMat(cx - w * 0.34f, cx + w * 0.34f, floorY, pal.equipPad)
            val lift = p * h * 0.13f
            val hip = Offset(cx, floorY - 12f)
            val shoulder = Offset(cx + w * 0.15f, floorY - 16f - lift)
            val hand = Offset(shoulder.x + w * 0.14f, shoulder.y - lift * 0.6f)
            val foot = Offset(cx - w * 0.22f, floorY - 12f - lift)
            drawMuscleHighlight(Offset((hip.x + shoulder.x) * 0.5f, hip.y - 6f), 15f, p, MintVital)
            drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
            drawAthleteHead(Offset(shoulder.x + 8f, shoulder.y - headR), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
        }

        ExerciseMotionKind.BOX_JUMP,
        ExerciseMotionKind.SQUAT_JUMP,
        ExerciseMotionKind.JUMPING_JACKS,
        ExerciseMotionKind.BURPEE,
        ExerciseMotionKind.JUMP_ROPE,
        ExerciseMotionKind.SPEED_SKATERS -> {
            when (kind) {
                ExerciseMotionKind.BOX_JUMP -> {
                    val boxTop = floorY - h * 0.24f
                    drawRoundRect(pal.equipPad, Offset(cx + w * 0.08f, boxTop), Size(w * 0.20f, h * 0.24f), CornerRadius(6f, 6f))
                    val arcX = cx - w * 0.14f + p * w * 0.30f
                    val arcY = floorY - sin(p * 3.14159f) * h * 0.34f - p * h * 0.24f
                    val hip = Offset(arcX, arcY - h * 0.28f)
                    val shoulder = Offset(arcX + 6f, hip.y - h * 0.22f)
                    val foot = Offset(arcX - 4f, arcY)
                    drawMotionArrow(Offset(cx - w * 0.14f, floorY - h * 0.10f), Offset(cx + w * 0.18f, boxTop - h * 0.08f), ElectricTeal)
                    drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f)
                    drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
                    drawLimbSegment(shoulder, Offset(shoulder.x + 14f, shoulder.y - 14f), WarmGold, pal, 9f)
                    drawAthleteHead(Offset(shoulder.x, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                }
                ExerciseMotionKind.JUMPING_JACKS -> {
                    val jumpY = p * h * 0.11f
                    val spread = p * w * 0.12f + w * 0.03f
                    val hip = Offset(cx, floorY - h * 0.35f - jumpY)
                    val shoulder = Offset(cx, hip.y - h * 0.23f)
                    val footL = Offset(cx - spread, floorY - jumpY)
                    val footR = Offset(cx + spread, floorY - jumpY)
                    val handL = Offset(cx - w * 0.05f - p * w * 0.12f, shoulder.y + h * 0.15f - p * h * 0.34f)
                    val handR = Offset(cx + w * 0.05f + p * w * 0.12f, shoulder.y + h * 0.15f - p * h * 0.34f)
                    drawLimbSegment(hip, footL, pal.secondaryLimb, pal, 10f)
                    drawLimbSegment(hip, footR, pal.secondaryLimb, pal, 10f)
                    drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
                    drawLimbSegment(shoulder, handL, WarmGold, pal, 9f)
                    drawLimbSegment(shoulder, handR, WarmGold, pal, 9f)
                    drawAthleteHead(Offset(cx, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                }
                ExerciseMotionKind.BURPEE -> {
                    drawMat(cx - w * 0.32f, cx + w * 0.30f, floorY, pal.equipPad)
                    if (p < 0.5f) {
                        val t = p * 2f
                        val foot = Offset(cx - w * 0.24f, floorY - 6f)
                        val shoulder = Offset(cx + w * 0.16f, floorY - h * 0.24f + t * 8f)
                        val hip = Offset((foot.x + shoulder.x) * 0.5f, (foot.y + shoulder.y) * 0.5f)
                        drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
                        drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                        drawLimbSegment(shoulder, Offset(cx + w * 0.18f, floorY - 6f), WarmGold, pal, 9f)
                        drawAthleteHead(Offset(shoulder.x + 8f, shoulder.y - headR), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                    } else {
                        val jumpY = (p - 0.5f) * 2f * h * 0.20f
                        val hip = Offset(cx, floorY - h * 0.34f - jumpY)
                        val shoulder = Offset(cx, hip.y - h * 0.22f)
                        drawLimbSegment(hip, Offset(cx - 8f, floorY - jumpY), pal.secondaryLimb, pal, 10f)
                        drawLimbSegment(hip, Offset(cx + 8f, floorY - jumpY), pal.secondaryLimb, pal, 10f)
                        drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
                        drawLimbSegment(shoulder, Offset(cx + 12f, shoulder.y - h * 0.15f), WarmGold, pal, 9f)
                        drawAthleteHead(Offset(cx, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                    }
                }
                else -> {
                    val bounce = abs(sin(p * 3.14159f)) * h * 0.16f
                    val hip = Offset(cx, floorY - h * 0.35f - bounce)
                    val shoulder = Offset(cx, hip.y - h * 0.23f)
                    val footL = Offset(cx - w * 0.05f, floorY - bounce)
                    val footR = Offset(cx + w * 0.05f, floorY - bounce)
                    drawMuscleHighlight(hip, 14f, p, FlameOrange)
                    drawLimbSegment(hip, footL, pal.secondaryLimb, pal, 10f)
                    drawLimbSegment(hip, footR, pal.secondaryLimb, pal, 10f)
                    drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
                    drawLimbSegment(shoulder, Offset(cx - w * 0.11f, shoulder.y + h * 0.08f), WarmGold, pal, 9f)
                    drawLimbSegment(shoulder, Offset(cx + w * 0.11f, shoulder.y + h * 0.08f), WarmGold, pal, 9f)
                    drawAthleteHead(Offset(cx, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
                }
            }
        }

        ExerciseMotionKind.KETTLEBELL_SWING -> {
            val foot = Offset(cx - w * 0.04f, floorY)
            val hip = Offset(cx - w * 0.08f + p * w * 0.07f, floorY - h * 0.34f)
            val shoulder = Offset(cx - w * 0.01f + (1f - p) * w * 0.06f, hip.y - h * 0.22f + (1f - p) * h * 0.06f)
            val armAngle = 100f - p * 98f // hanging down (+100°) -> swung forward horizontal (0°)
            val kbPos = drawAnglePoint(shoulder, h * 0.27f, armAngle)

            drawMuscleHighlight(hip, 16f, p, FlameOrange)
            drawMotionArrow(Offset(cx + w * 0.10f, floorY - h * 0.20f), Offset(cx + w * 0.24f, shoulder.y), ElectricTeal)
            drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawAthleteHead(Offset(shoulder.x, shoulder.y - headR * 1.35f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            drawLimbSegment(shoulder, kbPos, WarmGold, pal, 9f)
            drawKettlebell(kbPos, WarmGold)
        }

        ExerciseMotionKind.BATTLE_ROPES -> {
            val athleteX = cx - w * 0.20f
            val hip = Offset(athleteX - 10f, floorY - h * 0.30f)
            val knee = Offset(athleteX + 10f, floorY - h * 0.17f)
            val foot = Offset(athleteX, floorY)
            val shoulder = Offset(athleteX + 4f, hip.y - h * 0.23f)
            val handL = Offset(athleteX + w * 0.12f, shoulder.y + h * 0.08f + sin(p * 6.283f) * 14f)
            val handR = Offset(athleteX + w * 0.12f, shoulder.y + h * 0.10f - sin(p * 6.283f) * 14f)
            val anchor = Offset(cx + w * 0.34f, floorY - 8f)

            drawSineRope(handL, anchor, p, 0f, FlameOrange)
            drawSineRope(handR, anchor, p, 2.5f, WarmGold)
            drawLimbSegment(foot, knee, pal.secondaryLimb, pal, 10f)
            drawLimbSegment(knee, hip, pal.secondaryLimb, pal, 10f)
            drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
            drawLimbSegment(shoulder, handL, pal.primaryLimb, pal, 9f)
            drawLimbSegment(shoulder, handR, WarmGold, pal, 9f)
            drawAthleteHead(Offset(shoulder.x, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
        }

        ExerciseMotionKind.ROWING_ERG -> {
            val railY = floorY - h * 0.12f
            drawLine(pal.equipMetal, Offset(cx - w * 0.28f, railY), Offset(cx + w * 0.28f, railY), 7f, StrokeCap.Round)
            drawRoundRect(pal.equipPad, Offset(cx + w * 0.20f, railY - h * 0.22f), Size(18f, h * 0.22f), CornerRadius(4f, 4f))

            val slideX = cx - w * 0.12f * p
            val hip = Offset(slideX + w * 0.04f, railY - 12f)
            val shoulder = Offset(hip.x - w * 0.04f - p * w * 0.06f, hip.y - h * 0.24f)
            val foot = Offset(cx + w * 0.18f, railY - 4f)
            val knee = Offset((hip.x + foot.x) * 0.5f, hip.y - (1f - p) * h * 0.14f)
            val hand = Offset(shoulder.x + w * 0.16f - p * w * 0.14f, shoulder.y + h * 0.07f)

            drawLine(ElectricTeal, Offset(cx + w * 0.20f, railY - h * 0.15f), hand, 3f)
            drawLimbSegment(knee, foot, pal.secondaryLimb, pal, 10f, LimbRole.CALF)
            drawLimbSegment(hip, knee, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
            drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
            drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
            drawAthleteHead(Offset(shoulder.x, shoulder.y - headR * 1.35f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
        }

        ExerciseMotionKind.TREADMILL_WALK,
        ExerciseMotionKind.MOUNTAIN_CLIMBER,
        ExerciseMotionKind.HIGH_KNEES,
        ExerciseMotionKind.SHADOW_BOXING -> {
            if (kind == ExerciseMotionKind.MOUNTAIN_CLIMBER) {
                drawMat(cx - w * 0.32f, cx + w * 0.30f, floorY, pal.equipPad)
                val hand = Offset(cx + w * 0.18f, floorY - 6f)
                val shoulder = Offset(cx + w * 0.16f, floorY - h * 0.26f)
                val hip = Offset(cx - w * 0.04f, floorY - h * 0.22f)
                val kneeA = Offset(hip.x + w * 0.14f * p, hip.y + h * 0.12f)
                val footB = Offset(cx - w * 0.24f, floorY - 6f)
                drawMuscleHighlight(hip, 14f, p, FlameOrange)
                drawLimbSegment(hip, footB, pal.secondaryLimb.copy(alpha = 0.6f), pal, 9f, LimbRole.FULL_LEG)
                drawLimbSegment(hip, kneeA, pal.secondaryLimb, pal, 10f, LimbRole.THIGH)
                drawTorso(hip, shoulder, pal.torsoFill, 15f, pal)
                drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
                drawAthleteHead(Offset(shoulder.x + 10f, shoulder.y - headR), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            } else if (kind == ExerciseMotionKind.SHADOW_BOXING) {
                val hip = Offset(cx - w * 0.05f, floorY - h * 0.35f)
                val shoulder = Offset(cx - w * 0.03f, hip.y - h * 0.23f)
                val punchExt = p * w * 0.20f
                val handF = Offset(shoulder.x + w * 0.08f + punchExt, shoulder.y + 4f)
                val handB = Offset(shoulder.x + w * 0.06f, shoulder.y + 10f)
                drawMuscleHighlight(Offset(shoulder.x + 8f, shoulder.y), 13f, p, FlameOrange)
                drawLimbSegment(hip, Offset(cx - w * 0.12f, floorY), pal.secondaryLimb, pal, 10f)
                drawLimbSegment(hip, Offset(cx + w * 0.06f, floorY), pal.secondaryLimb, pal, 10f)
                drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
                drawLimbSegment(shoulder, handB, pal.primaryLimb, pal, 9f)
                drawLimbSegment(shoulder, handF, WarmGold, pal, 9f)
                drawCircle(FlameOrange, 9f, handF)
                drawAthleteHead(Offset(shoulder.x, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            } else {
                val swing = sin(p * 6.283f)
                val hip = Offset(cx, floorY - h * 0.35f)
                val shoulder = Offset(cx + 6f, hip.y - h * 0.23f)
                val kneeA = Offset(cx + swing * w * 0.10f, floorY - h * 0.22f - if (kind == ExerciseMotionKind.HIGH_KNEES) abs(swing) * h * 0.10f else 0f)
                val footA = Offset(kneeA.x - 6f, floorY - if (kind == ExerciseMotionKind.HIGH_KNEES) abs(swing) * h * 0.14f else 0f)
                val kneeB = Offset(cx - swing * w * 0.10f, floorY - h * 0.20f)
                val footB = Offset(kneeB.x - 8f, floorY)
                drawLimbSegment(hip, kneeB, pal.secondaryLimb.copy(alpha = 0.6f), pal, 9f)
                drawLimbSegment(kneeB, footB, pal.secondaryLimb.copy(alpha = 0.6f), pal, 9f)
                drawLimbSegment(hip, kneeA, pal.secondaryLimb, pal, 10f)
                drawLimbSegment(kneeA, footA, pal.secondaryLimb, pal, 10f)
                drawTorso(hip, shoulder, pal.torsoFill, 16f, pal)
                drawLimbSegment(shoulder, Offset(shoulder.x - swing * w * 0.08f, shoulder.y + h * 0.14f), WarmGold, pal, 9f)
                drawAthleteHead(Offset(shoulder.x, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, headbandColor = pal.outfitAccent, isFemale = pal.isFemale)
            }
        }

        ExerciseMotionKind.CAT_COW,
        ExerciseMotionKind.COBRA_STRETCH,
        ExerciseMotionKind.CHILDS_POSE,
        ExerciseMotionKind.WALL_ANGELS,
        ExerciseMotionKind.OVERHEAD_STRETCH,
        ExerciseMotionKind.BOX_BREATHING -> {
            when (kind) {
                ExerciseMotionKind.CAT_COW -> {
                    drawMat(cx - w * 0.30f, cx + w * 0.30f, floorY, pal.equipPad)
                    val hip = Offset(cx - w * 0.12f, floorY - h * 0.24f)
                    val shoulder = Offset(cx + w * 0.12f, floorY - h * 0.24f)
                    val midSpine = Offset(cx, floorY - h * 0.24f + (p - 0.5f) * h * 0.12f)
                    drawLimbSegment(hip, Offset(hip.x, floorY - 6f), pal.secondaryLimb, pal, 9f)
                    drawLimbSegment(shoulder, Offset(shoulder.x, floorY - 6f), WarmGold, pal, 9f)
                    drawLimbSegment(hip, midSpine, ElectricTeal, pal, 13f)
                    drawLimbSegment(midSpine, shoulder, ElectricTeal, pal, 13f)
                    drawAthleteHead(Offset(shoulder.x + 12f, shoulder.y - headR * 0.8f), headR, pal.primaryLimb, MintVital, isFemale = pal.isFemale)
                }
                ExerciseMotionKind.COBRA_STRETCH -> {
                    drawMat(cx - w * 0.32f, cx + w * 0.30f, floorY, pal.equipPad)
                    val foot = Offset(cx - w * 0.24f, floorY - 8f)
                    val hip = Offset(cx - w * 0.04f, floorY - 8f)
                    val shoulder = Offset(cx + w * 0.14f, floorY - h * 0.16f - p * h * 0.16f)
                    val hand = Offset(cx + w * 0.16f, floorY - 6f)
                    drawMuscleHighlight(Offset(hip.x + 12f, hip.y - 12f), 14f, p, MintVital)
                    drawLimbSegment(hip, foot, pal.secondaryLimb, pal, 10f, LimbRole.FULL_LEG)
                    drawTorso(hip, shoulder, ElectricTeal, 15f, pal)
                    drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
                    drawAthleteHead(Offset(shoulder.x + 8f, shoulder.y - headR), headR, pal.primaryLimb, MintVital, isFemale = pal.isFemale)
                }
                ExerciseMotionKind.CHILDS_POSE -> {
                    drawMat(cx - w * 0.32f, cx + w * 0.30f, floorY, pal.equipPad)
                    val hip = Offset(cx - w * 0.12f, floorY - h * 0.14f)
                    val shoulder = Offset(cx + w * 0.06f, floorY - h * 0.08f - p * 6f)
                    val hand = Offset(cx + w * 0.24f, floorY - 7f)
                    drawMuscleHighlight(Offset((hip.x + shoulder.x) * 0.5f, hip.y - 6f), 16f, p, ElectricTeal)
                    drawLimbSegment(hip, Offset(cx - w * 0.22f, floorY - 7f), pal.secondaryLimb, pal, 10f, LimbRole.CALF)
                    drawTorso(hip, shoulder, ElectricTeal, 15f, pal)
                    drawLimbSegment(shoulder, hand, WarmGold, pal, 9f)
                    drawAthleteHead(Offset(shoulder.x + 8f, floorY - headR - 6f), headR, pal.primaryLimb, MintVital, isFemale = pal.isFemale)
                }
                ExerciseMotionKind.BOX_BREATHING -> {
                    val pulseR = h * 0.18f + p * h * 0.15f
                    drawCircle(ElectricTeal.copy(alpha = 0.20f), radius = pulseR * 1.35f, center = Offset(cx, h * 0.45f))
                    drawCircle(MintVital.copy(alpha = 0.32f), radius = pulseR, center = Offset(cx, h * 0.45f))
                    drawCircle(ElectricTeal, radius = pulseR * 0.5f, center = Offset(cx, h * 0.45f))
                }
                else -> {
                    val hip = Offset(cx, floorY - h * 0.36f)
                    val shoulder = Offset(cx, floorY - h * 0.60f)
                    val reachY = shoulder.y - h * 0.06f - p * h * 0.18f
                    val leanX = sin(p * 3.14159f) * w * 0.05f
                    drawMuscleHighlight(Offset(cx, shoulder.y + 8f), 16f, p, ElectricTeal)
                    drawLimbSegment(hip, Offset(cx - w * 0.05f, floorY), pal.secondaryLimb, pal, 10f)
                    drawLimbSegment(hip, Offset(cx + w * 0.05f, floorY), pal.secondaryLimb, pal, 10f)
                    drawTorso(hip, Offset(shoulder.x + leanX, shoulder.y), ElectricTeal, 16f, pal)
                    drawLimbSegment(Offset(shoulder.x + leanX, shoulder.y), Offset(cx - w * 0.10f + leanX, reachY), WarmGold, pal, 9f)
                    drawLimbSegment(Offset(shoulder.x + leanX, shoulder.y), Offset(cx + w * 0.10f + leanX, reachY), WarmGold, pal, 9f)
                    drawAthleteHead(Offset(shoulder.x + leanX, shoulder.y - headR * 1.4f), headR, pal.primaryLimb, MintVital, isFemale = pal.isFemale)
                }
            }
        }

        else -> {}
    }
}
