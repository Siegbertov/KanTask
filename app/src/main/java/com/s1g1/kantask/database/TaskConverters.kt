package com.s1g1.kantask.database

import androidx.room.TypeConverter
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class TaskConverters {

    /* LocalDate */
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? =  date?.toEpochDay()
    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? = value?.let{LocalDate.ofEpochDay(it)}

    /* LocalTime */
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): Long? = time?.toSecondOfDay()?.toLong()
    @TypeConverter
    fun toLocalTime(value: Long?): LocalTime? = value?.let{LocalTime.ofSecondOfDay(it)}

    /* Duration */
    @TypeConverter
    fun fromDuration(duration: Duration?): Long? = duration?.toMinutes()
    @TypeConverter
    fun toDuration(value: Long?): Duration? = value?.let{Duration.ofMinutes(it)}

}