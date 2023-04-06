package com.github.nailkhaf.feature.transfers

import java.text.DateFormat
import java.util.*

internal actual fun formatDate(timestamp: ULong): String {
    return DateFormat.getDateInstance().format(Date(timestamp.toLong() * 1000))
}