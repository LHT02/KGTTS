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
fun ModelScreen(state: UiState) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("模型管理", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2CardContainerColor(),
            elevation = UiTokens.CardElevation
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("ASR 模型由语音识别资源包管理", fontWeight = FontWeight.SemiBold)
                Text("当前 Android 版本不再内置 ASR / 语音增强模型，请在“设置 - 识别”安装语音识别资源包。")
                Spacer(Modifier.height(8.dp))
                Text("当前 ASR 路径：", style = MaterialTheme.typography.labelSmall)
                Text(state.asrDir?.absolutePath ?: "未导入")
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2CardContainerColor(),
            elevation = UiTokens.CardElevation
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("语音包导入已迁移", fontWeight = FontWeight.SemiBold)
                Text("请前往“语音包”页面顶部文件夹按钮导入语音包。")
                Spacer(Modifier.height(8.dp))
                Text("当前语音包路径：", style = MaterialTheme.typography.labelSmall)
                Text(
                    when {
                        isSystemTtsVoiceDir(state.voiceDir) -> SYSTEM_TTS_DEFAULT_LABEL
                        state.voiceDir != null -> state.voiceDir.absolutePath
                        else -> "未选择"
                    }
                )
            }
        }
        Text("状态：${state.status}")
    }
}

@Composable
internal fun rememberAvatarBitmap(file: File): android.graphics.Bitmap? {
    val bitmap by produceState<android.graphics.Bitmap?>(
        initialValue = null,
        key1 = file.absolutePath,
        key2 = file.lastModified()
    ) {
        value = withContext(Dispatchers.IO) {
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        }
    }
    return bitmap
}

