package com.example.travelplaner.core.data.db.dao

import androidx.room.*
import com.example.travelplaner.core.data.db.City
import com.example.travelplaner.core.data.db.CityWithLandmarks
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CityDao {

    @Insert
    abstract fun insert(city: City)

    @Update
    abstract fun update(city: City)

    @Query(
        """
        delete from cities
    """
    )
    abstract fun deleteAll()

    @Query(
        """
        delete from cities
        where city_id == :id
    """
    )
    abstract fun deleteById(id: Long)

    @Query(
        """
           select * from cities 
        """
    )
    abstract fun readAllFlow(): Flow<List<City>>

    @Query(
        """
           select * from cities
           where city_id == :id
        """
    )
    abstract fun readById(id: Long): City

    @Transaction
    @Query(
        """
           select * from cities
           where city_id == :id
        """
    )
    abstract fun readWithLandmarksById(id: Long): Flow<CityWithLandmarks>
}