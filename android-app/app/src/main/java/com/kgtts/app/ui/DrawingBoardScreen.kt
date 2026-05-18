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


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawingBoardScreen(
    viewModel: MainViewModel,
    fullscreen: Boolean,
    onToggleFullscreen: () -> Unit
) {
    val context = LocalContext.current
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isDark = currentAppDarkTheme()
    val state = viewModel.uiState
    val deviceRotationDegrees = when (context.display?.rotation ?: Surface.ROTATION_0) {
        Surface.ROTATION_90 -> 90f
        Surface.ROTATION_180 -> 180f
        Surface.ROTATION_270 -> 270f
        else -> 0f
    }
    val autoRotationDegrees = if (state.drawingKeepCanvasOrientationToDevice) deviceRotationDegrees else 0f
    val manualRotationDegrees = viewModel.drawingManualRotationQuarterTurns * 90f
    val rotationDegrees = ((autoRotationDegrees - manualRotationDegrees) % 360f + 360f) % 360f
    val animatedRotationDegrees by animateFloatAsState(
        targetValue = rotationDegrees,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "drawing_canvas_rotation"
    )
    val boardFillColor = if (isDark) Color(0xFF2C3237) else Color(0xFFFCFDFE)
    val currentPoints = remember { mutableStateListOf<DrawPoint>() }
    var currentStrokeEraser by remember { mutableStateOf(false) }
    var viewportScale by rememberSaveable { mutableFloatStateOf(1f) }
    var viewportPanX by rememberSaveable { mutableFloatStateOf(0f) }
    var viewportPanY by rememberSaveable { mutableFloatStateOf(0f) }
    val toolbarCollapsed = viewModel.drawingToolbarCollapsed
    val navigationBarBottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val hasPortraitThreeButtonNav = !isLandscape && navigationBarBottomInset >= 32.dp
    val portraitToolbarBottomInset = if (hasPortraitThreeButtonNav) navigationBarBottomInset else 0.dp
    val boardReserveEndTarget = if (isLandscape) {
        if (toolbarCollapsed) 76.dp else 128.dp
    } else {
        0.dp
    }
    val boardReserveBottomTarget = if (isLandscape) {
        0.dp
    } else {
        when {
            toolbarCollapsed && hasPortraitThreeButtonNav -> 76.dp
            toolbarCollapsed -> 66.dp
            hasPortraitThreeButtonNav -> 168.dp
            else -> 120.dp
        }
    }
    val boardReserveEnd by animateDpAsState(
        targetValue = boardReserveEndTarget,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "drawing_board_reserve_end"
    )
    val boardReserveBottom by animateDpAsState(
        targetValue = boardReserveBottomTarget,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "drawing_board_reserve_bottom"
    )
    val palette = if (isDark) {
        listOf(
            Color(0xFF7DE8EA),
            Color(0xFF90CAF9),
            Color(0xFFFF9E9E),
            Color(0xFFAEE5B3),
            Color(0xFFFFE08A),
            Color(0xFFECEFF1),
            Color(0xFFD1C4E9)
        )
    } else {
        listOf(
            UiTokens.Primary,
            Color(0xFF1E88E5),
            Color(0xFFE53935),
            Color(0xFF43A047),
            Color(0xFFFFA000),
            Color(0xFF212121),
            Color(0xFF5E35B1)
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val contentHorizontalPadding = if (fullscreen) 0.dp else 16.dp
        val contentVerticalPadding = if (fullscreen) 0.dp else 16.dp
        val paperPadding = if (fullscreen) 8.dp else 16.dp

        val leftActionButtonSize = 36.dp
        val leftColorDotSize = 22.dp
        val leftItemSpacing = 8.dp
        val fixedActionCount = 3
        val fixedColorCount = 7
        val fixedMaxToolbarHeight =
            (leftActionButtonSize * fixedActionCount) +
            (leftItemSpacing * (fixedActionCount - 1)) +
            leftItemSpacing + // action section -> color section gap
            (leftColorDotSize * fixedColorCount) +
            (leftItemSpacing * (fixedColorCount - 1)) +
            (10.dp * 2) // card inner vertical padding

        val landscapeToolbarHeight = remember(maxHeight, fixedMaxToolbarHeight) {
            val verticalSafetyPadding = 16.dp
            val availableHeight = (maxHeight - verticalSafetyPadding * 2).coerceAtLeast(96.dp)
            minOf(availableHeight, fixedMaxToolbarHeight)
        }
        val fixedMaxToolbarWidth =
            (leftActionButtonSize * fixedActionCount) +
            (leftItemSpacing * (fixedActionCount - 1)) +
            10.dp + // left action row -> color row gap
            (leftColorDotSize * fixedColorCount) +
            (leftItemSpacing * (fixedColorCount - 1)) +
            (10.dp * 2) // card inner horizontal padding
        val portraitToolbarWidth = remember(maxWidth, fixedMaxToolbarWidth) {
            val horizontalSafetyPadding = 12.dp
            val availableWidth = (maxWidth - horizontalSafetyPadding * 2).coerceAtLeast(200.dp)
            minOf(availableWidth, fixedMaxToolbarWidth)
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = contentHorizontalPadding, vertical = contentVerticalPadding)
                .padding(end = boardReserveEnd, bottom = boardReserveBottom)
        ) {
            val density = LocalDensity.current
            val boardAspect = 1080f / 1920f
            val quarterTurn = rotationDegrees == 90f || rotationDegrees == 270f
            val displayAspect = if (quarterTurn) 1f / boardAspect else boardAspect
            val availableWidth = (maxWidth - paperPadding * 2).coerceAtLeast(0.dp)
            val availableHeight = (maxHeight - paperPadding * 2).coerceAtLeast(0.dp)
            val candidateWidthByHeight = availableHeight * displayAspect
            val useHeightAsBase = candidateWidthByHeight <= availableWidth
            val cardWidthDp = if (useHeightAsBase) candidateWidthByHeight else availableWidth
            val cardHeightDp = if (useHeightAsBase) availableHeight else (availableWidth / displayAspect)

            var boardSize by remember { mutableStateOf(IntSize.Zero) }
            val fallbackW = with(density) { cardWidthDp.toPx() }
            val fallbackH = with(density) { cardHeightDp.toPx() }
            val canvasW = if (boardSize.width > 0) boardSize.width.toFloat() else fallbackW
            val canvasH = if (boardSize.height > 0) boardSize.height.toFloat() else fallbackH
            val fitW: Float
            val fitH: Float
            if (quarterTurn) {
                // Logical board stays portrait; display becomes landscape after rotation.
                fitW = canvasH
                fitH = canvasW
            } else {
                fitW = canvasW
                fitH = canvasH
            }
            val left = (canvasW - fitW) / 2f
            val top = (canvasH - fitH) / 2f
            val pxScale = minOf(fitW / 1080f, fitH / 1920f)
            val center = Offset(canvasW / 2f, canvasH / 2f)
            val activeEraser = viewModel.drawEraser || currentStrokeEraser
            val activeWidth = if (activeEraser) viewModel.drawEraserSize * 5f else viewModel.drawBrushSize
            val containerW = with(density) { maxWidth.toPx() }
            val containerH = with(density) { maxHeight.toPx() }
            val cardOrigin = Offset(
                x = ((containerW - canvasW) * 0.5f).coerceAtLeast(0f),
                y = ((containerH - canvasH) * 0.5f).coerceAtLeast(0f)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(cardWidthDp, cardHeightDp)
                    .graphicsLayer {
                        scaleX = viewportScale
                        scaleY = viewportScale
                        translationX = viewportPanX
                        translationY = viewportPanY
                    }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { boardSize = it }
                        .mdCenteredShadow(
                            shape = RoundedCornerShape(UiTokens.Radius),
                            shadowStyle = MdCardShadowStyle
                        ),
                    shape = RoundedCornerShape(UiTokens.Radius),
                    backgroundColor = md2CardContainerColor(),
                    elevation = 0.dp
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        withTransform({
                            rotate(degrees = -animatedRotationDegrees, pivot = center)
                        }) {
                            drawRoundRect(
                                color = boardFillColor,
                                topLeft = Offset(left, top),
                                size = Size(fitW, fitH),
                                cornerRadius = CornerRadius(UiTokens.Radius.toPx(), UiTokens.Radius.toPx())
                            )

                            viewModel.drawStrokes.forEach { stroke ->
                                drawStrokeOnBoard(
                                    points = stroke.points,
                                    color = if (stroke.eraser) boardFillColor else stroke.color,
                                    width = stroke.width * pxScale,
                                    left = left,
                                    top = top,
                                    widthPx = fitW,
                                    heightPx = fitH
                                )
                            }
                            if (currentPoints.size > 1) {
                                drawStrokeOnBoard(
                                    points = currentPoints,
                                    color = if (activeEraser) boardFillColor else viewModel.drawColor,
                                    width = activeWidth * pxScale,
                                    left = left,
                                    top = top,
                                    widthPx = fitW,
                                    heightPx = fitH
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(rotationDegrees, fitW, fitH, left, top, canvasW, canvasH, cardOrigin) {
                        fun clampPan(raw: Offset, scale: Float): Offset {
                            val sx = (((canvasW * scale) - canvasW) * 0.5f).coerceAtLeast(0f)
                            val sy = (((canvasH * scale) - canvasH) * 0.5f).coerceAtLeast(0f)
                            return Offset(
                                x = raw.x.coerceIn(-sx, sx),
                                y = raw.y.coerceIn(-sy, sy)
                            )
                        }
                        fun containerToCardLocal(pos: Offset, pan: Offset, scale: Float): Offset {
                            val safeScale = scale.coerceAtLeast(0.0001f)
                            val unpanned = pos - cardOrigin - pan
                            return center + (unpanned - center) / safeScale
                        }
                        fun mapPoint(pos: Offset): Offset {
                            val local = containerToCardLocal(
                                pos = pos,
                                pan = Offset(viewportPanX, viewportPanY),
                                scale = viewportScale
                            )
                            return local.rotateAround(center, rotationDegrees)
                        }
                        fun commitCurrentStroke() {
                            if (currentPoints.size > 1) {
                                viewModel.appendDrawingStroke(
                                    points = currentPoints.toList(),
                                    eraserOverride = currentStrokeEraser
                                )
                            }
                        }
                        fun switchCurrentStrokeEraser(useEraser: Boolean) {
                            if (useEraser == currentStrokeEraser) return
                            val anchor = currentPoints.lastOrNull()
                            commitCurrentStroke()
                            currentPoints.clear()
                            anchor?.let { currentPoints.add(it) }
                            currentStrokeEraser = useEraser
                        }

                        awaitPointerEventScope {
                            while (true) {
                                var drawingActive = false
                                var transformActive = false
                                var drawPointerId: androidx.compose.ui.input.pointer.PointerId?
                                var trackedA: androidx.compose.ui.input.pointer.PointerId? = null
                                var trackedB: androidx.compose.ui.input.pointer.PointerId? = null
                                var lastFocus = Offset.Zero
                                var lastSpan = 0f

                                val down = awaitFirstDown(requireUnconsumed = false)
                                drawPointerId = down.id
                                currentStrokeEraser = viewModel.drawEraser || down.type == PointerType.Eraser
                                val downMapped = mapPoint(down.position)
                                currentPoints.clear()
                                if (downMapped.isInsideBoard(left, top, fitW, fitH)) {
                                    currentPoints.add(downMapped.toDrawPoint(left, top, fitW, fitH))
                                    drawingActive = true
                                }
                                down.consume()

                                while (true) {
                                    val event = awaitPointerEvent()
                                    val pressed = event.changes.filter { it.pressed }

                                    if (pressed.isEmpty()) {
                                        if (!transformActive && drawingActive && currentPoints.size > 1) {
                                            commitCurrentStroke()
                                        }
                                        currentPoints.clear()
                                        currentStrokeEraser = false
                                        break
                                    }

                                    if (pressed.size >= 2) {
                                        val a = trackedA?.let { id -> pressed.firstOrNull { it.id == id } } ?: pressed[0]
                                        val b = trackedB?.let { id -> pressed.firstOrNull { it.id == id } }
                                            ?: pressed.firstOrNull { it.id != a.id }
                                            ?: pressed[1]
                                        trackedA = a.id
                                        trackedB = b.id
                                        drawPointerId = null

                                        val focus = Offset(
                                            x = (a.position.x + b.position.x) * 0.5f,
                                            y = (a.position.y + b.position.y) * 0.5f
                                        )
                                        val span = (a.position - b.position).getDistance().coerceAtLeast(1f)

                                        if (!transformActive) {
                                            transformActive = true
                                            if (drawingActive && currentPoints.size > 1) {
                                                commitCurrentStroke()
                                            }
                                            drawingActive = false
                                            currentPoints.clear()
                                            lastFocus = focus
                                            lastSpan = span
                                        } else {
                                            val oldScale = viewportScale.coerceAtLeast(1f)
                                            val oldPan = Offset(viewportPanX, viewportPanY)
                                            val scaleBy = (span / lastSpan.coerceAtLeast(1f)).coerceIn(0.9f, 1.1f)
                                            val focusDelta = (focus - lastFocus).getDistance()
                                            val scaleDelta = kotlin.math.abs(scaleBy - 1f)
                                            if (focusDelta >= 0.18f || scaleDelta >= 0.0012f) {
                                                val targetScale = (oldScale * scaleBy).coerceIn(1f, 3.5f)
                                                val newScale = if (targetScale < 1.002f) 1f else targetScale
                                                val contentFocus = containerToCardLocal(
                                                    pos = lastFocus,
                                                    pan = oldPan,
                                                    scale = oldScale
                                                )
                                                val rawPan = focus - cardOrigin - center - (contentFocus - center) * newScale
                                                val clamped = if (newScale <= 1f) Offset.Zero else clampPan(rawPan, newScale)
                                                viewportScale = newScale
                                                viewportPanX = clamped.x
                                                viewportPanY = clamped.y
                                            }
                                            lastFocus = focus
                                            lastSpan = span
                                        }
                                        event.changes.forEach { if (it.pressed) it.consume() }
                                        continue
                                    }

                                    val one = drawPointerId?.let { id -> pressed.firstOrNull { it.id == id } } ?: pressed.first()
                                    if (transformActive) {
                                        transformActive = false
                                        drawPointerId = one.id
                                        trackedA = null
                                        trackedB = null
                                        lastSpan = 0f
                                        drawingActive = false
                                        currentPoints.clear()
                                    }

                                    val eventEraser = viewModel.drawEraser || event.usesDrawingTemporaryEraser()
                                    if (!drawingActive) {
                                        currentStrokeEraser = eventEraser
                                        val mapped = mapPoint(one.position)
                                        if (mapped.isInsideBoard(left, top, fitW, fitH)) {
                                            currentPoints.clear()
                                            currentPoints.add(mapped.toDrawPoint(left, top, fitW, fitH))
                                            drawingActive = true
                                        }
                                    } else {
                                        switchCurrentStrokeEraser(eventEraser)
                                        val mapped = mapPoint(one.position)
                                        if (mapped.isInsideBoard(left, top, fitW, fitH)) {
                                            currentPoints.add(mapped.toDrawPoint(left, top, fitW, fitH))
                                        }
                                    }
                                    one.consume()
                                }
                            }
                        }
                    }
            )
        }

        val toolbarAnchorModifier = if (isLandscape) {
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
        } else {
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp + portraitToolbarBottomInset)
        }

        DrawingToolbar(
            modifier = toolbarAnchorModifier,
            isLandscape = isLandscape,
            colors = palette,
            selectedColor = viewModel.drawColor,
            brushSize = viewModel.drawBrushSize,
            eraserSize = viewModel.drawEraserSize,
            eraserEnabled = viewModel.drawEraser,
            visible = !toolbarCollapsed,
            fullscreen = fullscreen,
            onToggleFullscreen = onToggleFullscreen,
            onToggleCollapsed = { viewModel.updateDrawingToolbarCollapsed(!toolbarCollapsed) },
            landscapeToolbarHeight = landscapeToolbarHeight,
            portraitToolbarWidth = portraitToolbarWidth,
            onPickColor = { viewModel.updateDrawColor(it) },
            onBrushSize = { viewModel.updateDrawBrushSize(it) },
            onToggleEraser = { viewModel.updateDrawEraser(it) },
            onClear = { viewModel.clearDrawingBoard() }
        )
        DrawingToolbarMini(
            modifier = toolbarAnchorModifier,
            isLandscape = isLandscape,
            visible = toolbarCollapsed,
            fullscreen = fullscreen,
            onToggleFullscreen = onToggleFullscreen,
            onToggleCollapsed = { viewModel.updateDrawingToolbarCollapsed(!toolbarCollapsed) }
        )
    }
}

@Composable
internal fun DrawingToolbar(
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    colors: List<Color>,
    selectedColor: Color,
    brushSize: Float,
    eraserSize: Float,
    eraserEnabled: Boolean,
    visible: Boolean,
    fullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    onToggleCollapsed: () -> Unit,
    landscapeToolbarHeight: Dp,
    portraitToolbarWidth: Dp,
    onPickColor: (Color) -> Unit,
    onBrushSize: (Float) -> Unit,
    onToggleEraser: (Boolean) -> Unit,
    onClear: () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = if (isLandscape) {
            fadeIn(animationSpec = tween(130)) + androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { full -> full / 2 },
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            )
        } else {
            fadeIn(animationSpec = tween(130)) + slideInVertically(
                initialOffsetY = { full -> full / 2 },
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            )
        },
        exit = if (isLandscape) {
            fadeOut(animationSpec = tween(100)) + androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { full -> full / 2 },
                animationSpec = tween(140, easing = FastOutSlowInEasing)
            )
        } else {
            fadeOut(animationSpec = tween(100)) + slideOutVertically(
                targetOffsetY = { full -> full / 2 },
                animationSpec = tween(140, easing = FastOutSlowInEasing)
            )
        }
    ) {
        Card(
            modifier = (if (isLandscape) Modifier else Modifier.width(portraitToolbarWidth))
                .mdCenteredShadow(
                    shape = RoundedCornerShape(UiTokens.Radius),
                    shadowStyle = MdCardShadowStyle
                ),
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2ElevatedCardContainerColor(),
            elevation = 0.dp
        ) {
            val activeSize = if (eraserEnabled) eraserSize else brushSize
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(landscapeToolbarHeight),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .width(36.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Md2ToolToggle(
                            icon = "edit",
                            selected = !eraserEnabled,
                            onClick = { onToggleEraser(false) },
                            contentDescription = "画笔"
                        )
                        Md2ToolToggle(
                            icon = "ink_eraser",
                            selected = eraserEnabled,
                            onClick = { onToggleEraser(true) },
                            contentDescription = "橡皮擦"
                        )
                        Md2ToolToggle(
                            icon = "delete_sweep",
                            selected = false,
                            onClick = onClear,
                            contentDescription = "清空"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                colors.forEach { color ->
                                    Md2ColorDot(
                                        color = color,
                                        selected = !eraserEnabled && selectedColor == color,
                                        onClick = { onPickColor(color) }
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .width(52.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Md2VerticalSlider(
                                value = activeSize,
                                onValueChange = onBrushSize,
                                valueRange = 2f..48f,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Md2ToolToggle(
                            icon = "chevron_right",
                            selected = false,
                            onClick = onToggleCollapsed,
                            contentDescription = "折叠工具栏"
                        )
                        Md2ToolToggle(
                            icon = if (fullscreen) "fullscreen_exit" else "fullscreen",
                            selected = false,
                            onClick = onToggleFullscreen,
                            contentDescription = if (fullscreen) "退出全屏" else "进入全屏"
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Md2ToolToggle(
                                icon = "edit",
                                selected = !eraserEnabled,
                                onClick = { onToggleEraser(false) },
                                contentDescription = "画笔"
                            )
                            Md2ToolToggle(
                                icon = "ink_eraser",
                                selected = eraserEnabled,
                                onClick = { onToggleEraser(true) },
                                contentDescription = "橡皮擦"
                            )
                            Md2ToolToggle(
                                icon = "delete_sweep",
                                selected = false,
                                onClick = onClear,
                                contentDescription = "清空"
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            colors.forEach { color ->
                                Md2ColorDot(
                                    color = color,
                                    selected = !eraserEnabled && selectedColor == color,
                                    onClick = { onPickColor(color) }
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = activeSize,
                            onValueChange = onBrushSize,
                            valueRange = 2f..48f,
                            modifier = Modifier.weight(1f)
                        )
                        Md2ToolToggle(
                            icon = "expand_more",
                            selected = false,
                            onClick = onToggleCollapsed,
                            contentDescription = "折叠工具栏"
                        )
                        Md2ToolToggle(
                            icon = if (fullscreen) "fullscreen_exit" else "fullscreen",
                            selected = false,
                            onClick = onToggleFullscreen,
                            contentDescription = if (fullscreen) "退出全屏" else "进入全屏"
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DrawingToolbarMini(
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    visible: Boolean,
    fullscreen: Boolean,
    onToggleFullscreen: () -> Unit,
    onToggleCollapsed: () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = if (isLandscape) {
            fadeIn(animationSpec = tween(130)) + androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { full -> full / 2 },
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            )
        } else {
            fadeIn(animationSpec = tween(130)) + slideInVertically(
                initialOffsetY = { full -> full / 2 },
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            )
        },
        exit = if (isLandscape) {
            fadeOut(animationSpec = tween(100)) + androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { full -> full / 2 },
                animationSpec = tween(140, easing = FastOutSlowInEasing)
            )
        } else {
            fadeOut(animationSpec = tween(100)) + slideOutVertically(
                targetOffsetY = { full -> full / 2 },
                animationSpec = tween(140, easing = FastOutSlowInEasing)
            )
        }
    ) {
        Card(
            modifier = Modifier.mdCenteredShadow(
                shape = RoundedCornerShape(UiTokens.Radius),
                shadowStyle = MdCardShadowStyle
            ),
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2ElevatedCardContainerColor(),
            elevation = 0.dp
        ) {
            if (isLandscape) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Md2ToolToggle(
                        icon = "chevron_left",
                        selected = false,
                        onClick = onToggleCollapsed,
                        contentDescription = "展开工具栏"
                    )
                    Md2ToolToggle(
                        icon = if (fullscreen) "fullscreen_exit" else "fullscreen",
                        selected = false,
                        onClick = onToggleFullscreen,
                        contentDescription = if (fullscreen) "退出全屏" else "进入全屏"
                    )
                }
            } else {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Md2ToolToggle(
                        icon = "expand_less",
                        selected = false,
                        onClick = onToggleCollapsed,
                        contentDescription = "展开工具栏"
                    )
                    Md2ToolToggle(
                        icon = if (fullscreen) "fullscreen_exit" else "fullscreen",
                        selected = false,
                        onClick = onToggleFullscreen,
                        contentDescription = if (fullscreen) "退出全屏" else "进入全屏"
                    )
                }
            }
        }
    }
}

@Composable
internal fun Md2ToolToggle(
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
    contentDescription: String
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            ),
        color = bg,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            MsIcon(icon, contentDescription = contentDescription)
        }
    }
}

@Composable
internal fun Md2ColorDot(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Surface(
        modifier = Modifier
            .size(22.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            ),
        shape = CircleShape,
        color = color,
        border = BorderStroke(1.5.dp, borderColor)
    ) {}
}

@Composable
internal fun Md2VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    val min = valueRange.start
    val max = valueRange.endInclusive
    val range = (max - min).coerceAtLeast(0.0001f)
    val coerced = value.coerceIn(min, max)
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
    val activeColor = MaterialTheme.colorScheme.primary

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .pointerInput(min, max) {
                fun yToValue(y: Float): Float {
                    val h = size.height.toFloat().coerceAtLeast(1f)
                    val frac = (1f - (y / h)).coerceIn(0f, 1f)
                    return (min + frac * range).coerceIn(min, max)
                }
                detectDragGestures(
                    onDragStart = { offset ->
                        onValueChange(yToValue(offset.y))
                    },
                    onDrag = { change, _ ->
                        onValueChange(yToValue(change.position.y))
                        change.consume()
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
        ) {
            val trackX = size.width / 2f
            val startY = 0f
            val endY = size.height
            val fraction = ((coerced - min) / range).coerceIn(0f, 1f)
            val thumbY = endY - (endY - startY) * fraction
            val trackW = 4.dp.toPx()

            drawLine(
                color = outlineColor,
                start = Offset(trackX, startY),
                end = Offset(trackX, endY),
                strokeWidth = trackW,
                cap = StrokeCap.Round
            )
            drawLine(
                color = activeColor,
                start = Offset(trackX, endY),
                end = Offset(trackX, thumbY),
                strokeWidth = trackW,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = activeColor,
                radius = 9.dp.toPx(),
                center = Offset(trackX, thumbY)
            )
        }
    }
}

internal fun Offset.isInsideBoard(left: Float, top: Float, width: Float, height: Float): Boolean {
    return x >= left && x <= left + width && y >= top && y <= top + height
}

internal fun Offset.rotateAround(center: Offset, degrees: Float): Offset {
    if (degrees == 0f) return this
    val rad = Math.toRadians(degrees.toDouble())
    val c = cos(rad).toFloat()
    val s = sin(rad).toFloat()
    val dx = x - center.x
    val dy = y - center.y
    return Offset(
        x = center.x + dx * c - dy * s,
        y = center.y + dx * s + dy * c
    )
}

@OptIn(ExperimentalComposeUiApi::class)
internal fun PointerEvent.usesDrawingTemporaryEraser(): Boolean {
    return changes.any { it.pressed && it.type == PointerType.Eraser } ||
        buttons.hasRawButtonMask(MotionEvent.BUTTON_STYLUS_PRIMARY) ||
        buttons.hasRawButtonMask(MotionEvent.BUTTON_STYLUS_SECONDARY)
}

internal fun PointerButtons.hasRawButtonMask(mask: Int): Boolean {
    val packedValue = toString()
        .substringAfter("packedValue=", missingDelimiterValue = "")
        .substringBefore(")")
        .toIntOrNull()
        ?: return false
    return (packedValue and mask) != 0
}

internal fun Offset.toDrawPoint(left: Float, top: Float, width: Float, height: Float): DrawPoint {
    return DrawPoint(
        x = ((x - left) / width).coerceIn(0f, 1f),
        y = ((y - top) / height).coerceIn(0f, 1f)
    )
}

internal fun DrawPoint.toOffset(left: Float, top: Float, width: Float, height: Float): Offset {
    return Offset(
        x = left + x * width,
        y = top + y * height
    )
}

internal fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStrokeOnBoard(
    points: List<DrawPoint>,
    color: Color,
    width: Float,
    left: Float,
    top: Float,
    widthPx: Float,
    heightPx: Float
) {
    if (points.size < 2) return
    for (i in 1 until points.size) {
        drawLine(
            color = color,
            start = points[i - 1].toOffset(left, top, widthPx, heightPx),
            end = points[i].toOffset(left, top, widthPx, heightPx),
            strokeWidth = width,
            cap = StrokeCap.Round
        )
    }
}


