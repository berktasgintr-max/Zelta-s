package com.example.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.example.R
import com.example.data.catalog.WorkoutCatalogHomeAndYouth
import com.example.data.catalog.localizedDifficulty
import com.example.data.catalog.localizedName
import com.example.data.catalog.localizedSubtitle
import com.example.data.catalog.localizedTargetMuscle
import com.example.data.catalog.localizedTitle
import com.example.data.model.ExerciseItem
import com.example.data.model.TargetGroup
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram
import com.example.ui.components.AppImageLoader
import com.example.ui.components.CleanTextField
import com.example.ui.i18n.LocalAppLanguageCode
import com.example.ui.i18n.LocalLocalizedContext
import com.example.ui.i18n.strRes

private val HeroScrimBrush = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.80f))
)

enum class WorkoutFilter(@get:StringRes val labelRes: Int) {
    ALL(R.string.filter_all),
    GYM(R.string.filter_gym),
    HOME(R.string.filter_home),
    YOUTH(R.string.filter_youth),
    PRO(R.string.filter_pro),
    CUSTOM(R.string.filter_custom)
}

fun workoutPreviewDrawable(workout: WorkoutProgram): Int {
    val focus = workout.focusArea.lowercase()
    val title = workout.title.lowercase()
    
    if (workout.targetGroup == com.example.data.model.TargetGroup.MALE_ONLY) {
        return when (workout.category) {
            WorkoutCategory.HOME -> if (focus == "upper" || title.contains("şınav") || title.contains("push") || title.contains("göğüs")) R.drawable.img_preview_home_male_pushups else R.drawable.img_preview_home_male_cardio
            WorkoutCategory.YOUTH -> R.drawable.img_preview_youth_male_athletic
            WorkoutCategory.GYM -> if (focus == "lower") R.drawable.img_preview_lower_legs else R.drawable.img_preview_upper_chest
        }
    } else if (workout.targetGroup == com.example.data.model.TargetGroup.FEMALE_ONLY) {
        return when (workout.category) {
            WorkoutCategory.HOME -> R.drawable.img_preview_home_core_stretch
            WorkoutCategory.YOUTH -> R.drawable.img_preview_youth_athletic
            WorkoutCategory.GYM -> R.drawable.img_preview_gym_female_upper
        }
    }

    return when (workout.category) {
        WorkoutCategory.HOME -> when {
            focus == "core" || title.contains("karın") || title.contains("core") ||
                title.contains("esneme") || title.contains("mobilite") || title.contains("mobility") ->
                R.drawable.img_preview_home_core_stretch
            focus == "lower" || title.contains("bacak") || title.contains("kalça") ||
                title.contains("hiit") || title.contains("tabata") ||
                title.contains("kardiyo") || title.contains("yağ yakım") || title.contains("fat") ->
                R.drawable.img_preview_home_legs_cardio
            else -> R.drawable.img_preview_home_pushups
        }
        WorkoutCategory.YOUTH -> R.drawable.img_preview_youth_athletic
        WorkoutCategory.GYM -> when {
            focus == "lower" -> R.drawable.img_preview_lower_legs
            focus == "core" -> R.drawable.img_preview_core_abs
            else -> R.drawable.img_preview_upper_chest
        }
    }
}

