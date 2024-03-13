package io.github.janmalch.textfieldsheet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.janmalch.textfieldsheet.ui.theme.TextFieldSheetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // WindowCompat.setDecorFitsSystemWindows(window, false) // TODO: overlap still broken

        setContent {
            TextFieldSheetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isSheetOpen by remember { mutableStateOf(false) }
                    var text by remember { mutableStateOf("") }
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(onClick = { isSheetOpen = true }) {
                                Icon(Icons.Rounded.Edit, contentDescription = null)
                            }
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            Text(text = text, modifier = Modifier.padding(24.dp))
                        }

                        if (isSheetOpen) {
                            TextFieldSheet(
                                value = text,
                                placeholder = "New task",
                                buttonText = "Save",
                                onValueChange = { text = it },
                                onDismissRequest = { isSheetOpen = false },
                                // optional: confirm dialog to discard dirty sheets
                                /*
                                discardContent = {
                                    AlertDialog(
                                        onDismissRequest = {} // not dismissible,
                                        title = { Text(text = "Discard current task?") },
                                        text = { Text(text = "Do you want to discard your current draft?") },
                                        dismissButton = {
                                            TextButton(onClick = { cancelDiscardingSheet() }) {
                                                Text(text = "Cancel")
                                            }
                                        },
                                        confirmButton = {
                                            TextButton(onClick = { discardSheetChanges() }) {
                                                Text(text = "Discard")
                                            }
                                        },
                                    )
                                },
                                */
                            )
                        }
                    }
                }
            }
        }
    }
}

