package com.example.data.catalog

enum class MuscleGroupZone {
    CHEST,
    BACK,
    SHOULDERS,
    ARMS,
    CORE,
    LEGS_GLUTES,
    FULL_BODY
}

data class ExerciseMediaSpec(
    val dbFolderId: String,
    val muscleZone: MuscleGroupZone
) {
    val startFrameUrl: String
        get() = "$BASE_URL/$dbFolderId/images/0.jpg"

    val peakFrameUrl: String
        get() = "$BASE_URL/$dbFolderId/images/1.jpg"

    companion object {
        private const val BASE_URL =
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises"
    }
}

object ExerciseMediaCatalog {

    private val entries: Map<String, ExerciseMediaSpec> = mapOf(
        // Gym 1: Push
        "g1_1" to ExerciseMediaSpec("Barbell_Bench_Press_-_Medium_Grip", MuscleGroupZone.CHEST),
        "g1_2" to ExerciseMediaSpec("Incline_Dumbbell_Press", MuscleGroupZone.CHEST),
        "g1_3" to ExerciseMediaSpec("Dumbbell_Shoulder_Press", MuscleGroupZone.SHOULDERS),
        "g1_4" to ExerciseMediaSpec("Triceps_Pushdown_-_Rope_Attachment", MuscleGroupZone.ARMS),

        // Gym 2: Pull
        "g2_1" to ExerciseMediaSpec("Wide-Grip_Lat_Pulldown", MuscleGroupZone.BACK),
        "g2_2" to ExerciseMediaSpec("Seated_Cable_Rows", MuscleGroupZone.BACK),
        "g2_3" to ExerciseMediaSpec("Face_Pull", MuscleGroupZone.SHOULDERS),
        "g2_4" to ExerciseMediaSpec("EZ-Bar_Curl", MuscleGroupZone.ARMS),

        // Gym 3: Legs
        "g3_1" to ExerciseMediaSpec("Barbell_Squat", MuscleGroupZone.LEGS_GLUTES),
        "g3_2" to ExerciseMediaSpec("Romanian_Deadlift", MuscleGroupZone.LEGS_GLUTES),
        "g3_3" to ExerciseMediaSpec("Leg_Press", MuscleGroupZone.LEGS_GLUTES),
        "g3_4" to ExerciseMediaSpec("Standing_Calf_Raises", MuscleGroupZone.LEGS_GLUTES),

        // Gym 4: Full Body Beginner
        "g4_1" to ExerciseMediaSpec("Goblet_Squat", MuscleGroupZone.LEGS_GLUTES),
        "g4_2" to ExerciseMediaSpec("Machine_Bench_Press", MuscleGroupZone.CHEST),
        "g4_3" to ExerciseMediaSpec("Leverage_Iso_Row", MuscleGroupZone.BACK),
        "g4_4" to ExerciseMediaSpec("Plank", MuscleGroupZone.CORE),

        // Gym 5: Upper Body
        "g5_1" to ExerciseMediaSpec("Dumbbell_Bench_Press", MuscleGroupZone.CHEST),
        "g5_2" to ExerciseMediaSpec("One-Arm_Dumbbell_Row", MuscleGroupZone.BACK),
        "g5_3" to ExerciseMediaSpec("Side_Lateral_Raise", MuscleGroupZone.SHOULDERS),
        "g5_4" to ExerciseMediaSpec("Hammer_Curls", MuscleGroupZone.ARMS),

        // Gym 6: Arms & Shoulders
        "g6_1" to ExerciseMediaSpec("Arnold_Dumbbell_Press", MuscleGroupZone.SHOULDERS),
        "g6_2" to ExerciseMediaSpec("Incline_Dumbbell_Curl", MuscleGroupZone.ARMS),
        "g6_3" to ExerciseMediaSpec("EZ-Bar_Skullcrusher", MuscleGroupZone.ARMS),
        "g6_4" to ExerciseMediaSpec("Side_Lateral_Raise", MuscleGroupZone.SHOULDERS),

        // Gym 7: Deadlift & Posterior Chain
        "g7_1" to ExerciseMediaSpec("Barbell_Deadlift", MuscleGroupZone.BACK),
        "g7_2" to ExerciseMediaSpec("Barbell_Hip_Thrust", MuscleGroupZone.LEGS_GLUTES),
        "g7_3" to ExerciseMediaSpec("Lying_Leg_Curls", MuscleGroupZone.LEGS_GLUTES),
        "g7_4" to ExerciseMediaSpec("Hanging_Leg_Raise", MuscleGroupZone.CORE),

        // Gym 8: Cardio & Metabolic HIIT
        "g8_1" to ExerciseMediaSpec("Rowing_Stationary", MuscleGroupZone.FULL_BODY),
        "g8_2" to ExerciseMediaSpec("One-Arm_Kettlebell_Swings", MuscleGroupZone.LEGS_GLUTES),
        "g8_3" to ExerciseMediaSpec("Battling_Ropes", MuscleGroupZone.SHOULDERS),
        "g8_4" to ExerciseMediaSpec("Walking_Treadmill", MuscleGroupZone.FULL_BODY),

        // Gym 9: Bench Strength
        "g9_1" to ExerciseMediaSpec("Barbell_Bench_Press_-_Medium_Grip", MuscleGroupZone.CHEST),
        "g9_2" to ExerciseMediaSpec("Dips_-_Chest_Version", MuscleGroupZone.CHEST),
        "g9_3" to ExerciseMediaSpec("Butterfly", MuscleGroupZone.CHEST),

        // Gym 10: Back & Traps
        "g10_1" to ExerciseMediaSpec("T-Bar_Row_with_Handle", MuscleGroupZone.BACK),
        "g10_2" to ExerciseMediaSpec("Close-Grip_Front_Lat_Pulldown", MuscleGroupZone.BACK),
        "g10_3" to ExerciseMediaSpec("Dumbbell_Shrug", MuscleGroupZone.SHOULDERS),

        // Gym 11: Quads & Knee Stability
        "g11_1" to ExerciseMediaSpec("Hack_Squat", MuscleGroupZone.LEGS_GLUTES),
        "g11_2" to ExerciseMediaSpec("Dumbbell_Lunges", MuscleGroupZone.LEGS_GLUTES),
        "g11_3" to ExerciseMediaSpec("Leg_Extensions", MuscleGroupZone.LEGS_GLUTES),

        // Gym 12: Core
        "g12_1" to ExerciseMediaSpec("Cable_Crunch", MuscleGroupZone.CORE),
        "g12_2" to ExerciseMediaSpec("Decline_Crunch", MuscleGroupZone.CORE),
        "g12_3" to ExerciseMediaSpec("Pallof_Press", MuscleGroupZone.CORE),

        // Gym PRO 1-4
        "gp1_1" to ExerciseMediaSpec("Smith_Machine_Incline_Bench_Press", MuscleGroupZone.CHEST),
        "gp1_2" to ExerciseMediaSpec("Cable_Crossover", MuscleGroupZone.CHEST),
        "gp1_3" to ExerciseMediaSpec("Side_Lateral_Raise", MuscleGroupZone.SHOULDERS),
        "gp2_1" to ExerciseMediaSpec("Pullups", MuscleGroupZone.BACK),
        "gp2_2" to ExerciseMediaSpec("Dumbbell_Incline_Row", MuscleGroupZone.BACK),
        "gp2_3" to ExerciseMediaSpec("Preacher_Curl", MuscleGroupZone.ARMS),
        "gp3_1" to ExerciseMediaSpec("Front_Barbell_Squat", MuscleGroupZone.LEGS_GLUTES),
        "gp3_2" to ExerciseMediaSpec("Split_Squats", MuscleGroupZone.LEGS_GLUTES),
        "gp3_3" to ExerciseMediaSpec("Front_Box_Jump", MuscleGroupZone.LEGS_GLUTES),
        "gp4_1" to ExerciseMediaSpec("Barbell_Full_Squat", MuscleGroupZone.LEGS_GLUTES),
        "gp4_2" to ExerciseMediaSpec("Barbell_Incline_Bench_Press_-_Medium_Grip", MuscleGroupZone.CHEST),
        "gp4_3" to ExerciseMediaSpec("Bent_Over_Barbell_Row", MuscleGroupZone.BACK),
        "gp4_4" to ExerciseMediaSpec("Farmers_Walk", MuscleGroupZone.CORE),

        // Home 1-12
        "h1_1" to ExerciseMediaSpec("Bodyweight_Squat", MuscleGroupZone.LEGS_GLUTES),
        "h1_2" to ExerciseMediaSpec("Pushups", MuscleGroupZone.CHEST),
        "h1_3" to ExerciseMediaSpec("Barbell_Glute_Bridge", MuscleGroupZone.LEGS_GLUTES),
        "h1_4" to ExerciseMediaSpec("Mountain_Climbers", MuscleGroupZone.CORE),

        "h2_1" to ExerciseMediaSpec("Decline_Push-Up", MuscleGroupZone.CHEST),
        "h2_2" to ExerciseMediaSpec("Close-Grip_Push-Up", MuscleGroupZone.CHEST),
        "h2_3" to ExerciseMediaSpec("Pushups", MuscleGroupZone.CHEST),

        "h3_1" to ExerciseMediaSpec("Air_Bike", MuscleGroupZone.CORE),
        "h3_2" to ExerciseMediaSpec("Flat_Bench_Lying_Leg_Raise", MuscleGroupZone.CORE),
        "h3_3" to ExerciseMediaSpec("Side_Bridge", MuscleGroupZone.CORE),

        "h4_1" to ExerciseMediaSpec("Bodyweight_Squat", MuscleGroupZone.LEGS_GLUTES),
        "h4_2" to ExerciseMediaSpec("Dumbbell_Rear_Lunge", MuscleGroupZone.LEGS_GLUTES),
        "h4_3" to ExerciseMediaSpec("Single_Leg_Glute_Bridge", MuscleGroupZone.LEGS_GLUTES),

        "h5_1" to ExerciseMediaSpec("Jumping_Jacks", MuscleGroupZone.FULL_BODY),
        "h5_2" to ExerciseMediaSpec("Mountain_Climbers", MuscleGroupZone.FULL_BODY),
        "h5_3" to ExerciseMediaSpec("Running_Treadmill", MuscleGroupZone.FULL_BODY),

        "h6_1" to ExerciseMediaSpec("Dumbbell_Squat_To_Shoulder_Press", MuscleGroupZone.FULL_BODY),
        "h6_2" to ExerciseMediaSpec("Bent_Over_Two-Dumbbell_Row", MuscleGroupZone.BACK),
        "h6_3" to ExerciseMediaSpec("Dumbbell_Bench_Press", MuscleGroupZone.CHEST),

        "h7_1" to ExerciseMediaSpec("Cat_Stretch", MuscleGroupZone.BACK),
        "h7_2" to ExerciseMediaSpec("World_Greatest_Stretch", MuscleGroupZone.FULL_BODY),
        "h7_3" to ExerciseMediaSpec("Bodyweight_Squat", MuscleGroupZone.LEGS_GLUTES),

        "h8_1" to ExerciseMediaSpec("Pushups", MuscleGroupZone.SHOULDERS),
        "h8_2" to ExerciseMediaSpec("Bench_Dips", MuscleGroupZone.ARMS),
        "h8_3" to ExerciseMediaSpec("Superman", MuscleGroupZone.BACK),

        "h9_1" to ExerciseMediaSpec("Freehand_Jump_Squat", MuscleGroupZone.LEGS_GLUTES),
        "h9_2" to ExerciseMediaSpec("Side_to_Side_Box_Shuffle", MuscleGroupZone.LEGS_GLUTES),
        "h9_3" to ExerciseMediaSpec("Plank", MuscleGroupZone.CORE),

        "h10_1" to ExerciseMediaSpec("Bodyweight_Squat", MuscleGroupZone.LEGS_GLUTES),
        "h10_2" to ExerciseMediaSpec("Inchworm", MuscleGroupZone.CORE),
        "h10_3" to ExerciseMediaSpec("Boxing_Bag", MuscleGroupZone.FULL_BODY),

        "h11_1" to ExerciseMediaSpec("Shoulder_Stretch", MuscleGroupZone.SHOULDERS),
        "h11_2" to ExerciseMediaSpec("Superman", MuscleGroupZone.BACK),
        "h11_3" to ExerciseMediaSpec("Superman", MuscleGroupZone.CORE),

        "h12_1" to ExerciseMediaSpec("Childs_Pose", MuscleGroupZone.BACK),
        "h12_2" to ExerciseMediaSpec("Piriformis_Stretch", MuscleGroupZone.LEGS_GLUTES),
        "h12_3" to ExerciseMediaSpec("Hamstring_Stretch", MuscleGroupZone.LEGS_GLUTES),

        // Home PRO 1-4
        "hp1_1" to ExerciseMediaSpec("Bodyweight_Squat", MuscleGroupZone.LEGS_GLUTES),
        "hp1_2" to ExerciseMediaSpec("Single-Arm_Push-Up", MuscleGroupZone.CHEST),
        "hp1_3" to ExerciseMediaSpec("Inverted_Row", MuscleGroupZone.BACK),

        "hp2_1" to ExerciseMediaSpec("Standing_Long_Jump", MuscleGroupZone.FULL_BODY),
        "hp2_2" to ExerciseMediaSpec("Split_Jump", MuscleGroupZone.LEGS_GLUTES),
        "hp2_3" to ExerciseMediaSpec("Plank", MuscleGroupZone.CORE),

        "hp3_1" to ExerciseMediaSpec("Decline_Push-Up", MuscleGroupZone.SHOULDERS),
        "hp3_2" to ExerciseMediaSpec("Isometric_Wipers", MuscleGroupZone.CORE),
        "hp3_3" to ExerciseMediaSpec("Plyo_Push-Up", MuscleGroupZone.CHEST),

        "hp4_1" to ExerciseMediaSpec("Plank", MuscleGroupZone.CORE),
        "hp4_2" to ExerciseMediaSpec("Jackknife_Sit-Up", MuscleGroupZone.CORE),
        "hp4_3" to ExerciseMediaSpec("Flat_Bench_Lying_Leg_Raise", MuscleGroupZone.CORE),

        // Youth 1-7 + Youth PRO 1
        "y1_1" to ExerciseMediaSpec("Pullups", MuscleGroupZone.BACK),
        "y1_2" to ExerciseMediaSpec("Upward_Stretch", MuscleGroupZone.BACK),
        "y1_3" to ExerciseMediaSpec("Side_Stretch", MuscleGroupZone.FULL_BODY),

        "y2_1" to ExerciseMediaSpec("Rope_Jumping", MuscleGroupZone.LEGS_GLUTES),
        "y2_2" to ExerciseMediaSpec("Knee_Tuck_Jump", MuscleGroupZone.LEGS_GLUTES),
        "y2_3" to ExerciseMediaSpec("Standing_Calf_Raises", MuscleGroupZone.LEGS_GLUTES),

        "y3_1" to ExerciseMediaSpec("Bodyweight_Squat", MuscleGroupZone.LEGS_GLUTES),
        "y3_2" to ExerciseMediaSpec("Push-Ups_-_Close_Triceps_Position", MuscleGroupZone.CHEST),
        "y3_3" to ExerciseMediaSpec("Inverted_Row", MuscleGroupZone.BACK),

        "y4_1" to ExerciseMediaSpec("Neck_Stretch", MuscleGroupZone.SHOULDERS),
        "y4_2" to ExerciseMediaSpec("Chest_And_Front_Of_Shoulder_Stretch", MuscleGroupZone.CHEST),
        "y4_3" to ExerciseMediaSpec("Pushups", MuscleGroupZone.SHOULDERS),

        "y5_1" to ExerciseMediaSpec("Lateral_Cone_Hops", MuscleGroupZone.LEGS_GLUTES),
        "y5_2" to ExerciseMediaSpec("Dumbbell_Single_Leg_Split_Squat", MuscleGroupZone.LEGS_GLUTES),
        "y5_3" to ExerciseMediaSpec("Mountain_Climbers", MuscleGroupZone.FULL_BODY),

        "y6_1" to ExerciseMediaSpec("Cocoons", MuscleGroupZone.CORE),
        "y6_2" to ExerciseMediaSpec("Plank", MuscleGroupZone.CORE),
        "y6_3" to ExerciseMediaSpec("Barbell_Glute_Bridge", MuscleGroupZone.LEGS_GLUTES),

        "y7_1" to ExerciseMediaSpec("Rope_Jumping", MuscleGroupZone.FULL_BODY),
        "y7_2" to ExerciseMediaSpec("Bodyweight_Lunges", MuscleGroupZone.LEGS_GLUTES),
        "y7_3" to ExerciseMediaSpec("Childs_Pose", MuscleGroupZone.FULL_BODY),

        "yp1_1" to ExerciseMediaSpec("Pullups", MuscleGroupZone.BACK),
        "yp1_2" to ExerciseMediaSpec("Depth_Jump_Leap", MuscleGroupZone.LEGS_GLUTES),
        "yp1_3" to ExerciseMediaSpec("Pushups", MuscleGroupZone.CHEST)
    )

    fun mediaFor(exerciseId: String): ExerciseMediaSpec =
        entries[exerciseId]
            ?: AzExerciseDatabase.findById(exerciseId)?.let {
                ExerciseMediaSpec(it.dbFolderId, it.muscleZone)
            }
            ?: ExerciseMediaSpec("Bodyweight_Squat", MuscleGroupZone.FULL_BODY)

    fun allMappedIds(): Set<String> =
        entries.keys + AzExerciseDatabase.ALL_EXERCISES.map { it.item.id }
}
