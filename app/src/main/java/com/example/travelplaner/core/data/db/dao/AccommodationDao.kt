package com.example.travelplaner.core.data.db.dao

import androidx.room.*
import com.example.travelplaner.core.data.db.Accommodation
import com.example.travelplaner.core.data.db.AccommodationWithCity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccommodationDao {

    @Insert
    abstract fun insert(accommodation: Accommodation)

    @Update
    abstract fun update(accommodation: Accommodation)

    @Query("""
        delete from accommodations
    """)
    abstract fun deleteAll()

    @Query("""
        delete from accommodations
        where accommodation_id == :id
    """)
    abstract fun deleteById(id: Long)

    @Query("""
        delete from accommodations
        where city_id == :id
    """)
    abstract fun deleteByCityId(id: Long)

    @Transaction
    @Query(
        """
           select * from accommodations
        """
    )
    abstract fun readAllFlow(): Flow<List<AccommodationWithCity>>
}