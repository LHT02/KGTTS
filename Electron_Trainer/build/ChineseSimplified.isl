; Simplified Chinese messages for KIGTTS Trainer Inno installer.
; Loaded after compiler:Default.isl so any untranslated fallback remains valid.

[LangOptions]
LanguageName=简体中文
LanguageID=$0804
LanguageCodePage=936
DialogFontName=Microsoft YaHei UI
DialogFontSize=9
WelcomeFontName=Microsoft YaHei UI
WelcomeFontSize=14

[Messages]
SetupAppTitle=安装
SetupWindowTitle=安装 - %1
UninstallAppTitle=卸载
UninstallAppFullTitle=%1 卸载

InformationTitle=信息
ConfirmTitle=确认
ErrorTitle=错误

SetupLdrStartupMessage=即将安装 %1。是否继续？
LdrCannotCreateTemp=无法创建临时文件。安装已中止
LdrCannotExecTemp=无法执行临时目录中的文件。安装已中止

LastErrorMessage=%1。%n%n错误 %2：%3
SetupFileMissing=安装目录中缺少文件 %1。请修复该问题，或重新获取安装程序。
SetupFileCorrupt=安装文件已损坏。请重新获取安装程序。
SetupFileCorruptOrWrongVer=安装文件已损坏，或与当前安装程序版本不兼容。请重新获取安装程序。
InvalidParameter=命令行参数无效：%n%n%1
SetupAlreadyRunning=安装程序已经在运行。
WindowsVersionNotSupported=此程序不支持当前 Windows 版本。
AdminPrivilegesRequired=安装此程序需要管理员权限。
PowerUserPrivilegesRequired=安装此程序需要管理员权限或 Power Users 组权限。
SetupAppRunningError=检测到 %1 正在运行。%n%n请先关闭它，然后点击“确定”继续，或点击“取消”退出。
UninstallAppRunningError=检测到 %1 正在运行。%n%n请先关闭它，然后点击“确定”继续，或点击“取消”退出。

ErrorCreatingDir=安装程序无法创建目录“%1”
ErrorTooManyFilesInDir=无法在目录“%1”中创建文件，因为该目录包含过多文件

ExitSetupTitle=退出安装
ExitSetupMessage=安装尚未完成。如果现在退出，程序将不会被安装。%n%n你可以稍后再次运行安装程序完成安装。%n%n是否退出安装？
AboutSetupMenuItem=关于安装程序(&A)...
AboutSetupTitle=关于安装程序
AboutSetupMessage=%1 版本 %2%n%3%n%n%1 主页：%n%4

ButtonBack=< 上一步(&B)
ButtonNext=下一步(&N) >
ButtonInstall=安装(&I)
ButtonOK=确定
ButtonCancel=取消
ButtonYes=是(&Y)
ButtonYesToAll=全部是(&A)
ButtonNo=否(&N)
ButtonNoToAll=全部否(&O)
ButtonFinish=完成(&F)
ButtonBrowse=浏览(&B)...
ButtonWizardBrowse=浏览(&R)...
ButtonNewFolder=新建文件夹(&M)

SelectLanguageTitle=选择安装语言
SelectLanguageLabel=请选择安装过程中使用的语言。

ClickNext=点击“下一步”继续，或点击“取消”退出安装。
BrowseDialogTitle=浏览文件夹
BrowseDialogLabel=请在下面的列表中选择文件夹，然后点击“确定”。
NewFolderName=新建文件夹

WelcomeLabel1=欢迎使用 [name] 安装向导
WelcomeLabel2=本向导将在你的电脑上安装 [name/ver]。%n%n建议在继续安装前关闭其它应用程序。

