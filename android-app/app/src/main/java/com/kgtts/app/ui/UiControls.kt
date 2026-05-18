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


internal class KigttsTextToolbarState {
    var rect by mutableStateOf(Rect.Zero)
    var visible by mutableStateOf(false)
    var onCopyRequested by mutableStateOf<(() -> Unit)?>(null)
    var onPasteRequested by mutableStateOf<(() -> Unit)?>(null)
    var onCutRequested by mutableStateOf<(() -> Unit)?>(null)
    var onSelectAllRequested by mutableStateOf<(() -> Unit)?>(null)

    fun show(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        this.rect = rect
        this.onCopyRequested = onCopyRequested
        this.onPasteRequested = onPasteRequested
        this.onCutRequested = onCutRequested
        this.onSelectAllRequested = onSelectAllRequested
        visible = true
    }

    fun hide() {
        visible = false
    }
}

internal class KigttsTextToolbar(
    private val state: KigttsTextToolbarState
) : TextToolbar {
    override val status: TextToolbarStatus
        get() = if (state.visible) TextToolbarStatus.Shown else TextToolbarStatus.Hidden

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        state.show(
            rect = rect,
            onCopyRequested = onCopyRequested,
            onPasteRequested = onPasteRequested,
            onCutRequested = onCutRequested,
            onSelectAllRequested = onSelectAllRequested
        )
    }

    override fun hide() {
        state.hide()
    }
}

internal data class KigttsTextToolbarAction(
    val icon: String,
    val contentDescription: String,
    val onClick: (() -> Unit)?
)

internal class KigttsTextToolbarPositionProvider(
    private val anchorRect: IntRect,
    private val marginPx: Int
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val centeredX = anchorRect.left + ((anchorRect.width - popupContentSize.width) / 2)
        val clampedX = centeredX.coerceIn(
            marginPx,
            (windowSize.width - popupContentSize.width - marginPx).coerceAtLeast(marginPx)
        )
        val aboveY = anchorRect.top - popupContentSize.height - marginPx
        val belowY = anchorRect.bottom + marginPx
        val targetY = if (aboveY >= marginPx) {
            aboveY
        } else {
            belowY.coerceAtMost(
                (windowSize.height - popupContentSize.height - marginPx).coerceAtLeast(marginPx)
            )
        }
        return IntOffset(clampedX, targetY)
    }
}

@Composable
internal fun KigttsTextToolbarPopup(
    state: KigttsTextToolbarState,
    darkTheme: Boolean
) {
    var rendered by remember { mutableStateOf(state.visible) }
    val menuAlpha by animateFloatAsState(
        targetValue = if (state.visible) 1f else 0f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "kigtts_text_toolbar_alpha"
    )
    val menuScale by animateFloatAsState(
        targetValue = if (state.visible) 1f else 0.94f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "kigtts_text_toolbar_scale"
    )
    LaunchedEffect(state.visible) {
        if (state.visible) {
            rendered = true
        } else if (rendered) {
            delay(180L)
            rendered = false
        }
    }
    if (!rendered) return
    val density = LocalDensity.current
    val marginPx = with(density) { 8.dp.roundToPx() }
    val anchorRect = remember(state.rect) {
        IntRect(
            left = state.rect.left.toInt(),
            top = state.rect.top.toInt(),
            right = state.rect.right.toInt(),
            bottom = state.rect.bottom.toInt()
        )
    }
    val actions = remember(
        state.onCopyRequested,
        state.onPasteRequested,
        state.onCutRequested,
        state.onSelectAllRequested
    ) {
        listOf(
            KigttsTextToolbarAction("select_all", "全选", state.onSelectAllRequested),
            KigttsTextToolbarAction("content_cut", "剪切", state.onCutRequested),
            KigttsTextToolbarAction("content_copy", "复制", state.onCopyRequested),
            KigttsTextToolbarAction("content_paste", "粘贴", state.onPasteRequested)
        ).filter { it.onClick != null }
    }
    if (actions.isEmpty()) return

    val backgroundColor = if (darkTheme) Color(0xFF2C2F33) else Color.White
    val contentColor = if (darkTheme) Color(0xFFE9EDF1) else Color(0xFF202428)
    val positionProvider = remember(anchorRect, marginPx) {
        KigttsTextToolbarPositionProvider(anchorRect, marginPx)
    }

    Popup(
        popupPositionProvider = positionProvider,
        properties = PopupProperties(focusable = false, dismissOnClickOutside = false)
    ) {
        KigttsFontScaleProvider {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .graphicsLayer {
                        alpha = menuAlpha
                        scaleX = menuScale
                        scaleY = menuScale
                        transformOrigin = TransformOrigin(0.5f, 0f)
                        clip = false
                    }
            ) {
                Card(
                    modifier = Modifier.wrapContentSize(),
                    shape = RoundedCornerShape(UiTokens.Radius),
                    backgroundColor = backgroundColor,
                    elevation = UiTokens.MenuElevation
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        actions.forEach { action ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(bounded = true, radius = 20.dp)
                                    ) {
                                        action.onClick?.invoke()
                                        state.hide()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                MsIcon(
                                    name = action.icon,
                                    contentDescription = action.contentDescription,
                                    tint = contentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

internal val Md2ControlShape = RoundedCornerShape(UiTokens.Radius)

@Composable
internal fun Md2Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val hapticOnClick = rememberKigttsHapticClick(onClick)
    Button(
        onClick = hapticOnClick,
        modifier = modifier,
        enabled = enabled,
        shape = Md2ControlShape,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        content = content
    )
}

@Composable
internal fun Md2OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val hapticOnClick = rememberKigttsHapticClick(onClick)
    OutlinedButton(
        onClick = hapticOnClick,
        modifier = modifier,
        enabled = enabled,
        shape = Md2ControlShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        content = content
    )
}

@Composable
internal fun Md2DropdownButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    expanded: Boolean = false
) {
    Md2OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            MsIcon(
                name = if (expanded) "expand_less" else "expand_more",
                contentDescription = null
            )
        }
    }
}

@Composable
internal fun Md2TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val hapticOnClick = rememberKigttsHapticClick(onClick)
    TextButton(
        onClick = hapticOnClick,
        modifier = modifier,
        enabled = enabled,
        shape = Md2ControlShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        content = content
    )
}

