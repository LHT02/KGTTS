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


internal object UiTokens {
    val Primary = Color(0xFF038387)
    val Radius = 4.dp
    val TopBarElevation = 8.dp
    val CardElevation = 2.dp
    val FabElevation = 6.dp
    val MenuElevation = 8.dp
    val PageTopBlank = 8.dp
    val PageBottomBlank = 92.dp
    val WideContentMaxWidth = 860.dp
    val WideListMaxWidth = 900.dp
    val DrawerWidthExpanded = 216.dp
    val DrawerWidthCollapsed = 72.dp
    val LightCard = Color(0xFFFFFFFF)
    val DarkCard = Color(0xFF1D2023)
}

internal fun isUltraSmallAdaptiveWindow(
    widthDp: Int,
    heightDp: Int,
    isLandscape: Boolean
): Boolean {
    val shortSide = minOf(widthDp, heightDp)
    val longSide = maxOf(widthDp, heightDp)
    if (shortSide <= 0 || longSide <= 0) return false
    val aspect = longSide.toFloat() / shortSide.toFloat()
    val nearSquareSmallWindow = shortSide < 520 && longSide < 760 && aspect <= 1.42f
    val flatTinyLandscapeWindow = isLandscape && shortSide < 430 && longSide < 760
    return nearSquareSmallWindow || flatTinyLandscapeWindow
}

@Composable
internal fun CenteredPageBox(
    maxWidth: Dp,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = horizontalPadding)
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .fillMaxHeight(),
            content = content
        )
    }
}

@Composable
internal fun CenteredPageColumn(
    maxWidth: Dp,
    modifier: Modifier = Modifier,
    scroll: ScrollState? = null,
    horizontalPadding: Dp = 16.dp,
    contentSpacing: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    CenteredPageBox(
        maxWidth = maxWidth,
        modifier = modifier,
        horizontalPadding = horizontalPadding
    ) {
        var columnModifier = Modifier.fillMaxWidth()
        if (scroll != null) {
            columnModifier = columnModifier.verticalScroll(scroll)
        }
        Column(
            modifier = columnModifier,
            verticalArrangement = Arrangement.spacedBy(contentSpacing),
            content = content
        )
    }
}

internal data class MdShadowLayer(
    val offsetY: Dp,
    val blur: Dp,
    val spread: Dp,
    val alpha: Float
)

internal data class MdShadowStyle(
    val umbra: MdShadowLayer,
    val penumbra: MdShadowLayer,
    val ambient: MdShadowLayer
)

internal val MdCardShadowStyle = MdShadowStyle(
    umbra = MdShadowLayer(offsetY = 3.dp, blur = 1.dp, spread = (-2).dp, alpha = 0.14f),
    penumbra = MdShadowLayer(offsetY = 2.dp, blur = 2.dp, spread = 0.dp, alpha = 0.098f),
    ambient = MdShadowLayer(offsetY = 1.dp, blur = 5.dp, spread = 0.dp, alpha = 0.084f)
)

internal val MdFabShadowStyle = MdShadowStyle(
    umbra = MdShadowLayer(offsetY = 3.dp, blur = 5.dp, spread = (-1).dp, alpha = 0.14f),
    penumbra = MdShadowLayer(offsetY = 6.dp, blur = 10.dp, spread = 0.dp, alpha = 0.098f),
    ambient = MdShadowLayer(offsetY = 1.dp, blur = 18.dp, spread = 0.dp, alpha = 0.084f)
)

