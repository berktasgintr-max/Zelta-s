package com.example.data.catalog

import androidx.annotation.StringRes
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.components.CoachAnimationMood
import kotlin.math.roundToInt

enum class FoodCategory(@StringRes val labelRes: Int) {
    POULTRY_MEAT(R.string.food_cat_meat),
    FISH_SEAFOOD(R.string.food_cat_fish),
    EGGS_DAIRY(R.string.food_cat_dairy),
    GRAINS_CARBS(R.string.food_cat_grains),
    LEGUMES_NUTS(R.string.food_cat_legumes),
    FRUITS(R.string.food_cat_fruits),
    VEGETABLES(R.string.food_cat_veggies),
    MEALS_BOWLS(R.string.food_cat_meals),
    SHAKES_SNACKS(R.string.food_cat_snacks),
    FATS_OILS(R.string.food_cat_fats)
}

data class ScaledFoodMacros(
    val grams: Int,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int
)

data class FitnessFoodItem(
    val id: String,
    val nameEn: String,
    val nameTr: String,
    val category: FoodCategory,
    val caloriesPer100g: Int,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val defaultPortionGrams: Int = 100,
    val portionHintEn: String = "100g serving",
    val portionHintTr: String = "100g porsiyon"
) {
    fun localizedName(langCode: String): String =
        if (langCode.equals("tr", ignoreCase = true)) nameTr else nameEn

    fun localizedPortionHint(langCode: String): String =
        if (langCode.equals("tr", ignoreCase = true)) portionHintTr else portionHintEn

    fun scaleForGrams(grams: Int): ScaledFoodMacros {
        val factor = grams.coerceAtLeast(1) / 100.0
        return ScaledFoodMacros(
            grams = grams.coerceAtLeast(1),
            calories = (caloriesPer100g * factor).roundToInt(),
            proteinGrams = (proteinPer100g * factor).roundToInt(),
            carbsGrams = (carbsPer100g * factor).roundToInt(),
            fatGrams = (fatPer100g * factor).roundToInt()
        )
    }
}

data class MascotNutritionFeedback(
    val title: String,
    val message: String,
    val mood: CoachAnimationMood,
    val proteinTargetGrams: Int
)

object FitnessFoodDatabase {

