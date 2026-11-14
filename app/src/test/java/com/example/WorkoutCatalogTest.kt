package com.example

import com.example.data.catalog.CoachWisdomAndStories
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.model.WorkoutCategory
import com.example.ui.components.resolveExerciseMotion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutCatalogTest {

    @Test
    fun catalogHas40DistinctWorkoutsWithStepsAndTips() {
        val all = WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
        assertEquals(40, all.size)
        assertEquals(40, all.map { it.id }.distinct().size)

        val gymCount = all.count { it.category == WorkoutCategory.GYM }
        val homeCount = all.count { it.category == WorkoutCategory.HOME }
        val youthCount = all.count { it.category == WorkoutCategory.YOUTH }
        assertEquals(16, gymCount)
        assertEquals(16, homeCount)
        assertEquals(8, youthCount)

        // Every workout has exercises and every exercise has step-by-step instructions
        all.forEach { workout ->
            assertTrue("${workout.id} has no exercises", workout.exercises.isNotEmpty())
            workout.exercises.forEach { ex ->
                assertTrue("${ex.id} has no how-to steps", ex.steps.isNotEmpty())
            }
        }

        // Verify exercises resolve to a wide variety of distinct motion animations (40+)
        val distinctMotionKinds = all.flatMap { it.exercises }.map { resolveExerciseMotion(it).kind }.distinct()
        assertTrue("Expected at least 40 distinct exercise animations, got ${distinctMotionKinds.size}", distinctMotionKinds.size >= 40)

        // Youth workouts are tagged as recommended for 0-18
        assertTrue(all.filter { it.category == WorkoutCategory.YOUTH }.all { it.isRecommendedForYouth })

        // 40 interactive chicken tips
        assertEquals(40, CoachWisdomAndStories.COACH_TIPS.size)
    }
}
