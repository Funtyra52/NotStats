package com.m1kunyastudio.notstats.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.m1kunyastudio.notstats.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    initialTwitchUrl: String,
    initialYoutubeUrl: String,
    initialYoutubeApiKey: String,
    initialLanguage: String,
    initialTheme: String,
    onLanguageChange: (String) -> Unit,
    onThemeChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var twitchUrl by remember { mutableStateOf(initialTwitchUrl) }
    var youtubeUrl by remember { mutableStateOf(initialYoutubeUrl) }
    var youtubeApiKey by remember { mutableStateOf(initialYoutubeApiKey) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings), color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(R.string.data_connections),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingsCard(label = "Twitch URL", value = twitchUrl, onValueChange = { twitchUrl = it }, placeholder = "https://twitch.tv/username")
            Spacer(modifier = Modifier.height(12.dp))
            SettingsCard(label = "YouTube URL", value = youtubeUrl, onValueChange = { youtubeUrl = it }, placeholder = "https://youtube.com/@username")

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.api_keys),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingsCard(label = "YouTube API Key", value = youtubeApiKey, onValueChange = { youtubeApiKey = it }, placeholder = "AIzaSy...")

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Language Selection
            SelectorCard(
                label = stringResource(R.string.language),
                options = listOf("system", "en", "ru"),
                optionLabels = listOf(stringResource(R.string.theme_system), stringResource(R.string.lang_en), stringResource(R.string.lang_ru)),
                selectedOption = initialLanguage,
                onOptionSelected = onLanguageChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Theme Selection
            SelectorCard(
                label = stringResource(R.string.theme),
                options = listOf("system", "light", "dark"),
                optionLabels = listOf(stringResource(R.string.theme_system), stringResource(R.string.theme_light), stringResource(R.string.theme_dark)),
                selectedOption = initialTheme,
                onOptionSelected = onThemeChange
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    onSave(twitchUrl, youtubeUrl, youtubeApiKey)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SelectorCard(label: String, options: List<String>, optionLabels: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEachIndexed { index, option ->
                    val isSelected = selectedOption == option
                    Button(
                        onClick = { onOptionSelected(option) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(optionLabels[index], style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCard(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
        }
    }
}
