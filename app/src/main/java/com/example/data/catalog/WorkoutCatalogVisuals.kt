package com.example.data.catalog

import com.example.data.model.ExerciseItem
import com.example.data.model.TargetGroup
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram

object WorkoutCatalogVisuals {

    private val visualPool: List<String> = listOf(
        "Barbell_Bench_Press_-_Medium_Grip", "Incline_Dumbbell_Press", "Dumbbell_Shoulder_Press",
        "Triceps_Pushdown_-_Rope_Attachment", "Wide-Grip_Lat_Pulldown", "Seated_Cable_Rows",
        "Face_Pull", "EZ-Bar_Curl", "Barbell_Squat", "Romanian_Deadlift", "Leg_Press",
        "Standing_Calf_Raises", "Goblet_Squat", "Machine_Bench_Press", "Leverage_Iso_Row",
        "Plank", "Dumbbell_Bench_Press", "One-Arm_Dumbbell_Row", "Side_Lateral_Raise",
        "Hammer_Curls", "Arnold_Dumbbell_Press", "Incline_Dumbbell_Curl", "EZ-Bar_Skullcrusher",
        "Barbell_Deadlift", "Barbell_Hip_Thrust", "Lying_Leg_Curls", "Hanging_Leg_Raise",
        "Rowing_Stationary", "One-Arm_Kettlebell_Swings", "Battling_Ropes", "Walking_Treadmill",
        "Dips_-_Chest_Version", "Butterfly", "T-Bar_Row_with_Handle",
        "Close-Grip_Front_Lat_Pulldown", "Dumbbell_Shrug", "Hack_Squat", "Dumbbell_Lunges",
        "Leg_Extensions", "Cable_Crunch", "Decline_Crunch", "Pallof_Press",
        "Smith_Machine_Incline_Bench_Press", "Cable_Crossover", "Pullups", "Dumbbell_Incline_Row",
        "Preacher_Curl", "Front_Barbell_Squat", "Split_Squats", "Front_Box_Jump",
        "Barbell_Full_Squat", "Barbell_Incline_Bench_Press_-_Medium_Grip",
        "Bent_Over_Barbell_Row", "Farmers_Walk", "Bodyweight_Squat", "Pushups",
        "Barbell_Glute_Bridge", "Mountain_Climbers", "Decline_Push-Up", "Close-Grip_Push-Up",
        "Air_Bike", "Flat_Bench_Lying_Leg_Raise", "Side_Bridge", "Dumbbell_Rear_Lunge",
        "Single_Leg_Glute_Bridge", "Jumping_Jacks", "Running_Treadmill",
        "Dumbbell_Squat_To_Shoulder_Press", "Bent_Over_Two-Dumbbell_Row", "Cat_Stretch",
        "World_Greatest_Stretch", "Bench_Dips", "Superman", "Freehand_Jump_Squat",
        "Side_to_Side_Box_Shuffle", "Inchworm", "Boxing_Bag", "Shoulder_Stretch",
        "Childs_Pose", "Piriformis_Stretch", "Hamstring_Stretch", "Single-Arm_Push-Up",
        "Inverted_Row", "Standing_Long_Jump", "Split_Jump", "Isometric_Wipers",
        "Plyo_Push-Up", "Jackknife_Sit-Up", "Upward_Stretch", "Side_Stretch",
        "Rope_Jumping", "Knee_Tuck_Jump", "Push-Ups_-_Close_Triceps_Position", "Neck_Stretch",
        "Chest_And_Front_Of_Shoulder_Stretch", "Lateral_Cone_Hops",
        "Dumbbell_Single_Leg_Split_Squat", "Cocoons", "Bodyweight_Lunges", "Depth_Jump_Leap",
        "Ab_Roller", "Barbell_Curl", "Dumbbell_Flyes", "Russian_Twist",
        "Seated_Calf_Raise", "Tricep_Dumbbell_Kickback", "Upright_Barbell_Row"
    )

    private const val BASE_URL =
        "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises"

