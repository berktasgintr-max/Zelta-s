package com.example.data.catalog

import com.example.data.model.UserProfile
import com.example.ui.components.CoachAnimationMood
import kotlin.math.roundToInt

object FitnessFoodItemsPart2 {
    val ITEMS: List<FitnessFoodItem> = listOf(
        // ==================== FRUITS (28) ====================
        FitnessFoodItem("food_banana", "Banana (Fresh)", "Muz (Taze)", FoodCategory.FRUITS, 89, 1.1, 22.8, 0.3, 120, "1 medium banana (120g)", "1 orta boy muz (120g)"),
        FitnessFoodItem("food_apple", "Apple (With Skin)", "Elma (Kabuklu)", FoodCategory.FRUITS, 52, 0.3, 13.8, 0.2, 160, "1 medium apple (160g)", "1 orta boy elma (160g)"),
        FitnessFoodItem("food_blueberries", "Blueberries", "Yaban Mersini", FoodCategory.FRUITS, 57, 0.7, 14.5, 0.3, 100, "1 handful cup (100g)", "1 kase (100g)"),
        FitnessFoodItem("food_strawberries", "Strawberries", "Çilek", FoodCategory.FRUITS, 32, 0.7, 7.7, 0.3, 150, "1 bowl (150g)", "1 kase (150g)"),
        FitnessFoodItem("food_avocado", "Avocado", "Avokado", FoodCategory.FRUITS, 160, 2.0, 8.5, 14.7, 80, "1/2 avocado (80g)", "Yarım avokado (80g)"),
        FitnessFoodItem("food_orange", "Orange (Peeled)", "Portakal", FoodCategory.FRUITS, 47, 0.9, 11.8, 0.1, 150, "1 orange (150g)", "1 orta boy (150g)"),
        FitnessFoodItem("food_kiwi", "Kiwi Fruit", "Kivi", FoodCategory.FRUITS, 61, 1.1, 14.7, 0.5, 140, "2 kiwis (140g)", "2 adet kivi (140g)"),
        FitnessFoodItem("food_watermelon", "Watermelon", "Karpuz", FoodCategory.FRUITS, 30, 0.6, 7.6, 0.2, 250, "1 big slice (250g)", "1 büyük dilim (250g)"),
        FitnessFoodItem("food_melon_cantaloupe", "Melon / Cantaloupe", "Kavun", FoodCategory.FRUITS, 34, 0.8, 8.2, 0.2, 200, "2 slices (200g)", "2 dilim (200g)"),
        FitnessFoodItem("food_pineapple", "Pineapple (Fresh)", "Ananas (Taze)", FoodCategory.FRUITS, 50, 0.5, 13.1, 0.1, 150, "1 cup chunks (150g)", "1 kase dilim (150g)"),
        FitnessFoodItem("food_mango", "Mango (Fresh)", "Mango", FoodCategory.FRUITS, 60, 0.8, 15.0, 0.4, 150, "1 cup (150g)", "1 kase (150g)"),
        FitnessFoodItem("food_grapes", "Grapes (Seedless)", "Üzüm (Taze)", FoodCategory.FRUITS, 69, 0.7, 18.1, 0.2, 120, "1 small bunch (120g)", "1 küçük salkım (120g)"),
        FitnessFoodItem("food_dates_medjool", "Dates (Dried / Medjool)", "Kuru Hurma", FoodCategory.FRUITS, 277, 1.8, 75.0, 0.2, 40, "2 large dates (40g)", "2 büyük hurma (40g)"),
        FitnessFoodItem("food_dried_figs", "Dried Figs", "Kuru İncir", FoodCategory.FRUITS, 249, 3.3, 63.9, 0.9, 40, "2 figs (40g)", "2 kuru incir (40g)"),
        FitnessFoodItem("food_dried_apricots", "Dried Apricots", "Kuru Kayısı", FoodCategory.FRUITS, 241, 3.4, 62.6, 0.5, 40, "4 apricots (40g)", "4 adet (40g)"),
        FitnessFoodItem("food_raisins", "Raisins (Seedless)", "Kuru Üzüm", FoodCategory.FRUITS, 299, 3.1, 79.2, 0.5, 30, "1 tbsp handful (30g)", "1 avuç (30g)"),
        FitnessFoodItem("food_pomegranate", "Pomegranate Seeds", "Nar Tanesi", FoodCategory.FRUITS, 83, 1.7, 18.7, 1.2, 120, "1 small bowl (120g)", "1 küçük kase (120g)"),
        FitnessFoodItem("food_pear", "Pear", "Armut", FoodCategory.FRUITS, 57, 0.4, 15.2, 0.1, 160, "1 medium pear (160g)", "1 orta boy (160g)"),
        FitnessFoodItem("food_peach", "Peach", "Şeftali", FoodCategory.FRUITS, 39, 0.9, 9.5, 0.3, 150, "1 peach (150g)", "1 orta şeftali (150g)"),
        FitnessFoodItem("food_cherries", "Sweet Cherries", "Kiraz", FoodCategory.FRUITS, 63, 1.1, 16.0, 0.2, 120, "1 cup (120g)", "1 kase (120g)"),
        FitnessFoodItem("food_sour_cherries", "Sour Cherries (Vişne)", "Vişne", FoodCategory.FRUITS, 50, 1.0, 12.2, 0.3, 120, "1 cup (120g)", "1 kase (120g)"),
        FitnessFoodItem("food_raspberries", "Raspberries", "Ahududu / Frambuaz", FoodCategory.FRUITS, 52, 1.2, 11.9, 0.7, 100, "1 cup (100g)", "1 kase (100g)"),
        FitnessFoodItem("food_blackberries", "Blackberries", "Böğürtlen", FoodCategory.FRUITS, 43, 1.4, 9.6, 0.5, 100, "1 cup (100g)", "1 kase (100g)"),
        FitnessFoodItem("food_mandarin", "Mandarin / Tangerine", "Mandalina", FoodCategory.FRUITS, 53, 0.8, 13.3, 0.3, 150, "2 mandarins (150g)", "2 mandalina (150g)"),
        FitnessFoodItem("food_grapefruit", "Grapefruit", "Greyfurt", FoodCategory.FRUITS, 42, 0.8, 10.7, 0.1, 200, "1 grapefruit (200g)", "1 greyfurt (200g)"),
        FitnessFoodItem("food_plum", "Fresh Plum", "Erik", FoodCategory.FRUITS, 46, 0.7, 11.4, 0.3, 120, "3 plums (120g)", "3 adet (120g)"),
        FitnessFoodItem("food_fresh_fig", "Fresh Fig", "Taze İncir", FoodCategory.FRUITS, 74, 0.8, 19.2, 0.3, 100, "2 figs (100g)", "2 taze incir (100g)"),
        FitnessFoodItem("food_lemon_juice", "Fresh Lemon Juice", "Taze Limon Suyu", FoodCategory.FRUITS, 22, 0.4, 6.9, 0.2, 30, "2 tbsp (30g)", "2 yemek kaşığı (30g)"),

        // ==================== VEGETABLES (28) ====================
        FitnessFoodItem("food_broccoli", "Broccoli (Steamed)", "Brokoli (Buharda)", FoodCategory.VEGETABLES, 35, 2.4, 7.2, 0.4, 150, "1 bowl florets (150g)", "1 kase (150g)"),
        FitnessFoodItem("food_spinach", "Fresh Spinach (Cooked/Raw)", "Ispanak", FoodCategory.VEGETABLES, 23, 2.9, 3.6, 0.4, 150, "1 big bowl (150g)", "1 büyük kase (150g)"),
        FitnessFoodItem("food_kale", "Kale Greens", "Kara Lahana / Kale", FoodCategory.VEGETABLES, 35, 2.9, 4.4, 1.5, 100, "100g chopped", "100g doğranmış"),
        FitnessFoodItem("food_bell_pepper", "Red Bell Pepper", "Kırmızı Kapya Biber", FoodCategory.VEGETABLES, 31, 1.0, 6.0, 0.3, 120, "1 pepper (120g)", "1 adet (120g)"),
        FitnessFoodItem("food_green_pepper", "Green Pepper", "Yeşil Çarliston / Sivri Biber", FoodCategory.VEGETABLES, 20, 0.9, 4.6, 0.2, 100, "2 peppers (100g)", "2 adet (100g)"),
        FitnessFoodItem("food_zucchini", "Zucchini (Grilled/Steamed)", "Kabak (Izgara/Haşlama)", FoodCategory.VEGETABLES, 17, 1.2, 3.1, 0.3, 180, "1 medium zucchini (180g)", "1 orta boy (180g)"),
        FitnessFoodItem("food_asparagus", "Asparagus (Roasted)", "Kuşkonmaz (Fırın)", FoodCategory.VEGETABLES, 22, 2.4, 4.1, 0.2, 130, "8 spears (130g)", "8 dal (130g)"),
        FitnessFoodItem("food_tomato", "Fresh Tomato", "Domates", FoodCategory.VEGETABLES, 18, 0.9, 3.9, 0.2, 150, "1 large tomato (150g)", "1 büyük domates (150g)"),
        FitnessFoodItem("food_cucumber", "Cucumber", "Salatalık", FoodCategory.VEGETABLES, 15, 0.7, 3.6, 0.1, 150, "1 cucumber (150g)", "1 adet (150g)"),
        FitnessFoodItem("food_carrot", "Carrot (Raw/Boiled)", "Havuç", FoodCategory.VEGETABLES, 41, 0.9, 9.6, 0.2, 100, "1 large carrot (100g)", "1 büyük havuç (100g)"),
        FitnessFoodItem("food_cauliflower", "Cauliflower (Steamed)", "Karnabahar (Buharda)", FoodCategory.VEGETABLES, 25, 1.9, 5.0, 0.3, 150, "1 bowl (150g)", "1 kase (150g)"),
        FitnessFoodItem("food_green_beans", "Green Beans (Steamed)", "Taze Fasulye (Haşlama)", FoodCategory.VEGETABLES, 31, 1.8, 7.0, 0.2, 150, "150g bowl", "150g porsiyon"),
        FitnessFoodItem("food_mushrooms", "White Mushrooms (Sauteed Plain)", "Kültür Mantarı (Sote)", FoodCategory.VEGETABLES, 28, 3.1, 4.3, 0.4, 150, "150g cooked", "150g pişmiş"),
        FitnessFoodItem("food_eggplant_roasted", "Eggplant / Aubergine (Roasted)", "Közlenmiş Patlıcan", FoodCategory.VEGETABLES, 35, 0.8, 8.6, 0.2, 150, "150g roasted", "150g közlenmiş"),
        FitnessFoodItem("food_arugula", "Fresh Arugula / Rocket", "Roka", FoodCategory.VEGETABLES, 25, 2.6, 3.7, 0.7, 60, "1 bunch (60g)", "1 demet (60g)"),
        FitnessFoodItem("food_lettuce_romaine", "Romaine / Iceberg Lettuce", "Marul / Göbek Salata", FoodCategory.VEGETABLES, 17, 1.2, 3.3, 0.3, 100, "1 big salad base (100g)", "1 büyük salata kasesi (100g)"),
        FitnessFoodItem("food_onion", "Yellow / Red Onion", "Kuru Soğan", FoodCategory.VEGETABLES, 40, 1.1, 9.3, 0.1, 80, "1 medium onion (80g)", "1 orta soğan (80g)"),
        FitnessFoodItem("food_beetroot_boiled", "Beetroot (Boiled)", "Haşlanmış Pancar", FoodCategory.VEGETABLES, 44, 1.7, 10.0, 0.2, 120, "120g sliced", "120g dilim"),
        FitnessFoodItem("food_cabbage_white", "White Cabbage", "Beyaz Lahana", FoodCategory.VEGETABLES, 25, 1.3, 5.8, 0.1, 150, "150g shredded", "150g doğranmış"),
        FitnessFoodItem("food_red_cabbage", "Red Cabbage", "Kırmızı / Mor Lahana", FoodCategory.VEGETABLES, 31, 1.4, 7.4, 0.2, 120, "120g shredded", "120g doğranmış"),
        FitnessFoodItem("food_brussels_sprouts", "Brussels Sprouts (Roasted)", "Brüksel Lahanası (Fırın)", FoodCategory.VEGETABLES, 43, 3.4, 9.0, 0.3, 150, "150g serving", "150g porsiyon"),
        FitnessFoodItem("food_pumpkin", "Pumpkin (Baked Plain)", "Balkabağı (Fırın)", FoodCategory.VEGETABLES, 26, 1.0, 6.5, 0.1, 200, "200g cubes", "200g dilim"),
        FitnessFoodItem("food_okra", "Okra (Stewed Plain)", "Bamya (Sade)", FoodCategory.VEGETABLES, 33, 1.9, 7.5, 0.2, 150, "150g bowl", "150g porsiyon"),
        FitnessFoodItem("food_leek", "Leek (Steamed)", "Pırasa", FoodCategory.VEGETABLES, 31, 0.8, 7.6, 0.2, 150, "150g serving", "150g porsiyon"),
        FitnessFoodItem("food_artichoke", "Artichoke Heart", "Enginar Kalbi", FoodCategory.VEGETABLES, 47, 3.3, 10.5, 0.2, 120, "2 hearts (120g)", "2 adet enginar (120g)"),
        FitnessFoodItem("food_celery_stalk", "Celery Stalk", "Kereviz Sapı", FoodCategory.VEGETABLES, 14, 0.7, 3.0, 0.2, 100, "2 stalks (100g)", "2 sap (100g)"),
        FitnessFoodItem("food_radish", "Red Radish", "Kırmızı Turp", FoodCategory.VEGETABLES, 16, 0.7, 3.4, 0.1, 100, "100g sliced", "100g dilim"),
        FitnessFoodItem("food_pickles", "Dill Pickles (Low Sodium)", "Salatalık Turşusu", FoodCategory.VEGETABLES, 12, 0.5, 2.4, 0.2, 60, "3 pickles (60g)", "3 adet (60g)"),

        // ==================== FITNESS MEALS & BOWLS (32) ====================
        FitnessFoodItem("food_meal_chicken_rice", "Grilled Chicken & Basmati Rice Bowl", "Izgara Tavuk & Pirinç Pilavı Kasesi", FoodCategory.MEALS_BOWLS, 148, 17.5, 14.5, 2.2, 350, "1 fitness bowl (350g)", "1 sporcu tabağı (350g)"),
        FitnessFoodItem("food_meal_chicken_bulgur", "Chicken Breast & Bulgur Pilaf", "Tavuk Göğsü & Bulgur Pilavı", FoodCategory.MEALS_BOWLS, 139, 17.2, 11.5, 2.4, 350, "1 plate (350g)", "1 tam tabak (350g)"),
        FitnessFoodItem("food_meal_salmon_quinoa", "Salmon, Quinoa & Broccoli Bowl", "Somon, Kinoa & Brokoli Kasesi", FoodCategory.MEALS_BOWLS, 156, 13.5, 11.0, 6.4, 320, "1 bowl (320g)", "1 kase (320g)"),
        FitnessFoodItem("food_meal_steak_sweet_potato", "Sirloin Steak & Sweet Potato", "Izgara Bonfile & Tatlı Patates", FoodCategory.MEALS_BOWLS, 152, 15.8, 10.5, 5.2, 350, "1 plate (350g)", "1 porsiyon (350g)"),
        FitnessFoodItem("food_meal_turkey_pasta", "Lean Turkey Meat & Wheat Pasta", "Hindi Kıyma & Tam Buğday Makarna", FoodCategory.MEALS_BOWLS, 145, 14.5, 14.2, 3.2, 350, "1 bowl (350g)", "1 tabak (350g)"),
        FitnessFoodItem("food_meal_protein_oats", "Protein Oats (Oats + Milk + Whey + Banana)", "Proteinli Yulaf Lapa (Süt + Whey + Muz)", FoodCategory.MEALS_BOWLS, 138, 11.2, 18.0, 2.4, 320, "1 full breakfast bowl (320g)", "1 kahvaltı kasesi (320g)"),
        FitnessFoodItem("food_meal_egg_avocado_toast", "2 Eggs + Avocado Whole-Grain Toast", "2 Yumurta + Avokadolu Tam Buğday Tost", FoodCategory.MEALS_BOWLS, 198, 9.6, 14.5, 11.5, 220, "2 toasts (220g)", "2 dilim tost (220g)"),
        FitnessFoodItem("food_meal_menemen", "Turkish Menemen (Eggs, Tomato, Pepper)", "Menemen (2 Yumurtalı)", FoodCategory.MEALS_BOWLS, 118, 6.8, 4.5, 8.2, 240, "1 skillet serving (240g)", "1 porsiyon menemen (240g)"),
        FitnessFoodItem("food_meal_omlette_cheese", "3-Egg White & Lor Cheese Omelette", "Lor Peynirli Yumurta Omlet", FoodCategory.MEALS_BOWLS, 126, 13.8, 1.8, 6.9, 200, "1 large omelette (200g)", "1 büyük omlet (200g)"),
        FitnessFoodItem("food_meal_tuna_salad", "High-Protein Tuna & Greens Salad", "Ton Balıklı Yeşil Salata", FoodCategory.MEALS_BOWLS, 98, 14.2, 3.5, 3.0, 280, "1 large bowl (280g)", "1 büyük salata kasesi (280g)"),
        FitnessFoodItem("food_meal_chicken_caesar_light", "Light Chicken Caesar Salad", "Fit Tavuklu Sezar Salata", FoodCategory.MEALS_BOWLS, 128, 14.5, 5.2, 5.4, 280, "1 bowl (280g)", "1 kase (280g)"),
        FitnessFoodItem("food_meal_lentil_soup", "Turkish Red Lentil Soup", "Mercimek Çorbası", FoodCategory.MEALS_BOWLS, 68, 4.2, 9.5, 1.6, 250, "1 soup bowl (250g)", "1 kase çorba (250g)"),
        FitnessFoodItem("food_meal_chicken_soup", "Chicken & Veggie Broth Soup", "Tavuk Suyu Sebze Çorbası", FoodCategory.MEALS_BOWLS, 54, 5.6, 4.5, 1.5, 250, "1 bowl (250g)", "1 kase (250g)"),
        FitnessFoodItem("food_meal_turkey_wrap", "Grilled Turkey Breast Lavash Wrap", "Izgara Hindi Lavaş Dürüm", FoodCategory.MEALS_BOWLS, 165, 16.5, 16.0, 3.8, 220, "1 wrap (220g)", "1 dürüm (220g)"),
        FitnessFoodItem("food_meal_chicken_wrap", "Grilled Chicken Lavash Wrap", "Fit Tavuk Şiş Dürüm", FoodCategory.MEALS_BOWLS, 174, 17.0, 16.8, 4.2, 230, "1 wrap (230g)", "1 dürüm (230g)"),
        FitnessFoodItem("food_meal_beef_burrito_bowl", "Lean Beef, Rice & Black Bean Bowl", "Dana Kıyma, Pirinç & Fasulye Kasesi", FoodCategory.MEALS_BOWLS, 158, 13.8, 16.5, 4.0, 350, "1 burrito bowl (350g)", "1 kase (350g)"),
        FitnessFoodItem("food_meal_tuna_pasta", "Tuna & Whole-Wheat Pasta Bowl", "Ton Balıklı Tam Buğday Makarna", FoodCategory.MEALS_BOWLS, 142, 13.5, 17.5, 2.0, 300, "1 plate (300g)", "1 tabak (300g)"),
        FitnessFoodItem("food_meal_kuru_fasulye_rice", "Turkish Beans & Rice (Kuru Pilav)", "Kuru Fasulye & Pilav", FoodCategory.MEALS_BOWLS, 135, 6.2, 21.0, 2.8, 320, "1 traditional plate (320g)", "1 tabak (320g)"),
        FitnessFoodItem("food_meal_karniyarik_fit", "Baked Stuffed Eggplant (Lean Beef)", "Fırın Karnıyarık (Az Yağlı Kıyma)", FoodCategory.MEALS_BOWLS, 112, 8.5, 6.5, 5.8, 260, "1 eggplant (260g)", "1 adet (260g)"),
        FitnessFoodItem("food_meal_grilled_meatball_salad", "Grilled Kofte & Shepherd Salad", "Izgara Köfte & Çoban Salata", FoodCategory.MEALS_BOWLS, 142, 12.2, 4.2, 8.4, 300, "1 plate (300g)", "1 tabak (300g)"),
        FitnessFoodItem("food_meal_greek_yogurt_bowl", "Greek Yogurt, Berries & Chia Bowl", "Süzme Yoğurt, Meyve & Chia Kasesi", FoodCategory.MEALS_BOWLS, 106, 8.5, 11.2, 3.1, 250, "1 bowl (250g)", "1 kase (250g)"),
        FitnessFoodItem("food_meal_tuna_toast", "Tuna Melt Whole-Grain Toast", "Ton Balıklı Tam Buğday Tost", FoodCategory.MEALS_BOWLS, 185, 16.2, 18.0, 5.2, 180, "1 double toast (180g)", "1 tam tost (180g)"),
        FitnessFoodItem("food_meal_shakshuka", "Poached Eggs in Tomato Pepper Sauce", "Fit Şakşuka / Domatesli Yumurta", FoodCategory.MEALS_BOWLS, 108, 6.9, 5.2, 6.7, 240, "1 bowl (240g)", "1 tabak (240g)"),
        FitnessFoodItem("food_meal_chickpea_rice", "Chickpea & Basmati Rice Bowl", "Nohutlu Basmati Pilavı", FoodCategory.MEALS_BOWLS, 146, 5.8, 25.0, 2.4, 300, "1 bowl (300g)", "1 tabak (300g)"),
        FitnessFoodItem("food_meal_chicken_curry_light", "Light Chicken Curry & Rice", "Hafif Köri Soslu Tavuk & Pilav", FoodCategory.MEALS_BOWLS, 152, 15.0, 15.5, 3.4, 330, "1 plate (330g)", "1 tabak (330g)"),
        FitnessFoodItem("food_meal_sushi_salmon", "Salmon Nigiri & Maki Rolls", "Somon Sushi (Nigiri/Maki)", FoodCategory.MEALS_BOWLS, 145, 7.8, 22.0, 2.8, 200, "8 pieces (200g)", "8 parça (200g)"),
        FitnessFoodItem("food_meal_protein_pancakes", "Banana Oat Protein Pancakes", "Muzlu Yulaflı Protein Pankek", FoodCategory.MEALS_BOWLS, 178, 14.5, 22.0, 3.5, 180, "3 pancakes (180g)", "3 adet pankek (180g)"),
        FitnessFoodItem("food_meal_toast_kasar", "Light Kashar Whole-Wheat Toast", "Az Yağlı Kaşarlı Tost", FoodCategory.MEALS_BOWLS, 248, 14.8, 29.0, 7.6, 130, "1 toast (130g)", "1 tam tost (130g)"),
        FitnessFoodItem("food_meal_mercimek_kofte", "Turkish Red Lentil Patties", "Mercimek Köftesi", FoodCategory.MEALS_BOWLS, 148, 6.2, 23.5, 3.2, 150, "5 pieces (150g)", "5 adet (150g)"),
        FitnessFoodItem("food_meal_cacik", "Turkish Cacik (Yogurt, Cucumber, Mint)", "Cacık", FoodCategory.MEALS_BOWLS, 48, 3.2, 3.5, 2.3, 200, "1 bowl (200g)", "1 kase (200g)"),
        FitnessFoodItem("food_meal_iskender_fit", "Lean Grilled Beef & Yogurt Plate", "Yoğurtlu Izgara Et Tabağı", FoodCategory.MEALS_BOWLS, 162, 16.8, 7.5, 7.2, 300, "1 plate (300g)", "1 porsiyon (300g)"),
        FitnessFoodItem("food_meal_ezogelin", "Ezogelin Lentil & Bulgur Soup", "Ezogelin Çorbası", FoodCategory.MEALS_BOWLS, 72, 4.0, 10.8, 1.5, 250, "1 bowl (250g)", "1 kase (250g)"),

        // ==================== SHAKES & FITNESS SNACKS (24) ====================
        FitnessFoodItem("food_whey_isolate", "Whey Protein Isolate (Dry Powder)", "Whey Protein İzolatı (Toz)", FoodCategory.SHAKES_SNACKS, 375, 86.0, 3.5, 1.5, 30, "1 scoop (30g)", "1 ölçek (30g)"),
        FitnessFoodItem("food_whey_concentrate", "Whey Protein Concentrate (Powder)", "Whey Protein Konsantresi (Toz)", FoodCategory.SHAKES_SNACKS, 392, 78.0, 7.5, 5.5, 30, "1 scoop (30g)", "1 ölçek (30g)"),
        FitnessFoodItem("food_casein_powder", "Micellar Casein Protein (Powder)", "Kazein Protein Tozu", FoodCategory.SHAKES_SNACKS, 368, 80.0, 5.0, 2.5, 30, "1 scoop (30g)", "1 ölçek (30g)"),
        FitnessFoodItem("food_plant_protein", "Pea & Rice Plant Protein Powder", "Bitkisel Protein Tozu", FoodCategory.SHAKES_SNACKS, 380, 76.0, 8.0, 4.5, 30, "1 scoop (30g)", "1 ölçek (30g)"),
        FitnessFoodItem("food_protein_bar", "High-Protein Bar (Low Sugar)", "Yüksek Proteinli Bar", FoodCategory.SHAKES_SNACKS, 345, 33.0, 32.0, 10.5, 60, "1 bar (60g)", "1 bar (60g)"),
        FitnessFoodItem("food_protein_milk_shake", "Ready-to-Drink Protein Milk (250ml)", "Hazır Protein Sütü (Şişe)", FoodCategory.SHAKES_SNACKS, 62, 10.0, 4.2, 0.5, 250, "1 bottle (250g)", "1 şişe (250g)"),
        FitnessFoodItem("food_mass_gainer", "Mass Gainer Powder", "Kilo Aldırıcı / Gainer Toz", FoodCategory.SHAKES_SNACKS, 382, 20.0, 70.0, 2.5, 100, "2 scoops (100g)", "2 ölçek (100g)"),
        FitnessFoodItem("food_dark_chocolate_85", "Dark Chocolate (85% Cocoa)", "Bitter Çikolata (%85 Kakao)", FoodCategory.SHAKES_SNACKS, 582, 9.5, 22.0, 49.0, 20, "2 squares (20g)", "2 kare (20g)"),
        FitnessFoodItem("food_dark_chocolate_70", "Dark Chocolate (70% Cocoa)", "Bitter Çikolata (%70 Kakao)", FoodCategory.SHAKES_SNACKS, 565, 7.8, 33.0, 42.5, 20, "2 squares (20g)", "2 kare (20g)"),
        FitnessFoodItem("food_popcorn_air", "Air-Popped Popcorn (No Butter)", "Yağsız Patlamış Mısır", FoodCategory.SHAKES_SNACKS, 382, 12.0, 77.8, 4.3, 30, "3 cups popped (30g)", "3 kase (30g)"),
        FitnessFoodItem("food_hummus", "Classic Hummus", "Klasik Humus", FoodCategory.SHAKES_SNACKS, 177, 7.9, 14.3, 9.6, 60, "2 tbsp (60g)", "2 yemek kaşığı (60g)"),
        FitnessFoodItem("food_tzatziki_dip", "Greek Yogurt Herb Dip", "Dereotlu Yoğurt Sos", FoodCategory.SHAKES_SNACKS, 78, 5.2, 4.2, 4.5, 60, "60g dip", "60g sos"),
        FitnessFoodItem("food_cottage_fruit_cup", "Cottage Cheese & Berries Snack", "Meyveli Lor / Süzme Atıştırmalık", FoodCategory.SHAKES_SNACKS, 74, 9.2, 5.8, 1.5, 150, "1 snack cup (150g)", "1 kase (150g)"),
        FitnessFoodItem("food_rice_cake_pb", "Rice Cake + Natural Peanut Butter", "Pirinç Patlağı + Fıstık Ezmesi", FoodCategory.SHAKES_SNACKS, 475, 15.5, 54.0, 22.0, 45, "2 cakes + 15g PB (45g)", "2 adet + 15g ezme (45g)"),
        FitnessFoodItem("food_protein_pudding", "High-Protein Chocolate Pudding", "Yüksek Proteinli Çikolatalı Puding", FoodCategory.SHAKES_SNACKS, 78, 10.0, 5.5, 1.5, 200, "1 pot (200g)", "1 kutu (200g)"),
        FitnessFoodItem("food_trail_mix", "Unsalted Nut & Berry Trail Mix", "Çiğ Kuruyemiş & Kuru Meyve Mix", FoodCategory.SHAKES_SNACKS, 485, 14.0, 38.0, 32.0, 35, "1 small pack (35g)", "1 küçük avuç (35g)"),
        FitnessFoodItem("food_corn_cakes", "Salted Corn Cakes", "Mısır Patlağı / Kıtır (Mısır)", FoodCategory.SHAKES_SNACKS, 385, 7.5, 81.0, 2.5, 30, "3 cakes (30g)", "3 adet (30g)"),
        FitnessFoodItem("food_honey_raw", "Raw Blossom Honey", "Doğal Çiçek Balı", FoodCategory.SHAKES_SNACKS, 304, 0.3, 82.4, 0.0, 15, "1 tbsp (15g)", "1 tatlı kaşığı (15g)"),
        FitnessFoodItem("food_protein_muffin", "Oat & Whey Protein Muffin", "Yulaflı Protein Kek", FoodCategory.SHAKES_SNACKS, 245, 16.5, 28.0, 7.5, 80, "1 muffin (80g)", "1 adet (80g)"),
        FitnessFoodItem("food_grissini_whole", "Whole-Wheat Breadsticks", "Tam Buğday Grissini", FoodCategory.SHAKES_SNACKS, 395, 12.0, 72.0, 6.0, 30, "3 sticks (30g)", "3 adet (30g)"),
        FitnessFoodItem("food_apple_cinnamon_chips", "Baked Apple Chips (No Added Sugar)", "Fırınlanmış Elma Cipsi", FoodCategory.SHAKES_SNACKS, 335, 1.5, 84.0, 0.8, 30, "1 bag (30g)", "1 paket (30g)"),
        FitnessFoodItem("food_black_coffee", "Black Coffee / Espresso (Unsweetened)", "Şekersiz Sade Kahve / Espresso", FoodCategory.SHAKES_SNACKS, 2, 0.1, 0.0, 0.0, 200, "1 cup (200ml)", "1 fincan (200ml)"),
        FitnessFoodItem("food_green_tea", "Green Tea (Unsweetened)", "Şekersiz Yeşil Çay", FoodCategory.SHAKES_SNACKS, 1, 0.0, 0.2, 0.0, 250, "1 mug (250ml)", "1 kupa (250ml)"),
        FitnessFoodItem("food_matcha_latte_skim", "Skinny Matcha Latte (Skim Milk)", "Yağsız Sütlü Matcha Latte", FoodCategory.SHAKES_SNACKS, 36, 3.2, 5.0, 0.2, 250, "1 tall cup (250ml)", "1 bardak (250ml)"),

        // ==================== HEALTHY FATS, OILS & CONDIMENTS (18) ====================
        FitnessFoodItem("food_olive_oil", "Extra Virgin Olive Oil", "Sızma Zeytinyağı", FoodCategory.FATS_OILS, 884, 0.0, 0.0, 100.0, 10, "1 tbsp (10g)", "1 yemek kaşığı (10g)"),
        FitnessFoodItem("food_butter", "Grass-Fed Butter", "Doğal Tereyağı", FoodCategory.FATS_OILS, 717, 0.9, 0.1, 81.1, 10, "1 tbsp (10g)", "1 yemek kaşığı (10g)"),
        FitnessFoodItem("food_ghee", "Clarified Butter (Ghee)", "Sade Yağ (Ghee)", FoodCategory.FATS_OILS, 876, 0.0, 0.0, 99.5, 10, "1 tbsp (10g)", "1 yemek kaşığı (10g)"),
        FitnessFoodItem("food_coconut_oil", "Virgin Coconut Oil", "Hindistan Cevizi Yağı", FoodCategory.FATS_OILS, 862, 0.0, 0.0, 100.0, 10, "1 tbsp (10g)", "1 yemek kaşığı (10g)"),
        FitnessFoodItem("food_avocado_oil", "Avocado Oil", "Avokado Yağı", FoodCategory.FATS_OILS, 884, 0.0, 0.0, 100.0, 10, "1 tbsp (10g)", "1 yemek kaşığı (10g)"),
        FitnessFoodItem("food_flaxseed_oil", "Cold-Pressed Flaxseed Oil", "Keten Tohumu Yağı", FoodCategory.FATS_OILS, 884, 0.0, 0.0, 100.0, 10, "1 tbsp (10g)", "1 yemek kaşığı (10g)"),
        FitnessFoodItem("food_black_olives", "Black Olives (Cured)", "Siyah Zeytin", FoodCategory.FATS_OILS, 145, 1.0, 3.8, 14.0, 30, "10 olives (30g)", "10 adet zeytin (30g)"),
        FitnessFoodItem("food_green_olives", "Green Olives", "Yeşil Zeytin", FoodCategory.FATS_OILS, 145, 1.0, 3.8, 15.3, 30, "10 olives (30g)", "10 adet zeytin (30g)"),
        FitnessFoodItem("food_guacamole", "Fresh Guacamole", "Guakamole (Avokado Sos)", FoodCategory.FATS_OILS, 157, 2.0, 8.5, 14.3, 50, "2 tbsp (50g)", "2 yemek kaşığı (50g)"),
        FitnessFoodItem("food_pesto", "Basil Pesto Sauce", "Fesleğenli Pesto Sos", FoodCategory.FATS_OILS, 446, 5.2, 6.4, 44.0, 20, "1 tbsp (20g)", "1 yemek kaşığı (20g)"),
        FitnessFoodItem("food_mayo_light", "Light Mayonnaise", "Light Mayonez", FoodCategory.FATS_OILS, 260, 0.6, 8.0, 25.0, 15, "1 tbsp (15g)", "1 yemek kaşığı (15g)"),
        FitnessFoodItem("food_mustard", "Dijon / Yellow Mustard", "Hardal Sos", FoodCategory.FATS_OILS, 66, 4.4, 5.3, 3.3, 15, "1 tbsp (15g)", "1 yemek kaşığı (15g)"),
        FitnessFoodItem("food_ketchup_zero", "No-Sugar-Added Ketchup", "Şekersiz Ketçap", FoodCategory.FATS_OILS, 45, 1.3, 9.0, 0.2, 20, "1 tbsp (20g)", "1 yemek kaşığı (20g)"),
        FitnessFoodItem("food_soy_sauce", "Low-Sodium Soy Sauce", "Az Tuzlu Soya Sosu", FoodCategory.FATS_OILS, 53, 8.1, 4.9, 0.6, 15, "1 tbsp (15g)", "1 yemek kaşığı (15g)"),
        FitnessFoodItem("food_balsamic_vinegar", "Balsamic Vinegar", "Balzamik Sirke", FoodCategory.FATS_OILS, 88, 0.5, 17.0, 0.0, 15, "1 tbsp (15g)", "1 yemek kaşığı (15g)"),
        FitnessFoodItem("food_tomato_paste", "Concentrated Tomato Paste", "Domates Salçası", FoodCategory.FATS_OILS, 82, 4.3, 18.9, 0.5, 20, "1 tbsp (20g)", "1 yemek kaşığı (20g)"),
        FitnessFoodItem("food_sesame_oil", "Toasted Sesame Oil", "Susam Yağı", FoodCategory.FATS_OILS, 884, 0.0, 0.0, 100.0, 10, "1 tbsp (10g)", "1 yemek kaşığı (10g)"),
        FitnessFoodItem("food_coconut_flakes", "Unsweetened Shredded Coconut", "Hindistan Cevizi Rendesi", FoodCategory.FATS_OILS, 660, 6.9, 23.7, 64.5, 20, "2 tbsp (20g)", "2 yemek kaşığı (20g)")
    )

