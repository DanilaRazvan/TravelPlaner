package com.example.travelplaner.core.di

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.example.travelplaner.core.data.db.TpDatabase
import com.example.travelplaner.core.data.db.dao.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Named("tp-database-name")
    fun provideAppDatabaseName(): String {
        return "travelplaner.db"
    }

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        @Named("tp-database-name") name: String,
        moshi: Moshi
    ): TpDatabase {
        val builder = Room.databaseBuilder(context, TpDatabase::class.java, name)
            .fallbackToDestructiveMigration()

        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideCityDao(db: TpDatabase): CityDao = db.cityDao()

    @Provides
    @Singleton
    fun provideAccommodationDao(db: TpDatabase): AccommodationDao = db.accommodationDao()

    @Provides
    @Singleton
    fun provideFlightDao(db: TpDatabase): FlightDao = db.flightDao()

    @Provides
    @Singleton
    fun provideLandmarkDao(db: TpDatabase): LandmarkDao = db.landmarkDao()

    @Provides
    @Singleton
    fun provideTripDao(db: TpDatabase): TripDao = db.tripDao()

    @Provides
    @Singleton
    fun provideTripsLandmarksDao(db: TpDatabase): TripsLandmarksDao = db.tripsLandmarksDao()
}