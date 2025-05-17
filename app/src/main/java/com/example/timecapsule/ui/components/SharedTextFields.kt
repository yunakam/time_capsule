package com.example.timecapsule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> rememberSuggestions(
    query: String,
    suggestionProvider: suspend (String) -> List<T>
): State<List<T>> {
    return produceState(initialValue = emptyList(), query) {
        value = if (query.isNotBlank()) {
            suggestionProvider(query)
        } else {
            emptyList()
        }
    }
}

@Composable
fun OptionalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    suggestions: List<String> = emptyList(),
    onSuggestionClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(start = 24.dp),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val showFloatingLabel = isFocused || value.isEmpty()
    val density = LocalDensity.current

    var expanded by remember { mutableStateOf(false) }
    var textFieldWidth by remember { mutableStateOf(0) } // so that the suggestion dropdown fits the width of the text field

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
            onValueChange = {
                onValueChange(it)
                if (onSuggestionClick != null) expanded = true
            },
            label = if (value.isEmpty() || isFocused) {
                {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        ),
                    )
                }
            } else null,
            leadingIcon = if (!showFloatingLabel) {
                {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
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
                .background(Color.Transparent)
                .onGloballyPositioned { coordinates ->
                    textFieldWidth = coordinates.size.width
                },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            interactionSource = interactionSource
        )

        // Suggestions dropdown
        if (onSuggestionClick != null && value.isNotBlank() && suggestions.isNotEmpty()) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(density) { textFieldWidth.toDp() })
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                        )
                    .padding(8.dp)
            ) {
                suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(
                            suggestion,
                            fontSize = 12.sp
                        ) },
                        onClick = {
                            onSuggestionClick(suggestion)
                            expanded = false
                        },
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }
    }
}

// backup class - not used
@Composable
fun OptionalTextField_backup(
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
                            fontSize = 10.sp,
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