package com.karleinstein.basemvvm.ui.screen.main

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import com.karleinstein.basemvvm.extension.toLocalDate
import com.karleinstein.basemvvm.model.Todo
import com.karleinstein.basemvvm.notification.TodoScheduler
import com.karleinstein.basemvvm.notification.TodoScheduler.addToCalendar
import com.karleinstein.basemvvm.usecase.GetTodoListUseCase
import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskRoute(
    viewModel: TodoHomeViewModel,
    onPopBack: () -> Unit
) {
    AddTaskScreen(
        viewModel = viewModel,
        onPopBack = onPopBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TodoHomeViewModel,
    modifier: Modifier = Modifier,
    onPopBack: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var addToCalendar by rememberSaveable { mutableStateOf(true) }

    var selectedDateMillis by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedHour by rememberSaveable { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by rememberSaveable { mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute
    )

    val formattedDateTime = remember(selectedDateMillis, selectedHour, selectedMinute) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
        }
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(calendar.time)
    }

    val context = LocalContext.current

    // Request notification permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Request exact alarm permission
    val exactAlarmLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(context, "Exact alarm permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Request permissions on launch
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                exactAlarmLauncher.launch(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                )
            }
        }
    }

    // Collect save done event
    LaunchedEffect(Unit) {
        viewModel.isSaveTodoDone.collect { savedTodo ->
            val triggerMillis = viewModel.getSelectedDateTimeMillis()

            TodoScheduler.scheduleNotification(
                context = context,
                todo = savedTodo,
                todoId = savedTodo.id,
                triggerAtMillis = triggerMillis
            )

            if (addToCalendar) {
                addToCalendar(
                    context = context,
                    todo = savedTodo,
                    triggerAtMillis = triggerMillis
                )
            }

            delay(500)
            isLoading = false
            Toast.makeText(context, "Todo saved successfully ✅", Toast.LENGTH_SHORT).show()
            onPopBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "New Task",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onPopBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Title Field
            item {
                PremiumTextField(
                    value = title,
                    onValueChange = { if (it.length <= 40) title = it },
                    label = "Task Title",
                    placeholder = "Enter task title",
                    supporting = "${title.length}/40"
                )
            }

            // Details Field
            item {
                PremiumTextField(
                    value = details,
                    onValueChange = { if (it.length <= 120) details = it },
                    label = "Task Details",
                    placeholder = "Describe your task...",
                    supporting = "${details.length}/120",
                    singleLine = false
                )
            }

            // Due Date & Time
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Due Date & Time",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = formattedDateTime,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // Add to Calendar Toggle
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Add to Calendar",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Creates a calendar event",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = addToCalendar,
                            onCheckedChange = { addToCalendar = it }
                        )
                    }
                }
            }

            // Create Button
            item {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        viewModel.saveTodo(
                            Todo(
                                title = title,
                                description = details,
                                dueDate = selectedDateMillis.toLocalDate(),
                            )
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    AnimatedContent(
                        targetState = isLoading,
                        label = "loading_animation"
                    ) { loading ->
                        if (loading) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Saving...",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        } else {
                            Text(
                                text = "Create Task",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }

        // Date + Time Picker Dialog
        if (showDatePicker) {
            DateTimePickerDialog(
                datePickerState = datePickerState,
                timePickerState = timePickerState,
                onDismiss = { showDatePicker = false },
                onConfirm = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDateMillis = millis
                        viewModel.updateSelectedDate(millis)
                    }
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    viewModel.updateSelectedTime(
                        hour = timePickerState.hour,
                        minute = timePickerState.minute
                    )
                    showDatePicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    datePickerState: DatePickerState,
    timePickerState: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (showTimePicker) onConfirm()
                    else showTimePicker = true
                }
            ) {
                Text(if (showTimePicker) "Confirm" else "Next")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (showTimePicker) showTimePicker = false
                    else onDismiss()
                }
            ) {
                Text(if (showTimePicker) "Back" else "Cancel")
            }
        },
        text = {
            AnimatedContent(
                targetState = showTimePicker,
                label = "date_time_switch"
            ) { isTimePicker ->
                if (isTimePicker) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select Time",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                        TimePicker(state = timePickerState)
                    }
                } else {
                    DatePicker(state = datePickerState)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    supporting: String,
    singleLine: Boolean = true
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            supportingText = {
                Text(
                    text = supporting,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AddTaskScreenPreview() {
    val mockGetTodoListUseCase = object : GetTodoListUseCase {
        override fun getTodoList(): Flow<List<Todo>> {
            return flowOf(emptyList())
        }

        override fun getAllTodosOutgoing(): Flow<List<Todo>> {
            return flowOf(emptyList())
        }

        override fun getAllTodosCompleted(): Flow<List<Todo>> {
            return flowOf(emptyList())
        }

        override fun getAllTodosOverDue(): Flow<List<Todo>> {
            return flowOf(emptyList())
        }

        override fun insertTodo(todo: Todo): Flow<Unit> {
            return flowOf(Unit)
        }
    }
    val mockViewModel = TodoHomeViewModel(
        savedStateHandle = SavedStateHandle(),
        getTodoListUseCase = mockGetTodoListUseCase
    )
    AddTaskScreen(viewModel = mockViewModel, modifier = Modifier, {})
}
