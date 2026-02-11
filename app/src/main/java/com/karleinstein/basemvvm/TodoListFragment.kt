package com.karleinstein.basemvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.karleinstein.basemvvm.viewmodel.TodoListViewModel
import kotlin.random.Random

class TodoListFragment : Fragment() {

    private val vm: TodoListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return context?.let {
            ComposeView(it).apply {
                setContent {
                    val todos by vm.todos.collectAsState(initial = emptyList())
                    Column(
                        Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars)
                    ) {
                        LazyColumn {
                            items(todos, key = { it.id }) { todo ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(todo.title)
                                    Checkbox(checked = todo.isDone, onCheckedChange = {
                                        vm.updateTodoList(todo.copy(isDone = it))
                                    })
                                }
                            }
                        }
                        TextField(
                            value = vm.text.value,
                            onValueChange = { vm.updateText(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button( onClick = {
                            vm.addTodo(vm.text.value)
                        }, content = {
                            Text("Add Todo")
                        })
                    }
                }
            }
        } ?: super.onCreateView(inflater, container, savedInstanceState)
    }
}