package com.m1kunyastudio.notstats.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    
    private val _twitchUrl = MutableStateFlow(prefs.getString("twitch_url", "") ?: "")
    val twitchUrl: StateFlow<String> = _twitchUrl

    private val _youtubeUrl = MutableStateFlow(prefs.getString("youtube_url", "") ?: "")
    val youtubeUrl: StateFlow<String> = _youtubeUrl

    private val _youtubeApiKey = MutableStateFlow(prefs.getString("youtube_api_key", "") ?: "")
    val youtubeApiKey: StateFlow<String> = _youtubeApiKey

    private val _language = MutableStateFlow(prefs.getString("language", "system") ?: "system")
    val language: StateFlow<String> = _language

    private val _theme = MutableStateFlow(prefs.getString("theme", "system") ?: "system")
    val theme: StateFlow<String> = _theme

    fun saveTwitchUrl(url: String) {
        prefs.edit().putString("twitch_url", url).apply()
        _twitchUrl.value = url
    }

    fun saveYoutubeUrl(url: String) {
        prefs.edit().putString("youtube_url", url).apply()
        _youtubeUrl.value = url
    }

    fun saveYoutubeApiKey(key: String) {
        prefs.edit().putString("youtube_api_key", key).apply()
        _youtubeApiKey.value = key
    }

    fun saveLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
        _language.value = lang
    }

    fun saveTheme(theme: String) {
        prefs.edit().putString("theme", theme).apply()
        _theme.value = theme
    }
}
