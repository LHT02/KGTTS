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


@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun AppScaffold(viewModel: MainViewModel) {
    val pageQuickSubtitle = 0
    val pageOverlay = 1
    val pageQuickCard = 2
    val pageVoicePack = 3
    val pageDrawing = 4
    val pageSoundboard = 5
    val pageSettings = 6

    var page by rememberSaveable { mutableStateOf(pageQuickSubtitle) }
    var drawingFullscreen by rememberSaveable { mutableStateOf(false) }
    var quickSubtitleFullscreen by rememberSaveable { mutableStateOf(false) }
    var runningStripCollapsed by rememberSaveable { mutableStateOf(true) }
    var logTopBarActions by remember { mutableStateOf<LogTopBarActions?>(null) }
    var quickCardTopBarActions by remember { mutableStateOf<QuickCardTopBarActions?>(null) }
    var quickSubtitleEditorBatchTopBarActions by remember { mutableStateOf<EditorBatchTopBarActions?>(null) }
    var soundboardEditorBatchTopBarActions by remember { mutableStateOf<EditorBatchTopBarActions?>(null) }
    var quickSubtitlePresetExportDialog by remember { mutableStateOf(false) }
    var soundboardPresetExportDialog by remember { mutableStateOf(false) }
    var showBuiltinQuickSubtitlePresetPicker by remember { mutableStateOf(false) }
    var showBuiltinSoundboardPresetPicker by remember { mutableStateOf(false) }
    var showBuiltinRecognitionResourcePicker by remember { mutableStateOf(false) }
    var showBuiltinKokoroVoicePicker by remember { mutableStateOf(false) }
    var recognitionResourceSourceDialog by remember { mutableStateOf(false) }
    var kokoroSourceDialog by remember { mutableStateOf(false) }
    var kokoroVoiceSettingsDialog by remember { mutableStateOf(false) }
    var recognitionResourceRequiredDialog by remember { mutableStateOf(false) }
    var startRealtimeAfterRecognitionResourceInstall by remember { mutableStateOf(false) }
    var quickCardWebMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var quickCardNavReady by remember { mutableStateOf(false) }
    var quickSubtitleFloatingInputPreview by remember {
        mutableStateOf<QuickSubtitleFloatingInputPreviewState?>(null)
    }
    var pendingQuickCardOverlayTarget by rememberSaveable { mutableStateOf<String?>(null) }
    val quickSubtitleNavController = rememberNavController()
    val soundboardNavController = rememberNavController()
    val quickCardNavController = rememberNavController()
    val settingsNavController = rememberNavController()
    val quickSubtitleBackStackEntry by quickSubtitleNavController.currentBackStackEntryAsState()
    val quickSubtitleRoute = quickSubtitleBackStackEntry?.destination?.route ?: QuickSubtitleRoutes.Main
    val soundboardBackStackEntry by soundboardNavController.currentBackStackEntryAsState()
    val soundboardRoute = soundboardBackStackEntry?.destination?.route ?: SoundboardRoutes.Main
    val quickCardBackStackEntry by quickCardNavController.currentBackStackEntryAsState()
    val quickCardRoute = quickCardBackStackEntry?.destination?.route ?: QuickCardRoutes.Main
    val quickCardWebUrl = remember(quickCardBackStackEntry) {
        Uri.decode(quickCardBackStackEntry?.arguments?.getString("url").orEmpty())
    }
    val settingsBackStackEntry by settingsNavController.currentBackStackEntryAsState()
    val settingsRoute = settingsBackStackEntry?.destination?.route ?: SettingsRoutes.Main
    val state = viewModel.uiState
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val localView = LocalView.current
    val density = LocalDensity.current
    val activity = context as? Activity
    val inMultiWindowMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        activity?.isInMultiWindowMode == true
    } else {
        false
    }
    val xiaomiMultiWindowUsesFrameworkInsets = inMultiWindowMode && isXiaomiFamilyDevice()
    val miuiFloatingTopCompensation = 0.dp
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDarkTheme = currentAppDarkTheme()
    val topBarColor = if (state.solidTopBar) md2CardContainerColor() else MaterialTheme.colorScheme.primary
    val topBarContentColor = if (state.solidTopBar) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
    val hiddenDrawerScrimColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = if (isDarkTheme) 0.56f else 0.32f
    )
    LaunchedEffect(
        state.liveSubtitleNotificationEnabled,
        state.status,
        viewModel.quickSubtitleCurrentText
    ) {
        LiveSubtitleNotificationBridge.update(
            context,
            state.liveSubtitleNotificationEnabled,
            viewModel.quickSubtitleCurrentText,
            state.status
        )
    }
    val desktopCaptionTopInset = with(density) {
        WindowInsets.captionBar.getTop(this).toDp()
    }
    val statusTopInset = with(density) {
        WindowInsets.statusBars.getTop(this).toDp()
    }
    val topBarDesktopMaximizeInset = when {
        // MIUI/HyperOS floating windows already offset the app content when decorFits is enabled
        // in MainActivity. Applying caption/status insets here again creates a double top gap.
        xiaomiMultiWindowUsesFrameworkInsets -> 0.dp
        desktopCaptionTopInset > 0.dp -> desktopCaptionTopInset
        inMultiWindowMode && statusTopInset > 0.dp -> statusTopInset
        else -> 0.dp
    }
    SideEffect {
        activity?.window?.let { window ->
            window.statusBarColor = topBarColor.toArgb()
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            WindowCompat.getInsetsController(window, window.decorView)
                .isAppearanceLightStatusBars = topBarColor.luminance() > 0.5f
            WindowCompat.getInsetsController(window, window.decorView)
                .isAppearanceLightNavigationBars = !isDarkTheme
        }
    }
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val ultraSmallAdaptiveWindow = isUltraSmallAdaptiveWindow(
        widthDp = configuration.screenWidthDp,
        heightDp = configuration.screenHeightDp,
        isLandscape = isLandscape
    )
    val forceLandscapeContentLayout = ultraSmallAdaptiveWindow &&
        minOf(configuration.screenWidthDp, configuration.screenHeightDp) < 520 &&
        (maxOf(configuration.screenWidthDp, configuration.screenHeightDp).toFloat() /
            minOf(configuration.screenWidthDp, configuration.screenHeightDp).coerceAtLeast(1)) <= 1.42f
    val layoutDirection = LocalLayoutDirection.current
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues()
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()
    val landscapeCutoutStart = if (isLandscape && !inMultiWindowMode) {
        displayCutoutPadding.calculateStartPadding(layoutDirection)
    } else {
        0.dp
    }
    val landscapeCutoutEnd = if (isLandscape && !inMultiWindowMode) {
        displayCutoutPadding.calculateEndPadding(layoutDirection)
    } else {
        0.dp
    }
    val landscapeNavBarStart = if (isLandscape && !inMultiWindowMode) {
        navigationBarsPadding.calculateStartPadding(layoutDirection)
    } else {
        0.dp
    }
    val landscapeNavBarEnd = if (isLandscape && !inMultiWindowMode) {
        navigationBarsPadding.calculateEndPadding(layoutDirection)
    } else {
        0.dp
    }
    val landscapeChromeStartInset = maxOf(landscapeCutoutStart, landscapeNavBarStart)
    val landscapeChromeEndInset = maxOf(landscapeCutoutEnd, landscapeNavBarEnd)
    val hiddenDrawerWidth = remember(configuration.screenWidthDp) {
        val screenWidth = configuration.screenWidthDp.dp
        val targetWidth = UiTokens.DrawerWidthExpanded
        // Keep a small right-side visible area on narrow screens to avoid hard overflow.
        val compatEdgeGap = 24.dp
        val maxAllowed = (screenWidth - compatEdgeGap).coerceAtLeast(0.dp)
        if (maxAllowed <= 0.dp) screenWidth else minOf(targetWidth, maxAllowed)
    }
    val permanentDrawerCollapsedWidth = UiTokens.DrawerWidthCollapsed + landscapeChromeStartInset
    val permanentDrawerExpandedWidth = UiTokens.DrawerWidthExpanded + landscapeChromeStartInset
    val hiddenDrawerSurfaceWidth = hiddenDrawerWidth + landscapeChromeStartInset
    val usePermanentDrawer =
        isLandscape && !ultraSmallAdaptiveWindow && state.landscapeDrawerMode == UserPrefs.DRAWER_MODE_PERMANENT
    val basePage = page
    val quickSubtitleEditorOpen =
        basePage == pageQuickSubtitle && quickSubtitleRoute == QuickSubtitleRoutes.Editor
    val quickSubtitleHistoryOpen =
        basePage == pageQuickSubtitle && quickSubtitleRoute == QuickSubtitleRoutes.History
    val quickSubtitleSubPageOpen =
        basePage == pageQuickSubtitle && quickSubtitleRoute != QuickSubtitleRoutes.Main
    val soundboardEditorOpen =
        basePage == pageSoundboard && soundboardRoute == SoundboardRoutes.Editor
    val soundboardSubPageOpen =
        basePage == pageSoundboard && soundboardRoute != SoundboardRoutes.Main
    val quickCardEditorOpen =
        basePage == pageQuickCard && quickCardRoute == QuickCardRoutes.Editor
    val quickCardSortOpen =
        basePage == pageQuickCard && quickCardRoute == QuickCardRoutes.Sort
    val quickCardScannerOpen =
        basePage == pageQuickCard && quickCardRoute == QuickCardRoutes.Scanner
    val quickCardScanTextOpen =
        basePage == pageQuickCard && quickCardRoute == QuickCardRoutes.ScanText
    val quickCardWebOpen =
        basePage == pageQuickCard && quickCardRoute == QuickCardRoutes.Web
    val quickCardMainOpen =
        basePage == pageQuickCard && quickCardRoute == QuickCardRoutes.Main
    val quickCardSubPageOpen =
        basePage == pageQuickCard && quickCardRoute != QuickCardRoutes.Main
    val settingsLogOpen =
        basePage == pageSettings && settingsRoute == SettingsRoutes.Log
    val settingsLicensesOpen =
        basePage == pageSettings && settingsRoute == SettingsRoutes.Licenses
    val settingsPrivacyOpen =
        basePage == pageSettings && settingsRoute == SettingsRoutes.Privacy
    val settingsAgreementOpen =
        basePage == pageSettings && settingsRoute == SettingsRoutes.Agreement
    var lastTopBarBackClickAtMs by remember { mutableLongStateOf(0L) }
    var drawerExpanded by rememberSaveable { mutableStateOf(false) }
    val runningStripEligible = !(drawingFullscreen && basePage == pageDrawing)
    val showRunningStripButton = runningStripEligible
    val showRunningStripPanel = runningStripEligible && !runningStripCollapsed
    val topMicLevel = viewModel.realtimeInputLevel
    val topPlaybackProgress = viewModel.realtimePlaybackProgress
    val drawerItems = listOf(
        DrawerItem(pageQuickSubtitle, "便捷字幕", "subtitles"),
        DrawerItem(pageQuickCard, "快捷名片", "id_card"),
        DrawerItem(pageDrawing, "画板", "draw"),
        DrawerItem(pageSoundboard, "音效板", "library_music"),
        DrawerItem(pageOverlay, "悬浮窗与热键", "open_in_new"),
        DrawerItem(pageVoicePack, "语音包", "record_voice_over"),
        DrawerItem(pageSettings, "设置", "tune")
    )
    val pendingQuickSubtitleLaunchRequest = viewModel.pendingQuickSubtitleLaunchRequest
    val pendingVoicePackInstallRequest = viewModel.pendingVoicePackInstallRequest
    val pendingPresetInstallRequest = viewModel.pendingPresetInstallRequest
    val drawerSelectedPage = basePage
    LaunchedEffect(drawerItems.size) {
        val validPages = drawerItems.map { it.page }.toSet()
        if (page !in validPages) {
            page = pageQuickSubtitle
        }
    }
    LaunchedEffect(basePage, quickSubtitleRoute) {
        if (basePage != pageQuickSubtitle && quickSubtitleRoute != QuickSubtitleRoutes.Main) {
            quickSubtitleNavController.popBackStack(QuickSubtitleRoutes.Main, inclusive = false)
        }
    }
    LaunchedEffect(basePage, soundboardRoute) {
        if (basePage != pageSoundboard && soundboardRoute != SoundboardRoutes.Main) {
            soundboardNavController.popBackStack(SoundboardRoutes.Main, inclusive = false)
        }
    }
    LaunchedEffect(basePage, quickCardRoute) {
        if (basePage != pageQuickCard && quickCardRoute != QuickCardRoutes.Main) {
            quickCardNavController.popBackStack(QuickCardRoutes.Main, inclusive = false)
        }
    }
    LaunchedEffect(basePage) {
        if (basePage != pageQuickCard) {
            quickCardNavReady = false
        }
    }
    LaunchedEffect(basePage, quickCardWebOpen) {
        if (basePage != pageQuickCard || !quickCardWebOpen) {
            quickCardWebMenuExpanded = false
        }
    }
    val pendingAccessibilityExplainRequest = viewModel.pendingAccessibilityExplainRequest
    LaunchedEffect(pendingAccessibilityExplainRequest?.requestId) {
        if (pendingAccessibilityExplainRequest != null) {
            page = pageOverlay
        }
    }
    LaunchedEffect(basePage, quickCardNavReady, pendingQuickCardOverlayTarget, quickCardRoute) {
        if (basePage != pageQuickCard || !quickCardNavReady) return@LaunchedEffect
        when (pendingQuickCardOverlayTarget) {
            OverlayBridge.TARGET_OPEN_QUICK_CARD -> {
                if (quickCardRoute != QuickCardRoutes.Main) {
                    quickCardNavController.popBackStack(QuickCardRoutes.Main, inclusive = false)
                }
                pendingQuickCardOverlayTarget = null
            }
            OverlayBridge.TARGET_OPEN_QR_SCANNER -> {
                if (quickCardRoute != QuickCardRoutes.Main &&
                    quickCardRoute != QuickCardRoutes.Scanner
                ) {
                    quickCardNavController.popBackStack(QuickCardRoutes.Main, inclusive = false)
                }
                if (quickCardRoute != QuickCardRoutes.Scanner) {
                    quickCardNavController.navigate(QuickCardRoutes.Scanner) {
                        launchSingleTop = true
                    }
                }
                pendingQuickCardOverlayTarget = null
            }
        }
    }
    LaunchedEffect(pendingQuickSubtitleLaunchRequest?.requestId) {
        val request = pendingQuickSubtitleLaunchRequest ?: return@LaunchedEffect
        when (request.target) {
            OverlayBridge.TARGET_OPEN_OVERLAY -> {
                page = pageOverlay
            }
            OverlayBridge.TARGET_OPEN_QUICK_CARD -> {
                page = pageQuickCard
                pendingQuickCardOverlayTarget = request.target
            }
            OverlayBridge.TARGET_OPEN_QR_SCANNER -> {
                page = pageQuickCard
                pendingQuickCardOverlayTarget = request.target
            }
            OverlayBridge.TARGET_OPEN_DRAWING -> {
                page = pageDrawing
            }
            OverlayBridge.TARGET_OPEN_SOUNDBOARD -> {
                request.soundboardGroupId?.let { viewModel.selectSoundboardGroupById(it) }
                page = pageSoundboard
                if (soundboardRoute != SoundboardRoutes.Main) {
                    soundboardNavController.popBackStack(SoundboardRoutes.Main, inclusive = false)
                }
            }
            OverlayBridge.TARGET_OPEN_VOICE_PACK -> {
                page = pageVoicePack
            }
            OverlayBridge.TARGET_OPEN_SETTINGS -> {
                page = pageSettings
                if (settingsRoute != SettingsRoutes.Main) {
                    settingsNavController.popBackStack(SettingsRoutes.Main, inclusive = false)
                }
            }
            else -> {
                if (request.navigateToPage) {
                    quickSubtitleFullscreen = false
                    if (page != pageQuickSubtitle) {
                        page = pageQuickSubtitle
                    }
                    if (quickSubtitleRoute != QuickSubtitleRoutes.Main) {
                        quickSubtitleNavController.popBackStack(QuickSubtitleRoutes.Main, inclusive = false)
                    }
                }
                viewModel.applyExternalQuickSubtitleRequest(
                    requestId = request.requestId,
                    target = request.target,
                    text = request.text
                )
            }
        }
        viewModel.consumeQuickSubtitleLaunchRequest(request.requestId)
        if (!usePermanentDrawer) {
            drawerState.close()
        }
    }
    LaunchedEffect(pendingVoicePackInstallRequest?.requestId) {
        val request = pendingVoicePackInstallRequest ?: return@LaunchedEffect
        page = pageVoicePack
        toast(context, request.message)
        viewModel.consumeVoicePackInstallRequest(request.requestId)
        if (!usePermanentDrawer) {
            drawerState.close()
        }
    }
    LaunchedEffect(pendingPresetInstallRequest?.requestId) {
        val request = pendingPresetInstallRequest ?: return@LaunchedEffect
        when (request.target) {
            PresetInstallTarget.QuickSubtitle -> {
                page = pageQuickSubtitle
                if (quickSubtitleRoute != QuickSubtitleRoutes.Editor) {
                    quickSubtitleNavController.navigate(QuickSubtitleRoutes.Editor) {
                        launchSingleTop = true
                    }
                }
            }
            PresetInstallTarget.Soundboard -> {
                page = pageSoundboard
                if (soundboardRoute != SoundboardRoutes.Editor) {
                    soundboardNavController.navigate(SoundboardRoutes.Editor) {
                        launchSingleTop = true
                    }
                }
            }
        }
        toast(context, request.message)
        viewModel.consumePresetInstallRequest(request.requestId)
        if (!usePermanentDrawer) {
            drawerState.close()
        }
    }
    LaunchedEffect(state.floatingOverlayEnabled) {
        if (!state.floatingOverlayEnabled) {
            FloatingOverlayService.stop(context)
        } else if (FloatingOverlayService.canDrawOverlays(context)) {
            FloatingOverlayService.start(context)
        } else {
            viewModel.setFloatingOverlayEnabled(false)
            FloatingOverlayService.stop(context)
        }
    }
    LaunchedEffect(basePage, settingsRoute) {
        if (basePage != pageSettings && settingsRoute != SettingsRoutes.Main) {
            settingsNavController.popBackStack(SettingsRoutes.Main, inclusive = false)
        }
    }
    LaunchedEffect(page) {
        if (page != pageDrawing) drawingFullscreen = false
        if (page != pageQuickSubtitle) quickSubtitleFullscreen = false
    }
    LaunchedEffect(drawingFullscreen, page) {
        if (drawingFullscreen && page == pageDrawing && !usePermanentDrawer) {
            scope.launch { drawerState.close() }
        }
    }
    LaunchedEffect(usePermanentDrawer) {
        if (usePermanentDrawer) {
            scope.launch { drawerState.close() }
        }
    }
    LaunchedEffect(basePage, settingsLogOpen) {
        if (!settingsLogOpen) {
            logTopBarActions = null
        }
    }
    LaunchedEffect(basePage, quickSubtitleRoute) {
        if (basePage != pageQuickSubtitle || quickSubtitleRoute != QuickSubtitleRoutes.Editor) {
            quickSubtitleEditorBatchTopBarActions = null
        }
    }
    LaunchedEffect(basePage, soundboardRoute) {
        if (basePage != pageSoundboard || soundboardRoute != SoundboardRoutes.Editor) {
            soundboardEditorBatchTopBarActions = null
        }
    }
    LaunchedEffect(basePage, quickCardEditorOpen) {
        if (basePage != pageQuickCard) {
            quickCardTopBarActions = null
        } else if (!quickCardEditorOpen) {
            // main page keeps actions set by QuickCardNavHost
        }
    }
    val baseSoftInputMode = remember(activity) {
        activity?.window?.attributes?.softInputMode
    }
    fun applySoftInputModeForRoute() {
        val window = activity?.window ?: return
        val base = baseSoftInputMode ?: return
        val stateMask = base and WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE
        val adjustMask = if (quickSubtitleEditorOpen) {
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        } else {
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        }
        window.setSoftInputMode(stateMask or adjustMask)
        AppLogger.i(
            "AppScaffold.applySoftInputModeForRoute page=$basePage route=$quickSubtitleRoute " +
                    "editorOpen=$quickSubtitleEditorOpen softInput=${softInputModeSummary(window.attributes.softInputMode)}"
        )
    }
    fun popSecondaryPageSafely() {
        val now = SystemClock.elapsedRealtime()
        // Guard double taps: avoid consecutive pop during transition.
        if (now - lastTopBarBackClickAtMs < 280L) return
        lastTopBarBackClickAtMs = now
        when {
            quickSubtitleSubPageOpen -> {
                quickSubtitleNavController.popBackStack(QuickSubtitleRoutes.Main, inclusive = false)
            }
            soundboardSubPageOpen -> {
                soundboardNavController.popBackStack(SoundboardRoutes.Main, inclusive = false)
            }
            settingsLogOpen || settingsLicensesOpen || settingsPrivacyOpen || settingsAgreementOpen -> {
                settingsNavController.popBackStack(SettingsRoutes.Main, inclusive = false)
            }
            quickCardEditorOpen -> {
                val handledByEditor = quickCardTopBarActions?.onBackRequest != null
                quickCardTopBarActions?.onBackRequest?.invoke()
                if (!handledByEditor) {
                    quickCardNavController.popBackStack(QuickCardRoutes.Main, inclusive = false)
                }
            }
            quickCardSubPageOpen -> {
                quickCardNavController.popBackStack(QuickCardRoutes.Main, inclusive = false)
            }
        }
    }

    fun clearFocusAndHideIme(reason: String) {
        activity?.currentFocus?.clearFocus()
        localView.clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(localView.windowToken, 0)
        AppLogger.i("AppScaffold.clearFocusAndHideIme reason=$reason")
    }

    SideEffect {
        applySoftInputModeForRoute()
    }
    DisposableEffect(activity, lifecycleOwner, baseSoftInputMode) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
                applySoftInputModeForRoute()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            val window = activity?.window
            val base = baseSoftInputMode
            if (window != null && base != null) {
                window.setSoftInputMode(base)
            }
        }
    }
    LaunchedEffect(basePage, quickSubtitleRoute) {
        if (basePage != pageQuickSubtitle || quickSubtitleRoute != QuickSubtitleRoutes.Main) {
            clearFocusAndHideIme("leave_quick_subtitle_main")
        }
    }
    LaunchedEffect(basePage, quickSubtitleRoute, quickSubtitleEditorOpen, inMultiWindowMode) {
        val mode = activity?.window?.attributes?.softInputMode ?: 0
        AppLogger.i(
            "AppScaffold.routeChanged page=$basePage route=$quickSubtitleRoute " +
                    "editorOpen=$quickSubtitleEditorOpen inMultiWindow=$inMultiWindowMode " +
                    "softInput=${softInputModeSummary(mode)}"
        )
    }

    var startRealtimeAfterPermissionGrant by remember { mutableStateOf(false) }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val shouldStartRealtime = startRealtimeAfterPermissionGrant
        startRealtimeAfterPermissionGrant = false
        if (granted) {
            if (shouldStartRealtime) {
                if (state.asrDir == null && !state.recognitionResourceInstalled) {
                    startRealtimeAfterRecognitionResourceInstall = true
                    recognitionResourceRequiredDialog = true
                    viewModel.refreshRecognitionResourceStatus()
                } else {
                    viewModel.start()
                }
            }
        } else {
            toast(context, "需要麦克风权限")
        }
    }
    val pendingRecordAudioPermissionRequest = viewModel.pendingRecordAudioPermissionRequest
    LaunchedEffect(pendingRecordAudioPermissionRequest?.requestId) {
        val request = pendingRecordAudioPermissionRequest ?: return@LaunchedEffect
        startRealtimeAfterPermissionGrant = request.startRealtimeOnGrant
        permLauncher.launch(Manifest.permission.RECORD_AUDIO)
        viewModel.consumeRecordAudioPermissionRequest(request.requestId)
    }
    val voicePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.importVoice(uri) else toast(context, "未选择文件")
    }
    val quickSubtitlePresetPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.importQuickSubtitlePresetPackage(uri) else toast(context, "未选择文件")
    }
    val soundboardPresetPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.importSoundboardPresetPackage(uri) else toast(context, "未选择文件")
    }
    val recognitionResourcePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.installRecognitionResources(uri) else toast(context, "未选择文件")
    }
    val kokoroVoicePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.installKokoroVoice(uri) else toast(context, "未选择文件")
    }
    var showBuiltinVoicePicker by remember { mutableStateOf(false) }
    val recognitionResourceMissing = state.asrDir == null && !state.recognitionResourceInstalled

    fun requestRecordAudioPermissionAndStart() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            viewModel.start()
        } else {
            startRealtimeAfterPermissionGrant = true
            permLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun requestRealtimeStartWithResourceCheck(autoStartAfterInstall: Boolean) {
        if (recognitionResourceMissing) {
            startRealtimeAfterPermissionGrant = false
            startRealtimeAfterRecognitionResourceInstall = autoStartAfterInstall
            recognitionResourceRequiredDialog = true
            viewModel.refreshRecognitionResourceStatus()
            return
        }
        requestRecordAudioPermissionAndStart()
    }

    LaunchedEffect(
        recognitionResourceRequiredDialog,
        startRealtimeAfterRecognitionResourceInstall,
        state.recognitionResourceInstalled,
        state.recognitionResourceBusy
    ) {
        if (
            recognitionResourceRequiredDialog &&
            startRealtimeAfterRecognitionResourceInstall &&
            state.recognitionResourceInstalled &&
            !state.recognitionResourceBusy
        ) {
            recognitionResourceRequiredDialog = false
            startRealtimeAfterRecognitionResourceInstall = false
            requestRecordAudioPermissionAndStart()
        }
    }

    val onToggleRun = {
        if (!state.running && state.ttsDisabled) {
            toast(context, TTS_DISABLED_MESSAGE)
        }
        if (state.running) {
            viewModel.stop()
        } else {
            requestRealtimeStartWithResourceCheck(autoStartAfterInstall = true)
        }
    }
    var pttConfirmOwnedByMainPanel by remember { mutableStateOf(false) }
    var pttTemporaryStartByMainPanel by remember { mutableStateOf(false) }

    val onPushToTalkPressStart = {
        pttConfirmOwnedByMainPanel = true
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            pttConfirmOwnedByMainPanel = false
            pttTemporaryStartByMainPanel = false
            startRealtimeAfterPermissionGrant = false
            if (recognitionResourceMissing) {
                recognitionResourceRequiredDialog = true
                startRealtimeAfterRecognitionResourceInstall = false
                viewModel.refreshRecognitionResourceStatus()
            } else {
                permLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        } else if (recognitionResourceMissing) {
            pttConfirmOwnedByMainPanel = false
            pttTemporaryStartByMainPanel = false
            recognitionResourceRequiredDialog = true
            startRealtimeAfterRecognitionResourceInstall = false
            viewModel.refreshRecognitionResourceStatus()
        } else {
            pttTemporaryStartByMainPanel = !state.running
            if (pttTemporaryStartByMainPanel) {
                viewModel.start()
            }
            viewModel.beginPushToTalkSession()
            viewModel.setPushToTalkPressed(true)
        }
    }
    val onPushToTalkPressEnd: (PttConfirmReleaseAction) -> Unit = { releaseAction ->
        val shouldStop = pttTemporaryStartByMainPanel
        viewModel.commitPushToTalkSession(releaseAction)
        viewModel.setPushToTalkPressed(false)
        pttConfirmOwnedByMainPanel = false
        pttTemporaryStartByMainPanel = false
        if (shouldStop) {
            viewModel.stop()
        }
    }
    LaunchedEffect(state.pushToTalkPressed) {
        if (!state.pushToTalkPressed) {
            pttConfirmOwnedByMainPanel = false
            pttTemporaryStartByMainPanel = false
        }
    }
    if (showBuiltinVoicePicker) {
        BuiltinFilePickerDialog(
            title = "选择语音包文件",
            allowedExtensions = setOf("zip", "kigvpk"),
            onDismiss = { showBuiltinVoicePicker = false },
            onPicked = { uri ->
                showBuiltinVoicePicker = false
                viewModel.importVoice(uri)
            },
            onOpenSystemPicker = {
                showBuiltinVoicePicker = false
                voicePicker.launch("*/*")
            }
        )
    }
    if (showBuiltinQuickSubtitlePresetPicker) {
        BuiltinFilePickerDialog(
            title = "选择便捷字幕预设",
            allowedExtensions = setOf("kigtpk", "zip", "json"),
            onDismiss = { showBuiltinQuickSubtitlePresetPicker = false },
            onPicked = { uri ->
                showBuiltinQuickSubtitlePresetPicker = false
                viewModel.importQuickSubtitlePresetPackage(uri)
            },
            onOpenSystemPicker = {
                showBuiltinQuickSubtitlePresetPicker = false
                quickSubtitlePresetPicker.launch("*/*")
            }
        )
    }
    if (showBuiltinSoundboardPresetPicker) {
        BuiltinFilePickerDialog(
            title = "选择音效板预设",
            allowedExtensions = setOf("kigspk", "zip", "json"),
            onDismiss = { showBuiltinSoundboardPresetPicker = false },
            onPicked = { uri ->
                showBuiltinSoundboardPresetPicker = false
                viewModel.importSoundboardPresetPackage(uri)
            },
            onOpenSystemPicker = {
                showBuiltinSoundboardPresetPicker = false
                soundboardPresetPicker.launch("*/*")
            }
        )
    }
    if (showBuiltinRecognitionResourcePicker) {
        BuiltinFilePickerDialog(
            title = "选择语音识别资源包",
            allowedExtensions = setOf("7z", "zip"),
            onDismiss = { showBuiltinRecognitionResourcePicker = false },
            onPicked = { uri ->
                showBuiltinRecognitionResourcePicker = false
                viewModel.installRecognitionResources(uri)
            },
            onOpenSystemPicker = {
                showBuiltinRecognitionResourcePicker = false
                recognitionResourcePicker.launch("*/*")
            }
        )
    }
    if (showBuiltinKokoroVoicePicker) {
        BuiltinFilePickerDialog(
            title = "选择 Kokoro 离线语音资源",
            allowedExtensions = setOf("zip", "tar", "bz2", "tbz2"),
            onDismiss = { showBuiltinKokoroVoicePicker = false },
            onPicked = { uri ->
                showBuiltinKokoroVoicePicker = false
                viewModel.installKokoroVoice(uri)
            },
            onOpenSystemPicker = {
                showBuiltinKokoroVoicePicker = false
                kokoroVoicePicker.launch("*/*")
            }
        )
    }
    if (recognitionResourceSourceDialog) {
        RecognitionResourceSourceDialog(
            modelScopeUrl = state.recognitionResourceModelScopeUrl,
            huggingFaceUrl = state.recognitionResourceHuggingFaceUrl,
            preferredSource = state.recognitionResourcePreferredSource,
            onDismiss = { recognitionResourceSourceDialog = false },
            onConfirm = { modelScopeUrl, huggingFaceUrl, preferredSource ->
                recognitionResourceSourceDialog = false
                viewModel.setRecognitionResourceSources(modelScopeUrl, huggingFaceUrl, preferredSource)
            }
        )
    }
    if (kokoroSourceDialog) {
        KokoroSourceDialog(
            hfUrl = state.kokoroHfUrl,
            hfMirrorUrl = state.kokoroHfMirrorUrl,
            modelScopeUrl = state.kokoroModelScopeUrl,
            preferredSource = state.kokoroPreferredSource,
            onDismiss = { kokoroSourceDialog = false },
            onConfirm = { hfUrl, hfMirrorUrl, modelScopeUrl, preferredSource ->
                kokoroSourceDialog = false
                viewModel.setKokoroSources(hfUrl, hfMirrorUrl, modelScopeUrl, preferredSource)
            }
        )
    }
    if (kokoroVoiceSettingsDialog) {
        KokoroVoiceSettingsDialog(
            state = state,
            onDismiss = { kokoroVoiceSettingsDialog = false },
            onSpeakerChange = { viewModel.setKokoroSpeakerId(it) }
        )
    }
    if (recognitionResourceRequiredDialog) {
        RecognitionResourceRequiredDialog(
            state = state,
            onDismiss = {
                if (!state.recognitionResourceBusy) {
                    recognitionResourceRequiredDialog = false
                    startRealtimeAfterRecognitionResourceInstall = false
                }
            },
            onDownload = {
                viewModel.downloadRecognitionResources()
            },
            onPickLocalPackage = {
                showBuiltinRecognitionResourcePicker = true
            },
            onOpenSources = {
                recognitionResourceSourceDialog = true
            }
        )
    }
    if (quickSubtitlePresetExportDialog) {
        PresetGroupExportDialog(
            title = "导出便捷字幕预设",
            groups = viewModel.quickSubtitleGroups.map { it.id to it.title.ifBlank { "未命名分组" } },
            onDismiss = { quickSubtitlePresetExportDialog = false },
            onConfirm = { ids ->
                quickSubtitlePresetExportDialog = false
                viewModel.exportQuickSubtitlePresetPackage(ids)
            }
        )
    }
    if (soundboardPresetExportDialog) {
        PresetGroupExportDialog(
            title = "导出音效板预设",
            groups = viewModel.soundboardGroups.map { it.id to it.title.ifBlank { "未命名分组" } },
            onDismiss = { soundboardPresetExportDialog = false },
            onConfirm = { ids ->
                soundboardPresetExportDialog = false
                viewModel.exportSoundboardPresetPackage(ids)
            }
        )
    }
    var realtimePttDragTarget by remember { mutableStateOf(PttConfirmDragTarget.DefaultSend) }
    val realtimeConfirmOverlayEnabled = false
    val realtimeShowPttConfirmOverlay =
        realtimeConfirmOverlayEnabled && state.pushToTalkPressed
    val realtimePttFabSize = 56.dp
    val realtimePttFabEndInset = 16.dp
    val realtimePttFabBottomOffset = 16.dp
    val realtimePttStatusStripBottomOffset = realtimePttFabBottomOffset
    val realtimePttStatusStripBottomBleed = 12.dp
    val realtimeCompactModeDetectionEnabled =
        isLandscape && realtimeConfirmOverlayEnabled
    val realtimeImeBottomInset =
        if (realtimeCompactModeDetectionEnabled) WindowInsets.ime.asPaddingValues().calculateBottomPadding() else 0.dp
    val realtimeNavBottomInset =
        if (realtimeCompactModeDetectionEnabled) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() else 0.dp
    val realtimeBottomObstructionInset =
        if (realtimeImeBottomInset > realtimeNavBottomInset) realtimeImeBottomInset else realtimeNavBottomInset
    val realtimeImeVisible = realtimeImeBottomInset > 0.dp
    val realtimePttTopButtonsRequiredHeight = 96.dp
    val realtimePttTopRowBottomReserved = if (isLandscape) 72.dp else 74.dp
    val realtimePttTopEstimatedAvailableHeight =
        configuration.screenHeightDp.dp - realtimeBottomObstructionInset -
            (realtimePttFabSize + realtimePttFabBottomOffset + realtimePttStatusStripBottomBleed + 72.dp)
    val realtimeCompactPttSideButtonsMode =
        realtimeCompactModeDetectionEnabled &&
            (realtimeImeVisible || realtimePttTopEstimatedAvailableHeight < realtimePttTopButtonsRequiredHeight)
    val realtimePttGuideText = when (realtimePttDragTarget) {
        PttConfirmDragTarget.DefaultSend -> "松开手指上屏"
        PttConfirmDragTarget.ToInput -> "松开手指上屏"
        PttConfirmDragTarget.Cancel -> "松开取消发送"
    }
    val realtimePttStripFabReserveWidth = realtimePttFabSize
    val realtimePttStatusStripEndInset = realtimePttFabEndInset
    val realtimePttStatusStripAnchorEndInset = realtimePttStatusStripEndInset + (realtimePttFabSize / 2)
    val realtimePttStatusStripOuterBleed = 12.dp
    val realtimePttStatusStripAnimatedEndInset by animateDpAsState(
        targetValue = if (realtimeShowPttConfirmOverlay) {
            realtimePttStatusStripEndInset
        } else {
            realtimePttStatusStripAnchorEndInset
        },
        animationSpec = if (realtimeShowPttConfirmOverlay) {
            tween(durationMillis = 220, easing = FastOutSlowInEasing)
        } else {
            tween(durationMillis = 180, easing = FastOutSlowInEasing)
        },
        label = "realtime_ptt_status_strip_end_inset"
    )
    val realtimePttStatusStripStartInset = (10.dp - realtimePttStatusStripOuterBleed).coerceAtLeast(0.dp)
    val realtimePttStatusStripTopBleed = (realtimePttStatusStripOuterBleed - 4.dp).coerceAtLeast(0.dp)
    val realtimePttStatusStripAnimatedEndInsetWithBleed =
        (realtimePttStatusStripAnimatedEndInset - realtimePttStatusStripOuterBleed).coerceAtLeast(0.dp)
    val realtimePttStatusStripBottomInset =
        (realtimePttStatusStripBottomOffset - realtimePttStatusStripBottomBleed).coerceAtLeast(0.dp)
    LaunchedEffect(realtimeShowPttConfirmOverlay) {
        if (!realtimeShowPttConfirmOverlay) {
            realtimePttDragTarget = PttConfirmDragTarget.DefaultSend
        }
    }

    val topBar: @Composable ((() -> Unit)) -> Unit = { onNavClick ->
        val currentTitle = if (quickSubtitleEditorOpen) {
            "编辑便捷字幕"
        } else if (soundboardEditorOpen) {
            "编辑音效板"
        } else if (quickSubtitleHistoryOpen) {
            "历史记录"
        } else if (quickCardEditorOpen) {
            "编辑快捷名片"
        } else if (quickCardSortOpen) {
            "管理名片"
        } else if (quickCardScannerOpen) {
            "扫描二维码"
        } else if (quickCardScanTextOpen) {
            "二维码结果"
        } else if (quickCardWebOpen) {
            "二维码网页"
        } else if (settingsLogOpen) {
            "日志"
        } else if (settingsLicensesOpen) {
            "开源许可证"
        } else if (settingsPrivacyOpen) {
            "隐私政策"
        } else if (settingsAgreementOpen) {
            "用户协议"
        } else {
            when (basePage) {
                pageQuickSubtitle -> "便捷字幕"
                pageOverlay -> "悬浮窗与热键"
                pageQuickCard -> "快捷名片"
                pageVoicePack -> "语音包"
                pageDrawing -> "画板"
                pageSoundboard -> "音效板"
                pageSettings -> "设置"
                else -> "KIGTTS"
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topBarDesktopMaximizeInset)
                .padding(top = miuiFloatingTopCompensation)
                .zIndex(2f),
            color = topBarColor,
            elevation = UiTokens.TopBarElevation
        ) {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (!inMultiWindowMode) Modifier.statusBarsPadding() else Modifier)
                    .padding(start = landscapeChromeStartInset, end = landscapeChromeEndInset),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Crossfade(
                            modifier = Modifier.weight(1f),
                            targetState = currentTitle,
                            animationSpec = tween(140, easing = LinearEasing),
                            label = "topbar_title_switch"
                        ) { titleText ->
                            Text(
                                text = titleText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        AnimatedVisibility(
                            visible = showRunningStripButton,
                            enter = fadeIn(animationSpec = tween(140)) +
                                    androidx.compose.animation.slideInHorizontally(
                                        initialOffsetX = { full -> full / 3 },
                                        animationSpec = tween(140, easing = FastOutSlowInEasing)
                                    ),
                            exit = fadeOut(animationSpec = tween(120)) +
                                    androidx.compose.animation.slideOutHorizontally(
                                        targetOffsetX = { full -> full / 3 },
                                        animationSpec = tween(120, easing = FastOutSlowInEasing)
                                    )
                        ) {
                            RunningStripTopBarToggle(
                                micLevel = topMicLevel,
                                playbackProgress = topPlaybackProgress,
                                expanded = !runningStripCollapsed,
                                pushToTalkMode = state.pushToTalkMode,
                                pushToTalkPressed = state.pushToTalkPressed,
                                ttsDisabled = state.ttsDisabled,
                                contentColor = topBarContentColor,
                                onToggle = { runningStripCollapsed = !runningStripCollapsed }
                            )
                        }
                    }
                },
                navigationIcon = {
                    AnimatedContent(
                        targetState = when {
                            quickSubtitleSubPageOpen -> 1
                            soundboardSubPageOpen -> 2
                            settingsLogOpen || settingsLicensesOpen || settingsPrivacyOpen || settingsAgreementOpen -> 3
                            quickCardSubPageOpen -> 4
                            else -> 0
                        },
                        transitionSpec = {
                            ContentTransform(
                                targetContentEnter = fadeIn(
                                    animationSpec = tween(120, easing = LinearEasing)
                                ),
                                initialContentExit = fadeOut(
                                    animationSpec = tween(120, easing = LinearEasing)
                                )
                            )
                        },
                        label = "topbar_nav_switch"
                    ) { navMode ->
                        if (navMode == 1 || navMode == 2 || navMode == 3 || navMode == 4) {
                            KigttsIconButton(onClick = {
                                popSecondaryPageSafely()
                            }) {
                                MsIcon("arrow_back", contentDescription = "返回")
                            }
                        } else {
                            KigttsIconButton(onClick = onNavClick) {
                                MsIcon("menu", contentDescription = "打开菜单")
                            }
                        }
                    }
                },
                actions = {
                    val quickCardActions = quickCardTopBarActions
                    val showQuickSubtitleActions =
                        basePage == pageQuickSubtitle && quickSubtitleRoute == QuickSubtitleRoutes.Main
                    val showQuickSubtitleCompactEditorAction =
                        showQuickSubtitleActions &&
                            !isLandscape &&
                            state.quickSubtitleCompactControls
                    val showQuickSubtitleEditorActions =
                        basePage == pageQuickSubtitle && quickSubtitleRoute == QuickSubtitleRoutes.Editor
                    val showSoundboardEditorActions =
                        basePage == pageSoundboard && soundboardRoute == SoundboardRoutes.Editor
                    val showSoundboardActions =
                        basePage == pageSoundboard && soundboardRoute == SoundboardRoutes.Main
                    val soundboardLayoutMode = viewModel.currentSoundboardLayout(isLandscape)
                    val soundboardGridMode = soundboardLayoutMode.isGrid
                    val quickSubtitleBatchActions = quickSubtitleEditorBatchTopBarActions
                    val soundboardBatchActions = soundboardEditorBatchTopBarActions
                    val showQuickSubtitleEditorBatchActions =
                        showQuickSubtitleEditorActions && quickSubtitleBatchActions != null
                    val showSoundboardEditorBatchActions =
                        showSoundboardEditorActions && soundboardBatchActions != null
                    val showQuickCardMainActions =
                        basePage == pageQuickCard &&
                                quickCardRoute == QuickCardRoutes.Main
                    val showQuickCardEditorActions =
                        basePage == pageQuickCard &&
                                quickCardRoute == QuickCardRoutes.Editor &&
                                viewModel.quickCardDraft?.isNew == false
                    val showQuickCardSortActions =
                        basePage == pageQuickCard &&
                                quickCardRoute == QuickCardRoutes.Sort
                    val showQuickCardWebActions =
                        basePage == pageQuickCard && quickCardRoute == QuickCardRoutes.Web
                    val showDrawingActions = basePage == pageDrawing
                    val showVoicePackActions = basePage == pageVoicePack
                    val showSettingsEntryActions =
                        basePage == pageSettings && settingsRoute == SettingsRoutes.Main
                    val showSettingsLogActions = basePage == pageSettings && settingsLogOpen
                    val settingsActions = logTopBarActions

                    val quickSubtitleAlpha by animateFloatAsState(
                        targetValue = if (showQuickSubtitleActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_quick_subtitle_actions_alpha"
                    )
                    val quickSubtitleEditorAlpha by animateFloatAsState(
                        targetValue = if (showQuickSubtitleEditorActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_quick_subtitle_editor_actions_alpha"
                    )
                    val soundboardEditorAlpha by animateFloatAsState(
                        targetValue = if (showSoundboardEditorActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_soundboard_editor_actions_alpha"
                    )
                    val soundboardAlpha by animateFloatAsState(
                        targetValue = if (showSoundboardActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_soundboard_actions_alpha"
                    )
                    val drawingAlpha by animateFloatAsState(
                        targetValue = if (showDrawingActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_drawing_actions_alpha"
                    )
                    val quickCardMainAlpha by animateFloatAsState(
                        targetValue = if (showQuickCardMainActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_quick_card_main_actions_alpha"
                    )
                    val quickCardEditorAlpha by animateFloatAsState(
                        targetValue = if (showQuickCardEditorActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_quick_card_editor_actions_alpha"
                    )
                    val quickCardSortAlpha by animateFloatAsState(
                        targetValue = if (showQuickCardSortActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_quick_card_sort_actions_alpha"
                    )
                    val quickCardWebAlpha by animateFloatAsState(
                        targetValue = if (showQuickCardWebActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_quick_card_web_actions_alpha"
                    )
                    val voicePackAlpha by animateFloatAsState(
                        targetValue = if (showVoicePackActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_voicepack_actions_alpha"
                    )
                    val settingsEntryAlpha by animateFloatAsState(
                        targetValue = if (showSettingsEntryActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_settings_entry_alpha"
                    )
                    val settingsLogAlpha by animateFloatAsState(
                        targetValue = if (showSettingsLogActions) 1f else 0f,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_settings_log_actions_alpha"
                    )
                    val actionsWidthTarget = when {
                        showSettingsLogActions -> 144.dp
                        showQuickSubtitleEditorBatchActions || showSoundboardEditorBatchActions -> 144.dp
                        showQuickSubtitleEditorActions || showSoundboardEditorActions -> 96.dp
                        showQuickSubtitleCompactEditorAction -> 96.dp
                        showQuickCardMainActions || showQuickCardEditorActions -> 96.dp
                        showQuickCardSortActions ->
                            if (quickCardActions?.canClose == true) 96.dp else 48.dp
                        showQuickCardWebActions -> 48.dp
                        showDrawingActions -> 144.dp
                        showQuickSubtitleActions ||
                            showSoundboardActions ||
                            showVoicePackActions ||
                            showSettingsEntryActions -> 48.dp
                        else -> 0.dp
                    }
                    val actionsWidth by animateDpAsState(
                        targetValue = actionsWidthTarget,
                        animationSpec = tween(130, easing = FastOutSlowInEasing),
                        label = "topbar_actions_width"
                    )

                    Box(
                        modifier = Modifier.width(actionsWidth),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        key("topbar_quick_subtitle_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = quickSubtitleAlpha }
                                    .zIndex(if (showQuickSubtitleActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (showQuickSubtitleCompactEditorAction) {
                                    KigttsIconButton(
                                        onClick = {
                                            quickSubtitleNavController.navigate(QuickSubtitleRoutes.Editor)
                                        },
                                        enabled = showQuickSubtitleActions
                                    ) {
                                        MsIcon(
                                            name = "edit",
                                            contentDescription = "编辑快捷文本"
                                        )
                                    }
                                }
                                KigttsIconButton(
                                    onClick = { quickSubtitleFullscreen = !quickSubtitleFullscreen },
                                    enabled = showQuickSubtitleActions
                                ) {
                                    MsIcon(
                                        name = if (quickSubtitleFullscreen) "fullscreen_exit" else "fullscreen",
                                        contentDescription = if (quickSubtitleFullscreen) "退出全屏" else "进入全屏"
                                    )
                                }
                            }
                        }

                        key("topbar_quick_subtitle_editor_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = quickSubtitleEditorAlpha }
                                    .zIndex(if (showQuickSubtitleEditorActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (quickSubtitleBatchActions != null) {
                                    KigttsIconButton(
                                        onClick = quickSubtitleBatchActions.onMove,
                                        enabled = showQuickSubtitleEditorActions && quickSubtitleBatchActions.canMove
                                    ) {
                                        MsIcon("drive_file_move", contentDescription = "移动到其它分组")
                                    }
                                    KigttsIconButton(
                                        onClick = quickSubtitleBatchActions.onDelete,
                                        enabled = showQuickSubtitleEditorActions && quickSubtitleBatchActions.canDelete
                                    ) {
                                        MsIcon("delete", contentDescription = "删除所选快捷文本")
                                    }
                                    KigttsIconButton(
                                        onClick = quickSubtitleBatchActions.onClose,
                                        enabled = showQuickSubtitleEditorActions
                                    ) {
                                        MsIcon("close", contentDescription = "退出批量管理")
                                    }
                                } else {
                                    KigttsIconButton(
                                        onClick = {
                                            if (state.useBuiltinFileManager) {
                                                showBuiltinQuickSubtitlePresetPicker = true
                                            } else {
                                                quickSubtitlePresetPicker.launch("*/*")
                                            }
                                        },
                                        enabled = showQuickSubtitleEditorActions
                                    ) {
                                        MsIcon("folder_open", contentDescription = "导入便捷字幕预设")
                                    }
                                    KigttsIconButton(
                                        onClick = { quickSubtitlePresetExportDialog = true },
                                        enabled = showQuickSubtitleEditorActions && viewModel.quickSubtitleGroups.isNotEmpty()
                                    ) {
                                        MsIcon("share", contentDescription = "导出便捷字幕预设")
                                    }
                                }
                            }
                        }

                        key("topbar_soundboard_editor_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = soundboardEditorAlpha }
                                    .zIndex(if (showSoundboardEditorActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (soundboardBatchActions != null) {
                                    KigttsIconButton(
                                        onClick = soundboardBatchActions.onMove,
                                        enabled = showSoundboardEditorActions && soundboardBatchActions.canMove
                                    ) {
                                        MsIcon("drive_file_move", contentDescription = "移动到其它分组")
                                    }
                                    KigttsIconButton(
                                        onClick = soundboardBatchActions.onDelete,
                                        enabled = showSoundboardEditorActions && soundboardBatchActions.canDelete
                                    ) {
                                        MsIcon("delete", contentDescription = "删除所选音效")
                                    }
                                    KigttsIconButton(
                                        onClick = soundboardBatchActions.onClose,
                                        enabled = showSoundboardEditorActions
                                    ) {
                                        MsIcon("close", contentDescription = "退出批量管理")
                                    }
                                } else {
                                    KigttsIconButton(
                                        onClick = {
                                            if (state.useBuiltinFileManager) {
                                                showBuiltinSoundboardPresetPicker = true
                                            } else {
                                                soundboardPresetPicker.launch("*/*")
                                            }
                                        },
                                        enabled = showSoundboardEditorActions
                                    ) {
                                        MsIcon("folder_open", contentDescription = "导入音效板预设")
                                    }
                                    KigttsIconButton(
                                        onClick = { soundboardPresetExportDialog = true },
                                        enabled = showSoundboardEditorActions && viewModel.soundboardGroups.isNotEmpty()
                                    ) {
                                        MsIcon("share", contentDescription = "导出音效板预设")
                                    }
                                }
                            }
                        }

                        key("topbar_soundboard_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = soundboardAlpha }
                                    .zIndex(if (showSoundboardActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                KigttsIconButton(
                                    onClick = {
                                        viewModel.updateSoundboardLayout(
                                            landscape = isLandscape,
                                            layout = if (soundboardGridMode) {
                                                SoundboardLayoutMode.List
                                            } else {
                                                SoundboardLayoutMode.Grid2
                                            }
                                        )
                                    },
                                    enabled = showSoundboardActions
                                ) {
                                    MsIcon(
                                        if (soundboardGridMode) "grid_view" else "view_list",
                                        contentDescription = if (soundboardGridMode) {
                                            "当前宫格，点击切换列表"
                                        } else {
                                            "当前列表，点击切换宫格"
                                        }
                                    )
                                }
                            }
                        }

                        key("topbar_drawing_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = drawingAlpha }
                                    .zIndex(if (showDrawingActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                KigttsIconButton(
                                    onClick = { viewModel.rotateDrawingCanvasQuarterTurns(-1) },
                                    enabled = showDrawingActions
                                ) {
                                    MsIcon("rotate_left", contentDescription = "向左旋转画布")
                                }
                                KigttsIconButton(
                                    onClick = { viewModel.rotateDrawingCanvasQuarterTurns(1) },
                                    enabled = showDrawingActions
                                ) {
                                    MsIcon("rotate_right", contentDescription = "向右旋转画布")
                                }
                                KigttsIconButton(
                                    onClick = { viewModel.saveDrawingSnapshot() },
                                    enabled = showDrawingActions && viewModel.drawStrokes.isNotEmpty()
                                ) {
                                    MsIcon("save", contentDescription = "保存画板")
                                }
                            }
                        }

                        key("topbar_quick_card_main_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = quickCardMainAlpha }
                                    .zIndex(if (showQuickCardMainActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                KigttsIconButton(
                                    onClick = { quickCardActions?.onNew?.invoke() },
                                    enabled = showQuickCardMainActions && quickCardActions != null
                                ) {
                                    MsIcon("add", contentDescription = "新建名片")
                                }
                                KigttsIconButton(
                                    onClick = { quickCardActions?.onScan?.invoke() },
                                    enabled = showQuickCardMainActions && quickCardActions != null
                                ) {
                                    MsIcon("qr_code_scanner", contentDescription = "扫描二维码")
                                }
                            }
                        }

                        key("topbar_quick_card_editor_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = quickCardEditorAlpha }
                                    .zIndex(if (showQuickCardEditorActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                KigttsIconButton(
                                    onClick = { quickCardActions?.onCopy?.invoke() },
                                    enabled = showQuickCardEditorActions && quickCardActions?.canCopy == true
                                ) {
                                    MsIcon("content_copy", contentDescription = "复制名片")
                                }
                                KigttsIconButton(
                                    onClick = { quickCardActions?.onDelete?.invoke() },
                                    enabled = showQuickCardEditorActions && quickCardActions?.canDelete == true
                                ) {
                                    MsIcon("delete", contentDescription = "删除名片")
                                }
                            }
                        }

                        key("topbar_quick_card_sort_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = quickCardSortAlpha }
                                    .zIndex(if (showQuickCardSortActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (quickCardActions?.canClose == true) {
                                    KigttsIconButton(
                                        onClick = { quickCardActions.onDelete?.invoke() },
                                        enabled = showQuickCardSortActions && quickCardActions.canDelete
                                    ) {
                                        MsIcon("delete", contentDescription = "删除选中名片")
                                    }
                                    KigttsIconButton(
                                        onClick = { quickCardActions.onClose?.invoke() },
                                        enabled = showQuickCardSortActions
                                    ) {
                                        MsIcon("close", contentDescription = "结束选择模式")
                                    }
                                } else {
                                    KigttsIconButton(
                                        onClick = { quickCardActions?.onConfirm?.invoke() },
                                        enabled = showQuickCardSortActions && quickCardActions?.canConfirm == true
                                    ) {
                                        MsIcon("check", contentDescription = "保存排序并返回")
                                    }
                                }
                            }
                        }

                        key("topbar_quick_card_web_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = quickCardWebAlpha }
                                    .zIndex(if (showQuickCardWebActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box {
                                    KigttsIconButton(
                                        onClick = { quickCardWebMenuExpanded = true },
                                        enabled = showQuickCardWebActions
                                    ) {
                                        MsIcon("more_vert", contentDescription = "更多")
                                    }
                                    Md2AnimatedOptionMenu(
                                        expanded = showQuickCardWebActions && quickCardWebMenuExpanded,
                                        onDismissRequest = { quickCardWebMenuExpanded = false }
                                    ) {
                                        M2DropdownMenuItem(
                                            onClick = {
                                                quickCardWebMenuExpanded = false
                                                quickCardActions?.onWebReload?.invoke()
                                            },
                                            enabled = quickCardActions?.canWebReload == true
                                        ) {
                                            Text("刷新")
                                        }
                                        M2DropdownMenuItem(
                                            onClick = {
                                                quickCardWebMenuExpanded = false
                                                quickCardActions?.onWebBack?.invoke()
                                            },
                                            enabled = quickCardActions?.canWebBack == true
                                        ) {
                                            Text("返回上一页")
                                        }
                                        M2DropdownMenuItem(
                                            onClick = {
                                                quickCardWebMenuExpanded = false
                                                quickCardActions?.onWebForward?.invoke()
                                            },
                                            enabled = quickCardActions?.canWebForward == true
                                        ) {
                                            Text("返回下一页")
                                        }
                                        M2DropdownMenuItem(
                                            onClick = {
                                                quickCardWebMenuExpanded = false
                                                if (quickCardWebUrl.isBlank()) {
                                                    toast(context, "链接为空")
                                                } else {
                                                    openQuickCardLink(context, quickCardWebUrl)
                                                }
                                            }
                                        ) {
                                            Text("用浏览器打开")
                                        }
                                        M2DropdownMenuItem(
                                            onClick = {
                                                quickCardWebMenuExpanded = false
                                                if (quickCardWebUrl.isBlank()) {
                                                    toast(context, "链接为空")
                                                } else {
                                                    clipboard.setText(AnnotatedString(quickCardWebUrl))
                                                    toast(context, "已复制链接")
                                                }
                                            }
                                        ) {
                                            Text("复制链接")
                                        }
                                        M2DropdownMenuItem(
                                            onClick = {
                                                quickCardWebMenuExpanded = false
                                                if (quickCardWebUrl.isBlank()) {
                                                    toast(context, "链接为空")
                                                } else {
                                                    sharePlainText(context, quickCardWebUrl, "分享链接")
                                                }
                                            }
                                        ) {
                                            Text("分享")
                                        }
                                    }
                                }
                            }
                        }

                        key("topbar_voicepack_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = voicePackAlpha }
                                    .zIndex(if (showVoicePackActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                KigttsIconButton(
                                    onClick = {
                                        if (state.useBuiltinFileManager) {
                                            showBuiltinVoicePicker = true
                                        } else {
                                            voicePicker.launch("*/*")
                                        }
                                    },
                                    enabled = showVoicePackActions
                                ) {
                                    MsIcon("folder_open", contentDescription = "导入语音包")
                                }
                            }
                        }

                        key("topbar_settings_log_entry_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = settingsEntryAlpha }
                                    .zIndex(if (showSettingsEntryActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                KigttsIconButton(
                                    onClick = { settingsNavController.navigate(SettingsRoutes.Log) },
                                    enabled = showSettingsEntryActions
                                ) {
                                    MsIcon("article", contentDescription = "打开日志")
                                }
                            }
                        }

                        key("topbar_settings_log_actions_layer") {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .graphicsLayer { alpha = settingsLogAlpha }
                                    .zIndex(if (showSettingsLogActions) 2f else 0f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                KigttsIconButton(
                                    onClick = { settingsActions?.onRefresh?.invoke() },
                                    enabled = showSettingsLogActions && settingsActions != null
                                ) {
                                    MsIcon("refresh", contentDescription = "刷新日志")
                                }
                                KigttsIconButton(
                                    onClick = { settingsActions?.onCopy?.invoke() },
                                    enabled = showSettingsLogActions && settingsActions?.canCopy == true
                                ) {
                                    MsIcon("content_copy", contentDescription = "复制日志")
                                }
                                KigttsIconButton(
                                    onClick = { settingsActions?.onShare?.invoke() },
                                    enabled = showSettingsLogActions && settingsActions?.canShare == true
                                ) {
                                    MsIcon("share", contentDescription = "分享日志")
                                }
                            }
                        }
                    }
                },
                backgroundColor = Color.Transparent,
                contentColor = topBarContentColor,
                elevation = 0.dp
            )
        }
    }

    val fab: @Composable () -> Unit = {}

    val contentArea: @Composable (Modifier) -> Unit = { modifier ->
        Box(modifier = modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = basePage,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = fadeIn(animationSpec = tween(220, delayMillis = 80)) +
                                slideInVertically(
                                    initialOffsetY = { full -> full / 10 },
                                    animationSpec = tween(220, delayMillis = 80)
                                ),
                        initialContentExit = fadeOut(animationSpec = tween(90)) +
                                slideOutVertically(
                                    targetOffsetY = { full -> -full / 10 },
                                    animationSpec = tween(90)
                                )
                    )
                },
                label = "page_switch"
            ) { current ->
                when (current) {
                    pageOverlay -> FloatingOverlayScreen(
                        viewModel = viewModel,
                        state = state,
                        onOpenMainSettings = {
                            page = pageSettings
                            if (settingsRoute != SettingsRoutes.Main) {
                                settingsNavController.popBackStack(SettingsRoutes.Main, inclusive = false)
                            }
                        }
                    )
                    pageQuickSubtitle -> QuickSubtitleNavHost(
                        navController = quickSubtitleNavController,
                        viewModel = viewModel,
                        state = state,
                        onToggleMic = onToggleRun,
                        onPushToTalkPressStart = onPushToTalkPressStart,
                        onPushToTalkPressEnd = onPushToTalkPressEnd,
                        pttConfirmOwnedByMainPanel = pttConfirmOwnedByMainPanel,
                        onFloatingInputPreviewChange = { quickSubtitleFloatingInputPreview = it },
                        onOpenHistory = {
                            quickSubtitleNavController.navigate(QuickSubtitleRoutes.History) {
                                launchSingleTop = true
                            }
                        },
                        onEditorBatchTopBarActionsChange = { quickSubtitleEditorBatchTopBarActions = it },
                        fullscreenMode = quickSubtitleFullscreen && !quickSubtitleSubPageOpen,
                        forceLandscapeLayout = forceLandscapeContentLayout,
                        ultraSmallAdaptiveWindow = ultraSmallAdaptiveWindow
                    )
                    pageQuickCard -> QuickCardNavHost(
                        navController = quickCardNavController,
                        viewModel = viewModel,
                        onNavReady = { quickCardNavReady = true },
                        onTopBarActionsChange = { quickCardTopBarActions = it }
                    )
                    pageVoicePack -> VoicePackScreen(viewModel, state)
                    pageDrawing -> DrawingBoardScreen(
                        viewModel = viewModel,
                        fullscreen = drawingFullscreen,
                        onToggleFullscreen = { drawingFullscreen = !drawingFullscreen },
                        forceLandscapeLayout = forceLandscapeContentLayout
                    )
                    pageSoundboard -> SoundboardNavHost(
                        navController = soundboardNavController,
                        viewModel = viewModel,
                        state = state,
                        onEditorBatchTopBarActionsChange = { soundboardEditorBatchTopBarActions = it },
                        ultraSmallAdaptiveWindow = ultraSmallAdaptiveWindow,
                        forceLandscapeLayout = forceLandscapeContentLayout
                    )
                    pageSettings -> SettingsNavHost(
                        navController = settingsNavController,
                        viewModel = viewModel,
                        state = state,
                        onTopBarActionsChange = { logTopBarActions = it },
                        onOpenRecognitionResourceSources = { recognitionResourceSourceDialog = true },
                        onPickRecognitionResourcePackage = { showBuiltinRecognitionResourcePicker = true },
                        onDownloadRecognitionResources = { viewModel.downloadRecognitionResources() },
                        onOpenKokoroSources = { kokoroSourceDialog = true },
                        onPickKokoroVoicePackage = { showBuiltinKokoroVoicePicker = true },
                        onDownloadKokoroVoice = { viewModel.downloadKokoroVoice() },
                        onOpenKokoroVoiceSettings = { kokoroVoiceSettingsDialog = true }
                    )
                }
            }
            AnimatedVisibility(
                visible = showRunningStripPanel,
                modifier = Modifier
                    .matchParentSize()
                    .zIndex(1f),
                enter = fadeIn(animationSpec = tween(120)),
                exit = fadeOut(animationSpec = tween(90))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            runningStripCollapsed = true
                        }
                )
            }
            AnimatedVisibility(
                visible = showRunningStripPanel,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .zIndex(2f),
                enter = fadeIn(animationSpec = tween(120)) +
                        slideInVertically(
                            initialOffsetY = { full -> -full },
                            animationSpec = tween(220, easing = FastOutSlowInEasing)
                        ),
                exit = fadeOut(animationSpec = tween(90)) +
                        slideOutVertically(
                            targetOffsetY = { full -> -full },
                            animationSpec = tween(170, easing = FastOutSlowInEasing)
                        )
            ) {
                RunningStatusTopStrip(
                    viewModel = viewModel,
                    status = state.status,
                            pushToTalkMode = state.pushToTalkMode,
                            pushToTalkPressed = state.pushToTalkPressed,
                            ttsDisabled = state.ttsDisabled,
                            playbackGainPercent = state.playbackGainPercent,
                            preferredInputType = state.preferredInputType,
                            preferredOutputType = state.preferredOutputType,
                            inputDeviceLabel = state.inputDeviceLabel,
                            outputDeviceLabel = state.outputDeviceLabel,
                    onToggleCollapsed = { runningStripCollapsed = !runningStripCollapsed },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (realtimeConfirmOverlayEnabled) {
                QuickSubtitlePttConfirmOverlay(
                    visible = realtimeShowPttConfirmOverlay,
                    dragTarget = realtimePttDragTarget,
                    streamingText = state.pushToTalkStreamingText,
                    isLandscape = isLandscape,
                    compactPttSideButtonsMode = realtimeCompactPttSideButtonsMode,
                    showInputAction = false,
                    applyNavigationBarsPadding = false,
                    topRowBottomReservedOverride = realtimePttTopRowBottomReserved
                )

                AnimatedVisibility(
                    visible = realtimeShowPttConfirmOverlay,
                    modifier = Modifier
                        .zIndex(6.5f)
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .imePadding()
                        .padding(
                            start = realtimePttStatusStripStartInset,
                            end = realtimePttStatusStripAnimatedEndInsetWithBleed,
                            bottom = realtimePttStatusStripBottomInset
                        ),
                    enter = fadeIn(animationSpec = tween(140)),
                    exit = fadeOut(animationSpec = tween(110))
                ) {
                    Box(
                        modifier = Modifier.padding(
                            start = realtimePttStatusStripOuterBleed,
                            top = realtimePttStatusStripTopBleed,
                            end = realtimePttStatusStripOuterBleed,
                            bottom = realtimePttStatusStripBottomBleed
                        )
                    ) {
                        QuickSubtitlePttConfirmBottomStrip(
                            guideText = realtimePttGuideText,
                            reserveFabWidth = realtimePttStripFabReserveWidth,
                            stripHeight = realtimePttFabSize
                        )
                    }
                }

                QuickSubtitlePttCompactSideButtonsOverlay(
                    visible = realtimeShowPttConfirmOverlay && realtimeCompactPttSideButtonsMode,
                    dragTarget = realtimePttDragTarget,
                    fabSize = realtimePttFabSize,
                    fabEndInset = realtimePttFabEndInset,
                    fabBottomOffset = realtimePttFabBottomOffset,
                    showInputAction = false,
                    applyNavigationBarsPadding = false
                )
            }
        }
    }

    val drawingImmersive = drawingFullscreen && basePage == pageDrawing
    val quickSubtitleImmersive =
        quickSubtitleFullscreen && basePage == pageQuickSubtitle && !quickSubtitleSubPageOpen
    val fullScreenImmersive = drawingImmersive || quickSubtitleImmersive
    BackHandler(enabled = drawingImmersive) {
        drawingFullscreen = false
    }
    BackHandler(enabled = quickSubtitleImmersive) {
        quickSubtitleFullscreen = false
    }
    LaunchedEffect(fullScreenImmersive) {
        if (fullScreenImmersive) {
            drawerExpanded = false
            drawerState.close()
        }
    }
    LaunchedEffect(drawingImmersive, inMultiWindowMode) {
        val window = activity?.window ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (drawingImmersive && !inMultiWindowMode) {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.statusBars())
            AppLogger.i("AppScaffold.statusBars=hidden drawingImmersive=true")
        } else {
            controller.show(WindowInsetsCompat.Type.statusBars())
            AppLogger.i("AppScaffold.statusBars=shown drawingImmersive=false")
        }
    }
    val topBarVisible = !fullScreenImmersive
    val animatedPermanentRailWidth by animateDpAsState(
        targetValue = if (topBarVisible) permanentDrawerCollapsedWidth else 0.dp,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "permanent_drawer_rail_width"
    )
    val animatedContentStartPadding by animateDpAsState(
        targetValue = if (fullScreenImmersive) landscapeCutoutStart else 0.dp,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "content_start_padding"
    )
    Box(modifier = Modifier.fillMaxSize()) {
        if (usePermanentDrawer) {
            Scaffold(
                topBar = {
                    AnimatedVisibility(
                        visible = topBarVisible,
                        enter = fadeIn(animationSpec = tween(130)) +
                                expandVertically(
                                    expandFrom = Alignment.Top,
                                    animationSpec = tween(180, easing = FastOutSlowInEasing)
                                ),
                        exit = fadeOut(animationSpec = tween(100)) +
                                shrinkVertically(
                                    shrinkTowards = Alignment.Top,
                                    animationSpec = tween(140, easing = FastOutSlowInEasing)
                                )
                    ) {
                        topBar { drawerExpanded = !drawerExpanded }
                    }
                },
                floatingActionButton = fab,
                backgroundColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Surface(
                            modifier = Modifier
                                .width(animatedPermanentRailWidth)
                                .fillMaxHeight()
                                .zIndex(3f),
                            shape = RectangleShape,
                            color = MaterialTheme.colorScheme.surface,
                            elevation = if (animatedPermanentRailWidth > 0.dp) UiTokens.MenuElevation else 0.dp
                        ) {
                            if (animatedPermanentRailWidth > 0.5.dp) {
                                AppDrawerContent(
                                    items = drawerItems,
                                    page = drawerSelectedPage,
                                    expanded = false,
                                    applyStatusBarPadding = false,
                                    showHeader = false,
                                    showTopDivider = false,
                                    topInset = 8.dp,
                                    horizontalStartInset = landscapeChromeStartInset,
                                    onSelect = { page = it }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize())
                            }
                        }
                        contentArea(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .graphicsLayer { clip = true }
                                .zIndex(0f)
                                .padding(start = animatedContentStartPadding, end = landscapeChromeEndInset)
                        )
                    }

                    AnimatedVisibility(
                        visible = drawerExpanded && topBarVisible,
                        modifier = Modifier
                            .matchParentSize()
                            .zIndex(3f),
                        enter = fadeIn(animationSpec = tween(120)) +
                                androidx.compose.animation.slideInHorizontally(
                                    initialOffsetX = { -it / 6 },
                                    animationSpec = tween(120, easing = FastOutSlowInEasing)
                                ),
                        exit = fadeOut(animationSpec = tween(90)) +
                                androidx.compose.animation.slideOutHorizontally(
                                    targetOffsetX = { -it / 6 },
                                    animationSpec = tween(90, easing = FastOutSlowInEasing)
                                )
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Surface(
                                modifier = Modifier
                                    .width(permanentDrawerExpandedWidth)
                                    .fillMaxHeight()
                                    .zIndex(4f),
                                shape = RectangleShape,
                                color = MaterialTheme.colorScheme.surface,
                                elevation = UiTokens.MenuElevation
                            ) {
                                AppDrawerContent(
                                    items = drawerItems,
                                    page = drawerSelectedPage,
                                    expanded = true,
                                    applyStatusBarPadding = false,
                                    showHeader = false,
                                    showTopDivider = false,
                                    topInset = 8.dp,
                                    horizontalStartInset = landscapeChromeStartInset,
                                    onSelect = {
                                        page = it
                                        drawerExpanded = false
                                    }
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        drawerExpanded = false
                                        runningStripCollapsed = true
                                    }
                            )
                        }
                    }
                }
            }
        } else {
            ModalDrawer(
                drawerState = drawerState,
                gesturesEnabled = basePage != pageDrawing &&
                        !state.pushToTalkPressed &&
                        !quickCardMainOpen,
                drawerShape = RectangleShape,
                drawerBackgroundColor = Color.Transparent,
                drawerElevation = 0.dp,
                scrimColor = hiddenDrawerScrimColor,
                drawerContent = {
                    Row(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(hiddenDrawerSurfaceWidth)
                                .fillMaxHeight(),
                            shape = RectangleShape,
                            color = MaterialTheme.colorScheme.surface,
                            elevation = UiTokens.MenuElevation
                        ) {
                            AppDrawerContent(
                                items = drawerItems,
                                page = drawerSelectedPage,
                                expanded = true,
                                applyStatusBarPadding = !inMultiWindowMode,
                                showHeader = true,
                                showTopDivider = true,
                                topInset = 12.dp,
                                horizontalStartInset = landscapeChromeStartInset,
                                onSelect = {
                                    page = it
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    scope.launch { drawerState.close() }
                                }
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        AnimatedVisibility(
                            visible = topBarVisible,
                            enter = fadeIn(animationSpec = tween(130)) +
                                    expandVertically(
                                        expandFrom = Alignment.Top,
                                        animationSpec = tween(180, easing = FastOutSlowInEasing)
                                    ),
                            exit = fadeOut(animationSpec = tween(100)) +
                                    shrinkVertically(
                                        shrinkTowards = Alignment.Top,
                                        animationSpec = tween(140, easing = FastOutSlowInEasing)
                                    )
                        ) {
                            topBar { scope.launch { drawerState.open() } }
                        }
                    },
                    floatingActionButton = fab,
                    backgroundColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        contentArea(
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(start = landscapeChromeStartInset, end = landscapeChromeEndInset)
                        )
                    }
                }
        }
        QuickSubtitleFloatingInputPreviewOverlay(
            preview = if (
                basePage == pageQuickSubtitle &&
                quickSubtitleRoute == QuickSubtitleRoutes.Main
            ) {
                quickSubtitleFloatingInputPreview
            } else {
                null
            },
            textAlign = if (viewModel.quickSubtitleCentered) TextAlign.Center else TextAlign.Start,
            fontWeight = if (viewModel.quickSubtitleBold) FontWeight.Bold else FontWeight.Normal,
            maxFontSizeSp = viewModel.quickSubtitleFontSizeSp,
            autoFitEnabled = state.quickSubtitleAutoFit,
            rotated180 = false,
            startPadding = landscapeChromeStartInset + 16.dp,
            endPadding = landscapeChromeEndInset + 16.dp,
            topPadding = statusTopInset + 6.dp,
            modifier = Modifier
                .matchParentSize()
                .zIndex(20f)
        )
    }
}

@Composable
internal fun QuickSubtitleFloatingInputPreviewOverlay(
    preview: QuickSubtitleFloatingInputPreviewState?,
    textAlign: TextAlign,
    fontWeight: FontWeight,
    maxFontSizeSp: Float,
    autoFitEnabled: Boolean,
    rotated180: Boolean,
    startPadding: Dp,
    endPadding: Dp,
    topPadding: Dp,
    modifier: Modifier = Modifier
) {
    var retainedPreview by remember { mutableStateOf<QuickSubtitleFloatingInputPreviewState?>(null) }
    LaunchedEffect(preview) {
        if (preview != null) retainedPreview = preview
    }
    AnimatedVisibility(
        visible = preview != null,
        modifier = modifier.padding(
            start = startPadding,
            end = endPadding,
            top = topPadding,
            bottom = retainedPreview?.bottomPadding ?: 0.dp
        ),
        enter = fadeIn(animationSpec = tween(150)) +
            scaleIn(
                initialScale = 0.96f,
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            ),
        exit = fadeOut(animationSpec = tween(120)) +
            scaleOut(
                targetScale = 0.98f,
                animationSpec = tween(140, easing = FastOutSlowInEasing)
            )
    ) {
        val activePreview = retainedPreview
        if (activePreview != null) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .mdCenteredShadow(
                        shape = RoundedCornerShape(UiTokens.Radius),
                        shadowStyle = MdCardShadowStyle
                    ),
                shape = RoundedCornerShape(UiTokens.Radius),
                backgroundColor = md2ElevatedCardContainerColor(UiTokens.MenuElevation),
                elevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    AnimatedContent(
                        targetState = activePreview.text to activePreview.cursorIndex,
                        transitionSpec = {
                            ContentTransform(
                                targetContentEnter = fadeIn(initialAlpha = 0.45f, animationSpec = tween(140)),
                                initialContentExit = fadeOut(targetAlpha = 0.45f, animationSpec = tween(160)),
                                sizeTransform = null
                            )
                        },
                        label = "quick_subtitle_root_input_preview_text_change"
                    ) { (text, cursorIndex) ->
                        Crossfade(
                            targetState = rotated180,
                            animationSpec = tween(160),
                            label = "quick_subtitle_root_input_preview_rotation"
                        ) { rotated ->
                            QuickSubtitleAdaptiveText(
                                text = text,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = textAlign,
                                fontWeight = fontWeight,
                                maxFontSizeSp = maxFontSizeSp,
                                minFontSizeSp = 14f,
                                lineHeightMultiplier = 1.15f,
                                autoFitEnabled = autoFitEnabled,
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = if (rotated) {
                                    if (textAlign == TextAlign.Center) Alignment.BottomCenter else Alignment.BottomStart
                                } else {
                                    Alignment.TopStart
                                },
                                textRotationZ = if (rotated) 180f else 0f,
                                cursorIndex = cursorIndex,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun AppDrawerContent(
    items: List<DrawerItem>,
    page: Int,
    expanded: Boolean,
    applyStatusBarPadding: Boolean,
    showHeader: Boolean,
    showTopDivider: Boolean,
    topInset: Dp,
    horizontalStartInset: Dp = 0.dp,
    onSelect: (Int) -> Unit
) {
    val drawerLogoRes = if (currentAppDarkTheme()) R.drawable.logo_white else R.drawable.logo_black
    val performKeyHaptic = rememberKigttsKeyHaptic()
    val animatedItemStartPadding by animateDpAsState(
        targetValue = if (expanded) 16.dp else 27.dp,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "drawer_item_start_padding"
    )
    val animatedLabelAlpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "drawer_label_alpha"
    )
    val animatedLabelTranslateX by animateFloatAsState(
        targetValue = if (expanded) 0f else -8f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "drawer_label_tx"
    )
    val animatedLabelSpacer by animateDpAsState(
        targetValue = if (expanded) 12.dp else 0.dp,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "drawer_label_spacer"
    )
    val animatedLabelWidth by animateDpAsState(
        targetValue = if (expanded) 120.dp else 0.dp,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "drawer_label_width"
    )
    val itemStartPadding = animatedItemStartPadding
    val labelAlpha = animatedLabelAlpha
    val labelTranslateX = animatedLabelTranslateX
    val labelSpacer = animatedLabelSpacer
    val labelWidth = animatedLabelWidth

    Column(
        modifier = Modifier
            .then(if (applyStatusBarPadding) Modifier.statusBarsPadding() else Modifier)
            .fillMaxSize()
            .padding(start = horizontalStartInset)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(topInset))
        if (showHeader && expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = drawerLogoRes),
                    contentDescription = "KIGTTS",
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(32.dp)
                )
            }
        }
        if (showTopDivider) {
            Divider()
        }
        items.forEach { item ->
            val selected = page == item.page
            val interaction = remember { MutableInteractionSource() }
            val pressed by interaction.collectIsPressedAsState()
            val bg = when {
                pressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else -> Color.Transparent
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(bg)
                    .clickable(
                        interactionSource = interaction,
                        indication = rememberRipple(bounded = true)
                    ) {
                        performKeyHaptic()
                        onSelect(item.page)
                    }
                    .padding(start = itemStartPadding, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                MsIcon(name = item.icon, contentDescription = item.title)
                Spacer(Modifier.width(labelSpacer))
                Box(
                    modifier = Modifier
                        .width(labelWidth)
                        .graphicsLayer {
                            alpha = labelAlpha
                            translationX = labelTranslateX
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}


