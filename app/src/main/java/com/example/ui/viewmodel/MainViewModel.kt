package com.example.ui.viewmodel

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.catalog.CoachWisdomAndStories
import com.example.data.catalog.ExerciseMediaCatalog
import com.example.data.catalog.FitnessFoodDatabase
import com.example.data.catalog.FitnessFoodItem
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.catalog.localizedTitle
import com.example.data.local.AdUnlockEntity
import com.example.data.local.CalendarEntryEntity
import com.example.data.local.CustomWorkoutMapper
import com.example.data.local.MealCalorieEntity
import com.example.data.local.PreferencesStore
import com.example.data.local.TavukKocDatabase
import com.example.data.local.WeightLogEntity
import com.example.data.model.AiChatMessage
import com.example.data.model.CalendarWorkoutEntry
import com.example.data.model.ExerciseItem
import com.example.data.model.TargetGroup
import com.example.data.model.UserProfile
import com.example.data.model.WeightEntry
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram
import com.example.data.repository.AiCoachRepository
import com.example.data.repository.FirestoreSyncRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocaleHelper
import com.example.ui.theme.AppThemeOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
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
    val prefsLoaded: Boolean = false,
    val profile: UserProfile = UserProfile(),
    val weightLogs: List<WeightEntry> = emptyList(),
    val calendarEntries: List<CalendarWorkoutEntry> = emptyList(),
    val customWorkouts: List<WorkoutProgram> = emptyList(),
    val calorieLogs: List<MealCalorieEntity> = emptyList(),
    val adUnlocks: Map<String, AdUnlockEntity> = emptyMap(),
    val currentUser: FirebaseUser? = null,
    val currentCoachTip: String = CoachWisdomAndStories.COACH_TIPS_EN.first(),
    val tipTapCount: Int = 0,
    val latestWheelOutcome: WheelSpinOutcome? = null,
    val aiMessages: List<AiChatMessage> = listOf(
        AiChatMessage(
            id = "welcome",
            isFromUser = false,
            text = "No excuses, champ. I'm FitChicken. Ask me anything about training, nutrition, posture, or your plan."
        )
    ),
    val aiUiState: AiUiState = AiUiState.Idle,
    val adSession: AdWatchSession? = null,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
    val isSyncingCloud: Boolean = false
)

