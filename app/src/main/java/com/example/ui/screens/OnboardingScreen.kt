package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.CoachWisdomAndStories
import com.example.data.model.UserProfile
import com.example.ui.components.FemaleTopPink
import com.example.ui.components.MaleShirtBlue
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.WarmGold
import com.example.ui.theme.YouthPurple
import kotlin.math.roundToInt

private fun turkishPercentAblative(percent: Int): String {
    val suffix = when {
        percent % 10 != 0 -> when (percent % 10) {
            1 -> "'inden"
            2 -> "'sinden"
            3 -> "'ünden"
            4 -> "'ünden"
            5 -> "'inden"
            6 -> "'sından"
            7 -> "'sinden"
            8 -> "'inden"
            9 -> "'undan"
            else -> "'inden"
        }
        else -> when (percent) {
            10 -> "'undan"
            20 -> "'sinden"
            30 -> "'undan"
            40 -> "'ından"
            50 -> "'sinden"
            60 -> "'ından"
            70 -> "'inden"
            80 -> "'inden"
            90 -> "'ından"
            100 -> "'ünden"
            else -> "'inden"
        }
    }
    return "%$percent$suffix"
}

private fun turkishPercentGenitive(percent: Int): String {
    val suffix = when {
        percent % 10 != 0 -> when (percent % 10) {
            1 -> "'inin"
            2 -> "'sinin"
            3 -> "'ünün"
            4 -> "'ünün"
            5 -> "'inin"
            6 -> "'sının"
            7 -> "'sinin"
            8 -> "'inin"
            9 -> "'unun"
            else -> "'inin"
        }
        else -> when (percent) {
            10 -> "'unun"
            20 -> "'sinin"
            30 -> "'unun"
            40 -> "'ının"
            50 -> "'sinin"
            60 -> "'ının"
            70 -> "'inin"
            80 -> "'inin"
            90 -> "'ının"
            100 -> "'ünün"
            else -> "'inin"
        }
    }
    return "%$percent$suffix"
}

