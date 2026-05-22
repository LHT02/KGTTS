package com.lhtstudio.kigtts.app.data

import android.content.Context
import com.lhtstudio.kigtts.app.util.AppLogger
import java.io.File

object ResourceStorageCleaner {
    private const val SHARE_MAX_AGE_MS = 24L * 60L * 60L * 1000L
    private const val TEMP_MAX_AGE_MS = 24L * 60L * 60L * 1000L
    private const val TTS_TEMP_MAX_AGE_MS = 60L * 60L * 1000L

    fun cleanupTransientStorage(context: Context) {
        cleanupShareCache(context)
        cleanupCacheChildren(
            root = context.cacheDir,
            maxAgeMs = TEMP_MAX_AGE_MS,
            nameMatches = { name ->
                name.startsWith("soundboard_import_") ||
                    name.startsWith("voice-avatar-")
            }
        )
        cleanupCacheChildren(
            root = File(context.cacheDir, "recognition_resources"),
            maxAgeMs = TEMP_MAX_AGE_MS,
            nameMatches = { name ->
                name.startsWith("download-") ||
                    name.startsWith("local-")
            }
        )
        cleanupCacheChildren(
            root = File(context.cacheDir, "kokoro_voice"),
            maxAgeMs = TEMP_MAX_AGE_MS,
            nameMatches = { name ->
                name.startsWith("local-") ||
                    name.startsWith(".import-") ||
                    name.startsWith(".download-")
            }
        )
        cleanupCacheChildren(
            root = context.cacheDir,
            maxAgeMs = TTS_TEMP_MAX_AGE_MS,
            nameMatches = { name ->
                name.startsWith("system_tts_") && name.endsWith(".wav")
            }
        )
    }

    fun cleanupShareCache(context: Context) {
        cleanupCacheChildren(
            root = File(context.cacheDir, "share"),
            maxAgeMs = SHARE_MAX_AGE_MS,
            nameMatches = { true }
        )
    }

    private fun cleanupCacheChildren(
        root: File,
        maxAgeMs: Long,
        nameMatches: (String) -> Boolean
    ) {
        val now = System.currentTimeMillis()
        root.listFiles()
            ?.filter { child ->
                nameMatches(child.name) &&
                    child.lastModified() > 0L &&
                    now - child.lastModified() >= maxAgeMs
            }
            ?.forEach { child ->
                runCatching {
                    if (child.isDirectory) {
                        child.deleteRecursively()
                    } else {
                        child.delete()
                    }
                }.onFailure { e ->
                    AppLogger.e("cleanup transient file failed path=${child.absolutePath}", e)
                }
            }
    }
}
