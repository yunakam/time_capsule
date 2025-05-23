package com.example.timecapsule.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.timecapsule.R
import com.example.timecapsule.data.Note
import com.google.accompanist.flowlayout.FlowRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun metaText(
    text: String,
    fontSize: TextUnit = 14.sp,
    color: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val isUrl = text.startsWith("http://") || text.startsWith("https://")
    var showDialog by remember { mutableStateOf(false) }

    if (isUrl) {
        // Show the clickable Text
        Text(
            text = text,
            fontSize = fontSize,
            color = color,
            textDecoration = TextDecoration.Underline,
            maxLines = 1,
            overflow = overflow,
            modifier = modifier.clickable {
                showDialog = true
            }
        )

        // Show the confirmation dialog if needed
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Open Link") },
                text = { Text("Do you want to open this link?\n\n$text") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(text))
                            context.startActivity(intent)
                        }
                    ) { Text("Open") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) { Text("Cancel") }
                }
            )
        }
    } else {
        val effectiveModifier = if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        }
        Text(
            text = text,
            fontStyle = FontStyle.Italic,
            fontSize = fontSize,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            modifier = effectiveModifier // Apply the constructed modifier
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
    onDismiss: () -> Unit,
    onFilterByAuthor: (String) -> Unit = {},
    onFilterByTitle: (String) -> Unit = {},
    onFilterByTag: (String) -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
       Surface(
           shape = MaterialTheme.shapes.medium,
           tonalElevation = 8.dp,
           color = MaterialTheme.colorScheme.surface,
           modifier = Modifier.padding(16.dp)
       ) {
           Box(
               modifier = Modifier
                   .fillMaxWidth()
                   .heightIn(max = 550.dp)
                   .padding(24.dp)
           ) {
               Column(
                   modifier = Modifier
                       .fillMaxWidth()
                       .verticalScroll(rememberScrollState())
                       .padding(bottom = 0.dp),
               ) {
                   Box(
                       modifier = Modifier
                           .fillMaxWidth()
                           .heightIn(max = 300.dp)
                           .verticalScroll(rememberScrollState())
                   ) {
                       Text(
                           note.text,
                           style = MaterialTheme.typography.bodyLarge,
                       )
                   }

                   Spacer(Modifier.height(24.dp))

                   // Optional fields with left padding
                   Column(
                       modifier = Modifier.padding(start = 16.dp),
                       verticalArrangement = Arrangement.spacedBy(0.dp)
                   ) {
                       note.author?.takeIf { it.isNotBlank() }?.let {
                           metaText(
                               "- $it",
                               fontSize = 16.sp,
                               onClick =  { onFilterByAuthor(it) },
                           )
                       }

                       Spacer(Modifier.height(6.dp))

                       note.sourceTitle?.takeIf { it.isNotBlank() }?.let { sourceTitle ->
                           val details = listOfNotNull(
                               note.page?.takeIf { it.isNotBlank() }?.let { "page $it" },
                               note.publisher?.takeIf { it.isNotBlank() }
                           ).takeIf { it.isNotEmpty() }
                               ?.joinToString(", ", prefix = " (", postfix = ")") ?: ""

                           Row(verticalAlignment = Alignment.CenterVertically) {
                               metaText(
                                   text = "\"$sourceTitle\"",
                                   modifier = Modifier.clickable { onFilterByTitle(sourceTitle) }
                               )
                               if (details.isNotBlank()) {
                                   metaText(
                                       text = details,
                                   )
                               }
                           }
                       }

                       Spacer(Modifier.height(12.dp))
                       note.sourceUrl?.takeIf { it.isNotBlank() }?.let {
                           metaText(it)
                       }

                       // Link icon
                       // NoteSourceLink(sourceUrl = note.sourceUrl)

                       Spacer(Modifier.height(24.dp))
                       note.tags?.takeIf { it.isNotEmpty() }?.let { tags ->
                           FlowRow(
                               mainAxisSpacing = 8.dp,
                               crossAxisSpacing = 8.dp,
                           ) {
                               tags.forEach { tag ->
                                   Box(
                                       modifier = Modifier.clickable { onFilterByTag(tag) }
                                   ) {
                                       TagChip(tag = tag, icon = true, removable = false)
                                   }                               }
                           }
                       }

                       Spacer(Modifier.height(8.dp))

                       Row(
                           modifier = Modifier.fillMaxWidth(),
                           horizontalArrangement = Arrangement.End
                       ) {
                           Column(modifier = Modifier.padding(end = 12.dp, bottom = 0.dp)) {
                               dataText("Created: ${formatDate(note.createdAt)}")
                               note.lastVisitedAt?.let {
                                   dataText("Last Visited: ${formatDate(it)}")
                               }
                               dataText("Visited: ${note.visitCount}")
                           }
                       }

                   }

                   // Delete and Edit icons
                   Spacer(Modifier.height(8.dp))
                   Row(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(0.dp),
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
               }
           }


       }
    }
}

fun formatDate(date: Date?): String =
    date?.let { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it) } ?: "-"



// for link icon
@Composable
fun NoteSourceLink(sourceUrl: String?, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    sourceUrl?.takeIf { it.isNotBlank() }?.let { url ->
        Icon(
            painter = painterResource(id = R.drawable.ic_link),
            contentDescription = "Open Source Link",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = modifier
                .size(18.dp)
                .clickable { uriHandler.openUri(url) }
        )
    }
}