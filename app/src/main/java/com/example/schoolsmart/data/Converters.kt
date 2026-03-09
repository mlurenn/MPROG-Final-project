package com.example.schoolsmart.data

import androidx.room.TypeConverter

// Converts emuns and lists to strings to store in database
class Converters {

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus):
            String = value.name

    @TypeConverter
    fun toTaskStatus(value: String):
            TaskStatus = enumValueOf(value)

    @TypeConverter
    fun fromTaskCategory(value: TaskCategory):
            String = value.name

    @TypeConverter
    fun toTaskCategory(value: String):
            TaskCategory = TaskCategory.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>):
            String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String):
            List<String> = value.split(",")
}