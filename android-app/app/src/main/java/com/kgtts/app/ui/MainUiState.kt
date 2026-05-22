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


data class UiState(
    val settingsLoaded: Boolean = false,
    val asrDir: File? = null,
    val voiceDir: File? = null,
    val voicePacks: List<VoicePackInfo> = emptyList(),
    val recognized: List<RecognizedItem> = emptyList(),
    val running: Boolean = false,
    val status: String = "待命",
    val muteWhilePlaying: Boolean = true,
    val muteWhilePlayingDelaySec: Float = 0.2f,
    val echoSuppression: Boolean = false,
    val communicationMode: Boolean = false,
    val preferredInputType: Int = AudioRoutePreference.INPUT_AUTO,
    val preferredOutputType: Int = AudioRoutePreference.OUTPUT_AUTO,
    val aec3Enabled: Boolean = true,
    val denoiserMode: Int = AudioDenoiserMode.RNNOISE,
    val speechEnhancementMode: Int = SpeechEnhancementMode.DPDFNET4_STREAMING,
    val aec3Status: String = "未启用",
    val aec3Diag: String = "AEC3 诊断：未启用",
    val classicVadEnabled: Boolean = false,
    val sileroVadEnabled: Boolean = true,
    val sileroVadThreshold: Float = UserPrefs.SILERO_VAD_DEFAULT_THRESHOLD,
    val sileroVadPreRollMs: Int = UserPrefs.SILERO_VAD_DEFAULT_PRE_ROLL_MS,
    val recognitionResourceInstalled: Boolean = false,
    val recognitionResourceName: String = "未安装",
    val recognitionResourceVersion: String = "",
    val recognitionResourceStatus: String = "未安装语音识别资源包",
    val recognitionResourceBusy: Boolean = false,
    val recognitionResourceProgressStage: String = "",
    val recognitionResourceProgress: Float = -1f,
    val recognitionResourceModelScopeUrl: String = UserPrefs.DEFAULT_RECOGNITION_RESOURCE_MODELSCOPE_URL,
    val recognitionResourceHuggingFaceUrl: String = UserPrefs.DEFAULT_RECOGNITION_RESOURCE_HUGGINGFACE_URL,
    val recognitionResourcePreferredSource: Int = UserPrefs.RECOGNITION_RESOURCE_SOURCE_MODELSCOPE,
    val kokoroInstalled: Boolean = false,
    val kokoroStatus: String = "未安装 Kokoro 语音包",
    val kokoroBusy: Boolean = false,
    val kokoroProgressStage: String = "",
    val kokoroProgress: Float = -1f,
    val kokoroHfUrl: String = UserPrefs.DEFAULT_KOKORO_HF_URL,
    val kokoroHfMirrorUrl: String = UserPrefs.DEFAULT_KOKORO_HFMIRROR_URL,
    val kokoroModelScopeUrl: String = UserPrefs.DEFAULT_KOKORO_MODELSCOPE_URL,
    val kokoroPreferredSource: Int = UserPrefs.KOKORO_SOURCE_MODELSCOPE,
    val kokoroSpeakerId: Int = UserPrefs.KOKORO_DEFAULT_SPEAKER_ID,
    val minVolumePercent: Int = 2,
    val playbackGainPercent: Int = 100,
    val audioFocusAvoidanceMode: Int = UserPrefs.AUDIO_FOCUS_AVOID_NONE,
    val piperNoiseScale: Float = 0.667f,
    val piperLengthScale: Float = 1.0f,
    val piperNoiseW: Float = 0.8f,
    val piperSentenceSilence: Float = 0.2f,
    val keepAlive: Boolean = true,
    val numberReplaceMode: Int = 0,
    val landscapeDrawerMode: Int = UserPrefs.DRAWER_MODE_PERMANENT,
    val solidTopBar: Boolean = true,
    val themeMode: Int = UserPrefs.THEME_MODE_FOLLOW_SYSTEM,
    val overlayThemeMode: Int = UserPrefs.THEME_MODE_FOLLOW_SYSTEM,
    val fontScaleBlockMode: Int = UserPrefs.FONT_SCALE_BLOCK_ICONS_ONLY,
    val hapticFeedbackEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val forceFullWidthTabsOnPhone: Boolean = false,
    val soundboardGridFullWidth: Boolean = false,
    val internalWebViewEnabled: Boolean = false,
    val drawingSaveRelativePath: String = UserPrefs.DEFAULT_DRAWING_SAVE_RELATIVE_PATH,
    val quickCardAutoSaveOnExit: Boolean = false,
    val useBuiltinFileManager: Boolean = true,
    val useBuiltinGallery: Boolean = true,
    val asrSendToQuickSubtitle: Boolean = true,
    val pushToTalkMode: Boolean = false,
    val pushToTalkConfirmInputMode: Boolean = false,
    val floatingOverlayEnabled: Boolean = false,
    val floatingOverlayAutoDock: Boolean = true,
    val floatingOverlayShowOnLockScreen: Boolean = false,
    val floatingOverlayHardcodedShortcutSupplement: Boolean = false,
    val volumeHotkeyUpDownEnabled: Boolean = false,
    val volumeHotkeyDownUpEnabled: Boolean = false,
    val volumeHotkeyWindowMs: Int = UserPrefs.VOLUME_HOTKEY_DEFAULT_WINDOW_MS,
    val volumeHotkeyAccessibilityEnabled: Boolean = false,
    val volumeHotkeyEnableWarningDismissed: Boolean = false,
    val volumeHotkeyUpDownAction: VolumeHotkeyActionSpec =
        VolumeHotkeyActions.defaultFor(VolumeHotkeySequence.UpDown),
    val volumeHotkeyDownUpAction: VolumeHotkeyActionSpec =
        VolumeHotkeyActions.defaultFor(VolumeHotkeySequence.DownUp),
    val ttsDisabled: Boolean = false,
    val soundboardKeywordTriggerEnabled: Boolean = false,
    val soundboardSuppressTtsOnKeyword: Boolean = false,
    val allowQuickTextTriggerSoundboard: Boolean = false,
    val quickSubtitleInterruptQueue: Boolean = true,
    val quickSubtitleAutoFit: Boolean = true,
    val quickSubtitleAllowLargeFont: Boolean = false,
    val quickSubtitleCompactControls: Boolean = false,
    val quickSubtitleKeepInputPreview: Boolean = true,
    val bluetoothMediaTitleSubtitle: Boolean = false,
    val liveSubtitleNotificationEnabled: Boolean = false,
    val drawingKeepCanvasOrientationToDevice: Boolean = true,
    val pushToTalkPressed: Boolean = false,
    val pushToTalkStreamingText: String = "",
    val speakerVerifyEnabled: Boolean = false,
    val speakerVerifyThreshold: Float = 0.5f,
    val speakerProfileReady: Boolean = false,
    val speakerProfiles: List<SpeakerProfileUiItem> = emptyList(),
    val speakerLastSimilarity: Float = -1f,
    val inputLevel: Float = 0f,
    val audioTestRecording: Boolean = false,
    val audioTestPlaying: Boolean = false,
    val audioTestHasClip: Boolean = false,
    val audioTestLevel: Float = 0f,
    val audioTestStatus: String = "未录制测试音频",
    val inputDeviceLabel: String = "未知",
    val outputDeviceLabel: String = "未知"
)