internal fun Modifier.mdCenteredShadow(
    shape: androidx.compose.ui.graphics.Shape,
    shadowStyle: MdShadowStyle
): Modifier = drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    fun drawLayer(layer: MdShadowLayer) {
        val blurPx = layer.blur.toPx()
        val offsetYPx = layer.offsetY.toPx()
        val spreadPx = layer.spread.toPx()
        val frameworkPaint = Paint().apply {
            isAntiAlias = true
            this.style = Paint.Style.FILL
            color = android.graphics.Color.argb(1, 0, 0, 0)
            setShadowLayer(blurPx, 0f, offsetYPx, Color.Black.copy(alpha = layer.alpha).toArgb())
        }
        drawIntoCanvas { canvas ->
            when (outline) {
                is Outline.Rectangle -> {
                    val rect = RectF(
                        -spreadPx,
                        -spreadPx,
                        size.width + spreadPx,
                        size.height + spreadPx
                    )
                    canvas.nativeCanvas.drawRect(rect, frameworkPaint)
                }
                is Outline.Rounded -> {
                    val roundRect = outline.roundRect
                    val rect = RectF(
                        roundRect.left - spreadPx,
                        roundRect.top - spreadPx,
                        roundRect.right + spreadPx,
                        roundRect.bottom + spreadPx
                    )
                    val radius = (roundRect.topLeftCornerRadius.x + spreadPx).coerceAtLeast(0f)
                    canvas.nativeCanvas.drawRoundRect(rect, radius, radius, frameworkPaint)
                }
                is Outline.Generic -> {
                    canvas.nativeCanvas.save()
                    canvas.nativeCanvas.translate(0f, 0f)
                    canvas.nativeCanvas.drawPath(outline.path.asAndroidPath(), frameworkPaint)
                    canvas.nativeCanvas.restore()
                }
            }
        }
    }
    drawLayer(shadowStyle.umbra)
    drawLayer(shadowStyle.penumbra)
    drawLayer(shadowStyle.ambient)
}

@Composable
internal fun MdShadowCardSurface(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(UiTokens.Radius),
    backgroundColor: Color = md2ElevatedCardContainerColor(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.mdCenteredShadow(shape = shape, shadowStyle = MdCardShadowStyle)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            color = backgroundColor,
            elevation = 0.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), content = content)
        }
    }
}

@Composable
internal fun MdShadowCircleActionSurface(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.mdCenteredShadow(shape = CircleShape, shadowStyle = MdFabShadowStyle)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            color = backgroundColor,
            contentColor = contentColor,
            elevation = 0.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center, content = content)
        }
    }
}

internal val KgtLightColors = lightColors(
    primary = UiTokens.Primary,
    onPrimary = Color.White,
    secondary = UiTokens.Primary,
    onSecondary = Color.White,
    background = Color(0xFFF1F3F5),
    onBackground = Color(0xFF111417),
    surface = UiTokens.LightCard,
    onSurface = Color(0xFF111417)
)

internal val KgtDarkColors = darkColors(
    primary = UiTokens.Primary,
    onPrimary = Color.White,
    secondary = UiTokens.Primary,
    onSecondary = Color.White,
    background = Color(0xFF121416),
    onBackground = Color(0xFFE4E8EB),
    surface = UiTokens.DarkCard,
    onSurface = Color(0xFFE4E8EB)
)

internal data class Md2ExtraColors(
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color
)

internal val KgtLightExtraColors = Md2ExtraColors(
    surfaceVariant = Color(0xFFE8ECEF),
    onSurfaceVariant = Color(0xFF495156),
    outline = Color(0xFF9CA5AC)
)

internal val KgtDarkExtraColors = Md2ExtraColors(
    surfaceVariant = Color(0xFF262A2E),
    onSurfaceVariant = Color(0xFFB6BEC4),
    outline = Color(0xFF757F87)
)

internal val LocalMd2ExtraColors = staticCompositionLocalOf { KgtLightExtraColors }
internal val LocalSuppressStaggeredFloatIn = staticCompositionLocalOf { false }

internal data class Md2ColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color
)

internal val MaterialTheme.colorScheme: Md2ColorScheme
    @Composable
    get() {
        val base = MaterialTheme.colors
        val extra = LocalMd2ExtraColors.current
        return Md2ColorScheme(
            primary = base.primary,
            onPrimary = base.onPrimary,
            secondary = base.secondary,
            onSecondary = base.onSecondary,
            background = base.background,
            onBackground = base.onBackground,
            surface = base.surface,
            onSurface = base.onSurface,
            surfaceVariant = extra.surfaceVariant,
            onSurfaceVariant = extra.onSurfaceVariant,
            outline = extra.outline
        )
    }

internal val Typography.titleSmall: TextStyle get() = subtitle2
internal val Typography.titleMedium: TextStyle get() = h6
internal val Typography.bodyLarge: TextStyle get() = body1
internal val Typography.bodyMedium: TextStyle get() = body2
internal val Typography.bodySmall: TextStyle get() = caption
internal val Typography.labelMedium: TextStyle get() = caption
internal val Typography.labelSmall: TextStyle get() = overline