WizardSelectDir=选择安装位置
SelectDirDesc=[name] 将安装到哪里？
SelectDirLabel3=安装程序会将 [name] 安装到以下文件夹。
SelectDirBrowseLabel=点击“下一步”继续。如需选择其它文件夹，请点击“浏览”。
DiskSpaceGBLabel=至少需要 [gb] GB 可用磁盘空间。
DiskSpaceMBLabel=至少需要 [mb] MB 可用磁盘空间。
CannotInstallToNetworkDrive=安装程序不能安装到网络驱动器。
CannotInstallToUNCPath=安装程序不能安装到 UNC 路径。
InvalidPath=请输入包含盘符的完整路径，例如：%n%nC:\APP%n%n或 UNC 路径：%n%n\\server\share
InvalidDrive=选择的驱动器或 UNC 共享不存在或无法访问。请选择其它位置。
DiskSpaceWarningTitle=磁盘空间不足
DiskSpaceWarning=安装程序至少需要 %1 KB 可用空间，但所选驱动器只有 %2 KB 可用。%n%n是否仍要继续？
DirNameTooLong=文件夹名称或路径过长。
InvalidDirName=文件夹名称无效。
DirExistsTitle=文件夹已存在
DirExists=文件夹：%n%n%1%n%n已经存在。是否仍要安装到该文件夹？
DirDoesntExistTitle=文件夹不存在
DirDoesntExist=文件夹：%n%n%1%n%n不存在。是否创建该文件夹？

WizardSelectProgramGroup=选择开始菜单文件夹
SelectStartMenuFolderDesc=安装程序应把快捷方式放在哪里？
SelectStartMenuFolderLabel3=安装程序将在以下开始菜单文件夹中创建快捷方式。
SelectStartMenuFolderBrowseLabel=点击“下一步”继续。如需选择其它文件夹，请点击“浏览”。
MustEnterGroupName=请输入文件夹名称。
GroupNameTooLong=文件夹名称或路径过长。
InvalidGroupName=文件夹名称无效。
NoProgramGroupCheck2=不创建开始菜单文件夹(&D)

WizardSelectTasks=选择附加任务
SelectTasksDesc=需要执行哪些附加任务？
SelectTasksLabel2=请选择安装 [name] 时需要执行的附加任务，然后点击“下一步”。

WizardReady=准备安装
ReadyLabel1=安装程序已准备好开始在你的电脑上安装 [name]。
ReadyLabel2a=点击“安装”继续；如需检查或修改设置，请点击“上一步”。
ReadyLabel2b=点击“安装”继续。
ReadyMemoUserInfo=用户信息：
ReadyMemoDir=安装位置：
ReadyMemoType=安装类型：
ReadyMemoComponents=选择的组件：
ReadyMemoGroup=开始菜单文件夹：
ReadyMemoTasks=附加任务：

WizardPreparing=准备安装
PreparingDesc=安装程序正在准备安装 [name]。
PreviousInstallNotCompleted=上一个程序的安装/卸载尚未完成。你需要重启电脑才能完成。%n%n重启后，请再次运行安装程序以完成 [name] 的安装。
CannotContinue=安装程序无法继续。请点击“取消”退出。
ApplicationsFound=以下应用正在使用需要更新的文件。建议允许安装程序自动关闭这些应用。
ApplicationsFound2=以下应用正在使用需要更新的文件。建议允许安装程序自动关闭这些应用。安装完成后，安装程序会尝试重新启动这些应用。
CloseApplications=自动关闭这些应用(&A)
DontCloseApplications=不要关闭这些应用(&D)
ErrorCloseApplications=安装程序无法自动关闭所有应用。建议先手动关闭正在使用相关文件的应用，然后继续。
PrepareToInstallNeedsRestart=安装程序必须重启电脑。重启后，请再次运行安装程序以完成 [name] 的安装。%n%n是否现在重启？

WizardInstalling=正在安装
InstallingLabel=请稍候，安装程序正在安装 [name]。

FinishedHeadingLabel=正在完成 [name] 安装向导
FinishedLabelNoIcons=安装程序已完成 [name] 的安装。
FinishedLabel=安装程序已完成 [name] 的安装。可以通过已创建的快捷方式启动应用。
ClickFinish=点击“完成”退出安装程序。
FinishedRestartLabel=要完成 [name] 的安装，必须重启电脑。是否现在重启？
FinishedRestartMessage=要完成 [name] 的安装，必须重启电脑。%n%n是否现在重启？
YesRadio=是，立即重启电脑(&Y)
NoRadio=否，稍后自行重启(&N)
RunEntryExec=运行 %1
RunEntryShellExec=查看 %1

