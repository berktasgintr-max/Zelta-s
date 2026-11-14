package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.catalog.CoachWisdomAndStories
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.local.AdUnlockEntity
import com.example.data.local.CalendarEntryEntity
import com.example.data.local.MealCalorieEntity
import com.example.data.local.PreferencesStore
import com.example.data.local.TavukKocDatabase
import com.example.data.local.WeightLogEntity
import com.example.data.model.AiChatMessage
import com.example.data.model.CalendarWorkoutEntry
import com.example.data.model.UserProfile
import com.example.data.model.WeightEntry
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram
import com.example.data.repository.AiCoachRepository
import com.example.data.repository.FirestoreSyncRepository
import com.example.ui.theme.AppThemeOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Streaming(val text: String) : AiUiState
    data class Error(val message: String) : AiUiState
}

sealed interface CalorieScanUiState {
    data object Idle : CalorieScanUiState
    data object Analyzing : CalorieScanUiState
    data class Result(val entity: MealCalorieEntity) : CalorieScanUiState
    data class Error(val message: String) : CalorieScanUiState
}

data class AdWatchSession(
    val workoutId: String,
    val workoutTitle: String,
    val currentAdIndex: Int,
    val totalAdsRequired: Int = 3,
    val remainingSeconds: Int = 4
)

data class WheelSlice(
    val label: String,
    val xpAmount: Int,
    val isProJackpot: Boolean = false
)

data class WheelSpinOutcome(
    val sliceIndex: Int,
    val label: String,
    val xpEarned: Int,
    val wonProJackpot: Boolean,
    val spinId: Long = System.currentTimeMillis()
)

data class MainUiState(
    val profile: UserProfile = UserProfile(),
    val weightLogs: List<WeightEntry> = emptyList(),
    val calendarEntries: List<CalendarWorkoutEntry> = emptyList(),
    val calorieLogs: List<MealCalorieEntity> = emptyList(),
    val adUnlocks: Map<String, AdUnlockEntity> = emptyMap(),
    val currentUser: FirebaseUser? = null,
    val currentCoachTip: String = CoachWisdomAndStories.COACH_TIPS.first(),
    val tipTapCount: Int = 0,
    val latestWheelOutcome: WheelSpinOutcome? = null,
    val aiMessages: List<AiChatMessage> = listOf(
        AiChatMessage(
            id = "welcome",
            isFromUser = false,
            text = "Gıt-gıdak şampiyon! Ben Koç Tavuk. Antrenman, beslenme, boy/postür veya programın hakkında ne merak ediyorsan sor!"
        )
    ),
    val aiUiState: AiUiState = AiUiState.Idle,
    val calorieScanState: CalorieScanUiState = CalorieScanUiState.Idle,
    val adSession: AdWatchSession? = null,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
    val isSyncingCloud: Boolean = false
)

