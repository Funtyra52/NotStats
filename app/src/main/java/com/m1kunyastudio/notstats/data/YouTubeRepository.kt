package com.m1kunyastudio.notstats.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class YouTubeRepository {

    private suspend fun makeRequest(urlString: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getChannelStats(handle: String, apiKey: String): Map<String, String>? {
        if (apiKey.isEmpty()) return mapOf("Error" to "Please add API Key in Settings")
        
        val cleanHandle = if (handle.startsWith("@")) handle else "@$handle"
        val encodedHandle = URLEncoder.encode(cleanHandle, "UTF-8")
        
        // 1. Получаем ID канала и основную статистику
        val url = "https://www.googleapis.com/youtube/v3/channels?part=statistics,contentDetails&forHandle=$encodedHandle&key=$apiKey"
        val response = makeRequest(url) ?: return null
        
        try {
            val json = JSONObject(response)
            val items = json.getJSONArray("items")
            if (items.length() == 0) return null
            
            val channel = items.getJSONObject(0)
            val stats = channel.getJSONObject("statistics")
            val contentDetails = channel.getJSONObject("contentDetails")
            val uploadsPlaylistId = contentDetails.getJSONObject("relatedPlaylists").getString("uploads")
            
            val result = mutableMapOf(
                "Subscribers" to stats.getString("subscriberCount"),
                "Total Videos" to stats.getString("videoCount"),
                "Total Views" to stats.getString("viewCount")
            )
            
            // 2. Получаем последнее видео из плейлиста загрузок
            val latestVideoUrl = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=$uploadsPlaylistId&maxResults=1&key=$apiKey"
            val videoResponse = makeRequest(latestVideoUrl)
            if (videoResponse != null) {
                val videoJson = JSONObject(videoResponse)
                val videoItems = videoJson.getJSONArray("items")
                if (videoItems.length() > 0) {
                    val title = videoItems.getJSONObject(0).getJSONObject("snippet").getString("title")
                    result["Latest Video"] = title
                }
            }
            
            return result
        } catch (e: Exception) {
            return null
        }
    }
}
