package com.m1kunyastudio.notstats

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.m1kunyastudio.notstats.ui.StatsScreen
import com.m1kunyastudio.notstats.ui.SettingsScreen
import com.m1kunyastudio.notstats.ui.SetupScreen
import com.m1kunyastudio.notstats.ui.theme.NotstatsTheme
import com.m1kunyastudio.notstats.data.SettingsManager
import com.m1kunyastudio.notstats.data.TwitchRepository
import com.m1kunyastudio.notstats.data.YouTubeRepository
import com.m1kunyastudio.notstats.ui.StatItem
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager
    private val twitchRepository = TwitchRepository()
    private val youtubeRepository = YouTubeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        
        enableEdgeToEdge()
        setContent {
            val savedTwitchUrl by settingsManager.twitchUrl.collectAsState()
            val savedYoutubeUrl by settingsManager.youtubeUrl.collectAsState()
            val savedYoutubeApiKey by settingsManager.youtubeApiKey.collectAsState()
            val savedLanguage by settingsManager.language.collectAsState()
            val savedTheme by settingsManager.theme.collectAsState()
            
            // Manual language application
            LaunchedEffect(savedLanguage) {
                val locale = if (savedLanguage == "system") Locale.getDefault() else Locale(savedLanguage)
                val config = Configuration(resources.configuration)
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }

            NotstatsTheme(themePreference = savedTheme) {
                var currentScreen by rememberSaveable { mutableStateOf("") }
                
                LaunchedEffect(savedTwitchUrl, savedYoutubeUrl, savedYoutubeApiKey) {
                    if (currentScreen == "") {
                        currentScreen = if (savedTwitchUrl.isEmpty() && savedYoutubeUrl.isEmpty() && savedYoutubeApiKey.isEmpty()) {
                            "setup"
                        } else {
                            "stats"
                        }
                    }
                }

                var twitchStats by remember { mutableStateOf<List<StatItem>>(emptyList()) }
                var youtubeStats by remember { mutableStateOf<List<StatItem>>(emptyList()) }
                var isRefreshing by remember { mutableStateOf(false) }

                val twitchUsername = remember(savedTwitchUrl) {
                    savedTwitchUrl.trim().substringAfterLast("/").substringBefore("?")
                }

                val youtubeInput = remember(savedYoutubeUrl) {
                    val trimmed = savedYoutubeUrl.trim().removeSuffix("/")
                    trimmed.substringAfterLast("/")
                }

                suspend fun fetchAllStats() {
                    isRefreshing = true
                    if (twitchUsername.isNotEmpty() && !twitchUsername.contains("twitch.tv")) {
                        val followers = twitchRepository.getFollowers(twitchUsername)
                        val status = twitchRepository.getStatus(twitchUsername)
                        val viewers = twitchRepository.getViewers(twitchUsername)
                        val uptime = twitchRepository.getUptime(twitchUsername)
                        val game = twitchRepository.getGame(twitchUsername)
                        val title = twitchRepository.getTitle(twitchUsername)
                        val age = twitchRepository.getAccountAge(twitchUsername)
                        
                        twitchStats = listOf(
                            StatItem("Status", status),
                            StatItem("Followers", followers),
                            StatItem("Viewers", viewers),
                            StatItem("Uptime", uptime),
                            StatItem("Category", game),
                            StatItem("Account Age", age),
                            StatItem("Title", title)
                        )
                    }

                    if (youtubeInput.isNotEmpty() && !youtubeInput.contains("youtube.com") && savedYoutubeApiKey.isNotEmpty()) {
                        val data = youtubeRepository.getChannelStats(youtubeInput, savedYoutubeApiKey)
                        if (data != null) {
                            youtubeStats = listOf(
                                StatItem("Subscribers", data["Subscribers"] ?: "0"),
                                StatItem("Total Videos", data["Total Videos"] ?: "0"),
                                StatItem("Total Views", data["Total Views"] ?: "0"),
                                StatItem("Latest Video", data["Latest Video"] ?: "No Video")
                            )
                        } else {
                            youtubeStats = emptyList()
                        }
                    }
                    isRefreshing = false
                }

                LaunchedEffect(twitchUsername, youtubeInput, savedYoutubeApiKey) {
                    fetchAllStats()
                }

                val scope = rememberCoroutineScope()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        "setup" -> SetupScreen(
                            currentLanguage = savedLanguage,
                            currentTheme = savedTheme,
                            onLanguageChange = { 
                                settingsManager.saveLanguage(it) 
                                recreate()
                            },
                            onThemeChange = { settingsManager.saveTheme(it) },
                            onComplete = { twitch, youtube, key ->
                                settingsManager.saveTwitchUrl(twitch)
                                settingsManager.saveYoutubeUrl(youtube)
                                settingsManager.saveYoutubeApiKey(key)
                                currentScreen = "stats"
                                recreate()
                            }
                        )
                        "stats" -> StatsScreen(
                            modifier = Modifier.padding(innerPadding),
                            twitchUrl = savedTwitchUrl,
                            youtubeUrl = savedYoutubeUrl,
                            twitchStats = twitchStats,
                            youtubeStats = youtubeStats,
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                scope.launch { fetchAllStats() }
                            },
                            onSettingsClick = { currentScreen = "settings" }
                        )
                        "settings" -> SettingsScreen(
                            initialTwitchUrl = savedTwitchUrl,
                            initialYoutubeUrl = savedYoutubeUrl,
                            initialYoutubeApiKey = savedYoutubeApiKey,
                            initialLanguage = savedLanguage,
                            initialTheme = savedTheme,
                            onLanguageChange = { 
                                settingsManager.saveLanguage(it) 
                                recreate()
                            },
                            onThemeChange = { settingsManager.saveTheme(it) },
                            onBack = { currentScreen = "stats" },
                            onSave = { newTwitch, newYoutube, newKey -> 
                                settingsManager.saveTwitchUrl(newTwitch)
                                settingsManager.saveYoutubeUrl(newYoutube)
                                settingsManager.saveYoutubeApiKey(newKey)
                            }
                        )
                    }
                }
            }
        }
    }
}
