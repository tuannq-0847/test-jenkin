package com.karleinstein.basemvvm.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import com.karleinstein.basemvvm.model.Todo

object TodoScheduler {

    // Schedule notification alarm
    fun scheduleNotification(context: Context, todo: Todo, todoId: Long, triggerAtMillis: Long) {
        val intent = Intent(context, TodoAlarmReceiver::class.java).apply {
            putExtra("todo_title", todo.title)
            putExtra("todo_id", todoId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(AlarmManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    // Add event to Google Calendar / device calendar
    fun addToCalendar(context: Context, todo: Todo, triggerAtMillis: Long) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, todo.title)
            putExtra(CalendarContract.Events.DESCRIPTION, todo.description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, triggerAtMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, triggerAtMillis + 60 * 60 * 1000) // +1 hour
            putExtra(CalendarContract.Events.HAS_ALARM, 1)
        }
        context.startActivity(intent)
    }

    // Cancel notification if task deleted
    fun cancelNotification(context: Context, todoId: Int) {
        val intent = Intent(context, TodoAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.cancel(pendingIntent)
    }
}