package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.WorkoutProgram
import com.example.firebase.attemptAutoSignIn
import com.example.firebase.configureAppCheck
import com.example.ui.components.ThemePickerDialog
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlannerAndWeightScreen
import com.example.ui.screens.ProfileAndProScreen
import com.example.ui.screens.WorkoutFilter
import com.example.ui.screens.WorkoutsScreen
import com.example.ui.theme.AppThemeOption
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.TavukKocTheme
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.AdWatchSession
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configure Firebase App Check with launch intent before rendering Compose UI or ViewModels.
        configureAppCheck(this, intent)

        setContent {
            val context = LocalContext.current
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(context))
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val activeTheme = AppThemeOption.fromId(uiState.profile.themeId)
            val appStrings = AppStrings.forLang(uiState.profile.appLanguage)

            CompositionLocalProvider(LocalAppStrings provides appStrings) {
                TavukKocTheme(themeOption = activeTheme) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        TavukKocRootApp(viewModel = viewModel, uiState = uiState)
                    }
                }
            }
        }
    }
}

@Composable
fun TavukKocRootApp(
    viewModel: MainViewModel,
    uiState: MainUiState
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val cm = CredentialManager.create(context)
        attemptAutoSignIn(
            context = context,
            credentialManager = cm,
            onAuthSuccess = {},
            onUnauthenticated = {},
            scope = scope
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.statusMessage, uiState.errorMessage) {
        val msg = uiState.statusMessage ?: uiState.errorMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    if (!uiState.profile.onboardingCompleted) {
        OnboardingScreen(
            initialProfile = uiState.profile,
            onSelectLanguage = viewModel::setLanguage,
            onComplete = { profile, selectedPlan ->
                viewModel.completeOnboarding(profile, selectedPlan)
            }
        )
        return
    }

    val defaultFilter = remember(
        uiState.profile.onboardingCompleted,
        uiState.profile.environment,
        uiState.profile.isYouthGrowthMode
    ) {
        when {
            uiState.profile.environment == "home" -> WorkoutFilter.HOME
            uiState.profile.isYouthGrowthMode -> WorkoutFilter.YOUTH
            uiState.profile.environment == "gym" -> WorkoutFilter.GYM
            else -> WorkoutFilter.ALL
        }
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var plannerSubTab by rememberSaveable { mutableIntStateOf(0) }
    var workoutFilter by remember { mutableStateOf(defaultFilter) }
    LaunchedEffect(defaultFilter) {
        workoutFilter = defaultFilter
    }
    var selectedWorkout by remember { mutableStateOf<WorkoutProgram?>(null) }
    var showThemePicker by remember { mutableStateOf(false) }

    val openCalorieScanner = {
        plannerSubTab = 2
        selectedWorkout = null
        selectedTab = 2
    }

    if (selectedWorkout != null) {
        BackHandler { selectedWorkout = null }
    } else if (selectedTab != 0) {
        BackHandler { selectedTab = 0 }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (selectedWorkout == null) {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Rounded.Home, contentDescription = strings.navCoach) },
                        label = { Text(strings.navCoach) },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedWorkout = null
                            selectedTab = 1
                        },
                        icon = { Icon(Icons.Rounded.FitnessCenter, contentDescription = strings.navWorkouts) },
                        label = { Text(strings.navWorkouts) },
                        modifier = Modifier.testTag("nav_workouts")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = strings.navPlanner) },
                        label = { Text(strings.navPlanner) },
                        modifier = Modifier.testTag("nav_planner")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Rounded.Psychology, contentDescription = strings.navAi) },
                        label = { Text(strings.navAi) },
                        modifier = Modifier.testTag("nav_ai")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(Icons.Rounded.Person, contentDescription = strings.navProfile) },
                        label = { Text(strings.navProfile) },
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "main_tabs") { tab ->
                when (tab) {
                    0 -> HomeScreen(
                        uiState = uiState,
                        personalizedWorkouts = viewModel.getPersonalizedWorkouts(),
                        isWorkoutAccessible = viewModel::isWorkoutAccessible,
                        adsWatchedForWorkout = viewModel::adsWatchedForWorkout,
                        onTapChicken = viewModel::tapChickenForAdvice,
                        onClaimDailyCheckIn = viewModel::claimDailyCheckIn,
                        onCompleteQuest = viewModel::completeDailyQuest,
                        onSpinWheel = viewModel::spinDailyWheel,
                        onSpinAnimationFinished = viewModel::announceWheelOutcome,
                        onToggleLanguage = viewModel::setLanguage,
                        onOpenThemePicker = { showThemePicker = true },
                        onOpenWorkout = { workout ->
                            selectedWorkout = workout
                            selectedTab = 1
                        },
                        onWatchAdForWorkout = viewModel::startRewardedAd,
                        onUpgradePro = { selectedTab = 4 },
                        onOpenYouthCatalog = {
                            workoutFilter = WorkoutFilter.YOUTH
                            selectedWorkout = null
                            selectedTab = 1
                        },
                        onOpenAiCoach = { selectedTab = 3 },
                        onOpenCalorieScanner = openCalorieScanner,
                        onAuthMessage = viewModel::showBannerMessage
                    )
                    1 -> WorkoutsScreen(
                        activeFilter = workoutFilter,
                        onSelectFilter = { workoutFilter = it },
                        gender = uiState.profile.gender,
                        isProActive = uiState.profile.isProActive,
                        isWorkoutAccessible = viewModel::isWorkoutAccessible,
                        adsWatchedForWorkout = viewModel::adsWatchedForWorkout,
                        onWatchAdForWorkout = viewModel::startRewardedAd,
                        onUpgradePro = { selectedTab = 4 },
                        onScheduleToday = { w ->
                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                            viewModel.scheduleWorkout(today, w)
                        },
                        onCompleteWorkout = viewModel::markWorkoutCompleted,
                        selectedWorkout = selectedWorkout,
                        onSelectWorkout = { selectedWorkout = it }
                    )
                    2 -> PlannerAndWeightScreen(
                        selectedSubTab = plannerSubTab,
                        onSelectSubTab = { plannerSubTab = it },
                        profile = uiState.profile,
                        weightLogs = uiState.weightLogs,
                        calendarEntries = uiState.calendarEntries,
                        calorieLogs = uiState.calorieLogs,
                        calorieScanState = uiState.calorieScanState,
                        isWorkoutAccessible = viewModel::isWorkoutAccessible,
                        onAddWeightLog = viewModel::addWeightLog,
                        onDeleteWeightLog = viewModel::deleteWeightLog,
                        onScheduleWorkout = viewModel::scheduleWorkout,
                        onAutoBuildProSchedule = viewModel::autoBuildWeeklyScheduleForPro,
                        onToggleCalendarCompleted = viewModel::toggleCalendarCompleted,
                        onDeleteCalendarEntry = viewModel::deleteCalendarEntry,
                        onAnalyzeMealPhoto = viewModel::analyzeMealPhoto,
                        onDeleteMealCalorieLog = viewModel::deleteMealCalorieLog,
                        onClearCalorieScanState = viewModel::clearCalorieScanState,
                        onUpgradePro = { selectedTab = 4 },
                        onOpenInviteTab = { selectedTab = 4 }
                    )
                    3 -> AiCoachScreen(
                        profile = uiState.profile,
                        messages = uiState.aiMessages,
                        aiUiState = uiState.aiUiState,
                        remainingFreeQuestions = viewModel.remainingFreeAiQuestions(),
                        onAskQuestion = viewModel::askAiCoach,
                        onUpgradePro = { selectedTab = 4 },
                        onOpenInviteTab = { selectedTab = 4 },
                        onOpenCalorieScanner = openCalorieScanner
                    )
                    4 -> ProfileAndProScreen(
                        uiState = uiState,
                        onSelectLanguage = viewModel::setLanguage,
                        onSelectGender = viewModel::setGender,
                        onSelectTheme = viewModel::setAppTheme,
                        onUpgradePro = viewModel::upgradeToPro,
                        onDowngradeFree = viewModel::downgradeToFree,
                        onRedeemFriendCode = viewModel::redeemFriendInviteCode,
                        onRefreshReferrals = viewModel::refreshReferralCountFromCloud,
                        onRestartQuiz = viewModel::restartOnboarding,
                        onMessage = viewModel::showBannerMessage
                    )
                }
            }
        }
    }

    if (showThemePicker) {
        ThemePickerDialog(
            currentTheme = AppThemeOption.fromId(uiState.profile.themeId),
            language = uiState.profile.appLanguage,
            onSelectTheme = viewModel::setAppTheme,
            onDismiss = { showThemePicker = false }
        )
    }

    // 3-Ad Unlock Modal for PRO Workouts on Free Plan
    uiState.adSession?.let { session ->
        RewardedAdDialog(
            session = session,
            language = uiState.profile.appLanguage,
            onClaimStep = viewModel::completeRewardedAdStep,
            onDismiss = viewModel::cancelRewardedAd
        )
    }
}

