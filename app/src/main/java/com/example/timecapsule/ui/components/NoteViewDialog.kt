package com.example.timecapsule.ui.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.core.net.toUri
import com.example.timecapsule.R
import com.example.timecapsule.data.Note
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
        SelectionContainer {
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
        }
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
                            val intent = Intent(Intent.ACTION_VIEW, text.toUri())
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
        SelectionContainer {
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
}

@Composable
fun dataText(
    text: String,
    fontSize: TextUnit = 12.sp,
    color: Color = MaterialTheme.colorScheme.outline,
    modifier: Modifier = Modifier
) {
    SelectionContainer {
        Text(
            text = text,
            fontSize = fontSize,
            color = color,
            lineHeight = fontSize * 1.1,
            modifier = modifier
        )
    }
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
       ) {
           Box(
               modifier = Modifier
                   .widthIn(max = 400.dp)
                   .heightIn(max = 650.dp)
                   .padding(24.dp)
           ) {
               Column(
                   modifier = Modifier.fillMaxWidth()
               ) {
                   SelectionContainer {
                       Column(
                           modifier = Modifier
                               .fillMaxWidth()
                               .weight(1f, fill = false)
                               .verticalScroll(rememberScrollState())
                               .padding(bottom = 0.dp),
                       ) {
                           Box(
                               modifier = Modifier
                                   .fillMaxWidth()
                                   .heightIn(max = 300.dp)
                                   .verticalScroll(rememberScrollState())
                           ) {
                               SelectionContainer {
                                   Text(
                                       note.text,
                                       style = MaterialTheme.typography.bodyLarge,
                                       modifier = Modifier.fillMaxWidth()
                                   )
                               }
                           }

                           Spacer(Modifier.height(14.dp))

                           // Optional fields with left padding
                           Column(
                               modifier = Modifier.padding(start = 16.dp),
                               verticalArrangement = Arrangement.spacedBy(0.dp)
                           ) {
                               note.saidWho?.takeIf { it.isNotBlank() }?.let {
                                   metaText(
                                       "- $it",
                                       fontSize = 16.sp,
                                       onClick =  { onFilterByAuthor(it) },
                                   )
                               }

                               Spacer(Modifier.height(6.dp))

//                               note.title?.takeIf { it.isNotBlank() }?.let { title ->
//                                   val details = listOfNotNull(
//                                       note.page?.takeIf { it.isNotBlank() }?.let { "page $it" },
//                                       note.source?.takeIf { it.isNotBlank() }
//                                   ).takeIf { it.isNotEmpty() }
//                                       ?.joinToString(", ", prefix = " (", postfix = ")") ?: ""
//                                   val fullString = buildString {
//                                       append('"')
//                                       append(title)
//                                       append('"')
//                                       if (details.isNotBlank()) append(details)
//                                   }
//                                   metaText(
//                                       text = fullString,
//                                       modifier = Modifier.clickable { onFilterByTitle(title) }
//                                   )
//                               } ?: run {
//                                   // If no title, just show page/source if available
//                                   val details = listOfNotNull(
//                                       note.page?.takeIf { it.isNotBlank() }?.let { "page $it" },
//                                       note.source?.takeIf { it.isNotBlank() }
//                                   )
//                                       .joinToString(", ")
//                                   if (details.isNotBlank()) {
//                                       metaText(details)
//                                   }
//                               }

                               val details = listOfNotNull(
                                   note.page?.takeIf { it.isNotBlank() }?.let { "page $it" },
                                   note.source?.takeIf { it.isNotBlank() }
                               )
                               val detailsString = when {
                                   details.isEmpty() -> ""
                                   note.title.isNullOrBlank() -> details.joinToString(", ")
                                   else -> details.joinToString(", ", prefix = " (", postfix = ")")
                               }

                               if (!note.title.isNullOrBlank()) {
                                   val fullString = buildString {
                                       append('"')
                                       append(note.title)
                                       append('"')
                                       if (detailsString.isNotBlank()) append(detailsString)
                                   }
                                   metaText(
                                       text = fullString,
                                       modifier = Modifier.clickable { onFilterByTitle(note.title) }
                                   )
                               } else if (detailsString.isNotBlank()) {
                                   metaText(detailsString)
                               }
                               
                               Spacer(Modifier.height(12.dp))
                               note.url?.takeIf { it.isNotBlank() }?.let {
                                   metaText(it)
                               }

                               // Link icon
                               // NoteSourceLink(url = note.url)

                               Spacer(Modifier.height(24.dp))
                               note.tags?.takeIf { it.isNotEmpty() }?.let { tags ->
                                   FlowRow(
                                       horizontalArrangement = Arrangement.spacedBy(8.dp),
                                       verticalArrangement = Arrangement.spacedBy(8.dp),
                                   ) {
                                       tags.forEach { tag ->
                                           Box(
                                               modifier = Modifier.clickable { onFilterByTag(tag) }
                                           ) {
                                               TagChip(tag = tag, icon = true, removable = false)
                                           }                               }
                                   }
                               }

                               Spacer(Modifier.height(18.dp))

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
                       }
                   }

                   // Delete and Edit icons (always visible, outside scroll area)
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
fun NoteSourceLink(url: String?, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    url?.takeIf { it.isNotBlank() }?.let { url ->
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
