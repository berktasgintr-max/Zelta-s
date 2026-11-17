package com.example.data.catalog

import com.example.data.model.UserProfile
import kotlin.math.abs
import kotlin.math.roundToInt

enum class CoachTopic {
    GREETING,
    PROTEIN_NUTRITION,
    FAT_LOSS,
    MUSCLE_GAIN,
    CHEST_PUSH,
    ARMS_SHOULDERS,
    BACK_POSTURE,
    LEGS_GLUTES,
    ABS_CORE,
    CARDIO_HIIT,
    YOUTH_GROWTH,
    RECOVERY_SLEEP,
    HYDRATION_SUPPS,
    MOTIVATION_PLATEAU,
    GENERAL_PLAN
}

private data class CoachRule(
    val topic: CoachTopic,
    val keywords: List<String>,
    val repliesEn: List<String>,
    val repliesTr: List<String>
)

object CoachKnowledgeBase {

    private val RULES: List<CoachRule> = listOf(
        CoachRule(
            topic = CoachTopic.GREETING,
            keywords = listOf("hello", "hi", "hey", "merhaba", "selam", "naber", "nasılsın", "who are you", "kimsin", "günaydın", "good morning", "hola", "hallo", "bonjour"),
            repliesEn = listOf(
                "No excuses, {NAME}! I'm FitChicken—your wings are pumped and your plan is locked in. Ask me about protein, fat loss, bench press, posture, or recovery!",
                "Cluck yeah, {NAME}! Ready to crush today's {GOAL_EN} target? Drop any training or nutrition question and let's dial it in!"
            ),
            repliesTr = listOf(
                "Bahane yok {NAME}! Ben FitChicken—kanatlarım ısındı, planın hazır. Protein, yağ yakımı, göğüs/kol, postür veya toparlanma hakkında ne istersen sor!",
                "Selam şampiyon {NAME}! Bugünkü {GOAL_TR} hedefin için hazırım. Antrenman veya beslenme sorunu yaz, hemen çözelim!"
            )
        ),
        CoachRule(
            topic = CoachTopic.PROTEIN_NUTRITION,
            keywords = listOf("protein", "nutrition", "diet", "eat", "food", "meal", "carb", "macro", "calorie", "chicken", "egg", "beslenme", "yemek", "makro", "kalori", "tavuk", "yumurta", "diyet", "öğün", "karbonhidrat"),
            repliesEn = listOf(
                "Fuel rule for {NAME}: aim for {PROTEIN_G}g protein daily (~1.8g/kg) across 3–4 meals within your {KCAL} kcal budget.\n• Top picks: grilled chicken breast, egg whites, strained Greek yogurt, tuna, and lentils.\n• Pair post-workout carbs (rice, oats, or sweet potato) within 90 minutes to refill glycogen!",
                "Keep nutrition simple and deadly consistent, {NAME}:\n• Hit {PROTEIN_G}g protein/day (25–35g per main meal).\n• Fill half your plate with veggies/fiber, add slow carbs around workouts, and log servings in the Macro Calculator tab!"
            ),
            repliesTr = listOf(
                "{NAME}, yakıt kuralı net: Günlük {KCAL} kcal bütçen içinde yaklaşık {PROTEIN_G}g protein (~1.8g/kg) hedefle.\n• En temiz kaynaklar: ızgara tavuk göğsü, yumurta beyazı, süzme yoğurt, lor peyniri, ton balığı ve mercimek.\n• Antrenman sonrası 90 dakika içinde pirinç veya yulafla kompleks karbonhidrat ekle!",
                "Beslenmeyi karmaşıklaştırma şampiyon {NAME}:\n• Ana öğünlere 25–35g protein dağıtıp günde {PROTEIN_G}g hedefini yakala.\n• Besin & Makro Hesaplayıcı sekmesinden gramajlarını girerek gün boyu kontrolü elinde tut!"
            )
        ),
        CoachRule(
            topic = CoachTopic.FAT_LOSS,
            keywords = listOf("fat", "burn", "cut", "lean", "lose weight", "weight loss", "deficit", "belly", "yağ", "yak", "kilo ver", "definisyon", "zayıfla", "göbek", "incel", "abnehmen"),
            repliesEn = listOf(
                "Smart cutting strategy for {NAME} ({WEIGHT}kg → {TARGET}kg):\n• Hold a steady 300–450 kcal deficit around {KCAL} kcal/day so fat drops while strength stays!\n• Keep protein high ({PROTEIN_G}g/day) and hit 8,000–10,000 daily steps.\n• Lift 3–4x/week—cardio helps burn energy, but strength training tells your body to keep every ounce of muscle.",
                "Spot reduction is a myth, {NAME}, but full-body consistency melts stubborn fat fast!\n• Swap sugary drinks for sparkling water or black coffee.\n• Prioritize 7–8h sleep (poor sleep spikes hunger hormones) and pair resistance workouts with brisk incline walks."
            ),
            repliesTr = listOf(
                "{NAME}, kas kaybetmeden yağ yakımı formülü ({WEIGHT} kg → {TARGET} kg):\n• Günlük {KCAL} kcal civarında 300–450 kalorilik kontrollü açık bırak.\n• Proteini yüksek tut ({PROTEIN_G}g/gün) ve günlük 8.000–10.000 adım hedefle.\n• Ağırlık/vücut ağırlığı antrenmanını aksatma ki vücut yağ yakarken kası korusun!",
                "Bölgesel eritme yok ama disiplinli bütçe var {NAME}!\n• Şekerli içecekleri sıfırlayıp bol su + sade kahve seç.\n• Akşam geç atıştırmalıkları yerine süzme yoğurt/çilek koy, antrenmanların yanına tempolu yürüyüş ekle."
            )
        ),
        CoachRule(
            topic = CoachTopic.MUSCLE_GAIN,
            keywords = listOf("muscle", "bulk", "hypertrophy", "mass", "size", "gain", "strength", "kas", "hacim", "kütle", "hipertrofi", "kilo al", "güç", "muskel"),
            repliesEn = listOf(
                "Hypertrophy blueprint for {NAME}:\n• Master Progressive Overload—add +1 clean rep or +1–2.5 kg to key lifts each week.\n• Work in the 8–12 rep range with a controlled 2–3s lowering (eccentric) phase.\n• Hit {PROTEIN_G}g protein daily and sleep 7.5–9 hours—remember, muscles repair and grow while you rest!",
                "Want thick, athletic muscle, {NAME}? Focus on compound lifts first (squats, presses, rows, pull-ups), rest 90–120s on heavy sets, and eat cleanly around {KCAL} kcal with quality rice, oats, eggs, and lean poultry."
            ),
            repliesTr = listOf(
                "{NAME}, temiz kas kütlesi (hipertrofi) rehberin:\n• Progressive Overload uygula: Her hafta temel hareketlerde +1 temiz tekrar veya +1–2.5 kg artır.\n• Hareketin iniş fazını 2–3 saniyede kontrollü yap, 8–12 tekrar aralığında kası hisset.\n• Günlük {PROTEIN_G}g protein + 7,5–9 saat uyku ile büyümeyi garantile!",
                "Güç ve hacim için önce bileşik hareketlere odaklan {NAME} (squat, press, row, barfiks). Ağır setlerde 90 saniye dinlen, antrenman sonrası öğününü atlama!"
            )
        ),
        CoachRule(
            topic = CoachTopic.CHEST_PUSH,
            keywords = listOf("chest", "bench", "pushup", "push-up", "push up", "pec", "göğüs", "şınav", "bench press", "brust"),
            repliesEn = listOf(
                "Build a boulder chest, {NAME}!\n• Pin your shoulder blades DOWN and BACK into the bench or floor—don't let shoulders roll forward.\n• Use a 45° elbow angle (not 90° flared) to protect shoulders and maximize pec tension.\n• Break push-up/bench plateaus with 3-second paused reps at the bottom plus incline presses!",
                "Stuck on bench press or push-ups? Add 2 warm-up sets, pause 1 second on your chest, drive feet firmly into the floor, and finish with cable/dumbbell flyes for deep pec stretch."
            ),
            repliesTr = listOf(
                "Sağlam göğüs gelişimi için altın kurallar {NAME}:\n• Bench veya şınavda kürek kemiklerini geriye ve aşağıya kilitle.\n• Dirseklerini 90° dışa değil, 45° açıyla açarak hem omzunu koru hem göğüs kasına yüklen.\n• Tıkanıklığı kırmak için göğüste 1 saniye duraklamalı (paused) tekrarlar ve eğik (incline) açılar ekle!",
                "Şınav ve bench press'te güçlenmek için 3 saniyede kontrollü inip patlayıcı itiş yap! İtiş günlerinin sonuna fly veya şınav drop-seti ekleyebilirsin."
            )
        ),
        CoachRule(
            topic = CoachTopic.ARMS_SHOULDERS,
            keywords = listOf("arm", "bicep", "tricep", "shoulder", "delt", "lateral", "kol", "omuz", "ön kol", "arka kol", "biceps", "triceps", "schulter"),
            repliesEn = listOf(
                "Sleeve-stretching arm & shoulder formula:\n• Triceps make up 2/3 of upper-arm size—prioritize overhead extensions and dips alongside pushdowns!\n• On bicep curls, lock your elbows beside your ribs (no lower-back swinging).\n• For 3D shoulders, nail strict lateral raises with pinkies level, stopping just at shoulder height.",
                "Bro-tip from FitChicken: Don't ego-lift on lateral raises or curls! Lighter dumbbells with a 2-second hold at the top build wider delts and sharper arm peaks fast."
            ),
            repliesTr = listOf(
                "Güçlü kollar ve geniş omuzlar için formül:\n• Kol hacminin %65'ini arka kol (triceps) oluşturur—pushdown yanına mutlaka overhead extension ve dips ekle!\n• Biceps curl'de dirseklerini gövdene sabitle, belinle ağırlığı savurma.\n• Geniş omuz görünümü için lateral raise'i hafif kiloyla, tepe noktada 1 saniye sıkarak yap!",
                "FitChicken tüyosu: Omuz ve kolda egonu kapıda bırak! Kontrollü ağırlık ve tam kasılma, sallayarak yapılan ağır setlerden iki kat hızlı sonuç verir."
            )
        ),
        CoachRule(
            topic = CoachTopic.BACK_POSTURE,
            keywords = listOf("back", "pullup", "pull-up", "pull up", "row", "lat", "posture", "slouch", "spine", "neck", "sırt", "barfiks", "kanat", "duruş", "kambur", "postür", "omurga", "boyun", "rücken"),
            repliesEn = listOf(
                "Unlock a V-taper back and tall posture, {NAME}:\n• Imagine pulling through your ELBOWS rather than your hands on rows and pulldowns.\n• For desk posture: do 2x15 band pull-aparts, 2x30s dead hangs, and thoracic extensions daily.\n• Match every pressing exercise with at least one horizontal rowing movement!",
                "Fixing rounded shoulders takes two keys: open tight chest/hip flexors daily, and strengthen mid-back rhomboids + lower traps with face pulls, scapular pull-ups, and chest-supported rows."
            ),
            repliesTr = listOf(
                "V-form sırt ve dik duruş rehberi {NAME}:\n• Sırt çekerken barı ellerinle değil, DİRSEKLERİNLE geriye çektiğini hayal et.\n• Kamburluk ve masa başı duruşu için her gün 2x30 sn barfikste asılma (dead hang) + kürek kemiği sıkıştırma (band pull-apart / face pull) uygula.\n• Her göğüs itişine karşılık mutlaka bir sırt çekişi ekle!",
                "Dik postür için göğüs önünü esnetip kürek kemikleri arasındaki sırt kaslarını güçlendirmen şart. Sırt ve Postür kategorimizdeki programlar tam sana göre!"
            )
        ),
        CoachRule(
            topic = CoachTopic.LEGS_GLUTES,
            keywords = listOf("leg", "squat", "lunge", "glute", "calf", "calves", "deadlift", "hamstring", "quad", "bacak", "kalça", "baldır", "diz", "beine"),
            repliesEn = listOf(
                "Never skip leg day, {NAME}! Strong legs power every athletic skill:\n• Drive knees outward in line with your toes on squats and keep your heels rooted.\n• Hit quads with squats/lunges and target hamstrings + glutes cleanly with Romanian deadlifts & glute bridges.\n• Finish with slow-tempo calf raises with a full bottom stretch!",
                "Lower-body mastery: keep your chest proud and brace your core before descending into squats or RDLs. If lower back feels strained, reduce load and focus on depth & hip hinge technique."
            ),
            repliesTr = listOf(
                "Bacak gününü asla atlama {NAME}! Güçlü bacaklar tüm vücudun motorudur:\n• Squat'ta topuklarını yere sabitle, dizlerin içe kaçmadan ayak parmak hizasında insin.\n• Ön bacak için squat/lunge, arka bacak ve kalça için RDL (Romanian Deadlift) ve glute bridge kombinle.\n• Sona kontrollü calf raise ekleyerek baldırları bitir!",
                "Deadlift ve squat'ta belini korumak için harekete başlamadan karnını sıkıca kilitle ve sırtını nötr tut. Ağırlıktan önce temiz form gelir!"
            )
        ),
        CoachRule(
            topic = CoachTopic.ABS_CORE,
            keywords = listOf("abs", "core", "plank", "waist", "crunch", "six pack", "sixpack", "stomach", "karın", "bel", "mekik", "baklava", "bauch"),
            repliesEn = listOf(
                "Abs are forged in training and revealed by nutrition, {NAME}:\n• Ditch endless 200-rep crunches—train anti-extension and anti-rotation with RKC planks, dead bugs, hollow holds, and hanging knee raises.\n• Train core 3x/week for 10–12 focused minutes while staying consistent with your {KCAL} kcal target!",
                "Pro core cue: squeeze your glutes and tuck your pelvis slightly on planks so your abs—not your lower back—do 100% of the work!"
            ),
            repliesTr = listOf(
                "Karın kası antrenmanda şekillenir, mutfakta görünür olur {NAME}:\n• Yüzlerce yarım mekik yerine plank, dead bug, hollow hold ve asılı bacak kaldırma gibi yüksek kaliteli core hareketleri yap.\n• Haftada 3 gün 10 dakika yoğun core çalışıp günlük {KCAL} kcal beslenme düzenine sadık kal!",
                "Plank yaparken kalçanı ve karnını aynı anda sık! Belin aşağı sarkıyorsa hareketi hemen düzelt ya da dizlerinden destek al."
            )
        ),
        CoachRule(
            topic = CoachTopic.CARDIO_HIIT,
            keywords = listOf("cardio", "hiit", "run", "running", "stamina", "endurance", "jump", "vertical", "skipping", "kardiyo", "koşu", "kondisyon", "nefes", "sıçrama", "ip atla", "dayanıklılık"),
            repliesEn = listOf(
                "Conditioning & stamina booster:\n• Blend 2 steady Zone-2 sessions (brisk incline walk, cycling, or light jog at conversational pace) with 1 crisp HIIT finisher per week.\n• For vertical jump & explosiveness: warm up ankles thoroughly, do crisp pogo hops and box/squat jumps with full 60–90s rest between explosive sets.",
                "Track your daily step floor first! Reaching 8,500+ steps daily builds huge aerobic capacity and burns extra calories without wrecking your leg recovery."
            ),
            repliesTr = listOf(
                "Kondisyon ve nefes kapasiteni patlatma planı:\n• Haftada 2 gün orta tempo (Zone-2 tempolu yürüyüş/bisiklet) + 1 gün kısa HIIT çalışması ekle.\n• Sıçrama ve patlayıcılık hedefliyorsan her sıçrama setinden sonra 60–90 sn tam dinlen ki sinir sistemin taze kalsın!",
                "En gizli kardiyo silahın günlük adımlar: 8.500–10.000 adım bandını yakalamak hem kalbi güçlendirir hem bacak antrenmanını yormadan yağ yakar."
            )
        ),
        CoachRule(
            topic = CoachTopic.YOUTH_GROWTH,
            keywords = listOf("teen", "youth", "height", "tall", "grow", "growth", "0-18", "young", "boy", "uzama", "ergen", "büyüme", "genç", "yaş", "plak"),
            repliesEn = listOf(
                "Ages 0–18 Growth & Posture Blueprint:\n• Protect growth plates: skip maximal 1-rep lifts and chase crisp bodyweight control, athletic coordination, and light-to-moderate technique work.\n• Spine decompression & posture: 2–3x30s dead hangs, cobra/cat-cow flows, and thoracic stretches support tall upright posture.\n• Growth hormone peaks during deep sleep—aim for 8.5–9.5 hours nightly plus calcium & protein-rich meals!",
                "Teen athlete rule: build your foundation now—vertical jump agility, core stability, and clean push/pull habits will pay dividends for decades without risking joint strain."
            ),
            repliesTr = listOf(
                "0–18 Yaş Sağlıklı Büyüme & Postür Rehberi:\n• Büyüme plaklarını koru: Tek tekrarlı aşırı ağır kaldırmalardan uzak dur; vücut ağırlığı kontrolü, çeviklik ve temiz tekniğe odaklan.\n• Omurga esnekliği ve dik duruş için her gün barfikste asılma (dead hang), kobra/kedi-deve esnemesi ve sıçrama drilleri uygula.\n• Büyüme hormonu derin uykuda salgılanır: Gece 8,5–9,5 saat uyu ve kalsiyum + protein dengeni koru!",
                "Ergenlikte düzenli spor boy uzamasını destekleyen hormonları ve postürü harika etkiler. Uygulamadaki '0-18 Yaş' filtreli programlar tam bu yaş grubu için güvenli tasarlandı."
            )
        ),
        CoachRule(
            topic = CoachTopic.RECOVERY_SLEEP,
            keywords = listOf("sleep", "recover", "recovery", "sore", "doms", "pain", "hurt", "rest", "stretch", "mobility", "uyku", "toparlan", "dinlen", "ağrı", "esneme", "hamlık", "sakatlık", "schlaf"),
            repliesEn = listOf(
                "Recovery is where strength is actually built, {NAME}:\n• Muscle soreness (DOMS) within 24–48h is normal, but sharp joint pain is a stop signal—always swap or lighten a movement if a joint hurts.\n• Prioritize 7.5–9 hours of dark, cool sleep, drink 2.5L+ water, and spend 5–10 minutes on light walking + hip/shoulder mobility.",
                "Rest days aren't lazy days—your nervous system and muscle fibers rebuild stronger when you sleep well, eat your {PROTEIN_G}g protein, and keep light blood flow moving."
            ),
            repliesTr = listOf(
                "{NAME}, kas aslında salonda değil dinlenirken büyür!\n• 24–48 saat süren tatlı kas ağrısı (hamlık) normaldir; ama keskin eklem ağrısı dur sinyalidir, hareketi hafiflet.\n• Günde 7,5–9 saat karanlık/serin odada uyu, 2,5L+ su iç ve 5–10 dakikalık kalça/omuz mobilitesi ekle.",
                "Dinlenme günü tembellik değildir şampiyon! Yeterli uyku ve {PROTEIN_G}g proteinle kas liflerin bir sonraki antrenmana çok daha güçlü döner."
            )
        ),
        CoachRule(
            topic = CoachTopic.HYDRATION_SUPPS,
            keywords = listOf("water", "hydrat", "creatine", "caffeine", "coffee", "supplement", "vitamin", "whey", "omega", "su", "kreatin", "kafein", "kahve", "takviye", "vitamin", "wasser"),
            repliesEn = listOf(
                "Evidence-based hydration & basics:\n• Hydration first: 35–40 ml water per kg body weight (~2.5–3.2L daily). Even mild dehydration drops strength and focus!\n• Proven staples (for adults): Whey protein (convenient food, not magic) and Creatine Monohydrate (3–5g daily, well hydrated).\n• Avoid caffeine within 8 hours of bedtime so your deep recovery sleep stays spotless.",
                "No supplement beats real whole food, water, and consistency, {NAME}! Get your 2.5L water quest checked off daily first."
            ),
            repliesTr = listOf(
                "Su ve bilimsel temel destek rehberi:\n• Önce su: Günde en az 2,5–3 litre su iç; %2'lik susuzluk bile kuvvetini belirgin düşürür.\n• Yetişkinler için kanıtlı temel destekler: Pratiklik için Whey protein ve düzenli günlük 3–5g Kreatin Monohidrat.\n• Uyku kalitesi için kafeini akşam saat 18:00'den sonraya bırakma!",
                "Hiçbir takviye gerçek yemek, su ve düzenli uykunun yerini tutmaz {NAME}! Önce günlük 2,5L su görevini tamamla."
            )
        ),
        CoachRule(
            topic = CoachTopic.MOTIVATION_PLATEAU,
            keywords = listOf("motivat", "tired", "plateau", "stuck", "quit", "discipline", "lazy", "hard", "boring", "sad", "stress", "motivasyon", "yorgun", "plato", "tıkan", "sıkıl", "üşen", "disiplin", "stres", "zor"),
            repliesEn = listOf(
                "Listen up, {NAME}: motivation visits on good days, DISCIPLINE shows up every day!\n• Feeling sluggish? Cut today's target in half—do just 15 minutes and 2 warm-up sets. Winning the start keeps your streak alive.\n• Hitting a plateau? Track reps cleanly, increase sleep by 45 minutes, and try swapping grip width or slowing your 3-second lowering tempo!",
                "Every champion had days they wanted to skip, {NAME}. Lace up, hit the 30-second posture opener quest, and let momentum carry you forward!"
            ),
            repliesTr = listOf(
                "Dinle {NAME}: Motivasyon iyi günlerde uğrar, DİSİPLİN her gün kapıyı açar!\n• Bugün enerjin düşükse hedefi küçült: Sadece 15 dakikalık kısa bir ev/esneme seansı yap—başlamak seriyi kurtarır.\n• Gelişimin durduysa (plato): Uykunu 45 dk artır, tekrarları kaydet ve iniş temposunu 3 saniyeye yavaşlat!",
                "Şampiyonlar bile bazen üşenir {NAME}, ama yine de ilk seti başlatır. Bugün en kısa antrenmanı seç, serini koru!"
            )
        )
    )

