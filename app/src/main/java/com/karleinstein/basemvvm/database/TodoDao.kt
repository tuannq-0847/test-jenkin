package com.karleinstein.basemvvm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.karleinstein.basemvvm.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY dueDate DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE state = 'IN_PROGRESS' ORDER BY dueDate DESC")
    fun getAllTodosOutgoing(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE state = 'DONE' ORDER BY dueDate DESC")
    fun getAllTodosCompleted(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE state = 'OVERDUE' ORDER BY dueDate DESC")
    fun getAllTodosOverDue(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM todos")
    suspend fun deleteAll()
}
