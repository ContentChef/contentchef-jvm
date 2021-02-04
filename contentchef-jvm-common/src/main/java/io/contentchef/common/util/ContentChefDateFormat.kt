package io.contentchef.common.util

import io.contentchef.common.util.ContentChefDateFormat.DATE_TIME_PATTERN
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Handles dates using the ContentChef [DATE_TIME_PATTERN]
 */
object ContentChefDateFormat {

    private const val DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    private val LOCALE = Locale.US

    private fun getDateFormat(): SimpleDateFormat {
        return SimpleDateFormat(DATE_TIME_PATTERN, LOCALE)
    }

    @JvmStatic
    fun formatDate(date: Date): String {
        return getDateFormat().format(date)
    }

    @JvmStatic
    fun parseDate(stringDate: String): Date? {
        return try {
            getDateFormat().parse(stringDate)
        } catch (e: ParseException) {
            null
        }
    }

}