package com.karleinstein.basemvvm.ui.screen.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karleinstein.basemvvm.model.StateTodo
import com.karleinstein.basemvvm.model.Todo
import com.karleinstein.basemvvm.viewmodel.GroupedTodos
import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenRoute(
    homeViewModel: TodoHomeViewModel,
    onAddClick: () -> Unit
) {
    val groupedTodos by homeViewModel.groupedTodos.collectAsState()
    HomeScreen(groupedTodos = groupedTodos, onAddClick = onAddClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    groupedTodos: GroupedTodos,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {}
) {
    val isCompletelyEmpty =
        groupedTodos.ongoing.isEmpty() &&
                groupedTodos.completed.isEmpty() &&
                groupedTodos.overdue.isEmpty()
    if (isCompletelyEmpty) {
        EmptyHomeState(onAddClick)
        return
    }
    var query by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Greeting
        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Good Morning 👋",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Let’s focus on what matters today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search
        item {
            PremiumSearchBar()
        }

        // Summary Card
        item {
            SummaryCard(groupedTodos)
        }

        // Ongoing Section
        if (groupedTodos.ongoing.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Ongoing",
                    count = groupedTodos.ongoing.size
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp), // Adjust this for peek amount
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(groupedTodos.ongoing) { todo ->
                        PremiumOngoingCard(
                            todo = todo,
                            modifier = Modifier
                                .fillParentMaxWidth(fraction = 0.8f) // Card takes 80% of screen width
                        )
                    }
                }
            }
        }

        // Overdue Section
        if (groupedTodos.overdue.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Overdue",
                    count = groupedTodos.overdue.size
                )
            }

            items(groupedTodos.overdue) { todo ->
                PremiumOverdueCard(todo)
            }
        }

        // Completed Section
        if (groupedTodos.completed.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Completed",
                    count = groupedTodos.completed.size
                )
            }

            items(groupedTodos.completed) { todo ->
                PremiumCompletedRow(todo)
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun SummaryCard(groupedTodos: GroupedTodos) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryItem("Ongoing", groupedTodos.ongoing.size)
            SummaryItem("Overdue", groupedTodos.overdue.size)
            SummaryItem("Done", groupedTodos.completed.size)
        }
    }
}

@Composable
fun SummaryItem(title: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$title ($count)",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "See all",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun PremiumOngoingCard(todo: Todo, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 3.dp,
        modifier = modifier
            .width(240.dp)
            .height(160.dp) // Fixed height for consistency
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = todo.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis, // Add this to handle long text
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f, fill = false) // Takes available space
            )

            Text(
                text = "Due ${todo.dueDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LinearProgressIndicator(
                progress = { 0.6f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun PremiumOverdueCard(todo: Todo) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚠ Overdue",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                text = todo.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Text(
                text = "Due ${todo.dueDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun PremiumCompletedRow(todo: Todo) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "✓",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSearchBar() {

    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = { expanded = true },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Search tasks...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (expanded) {
                        IconButton(onClick = {
                            query = ""
                            expanded = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear"
                            )
                        }
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp
    ) {
        Text(
            text = "Search results...",
            modifier = Modifier.padding(16.dp)
        )
    }

}

@Composable
fun EmptyHomeState(
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.TaskAlt,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No tasks yet",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start by creating your first task.\nStay organized and focused.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Create First Task")
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeScreenPreview() {
    val sampleTodos = GroupedTodos(
        ongoing = listOf(
            Todo(
                1,
                "Design the new login screen",
                "",
                StateTodo.StateInProgress,
                LocalDate.now().plusDays(2)
            ),
            Todo(
                2,
                "Implement the main dashboard",
                "",
                StateTodo.StateInProgress,
                LocalDate.now().plusDays(5)
            )
        ),
        overdue = listOf(
            Todo(
                3,
                "Fix the critical bug on production",
                "",
                StateTodo.StateInProgress,
                LocalDate.now().minusDays(1)
            )
        ),
        completed = listOf(
            Todo(4, "Release version 1.2.0", "",StateTodo.StateDone, LocalDate.now().minusDays(3)),
            Todo(
                5,
                "Write documentation for the API",
                "",
                StateTodo.StateDone,
                LocalDate.now().minusDays(7)
            )
        )
    )
    val empty = GroupedTodos()
    HomeScreen(groupedTodos = sampleTodos)
}