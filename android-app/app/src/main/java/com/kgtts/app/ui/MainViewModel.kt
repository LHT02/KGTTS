@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.lhtstudio.kigtts.app.ui

import android.annotation.SuppressLint
import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ComponentName
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.camera.core.CameraSelector
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.*
import androidx.compose.material.DropdownMenuItem as M2DropdownMenuItem
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.lhtstudio.kigtts.app.R
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.lhtstudio.kigtts.app.audio.AudioRoutePreference
import com.lhtstudio.kigtts.app.audio.AudioDenoiserMode
import com.lhtstudio.kigtts.app.audio.AudioLoopbackTester
import com.lhtstudio.kigtts.app.audio.AudioTestConfig
import com.lhtstudio.kigtts.app.audio.SoundboardManager
import com.lhtstudio.kigtts.app.audio.SoundboardPlaybackState
import com.lhtstudio.kigtts.app.audio.SpeechEnhancementMode
import com.lhtstudio.kigtts.app.audio.SpeakerEnrollResult
import com.lhtstudio.kigtts.app.audio.VadMode
import com.lhtstudio.kigtts.app.data.ModelRepository
import com.lhtstudio.kigtts.app.data.RecognitionResourceProgress
import com.lhtstudio.kigtts.app.data.RecognitionResourceStatus
import com.lhtstudio.kigtts.app.data.KokoroVoiceStatus
import com.lhtstudio.kigtts.app.data.SoundboardGroup
import com.lhtstudio.kigtts.app.data.SoundboardItem
import com.lhtstudio.kigtts.app.data.SoundboardLayoutMode
import com.lhtstudio.kigtts.app.data.SoundboardConfig
import com.lhtstudio.kigtts.app.data.SoundboardPresetIo
import com.lhtstudio.kigtts.app.data.KOKORO_VOICE_NAME
import com.lhtstudio.kigtts.app.data.SYSTEM_TTS_VOICE_NAME
import com.lhtstudio.kigtts.app.data.VoicePackInfo
import com.lhtstudio.kigtts.app.data.UserPrefs
import com.lhtstudio.kigtts.app.data.VoicePackMeta
import com.lhtstudio.kigtts.app.data.defaultSoundboardGroups
import com.lhtstudio.kigtts.app.data.isKokoroVoiceDir
import com.lhtstudio.kigtts.app.data.isSystemTtsVoiceDir
import com.lhtstudio.kigtts.app.data.parseSoundboardConfig
import com.lhtstudio.kigtts.app.data.serializeSoundboardConfig
import com.lhtstudio.kigtts.app.data.uniqueImportedGroupTitle
import com.lhtstudio.kigtts.app.overlay.FloatingOverlayService
import com.lhtstudio.kigtts.app.overlay.OverlayBridge
import com.lhtstudio.kigtts.app.overlay.RealtimeOwnerGate
import com.lhtstudio.kigtts.app.overlay.RealtimeRuntimeBridge
import com.lhtstudio.kigtts.app.service.KeepAliveService
import com.lhtstudio.kigtts.app.service.RealtimeHostService
import com.lhtstudio.kigtts.app.service.VolumeHotkeyAccessibilityGuideService
import com.lhtstudio.kigtts.app.service.VolumeHotkeyAccessibilityService
import com.lhtstudio.kigtts.app.service.VolumeHotkeyService
import com.lhtstudio.kigtts.app.util.AlipayScannerSupport
import com.lhtstudio.kigtts.app.util.AppLogger
import com.lhtstudio.kigtts.app.util.BluetoothMediaTitleBridge
import com.lhtstudio.kigtts.app.util.ExternalShortcutCatalog
import com.lhtstudio.kigtts.app.util.ExternalShortcutChoice
import com.lhtstudio.kigtts.app.util.LauncherMenuShortcuts
import com.lhtstudio.kigtts.app.util.LiveSubtitleNotificationBridge
import com.lhtstudio.kigtts.app.util.QqScannerSupport
import com.lhtstudio.kigtts.app.util.QuickCardRenderCache
import com.lhtstudio.kigtts.app.util.VolumeHotkeyActionSpec
import com.lhtstudio.kigtts.app.util.VolumeHotkeyActions
import com.lhtstudio.kigtts.app.util.VolumeHotkeySequence
import com.lhtstudio.kigtts.app.util.snapPlaybackGainPercent
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.InvertedLuminanceSource
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.json.JSONArray
import org.json.JSONObject
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.coroutines.resume
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.roundToInt