@Composable
fun OnboardingScreen(
    initialProfile: UserProfile,
    onSelectLanguage: (String) -> Unit,
    onComplete: (UserProfile, String) -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current
    val totalSteps = 15
    var step by remember { mutableIntStateOf(0) }

    var name by remember { mutableStateOf(if (initialProfile.displayName == "Sporcu") "" else initialProfile.displayName) }
    var gender by remember { mutableStateOf(initialProfile.gender) }
    var age by remember { mutableFloatStateOf(initialProfile.age.toFloat().coerceIn(10f, 80f)) }
    var heightCm by remember { mutableFloatStateOf(initialProfile.heightCm.toFloat().coerceIn(120f, 220f)) }
    var currentWeight by remember { mutableFloatStateOf(initialProfile.currentWeightKg.toFloat().coerceIn(35f, 180f)) }
    var targetWeight by remember { mutableFloatStateOf(initialProfile.targetWeightKg.toFloat().coerceIn(35f, 180f)) }
    var goal by remember { mutableStateOf(initialProfile.goal) }
    var activityLevel by remember { mutableStateOf(initialProfile.activityLevel) }
    var experienceLevel by remember { mutableStateOf(initialProfile.experienceLevel) }
    var environment by remember { mutableStateOf(initialProfile.environment) }
    var weeklyDays by remember { mutableIntStateOf(initialProfile.weeklyDays) }
    var sessionMinutes by remember { mutableIntStateOf(initialProfile.sessionMinutes) }
    var sleepQuality by remember { mutableStateOf(initialProfile.sleepQuality) }
    var nutritionHabit by remember { mutableStateOf(initialProfile.nutritionHabit) }
    var focusArea by remember { mutableStateOf(initialProfile.focusArea) }

    fun buildProfile(): UserProfile = initialProfile.copy(
        displayName = name.trim().ifEmpty { if (isEn) "Champion" else "Şampiyon" },
        gender = gender,
        appLanguage = strings.langCode,
        age = age.roundToInt(),
        heightCm = heightCm.roundToInt(),
        startWeightKg = currentWeight.roundToInt().toDouble(),
        currentWeightKg = currentWeight.roundToInt().toDouble(),
        targetWeightKg = targetWeight.roundToInt().toDouble(),
        goal = goal,
        activityLevel = activityLevel,
        experienceLevel = experienceLevel,
        environment = environment,
        weeklyDays = weeklyDays,
        sessionMinutes = sessionMinutes,
        sleepQuality = sleepQuality,
        nutritionHabit = nutritionHabit,
        focusArea = focusArea
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Top Header with progress & Language Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (step > 0) {
                IconButton(
                    onClick = { step-- },
                    modifier = Modifier.testTag("onboarding_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = if (isEn) "Back" else "Geri",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isEn) "Personal Analysis • ${step + 1} / ${totalSteps + 1}"
                    else "Kişisel Analiz • ${step + 1} / ${totalSteps + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (step + 1f) / (totalSteps + 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Instant TR | EN Switcher in Onboarding
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .clip(CircleShape)
                    .clickable { onSelectLanguage(if (isEn) "tr" else "en") }
                    .testTag("onboarding_language_toggle")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Rounded.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEn) "EN" else "TR",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Animated Step Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    val forward = targetState > initialState
                    (slideInHorizontally(animationSpec = tween(260)) { if (forward) it / 3 else -it / 3 } + fadeIn(tween(240))) togetherWith
                        (slideOutHorizontally(animationSpec = tween(220)) { if (forward) -it / 3 else it / 3 } + fadeOut(tween(180)))
                },
                label = "onboarding_step"
            ) { currentStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (currentStep) {
                        0 -> {
                            ChickenCoachSpeechHeader(
                                title = if (isEn) "Cock-a-doodle-doo! I'm Koç Tavuk!" else "Gıt-gıdak! Ben Koç Tavuk!",
                                subtitle = if (isEn) {
                                    "Let's run a 3-4 minute fitness analysis so I can build your ideal plan. First, what should I call you?"
                                } else {
                                    "Sana en doğru programı çıkarabilmem için 3-4 dakikalık detaylı bir analiz yapacağız. Önce sana nasıl hitap edeyim?"
                                }
                            )
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it.take(30) },
                                label = { Text(if (isEn) "Your Name or Nickname" else "Adın veya Lakabın") },
                                placeholder = { Text(if (isEn) "E.g., Alex, Champ..." else "Örn: Berk, Şampiyon...") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_name_input")
                            )
                            InfoCalloutCard(
                                title = if (isEn) "Why do we ask these details?" else "Neden bu kadar detaylı soruyoruz?",
                                body = if (isEn) {
                                    "Combining your age, height, weight, sleep, and training history lets us calibrate intensity, posture, and growth-safe volume."
                                } else {
                                    "Yaşın, boyun, kilon, uyku düzenin ve spor geçmişin birleştiğinde sana özel antrenman yoğunluğu ve güvenli gelişim stratejisi hesaplanır."
                                }
                            )
                        }
                        1 -> {
                            StepTitle(
                                title = if (isEn) "Which athlete character do you prefer?" else "Hareket rehberi karakterini seç",
                                subtitle = if (isEn) {
                                    "All animated exercise demonstrations will feature your chosen athlete (you can switch anytime in Settings)."
                                } else {
                                    "Tüm egzersiz animasyonlarında hareketleri gösterecek sporcu modelini seç (istediğin zaman Ayarlar'dan değiştirebilirsin)."
                                }
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                GenderChoiceCard(
                                    title = strings.maleLabel,
                                    subtitle = if (isEn) "Male athlete animations" else "Erkek sporcu animasyonları",
                                    icon = Icons.Rounded.Male,
                                    accent = MaleShirtBlue,
                                    selected = gender == "male",
                                    onClick = { gender = "male" },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("onboarding_gender_male")
                                )
                                GenderChoiceCard(
                                    title = strings.femaleLabel,
                                    subtitle = if (isEn) "Female athlete animations" else "Kadın sporcu animasyonları",
                                    icon = Icons.Rounded.Female,
                                    accent = FemaleTopPink,
                                    selected = gender == "female",
                                    onClick = { gender = "female" },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("onboarding_gender_female")
                                )
                            }
                        }
                        2 -> {
                            val ageInt = age.roundToInt()
                            StepTitle(
                                title = if (isEn) "How old are you?" else "Kaç yaşındasın?",
                                subtitle = if (isEn) "Your age determines joint-safe loads and youth growth-mode tracks." else "Yaşın, eklem güvenliği ve büyüme çağı egzersizlerini belirler."
                            )
                            MetricDisplayBadge(if (isEn) "$ageInt Years Old" else "$ageInt Yaş")
                            Slider(
                                value = age,
                                onValueChange = { age = it },
                                valueRange = 10f..75f,
                                colors = SliderDefaults.colors(thumbColor = FlameOrange, activeTrackColor = FlameOrange),
                                modifier = Modifier.testTag("onboarding_age_slider")
                            )
                            if (ageInt <= 18) {
                                Surface(
                                    color = ext.purpleContainer,
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, ext.purpleText, MaterialTheme.shapes.medium)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = ext.purpleText)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = if (isEn) {
                                                "Ages 0-18 Growth Mode Active! Posture, vertical jump, and growth-plate-safe workouts will be prioritized for you."
                                            } else {
                                                "0-18 Yaş Büyüme Çağı Modu Aktif! Ergenlikte dik duruş, dikey sıçrama ve güvenli güç gelişimi sağlayan özel egzersizler senin için öne çıkarılacak."
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                        3 -> {
                            val h = heightCm.roundToInt()
                            StepTitle(
                                title = if (isEn) "What is your height?" else "Boyun kaç santimetre?",
                                subtitle = if (isEn) "Used to analyze your body mass balance and target weight range." else "Vücut kitle dengeni ve hedef kilonu doğru analiz etmek için gerekiyor."
                            )
                            MetricDisplayBadge("$h cm")
                            Slider(
                                value = heightCm,
                                onValueChange = { heightCm = it },
                                valueRange = 130f..215f,
                                colors = SliderDefaults.colors(thumbColor = FlameOrange, activeTrackColor = FlameOrange),
                                modifier = Modifier.testTag("onboarding_height_slider")
                            )
                        }
                        4 -> {
                            val cw = currentWeight.roundToInt()
                            val tw = targetWeight.roundToInt()
                            StepTitle(
                                title = if (isEn) "Current & target weight?" else "Mevcut ve hedef kilon nedir?",
                                subtitle = if (isEn) "Your weight tracker chart will plot progress toward this goal." else "Kilo takibi grafiğin bu hedeflere göre çizilecek."
                            )
                            MetricDisplayBadge(if (isEn) "Current: $cw kg" else "Mevcut: $cw kg")
                            Slider(
                                value = currentWeight,
                                onValueChange = { currentWeight = it },
                                valueRange = 35f..160f,
                                colors = SliderDefaults.colors(thumbColor = FlameOrange, activeTrackColor = FlameOrange)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            MetricDisplayBadge(if (isEn) "Target: $tw kg" else "Hedef: $tw kg", accent = ElectricTeal)
                            Slider(
                                value = targetWeight,
                                onValueChange = { targetWeight = it },
                                valueRange = 35f..160f,
                                colors = SliderDefaults.colors(thumbColor = ElectricTeal, activeTrackColor = ElectricTeal)
                            )
                        }
                        5 -> {
                            StepTitle(
                                title = if (isEn) "What is your primary goal?" else "Ana fitness hedefin nedir?",
                                subtitle = if (isEn) "Koç Tavuk will tailor your strategy around this target." else "Koç Tavuk stratejini bu hedefe göre kuracak."
                            )
                            QuizOptionCard(
                                if (isEn) "Muscle Mass & Strength" else "Kas Kütlesi & Güç",
                                if (isEn) "Build fuller chest, shoulders, and arms" else "Hacim kazanmak, kolları ve omuzları güçlendirmek",
                                goal == "muscle"
                            ) { goal = "muscle" }
                            QuizOptionCard(
                                if (isEn) "Fat Loss & Definition" else "Yağ Yakımı & Sıkılaşma",
                                if (isEn) "Drop stubborn fat and look sharper" else "Fazla yağları yakıp daha fit ve keskin görünmek",
                                goal == "fat_loss"
                            ) { goal = "fat_loss" }
                            QuizOptionCard(
                                if (isEn) "General Conditioning & Energy" else "Genel Kondisyon & Enerji",
                                if (isEn) "Feel more energetic, resilient, and healthy" else "Daha dinç, dayanıklı ve sağlıklı hissetmek",
                                goal == "fitness"
                            ) { goal = "fitness" }
                            QuizOptionCard(
                                if (isEn) "Posture, Vertical Jump & Athletics" else "Duruş, Sıçrama & Atletik Gelişim",
                                if (isEn) "Spine mobility, jump power & tall posture (0-18 favorite)" else "Omurga açılması, sıçrama ve dik postür (0-18 Yaş favorisi)",
                                goal == "growth_posture"
                            ) { goal = "growth_posture" }
                        }
                        6 -> {
                            StepTitle(
                                title = if (isEn) "Daily physical activity level?" else "Günlük fiziksel aktiviten nasıl?",
                                subtitle = if (isEn) "Non-exercise movement shapes your recovery and calorie burn." else "Spor dışındaki hareketliliğin kalori ihtiyacını belirler."
                            )
                            QuizOptionCard(
                                if (isEn) "Desk-Bound / Low" else "Masa Başı / Hareketsiz",
                                if (isEn) "Sitting most of the day" else "Günün çoğunu oturarak geçiriyorum",
                                activityLevel == "sedentary"
                            ) { activityLevel = "sedentary" }
                            QuizOptionCard(
                                if (isEn) "Lightly Active" else "Hafif Hareketli",
                                if (isEn) "School/work walks & 4-6k daily steps" else "Okul/iş yürüyüşleri ve günlük 4-6 bin adım",
                                activityLevel == "light"
                            ) { activityLevel = "light" }
                            QuizOptionCard(
                                if (isEn) "Moderately Active" else "Orta Aktif",
                                if (isEn) "7-10k daily steps and regular movement" else "Günlük 7-10 bin adım, düzenli hareket",
                                activityLevel == "moderate"
                            ) { activityLevel = "moderate" }
                            QuizOptionCard(
                                if (isEn) "Very Active" else "Çok Aktif",
                                if (isEn) "On my feet all day or playing sports" else "Sürekli ayaktayım veya aktif spor yapıyorum",
                                activityLevel == "active"
                            ) { activityLevel = "active" }
                        }
                        7 -> {
                            StepTitle(
                                title = if (isEn) "Training background?" else "Spor geçmişin ve alışkanlıkların?",
                                subtitle = if (isEn) "Helps us set safe exercise progressions." else "Ağırlık ve tekrar yoğunluğunu seviyene göre ayarlayalım."
                            )
                            QuizOptionCard(
                                if (isEn) "Complete Beginner" else "Yeni Başlıyorum",
                                if (isEn) "No consistent routine yet" else "Düzenli spor geçmişim yok, temelden başlayacağım",
                                experienceLevel == "beginner"
                            ) { experienceLevel = "beginner" }
                            QuizOptionCard(
                                if (isEn) "Occasional" else "Arada Sırada",
                                if (isEn) "Trained on and off at home or gym" else "Düzensiz olarak evde veya salonda çalıştım",
                                experienceLevel == "occasional"
                            ) { experienceLevel = "occasional" }
                            QuizOptionCard(
                                if (isEn) "Consistent" else "Düzenli Sporcu",
                                if (isEn) "Working out multiple days per week for months" else "Aylardır haftada birkaç gün antrenman yapıyorum",
                                experienceLevel == "regular"
                            ) { experienceLevel = "regular" }
                            QuizOptionCard(
                                if (isEn) "Experienced / Advanced" else "Deneyimli / İleri",
                                if (isEn) "Confident in technique & ready for high intensity" else "Tekniklere hakimim, yüksek şiddete hazırım",
                                experienceLevel == "advanced"
                            ) { experienceLevel = "advanced" }
                        }
                        8 -> {
                            val percentile = when {
                                activityLevel == "active" || experienceLevel == "advanced" -> 78
                                activityLevel == "moderate" || experienceLevel == "regular" -> 54
                                else -> 32
                            }
                            MotivationPercentileCard(
                                percentBetter = percentile,
                                badgeLabel = if (isEn) "Top ${100 - percentile}% Tier!" else "Üst %${100 - percentile} Dilimde!",
                                headline = if (isEn) {
                                    "You are ahead of $percentile% of users!"
                                } else {
                                    "Kullanıcıların ${turkishPercentAblative(percentile)} daha iyi durumdasın!"
                                },
                                detail = if (isEn) {
                                    "Completing your physical profile already sets you apart from most people who never start. Koç Tavuk will push that potential to the peak!"
                                } else {
                                    "İlk adımı atıp fiziksel analizini doldurman bile seni harekete geçemeyen büyük bir kitlenin önüne taşıdı. Koç Tavuk olarak bu potansiyeli zirveye çıkaracağız!"
                                }
                            )
                        }
                        9 -> {
                            StepTitle(
                                title = if (isEn) "Where will you work out?" else "Antrenmanlarını nerede yapacaksın?",
                                subtitle = if (isEn) {
                                    "Home and Gym programs are built completely differently and filtered for your space."
                                } else {
                                    "Gym ve Evde antrenman programları tamamen farklı hazırlanır ve önerilerin buna göre seçilir."
                                }
                            )
                            QuizOptionCard(
                                if (isEn) "Gym (Machines & Free Weights)" else "Spor Salonu (Gym)",
                                if (isEn) "Full barbell, dumbbell, and cable power" else "Makineler, barlar ve dambıllarla tam salon gücü",
                                environment == "gym"
                            ) { environment = "gym" }
                            QuizOptionCard(
                                if (isEn) "Home Workouts" else "Evde Antrenman",
                                if (isEn) "Zero or minimal equipment at home" else "Ekipmansız veya minimal ekipmanla evde maksimum verim",
                                environment == "home"
                            ) { environment = "home" }
                            QuizOptionCard(
                                if (isEn) "Hybrid (Gym + Home)" else "Hibrit (Gym + Ev)",
                                if (isEn) "Gym when possible, home on busy days" else "Bazen salonda, vakit bulamadığımda evde devam",
                                environment == "hybrid"
                            ) { environment = "hybrid" }
                        }
                        10 -> {
                            StepTitle(
                                title = if (isEn) "Weekly frequency & session length?" else "Haftada kaç gün ve kaç dakika?",
                                subtitle = if (isEn) "A realistic schedule is the secret to never quitting." else "Gerçekçi bir takvim, sporu bırakmamanın en büyük sırrıdır."
                            )
                            Text(
                                text = if (isEn) "Training Days per Week: $weeklyDays" else "Haftalık Antrenman Günü: $weeklyDays gün",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ext.goldText
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf(2, 3, 4, 5, 6).forEach { d ->
                                    SelectableChipBox(
                                        label = if (isEn) "${d}d" else "$d Gün",
                                        selected = weeklyDays == d,
                                        onClick = { weeklyDays = d },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isEn) "Session Length: $sessionMinutes min" else "Antrenman Süresi: $sessionMinutes dakika",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ext.tealText
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf(20, 30, 45, 60).forEach { m ->
                                    SelectableChipBox(
                                        label = if (isEn) "$m min" else "$m dk",
                                        selected = sessionMinutes == m,
                                        onClick = { sessionMinutes = m },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        11 -> {
                            StepTitle(
                                title = if (isEn) "Sleep & nutrition habits?" else "Uyku ve beslenme alışkanlığın?",
                                subtitle = if (isEn) "70% of recovery and muscle growth happens outside the workout." else "Kas gelişiminin ve yağ yakımının %70'i antrenman dışında gerçekleşir."
                            )
                            Text(
                                text = if (isEn) "Sleep Schedule" else "Uyku Düzenin",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            QuizOptionCard(
                                if (isEn) "7-9 Hours Consistent" else "7-9 Saat Düzenli",
                                if (isEn) "I wake up rested" else "Sabahları dinç uyanıyorum",
                                sleepQuality == "7-8 saat düzenli"
                            ) { sleepQuality = "7-8 saat düzenli" }
                            QuizOptionCard(
                                if (isEn) "5-6 Hours / Irregular" else "5-6 Saat / Düzensiz",
                                if (isEn) "Often stay up late" else "Gece geç yatıyorum",
                                sleepQuality == "5-6 saat düzensiz"
                            ) { sleepQuality = "5-6 saat düzensiz" }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Nutrition Habit" else "Beslenme Alışkanlığın",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            QuizOptionCard(
                                if (isEn) "Protein & Whole-Food Focused" else "Protein & Ev Yemeği Odaklı",
                                if (isEn) "Structured daily meals" else "Düzenli öğün yapıyorum",
                                nutritionHabit == "Protein odaklı"
                            ) { nutritionHabit = "Protein odaklı" }
                            QuizOptionCard(
                                if (isEn) "Balanced But Improvable" else "Dengeli Ama Geliştirilebilir",
                                if (isEn) "Occasional snacks or skipped meals" else "Bazen atıştırmalık veya öğün atlama oluyor",
                                nutritionHabit == "Dengeli ama geliştirilebilir"
                            ) { nutritionHabit = "Dengeli ama geliştirilebilir" }
                        }
                        12 -> {
                            val scorePct = if (weeklyDays >= 4 && sleepQuality.contains("7-8")) 81 else 64
                            MotivationPercentileCard(
                                percentBetter = scorePct,
                                badgeLabel = if (isEn) "Top ${100 - scorePct}% Tier!" else "Üst %${100 - scorePct} Dilimde!",
                                headline = if (isEn) {
                                    "Your discipline beats $scorePct% of peers!"
                                } else {
                                    "Disiplinin kullanıcıların ${turkishPercentGenitive(scorePct)} önünde!"
                                },
                                detail = if (isEn) {
                                    "Training $weeklyDays days/week for $sessionMinutes minutes hits the sweet spot for lasting transformation."
                                } else {
                                    "Haftada $weeklyDays gün, $sessionMinutes dakikalık hedefin sürdürülebilir gelişim için ideal denge. Son bir adım kaldı!"
                                }
                            )
                        }
                        13 -> {
                            StepTitle(
                                title = if (isEn) "Priority focus area?" else "Öncelikli odak bölgen neresi?",
                                subtitle = if (isEn) "Where should we spotlight extra volume?" else "Tüm vücudu çalıştırırken özellikle nereyi öne çıkaralım?"
                            )
                            QuizOptionCard(
                                if (isEn) "Balanced Full Body" else "Tüm Vücut Dengeli",
                                if (isEn) "Proportional athletic symmetry" else "Her bölge orantılı ve atletik gelişsin",
                                focusArea == "full_body"
                            ) { focusArea = "full_body" }
                            QuizOptionCard(
                                if (isEn) "Upper Body (Chest, Back, Arms)" else "Üst Vücut (Göğüs, Sırt, Omuz, Kol)",
                                if (isEn) "Broader shoulders and stronger arms" else "Geniş omuzlar ve güçlü kollar",
                                focusArea == "upper"
                            ) { focusArea = "upper" }
                            QuizOptionCard(
                                if (isEn) "Lower Body (Legs, Glutes, Jump)" else "Alt Vücut (Bacak, Kalça, Sıçrama)",
                                if (isEn) "Powerful legs and explosive bounce" else "Güçlü bacaklar ve patlayıcı sıçrama",
                                focusArea == "lower"
                            ) { focusArea = "lower" }
                            QuizOptionCard(
                                if (isEn) "Core, Abs & Waistline" else "Karın, Core & Bel Çevresi",
                                if (isEn) "Defined abs and rock-solid stability" else "Sıkı karın kasları ve güçlü merkez",
                                focusArea == "core"
                            ) { focusArea = "core" }
                        }
                        14 -> {
                            ChickenCoachSpeechHeader(
                                title = if (isEn) "Awesome! Real Transformations" else "Harika! Dönüşen Sporcuları Gör",
                                subtitle = if (isEn) {
                                    "Thousands who built a strong physique and lifestyle with Koç Tavuk started right where you are:"
                                } else {
                                    "Koç Tavuk ile güçlü bir fizik ve disiplinli yaşam tarzı kuran binlerce kişiden bazıları seninle aynı yerden başladı:"
                                }
                            )
                            CoachWisdomAndStories.stories(strings.langCode).forEach { story ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${story.name} (${story.age})",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Surface(
                                                color = ext.mintContainer,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = story.badge,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ext.mintText,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        // 5-Star Verified Transformation Rating
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(5) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Star,
                                                    contentDescription = null,
                                                    tint = WarmGold,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = ext.goldContainer,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = if (isEn) "5.0 ★ Verified Athlete" else "5.0 ★ Doğrulanmış Sporcu",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ext.goldText,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = story.resultSummary,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = ext.goldText
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "\"${story.quote}\"",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        15 -> {
                            StepTitle(
                                title = if (isEn) "Choose Your Plan (PRO Optional)" else "Planını Seç (PRO Zorunlu Değil)",
                                subtitle = if (isEn) {
                                    "Your personalized plan is ready! Go PRO or start immediately with the Free Plan."
                                } else {
                                    "Sana özel hesaplanan program hazır! PRO Plan alabilir veya Ücretsiz Planla hemen başlayabilirsin."
                                }
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                                shape = MaterialTheme.shapes.large,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(2.dp, WarmGold, MaterialTheme.shapes.large)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Star, contentDescription = null, tint = ext.goldText)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isEn) "PRO PLAN BENEFITS" else "PRO PLAN AYRICALIKLARI",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ext.goldText
                                        )
                                    }
                                    PlanBullet(if (isEn) "Custom personalized program built from your quiz answers" else "Sorularına göre sana özel kişiselleştirilmiş program")
                                    PlanBullet(if (isEn) "AI Photo Calorie & Macro Calculator for your meals" else "Fotoğraf çekerek AI Kalori ve Makro Hesaplama")
                                    PlanBullet(if (isEn) "All ads completely removed (Ad-Free)" else "Reklamların tamamen engellenmesi (Sıfır Reklam)")
                                    PlanBullet(if (isEn) "All PRO Gym, Home & 0-18 Youth programs unlocked" else "Tüm PRO Gym, Evde ve 0-18 Yaş programları açık")
                                    PlanBullet(if (isEn) "UNLIMITED questions with Koç Tavuk AI" else "Koç Tavuk AI'ya SINIRSIZ soru sorma")
                                    PlanBullet(if (isEn) "One-tap auto weekly workout planner" else "Tek tıkla otomatik haftalık takvim oluşturma")
                                    PlanBullet(if (isEn) "Earn free: Invite 5 friends = 1 Month Free PRO!" else "Ücretsiz kazanma yolu: 5 arkadaşını davet et = 1 Ay Ücretsiz PRO!")

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = { onComplete(buildProfile(), "pro") },
                                        colors = ButtonDefaults.buttonColors(containerColor = WarmGold, contentColor = ext.onGoldButton),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("select_pro_plan_button")
                                    ) {
                                        Icon(Icons.Rounded.Verified, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isEn) "Start with PRO Plan" else "PRO Plan ile Başla", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = MaterialTheme.shapes.large,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = if (isEn) "FREE PLAN" else "ÜCRETSİZ PLAN (FREE)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isEn) {
                                            "• Full access to standard Gym, Home & 0-18 Youth workouts\n" +
                                                "• Custom PRO programs are locked (unlock individually by watching 3 ads)\n" +
                                                "• AI Coach daily limit of 3 questions • Includes ads"
                                        } else {
                                            "• Normal Gym, Evde ve 0-18 Yaş temel antrenmanlara erişim\n" +
                                                "• Özel kişisel programlar kilitlidir (isteğe bağlı 3 reklam izleyerek tek tek açabilirsin)\n" +
                                                "• AI Koç günlük 3 soru limiti • Reklam içerir"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    OutlinedButton(
                                        onClick = { onComplete(buildProfile(), "free") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("select_free_plan_button")
                                    ) {
                                        Icon(Icons.Rounded.LockOpen, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isEn) "Continue with Free Plan" else "Ücretsiz Plan ile Devam Et")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (step < totalSteps) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { step++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_next_button")
            ) {
                Text(
                    text = if (step == totalSteps - 1) {
                        if (isEn) "See Plans" else "Planları Gör"
                    } else {
                        if (isEn) "Continue" else "Devam Et"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun GenderChoiceCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) accent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .border(if (selected) 2.dp else 1.dp, if (selected) accent else MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.2f))
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(32.dp))
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ChickenCoachSpeechHeader(title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.horizontalGradient(
                    listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), MaterialTheme.colorScheme.surfaceVariant)
                )
            )
            .padding(14.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_koc_tavuk_0),
            contentDescription = "Koç Tavuk",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(92.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StepTitle(title: String, subtitle: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Text(text = subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun MetricDisplayBadge(text: String, accent: Color = FlameOrange) {
    val ext = LocalExtendedColors.current
    val resolvedAccent = if (accent == ElectricTeal) ext.tealText else ext.orangeText
    Surface(
        color = resolvedAccent.copy(alpha = 0.14f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.border(1.dp, resolvedAccent.copy(alpha = 0.55f), MaterialTheme.shapes.medium)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = resolvedAccent,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun QuizOptionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bgColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(bgColor)
            .border(if (selected) 2.dp else 1.dp, borderColor, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (selected) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun SelectableChipBox(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(48.dp)
            .clip(MaterialTheme.shapes.small)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MotivationPercentileCard(
    percentBetter: Int,
    badgeLabel: String,
    headline: String,
    detail: String
) {
    val ext = LocalExtendedColors.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, ext.mintText.copy(alpha = 0.55f), MaterialTheme.shapes.large)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(22.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_koc_tavuk_0),
                contentDescription = "Koç Tavuk",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(134.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(color = ext.mintContainer, shape = CircleShape) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = ext.mintText, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(badgeLabel, color = ext.mintText, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoCalloutCard(title: String, body: String) {
    val ext = LocalExtendedColors.current
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ext.goldText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PlanBullet(text: String) {
    val ext = LocalExtendedColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = ext.mintText, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
