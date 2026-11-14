package com.example.ui.components

import com.example.data.model.ExerciseDemoType
import com.example.data.model.ExerciseItem

enum class ExerciseMotionKind {
    BENCH_PRESS_FLAT,
    BENCH_PRESS_INCLINE,
    FLOOR_PRESS,
    SEATED_CHEST_PRESS,
    CHEST_FLY,
    OVERHEAD_PRESS,
    LATERAL_RAISE,
    SKULL_CRUSHER,
    CABLE_PUSHDOWN,
    DIPS,
    CHAIR_DIPS,
    LAT_PULLDOWN,
    CABLE_ROW,
    FACE_PULL,
    BENT_OVER_ROW,
    SHRUGS,
    PULL_UP,
    AUSTRALIAN_ROW,
    DEAD_HANG,
    BICEPS_CURL,
    INCLINE_CURL,
    BARBELL_SQUAT,
    GOBLET_SQUAT,
    BODYWEIGHT_SQUAT,
    DEADLIFT_HINGE,
    LEG_PRESS,
    LEG_EXTENSION,
    LYING_LEG_CURL,
    HIP_THRUST,
    GLUTE_BRIDGE,
    CALF_RAISE,
    LUNGE_SPLIT,
    BULGARIAN_SPLIT_SQUAT,
    PUSHUP_STANDARD,
    PUSHUP_DECLINE,
    PUSHUP_INCLINE,
    PIKE_PUSHUP,
    PLANK_HOLD,
    PLANK_DYNAMIC,
    SIDE_PLANK,
    HANGING_KNEE_RAISE,
    CABLE_CRUNCH,
    BICYCLE_CRUNCH,
    LEG_RAISE,
    V_UP,
    HOLLOW_HOLD,
    DEAD_BUG,
    BIRD_DOG,
    L_SIT_HOLD,
    SUPERMAN_HOLD,
    BOX_JUMP,
    SQUAT_JUMP,
    JUMPING_JACKS,
    BURPEE,
    JUMP_ROPE,
    SPEED_SKATERS,
    KETTLEBELL_SWING,
    BATTLE_ROPES,
    ROWING_ERG,
    TREADMILL_WALK,
    MOUNTAIN_CLIMBER,
    HIGH_KNEES,
    SHADOW_BOXING,
    CAT_COW,
    COBRA_STRETCH,
    CHILDS_POSE,
    WALL_ANGELS,
    OVERHEAD_STRETCH,
    BOX_BREATHING
}

data class ExerciseMotionSpec(
    val kind: ExerciseMotionKind,
    val phaseA: String,
    val phaseB: String,
    val cycleMs: Int = 1800
)

