import { Box, Button, IconButton, LinearProgress, Paper, Slider, Stack, TextField, Tooltip, Typography } from '@mui/material'
import type { SliderProps, SxProps, Theme } from '@mui/material'
import type { ComponentType } from 'react'
import { MsIcon } from '../components/MsIcon'

type PathFieldLikeProps = {
  label: string
  value: string
  onChange: (value: string) => void
  onPick: () => void
  onDropPath?: (value: string) => void
  onDropFiles?: (files: File[]) => Promise<string>
  helperText?: string
  placeholder?: string
}

export type VoicePreviewPageProps = {
  cardPaperSx: SxProps<Theme>
  PathFieldComponent: ComponentType<PathFieldLikeProps>
  previewVoicepack: string
  previewText: string
  previewBusy: boolean
  pipelineRunning: boolean
  previewAudioPath: string
  hasPreviewAudio: boolean
  previewPlaying: boolean
  previewCurrentTime: number
  previewDuration: number
  onPreviewVoicepackChange: (value: string) => void
  onPreviewTextChange: (value: string) => void
  onPickPreviewVoicepack: () => void
  onDropFiles: (files: File[]) => Promise<string>
  onStartPreview: () => void
  onExportPreviewAudio: () => void
  onOpenCurrentVoicepackDirectory: () => void
  onTogglePreviewPlay: () => void
  onStopPreviewPlay: () => void
  onSeekPreviewPlay: NonNullable<SliderProps['onChange']>
  formatTime: (value: number) => string
}

export function VoicePreviewPage({
  cardPaperSx,
  PathFieldComponent,
  previewVoicepack,
  previewText,
  previewBusy,
  pipelineRunning,
  previewAudioPath,
  hasPreviewAudio,
  previewPlaying,
  previewCurrentTime,
  previewDuration,
  onPreviewVoicepackChange,
  onPreviewTextChange,
  onPickPreviewVoicepack,
  onDropFiles,
  onStartPreview,
  onExportPreviewAudio,
  onOpenCurrentVoicepackDirectory,
  onTogglePreviewPlay,
  onStopPreviewPlay,
  onSeekPreviewPlay,
  formatTime,
}: VoicePreviewPageProps) {
  return (
    <Stack spacing={2}>
      <Paper sx={cardPaperSx}>
        <Typography variant="subtitle1" fontWeight={600} gutterBottom>
          语音包试听
        </Typography>
        <Stack spacing={2}>
          <PathFieldComponent
            label="语音包文件或目录"
            value={previewVoicepack}
            onChange={onPreviewVoicepackChange}
            onPick={onPickPreviewVoicepack}
            onDropPath={onPreviewVoicepackChange}
            onDropFiles={onDropFiles}
          />
          <TextField
            label="试听文本"
            value={previewText}
            onChange={(event) => onPreviewTextChange(event.target.value)}
            multiline
            minRows={3}
            fullWidth
          />
          <Stack direction="row" spacing={1}>
            <Button
              variant="contained"
              startIcon={<MsIcon name="play_arrow" size={18} />}
              onClick={onStartPreview}
              disabled={previewBusy || pipelineRunning}
            >
              {previewBusy ? '生成中...' : '生成试听'}
            </Button>
            <Button
              variant="outlined"
              startIcon={<MsIcon name="download" size={18} />}
              onClick={onExportPreviewAudio}
              disabled={!previewAudioPath}
            >
              导出音频
            </Button>
            <Button
              variant="outlined"
              startIcon={<MsIcon name="folder_open" size={18} />}
              onClick={onOpenCurrentVoicepackDirectory}
              disabled={!previewVoicepack.trim()}
            >
              打开当前语音包目录
            </Button>
          </Stack>
          {previewBusy && <LinearProgress />}
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Typography variant="subtitle1" fontWeight={600}>
          播放器
        </Typography>
        <Stack spacing={1} sx={{ mt: 1 }}>
          <Stack direction="row" spacing={1} alignItems="center">
            <Tooltip title={previewPlaying ? '暂停' : '播放'} arrow>
              <span>
                <IconButton color="primary" onClick={onTogglePreviewPlay} disabled={!hasPreviewAudio}>
                  <MsIcon name={previewPlaying ? 'pause' : 'play_arrow'} size={20} fill={1} />
                </IconButton>
              </span>
            </Tooltip>
            <Tooltip title="停止" arrow>
              <span>
                <IconButton onClick={onStopPreviewPlay} disabled={!hasPreviewAudio}>
                  <MsIcon name="stop" size={20} fill={1} />
                </IconButton>
              </span>
            </Tooltip>
            <Box sx={{ flex: 1, px: 1 }}>
              <Slider
                size="small"
                min={0}
                max={previewDuration > 0 ? previewDuration : 1}
                step={0.01}
                value={hasPreviewAudio ? Math.min(previewCurrentTime, previewDuration || 1) : 0}
                onChange={onSeekPreviewPlay}
                disabled={!hasPreviewAudio}
              />
            </Box>
            <Typography variant="caption" sx={{ minWidth: 96, textAlign: 'right', opacity: 0.75 }}>
              {formatTime(previewCurrentTime)} / {formatTime(previewDuration)}
            </Typography>
          </Stack>
          <Typography variant="caption" sx={{ opacity: 0.75, wordBreak: 'break-all' }}>
            {previewAudioPath || '暂无试听音频'}
          </Typography>
        </Stack>
      </Paper>
    </Stack>
  )
}
