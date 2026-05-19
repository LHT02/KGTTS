import {
  Alert,
  Box,
  Button,
  ButtonBase,
  Chip,
  FormControl,
  InputLabel,
  LinearProgress,
  MenuItem,
  Paper,
  Select,
  Stack,
  Typography,
  useTheme,
} from '@mui/material'
import type { SxProps, Theme } from '@mui/material'
import type { Dispatch, ReactNode, SetStateAction } from 'react'
import { MsIcon } from '../components/MsIcon'

type AppPage = 'guide' | 'prep' | 'settings' | 'preview' | 'soundboard' | 'logs' | 'about'

type GuideModeOption = {
  mode: TrainingMode
  icon: string
  tag: string
  title: string
  description: string
  beginnerNote: string
}

type GuideRow = {
  key: string
  label: string
  detail: string
  ready: boolean
}

export type QuickStartPageProps = {
  cardPaperSx: SxProps<Theme>
  resolvedThemeMode: 'light' | 'dark'
  guideStep: number
  guideStepMax: number
  guideStepProgress: number
  guideStepContent: ReactNode
  guideStepLabels: string[]
  guideNextIsVoxcpmPreview: boolean
  trainingMode: TrainingMode
  trainingModeLabels: Record<TrainingMode, string>
  guideModeOptions: GuideModeOption[]
  guideModeTitle: string
  guideDependencyRows: GuideRow[]
  guideMaterialRows: GuideRow[]
  guideOutputRows: GuideRow[]
  device: 'cpu' | 'cuda'
  pipelineRunning: boolean
  trainingFabBlockedByBackgroundTask: boolean
  previewBusy: boolean
  voxcpmBootstrapPreviewBusy: boolean
  onGuideStepChange: Dispatch<SetStateAction<number>>
  onStartPipeline: () => void | Promise<void>
  onAbortPipeline: () => void
  onStartGuideVoxcpmBootstrapPreview: () => void | Promise<void>
  onOpenGuideDependencyCheck: () => void | Promise<void>
  onPageChange: (page: AppPage) => void
  onTrainingModeChange: (mode: TrainingMode) => void
  onPiperDeviceChange: (device: 'cpu' | 'cuda') => void
  onPickAudioFiles: () => void
  onOpenDistillPresetDialog: () => void
  onPickResumeProjectDir: () => void
}

