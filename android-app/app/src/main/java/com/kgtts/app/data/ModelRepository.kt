package com.lhtstudio.kigtts.app.data

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.provider.OpenableColumns
import com.lhtstudio.kigtts.app.util.AppLogger
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

const val SYSTEM_TTS_VOICE_NAME = "__system_tts__"
private const val SYSTEM_TTS_VIRTUAL_DIR_NAME = "__system_tts_virtual__"

fun isSystemTtsVoiceDir(dir: File?): Boolean {
    return dir?.name == SYSTEM_TTS_VIRTUAL_DIR_NAME
}

data class ModelPaths(
    val asrDir: File?,
    val voiceDir: File?
)

data class VoicePackMeta(
    val name: String = "未命名",
    val remark: String = "",
    val avatar: String = "avatar.png",
    val pinned: Boolean = false,
    val order: Long = System.currentTimeMillis()
)

data class VoicePackInfo(
    val dir: File,
    val meta: VoicePackMeta
)

class ModelRepository(private val context: Context) {
    private val root = File(context.filesDir, "models")
    private val asrRoot = File(root, "asr")
    private val voiceRoot = File(root, "voice")
    private val recognitionResources = RecognitionResourceRepository(context)
    private val kokoroVoice = KokoroVoiceRepository(context)

    init {
        root.mkdirs()
        asrRoot.mkdirs()
        voiceRoot.mkdirs()
        cleanupStaleVoiceImports()
    }

    fun importAsr(uri: Uri, resolver: ContentResolver): File {
        val targetDir = File(asrRoot, safeName(uri))
        targetDir.mkdirs()
        AppLogger.i("importAsr uri=$uri target=${targetDir.absolutePath}")
        unzipToDir(uri, resolver, targetDir)
        AppLogger.i("importAsr done target=${targetDir.absolutePath}")
        return targetDir
    }

    fun importVoice(uri: Uri, resolver: ContentResolver): File {
        val targetDir = File(voiceRoot, safeName(uri, resolver))
        val importDir = File(voiceRoot, ".import-${System.currentTimeMillis()}")
        val preservedMeta = readVoiceMeta(targetDir)
        val preservedAvatar = preservedMeta
            ?.avatar
            ?.takeIf { it.isNotBlank() }
            ?.let { File(targetDir, it) }
            ?.takeIf { it.isFile }
            ?.let { source ->
                File(context.cacheDir, "voice-avatar-${System.currentTimeMillis()}.tmp").also { temp ->
                    source.copyTo(temp, overwrite = true)
                }
            }
        if (importDir.exists()) {
            importDir.deleteRecursively()
        }
        importDir.mkdirs()
        AppLogger.i("importVoice uri=$uri temp=${importDir.absolutePath} target=${targetDir.absolutePath}")
        try {
            unzipToDir(uri, resolver, importDir)
            validateVoicePack(importDir)
            if (targetDir.exists()) {
                targetDir.deleteRecursively()
            }
            importDir.parentFile?.mkdirs()
            val moved = importDir.renameTo(targetDir)
            if (!moved) {
                importDir.copyRecursively(targetDir, overwrite = true)
                importDir.deleteRecursively()
            }
            if (preservedMeta != null) {
                val avatarName = sanitizeMetaFileName(preservedMeta.avatar, "avatar.png")
                if (preservedAvatar?.isFile == true) {
                    preservedAvatar.copyTo(File(targetDir, avatarName), overwrite = true)
                }
                saveVoiceMeta(targetDir, preservedMeta.copy(avatar = avatarName))
            } else {
                ensureVoiceMeta(targetDir)
            }
            AppLogger.i("importVoice done target=${targetDir.absolutePath}")
            return targetDir
        } catch (e: Exception) {
            importDir.deleteRecursively()
            AppLogger.e("importVoice failed uri=$uri", e)
            throw e
        } finally {
            preservedAvatar?.delete()
        }
    }

    fun listVoicePacks(): List<VoicePackInfo> {
        val dirs = voiceRoot.listFiles()?.filter { it.isDirectory } ?: emptyList()
        val infos = dirs.map { dir ->
            VoicePackInfo(dir, ensureVoiceMeta(dir))
        }
        return infos.sortedWith(
            compareByDescending<VoicePackInfo> { it.meta.pinned }
                .thenBy { it.meta.order }
                .thenBy { it.meta.name }
        )
    }

