package com.example.travelplaner.core.data.db.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.travelplaner.core.data.db.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TripsLandmarksDao {

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(crossRef: TripLandmarkCrossRef)
}