@Composable
private fun RewardedAdDialog(
    session: AdWatchSession,
    language: String,
    onClaimStep: () -> Unit,
    onDismiss: () -> Unit
) {
    val isEn = language == "en"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEn) "Rewarded Ad (${session.currentAdIndex} / ${session.totalAdsRequired})"
                else "Ödüllü Reklam (${session.currentAdIndex} / ${session.totalAdsRequired})"
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.PlayCircle, contentDescription = null, tint = FlameOrange)
                Text(
                    text = if (isEn) {
                        "Watching sponsored ad to unlock '${session.workoutTitle}'."
                    } else {
                        "'${session.workoutTitle}' özel antrenmanını açmak için sponsorlu reklam izleniyor."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                LinearProgressIndicator(
                    progress = { (4 - session.remainingSeconds) / 4f },
                    modifier = Modifier.fillMaxWidth(),
                    color = WarmGold
                )
                Text(
                    text = if (session.remainingSeconds > 0) {
                        if (isEn) "Ad finishing in: ${session.remainingSeconds}s"
                        else "Reklam bitiyor: ${session.remainingSeconds} sn"
                    } else {
                        if (isEn) "Ad complete! Claim your reward."
                        else "Reklam tamamlandı! Ödülü alabilirsin."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (session.remainingSeconds > 0) FlameOrange else MaterialTheme.colorScheme.tertiary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onClaimStep,
                enabled = session.remainingSeconds == 0,
                modifier = Modifier.testTag("claim_ad_reward_button")
            ) {
                Text(
                    if (session.currentAdIndex >= 3) {
                        if (isEn) "Unlock Workout!" else "Antrenmanı Aç!"
                    } else {
                        if (isEn) "Next Step" else "Sonraki Adıma Geç"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isEn) "Cancel" else "İptal")
            }
        }
    )
}