data class SpeakerProfileUiItem(
    val id: String,
    val name: String
)

data class RecognizedItem(
    val id: Long,
    val text: String,
    val progress: Float = 0f
)

data class ExternalQuickSubtitleRequest(
    val requestId: Long,
    val target: String,
    val text: String,
    val navigateToPage: Boolean = true,
    val soundboardGroupId: Long? = null
)

data class ExternalRecordAudioPermissionRequest(
    val requestId: Long,
    val startRealtimeOnGrant: Boolean = false
)

data class ExternalAccessibilityExplainRequest(
    val requestId: Long
)

data class ExternalVoicePackInstallRequest(
    val requestId: Long,
    val message: String
)

data class ExternalPresetInstallRequest(
    val requestId: Long,
    val target: PresetInstallTarget,
    val message: String
)

enum class PresetInstallTarget {
    QuickSubtitle,
    Soundboard
}

internal fun isOverlayOpenTarget(target: String): Boolean {
    return target == OverlayBridge.TARGET_OPEN ||
            target == OverlayBridge.TARGET_OPEN_OVERLAY ||
            target == OverlayBridge.TARGET_OPEN_QUICK_CARD ||
            target == OverlayBridge.TARGET_OPEN_DRAWING ||
            target == OverlayBridge.TARGET_OPEN_SOUNDBOARD ||
            target == OverlayBridge.TARGET_OPEN_VOICE_PACK ||
            target == OverlayBridge.TARGET_OPEN_SETTINGS ||
            target == OverlayBridge.TARGET_OPEN_QR_SCANNER
}

