package com.example.timecapsule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NoteList(
    notes: List<Note>,
    onEdit: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(notes) { note ->
            ListItem(
                headlineContent = { Text(note.text) },
                supportingContent = {
                    Column {
                        if (!note.author.isNullOrBlank()) Text("Author: ${note.author}")
                        if (!note.sourceTitle.isNullOrBlank()) Text("Source: ${note.sourceTitle}")
                        if (!note.page.isNullOrBlank()) Text("Page: ${note.page}")
                        if (!note.publisher.isNullOrBlank()) Text("Publisher: ${note.publisher}")
                        if (!note.tags.isNullOrBlank()) Text("Tags: ${note.tags}")
                    }
                },
                modifier = Modifier
                    .clickable { onEdit(note) }
            )
            HorizontalDivider()
        }
    }
}