@Composable
internal fun VoicePackAvatarPlaceholder(
    modifier: Modifier,
    isSystemPack: Boolean,
    isKokoroPack: Boolean = false,
    logoSize: Dp = 50.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(UiTokens.Radius))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when {
            isSystemPack -> {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_launcher_monochrome),
                    contentDescription = null,
                    modifier = Modifier.size(logoSize),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(LocalContentColor.current)
                )
            }
            isKokoroPack -> {
                val kokoroIconSize = logoSize * 0.72f
                val iconTextSize = with(LocalDensity.current) { kokoroIconSize.toSp() }
                Text(
                    text = "groups",
                    color = LocalContentColor.current,
                    style = TextStyle(
                        fontFamily = MaterialSymbolsSharp,
                        fontWeight = FontWeight.W400,
                        fontSize = iconTextSize,
                        lineHeight = iconTextSize,
                        letterSpacing = 0.sp,
                        fontFeatureSettings = "'liga' 1"
                    )
                )
            }
            else -> {
                Text("无头像", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun VoicePackScreen(viewModel: MainViewModel, state: UiState) {
    val context = LocalContext.current
    var detailPackPath by remember { mutableStateOf<String?>(null) }
    var detailName by remember { mutableStateOf("") }
    var detailRemark by remember { mutableStateOf("") }
    var detailEditing by remember { mutableStateOf(false) }
    var deletePack by remember { mutableStateOf<VoicePackInfo?>(null) }
    var avatarTarget by remember { mutableStateOf<VoicePackInfo?>(null) }
    var showBuiltinAvatarGallery by remember { mutableStateOf(false) }
    val detailPack = detailPackPath?.let { path ->
        state.voicePacks.firstOrNull { it.dir.absolutePath == path }
    }

    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        val target = avatarTarget
        avatarTarget = null
        if (target == null) return@rememberLauncherForActivityResult
        if (result.isSuccessful) {
            val uri = result.uriContent
            if (uri != null) {
                viewModel.updateVoiceAvatar(target, uri)
            } else {
                toast(context, "裁剪失败：无输出")
            }
        } else {
            toast(context, "裁剪失败")
        }
    }
    fun launchAvatarCrop(uri: Uri) {
        val options = CropImageOptions(
            fixAspectRatio = true,
            aspectRatioX = 1,
            aspectRatioY = 1,
            activityTitle = "裁剪头像",
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
            outputRequestWidth = 400,
            outputRequestHeight = 400,
            outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_EXACT,
            guidelines = CropImageView.Guidelines.ON
        )
        cropLauncher.launch(CropImageContractOptions(uri, options))
    }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            launchAvatarCrop(uri)
        } else {
            avatarTarget = null
        }
    }
    LaunchedEffect(Unit) {
        viewModel.refreshVoicePacks()
    }

    CenteredPageColumn(
        maxWidth = UiTokens.WideListMaxWidth,
        contentSpacing = 0.dp
    ) {
            if (state.voicePacks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(Modifier.height(UiTokens.PageTopBlank))
                        Text("暂无语音包，请点击主标题栏导入按钮。")
                        Spacer(Modifier.height(UiTokens.PageBottomBlank))
                    }
                }
            } else {
                VoicePackRecyclerList(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    packs = state.voicePacks,
                    currentVoicePath = state.voiceDir?.absolutePath,
                    topBlankHeight = UiTokens.PageTopBlank,
                    bottomBlankHeight = UiTokens.PageBottomBlank,
                    onSelect = { viewModel.selectVoice(it.dir) },
                    onTogglePin = { viewModel.toggleVoicePin(it) },
                    onDetail = { pack ->
                        detailPackPath = pack.dir.absolutePath
                        detailName = pack.meta.name
                        detailRemark = pack.meta.remark
                        detailEditing = false
                    },
                    onShare = { viewModel.shareVoice(it) },
                    onDelete = { deletePack = it },
                    onReorder = { newOrder -> viewModel.reorderVoicePacks(newOrder) }
                )
            }
    }

    if (detailPack != null && isKokoroVoiceDir(detailPack.dir)) {
        KokoroVoiceSettingsDialog(
            state = state,
            onDismiss = { detailPackPath = null },
            onSpeakerChange = { viewModel.setKokoroSpeakerId(it) }
        )
    } else if (detailPack != null) {
        val avatarFile = remember(detailPack.dir.absolutePath, detailPack.meta.avatar) {
            File(detailPack.dir, detailPack.meta.avatar)
        }
        val avatarBitmap = rememberAvatarBitmap(avatarFile)
        AlertDialog(
            onDismissRequest = { detailPackPath = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("语音包详细信息", modifier = Modifier.weight(1f))
                    Md2IconButton(
                        icon = if (detailEditing) "check" else "edit",
                        contentDescription = if (detailEditing) "完成编辑" else "编辑",
                        onClick = { detailEditing = !detailEditing }
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        if (avatarBitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = avatarBitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(UiTokens.Radius))
                            )
                        } else {
                            VoicePackAvatarPlaceholder(
                                modifier = Modifier.size(64.dp),
                                isSystemPack = isSystemTtsVoiceDir(detailPack.dir),
                                isKokoroPack = isKokoroVoiceDir(detailPack.dir),
                                logoSize = 50.dp
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            if (!detailEditing) {
                                Text("名称：${detailPack.meta.name}", style = MaterialTheme.typography.bodyMedium)
                                val remarkText = detailPack.meta.remark.ifBlank { "无" }
                                Text("备注：$remarkText", style = MaterialTheme.typography.bodySmall)
                            } else {
                                Text("文件名：${detailPack.dir.name}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        if (detailEditing) {
                            Md2IconButton(
                                icon = "image",
                                contentDescription = "更换头像",
                                onClick = {
                                    avatarTarget = detailPack
                                    if (state.useBuiltinGallery) {
                                        showBuiltinAvatarGallery = true
                                    } else {
                                        imagePicker.launch("image/*")
                                    }
                                }
                            )
                        }
                    }
                    if (detailEditing) {
                        Md2OutlinedField(
                            value = detailName,
                            onValueChange = { detailName = it },
                            label = "名称"
                        )
                        Md2OutlinedField(
                            value = detailRemark,
                            onValueChange = { detailRemark = it },
                            label = "备注"
                        )
                    } else {
                        Text("文件名：${detailPack.dir.name}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                if (detailEditing) {
                    Md2TextButton(onClick = {
                        viewModel.updateVoiceMeta(detailPack, detailName, detailRemark)
                        detailEditing = false
                    }) {
                        Text("保存")
                    }
                }
            },
            dismissButton = {
                Md2TextButton(onClick = {
                    if (detailEditing) {
                        detailEditing = false
                        detailName = detailPack.meta.name
                        detailRemark = detailPack.meta.remark
                    } else {
                        detailPackPath = null
                    }
                }) {
                    Text(if (detailEditing) "取消编辑" else "关闭")
                }
            }
        )
    }

    if (deletePack != null) {
        val deletingKokoro = deletePack?.let { isKokoroVoiceDir(it.dir) } == true
        AlertDialog(
            onDismissRequest = { deletePack = null },
            title = { Text(if (deletingKokoro) "删除 Kokoro 离线语音" else "删除语音包") },
            text = {
                Text(
                    if (deletingKokoro) {
                        "确定删除 Kokoro 离线语音吗？删除后，“语音包”页面将不再显示 Kokoro，需要重新下载或本地安装后才能使用。"
                    } else {
                        "确定删除该语音包吗？此操作不可撤销。"
                    }
                )
            },
            confirmButton = {
                Md2TextButton(onClick = {
                    val pack = deletePack
                    if (pack != null) {
                        viewModel.deleteVoice(pack)
                    }
                    deletePack = null
                }) {
                    Text("删除")
                }
            },
            dismissButton = {
                Md2TextButton(onClick = { deletePack = null }) {
                    Text("取消")
                }
            }
        )
    }

    if (showBuiltinAvatarGallery) {
        BuiltinGalleryPickerDialog(
            title = "选择头像",
            onDismiss = {
                showBuiltinAvatarGallery = false
                avatarTarget = null
            },
            onPicked = { uri ->
                showBuiltinAvatarGallery = false
                launchAvatarCrop(uri)
            }
        )
    }
}

@Composable
internal fun VoicePackRecyclerList(
    modifier: Modifier = Modifier,
    packs: List<VoicePackInfo>,
    currentVoicePath: String?,
    topBlankHeight: Dp,
    bottomBlankHeight: Dp,
    onSelect: (VoicePackInfo) -> Unit,
    onTogglePin: (VoicePackInfo) -> Unit,
    onDetail: (VoicePackInfo) -> Unit,
    onShare: (VoicePackInfo) -> Unit,
    onDelete: (VoicePackInfo) -> Unit,
    onReorder: (List<VoicePackInfo>) -> Unit
) {
    val parentComposition = rememberCompositionContext()
    val density = LocalDensity.current
    val topBlankPx = with(density) { topBlankHeight.roundToPx() }
    val bottomBlankPx = with(density) { bottomBlankHeight.roundToPx() }

    val onSelectState = rememberUpdatedState(onSelect)
    val onTogglePinState = rememberUpdatedState(onTogglePin)
    val onDetailState = rememberUpdatedState(onDetail)
    val onShareState = rememberUpdatedState(onShare)
    val onDeleteState = rememberUpdatedState(onDelete)
    val onReorderState = rememberUpdatedState(onReorder)

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val recycler = RecyclerView(ctx).apply {
                layoutManager = object : LinearLayoutManager(ctx) {
                    override fun supportsPredictiveItemAnimations(): Boolean = false
                }
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

            val adapter = VoicePackRecyclerAdapter(
                parentComposition = parentComposition,
                onSelect = { onSelectState.value(it) },
                onTogglePin = { onTogglePinState.value(it) },
                onDetail = { onDetailState.value(it) },
                onShare = { onShareState.value(it) },
                onDelete = { onDeleteState.value(it) }
            )
            recycler.adapter = adapter

            val touchCallback = object : ItemTouchHelper.Callback() {
                private var moved = false
                private var activeViewHolder: RecyclerView.ViewHolder? = null
                private val edgeAutoScroller = DragEdgeAutoScroller()

                override fun isLongPressDragEnabled(): Boolean = false
                override fun isItemViewSwipeEnabled(): Boolean = false

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    if (!adapter.isDraggableAdapterPosition(viewHolder.bindingAdapterPosition)) {
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
                    val ok = adapter.moveWithinPinnedGroupAdapterPositions(from, to)
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
                        if (activeViewHolder !== viewHolder) {
                            activeViewHolder?.let { animateDragElevation(it.itemView, elevated = false) }
                            (activeViewHolder as? VoicePackRecyclerAdapter.VoicePackViewHolder)?.setDragged(false)
                        }
                        activeViewHolder = viewHolder
                        (viewHolder as? VoicePackRecyclerAdapter.VoicePackViewHolder)?.setDragged(true)
                        animateDragElevation(viewHolder.itemView, elevated = true)
                    } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                        edgeAutoScroller.stop()
                        activeViewHolder?.let { animateDragElevation(it.itemView, elevated = false) }
                        (activeViewHolder as? VoicePackRecyclerAdapter.VoicePackViewHolder)?.setDragged(false)
                        activeViewHolder = null
                    }
                    // Keep drag-lock until clearView so stale state cannot overwrite moved order.
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        adapter.isDragging = true
                    }
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    edgeAutoScroller.stop()
                    super.clearView(recyclerView, viewHolder)
                    animateDragElevation(viewHolder.itemView, elevated = false)
                    (viewHolder as? VoicePackRecyclerAdapter.VoicePackViewHolder)?.setDragged(false)
                    if (activeViewHolder === viewHolder) activeViewHolder = null
                    adapter.isDragging = false
                    if (moved) {
                        val snapshot = adapter.snapshot()
                        adapter.awaitExternalCommit(snapshot)
                        recyclerView.post { onReorderState.value(snapshot) }
                        moved = false
                    }
                }
            }
            val touchHelper = ItemTouchHelper(touchCallback)
            touchHelper.attachToRecyclerView(recycler)
            adapter.onStartDrag = { vh ->
                touchHelper.startDrag(vh)
            }
            recycler
        },
        update = { recycler ->
            val adapter = recycler.adapter as? VoicePackRecyclerAdapter ?: return@AndroidView
            val applyState = {
                recycler.setPadding(recycler.paddingLeft, topBlankPx, recycler.paddingRight, bottomBlankPx)
                adapter.updateCurrentVoicePath(currentVoicePath)
                adapter.submitFromState(packs)
            }
            // Always defer adapter updates to avoid dispatching notify* in an active layout pass.
            recycler.post(applyState)
        }
    )
}

