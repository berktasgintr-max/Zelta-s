package com.example

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Restaurant
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PreferencesStore
import com.example.data.model.WorkoutProgram
import com.example.firebase.attemptAutoSignIn
import com.example.firebase.configureAppCheck
import com.example.ui.components.ChoosePlanBottomSheet
import com.example.ui.components.ThemePickerDialog
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalAppLanguageCode
import com.example.ui.i18n.LocalAppLocale
import com.example.ui.i18n.LocalLocalizedContext
import com.example.ui.i18n.LocaleHelper
import com.example.ui.i18n.strRes
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.CalorieScannerTab
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlannerAndWeightScreen
import com.example.ui.screens.ProfileAndProScreen
import com.example.ui.screens.WorkoutFilter
import com.example.ui.screens.WorkoutsScreen
import com.example.ui.theme.AppThemeOption
import com.example.ui.theme.DarkOrangeBackground
import com.example.ui.theme.KocTavukTheme
import com.example.ui.theme.LightOrangeBackground
import com.example.ui.viewmodel.AdWatchSession
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var isPrefsReady = false
        splashScreen.setKeepOnScreenCondition { !isPrefsReady }

        window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.BLACK))

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ) { true },
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ) { true }
        )

        // Configure Firebase App Check with launch intent before rendering Compose UI or ViewModels.
        configureAppCheck(this, intent)

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                val context = LocalContext.current
                val view = LocalView.current
                val viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(context))
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val activeTheme = AppThemeOption.fromId(uiState.profile.themeId)
                val activeLangCode = AppLanguage.fromCode(uiState.profile.appLanguage).code
                val localizedContext = remember(context, activeLangCode) {
                    LocaleHelper.localizedContext(context, activeLangCode)
                }
                val appLocale = remember(activeLangCode) {
                    LocaleHelper.localeFor(activeLangCode)
                }

                LaunchedEffect(uiState.prefsLoaded) {
                    if (uiState.prefsLoaded) {
                        isPrefsReady = true
                    }
                }

                DisposableEffect(uiState.prefsLoaded, activeTheme.isDark) {
                    if (uiState.prefsLoaded) {
                        val bgArgb = if (activeTheme.isDark) DarkOrangeBackground.toArgb() else LightOrangeBackground.toArgb()
                        window.setBackgroundDrawable(ColorDrawable(bgArgb))
                        val insetsController = WindowCompat.getInsetsController(window, view)
                        insetsController.isAppearanceLightStatusBars = !activeTheme.isDark
                        insetsController.isAppearanceLightNavigationBars = !activeTheme.isDark
                    }
                    onDispose { }
                }

                val toggleTheme = {
                    val next = if (activeTheme == AppThemeOption.DARK_ORANGE) {
                        AppThemeOption.LIGHT_ORANGE
                    } else {
                        AppThemeOption.DARK_ORANGE
                    }
                    viewModel.setAppTheme(next)
                }

                CompositionLocalProvider(
                    LocalLocalizedContext provides localizedContext,
                    LocalAppLocale provides appLocale,
                    LocalAppLanguageCode provides activeLangCode
                ) {
                    KocTavukTheme(selectedTheme = activeTheme) {
                        val rootSurfaceColor = if (!uiState.prefsLoaded) {
                            Color.Black
                        } else {
                            MaterialTheme.colorScheme.background
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            color = rootSurfaceColor
                        ) {
                            if (!uiState.prefsLoaded) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black)
                                )
                            } else {
                                TavukKocRootApp(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    activeTheme = activeTheme,
                                    onToggleTheme = toggleTheme
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TavukKocRootApp(
    viewModel: MainViewModel,
    uiState: MainUiState,
    activeTheme: AppThemeOption,
    onToggleTheme: () -> Unit
) {
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
            activeTheme = activeTheme,
            onToggleTheme = onToggleTheme,
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
            uiState.profile.environment == "home" || uiState.profile.environment == "outdoor" -> WorkoutFilter.HOME
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
    var showAiCoach by rememberSaveable { mutableStateOf(false) }
    var showPricingModal by rememberSaveable { mutableStateOf(false) }

    val openCalorieScanner = {
        showAiCoach = false
        selectedWorkout = null
        selectedTab = 2
    }

    if (showAiCoach) {
        BackHandler { showAiCoach = false }
    } else if (selectedWorkout != null) {
        BackHandler { selectedWorkout = null }
    } else if (selectedTab != 0) {
        BackHandler { selectedTab = 0 }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (selectedWorkout == null && !showAiCoach) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    val navCoach = strRes(R.string.nav_coach)
                    val navWorkouts = strRes(R.string.nav_workouts)
                    val navCalorieScanner = strRes(R.string.nav_calorie_scanner)
                    val navPlanner = strRes(R.string.nav_planner)
                    val navProfile = strRes(R.string.nav_profile)
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            showAiCoach = false
                            selectedTab = 0
                        },
                        icon = { Icon(Icons.Rounded.Home, contentDescription = navCoach) },
                        label = {
                            Text(
                                text = navCoach,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            showAiCoach = false
                            selectedWorkout = null
                            selectedTab = 1
                        },
                        icon = { Icon(Icons.Rounded.FitnessCenter, contentDescription = navWorkouts) },
                        label = {
                            Text(
                                text = navWorkouts,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.testTag("nav_workouts")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = {
                            showAiCoach = false
                            selectedWorkout = null
                            selectedTab = 2
                        },
                        icon = { Icon(Icons.Rounded.Restaurant, contentDescription = navCalorieScanner) },
                        label = {
                            Text(
                                text = navCalorieScanner,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.testTag("nav_calorie_scanner")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = {
                            showAiCoach = false
                            selectedWorkout = null
                            selectedTab = 3
                        },
                        icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = navPlanner) },
                        label = {
                            Text(
                                text = navPlanner,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.testTag("nav_planner")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = {
                            showAiCoach = false
                            selectedWorkout = null
                            selectedTab = 4
                        },
                        icon = { Icon(Icons.Rounded.Person, contentDescription = navProfile) },
                        label = {
                            Text(
                                text = navProfile,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
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
            if (showAiCoach) {
                AiCoachScreen(
                    profile = uiState.profile,
                    messages = uiState.aiMessages,
                    aiUiState = uiState.aiUiState,
                    onAskQuestion = viewModel::askAiCoach,
                    onOpenCalorieScanner = openCalorieScanner,
                    onBack = { showAiCoach = false }
                )
            } else {
                when (selectedTab) {
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
                        onOpenThemePicker = { showThemePicker = true },
                        onOpenWorkout = { workout ->
                            if (viewModel.isWorkoutAccessible(workout)) {
                                selectedWorkout = workout
                                selectedTab = 1
                            } else {
                                viewModel.startRewardedAd(workout)
                            }
                        },
                        onWatchAdForWorkout = viewModel::startRewardedAd,
                        onOpenAiCoach = { showAiCoach = true },
                        onUpgradePro = { showPricingModal = true }
                    )
                    1 -> WorkoutsScreen(
                            activeFilter = workoutFilter,
                            onSelectFilter = { workoutFilter = it },
                            gender = uiState.profile.gender,
                            userProfile = uiState.profile,
                            customWorkouts = uiState.customWorkouts,
                            weightUnit = uiState.profile.weightUnit,
                            onWeightUnitChange = viewModel::setWeightUnit,
                            isProActive = uiState.profile.isProActive,
                            isWorkoutAccessible = viewModel::isWorkoutAccessible,
                            adsWatchedForWorkout = viewModel::adsWatchedForWorkout,
                            onWatchAdForWorkout = viewModel::startRewardedAd,
                            onUpgradePro = { showPricingModal = true },
                            onScheduleToday = { w ->
                                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                                viewModel.scheduleWorkout(today, w)
                            },
                            onCompleteWorkout = viewModel::markWorkoutCompleted,
                            onSaveCustomWorkout = { title, category, difficulty, exercises, scheduleDate ->
                                viewModel.saveCustomWorkout(title, category, difficulty, exercises, scheduleDate)
                            },
                            onDeleteCustomWorkout = viewModel::deleteCustomWorkout,
                            selectedWorkout = selectedWorkout,
                            onSelectWorkout = { w ->
                                if (w == null || viewModel.isWorkoutAccessible(w)) {
                                    selectedWorkout = w
                                } else {
                                    viewModel.startRewardedAd(w)
                                }
                            }
                        )
                        2 -> CalorieScannerTab(
                            profile = uiState.profile,
                            calorieLogs = uiState.calorieLogs,
                            onLogFood = viewModel::logFoodEntry,
                            onDeleteLog = viewModel::deleteMealCalorieLog
                        )
                        3 -> PlannerAndWeightScreen(
                            selectedSubTab = plannerSubTab.coerceIn(0, 1),
                            onSelectSubTab = { plannerSubTab = it.coerceIn(0, 1) },
                            profile = uiState.profile,
                            weightLogs = uiState.weightLogs,
                            calendarEntries = uiState.calendarEntries,
                            customWorkouts = uiState.customWorkouts,
                            isWorkoutAccessible = viewModel::isWorkoutAccessible,
                            onAddWeightLog = viewModel::addWeightLog,
                            onDeleteWeightLog = viewModel::deleteWeightLog,
                            onScheduleWorkout = viewModel::scheduleWorkout,
                            onAutoBuildProSchedule = viewModel::autoBuildWeeklyScheduleForPro,
                            onToggleCalendarCompleted = viewModel::toggleCalendarCompleted,
                            onDeleteCalendarEntry = viewModel::deleteCalendarEntry,
                            onUpdateTrainingDays = viewModel::updateTrainingDays,
                            onUpgradePro = { showPricingModal = true }
                        )
                        4 -> ProfileAndProScreen(
                            uiState = uiState,
                            onSelectLanguage = viewModel::setLanguage,
                            onSelectGender = viewModel::setGender,
                            onSelectTheme = viewModel::setAppTheme,
                            onUpgradePro = { showPricingModal = true },
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
            onSelectTheme = viewModel::setAppTheme,
            onDismiss = { showThemePicker = false }
        )
    }

    if (showPricingModal) {
        ChoosePlanBottomSheet(
            isProActive = uiState.profile.isProActive,
            onSelectPlan = { plan ->
                if (plan == "pro") {
                    viewModel.upgradeToPro()
                } else {
                    viewModel.downgradeToFree()
                }
                showPricingModal = false
            },
            onDismiss = { showPricingModal = false }
        )
    }

    // 3-Ad Unlock Modal for PRO Workouts on Free Plan
    uiState.adSession?.let { session ->
        RewardedAdDialog(
            session = session,
            onClaimStep = viewModel::completeRewardedAdStep,
            onDismiss = viewModel::cancelRewardedAd
        )
    }
}

@Composable
private fun RewardedAdDialog(
    session: AdWatchSession,
    onClaimStep: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(strRes(R.string.ad_dialog_title, session.currentAdIndex, session.totalAdsRequired))
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = strRes(R.string.ad_dialog_body, session.workoutTitle),
                    style = MaterialTheme.typography.bodyMedium
                )
                LinearProgressIndicator(
                    progress = { (4 - session.remainingSeconds) / 4f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (session.remainingSeconds > 0) {
                        strRes(R.string.ad_finishing_fmt, session.remainingSeconds)
                    } else {
                        strRes(R.string.ad_complete_claim)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
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
                        strRes(R.string.ad_unlock_workout)
                    } else {
                        strRes(R.string.common_next_step)
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strRes(R.string.common_cancel))
            }
        }
    )
}
