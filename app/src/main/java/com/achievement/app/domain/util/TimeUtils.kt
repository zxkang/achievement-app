package com.achievement.app.domain.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtils {
    fun parseISO8601Duration(duration: String): Long {
        val pattern = "PT(\\d+H)?(\\d+M)?(\\d+S)?".toRegex()
        val match = pattern.matchEntire(duration) ?: return 0L
        
        var totalMinutes = 0L
        
        match.groupValues[1].takeIf { it.isNotEmpty() }?.let {
            totalMinutes += it.removeSuffix("H").toLong() * 60
        }
        match.groupValues[2].takeIf { it.isNotEmpty() }?.let {
            totalMinutes += it.removeSuffix("M").toLong()
        }
        
        return totalMinutes
    }
    
    fun formatPlayTime(minutes: Long): String {
        val hours = minutes / 60
        val mins = minutes % 60
        
        return when {
            hours > 0 && mins > 0 -> "${hours}小时${mins}分钟"
            hours > 0 -> "${hours}小时"
            else -> "${mins}分钟"
        }
    }
    
    fun parseISO8601DateTime(dateTime: String): LocalDateTime {
        return try {
            Instant.parse(dateTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
    
    fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return dateTime.format(formatter)
    }
    
    fun formatRelativeTime(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(dateTime, now)
        
        return when {
            duration.toMinutes() < 1 -> "刚刚"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}分钟前"
            duration.toHours() < 24 -> "${duration.toHours()}小时前"
            duration.toDays() < 30 -> "${duration.toDays()}天前"
            duration.toDays() < 365 -> "${duration.toDays() / 30}个月前"
            else -> "${duration.toDays() / 365}年前"
        }
    }
}
