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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.karleinstein.basemvvm.model.Todo
import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel

@Composable
fun HomeScreenContent(
    todos: List<Todo>,
    text: String,
    onTextChange: (String) -> Unit,
    onToggle: (Todo) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                            onToggle(todo.copy(isDone = it))
                        }
                    )
                }
            }
        }

        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Add Todo")
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: TodoHomeViewModel,
    modifier: Modifier = Modifier
) {
    val todos by viewModel.todos.collectAsState(initial = emptyList())

    HomeScreenContent(
        todos = todos,
        text = viewModel.text.value,
        onTextChange = viewModel::updateText,
        onToggle = viewModel::updateTodoList,
        onAdd = { viewModel.addTodo(viewModel.text.value) },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

    val fakeTodos = listOf(
        Todo(1, "Learn Compose", false),
        Todo(2, "Build Todo App", true),
        Todo(3, "Master Navigation", false)
    )

    HomeScreenContent(
        todos = fakeTodos,
        text = "New task...",
        onTextChange = {},
        onToggle = {},
        onAdd = {}
    )
}
