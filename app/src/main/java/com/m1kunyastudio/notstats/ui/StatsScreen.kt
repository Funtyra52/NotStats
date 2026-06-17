package com.m1kunyastudio.notstats.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.m1kunyastudio.notstats.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

data class StatItem(val label: String, val value: String)

fun formatValue(label: String, value: String): String {
    val longValue = value.toLongOrNull() ?: return value
    
    val useAbbreviation = label == "Subscribers" || label == "Followers" || label == "Viewers"
    
    return if (useAbbreviation) {
        when {
            longValue >= 1_000_000_000 -> String.format(Locale.US, "%.1fB", longValue / 1_000_000_000.0)
            longValue >= 1_000_000 -> String.format(Locale.US, "%.1fM", longValue / 1_000_000.0)
            longValue >= 1_000 -> String.format(Locale.US, "%.1fK", longValue / 1_000.0)
            else -> longValue.toString()
        }
    } else {
        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.format(longValue)
    }
}

@Composable
fun getLocalizedLabel(label: String): String {
    return when (label) {
        "Subscribers" -> stringResource(R.string.subscribers)
        "Followers" -> stringResource(R.string.followers)
        "Viewers" -> stringResource(R.string.viewers)
        "Uptime" -> stringResource(R.string.uptime)
        "Category" -> stringResource(R.string.category)
        "Account Age" -> stringResource(R.string.account_age)
        "Title" -> stringResource(R.string.title)
        "Total Videos" -> stringResource(R.string.total_videos)
        "Total Views" -> stringResource(R.string.total_views)
        "Latest Video" -> stringResource(R.string.latest_video)
        "Status" -> stringResource(R.string.status)
        "Loading..." -> stringResource(R.string.loading)
        else -> label
    }
}

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    twitchUrl: String = "",
    youtubeUrl: String = "",
    twitchStats: List<StatItem> = emptyList(),
    youtubeStats: List<StatItem> = emptyList(),
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Twitch") }
    
    val profileUrl = if (selectedTab == "Twitch") twitchUrl else youtubeUrl
    val hasData = profileUrl.isNotEmpty()
    
    val currentStats = if (selectedTab == "Twitch") {
        if (hasData && twitchStats.isNotEmpty()) {
            twitchStats
        } else {
            listOf(
                StatItem("Status", if (twitchUrl.isEmpty()) stringResource(R.string.no_channel) else stringResource(R.string.loading))
            )
        }
    } else {
        if (hasData && youtubeStats.isNotEmpty()) {
            youtubeStats
        } else {
            listOf(
                StatItem("Status", if (youtubeUrl.isEmpty()) stringResource(R.string.no_channel) else stringResource(R.string.loading))
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "refresh")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedTab == "Twitch") stringResource(R.string.twitch_stats) else stringResource(R.string.youtube_stats),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row {
                    IconButton(onClick = onRefresh, enabled = !isRefreshing) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = if (isRefreshing) Modifier.rotate(rotation) else Modifier
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            
            Text(
                text = if (profileUrl.isEmpty()) stringResource(R.string.no_channel) else profileUrl,
                style = MaterialTheme.typography.bodySmall,
                color = if (hasData) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = currentStats,
                    span = { item ->
                        val isFullWidth = item.label == "Category" || 
                                         item.label == "Account Age" || 
                                         item.label == "Title" ||
                                         item.label == "Latest Video" ||
                                         item.label == "Total Views"
                        if (isFullWidth) GridItemSpan(2) else GridItemSpan(1)
                    }
                ) { stat ->
                    StatCard(stat)
                }
                
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        PillNavigation(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
fun StatCard(stat: StatItem) {
    val isFullWidth = stat.label == "Category" || 
                     stat.label == "Account Age" || 
                     stat.label == "Title" ||
                     stat.label == "Latest Video" ||
                     stat.label == "Total Views"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().then(
            if (isFullWidth) Modifier.wrapContentHeight() else Modifier.height(100.dp)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = getLocalizedLabel(stat.label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = formatValue(stat.label, stat.value),
                style = if (isFullWidth) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (isFullWidth) 3 else 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PillNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Twitch", "YouTube").forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}