    fun generateLocalReply(question: String, profile: UserProfile): String {
        val qLower = question.trim().lowercase()
        val isTr = profile.appLanguage.equals("tr", ignoreCase = true)

        // Check matched rules ordered by score (count of keyword hits)
        val scoredRules = RULES.mapNotNull { rule ->
            val hits = rule.keywords.count { kw -> qLower.contains(kw) }
            if (hits > 0) rule to hits else null
        }.sortedByDescending { it.second }

        val chosenTemplate = if (scoredRules.isNotEmpty()) {
            val topRule = scoredRules.first().first
            val pool = if (isTr) topRule.repliesTr else topRule.repliesEn
            val idx = abs(qLower.hashCode()) % pool.size
            // If user is 0-18 and asked about lifting/weight/muscle, append youth safety reminder!
            val base = pool[idx]
            if (profile.isYouthGrowthMode && topRule.topic != CoachTopic.YOUTH_GROWTH &&
                (topRule.topic == CoachTopic.MUSCLE_GAIN || topRule.topic == CoachTopic.FAT_LOSS || topRule.topic == CoachTopic.CHEST_PUSH)
            ) {
                val guard = if (isTr) {
                    "\n\n🛡️ 0–18 Büyüme Modu Notu: Maksimal ağır kilolar yerine temiz form, dik duruş ve yeterli uykuyu önceliklendir."
                } else {
                    "\n\n🛡️ Ages 0–18 Safety Note: Prioritize crisp form, spine posture, and plenty of sleep over maximal heavy weights."
                }
                base + guard
            } else {
                base
            }
        } else {
            // Smart default contextual response using user's profile + coach tip
            val tipPool = if (isTr) CoachWisdomAndStories.COACH_TIPS else CoachWisdomAndStories.COACH_TIPS_EN
            val pickedTip = tipPool[abs(qLower.hashCode()) % tipPool.size]
            if (isTr) {
                "Harika soru {NAME}! Şu an {GOAL_TR} hedefin doğrultusunda günlük {KCAL} kcal ve {PROTEIN_G}g protein bandında ilerlemen en verimli strateji.\n\n• Hızlı Koç Tavsiyesi: $pickedTip\n\nDaha spesifik rehber için 'protein', 'yağ yakımı', 'göğüs/sırt/bacak', 'uyku' veya '0-18 boy/postür' kelimelerinden biriyle sorabilirsin!"
            } else {
                "Great question, {NAME}! For your {GOAL_EN} focus, staying locked around {KCAL} kcal and {PROTEIN_G}g protein with consistent weekly sessions is your winning formula.\n\n• FitChicken Coach Tip: $pickedTip\n\nTip: Ask me specifically about protein, fat loss, muscle gain, chest/back/legs, sleep, hydration, or 0–18 posture!"
            }
        }

        return personalize(chosenTemplate, profile)
    }

