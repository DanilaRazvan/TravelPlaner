package com.example.travelplaner.core.data.db

import androidx.room.*
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id") val id: Long = 0L,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String
)

@Serializable
@Entity(tableName = "cities")
data class City(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "city_id") val id: Long = 0L,
    @ColumnInfo(name = "city_name") val name: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "city_photo_url") val photoUrl: String,
    @ColumnInfo(name = "city_description") val description: String,
)

@Serializable
@Entity(tableName = "flights")
data class Flight(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "flight_id") val id: Long = 0L,
    @ColumnInfo(name = "ticket_price") val ticketPrice: String,
    @ColumnInfo(name = "duration") val duration: String,
    @ColumnInfo(name = "to_city_id") val toCityId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "from") val from: Long,
    @ColumnInfo(name = "to") val to: Long,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
)

@Serializable
@Entity(tableName = "accommodations")
data class Accommodation(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "accommodation_id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "city_id") val cityId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "accommodation_photo_url") val photoUrl: String,
    @ColumnInfo(name = "from") val from: Long,
    @ColumnInfo(name = "to") val to: Long,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "additional_photos") val additionalPhotos: List<String>
)

@Serializable
@Entity(tableName = "landmarks")
data class Landmark(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "landmark_id") val id: Long = 0L,
    @ColumnInfo(name = "city_id") val cityId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "ticket_price") val ticketPrice: Float,
    @ColumnInfo(name = "landmark_description") val description: String,
    @ColumnInfo(name = "landmark_photo_url") val photoUrl: String,
    @ColumnInfo(name = "from") val from: String,
    @ColumnInfo(name = "until") val until: String,
    @ColumnInfo(name = "additional_photos") val additionalPhotos: List<String>
)

@Serializable
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "trip_id") val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "city_id") val cityId: Long,
    @ColumnInfo(name = "total_price") val totalPrice: Float,
)

@Serializable
@Entity(tableName = "trips_landmarks", primaryKeys = ["trip_id", "landmark_id"])
data class TripLandmarkCrossRef(
    @ColumnInfo(name = "trip_id") val tripId: Long,
    @ColumnInfo(name = "landmark_id") val landmarkId: Long
)

@Serializable
data class FlightWithCity(
    @Embedded val flight: Flight,
    @Relation(
        parentColumn = "to_city_id",
        entityColumn = "city_id"
    )
    val city: City
)

@Serializable
data class AccommodationWithCity(
    @Embedded val accommodation: Accommodation,
    @Relation(
        parentColumn = "city_id",
        entityColumn = "city_id"
    )
    val city: City
)

@Serializable
data class CityWithLandmarks(
    @Embedded val city: City,
    @Relation(
        parentColumn = "city_id",
        entityColumn = "city_id"
    )
    val landmarks: List<Landmark>
)

@Serializable
data class TripWithLandmarks(
    @Embedded val trip: Trip,
    @Relation(
        parentColumn = "trip_id",
        entityColumn = "landmark_id",
        associateBy = Junction(TripLandmarkCrossRef::class)
    )
    val landmarks: List<Landmark>
)