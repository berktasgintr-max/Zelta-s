package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.catalog.CoachKnowledgeBase
import com.example.data.catalog.CoachWisdomAndStories
import com.example.data.catalog.FitnessFoodDatabase
import com.example.data.catalog.FoodCategory
import com.example.data.model.UserProfile
import com.example.ui.components.CoachAnimationMood
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocaleHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OfflineCoachAndNutritionTest {

    @Test
    fun `offline food database contains 250 plus foods with valid macros and accurate gram scaling`() {
        val foods = FitnessFoodDatabase.ALL_FOODS
        assertTrue("Expected at least 250 foods, got ${foods.size}", foods.size >= 250)
        assertEquals(foods.size, foods.map { it.id }.toSet().size)

        foods.forEach { food ->
            assertTrue("${food.id} nameEn blank", food.nameEn.isNotBlank())
            assertTrue("${food.id} nameTr blank", food.nameTr.isNotBlank())
            assertTrue("${food.id} calories negative", food.caloriesPer100g >= 0)
            assertTrue("${food.id} protein negative", food.proteinPer100g >= 0.0)
            assertTrue("${food.id} carbs negative", food.carbsPer100g >= 0.0)
            assertTrue("${food.id} fat negative", food.fatPer100g >= 0.0)
        }

        val chicken = FitnessFoodDatabase.findById("food_chicken_breast_grilled")!!
        val scaled200 = chicken.scaleForGrams(200)
        assertEquals(200, scaled200.grams)
        assertEquals(330, scaled200.calories)
        assertEquals(62, scaled200.proteinGrams)

        val enMatches = FitnessFoodDatabase.search("salmon", FoodCategory.FISH_SEAFOOD)
        assertTrue(enMatches.any { it.id == "food_salmon_grilled" })

        val trMatches = FitnessFoodDatabase.search("tavuk", FoodCategory.POULTRY_MEAT)
        assertTrue(trMatches.any { it.id == "food_chicken_breast_grilled" })
    }

    @Test
    fun `mascot nutrition feedback adapts across empty, protein deficit, sweet spot, and over budget`() {
        val profile = UserProfile(currentWeightKg = 80.0, dailyCalorieTarget = 2200)

        val empty = FitnessFoodDatabase.evaluateDailyNutrition(profile, 0, 0, "en")
        assertEquals(CoachAnimationMood.IDLE, empty.mood)

        val needProtein = FitnessFoodDatabase.evaluateDailyNutrition(profile, 1400, 30, "en")
        assertEquals(CoachAnimationMood.COACHING, needProtein.mood)

        val sweetSpot = FitnessFoodDatabase.evaluateDailyNutrition(profile, 2100, 145, "en")
        assertEquals(CoachAnimationMood.CHEERING, sweetSpot.mood)

        val overBudget = FitnessFoodDatabase.evaluateDailyNutrition(profile, 2900, 150, "tr")
        assertEquals(CoachAnimationMood.COACHING, overBudget.mood)
        assertTrue(overBudget.message.isNotBlank())
    }

    @Test
    fun `offline rule based coach answers diverse topics instantaneously in EN and TR`() {
        val enProfile = UserProfile(
            displayName = "Alex",
            appLanguage = "en",
            currentWeightKg = 78.0,
            targetWeightKg = 74.0,
            dailyCalorieTarget = 2100
        )
        val trYouthProfile = UserProfile(
            displayName = "Deniz",
            age = 15,
            appLanguage = "tr",
            goal = "growth_posture",
            currentWeightKg = 62.0,
            targetWeightKg = 66.0,
            dailyCalorieTarget = 2300
        )

        val proteinReply = CoachKnowledgeBase.generateLocalReply("How much protein do I need?", enProfile)
        val fatLossReply = CoachKnowledgeBase.generateLocalReply("Best way to burn belly fat?", enProfile)
        val chestReply = CoachKnowledgeBase.generateLocalReply("How to grow chest on bench press?", enProfile)
        val legsReply = CoachKnowledgeBase.generateLocalReply("Best squat tips for leg day?", enProfile)
        val recoveryReply = CoachKnowledgeBase.generateLocalReply("How to improve sleep recovery?", enProfile)

        listOf(proteinReply, fatLossReply, chestReply, legsReply, recoveryReply).forEach { reply ->
            assertTrue("Expected substantial reply, got: $reply", reply.length > 40)
            assertFalse("Unresolved template token in: $reply", reply.contains("{NAME}") || reply.contains("{KCAL}"))
        }
        assertTrue(proteinReply.contains("protein", ignoreCase = true))
        assertTrue(fatLossReply.contains("fat", ignoreCase = true) || fatLossReply.contains("kcal", ignoreCase = true))
        assertTrue(chestReply.contains("pec", ignoreCase = true) || chestReply.contains("chest", ignoreCase = true))
        assertTrue(legsReply.contains("squat", ignoreCase = true) || legsReply.contains("leg", ignoreCase = true))
        assertTrue(recoveryReply.contains("sleep", ignoreCase = true) || recoveryReply.contains("rest", ignoreCase = true))
        assertNotEquals(proteinReply, fatLossReply)
        assertNotEquals(chestReply, legsReply)

        val postureTrReply = CoachKnowledgeBase.generateLocalReply("Boy uzaması ve duruş için esneme?", trYouthProfile)
        assertTrue(postureTrReply.contains("0–18") || postureTrReply.contains("postür", ignoreCase = true))
        assertTrue(postureTrReply.length > 40)
    }

    @Test
    fun `all 10 locales provide non-AI branded offline labels and complete formatter strings`() {
        val baseContext = ApplicationProvider.getApplicationContext<Context>()

        AppLanguage.entries.forEach { lang ->
            val ctx = LocaleHelper.localizedContext(baseContext, lang.code)
            val checkedStrings = listOf(
                ctx.getString(R.string.nav_ai),
                ctx.getString(R.string.ask_ai_title),
                ctx.getString(R.string.home_calorie_title),
                ctx.getString(R.string.tab_calories),
                ctx.getString(R.string.home_ai_card_title),
                ctx.getString(R.string.macro_search_hint),
                ctx.getString(R.string.wheel_spins_exhausted_today),
                ctx.getString(R.string.wheel_extra_spin_ad_btn),
                ctx.getString(R.string.badges_hide_btn),
                ctx.getString(R.string.badges_show_fmt, 27),
                ctx.getString(R.string.msg_food_logged, "Chicken Breast", 248, 25),
                ctx.getString(R.string.macro_per_100g_fmt, 165, "31", "0", "3.6")
            )
            checkedStrings.forEach { str ->
                assertTrue("Blank localized string in ${lang.code}", str.isNotBlank())
                assertFalse("Unexpected AI-Powered label in ${lang.code}: $str", str.contains("AI-Powered", ignoreCase = true))
                assertFalse("Unexpected AI Destekli label in ${lang.code}: $str", str.contains("AI Destekli", ignoreCase = true))
            }

            val stories = CoachWisdomAndStories.stories(ctx)
            stories.forEach { story ->
                assertFalse("Unexpected FitChicken AI in ${lang.code}: ${story.quote}", story.quote.contains("FitChicken AI"))
            }
        }
    }
}
