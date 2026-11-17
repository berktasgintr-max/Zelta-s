package com.example.data.repository

import android.content.Context
import com.example.data.catalog.CoachKnowledgeBase
import com.example.data.model.UserProfile

/**
 * 100% local, offline, zero-latency FitChicken Coach engine.
 * Scans user messages for fitness, nutrition, posture, recovery, and motivation keywords
 * and crafts personalized responses using the user's profile metrics.
 */
class AiCoachRepository(@Suppress("unused") private val appContext: Context) {

    fun getRuleBasedAdvice(question: String, profile: UserProfile): String {
        return CoachKnowledgeBase.generateLocalReply(question, profile)
    }
}
