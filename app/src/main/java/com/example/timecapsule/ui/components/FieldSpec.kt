package com.example.timecapsule.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier

// Stores parameters for the fields using OptionalTextField
data class FieldSpec(
    val value: String,
    val onValueChange: (String) -> Unit,
    val label: String,
    val modifier: Modifier = Modifier,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActions: KeyboardActions = KeyboardActions.Default,
    val suggestions: List<String> = emptyList(),
    val onSuggestionClick: ((String) -> Unit)? = null
)
