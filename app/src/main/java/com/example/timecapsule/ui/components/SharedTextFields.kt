package com.example.timecapsule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

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

@Composable
fun MainTextFieldNonHighlightable(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    randomPlaceholder: String = TextFieldPlaceholders.quotes[Random.nextInt(TextFieldPlaceholders.quotes.size)]

) {
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = randomPlaceholder,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .heightIn(min = 180.dp),
        singleLine = false,
        maxLines = 13,
    )
}

@Composable
fun MainTextFieldHighlightable(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    randomPlaceholder: String = TextFieldPlaceholders.quotes[Random.nextInt(TextFieldPlaceholders.quotes.size)],
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    lineHeight: TextUnit = 24.sp,
    maxLines: Int = 13
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = when {
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }
    val borderThickness = if (isFocused) 2.dp else 1.dp

    val boldStyle = SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

    BasicTextField(
        value = textFieldValue,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
            .focusable(interactionSource = remember { MutableInteractionSource() })
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .background(
                color = Color.Transparent,
            )
            .border(
                width = borderThickness,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontSize = fontSize,
            lineHeight = lineHeight,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        maxLines = maxLines,
        visualTransformation = {
            val text = it.text
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

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    var consumedOriginalLength = 0
                    var transformedLength = 0
                    for (match in regex.findAll(text)) {
                        val originalStart = match.range.first
                        val content = match.groupValues[1]

                        val textBeforeLength = originalStart - consumedOriginalLength
                        if (offset <= consumedOriginalLength + textBeforeLength) {
                            return transformedLength + (offset - consumedOriginalLength)
                        }
                        consumedOriginalLength += textBeforeLength
                        transformedLength += textBeforeLength

                        if (offset <= consumedOriginalLength + 2) {
                            return transformedLength
                        }
                        consumedOriginalLength += 2

                        if (offset <= consumedOriginalLength + content.length) {
                            return transformedLength + (offset - consumedOriginalLength)
                        }
                        consumedOriginalLength += content.length
                        transformedLength += content.length

                        if (offset <= consumedOriginalLength + 2) {
                            return transformedLength
                        }
                        consumedOriginalLength += 2
                    }
                    return transformedLength + (offset - consumedOriginalLength)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    var consumedTransformedLength = 0
                    var originalLength = 0
                    for (match in regex.findAll(text)) {
                        val originalStart = match.range.first
                        val content = match.groupValues[1]

                        val textBeforeLength = originalStart - originalLength
                        if (offset <= consumedTransformedLength + textBeforeLength) {
                            return originalLength + (offset - consumedTransformedLength)
                        }
                        consumedTransformedLength += textBeforeLength
                        originalLength += textBeforeLength
                        originalLength += 2

                        if (offset <= consumedTransformedLength + content.length) {
                            return originalLength + (offset - consumedTransformedLength)
                        }
                        consumedTransformedLength += content.length
                        originalLength += content.length
                        originalLength += 2
                    }
                    return originalLength + (offset - consumedTransformedLength)
                }
            }

            TransformedText(annotatedString, offsetMapping)
        },
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                if (textFieldValue.text.isEmpty()) {
                    Text(
                        text = randomPlaceholder,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = lineHeight,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}