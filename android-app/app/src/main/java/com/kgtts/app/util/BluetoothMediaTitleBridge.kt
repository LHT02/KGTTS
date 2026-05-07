package com.lhtstudio.kigtts.app.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock

object BluetoothMediaTitleBridge {
    private const val SESSION_TAG = "KIGTTS.BluetoothMediaTitle"
    private const val DEFAULT_TITLE = "KIGTTS"
    private const val ARTIST = "KIGTTS"
    private const val ALBUM = "便捷字幕"
    private const val MAX_TITLE_LENGTH = 64

    private val mainHandler = Handler(Looper.getMainLooper())
    private val lock = Any()
    private var enabled = false
    private var session: MediaSession? = null
    private var lastTitle = ""
    private var lastUpdateAtMs = 0L
    private var metadataRevision = 0L

    fun setEnabled(context: Context, value: Boolean, initialTitle: String = "") {
        val title = normalizeTitle(initialTitle).ifBlank { DEFAULT_TITLE }
        val appContext = context.applicationContext
        mainHandler.post {
            synchronized(lock) {
                enabled = value
                if (value) {
                    AppLogger.i("Bluetooth subtitle enabled initialTitle=${title.logPreview()}")
                    publishTitleLocked(appContext, lastTitle.ifBlank { title }, force = true)
                } else {
                    AppLogger.i("Bluetooth subtitle disabled")
                    lastTitle = ""
                    lastUpdateAtMs = 0L
                    session?.isActive = false
                    session?.release()
                    session = null
                }
            }
        }
    }

    fun updateSubtitle(context: Context, text: String) {
        val title = normalizeTitle(text)
        if (title.isBlank()) return
        val appContext = context.applicationContext
        mainHandler.post {
            synchronized(lock) {
                if (!enabled) return@synchronized
                publishTitleLocked(appContext, title, force = true)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun extendAfterPlaybackEnd(context: Context) {
        // Keep this as a no-op compatibility hook. Bluetooth subtitles should be
        // updated by new subtitle commits, not by playback-end refresh callbacks;
        // otherwise delayed audio progress/focus refreshes can republish stale state.
    }

    private fun ensureSessionLocked(context: Context): MediaSession {
        session?.let { return it }
        val created = MediaSession(context.applicationContext, SESSION_TAG).apply {
            @Suppress("DEPRECATION")
            setFlags(
                MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setPlaybackToLocal(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
            }
        }
        session = created
        return created
    }

    private fun publishTitleLocked(context: Context, title: String, force: Boolean) {
        if (!force && title == lastTitle) return
        val activeSession = ensureSessionLocked(context)
        val revision = ++metadataRevision
        val mediaId = "kigtts_subtitle_$revision"
        val queueId = revision
        val description = MediaDescription.Builder()
            .setMediaId(mediaId)
            .setTitle(title)
            .setSubtitle(ALBUM)
            .setDescription(ARTIST)
            .build()
        activeSession.setMetadata(
            MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, ALBUM)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, ARTIST)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, ALBUM)
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, ARTIST)
                .putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, ARTIST)
                .build()
        )
        activeSession.setQueue(listOf(MediaSession.QueueItem(description, queueId)))
        activeSession.setPlaybackState(createPlayingState(queueId))
        activeSession.setQueueTitle(title)
        activeSession.isActive = true
        lastTitle = title
        lastUpdateAtMs = SystemClock.elapsedRealtime()
        AppLogger.i("Bluetooth subtitle publish revision=$revision title=${title.logPreview()}")
    }

    private fun createPlayingState(activeQueueItemId: Long): PlaybackState {
        return PlaybackState.Builder()
            .setActions(
                PlaybackState.ACTION_PLAY or
                    PlaybackState.ACTION_PAUSE or
                    PlaybackState.ACTION_PLAY_PAUSE or
                    PlaybackState.ACTION_STOP
            )
            .setState(
                PlaybackState.STATE_PLAYING,
                0L,
                1f,
                SystemClock.elapsedRealtime()
            )
            .setActiveQueueItemId(activeQueueItemId)
            .build()
    }

    private fun normalizeTitle(text: String): String {
        return text
            .replace(Regex("\\s+"), " ")
            .trim()
            .let { normalized ->
                if (normalized.length <= MAX_TITLE_LENGTH) {
                    normalized
                } else {
                    normalized.take(MAX_TITLE_LENGTH - 1) + "…"
                }
            }
    }

    private fun String.logPreview(): String {
        return replace(Regex("\\s+"), " ")
            .trim()
            .let { if (it.length <= 24) it else it.take(24) + "..." }
    }
}
