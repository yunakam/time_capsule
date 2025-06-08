package com.example.timecapsule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecapsule.data.FilterType
import com.example.timecapsule.data.Note
import com.example.timecapsule.ui.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteredNotesScreen(
    filterType: FilterType,
    filterValue: String,
    allNotes: List<Note>,
    onDismiss: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onNoteDeleteClick: (Note) -> Unit,
    colorForNote: (Note) -> Color
) {
    val filteredNotes by remember(allNotes, filterType, filterValue) {
        derivedStateOf {
            allNotes.filter { note ->
                when (filterType) {
                    FilterType.AUTHOR -> note.saidWho?.equals(filterValue, ignoreCase = true) == true
                    FilterType.TITLE -> note.title?.equals(filterValue, ignoreCase = true) == true
                    FilterType.TAG -> note.tags?.any { it.equals(filterValue, ignoreCase = true) } == true
                }
            }.sortedByDescending { it.createdAt } // Keep consistent sorting
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${
                            when (filterType) {
                                FilterType.AUTHOR -> "Author"
                                FilterType.TITLE -> "Title"
                                FilterType.TAG -> "Tag"
                            }
                        }: $filterValue",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        ),
                        maxLines = 1,
                        overflow = Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notes found for this filter.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onDeleteClick = { onNoteDeleteClick(note) },
                            backgroundColor = colorForNote(note)
                        )
                    }
                }
            }
        }
    }
}