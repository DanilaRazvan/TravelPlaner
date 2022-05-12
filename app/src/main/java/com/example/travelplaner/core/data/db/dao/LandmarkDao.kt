package com.example.travelplaner.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.travelplaner.core.data.db.Landmark

@Dao
abstract class LandmarkDao {

    @Insert
    abstract suspend fun insert(landmark: Landmark)

    @Update
    abstract suspend fun update(landmark: Landmark)

    @Query("""
        select * from  landmarks
        where landmark_id == :id
    """)
    abstract suspend fun readById(id: Long): Landmark

    @Query("""
        delete from landmarks
        where landmark_id == :id
    """)
    abstract suspend fun deleteById(id: Long)
}