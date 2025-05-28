package com.example.timecapsule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.compose.ThemeType

enum class OnLaunch {
    ADD_NOTE_DIALOG,
    LOWEST_SCORE_NOTE,
    RANDOM_NOTE,
    NOTE_LIST,
}

data class Choice(
    val title: String,
    val value: Any
)

@Composable
fun SettingItemToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun <T> SettingItemChoice(
    title: String,
    dialogTitle: String = title,
    choices: List<Choice>,
    currentChoice: T,
    onChoiceChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var showChoiceDialog by remember { mutableStateOf(false) }
    var pendingChoice by remember { mutableStateOf(currentChoice) }

    // Show the choice dialog when needed
    if (showChoiceDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showChoiceDialog = false },
            title = { Text(dialogTitle) },
            text = {
                Column {
                    choices.forEach { choice ->
                        ListItem(
                            headlineContent = { Text(choice.title) },
                            leadingContent = {
                                RadioButton(
                                    selected = choice.value == pendingChoice,
                                    onClick = {
                                        @Suppress("UNCHECKED_CAST")
                                        pendingChoice = choice.value as T
                                    }
                                )
                            },
                            modifier = Modifier.padding(vertical = 0.dp),
                        )
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(onClick = { showChoiceDialog = false }) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        showChoiceDialog = false
                        if (pendingChoice != currentChoice) {
                            onChoiceChange(pendingChoice)
                        }
                    }) {
                        Text("Confirm")
                    }
                }
            }
        )
    }

    // The setting item UI
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                pendingChoice = currentChoice
                showChoiceDialog = true
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.widthIn(min = 80.dp, max = 150.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.width(16.dp))
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = choices.find { it.value == currentChoice }?.title ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = Int.MAX_VALUE,
                softWrap = true
            )
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier.widthIn(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Select $title",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onLaunch: OnLaunch = OnLaunch.ADD_NOTE_DIALOG,
    onLaunchChange: (OnLaunch) -> Unit = {},
    themeType: ThemeType = ThemeType.Default,
    onThemeTypeChange: (ThemeType) -> Unit = {},
) {
    var selectedOnLaunch by remember { mutableStateOf(onLaunch) }
    var selectedTheme by remember { mutableStateOf(themeType) }
    // Example toggle state
    var notificationsEnabled by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .heightIn(max = 600.dp)
                    .padding(vertical = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    SettingItemChoice(
                        title = "Upon launch",
                        choices = listOf(
                            Choice("New note", OnLaunch.ADD_NOTE_DIALOG),
                            Choice("Deepest buried note", OnLaunch.LOWEST_SCORE_NOTE),
                            Choice("Random note", OnLaunch.RANDOM_NOTE),
                            Choice("None", OnLaunch.NOTE_LIST)
                        ),
                        currentChoice = selectedOnLaunch,
                        onChoiceChange = { newSetting ->
                            selectedOnLaunch = newSetting
                            onLaunchChange(newSetting)
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    SettingItemChoice(
                        title = "Theme",
                        choices = listOf(
                            Choice("Default", ThemeType.Default),
                            Choice("Blue", ThemeType.Blue),
                            Choice("Red", ThemeType.Red),
                            Choice("Green", ThemeType.Green),
                            Choice("Purple", ThemeType.Purple),
                        ),
                        currentChoice = selectedTheme,
                        onChoiceChange = { newTheme ->
                            selectedTheme = newTheme
                            onThemeTypeChange(newTheme)
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    SettingItemToggle(
                        title = "Enable notifications",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
