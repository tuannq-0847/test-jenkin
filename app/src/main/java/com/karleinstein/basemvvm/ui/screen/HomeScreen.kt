package com.karleinstein.basemvvm.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel

@Composable
fun HomeScreen(
    viewModel: TodoHomeViewModel,
    modifier: Modifier = Modifier
) {
    val todos by viewModel.todos.collectAsState(initial = emptyList())
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(todos, key = { it.id }) { todo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(todo.title)
                    Checkbox(
                        checked = todo.isDone,
                        onCheckedChange = {
                            viewModel.updateTodoList(todo.copy(isDone = it))
                        }
                    )
                }
            }
        }
        
        TextField(
            value = viewModel.text.value,
            onValueChange = { viewModel.updateText(it) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { viewModel.addTodo(viewModel.text.value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Todo")
        }
    }
}
