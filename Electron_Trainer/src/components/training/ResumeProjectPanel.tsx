import { Alert, Button, Chip, CircularProgress, Paper, Stack, Typography } from '@mui/material'
import type { SxProps, Theme } from '@mui/material'
import type { ComponentType } from 'react'
import { MsIcon } from '../MsIcon'

type PathFieldLikeProps = {
  label: string
  value: string
  onChange: (value: string) => void
  onPick: () => void
  onDropPath?: (value: string) => void
  helperText?: string
  placeholder?: string
}

export type ResumeProjectPanelProps = {
  cardPaperSx: SxProps<Theme>
  PathFieldComponent: ComponentType<PathFieldLikeProps>
  resumeProjectDir: string
  resumeProjectBusy: boolean
  resumeProjectStatus: TrainingProjectStatus | null
  trainingModeLabels: Record<TrainingMode, string>
  onResumeProjectDirChange: (value: string) => void
  onResumeProjectStatusClear: () => void
  onPickResumeProjectDir: () => void
  onInspectResumeProject: (value?: string) => void | Promise<unknown>
}

export function ResumeProjectPanel({
  cardPaperSx,
  PathFieldComponent,
  resumeProjectDir,
  resumeProjectBusy,
  resumeProjectStatus,
  trainingModeLabels,
  onResumeProjectDirChange,
  onResumeProjectStatusClear,
  onPickResumeProjectDir,
  onInspectResumeProject,
}: ResumeProjectPanelProps) {
  const updateDir = (value: string) => {
    onResumeProjectDirChange(value)
    onResumeProjectStatusClear()
  }

  return (
    <Paper sx={cardPaperSx}>
      <Stack spacing={1.5}>
        <Typography variant="subtitle1" fontWeight={600}>
          旧项目继续训练
        </Typography>
        <PathFieldComponent
          label="旧训练项目目录"
          value={resumeProjectDir}
          onChange={updateDir}
          onPick={onPickResumeProjectDir}
          onDropPath={(value) => {
            updateDir(value)
            void onInspectResumeProject(value)
          }}
          helperText="选择之前创建的训练项目目录，软件会自动检查可继续使用的素材。"
          placeholder="选择旧训练项目根目录"
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
          <Button
            variant="outlined"
            startIcon={resumeProjectBusy ? <CircularProgress size={16} color="inherit" /> : <MsIcon name="refresh" size={18} />}
            onClick={() => {
              void onInspectResumeProject()
            }}
            disabled={resumeProjectBusy || !resumeProjectDir.trim()}
          >
            检查项目
          </Button>
          <Typography variant="caption" sx={{ alignSelf: 'center', opacity: 0.72 }}>
            继续训练会优先使用旧项目保存的设置和素材；当前页面的文本来源不会覆盖旧项目。
          </Typography>
        </Stack>
        {resumeProjectStatus && (
          <Alert severity={resumeProjectStatus.ok ? (resumeProjectStatus.needs_material_rebuild ? 'warning' : 'success') : 'error'}>
            <Stack spacing={1}>
              <Typography variant="body2">{resumeProjectStatus.message}</Typography>
              {resumeProjectStatus.material_status && (
                <Typography variant="body2" sx={{ opacity: 0.86 }}>
                  {resumeProjectStatus.material_status}
                </Typography>
              )}
              <Stack direction="row" spacing={0.75} useFlexGap flexWrap="wrap">
                <Chip
                  size="small"
                  label={`模式：${trainingModeLabels[String(resumeProjectStatus.mode || '') as TrainingMode] || resumeProjectStatus.mode || '未知'}`}
                />
                <Chip size="small" label={`文本记录：${resumeProjectStatus.metadata_count ?? 0} 条`} />
                <Chip size="small" label={`可用音频：${resumeProjectStatus.existing_count ?? 0} 条`} />
                <Chip
                  size="small"
                  color={resumeProjectStatus.missing_count ? 'warning' : 'default'}
                  label={`缺失/损坏音频：${resumeProjectStatus.missing_count ?? 0} 条`}
                />
                <Chip
                  size="small"
                  color={resumeProjectStatus.direct_train_ready ? 'success' : 'warning'}
                  label={resumeProjectStatus.direct_train_ready ? '素材完整：直接训练' : '需要准备素材'}
                />
                {resumeProjectStatus.input_audio_count !== undefined && resumeProjectStatus.input_audio_count > 0 && (
                  <Chip
                    size="small"
                    color={resumeProjectStatus.input_audio_missing_count ? 'warning' : 'default'}
                    label={`原始音频：${resumeProjectStatus.input_audio_available_count ?? 0}/${resumeProjectStatus.input_audio_count}`}
                  />
                )}
                {resumeProjectStatus.metadata_inconsistent && <Chip size="small" color="warning" label="文本记录不一致" />}
              </Stack>
              {resumeProjectStatus.config_summary && (
                <Typography variant="caption" sx={{ opacity: 0.78, wordBreak: 'break-all' }}>
                  配置摘要：{resumeProjectStatus.config_summary}
                </Typography>
              )}
              {resumeProjectStatus.config_path && (
                <Typography variant="caption" sx={{ opacity: 0.78, wordBreak: 'break-all' }}>
                  配置：{resumeProjectStatus.config_path}
                </Typography>
              )}
            </Stack>
          </Alert>
        )}
        <Alert severity="info">
          音频完整时会直接进入 Piper 训练；VoxCPM2 项目缺失或损坏音频会用项目内配置继续生成；GPT-SoVITS
          项目缺失或损坏音频但找不到对应模型时，会移除不可用文本后继续训练，若音频完全不可用则无法开始。
        </Alert>
      </Stack>
    </Paper>
  )
}
