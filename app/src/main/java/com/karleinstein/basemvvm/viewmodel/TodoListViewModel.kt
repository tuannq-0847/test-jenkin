package com.karleinstein.basemvvm.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.karleinstein.basemvvm.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

private val defaultTodos = listOf(
    Todo(1L, "abc", false),
    Todo(2L, "abc1", false),
    Todo(3L, "abc2", false),
    Todo(4L, "abc3", false)
)

class TodoListViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _todos = MutableStateFlow(
        (savedStateHandle[KEY_TODOS] as? List<Todo>) ?: defaultTodos
    )
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    var text = mutableStateOf(
        savedStateHandle.get<String>(KEY_TEXT) ?: ""
    )

    private fun saveTodos() {
        savedStateHandle[KEY_TODOS] = ArrayList(_todos.value)
    }

    private fun saveText() {
        savedStateHandle[KEY_TEXT] = text.value
    }

    fun addTodo(content: String) {
        _todos.value += Todo(Random.nextLong(), content)
        saveTodos()
    }

    fun removeTodo() {
        // implement when needed
    }

    fun updateTodoList(todo: Todo) {
        _todos.value = _todos.value.map { if (it.id == todo.id) todo else it }
        saveTodos()
    }

    fun updateText(value: String) {
        text.value = value
        saveText()
    }

    companion object {
        private const val KEY_TODOS = "todos"
        private const val KEY_TEXT = "text"
    }
}
