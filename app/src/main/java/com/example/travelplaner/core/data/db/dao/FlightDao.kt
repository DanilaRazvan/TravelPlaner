package com.example.travelplaner.core.data.db.dao

import androidx.room.*
import com.example.travelplaner.core.data.db.AccommodationWithCity
import com.example.travelplaner.core.data.db.Flight
import com.example.travelplaner.core.data.db.FlightWithCity
import com.example.travelplaner.core.data.db.Landmark
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FlightDao {

    @Insert
    abstract fun insert(flight: Flight)

    @Update
    abstract fun update(flight: Flight)

    @Query("""
        delete from flights
    """)
    abstract fun deleteAll()

    @Query("""
        delete from flights
        where flight_id == :id
    """)
    abstract fun deleteById(id: Long)
    @Query("""
        delete from flights
        where to_city_id == :id
    """)
    abstract fun deleteByCityId(id: Long)

    @Transaction
    @Query("""
        select * from flights
        where is_favorite == :isFavorite
    """)
    abstract fun readAllByFavoriteFlow(isFavorite: Boolean): Flow<List<FlightWithCity>>

    @Transaction
    @Query(
        """
           select * from flights 
        """
    )
    abstract fun readAllFlow(): Flow<List<FlightWithCity>>

    @Transaction
    @Query("""
        select * from  flights
        where flight_id == :id
    """)
    abstract suspend fun readById(id: Long): FlightWithCity

    @Transaction
    @Query("""
        select * from  flights
        where flight_id == :id
    """)
    abstract fun readByIdFlow(id: Long): Flow<FlightWithCity>
}