    fun systemTtsVirtualDir(): File = File(root, SYSTEM_TTS_VIRTUAL_DIR_NAME)

    fun kokoroVoiceDir(): File = kokoroVoice.voiceDir()

    fun kokoroVoiceStatus(): KokoroVoiceStatus = kokoroVoice.status()

    fun installKokoroVoice(
        uri: Uri,
        resolver: ContentResolver,
        onProgress: (RecognitionResourceProgress) -> Unit
    ): KokoroVoiceStatus {
        return kokoroVoice.installFromUri(uri, resolver, onProgress)
    }

    fun downloadKokoroVoice(
        url: String,
        onProgress: (RecognitionResourceProgress) -> Unit
    ): KokoroVoiceStatus {
        return kokoroVoice.downloadAndInstall(url, onProgress)
    }

    fun deleteKokoroVoice() {
        kokoroVoice.delete()
    }

    fun resolveAsr(name: String): File? {
        val dir = File(asrRoot, name)
        return if (dir.isDirectory) dir else null
    }

    fun recognitionResourceStatus(): RecognitionResourceStatus = recognitionResources.status()

    fun installRecognitionResources(
        uri: Uri,
        resolver: ContentResolver,
        onProgress: (RecognitionResourceProgress) -> Unit
    ): RecognitionResourceStatus {
        return recognitionResources.installFromUri(uri, resolver, onProgress)
    }

    fun downloadRecognitionResources(
        url: String,
        onProgress: (RecognitionResourceProgress) -> Unit
    ): RecognitionResourceStatus {
        return recognitionResources.downloadAndInstall(url, onProgress)
    }

    fun resolveVoicePack(name: String): File? {
        val dir = File(voiceRoot, name)
        return if (dir.isDirectory) dir else null
    }

    fun saveVoiceMeta(dir: File, meta: VoicePackMeta) {
        val normalized = normalizeVoiceMeta(dir, meta)
        val file = metaFile(dir)
        val json = JSONObject().apply {
            put("name", normalized.name)
            put("remark", normalized.remark)
            put("avatar", normalized.avatar)
            put("pinned", normalized.pinned)
            put("order", normalized.order)
        }
        writeTextAtomically(file, json.toString(2))
        ensureAvatar(dir, normalized.avatar)
    }

    fun updateVoiceMeta(dir: File, updater: (VoicePackMeta) -> VoicePackMeta) {
        val current = ensureVoiceMeta(dir)
        val next = updater(current)
        saveVoiceMeta(dir, next)
    }

    fun updateVoiceAvatar(dir: File, resolver: ContentResolver, uri: Uri, fileName: String = "avatar.png") {
        val safeFileName = sanitizeMetaFileName(fileName, "avatar.png")
        val bitmap = decodeAvatarBitmap(resolver, uri)
            ?: throw IOException("无法读取头像图片")
        val out = File(dir, safeFileName)
        val tmp = File(dir, "$safeFileName.tmp")
        tmp.parentFile?.mkdirs()
        FileOutputStream(tmp).use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        if (out.exists() && !out.delete()) {
            throw IOException("无法更新头像文件")
        }
        if (!tmp.renameTo(out)) {
            tmp.copyTo(out, overwrite = true)
            tmp.delete()
        }
        updateVoiceMeta(dir) { meta ->
            meta.copy(avatar = safeFileName)
        }
    }

    fun deleteVoicePack(dir: File) {
        val target = requireManagedVoicePackDir(dir)
        if (!target.exists()) return
        target.deleteRecursively()
    }

    fun zipVoicePack(dir: File, outZip: File) {
        outZip.parentFile?.mkdirs()
        ensureVoiceMeta(dir)
        ZipOutputStream(FileOutputStream(outZip)).use { zos ->
            dir.walkTopDown().forEach { file ->
                if (file.isDirectory) return@forEach
                val entryName = dir.toPath().relativize(file.toPath()).toString().replace('\\', '/')
                if (entryName == "${META_FILE_NAME}.tmp" || entryName == "${META_FILE_NAME}.bak") return@forEach
                val entry = ZipEntry(entryName)
                zos.putNextEntry(entry)
                file.inputStream().use { input -> input.copyTo(zos) }
                zos.closeEntry()
            }
        }
    }

