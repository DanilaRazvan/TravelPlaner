package com.example.travelplaner.core.data.db.dao

import androidx.room.*
import com.example.travelplaner.core.data.db.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao {

    @Insert
    abstract suspend fun insert(user: User): Long

    @Query("""
        delete from users
        where user_id == :id
    """)
    abstract suspend fun deleteById(id: Long)

    @Query("""
        select * from users
        where username == :username
    """)
    abstract suspend fun readByUsername(username: String): User?
}