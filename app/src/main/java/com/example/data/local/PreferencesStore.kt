package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.UserProfile
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "tavuk_koc_prefs")

class PreferencesStore(private val context: Context) {

    private val syncPrefs by lazy {
        context.getSharedPreferences("tavuk_koc_theme_sync", Context.MODE_PRIVATE)
    }

    fun getCachedThemeId(): String =
        syncPrefs.getString("theme_id", "light_orange") ?: "light_orange"

    fun cacheThemeSync(themeId: String) {
        syncPrefs.edit().putString("theme_id", themeId).apply()
    }

    private object Keys {
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val AGE = intPreferencesKey("age")
        val HEIGHT_CM = intPreferencesKey("height_cm")
        val START_WEIGHT = doublePreferencesKey("start_weight")
        val CURRENT_WEIGHT = doublePreferencesKey("current_weight")
        val TARGET_WEIGHT = doublePreferencesKey("target_weight")
        val GOAL = stringPreferencesKey("goal")
        val ENVIRONMENT = stringPreferencesKey("environment")
        val ACTIVITY_LEVEL = stringPreferencesKey("activity_level")
        val EXPERIENCE_LEVEL = stringPreferencesKey("experience_level")
        val WEEKLY_DAYS = intPreferencesKey("weekly_days")
        val SESSION_MINUTES = intPreferencesKey("session_minutes")
        val FOCUS_AREA = stringPreferencesKey("focus_area")
        val SLEEP_QUALITY = stringPreferencesKey("sleep_quality")
        val NUTRITION_HABIT = stringPreferencesKey("nutrition_habit")
        val PLAN_TIER = stringPreferencesKey("plan_tier")
        val PRO_SOURCE = stringPreferencesKey("pro_source")
        val PRO_EXPIRES_AT = longPreferencesKey("pro_expires_at")
        val INVITE_CODE = stringPreferencesKey("invite_code")
        val REFERRAL_COUNT = intPreferencesKey("referral_count")
        val REFERRED_BY_CODE = stringPreferencesKey("referred_by_code")
        val AI_USED_TODAY = intPreferencesKey("ai_used_today")
        val AI_QUOTA_DATE = stringPreferencesKey("ai_quota_date")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME_ID = stringPreferencesKey("theme_id")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val LAST_CHECK_IN_DATE = stringPreferencesKey("last_check_in_date")
        val TOTAL_XP = intPreferencesKey("total_xp")
        val DAILY_QUESTS_DATE = stringPreferencesKey("daily_quests_date")
        val QUEST_COACH_DONE = booleanPreferencesKey("quest_coach_done")
        val QUEST_CHALLENGE_DONE = booleanPreferencesKey("quest_challenge_done")
        val QUEST_HYDRATION_DONE = booleanPreferencesKey("quest_hydration_done")
        val WORKOUTS_COMPLETED_COUNT = intPreferencesKey("workouts_completed_count")
        val GENDER = stringPreferencesKey("gender")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val IS_LANGUAGE_SELECTED = booleanPreferencesKey("is_language_selected")
        val LAST_WHEEL_SPIN_DATE = stringPreferencesKey("last_wheel_spin_date")
        val BEST_STREAK_DAYS = intPreferencesKey("best_streak_days")
        val FOCUS_AREAS_CSV = stringPreferencesKey("focus_areas_csv")
        val BODY_FAT_CURRENT = intPreferencesKey("body_fat_current")
        val BODY_FAT_TARGET = intPreferencesKey("body_fat_target")
        val DAILY_CALORIE_TARGET = intPreferencesKey("daily_calorie_target")
        val STRESS_LEVEL = stringPreferencesKey("stress_level")
        val BAD_HABITS_CSV = stringPreferencesKey("bad_habits_csv")
        val SQUAT_CAPACITY = stringPreferencesKey("squat_capacity")
        val WANT_BETTER_SLEEP = booleanPreferencesKey("want_better_sleep")
        val WANT_LESS_STRESS = booleanPreferencesKey("want_less_stress")
        val MOTIVATION_LEVEL = stringPreferencesKey("motivation_level")
        val PLAN_PACE = stringPreferencesKey("plan_pace")
        val TRAINING_DAYS_CSV = stringPreferencesKey("training_days_csv")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val TARGET_DATE_ISO = stringPreferencesKey("target_date_iso")
        val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        val MEAL_SCANS_COUNT = intPreferencesKey("meal_scans_count")
        val DAILY_SPIN_COUNT = intPreferencesKey("daily_spin_count")
    }

