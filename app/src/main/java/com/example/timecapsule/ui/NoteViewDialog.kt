package com.example.timecapsule.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecapsule.R
import com.example.timecapsule.data.Note
import com.example.timecapsule.ui.components.TagChip
import com.google.accompanist.flowlayout.FlowRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun metaText(
    text: String,
    fontSize: TextUnit = 14.sp,
    color: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontStyle = FontStyle.Italic,
        fontSize = fontSize,
        color = color
    )
}

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
                        .padding(bottom = 0.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp, max = 300.dp)
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
                            metaText("- $it", fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(6.dp))

                        // Row for sourceTitle, page, and the icon with link to sourceUrl
                        Row {
                            note.sourceTitle?.takeIf { it.isNotBlank() }?.let {
                                metaText("\"$it\"")
                            }
                            Spacer(Modifier.width(12.dp))
                            note.page?.takeIf { it.isNotBlank() }?.let {
                                metaText(
                                    "page: $it",
                                    fontSize = 12.sp,
                                    modifier = Modifier.alignBy(LastBaseline)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            NoteSourceLink(sourceUrl = note.sourceUrl)
                        }

                        note.publisher?.takeIf { it.isNotBlank() }?.let {
                            Row{
                                Spacer(Modifier.width(18.dp))
                                metaText(
                                    "( $it )",
                                    modifier = Modifier.padding(start = 18.dp))
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        note.tags?.takeIf { it.isNotEmpty() }?.let { tags ->
                            FlowRow(
                                mainAxisSpacing = 8.dp,
                                crossAxisSpacing = 8.dp,
                            ) {
                                tags.forEach { tag ->
                                    TagChip(tag = tag, icon = true, removable = false)
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

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
        },
        confirmButton = {
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
        },
        dismissButton = {}
    )
}

fun formatDate(date: Date?): String =
    date?.let { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(it) } ?: "-"