SetupAborted=安装未完成。%n%n请修复问题后重新运行安装程序。
AbortRetryIgnoreSelectAction=选择操作
AbortRetryIgnoreRetry=重试(&T)
AbortRetryIgnoreIgnore=忽略错误并继续(&I)
AbortRetryIgnoreCancel=取消安装
RetryCancelSelectAction=选择操作
RetryCancelRetry=重试(&T)
RetryCancelCancel=取消

StatusClosingApplications=正在关闭应用...
StatusCreateDirs=正在创建目录...
StatusExtractFiles=正在解压文件...
StatusDownloadFiles=正在下载文件...
StatusCreateIcons=正在创建快捷方式...
StatusCreateIniEntries=正在创建 INI 条目...
StatusCreateRegistryEntries=正在创建注册表项...
StatusRegisterFiles=正在注册文件...
StatusSavingUninstall=正在保存卸载信息...
StatusRunProgram=正在完成安装...
StatusRestartingApplications=正在重启应用...
StatusRollback=正在回滚更改...

ErrorInternal2=内部错误：%1
ErrorFunctionFailedNoCode=%1 失败
ErrorFunctionFailed=%1 失败；代码 %2
ErrorFunctionFailedWithMessage=%1 失败；代码 %2。%n%3
ErrorExecutingProgram=无法执行文件：%n%1

SourceIsCorrupted=源文件已损坏
SourceDoesntExist=源文件“%1”不存在
ExistingFileReadOnly2=现有文件被标记为只读，无法替换。
ExistingFileReadOnlyRetry=移除只读属性并重试(&R)
ExistingFileReadOnlyKeepExisting=保留现有文件(&K)
FileExistsSelectAction=选择操作
FileExists2=文件已存在。
FileExistsOverwriteExisting=覆盖现有文件(&O)
FileExistsKeepExisting=保留现有文件(&K)
FileExistsOverwriteOrKeepAll=对后续冲突也执行此操作(&D)
ExistingFileNewerSelectAction=选择操作
ExistingFileNewer2=现有文件比安装程序要安装的文件更新。
ExistingFileNewerOverwriteExisting=覆盖现有文件(&O)
ExistingFileNewerKeepExisting=保留现有文件（推荐）(&K)
ExistingFileNewerOverwriteOrKeepAll=对后续冲突也执行此操作(&D)
ErrorReadingSource=读取源文件时出错：
ErrorCopying=复制文件时出错：
ErrorReplacingExistingFile=替换现有文件时出错：

UninstallNotFound=文件“%1”不存在。无法卸载。
UninstallOpenError=无法打开文件“%1”。无法卸载
UninstallUnsupportedVer=卸载日志文件“%1”的格式无法被当前卸载程序识别。无法卸载
ConfirmUninstall=是否确认完全移除 %1 及其所有组件？
OnlyAdminCanUninstall=只有具备管理员权限的用户才能卸载此安装。
UninstallStatusLabel=请稍候，正在从你的电脑移除 %1。
UninstalledAll=%1 已从你的电脑成功移除。
UninstalledMost=%1 卸载完成。%n%n有些项目无法移除，可手动删除。
UninstalledAndNeedsRestart=要完成 %1 的卸载，必须重启电脑。%n%n是否现在重启？
UninstallDataCorrupted=“%1”文件已损坏。无法卸载
WizardUninstalling=卸载状态
StatusUninstalling=正在卸载 %1...

[CustomMessages]
NameAndVersion=%1 版本 %2
AdditionalIcons=附加快捷方式：
CreateDesktopIcon=创建桌面快捷方式(&D)
CreateQuickLaunchIcon=创建快速启动快捷方式(&Q)
ProgramOnTheWeb=%1 主页
UninstallProgram=卸载 %1
LaunchProgram=启动 %1
AssocFileExtension=将 %1 关联到 %2 文件扩展名(&A)
AssocingFileExtension=正在关联 %1 到 %2 文件扩展名...
AutoStartProgramGroupDescription=开机启动：
AutoStartProgram=自动启动 %1