    val profileFlow: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        val existingCode = prefs[Keys.INVITE_CODE] ?: generateDefaultCode()
        val onboardingDone = prefs[Keys.ONBOARDING_COMPLETED] ?: false
        UserProfile(
            displayName = prefs[Keys.DISPLAY_NAME] ?: "Athlete",
            age = prefs[Keys.AGE] ?: 22,
            heightCm = prefs[Keys.HEIGHT_CM] ?: 175,
            startWeightKg = prefs[Keys.START_WEIGHT] ?: 75.0,
            currentWeightKg = prefs[Keys.CURRENT_WEIGHT] ?: 75.0,
            targetWeightKg = prefs[Keys.TARGET_WEIGHT] ?: 72.0,
            goal = prefs[Keys.GOAL] ?: "fitness",
            environment = prefs[Keys.ENVIRONMENT] ?: "hybrid",
            activityLevel = prefs[Keys.ACTIVITY_LEVEL] ?: "moderate",
            experienceLevel = prefs[Keys.EXPERIENCE_LEVEL] ?: "occasional",
            weeklyDays = prefs[Keys.WEEKLY_DAYS] ?: 4,
            sessionMinutes = prefs[Keys.SESSION_MINUTES] ?: 40,
            focusArea = prefs[Keys.FOCUS_AREA] ?: "full_body",
            sleepQuality = prefs[Keys.SLEEP_QUALITY] ?: "7-8 saat düzenli",
            nutritionHabit = prefs[Keys.NUTRITION_HABIT] ?: "Dengeli ama geliştirilebilir",
            planTier = prefs[Keys.PLAN_TIER] ?: "free",
            proSource = prefs[Keys.PRO_SOURCE] ?: "none",
            proExpiresAtMillis = prefs[Keys.PRO_EXPIRES_AT] ?: 0L,
            inviteCode = existingCode,
            referralCount = prefs[Keys.REFERRAL_COUNT] ?: 0,
            referredByCode = prefs[Keys.REFERRED_BY_CODE] ?: "",
            aiQuestionsUsedToday = prefs[Keys.AI_USED_TODAY] ?: 0,
            aiQuotaDate = prefs[Keys.AI_QUOTA_DATE] ?: "",
            onboardingCompleted = onboardingDone,
            themeId = prefs[Keys.THEME_ID] ?: "light_orange",
            streakDays = prefs[Keys.STREAK_DAYS] ?: 0,
            lastCheckInDate = prefs[Keys.LAST_CHECK_IN_DATE] ?: "",
            totalXp = prefs[Keys.TOTAL_XP] ?: 0,
            dailyQuestsDate = prefs[Keys.DAILY_QUESTS_DATE] ?: "",
            questCoachDone = prefs[Keys.QUEST_COACH_DONE] ?: false,
            questChallengeDone = prefs[Keys.QUEST_CHALLENGE_DONE] ?: false,
            questHydrationDone = prefs[Keys.QUEST_HYDRATION_DONE] ?: false,
            workoutsCompletedCount = prefs[Keys.WORKOUTS_COMPLETED_COUNT] ?: 0,
            gender = prefs[Keys.GENDER] ?: "male",
            appLanguage = prefs[Keys.APP_LANGUAGE] ?: AppLanguage.systemDefaultCode(),
            isLanguageSelected = true,
            lastWheelSpinDate = prefs[Keys.LAST_WHEEL_SPIN_DATE] ?: "",
            bestStreakDays = prefs[Keys.BEST_STREAK_DAYS] ?: 0,
            focusAreasCsv = prefs[Keys.FOCUS_AREAS_CSV] ?: "",
            bodyFatCurrentPct = prefs[Keys.BODY_FAT_CURRENT] ?: 22,
            bodyFatTargetPct = prefs[Keys.BODY_FAT_TARGET] ?: 15,
            dailyCalorieTarget = prefs[Keys.DAILY_CALORIE_TARGET] ?: 2100,
            stressLevel = prefs[Keys.STRESS_LEVEL] ?: "moderate",
            badHabitsCsv = prefs[Keys.BAD_HABITS_CSV] ?: "",
            squatCapacity = prefs[Keys.SQUAT_CAPACITY] ?: "10_20",
            wantBetterSleep = prefs[Keys.WANT_BETTER_SLEEP] ?: true,
            wantLessStress = prefs[Keys.WANT_LESS_STRESS] ?: true,
            motivationLevel = prefs[Keys.MOTIVATION_LEVEL] ?: "high",
            planPace = prefs[Keys.PLAN_PACE] ?: "balanced",
            trainingDaysCsv = prefs[Keys.TRAINING_DAYS_CSV] ?: "mon,wed,fri,sat",
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: false,
            targetDateIso = prefs[Keys.TARGET_DATE_ISO] ?: "",
            weightUnit = prefs[Keys.WEIGHT_UNIT] ?: "kg",
            mealScansCount = prefs[Keys.MEAL_SCANS_COUNT] ?: 0,
            dailySpinCount = prefs[Keys.DAILY_SPIN_COUNT] ?: 0
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        cacheThemeSync(profile.themeId)
        context.dataStore.edit { prefs ->
            prefs[Keys.DISPLAY_NAME] = profile.displayName
            prefs[Keys.AGE] = profile.age
            prefs[Keys.HEIGHT_CM] = profile.heightCm
            prefs[Keys.START_WEIGHT] = profile.startWeightKg
            prefs[Keys.CURRENT_WEIGHT] = profile.currentWeightKg
            prefs[Keys.TARGET_WEIGHT] = profile.targetWeightKg
            prefs[Keys.GOAL] = profile.goal
            prefs[Keys.ENVIRONMENT] = profile.environment
            prefs[Keys.ACTIVITY_LEVEL] = profile.activityLevel
            prefs[Keys.EXPERIENCE_LEVEL] = profile.experienceLevel
            prefs[Keys.WEEKLY_DAYS] = profile.weeklyDays
            prefs[Keys.SESSION_MINUTES] = profile.sessionMinutes
            prefs[Keys.FOCUS_AREA] = profile.focusArea
            prefs[Keys.SLEEP_QUALITY] = profile.sleepQuality
            prefs[Keys.NUTRITION_HABIT] = profile.nutritionHabit
            prefs[Keys.PLAN_TIER] = profile.planTier
            prefs[Keys.PRO_SOURCE] = profile.proSource
            prefs[Keys.PRO_EXPIRES_AT] = profile.proExpiresAtMillis
            prefs[Keys.INVITE_CODE] = profile.inviteCode
            prefs[Keys.REFERRAL_COUNT] = profile.referralCount
            prefs[Keys.REFERRED_BY_CODE] = profile.referredByCode
            prefs[Keys.AI_USED_TODAY] = profile.aiQuestionsUsedToday
            prefs[Keys.AI_QUOTA_DATE] = profile.aiQuotaDate
            prefs[Keys.ONBOARDING_COMPLETED] = profile.onboardingCompleted
            prefs[Keys.THEME_ID] = profile.themeId
            prefs[Keys.STREAK_DAYS] = profile.streakDays
            prefs[Keys.LAST_CHECK_IN_DATE] = profile.lastCheckInDate
            prefs[Keys.TOTAL_XP] = profile.totalXp
            prefs[Keys.DAILY_QUESTS_DATE] = profile.dailyQuestsDate
            prefs[Keys.QUEST_COACH_DONE] = profile.questCoachDone
            prefs[Keys.QUEST_CHALLENGE_DONE] = profile.questChallengeDone
            prefs[Keys.QUEST_HYDRATION_DONE] = profile.questHydrationDone
            prefs[Keys.WORKOUTS_COMPLETED_COUNT] = profile.workoutsCompletedCount
            prefs[Keys.GENDER] = profile.gender
            prefs[Keys.APP_LANGUAGE] = profile.appLanguage
            prefs[Keys.IS_LANGUAGE_SELECTED] = true
            prefs[Keys.LAST_WHEEL_SPIN_DATE] = profile.lastWheelSpinDate
            prefs[Keys.BEST_STREAK_DAYS] = profile.bestStreakDays
            prefs[Keys.FOCUS_AREAS_CSV] = profile.focusAreasCsv
            prefs[Keys.BODY_FAT_CURRENT] = profile.bodyFatCurrentPct
            prefs[Keys.BODY_FAT_TARGET] = profile.bodyFatTargetPct
            prefs[Keys.DAILY_CALORIE_TARGET] = profile.dailyCalorieTarget
            prefs[Keys.STRESS_LEVEL] = profile.stressLevel
            prefs[Keys.BAD_HABITS_CSV] = profile.badHabitsCsv
            prefs[Keys.SQUAT_CAPACITY] = profile.squatCapacity
            prefs[Keys.WANT_BETTER_SLEEP] = profile.wantBetterSleep
            prefs[Keys.WANT_LESS_STRESS] = profile.wantLessStress
            prefs[Keys.MOTIVATION_LEVEL] = profile.motivationLevel
            prefs[Keys.PLAN_PACE] = profile.planPace
            prefs[Keys.TRAINING_DAYS_CSV] = profile.trainingDaysCsv
            prefs[Keys.NOTIFICATIONS_ENABLED] = profile.notificationsEnabled
            prefs[Keys.TARGET_DATE_ISO] = profile.targetDateIso
            prefs[Keys.WEIGHT_UNIT] = profile.weightUnit
            prefs[Keys.MEAL_SCANS_COUNT] = profile.mealScansCount
            prefs[Keys.DAILY_SPIN_COUNT] = profile.dailySpinCount
        }
    }

    suspend fun ensureInviteCode(): String {
        var code = ""
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.INVITE_CODE]
            if (current.isNullOrBlank()) {
                val created = generateDefaultCode()
                prefs[Keys.INVITE_CODE] = created
                code = created
            } else {
                code = current
            }
        }
        return code
    }

    private fun generateDefaultCode(): String {
        val suffix = UUID.randomUUID().toString().replace("-", "").take(5).uppercase()
        return "FIT-$suffix"
    }
}