    fun sanitizeVoicePackShareName(name: String, fallback: String): String {
        val sanitized = name
            .trim()
            .ifEmpty { fallback }
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .trim('.')
            .ifEmpty { fallback.ifBlank { "voicepack" } }
        return sanitized
    }

    fun ensureBundledAsr(): File? {
        recognitionResources.installedAsrDir()?.let { dir ->
            AppLogger.i("recognitionResourceAsr present: ${dir.absolutePath}")
            return dir
        }
        AppLogger.e("recognitionResourceAsr missing; external resource package required")
        return null
    }

    private fun requireManagedVoicePackDir(dir: File): File {
        val canonicalTarget = dir.canonicalFile
        val canonicalRoot = voiceRoot.canonicalFile
        val rootPath = canonicalRoot.path
        val targetPath = canonicalTarget.path
        val withinRoot = targetPath == rootPath || targetPath.startsWith("$rootPath${File.separator}")
        if (!withinRoot || canonicalTarget == canonicalRoot) {
            throw SecurityException("非法语音包目录：${dir.absolutePath}")
        }
        return canonicalTarget
    }

    private fun safeName(uri: Uri, resolver: ContentResolver? = null): String {
        val last = displayName(uri, resolver)
            ?: uri.lastPathSegment?.substringAfterLast('/')
            ?: "package"
        return stripArchiveSuffix(last).ifBlank { "package" }
    }

