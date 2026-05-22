package com.lhtstudio.kigtts.app.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.lhtstudio.kigtts.app.R

@Composable
internal fun KigttsStartupLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Image(
                painter = painterResource(
                    id = if (currentAppDarkTheme()) R.drawable.logo_white else R.drawable.logo_black
                ),
                contentDescription = "KIGTTS Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.48f)
                    .height(58.dp)
            )
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun KigttsOnboardingScreen(
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnComplete by rememberUpdatedState(onComplete)
    var page by rememberSaveable { mutableIntStateOf(0) }
    var privacyAccepted by rememberSaveable { mutableStateOf(false) }
    var privacyOpen by rememberSaveable { mutableStateOf(false) }
    var refreshToken by remember { mutableIntStateOf(0) }
    val pageCount = 3

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        refreshToken++
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        refreshToken++
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        refreshToken++
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshToken++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = if (currentAppDarkTheme()) 0.24f else 0.18f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (
                    slideInHorizontally { it * direction } + fadeIn()
                    ).togetherWith(
                    slideOutHorizontally { -it * direction } + fadeOut()
                )
            },
            label = "kigtts_onboarding_page",
            modifier = Modifier.fillMaxSize()
        ) { targetPage ->
            OnboardingPageFrame(
                page = targetPage,
                pageCount = pageCount,
                canGoBack = targetPage > 0,
                canGoNext = targetPage != 0 || privacyAccepted,
                nextLabel = if (targetPage == pageCount - 1) "开始使用" else "下一步",
                onBack = { page = (page - 1).coerceAtLeast(0) },
                onNext = {
                    if (page == pageCount - 1) {
                        currentOnComplete()
                    } else {
                        page = (page + 1).coerceAtMost(pageCount - 1)
                    }
                }
            ) {
                when (targetPage) {
                    0 -> OnboardingWelcomePage(
                        privacyAccepted = privacyAccepted,
                        onPrivacyAcceptedChange = { privacyAccepted = it },
                        onOpenPrivacy = { privacyOpen = true }
                    )
                    1 -> OnboardingPermissionPage(
                        refreshToken = refreshToken,
                        onRequestMicrophone = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                        onRequestCamera = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        onRequestNotification = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        onOpenOverlaySettings = { openOverlayPermissionSettings(context) }
                    )
                    else -> OnboardingDonePage()
                }
            }
        }

        if (privacyOpen) {
            Dialog(
                onDismissRequest = { privacyOpen = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(Modifier.fillMaxSize()) {
                        LegalDocumentScreen(assetPath = "legal/privacy_policy.md")
                        IconButton(
                            onClick = { privacyOpen = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .statusBarsPadding()
                                .padding(12.dp)
                                .clip(CircleShape)
                                .background(md2CardContainerColor())
                        ) {
                            MsIcon("close", contentDescription = "关闭隐私政策")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageFrame(
    page: Int,
    pageCount: Int,
    canGoBack: Boolean,
    canGoNext: Boolean,
    nextLabel: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = if (index == page) 28.dp else 8.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == page) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.22f)
                            }
                        )
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canGoBack) {
                Md2TextButton(onClick = onBack) {
                    Text("上一步")
                }
            } else {
                Spacer(Modifier.width(1.dp))
            }
            Spacer(Modifier.weight(1f))
            Md2Button(
                onClick = onNext,
                enabled = canGoNext,
                content = { Text(nextLabel) }
            )
        }
    }
}

