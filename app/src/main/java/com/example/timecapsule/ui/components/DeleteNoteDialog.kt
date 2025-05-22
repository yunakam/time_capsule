package com.example.timecapsule.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable


@Composable
fun DeleteNoteDialog(
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Note") },
        text = { Text(
            "Are you sure you want to delete this note?",
        ) },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(
                    "Delete",
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                )
            }
        },
    )
}
