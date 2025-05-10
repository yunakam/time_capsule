
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecapsule.Note

@Composable
fun CompactBorderlessTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 24.dp)
            .padding(0.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )
    )
}

@Composable
fun AddNoteDialog(
    onSave: (Note) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var sourceTitle by remember { mutableStateOf("") }
    var sourceUrl by remember { mutableStateOf("") }
    var page by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // List of triples: value, onValueChange, label
    val fields = listOf(
        Triple(author, { v: String -> author = v }, "Author (optional)"),
        Triple(sourceTitle, { v: String -> sourceTitle = v }, "Source Title (optional)"),
        Triple(sourceUrl, { v: String -> sourceUrl = v }, "Source URL (optional)"),
        Triple(page, { v: String -> page = v }, "Page (optional)"),
        Triple(publisher, { v: String -> publisher = v }, "Publisher (optional)"),
        Triple(tags, { v: String -> tags = v }, "Tags (comma separated, optional)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Note") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
                // Main note field
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Note") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    singleLine = false,
                    maxLines = 10,
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Loop through all compact fields
                fields.forEachIndexed { idx, (value, onChange, label) ->
                    CompactBorderlessTextField(
                        value = value,
                        onValueChange = onChange,
                        label = label,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (idx < fields.size - 1) {
                        Spacer(modifier = Modifier.height(0.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onSave(
                            Note(
                                text = text,
                                author = author.ifBlank { null },
                                sourceTitle = sourceTitle.ifBlank { null },
                                sourceUrl = sourceUrl.ifBlank { null },
                                page = page.ifBlank { null },
                                publisher = publisher.ifBlank { null },
                                tags = tags.ifBlank { null }
                            )
                        )
                        onDismiss()
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