    val ALL_FOODS: List<FitnessFoodItem> by lazy {
        FitnessFoodDatabase.PART_1_ITEMS + ITEMS
    }

    fun findById(id: String): FitnessFoodItem? = ALL_FOODS.firstOrNull { it.id == id }

    fun search(
        query: String,
        category: FoodCategory? = null
    ): List<FitnessFoodItem> {
        val base = if (category == null) ALL_FOODS else ALL_FOODS.filter { it.category == category }
        val q = query.trim().lowercase()
        if (q.isEmpty()) return base
        return base.filter { item ->
            item.nameEn.lowercase().contains(q) || item.nameTr.lowercase().contains(q)
        }
    }

    fun dailyProteinTargetGrams(profile: UserProfile): Int =
        (profile.currentWeightKg * 1.8).roundToInt().coerceIn(70, 220)

    fun evaluateDailyNutrition(
        profile: UserProfile,
        consumedKcal: Int,
        consumedProtein: Int,
        langCode: String
    ): MascotNutritionFeedback {
        val targetKcal = profile.dailyCalorieTarget.coerceAtLeast(1200)
        val targetProtein = dailyProteinTargetGrams(profile)
        val isTr = langCode.equals("tr", ignoreCase = true)
        val kcalRatio = consumedKcal.toFloat() / targetKcal.toFloat()
        val proteinRatio = consumedProtein.toFloat() / targetProtein.toFloat()

        return when {
            consumedKcal == 0 -> MascotNutritionFeedback(
                title = if (isTr) "FitChicken Beslenme Takibi" else "FitChicken Fuel Check",
                message = if (isTr) {
                    "Henüz öğün eklemedin şampiyon! Aşağıdan yediğin besini ve gramajını seç, günlük ${targetKcal} kcal ve ${targetProtein}g protein hedefini dolduralım."
                } else {
                    "No meals logged yet, champ! Pick a food below and set its grams to fuel your ${targetKcal} kcal & ${targetProtein}g protein target."
                },
                mood = CoachAnimationMood.IDLE,
                proteinTargetGrams = targetProtein
            )
            proteinRatio >= 0.90f && kcalRatio in 0.75f..1.08f -> MascotNutritionFeedback(
                title = if (isTr) "Mükemmel Makro Dengesi!" else "Macro Sweet Spot Hit!",
                message = if (isTr) {
                    "Şampiyon! ${consumedProtein}g / ${targetProtein}g proteine ve ${consumedKcal} kcal'e ulaştın. Kasların toparlanma ve büyüme için tam yakıtlı!"
                } else {
                    "Boom, champ! You hit ${consumedProtein}g / ${targetProtein}g protein at ${consumedKcal} kcal. Your muscles are primed for recovery and growth!"
                },
                mood = CoachAnimationMood.CHEERING,
                proteinTargetGrams = targetProtein
            )
            kcalRatio > 1.12f -> MascotNutritionFeedback(
                title = if (isTr) "Kalori Hedefi Aşıldı" else "Above Calorie Budget",
                message = if (isTr) {
                    if (profile.isYouthGrowthMode) {
                        "Günlük kalorinin biraz üzerine çıktın (${consumedKcal} kcal). Büyüme çağındasın, kısıtlamak yerine akşam bol sebze ve hafif yürüyüşle dengele!"
                    } else {
                        "Hedefinin biraz üzerindesin (${consumedKcal} / ${targetKcal} kcal). Kalan saatlerde su tüketimini artırıp 20 dakikalık ekstra yürüyüş ekleyebilirsin!"
                    }
                } else {
                    if (profile.isYouthGrowthMode) {
                        "You're slightly over ${targetKcal} kcal today. Keep growing strong—pair dinner with fresh greens and a light walk rather than strict cuts!"
                    } else {
                        "Slightly over your ${targetKcal} kcal budget (${consumedKcal} kcal). Add extra water and a brisk 20-minute walk to balance the day!"
                    }
                },
                mood = CoachAnimationMood.COACHING,
                proteinTargetGrams = targetProtein
            )
            proteinRatio < 0.55f && consumedKcal > 400 -> MascotNutritionFeedback(
                title = if (isTr) "Protein Desteği Zamanı" else "Time to Boost Protein!",
                message = if (isTr) {
                    "Kalorin ${consumedKcal} kcal oldu ama protein şimdilik ${consumedProtein}g. Tavuk göğsü, lor peyniri, yumurta beyazı veya süzme yoğurt ekleyerek ${targetProtein}g hedefine yaklaş!"
                } else {
                    "You're at ${consumedKcal} kcal with ${consumedProtein}g protein so far. Add chicken breast, Greek yogurt, egg whites, or tuna to climb toward ${targetProtein}g!"
                },
                mood = CoachAnimationMood.COACHING,
                proteinTargetGrams = targetProtein
            )
            else -> {
                val remKcal = (targetKcal - consumedKcal).coerceAtLeast(0)
                val remProtein = (targetProtein - consumedProtein).coerceAtLeast(0)
                MascotNutritionFeedback(
                    title = if (isTr) "Harika İlerliyorsun!" else "Great Momentum Today!",
                    message = if (isTr) {
                        "Şu an ${consumedKcal} kcal ve ${consumedProtein}g protein kaydettin. Hedefine kalan: ${remKcal} kcal • ${remProtein}g protein. Aynen devam!"
                    } else {
                        "Logged ${consumedKcal} kcal and ${consumedProtein}g protein so far. Remaining today: ${remKcal} kcal • ${remProtein}g protein. Keep stacking clean meals!"
                    },
                    mood = CoachAnimationMood.CHEERING,
                    proteinTargetGrams = targetProtein
                )
            }
        }
    }
}