class MainViewModel(
    private val repo: ModelRepository,
    private val appContext: ComponentActivity
) : ViewModel() {
    private data class OverlayShortcutSeedEntry(
        val packageName: String,
        val className: String,
        val label: String
    )

    var uiState by mutableStateOf(UiState())
        private set
    var realtimeRecognized by mutableStateOf<List<RecognizedItem>>(emptyList())
        private set
    var realtimeInputLevel by mutableFloatStateOf(0f)
        private set
    var realtimePlaybackProgress by mutableFloatStateOf(0f)
        private set
    var pendingQuickSubtitleLaunchRequest by mutableStateOf<ExternalQuickSubtitleRequest?>(null)
        private set
    var pendingVoicePackInstallRequest by mutableStateOf<ExternalVoicePackInstallRequest?>(null)
        private set
    var pendingPresetInstallRequest by mutableStateOf<ExternalPresetInstallRequest?>(null)
        private set
    var pendingRecordAudioPermissionRequest by mutableStateOf<ExternalRecordAudioPermissionRequest?>(null)
        private set
    var pendingAccessibilityExplainRequest by mutableStateOf<ExternalAccessibilityExplainRequest?>(null)
        private set

    private var realtimeHost: RealtimeHostService? = null
    private var hostStateJob: Job? = null
    private var hostQuickSubtitleJob: Job? = null
    private var pendingHostAsrDir: File? = null
    private var pendingHostVoiceDir: File? = null
    private var pendingHostStartRequest = false

    private val audioTest = AudioLoopbackTester(appContext, viewModelScope) { snapshot ->
        viewModelScope.launch(Dispatchers.Main) {
            uiState = uiState.copy(
                audioTestRecording = snapshot.recording,
                audioTestPlaying = snapshot.playing,
                audioTestHasClip = snapshot.hasClip,
                audioTestLevel = snapshot.level,
                audioTestStatus = snapshot.status
            )
        }
    }
    private var restartJob: Job? = null
    private var settingsObserveJob: Job? = null
    private val lastProgressUpdateAtMs = mutableMapOf<Long, Long>()
    private var lastLevelUpdateAtMs = 0L
    private var speakerProfiles = mutableListOf<UserPrefs.SpeakerVerifyProfile>()
    private var pttSessionLastText: String = ""
    private var lastPttHistoryTextKey: String = ""
    private var lastPttHistoryAtMs: Long = 0L
    private var manualRecognizedIdSeed: Long = -1L
    private var pttSessionCommitConsumed: Boolean = false
    private var lastHandledQuickSubtitleLaunchRequestId: Long = Long.MIN_VALUE
    private val quickSubtitleInterruptRequestSerial = AtomicLong(0L)

    private fun mergePttTranscript(existing: String, incoming: String): String {
        val a = existing.trim()
        val b = incoming.trim()
        if (a.isEmpty()) return b
        if (b.isEmpty()) return a
        if (a == b) return a
        if (b.startsWith(a)) return b
        if (a.startsWith(b)) return a
        if (a.contains(b)) return a
        if (b.contains(a)) return b
        val overlapMax = kotlin.math.min(a.length, b.length)
        for (k in overlapMax downTo 1) {
            if (a.regionMatches(a.length - k, b, 0, k, ignoreCase = false)) {
                return (a + b.substring(k)).trim()
            }
        }
        // PTT 流式拼接不自动补空格，避免中文结果出现“断词空格”。
        return (a + b).replace(Regex("\\s+"), "").trim()
    }

    private fun appendPttFinalTranscript(text: String) {
        val normalized = text.trim()
        if (normalized.isEmpty()) return
        val merged = mergePttTranscript(pttSessionLastText, normalized)
        pttSessionLastText = merged
        if (merged != uiState.pushToTalkStreamingText) {
            uiState = uiState.copy(pushToTalkStreamingText = merged)
        }
    }

    private fun updatePttPreviewTranscript(text: String) {
        val normalized = text.trim()
        if (normalized.isEmpty()) return
        val preview = mergePttTranscript(pttSessionLastText, normalized)
        if (preview != uiState.pushToTalkStreamingText) {
            uiState = uiState.copy(pushToTalkStreamingText = preview)
        }
    }

    private fun normalizePttHistoryKey(text: String): String {
        return text.trim().trimEnd('。', '！', '？', '!', '?', '，', ',', '；', ';', '、', '.')
    }

    private fun shouldSkipPttDuplicateHistory(text: String): Boolean {
        if (!(uiState.pushToTalkMode && uiState.pushToTalkConfirmInputMode)) return false
        val key = normalizePttHistoryKey(text)
        if (key.isEmpty()) return true
        val now = SystemClock.uptimeMillis()
        val duplicated = key == lastPttHistoryTextKey && (now - lastPttHistoryAtMs) <= 1800L
        if (!duplicated) {
            lastPttHistoryTextKey = key
            lastPttHistoryAtMs = now
        }
        return duplicated
    }

    private fun resetPttHistoryDedup() {
        lastPttHistoryTextKey = ""
        lastPttHistoryAtMs = 0L
    }

    private fun appendRecognizedHistory(text: String, id: Long? = null, fromQuickText: Boolean = false) {
        val normalized = text.trim()
        if (normalized.isEmpty()) return
        val historyId = id ?: manualRecognizedIdSeed--
        if (id != null && realtimeRecognized.any { it.id == id }) return
        val item = RecognizedItem(id = historyId, text = normalized)
        val next = (listOf(item) + realtimeRecognized).take(MAX_RECOGNIZED_ITEMS)
        realtimeRecognized = next
        val validIds = next.asSequence().map { it.id }.toSet()
        lastProgressUpdateAtMs.keys.retainAll(validIds)
        maybeTriggerSoundboardFromText(normalized, fromQuickText = fromQuickText)
    }

    private companion object {
        private const val LEVEL_UPDATE_INTERVAL_MS = 33L
        private const val LEVEL_UPDATE_DELTA = 0.02f
        private const val PROGRESS_UPDATE_INTERVAL_MS = 48L
        private const val PROGRESS_UPDATE_DELTA = 0.02f
        private const val MAX_RECOGNIZED_ITEMS = 100
        private const val MAX_SPEAKER_PROFILES = 3
        private const val APP_REALTIME_OWNER_TAG = "app"
    }
    val drawStrokes = mutableStateListOf<DrawStrokeData>()
    val drawRedoStrokes = mutableStateListOf<DrawStrokeData>()
    var drawColor by mutableStateOf(UiTokens.Primary)
        private set
    var drawBrushSize by mutableStateOf(12f)
        private set
    var drawEraserSize by mutableStateOf(12f)
        private set
    var drawEraser by mutableStateOf(false)
        private set
    var quickSubtitleGroups by mutableStateOf(defaultQuickSubtitleGroups())
        private set
    var quickSubtitleSelectedGroupId by mutableLongStateOf(1L)
        private set
    var quickSubtitleCurrentText by mutableStateOf("您好，我现在\n不太方便\n说话")
        private set
    var quickSubtitleInputText by mutableStateOf("")
        private set
    var quickSubtitleContentRevision by mutableLongStateOf(0L)
        private set
    var quickSubtitlePlayOnSend by mutableStateOf(true)
        private set
    var quickSubtitleInputCollapsed by mutableStateOf(false)
        private set
    var quickSubtitleBold by mutableStateOf(true)
        private set
    var quickSubtitleCentered by mutableStateOf(false)
        private set
    var quickSubtitleRotated180 by mutableStateOf(false)
        private set
    var quickSubtitleShowActionButtons by mutableStateOf(true)
        private set
    var quickSubtitleListPopupLayout by mutableStateOf(QuickSubtitleListPopupLayout.Grid)
        private set
    var quickSubtitleFontSizeSp by mutableFloatStateOf(56f)
        private set
    var quickSubtitlePreviewVisible by mutableStateOf(false)
        private set
    var soundboardGroups by mutableStateOf(defaultSoundboardGroups())
        private set
    var soundboardSelectedGroupId by mutableLongStateOf(1L)
        private set
    var soundboardPortraitLayout by mutableStateOf(SoundboardLayoutMode.List)
        private set
    var soundboardLandscapeLayout by mutableStateOf(SoundboardLayoutMode.List)
        private set
    var soundboardPlaybackStates by mutableStateOf<Map<Long, SoundboardPlaybackState>>(emptyMap())
        private set
    var settingsSelectedCategoryName by mutableStateOf(SettingsCategory.Recognition.name)
        private set
    var quickCards by mutableStateOf<List<QuickCard>>(emptyList())
        private set
    var quickCardSelectedIndex by mutableIntStateOf(0)
        private set
    var quickCardPreviewCardId by mutableStateOf<Long?>(null)
        private set
    var quickCardDraft by mutableStateOf<QuickCardDraft?>(null)
        private set
    var drawingToolbarCollapsed by mutableStateOf(false)
        private set
    var drawingManualRotationQuarterTurns by mutableIntStateOf(0)
        private set
    private var quickSubtitleNextGroupId = 4L
    private var quickSubtitleSaving = false
    private var lastAppliedQuickSubtitleRequestId = 0L
    private var lastAppliedRecognizedSubtitleId = Long.MIN_VALUE
    private var soundboardNextGroupId = 2L
    private var soundboardNextItemId = 1L
    private var soundboardSaving = false
    private var pendingSoundboardSavePayload: String? = null
    private var pendingSoundboardGroupSelectionId: Long? = null
    private var quickCardsNextId = 1L
    private var quickCardsSaving = false

    init {
        loadQuickSubtitleConfig()
        loadSoundboardConfig()
        loadQuickCardConfig()
        refreshRecognitionResourceStatus()
        refreshKokoroVoiceStatus()
        observeSoundboardPlayback()
        observeSettingsChanges()
    }

    fun ensureInitialFloatingOverlayShortcuts() {
        viewModelScope.launch(Dispatchers.IO) {
            if (UserPrefs.isFloatingOverlayDefaultShortcutsSeeded(appContext)) return@launch
            val existing = UserPrefs.getFloatingOverlayShortcuts(appContext)
            if (!existing.isNullOrBlank()) {
                UserPrefs.setFloatingOverlayDefaultShortcutsSeeded(appContext, true)
                return@launch
            }
            val seeded = buildInitialFloatingOverlayShortcuts()
            if (seeded.isNotEmpty()) {
                val payload = JSONArray().apply {
                    seeded.forEach { item ->
                        put(
                            JSONObject().apply {
                                put("packageName", item.packageName)
                                put("className", item.className)
                                put("label", item.label)
                            }
                        )
                    }
                }.toString()
                UserPrefs.setFloatingOverlayShortcuts(appContext, payload)
            }
            UserPrefs.setFloatingOverlayDefaultShortcutsSeeded(appContext, true)
        }
    }

    fun attachRealtimeHost(service: RealtimeHostService) {
        if (realtimeHost === service) return
        detachRealtimeHost()
        realtimeHost = service
        service.setQuickSubtitlePlayOnSend(quickSubtitlePlayOnSend)
        val attachedSpeakerSimilarity = service.getSpeakerLastSimilarity()
        if (attachedSpeakerSimilarity >= 0f) {
            uiState = uiState.copy(speakerLastSimilarity = attachedSpeakerSimilarity)
        }
        hostStateJob = viewModelScope.launch {
            service.stateFlow().collectLatest { snapshot ->
                realtimeRecognized = snapshot.recognized
                realtimeInputLevel = snapshot.inputLevel.coerceIn(0f, 1f)
                realtimePlaybackProgress = snapshot.playbackProgress.coerceIn(0f, 1f)
                val quickSubtitleRequestId = snapshot.quickSubtitleRequestId
                val quickSubtitleText = snapshot.quickSubtitleText.trim()
                val previousSpeakerSimilarity = uiState.speakerLastSimilarity
                uiState = uiState.copy(
                    asrDir = snapshot.asrDir,
                    voiceDir = snapshot.voiceDir,
                    recognized = snapshot.recognized,
                    running = snapshot.running,
                    status = if (snapshot.status.isNotBlank()) snapshot.status else uiState.status,
                    aec3Status = snapshot.aec3Status,
                    aec3Diag = snapshot.aec3Diag,
                    pushToTalkPressed = snapshot.pushToTalkPressed,
                    pushToTalkStreamingText = snapshot.pushToTalkStreamingText,
                    speakerLastSimilarity = if (snapshot.speakerLastSimilarity >= 0f) {
                        snapshot.speakerLastSimilarity
                    } else {
                        previousSpeakerSimilarity
                    },
                    inputDeviceLabel = snapshot.inputDeviceLabel.ifBlank { uiState.inputDeviceLabel },
                    outputDeviceLabel = snapshot.outputDeviceLabel.ifBlank { uiState.outputDeviceLabel }
                )
                if (quickSubtitleRequestId > 0L && quickSubtitleText.isNotEmpty()) {
                    applyExternalQuickSubtitleRequest(
                        requestId = quickSubtitleRequestId,
                        target = OverlayBridge.TARGET_SUBTITLE,
                        text = quickSubtitleText
                    )
                }
                val latest = snapshot.recognized.firstOrNull()
                val latestText = latest?.text?.trim().orEmpty()
                if (
                    latest != null &&
                    latest.id >= 0L &&
                    latest.id != lastAppliedRecognizedSubtitleId &&
                    latestText.isNotEmpty()
                ) {
                    lastAppliedRecognizedSubtitleId = latest.id
                    applyQuickSubtitleResultText(latestText)
                }
            }
        }
        hostQuickSubtitleJob = viewModelScope.launch {
            service.quickSubtitleRequestFlow().collectLatest { request ->
                pendingQuickSubtitleLaunchRequest = request
            }
        }
        pendingHostAsrDir?.let { dir ->
            viewModelScope.launch {
                service.updateSelectedAsrDir(dir, preload = true)
                pendingHostAsrDir = null
            }
        }
        pendingHostVoiceDir?.let { dir ->
            viewModelScope.launch {
                service.updateSelectedVoiceDir(dir, preload = true)
                pendingHostVoiceDir = null
            }
        }
        if (pendingHostStartRequest) {
            pendingHostStartRequest = false
            service.startRealtime()
        }
        refreshVoicePacks()
    }

    fun detachRealtimeHost() {
        hostStateJob?.cancel()
        hostStateJob = null
        hostQuickSubtitleJob?.cancel()
        hostQuickSubtitleJob = null
        realtimeHost = null
    }

    private fun requestRealtimeHost(status: String? = null): RealtimeHostService? {
        val host = realtimeHost
        if (host != null) return host
        RealtimeHostService.ensureStarted(appContext)
        if (status != null) {
            uiState = uiState.copy(status = status)
        }
        return null
    }

    private fun buildInitialFloatingOverlayShortcuts(): List<OverlayShortcutSeedEntry> {
        val shortcuts = buildList {
            resolveLauncherShortcutForPackage("com.tencent.mobileqq")?.let(::add)
            resolveLauncherShortcutForPackage("com.tencent.mm")?.let(::add)
            resolveLauncherShortcutForPackage("com.eg.android.AlipayGphone")?.let(::add)
            resolveCameraLauncherShortcut()?.let(::add)
        }
        return shortcuts.distinctBy { "${it.packageName}/${it.className}" }
    }

    private fun resolveLauncherShortcutForPackage(packageName: String): OverlayShortcutSeedEntry? {
        val pm = appContext.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName) ?: return null
        val component = launchIntent.component ?: return null
        val label = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getApplicationInfo(packageName, 0)
            }
        }.mapCatching { info ->
            pm.getApplicationLabel(info).toString().trim()
        }.getOrDefault("")
        return OverlayShortcutSeedEntry(
            packageName = component.packageName,
            className = component.className,
            label = label.ifBlank { component.className.substringAfterLast('.') }
        )
    }

    private fun resolveCameraLauncherShortcut(): OverlayShortcutSeedEntry? {
        val pm = appContext.packageManager
        val cameraIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        val resolved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.resolveActivity(cameraIntent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.resolveActivity(cameraIntent, 0)
        } ?: return null
        val packageName = resolved.activityInfo?.packageName?.takeIf { it.isNotBlank() } ?: return null
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        val component = launchIntent?.component ?: ComponentName(
            packageName,
            resolved.activityInfo?.name?.takeIf { it.isNotBlank() } ?: return null
        )
        val label = resolved.loadLabel(pm)?.toString()?.trim().orEmpty().ifBlank {
            runCatching {
                val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    pm.getApplicationInfo(packageName, 0)
                }
                pm.getApplicationLabel(info).toString().trim()
            }.getOrDefault("")
        }
        return OverlayShortcutSeedEntry(
            packageName = component.packageName,
            className = component.className,
            label = label.ifBlank { component.className.substringAfterLast('.') }
        )
    }

    private fun applySettingsSnapshot(settings: UserPrefs.AppSettings) {
        FontScaleBlockRuntime.mode = settings.fontScaleBlockMode
        SoundboardManager.setPlaybackGainPercent(settings.playbackGainPercent)
        SoundboardManager.setAudioFocusAvoidanceMode(appContext, settings.audioFocusAvoidanceMode)
        BluetoothMediaTitleBridge.setEnabled(
            appContext,
            settings.bluetoothMediaTitleSubtitle,
            quickSubtitleCurrentText
        )
        if (settings.bluetoothMediaTitleSubtitle) {
            BluetoothMediaTitleBridge.updateSubtitle(appContext, quickSubtitleCurrentText)
        }
        val needsSpeakerBackendReset =
            settings.speakerVerifyBackendVersion != UserPrefs.SPEAKER_VERIFY_BACKEND_SHERPA_V1 &&
                    (settings.speakerVerifyEnabled || settings.speakerVerifyProfileCsv.isNotBlank())
        if (needsSpeakerBackendReset) {
            viewModelScope.launch(Dispatchers.IO) {
                UserPrefs.resetSpeakerVerifyBackend(appContext, enabled = false)
            }
        }
        speakerProfiles = if (needsSpeakerBackendReset) {
            mutableListOf()
        } else {
            UserPrefs.parseSpeakerVerifyProfiles(settings.speakerVerifyProfileCsv)
                .take(MAX_SPEAKER_PROFILES)
                .toMutableList()
        }
        val hasProfiles = speakerProfiles.isNotEmpty()
        val speakerVerifyEnabled = settings.speakerVerifyEnabled && hasProfiles
        val nextAec3Status = if (settings.aec3Enabled) {
            if (uiState.aec3Status == "未启用") "待启动" else uiState.aec3Status
        } else {
            "未启用"
        }
        val nextAec3Diag = if (settings.aec3Enabled) {
            if (uiState.aec3Diag == "AEC3 诊断：未启用") "AEC3 诊断：待启动" else uiState.aec3Diag
        } else {
            "AEC3 诊断：未启用"
        }
        uiState = uiState.copy(
            settingsLoaded = true,
            muteWhilePlaying = settings.muteWhilePlaying,
            muteWhilePlayingDelaySec = settings.muteWhilePlayingDelaySec,
            echoSuppression = settings.echoSuppression,
            communicationMode = settings.communicationMode,
            preferredInputType = settings.preferredInputType,
            preferredOutputType = settings.preferredOutputType,
            aec3Enabled = settings.aec3Enabled,
            denoiserMode = settings.denoiserMode,
            speechEnhancementMode = settings.speechEnhancementMode,
            aec3Status = nextAec3Status,
            aec3Diag = nextAec3Diag,
            classicVadEnabled = settings.classicVadEnabled,
            sileroVadEnabled = settings.sileroVadEnabled,
            sileroVadThreshold = settings.sileroVadThreshold,
            sileroVadPreRollMs = settings.sileroVadPreRollMs,
            recognitionResourceModelScopeUrl = settings.recognitionResourceModelScopeUrl,
            recognitionResourceHuggingFaceUrl = settings.recognitionResourceHuggingFaceUrl,
            recognitionResourcePreferredSource = settings.recognitionResourcePreferredSource,
            kokoroHfUrl = settings.kokoroHfUrl,
            kokoroHfMirrorUrl = settings.kokoroHfMirrorUrl,
            kokoroModelScopeUrl = settings.kokoroModelScopeUrl,
            kokoroPreferredSource = settings.kokoroPreferredSource,
            kokoroSpeakerId = settings.kokoroSpeakerId,
            minVolumePercent = settings.minVolumePercent,
            playbackGainPercent = settings.playbackGainPercent,
            audioFocusAvoidanceMode = settings.audioFocusAvoidanceMode,
            piperNoiseScale = settings.piperNoiseScale,
            piperLengthScale = settings.piperLengthScale,
            piperNoiseW = 0.8f,
            piperSentenceSilence = settings.piperSentenceSilence,
            keepAlive = settings.keepAlive,
            numberReplaceMode = settings.numberReplaceMode,
            landscapeDrawerMode = settings.landscapeDrawerMode,
            solidTopBar = settings.solidTopBar,
            themeMode = settings.themeMode,
            overlayThemeMode = settings.overlayThemeMode,
            fontScaleBlockMode = settings.fontScaleBlockMode,
            hapticFeedbackEnabled = settings.hapticFeedbackEnabled,
            onboardingCompleted = settings.onboardingCompleted,
            forceFullWidthTabsOnPhone = settings.forceFullWidthTabsOnPhone,
            soundboardGridFullWidth = settings.soundboardGridFullWidth,
            internalWebViewEnabled = settings.internalWebViewEnabled,
            drawingSaveRelativePath = normalizeDrawingSaveRelativePath(settings.drawingSaveRelativePath),
            quickCardAutoSaveOnExit = settings.quickCardAutoSaveOnExit,
            useBuiltinFileManager = settings.useBuiltinFileManager,
            useBuiltinGallery = settings.useBuiltinGallery,
            asrSendToQuickSubtitle = settings.asrSendToQuickSubtitle,
            pushToTalkMode = settings.pushToTalkMode,
            pushToTalkConfirmInputMode = settings.pushToTalkConfirmInput,
            floatingOverlayEnabled = settings.floatingOverlayEnabled,
            floatingOverlayAutoDock = settings.floatingOverlayAutoDock,
            floatingOverlayShowOnLockScreen = settings.floatingOverlayShowOnLockScreen,
            floatingOverlayHardcodedShortcutSupplement =
                settings.floatingOverlayHardcodedShortcutSupplement,
            volumeHotkeyUpDownEnabled = settings.volumeHotkeyUpDownEnabled,
            volumeHotkeyDownUpEnabled = settings.volumeHotkeyDownUpEnabled,
            volumeHotkeyWindowMs = settings.volumeHotkeyWindowMs,
            volumeHotkeyAccessibilityEnabled = settings.volumeHotkeyAccessibilityEnabled,
            volumeHotkeyEnableWarningDismissed = settings.volumeHotkeyEnableWarningDismissed,
            volumeHotkeyUpDownAction = settings.volumeHotkeyUpDownAction,
            volumeHotkeyDownUpAction = settings.volumeHotkeyDownUpAction,
            ttsDisabled = settings.ttsDisabled,
            soundboardKeywordTriggerEnabled = settings.soundboardKeywordTriggerEnabled,
            soundboardSuppressTtsOnKeyword = settings.soundboardSuppressTtsOnKeyword,
            allowQuickTextTriggerSoundboard = settings.allowQuickTextTriggerSoundboard,
            quickSubtitleInterruptQueue = settings.quickSubtitleInterruptQueue,
            quickSubtitleAutoFit = settings.quickSubtitleAutoFit,
            quickSubtitleAllowLargeFont = settings.quickSubtitleAllowLargeFont,
            quickSubtitleCompactControls = settings.quickSubtitleCompactControls,
            quickSubtitleKeepInputPreview = settings.quickSubtitleKeepInputPreview,
            bluetoothMediaTitleSubtitle = settings.bluetoothMediaTitleSubtitle,
            liveSubtitleNotificationEnabled = settings.liveSubtitleNotificationEnabled,
            drawingKeepCanvasOrientationToDevice = settings.drawingKeepCanvasOrientationToDevice,
            speakerVerifyEnabled = speakerVerifyEnabled,
            speakerVerifyThreshold = settings.speakerVerifyThreshold,
            speakerProfileReady = hasProfiles,
            speakerProfiles = speakerProfileUiItems(),
            speakerLastSimilarity = if (speakerVerifyEnabled) uiState.speakerLastSimilarity else -1f,
            pushToTalkPressed = if (settings.pushToTalkMode) uiState.pushToTalkPressed else false,
            pushToTalkStreamingText = if (settings.pushToTalkMode) uiState.pushToTalkStreamingText else ""
        )
        quickSubtitleListPopupLayout =
            if (settings.quickSubtitleListPopupGridMode) {
                QuickSubtitleListPopupLayout.Grid
            } else {
                QuickSubtitleListPopupLayout.List
            }
        applySettingsToController(settings)
        if (settings.speakerVerifyEnabled && !speakerVerifyEnabled) {
            viewModelScope.launch(Dispatchers.IO) {
                UserPrefs.setSpeakerVerifyEnabled(appContext, false)
            }
        }
    }

    private fun observeSettingsChanges() {
        settingsObserveJob?.cancel()
        settingsObserveJob = viewModelScope.launch {
            UserPrefs.observeSettings(appContext).collectLatest { settings ->
                applySettingsSnapshot(settings)
                VolumeHotkeyService.syncWithSettings(appContext)
            }
        }
    }

    private fun loadQuickSubtitleConfig() {
        viewModelScope.launch {
            val raw = UserPrefs.getQuickSubtitleConfig(appContext)
            if (raw.isNullOrBlank()) return@launch
            runCatching {
                parseQuickSubtitleConfig(raw)
            }
        }
    }

    private fun parseQuickSubtitleConfig(raw: String) {
        val root = JSONObject(raw)
        val groupsArr = root.optJSONArray("groups") ?: JSONArray()
        val parsedGroups = mutableListOf<QuickSubtitleGroup>()
        var maxId = 0L
        for (i in 0 until groupsArr.length()) {
            val g = groupsArr.optJSONObject(i) ?: continue
            val id = g.optLong("id", i.toLong() + 1L).coerceAtLeast(1L)
            val title = g.optString("title", "").trim()
            val icon = g.optString("icon", "sentiment_satisfied").ifBlank { "sentiment_satisfied" }
            val itemsArr = g.optJSONArray("items") ?: JSONArray()
            val items = mutableListOf<String>()
            for (j in 0 until itemsArr.length()) {
                val text = itemsArr.optString(j, "").trim()
                if (text.isNotEmpty()) items.add(text)
            }
            if (items.isEmpty()) items.add("请输入常用短句")
            parsedGroups.add(
                QuickSubtitleGroup(
                    id = id,
                    title = title,
                    icon = icon,
                    items = items
                )
            )
            if (id > maxId) maxId = id
        }
        val finalGroups = if (parsedGroups.isNotEmpty()) parsedGroups else defaultQuickSubtitleGroups()
        val selectedId = root.optLong("selectedGroupId", finalGroups.first().id)
        val fontSize = root.optDouble("fontSizeSp", 56.0).toFloat().coerceIn(28f, 800f)
        val currentText = root.optString("currentText", quickSubtitleCurrentText).ifBlank { quickSubtitleCurrentText }
        val inputText = root.optString("inputText", "")
        val playOnSend = root.optBoolean("playOnSend", true)
        val fontBold = root.optBoolean("fontBold", true)
        val textCentered = root.optBoolean("textCentered", false)
        val textRotated180 = root.optBoolean("textRotated180", false)
        val showActionButtons = root.optBoolean("showActionButtons", true)
        quickSubtitleGroups = finalGroups
        quickSubtitleSelectedGroupId =
            finalGroups.firstOrNull { it.id == selectedId }?.id ?: finalGroups.first().id
        quickSubtitleFontSizeSp = fontSize
        quickSubtitleCurrentText = currentText
        quickSubtitleInputText = inputText
        quickSubtitlePlayOnSend = playOnSend
        quickSubtitleBold = fontBold
        quickSubtitleCentered = textCentered
        quickSubtitleRotated180 = textRotated180
        quickSubtitleShowActionButtons = showActionButtons
        quickSubtitleNextGroupId = maxOf(maxId + 1L, (finalGroups.maxOfOrNull { it.id } ?: 0L) + 1L)
        syncBluetoothMediaTitleToCommittedQuickSubtitle(currentText)
    }

    private fun saveQuickSubtitleConfig() {
        if (quickSubtitleSaving) return
        quickSubtitleSaving = true
        val root = JSONObject().apply {
            put("selectedGroupId", quickSubtitleSelectedGroupId)
            put("fontSizeSp", quickSubtitleFontSizeSp.toDouble())
            put("currentText", quickSubtitleCurrentText)
            put("inputText", quickSubtitleInputText)
            put("playOnSend", quickSubtitlePlayOnSend)
            put("fontBold", quickSubtitleBold)
            put("textCentered", quickSubtitleCentered)
            put("textRotated180", quickSubtitleRotated180)
            put("showActionButtons", quickSubtitleShowActionButtons)
            val groupsArr = JSONArray()
            quickSubtitleGroups.forEach { g ->
                groupsArr.put(
                    JSONObject().apply {
                        put("id", g.id)
                        put("title", g.title)
                        put("icon", g.icon)
                        val itemsArr = JSONArray()
                        g.items.forEach { itemsArr.put(it) }
                        put("items", itemsArr)
                    }
                )
            }
            put("groups", groupsArr)
        }
        val payload = root.toString()
        viewModelScope.launch {
            try {
                UserPrefs.setQuickSubtitleConfig(appContext, payload)
                realtimeHost?.publishQuickSubtitleConfig(payload)
            } finally {
                quickSubtitleSaving = false
            }
        }
    }

    private fun markQuickSubtitleContentSubmitted() {
        quickSubtitleContentRevision++
    }

    private fun commitQuickSubtitleCurrentText(text: String) {
        quickSubtitleCurrentText = text
        syncBluetoothMediaTitleToCommittedQuickSubtitle(text)
    }

    private fun syncBluetoothMediaTitleToCommittedQuickSubtitle(
        text: String = quickSubtitleCurrentText
    ) {
        LiveSubtitleNotificationBridge.update(
            appContext,
            uiState.liveSubtitleNotificationEnabled,
            text,
            uiState.status
        )
        if (uiState.bluetoothMediaTitleSubtitle) {
            BluetoothMediaTitleBridge.updateSubtitle(appContext, text)
        }
    }

    fun currentQuickSubtitleGroupIndex(): Int {
        val idx = quickSubtitleGroups.indexOfFirst { it.id == quickSubtitleSelectedGroupId }
        return if (idx >= 0) idx else 0
    }

    fun selectQuickSubtitleGroup(index: Int) {
        val clamped = index.coerceIn(0, quickSubtitleGroups.lastIndex.coerceAtLeast(0))
        val target = quickSubtitleGroups.getOrNull(clamped) ?: return
        quickSubtitleSelectedGroupId = target.id
        saveQuickSubtitleConfig()
    }

    fun updateQuickSubtitleListPopupLayout(layout: QuickSubtitleListPopupLayout) {
        if (quickSubtitleListPopupLayout == layout) return
        quickSubtitleListPopupLayout = layout
        viewModelScope.launch {
            UserPrefs.setQuickSubtitleListPopupGridMode(
                appContext,
                layout == QuickSubtitleListPopupLayout.Grid
            )
        }
    }

    fun applyQuickSubtitleText(text: String, enqueueSpeak: Boolean = true) {
        val message = text.trim()
        if (message.isEmpty()) return
        commitQuickSubtitleCurrentText(message)
        markQuickSubtitleContentSubmitted()
        if (enqueueSpeak) {
            speakText(
                message,
                fromQuickText = true,
                interruptCurrent = uiState.quickSubtitleInterruptQueue
            )
        } else {
            maybeTriggerSoundboardFromText(message, fromQuickText = true)
        }
        saveQuickSubtitleConfig()
    }

    fun submitQuickSubtitlePreset(
        text: String,
        hasVoice: Boolean = true,
        interruptCurrent: Boolean = uiState.quickSubtitleInterruptQueue
    ) {
        val message = text.trim()
        if (message.isEmpty()) return
        commitQuickSubtitleCurrentText(message)
        markQuickSubtitleContentSubmitted()
        if (quickSubtitlePlayOnSend && hasVoice) {
            speakText(
                message,
                fromQuickText = true,
                interruptCurrent = interruptCurrent
            )
        } else {
            maybeTriggerSoundboardFromText(message, fromQuickText = true)
            uiState = uiState.copy(status = "已更新字幕文本")
        }
        saveQuickSubtitleConfig()
    }

    fun updateQuickSubtitleInputText(text: String) {
        quickSubtitleInputText = text
    }

    fun submitQuickSubtitleInput(playVoice: Boolean = quickSubtitlePlayOnSend) {
        val message = quickSubtitleInputText.trim()
        if (message.isEmpty()) return
        commitQuickSubtitleCurrentText(message)
        markQuickSubtitleContentSubmitted()
        quickSubtitleInputText = ""
        if (playVoice) {
            speakText(
                message,
                fromQuickText = true,
                interruptCurrent = uiState.quickSubtitleInterruptQueue
            )
        } else {
            maybeTriggerSoundboardFromText(message, fromQuickText = true)
            uiState = uiState.copy(status = "已更新字幕文本")
        }
        saveQuickSubtitleConfig()
    }

    fun handleQuickSubtitleLaunchRequest(
        requestId: Long,
        target: String,
        text: String,
        navigateToPage: Boolean = true,
        soundboardGroupId: Long? = null
    ) {
        val normalized = text.trim()
        val openPageTarget = isOverlayOpenTarget(target)
        val effectiveRequestId = if (openPageTarget) {
            nextQuickSubtitleLaunchRequestId()
        } else {
            requestId
        }
        if (effectiveRequestId == Long.MIN_VALUE) return
        if (effectiveRequestId == lastHandledQuickSubtitleLaunchRequestId) return
        if (!isOverlayOpenTarget(target) && normalized.isEmpty()) return
        lastHandledQuickSubtitleLaunchRequestId = effectiveRequestId
        pendingQuickSubtitleLaunchRequest = ExternalQuickSubtitleRequest(
            requestId = effectiveRequestId,
            target = target,
            text = normalized,
            navigateToPage = navigateToPage,
            soundboardGroupId = soundboardGroupId
        )
    }

    private fun nextQuickSubtitleLaunchRequestId(): Long {
        val now = SystemClock.uptimeMillis()
        return if (now <= lastHandledQuickSubtitleLaunchRequestId) {
            lastHandledQuickSubtitleLaunchRequestId + 1
        } else {
            now
        }
    }

    fun consumeQuickSubtitleLaunchRequest(requestId: Long) {
        if (pendingQuickSubtitleLaunchRequest?.requestId == requestId) {
            pendingQuickSubtitleLaunchRequest = null
        }
        realtimeHost?.consumeQuickSubtitleRequest(requestId)
    }

    private fun requestVoicePackInstallNavigation(message: String) {
        pendingVoicePackInstallRequest = ExternalVoicePackInstallRequest(
            requestId = SystemClock.uptimeMillis(),
            message = message
        )
    }

    fun consumeVoicePackInstallRequest(requestId: Long) {
        if (pendingVoicePackInstallRequest?.requestId == requestId) {
            pendingVoicePackInstallRequest = null
        }
    }

    private fun requestPresetInstallNavigation(target: PresetInstallTarget, message: String) {
        pendingPresetInstallRequest = ExternalPresetInstallRequest(
            requestId = SystemClock.uptimeMillis(),
            target = target,
            message = message
        )
    }

    fun consumePresetInstallRequest(requestId: Long) {
        if (pendingPresetInstallRequest?.requestId == requestId) {
            pendingPresetInstallRequest = null
        }
    }

    fun exportQuickSubtitlePresetPackage(groupIds: Set<Long>) {
        if (groupIds.isEmpty()) {
            uiState = uiState.copy(status = "请先选择要导出的便捷字幕分组")
            return
        }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    writeQuickSubtitlePresetPackage(
                        context = appContext,
                        groups = quickSubtitleGroups.filter { it.id in groupIds }
                    )
                }
            }
            result.onSuccess { file ->
                uiState = uiState.copy(status = "便捷字幕预设已导出：${file.absolutePath}")
                sharePresetFile(file, "application/x-kigtts-quicktext-preset", "分享便捷字幕预设")
            }.onFailure { e ->
                uiState = uiState.copy(status = "便捷字幕预设导出失败：${e.message ?: "未知错误"}")
            }
        }
    }

    fun importQuickSubtitlePresetPackage(uri: android.net.Uri, openEditorOnSuccess: Boolean = false) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { readQuickSubtitlePresetPackage(appContext, uri) }
            }
            result.onSuccess { imported ->
                if (imported.isEmpty()) {
                    uiState = uiState.copy(status = "便捷字幕预设包没有可导入分组")
                    return@onSuccess
                }
                val existingTitles = quickSubtitleGroups.map { it.title }.toMutableSet()
                val remapped = imported.map { group ->
                    val title = uniqueQuickSubtitleGroupTitle(group.title, existingTitles)
                    existingTitles += title
                    group.copy(id = quickSubtitleNextGroupId++, title = title)
                }
                quickSubtitleGroups = quickSubtitleGroups + remapped
                quickSubtitleSelectedGroupId = remapped.first().id
                saveQuickSubtitleConfig()
                val message = "便捷字幕预设已安装：${remapped.size} 个分组"
                uiState = uiState.copy(status = message)
                if (openEditorOnSuccess) {
                    requestPresetInstallNavigation(PresetInstallTarget.QuickSubtitle, message)
                }
            }.onFailure { e ->
                uiState = uiState.copy(status = "便捷字幕预设导入失败：${e.message ?: "未知错误"}")
            }
        }
    }

    fun requestRecordAudioPermission(startRealtimeOnGrant: Boolean = false) {
        pendingRecordAudioPermissionRequest = ExternalRecordAudioPermissionRequest(
            requestId = SystemClock.uptimeMillis(),
            startRealtimeOnGrant = startRealtimeOnGrant
        )
    }

    fun consumeRecordAudioPermissionRequest(requestId: Long) {
        if (pendingRecordAudioPermissionRequest?.requestId == requestId) {
            pendingRecordAudioPermissionRequest = null
        }
    }

    fun requestAccessibilityExplainDialog() {
        pendingAccessibilityExplainRequest =
            ExternalAccessibilityExplainRequest(requestId = SystemClock.uptimeMillis())
    }

    fun consumeAccessibilityExplainRequest(requestId: Long) {
        if (pendingAccessibilityExplainRequest?.requestId == requestId) {
            pendingAccessibilityExplainRequest = null
        }
    }

    fun applyExternalQuickSubtitleRequest(
        requestId: Long,
        target: String,
        text: String
    ) {
        if (requestId > 0L && requestId <= lastAppliedQuickSubtitleRequestId) return
        if (requestId > 0L) {
            lastAppliedQuickSubtitleRequestId = requestId
        }
        val normalized = text.trim()
        when (target) {
            OverlayBridge.TARGET_OPEN -> {
                loadQuickSubtitleConfig()
            }
            OverlayBridge.TARGET_INPUT -> {
                if (normalized.isEmpty()) return
                quickSubtitleInputCollapsed = false
                quickSubtitleInputText = normalized
                saveQuickSubtitleConfig()
            }
            else -> {
                applyQuickSubtitleResultText(normalized)
            }
        }
    }

    private fun applyQuickSubtitleResultText(text: String) {
        val normalized = text.trim()
        if (normalized.isEmpty()) return
        commitQuickSubtitleCurrentText(normalized)
        markQuickSubtitleContentSubmitted()
        saveQuickSubtitleConfig()
    }

    fun applyExternalQuickSubtitleRequest(target: String, text: String) {
        applyExternalQuickSubtitleRequest(
            requestId = nextQuickSubtitleLaunchRequestId(),
            target = target,
            text = text
        )
    }

    fun setQuickSubtitleFontSize(size: Float) {
        quickSubtitleFontSizeSp = size.coerceIn(28f, quickSubtitleFontSizeMaxSp())
        saveQuickSubtitleConfig()
    }

    private fun quickSubtitleFontSizeMaxSp(): Float =
        if (uiState.quickSubtitleAllowLargeFont) 800f else 96f

    private fun coerceQuickSubtitleFontSizeToCurrentLimit() {
        val coerced = quickSubtitleFontSizeSp.coerceIn(28f, quickSubtitleFontSizeMaxSp())
        if (coerced != quickSubtitleFontSizeSp) {
            quickSubtitleFontSizeSp = coerced
        }
    }

    fun openQuickSubtitlePreview() {
        quickSubtitlePreviewVisible = true
    }

    fun closeQuickSubtitlePreview() {
        quickSubtitlePreviewVisible = false
    }

    fun updateQuickSubtitlePlayOnSend(enabled: Boolean) {
        quickSubtitlePlayOnSend = enabled
        realtimeHost?.setQuickSubtitlePlayOnSend(enabled)
        saveQuickSubtitleConfig()
    }

    fun updateQuickSubtitleInputCollapsed(collapsed: Boolean) {
        quickSubtitleInputCollapsed = collapsed
    }

    fun updateQuickSubtitleBold(enabled: Boolean) {
        quickSubtitleBold = enabled
        saveQuickSubtitleConfig()
    }

    fun updateQuickSubtitleCentered(enabled: Boolean) {
        quickSubtitleCentered = enabled
        saveQuickSubtitleConfig()
    }

    fun updateQuickSubtitleRotated180(enabled: Boolean) {
        quickSubtitleRotated180 = enabled
        saveQuickSubtitleConfig()
    }

    fun updateQuickSubtitleShowActionButtons(showActionButtons: Boolean) {
        quickSubtitleShowActionButtons = showActionButtons
        saveQuickSubtitleConfig()
    }

    fun updateSettingsSelectedCategory(category: SettingsCategory) {
        settingsSelectedCategoryName = category.name
    }

    fun clearQuickSubtitleText() {
        commitQuickSubtitleCurrentText(QUICK_SUBTITLE_CLEARED_HINT)
        saveQuickSubtitleConfig()
    }

    fun updateQuickSubtitleGroupMeta(index: Int, title: String, icon: String) {
        if (index !in quickSubtitleGroups.indices) return
        val next = quickSubtitleGroups.toMutableList()
        val prev = next[index]
        next[index] = prev.copy(
            title = title.trim(),
            icon = icon.ifBlank { "sentiment_satisfied" }
        )
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
    }

    fun addQuickSubtitleGroup() {
        val newId = quickSubtitleNextGroupId++
        quickSubtitleGroups = quickSubtitleGroups + QuickSubtitleGroup(
            id = newId,
            title = "新分组",
            icon = "sentiment_neutral",
            items = listOf("请输入常用短句")
        )
        quickSubtitleSelectedGroupId = newId
        saveQuickSubtitleConfig()
    }

    fun removeQuickSubtitleGroup(index: Int) {
        if (quickSubtitleGroups.size <= 1) return
        if (index !in quickSubtitleGroups.indices) return
        val removedId = quickSubtitleGroups[index].id
        val next = quickSubtitleGroups.toMutableList().apply { removeAt(index) }
        quickSubtitleGroups = next
        if (quickSubtitleSelectedGroupId == removedId) {
            quickSubtitleSelectedGroupId = next[index.coerceAtMost(next.lastIndex)].id
        }
        saveQuickSubtitleConfig()
    }

    fun moveQuickSubtitleGroup(from: Int, to: Int) {
        if (from !in quickSubtitleGroups.indices || to !in quickSubtitleGroups.indices) return
        if (from == to) return
        val next = quickSubtitleGroups.toMutableList()
        val item = next.removeAt(from)
        next.add(to, item)
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
    }

    fun addQuickSubtitleItem(groupIndex: Int, value: String = "新快捷文本") {
        if (groupIndex !in quickSubtitleGroups.indices) return
        val text = value.trim().ifEmpty { "新快捷文本" }
        val next = quickSubtitleGroups.toMutableList()
        val g = next[groupIndex]
        next[groupIndex] = g.copy(items = g.items + text)
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
    }

    fun removeQuickSubtitleItem(groupIndex: Int, itemIndex: Int) {
        if (groupIndex !in quickSubtitleGroups.indices) return
        val g = quickSubtitleGroups[groupIndex]
        if (itemIndex !in g.items.indices) return
        if (g.items.size <= 1) return
        val items = g.items.toMutableList().apply { removeAt(itemIndex) }
        val next = quickSubtitleGroups.toMutableList()
        next[groupIndex] = g.copy(items = items)
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
    }

    fun removeQuickSubtitleItems(groupIndex: Int, itemIndexes: Set<Int>): Int {
        if (groupIndex !in quickSubtitleGroups.indices || itemIndexes.isEmpty()) return 0
        val group = quickSubtitleGroups[groupIndex]
        val validIndexes = itemIndexes.filter { it in group.items.indices }.distinct().sortedDescending()
        if (validIndexes.isEmpty()) return 0
        val items = group.items.toMutableList()
        validIndexes.forEach { idx -> items.removeAt(idx) }
        val next = quickSubtitleGroups.toMutableList()
        next[groupIndex] = group.copy(items = items)
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
        return validIndexes.size
    }

    fun moveQuickSubtitleItemsToGroup(fromGroupIndex: Int, itemIndexes: Set<Int>, toGroupIndex: Int): Int {
        if (fromGroupIndex !in quickSubtitleGroups.indices || toGroupIndex !in quickSubtitleGroups.indices) return 0
        if (fromGroupIndex == toGroupIndex || itemIndexes.isEmpty()) return 0
        val source = quickSubtitleGroups[fromGroupIndex]
        val target = quickSubtitleGroups[toGroupIndex]
        val validIndexes = itemIndexes.filter { it in source.items.indices }.distinct().sorted()
        if (validIndexes.isEmpty()) return 0
        val movedItems = validIndexes.map { source.items[it] }
        val remainItems = source.items.filterIndexed { index, _ -> index !in validIndexes }
        val next = quickSubtitleGroups.toMutableList()
        next[fromGroupIndex] = source.copy(items = remainItems)
        next[toGroupIndex] = target.copy(items = target.items + movedItems)
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
        return movedItems.size
    }

    fun moveQuickSubtitleItem(groupIndex: Int, from: Int, to: Int) {
        if (groupIndex !in quickSubtitleGroups.indices) return
        val g = quickSubtitleGroups[groupIndex]
        if (from !in g.items.indices || to !in g.items.indices || from == to) return
        val items = g.items.toMutableList()
        val item = items.removeAt(from)
        items.add(to, item)
        val next = quickSubtitleGroups.toMutableList()
        next[groupIndex] = g.copy(items = items)
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
    }

    fun updateQuickSubtitleItem(groupIndex: Int, itemIndex: Int, value: String) {
        if (groupIndex !in quickSubtitleGroups.indices) return
        val g = quickSubtitleGroups[groupIndex]
        if (itemIndex !in g.items.indices) return
        val items = g.items.toMutableList()
        items[itemIndex] = value
        val next = quickSubtitleGroups.toMutableList()
        next[groupIndex] = g.copy(items = items)
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
    }

    fun setQuickSubtitleItems(groupIndex: Int, items: List<String>) {
        if (groupIndex !in quickSubtitleGroups.indices) return
        val g = quickSubtitleGroups[groupIndex]
        val next = quickSubtitleGroups.toMutableList()
        next[groupIndex] = g.copy(items = items.toList())
        quickSubtitleGroups = next
        saveQuickSubtitleConfig()
    }

    private fun observeSoundboardPlayback() {
        viewModelScope.launch {
            SoundboardManager.playbackState().collectLatest { next ->
                soundboardPlaybackStates = next
            }
        }
    }

    private fun loadSoundboardConfig() {
        viewModelScope.launch {
            val parsed = parseSoundboardConfig(UserPrefs.getSoundboardConfig(appContext))
            applySoundboardConfig(parsed)
            SoundboardManager.updateCachedConfig(parsed)
        }
    }

    private fun applySoundboardConfig(config: com.lhtstudio.kigtts.app.data.SoundboardConfig) {
        val groups = config.groups.ifEmpty { defaultSoundboardGroups() }
        soundboardGroups = groups
        val pendingGroupId = pendingSoundboardGroupSelectionId
        val selectedId =
            groups.firstOrNull { it.id == pendingGroupId }?.id
                ?: groups.firstOrNull { it.id == config.selectedGroupId }?.id
                ?: groups.first().id
        soundboardSelectedGroupId = selectedId
        if (pendingGroupId == selectedId) {
            pendingSoundboardGroupSelectionId = null
        }
        val layout = config.portraitLayout.asAdaptiveMode()
        soundboardPortraitLayout = layout
        soundboardLandscapeLayout = layout
        soundboardNextGroupId = maxOf(2L, (groups.maxOfOrNull { it.id } ?: 0L) + 1L)
        soundboardNextItemId = maxOf(
            1L,
            groups.asSequence().flatMap { it.items.asSequence() }.maxOfOrNull { it.id }?.plus(1L) ?: 1L
        )
    }

    private fun saveSoundboardConfig() {
        val payload = serializeSoundboardConfig(
            SoundboardConfig(
                groups = soundboardGroups,
                selectedGroupId = soundboardSelectedGroupId,
                portraitLayout = soundboardPortraitLayout,
                landscapeLayout = soundboardLandscapeLayout
            )
        )
        val cachedConfig = parseSoundboardConfig(payload)
        SoundboardManager.updateCachedConfig(cachedConfig)
        if (soundboardSaving) {
            pendingSoundboardSavePayload = payload
            return
        }
        soundboardSaving = true
        viewModelScope.launch {
            try {
                var nextPayload = payload
                while (true) {
                    UserPrefs.setSoundboardConfig(appContext, nextPayload)
                    val pending = pendingSoundboardSavePayload
                    if (pending == null || pending == nextPayload) {
                        pendingSoundboardSavePayload = null
                        break
                    }
                    pendingSoundboardSavePayload = null
                    nextPayload = pending
                }
            } finally {
                soundboardSaving = false
            }
        }
    }

    private fun currentSoundboardConfig(): SoundboardConfig {
        return SoundboardConfig(
            groups = soundboardGroups,
            selectedGroupId = soundboardSelectedGroupId,
            portraitLayout = soundboardPortraitLayout,
            landscapeLayout = soundboardLandscapeLayout
        )
    }

    fun exportSoundboardPresetPackage(groupIds: Set<Long>) {
        if (groupIds.isEmpty()) {
            uiState = uiState.copy(status = "请先选择要导出的音效板分组")
            return
        }
        val config = currentSoundboardConfig()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    SoundboardPresetIo.exportPackage(appContext, config, groupIds)
                }
            }
            result.onSuccess { file ->
                uiState = uiState.copy(status = "音效板预设已导出：${file.absolutePath}")
                sharePresetFile(file, "application/x-kigtts-soundboard-preset", "分享音效板预设")
            }.onFailure { e ->
                uiState = uiState.copy(status = "音效板预设导出失败：${e.message ?: "未知错误"}")
            }
        }
    }

    fun importSoundboardPresetPackage(uri: android.net.Uri, openEditorOnSuccess: Boolean = false) {
        val current = currentSoundboardConfig()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { SoundboardPresetIo.importPackage(appContext, uri, current) }
            }
            result.onSuccess { config ->
                val importedCount = (config.groups.size - soundboardGroups.size).coerceAtLeast(0)
                applySoundboardConfig(config)
                SoundboardManager.updateCachedConfig(config)
                saveSoundboardConfig()
                val message = if (importedCount > 0) {
                    "音效板预设已安装：$importedCount 个分组"
                } else {
                    "音效板预设已安装"
                }
                uiState = uiState.copy(status = message)
                if (openEditorOnSuccess) {
                    requestPresetInstallNavigation(PresetInstallTarget.Soundboard, message)
                }
            }.onFailure { e ->
                uiState = uiState.copy(status = "音效板预设导入失败：${e.message ?: "未知错误"}")
            }
        }
    }

    fun currentSoundboardGroupIndex(): Int {
        val idx = soundboardGroups.indexOfFirst { it.id == soundboardSelectedGroupId }
        return if (idx >= 0) idx else 0
    }

    fun selectSoundboardGroup(index: Int) {
        val clamped = index.coerceIn(0, soundboardGroups.lastIndex.coerceAtLeast(0))
        val target = soundboardGroups.getOrNull(clamped) ?: return
        soundboardSelectedGroupId = target.id
        saveSoundboardConfig()
    }

    fun selectSoundboardGroupById(groupId: Long) {
        val target = soundboardGroups.firstOrNull { it.id == groupId }
        if (target == null) {
            pendingSoundboardGroupSelectionId = groupId
            return
        }
        soundboardSelectedGroupId = target.id
        saveSoundboardConfig()
    }

    fun currentSoundboardLayout(landscape: Boolean): SoundboardLayoutMode {
        return (if (landscape) soundboardLandscapeLayout else soundboardPortraitLayout).asAdaptiveMode()
    }

    @Suppress("UNUSED_PARAMETER")
    fun updateSoundboardLayout(landscape: Boolean, layout: SoundboardLayoutMode) {
        val unified = layout.asAdaptiveMode()
        soundboardPortraitLayout = unified
        soundboardLandscapeLayout = unified
        saveSoundboardConfig()
    }

    fun updateSoundboardGroupMeta(index: Int, title: String, icon: String) {
        if (index !in soundboardGroups.indices) return
        val next = soundboardGroups.toMutableList()
        val prev = next[index]
        next[index] = prev.copy(
            title = title.trim(),
            icon = icon.ifBlank { "music_note" }
        )
        soundboardGroups = next
        saveSoundboardConfig()
    }

    fun setSoundboardGroupKeywordWakeEnabled(index: Int, enabled: Boolean) {
        if (index !in soundboardGroups.indices) return
        val next = soundboardGroups.toMutableList()
        next[index] = next[index].copy(keywordWakeEnabled = enabled)
        soundboardGroups = next
        saveSoundboardConfig()
    }

    fun addSoundboardGroup() {
        val newId = soundboardNextGroupId++
        soundboardGroups = soundboardGroups + SoundboardGroup(
            id = newId,
            title = "新分组",
            icon = "music_note",
            keywordWakeEnabled = true,
            items = emptyList()
        )
        soundboardSelectedGroupId = newId
        saveSoundboardConfig()
    }

    fun removeSoundboardGroup(index: Int) {
        if (soundboardGroups.size <= 1) return
        if (index !in soundboardGroups.indices) return
        val removedId = soundboardGroups[index].id
        val next = soundboardGroups.toMutableList().apply { removeAt(index) }
        soundboardGroups = next
        if (soundboardSelectedGroupId == removedId) {
            soundboardSelectedGroupId = next[index.coerceAtMost(next.lastIndex)].id
        }
        saveSoundboardConfig()
    }

    fun moveSoundboardGroup(from: Int, to: Int) {
        if (from !in soundboardGroups.indices || to !in soundboardGroups.indices || from == to) return
        val next = soundboardGroups.toMutableList()
        val moved = next.removeAt(from)
        next.add(to, moved)
        soundboardGroups = next
        saveSoundboardConfig()
    }

    fun addSoundboardItem(groupIndex: Int, title: String = "新音效") {
        if (groupIndex !in soundboardGroups.indices) return
        val safeTitle = title.trim().ifEmpty { "新音效" }
        val next = soundboardGroups.toMutableList()
        val group = next[groupIndex]
        next[groupIndex] = group.copy(
            items = group.items + SoundboardItem(
                id = soundboardNextItemId++,
                title = safeTitle
            )
        )
        soundboardGroups = next
        saveSoundboardConfig()
    }

    fun updateSoundboardItem(
        groupIndex: Int,
        itemIndex: Int,
        transform: (SoundboardItem) -> SoundboardItem
    ) {
        if (groupIndex !in soundboardGroups.indices) return
        val group = soundboardGroups[groupIndex]
        if (itemIndex !in group.items.indices) return
        val items = group.items.toMutableList()
        items[itemIndex] = transform(items[itemIndex])
        val next = soundboardGroups.toMutableList()
        next[groupIndex] = group.copy(items = items)
        soundboardGroups = next
        saveSoundboardConfig()
    }

    fun setSoundboardItems(groupIndex: Int, items: List<SoundboardItem>) {
        if (groupIndex !in soundboardGroups.indices) return
        val next = soundboardGroups.toMutableList()
        next[groupIndex] = next[groupIndex].copy(items = items)
        soundboardGroups = next
        saveSoundboardConfig()
    }

    fun removeSoundboardItem(groupIndex: Int, itemIndex: Int) {
        if (groupIndex !in soundboardGroups.indices) return
        val group = soundboardGroups[groupIndex]
        if (itemIndex !in group.items.indices) return
        val removedItemId = group.items[itemIndex].id
        val items = group.items.toMutableList().apply { removeAt(itemIndex) }
        val next = soundboardGroups.toMutableList()
        next[groupIndex] = group.copy(items = items)
        soundboardGroups = next
        viewModelScope.launch { SoundboardManager.stop(removedItemId) }
        saveSoundboardConfig()
    }

    fun removeSoundboardItemsByIds(groupIndex: Int, itemIds: Set<Long>): Int {
        if (groupIndex !in soundboardGroups.indices || itemIds.isEmpty()) return 0
        val group = soundboardGroups[groupIndex]
        val removedIds = group.items.mapNotNull { item -> item.id.takeIf { it in itemIds } }.toSet()
        if (removedIds.isEmpty()) return 0
        val next = soundboardGroups.toMutableList()
        next[groupIndex] = group.copy(items = group.items.filterNot { it.id in removedIds })
        soundboardGroups = next
        viewModelScope.launch {
            removedIds.forEach { SoundboardManager.stop(it) }
        }
        saveSoundboardConfig()
        return removedIds.size
    }

    fun moveSoundboardItemsToGroup(fromGroupIndex: Int, itemIds: Set<Long>, toGroupIndex: Int): Int {
        if (fromGroupIndex !in soundboardGroups.indices || toGroupIndex !in soundboardGroups.indices) return 0
        if (fromGroupIndex == toGroupIndex || itemIds.isEmpty()) return 0
        val source = soundboardGroups[fromGroupIndex]
        val target = soundboardGroups[toGroupIndex]
        val movedItems = source.items.filter { it.id in itemIds }
        if (movedItems.isEmpty()) return 0
        val next = soundboardGroups.toMutableList()
        next[fromGroupIndex] = source.copy(items = source.items.filterNot { it.id in itemIds })
        next[toGroupIndex] = target.copy(items = target.items + movedItems)
        soundboardGroups = next
        saveSoundboardConfig()
        return movedItems.size
    }

    fun isSoundboardItemPlaying(itemId: Long): Boolean {
        return soundboardPlaybackStates[itemId]?.playing == true
    }

    fun soundboardItemProgress(itemId: Long): Float {
        return soundboardPlaybackStates[itemId]?.progress?.coerceIn(0f, 1f) ?: 0f
    }

    fun playSoundboardItem(item: SoundboardItem) {
        if (item.audioPath.isBlank()) {
            uiState = uiState.copy(status = "请先为该音效选择音频文件")
            return
        }
        viewModelScope.launch {
            val played = SoundboardManager.play(item)
            if (!played) {
                uiState = uiState.copy(status = "音效播放失败")
            }
        }
    }

    fun stopSoundboardItem(itemId: Long) {
        viewModelScope.launch {
            SoundboardManager.stop(itemId)
        }
    }

    fun importSoundboardAudioClip(
        groupIndex: Int,
        itemIndex: Int,
        uri: android.net.Uri,
        startMs: Long,
        endMs: Long
    ) {
        val item = soundboardGroups.getOrNull(groupIndex)?.items?.getOrNull(itemIndex) ?: return
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    SoundboardPresetIo.importAudioClip(
                        context = appContext,
                        uri = uri,
                        startMs = startMs,
                        endMs = endMs,
                        titleHint = item.title
                    )
                }
            }
            result.onSuccess { imported ->
                updateSoundboardItem(groupIndex, itemIndex) { current ->
                    current.copy(
                        audioPath = imported.path,
                        durationMs = imported.durationMs,
                        trimStartMs = imported.trimStartMs,
                        trimEndMs = imported.trimEndMs
                    )
                }
                uiState = uiState.copy(status = "音效已导入")
            }.onFailure { e ->
                uiState = uiState.copy(status = "音效导入失败：${e.message ?: "未知错误"}")
            }
        }
    }

    fun importSoundboardAudioFiles(groupIndex: Int, uris: List<android.net.Uri>) {
        if (groupIndex !in soundboardGroups.indices || uris.isEmpty()) return
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    uris.map { uri ->
                        val displayName = SoundboardPresetIo.displayName(appContext, uri)
                        val title = displayName.substringBeforeLast('.').ifBlank { "新音效" }
                        val imported = SoundboardPresetIo.importAudioClip(
                            context = appContext,
                            uri = uri,
                            startMs = 0L,
                            endMs = Long.MAX_VALUE,
                            titleHint = title
                        )
                        title to imported
                    }
                }
            }
            result.onSuccess { importedItems ->
                val next = soundboardGroups.toMutableList()
                val group = next.getOrNull(groupIndex) ?: return@onSuccess
                val newItems = importedItems.map { (title, imported) ->
                    SoundboardItem(
                        id = soundboardNextItemId++,
                        title = title.trim().ifBlank { "新音效" },
                        audioPath = imported.path,
                        durationMs = imported.durationMs,
                        trimStartMs = imported.trimStartMs,
                        trimEndMs = imported.trimEndMs
                    )
                }
                next[groupIndex] = group.copy(items = group.items + newItems)
                soundboardGroups = next
                saveSoundboardConfig()
                uiState = uiState.copy(status = "已批量导入 ${newItems.size} 个音效")
            }.onFailure { e ->
                uiState = uiState.copy(status = "批量导入音效失败：${e.message ?: "未知错误"}")
            }
        }
    }

    private fun maybeTriggerSoundboardFromText(text: String, fromQuickText: Boolean) {
        val normalized = text.trim()
        if (normalized.isEmpty()) return
        if (!uiState.soundboardKeywordTriggerEnabled) return
        if (fromQuickText && !uiState.allowQuickTextTriggerSoundboard) return
        viewModelScope.launch {
            SoundboardManager.triggerByText(appContext, normalized)
        }
    }

    private suspend fun shouldSuppressVoiceTtsForSoundboard(text: String): Boolean {
        val normalized = text.trim()
        if (normalized.isEmpty()) return false
        return uiState.soundboardKeywordTriggerEnabled &&
            uiState.soundboardSuppressTtsOnKeyword &&
            SoundboardManager.hasTriggerMatch(appContext, normalized)
    }

    private fun quickCardDir(): File {
        val dir = File(appContext.filesDir, "quick_cards")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun defaultQuickCardDraft(editId: Long, isNew: Boolean, prefillLink: String = ""): QuickCardDraft {
        return QuickCardDraft(
            editId = editId,
            isNew = isNew,
            type = QuickCardType.Text,
            title = "",
            note = "",
            themeColor = "#038387",
            link = prefillLink.trim(),
            portraitImagePath = "",
            landscapeImagePath = ""
        )
    }

    private fun QuickCard.toDraft(isNew: Boolean = false): QuickCardDraft {
        return QuickCardDraft(
            editId = id,
            isNew = isNew,
            type = type,
            title = title,
            note = note,
            themeColor = themeColor,
            link = link,
            portraitImagePath = portraitImagePath,
            landscapeImagePath = landscapeImagePath
        )
    }

    private fun loadQuickCardConfig() {
        viewModelScope.launch {
            val raw = UserPrefs.getQuickCardConfig(appContext)
            if (raw.isNullOrBlank()) return@launch
            runCatching {
                parseQuickCardConfig(raw)
            }
        }
    }

    private fun parseQuickCardConfig(raw: String) {
        val root = JSONObject(raw)
        val cardsArr = root.optJSONArray("cards") ?: JSONArray()
        val parsedCards = mutableListOf<QuickCard>()
        var maxId = 0L
        for (i in 0 until cardsArr.length()) {
            val obj = cardsArr.optJSONObject(i) ?: continue
            val id = obj.optLong("id", i.toLong() + 1L).coerceAtLeast(1L)
            val title = obj.optString("title", "")
            val note = obj.optString("note", "")
            val themeColor = obj.optString("themeColor", "#038387")
            val link = obj.optString("link", "")
            val portraitImagePath = obj.optString("portraitImagePath", "")
            val landscapeImagePath = obj.optString("landscapeImagePath", "")
            parsedCards += QuickCard(
                id = id,
                type = QuickCardType.Text,
                title = title,
                note = note,
                themeColor = normalizeQuickCardColor(themeColor),
                link = link,
                portraitImagePath = portraitImagePath,
                landscapeImagePath = landscapeImagePath
            )
            if (id > maxId) maxId = id
        }
        quickCards = parsedCards
        quickCardsNextId = maxOf(maxId + 1L, 1L)
        quickCardSelectedIndex = root.optInt("selectedIndex", 0).coerceIn(
            0,
            quickCards.lastIndex.coerceAtLeast(0)
        )
        prefetchQuickCardAssets()
    }

    private fun saveQuickCardConfig() {
        if (quickCardsSaving) return
        quickCardsSaving = true
        val root = JSONObject().apply {
            put("selectedIndex", quickCardSelectedIndex)
            val cardsArr = JSONArray()
            quickCards.forEach { c ->
                cardsArr.put(
                    JSONObject().apply {
                        put("id", c.id)
                        put("type", QuickCardType.Text.wireValue)
                        put("title", c.title)
                        put("note", c.note)
                        put("themeColor", c.themeColor)
                        put("link", c.link)
                        put("portraitImagePath", c.portraitImagePath)
                        put("landscapeImagePath", c.landscapeImagePath)
                    }
                )
            }
            put("cards", cardsArr)
        }
        val payload = root.toString()
        viewModelScope.launch {
            try {
                UserPrefs.setQuickCardConfig(appContext, payload)
            } finally {
                quickCardsSaving = false
            }
        }
        prefetchQuickCardAssets()
    }

    fun updateQuickCardSelectedIndex(index: Int) {
        if (quickCards.isEmpty()) {
            quickCardSelectedIndex = 0
            return
        }
        quickCardSelectedIndex = index.coerceIn(0, quickCards.lastIndex)
        saveQuickCardConfig()
        prefetchQuickCardAssets()
    }

    fun reorderQuickCardsByIds(orderedIds: List<Long>) {
        if (quickCards.size <= 1) return
        val byId = quickCards.associateBy { it.id }
        val seen = hashSetOf<Long>()
        val next = mutableListOf<QuickCard>()
        orderedIds.forEach { id ->
            if (seen.add(id)) {
                byId[id]?.let { next += it }
            }
        }
        quickCards.forEach { card ->
            if (seen.add(card.id)) {
                next += card
            }
        }
        if (next == quickCards) return
        val selectedId = quickCards.getOrNull(quickCardSelectedIndex)?.id
        quickCards = next
        quickCardSelectedIndex = selectedId
            ?.let { id -> quickCards.indexOfFirst { it.id == id } }
            ?.takeIf { it >= 0 }
            ?: 0
        saveQuickCardConfig()
        prefetchQuickCardAssets()
    }

    fun getQuickCard(id: Long): QuickCard? {
        return quickCards.firstOrNull { it.id == id }
    }

    fun openQuickCardPreview(cardId: Long) {
        quickCardPreviewCardId = cardId
    }

    fun closeQuickCardPreview() {
        quickCardPreviewCardId = null
    }

    fun beginCreateQuickCard(@Suppress("UNUSED_PARAMETER") type: QuickCardType, prefillLink: String = "") {
        val id = quickCardsNextId++
        quickCardDraft = defaultQuickCardDraft(editId = id, isNew = true, prefillLink = prefillLink)
    }

    fun beginEditQuickCard(cardId: Long) {
        val target = getQuickCard(cardId) ?: return
        quickCardDraft = target.toDraft(isNew = false)
    }

    fun clearQuickCardDraft() {
        quickCardDraft = null
    }

    fun updateQuickCardDraft(update: (QuickCardDraft) -> QuickCardDraft) {
        val old = quickCardDraft ?: return
        quickCardDraft = update(old)
    }

    private fun normalizeQuickCardColor(raw: String): String {
        val v = raw.trim()
        return if (Regex("^#[0-9a-fA-F]{6}$").matches(v)) v.lowercase(Locale.US) else "#038387"
    }

    private fun prefetchQuickCardAssets() {
        val cardsSnapshot = quickCards
        if (cardsSnapshot.isEmpty()) return
        val selected = quickCardSelectedIndex.coerceIn(0, cardsSnapshot.lastIndex)
        val candidateIndices = listOf(selected, selected - 1, selected + 1).distinct()
        viewModelScope.launch(Dispatchers.IO) {
            candidateIndices.forEach { index ->
                val card = cardsSnapshot.getOrNull(index) ?: return@forEach
                card.portraitImagePath.takeIf { it.isNotBlank() }?.let { QuickCardRenderCache.loadImage(it) }
                card.landscapeImagePath.takeIf { it.isNotBlank() }?.let { QuickCardRenderCache.loadImage(it) }
                card.link.takeIf { it.isNotBlank() }?.let { QuickCardRenderCache.loadQr(it) }
            }
        }
    }

    private fun normalizedQuickCardFromDraft(draft: QuickCardDraft): QuickCard {
        val id = draft.editId ?: -1L
        val normalized = draft.copy(
            title = draft.title.trim(),
            note = draft.note.trim(),
            themeColor = normalizeQuickCardColor(draft.themeColor),
            link = draft.link.trim()
        )
        return QuickCard(
            id = id,
            type = QuickCardType.Text,
            title = normalized.title,
            note = normalized.note,
            themeColor = normalized.themeColor,
            link = normalized.link,
            portraitImagePath = normalized.portraitImagePath,
            landscapeImagePath = normalized.landscapeImagePath
        )
    }

    fun hasQuickCardDraftChanges(): Boolean {
        val draft = quickCardDraft ?: return false
        val targetId = draft.editId ?: return true
        val base = quickCards.firstOrNull { it.id == targetId } ?: return true
        return normalizedQuickCardFromDraft(draft) != base
    }

    fun saveQuickCardDraft(): QuickCard? {
        val draft = quickCardDraft ?: return null
        val saved = normalizedQuickCardFromDraft(draft).let { normalized ->
            if (normalized.id > 0L) normalized else normalized.copy(id = quickCardsNextId++)
        }
        val next = quickCards.toMutableList()
        val idx = next.indexOfFirst { it.id == saved.id }
        if (idx >= 0) {
            next[idx] = saved
            quickCardSelectedIndex = idx
        } else {
            next += saved
            quickCardSelectedIndex = next.lastIndex
        }
        quickCards = next
        quickCardDraft = saved.toDraft(isNew = false)
        saveQuickCardConfig()
        return saved
    }

    fun duplicateEditingQuickCard(): QuickCard? {
        val draft = quickCardDraft ?: return null
        val sourceId = draft.editId ?: return null
        val source = quickCards.firstOrNull { it.id == sourceId } ?: return null
        val id = quickCardsNextId++
        val copied = source.copy(
            id = id,
            title = "${source.title} 副本"
        )
        quickCards = quickCards + copied
        quickCardSelectedIndex = quickCards.lastIndex
        quickCardDraft = copied.toDraft(isNew = false)
        saveQuickCardConfig()
        return copied
    }

    fun deleteEditingQuickCard(): Boolean {
        val draft = quickCardDraft ?: return false
        val id = draft.editId ?: return false
        val idx = quickCards.indexOfFirst { it.id == id }
        if (idx < 0) return false
        val next = quickCards.toMutableList()
        next.removeAt(idx)
        quickCards = next
        quickCardSelectedIndex = quickCardSelectedIndex.coerceIn(0, quickCards.lastIndex.coerceAtLeast(0))
        quickCardDraft = null
        saveQuickCardConfig()
        return true
    }

    fun deleteQuickCardsByIds(ids: Set<Long>): Int {
        val targetIds = ids.filterTo(hashSetOf()) { id -> quickCards.any { it.id == id } }
        if (targetIds.isEmpty()) return 0
        val selectedId = quickCards.getOrNull(quickCardSelectedIndex)?.id?.takeUnless { it in targetIds }
        val next = quickCards.filterNot { it.id in targetIds }
        val removedCount = quickCards.size - next.size
        if (removedCount <= 0) return 0
        quickCards = next
        quickCardSelectedIndex =
            selectedId
                ?.let { id -> quickCards.indexOfFirst { it.id == id } }
                ?.takeIf { it >= 0 }
                ?: quickCardSelectedIndex.coerceIn(0, quickCards.lastIndex.coerceAtLeast(0))
        if (quickCardPreviewCardId?.let { it in targetIds } == true) {
            quickCardPreviewCardId = null
        }
        if (quickCardDraft?.editId?.let { it in targetIds } == true) {
            quickCardDraft = null
        }
        saveQuickCardConfig()
        return removedCount
    }

    private fun copyUriToQuickCardImage(uri: android.net.Uri, fileName: String): String? {
        return runCatching {
            val outFile = File(quickCardDir(), fileName)
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                outFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return null
            outFile.absolutePath
        }.getOrNull()
    }

    fun setQuickCardDraftImage(uri: android.net.Uri, landscape: Boolean): Boolean {
        val draft = quickCardDraft ?: return false
        val id = draft.editId ?: return false
        val tag = if (landscape) "landscape" else "portrait"
        val fileName = "card_${id}_${tag}_${System.currentTimeMillis()}.png"
        val path = copyUriToQuickCardImage(uri, fileName) ?: return false
        quickCardDraft = if (landscape) {
            draft.copy(landscapeImagePath = path)
        } else {
            draft.copy(portraitImagePath = path)
        }
        return true
    }

    fun clearQuickCardDraftImage(landscape: Boolean) {
        val draft = quickCardDraft ?: return
        quickCardDraft = if (landscape) {
            draft.copy(landscapeImagePath = "")
        } else {
            draft.copy(portraitImagePath = "")
        }
    }

    private fun decodeQrContentFromBitmapInternal(bitmap: Bitmap): String? {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= 0 || height <= 0) return null
        return runCatching {
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)
            decodeQrWithZxing(source)
        }.getOrNull()
    }

    suspend fun decodeQrContentFromBitmap(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        val mlKit = createQrMlKitScanner()
        try {
            val mlResult = awaitTask(mlKit.process(InputImage.fromBitmap(bitmap, 0)))
                ?.firstDecodedQrText()
            if (!mlResult.isNullOrEmpty()) {
                return@withContext mlResult
            }
        } finally {
            runCatching { mlKit.close() }
        }
        decodeQrContentFromBitmapInternal(bitmap)
    }

    suspend fun decodeQrContentFromImage(uri: android.net.Uri): String? = withContext(Dispatchers.IO) {
        val mlKit = createQrMlKitScanner()
        try {
            val inputImage = runCatching { InputImage.fromFilePath(appContext, uri) }.getOrNull()
            if (inputImage != null) {
                val mlResult = awaitTask(mlKit.process(inputImage))?.firstDecodedQrText()
                if (!mlResult.isNullOrEmpty()) {
                    return@withContext mlResult
                }
            }
        } finally {
            runCatching { mlKit.close() }
        }
        val bmp = runCatching {
            appContext.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        }.getOrNull() ?: return@withContext null
        decodeQrContentFromBitmapInternal(bmp)
    }

    private fun preloadAsr(asrDir: File?) {
        if (asrDir == null) return
        val host = realtimeHost
        viewModelScope.launch {
            if (host != null) {
                host.updateSelectedAsrDir(asrDir, preload = true)
            } else {
                pendingHostAsrDir = asrDir
                requestRealtimeHost()
            }
        }
    }

    private fun preloadTts(voiceDir: File?) {
        if (voiceDir == null) return
        val host = realtimeHost
        viewModelScope.launch {
            val loaded = if (host != null) {
                host.updateSelectedVoiceDir(voiceDir, preload = true)
                true
            } else {
                pendingHostVoiceDir = voiceDir
                requestRealtimeHost()
                true
            }
            if (!loaded && uiState.voiceDir?.absolutePath == voiceDir.absolutePath) {
                uiState = uiState.copy(
                    status = if (isSystemTtsVoiceDir(voiceDir)) {
                        "系统 TTS 初始化失败，请先完成系统 TTS 设置"
                    } else {
                        "音色包加载失败"
                    }
                )
            }
        }
    }

    fun openSystemTtsSetup(context: Context) {
        val intents = listOf(
            android.content.Intent("com.android.settings.TTS_SETTINGS"),
            android.content.Intent(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
        )
        for (intent in intents) {
            val resolved = runCatching {
                context.packageManager.resolveActivity(intent, 0)
            }.getOrNull()
            if (resolved != null) {
                runCatching {
                    context.startActivity(intent.apply {
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }.onSuccess { return }.onFailure {
                    AppLogger.e("openSystemTtsSetup failed action=${intent.action}", it)
                }
            }
        }
        toast(context, "无法打开系统 TTS 设置")
    }

    private fun systemTtsVoiceDir(): File = repo.systemTtsVirtualDir()

    private fun isSystemTtsVoicePack(pack: VoicePackInfo): Boolean = isSystemTtsVoiceDir(pack.dir)

    private fun isKokoroVoicePack(pack: VoicePackInfo): Boolean = isKokoroVoiceDir(pack.dir)

    private suspend fun resolvePreferredVoiceDir(lastName: String?): File? {
        val resolved = when (lastName) {
            SYSTEM_TTS_VOICE_NAME -> systemTtsVoiceDir()
            KOKORO_VOICE_NAME -> repo.kokoroVoiceDir().takeIf { repo.kokoroVoiceStatus().installed }
            null -> null
            else -> repo.resolveVoicePack(lastName)
        }
        if (resolved != null) return resolved
        return withContext(Dispatchers.IO) { repo.listVoicePacks().firstOrNull()?.dir }
            ?: repo.kokoroVoiceDir().takeIf { repo.kokoroVoiceStatus().installed }
            ?: systemTtsVoiceDir()
    }

    private suspend fun loadSystemTtsVoicePackInfo(existing: List<VoicePackInfo>): VoicePackInfo {
        val defaultOrder = (existing.maxOfOrNull { it.meta.order } ?: -1L) + 1L
        val order = UserPrefs.getSystemTtsOrder(appContext) ?: defaultOrder.also {
            UserPrefs.setSystemTtsOrder(appContext, it)
        }
        return VoicePackInfo(
            dir = systemTtsVoiceDir(),
            meta = VoicePackMeta(
                name = SYSTEM_TTS_DEFAULT_LABEL,
                remark = SYSTEM_TTS_DEFAULT_REMARK,
                avatar = "avatar.png",
                pinned = UserPrefs.getSystemTtsPinned(appContext),
                order = order
            )
        )
    }

    private suspend fun loadVoicePackList(): List<VoicePackInfo> {
        val physical = withContext(Dispatchers.IO) { repo.listVoicePacks() }
        val kokoroStatus = withContext(Dispatchers.IO) { repo.kokoroVoiceStatus() }
        val kokoro = if (kokoroStatus.installed) {
            val defaultOrder = (physical.maxOfOrNull { it.meta.order } ?: -1L) + 1L
            val order = UserPrefs.getKokoroVoiceOrder(appContext) ?: defaultOrder.also {
                UserPrefs.setKokoroVoiceOrder(appContext, it)
            }
            listOf(
                VoicePackInfo(
                    dir = repo.kokoroVoiceDir(),
                    meta = VoicePackMeta(
                        name = "Kokoro",
                        remark = "离线朗读声音，可切换多个声音编号",
                        avatar = "avatar.png",
                        pinned = UserPrefs.getKokoroVoicePinned(appContext),
                        order = order
                    )
                )
            )
        } else {
            emptyList()
        }
        val system = loadSystemTtsVoicePackInfo(physical)
        return sortVoicePacks(kokoro + physical + system)
    }

    private suspend fun findFallbackVoicePack(excludingDir: File): File? {
        val excludedPath = excludingDir.absolutePath
        val listed = withContext(Dispatchers.IO) { repo.listVoicePacks() }
            .firstOrNull { it.dir.absolutePath != excludedPath }
            ?.dir
        if (listed != null) return listed
        val kokoro = repo.kokoroVoiceDir()
        if (kokoro.absolutePath != excludedPath && repo.kokoroVoiceStatus().installed) {
            return kokoro
        }
        return systemTtsVoiceDir().takeIf { it.absolutePath != excludedPath }
    }

    private fun fallbackVoiceStatus(dir: File): String {
        return when {
            isSystemTtsVoiceDir(dir) -> "已切换到系统 TTS"
            isKokoroVoiceDir(dir) -> "已切换备用语音包：Kokoro"
            else -> "已切换备用语音包：${dir.name}"
        }
    }

    private suspend fun stopRealtimeImmediatelyForVoicePackDeletion() {
        realtimeHost?.let { host ->
            host.stopForVoicePackDeletion()
            return
        }
        RealtimeOwnerGate.release(APP_REALTIME_OWNER_TAG)
        KeepAliveService.stop(appContext)
        realtimeInputLevel = 0f
        realtimePlaybackProgress = 0f
        uiState = uiState.copy(
            running = false,
            status = "当前语音包已删除，麦克风已停止",
            pushToTalkPressed = false,
            pushToTalkStreamingText = ""
        )
    }

    fun loadBundledAsr() {
        if (uiState.asrDir != null) return
        viewModelScope.launch {
            val (dir, loadStatus) = withContext(Dispatchers.IO) {
                val resolvedDir = repo.ensureBundledAsr()
                val resourceStatus = repo.recognitionResourceStatus()
                val statusText = if (
                    resolvedDir != null &&
                    resourceStatus.asrDir?.absolutePath == resolvedDir.absolutePath
                ) {
                    "已加载语音识别资源包"
                } else {
                    "已加载语音识别资源包"
                }
                resolvedDir to statusText
            }
            if (dir != null) {
                val host = realtimeHost
                if (host != null) {
                    host.updateSelectedAsrDir(dir, status = loadStatus, preload = true)
                } else {
                    uiState = uiState.copy(asrDir = dir, status = loadStatus)
                    preloadAsr(dir)
                }
            } else {
                uiState = uiState.copy(status = "请先安装语音识别资源包")
            }
        }
    }

    fun refreshRecognitionResourceStatus() {
        viewModelScope.launch {
            val status = withContext(Dispatchers.IO) { repo.recognitionResourceStatus() }
            val installedAsrDir = status.asrDir
            val shouldApplyAsrDir =
                installedAsrDir != null &&
                    uiState.asrDir?.absolutePath != installedAsrDir.absolutePath
            uiState = uiState.copy(
                recognitionResourceInstalled = status.installed,
                recognitionResourceName = status.name,
                recognitionResourceVersion = status.version,
                recognitionResourceStatus = recognitionResourceStatusText(status),
                recognitionResourceBusy = false,
                recognitionResourceProgressStage = "",
                recognitionResourceProgress = -1f,
                asrDir = installedAsrDir ?: uiState.asrDir
            )
            if (shouldApplyAsrDir && installedAsrDir != null) {
                val host = realtimeHost
                if (host != null) {
                    host.updateSelectedAsrDir(installedAsrDir, status = "已加载语音识别资源包", preload = true)
                } else {
                    preloadAsr(installedAsrDir)
                }
            }
        }
    }

    fun setRecognitionResourceSources(
        modelScopeUrl: String,
        huggingFaceUrl: String,
        preferredSource: Int
    ) {
        viewModelScope.launch {
            UserPrefs.setRecognitionResourceSources(
                appContext,
                modelScopeUrl = modelScopeUrl,
                huggingFaceUrl = huggingFaceUrl,
                preferredSource = preferredSource
            )
            uiState = uiState.copy(
                recognitionResourceModelScopeUrl = modelScopeUrl.trim(),
                recognitionResourceHuggingFaceUrl = huggingFaceUrl.trim(),
                recognitionResourcePreferredSource = preferredSource.coerceIn(
                    UserPrefs.RECOGNITION_RESOURCE_SOURCE_MODELSCOPE,
                    UserPrefs.RECOGNITION_RESOURCE_SOURCE_HUGGINGFACE
                )
            )
        }
    }

    fun downloadRecognitionResources() {
        if (uiState.recognitionResourceBusy) return
        val url = preferredRecognitionResourceUrl()
        if (url.isBlank()) {
            uiState = uiState.copy(status = "请先配置语音识别资源包下载源")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(
                recognitionResourceBusy = true,
                recognitionResourceProgressStage = "准备下载",
                recognitionResourceProgress = -1f,
                recognitionResourceStatus = "准备下载语音识别资源包"
            )
            try {
                val status = withContext(Dispatchers.IO) {
                    repo.downloadRecognitionResources(url) { progress ->
                        postRecognitionResourceProgress(progress)
                    }
                }
                applyInstalledRecognitionResource(status, "语音识别资源包安装完成")
            } catch (e: Exception) {
                AppLogger.e("downloadRecognitionResources failed", e)
                uiState = uiState.copy(
                    recognitionResourceBusy = false,
                    recognitionResourceProgressStage = "",
                    recognitionResourceProgress = -1f,
                    recognitionResourceStatus = "语音识别资源包安装失败：${e.message ?: "未知错误"}",
                    status = "语音识别资源包安装失败"
                )
            }
        }
    }

    fun installRecognitionResources(uri: android.net.Uri) {
        if (uiState.recognitionResourceBusy) return
        viewModelScope.launch {
            uiState = uiState.copy(
                recognitionResourceBusy = true,
                recognitionResourceProgressStage = "准备安装",
                recognitionResourceProgress = -1f,
                recognitionResourceStatus = "准备安装语音识别资源包"
            )
            try {
                val status = withContext(Dispatchers.IO) {
                    repo.installRecognitionResources(uri, appContext.contentResolver) { progress ->
                        postRecognitionResourceProgress(progress)
                    }
                }
                applyInstalledRecognitionResource(status, "语音识别资源包安装完成")
            } catch (e: Exception) {
                AppLogger.e("installRecognitionResources failed", e)
                uiState = uiState.copy(
                    recognitionResourceBusy = false,
                    recognitionResourceProgressStage = "",
                    recognitionResourceProgress = -1f,
                    recognitionResourceStatus = "语音识别资源包安装失败：${e.message ?: "未知错误"}",
                    status = "语音识别资源包安装失败"
                )
            }
        }
    }

    fun refreshKokoroVoiceStatus() {
        viewModelScope.launch {
            val status = withContext(Dispatchers.IO) { repo.kokoroVoiceStatus() }
            uiState = uiState.copy(
                kokoroInstalled = status.installed,
                kokoroStatus = kokoroStatusText(status),
                kokoroBusy = false,
                kokoroProgressStage = "",
                kokoroProgress = -1f
            )
            refreshVoicePacks()
        }
    }

    fun setKokoroSources(hfUrl: String, hfMirrorUrl: String, modelScopeUrl: String, preferredSource: Int) {
        viewModelScope.launch {
            UserPrefs.setKokoroSources(appContext, hfUrl, hfMirrorUrl, modelScopeUrl, preferredSource)
            uiState = uiState.copy(
                kokoroHfUrl = hfUrl.trim(),
                kokoroHfMirrorUrl = hfMirrorUrl.trim(),
                kokoroModelScopeUrl = modelScopeUrl.trim(),
                kokoroPreferredSource = preferredSource.coerceIn(
                    UserPrefs.KOKORO_SOURCE_HF,
                    UserPrefs.KOKORO_SOURCE_MODELSCOPE
                )
            )
        }
    }

    fun setKokoroSpeakerId(speakerId: Int) {
        val normalized = speakerId.coerceIn(UserPrefs.KOKORO_MIN_SPEAKER_ID, UserPrefs.KOKORO_MAX_SPEAKER_ID)
        uiState = uiState.copy(kokoroSpeakerId = normalized)
        realtimeHost?.setKokoroSpeakerId(normalized)
        viewModelScope.launch {
            UserPrefs.setKokoroSpeakerId(appContext, normalized)
        }
    }

    fun downloadKokoroVoice() {
        if (uiState.kokoroBusy) return
        val url = preferredKokoroSourceUrl()
        if (url.isBlank()) {
            uiState = uiState.copy(status = "请先配置 Kokoro 下载源")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(
                kokoroBusy = true,
                kokoroProgressStage = "准备下载",
                kokoroProgress = -1f,
                kokoroStatus = "准备下载 Kokoro 离线语音"
            )
            try {
                val status = withContext(Dispatchers.IO) {
                    repo.downloadKokoroVoice(url) { progress -> postKokoroProgress(progress) }
                }
                applyInstalledKokoroVoice(status, "Kokoro 离线语音安装完成")
            } catch (e: Exception) {
                AppLogger.e("downloadKokoroVoice failed", e)
                uiState = uiState.copy(
                    kokoroBusy = false,
                    kokoroProgressStage = "",
                    kokoroProgress = -1f,
                    kokoroStatus = "Kokoro 离线语音安装失败：${e.message ?: "未知错误"}",
                    status = "Kokoro 离线语音安装失败"
                )
            }
        }
    }

    fun installKokoroVoice(uri: android.net.Uri) {
        if (uiState.kokoroBusy) return
        viewModelScope.launch {
            uiState = uiState.copy(
                kokoroBusy = true,
                kokoroProgressStage = "准备安装",
                kokoroProgress = -1f,
                kokoroStatus = "准备安装 Kokoro 离线语音"
            )
            try {
                val status = withContext(Dispatchers.IO) {
                    repo.installKokoroVoice(uri, appContext.contentResolver) { progress -> postKokoroProgress(progress) }
                }
                applyInstalledKokoroVoice(status, "Kokoro 离线语音安装完成")
            } catch (e: Exception) {
                AppLogger.e("installKokoroVoice failed", e)
                uiState = uiState.copy(
                    kokoroBusy = false,
                    kokoroProgressStage = "",
                    kokoroProgress = -1f,
                    kokoroStatus = "Kokoro 离线语音安装失败：${e.message ?: "未知错误"}",
                    status = "Kokoro 离线语音安装失败"
                )
            }
        }
    }

    private fun preferredKokoroSourceUrl(): String {
        val hf = uiState.kokoroHfUrl.trim()
        val mirror = uiState.kokoroHfMirrorUrl.trim()
        val modelScope = uiState.kokoroModelScopeUrl.trim()
        return when (uiState.kokoroPreferredSource) {
            UserPrefs.KOKORO_SOURCE_HF -> hf.ifBlank { mirror.ifBlank { modelScope } }
            UserPrefs.KOKORO_SOURCE_MODELSCOPE -> modelScope.ifBlank { mirror.ifBlank { hf } }
            else -> mirror.ifBlank { hf.ifBlank { modelScope } }
        }
    }

    private fun postKokoroProgress(progress: RecognitionResourceProgress) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            uiState = uiState.copy(
                kokoroBusy = true,
                kokoroProgressStage = progress.stage,
                kokoroProgress = progress.fraction,
                kokoroStatus = if (progress.fraction in 0f..1f) {
                    "${progress.stage} ${(progress.fraction * 100f).roundToInt()}%"
                } else {
                    progress.stage
                }
            )
        }
    }

    private suspend fun applyInstalledKokoroVoice(status: KokoroVoiceStatus, message: String) {
        uiState = uiState.copy(
            kokoroInstalled = status.installed,
            kokoroStatus = kokoroStatusText(status),
            kokoroBusy = false,
            kokoroProgressStage = "",
            kokoroProgress = -1f,
            status = message
        )
        refreshVoicePacks()
    }

    private fun kokoroStatusText(status: KokoroVoiceStatus): String {
        if (!status.installed) return "未安装 Kokoro 离线语音。安装后可以在语音包页面选择 Kokoro。"
        return buildString {
            append("已安装 ")
            append(status.name)
            if (status.version.isNotBlank()) {
                append(" / ")
                append(status.version)
            }
            append("，可切换多个声音编号。")
        }
    }

    private fun preferredRecognitionResourceUrl(): String {
        val modelScope = uiState.recognitionResourceModelScopeUrl.trim()
        val huggingFace = uiState.recognitionResourceHuggingFaceUrl.trim()
        return if (uiState.recognitionResourcePreferredSource == UserPrefs.RECOGNITION_RESOURCE_SOURCE_HUGGINGFACE) {
            huggingFace.ifBlank { modelScope }
        } else {
            modelScope.ifBlank { huggingFace }
        }
    }

    private fun postRecognitionResourceProgress(progress: RecognitionResourceProgress) {
        appContext.runOnUiThread {
            uiState = uiState.copy(
                recognitionResourceBusy = true,
                recognitionResourceProgressStage = progress.stage,
                recognitionResourceProgress = progress.fraction,
                recognitionResourceStatus = if (progress.fraction in 0f..1f) {
                    "${progress.stage} ${(progress.fraction * 100f).roundToInt()}%"
                } else {
                    progress.stage
                }
            )
        }
    }

    private fun recognitionResourceStatusText(status: RecognitionResourceStatus): String {
        if (!status.installed) return "未安装语音识别资源包，请先从下载源或本地文件安装。"
        val version = status.version.takeIf { it.isNotBlank() }?.let { "，版本 $it" }.orEmpty()
        val asr = if (status.asrDir != null) "，ASR 可用" else "，ASR 未找到"
        return "${status.name}$version 已安装$asr"
    }

    private suspend fun applyInstalledRecognitionResource(
        status: RecognitionResourceStatus,
        message: String
    ) {
        val asrDir = status.asrDir
        if (asrDir != null) {
            UserPrefs.clearLastAsrName(appContext)
            val host = realtimeHost
            if (host != null) {
                host.updateSelectedAsrDir(asrDir, status = message, preload = true)
            } else {
                uiState = uiState.copy(asrDir = asrDir)
                preloadAsr(asrDir)
            }
        }
        uiState = uiState.copy(
            recognitionResourceInstalled = status.installed,
            recognitionResourceName = status.name,
            recognitionResourceVersion = status.version,
            recognitionResourceStatus = recognitionResourceStatusText(status),
            recognitionResourceBusy = false,
            recognitionResourceProgressStage = "",
            recognitionResourceProgress = -1f,
            status = message
        )
    }

    fun loadLastVoice() {
        viewModelScope.launch {
            val lastName = UserPrefs.getLastVoiceName(appContext)
            val lastDir = resolvePreferredVoiceDir(lastName)
            if (lastDir != null) {
                val voiceStatus = when {
                    isSystemTtsVoiceDir(lastDir) -> "已加载系统 TTS"
                    isKokoroVoiceDir(lastDir) -> "已加载 Kokoro"
                    else -> "已加载音色包"
                }
                uiState = uiState.copy(
                    voiceDir = lastDir,
                    status = voiceStatus
                )
                UserPrefs.setLastVoiceName(
                    appContext,
                    when {
                        isSystemTtsVoiceDir(lastDir) -> SYSTEM_TTS_VOICE_NAME
                        isKokoroVoiceDir(lastDir) -> KOKORO_VOICE_NAME
                        else -> lastDir.name
                    }
                )
            }
            val selectedVoice = uiState.voiceDir
            val host = realtimeHost
            if (host != null && selectedVoice != null) {
                host.updateSelectedVoiceDir(selectedVoice, status = uiState.status, preload = true)
            } else {
                preloadTts(selectedVoice)
            }
            refreshVoicePacks()
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            val settings = UserPrefs.getSettings(appContext)
            applySettingsSnapshot(settings)
        }
    }

    fun importAsr(uri: android.net.Uri) {
        viewModelScope.launch {
            val dir = withContext(Dispatchers.IO) { repo.importAsr(uri, appContext.contentResolver) }
            val host = realtimeHost
            if (host != null) {
                host.updateSelectedAsrDir(dir, status = "ASR 模型导入完成", preload = true)
            } else {
                uiState = uiState.copy(asrDir = dir, status = "ASR 模型导入完成")
                preloadAsr(dir)
            }
        }
    }

    fun importVoice(
        uri: android.net.Uri,
        openVoicePackPageOnSuccess: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val dir = withContext(Dispatchers.IO) { repo.importVoice(uri, appContext.contentResolver) }
                UserPrefs.setLastVoiceName(appContext, dir.name)
                val host = realtimeHost
                if (host != null) {
                    host.updateSelectedVoiceDir(dir, status = "音色包导入完成", preload = true)
                } else {
                    uiState = uiState.copy(voiceDir = dir, status = "音色包导入完成")
                    preloadTts(dir)
                }
                refreshVoicePacks()
                if (uiState.floatingOverlayEnabled) {
                    FloatingOverlayService.refresh(appContext)
                }
                if (openVoicePackPageOnSuccess) {
                    requestVoicePackInstallNavigation("语音包安装完成")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(status = e.message ?: "音色包导入失败")
                AppLogger.e("importVoice failed", e)
            }
        }
    }

    fun selectVoice(dir: File) {
        viewModelScope.launch {
            UserPrefs.setLastVoiceName(
                appContext,
                when {
                    isSystemTtsVoiceDir(dir) -> SYSTEM_TTS_VOICE_NAME
                    isKokoroVoiceDir(dir) -> KOKORO_VOICE_NAME
                    else -> dir.name
                }
            )
            val status = when {
                isSystemTtsVoiceDir(dir) -> "已选择系统 TTS"
                isKokoroVoiceDir(dir) -> "已选择 Kokoro"
                else -> "已选择音色包"
            }
            val host = realtimeHost
            if (host != null) {
                host.updateSelectedVoiceDir(dir, status = status, preload = true)
            } else {
                uiState = uiState.copy(
                    voiceDir = dir,
                    status = status
                )
                preloadTts(dir)
            }
            refreshVoicePacks()
            if (uiState.floatingOverlayEnabled) {
                FloatingOverlayService.refresh(appContext)
            }
        }
    }

    fun refreshVoicePacks() {
        viewModelScope.launch {
            val packs = loadVoicePackList()
            uiState = uiState.copy(voicePacks = packs)
        }
    }

    fun updateVoiceMeta(pack: VoicePackInfo, name: String, remark: String) {
        if (isSystemTtsVoicePack(pack) || isKokoroVoicePack(pack)) return
        val trimmedName = name.trim().ifEmpty { "未命名" }
        val trimmedRemark = remark.trim()
        viewModelScope.launch {
            repo.updateVoiceMeta(pack.dir) { meta ->
                meta.copy(name = trimmedName, remark = trimmedRemark)
            }
            refreshVoicePacks()
        }
    }

    fun updateVoiceAvatar(pack: VoicePackInfo, uri: android.net.Uri) {
        if (isSystemTtsVoicePack(pack) || isKokoroVoicePack(pack)) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.updateVoiceAvatar(pack.dir, appContext.contentResolver, uri, "avatar.png")
            }
            refreshVoicePacks()
        }
    }

    fun toggleVoicePin(pack: VoicePackInfo) {
        viewModelScope.launch {
            when {
                isSystemTtsVoicePack(pack) -> UserPrefs.setSystemTtsPinned(appContext, !pack.meta.pinned)
                isKokoroVoicePack(pack) -> UserPrefs.setKokoroVoicePinned(appContext, !pack.meta.pinned)
                else -> repo.updateVoiceMeta(pack.dir) { meta ->
                    meta.copy(pinned = !meta.pinned)
                }
            }
            refreshVoicePacks()
        }
    }

    fun moveVoice(pack: VoicePackInfo, delta: Int) {
        val list = uiState.voicePacks
        val idx = list.indexOfFirst { it.dir == pack.dir }
        if (idx < 0) return
        val newIdx = idx + delta
        if (newIdx !in list.indices) return
        val a = list[idx]
        val b = list[newIdx]
        if (a.meta.pinned != b.meta.pinned) return
        viewModelScope.launch {
            if (isSystemTtsVoiceDir(a.dir)) {
                UserPrefs.setSystemTtsOrder(appContext, b.meta.order)
            } else if (isKokoroVoiceDir(a.dir)) {
                UserPrefs.setKokoroVoiceOrder(appContext, b.meta.order)
            } else {
                repo.updateVoiceMeta(a.dir) { meta -> meta.copy(order = b.meta.order) }
            }
            if (isSystemTtsVoiceDir(b.dir)) {
                UserPrefs.setSystemTtsOrder(appContext, a.meta.order)
            } else if (isKokoroVoiceDir(b.dir)) {
                UserPrefs.setKokoroVoiceOrder(appContext, a.meta.order)
            } else {
                repo.updateVoiceMeta(b.dir) { meta -> meta.copy(order = a.meta.order) }
            }
            refreshVoicePacks()
        }
    }

    fun reorderVoicePacks(newOrder: List<VoicePackInfo>) {
        // Optimistically apply UI order to avoid one-frame fallback to stale state.
        uiState = uiState.copy(voicePacks = newOrder)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                newOrder.forEachIndexed { index, pack ->
                    if (isSystemTtsVoiceDir(pack.dir)) {
                        UserPrefs.setSystemTtsOrder(appContext, index.toLong())
                    } else if (isKokoroVoiceDir(pack.dir)) {
                        UserPrefs.setKokoroVoiceOrder(appContext, index.toLong())
                    } else {
                        repo.updateVoiceMeta(pack.dir) { meta ->
                            meta.copy(order = index.toLong())
                        }
                    }
                }
            }
            refreshVoicePacks()
        }
    }

    fun deleteVoice(pack: VoicePackInfo) {
        if (isSystemTtsVoicePack(pack)) {
            uiState = uiState.copy(status = "系统 TTS 不能删除")
            return
        }
        val current = uiState.voiceDir?.absolutePath == pack.dir.absolutePath
        viewModelScope.launch {
            val host = realtimeHost
            if (current) {
                val fallbackVoice = findFallbackVoicePack(pack.dir)
                if (fallbackVoice != null) {
                    val switched = if (host != null) {
                        host.updateSelectedVoiceDir(
                            fallbackVoice,
                            status = fallbackVoiceStatus(fallbackVoice),
                            preload = true
                        )
                        true
                    } else {
                        pendingHostVoiceDir = fallbackVoice
                        requestRealtimeHost()
                        true
                    }
                    if (!switched) {
                        uiState = uiState.copy(status = "切换备用语音包失败，已取消删除")
                        return@launch
                    }
                    UserPrefs.setLastVoiceName(
                        appContext,
                        when {
                            isSystemTtsVoiceDir(fallbackVoice) -> SYSTEM_TTS_VOICE_NAME
                            isKokoroVoiceDir(fallbackVoice) -> KOKORO_VOICE_NAME
                            else -> fallbackVoice.name
                        }
                    )
                    uiState = uiState.copy(
                        voiceDir = fallbackVoice,
                        status = fallbackVoiceStatus(fallbackVoice)
                    )
                } else {
                    if (uiState.running || host?.isMicActive() == true) {
                        stopRealtimeImmediatelyForVoicePackDeletion()
                    }
                    UserPrefs.clearLastVoiceName(appContext)
                    if (host != null) {
                        host.updateSelectedVoiceDir(null, preload = false)
                    }
                    uiState = uiState.copy(voiceDir = null)
                }
            }
            try {
                withContext(Dispatchers.IO) {
                    if (isKokoroVoicePack(pack)) {
                        repo.deleteKokoroVoice()
                    } else {
                        repo.deleteVoicePack(pack.dir)
                    }
                }
            } catch (e: SecurityException) {
                uiState = uiState.copy(status = e.message ?: "语音包删除失败")
                AppLogger.e("deleteVoice failed", e)
                return@launch
            }
            if (!current) {
                uiState = uiState.copy(status = "语音包已删除")
            } else if (uiState.voiceDir != null) {
                uiState = uiState.copy(status = "语音包已删除并切换到备用语音包")
            } else if (uiState.status.isBlank() || !uiState.status.contains("麦克风已停止")) {
                uiState = uiState.copy(status = "语音包已删除")
            }
            refreshVoicePacks()
            refreshKokoroVoiceStatus()
            if (uiState.floatingOverlayEnabled) {
                FloatingOverlayService.refresh(appContext)
            }
        }
    }

    fun shareVoice(pack: VoicePackInfo) {
        if (isSystemTtsVoicePack(pack)) {
            uiState = uiState.copy(status = "系统 TTS 不能分享")
            return
        }
        if (isKokoroVoicePack(pack)) {
            uiState = uiState.copy(status = "Kokoro 离线语音由设置中的资源安装器管理，不能作为普通语音包分享")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val shareDir = File(appContext.cacheDir, "share")
            val fileName = "${repo.sanitizeVoicePackShareName(pack.meta.name, pack.dir.name)}.kigvpk"
            val outZip = File(shareDir, fileName)
            repo.zipVoicePack(pack.dir, outZip)
            withContext(Dispatchers.Main) {
                val uri = FileProvider.getUriForFile(
                    appContext,
                    appContext.packageName + ".fileprovider",
                    outZip
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/x-kigtts-voicepack"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                appContext.startActivity(Intent.createChooser(intent, "分享语音包"))
            }
        }
    }

    private fun sharePresetFile(file: File, mimeType: String, chooserTitle: String) {
        runCatching {
            val uri = FileProvider.getUriForFile(
                appContext,
                appContext.packageName + ".fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            appContext.startActivity(Intent.createChooser(intent, chooserTitle))
        }.onFailure { e ->
            uiState = uiState.copy(status = "分享失败：${e.message ?: "未知错误"}")
        }
    }

    fun setMuteWhilePlaying(enabled: Boolean) {
        uiState = uiState.copy(muteWhilePlaying = enabled)
        realtimeHost?.setSuppressWhilePlaying(enabled)
        viewModelScope.launch {
            UserPrefs.setMuteWhilePlaying(appContext, enabled)
        }
    }

    fun setMuteWhilePlayingDelay(seconds: Float) {
        val clamped = seconds.coerceIn(0f, 5f)
        uiState = uiState.copy(muteWhilePlayingDelaySec = clamped)
        realtimeHost?.setSuppressDelaySec(clamped)
        viewModelScope.launch {
            UserPrefs.setMuteWhilePlayingDelaySec(appContext, clamped)
        }
    }

    fun setMinVolumePercent(percent: Int) {
        uiState = uiState.copy(minVolumePercent = percent)
        realtimeHost?.setMinVolumePercent(percent)
        viewModelScope.launch {
            UserPrefs.setMinVolumePercent(appContext, percent)
        }
    }

    private fun normalizeVadFlags(
        classicEnabled: Boolean,
        sileroEnabled: Boolean
    ): Pair<Boolean, Boolean> {
        return if (!classicEnabled && !sileroEnabled) {
            true to false
        } else {
            classicEnabled to sileroEnabled
        }
    }

    private fun persistVadFlags(classicEnabled: Boolean, sileroEnabled: Boolean) {
        realtimeHost?.setClassicVadEnabled(classicEnabled)
        realtimeHost?.setSileroVadEnabled(sileroEnabled)
        viewModelScope.launch {
            UserPrefs.setVadFlags(appContext, classicEnabled, sileroEnabled)
        }
    }

    fun setClassicVadEnabled(enabled: Boolean) {
        val (classicEnabled, sileroEnabled) = normalizeVadFlags(enabled, uiState.sileroVadEnabled)
        uiState = uiState.copy(
            classicVadEnabled = classicEnabled,
            sileroVadEnabled = sileroEnabled
        )
        persistVadFlags(classicEnabled, sileroEnabled)
    }

    fun setSileroVadEnabled(enabled: Boolean) {
        val (classicEnabled, sileroEnabled) = normalizeVadFlags(uiState.classicVadEnabled, enabled)
        uiState = uiState.copy(
            classicVadEnabled = classicEnabled,
            sileroVadEnabled = sileroEnabled
        )
        persistVadFlags(classicEnabled, sileroEnabled)
    }

    fun setSileroVadThreshold(threshold: Float) {
        val clamped = threshold.coerceIn(
            UserPrefs.SILERO_VAD_MIN_THRESHOLD,
            UserPrefs.SILERO_VAD_MAX_THRESHOLD
        )
        val stepped = (clamped / 0.05f).roundToInt() * 0.05f
        val normalized = stepped.coerceIn(
            UserPrefs.SILERO_VAD_MIN_THRESHOLD,
            UserPrefs.SILERO_VAD_MAX_THRESHOLD
        )
        uiState = uiState.copy(sileroVadThreshold = normalized)
        realtimeHost?.setSileroVadThreshold(normalized)
        viewModelScope.launch {
            UserPrefs.setSileroVadThreshold(appContext, normalized)
        }
    }

    fun setSileroVadPreRollMs(preRollMs: Int) {
        val normalized = ((preRollMs / 50f).roundToInt() * 50).coerceIn(
            UserPrefs.SILERO_VAD_MIN_PRE_ROLL_MS,
            UserPrefs.SILERO_VAD_MAX_PRE_ROLL_MS
        )
        uiState = uiState.copy(sileroVadPreRollMs = normalized)
        realtimeHost?.setSileroVadPreRollMs(normalized)
        viewModelScope.launch {
            UserPrefs.setSileroVadPreRollMs(appContext, normalized)
        }
    }

    fun setVadMode(mode: Int) {
        val (classicEnabled, sileroEnabled) = VadMode.toFlags(mode)
        uiState = uiState.copy(
            classicVadEnabled = classicEnabled,
            sileroVadEnabled = sileroEnabled
        )
        persistVadFlags(classicEnabled, sileroEnabled)
    }

    fun setPlaybackGainPercent(percent: Int) {
        val clamped = snapPlaybackGainPercent(percent)
        uiState = uiState.copy(playbackGainPercent = clamped)
        SoundboardManager.setPlaybackGainPercent(clamped)
        realtimeHost?.setPlaybackGainPercent(clamped)
        viewModelScope.launch {
            UserPrefs.setPlaybackGainPercent(appContext, clamped)
        }
    }

    fun setAudioFocusAvoidanceMode(mode: Int) {
        val normalized = UserPrefs.normalizeAudioFocusAvoidanceMode(mode)
        uiState = uiState.copy(audioFocusAvoidanceMode = normalized)
        SoundboardManager.setAudioFocusAvoidanceMode(appContext, normalized)
        realtimeHost?.setAudioFocusAvoidanceMode(normalized)
        viewModelScope.launch {
            UserPrefs.setAudioFocusAvoidanceMode(appContext, normalized)
        }
    }

    fun setPiperNoiseScale(value: Float) {
        val clamped = value.coerceIn(0f, 2f)
        uiState = uiState.copy(piperNoiseScale = clamped)
        realtimeHost?.setPiperNoiseScale(clamped)
        viewModelScope.launch {
            UserPrefs.setPiperNoiseScale(appContext, clamped)
        }
    }

    fun setPiperLengthScale(value: Float) {
        val clamped = value.coerceIn(0.1f, 5f)
        uiState = uiState.copy(piperLengthScale = clamped)
        realtimeHost?.setPiperLengthScale(clamped)
        viewModelScope.launch {
            UserPrefs.setPiperLengthScale(appContext, clamped)
        }
    }

    fun setPiperNoiseW(value: Float) {
        val clamped = value.coerceIn(0f, 2f)
        uiState = uiState.copy(piperNoiseW = clamped)
        realtimeHost?.setPiperNoiseW(clamped)
        viewModelScope.launch {
            UserPrefs.setPiperNoiseW(appContext, clamped)
        }
    }

    fun setPiperSentenceSilence(value: Float) {
        val clamped = value.coerceIn(0f, 2f)
        uiState = uiState.copy(piperSentenceSilence = clamped)
        realtimeHost?.setPiperSentenceSilenceSec(clamped)
        viewModelScope.launch {
            UserPrefs.setPiperSentenceSilence(appContext, clamped)
        }
    }

    fun setKeepAlive(enabled: Boolean) {
        val running = uiState.running
        uiState = uiState.copy(keepAlive = enabled)
        viewModelScope.launch {
            UserPrefs.setKeepAlive(appContext, enabled)
        }
        if (running) {
            if (enabled) {
                KeepAliveService.start(appContext)
            } else {
                KeepAliveService.stop(appContext)
            }
        }
    }

    fun setNumberReplaceMode(mode: Int) {
        val clamped = mode.coerceIn(0, 2)
        uiState = uiState.copy(numberReplaceMode = clamped)
        realtimeHost?.setNumberReplaceMode(clamped)
        viewModelScope.launch {
            UserPrefs.setNumberReplaceMode(appContext, clamped)
        }
    }

    fun setAsrSendToQuickSubtitle(enabled: Boolean) {
        uiState = uiState.copy(asrSendToQuickSubtitle = enabled)
        viewModelScope.launch {
            UserPrefs.setAsrSendToQuickSubtitle(appContext, enabled)
        }
    }

    fun setPushToTalkMode(enabled: Boolean) {
        pttSessionLastText = ""
        resetPttHistoryDedup()
        if (enabled && uiState.running) {
            stop()
        }
        uiState = uiState.copy(
            pushToTalkMode = enabled,
            running = if (enabled) false else uiState.running,
            pushToTalkPressed = false,
            pushToTalkStreamingText = ""
        )
        realtimeHost?.setPushToTalkStreamingEnabled(false)
        realtimeHost?.setSuppressAsrAutoSpeak(uiState.ttsDisabled || (enabled && uiState.pushToTalkConfirmInputMode))
        viewModelScope.launch {
            UserPrefs.setPushToTalkMode(appContext, enabled)
        }
    }

    fun setPushToTalkConfirmInputMode(enabled: Boolean) {
        pttSessionLastText = ""
        resetPttHistoryDedup()
        uiState = uiState.copy(
            pushToTalkConfirmInputMode = enabled,
            pushToTalkStreamingText = if (enabled) uiState.pushToTalkStreamingText else ""
        )
        val streamingEnabled = enabled && uiState.pushToTalkMode && uiState.pushToTalkPressed
        realtimeHost?.setPushToTalkStreamingEnabled(streamingEnabled)
        realtimeHost?.setSuppressAsrAutoSpeak(uiState.ttsDisabled || (enabled && uiState.pushToTalkMode))
        viewModelScope.launch {
            UserPrefs.setPushToTalkConfirmInput(appContext, enabled)
        }
    }

    fun setFloatingOverlayEnabled(enabled: Boolean) {
        uiState = uiState.copy(floatingOverlayEnabled = enabled)
        viewModelScope.launch {
            UserPrefs.setFloatingOverlayEnabled(appContext, enabled)
        }
    }

    fun setFloatingOverlayAutoDock(enabled: Boolean) {
        uiState = uiState.copy(floatingOverlayAutoDock = enabled)
        viewModelScope.launch {
            UserPrefs.setFloatingOverlayAutoDock(appContext, enabled)
        }
    }

    fun setFloatingOverlayShowOnLockScreen(enabled: Boolean) {
        uiState = uiState.copy(floatingOverlayShowOnLockScreen = enabled)
        viewModelScope.launch {
            UserPrefs.setFloatingOverlayShowOnLockScreen(appContext, enabled)
        }
    }

    fun setFloatingOverlayHardcodedShortcutSupplement(enabled: Boolean) {
        uiState = uiState.copy(floatingOverlayHardcodedShortcutSupplement = enabled)
        viewModelScope.launch {
            UserPrefs.setFloatingOverlayHardcodedShortcutSupplement(appContext, enabled)
        }
    }

    fun setVolumeHotkeyEnabled(sequence: VolumeHotkeySequence, enabled: Boolean) {
        uiState = when (sequence) {
            VolumeHotkeySequence.UpDown -> uiState.copy(volumeHotkeyUpDownEnabled = enabled)
            VolumeHotkeySequence.DownUp -> uiState.copy(volumeHotkeyDownUpEnabled = enabled)
        }
        viewModelScope.launch {
            UserPrefs.setVolumeHotkeyEnabled(appContext, sequence, enabled)
        }
    }

    fun setVolumeHotkeyAction(sequence: VolumeHotkeySequence, action: VolumeHotkeyActionSpec) {
        uiState = when (sequence) {
            VolumeHotkeySequence.UpDown -> uiState.copy(volumeHotkeyUpDownAction = action)
            VolumeHotkeySequence.DownUp -> uiState.copy(volumeHotkeyDownUpAction = action)
        }
        viewModelScope.launch {
            UserPrefs.setVolumeHotkeyAction(appContext, sequence, action)
        }
    }

    fun setVolumeHotkeyWindowMs(windowMs: Int) {
        val normalized = (windowMs / 100) * 100
        val clamped = normalized.coerceIn(
            UserPrefs.VOLUME_HOTKEY_MIN_WINDOW_MS,
            UserPrefs.VOLUME_HOTKEY_MAX_WINDOW_MS
        )
        uiState = uiState.copy(volumeHotkeyWindowMs = clamped)
        viewModelScope.launch {
            UserPrefs.setVolumeHotkeyWindowMs(appContext, clamped)
        }
    }

    fun setVolumeHotkeyAccessibilityEnabled(enabled: Boolean) {
        uiState = uiState.copy(volumeHotkeyAccessibilityEnabled = enabled)
        viewModelScope.launch {
            UserPrefs.setVolumeHotkeyAccessibilityEnabled(appContext, enabled)
        }
    }

    fun setVolumeHotkeyEnableWarningDismissed(dismissed: Boolean) {
        uiState = uiState.copy(volumeHotkeyEnableWarningDismissed = dismissed)
        viewModelScope.launch {
            UserPrefs.setVolumeHotkeyEnableWarningDismissed(appContext, dismissed)
        }
    }

    fun setTtsDisabled(enabled: Boolean) {
        uiState = uiState.copy(ttsDisabled = enabled)
        realtimeHost?.setTtsDisabled(enabled)
        realtimeHost?.setSuppressAsrAutoSpeak(
            enabled || (uiState.pushToTalkMode && uiState.pushToTalkConfirmInputMode)
        )
        viewModelScope.launch {
            UserPrefs.setTtsDisabled(appContext, enabled)
        }
    }

    fun setSoundboardKeywordTriggerEnabled(enabled: Boolean) {
        uiState = uiState.copy(soundboardKeywordTriggerEnabled = enabled)
        viewModelScope.launch {
            UserPrefs.setSoundboardKeywordTriggerEnabled(appContext, enabled)
        }
    }

    fun setSoundboardSuppressTtsOnKeyword(enabled: Boolean) {
        uiState = uiState.copy(soundboardSuppressTtsOnKeyword = enabled)
        viewModelScope.launch {
            UserPrefs.setSoundboardSuppressTtsOnKeyword(appContext, enabled)
        }
    }

    fun setAllowQuickTextTriggerSoundboard(enabled: Boolean) {
        uiState = uiState.copy(allowQuickTextTriggerSoundboard = enabled)
        viewModelScope.launch {
            UserPrefs.setAllowQuickTextTriggerSoundboard(appContext, enabled)
        }
    }

    fun setQuickSubtitleInterruptQueue(enabled: Boolean) {
        uiState = uiState.copy(quickSubtitleInterruptQueue = enabled)
        viewModelScope.launch {
            UserPrefs.setQuickSubtitleInterruptQueue(appContext, enabled)
        }
    }

    fun setQuickSubtitleAutoFit(enabled: Boolean) {
        uiState = uiState.copy(quickSubtitleAutoFit = enabled)
        viewModelScope.launch {
            UserPrefs.setQuickSubtitleAutoFit(appContext, enabled)
        }
    }

    fun setQuickSubtitleAllowLargeFont(enabled: Boolean) {
        val beforeFontSize = quickSubtitleFontSizeSp
        uiState = uiState.copy(quickSubtitleAllowLargeFont = enabled)
        if (!enabled) {
            coerceQuickSubtitleFontSizeToCurrentLimit()
        }
        val fontChanged = quickSubtitleFontSizeSp != beforeFontSize
        if (fontChanged) saveQuickSubtitleConfig()
        viewModelScope.launch {
            UserPrefs.setQuickSubtitleAllowLargeFont(appContext, enabled)
        }
    }

    fun setQuickSubtitleCompactControls(enabled: Boolean) {
        uiState = uiState.copy(quickSubtitleCompactControls = enabled)
        viewModelScope.launch {
            UserPrefs.setQuickSubtitleCompactControls(appContext, enabled)
        }
    }

    fun setQuickSubtitleKeepInputPreview(enabled: Boolean) {
        uiState = uiState.copy(quickSubtitleKeepInputPreview = enabled)
        viewModelScope.launch {
            UserPrefs.setQuickSubtitleKeepInputPreview(appContext, enabled)
        }
    }

    fun setBluetoothMediaTitleSubtitle(enabled: Boolean) {
        uiState = uiState.copy(bluetoothMediaTitleSubtitle = enabled)
        BluetoothMediaTitleBridge.setEnabled(appContext, enabled, quickSubtitleCurrentText)
        if (enabled) {
            syncBluetoothMediaTitleToCommittedQuickSubtitle()
        }
        viewModelScope.launch {
            UserPrefs.setBluetoothMediaTitleSubtitle(appContext, enabled)
        }
    }

    fun setLiveSubtitleNotificationEnabled(enabled: Boolean) {
        uiState = uiState.copy(liveSubtitleNotificationEnabled = enabled)
        LiveSubtitleNotificationBridge.update(
            appContext,
            enabled,
            quickSubtitleCurrentText,
            uiState.status
        )
        viewModelScope.launch {
            UserPrefs.setLiveSubtitleNotificationEnabled(appContext, enabled)
        }
    }

    fun setDrawingKeepCanvasOrientationToDevice(enabled: Boolean) {
        uiState = uiState.copy(drawingKeepCanvasOrientationToDevice = enabled)
        viewModelScope.launch {
            UserPrefs.setDrawingKeepCanvasOrientationToDevice(appContext, enabled)
        }
    }

    fun setPushToTalkPressed(pressed: Boolean) {
        if (uiState.pushToTalkPressed == pressed) return
        uiState = uiState.copy(
            pushToTalkPressed = pressed,
            pushToTalkStreamingText = if (!pressed) "" else uiState.pushToTalkStreamingText
        )
        val enabled = uiState.pushToTalkMode && uiState.pushToTalkConfirmInputMode && pressed
        val host = requestRealtimeHost()
        if (host != null) {
            host.setPushToTalkPressed(pressed)
        } else if (enabled) {
            pendingHostStartRequest = true
        }
    }

    fun beginPushToTalkSession() {
        if (!uiState.pushToTalkConfirmInputMode) return
        pttSessionLastText = ""
        pttSessionCommitConsumed = false
        resetPttHistoryDedup()
        uiState = uiState.copy(pushToTalkStreamingText = "")
        realtimeHost?.beginPushToTalkSession()
    }

    fun commitPushToTalkSession(action: PttConfirmReleaseAction) {
        if (!uiState.pushToTalkConfirmInputMode) return
        if (pttSessionCommitConsumed) return
        realtimeHost?.let { host ->
            pttSessionCommitConsumed = true
            val mappedAction = when (action) {
                PttConfirmReleaseAction.SendToSubtitle -> RealtimeRuntimeBridge.PttCommitAction.SendToSubtitle
                PttConfirmReleaseAction.SendToInput -> RealtimeRuntimeBridge.PttCommitAction.SendToInput
                PttConfirmReleaseAction.Cancel -> RealtimeRuntimeBridge.PttCommitAction.Cancel
            }
            host.commitPushToTalkSession(mappedAction)
            pttSessionLastText = ""
            resetPttHistoryDedup()
            return
        }
        pttSessionCommitConsumed = true
        val text = uiState.pushToTalkStreamingText.trim().ifBlank { pttSessionLastText.trim() }
        when (action) {
            PttConfirmReleaseAction.SendToSubtitle -> {
                if (text.isNotEmpty()) {
                    if (!quickSubtitlePlayOnSend) {
                        appendRecognizedHistory(text)
                        applyQuickSubtitleText(
                            text = text,
                            enqueueSpeak = false
                        )
                    } else {
                        // 朗读开启时，也只在松手提交：
                        // 先上屏，再手动入历史(绑定真实队列ID)，进度条由 onProgress 驱动。
                        applyQuickSubtitleText(
                            text = text,
                            enqueueSpeak = false
                        )
                        enqueuePttSpeakAndAppendHistory(text)
                    }
                }
            }
            PttConfirmReleaseAction.SendToInput -> {
                if (text.isNotEmpty()) {
                    appendRecognizedHistory(text)
                    quickSubtitleInputText = text
                }
            }
            PttConfirmReleaseAction.Cancel -> Unit
        }
        pttSessionLastText = ""
        resetPttHistoryDedup()
        uiState = uiState.copy(pushToTalkStreamingText = "")
    }

    private fun enqueuePttSpeakAndAppendHistory(text: String) {
        val message = text.trim()
        if (message.isEmpty()) return
        if (uiState.ttsDisabled) {
            appendRecognizedHistory(message)
            uiState = uiState.copy(status = TTS_DISABLED_MESSAGE)
            return
        }
        viewModelScope.launch {
            if (shouldSuppressVoiceTtsForSoundboard(message)) {
                appendRecognizedHistory(message)
                uiState = uiState.copy(status = "已触发音效板，跳过本句朗读")
                return@launch
            }
            val host = requestRealtimeHost("音频宿主初始化中")
            val queuedId = host?.speakText(message)
            if (queuedId != null) {
                appendRecognizedHistory(message, queuedId)
                uiState = uiState.copy(status = "已加入朗读队列")
            } else if (host != null) {
                appendRecognizedHistory(message)
            }
        }
    }

    fun canAddSpeakerProfile(): Boolean {
        return speakerProfiles.size < MAX_SPEAKER_PROFILES
    }

    private fun speakerProfileUiItems(): List<SpeakerProfileUiItem> {
        return speakerProfiles.mapIndexed { index, profile ->
            SpeakerProfileUiItem(id = profile.id, name = "样本 ${index + 1}")
        }
    }

    private fun speakerProfileVectors(): List<FloatArray> {
        return speakerProfiles.map { it.vector.copyOf() }
    }

    fun setSpeakerVerifyEnabled(enabled: Boolean) {
        uiState = uiState.copy(speakerVerifyEnabled = enabled)
        realtimeHost?.setSpeakerVerifyEnabled(enabled)
        viewModelScope.launch {
            UserPrefs.setSpeakerVerifyEnabled(appContext, enabled)
        }
        if (enabled && speakerProfiles.isEmpty()) {
            uiState = uiState.copy(status = "说话人验证已开启，请先采集本人语音样本")
        }
    }

    fun setSpeakerVerifyThreshold(threshold: Float) {
        val clamped = threshold.coerceIn(0.05f, 0.95f)
        uiState = uiState.copy(speakerVerifyThreshold = clamped)
        realtimeHost?.setSpeakerVerifyThreshold(clamped)
        viewModelScope.launch {
            UserPrefs.setSpeakerVerifyThreshold(appContext, clamped)
        }
    }

    fun clearSpeakerProfile() {
        speakerProfiles.clear()
        uiState = uiState.copy(
            speakerVerifyEnabled = false,
            speakerProfileReady = false,
            speakerProfiles = emptyList(),
            speakerLastSimilarity = -1f,
            status = "已清除本人语音样本"
        )
        realtimeHost?.let { host ->
            host.setSpeakerVerifyEnabled(false)
            host.clearSpeakerProfiles()
        }
        viewModelScope.launch {
            UserPrefs.setSpeakerVerifyEnabled(appContext, false)
            UserPrefs.setSpeakerVerifyProfiles(appContext, emptyList())
        }
    }

    fun removeSpeakerProfileAt(index: Int) {
        if (index !in speakerProfiles.indices) return
        speakerProfiles = speakerProfiles.toMutableList().apply { removeAt(index) }
        val hasProfiles = speakerProfiles.isNotEmpty()
        val keepVerify = uiState.speakerVerifyEnabled && hasProfiles
        uiState = uiState.copy(
            speakerVerifyEnabled = keepVerify,
            speakerProfileReady = hasProfiles,
            speakerProfiles = speakerProfileUiItems(),
            speakerLastSimilarity = if (hasProfiles) uiState.speakerLastSimilarity else -1f,
            status = if (hasProfiles) "已移除注册样本" else "已清除本人语音样本"
        )
        realtimeHost?.let { host ->
            host.setSpeakerVerifyEnabled(keepVerify)
            host.setSpeakerProfiles(speakerProfileVectors())
        }
        viewModelScope.launch {
            UserPrefs.setSpeakerVerifyEnabled(appContext, keepVerify)
            UserPrefs.setSpeakerVerifyProfiles(appContext, speakerProfiles)
        }
    }

    fun applySpeakerProfile(profile: FloatArray): Boolean {
        return applySpeakerProfiles(listOf(profile))
    }

    fun applySpeakerProfiles(profiles: List<FloatArray>): Boolean {
        val normalizedProfiles = profiles
            .mapNotNull { profile -> if (profile.isEmpty()) null else profile.copyOf() }
            .take(MAX_SPEAKER_PROFILES)
        if (normalizedProfiles.isEmpty()) {
            return false
        }
        val baseId = SystemClock.elapsedRealtime()
        speakerProfiles = normalizedProfiles.mapIndexed { index, profile ->
            UserPrefs.SpeakerVerifyProfile(
                id = "spk-$baseId-${index + 1}",
                name = "样本 ${index + 1}",
                vector = profile
            )
        }.toMutableList()
        realtimeHost?.setSpeakerProfiles(speakerProfileVectors())
        uiState = uiState.copy(
            speakerProfileReady = true,
            speakerProfiles = speakerProfileUiItems(),
            speakerLastSimilarity = -1f,
            status = "本人语音样本已保存（${speakerProfiles.size}/$MAX_SPEAKER_PROFILES）"
        )
        viewModelScope.launch {
            UserPrefs.setSpeakerVerifyProfiles(appContext, speakerProfiles)
        }
        return true
    }

    suspend fun enrollSpeakerProfileNow(
        durationSec: Float = 4f,
        onCapture: ((progress: Float, level: Float) -> Unit)? = null,
        persist: Boolean = true
    ): SpeakerEnrollResult {
        if (uiState.running) {
            val msg = "请先停止麦克风再采集本人样本"
            uiState = uiState.copy(status = msg)
            return SpeakerEnrollResult(success = false, message = msg)
        }
        uiState = uiState.copy(status = "说话人注册中（请持续说话约${durationSec.toInt()}秒）...")
        val host = requestRealtimeHost("音频宿主初始化中，请稍后重试")
            ?: return SpeakerEnrollResult(
                success = false,
                message = "音频宿主初始化中，请稍后重试"
            )
        val result = host.enrollSpeaker(durationSec, onCapture)
        if (result.success && result.profile != null) {
            if (persist) {
                val applied = applySpeakerProfile(result.profile)
                if (applied) {
                    uiState = uiState.copy(status = result.message)
                }
            }
        } else {
            uiState = uiState.copy(status = result.message)
        }
        return result
    }

    fun enrollSpeakerProfile() {
        viewModelScope.launch {
            enrollSpeakerProfileNow(4f)
        }
    }

    fun setLandscapeDrawerMode(mode: Int) {
        val clamped = mode.coerceIn(UserPrefs.DRAWER_MODE_HIDDEN, UserPrefs.DRAWER_MODE_PERMANENT)
        uiState = uiState.copy(landscapeDrawerMode = clamped)
        viewModelScope.launch {
            UserPrefs.setLandscapeDrawerMode(appContext, clamped)
        }
    }

    fun setSolidTopBar(enabled: Boolean) {
        uiState = uiState.copy(solidTopBar = enabled)
        viewModelScope.launch {
            UserPrefs.setSolidTopBar(appContext, enabled)
        }
    }

    fun setThemeMode(mode: Int) {
        val normalized = UserPrefs.normalizeThemeMode(mode)
        uiState = uiState.copy(themeMode = normalized)
        viewModelScope.launch {
            UserPrefs.setThemeMode(appContext, normalized)
        }
    }

    fun setOverlayThemeMode(mode: Int) {
        val normalized = UserPrefs.normalizeThemeMode(mode)
        uiState = uiState.copy(overlayThemeMode = normalized)
        viewModelScope.launch {
            UserPrefs.setOverlayThemeMode(appContext, normalized)
        }
    }

    fun setFontScaleBlockMode(mode: Int) {
        val normalized = UserPrefs.normalizeFontScaleBlockMode(mode)
        FontScaleBlockRuntime.mode = normalized
        uiState = uiState.copy(fontScaleBlockMode = normalized)
        viewModelScope.launch {
            UserPrefs.setFontScaleBlockMode(appContext, normalized)
        }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        uiState = uiState.copy(hapticFeedbackEnabled = enabled)
        viewModelScope.launch {
            UserPrefs.setHapticFeedbackEnabled(appContext, enabled)
        }
    }

    fun completeOnboarding() {
        uiState = uiState.copy(onboardingCompleted = true)
        viewModelScope.launch {
            UserPrefs.setOnboardingCompleted(appContext, true)
        }
    }

    fun setForceFullWidthTabsOnPhone(enabled: Boolean) {
        uiState = uiState.copy(forceFullWidthTabsOnPhone = enabled)
        viewModelScope.launch {
            UserPrefs.setForceFullWidthTabsOnPhone(appContext, enabled)
        }
    }

    fun setSoundboardGridFullWidth(enabled: Boolean) {
        uiState = uiState.copy(soundboardGridFullWidth = enabled)
        viewModelScope.launch {
            UserPrefs.setSoundboardGridFullWidth(appContext, enabled)
        }
    }

    fun setInternalWebViewEnabled(enabled: Boolean) {
        uiState = uiState.copy(internalWebViewEnabled = enabled)
        viewModelScope.launch {
            UserPrefs.setInternalWebViewEnabled(appContext, enabled)
        }
    }

    fun setDrawingSaveRelativePath(path: String) {
        val normalized = normalizeDrawingSaveRelativePath(path)
        uiState = uiState.copy(
            drawingSaveRelativePath = normalized,
            status = "画板保存路径：$normalized"
        )
        viewModelScope.launch {
            UserPrefs.setDrawingSaveRelativePath(appContext, normalized)
        }
    }

    fun setQuickCardAutoSaveOnExit(enabled: Boolean) {
        uiState = uiState.copy(quickCardAutoSaveOnExit = enabled)
        viewModelScope.launch {
            UserPrefs.setQuickCardAutoSaveOnExit(appContext, enabled)
        }
    }

    fun setUseBuiltinFileManager(enabled: Boolean) {
        uiState = uiState.copy(useBuiltinFileManager = enabled)
        viewModelScope.launch {
            UserPrefs.setUseBuiltinFileManager(appContext, enabled)
        }
    }

    fun setUseBuiltinGallery(enabled: Boolean) {
        uiState = uiState.copy(useBuiltinGallery = enabled)
        viewModelScope.launch {
            UserPrefs.setUseBuiltinGallery(appContext, enabled)
        }
    }

    fun setDrawingSavePathFromTreeUri(uri: android.net.Uri) {
        val resolved = drawingRelativePathFromTreeUri(uri)
        if (resolved == null) {
            uiState = uiState.copy(status = "不支持该目录，请选择内部存储目录")
            return
        }
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        runCatching {
            appContext.contentResolver.takePersistableUriPermission(uri, flags)
        }
        setDrawingSaveRelativePath(resolved)
    }

    fun updateDrawColor(color: Color) {
        drawColor = color
        drawEraser = false
    }

    fun updateDrawBrushSize(size: Float) {
        val clamped = size.coerceIn(2f, 48f)
        if (drawEraser) {
            drawEraserSize = clamped
        } else {
            drawBrushSize = clamped
        }
    }

    fun updateDrawEraser(enabled: Boolean) {
        drawEraser = enabled
    }

    fun updateDrawingToolbarCollapsed(collapsed: Boolean) {
        drawingToolbarCollapsed = collapsed
    }

    fun rotateDrawingCanvasQuarterTurns(delta: Int) {
        drawingManualRotationQuarterTurns =
            ((drawingManualRotationQuarterTurns + delta) % 4 + 4) % 4
    }

    fun clearDrawingBoard() {
        drawStrokes.clear()
        drawRedoStrokes.clear()
    }

    fun appendDrawingStroke(points: List<DrawPoint>, eraserOverride: Boolean? = null) {
        if (points.size < 2) return
        val useEraser = eraserOverride ?: drawEraser
        val effectiveWidth = if (useEraser) drawEraserSize * 5f else drawBrushSize
        drawRedoStrokes.clear()
        drawStrokes.add(
            DrawStrokeData(
                points = points,
                color = drawColor,
                width = effectiveWidth,
                eraser = useEraser
            )
        )
    }

    fun undoDrawingStroke() {
        val stroke = drawStrokes.removeLastOrNull() ?: return
        drawRedoStrokes.add(stroke)
    }

    fun redoDrawingStroke() {
        val stroke = drawRedoStrokes.removeLastOrNull() ?: return
        drawStrokes.add(stroke)
    }

    fun saveDrawingSnapshot() {
        val strokes = drawStrokes.toList()
        if (strokes.isEmpty()) {
            uiState = uiState.copy(status = "画板为空，无可保存内容")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                val width = 1080
                val height = 1920
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bitmap)
                canvas.drawColor(android.graphics.Color.WHITE)

                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeCap = Paint.Cap.ROUND
                    strokeJoin = Paint.Join.ROUND
                }

                strokes.forEach { stroke ->
                    paint.color = if (stroke.eraser) android.graphics.Color.WHITE else stroke.color.toArgb()
                    paint.strokeWidth = stroke.width
                    val pts = stroke.points
                    for (i in 1 until pts.size) {
                        val p0 = pts[i - 1]
                        val p1 = pts[i]
                        canvas.drawLine(
                            p0.x * width,
                            p0.y * height,
                            p1.x * width,
                            p1.y * height,
                            paint
                        )
                    }
                }

                val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val fileName = "drawing_$ts.png"
                val relativePath = normalizeDrawingSaveRelativePath(uiState.drawingSaveRelativePath)
                val resolver = appContext.contentResolver
                val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }
                val uri = resolver.insert(collection, values)
                    ?: error("无法创建图片媒体条目")
                try {
                    resolver.openOutputStream(uri)?.use { out ->
                        val ok = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        if (!ok) error("图片编码失败")
                    } ?: error("无法打开图片输出流")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        resolver.update(
                            uri,
                            ContentValues().apply {
                                put(MediaStore.Images.Media.IS_PENDING, 0)
                            },
                            null,
                            null
                        )
                    }
                } catch (e: Exception) {
                    resolver.delete(uri, null, null)
                    throw e
                } finally {
                    bitmap.recycle()
                }

                val fullPath = "/storage/emulated/0/${relativePath.trim('/')}/$fileName"
                runCatching {
                    MediaScannerConnection.scanFile(
                        appContext,
                        arrayOf(fullPath),
                        arrayOf("image/png"),
                        null
                    )
                    appContext.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                }
                DrawingSaveResult(fullPath = fullPath)
            }

            result.onSuccess { saved ->
                AppLogger.i("drawing saved: ${saved.fullPath}")
                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(status = "画板已保存：${saved.fullPath}")
                    Toast.makeText(appContext, "画板已保存：${saved.fullPath}", Toast.LENGTH_LONG).show()
                }
            }.onFailure { e ->
                AppLogger.e("drawing save failed", e)
                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(status = "画板保存失败：${e.message ?: "未知错误"}")
                    Toast.makeText(appContext, "画板保存失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setEchoSuppression(enabled: Boolean) {
        val wasRunning = uiState.running
        uiState = uiState.copy(echoSuppression = enabled)
        realtimeHost?.setUseVoiceCommunication(enabled)
        viewModelScope.launch {
            UserPrefs.setEchoSuppression(appContext, enabled)
        }
        if (wasRunning) {
            restartJob?.cancel()
            restartJob = viewModelScope.launch {
                realtimeHost?.restartRecorder()
            }
        }
    }

    fun setCommunicationMode(enabled: Boolean) {
        val wasRunning = uiState.running
        uiState = uiState.copy(communicationMode = enabled)
        realtimeHost?.setCommunicationMode(enabled)
        viewModelScope.launch {
            UserPrefs.setCommunicationMode(appContext, enabled)
        }
        if (wasRunning) {
            restartJob?.cancel()
            restartJob = viewModelScope.launch {
                realtimeHost?.restartRecorder()
            }
        }
    }

    fun setPreferredOutputType(type: Int) {
        val wasRunning = uiState.running
        uiState = uiState.copy(preferredOutputType = type)
        realtimeHost?.setPreferredOutputType(type)
        viewModelScope.launch {
            UserPrefs.setPreferredOutputType(appContext, type)
        }
        if (wasRunning) {
            restartJob?.cancel()
            restartJob = viewModelScope.launch {
                realtimeHost?.restartRecorder()
            }
        }
    }

    fun setPreferredInputType(type: Int) {
        val wasRunning = uiState.running
        uiState = uiState.copy(preferredInputType = type)
        realtimeHost?.setPreferredInputType(type)
        viewModelScope.launch {
            UserPrefs.setPreferredInputType(appContext, type)
        }
        if (wasRunning) {
            restartJob?.cancel()
            restartJob = viewModelScope.launch {
                realtimeHost?.restartRecorder()
            }
        }
    }

    fun setAec3Enabled(enabled: Boolean) {
        uiState = uiState.copy(
            aec3Enabled = enabled,
            aec3Status = if (enabled) "初始化中" else "未启用"
        )
        realtimeHost?.setUseAec3(enabled)
        viewModelScope.launch {
            UserPrefs.setAec3Enabled(appContext, enabled)
        }
    }

    fun setDenoiserMode(mode: Int) {
        val normalized = mode.coerceIn(AudioDenoiserMode.OFF, AudioDenoiserMode.SPEEX)
        uiState = uiState.copy(denoiserMode = normalized)
        realtimeHost?.setDenoiserMode(normalized)
        viewModelScope.launch {
            UserPrefs.setDenoiserMode(appContext, normalized)
        }
    }

    fun setSpeechEnhancementMode(mode: Int) {
        val normalized = SpeechEnhancementMode.clamp(mode)
        uiState = uiState.copy(speechEnhancementMode = normalized)
        realtimeHost?.setSpeechEnhancementMode(normalized)
        viewModelScope.launch {
            UserPrefs.setSpeechEnhancementMode(appContext, normalized)
        }
    }

    private fun currentAudioTestConfig(): AudioTestConfig {
        return AudioTestConfig(
            audioSource = if (uiState.echoSuppression) {
                MediaRecorder.AudioSource.VOICE_COMMUNICATION
            } else {
                MediaRecorder.AudioSource.MIC
            },
            preferredInputType = uiState.preferredInputType,
            preferredOutputType = uiState.preferredOutputType,
            useCommunicationMode = uiState.communicationMode,
            speechEnhancementMode = uiState.speechEnhancementMode
        )
    }

    fun startAudioTestRecording() {
        if (uiState.running || realtimeHost?.isMicActive() == true || RealtimeOwnerGate.currentOwner() != null) {
            uiState = uiState.copy(audioTestStatus = "请先停止语音转换再测试录音")
            return
        }
        val started = audioTest.startRecording(currentAudioTestConfig())
        if (!started) {
            uiState = uiState.copy(audioTestStatus = "音频测试：无法开始录制")
        }
    }

    fun stopAudioTestRecording() {
        audioTest.stopRecording()
    }

    fun startAudioTestPlayback() {
        val started = audioTest.play(currentAudioTestConfig())
        if (!started) {
            uiState = uiState.copy(audioTestStatus = "音频测试：请先录制一段测试音频")
        }
    }

    fun stopAudioTestPlayback() {
        audioTest.stopPlayback()
    }

    fun clearAudioTest() {
        audioTest.clear()
    }

    fun speakText(
        text: String,
        fromQuickText: Boolean = false,
        interruptCurrent: Boolean = false
    ) {
        val message = text.trim()
        if (message.isEmpty()) return
        if (uiState.ttsDisabled) {
            appendRecognizedHistory(message, fromQuickText = fromQuickText)
            uiState = uiState.copy(status = TTS_DISABLED_MESSAGE)
            return
        }
        if (uiState.voiceDir == null) {
            uiState = uiState.copy(status = "请先选择语音包")
            return
        }
        val interruptSerial = if (fromQuickText && interruptCurrent) {
            quickSubtitleInterruptRequestSerial.incrementAndGet()
        } else {
            null
        }
        viewModelScope.launch {
            if (interruptSerial != null && interruptSerial != quickSubtitleInterruptRequestSerial.get()) {
                return@launch
            }
            val host = requestRealtimeHost("音频宿主初始化中")
            if (interruptSerial != null && interruptSerial != quickSubtitleInterruptRequestSerial.get()) {
                return@launch
            }
            val queuedId = host?.speakText(message, interruptCurrent = interruptCurrent)
            if (interruptSerial != null && interruptSerial != quickSubtitleInterruptRequestSerial.get()) {
                return@launch
            }
            if (queuedId != null) {
                // 便捷字幕的快速文本/输入框触发朗读时，也要进入历史记录。
                // 使用队列ID绑定，避免与 onResult 回调重复插入。
                appendRecognizedHistory(message, queuedId, fromQuickText = fromQuickText)
                uiState = uiState.copy(status = "已加入朗读队列")
            }
        }
    }

    fun start() {
        val voice = uiState.voiceDir
        val requireVoice = !uiState.ttsDisabled
        if (requireVoice && voice == null) {
            uiState = uiState.copy(
                status = "请先选择语音包"
            )
            return
        }
        val asr = uiState.asrDir
        if (asr == null) {
            uiState = uiState.copy(status = "正在加载语音识别资源包")
            viewModelScope.launch {
                val dir = withContext(Dispatchers.IO) { repo.ensureBundledAsr() }
                if (dir == null) {
                    refreshRecognitionResourceStatus()
                    uiState = uiState.copy(status = "请先安装语音识别资源包")
                    return@launch
                }
                uiState = uiState.copy(asrDir = dir, status = "已加载语音识别资源包")
                val host = realtimeHost
                if (host != null) {
                    host.updateSelectedAsrDir(dir, status = "已加载语音识别资源包", preload = true)
                } else {
                    preloadAsr(dir)
                }
                start()
            }
            return
        }
        val host = requestRealtimeHost("音频宿主初始化中")
        if (host != null) {
            restartJob?.cancel()
            restartJob = null
            host.startRealtime()
            return
        }
        pendingHostStartRequest = true
    }

    fun stop() {
        realtimeHost?.let { host ->
            restartJob?.cancel()
            restartJob = null
            pttSessionLastText = ""
            resetPttHistoryDedup()
            pendingHostStartRequest = false
            host.stopRealtime()
            return
        }
        restartJob?.cancel()
        restartJob = null
        pttSessionLastText = ""
        resetPttHistoryDedup()
        pendingHostStartRequest = false
        RealtimeOwnerGate.release(APP_REALTIME_OWNER_TAG)
        KeepAliveService.stop(appContext)
        realtimeInputLevel = 0f
        realtimePlaybackProgress = 0f
        uiState = uiState.copy(
            running = false,
            status = "麦克风已停止",
            pushToTalkPressed = false,
            pushToTalkStreamingText = ""
        )
    }

    override fun onCleared() {
        detachRealtimeHost()
        audioTest.release()
        settingsObserveJob?.cancel()
        settingsObserveJob = null
        RealtimeOwnerGate.release(APP_REALTIME_OWNER_TAG)
        super.onCleared()
    }

    private fun applySettingsToController(settings: UserPrefs.AppSettings) {
        realtimeHost?.let { host ->
            host.setSuppressWhilePlaying(settings.muteWhilePlaying)
            host.setSuppressDelaySec(settings.muteWhilePlayingDelaySec)
            host.setMinVolumePercent(settings.minVolumePercent)
            host.setPlaybackGainPercent(settings.playbackGainPercent)
            host.setAudioFocusAvoidanceMode(settings.audioFocusAvoidanceMode)
            host.setPiperNoiseScale(settings.piperNoiseScale)
            host.setPiperLengthScale(settings.piperLengthScale)
            host.setPiperNoiseW(0.8f)
            host.setPiperSentenceSilenceSec(settings.piperSentenceSilence)
            host.setUseAec3(settings.aec3Enabled)
            host.setUseVoiceCommunication(settings.echoSuppression)
            host.setCommunicationMode(settings.communicationMode)
            host.setPreferredInputType(settings.preferredInputType)
            host.setPreferredOutputType(settings.preferredOutputType)
            host.setDenoiserMode(settings.denoiserMode)
            host.setSpeechEnhancementMode(settings.speechEnhancementMode)
            host.setClassicVadEnabled(settings.classicVadEnabled)
            host.setSileroVadEnabled(settings.sileroVadEnabled)
            host.setSileroVadThreshold(settings.sileroVadThreshold)
            host.setSileroVadPreRollMs(settings.sileroVadPreRollMs)
            host.setNumberReplaceMode(settings.numberReplaceMode)
            host.setSpeakerVerifyEnabled(uiState.speakerVerifyEnabled)
            host.setSpeakerVerifyThreshold(settings.speakerVerifyThreshold)
            host.setSpeakerProfiles(speakerProfileVectors())
            host.setSuppressAsrAutoSpeak(
                uiState.pushToTalkMode && uiState.pushToTalkConfirmInputMode
            )
            host.setPushToTalkStreamingEnabled(
                uiState.pushToTalkMode &&
                        uiState.pushToTalkConfirmInputMode &&
                        uiState.pushToTalkPressed
            )
            return
        }
    }
}

