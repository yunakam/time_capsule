package com.example.timecapsule.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timecapsule.R
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteCategory
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
private fun CategoryIcon(category: NoteCategory?) {
    val iconRes = when (category) {
        NoteCategory.BOOK -> R.drawable.ic_book
        NoteCategory.WEB -> R.drawable.ic_web
        NoteCategory.TALK -> R.drawable.ic_talk
        NoteCategory.THOUGHTS -> R.drawable.ic_thoughts
        null -> null
    }
    iconRes?.let {
        Icon(
            painter = painterResource(id = it),
            contentDescription = "Note category: ${category?.name}",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    backgroundColor: Color,
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val previewText = remember(note.text) {
        val originalText = note.text
        val boldStartIndex = originalText.indexOf("**")

        val textToProcess = if (boldStartIndex != -1) {
            val textFromBold = originalText.substring(boldStartIndex)
            if (boldStartIndex > 0) {
                "...$textFromBold"
            } else {
                textFromBold
            }
        } else {
            originalText
        }

        val cleanedText = textToProcess
            .replace("**", "")
            .split("\n")
            .dropLastWhile { it.trim().isEmpty() }
            .joinToString("\n")

        val lines = cleanedText.lines()
        if (lines.size > 5) {
            lines.take(5).joinToString("\n") + "\n..."
        } else {
            cleanedText
        }
    }

    Card(
        modifier = Modifier
            .widthIn(min = 115.dp, max = 240.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onDeleteClick() }
                )
            }
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Column {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = previewText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    CategoryIcon(note.category)
                    if (note.category != null) {
                        Spacer(modifier = Modifier.size(4.dp))
                    }
                    Text(
                        text = dateFormat.format(note.createdAt),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}