package cc.sovellus.vrcaa.helper

import kotlin.time.Duration.Companion.seconds

object TimeHelper {

    fun formatDuration(totalSeconds: Long): String
    {
        return totalSeconds.seconds.toComponents { d, h, m, s, _ ->
            listOf(d to "day", h to "hour", m.toLong() to "minute", s.toLong() to "second")
                .filter { it.first.toLong() > 0 }
                .joinToString(", ") { (n, unit) ->
                    "$n $unit${if (n == 1L) "" else "s"}"
                }
        }
    }
}