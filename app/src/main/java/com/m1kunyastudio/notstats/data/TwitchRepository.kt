package com.m1kunyastudio.notstats.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class TwitchRepository {

    private suspend fun fetchText(urlString: String): String? = withContext(Dispatchers.IO) {
        try {
            val cacheBuster = "t=${System.currentTimeMillis()}"
            val finalUrl = if (urlString.contains("?")) "$urlString&$cacheBuster" else "$urlString?$cacheBuster"
            
            val url = URL(finalUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.useCaches = false
            connection.setRequestProperty("Cache-Control", "no-cache")
            connection.setRequestProperty("Pragma", "no-cache")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText().trim() }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFollowers(username: String): String {
        return fetchText("https://decapi.me/twitch/followcount/$username") ?: "0"
    }

    suspend fun getStatus(username: String): String {
        val res = fetchText("https://decapi.me/twitch/uptime/$username")
        return when {
            res == null -> "Offline"
            res.contains("offline", ignoreCase = true) -> "Offline"
            res.isEmpty() -> "Offline"
            else -> "Live"
        }
    }
    
    suspend fun getViewers(username: String): String {
        val res = fetchText("https://decapi.me/twitch/viewercount/$username")
        return when {
            res == null -> "0"
            res.contains("offline", ignoreCase = true) -> "0"
            else -> res
        }
    }

    suspend fun getUptime(username: String): String {
        val res = fetchText("https://decapi.me/twitch/uptime/$username")
        return when {
            res == null -> "0h 0m"
            res.contains("offline", ignoreCase = true) -> "0h 0m"
            else -> {
                res.replace(" hours", "h")
                   .replace(" hour", "h")
                   .replace(" minutes", "m")
                   .replace(" minute", "m")
                   .replace(" seconds", "s")
                   .replace(" second", "s")
                   .replace(",", "")
            }
        }
    }

    suspend fun getGame(username: String): String {
        return fetchText("https://decapi.me/twitch/game/$username") ?: "Unknown"
    }

    suspend fun getTitle(username: String): String {
        return fetchText("https://decapi.me/twitch/title/$username") ?: "No Title"
    }

    suspend fun getAccountAge(username: String): String {
        val res = fetchText("https://decapi.me/twitch/accountage/$username") ?: "Unknown"
        return res.replace(" years", "y")
                  .replace(" year", "y")
                  .replace(" months", "mo")
                  .replace(" month", "mo")
                  .replace(" days", "d")
                  .replace(" day", "d")
                  .replace(",", "")
    }
}
