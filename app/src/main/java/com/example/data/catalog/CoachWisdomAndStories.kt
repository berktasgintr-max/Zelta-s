package com.example.data.catalog

import android.content.Context
import com.example.R
import com.example.data.model.SuccessStory

object CoachWisdomAndStories {

    // 40 tapping tips from FitChicken (Turkish)
    val COACH_TIPS: List<String> = listOf(
        "Bahane üretmeyi bırak, demiri kaldır! Bugün setlerin son tekrarında pes etme; gerçek gelişim tam orada başlar.",
        "Kanatlarımı bu göğüs kafesiyle büyüttüm! Şınav ve bench press'te kürek kemiklerini geriye sık.",
        "Su içmeyi unutma şampiyon! Günde en az 2,5 - 3 litre su kas performansını %15 artırır.",
        "Tavuk göğsü yemekten ben bile çekiniyorum ama protein hedefin için her öğüne 25-35 g protein ekle!",
        "Uyku yoksa kas da yok! Büyüme hormonu en çok derin uykunun ilk 4 saatinde salgılanır.",
        "Ağırlığı egon için değil, hedef kası hissetmek için seç. 3 saniyelik kontrollü iniş mucize yaratır.",
        "Isınmadan barın altına girme! 5 dakikalık dinamik ısınma sakatlık riskini yarı yarıya azaltır.",
        "Bacak gününü atlayan, salonda eksik yürür! Squat ve lunge tüm vücudun güç motorudur.",
        "Nefesini tutma! Zorlanırken (iterken veya çekerken) nefes ver, başlangıca dönerken nefes al.",
        "0-18 yaş arasındaysan omurga sağlığın altın değerinde: Barfikste asılma ve dik duruş egzersizlerini aksatma.",
        "Haftada 3 gün düzenli yapılan orta seviye antrenman, ayda 1 gün yapılan ağır antrenmanı her zaman yener.",
        "Karın kası mutfakta belirginleşir, salonda şekillenir! Plank yaparken kalçanı sıkmayı unutma.",
        "Set aralarında telefona dalıp 5 dakika bekleme; 60-90 saniyelik dinlenme kas pompasını zirvede tutar.",
        "Omuzların öne düşüyorsa sırt çekişlerini artır! Her itiş hareketine karşılık en az 1 çekiş yap.",
        "Başlangıçta mükemmel olman gerekmiyor; sadece bugün spor ayakkabını bağlaman bile büyük zafer!",
        "Tartıdaki rakam her sabah aynı çıkmaz; ödem ve su tutumu normaldir, 7 günlük ortalamaya bak.",
        "Kafein antrenmandan 30 dakika önce odaklanmanı artırır ama saat 18:00'den sonra uykunu bozabilir.",
        "Kısa ve odaklı 20 dakikalık bir ev antrenmanı, hiç gidilmemiş 1 saatlik salondan kat kat iyidir.",
        "Bileklerini ve dirseklerini sertçe kilitleme! Her itişin sonunda eklemlerinde kontrollü pay bırak.",
        "Kardiyo kalbini güçlendirir; nabzını koruyarak ağırlık çalışırken setler arası toparlanmanı hızlandırır.",
        "Hedefin yağ yakımıysa adımlarını say! Günlük 8.000 - 10.000 adım gizli süper gücündür.",
        "Mükemmel program yoktur, sürdürdüğün program vardır. Takvimini oluştur ve her seansa tik at!",
        "Biceps curl yaparken dirseklerini gövdene sabitle, belinle ağırlığı savurma!",
        "Sırt çalışırken ağırlığı ellerinle değil, dirseklerinle geriye çektiğini hayal et; kanatların alev alacak!",
        "Antrenman sonrası 1-2 saat içinde kaliteli protein ve kompleks karbonhidrat almayı ihmal etme.",
        "Esneme sıkıcı geliyorsa sadece 5 dakika ayır; yarınki kas ağrını ciddi oranda azaltır.",
        "Ergenlik çağında aşırı tek tekrar rekoru (1RM) yerine teknik ustalık ve patlayıcı çeviklik kazan!",
        "Formun bozulduğu an set bitmiştir. Temiz 10 tekrar, bozuk 15 tekrardan daha fazla kas inşa eder.",
        "Bugün enerjin düşükse ağırlığı %20 azalt ama antrenmanı yine yap. Disiplin, motivasyondan büyüktür!",
        "Ayna karşısında postürünü kontrol et: Baş dik, çene paralel, göğüs açık, omuzlar rahat geride.",
        "Deadlift'te barı kaval kemiğine yakın tut; sırtın düz kalmıyorsa ağırlığı hemen düşür!",
        "Plank'ta belin çukurlaşıyorsa karnını ve kalçanı daha sıkı kilitle ya da dizlerini yere koy.",
        "Sadece tartıdaki kiloya değil, bel çevrene ve enerjine bak; kas dokusu yağdan daha yoğundur!",
        "Şekerli içecekleri su veya sade maden suyuyla değiştirmek ayda ortalama 1,5 kg yağ kaybı sağlar.",
        "Her hafta bir önceki haftaya göre sadece 1 tekrar veya 1 kg artırmaya çalış (Progressive Overload).",
        "Evde ekipmanın yoksa tempoyu kullan: 3 saniye in, 1 saniye bekle, patlayıcı kalk!",
        "Sabah 5 dakikalık kedi-deve ve göğüs açma akışı gün boyu masa başı ağrılarını önler.",
        "Antrenman takvimine tik attıkça beynin ödül salgılar; haftalık serini sakın bozma!",
        "Zorlandığın hareket genellikle en çok gelişime açık olduğun noktadır. Üstüne git!",
        "Ben FitChicken'ım: Kanatlarım küçük diye pes etseydim şimdi bu kollara sahip olamazdım. Sıra sende!"
    )