export function QuickStartPage({
  cardPaperSx,
  resolvedThemeMode,
  guideStep,
  guideStepMax,
  guideStepProgress,
  guideStepContent,
  guideStepLabels,
  guideNextIsVoxcpmPreview,
  trainingMode,
  trainingModeLabels,
  guideModeOptions,
  guideModeTitle,
  guideDependencyRows,
  guideMaterialRows,
  guideOutputRows,
  device,
  pipelineRunning,
  trainingFabBlockedByBackgroundTask,
  previewBusy,
  voxcpmBootstrapPreviewBusy,
  onGuideStepChange,
  onStartPipeline,
  onAbortPipeline,
  onStartGuideVoxcpmBootstrapPreview,
  onOpenGuideDependencyCheck,
  onPageChange,
  onTrainingModeChange,
  onPiperDeviceChange,
  onPickAudioFiles,
  onOpenDistillPresetDialog,
  onPickResumeProjectDir,
}: QuickStartPageProps) {
  const theme = useTheme()

  if (guideStep >= 0) {
    return (
      <Stack spacing={2}>
        <Paper sx={cardPaperSx}>
          <Stack spacing={1.5}>
            <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5} alignItems={{ xs: 'stretch', md: 'center' }} justifyContent="space-between">
              <Box>
                <Typography variant="h5" fontWeight={700}>
                  快速开始
                </Typography>
                <Typography variant="body2" sx={{ mt: 0.5, opacity: 0.74 }}>
                  按步骤制作一个可导入 KIGTTS 手机应用的语音包。
                </Typography>
              </Box>
              <Chip label={`${guideStep + 1}/${guideStepLabels.length} ${guideStepLabels[guideStep]}`} color="primary" variant="outlined" />
            </Stack>
            <LinearProgress variant="determinate" value={guideStepProgress} />
            <Stack direction="row" spacing={0.75} useFlexGap flexWrap="wrap">
              {guideStepLabels.map((label, index) => (
                <Button
                  key={label}
                  size="small"
                  variant={guideStep === index ? 'contained' : 'text'}
                  onClick={() => onGuideStepChange(index)}
                >
                  {index + 1}. {label}
                </Button>
              ))}
            </Stack>
          </Stack>
        </Paper>

        <Paper sx={cardPaperSx}>{guideStepContent}</Paper>

        <Paper sx={cardPaperSx}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} justifyContent="space-between">
            <Button
              variant="text"
              startIcon={<MsIcon name="chevron_left" size={18} />}
              onClick={() => onGuideStepChange((prev) => Math.max(0, prev - 1))}
              disabled={guideStep === 0}
            >
              上一步
            </Button>
            <Button
              variant={guideStep === guideStepMax || guideNextIsVoxcpmPreview ? 'contained' : 'outlined'}
              endIcon={
                <MsIcon
                  name={guideStep === guideStepMax ? 'play_arrow' : guideNextIsVoxcpmPreview ? 'graphic_eq' : 'chevron_right'}
                  size={18}
                />
              }
              onClick={() => {
                if (guideStep === guideStepMax) {
                  void onStartPipeline()
                  return
                }
                if (guideNextIsVoxcpmPreview) {
                  void onStartGuideVoxcpmBootstrapPreview()
                  return
                }
                onGuideStepChange((prev) => Math.min(guideStepMax, prev + 1))
              }}
              disabled={
                guideStep === guideStepMax
                  ? pipelineRunning || trainingFabBlockedByBackgroundTask || previewBusy
                  : guideNextIsVoxcpmPreview
                    ? trainingFabBlockedByBackgroundTask || previewBusy || voxcpmBootstrapPreviewBusy
                    : false
              }
            >
              {guideStep === guideStepMax ? '开始制作语音包' : guideNextIsVoxcpmPreview ? '试听确认' : '下一步'}
            </Button>
          </Stack>
        </Paper>
      </Stack>
    )
  }

  return (
    <Stack spacing={2}>
      <Paper
        sx={{
          ...cardPaperSx,
          overflow: 'hidden',
          position: 'relative',
          background:
            resolvedThemeMode === 'dark'
              ? 'linear-gradient(135deg, rgba(0,150,136,0.18), rgba(15,23,42,0.18))'
              : 'linear-gradient(135deg, rgba(0,150,136,0.10), rgba(236,253,245,0.55))',
        }}
      >
        <Stack spacing={2}>
          <Stack direction={{ xs: 'column', lg: 'row' }} spacing={2} alignItems={{ xs: 'flex-start', lg: 'center' }} justifyContent="space-between">
            <Box sx={{ minWidth: 0 }}>
              <Typography variant="h5" fontWeight={700} gutterBottom>
                新手训练引导
              </Typography>
              <Typography variant="body2" sx={{ opacity: 0.78, maxWidth: 820 }}>
                不需要先理解 VAD、ASR、蒸馏或 Piper。按下面 5 步检查：选路线、装依赖、准备素材、确认输出、开始训练。
              </Typography>
            </Box>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} sx={{ width: { xs: '100%', lg: 'auto' } }}>
              <Button
                variant="contained"
                startIcon={<MsIcon name="checklist" size={18} />}
                onClick={() => {
                  void onOpenGuideDependencyCheck()
                }}
                disabled={pipelineRunning || trainingFabBlockedByBackgroundTask}
              >
                检查依赖
              </Button>
              <Button variant="outlined" startIcon={<MsIcon name="folder" size={18} />} onClick={() => onPageChange('prep')}>
                去训练准备
              </Button>
            </Stack>
          </Stack>
          <Alert severity="info">
            新手建议：先选择“我有自己的录音”，保持默认 GPU/CUDA 训练。如果当前电脑没有准备 CUDA 运行时，软件会引导你安装；装不上也可以切换 CPU。
          </Alert>
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1.5}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5} alignItems={{ xs: 'stretch', md: 'center' }} justifyContent="space-between">
            <Box>
              <Typography variant="subtitle1" fontWeight={700}>
                1. 选择你的训练路线
              </Typography>
              <Typography variant="body2" sx={{ opacity: 0.72 }}>
                这里的选择会同步修改“训练准备”页的训练模式。
              </Typography>
            </Box>
            <FormControl size="small" sx={{ minWidth: { xs: '100%', md: 260 } }}>
              <InputLabel>当前路线</InputLabel>
              <Select
                value={trainingMode}
                label="当前路线"
                onChange={(event) => onTrainingModeChange(event.target.value as TrainingMode)}
                disabled={pipelineRunning}
              >
                <MenuItem value="piper">Piper 标准</MenuItem>
                <MenuItem value="gsv_distill">GPT-SoVITS 蒸馏</MenuItem>
                <MenuItem value="voxcpm_distill">VoxCPM2 蒸馏</MenuItem>
                <MenuItem value="resume_project">从旧项目继续训练</MenuItem>
              </Select>
            </FormControl>
          </Stack>
          <Box sx={{ display: 'grid', gap: 1.25, gridTemplateColumns: { xs: '1fr', lg: 'repeat(4, 1fr)' } }}>
            {guideModeOptions.map((item) => {
              const active = item.mode === trainingMode
              return (
                <ButtonBase
                  key={item.mode}
                  onClick={() => onTrainingModeChange(item.mode)}
                  disabled={pipelineRunning}
                  sx={{
                    display: 'block',
                    textAlign: 'left',
                    borderRadius: 2,
                    border: '1px solid',
                    borderColor: active ? 'primary.main' : 'divider',
                    bgcolor: active ? 'action.selected' : 'background.default',
                    p: 1.5,
                    minHeight: 168,
                    transition: theme.transitions.create(['background-color', 'border-color', 'transform'], {
                      duration: theme.transitions.duration.shorter,
                    }),
                    '&:hover': {
                      bgcolor: 'action.hover',
                      transform: pipelineRunning ? 'none' : 'translateY(-1px)',
                    },
                  }}
                >
                  <Stack spacing={1} sx={{ height: '100%' }}>
                    <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
                      <MsIcon name={item.icon} size={24} fill={active ? 1 : 0} />
                      <Chip size="small" color={item.mode === 'piper' ? 'success' : active ? 'primary' : 'default'} label={item.tag} />
                    </Stack>
                    <Typography variant="subtitle2" fontWeight={700}>
                      {item.title}
                    </Typography>
                    <Typography variant="body2" sx={{ opacity: 0.76 }}>
                      {item.description}
                    </Typography>
                    <Typography variant="caption" sx={{ mt: 'auto', opacity: 0.66 }}>
                      {item.beginnerNote}
                    </Typography>
                  </Stack>
                </ButtonBase>
              )
            })}
          </Box>
        </Stack>
      </Paper>

      <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', xl: 'repeat(2, minmax(0, 1fr))' } }}>
        <Paper sx={cardPaperSx}>
          <Stack spacing={1.5}>
            <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
              <Box>
                <Typography variant="subtitle1" fontWeight={700}>
                  2. 准备依赖
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.72 }}>
                  训练、识别、蒸馏和导出需要不同运行时。缺什么装什么。
                </Typography>
              </Box>
              <Button
                size="small"
                variant="outlined"
                startIcon={<MsIcon name="build" size={18} />}
                onClick={() => {
                  void onOpenGuideDependencyCheck()
                }}
                disabled={pipelineRunning || trainingFabBlockedByBackgroundTask}
              >
                准备缺失项
              </Button>
            </Stack>
            <Stack spacing={1}>
              {guideDependencyRows.map((item) => (
                <GuideStatusRow key={item.key} item={item} readyLabel="已准备" pendingLabel="需要处理" />
              ))}
            </Stack>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
              <Button variant="text" startIcon={<MsIcon name="tune" size={18} />} onClick={() => onPageChange('settings')}>
                打开训练设置
              </Button>
              <Button
                variant="text"
                startIcon={<MsIcon name="memory" size={18} />}
                onClick={() => onPiperDeviceChange(device === 'cuda' ? 'cpu' : 'cuda')}
                disabled={pipelineRunning}
              >
                切换为 {device === 'cuda' ? 'CPU 训练' : 'GPU/CUDA 训练'}
              </Button>
            </Stack>
          </Stack>
        </Paper>

        <Paper sx={cardPaperSx}>
          <Stack spacing={1.5}>
            <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
              <Box>
                <Typography variant="subtitle1" fontWeight={700}>
                  3. 准备训练素材
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.72 }}>
                  当前路线：{guideModeTitle}
                </Typography>
              </Box>
              <Button size="small" variant="outlined" startIcon={<MsIcon name="folder_open" size={18} />} onClick={() => onPageChange('prep')}>
                去准备
              </Button>
            </Stack>
            <Stack spacing={1}>
              {guideMaterialRows.map((item) => (
                <GuideStatusRow key={item.key} item={item} readyLabel="已完成" pendingLabel="待完成" />
              ))}
            </Stack>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
              {trainingMode === 'piper' && (
                <Button variant="text" startIcon={<MsIcon name="add" size={18} />} onClick={onPickAudioFiles}>
                  添加录音
                </Button>
              )}
              {(trainingMode === 'gsv_distill' || trainingMode === 'voxcpm_distill') && (
                <Button variant="text" startIcon={<MsIcon name="library_books" size={18} />} onClick={onOpenDistillPresetDialog}>
                  添加内置预设文本
                </Button>
              )}
              {trainingMode === 'resume_project' && (
                <Button variant="text" startIcon={<MsIcon name="folder" size={18} />} onClick={onPickResumeProjectDir}>
                  选择旧项目
                </Button>
              )}
            </Stack>
          </Stack>
        </Paper>
      </Box>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1.5}>
          <Typography variant="subtitle1" fontWeight={700}>
            4. 确认输出和语音包信息
          </Typography>
          <Box sx={{ display: 'grid', gap: 1, gridTemplateColumns: { xs: '1fr', md: 'repeat(3, minmax(0, 1fr))' } }}>
            {guideOutputRows.map((item) => (
              <Box key={item.key} sx={{ p: 1.25, borderRadius: 2, bgcolor: 'background.default', minWidth: 0 }}>
                <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 0.5 }}>
                  <MsIcon name={item.ready ? 'check_circle' : 'info'} size={20} fill={item.ready ? 1 : 0} />
                  <Typography variant="body2" fontWeight={700}>
                    {item.label}
                  </Typography>
                </Stack>
                <Typography variant="caption" sx={{ opacity: 0.68, wordBreak: 'break-all' }}>
                  {item.detail}
                </Typography>
              </Box>
            ))}
          </Box>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
            <Button variant="text" startIcon={<MsIcon name="edit" size={18} />} onClick={() => onPageChange('prep')}>
              修改项目与语音包信息
            </Button>
            <Button variant="text" startIcon={<MsIcon name="settings" size={18} />} onClick={() => onPageChange('settings')}>
              调整训练参数
            </Button>
          </Stack>
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack direction={{ xs: 'column', lg: 'row' }} spacing={2} alignItems={{ xs: 'stretch', lg: 'center' }} justifyContent="space-between">
          <Stack spacing={0.75}>
            <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
              <Typography variant="subtitle1" fontWeight={700}>
                5. 开始训练
              </Typography>
              <Chip size="small" color="primary" label={trainingModeLabels[trainingMode]} />
              <Chip size="small" label={`Piper ${device === 'cuda' ? 'GPU/CUDA' : 'CPU'}`} />
            </Stack>
            <Typography variant="body2" sx={{ opacity: 0.72 }}>
              点击后会复用正式训练入口：缺依赖会弹出安装引导，旧项目需要重建素材会弹出确认，VoxCPM2 声音设定会先要求确认预览音色。
            </Typography>
          </Stack>
          <Button
            variant="contained"
            size="large"
            startIcon={<MsIcon name={pipelineRunning ? 'stop' : 'play_arrow'} size={20} fill={1} />}
            onClick={pipelineRunning ? onAbortPipeline : onStartPipeline}
            disabled={trainingFabBlockedByBackgroundTask || previewBusy}
          >
            {pipelineRunning ? '停止训练' : '我已准备好，开始训练语音包'}
          </Button>
        </Stack>
      </Paper>
    </Stack>
  )
}

function GuideStatusRow({
  item,
  readyLabel,
  pendingLabel,
}: {
  item: GuideRow
  readyLabel: string
  pendingLabel: string
}) {
  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: '28px minmax(0, 1fr)',
        gap: 1,
        alignItems: 'flex-start',
        py: 0.75,
      }}
    >
      <MsIcon name={item.ready ? 'check_circle' : 'radio_button_unchecked'} size={22} fill={item.ready ? 1 : 0} />
      <Box sx={{ minWidth: 0 }}>
        <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
          <Typography variant="body2" fontWeight={700}>
            {item.label}
          </Typography>
          <Chip size="small" color={item.ready ? 'success' : 'warning'} label={item.ready ? readyLabel : pendingLabel} />
        </Stack>
        <Typography variant="caption" sx={{ opacity: 0.68 }}>
          {item.detail}
        </Typography>
      </Box>
    </Box>
  )
}