class MainViewModel(
    private val prefs: PreferencesStore,
    private val database: TavukKocDatabase,
    private val firestoreSync: FirestoreSyncRepository,
    private val aiCoach: AiCoachRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val dao = database.dao()
    private val secureRandom = SecureRandom()

    private val _uiState = MutableStateFlow(MainUiState(currentUser = auth.currentUser))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var cloudListenersJob: Job? = null
    private var adTimerJob: Job? = null

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _uiState.update { it.copy(currentUser = user) }
        if (user != null) {
            startCloudSync()
        } else {
            stopCloudSync()
        }
    }

    init {
        auth.addAuthStateListener(authListener)

        viewModelScope.launch {
            prefs.ensureInviteCode()
            prefs.profileFlow.collectLatest { profile ->
                val normalized = normalizeDailyState(profile)
                val tips = CoachWisdomAndStories.tips(normalized.appLanguage)
                _uiState.update { state ->
                    val updatedTip = tips[state.tipTapCount % tips.size]
                    val updatedMsgs = if (state.aiMessages.size == 1 && state.aiMessages.first().id == "welcome") {
                        listOf(
                            state.aiMessages.first().copy(
                                text = if (normalized.appLanguage == "en") {
                                    "Cock-a-doodle-doo champ! I'm Koç Tavuk. Ask me anything about training, nutrition, posture, or your plan!"
                                } else {
                                    "Gıt-gıdak şampiyon! Ben Koç Tavuk. Antrenman, beslenme, boy/postür veya programın hakkında ne merak ediyorsan sor!"
                                }
                            )
                        )
                    } else {
                        state.aiMessages
                    }
                    state.copy(
                        profile = normalized,
                        currentCoachTip = updatedTip,
                        aiMessages = updatedMsgs
                    )
                }
            }
        }

        viewModelScope.launch {
            dao.observeWeightLogs().collectLatest { entities ->
                val items = entities.map { WeightEntry(it.id, it.weightKg, it.note, it.loggedAtMillis) }
                _uiState.update { it.copy(weightLogs = items) }
            }
        }

        viewModelScope.launch {
            dao.observeCalendarEntries().collectLatest { entities ->
                val items = entities.map {
                    CalendarWorkoutEntry(it.id, it.dateKey, it.workoutId, it.workoutTitle, it.completed, it.durationMinutes)
                }
                _uiState.update { it.copy(calendarEntries = items) }
            }
        }

        viewModelScope.launch {
            dao.observeAdUnlocks().collectLatest { entities ->
                _uiState.update { it.copy(adUnlocks = entities.associateBy { e -> e.workoutId }) }
            }
        }

        viewModelScope.launch {
            dao.observeMealCalories().collectLatest { logs ->
                _uiState.update { it.copy(calorieLogs = logs) }
            }
        }

        if (auth.currentUser != null) {
            startCloudSync()
        }
    }

    private fun startCloudSync() {
        if (cloudListenersJob != null) return
        cloudListenersJob = viewModelScope.launch {
            _uiState.update { it.copy(isSyncingCloud = true) }
            val localProfile = _uiState.value.profile
            val cloudResult = firestoreSync.getUserProfile()
            val cloudProfile = cloudResult.getOrNull()
            if (cloudProfile != null && cloudProfile.onboardingCompleted && !localProfile.onboardingCompleted) {
                prefs.saveProfile(cloudProfile.copy(
                    gender = localProfile.gender,
                    appLanguage = localProfile.appLanguage
                ))
            } else if (localProfile.onboardingCompleted) {
                firestoreSync.saveUserProfile(localProfile)
            }
            if (localProfile.inviteCode.isNotBlank()) {
                firestoreSync.ensureReferralCodeClaimed(localProfile.inviteCode)
                refreshReferralCountFromCloud()
            }

            _uiState.value.weightLogs.forEach { firestoreSync.upsertWeightLog(it) }
            _uiState.value.calendarEntries.forEach { firestoreSync.upsertCalendarEntry(it) }

            launch {
                firestoreSync.observeWeightLogs()
                    .catch { }
                    .collectLatest { cloudLogs ->
                        cloudLogs.forEach { entry ->
                            dao.upsertWeightLog(WeightLogEntity(entry.id, entry.weightKg, entry.note, entry.loggedAtMillis))
                        }
                    }
            }
            launch {
                firestoreSync.observeCalendarEntries()
                    .catch { }
                    .collectLatest { cloudCal ->
                        cloudCal.forEach { entry ->
                            dao.upsertCalendarEntry(
                                CalendarEntryEntity(
                                    entry.id,
                                    entry.dateKey,
                                    entry.workoutId,
                                    entry.workoutTitle,
                                    entry.completed,
                                    entry.durationMinutes
                                )
                            )
                        }
                    }
            }
            _uiState.update { it.copy(isSyncingCloud = false) }
        }
    }

    private fun stopCloudSync() {
        cloudListenersJob?.cancel()
        cloudListenersJob = null
    }

    fun clearMessages() {
        _uiState.update { it.copy(statusMessage = null, errorMessage = null) }
    }

    fun showBannerMessage(message: String) {
        _uiState.update { it.copy(statusMessage = message) }
    }

    fun setAppTheme(themeOption: AppThemeOption) {
        viewModelScope.launch {
            val lang = _uiState.value.profile.appLanguage
            val updated = _uiState.value.profile.copy(themeId = themeOption.id)
            prefs.saveProfile(updated)
            val msg = if (lang == "en") {
                "${themeOption.title(lang)} theme activated!"
            } else {
                "${themeOption.title(lang)} teması aktif edildi!"
            }
            _uiState.update { it.copy(statusMessage = msg) }
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            val normalizedLang = if (lang == "en") "en" else "tr"
            val updated = _uiState.value.profile.copy(appLanguage = normalizedLang)
            prefs.saveProfile(updated)
            val tips = CoachWisdomAndStories.tips(normalizedLang)
            _uiState.update { state ->
                state.copy(
                    currentCoachTip = tips[state.tipTapCount % tips.size],
                    statusMessage = if (normalizedLang == "en") "Language switched to English" else "Uygulama dili Türkçe olarak ayarlandı"
                )
            }
        }
    }

    fun setGender(gender: String) {
        viewModelScope.launch {
            val normalizedGender = if (gender == "female") "female" else "male"
            val updated = _uiState.value.profile.copy(gender = normalizedGender)
            prefs.saveProfile(updated)
            val isEn = updated.appLanguage == "en"
            _uiState.update {
                it.copy(
                    statusMessage = if (isEn) {
                        "Exercise guide updated to ${if (normalizedGender == "female") "Female" else "Male"} athlete!"
                    } else {
                        "Hareket rehberi ${if (normalizedGender == "female") "Kadın" else "Erkek"} sporcu olarak güncellendi!"
                    }
                )
            }
        }
    }

    fun claimDailyCheckIn() {
        viewModelScope.launch {
            val current = normalizeDailyState(_uiState.value.profile)
            val today = todayDateKey()
            val isEn = current.appLanguage == "en"
            if (current.lastCheckInDate == today) {
                _uiState.update {
                    it.copy(
                        statusMessage = if (isEn) "You already claimed today's streak bonus!"
                        else "Bugünün seri ödülünü zaten aldın! Yarın serini büyütmeye devam et."
                    )
                }
                return@launch
            }
            val yesterday = yesterdayDateKey()
            val newStreak = if (current.lastCheckInDate == yesterday) current.streakDays + 1 else 1
            val newBest = maxOf(current.bestStreakDays, newStreak)
            val xpBonus = 50 + (newStreak * 5).coerceAtMost(50)
            val updated = current.copy(
                streakDays = newStreak,
                bestStreakDays = newBest,
                lastCheckInDate = today,
                totalXp = current.totalXp + xpBonus
            )
            prefs.saveProfile(updated)
            _uiState.update {
                it.copy(
                    statusMessage = if (isEn) "Daily streak: $newStreak days! Earned +$xpBonus XP!"
                    else "Günlük seri: $newStreak gün! +$xpBonus XP kazandın!"
                )
            }
        }
    }

    fun completeDailyQuest(questKey: String) {
        viewModelScope.launch {
            val current = normalizeDailyState(_uiState.value.profile)
            val today = todayDateKey()
            val isEn = current.appLanguage == "en"
            var xpEarned = 0
            val updated = when (questKey) {
                "coach" -> if (!current.questCoachDone) {
                    xpEarned = 25
                    current.copy(questCoachDone = true, dailyQuestsDate = today)
                } else current
                "challenge" -> if (!current.questChallengeDone) {
                    xpEarned = 40
                    current.copy(questChallengeDone = true, dailyQuestsDate = today)
                } else current
                "hydration" -> if (!current.questHydrationDone) {
                    xpEarned = 35
                    current.copy(questHydrationDone = true, dailyQuestsDate = today)
                } else current
                else -> current
            }
            if (xpEarned == 0) return@launch
            val allThreeDone = updated.questCoachDone && updated.questChallengeDone && updated.questHydrationDone
            val comboBonus = if (allThreeDone) 50 else 0
            val finalProfile = updated.copy(totalXp = updated.totalXp + xpEarned + comboBonus)
            prefs.saveProfile(finalProfile)
            _uiState.update {
                it.copy(
                    statusMessage = if (isEn) {
                        if (comboBonus > 0) "All daily quests finished! +$xpEarned XP + $comboBonus XP Combo Bonus!"
                        else "Quest completed! +$xpEarned XP earned!"
                    } else {
                        if (comboBonus > 0) "Tüm günlük görevler tamamlandı! +$xpEarned XP + $comboBonus XP Kombo Bonusu!"
                        else "Görev tamamlandı! +$xpEarned XP kazandın!"
                    }
                )
            }
        }
    }

    fun spinDailyWheel(): WheelSpinOutcome? {
        val current = normalizeDailyState(_uiState.value.profile)
        val today = todayDateKey()
        val isEn = current.appLanguage == "en"
        if (current.lastWheelSpinDate == today) {
            _uiState.update {
                it.copy(
                    statusMessage = if (isEn) "You already used today's wheel spin. Come back tomorrow!"
                    else "Bugünkü çark çevirme hakkını zaten kullandın. Yarın tekrar gel!"
                )
            }
            return null
        }

        // Authentic 1 in 1,000 (0.1%) probability for 1-Month PRO Jackpot
        val wonPro = secureRandom.nextInt(1000) == 0
        val outcome = if (wonPro) {
            val slice = WHEEL_SLICES[PRO_SLICE_INDEX]
            WheelSpinOutcome(
                sliceIndex = PRO_SLICE_INDEX,
                label = slice.label,
                xpEarned = 0,
                wonProJackpot = true
            )
        } else {
            val xpIndices = WHEEL_SLICES.indices.filter { !WHEEL_SLICES[it].isProJackpot }
            val pickedIdx = xpIndices[secureRandom.nextInt(xpIndices.size)]
            val slice = WHEEL_SLICES[pickedIdx]
            WheelSpinOutcome(
                sliceIndex = pickedIdx,
                label = slice.label,
                xpEarned = slice.xpAmount,
                wonProJackpot = false
            )
        }

        viewModelScope.launch {
            val thirtyDaysMs = 30L * 24L * 60L * 60L * 1000L
            val updated = if (outcome.wonProJackpot) {
                current.copy(
                    lastWheelSpinDate = today,
                    planTier = "pro",
                    proSource = "wheel_jackpot",
                    proExpiresAtMillis = System.currentTimeMillis() + thirtyDaysMs
                )
            } else {
                current.copy(
                    lastWheelSpinDate = today,
                    totalXp = current.totalXp + outcome.xpEarned
                )
            }
            prefs.saveProfile(updated)
            if (outcome.wonProJackpot && auth.currentUser != null) {
                firestoreSync.saveUserProfile(updated)
            }
            _uiState.update { it.copy(latestWheelOutcome = outcome) }
        }
        return outcome
    }

    fun announceWheelOutcome(outcome: WheelSpinOutcome) {
        val isEn = _uiState.value.profile.appLanguage == "en"
        val msg = if (outcome.wonProJackpot) {
            if (isEn) "INCREDIBLE! 1-in-1,000 Jackpot hit — 1 Month PRO Plan unlocked!"
            else "İNANILMAZ! Binde 1 şans tuttu — 1 Aylık Ücretsiz PRO Plan kazandın!"
        } else {
            if (isEn) "Daily Wheel reward: +${outcome.xpEarned} XP added!"
            else "Günlük Çark ödülü: +${outcome.xpEarned} XP hesabına eklendi!"
        }
        _uiState.update { it.copy(statusMessage = msg) }
    }

    fun markWorkoutCompleted(workout: WorkoutProgram) {
        viewModelScope.launch {
            val current = normalizeDailyState(_uiState.value.profile)
            val today = todayDateKey()
            val yesterday = yesterdayDateKey()
            val newStreak = when (current.lastCheckInDate) {
                today -> current.streakDays.coerceAtLeast(1)
                yesterday -> current.streakDays + 1
                else -> 1
            }
            val newBest = maxOf(current.bestStreakDays, newStreak)
            val updated = current.copy(
                totalXp = current.totalXp + 60,
                workoutsCompletedCount = current.workoutsCompletedCount + 1,
                streakDays = newStreak,
                bestStreakDays = newBest,
                lastCheckInDate = today,
                dailyQuestsDate = today
            )
            prefs.saveProfile(updated)
            val isEn = current.appLanguage == "en"
            _uiState.update {
                it.copy(
                    statusMessage = if (isEn) "Awesome! '${workout.title}' completed: +60 XP and $newStreak day streak!"
                    else "Tebrikler! '${workout.title}' tamamlandı: +60 XP ve $newStreak günlük seri!"
                )
            }
        }
    }

    fun tapChickenForAdvice() {
        val profile = normalizeDailyState(_uiState.value.profile)
        val tips = CoachWisdomAndStories.tips(profile.appLanguage)
        val nextCount = _uiState.value.tipTapCount + 1
        val nextIndex = nextCount % tips.size
        _uiState.update {
            it.copy(
                currentCoachTip = tips[nextIndex],
                tipTapCount = nextCount
            )
        }
        // Strictly award +25 XP only once per day
        if (!profile.questCoachDone || profile.dailyQuestsDate != todayDateKey()) {
            completeDailyQuest("coach")
        }
    }

    fun completeOnboarding(profile: UserProfile, selectedPlanTier: String) {
        viewModelScope.launch {
            val code = prefs.ensureInviteCode()
            val finalProfile = profile.copy(
                inviteCode = code,
                planTier = selectedPlanTier,
                proSource = if (selectedPlanTier == "pro") "purchased" else "none",
                onboardingCompleted = true,
                aiQuotaDate = todayDateKey()
            )
            prefs.saveProfile(finalProfile)

            if (_uiState.value.weightLogs.isEmpty()) {
                val initLog = WeightEntry(
                    id = "log_${UUID.randomUUID().toString().take(10)}",
                    weightKg = finalProfile.startWeightKg,
                    note = if (finalProfile.appLanguage == "en") "Starting weight" else "Başlangıç kilosu",
                    loggedAtMillis = System.currentTimeMillis()
                )
                dao.upsertWeightLog(WeightLogEntity(initLog.id, initLog.weightKg, initLog.note, initLog.loggedAtMillis))
            }

            if (auth.currentUser != null) {
                firestoreSync.saveUserProfile(finalProfile)
                firestoreSync.ensureReferralCodeClaimed(code)
            }
        }
    }

    fun restartOnboarding() {
        viewModelScope.launch {
            val updated = _uiState.value.profile.copy(onboardingCompleted = false)
            prefs.saveProfile(updated)
            if (auth.currentUser != null) {
                firestoreSync.saveUserProfile(updated)
            }
        }
    }

    fun upgradeToPro() {
        viewModelScope.launch {
            val updated = _uiState.value.profile.copy(
                planTier = "pro",
                proSource = "purchased",
                proExpiresAtMillis = 0L
            )
            prefs.saveProfile(updated)
            if (auth.currentUser != null) {
                firestoreSync.saveUserProfile(updated)
            }
            val isEn = updated.appLanguage == "en"
            _uiState.update {
                it.copy(
                    statusMessage = if (isEn) "Congratulations! PRO Plan activated. Ads removed and all custom programs unlocked!"
                    else "Tebrikler! PRO Plan aktif edildi. Reklamlar kaldırıldı ve tüm özel programlar açıldı!"
                )
            }
        }
    }

    fun downgradeToFree() {
        viewModelScope.launch {
            val updated = _uiState.value.profile.copy(
                planTier = "free",
                proSource = "none",
                proExpiresAtMillis = 0L
            )
            prefs.saveProfile(updated)
            if (auth.currentUser != null) {
                firestoreSync.saveUserProfile(updated)
            }
            val isEn = updated.appLanguage == "en"
            _uiState.update {
                it.copy(statusMessage = if (isEn) "Switched to Free Plan." else "Ücretsiz Plana geçildi.")
            }
        }
    }

    fun isWorkoutAccessible(workout: WorkoutProgram): Boolean {
        val profile = _uiState.value.profile
        if (!workout.isProSpecial) return true
        if (profile.isProActive) return true
        return _uiState.value.adUnlocks[workout.id]?.unlocked == true
    }

    fun adsWatchedForWorkout(workoutId: String): Int {
        return _uiState.value.adUnlocks[workoutId]?.adsWatched ?: 0
    }

    fun startRewardedAd(workout: WorkoutProgram) {
        if (adTimerJob?.isActive == true) return
        val watched = adsWatchedForWorkout(workout.id)
        if (watched >= 3) return
        val nextAd = watched + 1
        _uiState.update {
            it.copy(
                adSession = AdWatchSession(
                    workoutId = workout.id,
                    workoutTitle = workout.title,
                    currentAdIndex = nextAd,
                    totalAdsRequired = 3,
                    remainingSeconds = 4
                )
            )
        }
        adTimerJob = viewModelScope.launch {
            for (sec in 4 downTo 1) {
                _uiState.update { state ->
                    state.copy(adSession = state.adSession?.copy(remainingSeconds = sec))
                }
                delay(1000L)
            }
            _uiState.update { state ->
                state.copy(adSession = state.adSession?.copy(remainingSeconds = 0))
            }
        }
    }

    fun completeRewardedAdStep() {
        val session = _uiState.value.adSession ?: return
        if (session.remainingSeconds > 0) return
        viewModelScope.launch {
            val current = dao.getAdUnlock(session.workoutId)
            val newWatched = ((current?.adsWatched ?: 0) + 1).coerceAtMost(3)
            val unlocked = newWatched >= 3
            dao.upsertAdUnlock(AdUnlockEntity(session.workoutId, newWatched, unlocked))
            val isEn = _uiState.value.profile.appLanguage == "en"
            _uiState.update {
                it.copy(
                    adSession = null,
                    statusMessage = if (isEn) {
                        if (unlocked) "3/3 ads watched! '${session.workoutTitle}' unlocked!"
                        else "Ad $newWatched/3 watched. ${3 - newWatched} left to unlock."
                    } else {
                        if (unlocked) "3/3 reklam tamamlandı! '${session.workoutTitle}' programı açıldı!"
                        else "Reklam $newWatched/3 izlendi. Açmak için ${3 - newWatched} reklam kaldı."
                    }
                )
            }
        }
    }

    fun cancelRewardedAd() {
        adTimerJob?.cancel()
        _uiState.update { it.copy(adSession = null) }
    }

    fun addWeightLog(weightKg: Double, note: String) {
        viewModelScope.launch {
            val id = "w_${UUID.randomUUID().toString().take(10)}"
            val entry = WeightEntry(
                id = id,
                weightKg = weightKg,
                note = note.trim(),
                loggedAtMillis = System.currentTimeMillis()
            )
            dao.upsertWeightLog(WeightLogEntity(entry.id, entry.weightKg, entry.note, entry.loggedAtMillis))
            val updatedProfile = _uiState.value.profile.copy(currentWeightKg = weightKg)
            prefs.saveProfile(updatedProfile)
            if (auth.currentUser != null) {
                firestoreSync.upsertWeightLog(entry)
                firestoreSync.saveUserProfile(updatedProfile)
            }
            val isEn = updatedProfile.appLanguage == "en"
            _uiState.update {
                it.copy(statusMessage = if (isEn) "Saved as $weightKg kg." else "$weightKg kg olarak kaydedildi.")
            }
        }
    }

    fun deleteWeightLog(logId: String) {
        viewModelScope.launch {
            dao.deleteWeightLog(logId)
            if (auth.currentUser != null) {
                firestoreSync.deleteWeightLog(logId)
            }
        }
    }

    fun scheduleWorkout(dateKey: String, workout: WorkoutProgram) {
        viewModelScope.launch {
            val id = "cal_${UUID.randomUUID().toString().take(10)}"
            val entry = CalendarWorkoutEntry(
                id = id,
                dateKey = dateKey,
                workoutId = workout.id,
                workoutTitle = workout.title,
                completed = false,
                durationMinutes = workout.durationMinutes
            )
            dao.upsertCalendarEntry(
                CalendarEntryEntity(
                    entry.id,
                    entry.dateKey,
                    entry.workoutId,
                    entry.workoutTitle,
                    entry.completed,
                    entry.durationMinutes
                )
            )
            if (auth.currentUser != null) {
                firestoreSync.upsertCalendarEntry(entry)
            }
            val isEn = _uiState.value.profile.appLanguage == "en"
            _uiState.update {
                it.copy(
                    statusMessage = if (isEn) "${workout.title} scheduled for $dateKey."
                    else "${workout.title} ($dateKey) takvime eklendi."
                )
            }
        }
    }

    fun autoBuildWeeklyScheduleForPro() {
        viewModelScope.launch {
            val profile = _uiState.value.profile
            val isEn = profile.appLanguage == "en"
            if (!profile.isProActive) {
                _uiState.update {
                    it.copy(
                        errorMessage = if (isEn) "Auto weekly planner is a PRO Plan benefit."
                        else "Otomatik kişisel takvim oluşturma PRO Plan ayrıcalığıdır."
                    )
                }
                return@launch
            }
            val recommended = getPersonalizedWorkouts(profile)
            val cal = Calendar.getInstance()
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val days = profile.weeklyDays.coerceIn(2, 6)
            for (i in 0 until days) {
                val dateKey = fmt.format(cal.time)
                val workout = recommended[i % recommended.size]
                val id = "cal_${dateKey}_${workout.id}"
                val entry = CalendarWorkoutEntry(id, dateKey, workout.id, workout.title, false, workout.durationMinutes)
                dao.upsertCalendarEntry(
                    CalendarEntryEntity(entry.id, entry.dateKey, entry.workoutId, entry.workoutTitle, entry.completed, entry.durationMinutes)
                )
                if (auth.currentUser != null) {
                    firestoreSync.upsertCalendarEntry(entry)
                }
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            _uiState.update {
                it.copy(
                    statusMessage = if (isEn) "PRO weekly custom calendar generated!"
                    else "PRO haftalık kişisel takvimin oluşturuldu!"
                )
            }
        }
    }

    fun toggleCalendarCompleted(entry: CalendarWorkoutEntry) {
        viewModelScope.launch {
            val updated = entry.copy(completed = !entry.completed)
            dao.upsertCalendarEntry(
                CalendarEntryEntity(
                    updated.id,
                    updated.dateKey,
                    updated.workoutId,
                    updated.workoutTitle,
                    updated.completed,
                    updated.durationMinutes
                )
            )
            if (auth.currentUser != null) {
                firestoreSync.upsertCalendarEntry(updated)
            }
        }
    }

    fun deleteCalendarEntry(entryId: String) {
        viewModelScope.launch {
            dao.deleteCalendarEntry(entryId)
            if (auth.currentUser != null) {
                firestoreSync.deleteCalendarEntry(entryId)
            }
        }
    }

    /**
     * Personalized recommendations:
     * - If user selected "home" ("Evde Antrenman"), strictly exclude GYM workouts and recommend HOME (plus YOUTH if 0-18).
     * - If user is 0-18 ("isYouthGrowthMode"), strictly prioritize 0-18 growth programs at the very top.
     */
    fun getPersonalizedWorkouts(profile: UserProfile = _uiState.value.profile): List<WorkoutProgram> {
        val basePool = when {
            profile.environment == "home" -> WorkoutCatalogHomeAndYouth.ALL_WORKOUTS.filter {
                it.category == WorkoutCategory.HOME || (profile.isYouthGrowthMode && it.category == WorkoutCategory.YOUTH)
            }
            profile.environment == "gym" -> WorkoutCatalogHomeAndYouth.ALL_WORKOUTS.filter {
                it.category == WorkoutCategory.GYM || (profile.isYouthGrowthMode && it.category == WorkoutCategory.YOUTH)
            }
            else -> WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
        }

        return basePool.sortedByDescending { w ->
            var score = 0
            val titleLower = w.title.lowercase()
            if (profile.isYouthGrowthMode) {
                if (w.isRecommendedForYouth) score += 50
                if (w.category == WorkoutCategory.YOUTH) score += 30
            }
            if (profile.environment == "home" && w.category == WorkoutCategory.HOME) score += 25
            if (profile.environment == "gym" && w.category == WorkoutCategory.GYM) score += 25
            if (w.isProSpecial) score += 15
            if (w.focusArea == profile.focusArea) score += 12
            when (profile.goal) {
                "fat_loss" -> if (titleLower.contains("yağ") || titleLower.contains("hiit") || titleLower.contains("kardiyo") || titleLower.contains("tabata")) score += 14
                "muscle" -> if (titleLower.contains("hipertrofi") || titleLower.contains("güç") || titleLower.contains("hacim") || titleLower.contains("push") || titleLower.contains("pull") || titleLower.contains("kas")) score += 14
                "growth_posture" -> if (titleLower.contains("boy") || titleLower.contains("duruş") || titleLower.contains("sıçrama") || titleLower.contains("postür")) score += 14
                "fitness" -> if (w.focusArea == "full_body" || titleLower.contains("kondisyon")) score += 10
            }
            if (kotlin.math.abs(w.durationMinutes - profile.sessionMinutes) <= 10) score += 6
            if (profile.age > 18 && w.category == WorkoutCategory.YOUTH) score -= 5
            score
        }.take(6)
    }

    fun clearCalorieScanState() {
        _uiState.update { it.copy(calorieScanState = CalorieScanUiState.Idle) }
    }

    fun analyzeMealPhoto(bitmap: Bitmap) {
        val currentProfile = _uiState.value.profile
        val isEn = currentProfile.appLanguage == "en"
        if (!currentProfile.isProActive) {
            _uiState.update {
                it.copy(
                    calorieScanState = CalorieScanUiState.Error(
                        if (isEn) "Photo Calorie AI Calculator is exclusive to the PRO Plan."
                        else "Fotoğrafla Kalori AI Hesaplama özelliği PRO Plan ayrıcalığıdır."
                    )
                )
            }
            return
        }
        if (_uiState.value.calorieScanState is CalorieScanUiState.Analyzing) return

        _uiState.update { it.copy(calorieScanState = CalorieScanUiState.Analyzing) }
        viewModelScope.launch {
            val res = aiCoach.analyzeMealPhoto(bitmap, currentProfile)
            res.onSuccess { analysis ->
                val entity = MealCalorieEntity(
                    id = "meal_${UUID.randomUUID().toString().take(10)}",
                    mealName = analysis.mealName,
                    calories = analysis.calories,
                    proteinGrams = analysis.proteinGrams,
                    carbsGrams = analysis.carbsGrams,
                    fatGrams = analysis.fatGrams,
                    portionSummary = analysis.portionSummary,
                    coachNote = analysis.coachNote,
                    loggedAtMillis = System.currentTimeMillis()
                )
                dao.upsertMealCalorie(entity)
                _uiState.update {
                    it.copy(
                        calorieScanState = CalorieScanUiState.Result(entity),
                        statusMessage = if (isEn) "${entity.mealName}: ${entity.calories} kcal logged!"
                        else "${entity.mealName}: ${entity.calories} kcal hesaplandı ve kaydedildi!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        calorieScanState = CalorieScanUiState.Error(
                            err.localizedMessage ?: if (isEn) "Failed to analyze photo." else "Fotoğraf analiz edilemedi."
                        )
                    )
                }
            }
        }
    }

    fun deleteMealCalorieLog(id: String) {
        viewModelScope.launch {
            dao.deleteMealCalorie(id)
        }
    }

    fun remainingFreeAiQuestions(): Int {
        val profile = normalizeDailyState(_uiState.value.profile)
        return if (profile.isProActive) Int.MAX_VALUE else (FREE_AI_DAILY_LIMIT - profile.aiQuestionsUsedToday).coerceAtLeast(0)
    }

    fun askAiCoach(question: String) {
        val trimmed = question.trim()
        if (trimmed.isEmpty()) return
        val currentProfile = normalizeDailyState(_uiState.value.profile)
        val isEn = currentProfile.appLanguage == "en"
        if (!currentProfile.isProActive && currentProfile.aiQuestionsUsedToday >= FREE_AI_DAILY_LIMIT) {
            _uiState.update {
                it.copy(
                    aiUiState = AiUiState.Error(
                        if (isEn) "Daily limit of $FREE_AI_DAILY_LIMIT free AI questions reached. Upgrade to PRO or invite 5 friends for unlimited access!"
                        else "Ücretsiz planda günlük $FREE_AI_DAILY_LIMIT AI soru hakkın doldu. Sınırsız AI Koç için PRO Plana geçebilir veya 5 arkadaşını davet edebilirsin!"
                    )
                )
            }
            return
        }

        val userMsg = AiChatMessage(id = "u_${System.currentTimeMillis()}", isFromUser = true, text = trimmed)
        val assistantMsgId = "a_${System.currentTimeMillis() + 1}"
        _uiState.update {
            it.copy(
                aiMessages = it.aiMessages + userMsg,
                aiUiState = AiUiState.Loading
            )
        }

        viewModelScope.launch {
            try {
                if (!currentProfile.isProActive) {
                    val updatedProfile = currentProfile.copy(
                        aiQuestionsUsedToday = currentProfile.aiQuestionsUsedToday + 1,
                        aiQuotaDate = todayDateKey()
                    )
                    prefs.saveProfile(updatedProfile)
                    if (auth.currentUser != null) {
                        firestoreSync.saveUserProfile(updatedProfile)
                    }
                }

                var lastFullText = ""
                aiCoach.streamAdvice(trimmed, currentProfile).collect { partial ->
                    lastFullText = partial
                    _uiState.update { it.copy(aiUiState = AiUiState.Streaming(partial)) }
                }

                val finalMsg = AiChatMessage(id = assistantMsgId, isFromUser = false, text = lastFullText)
                _uiState.update {
                    it.copy(
                        aiMessages = it.aiMessages + finalMsg,
                        aiUiState = AiUiState.Idle
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        aiUiState = AiUiState.Error(
                            e.localizedMessage ?: if (isEn) "Could not get AI response." else "AI yanıtı alınamadı."
                        )
                    )
                }
            }
        }
    }

    fun redeemFriendInviteCode(codeInput: String) {
        val code = codeInput.trim().uppercase()
        val isEn = _uiState.value.profile.appLanguage == "en"
        if (code.length < 4) {
            _uiState.update {
                it.copy(errorMessage = if (isEn) "Please enter a valid invite code." else "Lütfen geçerli bir davet kodu girin.")
            }
            return
        }
        if (auth.currentUser == null) {
            _uiState.update {
                it.copy(
                    errorMessage = if (isEn) "Sign in with Google to verify and save referral codes to the cloud."
                    else "Arkadaş davet kodunu doğrulamak ve buluta kaydetmek için Google ile giriş yapmalısın."
                )
            }
            return
        }
        viewModelScope.launch {
            val result = firestoreSync.redeemFriendCode(code)
            if (result.isSuccess) {
                val updated = _uiState.value.profile.copy(referredByCode = code)
                prefs.saveProfile(updated)
                firestoreSync.saveUserProfile(updated)
                _uiState.update {
                    it.copy(
                        statusMessage = if (isEn) "Invite code ($code) applied!"
                        else "Davet kodu ($code) onaylandı! Arkadaşının 5 kişilik PRO sayacına eklendi."
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = result.exceptionOrNull()?.localizedMessage
                            ?: if (isEn) "Could not apply invite code." else "Davet kodu kullanılamadı."
                    )
                }
            }
        }
    }

    fun refreshReferralCountFromCloud() {
        val code = _uiState.value.profile.inviteCode
        if (auth.currentUser == null || code.isBlank()) return
        viewModelScope.launch {
            firestoreSync.ensureReferralCodeClaimed(code)
            val res = firestoreSync.fetchReferralCount(code)
            res.onSuccess { count ->
                val current = _uiState.value.profile
                val unlockedOneMonthPro = count >= 5 && current.planTier != "pro"
                val thirtyDaysMs = 30L * 24L * 60L * 60L * 1000L
                val updated = if (unlockedOneMonthPro) {
                    current.copy(
                        referralCount = count,
                        planTier = "pro",
                        proSource = "referral_reward",
                        proExpiresAtMillis = System.currentTimeMillis() + thirtyDaysMs
                    )
                } else {
                    current.copy(referralCount = count)
                }
                prefs.saveProfile(updated)
                firestoreSync.saveUserProfile(updated)
                if (unlockedOneMonthPro) {
                    val isEn = updated.appLanguage == "en"
                    _uiState.update {
                        it.copy(
                            statusMessage = if (isEn) "5 friends invited! 1 Month Free PRO Plan unlocked!"
                            else "5 arkadaşını davet ettin! 1 Aylık Ücretsiz PRO Plan tanımlandı!"
                        )
                    }
                }
            }
        }
    }

    private fun normalizeDailyState(profile: UserProfile): UserProfile {
        val today = todayDateKey()
        val resetAi = if (profile.aiQuotaDate != today) {
            profile.copy(aiQuestionsUsedToday = 0, aiQuotaDate = today)
        } else {
            profile
        }
        return if (resetAi.dailyQuestsDate != today) {
            resetAi.copy(
                dailyQuestsDate = today,
                questCoachDone = false,
                questChallengeDone = false,
                questHydrationDone = false
            )
        } else {
            resetAi
        }
    }

    private fun todayDateKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun yesterdayDateKey(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
        stopCloudSync()
    }

    companion object {
        const val FREE_AI_DAILY_LIMIT = 3
        const val PRO_SLICE_INDEX = 3

        val WHEEL_SLICES: List<WheelSlice> = listOf(
            WheelSlice("+15 XP", 15),
            WheelSlice("+50 XP", 50),
            WheelSlice("+25 XP", 25),
            WheelSlice("1/1000 PRO", 0, isProJackpot = true),
            WheelSlice("+30 XP", 30),
            WheelSlice("+100 XP", 100),
            WheelSlice("+75 XP", 75),
            WheelSlice("+150 XP", 150)
        )

        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val appCtx = context.applicationContext
                val dbId = appCtx.getString(R.string.firestore_database_id)
                val firestore = FirebaseFirestore.getInstance(dbId)
                val auth = Firebase.auth
                return MainViewModel(
                    prefs = PreferencesStore(appCtx),
                    database = TavukKocDatabase.getInstance(appCtx),
                    firestoreSync = FirestoreSyncRepository(firestore, auth),
                    aiCoach = AiCoachRepository(),
                    auth = auth
                ) as T
            }
        }
    }
}
