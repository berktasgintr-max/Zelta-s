package com.example.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiCoachRepository"

data class MealCalorieAnalysis(
    val mealName: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val portionSummary: String,
    val coachNote: String
)

class AiCoachRepository {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "Sen 'Koç Tavuk' adında enerjik, esprili, motive edici ve spor biliminden anlayan kaslı bir fitness koçusun. " +
                        "Kullanıcının profilindeki dil tercihine göre (appLanguage='en' ise akıcı İngilizce, 'tr' ise akıcı ve yazım hatasız Türkçe) yanıt ver. " +
                        "Cevapların net, uygulanabilir, bilimsel ve dozunda tavuk/kanat mizahıyla süslü olsun. " +
                        "Eğer kullanıcı 0-18 yaş arasındaysa büyüme plaklarını koruyan, aşırı maksimum ağırlıklardan kaçınan, duruş, esneklik, uyku ve beslenme odaklı güvenli tavsiyeler ver. " +
                        "Yanıtları düzenli maddeler halinde ve kısa-orta uzunlukta tut."
                )
            }
        )
    }

    private val mealVisionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = Schema.obj(
                    mapOf(
                        "mealName" to Schema.string("Short appetizing name of the detected meal"),
                        "calories" to Schema.integer("Estimated total kcal for the visible portion"),
                        "proteinGrams" to Schema.integer("Estimated grams of protein"),
                        "carbsGrams" to Schema.integer("Estimated grams of carbohydrates"),
                        "fatGrams" to Schema.integer("Estimated grams of fat"),
                        "portionSummary" to Schema.string("Brief breakdown of visible ingredients and portion sizes"),
                        "coachNote" to Schema.string("1-2 motivating sentences from Koç Tavuk evaluating this meal for the user's goal")
                    )
                )
            }
        )
    }

    fun streamAdvice(question: String, profile: UserProfile): Flow<String> = flow {
        val isEn = profile.appLanguage == "en"
        val contextPrompt = buildString {
            appendLine("Response Language: ${if (isEn) "English" else "Turkish (Türkçe)"}")
            appendLine("User Profile:")
            appendLine("- Name: ${profile.displayName}, Age: ${profile.age} (${if (profile.isYouthGrowthMode) "Ages 0-18 Growth Phase" else "Adult"}), Gender: ${profile.gender}")
            appendLine("- Height: ${profile.heightCm} cm, Current Weight: ${profile.currentWeightKg} kg, Target Weight: ${profile.targetWeightKg} kg")
            appendLine("- Goal: ${profile.goal}, Environment: ${profile.environment}, Weekly Days: ${profile.weeklyDays}")
            appendLine("- Plan: ${if (profile.isProActive) "PRO Plan" else "Free Plan"}")
            appendLine()
            appendLine("User Question: $question")
        }

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                model.generateContentStream(contextPrompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException(
                    if (isEn) "Koç Tavuk could not generate a reply right now, please try again."
                    else "Koç Tavuk şu an yanıt üretemedi, lütfen tekrar dene."
                )
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI generation timed out", e)
            throw IllegalStateException(
                if (isEn) "Connection timed out (60s). Please try again."
                else "Bağlantı zaman aşımına uğradı (60 sn). Lütfen tekrar dene.",
                e
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI generation failed", e)
            throw e
        }
    }

    suspend fun analyzeMealPhoto(bitmap: Bitmap, profile: UserProfile): Result<MealCalorieAnalysis> {
        val isEn = profile.appLanguage == "en"
        return try {
            val prompt = buildString {
                appendLine("Analyze this meal photo as Koç Tavuk, an expert sports nutritionist.")
                appendLine("Output ALL text fields strictly in: ${if (isEn) "English" else "flawless Turkish (Türkçe)"}.")
                appendLine("User Goal: ${profile.goal}, Current Weight: ${profile.currentWeightKg} kg, Target Weight: ${profile.targetWeightKg} kg.")
                if (profile.isYouthGrowthMode) {
                    appendLine("User is ${profile.age} years old (Ages 0-18 growth phase) — never suggest extreme calorie restriction.")
                }
                appendLine("If the image does not clearly show food or drink, set calories to 0, mealName to '${if (isEn) "No food detected" else "Yiyecek algılanamadı"}', and explain politely in coachNote.")
            }
            val response = withTimeout(60_000L) {
                mealVisionModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
            }
            val rawJson = response.text?.trim().orEmpty()
            if (rawJson.isEmpty()) {
                return Result.failure(
                    IllegalStateException(
                        if (isEn) "Could not analyze the meal photo. Please try a clearer photo."
                        else "Öğün fotoğrafı analiz edilemedi. Lütfen daha net bir fotoğraf dene."
                    )
                )
            }
            val obj = JSONObject(rawJson)
            Result.success(
                MealCalorieAnalysis(
                    mealName = obj.optString("mealName", if (isEn) "Analyzed Meal" else "Analiz Edilen Öğün"),
                    calories = obj.optInt("calories", 0).coerceAtLeast(0),
                    proteinGrams = obj.optInt("proteinGrams", 0).coerceAtLeast(0),
                    carbsGrams = obj.optInt("carbsGrams", 0).coerceAtLeast(0),
                    fatGrams = obj.optInt("fatGrams", 0).coerceAtLeast(0),
                    portionSummary = obj.optString("portionSummary", ""),
                    coachNote = obj.optString("coachNote", "")
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Meal photo analysis timed out", e)
            Result.failure(
                IllegalStateException(
                    if (isEn) "Analysis timed out (60s). Please try again."
                    else "Analiz zaman aşımına uğradı (60 sn). Lütfen tekrar dene.",
                    e
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Meal photo analysis failed", e)
            Result.failure(e)
        }
    }
}
