package com.example.travelplaner.core.data.db.dao

import androidx.room.*
import com.example.travelplaner.core.data.db.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TripDao {

    @Insert
    abstract suspend fun insert(trip: Trip): Long

    @Update
    abstract suspend fun update(trip: Trip)

    @Query("""
        delete from trips
    """)
    abstract suspend fun deleteAll()

    @Query("""
        delete from trips
        where trip_id == :id
    """)
    abstract suspend fun deleteById(id: Long)

    @Query(
        """
           select * from trips 
        """
    )
    abstract suspend fun readAll(): List<Trip>

    @Query(
        """
           select * from trips 
        """
    )
    abstract fun readAllFlow(): Flow<List<Trip>>

    @Query(
        """
           select * from trips
           where trip_id == :id
        """
    )
    abstract suspend fun readById(id: Long): Trip

    @Transaction
    @Query(
        """
           select * from trips
           where trip_id == :id
        """
    )
    abstract suspend fun readWithLandmarksById(id: Long): TripWithLandmarks
}