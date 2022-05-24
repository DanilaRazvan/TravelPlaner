package com.example.travelplaner.core.data.db

import androidx.room.*

@Entity(tableName = "cities")
data class City(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "city_id") val id: Long = 0L,
    @ColumnInfo(name = "city_name") val name: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "city_photo_url") val photoUrl: String,
    @ColumnInfo(name = "city_description") val description: String,
)

@Entity(tableName = "flights")
data class Flight(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "flight_id") val id: Long = 0L,
    @ColumnInfo(name = "ticket_price") val ticketPrice: String,
    @ColumnInfo(name = "duration") val duration: String,
    @ColumnInfo(name = "to_city_id") val toCityId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "from") val from: Long,
    @ColumnInfo(name = "to") val to: Long,
)

@Entity(tableName = "accommodations")
data class Accommodation(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "accommodation_id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "accommodation_photo_url") val photoUrl: String,
    @ColumnInfo(name = "city_id") val cityId: Long,
    @ColumnInfo(name = "from") val from: Long,
    @ColumnInfo(name = "to") val to: Long,
)

@Entity(tableName = "landmarks")
data class Landmark(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "landmark_id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "ticket_price") val ticketPrice: Float,
    @ColumnInfo(name = "landmark_description") val description: String,
    @ColumnInfo(name = "landmark_photo_url") val photoUrl: String,
    @ColumnInfo(name = "from") val from: String,
    @ColumnInfo(name = "until") val until: String,
    @ColumnInfo(name = "city_id") val cityId: Long,
)

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "trip_id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "city_id") val cityId: Long,
    @ColumnInfo(name = "total_price") val totalPrice: Float,
)

@Entity(tableName = "trips_landmarks", primaryKeys = ["trip_id", "landmark_id"])
data class TripLandmarkCrossRef(
    @ColumnInfo(name = "trip_id") val tripId: Long,
    @ColumnInfo(name = "landmark_id") val landmarkId: Long
)

data class FlightWithCity(
    @Embedded val flight: Flight,
    @Relation(
        parentColumn = "to_city_id",
        entityColumn = "city_id"
    )
    val city: City
)

data class AccommodationWithCity(
    @Embedded val accommodation: Accommodation,
    @Relation(
        parentColumn = "city_id",
        entityColumn = "city_id"
    )
    val city: City
)

data class CityWithLandmarks(
    @Embedded val city: City,
    @Relation(
        parentColumn = "city_id",
        entityColumn = "city_id"
    )
    val landmarks: List<Landmark>
)

data class TripWithLandmarks(
    @Embedded val trip: Trip,
    @Relation(
        parentColumn = "trip_id",
        entityColumn = "landmark_id",
        associateBy = Junction(TripLandmarkCrossRef::class)
    )
    val landmarks: List<Landmark>
)