    private fun unzipToDir(uri: Uri, resolver: ContentResolver, outDir: File) {
        resolver.openInputStream(uri)?.use { stream ->
            ZipInputStream(stream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val outPath = entryOutputFile(outDir, entry)
                    if (entry.isDirectory) {
                        outPath.mkdirs()
                    } else {
                        outPath.parentFile?.mkdirs()
                        FileOutputStream(outPath).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
    }

    private fun displayName(uri: Uri, resolver: ContentResolver?): String? {
        if (resolver == null || uri.scheme != ContentResolver.SCHEME_CONTENT) return null
        val cursor: Cursor = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?: return null
        cursor.use {
            if (!it.moveToFirst()) return null
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx < 0) return null
            return it.getString(idx)
        }
    }

    private fun stripArchiveSuffix(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.endsWith(".kigvpk") -> name.dropLast(7)
            lower.endsWith(".zip") -> name.dropLast(4)
            else -> name
        }
    }

    private fun isValidVoicePackDir(dir: File): Boolean {
        return try {
            validateVoicePack(dir)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun entryOutputFile(outDir: File, entry: ZipEntry): File {
        val normalized = entry.name
            .replace('\\', '/')
            .trim()
            .removePrefix("/")
        if (normalized.isBlank()) {
            throw IOException("无效压缩包条目：空路径")
        }
        val outPath = File(outDir, normalized)
        val canonicalRoot = outDir.canonicalFile
        val canonicalOut = outPath.canonicalFile
        if (
            canonicalOut != canonicalRoot &&
            !canonicalOut.path.startsWith("${canonicalRoot.path}${File.separator}")
        ) {
            throw IOException("无效压缩包条目：${entry.name}")
        }
        return canonicalOut
    }

    private fun validateVoicePack(dir: File) {
        val manifestFile = File(dir, "manifest.json")
        if (!manifestFile.isFile) {
            throw IOException("无效语音包：缺少 manifest.json")
        }
        val json = try {
            JSONObject(manifestFile.readText(Charsets.UTF_8))
        } catch (e: Exception) {
            throw IOException("无效语音包：manifest.json 解析失败", e)
        }
        val files = json.optJSONObject("files")
            ?: throw IOException("无效语音包：manifest.json 缺少 files 配置")
        val requiredPaths = listOf(
            files.optString("model"),
            files.optString("config"),
            files.optString("phonemizer")
        )
        val requiredNames = listOf("model", "config", "phonemizer")
        requiredPaths.forEachIndexed { index, relPath ->
            val normalized = relPath.replace('\\', '/').trim().removePrefix("/")
            if (normalized.isBlank()) {
                throw IOException("无效语音包：manifest.files.${requiredNames[index]} 为空")
            }
            val target = File(dir, normalized)
            if (!target.isFile) {
                throw IOException("无效语音包：缺少必要文件 ${normalized}")
            }
        }
    }

    private fun metaFile(dir: File): File = File(dir, META_FILE_NAME)

    private fun backupMetaFile(dir: File): File = File(dir, "$META_FILE_NAME.bak")

    private fun ensureVoiceMeta(dir: File): VoicePackMeta {
        val parsed = readVoiceMeta(dir)
            ?: readVoiceMetaFile(backupMetaFile(dir))
            ?: deriveVoiceMeta(dir)
        val normalized = normalizeVoiceMeta(dir, parsed)
        saveVoiceMeta(dir, normalized)
        return normalized
    }

    private fun ensureAvatar(dir: File, fileName: String) {
        val safeFileName = sanitizeMetaFileName(fileName, "avatar.png")
        val file = File(dir, safeFileName)
        if (file.isFile && BitmapFactory.decodeFile(file.absolutePath) != null) return
        val bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        Canvas(bmp).apply { drawColor(Color.parseColor("#B0B0B0")) }
        FileOutputStream(file).use { out ->
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    private fun readVoiceMeta(dir: File): VoicePackMeta? = readVoiceMetaFile(metaFile(dir))

    private fun readVoiceMetaFile(file: File): VoicePackMeta? {
        if (!file.isFile) return null
        return try {
            val json = JSONObject(file.readText(Charsets.UTF_8))
            VoicePackMeta(
                name = json.optString("name", "").trim(),
                remark = json.optString("remark", "").trim(),
                avatar = json.optString("avatar", "avatar.png").trim(),
                pinned = json.optBoolean("pinned", false),
                order = json.optLong("order", System.currentTimeMillis())
            )
        } catch (e: Exception) {
            AppLogger.e("readVoiceMeta failed file=${file.absolutePath}", e)
            null
        }
    }

    private fun deriveVoiceMeta(dir: File): VoicePackMeta {
        val manifest = runCatching {
            JSONObject(File(dir, "manifest.json").readText(Charsets.UTF_8))
        }.getOrNull()
        val derivedName = listOf(
            manifest?.optString("name"),
            manifest?.optString("title"),
            manifest?.optString("voice"),
            dir.name
        ).firstOrNull { !it.isNullOrBlank() } ?: "未命名"
        return VoicePackMeta(name = derivedName.trim())
    }

    private fun normalizeVoiceMeta(dir: File, meta: VoicePackMeta): VoicePackMeta {
        val fallbackName = deriveVoiceMeta(dir).name.ifBlank { "未命名" }
        return meta.copy(
            name = meta.name.trim().ifBlank { fallbackName },
            remark = meta.remark.trim(),
            avatar = sanitizeMetaFileName(meta.avatar, "avatar.png"),
            order = meta.order.takeIf { it >= 0L } ?: System.currentTimeMillis()
        )
    }

    private fun writeTextAtomically(file: File, content: String) {
        file.parentFile?.mkdirs()
        val backup = File(file.parentFile, "${file.name}.bak")
        val currentLooksValid = file.isFile && runCatching {
            JSONObject(file.readText(Charsets.UTF_8))
            true
        }.getOrDefault(false)
        if (currentLooksValid) {
            runCatching { file.copyTo(backup, overwrite = true) }
        }
        val tmp = File(file.parentFile, "${file.name}.tmp")
        tmp.writeText(content, Charsets.UTF_8)
        if (file.exists() && !file.delete()) {
            tmp.delete()
            throw IOException("无法更新语音包元数据")
        }
        if (!tmp.renameTo(file)) {
            tmp.copyTo(file, overwrite = true)
            tmp.delete()
        }
    }

    private fun sanitizeMetaFileName(name: String, fallback: String): String {
        return name
            .replace('\\', '/')
            .substringAfterLast('/')
            .trim()
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .trim('.')
            .ifBlank { fallback }
    }

    private fun decodeAvatarBitmap(resolver: ContentResolver, uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        val maxSize = 512
        var sampleSize = 1
        while (bounds.outWidth / sampleSize > maxSize || bounds.outHeight / sampleSize > maxSize) {
            sampleSize *= 2
        }
        val opts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return resolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, opts)
        }
    }

    private fun cleanupStaleVoiceImports() {
        voiceRoot.listFiles()
            ?.filter { it.isDirectory && it.name.startsWith(".import-") }
            ?.forEach { it.deleteRecursively() }
    }

    companion object {
        private const val META_FILE_NAME = "voicepack.json"
    }
}
