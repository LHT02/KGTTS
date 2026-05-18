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



class MainActivity : ComponentActivity() {
    private var lastDecorFitsSystemWindows: Boolean = false
    private var pendingBackgroundReturnFix: Boolean = false
    private var delayedResumeFixRunnable: Runnable? = null
    private var lastHandledExternalVoicePackIntentKey: String? = null
    private var lastHandledExternalPresetIntentKey: String? = null
    private var realtimeHostBound = false
    private val realtimeHostConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? RealtimeHostService.LocalBinder ?: return
            realtimeHostBound = true
            viewModel.attachRealtimeHost(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            realtimeHostBound = false
            viewModel.detachRealtimeHost()
        }
    }

    private val viewModel: MainViewModel by viewModels {
        val repo = ModelRepository(this@MainActivity)
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repo, this@MainActivity) as T
            }
        }
    }

    override fun getResources(): Resources {
        val base = super.getResources()
        if (FontScaleBlockRuntime.mode != UserPrefs.FONT_SCALE_BLOCK_ALL) return base
        val config = Configuration(base.configuration)
        if (config.fontScale == 1f) return base
        config.fontScale = 1f
        return createConfigurationContext(config).resources
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.parseColor("#038387")),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        applyWindowInsetPolicyForMode()
        AppLogger.init(this)
        AppLogger.i("MainActivity.onCreate")
        RealtimeHostService.ensureStarted(this)
        bindService(
            Intent(this, RealtimeHostService::class.java),
            realtimeHostConnection,
            Context.BIND_AUTO_CREATE
        )
        viewModel.loadSettings()
        viewModel.ensureInitialFloatingOverlayShortcuts()
        lifecycleScope.launch(Dispatchers.Default) {
            LauncherMenuShortcuts.syncAppShortcuts(applicationContext)
        }
        setContent {
            val state = viewModel.uiState
            KigttsFontScaleProvider(state.fontScaleBlockMode) {
                val systemDark = isSystemInDarkTheme()
                val dark = UserPrefs.resolveThemeMode(state.themeMode, systemDark)
                val colors = if (dark) KgtDarkColors else KgtLightColors
                val extraColors = if (dark) KgtDarkExtraColors else KgtLightExtraColors
                val textToolbarState = remember { KigttsTextToolbarState() }
                val textToolbar = remember(textToolbarState) { KigttsTextToolbar(textToolbarState) }
                CompositionLocalProvider(
                    LocalMd2ExtraColors provides extraColors,
                    LocalKigttsHapticFeedbackEnabled provides state.hapticFeedbackEnabled
                ) {
                    CompositionLocalProvider(LocalTextToolbar provides textToolbar) {
                        MaterialTheme(colors = colors, typography = KgtTypography, shapes = Md2Shapes) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                Box(Modifier.fillMaxSize()) {
                                    AppScaffold(viewModel)
                                    KigttsTextToolbarPopup(
                                        state = textToolbarState,
                                        darkTheme = dark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        handleLaunchIntent(intent)
    }

    override fun onDestroy() {
        if (realtimeHostBound) {
            unbindService(realtimeHostConnection)
            realtimeHostBound = false
        }
        viewModel.detachRealtimeHost()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLaunchIntent(intent)
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        applyWindowInsetPolicyForMode()
        AppLogger.i(
            "MainActivity.onMultiWindowModeChanged inMultiWindow=$isInMultiWindowMode " +
                    "decorFits=$lastDecorFitsSystemWindows softInput=${softInputModeSummary(window.attributes.softInputMode)}"
        )
    }

    override fun onResume() {
        super.onResume()
        if (pendingBackgroundReturnFix) {
            pendingBackgroundReturnFix = false
            delayedResumeFixRunnable?.let { window.decorView.removeCallbacks(it) }
            delayedResumeFixRunnable = Runnable {
                val stateMask =
                    window.attributes.softInputMode and WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE
                window.setSoftInputMode(stateMask or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                WindowCompat.setDecorFitsSystemWindows(window, false)
                lastDecorFitsSystemWindows = false
                AppLogger.i(
                    "MainActivity.delayedResumeFix applied delayMs=500 " +
                            "decorFits=$lastDecorFitsSystemWindows softInput=${softInputModeSummary(window.attributes.softInputMode)}"
                )
            }
            window.decorView.postDelayed(delayedResumeFixRunnable, 500L)
            AppLogger.i("MainActivity.delayedResumeFix scheduled delayMs=500")
        }
        AppLogger.i(
            "MainActivity.onResume inMultiWindow=${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) isInMultiWindowMode else false} " +
                    "decorFits=$lastDecorFitsSystemWindows softInput=${softInputModeSummary(window.attributes.softInputMode)}"
        )
        syncFloatingOverlayState()
    }

    override fun onPause() {
        delayedResumeFixRunnable?.let { window.decorView.removeCallbacks(it) }
        delayedResumeFixRunnable = null
        AppLogger.i(
            "MainActivity.onPause inMultiWindow=${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) isInMultiWindowMode else false} " +
                    "decorFits=$lastDecorFitsSystemWindows softInput=${softInputModeSummary(window.attributes.softInputMode)}"
        )
        super.onPause()
    }

    override fun onStop() {
        pendingBackgroundReturnFix = true
        AppLogger.i("MainActivity.onStop markPendingBackgroundReturnFix=true")
        super.onStop()
    }

    private fun applyWindowInsetPolicyForMode() {
        val inMultiWindow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isInMultiWindowMode
        } else {
            false
        }
        // Multi-window/floating mode: let the framework fit system windows to avoid title-bar overlap.
        // Fullscreen mode: keep edge-to-edge behavior.
        WindowCompat.setDecorFitsSystemWindows(window, inMultiWindow)
        lastDecorFitsSystemWindows = inMultiWindow
        AppLogger.i(
            "applyWindowInsetPolicyForMode inMultiWindow=$inMultiWindow " +
                    "decorFits=$lastDecorFitsSystemWindows softInput=${softInputModeSummary(window.attributes.softInputMode)}"
        )
    }

    private fun handleLaunchIntent(intent: Intent?) {
        when (intent?.action) {
            OverlayBridge.ACTION_OPEN_QUICK_SUBTITLE -> {
                val requestId = intent.getLongExtra(OverlayBridge.EXTRA_REQUEST_ID, Long.MIN_VALUE)
                if (requestId == Long.MIN_VALUE) return
                val target = intent.getStringExtra(OverlayBridge.EXTRA_TARGET) ?: OverlayBridge.TARGET_SUBTITLE
                val text = intent.getStringExtra(OverlayBridge.EXTRA_TEXT).orEmpty()
                val navigateToPage = intent.getBooleanExtra(OverlayBridge.EXTRA_NAVIGATE_TO_PAGE, true)
                val soundboardGroupId =
                    intent.takeIf { it.hasExtra(OverlayBridge.EXTRA_SOUNDBOARD_GROUP_ID) }
                        ?.getLongExtra(OverlayBridge.EXTRA_SOUNDBOARD_GROUP_ID, Long.MIN_VALUE)
                        ?.takeIf { it != Long.MIN_VALUE }
                viewModel.handleQuickSubtitleLaunchRequest(
                    requestId = requestId,
                    target = target,
                    text = text,
                    navigateToPage = navigateToPage,
                    soundboardGroupId = soundboardGroupId
                )
                setIntent(Intent())
            }

            OverlayBridge.ACTION_REQUEST_RECORD_AUDIO_PERMISSION -> {
                val startRealtimeOnGrant =
                    intent.getBooleanExtra(OverlayBridge.EXTRA_START_REALTIME_ON_GRANT, false)
                viewModel.requestRecordAudioPermission(startRealtimeOnGrant)
                setIntent(Intent())
            }

            OverlayBridge.ACTION_SHOW_ACCESSIBILITY_GUIDE -> {
                viewModel.requestAccessibilityExplainDialog()
                setIntent(Intent())
            }

            Intent.ACTION_VIEW,
            Intent.ACTION_SEND -> {
                val uri = when (intent.action) {
                    Intent.ACTION_VIEW -> intent.data
                    Intent.ACTION_SEND -> @Suppress("DEPRECATION") (intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri)
                    else -> null
                } ?: return
                val presetTarget = presetInstallTargetForIntent(intent, uri)
                if (presetTarget != null) {
                    val key = "${intent.action}|$uri|${resolveExternalFileName(uri).orEmpty()}|$presetTarget"
                    if (lastHandledExternalPresetIntentKey == key) return
                    lastHandledExternalPresetIntentKey = key
                    when (presetTarget) {
                        PresetInstallTarget.QuickSubtitle ->
                            viewModel.importQuickSubtitlePresetPackage(uri, openEditorOnSuccess = true)
                        PresetInstallTarget.Soundboard ->
                            viewModel.importSoundboardPresetPackage(uri, openEditorOnSuccess = true)
                    }
                    setIntent(Intent())
                    return
                }
                if (!shouldHandleVoicePackIntent(intent, uri)) return
                val key = "${intent.action}|$uri|${resolveExternalFileName(uri).orEmpty()}"
                if (lastHandledExternalVoicePackIntentKey == key) return
                lastHandledExternalVoicePackIntentKey = key
                viewModel.importVoice(uri, openVoicePackPageOnSuccess = true)
                setIntent(Intent())
            }
        }
    }

    private fun presetInstallTargetForIntent(intent: Intent, uri: Uri): PresetInstallTarget? {
        val name = resolveExternalFileName(uri)?.lowercase().orEmpty()
        val mime = intent.type?.lowercase().orEmpty()
        return when {
            name.endsWith(".kigtpk") || mime == "application/x-kigtts-quicktext-preset" ->
                PresetInstallTarget.QuickSubtitle
            name.endsWith(".kigspk") || mime == "application/x-kigtts-soundboard-preset" ->
                PresetInstallTarget.Soundboard
            else -> null
        }
    }

    private fun shouldHandleVoicePackIntent(intent: Intent, uri: Uri): Boolean {
        val name = resolveExternalFileName(uri)?.lowercase().orEmpty()
        val mime = intent.type?.lowercase().orEmpty()
        return when (intent.action) {
            Intent.ACTION_VIEW ->
                name.endsWith(".kigvpk") || mime == "application/x-kigtts-voicepack"
            Intent.ACTION_SEND ->
                name.endsWith(".kigvpk") || name.endsWith(".zip") || mime == "application/x-kigtts-voicepack"
            else -> false
        }
    }

    private fun resolveExternalFileName(uri: Uri): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (index >= 0) {
                            return cursor.getString(index)
                        }
                    }
                }
        }
        return uri.lastPathSegment?.substringAfterLast('/')
    }

    private fun syncFloatingOverlayState() {
        val enabled = viewModel.uiState.floatingOverlayEnabled
        if (!enabled) {
            FloatingOverlayService.stop(this)
            return
        }
        if (!FloatingOverlayService.canDrawOverlays(this)) {
            viewModel.setFloatingOverlayEnabled(false)
            FloatingOverlayService.stop(this)
            return
        }
        FloatingOverlayService.start(this)
    }
}


