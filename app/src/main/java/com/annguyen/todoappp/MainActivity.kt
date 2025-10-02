package com.annguyen.todoappp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoApp()
                }
            }
        }
    }
}


 // Main composable function for the To-Do app.
 // Demonstrates state hoisting pattern - state is managed here and passed down to composed children.

@Composable
fun TodoApp() {
    // rememberSaveable preserves state across configuration changes (like rotation)
    var todoItems by rememberSaveable { mutableStateOf(listOf<TodoItem>()) }
    var nextId by rememberSaveable { mutableStateOf(1) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "TODO List",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        AddItemSection(
            onAddItem = { text ->
                // Add new item to the list
                todoItems = todoItems + TodoItem(id = nextId, text = text)
                nextId++
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Scrollable list for active and completed items
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            val activeItems = todoItems.filter { !it.isCompleted }
            if (activeItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Items",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(activeItems, key = { it.id }) { item ->
                    TodoItemRow(
                        item = item,
                        onCheckedChange = { isChecked ->
                            // Toggle completion status
                            todoItems = todoItems.map {
                                if (it.id == item.id) it.copy(isCompleted = isChecked)
                                else it
                            }
                        },
                        onDelete = {
                            todoItems = todoItems.filter { it.id != item.id }
                        }
                    )
                }
            } else {
                item {
                    EmptyStateMessage("No active items. Add a task to get started!")
                }
            }
            
            // Add spacing between sections
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Completed Items Section
            val completedItems = todoItems.filter { it.isCompleted }
            if (completedItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Completed Items",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(completedItems, key = { it.id }) { item ->
                    TodoItemRow(
                        item = item,
                        onCheckedChange = { isChecked ->
                            // Toggle completion status
                            todoItems = todoItems.map {
                                if (it.id == item.id) it.copy(isCompleted = isChecked)
                                else it
                            }
                        },
                        onDelete = {
                            // Remove item from list
                            todoItems = todoItems.filter { it.id != item.id }
                        }
                    )
                }
            }
        }
    }
}

 // Composable for input section so that it adds new things..
 // Demonstrates the use of stateless component and event callbacks (state hoisting)..
 // onAddItem Callback when a new item is to be added

@Composable
fun AddItemSection(onAddItem: (String) -> Unit) {
    // Local state for the text field
    var textInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Input field
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Enter the task name") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        
        Button(
            onClick = {
                val trimmedText = textInput.trim()
                if (trimmedText.isEmpty()) {
                    // Show validation message for blank input
                    Toast.makeText(context, "Please enter a task name", Toast.LENGTH_SHORT).show()
                } else {
                    // Add the item and clear the input
                    onAddItem(trimmedText)
                    textInput = ""
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Add")
        }
    }
}


 // Composable to one to do item row
 // Stateless component that accepts state and callbacks from parent (state hoisting)
 // item The TodoItem that is to be displayed
 // onCheckedChange Callback when state of checkbox is changing
 // onDelete Call back when delete button is clicked

@Composable
fun TodoItemRow(
    item: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.text,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp
            )
            
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = onCheckedChange
            )
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete"
                )
            }
        }
    }
}


 // Composable for displaying an empty state message.
 // message The message to display
@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
