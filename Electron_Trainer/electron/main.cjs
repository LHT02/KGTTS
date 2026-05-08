const { app, BrowserWindow, ipcMain, dialog, shell, Menu } = require('electron');
const path = require('path');
const { spawn } = require('child_process');
const fs = require('fs');

let mainWindow = null;
let backendProc = null;
let rendererReady = false;
let suppressBackendExit = false;
let textContextMenuMode = 'custom';
const pendingBackendEvents = [];

const isDev = !app.isPackaged;
const isMac = process.platform === 'darwin';

if (process.platform === 'win32') {
  app.setAppUserModelId('com.kigtts.trainer');
}

function resolveWindowIcon() {
  if (process.platform !== 'win32' && process.platform !== 'linux') {
    return undefined;
  }
  if (!isDev) {
    const packagedIcon = path.join(process.resourcesPath, 'build', 'icons', 'kigtts.ico');
    if (fs.existsSync(packagedIcon)) {
      return packagedIcon;
    }
    return undefined;
  }
  return path.join(__dirname, '..', 'build', 'icons', 'kigtts.ico');
}

function resolvePython() {
  if (process.env.KGTTS_BOOTSTRAP_PYTHON) {
    return process.env.KGTTS_BOOTSTRAP_PYTHON;
  }
  const exeName = process.platform === 'win32' ? 'python.exe' : 'bin/python3';
  if (!isDev) {
    return path.join(process.resourcesPath, 'python_bootstrap', exeName);
  }
  const repoRoot = path.join(__dirname, '..', '..');
  const devBootstrap = path.join(__dirname, '..', 'build', 'python_bootstrap', exeName);
  if (fs.existsSync(devBootstrap)) {
    return devBootstrap;
  }
  const devRuntimePython = path.join(repoRoot, 'pc_trainer', 'piper_env', exeName);
  if (fs.existsSync(devRuntimePython)) {
    return devRuntimePython;
  }
  return process.platform === 'win32' ? 'python.exe' : 'python3';
}

function resolveResources() {
  const userResources = path.join(app.getPath('userData'), 'resources', 'resources_pack');
  if (fs.existsSync(userResources)) {
    return userResources;
  }
  if (!isDev) {
    return path.join(process.resourcesPath, 'resources_pack');
  }
  const repoRoot = path.join(__dirname, '..', '..');
  return path.join(repoRoot, 'pc_trainer', 'resources_pack');
}

function resolveBaseDir() {
  if (!isDev) {
    return process.resourcesPath;
  }
  const repoRoot = path.join(__dirname, '..', '..');
  return path.join(repoRoot, 'pc_trainer');
}

function sendBackendEvent(payload) {
  if (mainWindow && mainWindow.webContents && rendererReady) {
    mainWindow.webContents.send('backend:event', payload);
    return;
  }
  pendingBackendEvents.push(payload);
}

function flushBackendEvents() {
  if (!mainWindow || !mainWindow.webContents || !rendererReady) return;
  while (pendingBackendEvents.length) {
    mainWindow.webContents.send('backend:event', pendingBackendEvents.shift());
  }
}

