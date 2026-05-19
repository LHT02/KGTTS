import { FormControl, InputLabel, MenuItem, Paper, Select, Stack, Typography } from '@mui/material'
import type { SxProps, Theme } from '@mui/material'

const TRAINING_MODE_DESCRIPTION: Record<TrainingMode, string> = {
  piper: '标准模式会重新做裁剪、VAD 和 ASR。',
  gsv_distill: '蒸馏模式会直接从 GPT-SoVITS 说话人模型生成语料，再继续训练并导出 KIGTTS 语音包。',
  voxcpm_distill: 'VoxCPM2 蒸馏会用音色描述或参考音频生成语料，再继续训练并导出 KIGTTS 语音包。',
  resume_project: '从旧项目读取已保存的训练模式和参数；音频完整时直接训练，缺失时按项目配置尝试补生成。',
}

export type TrainingModeCardProps = {
  cardPaperSx: SxProps<Theme>
  trainingMode: TrainingMode
  pipelineRunning: boolean
  onTrainingModeChange: (mode: TrainingMode) => void
}

export function TrainingModeCard({
  cardPaperSx,
  trainingMode,
  pipelineRunning,
  onTrainingModeChange,
}: TrainingModeCardProps) {
  return (
    <Paper sx={cardPaperSx}>
      <Typography variant="subtitle1" fontWeight={600} gutterBottom>
        训练模式
      </Typography>
      <Stack
        direction={{ xs: 'column', md: 'row' }}
        spacing={2}
        alignItems={{ xs: 'stretch', md: 'center' }}
        justifyContent="space-between"
      >
        <FormControl size="small" sx={{ minWidth: { xs: '100%', md: 280 } }}>
          <InputLabel>模式</InputLabel>
          <Select
            value={trainingMode}
            label="模式"
            onChange={(event) => onTrainingModeChange(event.target.value as TrainingMode)}
            disabled={pipelineRunning}
          >
            <MenuItem value="piper">Piper 标准</MenuItem>
            <MenuItem value="gsv_distill">GPT-SoVITS 蒸馏</MenuItem>
            <MenuItem value="voxcpm_distill">VoxCPM2 蒸馏</MenuItem>
            <MenuItem value="resume_project">从旧项目继续训练</MenuItem>
          </Select>
        </FormControl>
        <Typography variant="body2" sx={{ opacity: 0.78 }}>
          {TRAINING_MODE_DESCRIPTION[trainingMode]}
        </Typography>
      </Stack>
    </Paper>
  )
}
