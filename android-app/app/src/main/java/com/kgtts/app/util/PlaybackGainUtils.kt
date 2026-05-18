package com.lhtstudio.kigtts.app.util

internal const val PLAYBACK_GAIN_SNAP_TARGET = 100
internal const val PLAYBACK_GAIN_SNAP_RANGE = 20

internal fun snapPlaybackGainPercent(percent: Int): Int {
    val clamped = percent.coerceIn(0, 1000)
    return if (kotlin.math.abs(clamped - PLAYBACK_GAIN_SNAP_TARGET) <= PLAYBACK_GAIN_SNAP_RANGE) {
        PLAYBACK_GAIN_SNAP_TARGET
    } else {
        clamped
    }
}