internal val KgtTypography = Typography(
    h6 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    ),
    subtitle2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    ),
    body1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    ),
    body2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    ),
    button = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    ),
    caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    ),
    overline = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )
)

internal val Md2Shapes = Shapes(
    small = RoundedCornerShape(UiTokens.Radius),
    medium = RoundedCornerShape(UiTokens.Radius),
    large = RoundedCornerShape(UiTokens.Radius)
)

@Composable
internal fun currentAppDarkTheme(): Boolean = !MaterialTheme.colors.isLight

@Composable
internal fun md2CardContainerColor(): Color {
    return if (currentAppDarkTheme()) UiTokens.DarkCard else UiTokens.LightCard
}

@Composable
internal fun md2ElevatedCardContainerColor(elevation: Dp = UiTokens.CardElevation): Color {
    val base = md2CardContainerColor()
    return LocalElevationOverlay.current?.apply(base, elevation) ?: base
}

@Composable
internal fun pageBottomBlankPadding(extraContentGap: Dp = 44.dp): Dp {
    return WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + extraContentGap
}

internal val MaterialSymbolsSharp = FontFamily(
    Font(
        resId = R.font.material_symbols_sharp,
        weight = FontWeight.W500
    )
)

internal object FontScaleBlockRuntime {
    var mode by mutableIntStateOf(UserPrefs.FONT_SCALE_BLOCK_ICONS_ONLY)
}

internal val LocalFontScaleBlockMode = staticCompositionLocalOf {
    UserPrefs.FONT_SCALE_BLOCK_ICONS_ONLY
}

internal val LocalKigttsHapticFeedbackEnabled = staticCompositionLocalOf { false }

@Composable
internal fun KigttsFontScaleProvider(
    mode: Int = FontScaleBlockRuntime.mode,
    content: @Composable () -> Unit
) {
    val normalized = UserPrefs.normalizeFontScaleBlockMode(mode)
    val density = LocalDensity.current
    val providedDensity = if (normalized == UserPrefs.FONT_SCALE_BLOCK_ALL) {
        Density(density = density.density, fontScale = 1f)
    } else {
        density
    }
    CompositionLocalProvider(
        LocalFontScaleBlockMode provides normalized,
        LocalDensity provides providedDensity,
        content = content
    )
}

internal fun View.performKigttsKeyHaptic(enabled: Boolean) {
    if (!enabled) return
    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
}

@Composable
internal fun rememberKigttsKeyHaptic(): () -> Unit {
    val enabled = LocalKigttsHapticFeedbackEnabled.current
    val view = LocalView.current
    return remember(view, enabled) {
        { view.performKigttsKeyHaptic(enabled) }
    }
}

@Composable
internal fun KigttsIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val performHaptic = rememberKigttsKeyHaptic()
    val currentOnClick by rememberUpdatedState(onClick)
    IconButton(
        onClick = {
            performHaptic()
            currentOnClick()
        },
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}

@Composable
internal fun rememberKigttsHapticClick(onClick: () -> Unit): () -> Unit {
    val performHaptic = rememberKigttsKeyHaptic()
    val currentOnClick by rememberUpdatedState(onClick)
    return remember(performHaptic) {
        {
            performHaptic()
            currentOnClick()
        }
    }
}

@Composable
internal fun <T> rememberKigttsHapticValueChange(onValueChange: (T) -> Unit): (T) -> Unit {
    val performHaptic = rememberKigttsKeyHaptic()
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    return remember(performHaptic) {
        { value ->
            performHaptic()
            currentOnValueChange(value)
        }
    }
}

@Composable
internal fun MsIcon(
    name: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val fontScaleBlockMode = LocalFontScaleBlockMode.current
    val iconTextSize = if (fontScaleBlockMode == UserPrefs.FONT_SCALE_BLOCK_NONE) {
        24.sp
    } else {
        with(LocalDensity.current) { 24.dp.toSp() }
    }
    val a11yModifier = if (contentDescription != null) {
        modifier.semantics { this.contentDescription = contentDescription }
    } else {
        modifier
    }
    Text(
        text = name,
        modifier = a11yModifier,
        color = tint,
        style = TextStyle(
            fontFamily = MaterialSymbolsSharp,
            fontWeight = FontWeight.W500,
            fontSize = iconTextSize,
            lineHeight = iconTextSize,
            letterSpacing = 0.sp,
            fontFeatureSettings = "'liga' 1"
        )
    )
}

