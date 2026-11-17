package com.example.data.local

import com.example.data.catalog.AzExerciseDatabase
import com.example.data.catalog.ExerciseMediaCatalog
import com.example.data.model.ExerciseDemoType
import com.example.data.model.ExerciseItem
import com.example.data.model.TargetGroup
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram
import org.json.JSONArray
import org.json.JSONObject

object CustomWorkoutMapper {

    private const val BASE_URL =
        "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises"

    fun encodeExercises(exercises: List<ExerciseItem>): String {
        val arr = JSONArray()
        exercises.forEach { ex ->
            val obj = JSONObject()
            obj.put("id", ex.id)
            obj.put("name", ex.name)
            obj.put("targetMuscle", ex.targetMuscle)
            obj.put("setsReps", ex.setsReps)
            obj.put("restSeconds", ex.restSeconds)
            obj.put("demoType", ex.demoType.name)
            obj.put("steps", JSONArray(ex.steps))
            obj.put("formCue", ex.formCue)
            obj.put("breathingCue", ex.breathingCue)
            obj.put("commonMistake", ex.commonMistake)
            obj.put("targetGroup", ex.targetGroup.name)
            obj.put("secondaryMuscles", ex.secondaryMuscles)
            obj.put("nameTr", ex.nameTr)
            obj.put("targetMuscleTr", ex.targetMuscleTr)
            obj.put("secondaryMusclesTr", ex.secondaryMusclesTr)
            obj.put("stepsTr", JSONArray(ex.stepsTr))
            obj.put("formCueTr", ex.formCueTr)
            obj.put("breathingCueTr", ex.breathingCueTr)
            obj.put("commonMistakeTr", ex.commonMistakeTr)
            arr.put(obj)
        }
        return arr.toString()
    }

    fun decodeExercises(json: String): List<ExerciseItem> {
        if (json.isBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(json)
            buildList(arr.length()) {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val id = obj.optString("id", "az_pushup")
                    val azFallback = AzExerciseDatabase.findById(id)?.item

                    val demoName = obj.optString("demoType", ExerciseDemoType.PUSHUP.name)
                    val demoType = runCatching { ExerciseDemoType.valueOf(demoName) }
                        .getOrDefault(azFallback?.demoType ?: ExerciseDemoType.PUSHUP)

                    val groupName = obj.optString("targetGroup", TargetGroup.BOTH.name)
                    val targetGroup = runCatching { TargetGroup.valueOf(groupName) }
                        .getOrDefault(TargetGroup.BOTH)

                    val steps = obj.optJSONArray("steps")?.let { sArr ->
                        List(sArr.length()) { idx -> sArr.optString(idx) }.filter { it.isNotBlank() }
                    }.orEmpty().ifEmpty { azFallback?.steps ?: listOf("Maintain controlled form.") }

                    val stepsTr = obj.optJSONArray("stepsTr")?.let { sArr ->
                        List(sArr.length()) { idx -> sArr.optString(idx) }.filter { it.isNotBlank() }
                    }.orEmpty().ifEmpty { azFallback?.stepsTr ?: emptyList() }

                    add(
                        ExerciseItem(
                            id = id,
                            name = obj.optString("name", azFallback?.name ?: id),
                            targetMuscle = obj.optString("targetMuscle", azFallback?.targetMuscle ?: "Full Body"),
                            setsReps = obj.optString("setsReps", azFallback?.setsReps ?: "3 x 12"),
                            restSeconds = obj.optInt("restSeconds", azFallback?.restSeconds ?: 60),
                            demoType = demoType,
                            steps = steps,
                            formCue = obj.optString("formCue", azFallback?.formCue ?: "Controlled tempo"),
                            breathingCue = obj.optString("breathingCue", azFallback?.breathingCue ?: "Steady breathing"),
                            commonMistake = obj.optString("commonMistake", azFallback?.commonMistake ?: "Rushing reps"),
                            targetGroup = targetGroup,
                            secondaryMuscles = obj.optString("secondaryMuscles", azFallback?.secondaryMuscles ?: ""),
                            nameTr = obj.optString("nameTr", azFallback?.nameTr ?: ""),
                            targetMuscleTr = obj.optString("targetMuscleTr", azFallback?.targetMuscleTr ?: ""),
                            secondaryMusclesTr = obj.optString("secondaryMusclesTr", azFallback?.secondaryMusclesTr ?: ""),
                            stepsTr = stepsTr,
                            formCueTr = obj.optString("formCueTr", azFallback?.formCueTr ?: ""),
                            breathingCueTr = obj.optString("breathingCueTr", azFallback?.breathingCueTr ?: ""),
                            commonMistakeTr = obj.optString("commonMistakeTr", azFallback?.commonMistakeTr ?: "")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun toEntity(program: WorkoutProgram, createdAtMillis: Long = System.currentTimeMillis()): CustomWorkoutEntity {
        return CustomWorkoutEntity(
            id = program.id,
            title = program.title,
            categoryName = program.category.name,
            focusArea = program.focusArea,
            difficulty = program.difficulty,
            durationMinutes = program.durationMinutes,
            estimatedCalories = program.estimatedCalories,
            exercisesJson = encodeExercises(program.exercises),
            heroImageUrl = program.heroImageUrl,
            imagePrompt = program.imagePrompt,
            createdAtMillis = createdAtMillis
        )
    }

    fun toProgram(entity: CustomWorkoutEntity): WorkoutProgram {
        val exercises = decodeExercises(entity.exercisesJson)
        val category = runCatching { WorkoutCategory.valueOf(entity.categoryName) }
            .getOrDefault(WorkoutCategory.HOME)
        val leadFolder = exercises.firstOrNull()?.id?.let { ExerciseMediaCatalog.mediaFor(it).dbFolderId }
            ?: "Bodyweight_Squat"
        val resolvedHeroUrl = entity.heroImageUrl.ifBlank { "$BASE_URL/$leadFolder/images/0.jpg" }
        val resolvedPrompt = entity.imagePrompt.ifBlank {
            "Custom FitChicken athlete routine '${entity.title}' featuring ${exercises.joinToString(", ") { it.name }}"
        }

        return WorkoutProgram(
            id = entity.id,
            title = entity.title,
            subtitle = "${exercises.size} custom exercises • FitChicken Custom Studio",
            subtitleTr = "${exercises.size} özel egzersiz • FitChicken Özel Program",
            category = category,
            focusArea = entity.focusArea,
            difficulty = entity.difficulty,
            durationMinutes = entity.durationMinutes,
            estimatedCalories = entity.estimatedCalories,
            isProSpecial = false,
            isRecommendedForYouth = category == WorkoutCategory.YOUTH,
            exercises = exercises,
            targetGroup = TargetGroup.BOTH,
            imagePrompt = resolvedPrompt,
            heroImageUrl = resolvedHeroUrl,
            isCustom = true,
            titleTr = entity.title
        )
    }
}
