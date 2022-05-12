package com.example.travelplaner.core.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@ProvidedTypeConverter
class TpTypeConverters(private val moshi: Moshi) {

    @TypeConverter
    fun stringToStringList(value: String?): List<String>? {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        if (value == null) return null

        val adapter: JsonAdapter<List<String>> = moshi.adapter(type)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun stringListToString(value: List<String>?): String? {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        if (value == null) return null

        val adapter: JsonAdapter<List<String>> = moshi.adapter(type)
        return adapter.toJson(value)
    }


    @TypeConverter
    fun stringToLongList(value: String?): List<Long>? {
        val type = Types.newParameterizedType(List::class.java, Long::class.javaObjectType)
        if (value == null) return null

        val adapter: JsonAdapter<List<Long>> = moshi.adapter(type)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun longListToString(value: List<Long>?): String? {
        val type = Types.newParameterizedType(List::class.java, Long::class.javaObjectType)
        if (value == null) return null

        val adapter: JsonAdapter<List<Long>> = moshi.adapter(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun stringToIntList(value: String?): List<Int>? {
        val type = Types.newParameterizedType(List::class.java, Int::class.javaObjectType)
        if (value == null) return null

        val adapter: JsonAdapter<List<Int>> = moshi.adapter(type)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun intListToString(value: List<Int>?): String? {
        val type = Types.newParameterizedType(List::class.java, Int::class.javaObjectType)
        if (value == null) return null

        val adapter: JsonAdapter<List<Int>> = moshi.adapter(type)
        return adapter.toJson(value)
    }
}