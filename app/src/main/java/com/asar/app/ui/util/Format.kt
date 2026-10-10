package com.asar.app.ui.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dayFormat = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH)
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)

fun LocalDate.relativeDayLabel(today: LocalDate = LocalDate.now()): String = when (this) {
    today -> "Today"
    today.plusDays(1) -> "Tomorrow"
    today.minusDays(1) -> "Yesterday"
    else -> format(dayFormat)
}

fun LocalDateTime.friendlyDateTime(): String =
    "${toLocalDate().relativeDayLabel()} · ${format(timeFormat)}"

fun LocalDateTime.timeLabel(): String = format(timeFormat)

fun LocalDateTime.agoLabel(now: LocalDateTime = LocalDateTime.now()): String {
    val minutes = Duration.between(this, now).toMinutes().coerceAtLeast(0)
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 60 * 24 -> "${minutes / 60}h ago"
        else -> "${minutes / (60 * 24)}d ago"
    }
}

fun Double.km(): String =
    if (this < 1) "${(this * 1000).toInt()} m" else String.format(Locale.ENGLISH, "%.1f km", this)

fun Double.oneDecimal(): String = String.format(Locale.ENGLISH, "%.1f", this)

fun String.initials(): String =
    split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
