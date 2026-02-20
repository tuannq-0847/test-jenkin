package com.karleinstein.basemvvm.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.karleinstein.basemvvm.model.StateTodo
import com.karleinstein.basemvvm.model.Todo
import com.karleinstein.basemvvm.usecase.GetTodoListUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.Calendar
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TodoHomeViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init collects flows and exposes groupedTodos`() = runTest {
        val useCase = mockk<GetTodoListUseCase>()

        val completed = listOf(
            Todo(id = 10, title = "c", description = "", state = StateTodo.StateDone, dueDate = LocalDate.now())
        )
        val ongoing = listOf(
            Todo(id = 11, title = "o", description = "", state = StateTodo.StateInProgress, dueDate = LocalDate.now())
        )
        val overdue = listOf(
            Todo(id = 12, title = "od", description = "", state = StateTodo.StateInProgress, dueDate = LocalDate.now().minusDays(1))
        )

        every { useCase.getAllTodosCompleted() } returns flowOf(completed)
        every { useCase.getOngoingTasks(any()) } returns flowOf(ongoing)
        every { useCase.getOverdueTasks(any()) } returns flowOf(overdue)
        every { useCase.getAllTodosOutgoing() } returns flowOf(emptyList())
        every { useCase.getAllTodosOverDue() } returns flowOf(emptyList())
        every { useCase.getTodoList() } returns flowOf(emptyList())
        every { useCase.insertTodo(any()) } returns flowOf(Unit)

        val vm = TodoHomeViewModel(
            savedStateHandle = SavedStateHandle(),
            getTodoListUseCase = useCase
        )

        advanceUntilIdle()

        val grouped = vm.groupedTodos.value
        assertEquals(completed, grouped.completed)
        assertEquals(ongoing, grouped.ongoing)
        assertEquals(overdue, grouped.overdue)
    }

    @Test
    fun `saveTodo emits isSaveTodoDone`() = runTest {
        val useCase = mockk<GetTodoListUseCase>()

        every { useCase.getAllTodosCompleted() } returns flowOf(emptyList())
        every { useCase.getOngoingTasks(any()) } returns flowOf(emptyList())
        every { useCase.getOverdueTasks(any()) } returns flowOf(emptyList())
        every { useCase.getAllTodosOutgoing() } returns flowOf(emptyList())
        every { useCase.getAllTodosOverDue() } returns flowOf(emptyList())
        every { useCase.getTodoList() } returns flowOf(emptyList())
        every { useCase.insertTodo(any()) } returns flowOf(Unit)

        val vm = TodoHomeViewModel(SavedStateHandle(), useCase)
        advanceUntilIdle()

        val todo = Todo(id = 123, title = "t", description = "", state = StateTodo.StateInProgress, dueDate = LocalDate.now())

        val emitted = async {
            withTimeout(2.seconds) {
                vm.isSaveTodoDone.first()
            }
        }

        vm.saveTodo(todo)
        advanceUntilIdle()

        assertEquals(todo, emitted.await())
    }

    @Test
    fun `updateSelectedDate and updateSelectedTime affect getSelectedDateTimeMillis`() = runTest {
        val useCase = mockk<GetTodoListUseCase>()

        every { useCase.getAllTodosCompleted() } returns flowOf(emptyList())
        every { useCase.getOngoingTasks(any()) } returns flowOf(emptyList())
        every { useCase.getOverdueTasks(any()) } returns flowOf(emptyList())
        every { useCase.getAllTodosOutgoing() } returns flowOf(emptyList())
        every { useCase.getAllTodosOverDue() } returns flowOf(emptyList())
        every { useCase.getTodoList() } returns flowOf(emptyList())
        every { useCase.insertTodo(any()) } returns flowOf(Unit)

        val vm = TodoHomeViewModel(SavedStateHandle(), useCase)

        val baseMillis = 1_700_000_000_000L
        vm.updateSelectedDate(baseMillis)
        vm.updateSelectedTime(hour = 9, minute = 30)

        val actual = vm.getSelectedDateTimeMillis()
        val expected = Calendar.getInstance().apply {
            timeInMillis = baseMillis
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        assertEquals(expected, actual)
    }
}
