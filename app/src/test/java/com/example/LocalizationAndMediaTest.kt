package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.catalog.AzExerciseDatabase
import com.example.data.catalog.CoachWisdomAndStories
import com.example.data.catalog.ExerciseMediaCatalog
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.catalog.localizedBreathingCue
import com.example.data.catalog.localizedCommonMistake
import com.example.data.catalog.localizedDifficulty
import com.example.data.catalog.localizedFormCue
import com.example.data.catalog.localizedName
import com.example.data.catalog.localizedSecondaryMuscles
import com.example.data.catalog.localizedSetsReps
import com.example.data.catalog.localizedSteps
import com.example.data.catalog.localizedSubtitle
import com.example.data.catalog.localizedTargetMuscle
import com.example.data.catalog.localizedTitle
import com.example.data.local.CustomWorkoutMapper
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocaleHelper
import com.example.ui.theme.AppThemeOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocalizationAndMediaTest {

    @Test
    fun `defaults auto-detect system language and use Light Orange mascot theme`() {
        val defaults = UserProfile()
        assertEquals(AppLanguage.systemDefaultCode(), defaults.appLanguage)
        assertTrue(defaults.isLanguageSelected)
        assertEquals("light_orange", defaults.themeId)
        assertEquals("", defaults.focusAreasCsv)
        assertEquals("kg", defaults.weightUnit)

        assertEquals(2, AppThemeOption.entries.size)
        assertEquals(listOf(AppThemeOption.LIGHT_ORANGE, AppThemeOption.DARK_ORANGE), AppThemeOption.entries)
        assertEquals(AppThemeOption.LIGHT_ORANGE, AppThemeOption.fromId("unknown"))
        assertEquals(AppThemeOption.LIGHT_ORANGE, AppThemeOption.fromId("light_blue"))
        assertEquals(AppThemeOption.DARK_ORANGE, AppThemeOption.fromId("dark_orange"))
    }

    @Test
    fun `100 XP leveling, mascot ranks, and badges evaluate accurately`() {
        val baseContext = ApplicationProvider.getApplicationContext<Context>()
        val trCtx = LocaleHelper.localizedContext(baseContext, "tr")
        val enCtx = LocaleHelper.localizedContext(baseContext, "en")

        val rookie = UserProfile(totalXp = 99)
        assertEquals(1, rookie.chickenLevel)
        assertEquals(99, rookie.xpInCurrentLevel)
        assertEquals("Rookie Chick", enCtx.getString(rookie.chickenRankTitleRes()))
        assertEquals("Çırak Civciv", trCtx.getString(rookie.chickenRankTitleRes()))

        val level2 = UserProfile(totalXp = 100)
        assertEquals(2, level2.chickenLevel)
        assertEquals(0, level2.xpInCurrentLevel)

        val gymBro = UserProfile(totalXp = 500)
        assertEquals(6, gymBro.chickenLevel)
        assertEquals("Gym Bro Chicken", enCtx.getString(gymBro.chickenRankTitleRes()))
        assertEquals("Gym Bro Tavuk", trCtx.getString(gymBro.chickenRankTitleRes()))

        val ironRooster = UserProfile(totalXp = 1500)
        assertEquals(16, ironRooster.chickenLevel)
        assertEquals("Iron Rooster", enCtx.getString(ironRooster.chickenRankTitleRes()))
        assertEquals("Demir Horoz", trCtx.getString(ironRooster.chickenRankTitleRes()))

        val alphaBeast = UserProfile(totalXp = 3000)
        assertEquals(31, alphaBeast.chickenLevel)
        assertEquals("Alpha Beast Chicken", enCtx.getString(alphaBeast.chickenRankTitleRes()))
        assertEquals("Alfa Canavar Tavuk", trCtx.getString(alphaBeast.chickenRankTitleRes()))

        val lockedBadges = com.example.data.model.ChickenBadges.evaluate(UserProfile(), calorieLogsCount = 0)
        assertEquals(27, lockedBadges.size)
        assertTrue(lockedBadges.none { it.unlocked })

        val progressedProfile = UserProfile(
            totalXp = 500,
            workoutsCompletedCount = 1,
            streakDays = 3,
            mealScansCount = 1
        )
        val unlockedIds = com.example.data.model.ChickenBadges.evaluate(progressedProfile, calorieLogsCount = 0)
            .filter { it.unlocked }
            .map { it.id }
            .toSet()
        assertEquals(setOf("first_egg", "flame_chick", "smart_nutrition", "gym_bro_chicken"), unlockedIds)
    }

    @Test
    fun `core 40 workouts translate across all 10 languages and all 200 workouts resolve cleanly`() {
        val baseContext = ApplicationProvider.getApplicationContext<Context>()
        val coreWorkouts = WorkoutCatalogHomeAndYouth.CORE_40_WORKOUTS
        val allWorkouts = WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
        assertEquals(40, coreWorkouts.size)
        assertEquals(200, allWorkouts.size)
        val coreExerciseCount = coreWorkouts.sumOf { it.exercises.size }
        assertEquals(130, coreExerciseCount)

        AppLanguage.entries.forEach { lang ->
            val ctx = LocaleHelper.localizedContext(baseContext, lang.code)

            val scannerLabel = ctx.getString(R.string.nav_calorie_scanner)
            assertTrue("Blank nav_calorie_scanner in ${lang.code}", scannerLabel.isNotBlank())

            val tips = CoachWisdomAndStories.tips(ctx)
            assertEquals("Expected 40 tips in ${lang.code}", 40, tips.size)
            assertTrue(tips.all { it.isNotBlank() })

            val stories = CoachWisdomAndStories.stories(ctx)
            assertEquals("Expected 4 stories in ${lang.code}", 4, stories.size)
            assertTrue(stories.all { it.badge.isNotBlank() && it.resultSummary.isNotBlank() && it.quote.isNotBlank() })

            // Core 40 XML translations check across all 10 languages
            coreWorkouts.forEach { workout ->
                val title = workout.localizedTitle(ctx)
                val subtitle = workout.localizedSubtitle(ctx)
                val difficulty = workout.localizedDifficulty(ctx)
                assertTrue("Blank title for ${workout.id} in ${lang.code}", title.isNotBlank())
                assertTrue("Blank subtitle for ${workout.id} in ${lang.code}", subtitle.isNotBlank())
                assertTrue("Blank difficulty for ${workout.id} in ${lang.code}", difficulty.isNotBlank())
                if (lang != AppLanguage.TR) {
                    assertTrue(
                        "Workout ${workout.id} stayed untranslated Turkish in ${lang.code}: $title",
                        title != workout.title
                    )
                }

                workout.exercises.forEach { ex ->
                    assertTrue("Blank name for ${ex.id} in ${lang.code}", ex.localizedName(ctx).isNotBlank())
                    assertTrue("Blank muscle for ${ex.id} in ${lang.code}", ex.localizedTargetMuscle(ctx).isNotBlank())
                    assertTrue("Blank setsReps for ${ex.id} in ${lang.code}", ex.localizedSetsReps(ctx).isNotBlank())
                    val steps = ex.localizedSteps(ctx)
                    assertTrue("Empty steps for ${ex.id} in ${lang.code}", steps.isNotEmpty() && steps.all { it.isNotBlank() })
                    assertTrue("Blank formCue for ${ex.id} in ${lang.code}", ex.localizedFormCue(ctx).isNotBlank())
                    assertTrue("Blank breathingCue for ${ex.id} in ${lang.code}", ex.localizedBreathingCue(ctx).isNotBlank())
                    assertTrue("Blank commonMistake for ${ex.id} in ${lang.code}", ex.localizedCommonMistake(ctx).isNotBlank())
                }
            }
        }

        // All 200 workouts & A-Z exercises localize EN + TR completely
        val trCtx = LocaleHelper.localizedContext(baseContext, "tr")
        val enCtx = LocaleHelper.localizedContext(baseContext, "en")
        allWorkouts.forEach { workout ->
            assertTrue("${workout.id} blank EN title", workout.localizedTitle(enCtx).isNotBlank())
            assertTrue("${workout.id} blank TR title", workout.localizedTitle(trCtx).isNotBlank())
            assertTrue("${workout.id} blank EN difficulty", workout.localizedDifficulty(enCtx).isNotBlank())
            assertTrue("${workout.id} blank TR difficulty", workout.localizedDifficulty(trCtx).isNotBlank())
        }
        AzExerciseDatabase.ALL_EXERCISES.forEach { entry ->
            val ex = entry.item
            assertTrue("${ex.id} blank EN name", ex.localizedName(enCtx).isNotBlank())
            assertTrue("${ex.id} blank TR name", ex.localizedName(trCtx).isNotBlank())
            assertTrue("${ex.id} blank EN muscle", ex.localizedTargetMuscle(enCtx).isNotBlank())
            assertTrue("${ex.id} blank TR muscle", ex.localizedTargetMuscle(trCtx).isNotBlank())
            assertTrue("${ex.id} blank EN secondary", ex.localizedSecondaryMuscles(enCtx).isNotBlank())
            assertTrue("${ex.id} blank TR secondary", ex.localizedSecondaryMuscles(trCtx).isNotBlank())
        }
    }

    @Test
    fun `every exercise ID across 200 workouts and A-Z DB has a free-exercise-db media mapping and muscle zone`() {
        val workoutExIds = WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
            .flatMap { it.exercises }
            .map { it.id }
            .toSet()
        val azIds = AzExerciseDatabase.ALL_EXERCISES.map { it.item.id }.toSet()
        val allIds = workoutExIds + azIds

        val mappedIds = ExerciseMediaCatalog.allMappedIds()
        assertTrue("All exercise IDs must be mapped in ExerciseMediaCatalog", mappedIds.containsAll(allIds))

        allIds.forEach { id ->
            val spec = ExerciseMediaCatalog.mediaFor(id)
            assertTrue("$id startFrameUrl invalid", spec.startFrameUrl.endsWith("/images/0.jpg"))
            assertTrue("$id peakFrameUrl invalid", spec.peakFrameUrl.endsWith("/images/1.jpg"))
        }
    }

    @Test
    fun `custom workout mapper round-trips exercises and customized sets and reps cleanly`() {
        val sampleExercises = listOf(
            AzExerciseDatabase.findById("az_barbell_bench_press")!!.item.copy(setsReps = "4 x 8"),
            AzExerciseDatabase.findById("az_goblet_squat")!!.item.copy(setsReps = "3 x 12"),
            AzExerciseDatabase.findById("az_plank")!!.item.copy(setsReps = "3 x 45s")
        )
        val original = WorkoutProgram(
            id = "custom_test123",
            title = "Friday Mix",
            subtitle = "3 custom exercises",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "Intermediate",
            durationMinutes = 26,
            estimatedCalories = 208,
            exercises = sampleExercises,
            isCustom = true,
            heroImageUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Plank/images/0.jpg",
            imagePrompt = "Custom prompt",
            titleTr = "Friday Mix"
        )

        val entity = CustomWorkoutMapper.toEntity(original, createdAtMillis = 123456L)
        val decoded = CustomWorkoutMapper.toProgram(entity)

        assertTrue(decoded.isCustom)
        assertEquals("custom_test123", decoded.id)
        assertEquals("Friday Mix", decoded.title)
        assertEquals(WorkoutCategory.HOME, decoded.category)
        assertEquals(3, decoded.exercises.size)
        assertEquals("4 x 8", decoded.exercises[0].setsReps)
        assertEquals("az_goblet_squat", decoded.exercises[1].id)
        assertEquals("3 x 45s", decoded.exercises[2].setsReps)
    }
}
