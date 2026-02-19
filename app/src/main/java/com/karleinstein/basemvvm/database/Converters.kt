package com.karleinstein.basemvvm.database

import androidx.room.TypeConverter
import com.karleinstein.basemvvm.model.StateTodo
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromStateTodo(value: String): StateTodo {
        return when (value) {
            "DONE" -> StateTodo.StateDone
            "IN_PROGRESS" -> StateTodo.StateInProgress
            "OVERDUE" -> StateTodo.StateOverdue
            else -> StateTodo.StateInProgress
        }
    }

    @TypeConverter
    fun stateTodoToString(state: StateTodo): String {
        return when (state) {
            is StateTodo.StateDone -> "DONE"
            is StateTodo.StateInProgress -> "IN_PROGRESS"
            is StateTodo.StateOverdue -> "OVERDUE"
        }
    }
}