class MainViewModel(
    private val appContext: Context,
    private val prefs: PreferencesStore,
    private val database: TavukKocDatabase,
    private val firestoreSync: FirestoreSyncRepository,
    private val aiCoach: AiCoachRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val dao = database.dao()
    private val secureRandom = SecureRandom()

    private val _uiState = MutableStateFlow(
        MainUiState(
            profile = UserProfile(themeId = prefs.getCachedThemeId()),
            currentUser = auth.currentUser
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var cloudListenersJob: Job? = null
    private var adTimerJob: Job? = null

    private fun resCtx(langCode: String = _uiState.value.profile.appLanguage): Context =
        LocaleHelper.localizedContext(appContext, langCode)

    private fun str(@StringRes id: Int, vararg args: Any): String =
        if (args.isEmpty()) resCtx().getString(id) else resCtx().getString(id, *args)

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
                val langCode = AppLanguage.fromCode(normalized.appLanguage).code
                val localized = resCtx(langCode)
                val tips = CoachWisdomAndStories.tips(localized)
                val welcomeMsg = localized.getString(R.string.ai_welcome_msg)
                _uiState.update { state ->
                    val updatedTip = tips[state.tipTapCount % tips.size]
                    val updatedMsgs = if (state.aiMessages.size == 1 && state.aiMessages.first().id == "welcome") {
                        listOf(state.aiMessages.first().copy(text = welcomeMsg))
                    } else {
                        state.aiMessages
                    }
                    state.copy(
                        prefsLoaded = true,
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

        viewModelScope.launch {
            dao.observeCustomWorkouts().collectLatest { entities ->
                val programs = entities.map { CustomWorkoutMapper.toProgram(it) }
                _uiState.update { it.copy(customWorkouts = programs) }
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
        prefs.cacheThemeSync(themeOption.id)
        val lang = _uiState.value.profile.appLanguage
        val updated = _uiState.value.profile.copy(themeId = themeOption.id)
        val localized = resCtx(lang)
        val themeName = themeOption.title(localized)
        val msg = localized.getString(R.string.theme_activated_msg, themeName)
        _uiState.update { it.copy(profile = updated, statusMessage = msg) }
        viewModelScope.launch {
            prefs.saveProfile(updated)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            val target = AppLanguage.fromCode(lang)
            val updated = _uiState.value.profile.copy(
                appLanguage = target.code,
                isLanguageSelected = true
            )
            prefs.saveProfile(updated)
            val targetCtx = resCtx(target.code)
            val tips = CoachWisdomAndStories.tips(targetCtx)
            val welcomeMsg = targetCtx.getString(R.string.ai_welcome_msg)
            _uiState.update { state ->
                val updatedMsgs = if (state.aiMessages.size == 1 && state.aiMessages.first().id == "welcome") {
                    listOf(state.aiMessages.first().copy(text = welcomeMsg))
                } else {
                    state.aiMessages
                }
                state.copy(
                    currentCoachTip = tips[state.tipTapCount % tips.size],
                    aiMessages = updatedMsgs,
                    statusMessage = targetCtx.getString(R.string.lang_switched_toast, target.nativeName)
                )
            }
        }
    }

    fun completeLanguageSelection(lang: String) {
        viewModelScope.launch {
            val target = AppLanguage.fromCode(lang)
            val updated = _uiState.value.profile.copy(
                appLanguage = target.code,
                isLanguageSelected = true
            )
            prefs.saveProfile(updated)
        }
    }

    fun setGender(gender: String) {
        viewModelScope.launch {
            val normalizedGender = if (gender == "female") "female" else "male"
            val updated = _uiState.value.profile.copy(gender = normalizedGender)
            prefs.saveProfile(updated)
            val genderName = str(if (normalizedGender == "female") R.string.female_label else R.string.male_label)
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_gender_updated, genderName))
            }
        }
    }

    fun setWeightUnit(unit: String) {
        val normalized = if (unit.equals("lbs", ignoreCase = true)) "lbs" else "kg"
        if (_uiState.value.profile.weightUnit == normalized) return
        val updated = _uiState.value.profile.copy(weightUnit = normalized)
        _uiState.update { it.copy(profile = updated) }
        viewModelScope.launch {
            prefs.saveProfile(updated)
        }
    }

    fun updateTrainingDays(days: Set<String>) {
        if (days.isEmpty()) return
        viewModelScope.launch {
            val updated = _uiState.value.profile.copy(trainingDaysCsv = days.joinToString(","))
            _uiState.update { it.copy(profile = updated) }
            prefs.saveProfile(updated)
        }
    }

    fun claimDailyCheckIn() {
        viewModelScope.launch {
            val current = normalizeDailyState(_uiState.value.profile)
            val today = todayDateKey()
            if (current.lastCheckInDate == today) {
                _uiState.update {
                    it.copy(statusMessage = str(R.string.msg_streak_already_claimed))
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
                it.copy(statusMessage = str(R.string.msg_streak_earned, newStreak, xpBonus))
            }
        }
    }

    fun completeDailyQuest(questKey: String) {
        viewModelScope.launch {
            val current = normalizeDailyState(_uiState.value.profile)
            val today = todayDateKey()
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
                    statusMessage = if (comboBonus > 0) {
                        str(R.string.msg_quest_all_done)
                    } else {
                        str(R.string.msg_quest_done, xpEarned)
                    }
                )
            }
        }
    }

    fun spinDailyWheel(isExtraSpin: Boolean = false): WheelSpinOutcome? {
        val current = normalizeDailyState(_uiState.value.profile)
        val today = todayDateKey()
        val spinCount = if (current.lastWheelSpinDate == today) current.dailySpinCount else 0

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
            val nextSpinCount = spinCount + 1
            val updated = if (outcome.wonProJackpot) {
                current.copy(
                    lastWheelSpinDate = today,
                    dailySpinCount = nextSpinCount,
                    planTier = "pro",
                    proSource = "wheel_jackpot",
                    proExpiresAtMillis = System.currentTimeMillis() + thirtyDaysMs
                )
            } else {
                current.copy(
                    lastWheelSpinDate = today,
                    dailySpinCount = nextSpinCount,
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
        val msg = if (outcome.wonProJackpot) {
            str(R.string.msg_wheel_jackpot)
        } else {
            str(R.string.msg_wheel_xp, outcome.xpEarned)
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
                totalXp = current.totalXp + 50,
                workoutsCompletedCount = current.workoutsCompletedCount + 1,
                streakDays = newStreak,
                bestStreakDays = newBest,
                lastCheckInDate = today,
                dailyQuestsDate = today
            )
            prefs.saveProfile(updated)
            val localizedTitle = workout.localizedTitle(resCtx())
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_workout_completed, localizedTitle, newStreak))
            }
        }
    }

    fun tapChickenForAdvice() {
        val profile = normalizeDailyState(_uiState.value.profile)
        val tips = CoachWisdomAndStories.tips(resCtx(profile.appLanguage))
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
            val computedTargetDate = profile.targetDateIso.ifBlank {
                computeEstimatedTargetDateIso(profile.currentWeightKg, profile.targetWeightKg, profile.planPace)
            }
            val finalProfile = profile.copy(
                inviteCode = code,
                planTier = selectedPlanTier,
                proSource = if (selectedPlanTier == "pro") "purchased" else "none",
                onboardingCompleted = true,
                aiQuotaDate = todayDateKey(),
                targetDateIso = computedTargetDate
            )
            prefs.saveProfile(finalProfile)

            if (_uiState.value.weightLogs.isEmpty()) {
                val initLog = WeightEntry(
                    id = "log_${UUID.randomUUID().toString().take(10)}",
                    weightKg = finalProfile.startWeightKg,
                    note = resCtx(finalProfile.appLanguage).getString(R.string.starting_weight),
                    loggedAtMillis = System.currentTimeMillis()
                )
                dao.upsertWeightLog(WeightLogEntity(initLog.id, initLog.weightKg, initLog.note, initLog.loggedAtMillis))
            }

            if (_uiState.value.calendarEntries.isEmpty()) {
                seedInitialWeeklyCalendar(finalProfile)
            }

            if (auth.currentUser != null) {
                firestoreSync.saveUserProfile(finalProfile)
                firestoreSync.ensureReferralCodeClaimed(code)
            }
        }
    }

    private fun computeEstimatedTargetDateIso(
        currentWeight: Double,
        targetWeight: Double,
        planPace: String
    ): String {
        val diffKg = kotlin.math.abs(currentWeight - targetWeight)
        val weeklyRateKg = when (planPace) {
            "aggressive" -> 0.75
            "steady" -> 0.35
            else -> 0.55
        }
        val weeks = ((diffKg / weeklyRateKg).coerceAtLeast(4.0)).coerceAtMost(52.0)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, (weeks * 7).toInt())
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    private suspend fun seedInitialWeeklyCalendar(profile: UserProfile) {
        val selectedDayTokens = profile.trainingDaysCsv
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .toSet()
            .ifEmpty { setOf("mon", "wed", "fri", "sat") }
        val dayMap = mapOf(
            Calendar.MONDAY to "mon",
            Calendar.TUESDAY to "tue",
            Calendar.WEDNESDAY to "wed",
            Calendar.THURSDAY to "thu",
            Calendar.FRIDAY to "fri",
            Calendar.SATURDAY to "sat",
            Calendar.SUNDAY to "sun"
        )
        val recommended = getPersonalizedWorkouts(profile)
        if (recommended.isEmpty()) return
        val localizedCtx = resCtx(profile.appLanguage)
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        var workoutIdx = 0
        repeat(7) {
            val token = dayMap[cal.get(Calendar.DAY_OF_WEEK)]
            if (token != null && selectedDayTokens.contains(token)) {
                val dateKey = fmt.format(cal.time)
                val workout = recommended[workoutIdx % recommended.size]
                workoutIdx++
                val title = workout.localizedTitle(localizedCtx)
                val entry = CalendarWorkoutEntry(
                    id = "cal_seed_${dateKey}_${workout.id}",
                    dateKey = dateKey,
                    workoutId = workout.id,
                    workoutTitle = title,
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
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)
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
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_pro_activated))
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
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_switched_free))
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
        val localizedTitle = workout.localizedTitle(resCtx())
        _uiState.update {
            it.copy(
                adSession = AdWatchSession(
                    workoutId = workout.id,
                    workoutTitle = localizedTitle,
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
            _uiState.update {
                it.copy(
                    adSession = null,
                    statusMessage = if (unlocked) {
                        str(R.string.msg_ad_unlocked, session.workoutTitle)
                    } else {
                        str(R.string.msg_ad_progress, newWatched, 3 - newWatched)
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
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_weight_saved, weightKg.toString()))
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
            val localizedTitle = workout.localizedTitle(resCtx())
            val entry = CalendarWorkoutEntry(
                id = id,
                dateKey = dateKey,
                workoutId = workout.id,
                workoutTitle = localizedTitle,
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
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_workout_scheduled, localizedTitle, dateKey))
            }
        }
    }

    fun autoBuildWeeklyScheduleForPro() {
        viewModelScope.launch {
            val profile = _uiState.value.profile
            if (!profile.isProActive) {
                _uiState.update {
                    it.copy(errorMessage = str(R.string.err_auto_plan_pro))
                }
                return@launch
            }
            val recommended = getPersonalizedWorkouts(profile)
            val cal = Calendar.getInstance()
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val days = profile.weeklyDays.coerceIn(2, 6)
            val localizedCtx = resCtx(profile.appLanguage)
            for (i in 0 until days) {
                val dateKey = fmt.format(cal.time)
                val workout = recommended[i % recommended.size]
                val localizedTitle = workout.localizedTitle(localizedCtx)
                val id = "cal_${dateKey}_${workout.id}"
                val entry = CalendarWorkoutEntry(id, dateKey, workout.id, localizedTitle, false, workout.durationMinutes)
                dao.upsertCalendarEntry(
                    CalendarEntryEntity(entry.id, entry.dateKey, entry.workoutId, entry.workoutTitle, entry.completed, entry.durationMinutes)
                )
                if (auth.currentUser != null) {
                    firestoreSync.upsertCalendarEntry(entry)
                }
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_auto_plan_done))
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

    fun saveCustomWorkout(
        title: String,
        category: WorkoutCategory,
        difficulty: String,
        exercises: List<ExerciseItem>,
        scheduleDateKey: String? = null
    ): WorkoutProgram? {
        val cleanTitle = title.trim().ifBlank { "Custom FitChicken Session" }.take(60)
        if (exercises.isEmpty()) return null
        val customId = "custom_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val duration = (exercises.size * 7 + 5).coerceIn(15, 90)
        val calories = (duration * 8).coerceIn(90, 750)
        val leadZone = ExerciseMediaCatalog.mediaFor(exercises.first().id).muscleZone
        val derivedFocus = when (leadZone) {
            com.example.data.catalog.MuscleGroupZone.CHEST,
            com.example.data.catalog.MuscleGroupZone.BACK,
            com.example.data.catalog.MuscleGroupZone.SHOULDERS,
            com.example.data.catalog.MuscleGroupZone.ARMS -> "upper"
            com.example.data.catalog.MuscleGroupZone.LEGS_GLUTES -> "lower"
            com.example.data.catalog.MuscleGroupZone.CORE -> "core"
            com.example.data.catalog.MuscleGroupZone.FULL_BODY -> "full_body"
        }
        val leadFolder = ExerciseMediaCatalog.mediaFor(exercises.first().id).dbFolderId
        val heroUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/$leadFolder/images/0.jpg"
        val prompt = "Custom FitChicken workout '$cleanTitle' with ${exercises.joinToString(", ") { it.name }}"

        val created = WorkoutProgram(
            id = customId,
            title = cleanTitle,
            subtitle = "${exercises.size} custom exercises • FitChicken Custom Studio",
            subtitleTr = "${exercises.size} özel egzersiz • FitChicken Özel Program",
            category = category,
            focusArea = derivedFocus,
            difficulty = difficulty,
            durationMinutes = duration,
            estimatedCalories = calories,
            isProSpecial = false,
            isRecommendedForYouth = category == WorkoutCategory.YOUTH,
            exercises = exercises,
            targetGroup = TargetGroup.BOTH,
            imagePrompt = prompt,
            heroImageUrl = heroUrl,
            isCustom = true,
            titleTr = cleanTitle
        )
        viewModelScope.launch {
            dao.upsertCustomWorkout(CustomWorkoutMapper.toEntity(created))
            if (!scheduleDateKey.isNullOrBlank()) {
                val entryId = "cal_${UUID.randomUUID().toString().take(10)}"
                val calEntry = CalendarWorkoutEntry(
                    id = entryId,
                    dateKey = scheduleDateKey,
                    workoutId = created.id,
                    workoutTitle = cleanTitle,
                    completed = false,
                    durationMinutes = duration
                )
                dao.upsertCalendarEntry(
                    CalendarEntryEntity(
                        calEntry.id,
                        calEntry.dateKey,
                        calEntry.workoutId,
                        calEntry.workoutTitle,
                        calEntry.completed,
                        calEntry.durationMinutes
                    )
                )
                if (auth.currentUser != null) {
                    firestoreSync.upsertCalendarEntry(calEntry)
                }
                _uiState.update {
                    it.copy(statusMessage = str(R.string.msg_custom_saved_scheduled, cleanTitle, scheduleDateKey))
                }
            } else {
                _uiState.update {
                    it.copy(statusMessage = str(R.string.msg_custom_saved, cleanTitle))
                }
            }
        }
        return created
    }

    fun deleteCustomWorkout(workoutId: String) {
        viewModelScope.launch {
            dao.deleteCustomWorkout(workoutId)
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_custom_deleted))
            }
        }
    }

    /**
     * Personalized recommendations across the 200-workout library + user custom workouts:
     * Matches environment, gender (MALE_ONLY/FEMALE_ONLY/BOTH), age (0-18 youth priority),
     * experienceLevel, focus areas (EN/TR), goal, and session duration.
     */
    fun getPersonalizedWorkouts(profile: UserProfile = _uiState.value.profile): List<WorkoutProgram> {
        val combinedPool = _uiState.value.customWorkouts + WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
        val basePool = when {
            profile.environment == "home" || profile.environment == "outdoor" -> combinedPool.filter {
                it.category == WorkoutCategory.HOME || (profile.isYouthGrowthMode && it.category == WorkoutCategory.YOUTH)
            }
            profile.environment == "gym" -> combinedPool.filter {
                it.category == WorkoutCategory.GYM || (profile.isYouthGrowthMode && it.category == WorkoutCategory.YOUTH)
            }
            else -> combinedPool
        }
        val genderFiltered = basePool.filter { it.matchesGender(profile.gender) }.ifEmpty { basePool }
        val focusTokens = profile.focusAreasCsv
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .toSet()

        return genderFiltered.sortedByDescending { w ->
            var score = 0
            val titleLower = "${w.title} ${w.titleTr}".lowercase()
            val muscleBlob = w.exercises.joinToString(" ") {
                "${it.targetMuscle} ${it.targetMuscleTr} ${it.secondaryMuscles}".lowercase()
            }
            if (w.isCustom) score += 8
            if (profile.gender == "male" && w.targetGroup == TargetGroup.MALE_ONLY) score += 22
            if (profile.gender == "female" && w.targetGroup == TargetGroup.FEMALE_ONLY) score += 22
            if (profile.isYouthGrowthMode) {
                if (w.isRecommendedForYouth) score += 50
                if (w.category == WorkoutCategory.YOUTH) score += 30
            }
            if ((profile.environment == "home" || profile.environment == "outdoor") && w.category == WorkoutCategory.HOME) score += 25
            if (profile.environment == "gym" && w.category == WorkoutCategory.GYM) score += 25
            if (w.isProSpecial && profile.isProActive) score += 16 else if (!w.isProSpecial && !profile.isProActive) score += 10
            if (w.focusArea == profile.focusArea) score += 12

            val diffLower = w.difficulty.lowercase()
            when (profile.experienceLevel.lowercase()) {
                "beginner" -> if (diffLower.contains("beginner") || diffLower.contains("başlangıç")) score += 16
                "occasional" -> if (diffLower.contains("intermediate") || diffLower.contains("orta") || diffLower.contains("beginner") || diffLower.contains("başlangıç")) score += 14
                "regular" -> if (diffLower.contains("intermediate") || diffLower.contains("orta") || diffLower.contains("advanced") || diffLower.contains("zor")) score += 14
                "advanced" -> if (diffLower.contains("advanced") || diffLower.contains("pro") || diffLower.contains("zor") || diffLower.contains("ileri")) score += 16
            }

            if (focusTokens.contains("abs") && (w.focusArea == "core" || titleLower.contains("karın") || titleLower.contains("abs") || titleLower.contains("core"))) score += 10
            if (focusTokens.contains("chest") && (titleLower.contains("göğüs") || titleLower.contains("chest") || titleLower.contains("push") || muscleBlob.contains("göğüs") || muscleBlob.contains("chest") || muscleBlob.contains("pectoral"))) score += 10
            if (focusTokens.contains("arms") && (titleLower.contains("kol") || titleLower.contains("arms") || titleLower.contains("biceps") || muscleBlob.contains("biceps") || muscleBlob.contains("triceps"))) score += 10
            if (focusTokens.contains("shoulders") && (titleLower.contains("omuz") || titleLower.contains("shoulder") || muscleBlob.contains("omuz") || muscleBlob.contains("delt"))) score += 10
            if (focusTokens.contains("back") && (titleLower.contains("sırt") || titleLower.contains("back") || titleLower.contains("pull") || muscleBlob.contains("sırt") || muscleBlob.contains("lats"))) score += 10
            if (focusTokens.contains("legs") && (w.focusArea == "lower" || titleLower.contains("bacak") || titleLower.contains("leg") || titleLower.contains("glute") || titleLower.contains("kalça"))) score += 10

            when (profile.goal) {
                "fat_loss" -> if (titleLower.contains("yağ") || titleLower.contains("fat") || titleLower.contains("burn") || titleLower.contains("hiit") || titleLower.contains("kardiyo") || titleLower.contains("cardio") || titleLower.contains("tabata")) score += 14
                "muscle" -> if (titleLower.contains("hipertrofi") || titleLower.contains("hypertrophy") || titleLower.contains("güç") || titleLower.contains("strength") || titleLower.contains("hacim") || titleLower.contains("mass") || titleLower.contains("push") || titleLower.contains("pull") || titleLower.contains("kas")) score += 14
                "growth_posture" -> if (titleLower.contains("boy") || titleLower.contains("growth") || titleLower.contains("duruş") || titleLower.contains("posture") || titleLower.contains("sıçrama") || titleLower.contains("jump") || titleLower.contains("postür")) score += 14
                "fitness" -> if (w.focusArea == "full_body" || titleLower.contains("kondisyon") || titleLower.contains("athletic") || titleLower.contains("endurance")) score += 10
            }
            if (kotlin.math.abs(w.durationMinutes - profile.sessionMinutes) <= 10) score += 6
            if (profile.age > 18 && w.category == WorkoutCategory.YOUTH) score -= 5
            score
        }.take(6)
    }

    fun logFoodEntry(food: FitnessFoodItem, grams: Int) {
        val currentProfile = _uiState.value.profile
        val langCode = AppLanguage.fromCode(currentProfile.appLanguage).code
        val scaled = food.scaleForGrams(grams)
        val foodDisplay = food.localizedName(langCode)
        val todayLogsCount = _uiState.value.calorieLogs.count {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            it.loggedAtMillis >= cal.timeInMillis
        }
        val xpBonus = if (todayLogsCount == 0) 25 else 15
        val feedback = FitnessFoodDatabase.evaluateDailyNutrition(
            profile = currentProfile,
            consumedKcal = _uiState.value.calorieLogs.sumOf { it.calories } + scaled.calories,
            consumedProtein = _uiState.value.calorieLogs.sumOf { it.proteinGrams } + scaled.proteinGrams,
            langCode = langCode
        )

        viewModelScope.launch {
            val entity = MealCalorieEntity(
                id = "meal_${UUID.randomUUID().toString().take(10)}",
                mealName = "$foodDisplay (${scaled.grams}g)",
                calories = scaled.calories,
                proteinGrams = scaled.proteinGrams,
                carbsGrams = scaled.carbsGrams,
                fatGrams = scaled.fatGrams,
                portionSummary = "${scaled.grams}g • ${food.localizedPortionHint(langCode)}",
                coachNote = feedback.title,
                loggedAtMillis = System.currentTimeMillis()
            )
            dao.upsertMealCalorie(entity)
            val updatedProfile = currentProfile.copy(
                totalXp = currentProfile.totalXp + xpBonus,
                mealScansCount = currentProfile.mealScansCount + 1
            )
            prefs.saveProfile(updatedProfile)
            _uiState.update {
                it.copy(statusMessage = str(R.string.msg_food_logged, entity.mealName, scaled.calories, xpBonus))
            }
        }
    }

    fun deleteMealCalorieLog(id: String) {
        viewModelScope.launch {
            dao.deleteMealCalorie(id)
        }
    }

    fun askAiCoach(question: String) {
        val trimmed = question.trim()
        if (trimmed.isEmpty()) return
        val currentProfile = normalizeDailyState(_uiState.value.profile)
        val userMsg = AiChatMessage(id = "u_${System.currentTimeMillis()}", isFromUser = true, text = trimmed)
        val replyText = aiCoach.getRuleBasedAdvice(trimmed, currentProfile)
        val assistantMsg = AiChatMessage(
            id = "a_${System.currentTimeMillis() + 1}",
            isFromUser = false,
            text = replyText
        )
        _uiState.update {
            it.copy(
                aiMessages = it.aiMessages + userMsg + assistantMsg,
                aiUiState = AiUiState.Idle
            )
        }
    }

    fun redeemFriendInviteCode(codeInput: String) {
        val code = codeInput.trim().uppercase()
        if (code.length < 4) {
            _uiState.update {
                it.copy(errorMessage = str(R.string.err_invalid_invite))
            }
            return
        }
        if (auth.currentUser == null) {
            _uiState.update {
                it.copy(errorMessage = str(R.string.err_signin_referral))
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
                    it.copy(statusMessage = str(R.string.msg_invite_applied, code))
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = result.exceptionOrNull()?.localizedMessage
                            ?: str(R.string.err_invite_failed)
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
                    _uiState.update {
                        it.copy(statusMessage = str(R.string.msg_referral_5_pro))
                    }
                }
            }
        }
    }

    private fun normalizeDailyState(profile: UserProfile): UserProfile {
        val today = todayDateKey()
        val resetSpin = if (profile.lastWheelSpinDate != today) {
            profile.copy(dailySpinCount = 0)
        } else {
            profile
        }
        val resetAi = if (resetSpin.aiQuotaDate != today) {
            resetSpin.copy(aiQuestionsUsedToday = 0, aiQuotaDate = today)
        } else {
            resetSpin
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
                    appContext = appCtx,
                    prefs = PreferencesStore(appCtx),
                    database = TavukKocDatabase.getInstance(appCtx),
                    firestoreSync = FirestoreSyncRepository(firestore, auth),
                    aiCoach = AiCoachRepository(appCtx),
                    auth = auth
                ) as T
            }
        }
    }
}
