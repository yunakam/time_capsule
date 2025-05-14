package com.example.timecapsule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
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

@Composable
fun CompactBorderlessTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val showFloatingLabel = isFocused || value.isEmpty()

    // Only apply border when focused
    val borderModifier = if (isFocused) {
        Modifier
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(borderModifier)
            .fillMaxWidth()
            .height(48.dp)
            .padding(0.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = if (value.isEmpty() || isFocused) {
                {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        ),                    )
                }
            } else null,
            leadingIcon = if (!showFloatingLabel) {
                {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(start = 14.dp, end = 4.dp)
                    )
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .heightIn(min = 24.dp)
                .background(Color.Transparent),
            keyboardOptions = keyboardOptions,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,   // Remove underline
                unfocusedIndicatorColor = Color.Transparent, // Remove underline
                disabledIndicatorColor = Color.Transparent,  // Remove underline
            ),
            interactionSource = interactionSource
        )
    }
}

@Composable
fun SuggestionTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        androidx.compose.material3.DropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            suggestions.forEach { suggestion ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = {
                        onSuggestionClick(suggestion)
                        expanded = false
                    }
                )
            }
        }
    }
}
