package com.example.travelplaner.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.travelplaner.core.data.db.dao.*

@Database(
    entities = [
        User::class,
        City::class,
        Flight::class,
        Accommodation::class,
        Landmark::class,
        Trip::class,
        TripLandmarkCrossRef::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TpTypeConverters::class)
abstract class TpDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun flightDao(): FlightDao
    abstract fun accommodationDao(): AccommodationDao
    abstract fun landmarkDao(): LandmarkDao
    abstract fun tripDao(): TripDao
    abstract fun tripsLandmarksDao(): TripsLandmarksDao
    abstract fun userDao(): UserDao
}