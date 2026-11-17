package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.CalendarWorkoutEntry
import com.example.data.model.UserProfile
import com.example.data.model.WeightEntry
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import java.util.UUID

@Ignore("Requires local Firebase Auth/Firestore emulators running")
class FirestoreSyncRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndReadUserProfile_authenticatedOwner_succeeds() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repo = FirestoreSyncRepository(firestore, auth)
        val profile = UserProfile(
            displayName = "Ahmet",
            age = 17,
            heightCm = 178,
            startWeightKg = 68.0,
            currentWeightKg = 68.0,
            targetWeightKg = 72.0,
            goal = "growth_posture",
            environment = "home",
            inviteCode = "FIT-${UUID.randomUUID().toString().take(5).uppercase()}",
            onboardingCompleted = true
        )

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) { repo.saveUserProfile(profile) }
        assertTrue(saveResult.isSuccess)

        val loaded = withTimeout(DEFAULT_TIMEOUT_MS) { repo.getUserProfile().getOrThrow() }
        assertEquals("Ahmet", loaded?.displayName)
        assertEquals(17, loaded?.age)
    }

    @Test
    fun weightLogs_and_calendarEntries_emitRealtimeUpdates() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repo = FirestoreSyncRepository(firestore, auth)

        val logId = "log_${UUID.randomUUID().toString().take(8)}"
        val calId = "cal_${UUID.randomUUID().toString().take(8)}"

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.upsertWeightLog(WeightEntry(logId, 69.5, "Sabah", 1700000000000L)).getOrThrow()
            repo.upsertCalendarEntry(
                CalendarWorkoutEntry(calId, "2026-11-14", "youth_1", "Boy & Omurga Uzatma", false, 25)
            ).getOrThrow()
        }

        val logs = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeWeightLogs().first { list -> list.any { it.id == logId } }
        }
        assertTrue(logs.any { it.id == logId && it.weightKg == 69.5 })

        val entries = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeCalendarEntries().first { list -> list.any { it.id == calId } }
        }
        assertTrue(entries.any { it.id == calId && it.workoutId == "youth_1" })
    }

    @Test
    fun observeWeightLogs_unauthenticated_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repo = FirestoreSyncRepository(firestore, auth)

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) { repo.observeWeightLogs().first() }
        } catch (e: Throwable) {
            caught = e
        }
        assertTrue(caught is IllegalStateException || caught?.unwrapFirestoreException()?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
    }

    @Test
    fun referralFlow_crossUserRedemption_succeeds() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = FirestoreSyncRepository(firestore, auth)
        val code = "FIT-${UUID.randomUUID().toString().take(6).uppercase()}"

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.ensureReferralCodeClaimed(code).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = FirestoreSyncRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.redeemFriendCode(code).getOrThrow()
        }

        signInTestUser(ALICE_EMAIL)
        val count = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.fetchReferralCount(code).getOrThrow()
        }
        assertEquals(1, count)
        assertTrue(aliceUid.isNotBlank())
    }

    private companion object {
        const val ALICE_EMAIL = "alice_tavuk@test.com"
        const val BOB_EMAIL = "bob_tavuk@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
