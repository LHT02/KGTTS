const electron = require('electron');
const fs = require('fs');
const path = require('path');
const { spawn } = require('child_process');

const app =
  electron.app ||
  {
    isPackaged: false,
    getPath(name) {
      if (name === 'temp') return require('os').tmpdir();
      return process.cwd();
    },
  };
const isDev = !app.isPackaged;

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
  const durationMs = effectiveSoundboardItemDurationMs(item);
  const args = ['-y', '-hide_banner', '-loglevel', 'error'];
  if (startMs > 0) {
    args.push('-ss', formatFfmpegSeconds(startMs));
  }
  args.push('-i', item.audioPath);
  if (durationMs > 0) {
    args.push('-t', formatFfmpegSeconds(durationMs));
  }
  args.push('-vn', '-map_metadata', '-1', '-c:a', 'aac', '-b:a', '160k', '-f', 'adts', outputPath);
  await runProcess(ffmpegPath, args);
}

function nextUniqueNumericId(rawId, fallbackId, usedIds) {
  let id = Number(rawId) || fallbackId;
  while (usedIds.has(id)) {
    id += 1;
  }
  usedIds.add(id);
  return id;
}

function normalizeSoundboardExportConfig(config) {
  const usedGroupIds = new Set();
  const usedItemIds = new Set();
  const groups = Array.isArray(config?.groups) ? config.groups : [];
  const normalizedGroups = groups.map((group, groupIndex) => ({
    id: nextUniqueNumericId(group?.id, groupIndex + 1, usedGroupIds),
    title: String(group?.title || '').trim() || '未命名分组',
    icon: String(group?.icon || '').trim() || 'music_note',
    keywordWakeEnabled: group?.keywordWakeEnabled !== false,
    items: (Array.isArray(group?.items) ? group.items : []).map((item, itemIndex) => ({
      id: nextUniqueNumericId(item?.id, itemIndex + 1, usedItemIds),
      title: String(item?.title || '').trim() || '新音效',
      wakeWord: String(item?.wakeWord || '').trim(),
      audioPath: String(item?.audioPath || '').trim(),
      durationMs: Math.max(0, Number(item?.durationMs) || 0),
      trimStartMs: Math.max(0, Number(item?.trimStartMs) || 0),
      trimEndMs: Math.max(0, Number(item?.trimEndMs) || 0),
    })),
  }));
  const selectedGroupId = Number(config?.selectedGroupId);
  return {
    selectedGroupId: normalizedGroups.some((group) => group.id === selectedGroupId)
      ? selectedGroupId
      : Number(normalizedGroups[0]?.id) || 1,
    groups: normalizedGroups,
  };
}

function effectiveSoundboardItemDurationMs(item) {
  const startMs = Math.max(0, Number(item.trimStartMs) || 0);
  const fallbackEndMs = Math.max(0, Number(item.durationMs) || 0);
  const rawEndMs = Math.max(0, Number(item.trimEndMs) || 0);
  const endMs = rawEndMs || fallbackEndMs;
  return endMs > startMs ? endMs - startMs : 0;
}

function validateSoundboardExportConfig(config) {
  const missingAudio = [];
  const invalidTrim = [];
  for (const group of config.groups) {
    for (const item of group.items) {
      if (!item.audioPath || !fs.existsSync(item.audioPath)) {
        missingAudio.push(`${group.title} / ${item.title}`);
        continue;
      }
      if (effectiveSoundboardItemDurationMs(item) <= 0) {
        invalidTrim.push(`${group.title} / ${item.title}`);
      }
    }
  }
  if (missingAudio.length) {
    throw new Error(`以下音效缺少可用音频，无法导出：${missingAudio.slice(0, 5).join('、')}${missingAudio.length > 5 ? ' 等' : ''}`);
  }
  if (invalidTrim.length) {
    throw new Error(`以下音效的裁剪区间无效，结束时间必须大于起始时间：${invalidTrim.slice(0, 5).join('、')}${invalidTrim.length > 5 ? ' 等' : ''}`);
  }
}

function safeExtractedEntryPath(extractDir, audioEntry) {
  const normalizedEntry = String(audioEntry || '').replace(/\\/g, '/').replace(/^\/+/, '');
  if (!normalizedEntry || normalizedEntry.includes('\0')) return '';
  const target = path.resolve(extractDir, normalizedEntry);
  const root = path.resolve(extractDir);
  if (target !== root && !target.startsWith(`${root}${path.sep}`)) return '';
  return target;
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
  try {
    validateSoundboardExportConfig(normalized);
  } catch (err) {
    return { ok: false, message: String(err?.message || err) };
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
      selectedGroupId: normalized.selectedGroupId,
      groups: normalized.groups.map((group) => ({
        id: group.id,
        title: group.title,
        icon: group.icon,
        keywordWakeEnabled: group.keywordWakeEnabled,
        items: group.items.map((item) => {
          let entry = `audio/${sanitizeSoundboardFileSegment(item.title)}_${item.id}.aac`;
          let suffix = 2;
          while (usedEntries.has(entry)) {
            entry = `audio/${sanitizeSoundboardFileSegment(item.title)}_${item.id}_${suffix}.aac`;
            suffix += 1;
          }
          usedEntries.add(entry);
          const exportedDurationMs = effectiveSoundboardItemDurationMs(item);
          return {
            id: item.id,
            title: item.title,
            wakeWord: item.wakeWord,
            durationMs: exportedDurationMs,
            trimStartMs: 0,
            trimEndMs: exportedDurationMs,
            audioFile: entry.replace(/\\/g, '/'),
          };
        }),
      })),
    };
    const entryByItemId = new Map();
    for (const group of root.groups) {
      for (const item of group.items) {
        entryByItemId.set(item.id, item.audioFile);
      }
    }
    for (const group of normalized.groups) {
      for (const item of group.items) {
        const entry = String(entryByItemId.get(item.id) || '').replace(/\//g, path.sep);
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
        const extractedAudio = safeExtractedEntryPath(extractDir, item?.audioFile);
        let audioPath = '';
        if (extractedAudio && fs.existsSync(extractedAudio)) {
          const targetName = `${sanitizeSoundboardFileSegment(item?.title || 'audio')}_${Date.now()}_${itemIndex}${path.extname(extractedAudio) || '.audio'}`;
          audioPath = path.join(audioOutDir, targetName);
          fs.copyFileSync(extractedAudio, audioPath);
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

module.exports = {
  exportSoundboardPackage,
  importSoundboardPackage,
};