    // 40 tapping tips from FitChicken (English)
    val COACH_TIPS_EN: List<String> = listOf(
        "Drop the excuses and lift the iron! Never quit on the last two reps—that is where real growth lives.",
        "I built these wings with a big chest! Pin your shoulder blades back on every push-up and bench press.",
        "Stay hydrated, champ! Drinking 2.5 to 3 liters of water daily boosts muscle output by up to 15%.",
        "Even I respect chicken breast—aim for 25-35g of quality protein in each main meal.",
        "No sleep, no muscle! Growth hormone peaks during the first 4 hours of deep sleep.",
        "Lift to feel the muscle, not to feed your ego. A slow 3-second lowering phase works wonders.",
        "Never skip warm-ups! Just 5 minutes of dynamic mobility cuts injury risk in half.",
        "Skip leg day and you'll walk like a pigeon! Squats and lunges power your whole body.",
        "Don't hold your breath! Exhale during exertion and inhale smoothly as you reset.",
        "Ages 0-18: Protect your spine and posture—dead hangs and upright posture drills are gold.",
        "Three consistent workouts a week beat one heroic gym session once a month every single time.",
        "Abs are revealed in the kitchen and sculpted in training. Squeeze your glutes on every plank!",
        "Don't scroll your phone for 5 minutes between sets. Keep rest at 60-90 seconds for peak focus.",
        "Rounded shoulders? Double down on rowing and pull-aparts—match every push with a clean pull.",
        "You don't need to be perfect on day one; lacing up your shoes today is already a win.",
        "The scale fluctuates daily from water and glycogen—always track your 7-day average trend.",
        "Caffeine 30 minutes before training sharpens focus, but avoid it after 6 PM to guard your sleep.",
        "A focused 20-minute home session always beats a 60-minute gym session you skipped.",
        "Never slam your elbows or knees into hard lockout—keep slight control at the top of each rep.",
        "Cardio strengthens your heart so you recover faster between heavy strength sets.",
        "Chasing fat loss? Count your steps! 8,000-10,000 daily steps are your silent superpower.",
        "There is no magic routine—only the routine you stick to. Plan your calendar and check off days!",
        "Keep your elbows glued to your sides during curls—stop swinging your lower back.",
        "On back exercises, imagine pulling through your elbows rather than your hands.",
        "Refuel within 1-2 hours post-workout with lean protein and quality carbs.",
        "Spend just 5 minutes stretching afterward—your future self tomorrow will thank you.",
        "Teens: Chase flawless form and explosive athleticism instead of risky one-rep max lifts.",
        "A set ends the second your form breaks down. Clean 10 reps build more muscle than sloppy 15.",
        "Low energy today? Drop the weight by 20% and finish anyway. Discipline beats motivation.",
        "Check your posture in the mirror: chin level, chest proud, shoulders relaxed and back.",
        "Keep the bar close to your shins on deadlifts—if your back rounds, lighten the load immediately.",
        "If your lower back sags in a plank, brace harder or drop to your knees cleanly.",
        "Measure waist size and energy alongside scale weight—muscle is denser than fat.",
        "Swapping sugary drinks for water or sparkling water saves massive calories effortlessly.",
        "Progressive overload: try adding just 1 rep or 1 kg compared to last week.",
        "No home weights? Manipulate tempo: 3 seconds down, 1-second pause, explosive drive up.",
        "Five minutes of cat-cow and chest openers each morning erases desk stiffness.",
        "Every completed calendar session wires a winning habit—protect your weekly streak!",
        "The movement you avoid is usually the exact weakness holding you back. Attack it!",
        "I am FitChicken: If I quit when my wings were tiny, I'd never have these biceps. Your turn!"
    )

