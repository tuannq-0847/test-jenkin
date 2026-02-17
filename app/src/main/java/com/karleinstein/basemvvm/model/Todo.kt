package com.karleinstein.basemvvm.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val state: StateTodo = StateTodo.StateInProgress,
    val dueDate: LocalDate? = null
) : Parcelable{

    fun isOverDue(): Boolean =
        dueDate?.isBefore(LocalDate.now()) ?: false

}

@Parcelize
sealed class StateTodo : Parcelable {
    data object StateDone : StateTodo()
    data object StateInProgress : StateTodo()
    data object StateOverdue : StateTodo()
}
