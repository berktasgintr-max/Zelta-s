package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class WorkoutCategory(val labelTr: String, val labelEn: String) {
    GYM("Spor Salonu (Gym)", "Gym"),
    HOME("Evde Antrenman", "Home Workout"),
    YOUTH("0-18 Yaş Büyüme Çağı", "Ages 0-18 Growth");

    fun label(lang: String): String = if (lang == "en") labelEn else labelTr
}

enum class ExerciseDemoType {
    SQUAT,
    PUSHUP,
    PLANK,
    PULL,
    PRESS,
    LUNGE,
    JUMP,
    STRETCH,
    CURL,
    CARDIO
}

data class ExerciseItem(
    val id: String,
    val name: String,
    val targetMuscle: String,
    val setsReps: String,
    val restSeconds: Int,
    val demoType: ExerciseDemoType,
    val steps: List<String>,
    val formCue: String,
    val breathingCue: String,
    val commonMistake: String
)

data class WorkoutProgram(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: WorkoutCategory,
    val focusArea: String,
    val difficulty: String,
    val durationMinutes: Int,
    val estimatedCalories: Int,
    val isProSpecial: Boolean = false,
    val isRecommendedForYouth: Boolean = false,
    val exercises: List<ExerciseItem>
)

data class SuccessStory(
    val name: String,
    val age: Int,
    val badge: String,
    val resultSummary: String,
    val quote: String
)

@IgnoreExtraProperties
data class UserProfileDto(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Sporcu",
    val age: Int = 22,
    val heightCm: Int = 175,
    val startWeightKg: Double = 75.0,
    val currentWeightKg: Double = 75.0,
    val targetWeightKg: Double = 72.0,
    val goal: String = "fitness",
    val environment: String = "hybrid",
    val activityLevel: String = "moderate",
    val experienceLevel: String = "occasional",
    val weeklyDays: Int = 4,
    val sessionMinutes: Int = 40,
    val focusArea: String = "full_body",
    val planTier: String = "free",
    val proSource: String = "none",
    val proExpiresAtMillis: Long = 0L,
    val inviteCode: String = "TAVUK-1001",
    val referralCount: Int = 0,
    val referredByCode: String = "",
    val aiQuestionsUsedToday: Int = 0,
    val aiQuotaDate: String = "",
    val onboardingCompleted: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class WeightLogDto(
    @DocumentId val id: String = "",
    val weightKg: Double = 0.0,
    val note: String = "",
    val loggedAtMillis: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CalendarEntryDto(
    @DocumentId val id: String = "",
    val dateKey: String = "",
    val workoutId: String = "",
    val workoutTitle: String = "",
    val completed: Boolean = false,
    val durationMinutes: Int = 30,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ReferralCodeDto(
    @DocumentId val id: String = "",
    val ownerUid: String = "",
    val code: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ReferralRedemptionDto(
    @DocumentId val id: String = "",
    val code: String = "",
    val codeOwnerUid: String = "",
    val redeemerUid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class UserProfile(
    val displayName: String = "Sporcu",
    val age: Int = 22,
    val heightCm: Int = 175,
    val startWeightKg: Double = 75.0,
    val currentWeightKg: Double = 75.0,
    val targetWeightKg: Double = 72.0,
    val goal: String = "fitness",
    val environment: String = "hybrid",
    val activityLevel: String = "moderate",
    val experienceLevel: String = "occasional",
    val weeklyDays: Int = 4,
    val sessionMinutes: Int = 40,
    val focusArea: String = "full_body",
    val sleepQuality: String = "7-8 saat düzenli",
    val nutritionHabit: String = "Dengeli ama geliştirilebilir",
    val planTier: String = "free",
    val proSource: String = "none",
    val proExpiresAtMillis: Long = 0L,
    val inviteCode: String = "TAVUK-2026",
    val referralCount: Int = 0,
    val referredByCode: String = "",
    val aiQuestionsUsedToday: Int = 0,
    val aiQuotaDate: String = "",
    val onboardingCompleted: Boolean = false,
    val themeId: String = "fire_arena",
    val streakDays: Int = 0,
    val lastCheckInDate: String = "",
    val totalXp: Int = 0,
    val dailyQuestsDate: String = "",
    val questCoachDone: Boolean = false,
    val questChallengeDone: Boolean = false,
    val questHydrationDone: Boolean = false,
    val workoutsCompletedCount: Int = 0,
    val gender: String = "male",
    val appLanguage: String = "tr",
    val lastWheelSpinDate: String = "",
    val bestStreakDays: Int = 0
) {
    val isYouthGrowthMode: Boolean
        get() = age in 0..18

    val isProActive: Boolean
        get() = planTier == "pro" && (proExpiresAtMillis == 0L || proExpiresAtMillis > System.currentTimeMillis())

    val bmi: Double
        get() {
            val hMeters = heightCm / 100.0
            return if (hMeters > 0) currentWeightKg / (hMeters * hMeters) else 0.0
        }

    val chickenLevel: Int
        get() = (totalXp / 150) + 1

    val xpInCurrentLevel: Int
        get() = totalXp % 150

    fun chickenRankTitle(lang: String = appLanguage): String = if (lang == "en") {
        when {
            chickenLevel >= 10 -> "Legendary Champion Rooster"
            chickenLevel >= 7 -> "Golden Wing Master"
            chickenLevel >= 5 -> "Iron Wing Athlete"
            chickenLevel >= 3 -> "Dedicated Arena Fighter"
            chickenLevel == 2 -> "Rising Wing"
            else -> "Rookie Chick"
        }
    } else {
        when {
            chickenLevel >= 10 -> "Efsanevi Şampiyon Horoz"
            chickenLevel >= 7 -> "Altın Kanat Efendisi"
            chickenLevel >= 5 -> "Demir Kanat Atlet"
            chickenLevel >= 3 -> "Azimli Ring Horozu"
            chickenLevel == 2 -> "Güçlenen Kanat"
            else -> "Çırak Civciv"
        }
    }

    val chickenRankTitle: String
        get() = chickenRankTitle(appLanguage)
}

data class WeightEntry(
    val id: String,
    val weightKg: Double,
    val note: String,
    val loggedAtMillis: Long
)

data class CalendarWorkoutEntry(
    val id: String,
    val dateKey: String,
    val workoutId: String,
    val workoutTitle: String,
    val completed: Boolean,
    val durationMinutes: Int
)

data class AiChatMessage(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis()
)