@Composable
fun WorkoutHeroArtwork(
    workout: WorkoutProgram,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val fallbackRes = remember(workout.id, workout.category, workout.focusArea) {
        workoutPreviewDrawable(workout)
    }
    var useFallbackDrawable by remember(workout.id, workout.heroImageUrl, isPreview) {
        mutableStateOf(isPreview || workout.heroImageUrl.isBlank())
    }
    val imageLoader = remember(context) { AppImageLoader.get(context) }
    val modelData: Any = if (useFallbackDrawable) fallbackRes else workout.heroImageUrl
    val request = remember(modelData) {
        ImageRequest.Builder(context)
            .data(modelData)
            .size(560, 280)
            .precision(Precision.INEXACT)
            .crossfade(false)
            .build()
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        AsyncImage(
            model = request,
            imageLoader = imageLoader,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            onError = {
                if (!useFallbackDrawable) {
                    useFallbackDrawable = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun WorkoutsScreen(
    activeFilter: WorkoutFilter,
    onSelectFilter: (WorkoutFilter) -> Unit,
    gender: String,
    userProfile: UserProfile = UserProfile(gender = gender),
    customWorkouts: List<WorkoutProgram> = emptyList(),
    weightUnit: String = "kg",
    onWeightUnitChange: (String) -> Unit = {},
    isProActive: Boolean,
    isWorkoutAccessible: (WorkoutProgram) -> Boolean,
    adsWatchedForWorkout: (String) -> Int,
    onWatchAdForWorkout: (WorkoutProgram) -> Unit,
    onUpgradePro: () -> Unit,
    onScheduleToday: (WorkoutProgram) -> Unit,
    onCompleteWorkout: (WorkoutProgram) -> Unit,
    onSaveCustomWorkout: (String, WorkoutCategory, String, List<ExerciseItem>, String?) -> Unit = { _, _, _, _, _ -> },
    onDeleteCustomWorkout: (String) -> Unit = {},
    selectedWorkout: WorkoutProgram?,
    onSelectWorkout: (WorkoutProgram?) -> Unit
) {
    val localizedCtx = LocalLocalizedContext.current
    val langCode = LocalAppLanguageCode.current

    var applyGenderFilter by rememberSaveable(gender) { mutableStateOf(true) }
    var applySmartMatch by rememberSaveable { mutableStateOf(true) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showCustomBuilder by rememberSaveable { mutableStateOf(false) }

    val allCatalog = WorkoutCatalogHomeAndYouth.ALL_WORKOUTS
    val filterCounts = remember(customWorkouts, allCatalog.size) {
        mapOf(
            WorkoutFilter.ALL to (allCatalog.size + customWorkouts.size),
            WorkoutFilter.GYM to allCatalog.count { it.category == WorkoutCategory.GYM } +
                customWorkouts.count { it.category == WorkoutCategory.GYM },
            WorkoutFilter.HOME to allCatalog.count { it.category == WorkoutCategory.HOME } +
                customWorkouts.count { it.category == WorkoutCategory.HOME },
            WorkoutFilter.YOUTH to allCatalog.count { it.isRecommendedForYouth } +
                customWorkouts.count { it.category == WorkoutCategory.YOUTH },
            WorkoutFilter.PRO to allCatalog.count { it.isProSpecial },
            WorkoutFilter.CUSTOM to customWorkouts.size
        )
    }

    val filtered = remember(
        activeFilter,
        gender,
        applyGenderFilter,
        applySmartMatch,
        searchQuery,
        customWorkouts,
        userProfile,
        langCode
    ) {
        val combined = customWorkouts + allCatalog
        val byCategory = when (activeFilter) {
            WorkoutFilter.ALL -> combined
            WorkoutFilter.GYM -> combined.filter { it.category == WorkoutCategory.GYM }
            WorkoutFilter.HOME -> combined.filter { it.category == WorkoutCategory.HOME }
            WorkoutFilter.YOUTH -> combined.filter { it.isRecommendedForYouth }
            WorkoutFilter.PRO -> combined.filter { it.isProSpecial }
            WorkoutFilter.CUSTOM -> customWorkouts
        }
        val byGender = if (applyGenderFilter && activeFilter != WorkoutFilter.CUSTOM) {
            byCategory.filter { it.matchesGender(gender) }.ifEmpty { byCategory }
        } else {
            byCategory
        }
        val cleanSearch = searchQuery.trim().lowercase()
        val bySearch = if (cleanSearch.isEmpty()) {
            byGender
        } else {
            byGender.filter { w ->
                w.localizedTitle(localizedCtx).lowercase().contains(cleanSearch) ||
                    w.localizedSubtitle(localizedCtx).lowercase().contains(cleanSearch) ||
                    w.title.lowercase().contains(cleanSearch) ||
                    w.focusArea.lowercase().contains(cleanSearch) ||
                    w.exercises.any { ex ->
                        ex.localizedName(localizedCtx).lowercase().contains(cleanSearch) ||
                            ex.localizedTargetMuscle(localizedCtx).lowercase().contains(cleanSearch)
                    }
            }
        }
        val focusTokens = userProfile.focusAreasCsv
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .toSet()

        bySearch.sortedByDescending { w ->
            var score = 0
            if (w.isCustom) score += 200
            if (gender == "female" && w.targetGroup == TargetGroup.FEMALE_ONLY) score += 25
            if (gender == "male" && w.targetGroup == TargetGroup.MALE_ONLY) score += 25
            if (applySmartMatch) {
                if ((userProfile.environment == "home" || userProfile.environment == "outdoor") && w.category == WorkoutCategory.HOME) score += 18
                if (userProfile.environment == "gym" && w.category == WorkoutCategory.GYM) score += 18
                if (userProfile.isYouthGrowthMode && w.isRecommendedForYouth) score += 30
                val diffLower = w.difficulty.lowercase()
                when (userProfile.experienceLevel.lowercase()) {
                    "beginner" -> if (diffLower.contains("beginner") || diffLower.contains("başlangıç")) score += 14
                    "occasional" -> if (diffLower.contains("intermediate") || diffLower.contains("orta")) score += 12
                    "regular" -> if (diffLower.contains("intermediate") || diffLower.contains("orta") || diffLower.contains("advanced") || diffLower.contains("zor")) score += 12
                    "advanced" -> if (diffLower.contains("advanced") || diffLower.contains("pro") || diffLower.contains("zor") || diffLower.contains("ileri")) score += 14
                }
                val titleLower = "${w.title} ${w.titleTr}".lowercase()
                if (focusTokens.contains("abs") && (w.focusArea == "core" || titleLower.contains("abs") || titleLower.contains("karın"))) score += 10
                if (focusTokens.contains("chest") && (titleLower.contains("chest") || titleLower.contains("göğüs") || titleLower.contains("push"))) score += 10
                if (focusTokens.contains("back") && (titleLower.contains("back") || titleLower.contains("sırt") || titleLower.contains("pull"))) score += 10
                if (focusTokens.contains("legs") && (w.focusArea == "lower" || titleLower.contains("leg") || titleLower.contains("bacak") || titleLower.contains("glute"))) score += 10
                if (focusTokens.contains("arms") && (titleLower.contains("arm") || titleLower.contains("kol") || titleLower.contains("biceps"))) score += 10
                if (focusTokens.contains("shoulders") && (titleLower.contains("shoulder") || titleLower.contains("omuz"))) score += 10
            }
            score
        }
    }

    if (showCustomBuilder) {
        CustomWorkoutBuilderScreen(
            onBack = { showCustomBuilder = false },
            onSaveWorkout = { title, category, difficulty, exercises, scheduleDateKey ->
                onSaveCustomWorkout(title, category, difficulty, exercises, scheduleDateKey)
                onSelectFilter(WorkoutFilter.CUSTOM)
            }
        )
        return
    }

    if (selectedWorkout != null && isWorkoutAccessible(selectedWorkout)) {
        var isActiveSession by remember(selectedWorkout.id) { mutableStateOf(false) }
        if (isActiveSession) {
            ActiveWorkoutScreen(
                workout = selectedWorkout,
                weightUnit = weightUnit,
                onWeightUnitChange = onWeightUnitChange,
                onExitToPreview = { isActiveSession = false },
                onCompleteWorkout = {
                    isActiveSession = false
                    onCompleteWorkout(selectedWorkout)
                    onSelectWorkout(null)
                }
            )
        } else {
            WorkoutPreviewScreen(
                workout = selectedWorkout,
                onBack = { onSelectWorkout(null) },
                onScheduleToday = { onScheduleToday(selectedWorkout) },
                onStartWorkout = { isActiveSession = true }
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val bannerRes = when (activeFilter) {
            WorkoutFilter.HOME -> R.drawable.img_home_workout_banner
            WorkoutFilter.YOUTH -> R.drawable.img_preview_youth_athletic
            WorkoutFilter.GYM -> R.drawable.img_gym_workout_banner
            else -> R.drawable.img_workouts_hero_arena
        }
        val context = LocalContext.current
        val imageLoader = remember(context) { AppImageLoader.get(context) }
        val bannerRequest = remember(bannerRes) {
            ImageRequest.Builder(context)
                .data(bannerRes)
                .size(720, 220)
                .precision(Precision.INEXACT)
                .crossfade(false)
                .build()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(106.dp)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val bannerTitle = strRes(R.string.workouts_banner_title)
            AsyncImage(
                model = bannerRequest,
                imageLoader = imageLoader,
                contentDescription = bannerTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HeroScrimBrush)
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = bannerTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = strRes(R.string.workouts_banner_sub),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.86f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Search input across 200 workouts & custom routines
        CleanTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = strRes(R.string.workouts_search_hint),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            inputModifier = Modifier.testTag("workouts_search_input")
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            item(key = "smart_match_chip", contentType = "filter_chip") {
                FilterChip(
                    selected = applySmartMatch,
                    onClick = { applySmartMatch = !applySmartMatch },
                    leadingIcon = {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    label = {
                        Text(strRes(R.string.workouts_smart_match_chip), style = MaterialTheme.typography.labelLarge)
                    },
                    modifier = Modifier.testTag("workout_profile_match_filter")
                )
            }
            item(key = "gender_tailored_chip", contentType = "filter_chip") {
                FilterChip(
                    selected = applyGenderFilter,
                    onClick = { applyGenderFilter = !applyGenderFilter },
                    label = {
                        Text(
                            text = strRes(
                                if (gender == "female") R.string.filter_tailored_female
                                else R.string.filter_tailored_male
                            ),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    modifier = Modifier.testTag("workout_gender_tailored_filter")
                )
            }
            items(WorkoutFilter.entries, key = { it.name }, contentType = { "filter_chip" }) { filter ->
                val count = filterCounts[filter] ?: 0
                FilterChip(
                    selected = activeFilter == filter,
                    onClick = { onSelectFilter(filter) },
                    label = {
                        Text(
                            text = "${strRes(filter.labelRes)} ($count)",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    modifier = Modifier.testTag("workout_filter_${filter.name.lowercase()}")
                )
            }
        }

        val cardOutline = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
        val flatElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        val surfaceCardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Dedicated "Create Your Own Workout" Entry Card
            item(key = "custom_builder_entry_card", contentType = "builder_entry") {
                Card(
                    colors = surfaceCardColors,
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)),
                    elevation = flatElevation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCustomBuilder = true }
                        .testTag("open_custom_builder_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircleOutline,
                                    contentDescription = strRes(R.string.builder_card_title),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = strRes(R.string.builder_card_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strRes(R.string.builder_card_sub),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { showCustomBuilder = true },
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text(strRes(R.string.builder_card_cta), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                item(key = "workouts_empty_state", contentType = "empty_state") {
                    Card(
                        colors = surfaceCardColors,
                        shape = MaterialTheme.shapes.large,
                        border = cardOutline,
                        elevation = flatElevation,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strRes(R.string.workouts_empty_search),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(
                    items = filtered,
                    key = { it.id },
                    contentType = { "workout_card" }
                ) { workout ->
                    val accessible = isWorkoutAccessible(workout)
                    val adsWatched = adsWatchedForWorkout(workout.id)
                    val localizedTitle = workout.localizedTitle()

                    Card(
                        colors = surfaceCardColors,
                        shape = MaterialTheme.shapes.large,
                        border = cardOutline,
                        elevation = flatElevation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (accessible) onSelectWorkout(workout) else onUpgradePro()
                            }
                            .testTag("workout_card_${workout.id}")
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(152.dp)
                            ) {
                                WorkoutHeroArtwork(
                                    workout = workout,
                                    contentDescription = localizedTitle,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(HeroScrimBrush)
                                )

                                Column(
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .horizontalScroll(rememberScrollState())
                                        ) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = strRes(workout.category.labelRes),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            if (workout.isCustom) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.tertiary,
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = strRes(R.string.custom_badge),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onTertiary,
                                                        maxLines = 1,
                                                        softWrap = false,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                            if (workout.isProSpecial) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = strRes(if (isProActive) R.string.pro_open_badge else R.string.pro_badge),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        maxLines = 1,
                                                        softWrap = false,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                            if (workout.targetGroup != TargetGroup.BOTH) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = strRes(
                                                            if (workout.targetGroup == TargetGroup.FEMALE_ONLY) R.string.target_group_female_badge
                                                            else R.string.target_group_male_badge
                                                        ),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1,
                                                        softWrap = false,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                        if (!accessible) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = CircleShape
                                            ) {
                                                Icon(
                                                    Icons.Rounded.Lock,
                                                    contentDescription = strRes(R.string.common_locked),
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier
                                                        .padding(6.dp)
                                                        .size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.horizontalScroll(rememberScrollState())
                                    ) {
                                        MetaChip(
                                            icon = Icons.Rounded.AccessTime,
                                            text = "${workout.durationMinutes} ${strRes(R.string.unit_min)}"
                                        )
                                        MetaChip(
                                            icon = Icons.Rounded.LocalFireDepartment,
                                            text = "~${workout.estimatedCalories} kcal"
                                        )
                                        MetaChip(
                                            icon = Icons.Rounded.FitnessCenter,
                                            text = "${workout.exercises.size} ${strRes(R.string.exercises_short)}"
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (workout.isRecommendedForYouth) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = strRes(R.string.youth_recommended_badge),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = localizedTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = workout.localizedSubtitle(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = workout.localizedDifficulty(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (!accessible) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = { onWatchAdForWorkout(workout) },
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("watch_ad_unlock_${workout.id}")
                                        ) {
                                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${strRes(R.string.watch_ads_prefix)} ($adsWatched/3)",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = onUpgradePro,
                                            shape = CircleShape,
                                            modifier = Modifier.height(48.dp)
                                        ) {
                                            Text(
                                                text = strRes(R.string.unlock_with_pro),
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }
                                } else {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = { onSelectWorkout(workout) },
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                        ) {
                                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = strRes(R.string.start_workout),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { onScheduleToday(workout) },
                                            shape = CircleShape,
                                            modifier = Modifier.height(48.dp)
                                        ) {
                                            Icon(
                                                Icons.Rounded.CalendarToday,
                                                contentDescription = strRes(R.string.add_to_calendar),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        if (workout.isCustom) {
                                            IconButton(
                                                onClick = { onDeleteCustomWorkout(workout.id) },
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .testTag("delete_custom_workout_${workout.id}")
                                            ) {
                                                Icon(
                                                    Icons.Rounded.Delete,
                                                    contentDescription = strRes(R.string.common_delete),
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
