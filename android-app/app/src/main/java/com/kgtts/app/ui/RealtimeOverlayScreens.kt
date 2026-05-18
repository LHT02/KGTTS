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
fun RealtimeScreen(viewModel: MainViewModel) {
    val recognized = viewModel.realtimeRecognized
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val bottomPadding = pageBottomBlankPadding()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(
            top = UiTokens.PageTopBlank,
            bottom = bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (recognized.isEmpty()) {
            item {
                Text("暂无识别结果", style = MaterialTheme.typography.bodySmall)
            }
        } else {
            items(recognized, key = { it.id }) { item ->
                RecognizedQueueItemCard(
                    item = item,
                    onLongCopy = {
                        if (item.text.isNotBlank()) {
                            clipboard.setText(AnnotatedString(item.text))
                            toast(context, "已复制")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FloatingOverlayScreen(
    viewModel: MainViewModel,
    state: UiState,
    onOpenMainSettings: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val overlayPermissionGranted = remember { mutableStateOf(FloatingOverlayService.canDrawOverlays(context)) }
    val accessibilityPermissionGranted =
        remember { mutableStateOf(VolumeHotkeyAccessibilityService.isEnabled(context)) }
    var pendingOverlayPermissionEnable by remember { mutableStateOf(false) }
    var hotkeyActionPickerSequence by remember { mutableStateOf<VolumeHotkeySequence?>(null) }
    var externalShortcutPickerSequence by remember { mutableStateOf<VolumeHotkeySequence?>(null) }
    var externalShortcutSearchQuery by remember { mutableStateOf("") }
    var externalShortcutChoices by remember { mutableStateOf<List<ExternalShortcutChoice>>(emptyList()) }
    var externalShortcutLoading by remember { mutableStateOf(false) }
    var accessibilityExplainDialogOpen by remember { mutableStateOf(false) }
    var pendingVolumeHotkeyEnableSequence by remember { mutableStateOf<VolumeHotkeySequence?>(null) }
    var dismissVolumeHotkeyEnableWarning by remember { mutableStateOf(false) }
    val overlayPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val granted = FloatingOverlayService.canDrawOverlays(context)
            overlayPermissionGranted.value = granted
            if (granted && pendingOverlayPermissionEnable) {
                viewModel.setFloatingOverlayEnabled(true)
                FloatingOverlayService.start(context)
            } else if (!granted) {
                viewModel.setFloatingOverlayEnabled(false)
                FloatingOverlayService.stop(context)
                toast(context, "需要悬浮窗权限")
            }
            pendingOverlayPermissionEnable = false
        }
    val accessibilitySettingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val enabled = VolumeHotkeyAccessibilityService.isEnabled(context)
            accessibilityPermissionGranted.value = enabled
            if (enabled) {
                VolumeHotkeyAccessibilityGuideService.stop(context)
            }
            scope.launch {
                VolumeHotkeyService.syncWithSettings(context)
            }
        }
    val pendingAccessibilityExplainRequest = viewModel.pendingAccessibilityExplainRequest
    LaunchedEffect(pendingAccessibilityExplainRequest?.requestId) {
        val request = pendingAccessibilityExplainRequest ?: return@LaunchedEffect
        accessibilityExplainDialogOpen = true
        viewModel.consumeAccessibilityExplainRequest(request.requestId)
    }

    LaunchedEffect(Unit) {
        overlayPermissionGranted.value = FloatingOverlayService.canDrawOverlays(context)
        accessibilityPermissionGranted.value = VolumeHotkeyAccessibilityService.isEnabled(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
                overlayPermissionGranted.value = FloatingOverlayService.canDrawOverlays(context)
                accessibilityPermissionGranted.value = VolumeHotkeyAccessibilityService.isEnabled(context)
                if (accessibilityPermissionGranted.value) {
                    VolumeHotkeyAccessibilityGuideService.stop(context)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(externalShortcutPickerSequence) {
        if (externalShortcutPickerSequence == null) return@LaunchedEffect
        externalShortcutLoading = true
        externalShortcutSearchQuery = ""
        externalShortcutChoices = withContext(Dispatchers.IO) {
            ExternalShortcutCatalog.loadAllShortcutChoices(context)
        }
        externalShortcutLoading = false
    }

    val filteredExternalShortcutChoices =
        remember(externalShortcutChoices, externalShortcutSearchQuery) {
            val query = externalShortcutSearchQuery.trim()
            if (query.isBlank()) {
                externalShortcutChoices
            } else {
                externalShortcutChoices.filter { choice ->
                    val haystack = "${choice.appLabel} ${choice.shortcutTitle} ${choice.packageName}"
                    haystack.contains(query, ignoreCase = true)
                }
            }
        }

    val hotkeyMonitorModeLabel =
        when {
            state.volumeHotkeyAccessibilityEnabled && accessibilityPermissionGranted.value ->
                "无障碍按键监听"

            state.volumeHotkeyAccessibilityEnabled ->
                "等待无障碍授权，当前暂用音量变化监听"

            else -> "系统音量变化监听"
        }
    fun openAccessibilitySettingsWithGuide() {
        viewModel.setVolumeHotkeyAccessibilityEnabled(true)
        if (overlayPermissionGranted.value) {
            VolumeHotkeyAccessibilityGuideService.start(context)
        } else {
            toast(context, "未授予悬浮窗权限，引导悬浮窗不会显示")
        }
        accessibilitySettingsLauncher.launch(
            VolumeHotkeyAccessibilityService.buildSettingsIntent()
        )
    }

    fun requestVolumeHotkeyEnabled(sequence: VolumeHotkeySequence, enabled: Boolean) {
        if (!enabled) {
            viewModel.setVolumeHotkeyEnabled(sequence, false)
            return
        }
        if (accessibilityPermissionGranted.value) {
            viewModel.setVolumeHotkeyAccessibilityEnabled(true)
            viewModel.setVolumeHotkeyEnabled(sequence, true)
            return
        }
        if (state.volumeHotkeyEnableWarningDismissed) {
            viewModel.setVolumeHotkeyEnabled(sequence, true)
            return
        }
        dismissVolumeHotkeyEnableWarning = false
        pendingVolumeHotkeyEnableSequence = sequence
    }

    fun persistVolumeHotkeyEnableWarningChoiceIfNeeded() {
        if (dismissVolumeHotkeyEnableWarning) {
            viewModel.setVolumeHotkeyEnableWarningDismissed(true)
        }
    }

    CenteredPageColumn(
        maxWidth = UiTokens.WideContentMaxWidth,
        scroll = scroll
    ) {
        Spacer(Modifier.height(UiTokens.PageTopBlank))

        Md2StaggeredFloatIn(index = 0) {
            Md2SettingsCard(title = "悬浮窗状态") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Md2Switch(
                        checked = state.floatingOverlayEnabled,
                        onCheckedChange = { enabled ->
                            if (!enabled) {
                                pendingOverlayPermissionEnable = false
                                viewModel.setFloatingOverlayEnabled(false)
                                FloatingOverlayService.stop(context)
                            } else if (overlayPermissionGranted.value) {
                                viewModel.setFloatingOverlayEnabled(true)
                                FloatingOverlayService.start(context)
                            } else {
                                pendingOverlayPermissionEnable = true
                                overlayPermissionLauncher.launch(
                                    Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                )
                            }
                        }
                    )
                    Text("启用独立悬浮窗")
                }
                Text(
                    "权限状态：${if (overlayPermissionGranted.value) "已授权" else "未授权"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "运行状态：${if (state.floatingOverlayEnabled && overlayPermissionGranted.value) "已启用" else "未启用"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Md2OutlinedButton(
                        onClick = {
                            pendingOverlayPermissionEnable = false
                            overlayPermissionLauncher.launch(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                            )
                        }
                    ) {
                        Text("打开权限设置")
                    }
                    Md2TextButton(
                        onClick = { FloatingOverlayService.refresh(context) },
                        enabled = state.floatingOverlayEnabled && overlayPermissionGranted.value
                    ) {
                        Text("刷新悬浮窗")
                    }
                }
                Text(
                    "悬浮窗可吸附到屏幕边缘，并可在软件外直接打开快捷字幕、快捷名片、画板和音效板。",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Md2Switch(
                        checked = state.floatingOverlayAutoDock,
                        onCheckedChange = { viewModel.setFloatingOverlayAutoDock(it) }
                    )
                    Text("长时间不操作自动贴边")
                }
                Text(
                    "开启后，悬浮 FAB 在 3 秒无操作时会自动吸附到屏幕边缘，仅露出半边并降低透明度。",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Md2Switch(
                        checked = state.floatingOverlayShowOnLockScreen,
                        onCheckedChange = { viewModel.setFloatingOverlayShowOnLockScreen(it) }
                    )
                    Text("锁屏时显示悬浮窗")
                }
                Text(
                    "开启后会尝试让悬浮窗在锁屏界面上显示并响应操作。部分系统还需要在系统权限中允许锁屏显示或后台弹出界面。",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Md2OutlinedButton(onClick = onOpenMainSettings) {
                    Text("前往主设置页")
                }
            }
        }

        Md2StaggeredFloatIn(index = 1) {
            Md2SettingsCard(title = "音量热键") {
                Text(
                    "序列监听由独立服务处理，不挂在现有悬浮窗服务上。开启无障碍稳定监听后，会优先直接读取音量键事件；未授权时会自动回退到系统音量变化判定。",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Md2Switch(
                        checked = state.volumeHotkeyAccessibilityEnabled,
                        onCheckedChange = { enabled ->
                            if (!enabled) {
                                viewModel.setVolumeHotkeyAccessibilityEnabled(false)
                                VolumeHotkeyAccessibilityGuideService.stop(context)
                                scope.launch {
                                    VolumeHotkeyService.syncWithSettings(context)
                                }
                            } else if (accessibilityPermissionGranted.value) {
                                viewModel.setVolumeHotkeyAccessibilityEnabled(true)
                                scope.launch {
                                    VolumeHotkeyService.syncWithSettings(context)
                                }
                            } else {
                                accessibilityExplainDialogOpen = true
                            }
                        }
                    )
                    Text("优先使用无障碍稳定监听")
                }
                Text(
                    "权限状态：${if (accessibilityPermissionGranted.value) "已开启" else "未开启"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "当前监听方式：$hotkeyMonitorModeLabel",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Md2OutlinedButton(
                        onClick = {
                            if (
                                state.volumeHotkeyAccessibilityEnabled &&
                                !accessibilityPermissionGranted.value
                            ) {
                                accessibilityExplainDialogOpen = true
                            } else {
                                accessibilitySettingsLauncher.launch(
                                    VolumeHotkeyAccessibilityService.buildSettingsIntent()
                                )
                            }
                        }
                    ) {
                        Text("打开无障碍设置")
                    }
                    if (!accessibilityPermissionGranted.value && overlayPermissionGranted.value) {
                        Md2TextButton(
                            onClick = { VolumeHotkeyAccessibilityGuideService.stop(context) }
                        ) {
                            Text("关闭引导悬浮窗")
                        }
                    }
                }
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                )
                Text(
                    "序列判定时间：${"%.1f".format(Locale.US, state.volumeHotkeyWindowMs / 1000f)}s",
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = state.volumeHotkeyWindowMs.toFloat(),
                    onValueChange = { viewModel.setVolumeHotkeyWindowMs(it.roundToInt()) },
                    valueRange = UserPrefs.VOLUME_HOTKEY_MIN_WINDOW_MS.toFloat()..
                        UserPrefs.VOLUME_HOTKEY_MAX_WINDOW_MS.toFloat(),
                    steps = ((UserPrefs.VOLUME_HOTKEY_MAX_WINDOW_MS - UserPrefs.VOLUME_HOTKEY_MIN_WINDOW_MS) / 100) - 1,
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )
                Text(
                    "默认 1.5 秒。时间越长越容易触发，但误触概率也会更高。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f)
                )
                Spacer(Modifier.height(8.dp))
                VolumeHotkeySettingRow(
                    title = "音量加后减",
                    enabled = state.volumeHotkeyUpDownEnabled,
                    actionLabel = VolumeHotkeyActions.labelOf(state.volumeHotkeyUpDownAction),
                    supportingText = "先按音量加，再在设定时间内按音量减。",
                    onEnabledChange = {
                        requestVolumeHotkeyEnabled(VolumeHotkeySequence.UpDown, it)
                    },
                    onPickAction = { hotkeyActionPickerSequence = VolumeHotkeySequence.UpDown }
                )
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                )
                VolumeHotkeySettingRow(
                    title = "音量减后加",
                    enabled = state.volumeHotkeyDownUpEnabled,
                    actionLabel = VolumeHotkeyActions.labelOf(state.volumeHotkeyDownUpAction),
                    supportingText = "先按音量减，再在设定时间内按音量加。",
                    onEnabledChange = {
                        requestVolumeHotkeyEnabled(VolumeHotkeySequence.DownUp, it)
                    },
                    onPickAction = { hotkeyActionPickerSequence = VolumeHotkeySequence.DownUp }
                )
            }
        }

        Spacer(Modifier.height(pageBottomBlankPadding()))
    }

    pendingVolumeHotkeyEnableSequence?.let { sequence ->
        AlertDialog(
            onDismissRequest = {
                pendingVolumeHotkeyEnableSequence = null
                dismissVolumeHotkeyEnableWarning = false
            },
            title = { Text("建议开启无障碍稳定监听") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("部分系统由于设计原因，首次点按音量键时只会弹出音量调整控件，并不会真正调整音量。")
                    Text("未开启无障碍稳定监听时，KIGTTS 只能通过系统音量数值变化判断按键序列，可能需要多按几次音量键才能触发。")
                    Text("开启无障碍稳定监听后，可以直接读取音量键事件，触发会更稳定。")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = dismissVolumeHotkeyEnableWarning,
                            onCheckedChange = { dismissVolumeHotkeyEnableWarning = it }
                        )
                        Text("下次开启不再提示")
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            pendingVolumeHotkeyEnableSequence = null
                            dismissVolumeHotkeyEnableWarning = false
                        }
                    ) {
                        Text("取消")
                    }
                    TextButton(
                        onClick = {
                            persistVolumeHotkeyEnableWarningChoiceIfNeeded()
                            viewModel.setVolumeHotkeyEnabled(sequence, true)
                            pendingVolumeHotkeyEnableSequence = null
                            dismissVolumeHotkeyEnableWarning = false
                        }
                    ) {
                        Text("开启热键")
                    }
                    TextButton(
                        onClick = {
                            persistVolumeHotkeyEnableWarningChoiceIfNeeded()
                            viewModel.setVolumeHotkeyEnabled(sequence, true)
                            pendingVolumeHotkeyEnableSequence = null
                            dismissVolumeHotkeyEnableWarning = false
                            accessibilityExplainDialogOpen = true
                        }
                    ) {
                        Text("开启无障碍")
                    }
                }
            }
        )
    }

    hotkeyActionPickerSequence?.let { sequence ->
        AlertDialog(
            onDismissRequest = { hotkeyActionPickerSequence = null },
            title = {
                Text(
                    if (sequence == VolumeHotkeySequence.UpDown) "音量加后减"
                    else "音量减后加"
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("选择这个序列触发后的功能。", style = MaterialTheme.typography.bodySmall)
                    Text("直接打开", fontWeight = FontWeight.Bold)
                    VolumeHotkeyActions.directOptions.forEach { action ->
                        TextButton(
                            onClick = {
                                viewModel.setVolumeHotkeyAction(sequence, action)
                                hotkeyActionPickerSequence = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                VolumeHotkeyActions.labelOf(action),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
                    Text("悬浮窗", fontWeight = FontWeight.Bold)
                    VolumeHotkeyActions.overlayOptions.forEach { action ->
                        TextButton(
                            onClick = {
                                viewModel.setVolumeHotkeyAction(sequence, action)
                                hotkeyActionPickerSequence = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                VolumeHotkeyActions.labelOf(action),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
                    TextButton(
                        onClick = {
                            externalShortcutSearchQuery = ""
                            externalShortcutPickerSequence = sequence
                            hotkeyActionPickerSequence = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "第三方快捷方式",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { hotkeyActionPickerSequence = null }) {
                    Text("关闭")
                }
            }
        )
    }

    if (accessibilityExplainDialogOpen) {
        AlertDialog(
            onDismissRequest = { accessibilityExplainDialogOpen = false },
            title = { Text("启用无障碍稳定监听") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("KIGTTS 会通过无障碍服务更稳定地监听音量键上下序列，用来触发你配置的音量热键功能。")
                    Text("该服务还会在你主动打开 QQ 扫一扫时读取 QQ 界面节点并执行点击手势，用于直达 QQ 扫码页。")
                    Text("除上述热键和 QQ 扫一扫直达外，不会读取其它应用内容，也不会替你点击其它流程。")
                    Text("确认后会进入系统无障碍页面，请找到“KIGTTS 音量热键辅助”并开启。若已授予悬浮窗权限，会同时显示一个可拖动的步骤提示窗。")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        accessibilityExplainDialogOpen = false
                        openAccessibilitySettingsWithGuide()
                    }
                ) {
                    Text("前往无障碍设置")
                }
            },
            dismissButton = {
                TextButton(onClick = { accessibilityExplainDialogOpen = false }) {
                    Text("取消")
                }
            }
        )
    }

    externalShortcutPickerSequence?.let { sequence ->
        AlertDialog(
            onDismissRequest = {
                externalShortcutPickerSequence = null
                externalShortcutLoading = false
            },
            title = { Text("第三方快捷方式") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "这里读取当前已加入悬浮窗启动器的应用快捷方式；内嵌列表补全关闭时仅保留运行时可查询项和必要的固定入口。",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = externalShortcutSearchQuery,
                        onValueChange = { externalShortcutSearchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("搜索应用或快捷方式") }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 220.dp, max = 320.dp)
                    ) {
                        when {
                            externalShortcutLoading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            filteredExternalShortcutChoices.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("没有可用快捷方式", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            else -> {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredExternalShortcutChoices) { choice ->
                                        TextButton(
                                            onClick = {
                                                viewModel.setVolumeHotkeyAction(
                                                    sequence,
                                                    VolumeHotkeyActions.external(choice)
                                                )
                                                externalShortcutPickerSequence = null
                                                externalShortcutLoading = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                ExternalShortcutChoiceIcon(choice)
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(choice.shortcutTitle)
                                                    Text(
                                                        choice.appLabel,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        externalShortcutPickerSequence = null
                        externalShortcutLoading = false
                    }
                ) {
                    Text("关闭")
                }
            }
        )
    }
}

@Composable
internal fun ExternalShortcutChoiceIcon(
    choice: ExternalShortcutChoice,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        factory = { viewContext ->
            android.widget.ImageView(viewContext).apply {
                scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
                setPadding(5, 5, 5, 5)
            }
        },
        update = { imageView ->
            val icon =
                runCatching {
                    val pm = context.packageManager
                    if (choice.className.isNotBlank()) {
                        pm.getActivityIcon(ComponentName(choice.packageName, choice.className))
                    } else {
                        pm.getApplicationIcon(choice.packageName)
                    }
                }.recoverCatching {
                    context.packageManager.getApplicationIcon(choice.packageName)
                }.getOrElse {
                    ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)
                }
            imageView.setImageDrawable(icon)
        }
    )
}

@Composable
internal fun VolumeHotkeySettingRow(
    title: String,
    enabled: Boolean,
    actionLabel: String,
    supportingText: String,
    onEnabledChange: (Boolean) -> Unit,
    onPickAction: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Md2Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f)
                )
            }
        }
        Text(
            "当前功能：$actionLabel",
            style = MaterialTheme.typography.bodySmall
        )
        Md2OutlinedButton(
            onClick = onPickAction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("配置触发功能")
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun RecognizedQueueItemCard(
    item: RecognizedItem,
    onLongCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongCopy
            ),
        shape = RoundedCornerShape(UiTokens.Radius),
        backgroundColor = md2CardContainerColor(),
        elevation = UiTokens.CardElevation
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(item.text)
            LinearProgressIndicator(
                progress = item.progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}

@Composable
internal fun RunningStatusTopStrip(
    viewModel: MainViewModel,
    status: String,
    pushToTalkMode: Boolean,
    pushToTalkPressed: Boolean,
    ttsDisabled: Boolean,
    playbackGainPercent: Int,
    preferredInputType: Int,
    preferredOutputType: Int,
    inputDeviceLabel: String,
    outputDeviceLabel: String,
    onToggleCollapsed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inputLevel = viewModel.realtimeInputLevel
    val playbackProgress = viewModel.realtimePlaybackProgress
    val micIcon = when {
        ttsDisabled -> "mic_off"
        pushToTalkMode && pushToTalkPressed -> "settings_voice"
        else -> "mic"
    }
    var inputExpanded by remember { mutableStateOf(false) }
    var outputExpanded by remember { mutableStateOf(false) }
    val inputTypeOptions = remember {
        listOf(
            AudioRoutePreference.INPUT_AUTO to "自动",
            AudioRoutePreference.INPUT_BUILTIN_MIC to "内置麦克风/话筒",
            AudioRoutePreference.INPUT_USB to "USB 麦克风",
            AudioRoutePreference.INPUT_BLUETOOTH to "蓝牙麦克风",
            AudioRoutePreference.INPUT_WIRED to "有线麦克风"
        )
    }
    val outputTypeOptions = remember {
        listOf(
            AudioRoutePreference.OUTPUT_AUTO to "自动",
            AudioRoutePreference.OUTPUT_SPEAKER to "扬声器",
            AudioRoutePreference.OUTPUT_EARPIECE to "听筒",
            AudioRoutePreference.OUTPUT_BLUETOOTH to "蓝牙音频",
            AudioRoutePreference.OUTPUT_USB to "USB 音频",
            AudioRoutePreference.OUTPUT_WIRED to "有线耳机/线路"
        )
    }
    Surface(
        modifier = modifier,
        shape = RectangleShape,
        color = md2CardContainerColor(),
        elevation = UiTokens.MenuElevation
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = status,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Md2IconButton(
                    icon = "expand_less",
                    contentDescription = "折叠状态条",
                    onClick = onToggleCollapsed
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Crossfade(
                    targetState = micIcon,
                    animationSpec = tween(durationMillis = 180),
                    label = "running_strip_panel_mic_icon"
                ) { icon ->
                    MsIcon(icon, contentDescription = "麦克风音量")
                }
                LinearProgressIndicator(
                    progress = inputLevel.coerceIn(0f, 1f),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MsIcon("graphic_eq", contentDescription = "识别进度")
                LinearProgressIndicator(
                    progress = playbackProgress.coerceIn(0f, 1f),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true)
                            ) { inputExpanded = true }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MsIcon("mic", contentDescription = "输入设备")
                        Text(
                            text = inputDeviceLabel,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        MsIcon(
                            name = if (inputExpanded) "expand_less" else "expand_more",
                            contentDescription = "选择首选输入设备"
                        )
                    }
                    Md2AnimatedOptionMenu(
                        expanded = inputExpanded,
                        onDismissRequest = { inputExpanded = false }
                    ) {
                        inputTypeOptions.forEach { (value, label) ->
                            M2DropdownMenuItem(
                                onClick = {
                                    inputExpanded = false
                                    viewModel.setPreferredInputType(value)
                                }
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = if (value == preferredInputType) FontWeight.SemiBold else null
                                )
                            }
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true)
                            ) { outputExpanded = true }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MsIcon("volume_up", contentDescription = "输出设备")
                        Text(
                            text = outputDeviceLabel,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        MsIcon(
                            name = if (outputExpanded) "expand_less" else "expand_more",
                            contentDescription = "选择首选输出设备"
                        )
                    }
                    Md2AnimatedOptionMenu(
                        expanded = outputExpanded,
                        onDismissRequest = { outputExpanded = false }
                    ) {
                        outputTypeOptions.forEach { (value, label) ->
                            M2DropdownMenuItem(
                                onClick = {
                                    outputExpanded = false
                                    viewModel.setPreferredOutputType(value)
                                }
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = if (value == preferredOutputType) FontWeight.SemiBold else null
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MsIcon("mic", contentDescription = "按住说话")
                    Text("按住说话", style = MaterialTheme.typography.bodySmall)
                }
                Md2Switch(
                    checked = pushToTalkMode,
                    onCheckedChange = { viewModel.setPushToTalkMode(it) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MsIcon("mic_off", contentDescription = "禁用TTS")
                    Text("禁用TTS", style = MaterialTheme.typography.bodySmall)
                }
                Md2Switch(
                    checked = ttsDisabled,
                    onCheckedChange = { viewModel.setTtsDisabled(it) }
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "音量倍率：${playbackGainPercent}%",
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = playbackGainPercent.toFloat(),
                    onValueChange = { viewModel.setPlaybackGainPercent(it.toInt()) },
                    valueRange = 0f..1000f
                )
            }
        }
    }
}

@Composable
internal fun RunningStripTopBarToggle(
    micLevel: Float,
    playbackProgress: Float,
    expanded: Boolean,
    pushToTalkMode: Boolean,
    pushToTalkPressed: Boolean,
    ttsDisabled: Boolean,
    contentColor: Color,
    onToggle: () -> Unit
) {
    val hapticToggle = rememberKigttsHapticClick(onToggle)
    val micIcon = when {
        ttsDisabled -> "mic_off"
        pushToTalkMode && pushToTalkPressed -> "settings_voice"
        else -> "mic"
    }
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = hapticToggle
            ),
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Crossfade(
                    targetState = micIcon,
                    animationSpec = tween(durationMillis = 180),
                    label = "running_strip_toggle_mic_icon"
                ) { icon ->
                    MsIcon(icon, contentDescription = "麦克风音量", tint = contentColor)
                }
                LinearProgressIndicator(
                    progress = micLevel.coerceIn(0f, 1f),
                    modifier = Modifier
                        .width(30.dp)
                        .height(2.dp),
                    color = contentColor,
                    backgroundColor = contentColor.copy(alpha = 0.24f)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                MsIcon("graphic_eq", contentDescription = "播放进度", tint = contentColor)
                LinearProgressIndicator(
                    progress = playbackProgress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .width(30.dp)
                        .height(2.dp),
                    color = contentColor,
                    backgroundColor = contentColor.copy(alpha = 0.24f)
                )
            }
            MsIcon(
                name = if (expanded) "expand_less" else "expand_more",
                contentDescription = if (expanded) "收起状态条" else "展开状态条",
                tint = contentColor
            )
        }
    }
}


