package com.m1kunyastudio.notstats.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.m1kunyastudio.notstats.R

@Composable
fun SetupScreen(
    currentLanguage: String = "system",
    currentTheme: String = "system",
    onLanguageChange: (String) -> Unit,
    onThemeChange: (String) -> Unit,
    onComplete: (String, String, String) -> Unit
) {
    var twitchUrl by remember { mutableStateOf("") }
    var youtubeUrl by remember { mutableStateOf("") }
    var youtubeApiKey by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.setup_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        SettingsCard(
            label = "Twitch URL",
            value = twitchUrl,
            onValueChange = { twitchUrl = it },
            placeholder = "https://twitch.tv/username"
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(
            label = "YouTube URL",
            value = youtubeUrl,
            onValueChange = { youtubeUrl = it },
            placeholder = "https://youtube.com/@username"
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(
            label = "YouTube API Key",
            value = youtubeApiKey,
            onValueChange = { youtubeApiKey = it },
            placeholder = "AIzaSy..."
        )

        TextButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://console.cloud.google.com/apis/library/youtube.googleapis.com"))
                context.startActivity(intent)
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.youtube_api_hint),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.app_settings),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            textAlign = TextAlign.Start
        )

        SelectorCard(
            label = stringResource(R.string.language),
            options = listOf("system", "en", "ru"),
            optionLabels = listOf(stringResource(R.string.theme_system), stringResource(R.string.lang_en), stringResource(R.string.lang_ru)),
            selectedOption = currentLanguage,
            onOptionSelected = onLanguageChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        SelectorCard(
            label = stringResource(R.string.theme),
            options = listOf("system", "light", "dark"),
            optionLabels = listOf(stringResource(R.string.theme_system), stringResource(R.string.theme_light), stringResource(R.string.theme_dark)),
            selectedOption = currentTheme,
            onOptionSelected = onThemeChange
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { onComplete(twitchUrl, youtubeUrl, youtubeApiKey) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(R.string.save),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
