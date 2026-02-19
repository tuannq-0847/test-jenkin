package com.karleinstein.basemvvm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karleinstein.basemvvm.model.StateTodo
import com.karleinstein.basemvvm.model.Todo
import com.karleinstein.basemvvm.usecase.GetTodoListUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

private val defaultTodos: List<Todo>
    get() {
        val twoDaysAgo = LocalDate.now().minusDays(2)
        val yesterday = LocalDate.now().minusDays(1)
        val tomorrow = LocalDate.now().plusDays(1)
        val twoDaysFromNow = LocalDate.now().plusDays(2)

        return listOf(
            Todo(1L, "Submit project proposal", "",StateTodo.StateInProgress, tomorrow),
            Todo(2L, "Review PR from John", "",StateTodo.StateDone, yesterday),
            Todo(2L, "Review PR from Tuna", "",StateTodo.StateDone, yesterday),
            Todo(2L, "Review PR from Karl", "",StateTodo.StateDone, yesterday),
            Todo(2L, "Review PR from Einstein", "",StateTodo.StateDone, yesterday),
            Todo(3L, "Pay electricity bill", "",StateTodo.StateInProgress, twoDaysAgo),
            Todo(3L, "Pay house bill", "",StateTodo.StateInProgress, twoDaysAgo),
            Todo(3L, "Pay watere bill", "",StateTodo.StateInProgress, twoDaysAgo),
            Todo(4L, "Plan weekend trip", "",StateTodo.StateInProgress, twoDaysFromNow),
        )
    }

data class GroupedTodos(
    val overdue: List<Todo> = emptyList(),
    val ongoing: List<Todo> = emptyList(),
    val completed: List<Todo> = emptyList()
)

class TodoHomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getTodoListUseCase: GetTodoListUseCase
) : ViewModel() {
    private val _isSaveTodoDone = MutableSharedFlow<Todo>()
    val isSaveTodoDone: SharedFlow<Todo> = _isSaveTodoDone.asSharedFlow()

    private val _groupedTodos = MutableStateFlow(GroupedTodos())
    val groupedTodos: StateFlow<GroupedTodos> = _groupedTodos.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getTodoListUseCase.getAllTodosCompleted(),
                getTodoListUseCase.getOngoingTasks(LocalDate.now()),
                getTodoListUseCase.getOverdueTasks(LocalDate.now())
            ) { completed, outgoing, overdue ->
                GroupedTodos(
                    completed = completed,
                    ongoing = outgoing,
                    overdue = overdue
                )
            }.collect { grouped ->
                _groupedTodos.value = grouped
            }
        }
    }

    fun saveTodo(todo: Todo) {
        viewModelScope.launch {
            getTodoListUseCase.insertTodo(todo)
                .collect {
                    _isSaveTodoDone.emit(todo)
                }
        }
    }

    // Store selected date and time
    private var _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    private var _selectedHour = MutableStateFlow(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
    private var _selectedMinute = MutableStateFlow(Calendar.getInstance().get(Calendar.MINUTE))

    // Call these from AddTaskScreen when user picks date/time
    fun updateSelectedDate(dateMillis: Long) {
        _selectedDateMillis.value = dateMillis
    }

    fun updateSelectedTime(hour: Int, minute: Int) {
        _selectedHour.value = hour
        _selectedMinute.value = minute
    }

    // Combines date + time into single millis
    fun getSelectedDateTimeMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _selectedDateMillis.value
            set(Calendar.HOUR_OF_DAY, _selectedHour.value)
            set(Calendar.MINUTE, _selectedMinute.value)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }


    companion object {
        private const val KEY_TODOS = "todos"
    }
}