    val PART_1_ITEMS: List<FitnessFoodItem> = listOf(
        // ==================== POULTRY & MEAT (30) ====================
        FitnessFoodItem("food_chicken_breast_grilled", "Chicken Breast (Grilled)", "Tavuk Göğsü (Izgara)", FoodCategory.POULTRY_MEAT, 165, 31.0, 0.0, 3.6, 150, "1 medium fillet (150g)", "1 orta fileto (150g)"),
        FitnessFoodItem("food_chicken_breast_boiled", "Chicken Breast (Boiled)", "Tavuk Göğsü (Haşlama)", FoodCategory.POULTRY_MEAT, 151, 29.5, 0.0, 3.2, 150, "1 poached breast (150g)", "1 haşlanmış göğüs (150g)"),
        FitnessFoodItem("food_chicken_thigh_skinless", "Chicken Thigh (Skinless)", "Tavuk But (Derisiz)", FoodCategory.POULTRY_MEAT, 179, 24.8, 0.0, 8.2, 140, "1 boneless thigh (140g)", "1 kemiksiz but (140g)"),
        FitnessFoodItem("food_chicken_drumstick", "Chicken Drumstick (Roasted)", "Tavuk Baget (Fırın)", FoodCategory.POULTRY_MEAT, 195, 25.2, 0.0, 9.8, 120, "1 large drumstick (120g)", "1 büyük baget (120g)"),
        FitnessFoodItem("food_chicken_wings", "Chicken Wings (Baked)", "Tavuk Kanat (Fırın)", FoodCategory.POULTRY_MEAT, 254, 23.0, 0.0, 17.5, 150, "4 wings (150g)", "4 adet kanat (150g)"),
        FitnessFoodItem("food_chicken_mince_lean", "Lean Ground Chicken", "Tavuk Kıyması (Az Yağlı)", FoodCategory.POULTRY_MEAT, 143, 21.0, 0.0, 6.2, 150, "150g cooked portion", "150g pişmiş porsiyon"),
        FitnessFoodItem("food_turkey_breast", "Turkey Breast (Roasted)", "Hindi Göğüs (Fırın)", FoodCategory.POULTRY_MEAT, 135, 29.0, 0.2, 1.5, 150, "150g sliced breast", "150g dilim göğüs"),
        FitnessFoodItem("food_turkey_mince_lean", "Lean Ground Turkey (93/7)", "Hindi Kıyması (%93 Yağsız)", FoodCategory.POULTRY_MEAT, 150, 22.0, 0.0, 6.8, 150, "150g portion", "150g porsiyon"),
        FitnessFoodItem("food_turkey_ham", "Smoked Turkey Breast Slices", "Hindi Füme Dilim", FoodCategory.POULTRY_MEAT, 104, 21.5, 1.8, 1.2, 60, "4 slices (60g)", "4 dilim (60g)"),
        FitnessFoodItem("food_beef_sirloin", "Beef Sirloin Steak (Lean)", "Dana Kontrfile (Yağsız)", FoodCategory.POULTRY_MEAT, 206, 28.0, 0.0, 9.8, 180, "1 steak (180g)", "1 porsiyon biftek (180g)"),
        FitnessFoodItem("food_beef_tenderloin", "Beef Tenderloin (Bonfile)", "Dana Bonfile (Izgara)", FoodCategory.POULTRY_MEAT, 198, 28.5, 0.0, 8.6, 170, "1 medallion (170g)", "1 madalyon (170g)"),
        FitnessFoodItem("food_beef_mince_5pct", "Extra Lean Ground Beef (5%)", "Az Yağlı Dana Kıyma (%5)", FoodCategory.POULTRY_MEAT, 137, 21.4, 0.0, 5.0, 150, "150g portion", "150g porsiyon"),
        FitnessFoodItem("food_beef_mince_15pct", "Ground Beef (15% Fat)", "Orta Yağlı Dana Kıyma (%15)", FoodCategory.POULTRY_MEAT, 215, 19.5, 0.0, 15.0, 150, "150g portion", "150g porsiyon"),
        FitnessFoodItem("food_beef_ribeye", "Beef Ribeye Steak", "Dana Antrikot (Izgara)", FoodCategory.POULTRY_MEAT, 271, 24.5, 0.0, 18.8, 200, "1 steak (200g)", "1 porsiyon antrikot (200g)"),
        FitnessFoodItem("food_beef_meatballs", "Grilled Lean Beef Meatballs", "Izgara Dana Köfte", FoodCategory.POULTRY_MEAT, 212, 20.5, 5.2, 12.0, 150, "5 meatballs (150g)", "5 adet köfte (150g)"),
        FitnessFoodItem("food_beef_liver", "Beef Liver (Pan-Seared)", "Dana Ciğeri (Tava)", FoodCategory.POULTRY_MEAT, 175, 26.5, 5.1, 4.7, 120, "120g serving", "120g porsiyon"),
        FitnessFoodItem("food_beef_jerky", "Beef Jerky (Lean)", "Kurutulmuş Dana Eti", FoodCategory.POULTRY_MEAT, 285, 42.0, 6.5, 9.0, 40, "1 snack pack (40g)", "1 paket (40g)"),
        FitnessFoodItem("food_bresaola", "Bresaola (Cured Beef)", "Bresaola / Yağsız Pastırma", FoodCategory.POULTRY_MEAT, 151, 32.0, 0.5, 2.0, 50, "6 thin slices (50g)", "6 ince dilim (50g)"),
        FitnessFoodItem("food_pastirma_lean", "Turkish Pastirma (Fenugreek)", "Çemensiz Dana Pastırma", FoodCategory.POULTRY_MEAT, 215, 29.0, 1.5, 10.2, 40, "4 slices (40g)", "4 dilim (40g)"),
        FitnessFoodItem("food_lamb_chop", "Lamb Chop (Trimmed)", "Kuzu Pirzola (Yağı Alınmış)", FoodCategory.POULTRY_MEAT, 232, 25.0, 0.0, 14.0, 140, "2 chops (140g)", "2 adet pirzola (140g)"),
        FitnessFoodItem("food_lamb_leg", "Roasted Lamb Leg (Lean)", "Fırın Kuzu But (Yağsız)", FoodCategory.POULTRY_MEAT, 191, 27.5, 0.0, 8.2, 150, "150g sliced", "150g dilim"),
        FitnessFoodItem("food_veal_cutlet", "Veal Cutlet (Grilled)", "Süt Dana Külbastı", FoodCategory.POULTRY_MEAT, 158, 27.0, 0.0, 5.1, 150, "1 cutlet (150g)", "1 porsiyon (150g)"),
        FitnessFoodItem("food_duck_breast_skinless", "Duck Breast (Skinless)", "Ördek Göğsü (Derisiz)", FoodCategory.POULTRY_MEAT, 140, 23.5, 0.0, 4.3, 150, "150g breast", "150g göğüs"),
        FitnessFoodItem("food_chicken_sausage_lean", "Lean Chicken Sausage", "Az Yağlı Tavuk Sosis", FoodCategory.POULTRY_MEAT, 148, 17.5, 2.5, 7.2, 100, "2 links (100g)", "2 adet (100g)"),
        FitnessFoodItem("food_sucuk_beef", "Turkish Beef Sucuk", "Dana Sucuk (Izgara)", FoodCategory.POULTRY_MEAT, 332, 19.5, 2.0, 27.5, 50, "50g slices", "50g dilim"),
        FitnessFoodItem("food_doner_chicken", "Chicken Doner Meat (Plain)", "Tavuk Döner Eti (Sade)", FoodCategory.POULTRY_MEAT, 185, 25.0, 2.5, 8.2, 150, "150g portion", "150g porsiyon"),
        FitnessFoodItem("food_doner_beef", "Beef Doner Meat (Plain)", "Et Döner (Sade)", FoodCategory.POULTRY_MEAT, 245, 23.5, 2.0, 16.0, 150, "150g portion", "150g porsiyon"),
        FitnessFoodItem("food_roast_beef_deli", "Roast Beef Slices", "Rozbif Dilim", FoodCategory.POULTRY_MEAT, 142, 24.5, 1.0, 4.2, 80, "5 slices (80g)", "5 dilim (80g)"),
        FitnessFoodItem("food_chicken_liver", "Chicken Liver (Cooked)", "Tavuk Ciğeri (Pişmiş)", FoodCategory.POULTRY_MEAT, 167, 24.5, 0.9, 6.5, 120, "120g portion", "120g porsiyon"),
        FitnessFoodItem("food_bison_steak", "Lean Bison / Buffalo Steak", "Yağsız Bizon / Manda Eti", FoodCategory.POULTRY_MEAT, 146, 28.4, 0.0, 2.4, 170, "1 steak (170g)", "1 biftek (170g)"),

        // ==================== FISH & SEAFOOD (22) ====================
        FitnessFoodItem("food_salmon_grilled", "Atlantic Salmon (Grilled)", "Somon Balığı (Izgara)", FoodCategory.FISH_SEAFOOD, 208, 20.4, 0.0, 13.4, 150, "1 fillet (150g)", "1 fileto (150g)"),
        FitnessFoodItem("food_salmon_smoked", "Smoked Salmon", "Füme Somon", FoodCategory.FISH_SEAFOOD, 117, 18.3, 0.0, 4.3, 80, "80g slices", "80g dilim"),
        FitnessFoodItem("food_tuna_water", "Canned Tuna in Water (Drained)", "Ton Balığı (Suda/Süzülmüş)", FoodCategory.FISH_SEAFOOD, 116, 25.5, 0.0, 1.0, 120, "1 drained can (120g)", "1 süzülmüş kutu (120g)"),
        FitnessFoodItem("food_tuna_oil", "Canned Tuna in Olive Oil", "Ton Balığı (Zeytinyağlı)", FoodCategory.FISH_SEAFOOD, 198, 25.0, 0.0, 10.5, 120, "1 can (120g)", "1 kutu (120g)"),
        FitnessFoodItem("food_sea_bass", "Sea Bass / Levrek (Baked)", "Levrek (Fırın / Izgara)", FoodCategory.FISH_SEAFOOD, 124, 23.6, 0.0, 2.6, 180, "1 fillet (180g)", "1 fileto (180g)"),
        FitnessFoodItem("food_sea_bream", "Sea Bream / Çipura (Grilled)", "Çipura (Izgara)", FoodCategory.FISH_SEAFOOD, 135, 22.8, 0.0, 4.2, 180, "1 fillet (180g)", "1 fileto (180g)"),
        FitnessFoodItem("food_trout_rainbow", "Rainbow Trout (Baked)", "Alabalık (Fırın)", FoodCategory.FISH_SEAFOOD, 148, 22.9, 0.0, 5.8, 170, "1 trout fillet (170g)", "1 fileto (170g)"),
        FitnessFoodItem("food_mackerel", "Atlantic Mackerel (Grilled)", "Uskumru (Izgara)", FoodCategory.FISH_SEAFOOD, 235, 21.8, 0.0, 16.0, 150, "1 fillet (150g)", "1 fileto (150g)"),
        FitnessFoodItem("food_sardines", "Sardines (Grilled / Canned)", "Sardalya (Izgara)", FoodCategory.FISH_SEAFOOD, 185, 24.6, 0.0, 9.5, 120, "120g serving", "120g porsiyon"),
        FitnessFoodItem("food_anchovies_hamsi", "Black Sea Anchovies (Baked)", "Hamsi (Fırın/Buğulama)", FoodCategory.FISH_SEAFOOD, 142, 21.0, 0.0, 5.8, 150, "150g serving", "150g porsiyon"),
        FitnessFoodItem("food_bonito_palamut", "Atlantic Bonito / Palamut", "Palamut (Fırın)", FoodCategory.FISH_SEAFOOD, 168, 23.5, 0.0, 7.6, 180, "1 slice (180g)", "1 dilim (180g)"),
        FitnessFoodItem("food_cod", "Atlantic Cod (Steamed)", "Morina Balığı (Buhar)", FoodCategory.FISH_SEAFOOD, 82, 18.0, 0.0, 0.7, 170, "1 fillet (170g)", "1 fileto (170g)"),
        FitnessFoodItem("food_tilapia", "Tilapia Fillet (Baked)", "Tilapia Fileto (Fırın)", FoodCategory.FISH_SEAFOOD, 128, 26.2, 0.0, 2.7, 150, "1 fillet (150g)", "1 fileto (150g)"),
        FitnessFoodItem("food_halibut", "Halibut (Grilled)", "Pisi Balığı (Izgara)", FoodCategory.FISH_SEAFOOD, 111, 22.5, 0.0, 1.9, 150, "150g fillet", "150g fileto"),
        FitnessFoodItem("food_shrimp", "Shrimp / Prawns (Steamed)", "Karides (Haşlama/Izgara)", FoodCategory.FISH_SEAFOOD, 99, 24.0, 0.2, 0.3, 140, "140g peeled", "140g ayıklanmış"),
        FitnessFoodItem("food_calamari_grilled", "Squid / Calamari (Grilled)", "Kalamar (Izgara)", FoodCategory.FISH_SEAFOOD, 92, 15.6, 3.1, 1.4, 150, "150g portion", "150g porsiyon"),
        FitnessFoodItem("food_octopus", "Octopus (Boiled)", "Ahtapot (Haşlanmış)", FoodCategory.FISH_SEAFOOD, 114, 21.0, 2.2, 1.8, 140, "140g portion", "140g porsiyon"),
        FitnessFoodItem("food_mussels", "Mussels (Steamed)", "Midye İçi (Buharda)", FoodCategory.FISH_SEAFOOD, 138, 19.0, 5.5, 3.8, 120, "120g meat", "120g iç"),
        FitnessFoodItem("food_scallops", "Sea Scallops (Seared)", "Deniz Tarağı", FoodCategory.FISH_SEAFOOD, 111, 20.5, 3.2, 1.2, 120, "120g portion", "120g porsiyon"),
        FitnessFoodItem("food_swordfish", "Swordfish Steak", "Kılıç Balığı Şiş", FoodCategory.FISH_SEAFOOD, 172, 23.4, 0.0, 8.0, 160, "1 steak (160g)", "1 porsiyon (160g)"),
        FitnessFoodItem("food_red_mullet", "Red Mullet / Barbun (Baked)", "Barbun (Fırın)", FoodCategory.FISH_SEAFOOD, 132, 21.4, 0.0, 4.6, 150, "150g fish", "150g porsiyon"),
        FitnessFoodItem("food_crab_meat", "Crab Meat (Cooked)", "Yengeç Eti", FoodCategory.FISH_SEAFOOD, 97, 19.4, 0.0, 1.5, 120, "120g meat", "120g porsiyon"),

        // ==================== EGGS & DAIRY (28) ====================
        FitnessFoodItem("food_egg_whole", "Whole Egg (Boiled/Poached)", "Tam Yumurta (Haşlama)", FoodCategory.EGGS_DAIRY, 155, 12.6, 1.1, 10.6, 120, "2 large eggs (120g)", "2 büyük yumurta (120g)"),
        FitnessFoodItem("food_egg_white", "Egg Whites (Liquid/Cooked)", "Yumurta Beyazı", FoodCategory.EGGS_DAIRY, 52, 10.9, 0.7, 0.2, 150, "4 egg whites (150g)", "4 yumurta beyazı (150g)"),
        FitnessFoodItem("food_egg_scrambled", "Scrambled Eggs (Plain)", "Çırpılmış Yumurta", FoodCategory.EGGS_DAIRY, 149, 10.0, 1.6, 11.0, 150, "2-3 eggs (150g)", "2-3 yumurta (150g)"),
        FitnessFoodItem("food_greek_yogurt_0", "Non-Fat Greek Yogurt (0%)", "Yağsız Süzme Yoğurt (%0)", FoodCategory.EGGS_DAIRY, 59, 10.2, 3.6, 0.4, 200, "1 bowl (200g)", "1 kase (200g)"),
        FitnessFoodItem("food_greek_yogurt_2", "Low-Fat Greek Yogurt (2%)", "Az Yağlı Süzme Yoğurt (%2)", FoodCategory.EGGS_DAIRY, 73, 9.8, 3.8, 2.0, 200, "1 bowl (200g)", "1 kase (200g)"),
        FitnessFoodItem("food_yogurt_turkish", "Traditional Strained Yogurt", "Tam Yağlı Süzme Yoğurt", FoodCategory.EGGS_DAIRY, 118, 7.2, 4.5, 8.0, 150, "1 bowl (150g)", "1 kase (150g)"),
        FitnessFoodItem("food_yogurt_regular", "Plain Low-Fat Yogurt", "Ev / Sade Yoğurt (Az Yağlı)", FoodCategory.EGGS_DAIRY, 63, 4.8, 6.2, 1.8, 200, "1 glass bowl (200g)", "1 kase (200g)"),
        FitnessFoodItem("food_ayran", "Turkish Ayran (Salted Yogurt Drink)", "Ayran", FoodCategory.EGGS_DAIRY, 37, 2.1, 2.8, 1.9, 250, "1 tall glass (250g)", "1 büyük bardak (250g)"),
        FitnessFoodItem("food_kefir_plain", "Plain Kefir", "Sade Kefir", FoodCategory.EGGS_DAIRY, 52, 3.5, 4.5, 2.0, 250, "1 glass (250g)", "1 su bardağı (250g)"),
        FitnessFoodItem("food_cottage_cheese", "Cottage Cheese (Low-Fat)", "Taze Çökelek / Cottage Cheese", FoodCategory.EGGS_DAIRY, 86, 11.8, 3.4, 2.3, 150, "150g cup", "150g porsiyon"),
        FitnessFoodItem("food_lor_cheese", "Turkish Lor Cheese (High-Protein)", "Lor Peyniri (Tuzsuz)", FoodCategory.EGGS_DAIRY, 98, 14.5, 3.2, 2.8, 100, "100g serving", "100g porsiyon"),
        FitnessFoodItem("food_quark_skim", "Low-Fat Quark", "Yağsız Quark", FoodCategory.EGGS_DAIRY, 68, 12.2, 4.0, 0.3, 200, "1 tub (200g)", "1 kase (200g)"),
        FitnessFoodItem("food_skyr", "Icelandic Skyr (Plain)", "Sade Skyr", FoodCategory.EGGS_DAIRY, 63, 11.0, 4.0, 0.2, 170, "1 pot (170g)", "1 kutu (170g)"),
        FitnessFoodItem("food_milk_skim", "Skim Milk (0.1%)", "Yağsız Süt", FoodCategory.EGGS_DAIRY, 35, 3.5, 5.0, 0.1, 250, "1 glass (250ml)", "1 bardak (250ml)"),
        FitnessFoodItem("food_milk_semi", "Semi-Skim Milk (1.5%)", "Yarım Yağlı Süt", FoodCategory.EGGS_DAIRY, 46, 3.4, 4.9, 1.5, 250, "1 glass (250ml)", "1 bardak (250ml)"),
        FitnessFoodItem("food_milk_whole", "Whole Milk (3.5%)", "Tam Yağlı Süt", FoodCategory.EGGS_DAIRY, 62, 3.3, 4.8, 3.4, 250, "1 glass (250ml)", "1 bardak (250ml)"),
        FitnessFoodItem("food_beyaz_peynir_light", "Light White Feta Cheese", "Az Yağlı Beyaz Peynir", FoodCategory.EGGS_DAIRY, 175, 16.5, 2.2, 11.0, 60, "2 slices (60g)", "2 dilim (60g)"),
        FitnessFoodItem("food_beyaz_peynir_full", "Full-Fat White Feta Cheese", "Tam Yağlı Ezine Beyaz Peynir", FoodCategory.EGGS_DAIRY, 264, 15.2, 2.5, 21.5, 60, "2 slices (60g)", "2 dilim (60g)"),
        FitnessFoodItem("food_kasar_light", "Light Kashar Cheese", "Light Kaşar Peyniri", FoodCategory.EGGS_DAIRY, 238, 27.0, 1.5, 14.0, 40, "2 slices (40g)", "2 ince dilim (40g)"),
        FitnessFoodItem("food_kasar_aged", "Aged Kashar / Cheddar Cheese", "Eski Kaşar / Cheddar", FoodCategory.EGGS_DAIRY, 395, 25.5, 1.4, 32.0, 35, "35g cube", "35g dilim"),
        FitnessFoodItem("food_mozzarella_part_skim", "Mozzarella (Part-Skim)", "Mozzarella Peyniri", FoodCategory.EGGS_DAIRY, 254, 24.3, 2.8, 16.0, 50, "50g serving", "50g porsiyon"),
        FitnessFoodItem("food_parmesan", "Parmesan Cheese (Grated)", "Parmesan Peyniri (Rendelenmiş)", FoodCategory.EGGS_DAIRY, 420, 38.0, 3.2, 28.0, 20, "2 tbsp (20g)", "2 yemek kaşığı (20g)"),
        FitnessFoodItem("food_labneh", "Labneh Spread Cheese", "Labne Peynir", FoodCategory.EGGS_DAIRY, 198, 6.8, 3.8, 17.5, 40, "2 tbsp (40g)", "2 yemek kaşığı (40g)"),
        FitnessFoodItem("food_ricotta_light", "Ricotta Cheese (Part-Skim)", "Az Yağlı Ricotta", FoodCategory.EGGS_DAIRY, 138, 11.4, 5.1, 7.9, 100, "100g serving", "100g porsiyon"),
        FitnessFoodItem("food_halloumi_light", "Light Halloumi (Grilled)", "Light Hellim Peyniri (Izgara)", FoodCategory.EGGS_DAIRY, 232, 24.0, 2.0, 14.2, 60, "3 slices (60g)", "3 dilim (60g)"),
        FitnessFoodItem("food_soy_milk_unsweet", "Soy Milk (Unsweetened)", "Şekersiz Soya Sütü", FoodCategory.EGGS_DAIRY, 33, 3.3, 1.2, 1.6, 250, "1 glass (250ml)", "1 bardak (250ml)"),
        FitnessFoodItem("food_almond_milk_unsweet", "Almond Milk (Unsweetened)", "Şekersiz Badem Sütü", FoodCategory.EGGS_DAIRY, 15, 0.6, 0.3, 1.2, 250, "1 glass (250ml)", "1 bardak (250ml)"),
        FitnessFoodItem("food_oat_milk", "Oat Milk (Unsweetened)", "Yulaf Sütü", FoodCategory.EGGS_DAIRY, 44, 1.0, 6.8, 1.5, 250, "1 glass (250ml)", "1 bardak (250ml)"),

        // ==================== GRAINS & CARBS (32) ====================
        FitnessFoodItem("food_white_rice_cooked", "White Rice (Cooked)", "Beyaz Pirinç Pilavı (Yağsız)", FoodCategory.GRAINS_CARBS, 130, 2.7, 28.2, 0.3, 200, "1 bowl cooked (200g)", "1 kase pişmiş (200g)"),
        FitnessFoodItem("food_basmati_rice_cooked", "Basmati Rice (Cooked)", "Basmati Pirinç (Haşlama)", FoodCategory.GRAINS_CARBS, 121, 3.0, 25.6, 0.4, 200, "1 bowl cooked (200g)", "1 kase pişmiş (200g)"),
        FitnessFoodItem("food_brown_rice_cooked", "Brown Rice (Cooked)", "Esmer Pirinç (Pişmiş)", FoodCategory.GRAINS_CARBS, 112, 2.6, 23.5, 0.9, 200, "1 bowl cooked (200g)", "1 kase pişmiş (200g)"),
        FitnessFoodItem("food_rice_dry", "White Rice (Dry / Uncooked)", "Pirinç (Çiğ / Kuru)", FoodCategory.GRAINS_CARBS, 365, 7.1, 80.0, 0.7, 75, "75g dry weight", "75g çiğ tartım"),
        FitnessFoodItem("food_rolled_oats", "Rolled Oats (Dry)", "Yulaf Ezmesi (Kuru)", FoodCategory.GRAINS_CARBS, 379, 13.2, 67.7, 6.5, 60, "6 tbsp (60g)", "6 yemek kaşığı (60g)"),
        FitnessFoodItem("food_oatmeal_cooked", "Oatmeal (Cooked in Water)", "Yulaf Lapası (Su ile)", FoodCategory.GRAINS_CARBS, 71, 2.5, 12.0, 1.5, 250, "1 bowl (250g)", "1 kase (250g)"),
        FitnessFoodItem("food_bulgur_cooked", "Turkish Bulgur Pilaf (Plain)", "Bulgur Pilavı (Pişmiş)", FoodCategory.GRAINS_CARBS, 105, 3.2, 19.5, 1.2, 200, "1 bowl cooked (200g)", "1 kase pişmiş (200g)"),
        FitnessFoodItem("food_bulgur_dry", "Bulgur Wheat (Dry)", "Kuru Bulgur (Çiğ)", FoodCategory.GRAINS_CARBS, 342, 12.3, 75.9, 1.3, 75, "75g dry weight", "75g çiğ tartım"),
        FitnessFoodItem("food_quinoa_cooked", "Quinoa (Cooked)", "Kinoa (Haşlanmış)", FoodCategory.GRAINS_CARBS, 120, 4.4, 21.3, 1.9, 180, "1 cup cooked (180g)", "1 kase pişmiş (180g)"),
        FitnessFoodItem("food_buckwheat_cooked", "Buckwheat / Greçka (Cooked)", "Karabuğday / Greçka (Pişmiş)", FoodCategory.GRAINS_CARBS, 92, 3.4, 19.9, 0.6, 180, "1 bowl (180g)", "1 kase (180g)"),
        FitnessFoodItem("food_whole_wheat_pasta_cooked", "Whole Wheat Pasta (Cooked)", "Tam Buğday Makarna (Pişmiş)", FoodCategory.GRAINS_CARBS, 124, 5.3, 26.5, 0.6, 200, "1 plate (200g)", "1 tabak haşlanmış (200g)"),
        FitnessFoodItem("food_pasta_cooked", "White Pasta (Cooked)", "Klasik Makarna (Haşlanmış)", FoodCategory.GRAINS_CARBS, 158, 5.8, 30.9, 0.9, 200, "1 plate (200g)", "1 tabak haşlanmış (200g)"),
        FitnessFoodItem("food_protein_pasta_dry", "Red Lentil / Chickpea Pasta (Dry)", "Bakliyat / Protein Makarna (Çiğ)", FoodCategory.GRAINS_CARBS, 345, 22.0, 56.0, 2.5, 80, "80g dry weight", "80g çiğ tartım"),
        FitnessFoodItem("food_sweet_potato_baked", "Sweet Potato (Baked)", "Tatlı Patates (Fırın)", FoodCategory.GRAINS_CARBS, 90, 2.0, 20.7, 0.2, 200, "1 medium (200g)", "1 orta boy (200g)"),
        FitnessFoodItem("food_potato_boiled", "Boiled Potato", "Haşlanmış Patates", FoodCategory.GRAINS_CARBS, 87, 1.9, 20.1, 0.1, 200, "1 large potato (200g)", "1 büyük patates (200g)"),
        FitnessFoodItem("food_potato_baked_crispy", "Oven Roasted Potatoes (Light Oil)", "Fırın Patates (Az Yağlı)", FoodCategory.GRAINS_CARBS, 118, 2.2, 21.5, 2.8, 200, "200g serving", "200g porsiyon"),
        FitnessFoodItem("food_mashed_potato", "Mashed Potatoes (Light)", "Patates Püresi (Hafif)", FoodCategory.GRAINS_CARBS, 108, 2.1, 17.5, 3.2, 200, "1 cup (200g)", "1 kase (200g)"),
        FitnessFoodItem("food_sourdough_bread", "Sourdough Bread", "Ekşi Mayalı Ekmek", FoodCategory.GRAINS_CARBS, 250, 8.8, 49.0, 1.4, 50, "1 thick slice (50g)", "1 kalın dilim (50g)"),
        FitnessFoodItem("food_whole_wheat_bread", "Whole Wheat Bread", "Tam Buğday Ekmeği", FoodCategory.GRAINS_CARBS, 247, 10.5, 43.0, 3.2, 50, "2 slices (50g)", "2 ince dilim (50g)"),
        FitnessFoodItem("food_rye_bread", "Whole Rye Bread", "Tam Çavdar Ekmeği", FoodCategory.GRAINS_CARBS, 259, 8.5, 48.3, 3.3, 50, "2 slices (50g)", "2 dilim (50g)"),
        FitnessFoodItem("food_white_bread", "White Sandwich Bread / Somun", "Beyaz Ekmek", FoodCategory.GRAINS_CARBS, 265, 8.9, 49.0, 3.2, 50, "2 slices (50g)", "2 dilim (50g)"),
        FitnessFoodItem("food_lavash_wrap", "Whole Wheat Lavash / Tortilla", "Tam Buğday Lavaş / Tortilla", FoodCategory.GRAINS_CARBS, 290, 8.5, 52.0, 5.0, 60, "1 wrap (60g)", "1 adet lavaş (60g)"),
        FitnessFoodItem("food_simit", "Turkish Sesame Simit", "Susamlı Simit", FoodCategory.GRAINS_CARBS, 298, 9.6, 51.0, 6.2, 100, "1 whole simit (100g)", "1 tam simit (100g)"),
        FitnessFoodItem("food_rice_cake", "Plain Puffed Rice Cakes", "Pirinç Patlağı (Sade)", FoodCategory.GRAINS_CARBS, 387, 8.2, 81.5, 2.8, 30, "3 rice cakes (30g)", "3 adet (30g)"),
        FitnessFoodItem("food_cornflakes", "Corn Flakes (Plain)", "Mısır Gevreği (Sade)", FoodCategory.GRAINS_CARBS, 357, 7.5, 84.0, 0.8, 45, "1 bowl (45g)", "1 kase (45g)"),
        FitnessFoodItem("food_couscous_cooked", "Couscous (Cooked)", "Kuskus (Pişmiş)", FoodCategory.GRAINS_CARBS, 112, 3.8, 23.2, 0.2, 180, "1 cup (180g)", "1 kase (180g)"),
        FitnessFoodItem("food_cream_of_rice", "Cream of Rice / Rice Flour (Dry)", "Pirinç Unu (Kuru)", FoodCategory.GRAINS_CARBS, 366, 6.0, 80.1, 1.4, 60, "60g dry mix", "60g kuru tartım"),
        FitnessFoodItem("food_granola_low_sugar", "Low-Sugar Oat Granola", "Şekersiz Yulaf Granola", FoodCategory.GRAINS_CARBS, 415, 11.5, 58.0, 14.5, 50, "50g bowl", "50g porsiyon"),
        FitnessFoodItem("food_bagel", "Plain Bagel", "Sade Bagel / Açma", FoodCategory.GRAINS_CARBS, 257, 10.0, 50.5, 1.6, 95, "1 bagel (95g)", "1 adet (95g)"),
        FitnessFoodItem("food_corn_boiled", "Sweet Corn (Boiled Kernels)", "Haşlanmış Mısır Tanesi", FoodCategory.GRAINS_CARBS, 96, 3.4, 21.0, 1.5, 150, "1 cup (150g)", "1 kase (150g)"),
        FitnessFoodItem("food_barley_cooked", "Pearl Barley (Cooked)", "Arpa Şehriye / Yarma (Pişmiş)", FoodCategory.GRAINS_CARBS, 123, 2.3, 28.2, 0.4, 180, "1 cup (180g)", "1 kase (180g)"),
        FitnessFoodItem("food_oat_bran", "Oat Bran (Dry)", "Yulaf Kepeği", FoodCategory.GRAINS_CARBS, 246, 17.3, 66.2, 7.0, 40, "4 tbsp (40g)", "4 yemek kaşığı (40g)"),

        // ==================== LEGUMES & NUTS (26) ====================
        FitnessFoodItem("food_red_lentils_cooked", "Red Lentils (Cooked)", "Kırmızı Mercimek (Haşlanmış)", FoodCategory.LEGUMES_NUTS, 116, 9.0, 20.1, 0.4, 200, "1 cup cooked (200g)", "1 kase pişmiş (200g)"),
        FitnessFoodItem("food_green_lentils_cooked", "Green Lentils (Cooked)", "Yeşil Mercimek (Haşlanmış)", FoodCategory.LEGUMES_NUTS, 116, 9.0, 20.1, 0.4, 200, "1 cup cooked (200g)", "1 kase pişmiş (200g)"),
        FitnessFoodItem("food_chickpeas_cooked", "Chickpeas (Boiled / Canned)", "Nohut (Haşlanmış)", FoodCategory.LEGUMES_NUTS, 164, 8.9, 27.4, 2.6, 160, "1 cup (160g)", "1 kase (160g)"),
        FitnessFoodItem("food_black_beans_cooked", "Black Beans (Cooked)", "Siyah Fasulye (Pişmiş)", FoodCategory.LEGUMES_NUTS, 132, 8.9, 23.7, 0.5, 170, "1 cup (170g)", "1 kase (170g)"),
        FitnessFoodItem("food_white_beans_cooked", "White Cannellini Beans (Cooked)", "Kuru Fasulye (Haşlama)", FoodCategory.LEGUMES_NUTS, 139, 9.7, 25.1, 0.4, 180, "1 cup (180g)", "1 kase (180g)"),
        FitnessFoodItem("food_kidney_beans", "Red Kidney Beans (Cooked)", "Meksika Fasulyesi (Haşlama)", FoodCategory.LEGUMES_NUTS, 127, 8.7, 22.8, 0.5, 170, "1 cup (170g)", "1 kase (170g)"),
        FitnessFoodItem("food_edamame", "Edamame (Shelled, Steamed)", "Edamame (Buharda)", FoodCategory.LEGUMES_NUTS, 121, 11.9, 8.9, 5.2, 120, "120g bowl", "120g kase"),
        FitnessFoodItem("food_green_peas_cooked", "Green Peas (Cooked)", "Bezelye (Haşlanmış)", FoodCategory.LEGUMES_NUTS, 84, 5.4, 15.6, 0.2, 150, "1 cup (150g)", "1 kase (150g)"),
        FitnessFoodItem("food_tofu_firm", "Firm Tofu", "Sert Tofu", FoodCategory.LEGUMES_NUTS, 144, 15.8, 3.9, 8.7, 150, "150g block", "150g porsiyon"),
        FitnessFoodItem("food_tempeh", "Tempeh", "Tempeh", FoodCategory.LEGUMES_NUTS, 192, 20.3, 7.6, 10.8, 120, "120g portion", "120g porsiyon"),
        FitnessFoodItem("food_almonds_raw", "Raw Almonds", "Çiğ Badem", FoodCategory.LEGUMES_NUTS, 579, 21.2, 21.6, 49.9, 30, "1 handful (30g)", "1 avuç (30g)"),
        FitnessFoodItem("food_walnuts", "Walnuts", "Ceviz İçi", FoodCategory.LEGUMES_NUTS, 654, 15.2, 13.7, 65.2, 30, "6 halves (30g)", "6 tam ceviz içi (30g)"),
        FitnessFoodItem("food_hazelnuts", "Roasted Hazelnuts", "Kavrulmuş Fındık İçi", FoodCategory.LEGUMES_NUTS, 628, 15.0, 16.7, 60.8, 30, "1 handful (30g)", "1 avuç (30g)"),
        FitnessFoodItem("food_cashews", "Cashews (Roasted Unsalted)", "Kaju Fıstığı", FoodCategory.LEGUMES_NUTS, 553, 18.2, 30.2, 43.8, 30, "1 handful (30g)", "1 avuç (30g)"),
        FitnessFoodItem("food_pistachios", "Pistachios (Shelled)", "Antep Fıstığı İçi", FoodCategory.LEGUMES_NUTS, 562, 20.2, 27.2, 45.3, 30, "30g shelled", "30g iç"),
        FitnessFoodItem("food_peanuts", "Roasted Peanuts", "Yer Fıstığı", FoodCategory.LEGUMES_NUTS, 585, 23.7, 21.5, 49.7, 30, "1 handful (30g)", "1 avuç (30g)"),
        FitnessFoodItem("food_peanut_butter_natural", "Natural Peanut Butter (100%)", "Doğal Fıstık Ezmesi (%100)", FoodCategory.LEGUMES_NUTS, 588, 25.0, 20.0, 50.0, 30, "2 tbsp (30g)", "2 tatlı kaşığı (30g)"),
        FitnessFoodItem("food_almond_butter", "Almond Butter", "Badem Ezmesi", FoodCategory.LEGUMES_NUTS, 614, 21.0, 18.8, 55.5, 30, "2 tbsp (30g)", "2 tatlı kaşığı (30g)"),
        FitnessFoodItem("food_tahini", "Tahini (Sesame Paste)", "Sade Tahin", FoodCategory.LEGUMES_NUTS, 595, 17.0, 21.2, 53.8, 30, "2 tbsp (30g)", "2 yemek kaşığı (30g)"),
        FitnessFoodItem("food_chia_seeds", "Chia Seeds", "Chia Tohumu", FoodCategory.LEGUMES_NUTS, 486, 16.5, 42.1, 30.7, 20, "2 tbsp (20g)", "2 yemek kaşığı (20g)"),
        FitnessFoodItem("food_flax_seeds", "Ground Flaxseeds", "Keten Tohumu (Öğütülmüş)", FoodCategory.LEGUMES_NUTS, 534, 18.3, 28.9, 42.2, 20, "2 tbsp (20g)", "2 yemek kaşığı (20g)"),
        FitnessFoodItem("food_pumpkin_seeds", "Pumpkin Seeds (Shelled)", "Kabak Çekirdeği İçi", FoodCategory.LEGUMES_NUTS, 559, 30.2, 10.7, 49.0, 30, "30g handful", "1 avuç (30g)"),
        FitnessFoodItem("food_sunflower_seeds", "Sunflower Seeds (Shelled)", "Ayçekirdeği İçi", FoodCategory.LEGUMES_NUTS, 584, 20.8, 20.0, 51.5, 30, "30g serving", "30g porsiyon"),
        FitnessFoodItem("food_pecans", "Pecans", "Pekan Cevizi", FoodCategory.LEGUMES_NUTS, 691, 9.2, 13.9, 72.0, 30, "30g handful", "30g avuç"),
        FitnessFoodItem("food_brazil_nuts", "Brazil Nuts", "Brezilya Cevizi", FoodCategory.LEGUMES_NUTS, 659, 14.3, 11.7, 67.1, 20, "4 nuts (20g)", "4 adet (20g)"),
        FitnessFoodItem("food_roasted_chickpeas", "Roasted Chickpeas (Leblebi)", "Sarı Leblebi", FoodCategory.LEGUMES_NUTS, 364, 19.5, 61.0, 5.2, 40, "1 small cup (40g)", "1 küçük kase (40g)")
    )

    val ALL_FOODS: List<FitnessFoodItem> get() = FitnessFoodItemsPart2.ALL_FOODS
    fun findById(id: String): FitnessFoodItem? = FitnessFoodItemsPart2.findById(id)
    fun search(query: String, category: FoodCategory? = null): List<FitnessFoodItem> =
        FitnessFoodItemsPart2.search(query, category)
    fun dailyProteinTargetGrams(profile: UserProfile): Int =
        FitnessFoodItemsPart2.dailyProteinTargetGrams(profile)
    fun evaluateDailyNutrition(
        profile: UserProfile,
        consumedKcal: Int,
        consumedProtein: Int,
        langCode: String
    ): MascotNutritionFeedback =
        FitnessFoodItemsPart2.evaluateDailyNutrition(profile, consumedKcal, consumedProtein, langCode)
}