@Composable
private fun OnboardingWelcomePage(
    privacyAccepted: Boolean,
    onPrivacyAcceptedChange: (Boolean) -> Unit,
    onOpenPrivacy: () -> Unit
) {
    OnboardingCard {
        OnboardingLogo()
        Text(
            text = "欢迎使用 KIGTTS",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "这是一套面向 kigurumi 玩家和现场交流场景的辅助工具。你可以用它显示大字幕、管理快捷文本、展示名片、播放音效，并按需开启悬浮窗和热键。",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        Text(
            text = "使用前请阅读并确认隐私政策。涉及麦克风、相机、悬浮窗、通知、无障碍等能力时，KIGTTS 会在对应功能入口再次说明用途并由你主动授权。",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = privacyAccepted,
                onCheckedChange = onPrivacyAcceptedChange
            )
            Text(
                text = "我已阅读并同意 KIGTTS 隐私政策",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2
            )
        }
        Md2OutlinedButton(
            onClick = onOpenPrivacy,
            modifier = Modifier.fillMaxWidth()
        ) {
            MsIcon("policy", contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("查看完整隐私政策")
        }
    }
}

@Composable
private fun OnboardingPermissionPage(
    refreshToken: Int,
    onRequestMicrophone: () -> Unit,
    onRequestCamera: () -> Unit,
    onRequestNotification: () -> Unit,
    onOpenOverlaySettings: () -> Unit
) {
    val context = LocalContext.current
    @Suppress("UNUSED_VARIABLE")
    val ignoredRefresh = refreshToken
    OnboardingCard {
        MsIcon(
            name = "admin_panel_settings",
            contentDescription = null,
            modifier = Modifier.size(42.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "权限开启引导",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "这些权限都不是必须一次性开启。你可以先跳过，后续使用对应功能时再授权。",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        PermissionGuideRow(
            icon = "mic",
            title = "麦克风",
            description = "用于实时语音识别、音频测试和说话人验证。",
            granted = isPermissionGranted(context, Manifest.permission.RECORD_AUDIO),
            actionLabel = "授权",
            onClick = onRequestMicrophone
        )
        PermissionGuideRow(
            icon = "photo_camera",
            title = "相机",
            description = "用于扫一扫识别二维码，画面仅在本机用于识别。",
            granted = isPermissionGranted(context, Manifest.permission.CAMERA),
            actionLabel = "授权",
            onClick = onRequestCamera
        )
        PermissionGuideRow(
            icon = "notifications",
            title = "通知",
            description = "用于前台服务、实时通知和部分后台运行状态提示。",
            granted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS),
            actionLabel = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) "无需授权" else "授权",
            onClick = onRequestNotification,
            enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        )
        PermissionGuideRow(
            icon = "picture_in_picture",
            title = "悬浮窗",
            description = "用于在其它应用上方显示快捷入口、迷你字幕和迷你名片。",
            granted = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context),
            actionLabel = "去设置",
            onClick = onOpenOverlaySettings
        )
    }
}

@Composable
private fun OnboardingDonePage() {
    OnboardingCard {
        OnboardingLogo()
        MsIcon(
            name = "check_circle",
            contentDescription = null,
            modifier = Modifier.size(54.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "大功告成",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "基础引导已经完成。接下来可以先从便捷字幕开始，也可以在设置中继续调整语音识别、TTS、悬浮窗、音效板和快捷名片。",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = "只有点击“开始使用”后，KIGTTS 才会记录本次引导已完成；如果在前两页退出，下次启动仍会继续显示引导。",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OnboardingCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(UiTokens.Radius),
        backgroundColor = md2CardContainerColor(),
        elevation = UiTokens.CardElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
private fun OnboardingLogo() {
    Image(
        painter = painterResource(
            id = if (currentAppDarkTheme()) R.drawable.logo_white else R.drawable.logo_black
        ),
        contentDescription = "KIGTTS Logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth(0.62f)
            .height(72.dp)
    )
}

@Composable
private fun PermissionGuideRow(
    icon: String,
    title: String,
    description: String,
    granted: Boolean,
    actionLabel: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(UiTokens.Radius))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MsIcon(
            name = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (granted) "已开启" else "未开启",
                    color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.caption
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Md2OutlinedButton(
            onClick = onClick,
            enabled = enabled && !granted
        ) {
            Text(if (granted) "已完成" else actionLabel)
        }
    }
}

private fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

private fun openOverlayPermissionSettings(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching {
        context.startActivity(intent)
    }.onFailure {
        context.startActivity(
            Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
