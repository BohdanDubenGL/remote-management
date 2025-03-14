package com.globallogic.rdkb.remotemanagement.domain.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.format

data class WifiMotionEvent(
    val eventId: Int,
    val deviceMacAddress: String,
    val type: String,
    val time: String,
) {
    val formattedTime = Instant.parse(time)
        .toLocalDateTime(TimeZone.UTC)
        .format(dateTimeFormat)

    companion object {
        private val dateTimeFormat = LocalDateTime.Format {
            dayOfMonth()
            char(' ')
            monthName(MonthNames.ENGLISH_FULL)
            char(' ')
            year()
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    }
}
