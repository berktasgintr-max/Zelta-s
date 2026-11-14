package com.example.data.catalog

import com.example.data.model.ExerciseDemoType
import com.example.data.model.ExerciseItem
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutProgram

object WorkoutCatalogHomeAndYouth {

    private fun ex(
        id: String,
        name: String,
        muscle: String,
        sets: String,
        rest: Int,
        demo: ExerciseDemoType,
        steps: List<String>,
        cue: String,
        breath: String,
        mistake: String
    ) = ExerciseItem(id, name, muscle, sets, rest, demo, steps, cue, breath, mistake)

    // 16 HOME WORKOUTS (12 Free + 4 Pro Special)
    val homeWorkouts: List<WorkoutProgram> = listOf(
        WorkoutProgram(
            id = "home_1",
            title = "Evde Sıfır Ekipman Tüm Vücut",
            subtitle = "Sadece vücut ağırlığıyla salondaki kadar terleten temel program",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "Başlangıç",
            durationMinutes = 30,
            estimatedCalories = 240,
            exercises = listOf(
                ex("h1_1", "Bodyweight Squat", "Bacak & Kalça", "3 x 15", 45, ExerciseDemoType.SQUAT,
                    listOf("Ayakları omuz genişliğinde aç, kolları öne uzat.", "Görünmez bir sandalyeye oturur gibi kalçayı geriye indir.", "Topuklardan yere basarak doğrul."),
                    "Dizlerin ayak uçlarını aşırı geçmesin, göğsün dik kalsın.", "İnerken nefes al, kalkarken ver.", "Dizleri birbirine yaklaştırmak."),
                ex("h1_2", "Klasik Şınav (veya Diz Üstü)", "Göğüs & Kol", "3 x 10-12", 60, ExerciseDemoType.PUSHUP,
                    listOf("Elleri omuz genişliğinden biraz geniş yere koy.", "Vücudu tahta gibi düz tutarak göğsü yere yaklaştır.", "Avuç içleriyle yeri iterek yüksel."),
                    "Dirseklerin 45 derece geriye baksın, T şekli yapma.", "İnerken al, iterken ver.", "Belin aşağı sarkması."),
                ex("h1_3", "Glute Bridge", "Kalça & Bel", "3 x 15", 45, ExerciseDemoType.SQUAT,
                    listOf("Sırt üstü uzan, dizleri büküp ayakları kalça yakınına bas.", "Kalçayı sıkarak omuz-kalça-diz düz çizgisine kadar kaldır.", "Tepede 2 saniye sıkıp yavaşça in."),
                    "Beli değil kalça kaslarını sıkmaya odaklan.", "Yukarı kaldırırken nefes ver.", "Ayakları çok ileri koyup arka bacağı krampa sokmak."),
                ex("h1_4", "Mountain Climber", "Karın & Kardiyo", "3 x 30 sn", 45, ExerciseDemoType.CARDIO,
                    listOf("Yüksek şınav pozisyonu al.", "Dizlerini sırayla göğsüne doğru hızlıca çek."),
                    "Kalçanı zıplatmadan sabit tut.", "Ritmik nefes alıp ver.", "Kalçayı çok yukarı kaldırmak.")
            )
        ),
        WorkoutProgram(
            id = "home_2",
            title = "Evde Göğüs & Şınav Ustası",
            subtitle = "Farklı şınav açılarıyla üst, orta ve iç göğsü evde şekillendir",
            category = WorkoutCategory.HOME,
            focusArea = "upper",
            difficulty = "Orta",
            durationMinutes = 30,
            estimatedCalories = 230,
            exercises = listOf(
                ex("h2_1", "Decline Push-Up (Ayaklar Yüksekte)", "Üst Göğüs", "3 x 10", 60, ExerciseDemoType.PUSHUP,
                    listOf("Ayaklarını koltuk veya yatağın üstüne koy.", "Elleri yere yerleştir ve göğsü yere yaklaştırıp it."),
                    "Üst göğüs ve ön omuza ekstra yük bindirir.", "İterken nefes ver.", "Başını öne düşürmek."),
                ex("h2_2", "Diamond Push-Up", "İç Göğüs & Triceps", "3 x 8-10", 60, ExerciseDemoType.PUSHUP,
                    listOf("Ellerini göğsünün altında elmas/üçgen yapacak şekilde birleştir.", "Dirsekleri yanlara çok açmadan in ve it."),
                    "Triceps ve iç göğüste muazzam yanma sağlar.", "İterken nefes ver.", "Elleri yüzün önüne koymak."),
                ex("h2_3", "Tempo Negatif Şınav", "Göğüs Kontrolü", "3 x 8", 60, ExerciseDemoType.PUSHUP,
                    listOf("Normal şınav pozisyonundan 4 saniye sayarak yavaşça yere in.", "Göğüs yere değince hızlıca yukarı it."),
                    "Yavaş iniş kas liflerini daha güçlü uyarır.", "İnerken yavaşça nefes al.", "Son saniyede kendini bırakmak.")
            )
        ),
        WorkoutProgram(
            id = "home_3",
            title = "Evde Karın & Six-Pack Yakımı",
            subtitle = "Bel bölgesini sıkılaştıran 25 dakikalık yoğun core rutini",
            category = WorkoutCategory.HOME,
            focusArea = "core",
            difficulty = "Orta",
            durationMinutes = 25,
            estimatedCalories = 190,
            exercises = listOf(
                ex("h3_1", "Bicycle Crunch", "Üst & Yan Karın", "3 x 20", 40, ExerciseDemoType.PLANK,
                    listOf("Sırt üstü yat, eller başın yanında.", "Sağ dirseği sol dize yaklaştırırken sağ bacağı düz uzat, sonra değiştir."),
                    "Boynunu çekme, omuzunu çapraz dize döndür.", "Her dönüşte nefes ver.", "Bacakları çok hızlı ve yarım sallamak."),
                ex("h3_2", "Leg Raise", "Alt Karın", "3 x 12", 45, ExerciseDemoType.PLANK,
                    listOf("Ellerini kalçanın altına destek koy.", "Bacakları düz tutarak 90 dereceye kaldır ve yere değdirmeden indir."),
                    "Belin yerden kalkıyorsa bacakları daha az indir.", "İndirirken nefes al, kaldırırken ver.", "Beli boşlukta bırakmak."),
                ex("h3_3", "Side Plank", "Oblik & Bel", "3 x 30 sn/yan", 30, ExerciseDemoType.PLANK,
                    listOf("Yan dirseğin üzerinde gövdeni düz çizgi halinde kaldır.", "Kalçayı düşürmeden pozisyonu koru."),
                    "Bel yanlarını çelik gibi sıkılaştırır.", "Düzenli nefes al.", "Kalçayı öne veya arkaya döndürmek.")
            )
        ),
        WorkoutProgram(
            id = "home_4",
            title = "Evde Bacak & Kalça Ateşi",
            subtitle = "Ekipmansız güçlü bacaklar ve sıkı kalça için evde alt vücut",
            category = WorkoutCategory.HOME,
            focusArea = "lower",
            difficulty = "Orta",
            durationMinutes = 35,
            estimatedCalories = 290,
            exercises = listOf(
                ex("h4_1", "Sumo Squat", "İç Bacak & Kalça", "4 x 15", 45, ExerciseDemoType.SQUAT,
                    listOf("Ayakları omuzdan geniş aç, parmak uçları 45 derece dışa baksın.", "Dik gövdeyle aşağı çömel ve kalk."),
                    "İç bacak ve kalça kaslarını hedefler.", "Kalkarken nefes ver.", "Dizleri içe düşürmek."),
                ex("h4_2", "Reverse Lunge", "Ön Bacak & Kalça", "3 x 12/bacak", 60, ExerciseDemoType.LUNGE,
                    listOf("Dik duruştan bir ayağınla geriye adım at.", "Arka diz yere yaklaşsın, ön topukla kalk."),
                    "Öne lunge'a göre diz eklemi için daha konforludur.", "Kalkarken nefes ver.", "Öne aşırı eğilmek."),
                ex("h4_3", "Single-Leg Glute Bridge", "Tek Bacak Kalça", "3 x 12/bacak", 45, ExerciseDemoType.SQUAT,
                    listOf("Bir bacağı yukarı düz uzat.", "Diğer topukla kalçayı yukarı it ve 1 saniye sık."),
                    "Kalça gücünü ikiye katlar.", "İterken nefes ver.", "Kalçayı yana devirmek.")
            )
        ),
        WorkoutProgram(
            id = "home_5",
            title = "15 Dakika Yağ Yakım HIIT",
            subtitle = "Zamanı kısıtlı olanlar için evde yüksek nabızlı ter attırıcı",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "Zor",
            durationMinutes = 15,
            estimatedCalories = 210,
            exercises = listOf(
                ex("h5_1", "Jumping Jacks", "Isınma & Kardiyo", "4 x 40 sn", 20, ExerciseDemoType.JUMP,
                    listOf("Zıplayarak bacakları aç ve kolları baş üstünde birleştir.", "Tekrar zıplayarak başlangıca dön."),
                    "Ayak parmak uçlarında hafif ve sessiz in.", "Ritmik nefes al.", "Topuklara sert basmak."),
                ex("h5_2", "Burpee (Adımlı veya Sıçramalı)", "Tam Vücut Yakım", "4 x 30 sn", 30, ExerciseDemoType.JUMP,
                    listOf("Ellerini yere koy, ayakları geriye atıp şınav pozisyonu al.", "Ayakları öne çekip yukarı sıçra."),
                    "Yorulduğunda sıçramadan adımlı versiyona geç.", "Zıplarken nefes ver.", "Beli çukurlaştırmak."),
                ex("h5_3", "High Knees", "Kardiyo & Alt Karın", "4 x 30 sn", 30, ExerciseDemoType.CARDIO,
                    listOf("Olduğun yerde dizlerini bel hizasına kadar hızlıca çekerek koş."),
                    "Kollarını da koşu ritminde kullan.", "Düzenli nefes ver.", "Geriye yaslanmak.")
            )
        ),
        WorkoutProgram(
            id = "home_6",
            title = "Evde Dambıl ile Tam Güç",
            subtitle = "Evde sadece bir çift dambılı olanlar için komple fitness seansı",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "Orta",
            durationMinutes = 40,
            estimatedCalories = 310,
            exercises = listOf(
                ex("h6_1", "Dumbbell Thruster", "Bacak & Omuz", "4 x 12", 60, ExerciseDemoType.PRESS,
                    listOf("Dambılları omuzlarında tutarak squat yap.", "Kalkış ivmesiyle dambılları başının üstüne it."),
                    "Tek harekette tüm vücudu çalıştırır.", "Yukarı iterken nefes ver.", "Sadece kollarla itip bacak ivmesini kullanmamak."),
                ex("h6_2", "Dumbbell Bent-Over Row", "Sırt", "4 x 12", 60, ExerciseDemoType.PULL,
                    listOf("Kalçadan 45 derece eğil, sırt düz.", "Dambılları kalçana doğru çekip kürek kemiklerini sık."),
                    "Boynunu doğal omurga çizgisinde tut.", "Çekerken nefes ver.", "Beli kamburlaştırmak."),
                ex("h6_3", "Dumbbell Floor Press", "Göğüs & Triceps", "3 x 12", 60, ExerciseDemoType.PRESS,
                    listOf("Yere sırt üstü uzan, dizler bükülü.", "Dirsekler yere değince durakla ve yukarı it."),
                    "Omuz eklemini koruyan harika bir ev göğüs hareketidir.", "İterken nefes ver.", "Dirsekleri yere sert çarpmak.")
            )
        ),
        WorkoutProgram(
            id = "home_7",
            title = "Sabah Mobilite & Enerji Açılışı",
            subtitle = "Güne ağrısız, esnek ve zinde başlamak için 20 dakikalık akış",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "Başlangıç",
            durationMinutes = 20,
            estimatedCalories = 110,
            exercises = listOf(
                ex("h7_1", "Cat-Cow (Kedi-Deve)", "Omurga Esnekliği", "3 x 10", 20, ExerciseDemoType.STRETCH,
                    listOf("Dört ayak pozisyonunda eller omuz altında olsun.", "Nefes alırken göğsü açıp beli hafif çukurlaştır, nefes verirken sırtı tavana kabart."),
                    "Omurgayı tek tek hissederek hareket ettir.", "Hareketle nefesi eşitle.", "Çok hızlı yapmak."),
                ex("h7_2", "World's Greatest Stretch", "Kalça & Torasik", "3 x 6/yan", 30, ExerciseDemoType.STRETCH,
                    listOf("Derin lunge adımına geç, bir elini yere koy.", "Diğer kolunu tavana döndürerek göğsünü aç."),
                    "Masa başı sertliğini çözen en etkili harekettir.", "Tavana dönerken nefes ver.", "Arka dizi yere bırakmak."),
                ex("h7_3", "Deep Squat Hold", "Kalça & Bilek", "3 x 30 sn", 30, ExerciseDemoType.SQUAT,
                    listOf("Tam derin çömelme pozisyonuna in.", "Dirseklerinle dizleri hafifçe dışa it ve göğsü dik tut."),
                    "Topuklar yere tam basmalı.", "Derin diyafram nefesi al.", "Topukların üstünde parmak ucuna kalkmak.")
            )
        ),
        WorkoutProgram(
            id = "home_8",
            title = "Evde Kol, Omuz & Sırt Duruş",
            subtitle = "Sandalye ve şınav varyasyonlarıyla üst vücut sıkılaşma",
            category = WorkoutCategory.HOME,
            focusArea = "upper",
            difficulty = "Başlangıç",
            durationMinutes = 30,
            estimatedCalories = 210,
            exercises = listOf(
                ex("h8_1", "Pike Push-Up", "Omuz", "3 x 8-10", 60, ExerciseDemoType.PUSHUP,
                    listOf("Kalçanı tavana kaldırarak ters V şekli al.", "Başının tepesini ellerinin hafif önüne indirip it."),
                    "Evde omuz presi yerine geçen en güçlü harekettir.", "İterken nefes ver.", "Dirsekleri çok dışa açmak."),
                ex("h8_2", "Chair Triceps Dips", "Arka Kol", "3 x 12", 45, ExerciseDemoType.PRESS,
                    listOf("Ellerini sağlam bir sandalyenin kenarına koy.", "Sırtını sandalyeye yakın tutarak dirsekleri 90 derece bük ve it."),
                    "Omuzların kulaklara çıkmasın.", "Yukarı iterken nefes ver.", "Sandalyeden çok uzaklaşmak."),
                ex("h8_3", "Superman Hold", "Alt Sırt & Duruş", "3 x 12", 45, ExerciseDemoType.STRETCH,
                    listOf("Yüzüstü uzan, kolları ve bacakları öne/arkaya uzat.", "Göğsü ve bacakları yerden hafifçe kaldırıp 2 saniye tut."),
                    "Sırt ve bel kaslarını güçlendirir.", "Kaldırırken nefes ver.", "Boynu geriye aşırı kasmak.")
            )
        ),
        WorkoutProgram(
            id = "home_9",
            title = "Tabata Kalori Bombası",
            subtitle = "20 saniye maksimum efor, 10 saniye dinlenme formatında 24 dakika",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "Zor",
            durationMinutes = 24,
            estimatedCalories = 320,
            exercises = listOf(
                ex("h9_1", "Squat Jump", "Bacak & Kardiyo", "4 x 20 sn", 10, ExerciseDemoType.JUMP,
                    listOf("Squat pozisyonuna in ve yukarı patlayıcı sıçra.", "Yumuşakça doğrudan bir sonraki squata in."),
                    "Dizlerini kilitlemeden yumuşak iniş yap.", "Sıçrarken nefes ver.", "Sert topuk inişi yapmak."),
                ex("h9_2", "Speed Skaters", "Kalça & Çeviklik", "4 x 20 sn", 10, ExerciseDemoType.LUNGE,
                    listOf("Sağdan sola patenci gibi yan sıçrayışlar yap.", "Arka ayağı diğerinin gerisine çapraz dokundur."),
                    "Denge ve yan kalça kaslarını yakar.", "Ritmik nefes al.", "Dikleşip kalçayı kilitlemek."),
                ex("h9_3", "Plank Shoulder Taps", "Core & Omuz", "4 x 20 sn", 10, ExerciseDemoType.PLANK,
                    listOf("Yüksek plank pozisyonunda ayakları omuzdan geniş aç.", "Kalçayı sallamadan sırayla karşı omza dokun."),
                    "Gövden kaya gibi sabit kalmalı.", "Düzenli nefes ver.", "Kalçayı sağa sola devirmek.")
            )
        ),
        WorkoutProgram(
            id = "home_10",
            title = "Eklem Dostu Düşük Etkili Kardiyo",
            subtitle = "Zıplama olmadan evde nabzı yükselten sessiz apartman antrenmanı",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "Başlangıç",
            durationMinutes = 25,
            estimatedCalories = 190,
            exercises = listOf(
                ex("h10_1", "Step Squat to Reach", "Bacak & Omuz", "3 x 14", 40, ExerciseDemoType.SQUAT,
                    listOf("Bir adım yana açıp squat yap, kalkarken kolları tavana uzat.", "Diğer tarafa tekrarla."),
                    "Komşuları rahatsız etmeden yüksek kalori yakar.", "Uzanırken nefes ver.", "Dizleri içe bükmek."),
                ex("h10_2", "Inchworm Walkout", "Core & Esneklik", "3 x 8", 45, ExerciseDemoType.PLANK,
                    listOf("Ayakta eğilip ellerini yere koy, plank pozisyonuna kadar ellerinle yürü.", "Geri yürüyerek kalk."),
                    "Arka bacak ve omuzları aynı anda ısıtır.", "İleri yürürken nefes ver.", "Plankta beli düşürmek."),
                ex("h10_3", "Shadow Boxing", "Kol & Kardiyo", "3 x 45 sn", 30, ExerciseDemoType.CARDIO,
                    listOf("Boks gardı al, tempolu direkt ve kroşe yumruklar çıkar.", "Ayaklarda hafif yaylan."),
                    "Üst vücut dayanıklılığı için harikadır.", "Her yumrukta kısa nefes ver.", "Omuzları sıkmak.")
            )
        ),
        WorkoutProgram(
            id = "home_11",
            title = "Evde Sırt & Dik Duruş Terapisi",
            subtitle = "Kamburluğu azaltan, sırt ve arka omuzları uyandıran ev seansı",
            category = WorkoutCategory.HOME,
            focusArea = "upper",
            difficulty = "Başlangıç",
            durationMinutes = 25,
            estimatedCalories = 140,
            exercises = listOf(
                ex("h11_1", "Wall Angels", "Omuz & Duruş", "3 x 12", 30, ExerciseDemoType.STRETCH,
                    listOf("Sırtını, kalçanı ve başını duvara yasla.", "Kolları W şeklinde duvara temas ettirip yavaşça Y şekline yukarı kaydır."),
                    "Bel boşluğunu duvara yakın tut.", "Yukarı kaydırırken nefes ver.", "Kolların duvardan ayrılması."),
                ex("h11_2", "Prone Y-T-W Raises", "Arka Omuz & Sırt", "3 x 8/harf", 45, ExerciseDemoType.PULL,
                    listOf("Yüzüstü uzan, kolları sırasıyla Y, T ve W harfi şeklinde yerden kaldır."),
                    "Kürek kemiklerini sıkarak çalış.", "Kaldırırken nefes ver.", "Hızlı sallamak."),
                ex("h11_3", "Bird-Dog", "Bel & Core Dengesi", "3 x 10/yan", 30, ExerciseDemoType.PLANK,
                    listOf("Dört ayak pozisyonunda sağ kolu öne, sol bacağı geriye paralel uzat.", "2 saniye tutup değiştir."),
                    "Omurgayı stabilize eder.", "Uzatırken nefes ver.", "Bacağı çok yukarı kaldırıp beli bükmek.")
            )
        ),
        WorkoutProgram(
            id = "home_12",
            title = "Akşam Esneme & Toparlanma",
            subtitle = "Antrenman sonrası kas gerginliğini alan derin gevşeme rutini",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "Başlangıç",
            durationMinutes = 18,
            estimatedCalories = 70,
            exercises = listOf(
                ex("h12_1", "Child's Pose (Çocuk Pozu)", "Sırt & Omuz", "3 x 45 sn", 15, ExerciseDemoType.STRETCH,
                    listOf("Dizlerin üzerine otur, kolları öne uzatarak alnını mata koy.", "Her nefeste sırtının açıldığını hisset."),
                    "Derin ve yavaş nefeslerle sinir sistemini sakinleştir.", "4 sn al, 6 sn ver.", "Omuzları sıkmak."),
                ex("h12_2", "Pigeon Stretch", "Kalça & Piriformis", "2 x 45 sn/yan", 15, ExerciseDemoType.STRETCH,
                    listOf("Bir bacağı önde bükülü yerleştir, diğer bacağı geriye düz uzat.", "Gövdeni hafifçe öne bırak."),
                    "Kalça sıkışıklığını çözer.", "Derin nefesle gevşe.", "Dizde ağrı varsa büküyü azalt."),
                ex("h12_3", "Hamstring Doorway / Towel Stretch", "Arka Bacak", "2 x 45 sn/yan", 15, ExerciseDemoType.STRETCH,
                    listOf("Sırt üstü uzanıp bir bacağı yukarı uzat, havluyla kendine nazikçe çek."),
                    "Arka bacak esnekliği bel ağrısını azaltır.", "Nefes verirken esnet.", "Diz eklemini zorlamak.")
            )
        ),
        // 4 PRO-SPECIAL HOME WORKOUTS
        WorkoutProgram(
            id = "home_pro_1",
            title = "PRO: Evde Ekipmansız Kas İnşası",
            subtitle = "Yüksek zaman-altı-gerilim (TUT) ile evde gerçek hipertrofi",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "İleri",
            durationMinutes = 45,
            estimatedCalories = 350,
            isProSpecial = true,
            exercises = listOf(
                ex("hp1_1", "1.5 Tekrar Tempo Squat", "Bacak & Kalça", "4 x 12", 75, ExerciseDemoType.SQUAT,
                    listOf("Tam aşağı in, yarıya kadar kalk, tekrar dibe in ve tam kalk."),
                    "Ekipmansız bacak gelişiminin sırrıdır.", "Tam kalkarken nefes ver.", "Hızlı zıplamak."),
                ex("hp1_2", "Archer Push-Up", "Göğüs & Tek Kol Gücü", "4 x 8/yan", 75, ExerciseDemoType.PUSHUP,
                    listOf("Elleri geniş koy, bir kolu düz uzatırken diğer kola doğru şınav in."),
                    "Tek kol şınava giden en güçlü basamaktır.", "İterken nefes ver.", "Kalçayı döndürmek."),
                ex("hp1_3", "Towel Doorframe / Table Row", "Sırt", "4 x 12", 60, ExerciseDemoType.PULL,
                    listOf("Sağlam masa altına uzanıp kenarından kavrayarak göğsünü masaya çek (Inverted Row)."),
                    "Evde sırt kalınlığı için en iyi vücut ağırlığı çekişi!", "Çekerken nefes ver.", "Kalçayı sarkıtmak.")
            )
        ),
        WorkoutProgram(
            id = "home_pro_2",
            title = "PRO: 30 Dk Ekstrem Yağ Yakımı",
            subtitle = "Afterburn (EPOC) etkisiyle 24 saat kalori yaktıran pro protokol",
            category = WorkoutCategory.HOME,
            focusArea = "full_body",
            difficulty = "İleri",
            durationMinutes = 30,
            estimatedCalories = 390,
            isProSpecial = true,
            exercises = listOf(
                ex("hp2_1", "Burpee Broad Jump", "Tam Vücut", "4 x 10", 60, ExerciseDemoType.JUMP,
                    listOf("Burpee yapıp yukarı yerine ileriye doğru sıçra."),
                    "Patlayıcı güç ve nabız zirvesi.", "Sıçrarken nefes ver.", "Sert iniş yapmak."),
                ex("hp2_2", "Plyo Lunge Switch", "Bacak & Kondisyon", "4 x 16", 45, ExerciseDemoType.JUMP,
                    listOf("Lunge pozisyonundan sıçrayarak havada bacak değiştir."),
                    "Gövde dik kalsın.", "Değişirken nefes ver.", "Dizleri yere çarpmak."),
                ex("hp2_3", "Plank Up-Downs", "Core & Omuz", "4 x 12", 45, ExerciseDemoType.PLANK,
                    listOf("Dirsek planktan sırayla avuç içi planka çık ve geri in."),
                    "Her sette başlangıç kolunu değiştir.", "Yükselirken nefes ver.", "Kalçayı sallamak.")
            )
        ),
        WorkoutProgram(
            id = "home_pro_3",
            title = "PRO: Kalistenik Temelleri",
            subtitle = "Vücut kontrolü, L-sit hazırlığı ve patlayıcı şınav serisi",
            category = WorkoutCategory.HOME,
            focusArea = "upper",
            difficulty = "İleri",
            durationMinutes = 40,
            estimatedCalories = 300,
            isProSpecial = true,
            exercises = listOf(
                ex("hp3_1", "Pseudo Planche Push-Up", "Omuz & Göğüs", "4 x 8", 75, ExerciseDemoType.PUSHUP,
                    listOf("Parmaklar yanlara/geriye dönük, omuzları ellerin önüne doğru eğip şınav çek."),
                    "İleri seviye omuz gücü inşa eder.", "İterken nefes ver.", "Beli düşürmek."),
                ex("hp3_2", "Tuck L-Sit Hold", "Core & Triceps", "4 x 20 sn", 60, ExerciseDemoType.PLANK,
                    listOf("Yere oturup elleri kalça yanına bas, kendini it ve dizleri göğse çekerek havada tut."),
                    "Omuzları kulaklardan uzaklaştır.", "Kısa kesik nefesler yerine düzenli nefes al.", "Dirsekleri bükmek."),
                ex("hp3_3", "Explosive Clapping Push-Up", "Patlayıcı Güç", "3 x 6", 90, ExerciseDemoType.PUSHUP,
                    listOf("Şınav dibinden eller yerden kesilecek kadar hızlı it (istersen el çırp)."),
                    "Hızlı kasılan kas liflerini ateşler.", "İterken nefes ver.", "Yorgunken yapmaya devam etmek.")
            )
        ),
        WorkoutProgram(
            id = "home_pro_4",
            title = "PRO: Core & Denge Zirvesi",
            subtitle = "Atletik merkez bölgesi ve derin karın kasları için pro rutin",
            category = WorkoutCategory.HOME,
            focusArea = "core",
            difficulty = "İleri",
            durationMinutes = 30,
            estimatedCalories = 250,
            isProSpecial = true,
            exercises = listOf(
                ex("hp4_1", "Hollow Body Hold", "Tüm Core", "4 x 30 sn", 45, ExerciseDemoType.PLANK,
                    listOf("Sırt üstü uzan, belini yere tamamen yapıştır.", "Omuzları ve düz bacakları yerden kaldırıp muz şekli al."),
                    "Bel yerden kalkarsa dizleri hafifçe bük.", "Ritmik nefes al.", "Bel boşluğu bırakmak."),
                ex("hp4_2", "V-Up", "Üst & Alt Karın", "3 x 12", 45, ExerciseDemoType.PLANK,
                    listOf("Düz yatıştan eller ve ayakları aynı anda yukarıda buluştur."),
                    "Tam bir V harfi oluştur.", "Kapanırken nefes ver.", "Yere sert düşmek."),
                ex("hp4_3", "Dragon Flag Negatives", "İleri Karın", "3 x 5", 60, ExerciseDemoType.PLANK,
                    listOf("Ağır bir koltuk/direk tut, vücudu omuzlara dik kaldır ve dümdüz yavaşça indir."),
                    "En üst düzey karın egzersizlerinden biridir.", "İnerken yavaşça nefes al.", "Belden kırılmak.")
            )
        )
    )