enum class PttConfirmReleaseAction {
    SendToSubtitle,
    SendToInput,
    Cancel
}

enum class PttConfirmDragTarget {
    DefaultSend,
    ToInput,
    Cancel
}

data class QuickSubtitleGroup(
    val id: Long,
    val title: String,
    val icon: String,
    val items: List<String>
)

enum class QuickCardType(val wireValue: String) {
    Image("image"),
    Qr("qr"),
    Text("text");

    companion object {
        fun fromWire(raw: String?): QuickCardType {
            return entries.firstOrNull { it.wireValue == raw } ?: Text
        }
    }
}

data class QuickCard(
    val id: Long,
    val type: QuickCardType,
    val title: String,
    val note: String = "",
    val themeColor: String = "#038387",
    val link: String = "",
    val portraitImagePath: String = "",
    val landscapeImagePath: String = ""
)

data class QuickCardDraft(
    val editId: Long? = null,
    val isNew: Boolean = false,
    val type: QuickCardType = QuickCardType.Text,
    val title: String = "",
    val note: String = "",
    val themeColor: String = "#038387",
    val link: String = "",
    val portraitImagePath: String = "",
    val landscapeImagePath: String = ""
)

data class DrawPoint(
    val x: Float,
    val y: Float
)

data class DrawStrokeData(
    val points: List<DrawPoint>,
    val color: Color,
    val width: Float,
    val eraser: Boolean
)

data class DrawingSaveResult(
    val fullPath: String
)

internal fun defaultQuickSubtitleGroups(): List<QuickSubtitleGroup> = listOf(
    QuickSubtitleGroup(
        id = 1L,
        title = "常用",
        icon = "sentiment_satisfied",
        items = listOf(
            "您好，我现在不太方便说话",
            "您好，可以加个好友吗",
            "稍等一下，我马上回复您",
            "感谢理解，辛苦了"
        )
    ),
    QuickSubtitleGroup(
        id = 2L,
        title = "游戏",
        icon = "sports_esports",
        items = listOf(
            "我在组队，语音不方便",
            "请跟我走这边",
            "注意右侧有人",
            "这把打得很好"
        )
    ),
    QuickSubtitleGroup(
        id = 3L,
        title = "办公",
        icon = "work",
        items = listOf(
            "我在开会，稍后回复",
            "请把需求再发我一份",
            "这个我今天内处理",
            "收到，谢谢"
        )
    )
)

internal val QuickSubtitleGroupIconChoices = listOf(
    "sentiment_satisfied",
    "sentiment_very_satisfied",
    "sentiment_neutral",
    "sentiment_dissatisfied",
    "record_voice_over",
    "chat",
    "forum",
    "sms",
    "alternate_email",
    "emoji_people",
    "person",
    "groups",
    "accessibility_new",
    "support_agent",
    "translate",
    "work",
    "school",
    "home",
    "restaurant",
    "shopping_bag",
    "local_hospital",
    "directions_car",
    "train",
    "flight",
    "location_on",
    "schedule",
    "event",
    "payments",
    "sports_esports",
    "favorite",
    "thumb_up",
    "handshake",
    "celebration",
    "pets",
    "info",
    "warning"
)

internal val SoundboardGroupIconChoices = listOf(
    "music_note",
    "library_music",
    "queue_music",
    "album",
    "graphic_eq",
    "equalizer",
    "volume_up",
    "campaign",
    "mic",
    "record_voice_over",
    "radio",
    "piano",
    "notifications",
    "alarm",
    "celebration",
    "movie",
    "theaters",
    "sports_esports",
    "sports_soccer",
    "directions_run",
    "emoji_events",
    "bolt",
    "rocket_launch",
    "mood",
    "favorite",
    "chat",
    "work",
    "school",
    "restaurant",
    "pets"
)


