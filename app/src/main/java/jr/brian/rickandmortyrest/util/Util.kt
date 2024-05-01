package jr.brian.rickandmortyrest.util

import java.text.SimpleDateFormat
import java.util.Locale

private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
private val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

fun String.formatDate(): String {
    inputFormat.parse(this)?.let {
        return outputFormat.format(it)
    }
    return this
}