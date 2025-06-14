package com.example.timecapsule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        .fillMaxWidth(),
    maxLines: Int = 1,
    singleLine: Boolean = true,
    onFocusChanged: ((Boolean) -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val (suggestionSelected, setSuggestionSelected) = remember { mutableStateOf(false) }

    // Call onFocusChanged when focus changes
    LaunchedEffect(isFocused) {
        onFocusChanged?.invoke(isFocused)
    }

    // Only apply border when focused
    val borderModifier = if (isFocused) {
        Modifier
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
    } else {
        Modifier
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(start = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .padding(top = 18.dp, end = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (label == "saidWho") {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.padding(vertical = 0.dp)
        ) {
            TextField(
                value = value,
                onValueChange = { newValue: String ->
                    onValueChange(newValue)
                    setSuggestionSelected(false)
                },
                modifier = modifier
                    .then(borderModifier)
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Transparent),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                maxLines = maxLines,
                singleLine = singleLine,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                interactionSource = interactionSource,
                placeholder = null,
                label = null,
                )

            // Suggestion list shows up as Column
            if (
                onSuggestionClick != null &&
                suggestions.isNotEmpty() &&
                isFocused &&
                !suggestionSelected
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    suggestions.forEach { suggestion ->
                        Text(
                            text = suggestion,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable {
                                    onSuggestionClick(suggestion)
                                    setSuggestionSelected(true)
                                }
                        )
                    }
                }
            }
        }
    }
}
