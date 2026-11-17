package com.example.data.model

import androidx.annotation.StringRes
import com.example.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class WorkoutCategory(
    val labelTr: String,
    val labelEn: String,
    @get:StringRes val labelRes: Int
) {
    GYM("Spor Salonu (Gym)", "Gym", R.string.cat_gym),
    HOME("Evde Antrenman", "Home Workout", R.string.cat_home),
    YOUTH("0-18 Yaş Büyüme Çağı", "Ages 0-18 Growth", R.string.cat_youth);

    fun label(lang: String): String = if (lang == "tr") labelTr else labelEn
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

enum class TargetGroup {
    BOTH,
    MALE_ONLY,
    FEMALE_ONLY
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
    val commonMistake: String,
    val targetGroup: TargetGroup = TargetGroup.BOTH,
    val secondaryMuscles: String = "",
    val nameTr: String = "",
    val targetMuscleTr: String = "",
    val secondaryMusclesTr: String = "",
    val stepsTr: List<String> = emptyList(),
    val formCueTr: String = "",
    val breathingCueTr: String = "",
    val commonMistakeTr: String = ""
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
    val exercises: List<ExerciseItem>,
    val targetGroup: TargetGroup = TargetGroup.BOTH,
    val imagePrompt: String = "",
    val heroImageUrl: String = "",
    val isCustom: Boolean = false,
    val titleTr: String = "",
    val subtitleTr: String = ""
) {
    fun matchesGender(gender: String): Boolean = when (gender.lowercase()) {
        "female" -> targetGroup == TargetGroup.BOTH || targetGroup == TargetGroup.FEMALE_ONLY
        "male" -> targetGroup == TargetGroup.BOTH || targetGroup == TargetGroup.MALE_ONLY
        else -> true
    }
}

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
    val inviteCode: String = "FIT-1001",
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
    val displayName: String = "Athlete",
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
    val inviteCode: String = "FIT-2026",
    val referralCount: Int = 0,
    val referredByCode: String = "",
    val aiQuestionsUsedToday: Int = 0,
    val aiQuotaDate: String = "",
    val onboardingCompleted: Boolean = false,
    val themeId: String = "light_orange",
    val streakDays: Int = 0,
    val lastCheckInDate: String = "",
    val totalXp: Int = 0,
    val dailyQuestsDate: String = "",
    val questCoachDone: Boolean = false,
    val questChallengeDone: Boolean = false,
    val questHydrationDone: Boolean = false,
    val workoutsCompletedCount: Int = 0,
    val gender: String = "male",
    val appLanguage: String = com.example.ui.i18n.AppLanguage.systemDefaultCode(),
    val isLanguageSelected: Boolean = true,
    val lastWheelSpinDate: String = "",
    val bestStreakDays: Int = 0,
    val focusAreasCsv: String = "",
    val bodyFatCurrentPct: Int = 22,
    val bodyFatTargetPct: Int = 15,
    val dailyCalorieTarget: Int = 2100,
    val stressLevel: String = "moderate",
    val badHabitsCsv: String = "",
    val squatCapacity: String = "10_20",
    val wantBetterSleep: Boolean = true,
    val wantLessStress: Boolean = true,
    val motivationLevel: String = "high",
    val planPace: String = "balanced",
    val trainingDaysCsv: String = "mon,wed,fri,sat",
    val notificationsEnabled: Boolean = false,
    val targetDateIso: String = "",
    val weightUnit: String = "kg",
    val mealScansCount: Int = 0,
    val dailySpinCount: Int = 0
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
        get() = (totalXp / 100) + 1

    val xpInCurrentLevel: Int
        get() = totalXp % 100

    @StringRes
    fun chickenRankTitleRes(): Int = when {
        chickenLevel >= 31 -> R.string.rank_alpha_beast
        chickenLevel >= 16 -> R.string.rank_iron_rooster
        chickenLevel >= 6 -> R.string.rank_gym_bro
        else -> R.string.rank_rookie
    }

    fun chickenRankTitle(lang: String = appLanguage): String = if (lang == "tr") {
        when {
            chickenLevel >= 31 -> "Alfa Canavar Tavuk"
            chickenLevel >= 16 -> "Demir Horoz"
            chickenLevel >= 6 -> "Gym Bro Tavuk"
            else -> "Çırak Civciv"
        }
    } else {
        when {
            chickenLevel >= 31 -> "Alpha Beast Chicken"
            chickenLevel >= 16 -> "Iron Rooster"
            chickenLevel >= 6 -> "Gym Bro Chicken"
            else -> "Rookie Chick"
        }
    }

    val chickenRankTitle: String
        get() = chickenRankTitle(appLanguage)
}