@Composable
internal fun Md2StaggeredFloatIn(
    index: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val effectiveEnabled = enabled && !LocalSuppressStaggeredFloatIn.current
    var visible by remember(index, effectiveEnabled) { mutableStateOf(!effectiveEnabled) }

    LaunchedEffect(index, effectiveEnabled) {
        if (!effectiveEnabled) {
            visible = true
            return@LaunchedEffect
        }
        visible = false
        delay((40L * index).coerceAtMost(260L))
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(220)) +
                slideInVertically(
                    initialOffsetY = { full -> (full * 0.12f).toInt() },
                    animationSpec = tween(240, easing = FastOutSlowInEasing)
                ),
        exit = fadeOut(animationSpec = tween(90))
    ) {
        content()
    }
}

internal data class DrawerItem(
    val page: Int,
    val title: String,
    val icon: String
)

data class LogTopBarActions(
    val onRefresh: () -> Unit,
    val onCopy: () -> Unit,
    val onShare: () -> Unit,
    val canCopy: Boolean,
    val canShare: Boolean
)

data class QuickCardTopBarActions(
    val onNew: () -> Unit = {},
    val onScan: () -> Unit = {},
    val onCopy: (() -> Unit)? = null,
    val onDelete: (() -> Unit)? = null,
    val onConfirm: (() -> Unit)? = null,
    val onClose: (() -> Unit)? = null,
    val onWebReload: (() -> Unit)? = null,
    val onWebBack: (() -> Unit)? = null,
    val onWebForward: (() -> Unit)? = null,
    val onBackRequest: (() -> Unit)? = null,
    val canCopy: Boolean = false,
    val canDelete: Boolean = false,
    val canConfirm: Boolean = false,
    val canClose: Boolean = false,
    val canWebReload: Boolean = false,
    val canWebBack: Boolean = false,
    val canWebForward: Boolean = false
)

data class PresetTopBarActions(
    val onImport: () -> Unit,
    val onExport: () -> Unit
)

data class EditorBatchTopBarActions(
    val onMove: () -> Unit,
    val onDelete: () -> Unit,
    val onClose: () -> Unit,
    val canMove: Boolean = true,
    val canDelete: Boolean = true
)

internal object QuickSubtitleRoutes {
    const val Main = "quick_subtitle/main"
    const val Editor = "quick_subtitle/editor"
    const val History = "quick_subtitle/history"
}

data class QuickSubtitleFloatingInputPreviewState(
    val text: AnnotatedString,
    val cursorIndex: Int,
    val bottomPadding: Dp
)

internal object SoundboardRoutes {
    const val Main = "soundboard/main"
    const val Editor = "soundboard/editor"
}

internal object QuickCardRoutes {
    const val Main = "quick_card/main"
    const val Editor = "quick_card/editor"
    const val Sort = "quick_card/sort"
    const val Scanner = "quick_card/scanner"
    private const val ScanCandidatesArg = "items"
    const val ScanCandidates = "quick_card/scan_candidates/{$ScanCandidatesArg}"
    private const val ScanTextArg = "text"
    const val ScanText = "quick_card/scan_text/{$ScanTextArg}"
    private const val WebArg = "url"
    const val Web = "quick_card/web/{$WebArg}"

    fun scanCandidates(items: List<String>): String =
        "quick_card/scan_candidates/${Uri.encode(JSONArray(items).toString())}"
    fun scanText(text: String): String = "quick_card/scan_text/${Uri.encode(text)}"
    fun web(url: String): String = "quick_card/web/${Uri.encode(url)}"
}

internal object SettingsRoutes {
    const val Main = "settings/main"
    const val Log = "settings/log"
    const val Licenses = "settings/licenses"
    const val Privacy = "settings/privacy"
}

internal val SoundboardAudioFileExtensions = setOf(
    "m4a",
    "mp4",
    "aac",
    "mp3",
    "wav",
    "wave",
    "flac",
    "ogg",
    "oga",
    "opus",
    "amr",
    "awb",
    "3gp",
    "3gpp",
    "webm"
)

internal const val TTS_DISABLED_MESSAGE = "TTS已禁用，如需打开，请打开顶部音频状态菜单将“禁用TTS”选项关闭"
internal var quickCardSortHintShownThisProcess = false

