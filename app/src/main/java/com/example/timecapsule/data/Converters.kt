package com.example.timecapsule.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromTags(tags: String?): List<String>? =
        tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }

    @TypeConverter
    fun tagsToString(tags: List<String>?): String? =
        tags?.filter { it.isNotEmpty() }?.joinToString(",")
}
