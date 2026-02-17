package com.karleinstein.basemvvm.usecase

import com.karleinstein.basemvvm.database.TodoDao
import com.karleinstein.basemvvm.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface GetTodoListUseCase {

    fun getTodoList(): Flow<List<Todo>>

    fun getAllTodosOutgoing(): Flow<List<Todo>>
    fun getAllTodosCompleted(): Flow<List<Todo>>
    fun getAllTodosOverDue(): Flow<List<Todo>>

    fun insertTodo(todo: Todo): Flow<Unit>
}

class GetTodoListUseCaseImpl(private val todoDao: TodoDao) : GetTodoListUseCase {

    override fun getTodoList(): Flow<List<Todo>> {
        return todoDao.getAllTodos()
    }

    override fun getAllTodosOutgoing(): Flow<List<Todo>> {
        return todoDao.getAllTodosOutgoing()
    }

    override fun getAllTodosCompleted(): Flow<List<Todo>> {
        return todoDao.getAllTodosCompleted()
    }

    override fun getAllTodosOverDue(): Flow<List<Todo>> {
        return todoDao.getAllTodosOverDue()
    }

    override fun insertTodo(todo: Todo): Flow<Unit> {
        return flow {
            todoDao.insert(todo)
            emit(Unit)
        }
    }
}
