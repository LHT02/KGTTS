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
internal fun QuickSubtitleNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    state: UiState,
    onToggleMic: () -> Unit,
    onPushToTalkPressStart: () -> Unit,
    onPushToTalkPressEnd: (PttConfirmReleaseAction) -> Unit,
    pttConfirmOwnedByMainPanel: Boolean,
    onFloatingInputPreviewChange: (QuickSubtitleFloatingInputPreviewState?) -> Unit,
    onOpenHistory: () -> Unit,
    onEditorBatchTopBarActionsChange: (EditorBatchTopBarActions?) -> Unit,
    fullscreenMode: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = QuickSubtitleRoutes.Main,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            if (initialState.destination.route == QuickSubtitleRoutes.Main &&
                targetState.destination.route == QuickSubtitleRoutes.Editor
            ) {
                fadeIn(animationSpec = tween(180)) +
                        androidx.compose.animation.slideInHorizontally(
                            initialOffsetX = { full -> full / 10 },
                            animationSpec = tween(180, easing = FastOutSlowInEasing)
                        )
            } else {
                fadeIn(animationSpec = tween(120))
            }
        },
        exitTransition = {
            if (initialState.destination.route == QuickSubtitleRoutes.Main &&
                targetState.destination.route == QuickSubtitleRoutes.Editor
            ) {
                fadeOut(animationSpec = tween(130)) +
                        androidx.compose.animation.slideOutHorizontally(
                            targetOffsetX = { full -> -full / 14 },
                            animationSpec = tween(130, easing = FastOutSlowInEasing)
                        )
            } else {
                fadeOut(animationSpec = tween(90))
            }
        },
        popEnterTransition = {
            if (initialState.destination.route == QuickSubtitleRoutes.Editor &&
                targetState.destination.route == QuickSubtitleRoutes.Main
            ) {
                fadeIn(animationSpec = tween(170)) +
                        androidx.compose.animation.slideInHorizontally(
                            initialOffsetX = { full -> -full / 12 },
                            animationSpec = tween(170, easing = FastOutSlowInEasing)
                        )
            } else {
                fadeIn(animationSpec = tween(120))
            }
        },
        popExitTransition = {
            if (initialState.destination.route == QuickSubtitleRoutes.Editor &&
                targetState.destination.route == QuickSubtitleRoutes.Main
            ) {
                fadeOut(animationSpec = tween(130)) +
                        androidx.compose.animation.slideOutHorizontally(
                            targetOffsetX = { full -> full / 16 },
                            animationSpec = tween(130, easing = FastOutSlowInEasing)
                        )
            } else {
                fadeOut(animationSpec = tween(90))
            }
        }
    ) {
        composable(QuickSubtitleRoutes.Main) {
            QuickSubtitleScreen(
                viewModel = viewModel,
                state = state,
                onToggleMic = onToggleMic,
                onPushToTalkPressStart = onPushToTalkPressStart,
                onPushToTalkPressEnd = onPushToTalkPressEnd,
                pttConfirmOwnedByMainPanel = pttConfirmOwnedByMainPanel,
                onFloatingInputPreviewChange = onFloatingInputPreviewChange,
                onOpenHistory = onOpenHistory,
                onOpenEditor = { navController.navigate(QuickSubtitleRoutes.Editor) },
                fullscreenMode = fullscreenMode
            )
        }
        composable(QuickSubtitleRoutes.Editor) {
            QuickSubtitleEditorScreen(
                viewModel = viewModel,
                onBatchTopBarActionsChange = onEditorBatchTopBarActionsChange,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(QuickSubtitleRoutes.History) {
            RealtimeScreen(viewModel)
        }
    }
}


@Composable
@OptIn(ExperimentalComposeUiApi::class)
internal fun QuickSubtitleMicFab(
    state: UiState,
    compactPttSideButtonsMode: Boolean = false,
    enableInputAction: Boolean = true,
    onToggleMic: () -> Unit,
    onPushToTalkPressStart: () -> Unit,
    onPushToTalkPressEnd: (PttConfirmReleaseAction) -> Unit,
    onPttDragTargetChanged: (PttConfirmDragTarget) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var pushToTalkPressed by remember(state.pushToTalkMode) { mutableStateOf(false) }
    val pttInteractionSource = remember { MutableInteractionSource() }
    var pttPress by remember { mutableStateOf<PressInteraction.Press?>(null) }
    val density = LocalDensity.current
    // Zone split (matching the 3-zone behavior):
    // 1) Bottom zone: release => send to subtitle
    // 2) Top-left large zone: release => send to input box
    // 3) Top-right narrow zone: release => cancel
    val topZoneTriggerPx = remember(density) { with(density) { 56.dp.toPx() } }
    val cancelZoneLeftBiasPx = remember(density) { with(density) { 12.dp.toPx() } }
    val sideZoneTriggerPx = remember(density) { with(density) { 52.dp.toPx() } }
    val sideZoneVerticalTolerancePx = remember(density) { with(density) { 56.dp.toPx() } }
    var downX by remember { mutableFloatStateOf(0f) }
    var downY by remember { mutableFloatStateOf(0f) }
    var dragTarget by remember { mutableStateOf(PttConfirmDragTarget.DefaultSend) }
    fun resolveDragTarget(x: Float, y: Float): PttConfirmDragTarget {
        if (!state.pushToTalkConfirmInputMode) return PttConfirmDragTarget.DefaultSend
        val dx = x - downX
        val dy = y - downY
        if (compactPttSideButtonsMode) {
            val inSideBand = kotlin.math.abs(dy) <= sideZoneVerticalTolerancePx
            return when {
                inSideBand && dx >= sideZoneTriggerPx -> PttConfirmDragTarget.Cancel
                enableInputAction && inSideBand && dx <= -sideZoneTriggerPx -> PttConfirmDragTarget.ToInput
                else -> PttConfirmDragTarget.DefaultSend
            }
        }
        if (dy >= -topZoneTriggerPx) {
            return PttConfirmDragTarget.DefaultSend
        }
        // In top area:
        // - a narrow band near the right side (and around cancel button's left side) => cancel
        // - all remaining left area => send to input box
        return if (dx >= -cancelZoneLeftBiasPx) {
            PttConfirmDragTarget.Cancel
        } else if (enableInputAction) {
            PttConfirmDragTarget.ToInput
        } else {
            PttConfirmDragTarget.DefaultSend
        }
    }
    val pttModifier = if (state.pushToTalkMode) {
        modifier
            .pointerInteropFilter { event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        pushToTalkPressed = true
                        downX = event.x
                        downY = event.y
                        dragTarget = PttConfirmDragTarget.DefaultSend
                        onPttDragTargetChanged(PttConfirmDragTarget.DefaultSend)
                        onPushToTalkPressStart()
                        val press = PressInteraction.Press(Offset(event.x, event.y))
                        pttPress = press
                        pttInteractionSource.tryEmit(press)
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val nextTarget = resolveDragTarget(event.x, event.y)
                        if (nextTarget != dragTarget) {
                            dragTarget = nextTarget
                            onPttDragTargetChanged(nextTarget)
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val nextTarget = resolveDragTarget(event.x, event.y)
                        if (nextTarget != dragTarget) {
                            dragTarget = nextTarget
                            onPttDragTargetChanged(nextTarget)
                        }
                        if (pushToTalkPressed) {
                            pushToTalkPressed = false
                            val releaseAction = when (dragTarget) {
                                PttConfirmDragTarget.DefaultSend -> PttConfirmReleaseAction.SendToSubtitle
                                PttConfirmDragTarget.ToInput -> {
                                    if (enableInputAction) PttConfirmReleaseAction.SendToInput
                                    else PttConfirmReleaseAction.SendToSubtitle
                                }
                                PttConfirmDragTarget.Cancel -> PttConfirmReleaseAction.Cancel
                            }
                            onPushToTalkPressEnd(releaseAction)
                        }
                        dragTarget = PttConfirmDragTarget.DefaultSend
                        onPttDragTargetChanged(PttConfirmDragTarget.DefaultSend)
                        pttPress?.let { press ->
                            pttInteractionSource.tryEmit(PressInteraction.Release(press))
                        }
                        pttPress = null
                        true
                    }
                    MotionEvent.ACTION_CANCEL,
                    MotionEvent.ACTION_OUTSIDE -> {
                        if (pushToTalkPressed) {
                            pushToTalkPressed = false
                            onPushToTalkPressEnd(PttConfirmReleaseAction.Cancel)
                        }
                        dragTarget = PttConfirmDragTarget.DefaultSend
                        onPttDragTargetChanged(PttConfirmDragTarget.DefaultSend)
                        pttPress?.let { press ->
                            pttInteractionSource.tryEmit(PressInteraction.Cancel(press))
                        }
                        pttPress = null
                        true
                    }
                    else -> true
                }
            }
    } else {
        modifier
    }
    FloatingActionButton(
        onClick = if (state.pushToTalkMode) ({}) else onToggleMic,
        modifier = pttModifier.mdCenteredShadow(shape = CircleShape, shadowStyle = MdFabShadowStyle),
        interactionSource = pttInteractionSource,
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        if (state.pushToTalkMode) {
            Crossfade(
                targetState = pushToTalkPressed,
                animationSpec = tween(durationMillis = 180),
                label = "quick_subtitle_ptt_fab_icon"
            ) { pressed ->
                MsIcon(
                    name = if (pressed) "settings_voice" else "mic",
                    contentDescription = if (pressed) "按住说话中" else "按住说话",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            MsIcon(
                name = if (state.running) "stop" else "play_arrow",
                contentDescription = if (state.running) "关闭麦克风" else "开启麦克风",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

enum class QuickSubtitleListPopupLayout {
    Grid,
    List
}

@Composable
internal fun QuickSubtitlePopupItem(
    text: String,
    grid: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(if (grid) 76.dp else 64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(UiTokens.Radius),
        backgroundColor = md2CardContainerColor(),
        elevation = UiTokens.CardElevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                maxLines = if (grid) 2 else 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
internal fun QuickSubtitleListPopupTabs(
    groups: List<QuickSubtitleGroup>,
    selectedIndex: Int,
    vertical: Boolean,
    showVerticalLabels: Boolean = false,
    layoutMode: QuickSubtitleListPopupLayout,
    onSelectGroup: (Int) -> Unit,
    onToggleLayout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(UiTokens.Radius),
        backgroundColor = md2CardContainerColor(),
        elevation = UiTokens.CardElevation
    ) {
        if (vertical) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 3.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    groups.forEachIndexed { index, group ->
                        val selected = selectedIndex == index
                        val title = group.title.ifBlank { "未命名分组" }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(UiTokens.Radius))
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                    else Color.Transparent
                                )
                                .clickable { onSelectGroup(index) },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = if (showVerticalLabels) {
                                Arrangement.spacedBy(8.dp)
                            } else {
                                Arrangement.Center
                            }
                        ) {
                            if (showVerticalLabels) {
                                Spacer(Modifier.width(8.dp))
                            }
                            MsIcon(
                                name = group.icon,
                                contentDescription = title
                            )
                            if (showVerticalLabels) {
                                Text(
                                    text = title,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 8.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.28f))
                )
                KigttsIconButton(
                    onClick = onToggleLayout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    MsIcon(
                        name = if (layoutMode == QuickSubtitleListPopupLayout.Grid) "grid_view" else "view_list",
                        contentDescription = if (layoutMode == QuickSubtitleListPopupLayout.Grid) "当前宫格，点击切换列表" else "当前列表，点击切换宫格"
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    groups.forEachIndexed { index, group ->
                        val selected = selectedIndex == index
                        Row(
                            modifier = Modifier
                                .height(44.dp)
                                .clip(RoundedCornerShape(UiTokens.Radius))
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                    else Color.Transparent
                                )
                                .clickable { onSelectGroup(index) }
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val title = group.title.ifBlank { "未命名分组" }
                            MsIcon(group.icon, contentDescription = title)
                            Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(Modifier.width(2.dp))
                    }
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(34.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.28f))
                )
                KigttsIconButton(
                    onClick = onToggleLayout,
                    modifier = Modifier
                        .width(52.dp)
                        .fillMaxHeight()
                ) {
                    MsIcon(
                        name = if (layoutMode == QuickSubtitleListPopupLayout.Grid) "grid_view" else "view_list",
                        contentDescription = if (layoutMode == QuickSubtitleListPopupLayout.Grid) "当前宫格，点击切换列表" else "当前列表，点击切换宫格"
                    )
                }
            }
        }
    }
}

@Composable
internal fun QuickSubtitleListDialog(
    groups: List<QuickSubtitleGroup>,
    initialGroupIndex: Int,
    layoutMode: QuickSubtitleListPopupLayout,
    forceFullWidthTabsOnPhone: Boolean,
    onLayoutModeChange: (QuickSubtitleListPopupLayout) -> Unit,
    onSelectGroup: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    if (groups.isEmpty()) return
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenLongSideDp = maxOf(configuration.screenWidthDp, configuration.screenHeightDp)
    val screenShortSideDp = minOf(configuration.screenWidthDp, configuration.screenHeightDp)
    val phoneUa = screenShortSideDp < 600 || screenLongSideDp < 900
    val useFullWidthLandscapeTabs = isLandscape && (!phoneUa || forceFullWidthTabsOnPhone)
    val landscapeTabRailWidth = if (useFullWidthLandscapeTabs) 136.dp else 58.dp
    val performKeyHaptic = rememberKigttsKeyHaptic()
    val popupGroupHintState = rememberGroupSwitchHintState()
    var selectedGroupIndex by remember(groups, initialGroupIndex) {
        mutableIntStateOf(initialGroupIndex.coerceIn(0, groups.lastIndex))
    }
    val content: @Composable (Modifier) -> Unit = { modifier ->
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2CardContainerColor(),
            elevation = UiTokens.CardElevation
        ) {
            AnimatedContent(
                targetState = selectedGroupIndex to layoutMode,
                transitionSpec = {
                    if (initialState.first == targetState.first && initialState.second != targetState.second) {
                        ContentTransform(
                            targetContentEnter = fadeIn(animationSpec = tween(150)),
                            initialContentExit = fadeOut(animationSpec = tween(120)),
                            sizeTransform = null
                        )
                    } else {
                        quickSubtitlePopupGroupSwitchTransform(
                            initialIndex = initialState.first,
                            targetIndex = targetState.first,
                            isLandscape = isLandscape
                        )
                    }
                },
                label = "quick_subtitle_list_popup_group_switch"
            ) { (targetGroupIndex, targetLayoutMode) ->
                val targetItems = groups.getOrNull(targetGroupIndex)?.items.orEmpty()
                val grid = targetLayoutMode == QuickSubtitleListPopupLayout.Grid
                if (targetItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "当前分组暂无快捷文本",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (grid) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(138.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = targetItems.size,
                            key = { index -> "${targetGroupIndex}_${index}_${targetItems[index]}" }
                        ) { index ->
                            val text = targetItems[index]
                            QuickSubtitlePopupItem(
                                text = text,
                                grid = true,
                                onClick = {
                                    performKeyHaptic()
                                    onSubmit(text)
                                    onDismiss()
                                }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = targetItems,
                            key = { index, text -> "${targetGroupIndex}_${index}_$text" }
                        ) { _, text ->
                            QuickSubtitlePopupItem(
                                text = text,
                                grid = false,
                                onClick = {
                                    performKeyHaptic()
                                    onSubmit(text)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.36f))
                .clickable(onClick = onDismiss)
                .padding(
                    horizontal = if (isLandscape) 26.dp else 16.dp,
                    vertical = if (isLandscape) 18.dp else 26.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = if (isLandscape) 760.dp else 520.dp)
                    .heightIn(max = if (isLandscape) 360.dp else 560.dp)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        content(Modifier.weight(1f).fillMaxHeight())
                        QuickSubtitleListPopupTabs(
                            groups = groups,
                            selectedIndex = selectedGroupIndex,
                            vertical = true,
                            showVerticalLabels = useFullWidthLandscapeTabs,
                            layoutMode = layoutMode,
                            onSelectGroup = {
                                performKeyHaptic()
                                if (it != selectedGroupIndex) {
                                    popupGroupHintState.show(groups[it].title.ifBlank { "未命名分组" })
                                }
                                selectedGroupIndex = it
                                onSelectGroup(it)
                            },
                            onToggleLayout = {
                                performKeyHaptic()
                                onLayoutModeChange(
                                    if (layoutMode == QuickSubtitleListPopupLayout.Grid) QuickSubtitleListPopupLayout.List
                                    else QuickSubtitleListPopupLayout.Grid
                                )
                            },
                            modifier = Modifier
                                .width(landscapeTabRailWidth)
                                .fillMaxHeight()
                        )
                    }
                    GroupSwitchHintCard(
                        state = popupGroupHintState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = landscapeTabRailWidth + 10.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        content(Modifier.weight(1f).fillMaxWidth())
                        QuickSubtitleListPopupTabs(
                            groups = groups,
                            selectedIndex = selectedGroupIndex,
                            vertical = false,
                            layoutMode = layoutMode,
                            onSelectGroup = {
                                performKeyHaptic()
                                selectedGroupIndex = it
                                onSelectGroup(it)
                            },
                            onToggleLayout = {
                                performKeyHaptic()
                                onLayoutModeChange(
                                    if (layoutMode == QuickSubtitleListPopupLayout.Grid) QuickSubtitleListPopupLayout.List
                                    else QuickSubtitleListPopupLayout.Grid
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

internal fun quickSubtitlePopupGroupSwitchTransform(
    initialIndex: Int,
    targetIndex: Int,
    isLandscape: Boolean
): ContentTransform {
    val forward = targetIndex >= initialIndex
    return if (isLandscape) {
        ContentTransform(
            targetContentEnter = fadeIn(animationSpec = tween(190)) +
                slideInVertically(
                    initialOffsetY = { full ->
                        val d = kotlin.math.min(full / 3, 54)
                        if (forward) d else -d
                    },
                    animationSpec = tween(210, easing = FastOutSlowInEasing)
                ),
            initialContentExit = fadeOut(animationSpec = tween(150)) +
                slideOutVertically(
                    targetOffsetY = { full ->
                        val d = kotlin.math.min(full / 4, 40)
                        if (forward) -d else d
                    },
                    animationSpec = tween(170, easing = FastOutSlowInEasing)
                ),
            sizeTransform = null
        )
    } else {
        ContentTransform(
            targetContentEnter = fadeIn(animationSpec = tween(190)) +
                slideInHorizontally(
                    initialOffsetX = { full ->
                        val d = kotlin.math.min(full / 3, 120)
                        if (forward) d else -d
                    },
                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                ),
            initialContentExit = fadeOut(animationSpec = tween(150)) +
                slideOutHorizontally(
                    targetOffsetX = { full ->
                        val d = kotlin.math.min(full / 4, 88)
                        if (forward) -d else d
                    },
                    animationSpec = tween(180, easing = FastOutSlowInEasing)
                ),
            sizeTransform = null
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
fun QuickSubtitleScreen(
    viewModel: MainViewModel,
    state: UiState,
    onToggleMic: () -> Unit,
    onPushToTalkPressStart: () -> Unit,
    onPushToTalkPressEnd: (PttConfirmReleaseAction) -> Unit,
    pttConfirmOwnedByMainPanel: Boolean,
    onFloatingInputPreviewChange: (QuickSubtitleFloatingInputPreviewState?) -> Unit = {},
    onOpenHistory: () -> Unit,
    onOpenEditor: () -> Unit,
    fullscreenMode: Boolean
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val groups = viewModel.quickSubtitleGroups
    val selectedGroupIndex = viewModel.currentQuickSubtitleGroupIndex().coerceIn(0, groups.lastIndex.coerceAtLeast(0))
    val quickItemsScrollState = rememberScrollState()
    val subtitleText = viewModel.quickSubtitleCurrentText
    val subtitleFontSizeMax = if (state.quickSubtitleAllowLargeFont) 800f else 96f
    val subtitleSize = viewModel.quickSubtitleFontSizeSp.coerceIn(28f, subtitleFontSizeMax)
    val fullscreenPreviewFontSizeMax = if (state.quickSubtitleAllowLargeFont) 800f else 140f
    val subtitleBold = viewModel.quickSubtitleBold
    val subtitleCentered = viewModel.quickSubtitleCentered
    val subtitleRotated180 = viewModel.quickSubtitleRotated180
    val subtitleTextColor = if (subtitleText == QUICK_SUBTITLE_CLEARED_HINT) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val subtitleAlign = if (subtitleCentered) TextAlign.Center else TextAlign.Start
    val inputText = viewModel.quickSubtitleInputText
    val playOnSend = viewModel.quickSubtitlePlayOnSend
    val quickInputCollapsed = viewModel.quickSubtitleInputCollapsed
    val subtitleFullscreenDialogVisible = viewModel.quickSubtitlePreviewVisible
    val quickSubtitleAutoFit = state.quickSubtitleAutoFit
    val quickSubtitleContentRevision = viewModel.quickSubtitleContentRevision
    val useCompactQuickTextControls = state.quickSubtitleCompactControls && !isLandscape
    val showQuickSubtitleActionButtons = viewModel.quickSubtitleShowActionButtons
    val density = LocalDensity.current
    val actionPanelToggleIcon =
        if (showQuickSubtitleActionButtons) {
            "search"
        } else if (isLandscape) {
            "more_vert"
        } else {
            "more_horiz"
        }
    val performKeyHaptic = rememberKigttsKeyHaptic()
    val copySubtitleText = {
        performKeyHaptic()
        val content = subtitleText.trim()
        if (content.isNotEmpty()) {
            clipboard.setText(AnnotatedString(content))
            toast(context, "已复制")
        }
    }
    val addCurrentTextToQuickItems: (Int) -> Unit = { groupIndex ->
        viewModel.addQuickSubtitleItem(groupIndex = groupIndex, value = subtitleText)
        toast(context, "已新增快捷文本")
    }
    val actionPanelToggleDescription =
        if (showQuickSubtitleActionButtons) "切换到字体缩放" else "切换到快捷操作"
    val portraitSubtitleControlAreaHeight = 48.dp
    val portraitSubtitleControlBaselineOffset = (-6).dp
    val compactQuickGroupSwipeThresholdPx = with(density) { 18.dp.toPx() }
    var compactQuickGroupSuppressAnimation by remember { mutableStateOf(false) }
    val currentCompactSelectedGroupIndex by rememberUpdatedState(selectedGroupIndex)
    val groupHintState = rememberGroupSwitchHintState()
    fun selectQuickSubtitleGroupWithHint(index: Int, holdHintUntilRelease: Boolean = false) {
        if (index !in groups.indices) return
        if (index != selectedGroupIndex) {
            groupHintState.show(groups[index].title.ifBlank { "未命名分组" }, holdHintUntilRelease)
        }
        viewModel.selectQuickSubtitleGroup(index)
    }
    val rotatedSubtitleText: @Composable (
        text: AnnotatedString,
        color: Color,
        maxFontSizeSp: Float,
        minFontSizeSp: Float,
        lineHeightMultiplier: Float,
        modifier: Modifier,
        cursorIndex: Int?,
        rotateEnabled: Boolean
    ) -> Unit = { text, color, maxFontSizeSp, minFontSizeSp, lineHeightMultiplier, modifier, cursorIndex, rotateEnabled ->
        Crossfade(
            targetState = subtitleRotated180 && rotateEnabled,
            animationSpec = tween(160),
            label = "quick_subtitle_rotation_fade"
        ) { rotated ->
            QuickSubtitleAdaptiveText(
                text = text,
                color = color,
                textAlign = subtitleAlign,
                fontWeight = if (subtitleBold) FontWeight.Bold else FontWeight.Normal,
                maxFontSizeSp = maxFontSizeSp,
                minFontSizeSp = minFontSizeSp,
                lineHeightMultiplier = lineHeightMultiplier,
                autoFitEnabled = quickSubtitleAutoFit,
                modifier = modifier,
                contentAlignment = if (rotated) {
                    if (subtitleCentered) Alignment.BottomCenter else Alignment.BottomStart
                } else {
                    Alignment.TopStart
                },
                textRotationZ = if (rotated) 180f else 0f,
                cursorIndex = cursorIndex,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        }
    }
    val subtitleActionButtonsColumn: @Composable (Modifier) -> Unit = { modifier ->
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Md2IconButton(
                icon = "format_bold",
                contentDescription = if (subtitleBold) "关闭粗体" else "开启粗体",
                onClick = { viewModel.updateQuickSubtitleBold(!subtitleBold) }
            )
            Md2IconButton(
                icon = if (subtitleCentered) "format_align_center" else "format_align_left",
                contentDescription = if (subtitleCentered) "左对齐文本" else "居中文本",
                onClick = { viewModel.updateQuickSubtitleCentered(!subtitleCentered) }
            )
            Md2IconButton(
                icon = "swap_vert",
                contentDescription = if (subtitleRotated180) "恢复字幕方向" else "倒置字幕",
                onClick = { viewModel.updateQuickSubtitleRotated180(!subtitleRotated180) }
            )
            Md2IconButton(
                icon = "cleaning_services",
                contentDescription = "清屏",
                onClick = { viewModel.clearQuickSubtitleText() }
            )
            Md2IconButton(
                icon = "history",
                contentDescription = "历史记录",
                onClick = onOpenHistory
            )
        }
    }
    val subtitleActionButtonsRow: @Composable (Modifier) -> Unit = { modifier ->
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start
        ) {
            Md2IconButton(
                icon = "format_bold",
                contentDescription = if (subtitleBold) "关闭粗体" else "开启粗体",
                onClick = { viewModel.updateQuickSubtitleBold(!subtitleBold) }
            )
            Md2IconButton(
                icon = if (subtitleCentered) "format_align_center" else "format_align_left",
                contentDescription = if (subtitleCentered) "左对齐文本" else "居中文本",
                onClick = { viewModel.updateQuickSubtitleCentered(!subtitleCentered) }
            )
            Md2IconButton(
                icon = "swap_vert",
                contentDescription = if (subtitleRotated180) "恢复字幕方向" else "倒置字幕",
                onClick = { viewModel.updateQuickSubtitleRotated180(!subtitleRotated180) }
            )
            Md2IconButton(
                icon = "cleaning_services",
                contentDescription = "清屏",
                onClick = { viewModel.clearQuickSubtitleText() }
            )
            Md2IconButton(
                icon = "history",
                contentDescription = "历史记录",
                onClick = onOpenHistory
            )
        }
    }
    var pttDragTarget by remember { mutableStateOf(PttConfirmDragTarget.DefaultSend) }
    val showPttConfirmOverlay =
        state.pushToTalkMode &&
        state.pushToTalkConfirmInputMode &&
        state.pushToTalkPressed &&
        pttConfirmOwnedByMainPanel
    val useFloatingFabPortrait = !isLandscape
    val useFloatingFabLandscapeOverlay =
        isLandscape &&
        state.pushToTalkMode &&
        state.pushToTalkConfirmInputMode
    val useOverlayFab = useFloatingFabPortrait || useFloatingFabLandscapeOverlay
    val pttFabSize = if (isLandscape) 48.dp else 56.dp
    val pttFabEndInset = if (isLandscape) 64.dp else 20.dp
    val pttOverlayBottomOffset = if (isLandscape) 0.dp else 80.dp
    val pttFabBottomOffset = if (isLandscape) 12.dp else pttOverlayBottomOffset
    val pttStatusStripBottomOffset = pttFabBottomOffset
    val pttStatusStripBottomBleed = if (isLandscape) 12.dp else 14.dp
    val compactModeDetectionEnabled =
        isLandscape && state.pushToTalkMode && state.pushToTalkConfirmInputMode
    val pttImeBottomInset =
        if (compactModeDetectionEnabled) WindowInsets.ime.asPaddingValues().calculateBottomPadding() else 0.dp
    val pttNavBottomInset =
        if (compactModeDetectionEnabled) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() else 0.dp
    val pttBottomObstructionInset =
        if (pttImeBottomInset > pttNavBottomInset) pttImeBottomInset else pttNavBottomInset
    val pttImeVisible = pttImeBottomInset > 0.dp
    val pttPendingText = state.pushToTalkStreamingText.ifBlank { "正在识别..." }
    val pttTopButtonsRequiredHeight = 96.dp
    val pttTopEstimatedAvailableHeight =
        configuration.screenHeightDp.dp - pttBottomObstructionInset -
            (pttFabSize + pttFabBottomOffset + pttStatusStripBottomBleed + 72.dp)
    val compactPttSideButtonsMode =
        compactModeDetectionEnabled && (pttImeVisible || pttTopEstimatedAvailableHeight < pttTopButtonsRequiredHeight)
    val pttGuideText = when (pttDragTarget) {
        PttConfirmDragTarget.DefaultSend ->
            if (compactPttSideButtonsMode) pttPendingText else "松开手指上屏"
        PttConfirmDragTarget.ToInput -> "松开输入到文本框"
        PttConfirmDragTarget.Cancel -> "松开取消发送"
    }
    val pttStripFabReserveWidth = if (useOverlayFab) pttFabSize else 0.dp
    val pttStatusStripEndInset = if (useOverlayFab) pttFabEndInset else 10.dp
    val pttStatusStripAnchorEndInset = pttStatusStripEndInset + (pttFabSize / 2)
    val pttStatusStripOuterBleed = 12.dp
    val pttStatusStripAnimatedEndInset by animateDpAsState(
        targetValue = if (showPttConfirmOverlay) pttStatusStripEndInset else pttStatusStripAnchorEndInset,
        animationSpec = if (showPttConfirmOverlay) {
            tween(durationMillis = 220, easing = FastOutSlowInEasing)
        } else {
            tween(durationMillis = 180, easing = FastOutSlowInEasing)
        },
        label = "ptt_status_strip_end_inset"
    )
    val pttStatusStripStartInset = (10.dp - pttStatusStripOuterBleed).coerceAtLeast(0.dp)
    val pttStatusStripTopBleed = (pttStatusStripOuterBleed - 4.dp).coerceAtLeast(0.dp)
    val pttStatusStripAnimatedEndInsetWithBleed =
        (pttStatusStripAnimatedEndInset - pttStatusStripOuterBleed).coerceAtLeast(0.dp)
    val pttStatusStripBottomInset = (pttStatusStripBottomOffset - pttStatusStripBottomBleed).coerceAtLeast(0.dp)
    LaunchedEffect(showPttConfirmOverlay) {
        if (!showPttConfirmOverlay) {
            pttDragTarget = PttConfirmDragTarget.DefaultSend
        }
    }
    var inputFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = inputText,
                selection = TextRange(inputText.length)
            )
        )
    }
    var inputFieldFocused by remember { mutableStateOf(false) }
    var keyboardSeenWhileInputFocused by remember { mutableStateOf(false) }
    var bottomInputBarHeightPx by remember { mutableIntStateOf(0) }
    var inputPreviewBlockedRevision by remember { mutableLongStateOf(Long.MIN_VALUE) }
    val imeBottomInset = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val keyboardVisible = imeBottomInset > 0.dp
    val bottomInputBarHeight = with(density) { bottomInputBarHeightPx.toDp() }
    val inputTextHasContent = inputFieldValue.text.isNotEmpty()
    LaunchedEffect(quickSubtitleContentRevision) {
        if (quickSubtitleContentRevision > 0L && inputTextHasContent) {
            inputPreviewBlockedRevision = quickSubtitleContentRevision
        }
    }
    LaunchedEffect(inputFieldValue.text) {
        if (inputFieldValue.text.isNotEmpty()) {
            inputPreviewBlockedRevision = Long.MIN_VALUE
        }
    }
    val inputPreviewAllowed =
        inputTextHasContent && inputPreviewBlockedRevision != quickSubtitleContentRevision
    val persistentInputPreviewActive =
        state.quickSubtitleKeepInputPreview && inputPreviewAllowed && !inputFieldFocused
    val editingInputPreviewActive = inputPreviewAllowed && inputFieldFocused
    val screenLongSideDp = maxOf(configuration.screenWidthDp, configuration.screenHeightDp)
    val screenShortSideDp = minOf(configuration.screenWidthDp, configuration.screenHeightDp)
    val portraitKeyboardAvailableHeight =
        (configuration.screenHeightDp.dp - imeBottomInset - bottomInputBarHeight - 24.dp)
            .coerceAtLeast(0.dp)
    val phoneUa =
        screenShortSideDp < 600 ||
        screenLongSideDp < 900 ||
        (!isLandscape && keyboardVisible && portraitKeyboardAvailableHeight < 620.dp)
    val landscapePhoneInputPreviewMode = isLandscape && phoneUa && configuration.screenHeightDp < 500
    val portraitPhoneKeyboardInputMode = !isLandscape && phoneUa && keyboardVisible && inputFieldFocused
    val floatingInputPreviewActive = editingInputPreviewActive && landscapePhoneInputPreviewMode
    val inlineInputPreviewActive =
        (editingInputPreviewActive && !landscapePhoneInputPreviewMode) ||
            (persistentInputPreviewActive && !floatingInputPreviewActive)
    val inputPreviewCursorIndex = inputFieldValue.selection.start.coerceIn(0, inputFieldValue.text.length)
    val inputPreviewText = remember(inputFieldValue.text) { AnnotatedString(inputFieldValue.text) }
    val displayedSubtitleText = if (inlineInputPreviewActive) inputPreviewText else AnnotatedString(subtitleText)
    val shouldHideSubtitleControlsForInput =
        !floatingInputPreviewActive && (editingInputPreviewActive && phoneUa || portraitPhoneKeyboardInputMode)
    val subtitleControlsVisible = floatingInputPreviewActive || !shouldHideSubtitleControlsForInput
    LaunchedEffect(inputText) {
        if (inputText != inputFieldValue.text) {
            inputFieldValue = TextFieldValue(
                text = inputText,
                selection = TextRange(inputText.length)
            )
        }
    }
    LaunchedEffect(inputFieldFocused) {
        if (!inputFieldFocused) {
            keyboardSeenWhileInputFocused = false
        }
    }
    LaunchedEffect(keyboardVisible, inputFieldFocused, keyboardSeenWhileInputFocused) {
        if (!inputFieldFocused) return@LaunchedEffect
        if (keyboardVisible) {
            keyboardSeenWhileInputFocused = true
        } else if (keyboardSeenWhileInputFocused) {
            focusManager.clearFocus(force = true)
            inputFieldFocused = false
            keyboardSeenWhileInputFocused = false
        }
    }
    val hasVoice = state.voiceDir != null
    var quickSubtitleListDialogVisible by rememberSaveable { mutableStateOf(false) }
    val quickSubtitleListDialogLayoutMode = viewModel.quickSubtitleListPopupLayout
    val openQuickSubtitleListDialog = {
        performKeyHaptic()
        quickSubtitleListDialogVisible = true
    }
    val statusBarInsetTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarsPadding = WindowInsets.navigationBars.asPaddingValues()
    val navBarsBottomInset = navBarsPadding.calculateBottomPadding()
    val quickSubtitleTopBlankTarget =
        if (fullscreenMode) (statusBarInsetTop + UiTokens.PageTopBlank) else UiTokens.PageTopBlank
    val quickSubtitleTopBlank by animateDpAsState(
        targetValue = quickSubtitleTopBlankTarget,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "quick_subtitle_top_blank"
    )
    val landscapeQuickPanelWidth = 220.dp
    val landscapeQuickPanelGap = 8.dp
    val quickSubtitleBottomBlankBase = if (isLandscape) {
        UiTokens.PageBottomBlank - 12.dp + navBarsBottomInset
    } else {
        UiTokens.PageBottomBlank + 50.dp + navBarsBottomInset
    }
    val keyboardRaisedBottomBlank = imeBottomInset + bottomInputBarHeight + 8.dp
    val quickSubtitleBottomBlankTarget = if (
        keyboardVisible &&
        keyboardRaisedBottomBlank > quickSubtitleBottomBlankBase
    ) {
        keyboardRaisedBottomBlank
    } else {
        quickSubtitleBottomBlankBase
    }
    val quickSubtitleBottomBlank by animateDpAsState(
        targetValue = quickSubtitleBottomBlankTarget,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "quick_subtitle_bottom_blank"
    )
    LaunchedEffect(
        floatingInputPreviewActive,
        inputPreviewText,
        inputPreviewCursorIndex,
        imeBottomInset,
        bottomInputBarHeight
    ) {
        onFloatingInputPreviewChange(
            if (floatingInputPreviewActive) {
                QuickSubtitleFloatingInputPreviewState(
                    text = inputPreviewText,
                    cursorIndex = inputPreviewCursorIndex,
                    bottomPadding = imeBottomInset + bottomInputBarHeight + 8.dp
                )
            } else {
                null
            }
        )
    }
    DisposableEffect(Unit) {
        onDispose { onFloatingInputPreviewChange(null) }
    }
    val subtitleDisplayContent: @Composable (Boolean, AnnotatedString, Int?, Modifier) -> Unit =
        { preview, displayText, cursorIndex, modifier ->
        AnimatedContent(
            targetState = Triple(preview, displayText, cursorIndex),
            transitionSpec = {
                val previewTextEditTransition = initialState.first && targetState.first
                ContentTransform(
                    targetContentEnter = if (previewTextEditTransition) {
                        fadeIn(initialAlpha = 0.45f, animationSpec = tween(140))
                    } else {
                        fadeIn(animationSpec = tween(180)) +
                            slideInVertically(
                                initialOffsetY = { full -> full / 8 },
                                animationSpec = tween(200, easing = FastOutSlowInEasing)
                        )
                    },
                    initialContentExit = if (previewTextEditTransition) {
                        fadeOut(targetAlpha = 0.45f, animationSpec = tween(160))
                    } else {
                        fadeOut(animationSpec = tween(120))
                    },
                    sizeTransform = null
                )
            },
            label = "quick_subtitle_display_text_change"
        ) { (preview, text, cursorIndex) ->
            val textColor = when {
                preview -> MaterialTheme.colorScheme.onSurface
                text.text == QUICK_SUBTITLE_CLEARED_HINT -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                else -> MaterialTheme.colorScheme.onSurface
            }
            rotatedSubtitleText(
                text,
                textColor,
                subtitleSize,
                14f,
                1.15f,
                modifier,
                if (preview) cursorIndex else null,
                !preview || !keyboardVisible
            )
        }
    }
    val quickPanelExpanded = !quickInputCollapsed
    val quickPanelAnimatedWidth by animateDpAsState(
        targetValue = if (isLandscape && quickPanelExpanded) landscapeQuickPanelWidth else 0.dp,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "quick_subtitle_right_panel_width"
    )
    val quickPanelAnimatedGap by animateDpAsState(
        targetValue = if (isLandscape && quickPanelExpanded) landscapeQuickPanelGap else 0.dp,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "quick_subtitle_right_panel_gap"
    )
    val quickPanelAnimatedAlpha by animateFloatAsState(
        targetValue = if (isLandscape && quickPanelExpanded) 1f else 0f,
        animationSpec = tween(160, easing = FastOutSlowInEasing),
        label = "quick_subtitle_right_panel_alpha"
    )
    DisposableEffect(lifecycleOwner, focusManager) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                focusManager.clearFocus(force = true)
                AppLogger.i("QuickSubtitleScreen.onPause clearFocus")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(quickSubtitleTopBlank))
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(min = 260.dp),
                    horizontalArrangement = Arrangement.spacedBy(quickPanelAnimatedGap)
                ) {
                    Md2StaggeredFloatIn(
                        index = 0,
                        enabled = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(3.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .mdCenteredShadow(
                                    shape = RoundedCornerShape(UiTokens.Radius),
                                    shadowStyle = MdCardShadowStyle
                                ),
                            shape = RoundedCornerShape(UiTokens.Radius),
                            backgroundColor = md2ElevatedCardContainerColor(UiTokens.CardElevation),
                            elevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .combinedClickable(
                                            onClick = {
                                                performKeyHaptic()
                                                viewModel.openQuickSubtitlePreview()
                                            },
                                            onLongClick = copySubtitleText
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        subtitleDisplayContent(
                                            inlineInputPreviewActive,
                                            displayedSubtitleText,
                                            if (editingInputPreviewActive && inlineInputPreviewActive) inputPreviewCursorIndex else null,
                                            Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                AnimatedVisibility(
                                    visible = subtitleControlsVisible,
                                    enter = fadeIn(animationSpec = tween(140)) + expandHorizontally(
                                        animationSpec = tween(180, easing = FastOutSlowInEasing)
                                    ),
                                    exit = fadeOut(animationSpec = tween(120)) + shrinkHorizontally(
                                        animationSpec = tween(160, easing = FastOutSlowInEasing)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .fillMaxHeight(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .width(40.dp)
                                                .fillMaxHeight(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxWidth()
                                            ) {
                                                Crossfade(
                                                    targetState = showQuickSubtitleActionButtons,
                                                    animationSpec = tween(180),
                                                    label = "quick_subtitle_controls_landscape"
                                                ) { showButtons ->
                                                    if (showButtons) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize(),
                                                            contentAlignment = Alignment.TopCenter
                                                        ) {
                                                            subtitleActionButtonsColumn(
                                                                Modifier
                                                                    .fillMaxWidth()
                                                                    .verticalScroll(rememberScrollState())
                                                                    .padding(top = 4.dp, bottom = 4.dp)
                                                            )
                                                        }
                                                    } else {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxSize(),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            MsIcon("search", contentDescription = "字体大小")
                                                            Spacer(Modifier.height(4.dp))
                                                            Box(
                                                                modifier = Modifier
                                                                    .padding(top = 4.dp, bottom = 4.dp)
                                                                    .weight(1f)
                                                                    .fillMaxWidth(),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Md2VerticalSlider(
                                                                    value = subtitleSize,
                                                                    onValueChange = { viewModel.setQuickSubtitleFontSize(it) },
                                                                    valueRange = 28f..subtitleFontSizeMax,
                                                                    modifier = Modifier
                                                                        .fillMaxHeight()
                                                                        .width(28.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Md2IconButton(
                                                icon = actionPanelToggleIcon,
                                                contentDescription = actionPanelToggleDescription,
                                                onClick = {
                                                    viewModel.updateQuickSubtitleShowActionButtons(!showQuickSubtitleActionButtons)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .width(quickPanelAnimatedWidth)
                            .fillMaxHeight()
                            .graphicsLayer { alpha = quickPanelAnimatedAlpha }
                    ) {
                        Md2StaggeredFloatIn(
                            index = 1,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .mdCenteredShadow(
                                        shape = RoundedCornerShape(UiTokens.Radius),
                                        shadowStyle = MdCardShadowStyle
                                    ),
                                shape = RoundedCornerShape(UiTokens.Radius),
                                backgroundColor = md2ElevatedCardContainerColor(),
                                elevation = 0.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .padding(horizontal = 6.dp)
                                    ) {
                                        AnimatedContent(
                                            targetState = selectedGroupIndex,
                                            transitionSpec = {
                                                val forward = targetState >= initialState
                                                ContentTransform(
                                                    targetContentEnter = fadeIn(animationSpec = tween(200)) +
                                                        slideInVertically(
                                                            initialOffsetY = { full ->
                                                                val d = kotlin.math.min(full / 3, 28)
                                                                if (forward) d else -d
                                                            },
                                                            animationSpec = tween(180, easing = FastOutSlowInEasing)
                                                        ),
                                                    initialContentExit = fadeOut(animationSpec = tween(170)) +
                                                        slideOutVertically(
                                                            targetOffsetY = { full ->
                                                                val d = kotlin.math.min(full / 4, 22)
                                                                if (forward) -d else d
                                                            },
                                                            animationSpec = tween(160, easing = FastOutSlowInEasing)
                                                        ),
                                                    sizeTransform = androidx.compose.animation.SizeTransform(clip = false)
                                                )
                                            },
                                            label = "quick_subtitle_items_switch_landscape"
                                        ) { groupIndex ->
                                            val animatedQuickItems = groups.getOrNull(groupIndex)?.items.orEmpty()
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 3.dp)
                                                    .verticalScroll(quickItemsScrollState),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Spacer(Modifier.height(3.dp))
                                                animatedQuickItems.forEach { text ->
                                                    Card(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(72.dp)
                                                            .mdCenteredShadow(
                                                                shape = RoundedCornerShape(UiTokens.Radius),
                                                                shadowStyle = MdCardShadowStyle
                                                            )
                                                            .combinedClickable(
                                                                onClick = {
                                                                    performKeyHaptic()
                                                                    viewModel.submitQuickSubtitlePreset(
                                                                        text = text,
                                                                        hasVoice = hasVoice
                                                                    )
                                                                },
                                                                onLongClick = openQuickSubtitleListDialog
                                                            ),
                                                        shape = RoundedCornerShape(UiTokens.Radius),
                                                        backgroundColor = md2ElevatedCardContainerColor(UiTokens.MenuElevation),
                                                        elevation = 0.dp
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                                            contentAlignment = Alignment.CenterStart
                                                        ) {
                                                            Text(
                                                                text = text,
                                                                maxLines = 2,
                                                                overflow = TextOverflow.Ellipsis,
                                                                style = MaterialTheme.typography.bodyLarge
                                                            )
                                                        }
                                                    }
                                                }
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(56.dp)
                                                        .mdCenteredShadow(
                                                            shape = RoundedCornerShape(UiTokens.Radius),
                                                            shadowStyle = MdCardShadowStyle
                                                        )
                                                        .clickable {
                                                            performKeyHaptic()
                                                            addCurrentTextToQuickItems(groupIndex)
                                                        },
                                                    shape = RoundedCornerShape(UiTokens.Radius),
                                                    backgroundColor = md2ElevatedCardContainerColor(UiTokens.MenuElevation),
                                                    elevation = 0.dp
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        MsIcon("add", contentDescription = "添加当前文本")
                                                    }
                                                }
                                                Spacer(Modifier.height(3.dp))
                                            }
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(1.dp)
                                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                                    )
                                    Column(
                                        modifier = Modifier
                                            .width(44.dp)
                                            .fillMaxHeight()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                                .verticalScroll(rememberScrollState())
                                                .padding(horizontal = 2.dp, vertical = 4.dp),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            groups.forEachIndexed { index, group ->
                                                val selected = selectedGroupIndex == index
                                                val tabBg =
                                                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(44.dp)
                                                        .clip(RoundedCornerShape(UiTokens.Radius))
                                                        .background(tabBg)
                                                        .clickable {
                                                            performKeyHaptic()
                                                            selectQuickSubtitleGroupWithHint(index)
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    MsIcon(
                                                        group.icon,
                                                        contentDescription = group.title.ifBlank { "未命名分组" }
                                                    )
                                                }
                                            }
                                        }
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                KigttsIconButton(onClick = {
                                                    onOpenEditor()
                                                }) {
                                                    MsIcon(
                                                        "edit",
                                                        contentDescription = "编辑快捷文本",
                                                        tint = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        GroupSwitchHintCard(
                            state = groupHintState,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 54.dp)
                        )
                    }
                }
            } else {
                Md2StaggeredFloatIn(
                    index = 0,
                    enabled = false,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(min = 260.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(UiTokens.Radius),
                        backgroundColor = md2CardContainerColor(),
                        elevation = UiTokens.CardElevation
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            performKeyHaptic()
                                            viewModel.openQuickSubtitlePreview()
                                        },
                                        onLongClick = copySubtitleText
                                    )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    subtitleDisplayContent(
                                        inlineInputPreviewActive,
                                        displayedSubtitleText,
                                        if (editingInputPreviewActive && inlineInputPreviewActive) inputPreviewCursorIndex else null,
                                        Modifier.fillMaxSize()
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = subtitleControlsVisible,
                                enter = fadeIn(animationSpec = tween(140)) +
                                    expandVertically(animationSpec = tween(180, easing = FastOutSlowInEasing)),
                                exit = fadeOut(animationSpec = tween(120)) +
                                    shrinkVertically(animationSpec = tween(160, easing = FastOutSlowInEasing))
                            ) {
                                Column {
                                    Spacer(Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(portraitSubtitleControlAreaHeight)
                                    ) {
                                        Crossfade(
                                            targetState = showQuickSubtitleActionButtons,
                                            animationSpec = tween(180),
                                            label = "quick_subtitle_controls_portrait"
                                        ) { showButtons ->
                                            if (showButtons) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize(),
                                                    contentAlignment = Alignment.BottomStart
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(end = 44.dp)
                                                            .offset(y = portraitSubtitleControlBaselineOffset),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        subtitleActionButtonsRow(Modifier.weight(1f))
                                                    }
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize(),
                                                    contentAlignment = Alignment.BottomStart
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 4.dp, end = 44.dp)
                                                            .offset(y = portraitSubtitleControlBaselineOffset),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        MsIcon("search", contentDescription = "字体大小")
                                                        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                                                            Slider(
                                                                value = subtitleSize,
                                                                onValueChange = { viewModel.setQuickSubtitleFontSize(it) },
                                                                valueRange = 28f..subtitleFontSizeMax,
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .height(36.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Md2IconButton(
                                            icon = actionPanelToggleIcon,
                                            contentDescription = actionPanelToggleDescription,
                                            onClick = {
                                                viewModel.updateQuickSubtitleShowActionButtons(!showQuickSubtitleActionButtons)
                                            },
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .offset(y = portraitSubtitleControlBaselineOffset)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!isLandscape) {
                AnimatedVisibility(
                    visible = !quickInputCollapsed && !portraitPhoneKeyboardInputMode,
                    enter = fadeIn(animationSpec = tween(140)) +
                        expandVertically(animationSpec = tween(180, easing = FastOutSlowInEasing)),
                    exit = fadeOut(animationSpec = tween(120)) +
                        shrinkVertically(animationSpec = tween(160, easing = FastOutSlowInEasing))
                ) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        Md2StaggeredFloatIn(index = 1, enabled = false) {
                            if (useCompactQuickTextControls) {
                                val compactQuickTextCardColor =
                                    md2ElevatedCardContainerColor(UiTokens.CardElevation)
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 3.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(110.dp),
                                            shape = RoundedCornerShape(UiTokens.Radius),
                                            backgroundColor = compactQuickTextCardColor,
                                            elevation = UiTokens.CardElevation
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(vertical = 8.dp)
                                            ) {
                                                AnimatedContent(
                                                    targetState = selectedGroupIndex,
                                                    transitionSpec = {
                                                        if (compactQuickGroupSuppressAnimation || groups.size <= 1) {
                                                            ContentTransform(
                                                                targetContentEnter = fadeIn(animationSpec = tween(0)),
                                                                initialContentExit = fadeOut(animationSpec = tween(0)),
                                                                sizeTransform = null
                                                            )
                                                        } else {
                                                            val forward = targetState == if (initialState < groups.lastIndex) initialState + 1 else 0
                                                            ContentTransform(
                                                                targetContentEnter = fadeIn(animationSpec = tween(200)) +
                                                                    slideInVertically(
                                                                        initialOffsetY = { full -> if (forward) full / 3 else -full / 3 },
                                                                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                                                                    ),
                                                                initialContentExit = fadeOut(animationSpec = tween(170)) +
                                                                    slideOutVertically(
                                                                        targetOffsetY = { full -> if (forward) -full / 4 else full / 4 },
                                                                        animationSpec = tween(210, easing = FastOutSlowInEasing)
                                                                    ),
                                                                sizeTransform = null
                                                            )
                                                        }
                                                    },
                                                    label = "quick_subtitle_items_switch_portrait_compact"
                                                ) { groupIndex ->
                                                    val animatedQuickItems = groups.getOrNull(groupIndex)?.items.orEmpty()
                                                    val compactScrollState = rememberScrollState()
                                                    val compactLeftFadeAlpha by animateFloatAsState(
                                                        targetValue = if (
                                                            compactScrollState.maxValue > 0 &&
                                                            compactScrollState.value > 0
                                                        ) 1f else 0f,
                                                        animationSpec = tween(140),
                                                        label = "quick_subtitle_compact_left_fade"
                                                    )
                                                    val compactRightFadeAlpha by animateFloatAsState(
                                                        targetValue = if (
                                                            compactScrollState.maxValue > 0 &&
                                                            compactScrollState.value < compactScrollState.maxValue
                                                        ) 1f else 0f,
                                                        animationSpec = tween(140),
                                                        label = "quick_subtitle_compact_right_fade"
                                                    )
                                                    Box(
                                                        modifier = Modifier.fillMaxSize()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .horizontalScroll(compactScrollState),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Spacer(Modifier.width(10.dp))
                                                            animatedQuickItems.forEach { text ->
                                                                Box(
                                                                    modifier = Modifier
                                                                        .width(148.dp)
                                                                        .height(94.dp)
                                                                        .combinedClickable(
                                                                            onClick = {
                                                                                performKeyHaptic()
                                                                                viewModel.submitQuickSubtitlePreset(
                                                                                    text = text,
                                                                                    hasVoice = hasVoice,
                                                                                    interruptCurrent = state.quickSubtitleInterruptQueue
                                                                                )
                                                                            },
                                                                            onLongClick = openQuickSubtitleListDialog
                                                                        )
                                                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                                                    contentAlignment = Alignment.CenterStart
                                                                ) {
                                                                    Text(
                                                                        text = text,
                                                                        maxLines = 2,
                                                                        overflow = TextOverflow.Ellipsis,
                                                                        style = MaterialTheme.typography.bodyLarge
                                                                    )
                                                                }
                                                                Box(
                                                                    modifier = Modifier
                                                                        .height(58.dp)
                                                                        .width(1.dp)
                                                                        .background(
                                                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                                                                        )
                                                                )
                                                            }
                                                            Box(
                                                                modifier = Modifier
                                                                    .width(86.dp)
                                                                    .height(94.dp)
                                                                    .clickable {
                                                                        performKeyHaptic()
                                                                        addCurrentTextToQuickItems(groupIndex)
                                                                    },
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                MsIcon("add", contentDescription = "添加当前文本")
                                                            }
                                                            Spacer(Modifier.width(10.dp))
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .align(Alignment.CenterStart)
                                                                .fillMaxHeight()
                                                                .width(18.dp)
                                                                .background(
                                                                    Brush.horizontalGradient(
                                                                        listOf(
                                                                            compactQuickTextCardColor,
                                                                            compactQuickTextCardColor.copy(alpha = 0f)
                                                                        )
                                                                    )
                                                                )
                                                                .graphicsLayer { alpha = compactLeftFadeAlpha }
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .align(Alignment.CenterEnd)
                                                                .fillMaxHeight()
                                                                .width(18.dp)
                                                                .background(
                                                                    Brush.horizontalGradient(
                                                                        listOf(
                                                                            compactQuickTextCardColor.copy(alpha = 0f),
                                                                            compactQuickTextCardColor
                                                                        )
                                                                    )
                                                                )
                                                                .graphicsLayer { alpha = compactRightFadeAlpha }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        val compactDisplayGroup = groups.getOrNull(selectedGroupIndex)
                                        Card(
                                            modifier = Modifier
                                                .width(56.dp)
                                                .height(110.dp)
                                                .pointerInput(groups.size) {
                                                    var accumulatedDrag = 0f
                                                    var gestureGroupIndex = 0
                                                    detectDragGestures(
                                                        onDragStart = {
                                                            accumulatedDrag = 0f
                                                            gestureGroupIndex = currentCompactSelectedGroupIndex
                                                            compactQuickGroupSuppressAnimation = true
                                                            groupHintState.beginHold()
                                                        },
                                                        onDragEnd = {
                                                            accumulatedDrag = 0f
                                                            compactQuickGroupSuppressAnimation = false
                                                            groupHintState.release()
                                                        },
                                                        onDragCancel = {
                                                            accumulatedDrag = 0f
                                                            compactQuickGroupSuppressAnimation = false
                                                            groupHintState.release()
                                                        }
                                                    ) { change, dragAmount ->
                                                        if (groups.isEmpty()) return@detectDragGestures
                                                        change.consume()
                                                        accumulatedDrag += dragAmount.y
                                                        if (kotlin.math.abs(accumulatedDrag) >= compactQuickGroupSwipeThresholdPx) {
                                                            val target = if (accumulatedDrag > 0f) {
                                                                if (gestureGroupIndex > 0) gestureGroupIndex - 1 else groups.lastIndex
                                                            } else {
                                                                if (gestureGroupIndex < groups.lastIndex) gestureGroupIndex + 1 else 0
                                                            }
                                                            performKeyHaptic()
                                                            groupHintState.show(groups[target].title.ifBlank { "未命名分组" }, hold = true)
                                                            viewModel.selectQuickSubtitleGroup(target)
                                                            gestureGroupIndex = target
                                                            accumulatedDrag = 0f
                                                        }
                                                    }
                                            },
                                            shape = RoundedCornerShape(UiTokens.Radius),
                                            backgroundColor = compactQuickTextCardColor,
                                            elevation = UiTokens.CardElevation
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(vertical = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Md2IconButton(
                                                    icon = "keyboard_arrow_up",
                                                    contentDescription = "上一分组",
                                                    onClick = {
                                                        if (groups.isNotEmpty()) {
                                                            compactQuickGroupSuppressAnimation = false
                                                            val target = if (selectedGroupIndex > 0) {
                                                                selectedGroupIndex - 1
                                                            } else {
                                                                groups.lastIndex
                                                            }
                                                            selectQuickSubtitleGroupWithHint(target)
                                                        }
                                                    }
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (compactDisplayGroup != null) {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                                        ) {
                                                            MsIcon(
                                                                compactDisplayGroup.icon,
                                                                contentDescription = compactDisplayGroup.title.ifBlank { "当前分组" }
                                                            )
                                                            Box(
                                                                modifier = Modifier
                                                                    .width(18.dp)
                                                                    .height(2.dp)
                                                                    .clip(RoundedCornerShape(999.dp))
                                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.55f))
                                                            )
                                                        }
                                                    }
                                                }
                                                Md2IconButton(
                                                    icon = "keyboard_arrow_down",
                                                    contentDescription = "下一分组",
                                                    onClick = {
                                                        if (groups.isNotEmpty()) {
                                                            compactQuickGroupSuppressAnimation = false
                                                            val target = if (selectedGroupIndex < groups.lastIndex) {
                                                                selectedGroupIndex + 1
                                                            } else {
                                                                0
                                                            }
                                                            selectQuickSubtitleGroupWithHint(target)
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    GroupSwitchHintCard(
                                        state = groupHintState,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 64.dp)
                                    )
                                }
                            } else {
                                Column {
                                    AnimatedContent(
                                        targetState = selectedGroupIndex,
                                        transitionSpec = {
                                            val forward = targetState >= initialState
                                            ContentTransform(
                                                targetContentEnter = fadeIn(animationSpec = tween(200)) +
                                                    slideInHorizontally(
                                                        initialOffsetX = { full -> if (forward) full / 3 else -full / 3 },
                                                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                                                    ),
                                                initialContentExit = fadeOut(animationSpec = tween(170)) +
                                                    slideOutHorizontally(
                                                        targetOffsetX = { full -> if (forward) -full / 4 else full / 4 },
                                                        animationSpec = tween(210, easing = FastOutSlowInEasing)
                                                    ),
                                                sizeTransform = null
                                            )
                                        },
                                        label = "quick_subtitle_items_switch_portrait"
                                    ) { groupIndex ->
                                        val animatedQuickItems = groups.getOrNull(groupIndex)?.items.orEmpty()
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp)
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Spacer(Modifier.width(8.dp))
                                            animatedQuickItems.forEach { text ->
                                                Card(
                                                    modifier = Modifier
                                                        .padding(vertical = 3.dp)
                                                        .width(148.dp)
                                                        .height(94.dp)
                                                        .combinedClickable(
                                                            onClick = {
                                                                performKeyHaptic()
                                                                viewModel.submitQuickSubtitlePreset(
                                                                    text = text,
                                                                    hasVoice = hasVoice
                                                                )
                                                            },
                                                            onLongClick = openQuickSubtitleListDialog
                                                        ),
                                                    shape = RoundedCornerShape(UiTokens.Radius),
                                                    backgroundColor = md2CardContainerColor(),
                                                    elevation = UiTokens.CardElevation
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                                        contentAlignment = Alignment.CenterStart
                                                    ) {
                                                        Text(
                                                            text = text,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis,
                                                            style = MaterialTheme.typography.bodyLarge
                                                        )
                                                    }
                                                }
                                            }
                                            Card(
                                                modifier = Modifier
                                                    .padding(vertical = 3.dp)
                                                    .width(86.dp)
                                                    .height(94.dp)
                                                    .clickable {
                                                        performKeyHaptic()
                                                        addCurrentTextToQuickItems(groupIndex)
                                                    },
                                                shape = RoundedCornerShape(UiTokens.Radius),
                                                backgroundColor = md2CardContainerColor(),
                                                elevation = UiTokens.CardElevation
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    MsIcon("add", contentDescription = "添加当前文本")
                                                }
                                            }
                                            Spacer(Modifier.width(8.dp))
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))
                                    Md2StaggeredFloatIn(
                                        index = 2,
                                        enabled = false,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 3.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(UiTokens.Radius),
                                            backgroundColor = md2CardContainerColor(),
                                            elevation = UiTokens.CardElevation
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                        .horizontalScroll(rememberScrollState()),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    groups.forEachIndexed { index, group ->
                                                        val selected = selectedGroupIndex == index
                                                        val tabBg =
                                                            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent
                                                        Row(
                                                            modifier = Modifier
                                                                .height(48.dp)
                                                                .clip(RoundedCornerShape(UiTokens.Radius))
                                                                .background(tabBg)
                                                                .clickable {
                                                                    performKeyHaptic()
                                                                    viewModel.selectQuickSubtitleGroup(index)
                                                                }
                                                                .padding(horizontal = 10.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            val displayTitle = group.title.ifBlank { "未命名分组" }
                                                            MsIcon(group.icon, contentDescription = displayTitle)
                                                            Text(displayTitle, maxLines = 1)
                                                        }
                                                        if (index != groups.lastIndex) {
                                                            Spacer(Modifier.width(2.dp))
                                                        }
                                                    }
                                                }
                                                Surface(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .width(52.dp),
                                                    color = MaterialTheme.colorScheme.primary
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        KigttsIconButton(onClick = {
                                                            onOpenEditor()
                                                        }) {
                                                            MsIcon(
                                                                "edit",
                                                                contentDescription = "编辑快捷文本",
                                                                tint = MaterialTheme.colorScheme.onPrimary
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(3.dp))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(quickSubtitleBottomBlank))
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding(),
            shape = RectangleShape,
            color = md2CardContainerColor(),
            elevation = UiTokens.CardElevation
        ) {
            val sendInput = {
                if (inputFieldValue.text.trim().isNotEmpty()) {
                    viewModel.submitQuickSubtitleInput(
                        playVoice = playOnSend && hasVoice
                    )
                    inputFieldValue = TextFieldValue("")
                }
            }
            val actionButtons: @Composable () -> Unit = {
                Md2IconButton(
                    icon = "arrow_back",
                    contentDescription = "光标左移",
                    onClick = {
                        val current = inputFieldValue.selection.start.coerceIn(0, inputFieldValue.text.length)
                        val target = (current - 1).coerceAtLeast(0)
                        inputFieldValue = inputFieldValue.copy(selection = TextRange(target))
                    }
                )
                Md2IconButton(
                    icon = "arrow_forward",
                    contentDescription = "光标右移",
                    onClick = {
                        val current = inputFieldValue.selection.end.coerceIn(0, inputFieldValue.text.length)
                        val target = (current + 1).coerceAtMost(inputFieldValue.text.length)
                        inputFieldValue = inputFieldValue.copy(selection = TextRange(target))
                    }
                )
                Md2IconButton(
                    icon = if (playOnSend) "volume_up" else "volume_off",
                    contentDescription = if (playOnSend) "发送时播放语音：开" else "发送时播放语音：关",
                    onClick = {
                        viewModel.updateQuickSubtitlePlayOnSend(!playOnSend)
                    }
                )
                Md2IconButton(
                    icon = if (quickInputCollapsed) "subtitles_off" else "subtitles",
                    contentDescription = if (quickInputCollapsed) "展开快捷输入区域" else "收起快捷输入区域",
                    onClick = {
                        viewModel.updateQuickSubtitleInputCollapsed(!quickInputCollapsed)
                    }
                )
                Md2IconButton(
                    icon = "play_arrow",
                    contentDescription = "朗读当前字幕",
                    onClick = {
                        viewModel.applyQuickSubtitleText(subtitleText, enqueueSpeak = hasVoice)
                    }
                )
            }
            Column(
                modifier = Modifier
                    .onSizeChanged { bottomInputBarHeightPx = it.height }
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            actionButtons()
                        }
                        OutlinedTextField(
                            value = inputFieldValue,
                            onValueChange = {
                                inputFieldValue = it
                                viewModel.updateQuickSubtitleInputText(it.text)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { inputFieldFocused = it.isFocused },
                            singleLine = true,
                            placeholder = { Text("请输入文本") },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = { sendInput() },
                                onDone = { sendInput() }
                            ),
                            trailingIcon = {
                                if (inputFieldValue.text.isNotEmpty()) {
                                    KigttsIconButton(
                                        onClick = {
                                            inputFieldValue = TextFieldValue("")
                                            viewModel.updateQuickSubtitleInputText("")
                                        }
                                    ) {
                                        MsIcon("close", contentDescription = "清空输入")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(UiTokens.Radius),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        if (!useOverlayFab) {
                            QuickSubtitleMicFab(
                                state = state,
                                compactPttSideButtonsMode = compactPttSideButtonsMode,
                                onToggleMic = onToggleMic,
                                onPushToTalkPressStart = onPushToTalkPressStart,
                                onPushToTalkPressEnd = onPushToTalkPressEnd,
                                onPttDragTargetChanged = { pttDragTarget = it },
                                modifier = Modifier.size(48.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.size(48.dp))
                        }
                        KigttsIconButton(
                            onClick = sendInput,
                            enabled = inputFieldValue.text.trim().isNotEmpty()
                        ) {
                            MsIcon(
                                name = "send",
                                contentDescription = "发送到朗读队列",
                                tint = if (inputFieldValue.text.trim().isNotEmpty()) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
                            )
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        actionButtons()
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputFieldValue,
                            onValueChange = {
                                inputFieldValue = it
                                viewModel.updateQuickSubtitleInputText(it.text)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { inputFieldFocused = it.isFocused },
                            singleLine = true,
                            placeholder = { Text("请输入文本") },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = { sendInput() },
                                onDone = { sendInput() }
                            ),
                            trailingIcon = {
                                if (inputFieldValue.text.isNotEmpty()) {
                                    KigttsIconButton(
                                        onClick = {
                                            inputFieldValue = TextFieldValue("")
                                            viewModel.updateQuickSubtitleInputText("")
                                        }
                                    ) {
                                        MsIcon("close", contentDescription = "清空输入")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(UiTokens.Radius),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        KigttsIconButton(
                            onClick = sendInput,
                            enabled = inputFieldValue.text.trim().isNotEmpty()
                        ) {
                            MsIcon(
                                name = "send",
                                contentDescription = "发送到朗读队列",
                                tint = if (inputFieldValue.text.trim().isNotEmpty()) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
                            )
                        }
                    }
                }
            }
        }

        QuickSubtitlePttConfirmOverlay(
            visible = showPttConfirmOverlay,
            dragTarget = pttDragTarget,
            streamingText = state.pushToTalkStreamingText,
            isLandscape = isLandscape,
            compactPttSideButtonsMode = compactPttSideButtonsMode
        )

        AnimatedVisibility(
            visible = showPttConfirmOverlay,
            modifier = Modifier
                .zIndex(6.5f)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding()
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                .padding(
                    start = pttStatusStripStartInset,
                    end = pttStatusStripAnimatedEndInsetWithBleed,
                    bottom = pttStatusStripBottomInset
                ),
            enter = fadeIn(animationSpec = tween(140)),
            exit = fadeOut(animationSpec = tween(110))
        ) {
            Box(
                modifier = Modifier.padding(
                    start = pttStatusStripOuterBleed,
                    top = pttStatusStripTopBleed,
                    end = pttStatusStripOuterBleed,
                    bottom = pttStatusStripBottomBleed
                )
            ) {
                QuickSubtitlePttConfirmBottomStrip(
                    guideText = pttGuideText,
                    reserveFabWidth = pttStripFabReserveWidth,
                    stripHeight = pttFabSize
                )
            }
        }

        QuickSubtitlePttCompactSideButtonsOverlay(
            visible = showPttConfirmOverlay && compactPttSideButtonsMode,
            dragTarget = pttDragTarget,
            fabSize = pttFabSize,
            fabEndInset = pttFabEndInset,
            fabBottomOffset = pttFabBottomOffset
        )

        if (useOverlayFab) {
            val fabModifier = if (isLandscape) {
                Modifier
                    .zIndex(7f)
                    .align(Alignment.BottomEnd)
                    .imePadding()
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                    // Keep same visual slot as the inline landscape FAB (before the send button).
                    .padding(end = pttFabEndInset, bottom = pttFabBottomOffset)
                    .size(pttFabSize)
            } else {
                Modifier
                    .zIndex(7f)
                    .align(Alignment.BottomEnd)
                    .imePadding()
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                    .padding(end = pttFabEndInset, bottom = pttFabBottomOffset)
                    .size(pttFabSize)
            }
            QuickSubtitleMicFab(
                state = state,
                compactPttSideButtonsMode = compactPttSideButtonsMode,
                onToggleMic = onToggleMic,
                onPushToTalkPressStart = onPushToTalkPressStart,
                onPushToTalkPressEnd = onPushToTalkPressEnd,
                onPttDragTargetChanged = { pttDragTarget = it },
                modifier = fabModifier
            )
        }

        if (quickSubtitleListDialogVisible) {
            QuickSubtitleListDialog(
                groups = groups,
                initialGroupIndex = selectedGroupIndex,
                layoutMode = quickSubtitleListDialogLayoutMode,
                forceFullWidthTabsOnPhone = state.forceFullWidthTabsOnPhone,
                onLayoutModeChange = { viewModel.updateQuickSubtitleListPopupLayout(it) },
                onSelectGroup = { viewModel.selectQuickSubtitleGroup(it) },
                onDismiss = { quickSubtitleListDialogVisible = false },
                onSubmit = { text ->
                    viewModel.submitQuickSubtitlePreset(
                        text = text,
                        hasVoice = hasVoice,
                        interruptCurrent = state.quickSubtitleInterruptQueue
                    )
                }
            )
        }

        if (subtitleFullscreenDialogVisible) {
            Dialog(
                onDismissRequest = { viewModel.closeQuickSubtitlePreview() },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.52f))
                        .combinedClickable(
                            onClick = {
                                performKeyHaptic()
                                viewModel.closeQuickSubtitlePreview()
                            },
                            onLongClick = copySubtitleText
                        )
                        .padding(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(UiTokens.Radius),
                        backgroundColor = md2CardContainerColor(),
                        elevation = UiTokens.MenuElevation
                    ) {
                        rotatedSubtitleText(
                            AnnotatedString(subtitleText),
                            subtitleTextColor,
                            (subtitleSize * 1.25f).coerceIn(36f, fullscreenPreviewFontSizeMax),
                            18f,
                            1.36f,
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            null,
                            true
                        )
                    }
                }
            }
        }
    }
}

internal data class QuickSubtitleFitResult(
    val fontSizeSp: Float,
    val needsScroll: Boolean
)

@Composable
internal fun QuickSubtitleAdaptiveText(
    text: AnnotatedString,
    color: Color,
    textAlign: TextAlign,
    fontWeight: FontWeight,
    maxFontSizeSp: Float,
    minFontSizeSp: Float,
    lineHeightMultiplier: Float,
    autoFitEnabled: Boolean,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    textRotationZ: Float = 0f,
    cursorIndex: Int? = null,
    cursorColor: Color = Color.Unspecified,
    cursorWidth: Dp = 2.5.dp
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val scrollState = rememberScrollState()
        val textMeasurer = rememberTextMeasurer()
        var textLayoutResult by remember(text) { mutableStateOf<TextLayoutResult?>(null) }
        val cursorStrokeWidthPx = with(density) { cursorWidth.toPx() }
        val maxWidthPx = remember(maxWidth, density) { with(density) { maxWidth.roundToPx() }.coerceAtLeast(1) }
        val maxHeightPx = remember(maxHeight, density) { with(density) { maxHeight.roundToPx() }.coerceAtLeast(1) }
        val boundedMaxFont = maxFontSizeSp.coerceAtLeast(minFontSizeSp)
        val fitResult = remember(
            text,
            color,
            textAlign,
            fontWeight,
            boundedMaxFont,
            minFontSizeSp,
            lineHeightMultiplier,
            autoFitEnabled,
            maxWidthPx,
            maxHeightPx,
            density
        ) {
            if (!autoFitEnabled) {
                QuickSubtitleFitResult(fontSizeSp = boundedMaxFont, needsScroll = true)
            } else {
                fun overflows(sizeSp: Float): Boolean {
                    val lineHeightSp = (sizeSp * lineHeightMultiplier).coerceAtLeast(sizeSp)
                    val result = textMeasurer.measure(
                        text = text,
                        style = TextStyle(
                            fontWeight = fontWeight,
                            fontSize = sizeSp.sp,
                            lineHeight = lineHeightSp.sp,
                            textAlign = textAlign,
                            color = color
                        ),
                        overflow = TextOverflow.Clip,
                        softWrap = true,
                        maxLines = Int.MAX_VALUE,
                        constraints = Constraints(
                            maxWidth = maxWidthPx,
                            maxHeight = maxHeightPx
                        )
                    )
                    return result.hasVisualOverflow || result.didOverflowHeight || result.didOverflowWidth
                }

                val minSize = minFontSizeSp.coerceAtMost(boundedMaxFont)
                if (!overflows(boundedMaxFont)) {
                    QuickSubtitleFitResult(fontSizeSp = boundedMaxFont, needsScroll = false)
                } else if (overflows(minSize)) {
                    QuickSubtitleFitResult(fontSizeSp = minSize, needsScroll = true)
                } else {
                    var low = minSize
                    var high = boundedMaxFont
                    var best = minSize
                    repeat(12) {
                        val mid = (low + high) / 2f
                        if (overflows(mid)) {
                            high = mid
                        } else {
                            best = mid
                            low = mid
                        }
                    }
                    QuickSubtitleFitResult(fontSizeSp = best, needsScroll = false)
                }
            }
        }
        val contentModifier = if (fitResult.needsScroll) {
            Modifier
                .fillMaxWidth()
                .heightIn(min = maxHeight)
                .verticalScroll(scrollState)
        } else {
            Modifier.fillMaxSize()
        }
        val rotateWholeViewport = textRotationZ != 0f && !fitResult.needsScroll
        val rotateTextOnly = textRotationZ != 0f && fitResult.needsScroll
        Box(
            modifier = contentModifier.then(
                if (rotateWholeViewport) Modifier.graphicsLayer(rotationZ = textRotationZ) else Modifier
            ),
            contentAlignment = contentAlignment
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = fontWeight,
                    fontSize = fitResult.fontSizeSp.sp,
                    lineHeight = (fitResult.fontSizeSp * lineHeightMultiplier).sp
                ),
                color = color,
                textAlign = textAlign,
                onTextLayout = { textLayoutResult = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        drawContent()
                        val index = cursorIndex?.coerceIn(0, text.text.length) ?: return@drawWithContent
                        if (cursorColor == Color.Unspecified) return@drawWithContent
                        val layout = textLayoutResult ?: return@drawWithContent
                        val rect = layout.getCursorRect(index)
                        val x = rect.left.coerceIn(0f, size.width)
                        drawLine(
                            color = cursorColor,
                            start = Offset(x, rect.top),
                            end = Offset(x, rect.bottom),
                            strokeWidth = cursorStrokeWidthPx
                        )
                    }
                    .then(if (rotateTextOnly) Modifier.graphicsLayer(rotationZ = textRotationZ) else Modifier)
            )
        }
    }
}

@Composable
internal fun QuickSubtitlePttConfirmOverlay(
    visible: Boolean,
    dragTarget: PttConfirmDragTarget,
    streamingText: String,
    isLandscape: Boolean,
    compactPttSideButtonsMode: Boolean,
    showInputAction: Boolean = true,
    applyNavigationBarsPadding: Boolean = true,
    topRowBottomReservedOverride: Dp? = null
) {
    val overlayHorizontalPadding = 16.dp
    val topRowBottomReserved = topRowBottomReservedOverride ?: if (isLandscape) 84.dp else 142.dp
    val topRowVerticalPadding = if (isLandscape) 6.dp else 18.dp

    val displayText = streamingText.ifBlank { "正在识别..." }
    val overlayBrush: Brush = if (compactPttSideButtonsMode) {
        SolidColor(Color.Black.copy(alpha = 0.34f))
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                Color.Black.copy(alpha = 0.38f)
            )
        )
    }

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(6f),
        enter = fadeIn(animationSpec = tween(120)),
        exit = fadeOut(animationSpec = tween(110))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .imePadding()
                    .then(
                        if (applyNavigationBarsPadding) {
                            Modifier.windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                        } else {
                            Modifier
                        }
                    )
                    .background(overlayBrush)
            )
            if (!compactPttSideButtonsMode) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = overlayHorizontalPadding, vertical = topRowVerticalPadding)
                        .imePadding()
                        .then(
                            if (applyNavigationBarsPadding) {
                                Modifier.windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                            } else {
                                Modifier
                            }
                        )
                        .padding(bottom = topRowBottomReserved),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedContent(
                            modifier = Modifier.weight(1f),
                            targetState = displayText,
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
                            label = "ptt_confirm_stream_text_top"
                        ) { text ->
                            Text(
                                text = text,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.h6,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                softWrap = true,
                                overflow = TextOverflow.Clip
                            )
                        }
                        if (showInputAction) {
                            Surface(
                                modifier = Modifier
                                    .requiredSize(72.dp)
                                    .mdCenteredShadow(
                                        shape = CircleShape,
                                        shadowStyle = MdFabShadowStyle
                                    ),
                                shape = CircleShape,
                                color = if (dragTarget == PttConfirmDragTarget.ToInput) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color(0xFF202124)
                                },
                                elevation = 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    MsIcon(
                                        name = "keyboard_return",
                                        contentDescription = "输入到文本框",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .requiredSize(72.dp)
                                .mdCenteredShadow(
                                    shape = CircleShape,
                                        shadowStyle = MdFabShadowStyle
                                ),
                            shape = CircleShape,
                            color = if (dragTarget == PttConfirmDragTarget.Cancel) {
                                Color(0xFFB00020)
                            } else {
                                Color(0xFF202124)
                            },
                            elevation = 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                MsIcon(
                                    name = "close",
                                    contentDescription = "取消发送",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun QuickSubtitlePttCompactSideButtonsOverlay(
    visible: Boolean,
    dragTarget: PttConfirmDragTarget,
    fabSize: Dp,
    fabEndInset: Dp,
    fabBottomOffset: Dp,
    showInputAction: Boolean = true,
    applyNavigationBarsPadding: Boolean = true
) {
    val sideGap = 10.dp
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .fillMaxSize()
            .zIndex(6.9f),
        enter = fadeIn(animationSpec = tween(120)),
        exit = fadeOut(animationSpec = tween(110))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val cancelEndInset = (fabEndInset - fabSize - sideGap).coerceAtLeast(0.dp)
            if (showInputAction) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .imePadding()
                        .then(
                            if (applyNavigationBarsPadding) {
                                Modifier.windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                            } else {
                                Modifier
                            }
                        )
                        .padding(end = fabEndInset + fabSize + sideGap, bottom = fabBottomOffset)
                        .requiredSize(fabSize)
                        .mdCenteredShadow(shape = CircleShape, shadowStyle = MdFabShadowStyle),
                    shape = CircleShape,
                    color = if (dragTarget == PttConfirmDragTarget.ToInput) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color(0xFF202124)
                    },
                    elevation = 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        MsIcon(
                            name = "keyboard_return",
                            contentDescription = "输入到文本框",
                            tint = Color.White
                        )
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .imePadding()
                    .then(
                        if (applyNavigationBarsPadding) {
                            Modifier.windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                        } else {
                            Modifier
                        }
                )
                    .padding(end = cancelEndInset, bottom = fabBottomOffset)
                    .requiredSize(fabSize)
                    .mdCenteredShadow(shape = CircleShape, shadowStyle = MdFabShadowStyle),
                shape = CircleShape,
                color = if (dragTarget == PttConfirmDragTarget.Cancel) {
                    Color(0xFFB00020)
                } else {
                    Color(0xFF202124)
                },
                elevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MsIcon(
                        name = "close",
                        contentDescription = "取消发送",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
internal fun QuickSubtitlePttConfirmBottomStrip(
    guideText: String,
    reserveFabWidth: Dp,
    stripHeight: Dp
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(stripHeight)
            .mdCenteredShadow(
                shape = RoundedCornerShape(42.dp),
                shadowStyle = MdFabShadowStyle
            ),
        shape = RoundedCornerShape(42.dp),
        color = md2ElevatedCardContainerColor(UiTokens.FabElevation),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MsIcon("graphic_eq", contentDescription = "识别中")
            AnimatedContent(
                targetState = guideText,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = fadeIn(animationSpec = tween(150)),
                        initialContentExit = fadeOut(animationSpec = tween(120))
                    )
                },
                label = "ptt_confirm_guide_text_strip"
            ) { text ->
                Text(
                    text = text,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.width(reserveFabWidth))
        }
    }
}

@Composable
internal fun Md2CardTitleText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        fontWeight = FontWeight.Bold
    )
}

@Composable
internal fun GroupIconPickerRow(
    selectedIcon: String,
    iconChoices: List<String>,
    onIconSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(UiTokens.Radius))
            .clickable { showDialog = true }
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
            elevation = 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                MsIcon(selectedIcon, contentDescription = null)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("分组图标", fontWeight = FontWeight.SemiBold)
            Text(
                selectedIcon,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        MsIcon("widgets", contentDescription = "选择分组图标")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("分组图标") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "选择一个用于分组 TAB 和列表展示的图标。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(48.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(iconChoices.size) { index ->
                            val icon = iconChoices[index]
                            val selected = icon == selectedIcon
                            Surface(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        onIconSelected(icon)
                                        showDialog = false
                                    },
                                shape = CircleShape,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f)
                                },
                                border = if (selected) {
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                } else {
                                    null
                                },
                                elevation = 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    MsIcon(icon, contentDescription = icon)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Md2TextButton(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun QuickSubtitleEditorScreen(
    viewModel: MainViewModel,
    onBatchTopBarActionsChange: (EditorBatchTopBarActions?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val tabletLike = minOf(configuration.screenWidthDp, configuration.screenHeightDp) >= 600
    val tabletTwoPane = tabletLike && configuration.screenWidthDp >= 900
    val editorMaxWidth = if (tabletTwoPane) 1180.dp else UiTokens.WideContentMaxWidth
    val editorLeftColumnWidth = if (configuration.screenWidthDp < 720) 260.dp else 320.dp
    val groups = viewModel.quickSubtitleGroups
    val compactControls = viewModel.uiState.quickSubtitleCompactControls
    var selectedGroupIndex by remember(groups, viewModel.quickSubtitleSelectedGroupId) {
        mutableIntStateOf(
            viewModel.currentQuickSubtitleGroupIndex().coerceIn(0, groups.lastIndex.coerceAtLeast(0))
        )
    }
    val selectedGroup = groups.getOrNull(selectedGroupIndex)
    val iconChoices = remember { QuickSubtitleGroupIconChoices }
    val groupNameBringIntoViewRequester = remember { BringIntoViewRequester() }
    val bringIntoViewScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val groupTabsScrollState = rememberScrollState()
    val groupTabsScrollScope = rememberCoroutineScope()
    val pageEdgeScrollScope = rememberCoroutineScope()
    var pendingScrollToNewGroup by remember { mutableIntStateOf(0) }
    var batchSelectionMode by remember { mutableStateOf(false) }
    var selectedItemIndexes by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var showBatchDeleteConfirm by remember { mutableStateOf(false) }
    var showBatchMoveDialog by remember { mutableStateOf(false) }

    fun clearBatchSelection() {
        batchSelectionMode = false
        selectedItemIndexes = emptySet()
    }

    fun enterBatchSelection(index: Int) {
        batchSelectionMode = true
        selectedItemIndexes = selectedItemIndexes + index
    }

    fun toggleBatchSelection(index: Int) {
        selectedItemIndexes = if (index in selectedItemIndexes) {
            selectedItemIndexes - index
        } else {
            selectedItemIndexes + index
        }
        if (selectedItemIndexes.isEmpty()) {
            batchSelectionMode = false
        }
    }

    suspend fun scrollGroupTabsToEndWhenReady(request: Int) {
        repeat(12) {
            delay(16)
            val maxScroll = groupTabsScrollState.maxValue
            if (maxScroll > 0) {
                groupTabsScrollState.scrollTo(maxScroll)
                if (pendingScrollToNewGroup == request) pendingScrollToNewGroup = 0
                return
            }
        }
        groupTabsScrollState.scrollTo(groupTabsScrollState.maxValue)
        if (pendingScrollToNewGroup == request) pendingScrollToNewGroup = 0
    }

    LaunchedEffect(groups.size, pendingScrollToNewGroup) {
        if (pendingScrollToNewGroup <= 0 || groups.isEmpty()) return@LaunchedEffect
        scrollGroupTabsToEndWhenReady(pendingScrollToNewGroup)
    }

    LaunchedEffect(selectedGroup?.items?.size, selectedGroupIndex) {
        val itemCount = selectedGroup?.items?.size ?: 0
        selectedItemIndexes = selectedItemIndexes.filter { it in 0 until itemCount }.toSet()
        if (selectedItemIndexes.isEmpty()) {
            batchSelectionMode = false
        }
    }

    LaunchedEffect(batchSelectionMode, selectedItemIndexes, groups.size, selectedGroupIndex) {
        if (batchSelectionMode) {
            onBatchTopBarActionsChange(
                EditorBatchTopBarActions(
                    onMove = { showBatchMoveDialog = true },
                    onDelete = { showBatchDeleteConfirm = true },
                    onClose = { clearBatchSelection() },
                    canMove = selectedItemIndexes.isNotEmpty() && groups.size > 1,
                    canDelete = selectedItemIndexes.isNotEmpty()
                )
            )
        } else {
            onBatchTopBarActionsChange(null)
        }
    }

    DisposableEffect(Unit) {
        onDispose { onBatchTopBarActionsChange(null) }
    }

    val settingsCard: @Composable () -> Unit = {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2CardContainerColor(),
            elevation = UiTokens.CardElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Md2SettingSwitchRow(
                    title = "使用更紧凑的快捷文本控件",
                    checked = compactControls,
                    onCheckedChange = { viewModel.setQuickSubtitleCompactControls(it) },
                    supportingText = "仅影响主界面竖屏便捷字幕。开启后会压缩快捷文本区高度，并把编辑入口移到顶栏。"
                )
            }
        }
    }

    val groupsCard: @Composable () -> Unit = {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2CardContainerColor(),
            elevation = UiTokens.CardElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Md2CardTitleText("分组", modifier = Modifier.weight(1f))
                    Md2TextButton(onClick = {
                        clearBatchSelection()
                        pendingScrollToNewGroup += 1
                        viewModel.addQuickSubtitleGroup()
                        toast(context, "已新增分组")
                    }) {
                        MsIcon("add", contentDescription = "新增分组")
                        Spacer(Modifier.width(4.dp))
                        Text("新增")
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(groupTabsScrollState)
                ) {
                    Row(
                        modifier = Modifier.onSizeChanged {
                            val request = pendingScrollToNewGroup
                            if (request > 0) {
                                groupTabsScrollScope.launch {
                                    scrollGroupTabsToEndWhenReady(request)
                                }
                            }
                        },
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        groups.forEachIndexed { idx, group ->
                            val selected = idx == selectedGroupIndex
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(UiTokens.Radius))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    )
                                    .clickable {
                                        clearBatchSelection()
                                        selectedGroupIndex = idx
                                        viewModel.selectQuickSubtitleGroup(idx)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val displayTitle = group.title.ifBlank { "未命名分组" }
                                MsIcon(group.icon, contentDescription = displayTitle)
                                Text(displayTitle)
                                Text("(${group.items.size})", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                if (selectedGroup != null) {
                    var groupNameFocused by remember(selectedGroupIndex) { mutableStateOf(false) }
                    Md2OutlinedField(
                        value = selectedGroup.title,
                        onValueChange = {
                            viewModel.updateQuickSubtitleGroupMeta(
                                selectedGroupIndex,
                                it,
                                selectedGroup.icon
                            )
                        },
                        label = "分组名称",
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(groupNameBringIntoViewRequester)
                            .onFocusChanged { focusState ->
                                groupNameFocused = focusState.isFocused
                                if (focusState.isFocused) {
                                    bringIntoViewScope.launch {
                                        groupNameBringIntoViewRequester.bringIntoView()
                                    }
                                }
                            },
                        trailingIcon = if (groupNameFocused && selectedGroup.title.isNotEmpty()) {
                            {
                                Md2ClearFieldButton {
                                    viewModel.updateQuickSubtitleGroupMeta(
                                        selectedGroupIndex,
                                        "",
                                        selectedGroup.icon
                                    )
                                }
                            }
                        } else {
                            null
                        }
                    )
                    GroupIconPickerRow(
                        selectedIcon = selectedGroup.icon,
                        iconChoices = iconChoices,
                        onIconSelected = { icon ->
                            viewModel.updateQuickSubtitleGroupMeta(
                                selectedGroupIndex,
                                selectedGroup.title,
                                icon
                            )
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Md2IconButton(
                            icon = "arrow_back",
                            contentDescription = "分组左移",
                            onClick = {
                                if (selectedGroupIndex > 0) {
                                    clearBatchSelection()
                                    viewModel.moveQuickSubtitleGroup(selectedGroupIndex, selectedGroupIndex - 1)
                                    selectedGroupIndex -= 1
                                }
                            },
                            enabled = selectedGroupIndex > 0
                        )
                        Md2IconButton(
                            icon = "arrow_forward",
                            contentDescription = "分组右移",
                            onClick = {
                                if (selectedGroupIndex < groups.lastIndex) {
                                    clearBatchSelection()
                                    viewModel.moveQuickSubtitleGroup(selectedGroupIndex, selectedGroupIndex + 1)
                                    selectedGroupIndex += 1
                                }
                            },
                            enabled = selectedGroupIndex < groups.lastIndex
                        )
                        Md2IconButton(
                            icon = "delete",
                            contentDescription = "删除分组",
                            onClick = {
                                viewModel.removeQuickSubtitleGroup(selectedGroupIndex)
                                selectedGroupIndex = viewModel.currentQuickSubtitleGroupIndex()
                                clearBatchSelection()
                            },
                            enabled = groups.size > 1
                        )
                    }
                }
            }
        }
    }

    val itemsCard: @Composable (Boolean) -> Unit = { internalScroll ->
        if (selectedGroup != null) {
            QuickSubtitleItemsRecyclerCard(
                modifier = if (internalScroll) Modifier.fillMaxHeight() else Modifier,
                internalListScroll = internalScroll,
                items = selectedGroup.items,
                selectionMode = batchSelectionMode,
                selectedIndexes = selectedItemIndexes,
                parentEdgeScrollBy = if (internalScroll) null else { delta ->
                    val canScroll = if (delta < 0) listState.canScrollBackward else listState.canScrollForward
                    if (canScroll) {
                        pageEdgeScrollScope.launch {
                            listState.scrollBy(delta.toFloat())
                        }
                    }
                    canScroll
                },
                onAdd = {
                    viewModel.addQuickSubtitleItem(selectedGroupIndex, it)
                    toast(context, "已新增快捷文本")
                },
                onItemsChanged = { reordered ->
                    viewModel.setQuickSubtitleItems(selectedGroupIndex, reordered)
                },
                onItemTextChanged = { itemIndex, value ->
                    viewModel.updateQuickSubtitleItem(selectedGroupIndex, itemIndex, value)
                },
                onEnterSelectionMode = { index -> enterBatchSelection(index) },
                onToggleSelection = { index -> toggleBatchSelection(index) }
            )
        }
    }

    CenteredPageBox(
        maxWidth = editorMaxWidth,
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(
                top = UiTokens.PageTopBlank,
                bottom = UiTokens.PageBottomBlank
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (tabletTwoPane) {
                item(key = "quick_subtitle_editor_two_pane") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillParentMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .width(editorLeftColumnWidth)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            settingsCard()
                            groupsCard()
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            itemsCard(true)
                        }
                    }
                }
            } else {
            item(key = "quick_subtitle_editor_settings_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(UiTokens.Radius),
                    backgroundColor = md2CardContainerColor(),
                    elevation = UiTokens.CardElevation
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Md2SettingSwitchRow(
                            title = "使用更紧凑的快捷文本控件",
                            checked = compactControls,
                            onCheckedChange = { viewModel.setQuickSubtitleCompactControls(it) },
                            supportingText = "仅影响主界面竖屏便捷字幕。开启后会压缩快捷文本区高度，并把编辑入口移到顶栏。"
                        )
                    }
                }
            }
            item(key = "groups_card") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(UiTokens.Radius),
                backgroundColor = md2CardContainerColor(),
                elevation = UiTokens.CardElevation
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Md2CardTitleText("分组", modifier = Modifier.weight(1f))
                        Md2TextButton(onClick = {
                            clearBatchSelection()
                            pendingScrollToNewGroup += 1
                            viewModel.addQuickSubtitleGroup()
                            toast(context, "已新增分组")
                        }) {
                            MsIcon("add", contentDescription = "新增分组")
                            Spacer(Modifier.width(4.dp))
                            Text("新增")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(groupTabsScrollState)
                    ) {
                        Row(
                            modifier = Modifier.onSizeChanged {
                                val request = pendingScrollToNewGroup
                                if (request > 0) {
                                    groupTabsScrollScope.launch {
                                        scrollGroupTabsToEndWhenReady(request)
                                    }
                                }
                            },
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            groups.forEachIndexed { idx, group ->
                                val selected = idx == selectedGroupIndex
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(UiTokens.Radius))
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        )
                                        .clickable {
                                            clearBatchSelection()
                                            selectedGroupIndex = idx
                                            viewModel.selectQuickSubtitleGroup(idx)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val displayTitle = group.title.ifBlank { "未命名分组" }
                                    MsIcon(group.icon, contentDescription = displayTitle)
                                    Text(displayTitle)
                                    Text("(${group.items.size})", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    if (selectedGroup != null) {
                        var groupNameFocused by remember(selectedGroupIndex) { mutableStateOf(false) }
                        Md2OutlinedField(
                            value = selectedGroup.title,
                            onValueChange = {
                                viewModel.updateQuickSubtitleGroupMeta(
                                    selectedGroupIndex,
                                    it,
                                    selectedGroup.icon
                                )
                            },
                            label = "分组名称",
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(groupNameBringIntoViewRequester)
                                .onFocusChanged { focusState ->
                                    groupNameFocused = focusState.isFocused
                                    if (focusState.isFocused) {
                                        bringIntoViewScope.launch {
                                            groupNameBringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            trailingIcon = if (groupNameFocused && selectedGroup.title.isNotEmpty()) {
                                {
                                    Md2ClearFieldButton {
                                        viewModel.updateQuickSubtitleGroupMeta(
                                            selectedGroupIndex,
                                            "",
                                            selectedGroup.icon
                                        )
                                    }
                                }
                            } else {
                                null
                            }
                        )
                        GroupIconPickerRow(
                            selectedIcon = selectedGroup.icon,
                            iconChoices = iconChoices,
                            onIconSelected = { icon ->
                                viewModel.updateQuickSubtitleGroupMeta(
                                    selectedGroupIndex,
                                    selectedGroup.title,
                                    icon
                                )
                            }
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Md2IconButton(
                                icon = "arrow_back",
                                contentDescription = "分组左移",
                                onClick = {
                                    if (selectedGroupIndex > 0) {
                                        clearBatchSelection()
                                        viewModel.moveQuickSubtitleGroup(selectedGroupIndex, selectedGroupIndex - 1)
                                        selectedGroupIndex -= 1
                                    }
                                },
                                enabled = selectedGroupIndex > 0
                            )
                            Md2IconButton(
                                icon = "arrow_forward",
                                contentDescription = "分组右移",
                                onClick = {
                                    if (selectedGroupIndex < groups.lastIndex) {
                                        clearBatchSelection()
                                        viewModel.moveQuickSubtitleGroup(selectedGroupIndex, selectedGroupIndex + 1)
                                        selectedGroupIndex += 1
                                    }
                                },
                                enabled = selectedGroupIndex < groups.lastIndex
                            )
                            Md2IconButton(
                                icon = "delete",
                                contentDescription = "删除分组",
                                onClick = {
                                    viewModel.removeQuickSubtitleGroup(selectedGroupIndex)
                                    selectedGroupIndex = viewModel.currentQuickSubtitleGroupIndex()
                                    clearBatchSelection()
                                },
                                enabled = groups.size > 1
                            )
                        }
                    }
                }
            }
        }

        if (selectedGroup != null) {
            item(key = "items_card") {
                QuickSubtitleItemsRecyclerCard(
                    items = selectedGroup.items,
                    selectionMode = batchSelectionMode,
                    selectedIndexes = selectedItemIndexes,
                    parentEdgeScrollBy = { delta ->
                        val canScroll = if (delta < 0) listState.canScrollBackward else listState.canScrollForward
                        if (canScroll) {
                            pageEdgeScrollScope.launch {
                                listState.scrollBy(delta.toFloat())
                            }
                        }
                        canScroll
                    },
                    onAdd = {
                        viewModel.addQuickSubtitleItem(selectedGroupIndex, it)
                        toast(context, "已新增快捷文本")
                    },
                    onItemsChanged = { reordered ->
                        viewModel.setQuickSubtitleItems(selectedGroupIndex, reordered)
                    },
                    onItemTextChanged = { itemIndex, value ->
                        viewModel.updateQuickSubtitleItem(selectedGroupIndex, itemIndex, value)
                    },
                    onEnterSelectionMode = { index -> enterBatchSelection(index) },
                    onToggleSelection = { index -> toggleBatchSelection(index) }
                )
            }
            }
        }
        }
    }

    if (showBatchDeleteConfirm) {
        val count = selectedItemIndexes.size
        AlertDialog(
            onDismissRequest = { showBatchDeleteConfirm = false },
            title = { Text("删除快捷文本") },
            text = { Text("确定删除已选择的 $count 条快捷文本吗？") },
            confirmButton = {
                Md2TextButton(onClick = {
                    val removed = viewModel.removeQuickSubtitleItems(selectedGroupIndex, selectedItemIndexes)
                    showBatchDeleteConfirm = false
                    clearBatchSelection()
                    if (removed > 0) toast(context, "已删除 $removed 条快捷文本")
                }) {
                    Text("删除")
                }
            },
            dismissButton = {
                Md2TextButton(onClick = { showBatchDeleteConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showBatchMoveDialog) {
        AlertDialog(
            onDismissRequest = { showBatchMoveDialog = false },
            title = { Text("移动到其它分组") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(groups) { idx, group ->
                        if (idx != selectedGroupIndex) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(UiTokens.Radius))
                                    .clickable {
                                        val moved = viewModel.moveQuickSubtitleItemsToGroup(
                                            selectedGroupIndex,
                                            selectedItemIndexes,
                                            idx
                                        )
                                        showBatchMoveDialog = false
                                        clearBatchSelection()
                                        if (moved > 0) toast(context, "已移动 $moved 条快捷文本")
                                    }
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MsIcon(group.icon, contentDescription = group.title)
                                Text(
                                    text = group.title.ifBlank { "未命名分组" },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Md2TextButton(onClick = { showBatchMoveDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
internal fun QuickSubtitleItemsRecyclerCard(
    modifier: Modifier = Modifier,
    internalListScroll: Boolean = false,
    items: List<String>,
    selectionMode: Boolean,
    selectedIndexes: Set<Int>,
    parentEdgeScrollBy: ((Int) -> Boolean)? = null,
    onAdd: (String) -> Unit,
    onItemsChanged: (List<String>) -> Unit,
    onItemTextChanged: (Int, String) -> Unit,
    onEnterSelectionMode: (Int) -> Unit,
    onToggleSelection: (Int) -> Unit
) {
    var editTargetIndex by remember(items) { mutableStateOf<Int?>(null) }
    var editText by remember { mutableStateOf("") }
    var editTextFocused by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var addText by remember { mutableStateOf("") }
    var addTextFocused by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(UiTokens.Radius),
        backgroundColor = md2CardContainerColor(),
        elevation = UiTokens.CardElevation
    ) {
        Column(modifier = if (internalListScroll) Modifier.fillMaxSize() else Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Md2CardTitleText("快捷文本", modifier = Modifier.weight(1f))
                Md2TextButton(onClick = {
                    addText = ""
                    showAddDialog = true
                }) {
                    MsIcon("add", contentDescription = "新增文本")
                    Spacer(Modifier.width(4.dp))
                    Text("新增")
                }
            }
            val listModifier = if (internalListScroll) {
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            } else {
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 92.dp)
            }
            QuickSubtitleItemsRecyclerList(
                modifier = listModifier
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                items = items,
                selectionMode = selectionMode,
                selectedIndexes = selectedIndexes,
                onItemsChanged = onItemsChanged,
                onEditRequested = { index, value ->
                    editTargetIndex = index
                    editText = value
                },
                onEnterSelectionMode = onEnterSelectionMode,
                onToggleSelection = onToggleSelection,
                parentEdgeScrollBy = parentEdgeScrollBy,
                nestedScrollingEnabled = internalListScroll
            )
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("新增快捷文本") },
            text = {
                OutlinedTextField(
                    value = addText,
                    onValueChange = { addText = it },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .onFocusChanged { addTextFocused = it.isFocused },
                    label = { Text("快捷文本") },
                    maxLines = 4,
                    shape = Md2ControlShape,
                    trailingIcon = if (addTextFocused && addText.isNotEmpty()) {
                        { Md2ClearFieldButton { addText = "" } }
                    } else {
                        null
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            confirmButton = {
                Md2TextButton(
                    enabled = addText.trim().isNotEmpty(),
                    onClick = {
                        val text = addText.trim()
                        if (text.isNotEmpty()) {
                            onAdd(text)
                            showAddDialog = false
                            addText = ""
                        }
                    }
                ) {
                    Text("添加")
                }
            },
            dismissButton = {
                Md2TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    val editingIndex = editTargetIndex
    if (editingIndex != null && editingIndex in items.indices) {
        AlertDialog(
            onDismissRequest = { editTargetIndex = null },
            title = { Text("编辑快捷文本") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { editTextFocused = it.isFocused },
                    label = { Text("快捷文本") },
                    maxLines = 4,
                    shape = Md2ControlShape,
                    trailingIcon = if (editTextFocused && editText.isNotEmpty()) {
                        { Md2ClearFieldButton { editText = "" } }
                    } else {
                        null
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            confirmButton = {
                Md2TextButton(onClick = {
                    val idx = editTargetIndex
                    if (idx != null && idx in items.indices) {
                        onItemTextChanged(idx, editText)
                    }
                    editTargetIndex = null
                }) {
                    Text("保存")
                }
            },
            dismissButton = {
                Md2TextButton(onClick = { editTargetIndex = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
internal fun QuickSubtitleItemsRecyclerList(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectionMode: Boolean,
    selectedIndexes: Set<Int>,
    onItemsChanged: (List<String>) -> Unit,
    onEditRequested: (Int, String) -> Unit,
    onEnterSelectionMode: (Int) -> Unit,
    onToggleSelection: (Int) -> Unit,
    parentEdgeScrollBy: ((Int) -> Boolean)? = null,
    nestedScrollingEnabled: Boolean = false
) {
    val parentComposition = rememberCompositionContext()
    val onItemsChangedState = rememberUpdatedState(onItemsChanged)
    val onEditRequestedState = rememberUpdatedState(onEditRequested)
    val onEnterSelectionModeState = rememberUpdatedState(onEnterSelectionMode)
    val onToggleSelectionState = rememberUpdatedState(onToggleSelection)

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val recycler = RecyclerView(ctx).apply {
                layoutManager = LinearLayoutManager(ctx)
                overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
                clipToPadding = false
                clipChildren = false
                isNestedScrollingEnabled = nestedScrollingEnabled
                itemAnimator = DefaultItemAnimator().apply {
                    supportsChangeAnimations = false
                    addDuration = 120L
                    removeDuration = 120L
                    moveDuration = 160L
                    changeDuration = 0L
                }
            }

            val adapter = QuickSubtitleItemRecyclerAdapter(
                parentComposition = parentComposition,
                onItemsChanged = { changed -> onItemsChangedState.value(changed) },
                onEditRequested = { index, value -> onEditRequestedState.value(index, value) },
                onEnterSelectionMode = { index -> onEnterSelectionModeState.value(index) },
                onToggleSelection = { index -> onToggleSelectionState.value(index) }
            )
            recycler.adapter = adapter

            val touchCallback = object : ItemTouchHelper.Callback() {
                private var activeViewHolder: RecyclerView.ViewHolder? = null
                private var moved = false
                private val edgeAutoScroller = DragEdgeAutoScroller()

                override fun isLongPressDragEnabled(): Boolean = false
                override fun isItemViewSwipeEnabled(): Boolean = false

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    if (adapter.selectionMode) return makeMovementFlags(0, 0)
                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    return makeMovementFlags(dragFlags, 0)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.bindingAdapterPosition
                    val to = target.bindingAdapterPosition
                    val ok = adapter.move(from, to)
                    moved = moved || ok
                    return ok
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

                override fun onChildDraw(
                    c: android.graphics.Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
                        edgeAutoScroller.update(recyclerView, viewHolder.itemView, dY, parentEdgeScrollBy)
                    } else {
                        edgeAutoScroller.stop()
                    }
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
                        if (activeViewHolder !== viewHolder) activeViewHolder = viewHolder
                        activeViewHolder = viewHolder
                        adapter.setDraggingPosition(viewHolder.bindingAdapterPosition)
                    } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                        edgeAutoScroller.stop()
                        activeViewHolder = null
                        adapter.clearDraggingItem()
                    }
                    adapter.isDragging = actionState == ItemTouchHelper.ACTION_STATE_DRAG
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    edgeAutoScroller.stop()
                    super.clearView(recyclerView, viewHolder)
                    if (activeViewHolder === viewHolder) activeViewHolder = null
                    adapter.isDragging = false
                    adapter.clearDraggingItem()
                    if (moved) {
                        onItemsChangedState.value(adapter.snapshotTexts())
                        moved = false
                    }
                }
            }
            val touchHelper = ItemTouchHelper(touchCallback)
            touchHelper.attachToRecyclerView(recycler)
            adapter.onStartDrag = { vh -> touchHelper.startDrag(vh) }
            recycler
        },
        update = { recycler ->
            recycler.isNestedScrollingEnabled = nestedScrollingEnabled
            val adapter = recycler.adapter as? QuickSubtitleItemRecyclerAdapter ?: return@AndroidView
            adapter.updateSelection(selectionMode, selectedIndexes)
            adapter.submitFromState(items)
        }
    )
}

internal data class QuickSubtitleEditableItem(
    val id: Long,
    var text: String
)

internal class QuickSubtitleItemRecyclerAdapter(
    private val parentComposition: CompositionContext,
    private val onItemsChanged: (List<String>) -> Unit,
    private val onEditRequested: (Int, String) -> Unit,
    private val onEnterSelectionMode: (Int) -> Unit,
    private val onToggleSelection: (Int) -> Unit
) : RecyclerView.Adapter<QuickSubtitleItemRecyclerAdapter.ItemViewHolder>() {

    private val items = mutableListOf<QuickSubtitleEditableItem>()
    private var nextId = 1L
    var isDragging: Boolean = false
    var selectionMode: Boolean = false
        private set
    private var selectedIndexes: Set<Int> = emptySet()
    var onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null
    private var draggingItemId: Long? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = items[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val composeView = ComposeView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setParentCompositionContext(parentComposition)
        }
        return ItemViewHolder(composeView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (!isDragging) {
            holder.itemView.translationZ = 0f
        }
        val row = items[position]
        holder.bind(
            text = row.text,
            isDragged = draggingItemId == row.id,
            selectionMode = selectionMode,
            selected = position in selectedIndexes,
            canDelete = items.size > 1,
            onDelete = {
                val idx = holder.bindingAdapterPosition
                if (idx in items.indices && items.size > 1) {
                    items.removeAt(idx)
                    notifyItemRemoved(idx)
                    onItemsChanged(snapshotTexts())
                }
            },
            onEdit = {
                val idx = holder.bindingAdapterPosition
                if (idx in items.indices) {
                    onEditRequested(idx, items[idx].text)
                }
            },
            onEnterSelectionMode = {
                val idx = holder.bindingAdapterPosition
                if (idx in items.indices) onEnterSelectionMode(idx)
            },
            onToggleSelection = {
                val idx = holder.bindingAdapterPosition
                if (idx in items.indices) onToggleSelection(idx)
            },
            onStartDrag = {
                if (!selectionMode && holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onStartDrag?.invoke(holder)
                }
            }
        )
    }

    fun updateSelection(selectionMode: Boolean, selectedIndexes: Set<Int>) {
        val changed = this.selectionMode != selectionMode || this.selectedIndexes != selectedIndexes
        this.selectionMode = selectionMode
        this.selectedIndexes = selectedIndexes
        if (changed) notifyDataSetChanged()
    }

    fun submitFromState(newItems: List<String>) {
        if (isDragging) return
        val oldItems = items.toList()
        val used = BooleanArray(oldItems.size)
        val mapped = ArrayList<QuickSubtitleEditableItem>(newItems.size)

        for (text in newItems) {
            var matchedIndex = -1
            for (i in oldItems.indices) {
                if (!used[i] && oldItems[i].text == text) {
                    matchedIndex = i
                    break
                }
            }
            if (matchedIndex >= 0) {
                used[matchedIndex] = true
                mapped += oldItems[matchedIndex].copy(text = text)
            } else {
                mapped += QuickSubtitleEditableItem(id = nextId++, text = text)
            }
        }

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size
            override fun getNewListSize(): Int = mapped.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].id == mapped[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].text == mapped[newItemPosition].text
            }
        })

        items.clear()
        items.addAll(mapped)
        if (draggingItemId != null && items.none { it.id == draggingItemId }) {
            draggingItemId = null
        }
        diff.dispatchUpdatesTo(this)
    }

    fun move(from: Int, to: Int): Boolean {
        if (from == to || from !in items.indices || to !in items.indices) return false
        val moved = items.removeAt(from)
        items.add(to, moved)
        notifyItemMoved(from, to)
        return true
    }

    fun snapshotTexts(): List<String> = items.map { it.text }

    fun setDraggingPosition(position: Int) {
        val targetId = items.getOrNull(position)?.id
        if (draggingItemId == targetId) return
        val oldId = draggingItemId
        draggingItemId = targetId
        oldId?.let { id ->
            val idx = items.indexOfFirst { it.id == id }
            if (idx >= 0) notifyItemChanged(idx)
        }
        targetId?.let { id ->
            val idx = items.indexOfFirst { it.id == id }
            if (idx >= 0) notifyItemChanged(idx)
        }
    }

    fun clearDraggingItem() {
        val oldId = draggingItemId ?: return
        draggingItemId = null
        val idx = items.indexOfFirst { it.id == oldId }
        if (idx >= 0) notifyItemChanged(idx)
    }

    class ItemViewHolder(
        private val composeView: ComposeView
    ) : RecyclerView.ViewHolder(composeView) {
        fun bind(
            text: String,
            isDragged: Boolean,
            selectionMode: Boolean,
            selected: Boolean,
            canDelete: Boolean,
            onDelete: () -> Unit,
            onEdit: () -> Unit,
            onEnterSelectionMode: () -> Unit,
            onToggleSelection: () -> Unit,
            onStartDrag: () -> Unit
        ) {
            composeView.setContent {
                KigttsFontScaleProvider {
                    QuickSubtitleEditableRow(
                        value = text,
                        isDragged = isDragged,
                        selectionMode = selectionMode,
                        selected = selected,
                        canDelete = canDelete,
                        onDelete = onDelete,
                        onEdit = onEdit,
                        onEnterSelectionMode = onEnterSelectionMode,
                        onToggleSelection = onToggleSelection,
                        onStartDrag = onStartDrag
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
internal fun QuickSubtitleEditableRow(
    value: String,
    isDragged: Boolean,
    selectionMode: Boolean,
    selected: Boolean,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onEnterSelectionMode: () -> Unit,
    onToggleSelection: () -> Unit,
    onStartDrag: () -> Unit
) {
    val rowElevation by animateDpAsState(
        targetValue = if (isDragged) 10.dp else 0.dp,
        animationSpec = tween(
            durationMillis = if (isDragged) 120 else 160,
            easing = FastOutSlowInEasing
        ),
        label = "quick_subtitle_item_elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .heightIn(min = 72.dp)
            .combinedClickable(
                onClick = {
                    if (selectionMode) onToggleSelection()
                },
                onLongClick = onEnterSelectionMode
            ),
        shape = RoundedCornerShape(UiTokens.Radius),
        backgroundColor = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        } else {
            md2CardContainerColor()
        },
        elevation = rowElevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AnimatedVisibility(visible = selectionMode) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = { onToggleSelection() }
                )
            }
            Text(
                text = value.ifBlank { "（空文本）" },
                modifier = Modifier
                    .weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            AnimatedVisibility(
                visible = !selectionMode,
                enter = fadeIn(animationSpec = tween(120)) + expandHorizontally(animationSpec = tween(120)),
                exit = fadeOut(animationSpec = tween(100)) + shrinkHorizontally(animationSpec = tween(100))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Md2IconButton(
                        icon = "edit",
                        contentDescription = "编辑文本",
                        onClick = onEdit
                    )
                    Md2IconButton(
                        icon = "drag_indicator",
                        contentDescription = "拖动排序",
                        onClick = {},
                        modifier = Modifier.pointerInteropFilter { ev ->
                            when (ev.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    onStartDrag()
                                    true
                                }
                                MotionEvent.ACTION_MOVE,
                                MotionEvent.ACTION_UP,
                                MotionEvent.ACTION_CANCEL -> true
                                else -> false
                            }
                        }
                    )
                    Md2IconButton(
                        icon = "delete",
                        contentDescription = "删除文本",
                        onClick = onDelete,
                        enabled = canDelete
                    )
                }
            }
        }
    }
}