    // 8 YOUTH / 0-18 GROWTH & ADOLESCENCE WORKOUTS ("0-18 Yaş İçin Tavsiye Edilir", open to adults too!)
    val youthWorkouts: List<WorkoutProgram> = listOf(
        WorkoutProgram(
            id = "youth_1",
            title = "Boy & Omurga Uzatma Mobilitesi",
            subtitle = "Büyüme plaklarını koruyarak omurgayı açan ve postürü uzatan güvenli akış",
            category = WorkoutCategory.YOUTH,
            focusArea = "full_body",
            difficulty = "Başlangıç",
            durationMinutes = 25,
            estimatedCalories = 130,
            isRecommendedForYouth = true,
            exercises = listOf(
                ex("y1_1", "Dead Hang (Barfikste Asılma)", "Omurga Dekompresyonu", "3 x 30 sn", 45, ExerciseDemoType.PULL,
                    listOf("Barfiks barına ellerle asıl, ayakların yerden kesilsin.", "Omuzları ve belini tamamen gevşeterek yerçekimiyle omurganın uzamasına izin ver."),
                    "Ergenlik çağında omurga disklerini rahatlatmanın en doğal yoludur.", "Derin burun nefesi al.", "Nefes tutup omuzları sıkmak."),
                ex("y1_2", "Cobra Stretch (Kobra Esnemesi)", "Omurga & Göğüs", "3 x 30 sn", 30, ExerciseDemoType.STRETCH,
                    listOf("Yüzüstü uzan, elleri omuz altına koy.", "Kalça yerde kalacak şekilde göğsü yukarı kaldırıp karşıya bak."),
                    "Kambur duruşu tersine çevirir.", "Yukarıda derin nefes al.", "Beli acıtacak kadar zorlamak."),
                ex("y1_3", "Standing Overhead Reach & Side Bend", "Yan Zincir & Boy", "3 x 10/yan", 30, ExerciseDemoType.STRETCH,
                    listOf("Parmakları kenetleyip tavana doğru maksimum uzan.", "Parmak ucuna yükselip 5 saniye tut ve yanlara nazikçe esne."),
                    "Dik duruş alışkanlığı kazandırır.", "Uzanırken nefes al.", "Beli geriye bükmek.")
            )
        ),
        WorkoutProgram(
            id = "youth_2",
            title = "Basketbol & Voleybol Dikey Sıçrama",
            subtitle = "Ergenlik döneminde sıçrama yüksekliğini ve patlayıcı gücü artıran program",
            category = WorkoutCategory.YOUTH,
            focusArea = "lower",
            difficulty = "Orta",
            durationMinutes = 35,
            estimatedCalories = 280,
            isRecommendedForYouth = true,
            exercises = listOf(
                ex("y2_1", "Pogo Jumps (Ayak Bileği Yaylanması)", "Tendon & Sıçrama", "4 x 20", 45, ExerciseDemoType.JUMP,
                    listOf("Dizleri çok az büküp sadece ayak bilekleriyle seri ve yüksek sıçra.", "Yere değdiğin an yay gibi tekrar zıpla."),
                    "Aşil tendonunu yay gibi güçlendirir.", "Ritmik nefes al.", "Topukları yere vurmak."),
                ex("y2_2", "Tuck Jump (Dizi Çekerek Sıçrama)", "Patlayıcı Güç", "3 x 8", 60, ExerciseDemoType.JUMP,
                    listOf("Yukarı maksimum sıçrarken dizlerini göğsüne doğru çek.", "Yumuşakça bacakları açıp dizleri bükerek in."),
                    "Sıçrama yüksekliğini doğrudan artırır.", "Sıçrarken nefes ver.", "Sert dizlerle yere inmek."),
                ex("y2_3", "Single-Leg Calf Raise", "Baldır Gücü", "3 x 15/bacak", 45, ExerciseDemoType.SQUAT,
                    listOf("Bir basamak kenarında tek ayakla topuğu aşağı indirip en tepeye çık."),
                    "Her sıçramanın son itişini güçlendirir.", "Yükselirken nefes ver.", "Hızlı sallanmak.")
            )
        ),
        WorkoutProgram(
            id = "youth_3",
            title = "Güvenli Vücut Ağırlığı Temel Güç",
            subtitle = "Büyüme çağında eklemleri yormadan atletik temel ve kas koordinasyonu kurar",
            category = WorkoutCategory.YOUTH,
            focusArea = "full_body",
            difficulty = "Başlangıç",
            durationMinutes = 30,
            estimatedCalories = 220,
            isRecommendedForYouth = true,
            exercises = listOf(
                ex("y3_1", "Tempo Bodyweight Squat", "Bacak & Teknik", "3 x 15", 45, ExerciseDemoType.SQUAT,
                    listOf("3 saniyede kontrollü aşağı in.", "Alt noktada 1 saniye dur ve kalk."),
                    "Ağır bar altına girmeden mükemmel squat tekniği öğretir.", "Kalkarken nefes ver.", "Dizleri içe düşürmek."),
                ex("y3_2", "Incline Push-Up", "Göğüs & Omuz", "3 x 12", 45, ExerciseDemoType.PUSHUP,
                    listOf("Elleri sağlam bir masa veya bank üzerine koy.", "Gövde düz iken göğsü kenara yaklaştırıp it."),
                    "Temiz şınav formunu güvenle oturtur.", "İterken nefes ver.", "Beli aşağı bırakmak."),
                ex("y3_3", "Australian Pull-Up", "Sırt & Kol", "3 x 10", 60, ExerciseDemoType.PULL,
                    listOf("Alçak bir barın altında gövdeyi düz tutup göğsü bara çek."),
                    "Büyüme çağında barfikse en güvenli hazırlıktır.", "Çekerken nefes ver.", "Boynu öne uzatmak.")
            )
        ),
        WorkoutProgram(
            id = "youth_4",
            title = "Okul & Masa Başı Duruş Düzeltme",
            subtitle = "Ders çalışmaktan öne düşen omuz ve boyun duruşunu düzelten özel seri",
            category = WorkoutCategory.YOUTH,
            focusArea = "upper",
            difficulty = "Başlangıç",
            durationMinutes = 20,
            estimatedCalories = 110,
            isRecommendedForYouth = true,
            exercises = listOf(
                ex("y4_1", "Chin Tuck (Boyun Hizalama)", "Boyun & Postür", "3 x 12", 20, ExerciseDemoType.STRETCH,
                    listOf("Dik otur veya duvara yaslan.", "Başını eğmeden çeneni geriye doğru (gıdı yapar gibi) çek ve 3 sn tut."),
                    "Teknoloji boynu (text neck) problemini düzeltir.", "Normal nefes al.", "Çeneyi aşağı eğmek."),
                ex("y4_2", "Doorway Chest Stretch", "Göğüs Açma", "3 x 30 sn", 20, ExerciseDemoType.STRETCH,
                    listOf("Ön kollarını kapı çerçevesine 90 derece koy.", "Bir adım öne çıkarak göğsünün önünü aç."),
                    "Öne kapanan omuzları geriye açar.", "Derin nefes al.", "Beli aşırı büküp öne zorlamak."),
                ex("y4_3", "Scapular Push-Up", "Kürek Kemiği Kontrolü", "3 x 12", 30, ExerciseDemoType.PUSHUP,
                    listOf("Plank pozisyonunda kolları bükmeden sadece kürek kemiklerini birbirine yaklaştırıp uzaklaştır."),
                    "Omuz sağlığını ve dik duruşu destekler.", "İterken nefes ver.", "Dirsekleri bükmek.")
            )
        ),
        WorkoutProgram(
            id = "youth_5",
            title = "Ergenlik Çeviklik & Koordinasyon",
            subtitle = "Sinir-kas iletişimini hızlandıran futbol, basketbol ve atletizm dostu çalışma",
            category = WorkoutCategory.YOUTH,
            focusArea = "full_body",
            difficulty = "Orta",
            durationMinutes = 30,
            estimatedCalories = 270,
            isRecommendedForYouth = true,
            exercises = listOf(
                ex("y5_1", "Lateral Quick Shuffle", "Yan Çeviklik", "4 x 20 sn", 30, ExerciseDemoType.CARDIO,
                    listOf("Yarım squat pozisyonunda sağa 3 adım, sola 3 adım hızlıca kay."),
                    "Ağırlık merkezini alçak tut.", "Ritmik nefes al.", "Ayakları birbirine çaprazlamak."),
                ex("y5_2", "Single-Leg Balance & Reach", "Ayak Bileği & Denge", "3 x 8/bacak", 30, ExerciseDemoType.LUNGE,
                    listOf("Tek ayak üzerinde dur ve diğer ayağını dokunmadan öne, yana ve arkaya uzat."),
                    "Bilek burkulmalarını önler.", "Odaklanıp yavaş nefes al.", "Dizini içe düşürmek."),
                ex("y5_3", "Bear Crawl", "Tüm Vücut Koordinasyon", "3 x 20 sn", 45, ExerciseDemoType.PLANK,
                    listOf("Dizler yerden 3 cm yukarıda dört ayak pozisyonunda karşı el-karşı ayak ileri yürü."),
                    "Sırtını düz bir masa gibi tut.", "Düzenli nefes ver.", "Kalçayı tavana dikmek.")
            )
        ),
        WorkoutProgram(
            id = "youth_6",
            title = "Genç Sporcu Core & Denge",
            subtitle = "Bel sağlığını koruyan ve her spor dalında performansı artıran merkez gücü",
            category = WorkoutCategory.YOUTH,
            focusArea = "core",
            difficulty = "Başlangıç",
            durationMinutes = 25,
            estimatedCalories = 160,
            isRecommendedForYouth = true,
            exercises = listOf(
                ex("y6_1", "Dead Bug", "Derin Core", "3 x 12", 30, ExerciseDemoType.PLANK,
                    listOf("Sırt üstü uzan, kollar tavana, dizler 90 derece havada.", "Belini yere yapıştırıp karşı kol ve bacağı yere yaklaştır."),
                    "Bel ağrısı olmadan merkez bölgeyi güçlendirir.", "Uzatırken nefes ver.", "Belin yerden kalkması."),
                ex("y6_2", "Forearm Plank", "Core Dayanıklılığı", "3 x 35 sn", 30, ExerciseDemoType.PLANK,
                    listOf("Dirsekler omuz altında, gövde düz çizgi halinde sabit tut."),
                    "Süreden çok mükemmel form önemlidir.", "Düzenli nefes al.", "Kalçayı düşürmek."),
                ex("y6_3", "Glute Bridge March", "Kalça & Stabilite", "3 x 16", 40, ExerciseDemoType.SQUAT,
                    listOf("Köprü pozisyonundayken sırayla dizleri yukarı kaldır."),
                    "Kalça yüksekliğini sabit tutmaya çalış.", "Kaldırırken nefes ver.", "Kalçanın yana düşmesi.")
            )
        ),
        WorkoutProgram(
            id = "youth_7",
            title = "Eklem Dostu Kondisyon & Nefes",
            subtitle = "Akciğer kapasitesini ve dayanıklılığı büyüme çağında zirveye taşıyan akış",
            category = WorkoutCategory.YOUTH,
            focusArea = "full_body",
            difficulty = "Orta",
            durationMinutes = 28,
            estimatedCalories = 250,
            isRecommendedForYouth = true,
            exercises = listOf(
                ex("y7_1", "Jump Rope (İp Atlama veya İpsiz)", "Kondisyon & Ritim", "4 x 45 sn", 30, ExerciseDemoType.JUMP,
                    listOf("Ayak uçlarında ritmik ve hafif sıçramalar yap."),
                    "Koordinasyon ve kalp sağlığı için eşsizdir.", "Burundan nefes alıp ver.", "Çok yüksek zıplamak."),
                ex("y7_2", "Alternating Reverse Lunge", "Bacak Dayanıklılığı", "3 x 16", 45, ExerciseDemoType.LUNGE,
                    listOf("Geriye adımlayıp dik duruşla kalk."),
                    "Diz sağlığına en dost bacak egzersizidir.", "Kalkarken nefes ver.", "Ön dizin içe kayması."),
                ex("y7_3", "Box Breathing (Kutu Nefesi)", "Toparlanma", "3 tur", 15, ExerciseDemoType.STRETCH,
                    listOf("4 sn nefes al, 4 sn tut, 4 sn ver, 4 sn bekle."),
                    "Nabzı hızlıca düşürür ve odaklanmayı artırır.", "Diyaframdan nefes al.", "Omuzları yukarı çekmek.")
            )
        ),
        WorkoutProgram(
            id = "youth_pro_1",
            title = "PRO: Genç Atlet Tam Gelişim",
            subtitle = "0-18 yaş için boy postürü, sıçrama, çeviklik ve atletik güç paketi",
            category = WorkoutCategory.YOUTH,
            focusArea = "full_body",
            difficulty = "Orta",
            durationMinutes = 40,
            estimatedCalories = 320,
            isProSpecial = true,
            isRecommendedForYouth = true,
            exercises = listOf(
                ex("yp1_1", "Dead Hang + Scapular Pull", "Omurga & Sırt", "3 x 8 + 20 sn", 60, ExerciseDemoType.PULL,
                    listOf("Asılı pozisyonda kolları bükmeden kürek kemiklerini aşağı çek, sonra serbest asıl."),
                    "Hem postür hem barfiks gücü kazandırır.", "Çekerken nefes ver.", "Sallanmak."),
                ex("yp1_2", "Depth Drop to Squat Jump", "Elit Sıçrama", "3 x 6", 75, ExerciseDemoType.JUMP,
                    listOf("Alçak basamaktan yumuşak inip anında yukarı patlayıcı sıçra."),
                    "Reaktif güç geliştirir.", "Sıçrarken nefes ver.", "Dizleri kilitleyip sert inmek."),
                ex("yp1_3", "Tempo Push-Up + Plank", "Üst Vücut & Core", "3 x 10 + 30 sn", 60, ExerciseDemoType.PUSHUP,
                    listOf("10 kontrollü şınavın hemen ardından 30 sn plank tut."),
                    "Büyüme çağında güvenli üst vücut kası inşa eder.", "Düzenli nefes al.", "Formu bozmak.")
            )
        )
    )

    val ALL_WORKOUTS: List<WorkoutProgram> =
        WorkoutCatalogGym.gymWorkouts + homeWorkouts + youthWorkouts
}
