package com.example.timecapsule.ui.components

import androidx.compose.foundation.text.KeyboardOptions

// Stores parameters for the fields using CompactBorderlessTextField
data class FieldSpec(
    val value: String,
    val onValueChange: (String) -> Unit,
    val label: String,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val suggestions: List<String> = emptyList(),
    val onSuggestionClick: ((String) -> Unit)? = null
)