data class ChickenBadge(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean
)

object ChickenBadges {
    fun evaluate(profile: UserProfile, calorieLogsCount: Int): List<ChickenBadge> {
        val streakPeak = maxOf(profile.streakDays, profile.bestStreakDays)
        val hasScannedMeal = profile.mealScansCount >= 1 || calorieLogsCount >= 1
        val isTr = profile.appLanguage == "tr"
        return listOf(
            ChickenBadge(
                id = "first_egg",
                title = if (isTr) "İlk Yumurta" else "First Egg",
                description = if (isTr) "İlk antrenmanını başarıyla tamamla" else "Complete your first workout successfully",
                unlocked = profile.workoutsCompletedCount >= 1
            ),
            ChickenBadge(
                id = "flame_chick",
                title = if (isTr) "Ateşli Civciv" else "Flame Chick",
                description = if (isTr) "3 günlük antrenman serisi yakala" else "Maintain a 3-day workout streak",
                unlocked = streakPeak >= 3
            ),
            ChickenBadge(
                id = "smart_nutrition",
                title = if (isTr) "Akıllı Beslenme" else "Smart Nutrition",
                description = if (isTr) "İlk öğün kalori taramasını gerçekleştir" else "Scan or log your first meal",
                unlocked = hasScannedMeal
            ),
            ChickenBadge(
                id = "gym_bro_chicken",
                title = if (isTr) "Gym Bro Tavuk" else "Gym Bro Chicken",
                description = if (isTr) "Seviye 5 ve üzerine ulaş" else "Reach Level 5 and above",
                unlocked = profile.chickenLevel >= 5
            ),
            ChickenBadge(
                id = "iron_rooster",
                title = if (isTr) "Demir Horoz" else "Iron Rooster",
                description = if (isTr) "10 antrenman programını tamamla" else "Complete 10 workouts",
                unlocked = profile.workoutsCompletedCount >= 10
            ),
            ChickenBadge(
                id = "alpha_beast",
                title = if (isTr) "Alfa Canavar" else "Alpha Beast",
                description = if (isTr) "Seviye 20 efsanevi seviyesine ulaş" else "Reach Level 20 milestone",
                unlocked = profile.chickenLevel >= 20
            ),
            ChickenBadge(
                id = "streak_7",
                title = if (isTr) "7 Günlük Seri" else "7-Day Streak",
                description = if (isTr) "7 gün kesintisiz aktif ol" else "Stay active for 7 days in a row",
                unlocked = streakPeak >= 7
            ),
            ChickenBadge(
                id = "streak_14",
                title = if (isTr) "14 Günlük Seri" else "14-Day Streak",
                description = if (isTr) "14 gün disiplini elden bırakma" else "Maintain discipline for 14 days",
                unlocked = streakPeak >= 14
            ),
            ChickenBadge(
                id = "streak_30",
                title = if (isTr) "30 Günlük Efsane" else "30-Day Legend",
                description = if (isTr) "Tam 1 ay boyunca aralıksız devam et" else "Unstoppable 30-day streak",
                unlocked = streakPeak >= 30
            ),
            ChickenBadge(
                id = "workouts_25",
                title = if (isTr) "Antrenman Kıdemlisi" else "Workout Veteran",
                description = if (isTr) "25 antrenman seansını bitir" else "Finish 25 workout sessions",
                unlocked = profile.workoutsCompletedCount >= 25
            ),
            ChickenBadge(
                id = "workouts_50",
                title = if (isTr) "Demir Yumruk" else "Iron Fist",
                description = if (isTr) "50 antrenman seansını devir" else "Conquer 50 workout sessions",
                unlocked = profile.workoutsCompletedCount >= 50
            ),
            ChickenBadge(
                id = "workouts_100",
                title = if (isTr) "Yüzlük Efsane" else "Century Legend",
                description = if (isTr) "100 antrenmanla zirveye çık" else "Complete 100 workouts",
                unlocked = profile.workoutsCompletedCount >= 100
            ),
            ChickenBadge(
                id = "meals_5",
                title = if (isTr) "Kalori Avcısı" else "Calorie Hunter",
                description = if (isTr) "5 öğün kalori analizi yap" else "Log 5 meal scans",
                unlocked = profile.mealScansCount >= 5
            ),
            ChickenBadge(
                id = "meals_20",
                title = if (isTr) "Beslenme Uzmanı" else "Nutrition Expert",
                description = if (isTr) "20 öğün kaydı ve takibi yap" else "Log 20 meal scans",
                unlocked = profile.mealScansCount >= 20
            ),
            ChickenBadge(
                id = "wheel_spin",
                title = if (isTr) "Şanslı Çark" else "Lucky Wheeler",
                description = if (isTr) "Günlük XP çarkını başarıyla çevir" else "Spin the daily XP wheel",
                unlocked = profile.lastWheelSpinDate.isNotEmpty()
            ),
            ChickenBadge(
                id = "pro_member",
                title = if (isTr) "PRO Üye" else "PRO Member",
                description = if (isTr) "FitChicken PRO ayrıcalıklarını aktif et" else "Unlock PRO plan privileges",
                unlocked = profile.isProActive
            ),
            ChickenBadge(
                id = "early_bird",
                title = if (isTr) "Sabah Kuşu" else "Early Bird",
                description = if (isTr) "Erken saatlerde antrenman tamamla" else "Complete morning workouts",
                unlocked = profile.workoutsCompletedCount >= 3
            ),
            ChickenBadge(
                id = "night_owl",
                title = if (isTr) "Gece Avcısı" else "Night Owl",
                description = if (isTr) "Günün geç saatlerinde disiplini koru" else "Late evening workout dedication",
                unlocked = profile.workoutsCompletedCount >= 5
            ),
            ChickenBadge(
                id = "squat_master",
                title = if (isTr) "Squat Kralı" else "Squat Master",
                description = if (isTr) "Güç kapasiteni üst seviyeye taşı" else "Build elite squat capacity",
                unlocked = profile.squatCapacity == "35_plus" || profile.squatCapacity == "20_35"
            ),
            ChickenBadge(
                id = "hydration_champ",
                title = if (isTr) "Su Şampiyonu" else "Hydration Champ",
                description = if (isTr) "Günlük su ve protein görevini onayla" else "Confirm daily hydration and protein",
                unlocked = profile.questHydrationDone
            ),
            ChickenBadge(
                id = "breath_master",
                title = if (isTr) "Nefes Ustası" else "Breath Master",
                description = if (isTr) "Mobilizasyon ve nefes görevini bitir" else "Complete mobility and breath opener",
                unlocked = profile.questChallengeDone
            ),
            ChickenBadge(
                id = "level_10",
                title = if (isTr) "Seviye 10 Ustası" else "Level 10 Master",
                description = if (isTr) "Deneyim puanıyla 10. seviyeye tırman" else "Reach Level 10",
                unlocked = profile.chickenLevel >= 10
            ),
            ChickenBadge(
                id = "level_25",
                title = if (isTr) "Seviye 25 Şampiyonu" else "Level 25 Champion",
                description = if (isTr) "Deneyim puanıyla 25. seviyeye ulaş" else "Reach Level 25",
                unlocked = profile.chickenLevel >= 25
            ),
            ChickenBadge(
                id = "level_50",
                title = if (isTr) "Seviye 50 Zirve" else "Level 50 Apex",
                description = if (isTr) "FitChicken zirvesine adını yazdır" else "Reach Level 50 Apex",
                unlocked = profile.chickenLevel >= 50
            ),
            ChickenBadge(
                id = "social_connector",
                title = if (isTr) "Sosyal Tavuk" else "Social Connector",
                description = if (isTr) "Arkadaş davet kodunu paylaş" else "Share invite code with friends",
                unlocked = profile.referralCount >= 1
            ),
            ChickenBadge(
                id = "consistency_king",
                title = if (isTr) "Kararlılık Abidesi" else "Consistency King",
                description = if (isTr) "İstikrarlı antrenman rutini kur" else "Establish steady training routine",
                unlocked = streakPeak >= 5 && profile.workoutsCompletedCount >= 8
            ),
            ChickenBadge(
                id = "farm_leader",
                title = if (isTr) "Çiftlik Lideri" else "Farm Leader",
                description = if (isTr) "5 arkadaşına PRO ayrıcalığı kazandır" else "Invite 5 friends to unlock PRO",
                unlocked = profile.referralCount >= 5
            )
        )
    }
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
