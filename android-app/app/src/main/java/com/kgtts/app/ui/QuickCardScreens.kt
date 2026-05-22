@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.lhtstudio.kigtts.app.ui

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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.lhtstudio.kigtts.app.data.ResourceStorageCleaner
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
internal fun QuickCardNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    onNavReady: () -> Unit,
    onTopBarActionsChange: (QuickCardTopBarActions?) -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose { onTopBarActionsChange(null) }
    }
    val navigateDecodedQrResult: (String, String) -> Unit = { decoded, popRoute ->
        if (isWeChatQrContent(decoded)) {
            if (isPackageInstalled(context, WECHAT_PACKAGE_NAME)) {
                toast(context, "该二维码为微信二维码，需要使用微信进行扫描")
                if (!launchWeChatScanner(context)) {
                    toast(context, "打开微信失败，请手动打开微信扫一扫")
                }
            } else {
                toast(context, "该二维码为微信二维码，需要安装微信")
                val browserTarget = normalizeQrTextToWebUrl(decoded) ?: WECHAT_BROWSER_FALLBACK_URL
                if (!openExternalBrowser(context, browserTarget)) {
                    toast(context, "无法打开系统浏览器")
                }
            }
            navController.popBackStack(popRoute, inclusive = true)
        } else if (QqScannerSupport.isQqQrContent(decoded)) {
            if (isPackageInstalled(context, QqScannerSupport.QQ_PACKAGE_NAME)) {
                val qqAccessibilityEnabled = VolumeHotkeyAccessibilityService.isEnabled(context)
                if (qqAccessibilityEnabled) {
                    if (
                        VolumeHotkeyAccessibilityService.requestOpenQqScanner(context) ||
                        QqScannerSupport.launchQq(context)
                    ) {
                        toast(context, "该二维码为QQ二维码，请使用QQ进行扫描")
                    } else {
                        toast(context, "打开QQ失败，请手动打开QQ扫一扫")
                    }
                } else {
                    if (QqScannerSupport.launchQq(context)) {
                        toast(context, "该二维码为QQ二维码，已跳转至QQ。直达QQ扫一扫需要开启无障碍权限")
                    } else {
                        toast(context, "打开QQ失败，请手动打开QQ扫一扫")
                    }
                }
            } else {
                toast(context, "该二维码为QQ二维码，需要安装QQ")
                val browserTarget = normalizeQrTextToWebUrl(decoded) ?: QqScannerSupport.QQ_BROWSER_FALLBACK_URL
                if (!openExternalBrowser(context, browserTarget)) {
                    toast(context, "无法打开系统浏览器")
                }
            }
            navController.popBackStack(popRoute, inclusive = true)
        } else if (AlipayScannerSupport.isAlipayQrContent(decoded)) {
            if (isPackageInstalled(context, AlipayScannerSupport.ALIPAY_PACKAGE_NAME)) {
                toast(context, "该二维码为支付宝二维码，需要使用支付宝进行扫描")
                if (!AlipayScannerSupport.launchScanner(context)) {
                    toast(context, "打开支付宝失败，请手动打开支付宝扫一扫")
                }
            } else {
                toast(context, "该二维码为支付宝二维码，需要安装支付宝")
                val browserTarget = normalizeQrTextToWebUrl(decoded)
                    ?: AlipayScannerSupport.ALIPAY_BROWSER_FALLBACK_URL
                if (!openExternalBrowser(context, browserTarget)) {
                    toast(context, "无法打开系统浏览器")
                }
            }
            navController.popBackStack(popRoute, inclusive = true)
        } else {
            val url = normalizeQrTextToWebUrl(decoded)
            if (url.isNullOrEmpty()) {
                navController.navigate(QuickCardRoutes.scanText(decoded)) {
                    popUpTo(popRoute) { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                if (openChromeCustomTab(context, url)) {
                    navController.popBackStack(popRoute, inclusive = true)
                } else {
                    navController.navigate(QuickCardRoutes.web(url)) {
                        popUpTo(popRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
    val handleDecodedQrResult: (String) -> Unit = { decoded ->
        navigateDecodedQrResult(decoded, QuickCardRoutes.Scanner)
    }
    NavHost(
        navController = navController,
        startDestination = QuickCardRoutes.Main,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            if (initialState.destination.route == QuickCardRoutes.Main &&
                targetState.destination.route == QuickCardRoutes.Editor
            ) {
                fadeIn(animationSpec = tween(170)) +
                        slideInHorizontally(
                            initialOffsetX = { full -> full / 10 },
                            animationSpec = tween(170, easing = FastOutSlowInEasing)
                        )
            } else {
                fadeIn(animationSpec = tween(120))
            }
        },
        exitTransition = {
            if (initialState.destination.route == QuickCardRoutes.Main &&
                targetState.destination.route == QuickCardRoutes.Editor
            ) {
                fadeOut(animationSpec = tween(120)) +
                        slideOutHorizontally(
                            targetOffsetX = { full -> -full / 12 },
                            animationSpec = tween(120, easing = FastOutSlowInEasing)
                        )
            } else {
                fadeOut(animationSpec = tween(90))
            }
        },
        popEnterTransition = {
            if (initialState.destination.route == QuickCardRoutes.Editor &&
                targetState.destination.route == QuickCardRoutes.Main
            ) {
                fadeIn(animationSpec = tween(150)) +
                        slideInHorizontally(
                            initialOffsetX = { full -> -full / 12 },
                            animationSpec = tween(150, easing = FastOutSlowInEasing)
                        )
            } else {
                fadeIn(animationSpec = tween(120))
            }
        },
        popExitTransition = {
            if (initialState.destination.route == QuickCardRoutes.Editor &&
                targetState.destination.route == QuickCardRoutes.Main
            ) {
                fadeOut(animationSpec = tween(120)) +
                        slideOutHorizontally(
                            targetOffsetX = { full -> full / 14 },
                            animationSpec = tween(120, easing = FastOutSlowInEasing)
                        )
            } else {
                fadeOut(animationSpec = tween(90))
            }
        }
    ) {
        composable(QuickCardRoutes.Main) {
            QuickCardMainScreen(
                viewModel = viewModel,
                onTopBarActionsChange = onTopBarActionsChange,
                onOpenEditor = { cardId ->
                    viewModel.beginEditQuickCard(cardId)
                    navController.navigate(QuickCardRoutes.Editor) { launchSingleTop = true }
                },
                onOpenSort = {
                    navController.navigate(QuickCardRoutes.Sort) { launchSingleTop = true }
                },
                onCreateCard = { type, link ->
                    viewModel.beginCreateQuickCard(type, prefillLink = link)
                    navController.navigate(QuickCardRoutes.Editor) { launchSingleTop = true }
                },
                onOpenScanner = {
                    navController.navigate(QuickCardRoutes.Scanner) { launchSingleTop = true }
                }
            )
        }
        composable(QuickCardRoutes.Sort) {
            QuickCardSortScreen(
                viewModel = viewModel,
                onTopBarActionsChange = onTopBarActionsChange,
                onDone = {
                    navController.popBackStack(QuickCardRoutes.Main, inclusive = false)
                }
            )
        }
        composable(QuickCardRoutes.Editor) {
            QuickCardEditorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onTopBarActionsChange = onTopBarActionsChange
            )
        }
        composable(QuickCardRoutes.Scanner) {
            QuickCardScannerScreen(
                onTopBarActionsChange = onTopBarActionsChange,
                onOpenFailed = { navController.popBackStack() },
                onResult = handleDecodedQrResult,
                onCandidates = { items ->
                    navController.navigate(QuickCardRoutes.scanCandidates(items)) {
                        popUpTo(QuickCardRoutes.Scanner) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = QuickCardRoutes.ScanCandidates,
            arguments = listOf(navArgument("items") { type = NavType.StringType })
        ) { entry ->
            val items = remember(entry) {
                runCatching {
                    val raw = Uri.decode(entry.arguments?.getString("items").orEmpty())
                    val arr = JSONArray(raw)
                    List(arr.length()) { idx -> arr.optString(idx).trim() }
                        .filter { it.isNotEmpty() }
                }.getOrDefault(emptyList())
            }
            QuickCardScanCandidatesScreen(
                items = items,
                onTopBarActionsChange = onTopBarActionsChange,
                onSelect = { decoded ->
                    navigateDecodedQrResult(decoded, QuickCardRoutes.ScanCandidates)
                }
            )
        }
        composable(
            route = QuickCardRoutes.ScanText,
            arguments = listOf(navArgument("text") { type = NavType.StringType })
        ) { entry ->
            QuickCardScanTextScreen(
                text = Uri.decode(entry.arguments?.getString("text").orEmpty()),
                onTopBarActionsChange = onTopBarActionsChange
            )
        }
        composable(
            route = QuickCardRoutes.Web,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { entry ->
            QuickCardWebViewScreen(
                url = Uri.decode(entry.arguments?.getString("url").orEmpty()),
                internalWebViewEnabled = viewModel.uiState.internalWebViewEnabled,
                onTopBarActionsChange = onTopBarActionsChange
            )
        }
    }
    LaunchedEffect(navController) {
        onNavReady()
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun QuickCardMainScreen(
    viewModel: MainViewModel,
    onTopBarActionsChange: (QuickCardTopBarActions?) -> Unit,
    onOpenEditor: (Long) -> Unit,
    onOpenSort: () -> Unit,
    onCreateCard: (QuickCardType, String) -> Unit,
    onOpenScanner: () -> Unit
) {
    val context = LocalContext.current
    val performKeyHaptic = rememberKigttsKeyHaptic()
    val cards = viewModel.quickCards
    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val previewCardId = viewModel.quickCardPreviewCardId
    val previewCard = remember(cards, previewCardId) {
        previewCardId?.let { id -> cards.firstOrNull { it.id == id } }
    }
    val closePreview: () -> Unit = { viewModel.closeQuickCardPreview() }
    val closePreviewWithHaptic: () -> Unit = {
        performKeyHaptic()
        viewModel.closeQuickCardPreview()
    }
    val onCreateCardState = rememberUpdatedState(onCreateCard)
    val onOpenScannerState = rememberUpdatedState(onOpenScanner)
    var cameraPermissionDialogOpen by rememberSaveable { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            onOpenScannerState.value()
        } else {
            toast(context, "未授予相机权限")
        }
    }

    val topActions = remember(cameraPermissionLauncher, context) {
        QuickCardTopBarActions(
            onNew = { onCreateCardState.value(QuickCardType.Text, "") },
            onScan = {
                val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
                if (granted) {
                    onOpenScannerState.value()
                } else {
                    cameraPermissionDialogOpen = true
                }
            }
        )
    }
    if (cameraPermissionDialogOpen) {
        AlertDialog(
            onDismissRequest = { cameraPermissionDialogOpen = false },
            title = { Text("需要相机权限") },
            text = {
                Text("扫一扫需要使用相机预览画面来识别二维码。识别过程在本机完成，KIGTTS 不会上传相机画面或二维码截图。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        cameraPermissionDialogOpen = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("允许并继续")
                }
            },
            dismissButton = {
                TextButton(onClick = { cameraPermissionDialogOpen = false }) {
                    Text("取消")
                }
            }
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, topActions) {
        onTopBarActionsChange(topActions)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onTopBarActionsChange(topActions)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val pageCount = (cards.size + 1).coerceAtLeast(1) // always keep a trailing "new card" page
    val selectedPage = if (cards.isEmpty()) 0 else viewModel.quickCardSelectedIndex.coerceIn(0, cards.lastIndex)
    var pagerPageIndex by rememberSaveable { mutableIntStateOf(selectedPage) }
    var showSortHint by rememberSaveable { mutableStateOf(false) }
    val canShowSortHint = cards.size > 1
    LaunchedEffect(canShowSortHint) {
        if (canShowSortHint && !quickCardSortHintShownThisProcess) {
            quickCardSortHintShownThisProcess = true
            showSortHint = true
            delay(2_000)
            showSortHint = false
        } else if (!canShowSortHint) {
            showSortHint = false
        }
    }
    LaunchedEffect(pageCount, selectedPage) {
        val maxPage = (pageCount - 1).coerceAtLeast(0)
        if (pagerPageIndex > maxPage) {
            pagerPageIndex = maxPage
        } else if (pagerPageIndex < cards.size && pagerPageIndex != selectedPage) {
            // sync real card pages with ViewModel selection; keep trailing placeholder page as-is.
            pagerPageIndex = selectedPage
        }
    }
    val topMargin = UiTokens.PageTopBlank
    val bottomMargin = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(topMargin))
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    key("quick_card_pager_landscape") {
                        Box(modifier = Modifier.fillMaxSize()) {
                            QuickCardPagerView(
                                cards = cards,
                                currentIndex = pagerPageIndex,
                                landscape = true,
                                modifier = Modifier.fillMaxSize(),
                                onPageChanged = { page ->
                                    val safePage = page.coerceIn(0, (pageCount - 1).coerceAtLeast(0))
                                    pagerPageIndex = safePage
                                    if (cards.isNotEmpty() && safePage < cards.size) {
                                        viewModel.updateQuickCardSelectedIndex(safePage.coerceIn(0, cards.lastIndex))
                                    }
                                },
                                onCardClick = { card ->
                                    performKeyHaptic()
                                    if (card == null) {
                                        onCreateCard(QuickCardType.Text, "")
                                    } else {
                                        viewModel.openQuickCardPreview(card.id)
                                    }
                                },
                                onCardLongPress = { card ->
                                    if (card != null) {
                                        performKeyHaptic()
                                        onOpenSort()
                                    }
                                },
                                onEdit = { card ->
                                    performKeyHaptic()
                                    onOpenEditor(card.id)
                                },
                                onShare = { target ->
                                    performKeyHaptic()
                                    shareQuickCard(context, target, true)
                                }
                            )
                            QuickCardSortHintOverlay(
                                visible = showSortHint,
                                landscape = true,
                                onDismiss = { showSortHint = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    QuickCardIndicatorRail(
                        count = pageCount,
                        current = pagerPageIndex,
                        vertical = false
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    key("quick_card_pager_portrait") {
                        Box(modifier = Modifier.fillMaxSize()) {
                            QuickCardPagerView(
                                cards = cards,
                                currentIndex = pagerPageIndex,
                                landscape = false,
                                modifier = Modifier
                                    .fillMaxSize(),
                                onPageChanged = { page ->
                                    val safePage = page.coerceIn(0, (pageCount - 1).coerceAtLeast(0))
                                    pagerPageIndex = safePage
                                    if (cards.isNotEmpty() && safePage < cards.size) {
                                        viewModel.updateQuickCardSelectedIndex(safePage.coerceIn(0, cards.lastIndex))
                                    }
                                },
                                onCardClick = { card ->
                                    performKeyHaptic()
                                    if (card == null) {
                                        onCreateCard(QuickCardType.Text, "")
                                    } else {
                                        viewModel.openQuickCardPreview(card.id)
                                    }
                                },
                                onCardLongPress = { card ->
                                    if (card != null) {
                                        performKeyHaptic()
                                        onOpenSort()
                                    }
                                },
                                onEdit = { card ->
                                    performKeyHaptic()
                                    onOpenEditor(card.id)
                                },
                                onShare = { target ->
                                    performKeyHaptic()
                                    shareQuickCard(context, target, false)
                                }
                            )
                            QuickCardSortHintOverlay(
                                visible = showSortHint,
                                landscape = false,
                                onDismiss = { showSortHint = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    QuickCardIndicatorRail(
                        count = pageCount,
                        current = pagerPageIndex,
                        vertical = false
                    )
                }
            }
            Spacer(Modifier.height(bottomMargin))
        }

    }

    if (previewCardId != null) {
        Dialog(
            onDismissRequest = closePreview,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.52f))
                    .padding(horizontal = 18.dp, vertical = 24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { closePreviewWithHaptic() },
                contentAlignment = Alignment.Center
            ) {
                val dialogCardAspect = if (isLandscape) QUICK_CARD_ASPECT_LANDSCAPE else QUICK_CARD_ASPECT_PORTRAIT
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                val maxCardWidth = if (isLandscape) {
                    maxWidth * QUICK_CARD_LANDSCAPE_CARD_WIDTH_FRACTION
                } else {
                    maxWidth
                }
                val maxCardHeight = maxHeight
                val widthByHeight = maxCardHeight * dialogCardAspect
                val finalWidth = minOf(maxCardWidth, widthByHeight)
                val finalHeight = finalWidth / dialogCardAspect

                    if (previewCard != null) {
                        QuickCardPreviewCard(
                            card = previewCard,
                            landscape = isLandscape,
                            modifier = if (isLandscape) {
                                Modifier.size(width = finalWidth, height = finalHeight)
                            } else {
                                Modifier.width(finalWidth)
                            },
                            onClick = { closePreviewWithHaptic() },
                            onLongClick = {},
                            onEdit = { card ->
                                performKeyHaptic()
                                closePreview()
                                onOpenEditor(card.id)
                            },
                            onShare = { target ->
                                performKeyHaptic()
                                shareQuickCard(context, target, isLandscape)
                            }
                        )
                    } else {
                        Card(
                            modifier = if (isLandscape) {
                                Modifier.size(width = finalWidth, height = finalHeight)
                            } else {
                                Modifier.width(finalWidth)
                            },
                            shape = RoundedCornerShape(UiTokens.Radius),
                            backgroundColor = md2CardContainerColor(),
                            elevation = UiTokens.CardElevation
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
internal fun QuickCardSortHintOverlay(
    visible: Boolean,
    landscape: Boolean,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(170, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                        onDismiss()
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(if (landscape) 0.42f else 0.34f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.42f)
                            )
                        )
                    )
                    .padding(
                        horizontal = if (landscape) 54.dp else 38.dp,
                        vertical = if (landscape) 18.dp else 26.dp
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "长按对名片进行排序和管理",
                    color = Color.White.copy(alpha = 0.86f),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
internal fun QuickCardSortScreen(
    viewModel: MainViewModel,
    onTopBarActionsChange: (QuickCardTopBarActions?) -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val cards = viewModel.quickCards
    val topBlank = UiTokens.PageTopBlank
    val bottomBlank = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 8.dp
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var pendingDeleteIds by remember { mutableStateOf<Set<Long>?>(null) }

    LaunchedEffect(cards, selectedIds, selectionMode) {
        val validIds = cards.mapTo(hashSetOf()) { it.id }
        val filtered = selectedIds.filterTo(hashSetOf()) { it in validIds }
        if (filtered != selectedIds) selectedIds = filtered
        if (selectionMode && cards.isEmpty()) {
            selectionMode = false
            selectedIds = emptySet()
        }
    }

    val topActions = remember(selectionMode, selectedIds, onDone) {
        if (selectionMode) {
            QuickCardTopBarActions(
                onDelete = {
                    if (selectedIds.isNotEmpty()) pendingDeleteIds = selectedIds
                },
                onClose = {
                    selectionMode = false
                    selectedIds = emptySet()
                },
                canDelete = selectedIds.isNotEmpty(),
                canClose = true
            )
        } else {
            QuickCardTopBarActions(
                onConfirm = onDone,
                canConfirm = true
            )
        }
    }
    LaunchedEffect(topActions) {
        onTopBarActionsChange(topActions)
    }

    pendingDeleteIds?.let { deleteIds ->
        val count = deleteIds.size
        AlertDialog(
            onDismissRequest = { pendingDeleteIds = null },
            title = { Text("删除名片") },
            text = {
                Text(if (count > 1) "确定删除选中的 $count 张名片？" else "确定删除这张名片？")
            },
            confirmButton = {
                Md2TextButton(
                    onClick = {
                        pendingDeleteIds = null
                        val removed = viewModel.deleteQuickCardsByIds(deleteIds)
                        if (removed > 0) {
                            selectedIds = selectedIds - deleteIds
                            if (selectedIds.isEmpty()) selectionMode = false
                            toast(context, if (removed > 1) "已删除 $removed 张名片" else "已删除名片")
                        }
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                Md2TextButton(onClick = { pendingDeleteIds = null }) {
                    Text("取消")
                }
            }
        )
    }

    CenteredPageColumn(
        maxWidth = UiTokens.WideListMaxWidth,
        contentSpacing = 0.dp
    ) {
            QuickCardSortRecyclerList(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                cards = cards,
                topBlankHeight = topBlank,
                bottomBlankHeight = bottomBlank,
                selectionMode = selectionMode,
                selectedIds = selectedIds,
                onEnterSelectionMode = { id ->
                    selectionMode = true
                    selectedIds = selectedIds + id
                },
                onToggleSelection = { id ->
                    selectedIds =
                        if (id in selectedIds) selectedIds - id else selectedIds + id
                },
                onDeleteCard = { id -> pendingDeleteIds = setOf(id) },
                onReorder = { ids ->
                    viewModel.reorderQuickCardsByIds(ids)
                }
            )
    }
}

@Composable
internal fun QuickCardSortRecyclerList(
    modifier: Modifier = Modifier,
    cards: List<QuickCard>,
    topBlankHeight: Dp,
    bottomBlankHeight: Dp,
    selectionMode: Boolean,
    selectedIds: Set<Long>,
    onEnterSelectionMode: (Long) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onDeleteCard: (Long) -> Unit,
    onReorder: (List<Long>) -> Unit
) {
    val parentComposition = rememberCompositionContext()
    val density = LocalDensity.current
    val topBlankPx = with(density) { topBlankHeight.roundToPx() }
    val bottomBlankPx = with(density) { bottomBlankHeight.roundToPx() }
    val onReorderState = rememberUpdatedState(onReorder)
    val onEnterSelectionModeState = rememberUpdatedState(onEnterSelectionMode)
    val onToggleSelectionState = rememberUpdatedState(onToggleSelection)
    val onDeleteCardState = rememberUpdatedState(onDeleteCard)

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val recycler = RecyclerView(ctx).apply {
                layoutManager = LinearLayoutManager(ctx)
                overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
                clipToPadding = false
                clipChildren = false
                itemAnimator = DefaultItemAnimator().apply {
                    supportsChangeAnimations = false
                    addDuration = 120L
                    removeDuration = 120L
                    moveDuration = 160L
                    changeDuration = 0L
                }
                setPadding(paddingLeft, topBlankPx, paddingRight, bottomBlankPx)
            }

            val adapter = QuickCardSortRecyclerAdapter(parentComposition = parentComposition)
            recycler.adapter = adapter

            val touchCallback = object : ItemTouchHelper.Callback() {
                private var moved = false
                private val edgeAutoScroller = DragEdgeAutoScroller()

                override fun isLongPressDragEnabled(): Boolean = false
                override fun isItemViewSwipeEnabled(): Boolean = false

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    if (adapter.selectionMode) {
                        return makeMovementFlags(0, 0)
                    }
                    if (viewHolder.bindingAdapterPosition == 0) {
                        return makeMovementFlags(0, 0)
                    }
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
                        edgeAutoScroller.update(recyclerView, viewHolder.itemView, dY)
                    } else {
                        edgeAutoScroller.stop()
                    }
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
                        adapter.setDraggingPosition(viewHolder.bindingAdapterPosition)
                    } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                        edgeAutoScroller.stop()
                        adapter.clearDraggingItem()
                    }
                    adapter.isDragging = actionState == ItemTouchHelper.ACTION_STATE_DRAG
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    edgeAutoScroller.stop()
                    super.clearView(recyclerView, viewHolder)
                    adapter.isDragging = false
                    adapter.clearDraggingItem()
                    if (moved) {
                        onReorderState.value(adapter.snapshotIds())
                        moved = false
                    }
                }
            }
            val touchHelper = ItemTouchHelper(touchCallback)
            touchHelper.attachToRecyclerView(recycler)
            adapter.onStartDrag = { vh -> touchHelper.startDrag(vh) }
            adapter.onEnterSelectionMode = { id -> onEnterSelectionModeState.value(id) }
            adapter.onToggleSelection = { id -> onToggleSelectionState.value(id) }
            adapter.onDeleteCard = { id -> onDeleteCardState.value(id) }
            recycler
        },
        update = { recycler ->
            val adapter = recycler.adapter as? QuickCardSortRecyclerAdapter ?: return@AndroidView
            adapter.onEnterSelectionMode = { id -> onEnterSelectionModeState.value(id) }
            adapter.onToggleSelection = { id -> onToggleSelectionState.value(id) }
            adapter.onDeleteCard = { id -> onDeleteCardState.value(id) }
            adapter.updateSelectionState(selectionMode, selectedIds)
            recycler.setPadding(recycler.paddingLeft, topBlankPx, recycler.paddingRight, bottomBlankPx)
            recycler.post {
                adapter.submitFromState(cards)
            }
        }
    )
}

internal class QuickCardSortRecyclerAdapter(
    private val parentComposition: CompositionContext
) : RecyclerView.Adapter<QuickCardSortRecyclerAdapter.ItemViewHolder>() {
    private val items = mutableListOf<QuickCard>()
    var isDragging: Boolean = false
    var selectionMode: Boolean = false
    var onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null
    var onEnterSelectionMode: ((Long) -> Unit)? = null
    var onToggleSelection: ((Long) -> Unit)? = null
    var onDeleteCard: ((Long) -> Unit)? = null
    private var draggingItemId: Long? = null
    private var selectedIds: Set<Long> = emptySet()

    private companion object {
        const val HEADER_ID = Long.MIN_VALUE + 4601L
        const val HEADER_POSITION = 0
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return if (position == HEADER_POSITION) HEADER_ID else items[position - 1].id
    }

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

    override fun getItemCount(): Int = items.size + 1

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (position == HEADER_POSITION) {
            holder.bindHeader()
            return
        }
        val card = items[position - 1]
        holder.bind(
            card = card,
            isDragged = draggingItemId == card.id,
            selectionMode = selectionMode,
            selected = card.id in selectedIds,
            onStartDrag = {
                if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onStartDrag?.invoke(holder)
                }
            },
            onEnterSelectionMode = { onEnterSelectionMode?.invoke(card.id) },
            onToggleSelection = { onToggleSelection?.invoke(card.id) },
            onDelete = { onDeleteCard?.invoke(card.id) }
        )
    }

    fun submitFromState(newItems: List<QuickCard>) {
        if (isDragging) return
        if (items == newItems) return
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun move(from: Int, to: Int): Boolean {
        val fromIndex = from - 1
        val toIndex = to - 1
        if (fromIndex == toIndex || fromIndex !in items.indices || toIndex !in items.indices) return false
        val moved = items.removeAt(fromIndex)
        items.add(toIndex, moved)
        notifyItemMoved(from, to)
        return true
    }

    fun snapshotIds(): List<Long> = items.map { it.id }

    fun updateSelectionState(selectionMode: Boolean, selectedIds: Set<Long>) {
        if (this.selectionMode == selectionMode && this.selectedIds == selectedIds) return
        this.selectionMode = selectionMode
        this.selectedIds = selectedIds.toSet()
        notifyDataSetChanged()
    }

    fun setDraggingPosition(position: Int) {
        val targetId = items.getOrNull(position - 1)?.id
        if (draggingItemId == targetId) return
        val oldId = draggingItemId
        draggingItemId = targetId
        oldId?.let { id ->
            val idx = items.indexOfFirst { it.id == id }
            if (idx >= 0) notifyItemChanged(idx + 1)
        }
        targetId?.let { id ->
            val idx = items.indexOfFirst { it.id == id }
            if (idx >= 0) notifyItemChanged(idx + 1)
        }
    }

    fun clearDraggingItem() {
        val oldId = draggingItemId ?: return
        draggingItemId = null
        val idx = items.indexOfFirst { it.id == oldId }
        if (idx >= 0) notifyItemChanged(idx + 1)
    }

    class ItemViewHolder(private val composeView: ComposeView) : RecyclerView.ViewHolder(composeView) {
        fun bindHeader() {
            composeView.setContent {
                KigttsFontScaleProvider {
                    QuickCardSortHeaderRow()
                }
            }
        }

        fun bind(
            card: QuickCard,
            isDragged: Boolean,
            selectionMode: Boolean,
            selected: Boolean,
            onStartDrag: () -> Unit,
            onEnterSelectionMode: () -> Unit,
            onToggleSelection: () -> Unit,
            onDelete: () -> Unit
        ) {
            composeView.setContent {
                KigttsFontScaleProvider {
                    QuickCardSortRow(
                        card = card,
                        isDragged = isDragged,
                        selectionMode = selectionMode,
                        selected = selected,
                        onStartDrag = onStartDrag,
                        onEnterSelectionMode = onEnterSelectionMode,
                        onToggleSelection = onToggleSelection,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@Composable
internal fun QuickCardSortHeaderRow() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp),
        shape = RoundedCornerShape(UiTokens.Radius),
        backgroundColor = md2CardContainerColor(),
        elevation = UiTokens.CardElevation
    ) {
        Text(
            text = "拖动右侧排序按钮调整名片顺序",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
internal fun QuickCardSortRow(
    card: QuickCard,
    isDragged: Boolean,
    selectionMode: Boolean,
    selected: Boolean,
    onStartDrag: () -> Unit,
    onEnterSelectionMode: () -> Unit,
    onToggleSelection: () -> Unit,
    onDelete: () -> Unit
) {
    val rowElevation by animateDpAsState(
        targetValue = if (isDragged) 10.dp else UiTokens.CardElevation,
        animationSpec = tween(
            durationMillis = if (isDragged) 120 else 160,
            easing = FastOutSlowInEasing
        ),
        label = "quick_card_sort_item_elevation"
    )
    val cardContainerColor = md2CardContainerColor()
    val rowOverlayColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = tween(140, easing = FastOutSlowInEasing),
        label = "quick_card_sort_selected_overlay"
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
        backgroundColor = cardContainerColor,
        elevation = rowElevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .background(rowOverlayColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AnimatedVisibility(
                    visible = selectionMode,
                    enter = fadeIn(animationSpec = tween(120)) + expandHorizontally(animationSpec = tween(120)),
                    exit = fadeOut(animationSpec = tween(100)) + shrinkHorizontally(animationSpec = tween(100))
                ) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = { onToggleSelection() }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.title.ifBlank { "未命名名片" },
                        style = MaterialTheme.typography.subtitle1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "快捷名片",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Md2IconButton(
                    icon = "delete",
                    contentDescription = "删除名片",
                    onClick = onDelete
                )
                Md2IconButton(
                    icon = "drag_indicator",
                    contentDescription = "拖动排序",
                    onClick = {},
                    enabled = !selectionMode,
                    modifier = Modifier.pointerInteropFilter { ev ->
                        if (selectionMode) return@pointerInteropFilter false
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
            }
        }
    }
}

@Composable
internal fun QuickCardScannerScreen(
    onTopBarActionsChange: (QuickCardTopBarActions?) -> Unit,
    onOpenFailed: () -> Unit,
    onResult: (String) -> Unit,
    onCandidates: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var cameraPermissionDialogOpen by rememberSaveable { mutableStateOf(!cameraPermissionGranted) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        cameraPermissionGranted = granted
        if (!granted) {
            toast(context, "未授予相机权限")
            onOpenFailed()
        }
    }
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val scanner = remember(lifecycleOwner) { createQrMlKitScanner() }
    val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }
    val analyzerExecutor = remember(lifecycleOwner) { Executors.newSingleThreadExecutor() }
    val scanned = remember { AtomicBoolean(false) }
    val analyzing = remember { AtomicBoolean(false) }
    val disposed = remember(lifecycleOwner) { AtomicBoolean(false) }
    val onResultState = rememberUpdatedState(onResult)
    val onCandidatesState = rememberUpdatedState(onCandidates)
    var cameraReady by remember { mutableStateOf(false) }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }
    var minZoomRatio by remember { mutableStateOf(1f) }
    var maxZoomRatio by remember { mutableStateOf(1f) }
    var zoomRatio by remember { mutableStateOf(1f) }
    var torchEnabled by remember { mutableStateOf(false) }
    var flashAvailable by remember { mutableStateOf(false) }
    val scaleDetector = remember(context) {
        ScaleGestureDetector(
            context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val camera = boundCamera ?: return false
                    val target = (zoomRatio * detector.scaleFactor)
                        .coerceIn(minZoomRatio, maxZoomRatio.coerceAtLeast(minZoomRatio))
                    if (kotlin.math.abs(target - zoomRatio) < 0.01f) return false
                    camera.cameraControl.setZoomRatio(target)
                    zoomRatio = target
                    return true
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        onTopBarActionsChange(null)
    }

    DisposableEffect(scanner, analyzerExecutor) {
        onDispose {
            runCatching { scanner.close() }
            analyzerExecutor.shutdown()
        }
    }

    if (cameraPermissionDialogOpen && !cameraPermissionGranted) {
        AlertDialog(
            onDismissRequest = {
                cameraPermissionDialogOpen = false
                onOpenFailed()
            },
            title = { Text("需要相机权限") },
            text = {
                Text("扫一扫需要使用相机预览画面来识别二维码。识别过程在本机完成，KIGTTS 不会上传相机画面或二维码截图。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        cameraPermissionDialogOpen = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("允许并继续")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        cameraPermissionDialogOpen = false
                        onOpenFailed()
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }

    DisposableEffect(previewView, scaleDetector) {
        previewView.setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            false
        }
        onDispose {
            previewView.setOnTouchListener(null)
        }
    }

    DisposableEffect(previewView, lifecycleOwner, cameraPermissionGranted) {
        if (!cameraPermissionGranted) {
            onDispose {}
        } else {
        disposed.set(false)
        scanned.set(false)
        analyzing.set(false)
        cameraReady = false
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val listener = Runnable {
            if (disposed.get()) return@Runnable
            val provider = runCatching { providerFuture.get() }.getOrNull()
            if (provider == null) {
                if (!disposed.get()) {
                    toast(context, "相机初始化失败")
                    onOpenFailed()
                }
                return@Runnable
            }
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(analyzerExecutor) { imageProxy ->
                if (disposed.get() || scanned.get() || !analyzing.compareAndSet(false, true)) {
                    imageProxy.close()
                    return@setAnalyzer
                }
                try {
                    val mediaImage = imageProxy.image
                    if (mediaImage != null && !disposed.get()) {
                        val inputImage =
                            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        val mlTexts = runBlocking {
                            awaitTask(scanner.process(inputImage))?.decodedQrTexts().orEmpty()
                        }
                        when {
                            mlTexts.size > 1 && scanned.compareAndSet(false, true) -> {
                                mainExecutor.execute {
                                    if (!disposed.get()) {
                                        onCandidatesState.value(mlTexts)
                                    }
                                }
                            }
                            mlTexts.size == 1 && scanned.compareAndSet(false, true) -> {
                                val result = mlTexts.first()
                                mainExecutor.execute {
                                    if (!disposed.get()) {
                                        onResultState.value(result)
                                    }
                                }
                            }
                        }
                    }
                    if (!scanned.get() && !disposed.get()) {
                        val zxingText = decodeQrFromImageProxy(imageProxy)?.trim().orEmpty()
                        if (zxingText.isNotEmpty() && scanned.compareAndSet(false, true)) {
                            mainExecutor.execute {
                                if (!disposed.get()) {
                                    onResultState.value(zxingText)
                                }
                            }
                        }
                    }
                } finally {
                    analyzing.set(false)
                    imageProxy.close()
                }
            }

            runCatching {
                provider.unbindAll()
                val camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
                if (disposed.get()) {
                    provider.unbindAll()
                    return@runCatching
                }
                boundCamera = camera
                flashAvailable = camera.cameraInfo.hasFlashUnit()
                torchEnabled = camera.cameraInfo.torchState.value == TorchState.ON
                camera.cameraInfo.zoomState.value?.let { state ->
                    minZoomRatio = state.minZoomRatio
                    maxZoomRatio = state.maxZoomRatio.coerceAtLeast(state.minZoomRatio)
                    zoomRatio = state.zoomRatio.coerceIn(minZoomRatio, maxZoomRatio)
                }
                cameraReady = true
            }.onFailure {
                if (!disposed.get()) {
                    AppLogger.e("quickCard scanner bind failed", it)
                    toast(context, "无法打开相机")
                    onOpenFailed()
                }
            }
        }
        providerFuture.addListener(listener, mainExecutor)
        onDispose {
            disposed.set(true)
            boundCamera = null
            cameraReady = false
            runCatching {
                if (providerFuture.isDone) {
                    providerFuture.get().unbindAll()
                }
            }
            analyzing.set(false)
        }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        val finderSize = if (isLandscape) 214.dp else 260.dp
        val outerMaskColor = Color.Black.copy(alpha = 0.62f)

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val sideMaskWidth = ((maxWidth - finderSize) / 2f).coerceAtLeast(0.dp)
            val verticalMaskHeight = ((maxHeight - finderSize) / 2f).coerceAtLeast(0.dp)

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(verticalMaskHeight)
                    .background(outerMaskColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(verticalMaskHeight)
                    .background(outerMaskColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(sideMaskWidth)
                    .height(finderSize)
                    .background(outerMaskColor)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(sideMaskWidth)
                    .height(finderSize)
                    .background(outerMaskColor)
            )

            Text(
                text = if (cameraReady) "将二维码置于取景框内自动识别" else "正在打开相机...",
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = -(finderSize / 2f) - if (isLandscape) 18.dp else 24.dp)
                    .padding(horizontal = 16.dp)
            )

            Box(
                modifier = Modifier
                    .size(finderSize)
                    .align(Alignment.Center)
            ) {
                QrScannerFinderFrame(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = finderSize / 2f + if (isLandscape) 24.dp else 32.dp)
                    .padding(horizontal = if (isLandscape) 20.dp else 28.dp)
                    .fillMaxWidth(if (isLandscape) 0.62f else 0.78f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MsIcon("zoom_in", contentDescription = "放大", tint = Color.White)
                    Slider(
                        value = zoomRatio,
                        onValueChange = { target ->
                            val camera = boundCamera ?: return@Slider
                            val resolved = target.coerceIn(minZoomRatio, maxZoomRatio.coerceAtLeast(minZoomRatio))
                            camera.cameraControl.setZoomRatio(resolved)
                            zoomRatio = resolved
                        },
                        valueRange = minZoomRatio..maxZoomRatio.coerceAtLeast(minZoomRatio),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colors.primary,
                            activeTrackColor = MaterialTheme.colors.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                CompositionLocalProvider(
                    LocalContentColor provides if (cameraReady && flashAvailable) {
                        Color.White
                    } else {
                        Color.White.copy(alpha = 0.42f)
                    }
                ) {
                    Md2IconButton(
                        icon = if (torchEnabled) "flash_on" else "flash_off",
                        contentDescription = "手电筒",
                        onClick = {
                            val camera = boundCamera ?: return@Md2IconButton
                            val enabled = !torchEnabled
                            camera.cameraControl.enableTorch(enabled)
                            torchEnabled = enabled
                        },
                        enabled = cameraReady && flashAvailable
                    )
                }
            }
        }
    }
}

@Composable
internal fun QrScannerFinderFrame(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cornerRatio = 0.22f
        val thick = size.minDimension * 0.018f
        val thin = thick * 0.42f
        val halfThick = thick / 2f
        val left = halfThick
        val right = (w - halfThick).coerceAtLeast(left)
        val top = halfThick
        val bottom = (h - halfThick).coerceAtLeast(top)
        val innerW = (right - left).coerceAtLeast(0f)
        val innerH = (bottom - top).coerceAtLeast(0f)
        val cornerW = innerW * cornerRatio
        val cornerH = innerH * cornerRatio

        fun hSeg(x1: Float, x2: Float, y: Float, stroke: Float) {
            drawLine(
                color = color,
                start = Offset(x1, y),
                end = Offset(x2, y),
                strokeWidth = stroke,
                cap = StrokeCap.Square
            )
        }

        fun vSeg(x: Float, y1: Float, y2: Float, stroke: Float) {
            drawLine(
                color = color,
                start = Offset(x, y1),
                end = Offset(x, y2),
                strokeWidth = stroke,
                cap = StrokeCap.Square
            )
        }

        hSeg(left, left + cornerW, top, thick)
        hSeg(left + cornerW, right - cornerW, top, thin)
        hSeg(right - cornerW, right, top, thick)

        hSeg(left, left + cornerW, bottom, thick)
        hSeg(left + cornerW, right - cornerW, bottom, thin)
        hSeg(right - cornerW, right, bottom, thick)

        vSeg(left, top, top + cornerH, thick)
        vSeg(left, top + cornerH, bottom - cornerH, thin)
        vSeg(left, bottom - cornerH, bottom, thick)

        vSeg(right, top, top + cornerH, thick)
        vSeg(right, top + cornerH, bottom - cornerH, thin)
        vSeg(right, bottom - cornerH, bottom, thick)
    }
}

@Composable
internal fun QuickCardScanCandidatesScreen(
    items: List<String>,
    onTopBarActionsChange: (QuickCardTopBarActions?) -> Unit,
    onSelect: (String) -> Unit
) {
    val scroll = rememberScrollState()

    LaunchedEffect(Unit) {
        onTopBarActionsChange(null)
    }

    CenteredPageColumn(
        maxWidth = UiTokens.WideContentMaxWidth,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        scroll = scroll,
        horizontalPadding = 20.dp,
        contentSpacing = 12.dp
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "检测到多个二维码",
            style = MaterialTheme.typography.h6
        )
        Text(
            text = "请选择要打开的二维码内容",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f)
        )
        if (items.isEmpty()) {
            Card(
                shape = RoundedCornerShape(UiTokens.Radius),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = UiTokens.CardElevation
            ) {
                Text(
                    text = "没有可用候选项",
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.body1
                )
            }
        } else {
            items.forEachIndexed { index, item ->
                Card(
                    shape = RoundedCornerShape(UiTokens.Radius),
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = UiTokens.CardElevation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(item) }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "候选 ${index + 1}",
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.primary
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.body1,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
internal fun QuickCardScanTextScreen(
    text: String,
    onTopBarActionsChange: (QuickCardTopBarActions?) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val content = remember(text) { text.ifBlank { "(空内容)" } }

    LaunchedEffect(Unit) {
        onTopBarActionsChange(null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SelectionContainer {
                Text(
                    text = content,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Md2Button(onClick = {
                    clipboard.setText(AnnotatedString(content))
                    toast(context, "已复制")
                }) {
                    Text("复制")
                }
                Md2OutlinedButton(onClick = {
                    sharePlainText(context, content, "分享二维码结果")
                }) {
                    Text("分享")
                }
            }
        }
    }
}

@Composable
internal fun QuickCardWebViewScreen(
    url: String,
    internalWebViewEnabled: Boolean,
    onTopBarActionsChange: (QuickCardTopBarActions?) -> Unit
) {
    if (!internalWebViewEnabled) {
        DisposableEffect(Unit) {
            onTopBarActionsChange(null)
            onDispose { onTopBarActionsChange(null) }
        }
        QuickCardExternalLinkPage(url = url)
        return
    }
    if (!isHttpWebUrl(url)) {
        DisposableEffect(Unit) {
            onTopBarActionsChange(null)
            onDispose { onTopBarActionsChange(null) }
        }
        QuickCardWebErrorPage(
            error = QuickCardWebError(
                url = url,
                detail = "内置 WebView 仅允许打开 http/https 页面。"
            ),
            onRetry = {},
            onOpenExternal = {
                if (!openExternalBrowser(it, url)) {
                    toast(it, "无法打开系统浏览器")
                }
            }
        )
        return
    }
    if (!isWebViewAllowedUrl(url)) {
        DisposableEffect(Unit) {
            onTopBarActionsChange(null)
            onDispose { onTopBarActionsChange(null) }
        }
        QuickCardExternalLinkPage(url = url)
        return
    }
    var loading by remember(url) { mutableStateOf(true) }
    var webError by remember(url) { mutableStateOf<QuickCardWebError?>(null) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    val onTopBarActionsChangeState = rememberUpdatedState(onTopBarActionsChange)
    val publishWebActions: () -> Unit = remember {
        {
            val webView = webViewRef.value
            val canBack = webView?.canGoBack() == true
            val canForward = webView?.canGoForward() == true
            onTopBarActionsChangeState.value(
                QuickCardTopBarActions(
                    onWebReload = {
                        webError = null
                        loading = true
                        webViewRef.value?.reload()
                    },
                    onWebBack = if (canBack) ({ webViewRef.value?.goBack() }) else null,
                    onWebForward = if (canForward) ({ webViewRef.value?.goForward() }) else null,
                    canWebReload = webView != null,
                    canWebBack = canBack,
                    canWebForward = canForward
                )
            )
        }
    }
    LaunchedEffect(Unit) {
        publishWebActions()
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef.value?.let { webView ->
                runCatching {
                    webView.stopLoading()
                    webView.destroy()
                }
            }
            webViewRef.value = null
            onTopBarActionsChangeState.value(null)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewRef.value = this
                    settings.javaScriptEnabled = false
                    settings.javaScriptCanOpenWindowsAutomatically = false
                    settings.domStorageEnabled = false
                    settings.databaseEnabled = false
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.allowFileAccessFromFileURLs = false
                    settings.allowUniversalAccessFromFileURLs = false
                    settings.setSupportMultipleWindows(false)
                    settings.mediaPlaybackRequiresUserGesture = true
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
                        android.webkit.CookieManager.getInstance()
                            .setAcceptThirdPartyCookies(this, false)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        settings.safeBrowsingEnabled = true
                    }
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val targetUrl = request?.url?.toString().orEmpty()
                            if (targetUrl.isBlank() || isWebViewAllowedUrl(targetUrl)) {
                                return false
                            }
                            if (!openExternalBrowser(context, targetUrl)) {
                                toast(context, "无法打开外部链接")
                            }
                            return true
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            loading = true
                            webError = null
                            publishWebActions()
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            loading = false
                            publishWebActions()
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            if (request?.isForMainFrame == true) {
                                loading = false
                                webError = QuickCardWebError(
                                    url = request.url?.toString().orEmpty().ifBlank { url },
                                    detail = buildString {
                                        append("错误代码：")
                                        append(error?.errorCode ?: 0)
                                        val description = error?.description?.toString().orEmpty()
                                        if (description.isNotBlank()) {
                                            append('\n')
                                            append(description)
                                        }
                                    }
                                )
                                publishWebActions()
                            }
                        }

                        override fun onReceivedHttpError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            errorResponse: WebResourceResponse?
                        ) {
                            if (request?.isForMainFrame == true) {
                                loading = false
                                val statusCode = errorResponse?.statusCode ?: 0
                                val reason = errorResponse?.reasonPhrase.orEmpty()
                                webError = QuickCardWebError(
                                    url = request.url?.toString().orEmpty().ifBlank { url },
                                    detail = "HTTP $statusCode${if (reason.isBlank()) "" else " $reason"}"
                                )
                                publishWebActions()
                            }
                        }
                    }
                    loadUrl(url)
                    publishWebActions()
                }
            },
            update = { webView ->
                if (!url.equals(webView.url.orEmpty(), ignoreCase = true)) {
                    loading = true
                    webError = null
                    if (isWebViewAllowedUrl(url)) {
                        webView.loadUrl(url)
                    } else {
                        webView.stopLoading()
                        webError = QuickCardWebError(
                            url = url,
                            detail = if (isHttpWebUrl(url)) {
                                "该网址不在内置 WebView 白名单中，请使用外部浏览器打开。"
                            } else {
                                "内置 WebView 仅允许打开 http/https 页面。"
                            }
                        )
                    }
                }
            }
        )
        webError?.let { error ->
            QuickCardWebErrorPage(
                error = error,
                onRetry = {
                    webError = null
                    loading = true
                    webViewRef.value?.loadUrl(error.url)
                },
                onOpenExternal = {
                    if (!openExternalBrowser(it, error.url)) {
                        toast(it, "无法打开系统浏览器")
                    }
                }
            )
        }
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(animationSpec = tween(90)),
            exit = fadeOut(animationSpec = tween(120)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

internal fun isWebViewAllowedUrl(url: String): Boolean {
    val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return false
    val scheme = uri.scheme?.lowercase()
    if (scheme != "https" && scheme != "http") return false
    val host = uri.host
        ?.trim()
        ?.trimEnd('.')
        ?.lowercase()
        ?: return false
    return host == "lhtstudio.com" || host.endsWith(".lhtstudio.com")
}

internal fun isHttpWebUrl(url: String): Boolean {
    val scheme = runCatching { Uri.parse(url).scheme?.lowercase() }.getOrNull()
    return scheme == "https" || scheme == "http"
}

internal data class QuickCardWebError(
    val url: String,
    val detail: String
)

@Composable
internal fun QuickCardExternalLinkPage(url: String) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2CardContainerColor(),
            elevation = UiTokens.CardElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MsIcon(
                    name = "info",
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
                Text(
                    text = "第三方外部链接",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "您正在访问的是第三方外部链接，并非本软件提供的内容。\n继续访问后，您的一切操作与后果均与本软件无关。",
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                )
                SelectionContainer {
                    Text(
                        text = url,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(UiTokens.Radius))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f))
                            .padding(12.dp)
                    )
                }
                Md2Button(
                    onClick = {
                        if (!openExternalBrowser(context, url)) {
                            toast(context, "无法打开系统浏览器")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("使用浏览器打开")
                }
            }
        }
    }
}

@Composable
internal fun QuickCardWebErrorPage(
    error: QuickCardWebError,
    onRetry: () -> Unit,
    onOpenExternal: (Context) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2CardContainerColor(),
            elevation = UiTokens.CardElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MsIcon(
                    name = "info",
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
                Text(
                    text = "无法打开此页面",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "浏览器报错详细信息",
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = error.detail.ifBlank { "未知错误" },
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f)
                    )
                    Text(
                        text = "网址",
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.SemiBold
                    )
                    SelectionContainer {
                        Text(
                            text = error.url,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(UiTokens.Radius))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f))
                                .padding(12.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Md2OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("重试")
                    }
                    Md2Button(
                        onClick = { onOpenExternal(context) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("使用浏览器打开")
                    }
                }
            }
        }
    }
}

internal fun decodeQrFromImageProxy(
    imageProxy: ImageProxy
): String? {
    val width = imageProxy.width
    val height = imageProxy.height
    if (width <= 0 || height <= 0) return null

    val yPlane = imageProxy.planes.firstOrNull() ?: return null
    val rowStride = yPlane.rowStride
    val pixelStride = yPlane.pixelStride
    val yBuffer = yPlane.buffer
    yBuffer.rewind()

    val luminance = ByteArray(width * height)
    if (pixelStride == 1 && rowStride == width) {
        if (yBuffer.remaining() < luminance.size) return null
        yBuffer.get(luminance)
    } else {
        val rowBytes = ByteArray(rowStride)
        var dstOffset = 0
        for (row in 0 until height) {
            val toRead = minOf(rowStride, yBuffer.remaining())
            if (toRead <= 0) break
            yBuffer.get(rowBytes, 0, toRead)
            var col = 0
            var src = 0
            while (col < width && src < toRead) {
                luminance[dstOffset + col] = rowBytes[src]
                col++
                src += pixelStride
            }
            dstOffset += width
        }
    }

    val source = PlanarYUVLuminanceSource(
        luminance,
        width,
        height,
        0,
        0,
        width,
        height,
        false
    )
    return decodeQrWithZxing(source)
}

@Composable
internal fun QuickCardPagerView(
    cards: List<QuickCard>,
    currentIndex: Int,
    landscape: Boolean,
    modifier: Modifier = Modifier,
    onPageChanged: (Int) -> Unit,
    onCardClick: (QuickCard?) -> Unit,
    onCardLongPress: (QuickCard?) -> Unit,
    onEdit: (QuickCard) -> Unit,
    onShare: (QuickCard) -> Unit
) {
    val onPageChangedState by rememberUpdatedState(onPageChanged)
    val onCardClickState by rememberUpdatedState(onCardClick)
    val onCardLongPressState by rememberUpdatedState(onCardLongPress)
    val onEditState by rememberUpdatedState(onEdit)
    val onShareState by rememberUpdatedState(onShare)

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val density = context.resources.displayMetrics.density
            val edgeGuardPx = (density * if (landscape) 56f else 6f).toInt()
            val pageMarginPx = (density * if (landscape) 2f else 0f).toInt()
            ViewPager2(context).apply {
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                offscreenPageLimit = if (landscape) 3 else 2
                clipToPadding = false
                clipChildren = false
                setPadding(edgeGuardPx, 0, edgeGuardPx, 0)
                setPageTransformer(MarginPageTransformer(pageMarginPx))
                (getChildAt(0) as? RecyclerView)?.apply {
                    overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                    clipToPadding = false
                    clipChildren = false
                    itemAnimator = null
                    setHasFixedSize(true)
                }
                var userGestureInProgress = false
                val callback = object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(state: Int) {
                        when (state) {
                            ViewPager2.SCROLL_STATE_DRAGGING -> userGestureInProgress = true
                            ViewPager2.SCROLL_STATE_IDLE -> userGestureInProgress = false
                        }
                    }

                    override fun onPageSelected(position: Int) {
                        // Ignore programmatic page changes caused by data updates/reorder.
                        // Only treat user-driven drags as page-selection input.
                        if (userGestureInProgress) {
                            onPageChangedState(position)
                        }
                    }
                }
                registerOnPageChangeCallback(callback)
                tag = callback
                adapter = QuickCardPagerAdapter()
            }
        },
        update = { pager ->
            val adapter = (pager.adapter as? QuickCardPagerAdapter) ?: return@AndroidView
            val density = pager.context.resources.displayMetrics.density
            val edgeGuardPx = (density * if (landscape) 56f else 6f).toInt()
            val pageMarginPx = (density * if (landscape) 2f else 0f).toInt()
            pager.offscreenPageLimit = if (landscape) 3 else 2
            pager.setPadding(edgeGuardPx, 0, edgeGuardPx, 0)
            pager.setPageTransformer(MarginPageTransformer(pageMarginPx))
            adapter.landscape = landscape
            adapter.onCardClick = onCardClickState
            adapter.onCardLongPress = onCardLongPressState
            adapter.onEdit = onEditState
            adapter.onShare = onShareState
            adapter.submitCards(cards)
            val target = currentIndex.coerceIn(0, (adapter.itemCount - 1).coerceAtLeast(0))
            if (pager.currentItem != target) {
                pager.setCurrentItem(target, false)
            }
        }
    )
}

internal class QuickCardPagerAdapter : RecyclerView.Adapter<QuickCardPagerAdapter.QuickCardPageViewHolder>() {
    private var items: List<QuickCard?> = listOf(null)
    var landscape: Boolean = false
    var onCardClick: (QuickCard?) -> Unit = {}
    var onCardLongPress: (QuickCard?) -> Unit = {}
    var onEdit: (QuickCard) -> Unit = {}
    var onShare: (QuickCard) -> Unit = {}

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position]?.id ?: Long.MIN_VALUE
    }

    fun submitCards(cards: List<QuickCard>) {
        // Always keep trailing placeholder page for creating a new card.
        val next: List<QuickCard?> = cards + listOf(null)
        if (items == next) return
        items = next
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickCardPageViewHolder {
        val composeView = ComposeView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
        return QuickCardPageViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: QuickCardPageViewHolder, position: Int) {
        val card = items[position]
        val isLandscape = landscape
        val click = onCardClick
        val longPress = onCardLongPress
        val edit = onEdit
        val share = onShare
        holder.composeView.setContent {
            KigttsFontScaleProvider {
                val cardAspect = if (isLandscape) QUICK_CARD_ASPECT_LANDSCAPE else QUICK_CARD_ASPECT_PORTRAIT
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val maxCardWidth = if (isLandscape) {
                        maxWidth * QUICK_CARD_LANDSCAPE_CARD_WIDTH_FRACTION
                    } else {
                        maxWidth
                    }
                    val maxCardHeight = maxHeight
                    val widthByHeight = maxCardHeight * cardAspect
                    val finalWidth = minOf(maxCardWidth, widthByHeight)
                    val finalHeight = finalWidth / cardAspect

                    QuickCardPreviewCard(
                        card = card,
                        landscape = isLandscape,
                        modifier = if (isLandscape) {
                            Modifier.size(width = finalWidth, height = finalHeight)
                        } else {
                            Modifier.width(finalWidth)
                        },
                        onClick = { click(card) },
                        onLongClick = { longPress(card) },
                        onEdit = { target -> edit(target) },
                        onShare = { target -> share(target) }
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class QuickCardPageViewHolder(
        val composeView: ComposeView
    ) : RecyclerView.ViewHolder(composeView)
}

@Composable
internal fun QuickCardIndicatorRail(
    count: Int,
    current: Int,
    vertical: Boolean = true
) {
    val safeCount = count.coerceAtLeast(1)
    val dotSize = 6.dp
    val gap = 6.dp
    val contentSpan = dotSize * safeCount + gap * (safeCount - 1)
    val trackModifier = if (vertical) {
        Modifier.width(14.dp).height(contentSpan + 12.dp)
    } else {
        Modifier.height(14.dp).width(contentSpan + 12.dp)
    }
    val arrangement = Arrangement.spacedBy(6.dp)

    Card(
        shape = RoundedCornerShape(50),
        backgroundColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
        elevation = 0.dp
    ) {
        if (vertical) {
            Column(
                modifier = trackModifier.padding(horizontal = 4.dp, vertical = 6.dp),
                verticalArrangement = arrangement,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(count) { index ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (index == current) UiTokens.Primary else Color.White.copy(alpha = 0.85f))
                    )
                }
            }
        } else {
            Row(
                modifier = trackModifier.padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = arrangement,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(count) { index ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (index == current) UiTokens.Primary else Color.White.copy(alpha = 0.85f))
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun QuickCardPreviewCard(
    card: QuickCard?,
    landscape: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEdit: (QuickCard) -> Unit,
    onShare: (QuickCard) -> Unit
) {
    Card(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        shape = RoundedCornerShape(UiTokens.Radius),
        backgroundColor = md2CardContainerColor(),
        elevation = UiTokens.CardElevation
    ) {
        if (card == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(quickCardDisplayAspect(landscape))
                    .padding(16.dp)
                    .clip(RoundedCornerShape(UiTokens.Radius))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MsIcon("add_circle", contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text("点击以新建名片", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            return@Card
        }

        QuickCardUnifiedContent(card = card, landscape = landscape, onEdit = onEdit, onShare = onShare)
    }
}

@Composable
internal fun QuickCardBrandLogo(
    modifier: Modifier = Modifier,
    light: Boolean? = null
) {
    val logoRes = if (light ?: currentAppDarkTheme()) R.drawable.logo_white else R.drawable.logo_black
    Image(
        painter = androidx.compose.ui.res.painterResource(id = logoRes),
        contentDescription = "KIGTTS",
        modifier = modifier.height(18.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
internal fun QuickCardUnifiedContent(
    card: QuickCard,
    landscape: Boolean,
    onEdit: (QuickCard) -> Unit,
    onShare: (QuickCard) -> Unit
) {
    val context = LocalContext.current
    val theme = quickCardThemeColor(card.themeColor)
    val onTheme = quickCardThemeOnColor(theme)
    val linkText = card.link.trim()
    val imageBitmap = rememberQuickCardBitmap(card.heroImagePath(landscape))
    val qrBitmap = rememberQuickCardQrBitmap(linkText)
    val hasLink = linkText.isNotEmpty()
    val hasImage = imageBitmap != null
    val foreground = if (hasImage) Color.White else onTheme
    val titleText = card.title.trim()
    val noteText = card.note.trim()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(quickCardDisplayAspect(landscape))
            .clip(RoundedCornerShape(UiTokens.Radius))
            .background(theme)
    ) {
        if (hasImage) {
            Image(
                bitmap = imageBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(92.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.42f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(86.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.46f))
                        )
                    )
            )
        } else {
            QuickCardDecorativeBackgroundText(
                text = titleText,
                color = foreground.copy(alpha = 0.22f),
                landscape = landscape,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 14.dp, end = 72.dp)
        ) {
            if (titleText.isNotEmpty()) {
                Text(
                    text = titleText,
                    color = foreground,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (noteText.isNotEmpty()) {
                Text(
                    text = noteText,
                    color = foreground.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        QuickCardOverlayIconButton(
            icon = "edit",
            contentDescription = "编辑名片",
            tint = foreground,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
        ) { onEdit(card) }

        if (hasLink) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(if (landscape) 0.28f else 0.46f)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(UiTokens.Radius),
                backgroundColor = Color.White,
                elevation = UiTokens.CardElevation
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "二维码",
                            modifier = Modifier.fillMaxSize(0.86f)
                        )
                    }
                }
            }
        }

        if (hasLink) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(if (landscape) 0.56f else 0.74f)
                    .padding(start = 14.dp, bottom = 10.dp, end = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickCardOverlayIconButton(
                        icon = "open_in_new",
                        contentDescription = "打开链接",
                        tint = foreground
                    ) { openQuickCardLink(context, linkText) }
                    QuickCardOverlayIconButton(
                        icon = "share",
                        contentDescription = "分享链接",
                        tint = foreground
                    ) { onShare(card) }
                }
                Text(
                    text = linkText,
                    color = foreground,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        QuickCardBrandLogo(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 14.dp),
            light = hasImage || foreground == Color.White
        )
    }
}

@Composable
internal fun QuickCardDecorativeBackgroundText(
    text: String,
    color: Color,
    landscape: Boolean,
    modifier: Modifier = Modifier
) {
    if (text.isBlank()) return
    var textWidthPx by remember(text) { mutableIntStateOf(0) }
    Box(modifier = modifier) {
        if (landscape) {
            Text(
                text = text,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .graphicsLayer(clip = false)
                    .width(520.dp)
                    .padding(start = 12.dp, bottom = 6.dp)
            )
        } else {
            val topPad = 10.dp
            val topPadPx = with(LocalDensity.current) { topPad.toPx() }
            Text(
                text = text,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 88.sp,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                onTextLayout = { textWidthPx = it.size.width },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 10.dp)
                    .graphicsLayer(
                        rotationZ = 90f,
                        transformOrigin = TransformOrigin(1f, 0f),
                        translationY = textWidthPx.toFloat() + topPadPx,
                        clip = false
                    )
            )
        }
    }
}

@Composable
internal fun QuickCardPortraitContent(
    card: QuickCard,
    onEdit: (QuickCard) -> Unit,
    onShare: (QuickCard) -> Unit
) {
    val theme = quickCardThemeColor(card.themeColor)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(QUICK_CARD_CONTENT_ASPECT_PORTRAIT)
                .clip(RoundedCornerShape(UiTokens.Radius))
        ) {
            QuickCardHeroArea(
                card = card,
                landscape = false,
                modifier = Modifier.fillMaxSize(),
                onShare = onShare
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if (card.title.isNotBlank()) {
                        Text(
                            card.title.trim(),
                            style = MaterialTheme.typography.h6,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (card.note.isNotBlank()) {
                        Text(
                            card.note.trim(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.width(6.dp))
                Md2IconButton(
                    icon = "edit",
                    contentDescription = "编辑名片",
                    onClick = { onEdit(card) }
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickCardBrandLogo()
            }
            Spacer(Modifier.height(1.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(theme)
        )
    }
}

@Composable
internal fun QuickCardLandscapeContent(
    card: QuickCard,
    onEdit: (QuickCard) -> Unit,
    onShare: (QuickCard) -> Unit
) {
    val theme = quickCardThemeColor(card.themeColor)
    val density = LocalDensity.current
    val designHeight = 180.dp
    val heroWidth = designHeight * QUICK_CARD_ASPECT_LANDSCAPE
    val detailsWidth = 132.dp
    val groupWidth = heroWidth + 10.dp + detailsWidth + 8.dp + 4.dp
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val scale = minOf(
            with(density) { maxWidth.toPx() / groupWidth.toPx() },
            with(density) { maxHeight.toPx() / designHeight.toPx() }
        )
        Box(
            modifier = Modifier.size(groupWidth * scale, designHeight * scale),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .size(groupWidth, designHeight)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        transformOrigin = TransformOrigin.Center
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(width = heroWidth, height = designHeight)
                        .clip(RoundedCornerShape(UiTokens.Radius))
                ) {
                    QuickCardHeroArea(
                        card = card,
                        landscape = true,
                        modifier = Modifier.fillMaxSize(),
                        onShare = onShare
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .width(detailsWidth)
                        .fillMaxHeight()
                        .padding(vertical = 2.dp)
                ) {
                    if (card.title.isNotBlank()) {
                        Text(
                            card.title.trim(),
                            style = MaterialTheme.typography.h6,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (card.note.isNotBlank()) {
                        Text(
                            card.note.trim(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Md2IconButton(
                            icon = "edit",
                            contentDescription = "编辑名片",
                            onClick = { onEdit(card) }
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        QuickCardBrandLogo()
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(theme)
                )
            }
        }
    }
}

@Composable
internal fun QuickCardHeroArea(
    card: QuickCard,
    landscape: Boolean,
    modifier: Modifier,
    onShare: (QuickCard) -> Unit
) {
    val context = LocalContext.current
    val theme = quickCardThemeColor(card.themeColor)
    val onTheme = quickCardThemeOnColor(theme)
    val linkText = card.link.trim()
    val imagePath = card.heroImagePath(landscape)
    val imageBitmap = rememberQuickCardBitmap(imagePath)
    val qrBitmap = rememberQuickCardQrBitmap(linkText)
    val showLinkShare = linkText.isNotEmpty() && (card.type == QuickCardType.Qr || card.type == QuickCardType.Text)
    val showImageLinkActions = card.type == QuickCardType.Image && linkText.isNotEmpty()

    Box(modifier = modifier.background(theme)) {
        when (card.type) {
            QuickCardType.Image -> {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("未设置图片", color = onTheme.copy(alpha = 0.85f))
                    }
                }
                if (showImageLinkActions) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .height(if (landscape) 84.dp else 96.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.42f)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = linkText,
                            modifier = Modifier.weight(1f),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            QuickCardOverlayIconButton(
                                icon = "open_in_new",
                                contentDescription = "打开链接",
                                tint = Color.White
                            ) { openQuickCardLink(context, linkText) }
                            QuickCardOverlayIconButton(
                                icon = "share",
                                contentDescription = "分享链接",
                                tint = Color.White
                            ) { sharePlainText(context, linkText, "分享链接") }
                        }
                    }
                }
            }

            QuickCardType.Qr -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (qrBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(if (landscape) 116.dp else 146.dp)
                                .clip(RoundedCornerShape(UiTokens.Radius))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "二维码",
                                modifier = Modifier.fillMaxSize(0.86f)
                            )
                        }
                    } else {
                        Text("未设置链接", color = onTheme.copy(alpha = 0.9f))
                    }
                    if (linkText.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = linkText,
                            color = onTheme,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        CompositionLocalProvider(LocalContentColor provides onTheme) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Md2IconButton(
                                    icon = "open_in_new",
                                    contentDescription = "打开链接",
                                    onClick = { openQuickCardLink(context, linkText) }
                                )
                                Md2IconButton(
                                    icon = "share",
                                    contentDescription = "分享链接",
                                    onClick = { onShare(card) }
                                )
                            }
                        }
                    }
                }
            }

            QuickCardType.Text -> {
                val watermark = card.title.trim()
                var portraitWatermarkWidthPx by remember(watermark) { mutableIntStateOf(0) }
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (landscape) {
                        Text(
                            text = watermark,
                            color = onTheme.copy(alpha = 0.22f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 64.sp,
                            softWrap = false,
                            maxLines = 1,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .graphicsLayer(clip = false)
                                .width(maxWidth * 1.8f)
                                .padding(start = 8.dp, bottom = 4.dp)
                        )
                    } else {
                        val topPad = 10.dp
                        val topPadPx = with(LocalDensity.current) { topPad.toPx() }
                        Text(
                            text = watermark,
                            color = onTheme.copy(alpha = 0.22f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 96.sp,
                            softWrap = false,
                            maxLines = 1,
                            overflow = TextOverflow.Visible,
                            onTextLayout = { portraitWatermarkWidthPx = it.size.width },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 10.dp)
                                .graphicsLayer(
                                    rotationZ = 90f,
                                    transformOrigin = TransformOrigin(1f, 0f),
                                    translationY = portraitWatermarkWidthPx.toFloat() + topPadPx,
                                    clip = false
                                )
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 16.dp)
                ) {
                    if (card.title.isNotBlank()) {
                        Text(
                            text = card.title.trim(),
                            color = onTheme,
                            style = MaterialTheme.typography.h5,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (card.note.isNotBlank()) {
                        Text(
                            text = card.note,
                            color = onTheme.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    if (showLinkShare) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = linkText,
                                modifier = Modifier.weight(1f),
                                color = onTheme,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            CompositionLocalProvider(LocalContentColor provides onTheme) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Md2IconButton(
                                        icon = "open_in_new",
                                        contentDescription = "打开链接",
                                        onClick = { openQuickCardLink(context, linkText) }
                                    )
                                    Md2IconButton(
                                        icon = "share",
                                        contentDescription = "分享链接",
                                        onClick = { onShare(card) }
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

@Composable
internal fun QuickCardOverlayIconButton(
    icon: String,
    contentDescription: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    KigttsIconButton(
        onClick = onClick,
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
    ) {
        MsIcon(
            name = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Composable
internal fun QuickCardTypeChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    if (selected) {
        Md2Button(onClick = onClick) { Text(label) }
    } else {
        Md2OutlinedButton(onClick = onClick) { Text(label) }
    }
}

@Composable
internal fun QuickCardEditorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onTopBarActionsChange: (QuickCardTopBarActions?) -> Unit
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val draft = viewModel.quickCardDraft
    var cropLandscape by rememberSaveable { mutableStateOf(false) }
    var activeCropLandscape by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }
    var showThemeColorDialog by remember { mutableStateOf(false) }
    var showBuiltinGalleryPicker by remember { mutableStateOf(false) }
    var themeHexInput by rememberSaveable { mutableStateOf("#038387") }
    var themeHue by rememberSaveable { mutableFloatStateOf(180f) }
    var themeSat by rememberSaveable { mutableFloatStateOf(1f) }
    var themeLight by rememberSaveable { mutableFloatStateOf(0.27f) }
    var exitConfirmAutoSaveChecked by remember { mutableStateOf(false) }
    var suppressNullDraftAutoBack by remember { mutableStateOf(false) }
    val presetColors = remember {
        listOf(
            "#f44336", "#e91e63", "#9c27b0", "#673ab7", "#3f51b5",
            "#2196f3", "#03a9f4", "#00bcd4", "#009688", "#4caf50",
            "#8bc34a", "#cddc39", "#ffeb3b", "#ffc107", "#ff9800",
            "#ff5722", "#795548", "#9e9e9e", "#607d8b", "#038387"
        )
    }
    fun normalizeHexOrNull(raw: String): String? {
        val v = raw.trim().let { if (it.startsWith("#")) it else "#$it" }
        return if (Regex("^#[0-9a-fA-F]{6}$").matches(v)) v.lowercase(Locale.US) else null
    }
    fun syncThemePickerFromHex(hex: String) {
        themeHexInput = normalizeHexOrNull(hex) ?: "#038387"
        val hsl = composeColorToHsl(quickCardThemeColor(themeHexInput))
        themeHue = hsl[0]
        themeSat = hsl[1]
        themeLight = hsl[2]
    }

    if (draft == null) {
        LaunchedEffect(suppressNullDraftAutoBack) {
            if (!suppressNullDraftAutoBack) onBack()
        }
        return
    }

    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            if (uri != null) {
                if (!viewModel.setQuickCardDraftImage(uri, landscape = activeCropLandscape)) {
                    toast(context, "设置图片失败")
                }
            } else {
                toast(context, "裁剪失败：无输出")
            }
        } else {
            toast(context, "裁剪失败")
        }
    }
    fun launchQuickCardCrop(uri: Uri) {
        val targetLandscape = cropLandscape
        activeCropLandscape = targetLandscape
        val aspectX = if (targetLandscape) 16 else 9
        val aspectY = if (targetLandscape) 9 else 16
        val outputWidth = if (targetLandscape) 1920 else 1080
        val outputHeight = if (targetLandscape) 1080 else 1920
        val options = CropImageOptions(
            fixAspectRatio = true,
            aspectRatioX = aspectX,
            aspectRatioY = aspectY,
            activityTitle = if (targetLandscape) {
                "裁剪横屏名片图片（16:9）"
            } else {
                "裁剪竖屏名片图片（9:16）"
            },
            cropMenuCropButtonTitle = "确认",
            activityMenuIconColor = 0xFFFFFFFF.toInt(),
            activityMenuTextColor = 0xFFFFFFFF.toInt(),
            activityBackgroundColor = 0xFF121212.toInt(),
            toolbarColor = 0xFF038387.toInt(),
            toolbarTitleColor = 0xFFFFFFFF.toInt(),
            toolbarBackButtonColor = 0xFFFFFFFF.toInt(),
            toolbarTintColor = 0xFFFFFFFF.toInt(),
            outputCompressFormat = android.graphics.Bitmap.CompressFormat.PNG,
            outputCompressQuality = 100,
            outputRequestWidth = outputWidth,
            outputRequestHeight = outputHeight,
            outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_EXACT
        )
        cropLauncher.launch(CropImageContractOptions(uri, options))
    }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        launchQuickCardCrop(uri)
    }

    fun requestExitEditor() {
        val autoSave = uiState.quickCardAutoSaveOnExit
        val hasChanges = viewModel.hasQuickCardDraftChanges()
        if (!hasChanges) {
            onBack()
            return
        }
        if (autoSave) {
            val saved = viewModel.saveQuickCardDraft()
            if (saved != null) {
                toast(context, "已自动保存名片")
                onBack()
            } else {
                toast(context, "自动保存失败")
            }
            return
        }
        exitConfirmAutoSaveChecked = false
        showExitConfirm = true
    }

    BackHandler {
        requestExitEditor()
    }
    val requestExitEditorState = rememberUpdatedState { requestExitEditor() }

    val isExisting = !draft.isNew && draft.editId != null
    LaunchedEffect(draft.themeColor) {
        if (!showThemeColorDialog) {
            themeHexInput = draft.themeColor
            val hsl = composeColorToHsl(quickCardThemeColor(draft.themeColor))
            themeHue = hsl[0]
            themeSat = hsl[1]
            themeLight = hsl[2]
        }
    }
    val editorActions = remember(isExisting, context, viewModel) {
        if (!isExisting) {
            QuickCardTopBarActions(
                onNew = {},
                onScan = {},
                onBackRequest = { requestExitEditorState.value() }
            )
        } else {
            QuickCardTopBarActions(
                onNew = {},
                onScan = {},
                onCopy = {
                    val copied = viewModel.duplicateEditingQuickCard()
                    if (copied != null) toast(context, "已复制名片")
                },
                onDelete = { showDeleteConfirm = true },
                onBackRequest = { requestExitEditorState.value() },
                canCopy = true,
                canDelete = true
            )
        }
    }

    LaunchedEffect(editorActions) {
        onTopBarActionsChange(editorActions)
    }

    var titleFocused by remember { mutableStateOf(false) }
    var noteFocused by remember { mutableStateOf(false) }
    var linkFocused by remember { mutableStateOf(false) }

    CenteredPageColumn(
        maxWidth = UiTokens.WideContentMaxWidth,
        scroll = rememberScrollState()
    ) {
            Spacer(Modifier.height(UiTokens.PageTopBlank))

        Md2SettingsCard("基础信息") {
            Md2OutlinedField(
                value = draft.title,
                onValueChange = { viewModel.updateQuickCardDraft { old -> old.copy(title = it) } },
                label = "标题",
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { titleFocused = it.isFocused },
                trailingIcon = if (titleFocused && draft.title.isNotEmpty()) {
                    {
                        Md2ClearFieldButton {
                            viewModel.updateQuickCardDraft { old -> old.copy(title = "") }
                        }
                    }
                } else {
                    null
                }
            )
            OutlinedTextField(
                value = draft.note,
                onValueChange = { viewModel.updateQuickCardDraft { old -> old.copy(note = it) } },
                label = { Text("备注") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { noteFocused = it.isFocused },
                maxLines = 3,
                shape = Md2ControlShape,
                trailingIcon = if (noteFocused && draft.note.isNotEmpty()) {
                    {
                        Md2ClearFieldButton {
                            viewModel.updateQuickCardDraft { old -> old.copy(note = "") }
                        }
                    }
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
            Md2OutlinedField(
                value = draft.link,
                onValueChange = { viewModel.updateQuickCardDraft { old -> old.copy(link = it) } },
                label = "链接",
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { linkFocused = it.isFocused },
                trailingIcon = if (linkFocused && draft.link.isNotEmpty()) {
                    {
                        Md2ClearFieldButton {
                            viewModel.updateQuickCardDraft { old -> old.copy(link = "") }
                        }
                    }
                } else {
                    null
                }
            )
        }

        Md2SettingsCard("主题色") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(UiTokens.Radius))
                    .clickable {
                        syncThemePickerFromHex(draft.themeColor)
                        showThemeColorDialog = true
                    }
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(quickCardThemeColor(draft.themeColor))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("当前主题色", fontWeight = FontWeight.SemiBold)
                    Text(
                        draft.themeColor.uppercase(Locale.US),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MsIcon("palette", contentDescription = "打开名片主题色")
            }
        }

        Md2SettingsCard("背景图片") {
            QuickCardImagePathRow(
                title = "竖屏背景图",
                path = draft.portraitImagePath,
                onClear = { viewModel.clearQuickCardDraftImage(landscape = false) },
                onPick = {
                    cropLandscape = false
                    if (uiState.useBuiltinGallery) {
                        showBuiltinGalleryPicker = true
                    } else {
                        imagePicker.launch("image/*")
                    }
                }
            )
            QuickCardImagePathRow(
                title = "横屏背景图",
                path = draft.landscapeImagePath,
                onClear = { viewModel.clearQuickCardDraftImage(landscape = true) },
                onPick = {
                    cropLandscape = true
                    if (uiState.useBuiltinGallery) {
                        showBuiltinGalleryPicker = true
                    } else {
                        imagePicker.launch("image/*")
                    }
                }
            )
            Text(
                text = "未设置图片时使用主题色背景，并显示装饰文字。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Md2Button(
                onClick = {
                    val saved = viewModel.saveQuickCardDraft()
                    if (saved != null) {
                        toast(context, "已保存名片")
                        onBack()
                    } else {
                        toast(context, "保存失败")
                    }
                }
            ) {
                Text("保存")
            }
        }
            Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("删除名片") },
            text = { Text("确定删除当前名片吗？") },
            confirmButton = {
                Md2TextButton(onClick = {
                    showDeleteConfirm = false
                    suppressNullDraftAutoBack = true
                    if (viewModel.deleteEditingQuickCard()) {
                        toast(context, "已删除名片")
                    }
                    onBack()
                }) { Text("删除") }
            },
            dismissButton = {
                Md2TextButton(onClick = { showDeleteConfirm = false }) { Text("取消") }
            }
        )
    }

    if (showBuiltinGalleryPicker) {
        BuiltinGalleryPickerDialog(
            title = "选择图片",
            onDismiss = { showBuiltinGalleryPicker = false },
            onPicked = { uri ->
                showBuiltinGalleryPicker = false
                launchQuickCardCrop(uri)
            }
        )
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text("名片已编辑") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("是否保存名片后再退出编辑？")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = exitConfirmAutoSaveChecked,
                            onCheckedChange = { exitConfirmAutoSaveChecked = it }
                        )
                        Text("下次退出编辑时自动保存")
                    }
                }
            },
            confirmButton = {
                Md2TextButton(onClick = {
                    showExitConfirm = false
                    if (exitConfirmAutoSaveChecked) {
                        viewModel.setQuickCardAutoSaveOnExit(true)
                    }
                    val saved = viewModel.saveQuickCardDraft()
                    if (saved != null) {
                        toast(context, "已保存名片")
                        onBack()
                    } else {
                        toast(context, "保存失败")
                    }
                }) { Text("保存并退出") }
            },
            dismissButton = {
                Row {
                    Md2TextButton(onClick = {
                        showExitConfirm = false
                        if (exitConfirmAutoSaveChecked) {
                            viewModel.setQuickCardAutoSaveOnExit(true)
                        }
                        onBack()
                    }) { Text("不保存退出") }
                    Md2TextButton(onClick = { showExitConfirm = false }) { Text("取消") }
                }
            }
        )
    }

    if (showThemeColorDialog) {
        AlertDialog(
            onDismissRequest = { showThemeColorDialog = false },
            title = { Text("名片主题色") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val preview = hslToComposeColor(themeHue, themeSat, themeLight)
                    Text(
                        text = "候选主题色",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        presetColors.forEach { hex ->
                            val c = quickCardThemeColor(hex)
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .clickable {
                                        syncThemePickerFromHex(hex)
                                    }
                            )
                        }
                    }
                    val hueGradient = Brush.horizontalGradient(
                        listOf(
                            hslToComposeColor(0f, 1f, 0.5f),
                            hslToComposeColor(60f, 1f, 0.5f),
                            hslToComposeColor(120f, 1f, 0.5f),
                            hslToComposeColor(180f, 1f, 0.5f),
                            hslToComposeColor(240f, 1f, 0.5f),
                            hslToComposeColor(300f, 1f, 0.5f),
                            hslToComposeColor(360f, 1f, 0.5f)
                        )
                    )
                    val satGradient = remember(themeHue, themeLight) {
                        Brush.horizontalGradient(
                            listOf(
                                hslToComposeColor(themeHue, 0f, themeLight),
                                hslToComposeColor(themeHue, 1f, themeLight)
                            )
                        )
                    }
                    val lightGradient = remember(themeHue, themeSat) {
                        Brush.horizontalGradient(
                            listOf(
                                hslToComposeColor(themeHue, themeSat, 0f),
                                hslToComposeColor(themeHue, themeSat, 0.5f),
                                hslToComposeColor(themeHue, themeSat, 1f)
                            )
                        )
                    }
                    HslGradientSlider(
                        label = "色相",
                        value = themeHue,
                        valueRange = 0f..360f,
                        gradient = hueGradient,
                        onValueChange = {
                            themeHue = it
                            themeHexInput = colorToHexRgb(hslToComposeColor(themeHue, themeSat, themeLight))
                        }
                    )
                    HslGradientSlider(
                        label = "饱和度",
                        value = themeSat,
                        valueRange = 0f..1f,
                        gradient = satGradient,
                        onValueChange = {
                            themeSat = it
                            themeHexInput = colorToHexRgb(hslToComposeColor(themeHue, themeSat, themeLight))
                        }
                    )
                    HslGradientSlider(
                        label = "亮度",
                        value = themeLight,
                        valueRange = 0f..1f,
                        gradient = lightGradient,
                        onValueChange = {
                            themeLight = it
                            themeHexInput = colorToHexRgb(hslToComposeColor(themeHue, themeSat, themeLight))
                        },
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(UiTokens.Radius))
                            .background(preview)
                    )
                    OutlinedTextField(
                        value = themeHexInput,
                        onValueChange = {
                            themeHexInput = it
                            val normalized = normalizeHexOrNull(it)
                            if (normalized != null) {
                                val hsl = composeColorToHsl(quickCardThemeColor(normalized))
                                themeHue = hsl[0]
                                themeSat = hsl[1]
                                themeLight = hsl[2]
                            }
                        },
                        singleLine = true,
                        label = { Text("HEX（#RRGGBB）") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Done
                        ),
                        shape = Md2ControlShape,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "拖动三条滑条设置色相、饱和度和亮度",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Md2TextButton(
                    onClick = {
                        val normalized = normalizeHexOrNull(themeHexInput)
                        if (normalized == null) {
                            toast(context, "HEX 格式错误")
                        } else {
                            viewModel.updateQuickCardDraft { old -> old.copy(themeColor = normalized) }
                            showThemeColorDialog = false
                        }
                    }
                ) { Text("应用") }
            },
            dismissButton = {
                Md2TextButton(onClick = { showThemeColorDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
internal fun QuickCardImagePathRow(
    title: String,
    path: String,
    onClear: () -> Unit,
    onPick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (path.isBlank()) "未选择图片" else path,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (path.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Md2IconButton(
                icon = "close",
                contentDescription = "清空图片",
                onClick = onClear,
                enabled = path.isNotBlank()
            )
            Md2IconButton(
                icon = "folder_open",
                contentDescription = "选择图片",
                onClick = onPick
            )
        }
    }
}

internal fun composeColorToHsl(color: Color): FloatArray {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color.toArgb(), hsl)
    return hsl
}

internal fun hslToComposeColor(h: Float, s: Float, l: Float): Color {
    val hue = ((h % 360f) + 360f) % 360f
    val sat = s.coerceIn(0f, 1f)
    val light = l.coerceIn(0f, 1f)
    return Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, light)))
}

internal fun colorToHexRgb(color: Color): String {
    val argb = color.toArgb()
    val rgb = argb and 0x00FFFFFF
    return String.format(Locale.US, "#%06x", rgb)
}

@Composable
internal fun HslGradientSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    gradient: Brush,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.Center)
                    .background(gradient, RectangleShape)
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )
        }
    }
}


internal const val QUICK_CARD_ASPECT_PORTRAIT = 9f / 16f
internal const val QUICK_CARD_ASPECT_LANDSCAPE = 16f / 9f
internal const val QUICK_CARD_LANDSCAPE_CARD_WIDTH_FRACTION = 0.94f
internal const val QUICK_CARD_CONTENT_ASPECT_PORTRAIT = QUICK_CARD_ASPECT_PORTRAIT
internal const val QUICK_CARD_CONTENT_ASPECT_LANDSCAPE = QUICK_CARD_ASPECT_LANDSCAPE

internal fun quickCardDisplayAspect(landscape: Boolean): Float =
    if (landscape) QUICK_CARD_ASPECT_LANDSCAPE else QUICK_CARD_ASPECT_PORTRAIT

internal fun quickCardThemeColor(hex: String): Color {
    return runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrElse { UiTokens.Primary }
}

internal fun quickCardThemeOnColor(bg: Color): Color {
    return if (bg.luminance() > 0.56f) Color(0xFF111417) else Color.White
}

internal fun QuickCard.heroImagePath(landscape: Boolean): String {
    return if (landscape) {
        landscapeImagePath.ifBlank { portraitImagePath }
    } else {
        portraitImagePath.ifBlank { landscapeImagePath }
    }
}

internal fun QuickCardDraft.toPreviewCard(): QuickCard {
    return QuickCard(
        id = editId ?: -1L,
        type = QuickCardType.Text,
        title = title.trim(),
        note = note.trim(),
        themeColor = themeColor,
        link = link,
        portraitImagePath = portraitImagePath,
        landscapeImagePath = landscapeImagePath
    )
}

internal fun buildQuickCardShareText(card: QuickCard): String {
    return buildString {
        append(card.title.ifBlank { "名片" })
        if (card.note.isNotBlank()) append("\n${card.note}")
        if (card.link.isNotBlank()) append("\n${card.link}")
    }
}

internal fun openQuickCardLink(context: Context, rawLink: String) {
    val normalized = normalizeQrTextToWebUrl(rawLink)
    if (normalized.isNullOrBlank()) {
        toast(context, "链接无效")
        return
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalized))
        context.startActivity(intent)
    } catch (e: Exception) {
        toast(context, "打开链接失败: ${e.message}")
    }
}

internal fun shareQuickCard(context: Context, card: QuickCard, landscape: Boolean) {
    try {
        val shareText = buildQuickCardShareText(card)
        if (card.type == QuickCardType.Image) {
            val imagePath = card.heroImagePath(landscape)
            val source = if (imagePath.isBlank()) null else File(imagePath)
            if (source != null && source.exists()) {
                val shareDir = File(context.cacheDir, "share")
                ResourceStorageCleaner.cleanupShareCache(context)
                if (!shareDir.exists()) shareDir.mkdirs()
                val out = File(shareDir, "quick_card_${System.currentTimeMillis()}.png")
                source.copyTo(out, overwrite = true)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    out
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "分享名片"))
                return
            }
        }
        val textIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(textIntent, "分享名片"))
    } catch (e: Exception) {
        toast(context, "分享失败: ${e.message}")
    }
}

internal fun generateQuickCardQrBitmap(content: String, sizePx: Int = 640): Bitmap? {
    return QuickCardRenderCache.loadQr(content, sizePx)
}

@Composable
internal fun rememberQuickCardBitmap(path: String): Bitmap? {
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = path) {
        value = withContext(Dispatchers.IO) {
            QuickCardRenderCache.loadImage(path)
        }
    }
    return bitmap
}

@Composable
internal fun rememberQuickCardQrBitmap(content: String): Bitmap? {
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = content) {
        value = withContext(Dispatchers.Default) {
            QuickCardRenderCache.loadQr(content)
        }
    }
    return bitmap
}


