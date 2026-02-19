package com.karleinstein.basemvvm.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.karleinstein.basemvvm.usecase.GetTodoListUseCase
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class OverdueWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val useCase: GetTodoListUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now()

        val overdueTasks = useCase.getOverdueTasks(today).first()
        if (overdueTasks.isNotEmpty()) {
            val now = System.currentTimeMillis()
            overdueTasks.forEach { todo ->
                TodoScheduler.scheduleNotification(
                    context = applicationContext,
                    todo = todo,
                    todoId = todo.id,
                    triggerAtMillis = now
                )
            }
        }

        return Result.success()
    }
}
