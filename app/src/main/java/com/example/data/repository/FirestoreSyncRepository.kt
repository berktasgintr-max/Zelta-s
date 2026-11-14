package com.example.data.repository

import com.example.data.model.CalendarEntryDto
import com.example.data.model.CalendarWorkoutEntry
import com.example.data.model.UserProfile
import com.example.data.model.UserProfileDto
import com.example.data.model.WeightEntry
import com.example.data.model.WeightLogDto
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreSyncRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Firestore erişimi için Google ile giriş yapılmalıdır.")
    }

    suspend fun getUserProfile(): Result<UserProfile?> {
        val uid = requireUserId()
        val path = "users/$uid"
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            if (!snapshot.exists()) {
                Result.success(null)
            } else {
                val dto = snapshot.toObject(
                    UserProfileDto::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                Result.success(dto?.toDomain())
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val existing = docRef.get().await()
            val payload = hashMapOf<String, Any>(
                "userId" to uid,
                "displayName" to profile.displayName.take(50).ifBlank { "Sporcu" },
                "age" to profile.age.coerceIn(5, 120),
                "heightCm" to profile.heightCm.coerceIn(80, 250),
                "startWeightKg" to profile.startWeightKg.coerceIn(20.0, 350.0),
                "currentWeightKg" to profile.currentWeightKg.coerceIn(20.0, 350.0),
                "targetWeightKg" to profile.targetWeightKg.coerceIn(20.0, 350.0),
                "goal" to profile.goal,
                "environment" to profile.environment,
                "activityLevel" to profile.activityLevel,
                "experienceLevel" to profile.experienceLevel,
                "weeklyDays" to profile.weeklyDays.coerceIn(1, 7),
                "sessionMinutes" to profile.sessionMinutes.coerceIn(10, 180),
                "focusArea" to profile.focusArea,
                "planTier" to profile.planTier,
                "proSource" to profile.proSource,
                "proExpiresAtMillis" to profile.proExpiresAtMillis.coerceAtLeast(0L),
                "inviteCode" to profile.inviteCode.take(24),
                "referralCount" to profile.referralCount.coerceIn(0, 1000),
                "referredByCode" to profile.referredByCode.take(24),
                "aiQuestionsUsedToday" to profile.aiQuestionsUsedToday.coerceIn(0, 1000),
                "aiQuotaDate" to profile.aiQuotaDate.take(20),
                "onboardingCompleted" to profile.onboardingCompleted,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (existing.exists()) {
                docRef.update(payload).await()
            } else {
                payload["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeWeightLogs(): Flow<List<WeightEntry>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/weightLogs"
        emitAll(
            db.collection("users").document(uid).collection("weightLogs")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        WeightLogDto::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).map { dto ->
                        WeightEntry(
                            id = dto.id,
                            weightKg = dto.weightKg,
                            note = dto.note,
                            loggedAtMillis = dto.loggedAtMillis
                        )
                    }.sortedBy { it.loggedAtMillis }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun upsertWeightLog(entry: WeightEntry): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("weightLogs").document(entry.id)
        return try {
            val existing = docRef.get().await()
            val payload = hashMapOf<String, Any>(
                "weightKg" to entry.weightKg.coerceIn(20.0, 350.0),
                "note" to entry.note.take(120),
                "loggedAtMillis" to entry.loggedAtMillis.coerceAtLeast(0L),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (existing.exists()) {
                docRef.update(payload).await()
            } else {
                payload["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteWeightLog(logId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("weightLogs").document(logId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeCalendarEntries(): Flow<List<CalendarWorkoutEntry>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/calendarEntries"
        emitAll(
            db.collection("users").document(uid).collection("calendarEntries")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        CalendarEntryDto::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).map { dto ->
                        CalendarWorkoutEntry(
                            id = dto.id,
                            dateKey = dto.dateKey,
                            workoutId = dto.workoutId,
                            workoutTitle = dto.workoutTitle,
                            completed = dto.completed,
                            durationMinutes = dto.durationMinutes
                        )
                    }.sortedBy { it.dateKey }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun upsertCalendarEntry(entry: CalendarWorkoutEntry): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("calendarEntries").document(entry.id)
        return try {
            val existing = docRef.get().await()
            val payload = hashMapOf<String, Any>(
                "dateKey" to entry.dateKey.take(10),
                "workoutId" to entry.workoutId.take(64),
                "workoutTitle" to entry.workoutTitle.take(100),
                "completed" to entry.completed,
                "durationMinutes" to entry.durationMinutes.coerceIn(1, 240),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (existing.exists()) {
                docRef.update(payload).await()
            } else {
                payload["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteCalendarEntry(entryId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("calendarEntries").document(entryId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun ensureReferralCodeClaimed(inviteCode: String): Result<Unit> {
        val uid = requireUserId()
        val cleanCode = inviteCode.trim().uppercase()
        val docRef = db.collection("referralCodes").document(cleanCode)
        return try {
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val payload = mapOf(
                    "ownerUid" to uid,
                    "code" to cleanCode,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun redeemFriendCode(friendCode: String): Result<Unit> {
        val uid = requireUserId()
        val cleanCode = friendCode.trim().uppercase()
        val codeRef = db.collection("referralCodes").document(cleanCode)
        return try {
            val codeSnap = codeRef.get().await()
            if (!codeSnap.exists()) {
                return Result.failure(IllegalArgumentException("Davet kodu bulunamadı: $cleanCode"))
            }
            val ownerUid = codeSnap.getString("ownerUid")
                ?: return Result.failure(IllegalStateException("Davet kodu sahibi geçersiz."))
            if (ownerUid == uid) {
                return Result.failure(IllegalArgumentException("Kendi davet kodunu kullanamazsın."))
            }
            val redemptionRef = codeRef.collection("redemptions").document(uid)
            val payload = mapOf(
                "code" to cleanCode,
                "codeOwnerUid" to ownerUid,
                "redeemerUid" to uid,
                "createdAt" to FieldValue.serverTimestamp()
            )
            redemptionRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "referralCodes/$cleanCode/redemptions/$uid")
            Result.failure(e)
        }
    }

    suspend fun fetchReferralCount(inviteCode: String): Result<Int> {
        val uid = requireUserId()
        val cleanCode = inviteCode.trim().uppercase()
        val path = "referralCodes/$cleanCode/redemptions"
        return try {
            val snap = db.collection("referralCodes").document(cleanCode)
                .collection("redemptions")
                .whereEqualTo("codeOwnerUid", uid)
                .get()
                .await()
            Result.success(snap.size())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    private fun UserProfileDto.toDomain(): UserProfile {
        return UserProfile(
            displayName = displayName,
            age = age,
            heightCm = heightCm,
            startWeightKg = startWeightKg,
            currentWeightKg = currentWeightKg,
            targetWeightKg = targetWeightKg,
            goal = goal,
            environment = environment,
            activityLevel = activityLevel,
            experienceLevel = experienceLevel,
            weeklyDays = weeklyDays,
            sessionMinutes = sessionMinutes,
            focusArea = focusArea,
            planTier = planTier,
            proSource = proSource,
            proExpiresAtMillis = proExpiresAtMillis,
            inviteCode = inviteCode,
            referralCount = referralCount,
            referredByCode = referredByCode,
            aiQuestionsUsedToday = aiQuestionsUsedToday,
            aiQuotaDate = aiQuotaDate,
            onboardingCompleted = onboardingCompleted
        )
    }
}
