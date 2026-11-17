package com.example

import com.example.data.catalog.AzExerciseDatabase
import com.example.data.catalog.CoachWisdomAndStories
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.model.TargetGroup
import com.example.data.model.WorkoutCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutCatalogTest {

    @Test
    fun catalogHas200DistinctWorkoutsWithExact80ProAnd120FreeTierAndUniqueVisuals() {
        val all = WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
        assertEquals(200, all.size)
        assertEquals(200, all.map { it.id }.distinct().size)

        val proCount = all.count { it.isProSpecial }
        val freeCount = all.count { !it.isProSpecial }
        assertEquals(80, proCount)
        assertEquals(120, freeCount)

        val gymCount = all.count { it.category == WorkoutCategory.GYM }
        val homeCount = all.count { it.category == WorkoutCategory.HOME }
        val youthCount = all.count { it.category == WorkoutCategory.YOUTH }
        assertEquals(80, gymCount)
        assertEquals(80, homeCount)
        assertEquals(40, youthCount)

        // Unique heroImageUrl and unique imagePrompt across all 200 workouts
        val distinctUrls = all.map { it.heroImageUrl.trim() }.filter { it.isNotEmpty() }.distinct()
        val distinctPrompts = all.map { it.imagePrompt.trim() }.filter { it.isNotEmpty() }.distinct()
        assertEquals(200, distinctUrls.size)
        assertEquals(200, distinctPrompts.size)

        val coreIds = WorkoutCatalogHomeAndYouth.CORE_40_WORKOUTS.map { it.id }.toSet()

        // Every workout has exercises with complete metadata and step-by-step instructions
        all.forEach { workout ->
            assertTrue("${workout.id} has no exercises", workout.exercises.isNotEmpty())
            if (!coreIds.contains(workout.id)) {
                assertTrue(
                    "Library workout ${workout.id} expected >= 4 resolved exercises, got ${workout.exercises.size}",
                    workout.exercises.size >= 4
                )
            }
            workout.exercises.forEach { ex ->
                assertTrue("${ex.id} has blank name", ex.name.isNotBlank())
                assertTrue("${ex.id} has blank targetMuscle", ex.targetMuscle.isNotBlank())
                assertTrue("${ex.id} has blank setsReps", ex.setsReps.isNotBlank())
                assertTrue("${ex.id} has no how-to steps", ex.steps.isNotEmpty())
                assertTrue("${ex.id} has blank formCue", ex.formCue.isNotBlank())
                assertTrue("${ex.id} has blank breathingCue", ex.breathingCue.isNotBlank())
                assertTrue("${ex.id} has blank commonMistake", ex.commonMistake.isNotBlank())
            }
        }

        // Youth workouts are tagged as recommended for 0-18
        assertTrue(all.filter { it.category == WorkoutCategory.YOUTH }.all { it.isRecommendedForYouth })

        // 40 interactive chicken tips
        assertEquals(40, CoachWisdomAndStories.COACH_TIPS.size)
    }

    @Test
    fun targetGroupGenderFilteringCoversBothMaleAndFemaleSplits() {
        val all = WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
        assertTrue(all.any { it.targetGroup == TargetGroup.BOTH })
        assertTrue(all.any { it.targetGroup == TargetGroup.MALE_ONLY })
        assertTrue(all.any { it.targetGroup == TargetGroup.FEMALE_ONLY })

        val malePool = all.filter { it.matchesGender("male") }
        val femalePool = all.filter { it.matchesGender("female") }
        assertTrue(malePool.none { it.targetGroup == TargetGroup.FEMALE_ONLY })
        assertTrue(femalePool.none { it.targetGroup == TargetGroup.MALE_ONLY })
        assertTrue(malePool.size >= 140 && femalePool.size >= 140)
    }

    @Test
    fun azExerciseDatabaseCoversEveryLetterAtoZWithMuscleMetadata() {
        val az = AzExerciseDatabase.ALL_EXERCISES
        assertTrue("Expected at least 60 A-Z exercises, got ${az.size}", az.size >= 60)

        val letters = az.mapNotNull { it.item.name.trim().firstOrNull()?.uppercaseChar() }.toSet()
        for (ch in 'A'..'Z') {
            assertTrue("Missing A-Z exercise starting with letter '$ch'", letters.contains(ch))
        }

        // Verify sorted A-Z
        val namesUpper = az.map { it.item.name.uppercase() }
        assertEquals(namesUpper.sorted(), namesUpper)

        az.forEach { entry ->
            assertTrue("${entry.item.id} missing secondaryMuscles", entry.item.secondaryMuscles.isNotBlank())
            assertTrue("${entry.item.id} missing targetMuscleTr", entry.item.targetMuscleTr.isNotBlank())
        }
    }
}
