package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Scale
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.catalog.FitnessFoodDatabase
import com.example.data.catalog.FitnessFoodItem
import com.example.data.catalog.FoodCategory
import com.example.data.local.MealCalorieEntity
import com.example.data.model.UserProfile
import com.example.ui.components.AnimatedKocTavuk
import com.example.ui.components.CleanTextField
import com.example.ui.i18n.LocalAppLanguageCode
import com.example.ui.i18n.LocalAppLocale
import com.example.ui.i18n.strRes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CalorieScannerTab(
    profile: UserProfile,
    calorieLogs: List<MealCalorieEntity>,
    onLogFood: (FitnessFoodItem, Int) -> Unit,
    onDeleteLog: (String) -> Unit
) {
    val langCode = LocalAppLanguageCode.current
    val locale = LocalAppLocale.current
    val cardBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryKey by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedCategory = remember(selectedCategoryKey) {
        FoodCategory.entries.firstOrNull { it.name == selectedCategoryKey }
    }
    var activeFood by remember { mutableStateOf<FitnessFoodItem?>(null) }
    var portionGrams by rememberIntState(100)

    val todayLogs = remember(calorieLogs) {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        calorieLogs.filter { it.loggedAtMillis >= startOfDay }
    }
    val todayKcal = todayLogs.sumOf { it.calories }
    val todayProtein = todayLogs.sumOf { it.proteinGrams }
    val todayCarbs = todayLogs.sumOf { it.carbsGrams }
    val todayFat = todayLogs.sumOf { it.fatGrams }

    val targetKcal = profile.dailyCalorieTarget.coerceAtLeast(1200)
    val feedback = remember(profile, todayKcal, todayProtein, langCode) {
        FitnessFoodDatabase.evaluateDailyNutrition(
            profile = profile,
            consumedKcal = todayKcal,
            consumedProtein = todayProtein,
            langCode = langCode
        )
    }
    val targetProtein = feedback.proteinTargetGrams.coerceAtLeast(60)
    val nextXpBonus = if (todayLogs.isEmpty()) 25 else 15

    val filteredFoods = remember(searchQuery, selectedCategory) {
        FitnessFoodDatabase.search(searchQuery, selectedCategory)
    }

    activeFood?.let { food ->
        FoodPortionDialog(
            food = food,
            langCode = langCode,
            grams = portionGrams,
            xpBonus = nextXpBonus,
            onGramsChange = { portionGrams = it.coerceIn(10, 1000) },
            onConfirmLog = {
                onLogFood(food, portionGrams)
                activeFood = null
            },
            onDismiss = { activeFood = null }
        )
    }

    val flatElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val surfaceCardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Daily Calorie & Protein Progress Card
        item(key = "macro_daily_goals", contentType = "summary_card") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = cardBorder,
                elevation = flatElevation,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("macro_daily_goals_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strRes(R.string.home_calorie_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = strRes(R.string.calorie_pro_required),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Calorie Goal Bar
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = strRes(R.string.macro_daily_calorie_goal_fmt, todayKcal, targetKcal),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${((todayKcal.toFloat() / targetKcal) * 100).roundToInt().coerceIn(0, 999)}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (todayKcal.toFloat() / targetKcal.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.background
                        )
                    }

                    // Protein Goal Bar
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = strRes(R.string.macro_daily_protein_goal_fmt, todayProtein, targetProtein),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${((todayProtein.toFloat() / targetProtein) * 100).roundToInt().coerceIn(0, 999)}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (todayProtein.toFloat() / targetProtein.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.background
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        MacroPill(strRes(R.string.macro_protein), "${todayProtein}g", Modifier.weight(1f))
                        MacroPill(strRes(R.string.macro_carbs), "${todayCarbs}g", Modifier.weight(1f))
                        MacroPill(strRes(R.string.macro_fat), "${todayFat}g", Modifier.weight(1f))
                    }
                }
            }
        }

        // Dynamic FitChicken Mascot Nutrition Feedback Card
        item(key = "macro_mascot_feedback", contentType = "mascot_card") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = cardBorder,
                elevation = flatElevation,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("macro_mascot_feedback_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    AnimatedKocTavuk(
                        size = 76.dp,
                        mood = feedback.mood,
                        triggerKey = "${todayKcal}_${todayProtein}",
                        showAura = false,
                        contentDescription = feedback.title
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                        Text(
                            text = feedback.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = feedback.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = strRes(R.string.macro_database_count_fmt, FitnessFoodDatabase.ALL_FOODS.size),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Search Bar & Category Filter Chips
        item(key = "macro_search_filters", contentType = "search_bar") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    CleanTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = strRes(R.string.macro_search_hint),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        inputModifier = Modifier.testTag("macro_food_search_input")
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = strRes(R.string.common_close))
                        }
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item(key = "cat_all", contentType = "filter_chip") {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategoryKey = null },
                            label = { Text(strRes(R.string.macro_all_categories)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("food_filter_all")
                        )
                    }
                    items(FoodCategory.entries, key = { it.name }, contentType = { "filter_chip" }) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryKey = if (isSelected) null else cat.name
                            },
                            label = { Text(strRes(cat.labelRes)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("food_filter_${cat.name}")
                        )
                    }
                }

                Text(
                    text = strRes(R.string.macro_tap_hint),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Food Catalog Results
        if (filteredFoods.isEmpty()) {
            item(key = "macro_empty_search", contentType = "empty_state") {
                Card(
                    colors = surfaceCardColors,
                    shape = MaterialTheme.shapes.large,
                    border = cardBorder,
                    elevation = flatElevation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strRes(R.string.macro_empty_search),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }
            }
        } else {
            items(
                items = filteredFoods,
                key = { it.id },
                contentType = { "food_item" }
            ) { food ->
                Card(
                    colors = surfaceCardColors,
                    shape = MaterialTheme.shapes.medium,
                    border = cardBorder,
                    elevation = flatElevation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            portionGrams = food.defaultPortionGrams.coerceIn(10, 500)
                            activeFood = food
                        }
                        .testTag("food_item_${food.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Scale,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = food.localizedName(langCode),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${strRes(food.category.labelRes)} • ${food.localizedPortionHint(langCode)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = strRes(
                                    R.string.macro_per_100g_fmt,
                                    food.caloriesPer100g,
                                    formatMacro(food.proteinPer100g),
                                    formatMacro(food.carbsPer100g),
                                    formatMacro(food.fatPer100g)
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = strRes(R.string.macro_portion_title),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Logged Meals Section
        item(key = "macro_recent_meals_header", contentType = "section_header") {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = strRes(R.string.recent_meals),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (calorieLogs.isEmpty()) {
            item(key = "macro_no_logs", contentType = "empty_state") {
                Card(
                    colors = surfaceCardColors,
                    shape = MaterialTheme.shapes.medium,
                    border = cardBorder,
                    elevation = flatElevation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strRes(R.string.macro_no_logs_today),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        } else {
            items(
                items = calorieLogs,
                key = { it.id },
                contentType = { "meal_log" }
            ) { meal ->
                val timeStr = remember(meal.loggedAtMillis, langCode) {
                    SimpleDateFormat("dd MMM, HH:mm", locale).format(Date(meal.loggedAtMillis))
                }
                Card(
                    colors = surfaceCardColors,
                    shape = MaterialTheme.shapes.medium,
                    border = cardBorder,
                    elevation = flatElevation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Icon(Icons.Rounded.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = meal.mealName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${meal.calories} kcal • P:${meal.proteinGrams}g  C:${meal.carbsGrams}g  F:${meal.fatGrams}g",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = timeStr,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { onDeleteLog(meal.id) },
                            modifier = Modifier.size(48.dp)
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

@Composable
private fun FoodPortionDialog(
    food: FitnessFoodItem,
    langCode: String,
    grams: Int,
    xpBonus: Int,
    onGramsChange: (Int) -> Unit,
    onConfirmLog: () -> Unit,
    onDismiss: () -> Unit
) {
    val scaled = remember(food, grams) { food.scaleForGrams(grams) }
    val presets = remember { listOf(50, 100, 150, 200, 250) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.36f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("macro_portion_dialog")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = food.localizedName(langCode),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${strRes(food.category.labelRes)} • ${food.localizedPortionHint(langCode)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = strRes(R.string.common_close))
                    }
                }

                Text(
                    text = strRes(R.string.macro_portion_title),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presets.forEach { preset ->
                        val selected = grams == preset
                        Surface(
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            shape = CircleShape,
                            border = BorderStroke(
                                1.dp,
                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 42.dp)
                                .clip(CircleShape)
                                .clickable { onGramsChange(preset) }
                                .testTag("portion_preset_$preset")
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = strRes(R.string.macro_grams_unit, preset),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { onGramsChange((grams - 10).coerceAtLeast(10)) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Remove, contentDescription = "-10g", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Text(
                        text = strRes(R.string.macro_grams_unit, scaled.grams),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { onGramsChange((grams + 10).coerceAtMost(1000)) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Add, contentDescription = "+10g", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Slider(
                    value = grams.toFloat().coerceIn(10f, 500f),
                    onValueChange = { onGramsChange(it.roundToInt()) },
                    valueRange = 10f..500f
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MacroPill("kcal", "${scaled.calories}", Modifier.weight(1f))
                    MacroPill(strRes(R.string.macro_protein), "${scaled.proteinGrams}g", Modifier.weight(1f))
                    MacroPill(strRes(R.string.macro_carbs), "${scaled.carbsGrams}g", Modifier.weight(1f))
                    MacroPill(strRes(R.string.macro_fat), "${scaled.fatGrams}g", Modifier.weight(1f))
                }

                Button(
                    onClick = onConfirmLog,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("macro_log_food_button")
                ) {
                    Text(
                        text = strRes(R.string.macro_log_button_fmt, scaled.calories, xpBonus),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

private fun formatMacro(value: Double): String =
    if (abs(value - value.toInt()) < 0.05) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }

@Composable
private fun rememberIntState(initial: Int) = rememberSaveable { mutableIntStateOf(initial) }
