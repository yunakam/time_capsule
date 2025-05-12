package com.example.timecapsule

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun metaText(
    text: String,
    fontSize: TextUnit = 14.sp,
    color: Color = MaterialTheme.colorScheme.outline,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUrl = text.startsWith("http://") || text.startsWith("https://")

    if (isUrl) {
        ClickableText(
            text = AnnotatedString(
                text,
                spanStyle = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontSize = fontSize,
                    color = color
                )
            ),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(text))
                context.startActivity(intent)
            }
        )
    } else {
        Text(
            text = text,
            fontStyle = FontStyle.Italic,
            fontSize = fontSize,
            color = color
        )
    }
}

@Composable
fun dataText(
    text: String,
    fontSize: TextUnit = 12.sp,
    color: Color = MaterialTheme.colorScheme.outline,
    modifier: Modifier = Modifier
) {
Text(
    text = text,
    fontSize = fontSize,
    color = color
    )
}

@Composable
fun NoteViewDialog(
    note: Note,
    onEdit: () -> Unit,
    onDelete: (Note) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 550.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(note.text, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))

                    // Optional fields with left padding
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        note.author?.takeIf { it.isNotBlank() }?.let {
                            metaText("- $it", fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        note.sourceTitle?.takeIf { it.isNotBlank() }?.let {
                            metaText("\"$it\"")
                        }
                        note.sourceUrl?.takeIf { it.isNotBlank() }?.let {
                            metaText("$it")
                        }
                        note.page?.takeIf { it.isNotBlank() }?.let {
                            metaText("Page: $it")
                        }
                        note.publisher?.takeIf { it.isNotBlank() }?.let {
                            metaText("Publisher: $it")
                        }
                        note.tags?.takeIf { it.isNotBlank() }?.let {
                            metaText("Tags: ${formatTags(it)}")
                        }
                        Spacer(Modifier.height(24.dp))
                        dataText("Created: ${formatDate(note.createdAt)}")
                        note.lastVisitedAt?.let {
                            dataText("Last Visited: ${formatDate(it)}")
                        }
                        dataText("Visited: ${note.visitCount}")
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onDelete(note) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Note")
                }
            }
        },
        dismissButton = {}
    )
}

fun formatDate(date: Date?): String =
    date?.let { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it) } ?: "-"

fun formatTags(tags: String?): String =
    tags?.split(",")?.joinToString(", ") { it.trim() }?.takeIf { it.isNotBlank() } ?: "-"