    private fun personalize(template: String, profile: UserProfile): String {
        val safeName = profile.displayName.ifBlank {
            if (profile.appLanguage.equals("tr", ignoreCase = true)) "Şampiyon" else "Champ"
        }
        val proteinG = (profile.currentWeightKg * 1.8).roundToInt().coerceIn(70, 220)
        val goalEn = when (profile.goal) {
            "fat_loss" -> "Fat Loss & Definition"
            "muscle" -> "Muscle & Strength"
            "growth_posture" -> "Posture & Athletic Growth"
            else -> "General Conditioning"
        }
        val goalTr = when (profile.goal) {
            "fat_loss" -> "Yağ Yakımı & Sıkılaşma"
            "muscle" -> "Kas Kütlesi & Güç"
            "growth_posture" -> "Postür & Atletik Gelişim"
            else -> "Genel Kondisyon"
        }

        return template
            .replace("{NAME}", safeName)
            .replace("{KCAL}", profile.dailyCalorieTarget.coerceAtLeast(1200).toString())
            .replace("{PROTEIN_G}", proteinG.toString())
            .replace("{WEIGHT}", profile.currentWeightKg.roundToInt().toString())
            .replace("{TARGET}", profile.targetWeightKg.roundToInt().toString())
            .replace("{GOAL_EN}", goalEn)
            .replace("{GOAL_TR}", goalTr)
    }
}
