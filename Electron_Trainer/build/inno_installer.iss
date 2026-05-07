#ifndef MyAppName
  #define MyAppName "KIGTTS Trainer"
#endif

#ifndef MyAppVersion
  #define MyAppVersion "0.0.0"
#endif

#ifndef MyAppPublisher
  #define MyAppPublisher "KIGTTS"
#endif

#ifndef MyAppExeName
  #define MyAppExeName "KIGTTS Trainer.exe"
#endif

#ifndef SourceDir
  #define SourceDir "..\\dist\\win-unpacked"
#endif

#ifndef OutputDir
  #define OutputDir "..\\dist\\inno"
#endif

[Setup]
AppId={{FECF2832-1C60-59FB-908E-C646DE3BE223}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={autopf}\\{#MyAppName}
DefaultGroupName={#MyAppName}
UsePreviousAppDir=no
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
OutputDir={#OutputDir}
OutputBaseFilename={#MyAppName} Setup {#MyAppVersion}
SetupIconFile=icons\kigtts.ico
WizardImageFile=icons\inno_wizard_logo_202x386.png,icons\inno_wizard_logo_269x515.png,icons\inno_wizard_logo_336x643.png,icons\inno_wizard_logo_403x772.png,icons\inno_wizard_logo_430x824.png
WizardSmallImageFile=icons\inno_wizard_small_58.png,icons\inno_wizard_small_77.png,icons\inno_wizard_small_97.png,icons\inno_wizard_small_116.png,icons\inno_wizard_small_124.png,icons\inno_wizard_small_143.png,icons\inno_wizard_small_159.png
UninstallDisplayIcon={app}\\{#MyAppExeName}
Compression=lzma2/ultra64
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "chinesesimplified"; MessagesFile: "compiler:Default.isl,ChineseSimplified.isl"
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "{#SourceDir}\\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\\{#MyAppName}"; Filename: "{app}\\{#MyAppExeName}"
Name: "{group}\\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\\{#MyAppName}"; Filename: "{app}\\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent
