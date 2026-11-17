package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey val id: String,
    val weightKg: Double,
    val note: String,
    val loggedAtMillis: Long
)

@Entity(tableName = "calendar_entries")
data class CalendarEntryEntity(
    @PrimaryKey val id: String,
    val dateKey: String,
    val workoutId: String,
    val workoutTitle: String,
    val completed: Boolean,
    val durationMinutes: Int
)

@Entity(tableName = "ad_unlocks")
data class AdUnlockEntity(
    @PrimaryKey val workoutId: String,
    val adsWatched: Int,
    val unlocked: Boolean
)

@Entity(tableName = "meal_calories")
data class MealCalorieEntity(
    @PrimaryKey val id: String,
    val mealName: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val portionSummary: String,
    val coachNote: String,
    val loggedAtMillis: Long
)

@Entity(tableName = "custom_workouts")
data class CustomWorkoutEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryName: String,
    val focusArea: String,
    val difficulty: String,
    val durationMinutes: Int,
    val estimatedCalories: Int,
    val exercisesJson: String,
    val heroImageUrl: String,
    val imagePrompt: String,
    val createdAtMillis: Long
)

@Dao
interface TavukKocDao {
    @Query("SELECT * FROM weight_logs ORDER BY loggedAtMillis ASC")
    fun observeWeightLogs(): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeightLog(entry: WeightLogEntity)

    @Query("DELETE FROM weight_logs WHERE id = :id")
    suspend fun deleteWeightLog(id: String)

    @Query("SELECT * FROM calendar_entries ORDER BY dateKey ASC")
    fun observeCalendarEntries(): Flow<List<CalendarEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCalendarEntry(entry: CalendarEntryEntity)

    @Query("DELETE FROM calendar_entries WHERE id = :id")
    suspend fun deleteCalendarEntry(id: String)

    @Query("SELECT * FROM ad_unlocks")
    fun observeAdUnlocks(): Flow<List<AdUnlockEntity>>

    @Query("SELECT * FROM ad_unlocks WHERE workoutId = :workoutId LIMIT 1")
    suspend fun getAdUnlock(workoutId: String): AdUnlockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAdUnlock(entity: AdUnlockEntity)

    @Query("SELECT * FROM meal_calories ORDER BY loggedAtMillis DESC")
    fun observeMealCalories(): Flow<List<MealCalorieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMealCalorie(entry: MealCalorieEntity)

    @Query("DELETE FROM meal_calories WHERE id = :id")
    suspend fun deleteMealCalorie(id: String)

    @Query("SELECT * FROM custom_workouts ORDER BY createdAtMillis DESC")
    fun observeCustomWorkouts(): Flow<List<CustomWorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomWorkout(entity: CustomWorkoutEntity)

    @Query("DELETE FROM custom_workouts WHERE id = :id")
    suspend fun deleteCustomWorkout(id: String)
}

@Database(
    entities = [
        WeightLogEntity::class,
        CalendarEntryEntity::class,
        AdUnlockEntity::class,
        MealCalorieEntity::class,
        CustomWorkoutEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class TavukKocDatabase : RoomDatabase() {
    abstract fun dao(): TavukKocDao

    companion object {
        @Volatile
        private var INSTANCE: TavukKocDatabase? = null

        fun getInstance(context: Context): TavukKocDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TavukKocDatabase::class.java,
                    "tavuk_koc_local.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
