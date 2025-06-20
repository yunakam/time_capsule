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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.example.timecapsule.R
import com.example.timecapsule.data.Note
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun metaText(
    text: String,
    fontSize: TextUnit = 14.sp,
    color: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier,
//    maxLines: Int = 2,
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
//                maxLines = maxLines,
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

@OptIn(ExperimentalMaterial3Api::class)
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
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
       Surface(
           shape = MaterialTheme.shapes.medium,
           tonalElevation = 8.dp,
           color = MaterialTheme.colorScheme.surface,
       ) {
           Box(
               modifier = Modifier
                   .fillMaxWidth(0.9f)
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
                                   .heightIn(max = 320.dp)
                                   .verticalScroll(rememberScrollState())
                           ) {
                               SelectionContainer {
                                   val text = note.text
                                   val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
                                   val regex = "\\*\\*(.*?)\\*\\*".toRegex()

                                   val annotatedString = buildAnnotatedString {
                                       var lastIndex = 0
                                       regex.findAll(text).forEach { match ->
                                           val startIndex = match.range.first
                                           val endIndex = match.range.last + 1
                                           val content = match.groupValues[1]

                                           if (startIndex > lastIndex) {
                                               append(text.substring(lastIndex, startIndex))
                                           }

                                           pushStyle(boldStyle)
                                           append(content)
                                           pop()

                                           lastIndex = endIndex
                                       }
                                       if (lastIndex < text.length) {
                                           append(text.substring(lastIndex))
                                       }
                                   }

                                   Text(
                                       text = annotatedString,
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

                               val details = listOfNotNull(
                                   note.page?.takeIf { it.isNotBlank() }?.let { "page $it" },
                                   note.source?.takeIf { it.isNotBlank() }
                               )
                               val detailsString = when {
                                   details.isEmpty() -> ""
                                   note.title.isNullOrBlank() -> details.joinToString(", ")
                                   else -> details.joinToString(", ", prefix = "(", postfix = ")")
                               }

                               if (!note.title.isNullOrBlank()) {
                                   val fullString = buildString {
                                       append('"')
                                       append(note.title)
                                       append('"')
                                       if (detailsString.isNotBlank()) {
                                           append('\u00A0') // Non-breaking space (but still breaks...)
                                           append(detailsString)
                                       }
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

                               Spacer(Modifier.height(12.dp))
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

                               Spacer(Modifier.height(12.dp))
                           }
                       }
                   }

                   // Info, Delete and Edit icons (always visible, outside scroll area)
                   Spacer(Modifier.height(8.dp))
                   Row(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(0.dp),
                       horizontalArrangement = Arrangement.End,
                       verticalAlignment = Alignment.CenterVertically
                   ) {
                       val tooltipState = rememberTooltipState(isPersistent = true)
                       val scope = rememberCoroutineScope()
                       TooltipBox(
                           positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                           tooltip = {
                               Surface(
                                   shape = MaterialTheme.shapes.small,
                                   color = MaterialTheme.colorScheme.inverseSurface,
                                   tonalElevation = 2.dp
                               ) {
                                   Column(modifier = Modifier.padding(8.dp)) {
                                       dataText(
                                           text = "Created: ${formatDate(note.createdAt)}",
                                           color = MaterialTheme.colorScheme.inverseOnSurface
                                       )
                                       note.lastVisitedAt?.let {
                                           dataText(
                                               text = "Last Visited: ${formatDate(it)}",
                                               color = MaterialTheme.colorScheme.inverseOnSurface
                                           )
                                       }
                                       dataText(
                                           text = "Visited: ${note.visitCount}",
                                           color = MaterialTheme.colorScheme.inverseOnSurface
                                       )
                                   }
                               }
                           },
                           state = tooltipState
                       ) {
                           IconButton(onClick = { scope.launch { tooltipState.show() } }) {
                               Icon(
                                   Icons.Default.Info,
                                   contentDescription = "Note Information",
                                   tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                               )
                           }
                       }
                       Spacer(Modifier.width(12.dp))

                       IconButton(
                           onClick = { onDelete(note) },
                           modifier = Modifier.size(28.dp)
                       ) {
                           Icon(
                               Icons.Default.Delete,
                               contentDescription = "Delete Note",
                               modifier = Modifier.size(24.dp)
                               )
                       }
                       Spacer(modifier = Modifier.width(12.dp))
                       IconButton(
                           onClick = onEdit,
                           modifier = Modifier.size(28.dp)
                       ) {
                           Icon(
                               Icons.Default.Edit,
                               contentDescription = "Edit Note",
                               modifier = Modifier.size(24.dp)
                           )
                       }
                       Spacer(modifier = Modifier.width(12.dp))
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