    fun tips(lang: String): List<String> = if (lang == "en") COACH_TIPS_EN else COACH_TIPS

    fun tips(context: Context): List<String> {
        val arr = context.resources.getStringArray(R.array.coach_tips)
        return if (arr.isNotEmpty()) arr.toList() else COACH_TIPS_EN
    }

    val SUCCESS_STORIES: List<SuccessStory> = listOf(
        SuccessStory(
            name = "Mehmet Karahan",
            age = 27,
            badge = "4 Ayda -11.5 kg",
            resultSummary = "94 kg → 82.5 kg • Yağ Oranı %26 → %16",
            quote = "Başta bana özel çıkan programı uyguladım, takvime sadık kaldım. FitChicken'dan her gün tavsiye alıp görevleri tamamlamak sporu bırakmamamı sağladı!"
        ),
        SuccessStory(
            name = "Emre Taşkın",
            age = 16,
            badge = "0-18 Büyüme Modu",
            resultSummary = "+4 cm Postür Açılması • +12 cm Sıçrama",
            quote = "Basketbol için büyüme çağı modundaki sıçrama ve postür esnemelerini yaptım. Hem kambur duruşum düzeldi hem maçlarda çok daha hızlıyım."
        ),
        SuccessStory(
            name = "Selin Yıldız",
            age = 24,
            badge = "Evde 90 Gün",
            resultSummary = "Sıfır Ekipman • Haftada 4 Gün",
            quote = "Salona gidecek vaktim yoktu. Evde antrenman kategorisindeki hareketlerin adımları ve animasyonları o kadar net ki sanki yanımda özel hoca varmış gibi!"
        ),
        SuccessStory(
            name = "Burak Demir",
            age = 31,
            badge = "Pro Hipertrofi",
            resultSummary = "+4.8 kg Temiz Kas Kütlesi",
            quote = "FitChicken Koçuna beslenme ve set aralarıyla ilgili aklıma takılan her şeyi sordum. Hem bilimsel hem de çok eğlenceli yanıtlarla antrenman kalitem ikiye katlandı."
        )
    )

    val SUCCESS_STORIES_EN: List<SuccessStory> = listOf(
        SuccessStory(
            name = "Mehmet Karahan",
            age = 27,
            badge = "-11.5 kg in 4 Mos",
            resultSummary = "94 kg → 82.5 kg • Body Fat 26% → 16%",
            quote = "I followed my custom plan and stayed loyal to the calendar. Tapping FitChicken every day kept my streak alive!"
        ),
        SuccessStory(
            name = "Emre Taşkın",
            age = 16,
            badge = "0-18 Growth Mode",
            resultSummary = "+4 cm Posture Lift • +12 cm Vertical Jump",
            quote = "Used the teen growth & jump mobility tracks for basketball. Fixed my posture and got noticeably faster on court."
        ),
        SuccessStory(
            name = "Selin Yıldız",
            age = 24,
            badge = "90 Days at Home",
            resultSummary = "Zero Equipment • 4 Days/Week",
            quote = "No time for a gym, so the home track and form animations felt like having a real personal trainer at home!"
        ),
        SuccessStory(
            name = "Burak Demir",
            age = 31,
            badge = "Pro Hypertrophy",
            resultSummary = "+4.8 kg Lean Muscle",
            quote = "Asked FitChicken Coach everything about nutrition and rest times. Clear science with great humor doubled my results."
        )
    )

    fun stories(lang: String): List<SuccessStory> = if (lang == "en") SUCCESS_STORIES_EN else SUCCESS_STORIES

    fun stories(context: Context): List<SuccessStory> = listOf(
        SuccessStory(
            name = "Mehmet Karahan",
            age = 27,
            badge = context.getString(R.string.story_1_badge),
            resultSummary = context.getString(R.string.story_1_result),
            quote = context.getString(R.string.story_1_quote)
        ),
        SuccessStory(
            name = "Emre Taşkın",
            age = 16,
            badge = context.getString(R.string.story_2_badge),
            resultSummary = context.getString(R.string.story_2_result),
            quote = context.getString(R.string.story_2_quote)
        ),
        SuccessStory(
            name = "Selin Yıldız",
            age = 24,
            badge = context.getString(R.string.story_3_badge),
            resultSummary = context.getString(R.string.story_3_result),
            quote = context.getString(R.string.story_3_quote)
        ),
        SuccessStory(
            name = "Burak Demir",
            age = 31,
            badge = context.getString(R.string.story_4_badge),
            resultSummary = context.getString(R.string.story_4_result),
            quote = context.getString(R.string.story_4_quote)
        )
    )
}