function startBackend() {
  const pythonPath = resolvePython();
  let backendScript = path.join(__dirname, '..', 'backend', 'server.py');
  if (!isDev) {
    const unpacked = path.join(process.resourcesPath, 'app.asar.unpacked', 'backend', 'server.py');
    if (fs.existsSync(unpacked)) {
      backendScript = unpacked;
    }
  }
  const env = {
    ...process.env,
    PYTHONDONTWRITEBYTECODE: '1',
    PYTHONIOENCODING: 'utf-8',
    PYTHONUTF8: '1',
    KGTTS_RESOURCES: resolveResources(),
    KGTTS_BASE_DIR: resolveBaseDir(),
    KGTTS_APP_DIR: isDev ? path.join(__dirname, '..') : process.resourcesPath,
    KGTTS_USER_DATA: app.getPath('userData'),
  };

  if ((path.isAbsolute(pythonPath) || pythonPath.includes(path.sep)) && !fs.existsSync(pythonPath)) {
    sendBackendEvent({
      type: 'error',
      id: '',
      message: `启动组件缺失，请重新安装 KIGTTS Trainer。缺失文件：${pythonPath}`,
    });
    return;
  }

  backendProc = spawn(pythonPath, ['-u', backendScript], {
    stdio: ['pipe', 'pipe', 'pipe'],
    env,
  });

  backendProc.stdout.setEncoding('utf8');
  backendProc.stdout.on('data', (chunk) => {
    const lines = chunk.split(/\r?\n/).filter(Boolean);
    for (const line of lines) {
      try {
        const msg = JSON.parse(line);
        sendBackendEvent(msg);
      } catch (err) {
        sendBackendEvent({
          type: 'error',
          id: '',
          message: `后台服务返回了无法识别的内容，请重启软件后再试。${err}`,
          raw: line,
        });
      }
    }
  });

  backendProc.stderr.setEncoding('utf8');
  backendProc.stderr.on('data', (chunk) => {
    sendBackendEvent({
      type: 'log',
      id: '',
      message: chunk.toString(),
    });
  });

  backendProc.on('exit', (code) => {
    if (suppressBackendExit) {
      suppressBackendExit = false;
      return;
    }
    sendBackendEvent({
      type: 'error',
      id: '',
      message: `后台服务已停止，请重启软件后再试。（代码 ${code}）`,
    });
  });
}

function stopBackend() {
  return new Promise((resolve) => {
    if (!backendProc) {
      resolve();
      return;
    }
    const proc = backendProc;
    let done = false;
    suppressBackendExit = true;
    const finalize = () => {
      if (done) return;
      done = true;
      if (backendProc === proc) {
        backendProc = null;
      }
      resolve();
    };
    proc.once('exit', finalize);
    try {
      proc.kill();
    } catch (err) {
      finalize();
      return;
    }
    setTimeout(() => {
      if (done) return;
      try {
        proc.kill('SIGKILL');
      } catch (err) {
        // ignore
      }
      finalize();
    }, 2000);
  });
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 860,
    backgroundColor: '#0f1115',
    title: 'KIGTTS Trainer',
    icon: resolveWindowIcon(),
    frame: false,
    titleBarStyle: isMac ? 'hiddenInset' : undefined,
    trafficLightPosition: isMac ? { x: 12, y: 12 } : undefined,
    minWidth: 960,
    minHeight: 640,
    webPreferences: {
      preload: path.join(__dirname, 'preload.cjs'),
      contextIsolation: true,
    },
  });

  mainWindow.setMenu(null);

  if (isDev) {
    mainWindow.loadURL('http://localhost:5173');
    mainWindow.webContents.openDevTools({ mode: 'detach' });
  } else {
    mainWindow.loadFile(path.join(__dirname, '..', 'dist-renderer', 'index.html'));
  }

  mainWindow.webContents.on(
    'did-fail-load',
    (_event, errorCode, errorDescription, validatedURL, isMainFrame) => {
      console.error(
        `[renderer] did-fail-load code=${errorCode} mainFrame=${isMainFrame} url=${validatedURL} desc=${errorDescription}`,
      );
    },
  );

  mainWindow.webContents.on('console-message', (_event, level, message, line, sourceId) => {
    const lvl = ['verbose', 'info', 'warn', 'error'][level] || String(level);
    console.log(`[renderer:${lvl}] ${message} (${sourceId}:${line})`);
  });

  mainWindow.webContents.on('render-process-gone', (_event, details) => {
    console.error(`[renderer] process-gone reason=${details.reason} exitCode=${details.exitCode}`);
  });

  mainWindow.webContents.on('unresponsive', () => {
    console.error('[renderer] window unresponsive');
  });

  mainWindow.webContents.on('did-finish-load', () => {
    rendererReady = true;
    flushBackendEvents();
  });

  mainWindow.webContents.on('context-menu', (event, params) => {
    if (!mainWindow || !params?.isEditable) {
      return;
    }
    event.preventDefault();
    if (textContextMenuMode !== 'native') {
      return;
    }
    const editFlags = params.editFlags || {};
    const menu = Menu.buildFromTemplate([
      {
        label: '全选',
        role: 'selectAll',
        enabled: editFlags.canSelectAll !== false,
      },
      {
        label: '剪切',
        role: 'cut',
        enabled: !!editFlags.canCut,
      },
      {
        label: '复制',
        role: 'copy',
        enabled: !!editFlags.canCopy,
      },
      {
        label: '粘贴',
        role: 'paste',
        enabled: !!editFlags.canPaste,
      },
    ]);
    menu.popup({ window: mainWindow });
  });
}