@Composable
internal fun Md2IconButton(
    icon: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val hapticOnClick = rememberKigttsHapticClick(onClick)
    IconButton(
        onClick = hapticOnClick,
        enabled = enabled,
        modifier = modifier.size(36.dp)
    ) {
        MsIcon(
            name = icon,
            contentDescription = contentDescription,
            tint = if (enabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
        )
    }
}

@Composable
internal fun RecognitionResourceSourceDialog(
    modelScopeUrl: String,
    huggingFaceUrl: String,
    preferredSource: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var modelScope by remember(modelScopeUrl) { mutableStateOf(modelScopeUrl) }
    var huggingFace by remember(huggingFaceUrl) { mutableStateOf(huggingFaceUrl) }
    var preferred by remember(preferredSource) {
        mutableIntStateOf(
            preferredSource.coerceIn(
                UserPrefs.RECOGNITION_RESOURCE_SOURCE_MODELSCOPE,
                UserPrefs.RECOGNITION_RESOURCE_SOURCE_HUGGINGFACE
            )
        )
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(UiTokens.Radius),
        title = { Text("语音识别资源包下载源") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "下载的资源包会安装到软件内部目录；安装完成后会自动清理下载得到的 7z 包。本地安装不会删除原文件。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Md2OutlinedField(
                    value = modelScope,
                    onValueChange = { modelScope = it },
                    label = "魔搭下载源",
                    modifier = Modifier.fillMaxWidth()
                )
                Md2OutlinedField(
                    value = huggingFace,
                    onValueChange = { huggingFace = it },
                    label = "Hugging Face 下载源",
                    modifier = Modifier.fillMaxWidth()
                )
                Text("优先下载源", style = MaterialTheme.typography.bodySmall)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(UiTokens.Radius))
                        .clickable { preferred = UserPrefs.RECOGNITION_RESOURCE_SOURCE_MODELSCOPE }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = preferred == UserPrefs.RECOGNITION_RESOURCE_SOURCE_MODELSCOPE,
                        onClick = { preferred = UserPrefs.RECOGNITION_RESOURCE_SOURCE_MODELSCOPE }
                    )
                    Text("魔搭")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(UiTokens.Radius))
                        .clickable { preferred = UserPrefs.RECOGNITION_RESOURCE_SOURCE_HUGGINGFACE }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = preferred == UserPrefs.RECOGNITION_RESOURCE_SOURCE_HUGGINGFACE,
                        onClick = { preferred = UserPrefs.RECOGNITION_RESOURCE_SOURCE_HUGGINGFACE }
                    )
                    Text("Hugging Face")
                }
            }
        },
        confirmButton = {
            Md2TextButton(
                onClick = { onConfirm(modelScope, huggingFace, preferred) }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Md2TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
internal fun KokoroSourceDialog(
    hfUrl: String,
    hfMirrorUrl: String,
    modelScopeUrl: String,
    preferredSource: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int) -> Unit
) {
    var hf by remember(hfUrl) { mutableStateOf(hfUrl) }
    var hfMirror by remember(hfMirrorUrl) { mutableStateOf(hfMirrorUrl) }
    var modelScope by remember(modelScopeUrl) { mutableStateOf(modelScopeUrl) }
    var preferred by remember(preferredSource) {
        mutableIntStateOf(
            preferredSource.coerceIn(
                UserPrefs.KOKORO_SOURCE_HF,
                UserPrefs.KOKORO_SOURCE_MODELSCOPE
            )
        )
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(UiTokens.Radius),
        title = { Text("Kokoro 下载源") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "如果默认下载速度慢或下载失败，可以在这里切换下载来源。一般建议保持默认源；如果无法下载，再尝试另一个来源。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "下载完成后会自动安装到软件内部目录。安装成功后，Kokoro 会出现在“语音包”页面。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Md2OutlinedField(
                    value = modelScope,
                    onValueChange = { modelScope = it },
                    label = "ModelScope 下载源",
                    modifier = Modifier.fillMaxWidth()
                )
                Md2OutlinedField(
                    value = hfMirror,
                    onValueChange = { hfMirror = it },
                    label = "HF-Mirror 下载源",
                    modifier = Modifier.fillMaxWidth()
                )
                Md2OutlinedField(
                    value = hf,
                    onValueChange = { hf = it },
                    label = "Hugging Face 下载源",
                    modifier = Modifier.fillMaxWidth()
                )
                Text("优先下载源", style = MaterialTheme.typography.bodySmall)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(UiTokens.Radius))
                        .clickable { preferred = UserPrefs.KOKORO_SOURCE_MODELSCOPE }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = preferred == UserPrefs.KOKORO_SOURCE_MODELSCOPE,
                        onClick = { preferred = UserPrefs.KOKORO_SOURCE_MODELSCOPE }
                    )
                    Text("ModelScope（默认）")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(UiTokens.Radius))
                        .clickable { preferred = UserPrefs.KOKORO_SOURCE_HFMIRROR }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = preferred == UserPrefs.KOKORO_SOURCE_HFMIRROR,
                        onClick = { preferred = UserPrefs.KOKORO_SOURCE_HFMIRROR }
                    )
                    Text("HF-Mirror")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(UiTokens.Radius))
                        .clickable { preferred = UserPrefs.KOKORO_SOURCE_HF }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = preferred == UserPrefs.KOKORO_SOURCE_HF,
                        onClick = { preferred = UserPrefs.KOKORO_SOURCE_HF }
                    )
                    Text("Hugging Face")
                }
            }
        },
        confirmButton = {
            Md2TextButton(
                onClick = { onConfirm(hf, hfMirror, modelScope, preferred) }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Md2TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
internal fun KokoroVoiceSettingsDialog(
    state: UiState,
    onDismiss: () -> Unit,
    onSpeakerChange: (Int) -> Unit
) {
    val onSpeakerChangeState = rememberUpdatedState(onSpeakerChange)
    val speakerId = state.kokoroSpeakerId.coerceIn(
        UserPrefs.KOKORO_MIN_SPEAKER_ID,
        UserPrefs.KOKORO_MAX_SPEAKER_ID
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(UiTokens.Radius),
        title = { Text("Kokoro 设置") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "关于 Kokoro",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Kokoro 是一套本地离线朗读声音。安装后不需要联网即可使用，适合想要使用软件自带朗读声音、不依赖系统朗读声音的场景。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "选择声音",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "编号不是音质等级，只代表不同声音风格。可用范围：0-1 美式女声，2 英式女声，3-57 中文女声，58-102 中文男声。你可以切换编号后试听，选择更适合自己的声音。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("声音编号：$speakerId", style = MaterialTheme.typography.bodyMedium)
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    factory = { ctx ->
                        NumberPicker(ctx).apply {
                            minValue = UserPrefs.KOKORO_MIN_SPEAKER_ID
                            maxValue = UserPrefs.KOKORO_MAX_SPEAKER_ID
                            wrapSelectorWheel = false
                            value = speakerId
                            setOnValueChangedListener { _, _, newVal ->
                                onSpeakerChangeState.value(newVal)
                            }
                        }
                    },
                    update = { picker ->
                        if (picker.minValue != UserPrefs.KOKORO_MIN_SPEAKER_ID) {
                            picker.minValue = UserPrefs.KOKORO_MIN_SPEAKER_ID
                        }
                        if (picker.maxValue != UserPrefs.KOKORO_MAX_SPEAKER_ID) {
                            picker.maxValue = UserPrefs.KOKORO_MAX_SPEAKER_ID
                        }
                        if (picker.value != speakerId) {
                            picker.value = speakerId
                        }
                    }
                )
            }
        },
        confirmButton = {
            Md2TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@Composable
internal fun RecognitionResourceRequiredDialog(
    state: UiState,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onPickLocalPackage: () -> Unit,
    onOpenSources: () -> Unit
) {
    val busy = state.recognitionResourceBusy
    val installed = state.recognitionResourceInstalled
    AlertDialog(
        onDismissRequest = {
            if (!busy) onDismiss()
        },
        shape = RoundedCornerShape(UiTokens.Radius),
        title = { Text(if (installed) "语音识别资源包已安装" else "需要语音识别资源包") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (installed) {
                        "资源包安装完成，可以继续开启语音识别。"
                    } else {
                        "当前未安装语音识别资源包。语音识别、Silero VAD 和 AI 语音增强模型已经从 APK 解耦，需要先下载或从本地安装资源包。"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = state.recognitionResourceStatus,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (busy) {
                    val progress = state.recognitionResourceProgress
                    if (progress in 0f..1f) {
                        LinearProgressIndicator(
                            progress = progress.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    Text(
                        text = state.recognitionResourceProgressStage.ifBlank { "处理中" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!installed) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Md2Button(
                            onClick = onDownload,
                            enabled = !busy,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("下载并安装")
                        }
                        Md2OutlinedButton(
                            onClick = onPickLocalPackage,
                            enabled = !busy,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("本地安装")
                        }
                    }
                    Md2TextButton(
                        onClick = onOpenSources,
                        enabled = !busy
                    ) {
                        Text("管理下载源")
                    }
                }
            }
        },
        confirmButton = {
            Md2TextButton(
                onClick = onDismiss,
                enabled = !busy
            ) {
                Text(if (installed) "完成" else "稍后再说")
            }
        }
    )
}

@Composable
internal fun Md2Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val dark = currentAppDarkTheme()
    val hapticOnCheckedChange = rememberKigttsHapticValueChange(onCheckedChange)
    val uncheckedTrack = if (dark) Color(0xFF697378) else Color(0xFFB3C1C6)
    val uncheckedThumb = if (dark) Color(0xFFE6EFF2) else Color.White
    // Disabled state should look clearly gray, not transparent/faded-out.
    val disabledTrack = if (dark) Color(0xFF4D555B) else Color(0xFFD0D6DB)
    val disabledThumb = if (dark) Color(0xFF99A2A9) else Color(0xFF8E979E)
    Switch(
        checked = checked,
        onCheckedChange = hapticOnCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.48f),
            uncheckedThumbColor = uncheckedThumb,
            uncheckedTrackColor = uncheckedTrack,
            disabledCheckedThumbColor = disabledThumb,
            disabledCheckedTrackColor = disabledTrack,
            disabledUncheckedThumbColor = disabledThumb,
            disabledUncheckedTrackColor = disabledTrack
        )
    )
}

@Composable
internal fun Md2OutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        shape = Md2ControlShape,
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
internal fun Md2DialogOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    topPadding: Dp = 16.dp,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            label = { Text(label) },
            singleLine = singleLine,
            maxLines = maxLines,
            shape = Md2ControlShape,
            trailingIcon = trailingIcon,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
internal fun Md2ClearFieldButton(
    contentDescription: String = "清除",
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        MsIcon("close", contentDescription = contentDescription)
    }
}

@Composable
internal fun rememberTopEndDropdownPopupPositionProvider(verticalMargin: Dp = 4.dp): PopupPositionProvider {
    val density = LocalDensity.current
    return remember(density, verticalMargin) {
        object : PopupPositionProvider {
            private val verticalMarginPx = with(density) { verticalMargin.roundToPx() }

            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val preferredX = when (layoutDirection) {
                    LayoutDirection.Ltr -> anchorBounds.right - popupContentSize.width
                    LayoutDirection.Rtl -> anchorBounds.left
                }
                val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
                val x = preferredX.coerceIn(0, maxX)
                val belowY = anchorBounds.bottom + verticalMarginPx
                val aboveY = anchorBounds.top - verticalMarginPx - popupContentSize.height
                val y = if (belowY + popupContentSize.height <= windowSize.height) {
                    belowY
                } else {
                    aboveY.coerceAtLeast(0)
                }
                return IntOffset(x, y)
            }
        }
    }
}