internal class VoicePackRecyclerAdapter(
    private val parentComposition: CompositionContext,
    private val onSelect: (VoicePackInfo) -> Unit,
    private val onTogglePin: (VoicePackInfo) -> Unit,
    private val onDetail: (VoicePackInfo) -> Unit,
    private val onShare: (VoicePackInfo) -> Unit,
    private val onDelete: (VoicePackInfo) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<VoicePackInfo>()
    private val stagedAppearedIds = hashSetOf<Long>()
    private var runStaggerOnNextBind = true
    private var staggerReleaseScheduled = false
    private var nextStableId = 1L
    private val stableIdsByPath = hashMapOf<String, Long>()
    private var awaitingCommitPaths: List<String>? = null
    private var awaitingCommitDeadlineMs: Long = 0L
    var isDragging: Boolean = false
    var onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null

    var currentVoicePath: String? = null
        private set

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        if (position !in items.indices) return RecyclerView.NO_ID
        return stableIdForPath(items[position].dir.absolutePath)
    }

    override fun getItemViewType(position: Int): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val composeView = ComposeView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setParentCompositionContext(parentComposition)
        }
        return VoicePackViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as VoicePackViewHolder
        if (!isDragging) {
            holder.itemView.translationZ = 0f
        }
        val itemId = getItemId(position)
        val shouldStagger = runStaggerOnNextBind && !stagedAppearedIds.contains(itemId)
        if (shouldStagger) {
            stagedAppearedIds.add(itemId)
            val dataIndex = position.coerceAtLeast(0)
            animateVoicePackStaggerEnter(holder.itemView, dataIndex)
            if (!staggerReleaseScheduled) {
                staggerReleaseScheduled = true
                holder.itemView.postDelayed(
                    { runStaggerOnNextBind = false },
                    560L
                )
            }
        } else {
            holder.itemView.animate().cancel()
            holder.itemView.alpha = 1f
            holder.itemView.translationY = 0f
        }
        holder.setDragged(false)
        val dataIndex = position
        if (dataIndex !in items.indices) return
        val pack = items[dataIndex]
        holder.bind(
            pack = pack,
            isCurrent = currentVoicePath == pack.dir.absolutePath,
            onSelect = onSelect,
            onTogglePin = onTogglePin,
            onDetail = onDetail,
            onShare = onShare,
            onDelete = onDelete,
            onStartDrag = {
                if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onStartDrag?.invoke(holder)
                }
            }
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is VoicePackViewHolder) {
            holder.setDragged(false)
        }
        holder.itemView.translationZ = 0f
        holder.itemView.alpha = 1f
        holder.itemView.translationY = 0f
        super.onViewRecycled(holder)
    }

    fun submitFromState(newItems: List<VoicePackInfo>) {
        if (isDragging) {
            // During drag, ignore external list pushes.
            // Final order is committed by onReorder callback on clearView.
            return
        }
        awaitingCommitPaths?.let { pending ->
            val incoming = newItems.map { it.dir.absolutePath }
            when {
                incoming == pending -> {
                    awaitingCommitPaths = null
                    awaitingCommitDeadlineMs = 0L
                }
                SystemClock.uptimeMillis() < awaitingCommitDeadlineMs -> {
                    // Drop stale state while waiting for committed order from ViewModel.
                    return
                }
                else -> {
                    awaitingCommitPaths = null
                    awaitingCommitDeadlineMs = 0L
                }
            }
        }
        if (items == newItems) return
        val shouldRunStagger = items.isEmpty() && newItems.isNotEmpty()
        if (shouldRunStagger) {
            runStaggerOnNextBind = true
            staggerReleaseScheduled = false
            stagedAppearedIds.clear()
        }
        val oldItems = items.toList()
        val oldCount = oldItems.size
        val newCount = newItems.size
        val newPaths = newItems.asSequence().map { it.dir.absolutePath }.toHashSet()
        stableIdsByPath.keys.retainAll(newPaths)

        items.clear()
        items.addAll(newItems)

        if (oldCount == 0 || newCount == 0) {
            notifyDataSetChanged()
            return
        }

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size
            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].dir.absolutePath == newItems[newItemPosition].dir.absolutePath
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = oldItems[oldItemPosition]
                val new = newItems[newItemPosition]
                if (old.dir.absolutePath != new.dir.absolutePath) return false
                // Ignore "order" in content comparison: it is persistence metadata and
                // can change after drag commit without any visible row content change.
                return old.meta.name == new.meta.name &&
                        old.meta.remark == new.meta.remark &&
                        old.meta.avatar == new.meta.avatar &&
                        old.meta.pinned == new.meta.pinned
            }
        })
        diff.dispatchUpdatesTo(this)
    }

    fun updateCurrentVoicePath(path: String?) {
        if (currentVoicePath == path) return
        val oldPath = currentVoicePath
        currentVoicePath = path
        if (isDragging) return
        val oldIdx = oldPath?.let { p -> items.indexOfFirst { it.dir.absolutePath == p } } ?: -1
        val newIdx = path?.let { p -> items.indexOfFirst { it.dir.absolutePath == p } } ?: -1
        if (oldIdx >= 0) notifyItemChanged(oldIdx)
        if (newIdx >= 0 && newIdx != oldIdx) notifyItemChanged(newIdx)
    }

    fun snapshot(): List<VoicePackInfo> = items.toList()

    fun awaitExternalCommit(snapshot: List<VoicePackInfo>) {
        awaitingCommitPaths = snapshot.map { it.dir.absolutePath }
        awaitingCommitDeadlineMs = SystemClock.uptimeMillis() + 1800L
    }

    fun isDraggableAdapterPosition(position: Int): Boolean {
        if (position == RecyclerView.NO_POSITION) return false
        return position in items.indices
    }

    fun moveWithinPinnedGroupAdapterPositions(fromAdapter: Int, toAdapter: Int): Boolean {
        if (!isDraggableAdapterPosition(fromAdapter) || !isDraggableAdapterPosition(toAdapter)) {
            return false
        }
        val from = fromAdapter
        val to = toAdapter
        if (from == to || from !in items.indices || to !in items.indices) return false
        val fromPinned = items[from].meta.pinned
        val toPinned = items[to].meta.pinned
        if (fromPinned != toPinned) return false
        items.move(from, to)
        notifyItemMoved(fromAdapter, toAdapter)
        return true
    }

    private fun stableIdForPath(path: String): Long {
        return stableIdsByPath.getOrPut(path) { nextStableId++ }
    }

    class VoicePackViewHolder(
        private val composeView: ComposeView
    ) : RecyclerView.ViewHolder(composeView) {
        private val draggedState = mutableStateOf(false)

        fun setDragged(dragged: Boolean) {
            draggedState.value = dragged
        }

        fun bind(
            pack: VoicePackInfo,
            isCurrent: Boolean,
            onSelect: (VoicePackInfo) -> Unit,
            onTogglePin: (VoicePackInfo) -> Unit,
            onDetail: (VoicePackInfo) -> Unit,
            onShare: (VoicePackInfo) -> Unit,
            onDelete: (VoicePackInfo) -> Unit,
            onStartDrag: () -> Unit
        ) {
            composeView.setContent {
                KigttsFontScaleProvider {
                    VoicePackCardContent(
                        pack = pack,
                        isCurrent = isCurrent,
                        isDragged = draggedState.value,
                        onSelect = { onSelect(pack) },
                        onTogglePin = { onTogglePin(pack) },
                        onDetail = { onDetail(pack) },
                        onShare = { onShare(pack) },
                        onDelete = { onDelete(pack) },
                        onStartDrag = onStartDrag
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
internal fun VoicePackCardContent(
    pack: VoicePackInfo,
    isCurrent: Boolean,
    isDragged: Boolean,
    onSelect: () -> Unit,
    onTogglePin: () -> Unit,
    onDetail: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onStartDrag: () -> Unit
) {
    val isSystemPack = isSystemTtsVoiceDir(pack.dir)
    val isKokoroPack = isKokoroVoiceDir(pack.dir)
    val avatarFile = File(pack.dir, pack.meta.avatar)
    val avatarBitmap = rememberAvatarBitmap(avatarFile)
    val cardElevation by animateDpAsState(
        targetValue = if (isDragged) 10.dp else UiTokens.CardElevation,
        animationSpec = tween(
            durationMillis = if (isDragged) 120 else 160,
            easing = FastOutSlowInEasing
        ),
        label = "voice_pack_card_elevation"
    )

    Box(modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp)) {
        Card(
            shape = RoundedCornerShape(UiTokens.Radius),
            backgroundColor = md2CardContainerColor(),
            elevation = cardElevation
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (avatarBitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = avatarBitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(UiTokens.Radius))
                        )
                    } else {
                        VoicePackAvatarPlaceholder(
                            modifier = Modifier.size(72.dp),
                            isSystemPack = isSystemPack,
                            isKokoroPack = isKokoroPack,
                            logoSize = 58.dp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(pack.meta.name, fontWeight = FontWeight.SemiBold)
                            if (isSystemPack) {
                                Spacer(Modifier.width(6.dp))
                                Text("系统", style = MaterialTheme.typography.bodySmall)
                            }
                            if (pack.meta.pinned) {
                                Spacer(Modifier.width(6.dp))
                                Text("置顶", style = MaterialTheme.typography.bodySmall)
                            }
                            if (isCurrent) {
                                Spacer(Modifier.width(6.dp))
                                Text("当前", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        if (pack.meta.remark.isNotBlank()) {
                            Text(pack.meta.remark, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Md2IconButton(
                        icon = "drag_indicator",
                        contentDescription = "按住拖动排序",
                        onClick = {},
                        modifier = Modifier.pointerInteropFilter { ev ->
                            when (ev.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    onStartDrag()
                                    true
                                }
                                MotionEvent.ACTION_UP,
                                MotionEvent.ACTION_CANCEL,
                                MotionEvent.ACTION_MOVE -> true
                                else -> false
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Md2IconButton(
                        icon = if (isCurrent) "check_circle" else "play_circle",
                        contentDescription = if (isCurrent) "当前使用" else "使用该语音包",
                        onClick = onSelect,
                        enabled = !isCurrent
                    )
                    Md2IconButton(
                        icon = if (pack.meta.pinned) "keep_off" else "push_pin",
                        contentDescription = if (pack.meta.pinned) "取消置顶" else "置顶",
                        onClick = onTogglePin
                    )
                    Md2IconButton(
                        icon = if (isKokoroPack) "settings" else "info",
                        contentDescription = if (isKokoroPack) "Kokoro 设置" else "语音包详细信息",
                        onClick = onDetail,
                        enabled = !isSystemPack
                    )
                    Md2IconButton(
                        icon = "share",
                        contentDescription = "分享语音包",
                        onClick = onShare,
                        enabled = !isSystemPack && !isKokoroPack
                    )
                    Md2IconButton(
                        icon = "delete",
                        contentDescription = "删除语音包",
                        onClick = onDelete,
                        enabled = !isSystemPack
                    )
                }
            }
        }
    }
}

internal fun MutableList<VoicePackInfo>.move(from: Int, to: Int) {
    if (from == to || from !in indices || to !in indices) return
    val item = removeAt(from)
    add(to, item)
}

internal fun stablePathId64(path: String): Long {
    // 64-bit FNV-1a hash to reduce stable-id collisions on long paths.
    var h = -3750763034362895579L
    path.forEach { ch ->
        h = h xor ch.code.toLong()
        h *= 1099511628211L
    }
    return h
}

internal class DragEdgeAutoScroller {
    private var recyclerView: RecyclerView? = null
    private var externalScrollBy: ((Int) -> Boolean)? = null
    private var direction: Int = 0
    private var stepPx: Int = 0
    private var isPosted: Boolean = false

    private val scrollTick = object : Runnable {
        override fun run() {
            val rv = recyclerView
            val dir = direction
            if (rv == null || dir == 0 || !rv.isAttachedToWindow) {
                direction = 0
                stepPx = 0
                recyclerView = null
                externalScrollBy = null
                isPosted = false
                return
            }
            val delta = dir * stepPx.coerceAtLeast(1)
            val consumed = if (rv.canScrollVertically(dir)) {
                rv.scrollBy(0, delta)
                true
            } else {
                externalScrollBy?.invoke(delta) == true
            }
            if (!consumed) {
                direction = 0
                stepPx = 0
                recyclerView = null
                externalScrollBy = null
                isPosted = false
                return
            }
            rv.postOnAnimation(this)
        }
    }

    fun update(
        recyclerView: RecyclerView,
        draggedView: View,
        dY: Float,
        externalScrollBy: ((Int) -> Boolean)? = null
    ) {
        val density = recyclerView.resources.displayMetrics.density
        val edgePx = (72f * density).roundToInt().coerceAtLeast(1)
        val minStepPx = (2f * density).roundToInt().coerceAtLeast(1)
        val maxStepPx = (8f * density).roundToInt().coerceAtLeast(minStepPx)
        val (draggedTop, draggedBottom, topEdge, bottomEdge) = if (externalScrollBy != null) {
            val visibleFrame = android.graphics.Rect()
            val location = IntArray(2)
            recyclerView.getWindowVisibleDisplayFrame(visibleFrame)
            draggedView.getLocationOnScreen(location)
            val itemTop = location[1] + dY
            val itemBottom = location[1] + draggedView.height + dY
            Quad(itemTop, itemBottom, visibleFrame.top + edgePx.toFloat(), visibleFrame.bottom - edgePx.toFloat())
        } else {
            Quad(
                draggedView.top + dY,
                draggedView.bottom + dY,
                recyclerView.paddingTop + edgePx.toFloat(),
                recyclerView.height - recyclerView.paddingBottom - edgePx.toFloat()
            )
        }
        val topOverlap = topEdge - draggedTop
        val bottomOverlap = draggedBottom - bottomEdge
        val nextDirection = when {
            topOverlap > 0f -> -1
            bottomOverlap > 0f -> 1
            else -> 0
        }
        if (nextDirection == 0 || (!recyclerView.canScrollVertically(nextDirection) && externalScrollBy == null)) {
            stop()
            return
        }
        val overlap = if (nextDirection < 0) topOverlap else bottomOverlap
        val factor = (overlap / edgePx).coerceIn(0f, 1f)
        stepPx = (minStepPx + ((maxStepPx - minStepPx) * factor)).roundToInt().coerceAtLeast(minStepPx)
        this.recyclerView = recyclerView
        this.externalScrollBy = externalScrollBy
        direction = nextDirection
        if (!isPosted) {
            isPosted = true
            recyclerView.postOnAnimation(scrollTick)
        }
    }

    fun stop() {
        direction = 0
        stepPx = 0
        recyclerView = null
        externalScrollBy = null
    }

    private data class Quad(
        val draggedTop: Float,
        val draggedBottom: Float,
        val topEdge: Float,
        val bottomEdge: Float
    )
}

internal fun animateDragElevation(view: View, elevated: Boolean) {
    val targetZ = if (elevated) 12f * view.resources.displayMetrics.density else 0f
    val duration = if (elevated) 120L else 160L
    view.animate()
        .cancel()
    view.animate()
        .translationZ(targetZ)
        .setDuration(duration)
        .setInterpolator(FastOutSlowInInterpolator())
        .start()
}

internal fun animateVoicePackStaggerEnter(view: View, position: Int) {
    val density = view.resources.displayMetrics.density
    val offsetY = 12f * density
    val delayMs = (position.coerceIn(0, 10) * 36L)
    view.animate().cancel()
    view.animate().setListener(null)
    view.alpha = 0f
    view.translationY = offsetY
    view.animate()
        .alpha(1f)
        .translationY(0f)
        .setStartDelay(delayMs)
        .setDuration(220L)
        .setInterpolator(FastOutSlowInInterpolator())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                resetVoicePackStaggerView(view)
            }

            override fun onAnimationEnd(animation: Animator) {
                resetVoicePackStaggerView(view)
            }
        })
        .start()
    view.postDelayed(
        { resetVoicePackStaggerView(view) },
        delayMs + 300L
    )
}

internal fun resetVoicePackStaggerView(view: View) {
    view.animate().setListener(null)
    view.alpha = 1f
    view.translationY = 0f
}


