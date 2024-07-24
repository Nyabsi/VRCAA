package cc.sovellus.vrcaa.extension

import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

internal val LocalDateTime.milliseconds: Long
    get() { return this.toEpochSecond(ZoneOffset.UTC).milliseconds.inWholeMilliseconds }