@Composable
internal fun Md2AnimatedOptionMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var rendered by remember { mutableStateOf(expanded) }
    val popupPositionProvider = rememberTopEndDropdownPopupPositionProvider()
    val menuAlpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "md2_option_menu_alpha"
    )
    val menuScale by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.94f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "md2_option_menu_scale"
    )
    LaunchedEffect(expanded) {
        if (expanded) {
            rendered = true
        } else if (rendered) {
            delay(180L)
            rendered = false
        }
    }
    if (!rendered) return
    val maxMenuHeight = (LocalConfiguration.current.screenHeightDp.dp - 96.dp)
        .coerceAtLeast(160.dp)
    Popup(
        popupPositionProvider = popupPositionProvider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true)
    ) {
        KigttsFontScaleProvider {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .graphicsLayer {
                        alpha = menuAlpha
                        scaleX = menuScale
                        scaleY = menuScale
                        transformOrigin = TransformOrigin(1f, 0f)
                        clip = false
                    }
            ) {
                Card(
                    modifier = Modifier
                        .widthIn(min = 196.dp, max = 216.dp)
                        .then(modifier),
                    shape = RoundedCornerShape(4.dp),
                    backgroundColor = md2CardContainerColor(),
                    elevation = UiTokens.MenuElevation
                ) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = maxMenuHeight)
                            .verticalScroll(rememberScrollState()),
                        content = content
                    )
                }
            }
        }
    }
}

