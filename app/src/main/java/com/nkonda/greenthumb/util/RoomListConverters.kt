package com.nkonda.greenthumb.util

import androidx.room.TypeConverter
import com.nkonda.greenthumb.data.Task
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class StringListConverter {

    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(value: List<String>): String {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)
        return adapter.toJson(value)
    }
}

class TaskListConverter {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromString(value: String): List<Task> {
        val listType = Types.newParameterizedType(List::class.java, Task::class.java)
        val adapter: JsonAdapter<List<Task>> = moshi.adapter(listType)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(value: List<Task>?): String {
        val listType = Types.newParameterizedType(List::class.java, Task::class.java)
        val adapter: JsonAdapter<List<Task>> = moshi.adapter(listType)
        return adapter.toJson(value)
    }
}