    fun assignUniqueVisuals(programs: List<WorkoutProgram>): List<WorkoutProgram> {
        val usedUrls = HashSet<String>(programs.size * 2)
        val usedPrompts = HashSet<String>(programs.size * 2)

        return programs.mapIndexed { index, program ->
            val preferredFolder = program.exercises.firstOrNull()?.id?.let { exId ->
                ExerciseMediaCatalog.mediaFor(exId).dbFolderId
            }
            val url = pickUniqueUrl(index, preferredFolder, usedUrls)
            val prompt = buildUniqueImagePrompt(index, program, usedPrompts)
            program.copy(
                heroImageUrl = url,
                imagePrompt = prompt
            )
        }
    }

    private fun pickUniqueUrl(
        index: Int,
        preferredFolder: String?,
        usedUrls: MutableSet<String>
    ): String {
        val candidates = buildList {
            if (!preferredFolder.isNullOrBlank()) {
                add("$BASE_URL/$preferredFolder/images/0.jpg")
                add("$BASE_URL/$preferredFolder/images/1.jpg")
            }
            val poolSize = visualPool.size
            for (offset in 0 until poolSize) {
                val folder = visualPool[(index + offset) % poolSize]
                val primaryFrame = if ((index / poolSize) % 2 == 0) 0 else 1
                val altFrame = 1 - primaryFrame
                add("$BASE_URL/$folder/images/$primaryFrame.jpg")
                add("$BASE_URL/$folder/images/$altFrame.jpg")
            }
        }
        val chosen = candidates.firstOrNull { usedUrls.add(it) }
            ?: "$BASE_URL/${visualPool[index % visualPool.size]}/images/${index % 2}.jpg".also { usedUrls.add(it) }
        return chosen
    }

    private fun buildUniqueImagePrompt(
        index: Int,
        program: WorkoutProgram,
        usedPrompts: MutableSet<String>
    ): String {
        val scene = when (program.category) {
            WorkoutCategory.GYM -> "modern dark-slate athletic performance gym with warm amber rim lighting"
            WorkoutCategory.HOME -> "sunlit minimalist home training studio with matte rubber floor"
            WorkoutCategory.YOUTH -> "bright collegiate indoor track & posture studio with natural daylight"
        }
        val athleteTag = when (program.targetGroup) {
            TargetGroup.MALE_ONLY -> "male athlete"
            TargetGroup.FEMALE_ONLY -> "female athlete"
            TargetGroup.BOTH -> "athletic performer"
        }
        val leadExercise = program.exercises.firstOrNull()?.name ?: program.focusArea
        val tierStyle = if (program.isProSpecial) "PRO championship editorial shot" else "clean sports photography"
        val paletteTone = listOf(
            "burnt-orange rim light", "crisp charcoal contrast", "kinetic motion blur accent",
            "deep studio spotlight", "warm golden-hour flare", "editorial monochrome highlight"
        )[index % 6]

        var prompt = "#${index + 1} [${program.id}] $tierStyle of a $athleteTag performing $leadExercise for '${program.title}' (${program.focusArea}, ${program.difficulty}) in a $scene, $paletteTone"
        if (!usedPrompts.add(prompt)) {
            prompt = "$prompt • variation-${index + 1}"
            usedPrompts.add(prompt)
        }
        return prompt
    }

    internal fun buildProgram(
        id: String,
        titleEn: String,
        titleTr: String,
        subtitleEn: String,
        subtitleTr: String,
        category: WorkoutCategory,
        focusArea: String,
        difficulty: String,
        durationMinutes: Int,
        estimatedCalories: Int,
        isPro: Boolean,
        targetGroup: TargetGroup,
        exerciseIds: List<String>,
        isYouth: Boolean = category == WorkoutCategory.YOUTH
    ): WorkoutProgram {
        val resolvedExercises: List<ExerciseItem> = exerciseIds.distinct().mapNotNull { exId ->
            AzExerciseDatabase.findById(exId)?.item
        }
        return WorkoutProgram(
            id = id,
            title = titleEn,
            subtitle = subtitleEn,
            category = category,
            focusArea = focusArea,
            difficulty = difficulty,
            durationMinutes = durationMinutes,
            estimatedCalories = estimatedCalories,
            isProSpecial = isPro,
            isRecommendedForYouth = isYouth,
            exercises = resolvedExercises,
            targetGroup = targetGroup,
            titleTr = titleTr,
            subtitleTr = subtitleTr
        )
    }
}