function sanitizeSoundboardFileSegment(raw) {
  return String(raw || '')
    .trim()
    .replace(/[\\/:*?"<>|\s]+/g, '_')
    .slice(0, 48) || 'audio';
}

function resolve7zaPath() {
  const candidates = [];
  if (!isDev) {
    candidates.push(path.join(process.resourcesPath, 'tools', '7za.exe'));
  }
  candidates.push(path.join(__dirname, '..', 'node_modules', '7zip-bin', 'win', 'x64', '7za.exe'));
  candidates.push(path.join(__dirname, '..', '..', 'Electron_Trainer', 'node_modules', '7zip-bin', 'win', 'x64', '7za.exe'));
  for (const candidate of candidates) {
    if (fs.existsSync(candidate)) return candidate;
  }
  return '';
}

function resolveFfmpegPath() {
  const candidates = [];
  if (!isDev) {
    candidates.push(path.join(process.resourcesPath, 'tools', 'ffmpeg.exe'));
  }
  candidates.push(path.join(__dirname, '..', 'node_modules', 'ffmpeg-static', 'ffmpeg.exe'));
  candidates.push(path.join(__dirname, '..', '..', 'Electron_Trainer', 'node_modules', 'ffmpeg-static', 'ffmpeg.exe'));
  for (const candidate of candidates) {
    if (fs.existsSync(candidate)) return candidate;
  }
  return '';
}

function runProcess(exe, args, opts = {}) {
  return new Promise((resolve, reject) => {
    const child = spawn(exe, args, {
      ...opts,
      windowsHide: true,
      stdio: ['ignore', 'pipe', 'pipe'],
    });
    let stdout = '';
    let stderr = '';
    child.stdout?.setEncoding('utf8');
    child.stderr?.setEncoding('utf8');
    child.stdout?.on('data', (chunk) => {
      stdout += chunk;
    });
    child.stderr?.on('data', (chunk) => {
      stderr += chunk;
    });
    child.on('error', reject);
    child.on('exit', (code) => {
      if (code === 0) {
        resolve({ stdout, stderr });
        return;
      }
      reject(new Error((stderr || stdout || `${exe} exited with code ${code}`).trim()));
    });
  });
}

function formatFfmpegSeconds(ms) {
  const seconds = Math.max(0, Number(ms) || 0) / 1000;
  return seconds.toFixed(3).replace(/\.?0+$/, '') || '0';
}

async function transcodeSoundboardItemToAac(ffmpegPath, item, outputPath) {
  const startMs = Math.max(0, Number(item.trimStartMs) || 0);
  const fallbackEndMs = Math.max(0, Number(item.durationMs) || 0);
  const rawEndMs = Math.max(0, Number(item.trimEndMs) || 0);
  const endMs = rawEndMs || fallbackEndMs;
  const durationMs = endMs > startMs ? endMs - startMs : 0;
  const args = ['-y', '-hide_banner', '-loglevel', 'error', '-i', item.audioPath];
  if (startMs > 0) {
    args.push('-ss', formatFfmpegSeconds(startMs));
  }
  if (durationMs > 0) {
    args.push('-t', formatFfmpegSeconds(durationMs));
  }
  args.push('-vn', '-map_metadata', '-1', '-c:a', 'aac', '-b:a', '160k', '-f', 'adts', outputPath);
  await runProcess(ffmpegPath, args);
}

function normalizeSoundboardExportConfig(config) {
  const groups = Array.isArray(config?.groups) ? config.groups : [];
  return {
    selectedGroupId: Number(config?.selectedGroupId) || Number(groups[0]?.id) || 1,
    portraitLayout: String(config?.portraitLayout || 'list'),
    landscapeLayout: String(config?.landscapeLayout || 'grid_5'),
    groups: groups.map((group, groupIndex) => ({
      id: Number(group?.id) || groupIndex + 1,
      title: String(group?.title || '').trim() || '未命名分组',
      icon: String(group?.icon || '').trim() || 'music_note',
      keywordWakeEnabled: group?.keywordWakeEnabled !== false,
      items: (Array.isArray(group?.items) ? group.items : []).map((item, itemIndex) => ({
        id: Number(item?.id) || itemIndex + 1,
        title: String(item?.title || '').trim() || '新音效',
        wakeWord: String(item?.wakeWord || '').trim(),
        audioPath: String(item?.audioPath || '').trim(),
        durationMs: Math.max(0, Number(item?.durationMs) || 0),
        trimStartMs: Math.max(0, Number(item?.trimStartMs) || 0),
        trimEndMs: Math.max(0, Number(item?.trimEndMs) || 0),
      })),
    })),
  };
}

async function exportSoundboardPackage(config, outputPath) {
  const sevenZip = resolve7zaPath();
  if (!sevenZip) {
    return { ok: false, message: '缺少 7z 打包组件，请重新安装 KIGTTS Trainer。' };
  }
  const ffmpegPath = resolveFfmpegPath();
  if (!ffmpegPath) {
    return { ok: false, message: '缺少音频转换组件，请重新安装 KIGTTS Trainer。' };
  }
  const normalized = normalizeSoundboardExportConfig(config);
  if (!normalized.groups.some((group) => group.items.length > 0)) {
    return { ok: false, message: '音效包里还没有音效。' };
  }
  let outPath = String(outputPath || '').trim();
  if (!outPath) {
    return { ok: false, message: '未选择导出路径。' };
  }
  if (!outPath.toLowerCase().endsWith('.kigspk')) {
    outPath += '.kigspk';
  }
  const tempRoot = fs.mkdtempSync(path.join(app.getPath('temp'), 'kigtts-soundboard-export-'));
  try {
    const audioDir = path.join(tempRoot, 'audio');
    fs.mkdirSync(audioDir, { recursive: true });
    const usedEntries = new Set();
    const root = {
      type: 'soundboard',
      version: 1,
      selectedGroupId: normalized.groups[0]?.id || normalized.selectedGroupId,
      portraitLayout: normalized.portraitLayout,
      landscapeLayout: normalized.landscapeLayout,
      groups: normalized.groups.map((group) => ({
        id: group.id,
        title: group.title,
        icon: group.icon,
        keywordWakeEnabled: group.keywordWakeEnabled,
        items: group.items.map((item) => {
          let audioFile = '';
          if (item.audioPath && fs.existsSync(item.audioPath)) {
            let entry = `audio/${sanitizeSoundboardFileSegment(item.title)}_${item.id}.aac`;
            let suffix = 2;
            while (usedEntries.has(entry)) {
              entry = `audio/${sanitizeSoundboardFileSegment(item.title)}_${item.id}_${suffix}.aac`;
              suffix += 1;
            }
            usedEntries.add(entry);
            audioFile = entry.replace(/\\/g, '/');
          }
          const exportedDurationMs =
            item.audioPath && fs.existsSync(item.audioPath)
              ? Math.max(0, (item.trimEndMs || item.durationMs || 0) - Math.max(0, item.trimStartMs || 0)) || item.durationMs || 0
              : item.durationMs;
          return {
            id: item.id,
            title: item.title,
            wakeWord: item.wakeWord,
            durationMs: exportedDurationMs,
            trimStartMs: 0,
            trimEndMs: exportedDurationMs,
            audioFile,
          };
        }),
      })),
    };
    for (const group of normalized.groups) {
      for (const item of group.items) {
        if (!item.audioPath || !fs.existsSync(item.audioPath)) continue;
        let entry = root.groups
          .find((rootGroup) => rootGroup.id === group.id)
          ?.items.find((rootItem) => rootItem.id === item.id)?.audioFile;
        if (!entry) continue;
        entry = entry.replace(/\//g, path.sep);
        try {
          await transcodeSoundboardItemToAac(ffmpegPath, item, path.join(tempRoot, entry));
        } catch (err) {
          throw new Error(`音效“${item.title}”转换 AAC 失败：${String(err?.message || err)}`);
        }
      }
    }
    fs.writeFileSync(path.join(tempRoot, 'preset.json'), JSON.stringify(root, null, 2), 'utf8');
    fs.mkdirSync(path.dirname(outPath), { recursive: true });
    if (fs.existsSync(outPath)) fs.rmSync(outPath, { force: true });
    await runProcess(sevenZip, ['a', '-tzip', outPath, '.\\*', '-mx=5'], { cwd: tempRoot });
    return { ok: true, path: outPath };
  } catch (err) {
    return { ok: false, message: String(err?.message || err) };
  } finally {
    fs.rmSync(tempRoot, { recursive: true, force: true });
  }
}

async function importSoundboardPackage(packagePath) {
  const sevenZip = resolve7zaPath();
  if (!sevenZip) {
    return { ok: false, message: '缺少 7z 解包组件，请重新安装 KIGTTS Trainer。' };
  }
  const source = String(packagePath || '').trim();
  if (!source || !fs.existsSync(source)) {
    return { ok: false, message: '音效包文件不存在。' };
  }
  const extractDir = fs.mkdtempSync(path.join(app.getPath('temp'), 'kigtts-soundboard-import-'));
  const importRoot = path.join(app.getPath('userData'), 'soundboard_editor', 'imports', String(Date.now()));
  try {
    await runProcess(sevenZip, ['x', source, `-o${extractDir}`, '-y']);
    const presetPath = path.join(extractDir, 'preset.json');
    if (!fs.existsSync(presetPath)) {
      return { ok: false, message: '音效包缺少 preset.json。' };
    }
    const root = JSON.parse(fs.readFileSync(presetPath, 'utf8'));
    if (root?.type !== 'soundboard') {
      return { ok: false, message: '这不是 KIGTTS 音效包。' };
    }
    const audioOutDir = path.join(importRoot, 'audio');
    fs.mkdirSync(audioOutDir, { recursive: true });
    const groups = (Array.isArray(root.groups) ? root.groups : []).map((group, groupIndex) => ({
      id: Number(group?.id) || groupIndex + 1,
      title: String(group?.title || '').trim() || '未命名分组',
      icon: String(group?.icon || '').trim() || 'music_note',
      keywordWakeEnabled: group?.keywordWakeEnabled !== false,
      items: (Array.isArray(group?.items) ? group.items : []).map((item, itemIndex) => {
        const audioEntry = String(item?.audioFile || '').replace(/\\/g, '/').replace(/^\/+/, '');
        let audioPath = '';
        if (audioEntry && !audioEntry.includes('../')) {
          const extractedAudio = path.join(extractDir, audioEntry);
          if (fs.existsSync(extractedAudio)) {
            const targetName = `${sanitizeSoundboardFileSegment(item?.title || 'audio')}_${Date.now()}_${itemIndex}${path.extname(extractedAudio) || '.audio'}`;
            audioPath = path.join(audioOutDir, targetName);
            fs.copyFileSync(extractedAudio, audioPath);
          }
        }
        return {
          id: Number(item?.id) || itemIndex + 1,
          title: String(item?.title || '').trim() || '新音效',
          wakeWord: String(item?.wakeWord || '').trim(),
          audioPath,
          durationMs: Math.max(0, Number(item?.durationMs) || 0),
          trimStartMs: Math.max(0, Number(item?.trimStartMs) || 0),
          trimEndMs: Math.max(0, Number(item?.trimEndMs) || Number(item?.durationMs) || 0),
        };
      }),
    }));
    if (!groups.length) {
      return { ok: false, message: '音效包里没有可导入分组。' };
    }
    return {
      ok: true,
      message: `已导入 ${groups.length} 个分组。`,
      config: {
        selectedGroupId: Number(root.selectedGroupId) || groups[0].id,
        portraitLayout: String(root.portraitLayout || 'list'),
        landscapeLayout: String(root.landscapeLayout || 'grid_5'),
        groups,
      },
    };
  } catch (err) {
    fs.rmSync(importRoot, { recursive: true, force: true });
    return { ok: false, message: String(err?.message || err) };
  } finally {
    fs.rmSync(extractDir, { recursive: true, force: true });
  }
}

app.whenReady().then(() => {
  createWindow();
  startBackend();

  const sendWindowState = () => {
    if (mainWindow && mainWindow.webContents) {
      mainWindow.webContents.send('window:state', {
        maximized: mainWindow.isMaximized(),
      });
    }
  };

  if (mainWindow) {
    mainWindow.on('maximize', sendWindowState);
    mainWindow.on('unmaximize', sendWindowState);
    mainWindow.on('restore', sendWindowState);
  }

  ipcMain.on('backend:send', (_, msg) => {
    if (
      !backendProc ||
      backendProc.killed ||
      backendProc.exitCode !== null ||
      !backendProc.stdin ||
      backendProc.stdin.destroyed ||
      !backendProc.stdin.writable
    ) {
      return;
    }
    try {
      backendProc.stdin.write(`${JSON.stringify(msg)}\n`);
    } catch (err) {
      sendBackendEvent({
        type: 'error',
        id: '',
        message: `无法向后台服务发送任务，请重启软件后再试。${String(err)}`,
      });
    }
  });

  ipcMain.on('backend:restart', () => {
    sendBackendEvent({ type: 'log', id: '', message: '[SYS] 后台服务重启中...' });
    stopBackend().then(() => {
      startBackend();
      sendBackendEvent({ type: 'log', id: '', message: '[SYS] 后台服务已重启' });
    });
  });

  ipcMain.on('context-menu:set-mode', (_, mode) => {
    if (mode === 'native' || mode === 'custom') {
      textContextMenuMode = mode;
    }
  });

  ipcMain.on('window:minimize', () => {
    if (mainWindow) {
      mainWindow.minimize();
    }
  });
  ipcMain.on('window:toggleMaximize', () => {
    if (!mainWindow) return;
    if (mainWindow.isMaximized()) {
      mainWindow.unmaximize();
    } else {
      mainWindow.maximize();
    }
    sendWindowState();
  });
  ipcMain.on('window:close', () => {
    if (mainWindow) {
      mainWindow.close();
    }
  });
  ipcMain.handle('window:isMaximized', () => {
    return mainWindow ? mainWindow.isMaximized() : false;
  });

  ipcMain.handle('dialog:openFiles', async (_, opts = {}) => {
    const result = await dialog.showOpenDialog(mainWindow, {
      title: opts.title || undefined,
      properties: opts.properties || ['openFile', 'multiSelections'],
      filters:
        opts.filters || [
          { name: 'Audio', extensions: ['wav', 'mp3', 'm4a', 'flac'] },
          { name: 'All', extensions: ['*'] },
        ],
    });
    return result.canceled ? [] : result.filePaths;
  });

  ipcMain.handle('dialog:openDir', async () => {
    const result = await dialog.showOpenDialog(mainWindow, {
      properties: ['openDirectory'],
    });
    return result.canceled ? '' : result.filePaths[0];
  });

  ipcMain.handle('dialog:openFile', async (_, opts = {}) => {
    const result = await dialog.showOpenDialog(mainWindow, {
      properties: ['openFile'],
      filters: opts.filters || [{ name: 'All', extensions: ['*'] }],
    });
    return result.canceled ? '' : result.filePaths[0];
  });

  ipcMain.handle('dialog:saveFile', async (_, opts = {}) => {
    const result = await dialog.showSaveDialog(mainWindow, {
      title: opts.title || '保存文件',
      defaultPath: opts.defaultPath || '',
      filters: opts.filters || [{ name: 'All', extensions: ['*'] }],
    });
    return result.canceled ? '' : (result.filePath || '');
  });

  ipcMain.handle('path:dirname', (_, filePath) => {
    if (!filePath) return '';
    return path.dirname(filePath);
  });

  ipcMain.handle('path:openInExplorer', async (_, targetPath) => {
    try {
      if (!targetPath || typeof targetPath !== 'string') {
        return { ok: false, message: '路径为空' };
      }
      const resolved = path.resolve(targetPath);
      if (!fs.existsSync(resolved)) {
        return { ok: false, message: '路径不存在', target: resolved };
      }
      const stat = fs.statSync(resolved);
      if (stat.isDirectory()) {
        const errMsg = await shell.openPath(resolved);
        if (errMsg) {
          return { ok: false, message: errMsg, target: resolved };
        }
        return { ok: true, target: resolved };
      }
      shell.showItemInFolder(resolved);
      return { ok: true, target: resolved };
    } catch (err) {
      return { ok: false, message: String(err) };
    }
  });

  ipcMain.handle('path:openExternal', async (_, targetUrl) => {
    try {
      if (!targetUrl || typeof targetUrl !== 'string') {
        return { ok: false, message: '链接为空' };
      }
      await shell.openExternal(targetUrl);
      return { ok: true, target: targetUrl };
    } catch (err) {
      return { ok: false, message: String(err) };
    }
  });

  ipcMain.handle('project:openOutputDir', async (_, outputDir) => {
    try {
      if (!outputDir || typeof outputDir !== 'string') {
        return { ok: false, message: '输出目录为空' };
      }
      const projectRoot = path.resolve(outputDir);
      const exportDir = path.join(projectRoot, 'export');
      let target = projectRoot;
      if (
        fs.existsSync(path.join(exportDir, 'voicepack.kigvpk')) ||
        fs.existsSync(path.join(exportDir, 'voicepack.zip')) ||
        fs.existsSync(exportDir)
      ) {
        target = exportDir;
      }
      if (!fs.existsSync(target)) {
        return { ok: false, message: '目录不存在', target };
      }
      const errMsg = await shell.openPath(target);
      if (errMsg) {
        return { ok: false, message: errMsg, target };
      }
      return { ok: true, target };
    } catch (err) {
      return { ok: false, message: String(err) };
    }
  });

  ipcMain.handle('project:clearWorkCache', async (_, outputDir) => {
    try {
      if (!outputDir || typeof outputDir !== 'string') {
        return { ok: false, message: '输出目录为空' };
      }
      const projectRoot = path.resolve(outputDir);
      if (!fs.existsSync(projectRoot)) {
        return { ok: false, message: '输出目录不存在', path: projectRoot };
      }
      const workDir = path.join(projectRoot, 'work');
      if (!fs.existsSync(workDir)) {
        return { ok: true, cleared: false, path: workDir };
      }
      fs.rmSync(workDir, { recursive: true, force: true });
      fs.mkdirSync(workDir, { recursive: true });
      return { ok: true, cleared: true, path: workDir };
    } catch (err) {
      return { ok: false, message: String(err) };
    }
  });

  ipcMain.handle('project:ensureDefaultOutputDir', async () => {
    try {
      const docsDir = app.getPath('documents');
      const root = path.join(docsDir, 'TTSPACK');
      const now = new Date();
      const pad2 = (n) => String(n).padStart(2, '0');
      const stamp =
        `${now.getFullYear()}${pad2(now.getMonth() + 1)}${pad2(now.getDate())}-` +
        `${pad2(now.getHours())}${pad2(now.getMinutes())}${pad2(now.getSeconds())}`;
      const target = path.join(root, stamp);
      fs.mkdirSync(target, { recursive: true });
      return { ok: true, path: target };
    } catch (err) {
      return { ok: false, message: String(err) };
    }
  });

  ipcMain.handle('fs:saveImage', async (_, payload) => {
    try {
      const dataUrl = payload?.dataUrl;
      if (!dataUrl || typeof dataUrl !== 'string') {
        return '';
      }
      const matches = dataUrl.match(/^data:(image\/[a-zA-Z0-9.+-]+);base64,(.*)$/);
      if (!matches) return '';
      const mime = matches[1];
      const base64 = matches[2];
      const ext = mime.includes('png') ? 'png' : mime.includes('jpeg') ? 'jpg' : 'png';
      const outDir = path.join(app.getPath('userData'), 'avatars');
      fs.mkdirSync(outDir, { recursive: true });
      const filename = `avatar-${Date.now()}.${ext}`;
      const outPath = path.join(outDir, filename);
      fs.writeFileSync(outPath, Buffer.from(base64, 'base64'));
      return outPath;
    } catch (err) {
      return '';
    }
  });

  ipcMain.handle('fs:readImage', async (_, filePath) => {
    try {
      if (!filePath || typeof filePath !== 'string') {
        return '';
      }
      if (!fs.existsSync(filePath)) {
        return '';
      }
      const ext = path.extname(filePath).toLowerCase();
      const mime =
        ext === '.jpg' || ext === '.jpeg'
          ? 'image/jpeg'
          : ext === '.webp'
            ? 'image/webp'
            : ext === '.bmp'
              ? 'image/bmp'
              : 'image/png';
      const data = fs.readFileSync(filePath);
      return `data:${mime};base64,${data.toString('base64')}`;
    } catch (err) {
      return '';
    }
  });

  ipcMain.handle('fs:saveDroppedFile', async (_, payload) => {
    try {
      const name = payload?.name;
      const data = payload?.data;
      if (!name || typeof name !== 'string' || !data) {
        return '';
      }
      let buffer = null;
      if (data instanceof ArrayBuffer) {
        buffer = Buffer.from(new Uint8Array(data));
      } else if (ArrayBuffer.isView(data)) {
        buffer = Buffer.from(data.buffer);
      } else if (Buffer.isBuffer(data)) {
        buffer = data;
      }
      if (!buffer) {
        return '';
      }
      const safeName = name.replace(/[^\w.\-]+/g, '_');
      const outDir = path.join(app.getPath('userData'), 'drops');
      fs.mkdirSync(outDir, { recursive: true });
      const filename = `${Date.now()}-${safeName}`;
      const outPath = path.join(outDir, filename);
      fs.writeFileSync(outPath, buffer);
      return outPath;
    } catch (err) {
      return '';
    }
  });

  ipcMain.handle('fs:copyFile', async (_, payload) => {
    try {
      const src = payload?.src;
      const dst = payload?.dst;
      if (!src || !dst || typeof src !== 'string' || typeof dst !== 'string') {
        return false;
      }
      if (!fs.existsSync(src)) {
        return false;
      }
      fs.mkdirSync(path.dirname(dst), { recursive: true });
      fs.copyFileSync(src, dst);
      return true;
    } catch (err) {
      return false;
    }
  });

  ipcMain.handle('fs:writeTextFile', async (_, payload) => {
    try {
      const filePath = payload?.path;
      const text = payload?.text;
      if (!filePath || typeof filePath !== 'string') {
        return false;
      }
      if (text !== undefined && typeof text !== 'string') {
        return false;
      }
      fs.mkdirSync(path.dirname(filePath), { recursive: true });
      fs.writeFileSync(filePath, text || '', 'utf8');
      return true;
    } catch (err) {
      return false;
    }
  });

  ipcMain.handle('fs:ensureTextPresetFile', async (_, payload) => {
    try {
      const inputName = payload?.name;
      const text = payload?.text;
      if (!inputName || typeof inputName !== 'string') {
        return '';
      }
      if (text !== undefined && typeof text !== 'string') {
        return '';
      }
      const safeName = path
        .basename(inputName)
        .replace(/[<>:"/\\|?*\x00-\x1F]/g, '_')
        .trim() || `preset-${Date.now()}.txt`;
      const outDir = path.join(app.getPath('userData'), 'text_presets');
      fs.mkdirSync(outDir, { recursive: true });
      const outPath = path.join(outDir, safeName);
      fs.writeFileSync(outPath, text || '', 'utf8');
      return outPath;
    } catch (err) {
      return '';
    }
  });

  ipcMain.handle('soundboard:exportPackage', async (_, payload = {}) => {
    return exportSoundboardPackage(payload.config, payload.outputPath);
  });

  ipcMain.handle('soundboard:importPackage', async (_, packagePath) => {
    return importSoundboardPackage(packagePath);
  });

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});