@Composable
internal fun Md2SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null
) {
    val contentAlpha = if (enabled) 1f else 0.56f
    val hapticToggle = rememberKigttsHapticClick { onCheckedChange(!checked) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            ) { hapticToggle() }
            .padding(horizontal = 2.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
            )
            Md2Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
        if (!supportingText.isNullOrBlank()) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
            )
        }
    }
}

@Composable
internal fun Md2SettingDropdownRow(
    title: String,
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    menuContent: @Composable ColumnScope.() -> Unit
) {
    val contentAlpha = if (enabled) 1f else 0.56f
    val hapticExpand = rememberKigttsHapticClick { onExpandedChange(true) }
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) { hapticExpand() }
                .padding(horizontal = 2.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                MsIcon(
                    name = if (expanded) "expand_less" else "expand_more",
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                )
            }
            if (!supportingText.isNullOrBlank()) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                )
            }
        }
        Md2AnimatedOptionMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            menuContent()
        }
    }
}

enum class SettingsCategory(val title: String, val icon: String) {
    Recognition("识别", "graphic_eq"),
    Audio("音频", "volume_up"),
    System("系统", "tune"),
    About("关于", "info")
}

@Composable
internal fun SettingsTabsCard(
    selectedCategory: SettingsCategory,
    compact: Boolean,
    onSelect: (SettingsCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)
    if (compact) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                SettingsCategory.entries.forEach { category ->
                    SettingsTabButton(
                        category = category,
                        selected = selectedCategory == category,
                        compact = true,
                        onClick = { onSelect(category) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerColor)
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            SettingsCategory.entries.forEach { category ->
                SettingsTabButton(
                    category = category,
                    selected = selectedCategory == category,
                    compact = false,
                    onClick = { onSelect(category) }
                )
            }
        }
    }
}

@Composable
internal fun SettingsTabButton(
    category: SettingsCategory,
    selected: Boolean,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val indicatorColor = MaterialTheme.colorScheme.primary
    val contentColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RectangleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = onClick
            ),
    ) {
        if (compact) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MsIcon(
                    name = category.icon,
                    contentDescription = category.title,
                    tint = contentColor
                )
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(0.72f)
                        .height(2.dp)
                        .background(indicatorColor)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 12.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MsIcon(
                    name = category.icon,
                    contentDescription = category.title,
                    tint = contentColor
                )
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(indicatorColor)
                )
            }
        }
    }
}

