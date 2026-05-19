export type SoundboardEditorItem = {
  id: number
  title: string
  wakeWord: string
  audioPath: string
  durationMs: number
  trimStartMs: number
  trimEndMs: number
}

export type SoundboardEditorGroup = {
  id: number
  title: string
  icon: string
  keywordWakeEnabled: boolean
  items: SoundboardEditorItem[]
}

export type SoundboardEditorConfig = {
  selectedGroupId: number
  groups: SoundboardEditorGroup[]
}

export type SoundboardBatchEditState = {
  titlePrefix: string
  titleSuffix: string
  wakeWord: string
  replaceWakeWord: boolean
  targetGroupId: string
}

export type SoundboardItemDragState = {
  itemId: number
  pointerId: number
  originIndex: number
  targetIndex: number
  startY: number
  pointerOffsetY: number
  rowHeight: number
  rowGap: number
  rowCenters: number[]
  startScrollTop: number
}

export type SoundboardPreviewPlaybackState = {
  playing: boolean
  currentTime: number
  duration: number
  requestId: number
}

export const SOUNDBOARD_EDITOR_STORAGE_KEY = 'kigtts_soundboard_editor_config'

export const SOUNDBOARD_AUDIO_EXTENSIONS = ['wav', 'mp3', 'm4a', 'aac', 'flac', 'ogg', 'opus']

export const SOUNDBOARD_GROUP_ICONS = [
  'music_note',
  'campaign',
  'mood',
  'celebration',
  'pets',
  'favorite',
  'stars',
  'sports_esports',
  'notifications',
  'record_voice_over',
  'theater_comedy',
  'bolt',
]

export const createSoundboardId = () => Date.now() + Math.floor(Math.random() * 100000)

export const defaultSoundboardEditorConfig = (): SoundboardEditorConfig => ({
  selectedGroupId: 1,
  groups: [
    {
      id: 1,
      title: '常用音效',
      icon: 'music_note',
      keywordWakeEnabled: true,
      items: [],
    },
  ],
})

export const normalizeSoundboardEditorConfig = (input: unknown): SoundboardEditorConfig => {
  const fallback = defaultSoundboardEditorConfig()
  if (!input || typeof input !== 'object') return fallback
  const root = input as Partial<SoundboardEditorConfig>
  const groupsRaw = Array.isArray(root.groups) ? root.groups : []
  const groups = groupsRaw
    .map((groupRaw, groupIndex) => {
      const group = groupRaw as Partial<SoundboardEditorGroup>
      const itemsRaw = Array.isArray(group.items) ? group.items : []
      const items = itemsRaw.map((itemRaw, itemIndex) => {
        const item = itemRaw as Partial<SoundboardEditorItem>
        const duration = Number(item.durationMs) || 0
        return {
          id: Number(item.id) || createSoundboardId() + itemIndex,
          title: String(item.title || '').trim() || '新音效',
          wakeWord: String(item.wakeWord || '').trim(),
          audioPath: String(item.audioPath || '').trim(),
          durationMs: Math.max(0, duration),
          trimStartMs: Math.max(0, Number(item.trimStartMs) || 0),
          trimEndMs: Math.max(0, Number(item.trimEndMs) || duration || 0),
        }
      })
      return {
        id: Number(group.id) || groupIndex + 1,
        title: String(group.title || '').trim() || '未命名分组',
        icon: String(group.icon || '').trim() || 'music_note',
        keywordWakeEnabled: group.keywordWakeEnabled !== false,
        items,
      }
    })
    .filter((group) => group.title || group.items.length)
  const safeGroups = groups.length ? groups : fallback.groups
  const selectedGroupId = Number(root.selectedGroupId)
  return {
    selectedGroupId: safeGroups.some((group) => group.id === selectedGroupId) ? selectedGroupId : safeGroups[0].id,
    groups: safeGroups,
  }
}

export const readSavedSoundboardEditorConfig = (): SoundboardEditorConfig => {
  try {
    const raw = window.localStorage.getItem(SOUNDBOARD_EDITOR_STORAGE_KEY)
    if (!raw) return defaultSoundboardEditorConfig()
    return normalizeSoundboardEditorConfig(JSON.parse(raw))
  } catch {
    return defaultSoundboardEditorConfig()
  }
}

export const soundboardFileTitle = (filePath: string) => {
  const name = filePath.split(/[\\/]/).pop() || '新音效'
  return name.replace(/\.[^.]+$/, '').trim() || '新音效'
}

export const isSoundboardAudioPath = (filePath: string) => {
  const ext = filePath.split('.').pop()?.toLowerCase() || ''
  return SOUNDBOARD_AUDIO_EXTENSIONS.includes(ext)
}

export const formatDurationMsForSoundboard = (ms: number) => {
  const safe = Math.max(0, Math.floor(ms || 0))
  const totalSeconds = Math.floor(safe / 1000)
  const minutes = Math.floor(totalSeconds / 60)
  const seconds = totalSeconds % 60
  const fraction = Math.floor((safe % 1000) / 100)
  return `${minutes}:${String(seconds).padStart(2, '0')}.${fraction}`
}