fun resolveExerciseMotion(exercise: ExerciseItem): ExerciseMotionSpec {
    val kind = when (exercise.id) {
        "g1_1", "g5_1", "g9_1" -> ExerciseMotionKind.BENCH_PRESS_FLAT
        "g1_2", "gp1_1", "gp4_2" -> ExerciseMotionKind.BENCH_PRESS_INCLINE
        "g1_3", "g6_1", "h6_1" -> ExerciseMotionKind.OVERHEAD_PRESS
        "g1_4" -> ExerciseMotionKind.CABLE_PUSHDOWN
        "g2_1", "g10_2" -> ExerciseMotionKind.LAT_PULLDOWN
        "g2_2", "g4_3" -> ExerciseMotionKind.CABLE_ROW
        "g2_3" -> ExerciseMotionKind.FACE_PULL
        "g2_4", "g5_4", "gp2_3" -> ExerciseMotionKind.BICEPS_CURL
        "g6_2" -> ExerciseMotionKind.INCLINE_CURL
        "g6_3" -> ExerciseMotionKind.SKULL_CRUSHER
        "g5_3", "g6_4", "gp1_3" -> ExerciseMotionKind.LATERAL_RAISE
        "g3_1", "g11_1", "gp3_1", "gp4_1" -> ExerciseMotionKind.BARBELL_SQUAT
        "g4_1" -> ExerciseMotionKind.GOBLET_SQUAT
        "g3_2", "g7_1" -> ExerciseMotionKind.DEADLIFT_HINGE
        "g3_3" -> ExerciseMotionKind.LEG_PRESS
        "g3_4", "y2_3" -> ExerciseMotionKind.CALF_RAISE
        "g4_2", "g12_3" -> ExerciseMotionKind.SEATED_CHEST_PRESS
        "g4_4", "y6_2" -> ExerciseMotionKind.PLANK_HOLD
        "g5_2", "g10_1", "gp2_2", "gp4_3", "h6_2" -> ExerciseMotionKind.BENT_OVER_ROW
        "g7_2" -> ExerciseMotionKind.HIP_THRUST
        "g7_3" -> ExerciseMotionKind.LYING_LEG_CURL
        "g7_4" -> ExerciseMotionKind.HANGING_KNEE_RAISE
        "g8_1" -> ExerciseMotionKind.ROWING_ERG
        "g8_2" -> ExerciseMotionKind.KETTLEBELL_SWING
        "g8_3" -> ExerciseMotionKind.BATTLE_ROPES
        "g8_4", "gp4_4" -> ExerciseMotionKind.TREADMILL_WALK
        "g9_2" -> ExerciseMotionKind.DIPS
        "g9_3", "gp1_2" -> ExerciseMotionKind.CHEST_FLY
        "g10_3" -> ExerciseMotionKind.SHRUGS
        "g11_2", "h4_2", "y5_2", "y7_2" -> ExerciseMotionKind.LUNGE_SPLIT
        "g11_3" -> ExerciseMotionKind.LEG_EXTENSION
        "g12_1" -> ExerciseMotionKind.CABLE_CRUNCH
        "g12_2" -> ExerciseMotionKind.V_UP
        "gp2_1" -> ExerciseMotionKind.PULL_UP
        "gp3_2" -> ExerciseMotionKind.BULGARIAN_SPLIT_SQUAT
        "gp3_3", "yp1_2" -> ExerciseMotionKind.BOX_JUMP
        "h1_1", "h4_1", "h7_3", "h10_1", "hp1_1", "y3_1" -> ExerciseMotionKind.BODYWEIGHT_SQUAT
        "h1_2", "h2_2", "h2_3", "hp1_2", "hp3_1", "hp3_3", "y4_3", "yp1_3" -> ExerciseMotionKind.PUSHUP_STANDARD
        "h1_3", "h4_3", "y6_3" -> ExerciseMotionKind.GLUTE_BRIDGE
        "h1_4" -> ExerciseMotionKind.MOUNTAIN_CLIMBER
        "h2_1" -> ExerciseMotionKind.PUSHUP_DECLINE
        "h3_1" -> ExerciseMotionKind.BICYCLE_CRUNCH
        "h3_2", "h12_3", "hp4_3" -> ExerciseMotionKind.LEG_RAISE
        "h3_3" -> ExerciseMotionKind.SIDE_PLANK
        "h5_1" -> ExerciseMotionKind.JUMPING_JACKS
        "h5_2", "hp2_1" -> ExerciseMotionKind.BURPEE
        "h5_3" -> ExerciseMotionKind.HIGH_KNEES
        "h6_3" -> ExerciseMotionKind.FLOOR_PRESS
        "h7_1" -> ExerciseMotionKind.CAT_COW
        "h7_2" -> ExerciseMotionKind.LUNGE_SPLIT
        "h8_1" -> ExerciseMotionKind.PIKE_PUSHUP
        "h8_2" -> ExerciseMotionKind.CHAIR_DIPS
        "h8_3", "h11_2" -> ExerciseMotionKind.SUPERMAN_HOLD
        "h9_1", "y2_2" -> ExerciseMotionKind.SQUAT_JUMP
        "h9_2", "y5_1" -> ExerciseMotionKind.SPEED_SKATERS
        "h9_3", "h10_2", "hp2_3", "y5_3" -> ExerciseMotionKind.PLANK_DYNAMIC
        "h10_3" -> ExerciseMotionKind.SHADOW_BOXING
        "h11_1", "y4_2" -> ExerciseMotionKind.WALL_ANGELS
        "h11_3" -> ExerciseMotionKind.BIRD_DOG
        "h12_1", "h12_2" -> ExerciseMotionKind.CHILDS_POSE
        "hp1_3", "y3_3" -> ExerciseMotionKind.AUSTRALIAN_ROW
        "hp2_2" -> ExerciseMotionKind.SQUAT_JUMP
        "hp3_2" -> ExerciseMotionKind.L_SIT_HOLD
        "hp4_1" -> ExerciseMotionKind.HOLLOW_HOLD
        "hp4_2" -> ExerciseMotionKind.V_UP
        "y1_1", "yp1_1" -> ExerciseMotionKind.DEAD_HANG
        "y1_2" -> ExerciseMotionKind.COBRA_STRETCH
        "y1_3", "y4_1" -> ExerciseMotionKind.OVERHEAD_STRETCH
        "y2_1", "y7_1" -> ExerciseMotionKind.JUMP_ROPE
        "y3_2" -> ExerciseMotionKind.PUSHUP_INCLINE
        "y6_1" -> ExerciseMotionKind.DEAD_BUG
        "y7_3" -> ExerciseMotionKind.BOX_BREATHING
        else -> when (exercise.demoType) {
            ExerciseDemoType.SQUAT -> ExerciseMotionKind.BODYWEIGHT_SQUAT
            ExerciseDemoType.PUSHUP -> ExerciseMotionKind.PUSHUP_STANDARD
            ExerciseDemoType.PLANK -> ExerciseMotionKind.PLANK_HOLD
            ExerciseDemoType.PULL -> ExerciseMotionKind.LAT_PULLDOWN
            ExerciseDemoType.PRESS -> ExerciseMotionKind.OVERHEAD_PRESS
            ExerciseDemoType.LUNGE -> ExerciseMotionKind.LUNGE_SPLIT
            ExerciseDemoType.JUMP -> ExerciseMotionKind.SQUAT_JUMP
            ExerciseDemoType.STRETCH -> ExerciseMotionKind.OVERHEAD_STRETCH
            ExerciseDemoType.CURL -> ExerciseMotionKind.BICEPS_CURL
            ExerciseDemoType.CARDIO -> ExerciseMotionKind.HIGH_KNEES
        }
    }

    return when (kind) {
        ExerciseMotionKind.BENCH_PRESS_FLAT,
        ExerciseMotionKind.BENCH_PRESS_INCLINE,
        ExerciseMotionKind.FLOOR_PRESS,
        ExerciseMotionKind.SEATED_CHEST_PRESS -> ExerciseMotionSpec(kind, "Kontrollü İniş • Nefes Al", "Güçlü İtiş • Nefes Ver", 1900)
        ExerciseMotionKind.CHEST_FLY -> ExerciseMotionSpec(kind, "Göğsü Aç • Nefes Al", "Ortada Sık • Nefes Ver", 1900)
        ExerciseMotionKind.OVERHEAD_PRESS -> ExerciseMotionSpec(kind, "Omuz Hizası • Nefes Al", "Yukarı İt • Nefes Ver", 1800)
        ExerciseMotionKind.LATERAL_RAISE -> ExerciseMotionSpec(kind, "Yana Aç • Nefes Ver", "Yavaş İndir • Nefes Al", 1700)
        ExerciseMotionKind.SKULL_CRUSHER,
        ExerciseMotionKind.CABLE_PUSHDOWN -> ExerciseMotionSpec(kind, "Dirsek Sabit Bük • Nefes Al", "Kolu Kilitlemeden Aç • Nefes Ver", 1600)
        ExerciseMotionKind.DIPS,
        ExerciseMotionKind.CHAIR_DIPS -> ExerciseMotionSpec(kind, "90° Kontrollü İn • Nefes Al", "Avuçla İt • Nefes Ver", 1750)
        ExerciseMotionKind.LAT_PULLDOWN,
        ExerciseMotionKind.CABLE_ROW,
        ExerciseMotionKind.FACE_PULL,
        ExerciseMotionKind.BENT_OVER_ROW,
        ExerciseMotionKind.PULL_UP,
        ExerciseMotionKind.AUSTRALIAN_ROW -> ExerciseMotionSpec(kind, "Sırtla Çek & Sık • Nefes Ver", "Kontrollü Uzat • Nefes Al", 1850)
        ExerciseMotionKind.SHRUGS -> ExerciseMotionSpec(kind, "Omuzları Çek • 1 sn Tut", "Yavaş Bırak • Nefes Al", 1500)
        ExerciseMotionKind.DEAD_HANG -> ExerciseMotionSpec(kind, "Omuzları Gevşet • Uzama", "Derin Diyafram Nefesi", 2400)
        ExerciseMotionKind.BICEPS_CURL,
        ExerciseMotionKind.INCLINE_CURL -> ExerciseMotionSpec(kind, "Dirsek Sabit Kaldır • Nefes Ver", "3 sn Yavaş İndir • Nefes Al", 1700)
        ExerciseMotionKind.BARBELL_SQUAT,
        ExerciseMotionKind.GOBLET_SQUAT,
        ExerciseMotionKind.BODYWEIGHT_SQUAT -> ExerciseMotionSpec(kind, "Kalçayı Geri İt • Nefes Al", "Topuktan İt • Nefes Ver", 2000)
        ExerciseMotionKind.DEADLIFT_HINGE -> ExerciseMotionSpec(kind, "Kalçadan Kırıl • Nefes Al", "Kalçayı Kilitle • Nefes Ver", 2000)
        ExerciseMotionKind.LEG_PRESS -> ExerciseMotionSpec(kind, "Dizleri Yaklaştır • Nefes Al", "Topukla İt • Nefes Ver", 1900)
        ExerciseMotionKind.LEG_EXTENSION,
        ExerciseMotionKind.LYING_LEG_CURL -> ExerciseMotionSpec(kind, "Tam Kas & 1 sn Sık • Nefes Ver", "Yavaş İndir • Nefes Al", 1700)
        ExerciseMotionKind.HIP_THRUST,
        ExerciseMotionKind.GLUTE_BRIDGE -> ExerciseMotionSpec(kind, "Kalçayı İt & Sık • Nefes Ver", "Kontrollü İn • Nefes Al", 1750)
        ExerciseMotionKind.CALF_RAISE -> ExerciseMotionSpec(kind, "Topuğu Esnet • Nefes Al", "Parmak Ucuna Çık • Nefes Ver", 1500)
        ExerciseMotionKind.LUNGE_SPLIT,
        ExerciseMotionKind.BULGARIAN_SPLIT_SQUAT -> ExerciseMotionSpec(kind, "Arka Dizi İndir • Nefes Al", "Ön Topukla Kalk • Nefes Ver", 1850)
        ExerciseMotionKind.PUSHUP_STANDARD,
        ExerciseMotionKind.PUSHUP_DECLINE,
        ExerciseMotionKind.PUSHUP_INCLINE,
        ExerciseMotionKind.PIKE_PUSHUP -> ExerciseMotionSpec(kind, "Gövde Düz İn • Nefes Al", "Yeri İt • Nefes Ver", 1750)
        ExerciseMotionKind.PLANK_HOLD,
        ExerciseMotionKind.SIDE_PLANK,
        ExerciseMotionKind.HOLLOW_HOLD,
        ExerciseMotionKind.L_SIT_HOLD -> ExerciseMotionSpec(kind, "Karnı & Kalçayı Sık", "Düzenli Nefes Akışı", 2200)
        ExerciseMotionKind.PLANK_DYNAMIC,
        ExerciseMotionKind.BIRD_DOG,
        ExerciseMotionKind.SUPERMAN_HOLD -> ExerciseMotionSpec(kind, "Dengeyi Koru • Nefes Ver", "Başlangıca Dön • Nefes Al", 1600)
        ExerciseMotionKind.HANGING_KNEE_RAISE,
        ExerciseMotionKind.CABLE_CRUNCH,
        ExerciseMotionKind.BICYCLE_CRUNCH,
        ExerciseMotionKind.LEG_RAISE,
        ExerciseMotionKind.V_UP,
        ExerciseMotionKind.DEAD_BUG -> ExerciseMotionSpec(kind, "Karnı Sık & Topla • Nefes Ver", "Kontrollü Uzat • Nefes Al", 1650)
        ExerciseMotionKind.BOX_JUMP,
        ExerciseMotionKind.SQUAT_JUMP,
        ExerciseMotionKind.JUMPING_JACKS,
        ExerciseMotionKind.BURPEE,
        ExerciseMotionKind.JUMP_ROPE,
        ExerciseMotionKind.SPEED_SKATERS -> ExerciseMotionSpec(kind, "Hazırlan & Yaylan • Nefes Al", "Patlayıcı Sıçra • Nefes Ver", 1250)
        ExerciseMotionKind.KETTLEBELL_SWING -> ExerciseMotionSpec(kind, "Kalçadan Savur • Nefes Ver", "Bacak Arasına Al • Nefes Al", 1300)
        ExerciseMotionKind.BATTLE_ROPES -> ExerciseMotionSpec(kind, "Sağ/Sol Dalga Üret", "Core Sabit • Ritmik Nefes", 950)
        ExerciseMotionKind.ROWING_ERG -> ExerciseMotionSpec(kind, "Bacakla İt & Çek • Nefes Ver", "Öne Uzan • Nefes Al", 1450)
        ExerciseMotionKind.TREADMILL_WALK,
        ExerciseMotionKind.MOUNTAIN_CLIMBER,
        ExerciseMotionKind.HIGH_KNEES,
        ExerciseMotionKind.SHADOW_BOXING -> ExerciseMotionSpec(kind, "Yüksek Tempo & Ritim", "Kesintisiz Nefes Ver", 950)
        ExerciseMotionKind.CAT_COW,
        ExerciseMotionKind.COBRA_STRETCH,
        ExerciseMotionKind.CHILDS_POSE,
        ExerciseMotionKind.WALL_ANGELS,
        ExerciseMotionKind.OVERHEAD_STRETCH -> ExerciseMotionSpec(kind, "Omurgayı Aç • Derin Nefes Al", "Gevşe & Uzat • Yavaş Ver", 2400)
        ExerciseMotionKind.BOX_BREATHING -> ExerciseMotionSpec(kind, "4 sn Nefes Al & Tut", "4 sn Yavaşça Nefes Ver", 3200)
    }
}
