import {
  Alert,
  Box,
  Button,
  Chip,
  FormControl,
  FormControlLabel,
  IconButton,
  InputAdornment,
  InputLabel,
  LinearProgress,
  MenuItem,
  Paper,
  Select,
  Stack,
  Switch,
  TextField,
  Typography,
} from '@mui/material'
import type { SxProps, Theme } from '@mui/material'
import type { ComponentType } from 'react'
import { MsIcon } from '../components/MsIcon'

type SettingsProgressStage = Exclude<PipelineStage, 'idle' | 'preview' | 'runtime'>
type SettingsProgressMap = Record<SettingsProgressStage, number>
type RuntimeStatusWithSources = PiperRuntimeStatus | PiperCudaRuntimeStatus | VoxCpmRuntimeStatus

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

const runtimeSourceLabels: Record<string, string> = {
  aliyun: '阿里云',
  bfsu: '北外',
  tencent: '腾讯云',
  sjtu: '上交',
  sustech: '南科大',
  tuna: '清华',
  ustc: '中科大',
  nju: '南大',
  huawei: '华为云',
  volces: '火山引擎',
  official: '官方源',
  conda: 'Conda',
  tuna_sustech_nvidia: '清华 + 南科大 nvidia',
  bfsu_sustech_nvidia: '北外 + 南科大 nvidia',
  ustc_sustech_nvidia: '中科大 + 南科大 nvidia',
  nju_sustech_nvidia: '南大 + 南科大 nvidia',
}

const getRuntimeSourceLabel = (source?: string | null) => {
  if (!source) return ''
  return runtimeSourceLabels[source] ?? source
}

const formatRuntimeSources = (status?: RuntimeStatusWithSources | null) => {
  if (!status) return ''
  const parts = [
    status.conda_source ? `Conda: ${getRuntimeSourceLabel(status.conda_source)}` : '',
    status.torch_source ? `Torch: ${getRuntimeSourceLabel(status.torch_source)}` : '',
    status.pip_toolchain_source ? `pip工具链: ${getRuntimeSourceLabel(status.pip_toolchain_source)}` : '',
    status.pip_dependency_source ? `pip依赖: ${getRuntimeSourceLabel(status.pip_dependency_source)}` : '',
  ].filter(Boolean)
  if (!parts.length && (status.source_label || status.source)) {
    parts.push(status.source_label ? `来源: ${status.source_label}` : `来源: ${getRuntimeSourceLabel(status.source)}`)
  }
  return parts.join(' / ')
}

const getCudaRuntimeChipColor = (status: PiperCudaRuntimeStatus | null): 'default' | 'success' | 'warning' | 'error' => {
  if (!status) return 'default'
  if (status.status === 'error') return 'error'
  if (!status.available) return 'warning'
  if (status.cuda_available === false) return 'warning'
  return 'success'
}

const getCudaRuntimeChipLabel = (status: PiperCudaRuntimeStatus | null) => {
  if (!status) return '未检测'
  if (status.status === 'error') return '运行时异常'
  if (!status.available) return '未安装'
  if (status.cuda_available === false) return '已安装 / CUDA 不可用'
  return '已就绪'
}

const getPiperRuntimeChipColor = (status: PiperRuntimeStatus | null): 'default' | 'success' | 'warning' | 'error' => {
  if (!status) return 'default'
  if (status.status === 'error') return 'error'
  if (!status.available) return 'warning'
  return 'success'
}

const getPiperRuntimeChipLabel = (status: PiperRuntimeStatus | null) => {
  if (!status) return '未检测'
  if (status.status === 'error') return '运行时异常'
  if (!status.available) return '未安装'
  return '已就绪'
}

const getTrainerResourcesChipColor = (status: TrainerResourceStatus | null): 'default' | 'success' | 'warning' | 'error' => {
  if (!status) return 'default'
  if (status.status === 'error') return 'error'
  if (!status.available) return 'warning'
  if (!status.external_available) return 'warning'
  return 'success'
}

const getTrainerResourcesChipLabel = (status: TrainerResourceStatus | null) => {
  if (!status) return '未检测'
  if (status.status === 'error') return '资源异常'
  if (!status.available) return '未安装'
  if (!status.external_available) return '可用但建议安装'
  return '已就绪'
}

export type SettingsPageProps = {
  cardPaperSx: SxProps<Theme>
  runtimeActionRowSx: SxProps<Theme>
  PathFieldComponent: ComponentType<PathFieldLikeProps>
  quality: 'A' | 'B'
  sampleRate: string
  trainBatchSize: string
  denoise: boolean
  trimSilence: boolean
  asrModel: string
  baseCkpt: string
  useEspeak: boolean
  piperConfig: string
  device: 'cpu' | 'cuda'
  downloadSourceConfig: DownloadSourceConfig | null
  downloadSourceBusy: boolean
  pipelineRunning: boolean
  trainerResourcesStatus: TrainerResourceStatus | null
  trainerResourcesBusy: boolean
  trainerResourcesProgressMessage: string
  trainerResourcesProgressValue: number
  piperRuntimeStatus: PiperRuntimeStatus | null
  piperRuntimeBusy: boolean
  piperRuntimeProgressMessage: string
  piperRuntimeProgressValue: number
  cudaRuntimeStatus: PiperCudaRuntimeStatus | null
  cudaRuntimeBusy: boolean
  cudaRuntimeProgressMessage: string
  cudaRuntimeProgressValue: number
  activeProgressStages: SettingsProgressStage[]
  progress: SettingsProgressMap
  stageLabels: Record<SettingsProgressStage, string>
  themeMode: 'system' | 'light' | 'dark'
  contextMenuMode: 'native' | 'custom'
  onQualityChange: (value: 'A' | 'B') => void
  onSampleRateChange: (value: string) => void
  onTrainBatchSizeChange: (value: string) => void
  onDenoiseChange: (value: boolean) => void
  onTrimSilenceChange: (value: boolean) => void
  onAsrModelChange: (value: string) => void
  onBaseCkptChange: (value: string) => void
  onUseEspeakChange: (value: boolean) => void
  onPiperConfigChange: (value: string) => void
  onPiperDeviceChange: (value: 'cpu' | 'cuda') => void
  onPickAsrModel: () => void
  onPickBaseCkpt: () => void
  onPickPiperConfig: () => void
  onDropFiles: (files: File[]) => Promise<string>
  onRefreshDownloadSourceConfig: () => unknown | Promise<unknown>
  onOpenDownloadSourceDialog: () => unknown | Promise<unknown>
  onSavePreferredDownloadSource: (groupKey: string, sourceId: string) => unknown | Promise<unknown>
  onRefreshTrainerResourcesStatus: () => unknown | Promise<unknown>
  onInstallTrainerResources: (force: boolean) => unknown | Promise<unknown>
  onInstallTrainerResourcesFromLocal: () => unknown | Promise<unknown>
  onOpenTrainerResourcesDirectory: () => unknown | Promise<unknown>
  onRefreshPiperRuntimeStatus: () => unknown | Promise<unknown>
  onInstallPiperRuntime: (force: boolean) => unknown | Promise<unknown>
  onInstallPiperRuntimeFromLocal: () => unknown | Promise<unknown>
  onOpenPiperRuntimeDirectory: () => unknown | Promise<unknown>
  onRefreshCudaRuntimeStatus: () => unknown | Promise<unknown>
  onInstallCudaRuntime: (force: boolean) => unknown | Promise<unknown>
  onInstallCudaRuntimeFromLocal: () => unknown | Promise<unknown>
  onOpenCudaRuntimeDirectory: () => unknown | Promise<unknown>
  onThemeModeChange: (value: 'system' | 'light' | 'dark') => void
  onContextMenuModeChange: (value: 'native' | 'custom') => void
  onRequestBackendRestart: () => void
}

export function SettingsPage({
  cardPaperSx,
  runtimeActionRowSx,
  PathFieldComponent,
  quality,
  sampleRate,
  trainBatchSize,
  denoise,
  trimSilence,
  asrModel,
  baseCkpt,
  useEspeak,
  piperConfig,
  device,
  downloadSourceConfig,
  downloadSourceBusy,
  pipelineRunning,
  trainerResourcesStatus,
  trainerResourcesBusy,
  trainerResourcesProgressMessage,
  trainerResourcesProgressValue,
  piperRuntimeStatus,
  piperRuntimeBusy,
  piperRuntimeProgressMessage,
  piperRuntimeProgressValue,
  cudaRuntimeStatus,
  cudaRuntimeBusy,
  cudaRuntimeProgressMessage,
  cudaRuntimeProgressValue,
  activeProgressStages,
  progress,
  stageLabels,
  themeMode,
  contextMenuMode,
  onQualityChange,
  onSampleRateChange,
  onTrainBatchSizeChange,
  onDenoiseChange,
  onTrimSilenceChange,
  onAsrModelChange,
  onBaseCkptChange,
  onUseEspeakChange,
  onPiperConfigChange,
  onPiperDeviceChange,
  onPickAsrModel,
  onPickBaseCkpt,
  onPickPiperConfig,
  onDropFiles,
  onRefreshDownloadSourceConfig,
  onOpenDownloadSourceDialog,
  onSavePreferredDownloadSource,
  onRefreshTrainerResourcesStatus,
  onInstallTrainerResources,
  onInstallTrainerResourcesFromLocal,
  onOpenTrainerResourcesDirectory,
  onRefreshPiperRuntimeStatus,
  onInstallPiperRuntime,
  onInstallPiperRuntimeFromLocal,
  onOpenPiperRuntimeDirectory,
  onRefreshCudaRuntimeStatus,
  onInstallCudaRuntime,
  onInstallCudaRuntimeFromLocal,
  onOpenCudaRuntimeDirectory,
  onThemeModeChange,
  onContextMenuModeChange,
  onRequestBackendRestart,
}: SettingsPageProps) {
  return (
    <Stack spacing={2}>
      <Paper sx={cardPaperSx}>
        <Typography variant="subtitle1" fontWeight={600} gutterBottom>
          训练参数
        </Typography>
        <Box
          sx={{
            display: 'grid',
            gap: 2,
            gridTemplateColumns: { xs: '1fr', md: 'repeat(2, 1fr)' },
          }}
        >
          <FormControl fullWidth size="small">
            <InputLabel>音频等级</InputLabel>
            <Select value={quality} label="音频等级" onChange={(event) => onQualityChange(event.target.value as 'A' | 'B')}>
              <MenuItem value="A">A 档</MenuItem>
              <MenuItem value="B">B 档(降噪)</MenuItem>
            </Select>
          </FormControl>
          <TextField
            label="采样率"
            value={sampleRate}
            onChange={(event) => onSampleRateChange(event.target.value)}
            fullWidth
            size="small"
            InputProps={{
              endAdornment: sampleRate ? (
                <InputAdornment position="end">
                  <IconButton size="small" onClick={() => onSampleRateChange('')}>
                    <MsIcon name="close" size={18} />
                  </IconButton>
                </InputAdornment>
              ) : undefined,
            }}
          />
          <TextField
            label="训练批量大小"
            value={trainBatchSize}
            onChange={(event) => onTrainBatchSizeChange(event.target.value)}
            fullWidth
            size="small"
            helperText="显存不足时会自动减小这个数并重试"
            InputProps={{
              endAdornment: trainBatchSize ? (
                <InputAdornment position="end">
                  <IconButton size="small" onClick={() => onTrainBatchSizeChange('')}>
                    <MsIcon name="close" size={18} />
                  </IconButton>
                </InputAdornment>
              ) : undefined,
            }}
          />
          <Box sx={{ gridColumn: '1 / -1' }}>
            <Stack spacing={0.5}>
              <FormControlLabel
                control={<Switch checked={denoise} onChange={(event) => onDenoiseChange(event.target.checked)} />}
                label="开启降噪/筛选"
              />
              <FormControlLabel
                control={<Switch checked={trimSilence} onChange={(event) => onTrimSilenceChange(event.target.checked)} />}
                label="训练时自动裁剪音频首尾空白"
              />
              <Typography variant="caption" sx={{ color: 'text.secondary', display: 'block', pl: 5 }}>
                默认开启；只会裁剪送入训练的临时音频，不会改动原始录音或蒸馏语料文件。
              </Typography>
            </Stack>
          </Box>
          <PathFieldComponent
            label="语音识别模型包"
            value={asrModel}
            onChange={onAsrModelChange}
            onPick={onPickAsrModel}
            onDropPath={onAsrModelChange}
            onDropFiles={onDropFiles}
          />
          <PathFieldComponent
            label="Piper 基线模型（可选）"
            value={baseCkpt}
            onChange={onBaseCkptChange}
            onPick={onPickBaseCkpt}
            onDropPath={onBaseCkptChange}
            onDropFiles={onDropFiles}
          />
          <FormControlLabel
            control={<Switch checked={useEspeak} onChange={(event) => onUseEspeakChange(event.target.checked)} />}
            label="使用兼容发音模式（espeak-ng）"
          />
          <PathFieldComponent
            label="Piper 发音配置"
            value={piperConfig}
            onChange={onPiperConfigChange}
            onPick={onPickPiperConfig}
            onDropPath={onPiperConfigChange}
            onDropFiles={onDropFiles}
          />
          <FormControl fullWidth size="small">
            <InputLabel>Piper 训练设备</InputLabel>
            <Select value={device} label="Piper 训练设备" onChange={(event) => onPiperDeviceChange(event.target.value as 'cpu' | 'cuda')}>
              <MenuItem value="cpu">CPU</MenuItem>
              <MenuItem value="cuda">GPU/CUDA</MenuItem>
            </Select>
          </FormControl>
        </Box>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1.5}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.25} alignItems={{ xs: 'flex-start', md: 'center' }} justifyContent="space-between">
            <Stack spacing={0.25}>
              <Typography variant="subtitle1" fontWeight={600}>
                下载源设置
              </Typography>
              <Typography variant="body2" sx={{ opacity: 0.72 }}>
                软件会优先使用你选择的下载源；如果该源暂时不可用，会自动尝试同一组里的其它链接。
              </Typography>
            </Stack>
            <Box sx={runtimeActionRowSx}>
              <Button
                variant="outlined"
                startIcon={<MsIcon name="refresh" size={18} />}
                onClick={() => {
                  void onRefreshDownloadSourceConfig()
                }}
                disabled={downloadSourceBusy || pipelineRunning}
              >
                刷新配置
              </Button>
              <Button
                variant="contained"
                startIcon={<MsIcon name="edit" size={18} />}
                onClick={() => {
                  void onOpenDownloadSourceDialog()
                }}
                disabled={downloadSourceBusy || pipelineRunning}
              >
                编辑链接
              </Button>
            </Box>
          </Stack>

          {downloadSourceConfig ? (
            <Stack spacing={1}>
              {downloadSourceConfig.groups.map((group) => {
                const selectableSources = group.sources.length ? group.sources : [{ id: '', label: '未配置', url: '' }]
                const selected = group.preferred_source_id || downloadSourceConfig.preferred_sources[group.key] || selectableSources[0]?.id || ''
                const configuredCount = group.sources.filter((source) => source.url.trim()).length
                return (
                  <Stack
                    key={group.key}
                    direction={{ xs: 'column', md: 'row' }}
                    spacing={1}
                    alignItems={{ xs: 'stretch', md: 'center' }}
                    justifyContent="space-between"
                    sx={{ py: 0.75 }}
                  >
                    <Box sx={{ minWidth: 0 }}>
                      <Typography variant="body2" fontWeight={600}>
                        {group.label}
                      </Typography>
                      <Typography variant="caption" sx={{ opacity: 0.68 }}>
                        已填写 {configuredCount}/{group.sources.length} 个源
                      </Typography>
                    </Box>
                    <FormControl size="small" sx={{ minWidth: { xs: '100%', md: 260 } }}>
                      <InputLabel>优先下载源</InputLabel>
                      <Select
                        value={selected}
                        label="优先下载源"
                        onChange={(event) => {
                          void onSavePreferredDownloadSource(group.key, String(event.target.value))
                        }}
                        disabled={downloadSourceBusy || pipelineRunning || !group.sources.length}
                      >
                        {selectableSources.map((source) => (
                          <MenuItem key={source.id || 'empty'} value={source.id}>
                            {source.label}
                            {source.url.trim() ? '' : '（未填写链接）'}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Stack>
                )
              })}
              <Typography variant="caption" sx={{ opacity: 0.64 }}>
                可在“编辑链接”中填写或调整 ModelScope、Hugging Face 等下载地址。
              </Typography>
            </Stack>
          ) : (
            <Alert severity="info">尚未读取下载源设置。</Alert>
          )}
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1.5}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.25} alignItems={{ xs: 'flex-start', md: 'center' }} justifyContent="space-between">
            <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
              <Typography variant="subtitle1" fontWeight={600}>
                训练资源包
              </Typography>
              <Chip size="small" color={getTrainerResourcesChipColor(trainerResourcesStatus)} label={getTrainerResourcesChipLabel(trainerResourcesStatus)} />
            </Stack>
            <Box sx={runtimeActionRowSx}>
              <Button
                variant="outlined"
                startIcon={<MsIcon name="refresh" size={18} />}
                onClick={() => {
                  void onRefreshTrainerResourcesStatus()
                }}
                disabled={trainerResourcesBusy || pipelineRunning}
              >
                刷新状态
              </Button>
              <Button
                variant="contained"
                startIcon={<MsIcon name={trainerResourcesStatus?.external_available ? 'archive' : 'download'} size={18} />}
                onClick={() => {
                  void onInstallTrainerResources(Boolean(trainerResourcesStatus?.external_available))
                }}
                disabled={trainerResourcesBusy || pipelineRunning}
              >
                {trainerResourcesStatus?.external_available ? '重新解压' : '下载解压'}
              </Button>
              <Button
                variant="outlined"
                startIcon={<MsIcon name="upload_file" size={18} />}
                onClick={() => {
                  void onInstallTrainerResourcesFromLocal()
                }}
                disabled={trainerResourcesBusy || pipelineRunning}
              >
                本地安装
              </Button>
            </Box>
          </Stack>
          <Box sx={{ minWidth: 0 }}>
            <Typography variant="body2" sx={{ opacity: 0.78 }}>
              首次训练前需要准备训练资源包，里面包含 ASR 模型、Piper 基线、发音字典和 espeak-ng 等必要资源。
            </Typography>
            <Typography variant="caption" sx={{ mt: 0.5, display: 'block', opacity: 0.62 }}>
              可以在线下载，也可以选择你已经下载好的本地 7z 文件安装。
            </Typography>
          </Box>

          {(trainerResourcesBusy || trainerResourcesProgressMessage) && (
            <Stack spacing={0.75}>
              {trainerResourcesBusy && (
                <LinearProgress
                  variant={trainerResourcesProgressValue > 0 ? 'determinate' : 'indeterminate'}
                  value={Math.min(100, Math.max(0, trainerResourcesProgressValue * 100))}
                />
              )}
              {trainerResourcesBusy && trainerResourcesProgressValue > 0 && (
                <Typography variant="caption" sx={{ opacity: 0.68 }}>
                  安装进度：{Math.round(trainerResourcesProgressValue * 100)}%
                </Typography>
              )}
              <Typography variant="caption" sx={{ opacity: 0.78 }}>
                {trainerResourcesProgressMessage || '正在处理训练资源包...'}
              </Typography>
            </Stack>
          )}

          {trainerResourcesStatus && (
            <Alert
              severity={
                trainerResourcesStatus.status === 'error'
                  ? 'error'
                  : trainerResourcesStatus.external_available
                    ? 'success'
                    : trainerResourcesStatus.available
                      ? 'warning'
                      : 'info'
              }
            >
              <Stack spacing={0.5}>
                <Typography variant="body2">{trainerResourcesStatus.message}</Typography>
                <Typography variant="caption" sx={{ opacity: 0.8, wordBreak: 'break-all' }}>
                  当前资源目录：{trainerResourcesStatus.active_resources_root || '未找到'}
                </Typography>
                <Typography variant="caption" sx={{ opacity: 0.8, wordBreak: 'break-all' }}>
                  外置资源目录：{trainerResourcesStatus.resources_root}
                </Typography>
                <Typography variant="caption" sx={{ opacity: 0.8 }}>
                  ASR 模型：{trainerResourcesStatus.asr_model_count ?? 0} / Piper 基线：{trainerResourcesStatus.piper_checkpoint_count ?? 0}
                  {trainerResourcesStatus.phonemizer_available ? ' / phonemizer 已就绪' : ' / phonemizer 缺失'}
                  {trainerResourcesStatus.espeak_available ? ' / espeak-ng 已就绪' : ' / espeak-ng 缺失'}
                </Typography>
                {trainerResourcesStatus.source_label && (
                  <Typography variant="caption" sx={{ opacity: 0.8 }}>
                    安装来源：{trainerResourcesStatus.source_label}
                  </Typography>
                )}
              </Stack>
            </Alert>
          )}

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
            <Button
              variant="outlined"
              startIcon={<MsIcon name="folder_open" size={18} />}
              onClick={() => {
                void onOpenTrainerResourcesDirectory()
              }}
              disabled={trainerResourcesBusy || !trainerResourcesStatus}
            >
              打开资源目录
            </Button>
            <Typography variant="caption" sx={{ alignSelf: 'center', opacity: 0.72 }}>
              安装完成后会自动刷新默认模型和基线路径。
            </Typography>
          </Stack>
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1.5}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.25} alignItems={{ xs: 'flex-start', md: 'center' }} justifyContent="space-between">
            <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
              <Typography variant="subtitle1" fontWeight={600}>
                Piper 基础运行时
              </Typography>
              <Chip size="small" color={getPiperRuntimeChipColor(piperRuntimeStatus)} label={getPiperRuntimeChipLabel(piperRuntimeStatus)} />
            </Stack>
            <Box sx={runtimeActionRowSx}>
              <Button
                variant="outlined"
                startIcon={<MsIcon name="refresh" size={18} />}
                onClick={() => {
                  void onRefreshPiperRuntimeStatus()
                }}
                disabled={piperRuntimeBusy || pipelineRunning}
              >
                刷新状态
              </Button>
              <Button
                variant="contained"
                startIcon={<MsIcon name={piperRuntimeStatus?.available ? 'archive' : 'download'} size={18} />}
                onClick={() => {
                  void onInstallPiperRuntime(Boolean(piperRuntimeStatus?.available))
                }}
                disabled={piperRuntimeBusy || pipelineRunning}
              >
                {piperRuntimeStatus?.available ? '重新解压' : '下载解压'}
              </Button>
              <Button
                variant="outlined"
                startIcon={<MsIcon name="upload_file" size={18} />}
                onClick={() => {
                  void onInstallPiperRuntimeFromLocal()
                }}
                disabled={piperRuntimeBusy || pipelineRunning}
              >
                本地安装
              </Button>
            </Box>
          </Stack>
          <Box sx={{ minWidth: 0 }}>
            <Typography variant="body2" sx={{ opacity: 0.78 }}>
              Piper 基础运行时用于 CPU 训练、预处理和导出。首次使用前需要先安装。
            </Typography>
            <Typography variant="caption" sx={{ mt: 0.5, display: 'block', opacity: 0.62 }}>
              可以在线下载，也可以选择你已经下载好的本地 7z 文件安装。
            </Typography>
          </Box>

          {(piperRuntimeBusy || piperRuntimeProgressMessage) && (
            <Stack spacing={0.75}>
              {piperRuntimeBusy && (
                <LinearProgress
                  variant={piperRuntimeProgressValue > 0 ? 'determinate' : 'indeterminate'}
                  value={Math.min(100, Math.max(0, piperRuntimeProgressValue * 100))}
                />
              )}
              {piperRuntimeBusy && piperRuntimeProgressValue > 0 && (
                <Typography variant="caption" sx={{ opacity: 0.68 }}>
                  安装进度：{Math.round(piperRuntimeProgressValue * 100)}%
                </Typography>
              )}
              <Typography variant="caption" sx={{ opacity: 0.78 }}>
                {piperRuntimeProgressMessage || '正在处理 Piper 基础运行时...'}
              </Typography>
            </Stack>
          )}

          {piperRuntimeStatus && (
            <Alert severity={piperRuntimeStatus.status === 'error' ? 'error' : piperRuntimeStatus.available ? 'success' : 'warning'}>
              <Stack spacing={0.5}>
                <Typography variant="body2">{piperRuntimeStatus.message}</Typography>
                <Typography variant="caption" sx={{ opacity: 0.8 }}>
                  运行时目录：{piperRuntimeStatus.env_path}
                </Typography>
                {piperRuntimeStatus.torch_version && (
                  <Typography variant="caption" sx={{ opacity: 0.8 }}>
                    Torch：{piperRuntimeStatus.torch_version}
                    {piperRuntimeStatus.pytorch_lightning_version ? ` / Lightning ${piperRuntimeStatus.pytorch_lightning_version}` : ''}
                  </Typography>
                )}
                {piperRuntimeStatus.source_label && (
                  <Typography variant="caption" sx={{ opacity: 0.8 }}>
                    安装来源：{piperRuntimeStatus.source_label}
                  </Typography>
                )}
              </Stack>
            </Alert>
          )}

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
            <Button
              variant="outlined"
              startIcon={<MsIcon name="folder_open" size={18} />}
              onClick={() => {
                void onOpenPiperRuntimeDirectory()
              }}
              disabled={piperRuntimeBusy || !piperRuntimeStatus}
            >
              打开运行时目录
            </Button>
            <Typography variant="caption" sx={{ alignSelf: 'center', opacity: 0.72 }}>
              CPU 训练和通用 Piper 流程会使用这个基础运行时。
            </Typography>
          </Stack>
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1.5}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.25} alignItems={{ xs: 'flex-start', md: 'center' }} justifyContent="space-between">
            <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
              <Typography variant="subtitle1" fontWeight={600}>
                Piper CUDA 运行时
              </Typography>
              <Chip size="small" color={getCudaRuntimeChipColor(cudaRuntimeStatus)} label={getCudaRuntimeChipLabel(cudaRuntimeStatus)} />
            </Stack>
            <Box sx={runtimeActionRowSx}>
              <Button
                variant="outlined"
                startIcon={<MsIcon name="refresh" size={18} />}
                onClick={() => {
                  void onRefreshCudaRuntimeStatus()
                }}
                disabled={cudaRuntimeBusy || pipelineRunning}
              >
                刷新状态
              </Button>
              <Button
                variant="contained"
                startIcon={<MsIcon name={cudaRuntimeStatus?.available ? 'build' : 'download'} size={18} />}
                onClick={() => {
                  void onInstallCudaRuntime(Boolean(cudaRuntimeStatus?.available))
                }}
                disabled={cudaRuntimeBusy || pipelineRunning}
              >
                {cudaRuntimeStatus?.available ? '重新解压' : '安装运行时'}
              </Button>
              <Button
                variant="outlined"
                startIcon={<MsIcon name="upload_file" size={18} />}
                onClick={() => {
                  void onInstallCudaRuntimeFromLocal()
                }}
                disabled={cudaRuntimeBusy || pipelineRunning}
              >
                本地安装
              </Button>
            </Box>
          </Stack>
          <Box sx={{ minWidth: 0 }}>
            <Typography variant="body2" sx={{ opacity: 0.78 }}>
              Piper CUDA 运行时用于显卡训练。首次使用 GPU/CUDA 训练前需要先安装。
            </Typography>
            <Typography variant="caption" sx={{ mt: 0.5, display: 'block', opacity: 0.62 }}>
              软件会自动选择可用下载源；也可以选择你已经下载好的本地 7z 文件安装。
            </Typography>
          </Box>

          {(cudaRuntimeBusy || cudaRuntimeProgressMessage) && (
            <Stack spacing={0.75}>
              {cudaRuntimeBusy && (
                <LinearProgress
                  variant={cudaRuntimeProgressValue > 0 ? 'determinate' : 'indeterminate'}
                  value={Math.min(100, Math.max(0, cudaRuntimeProgressValue * 100))}
                />
              )}
              {cudaRuntimeBusy && cudaRuntimeProgressValue > 0 && (
                <Typography variant="caption" sx={{ opacity: 0.68 }}>
                  安装进度：{Math.round(cudaRuntimeProgressValue * 100)}%
                </Typography>
              )}
              <Typography variant="caption" sx={{ opacity: 0.78 }}>
                {cudaRuntimeProgressMessage || '正在处理 Piper CUDA 运行时...'}
              </Typography>
            </Stack>
          )}

          {cudaRuntimeStatus && (
            <Alert severity={cudaRuntimeStatus.status === 'error' ? 'error' : cudaRuntimeStatus.cuda_available === false ? 'warning' : cudaRuntimeStatus.available ? 'success' : 'info'}>
              <Stack spacing={0.5}>
                <Typography variant="body2">{cudaRuntimeStatus.message}</Typography>
                <Typography variant="caption" sx={{ opacity: 0.8 }}>
                  运行时目录：{cudaRuntimeStatus.env_path}
                </Typography>
                {cudaRuntimeStatus.driver_version && (
                  <Typography variant="caption" sx={{ opacity: 0.8 }}>
                    NVIDIA 驱动：{cudaRuntimeStatus.driver_version}
                    {cudaRuntimeStatus.gpu_name ? ` / ${cudaRuntimeStatus.gpu_name}` : ''}
                    {cudaRuntimeStatus.gpu_memory ? ` / ${cudaRuntimeStatus.gpu_memory}` : ''}
                  </Typography>
                )}
                {cudaRuntimeStatus.torch_version && (
                  <Typography variant="caption" sx={{ opacity: 0.8 }}>
                    Torch：{cudaRuntimeStatus.torch_version}
                    {cudaRuntimeStatus.torch_cuda_version ? ` / CUDA Runtime ${cudaRuntimeStatus.torch_cuda_version}` : ''}
                  </Typography>
                )}
                {formatRuntimeSources(cudaRuntimeStatus) && (
                  <Typography variant="caption" sx={{ opacity: 0.8 }}>
                    安装来源：{formatRuntimeSources(cudaRuntimeStatus)}
                  </Typography>
                )}
              </Stack>
            </Alert>
          )}

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
            <Button
              variant="outlined"
              startIcon={<MsIcon name="folder_open" size={18} />}
              onClick={() => {
                void onOpenCudaRuntimeDirectory()
              }}
              disabled={cudaRuntimeBusy || !cudaRuntimeStatus}
            >
              打开运行时目录
            </Button>
            <Typography variant="caption" sx={{ alignSelf: 'center', opacity: 0.72 }}>
              建议仅在 NVIDIA 驱动正常、系统已能识别 GPU 的机器上安装。
            </Typography>
          </Stack>
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1}>
          <Typography variant="subtitle1" fontWeight={600}>
            进度
          </Typography>
          {activeProgressStages.map((stage) => (
            <Box key={stage}>
              <Stack direction="row" justifyContent="space-between">
                <Typography variant="caption" sx={{ opacity: 0.7 }}>
                  {stageLabels[stage]}
                </Typography>
                <Typography variant="caption">{Math.round((progress[stage] ?? 0) * 100)}%</Typography>
              </Stack>
              <LinearProgress variant="determinate" value={(progress[stage] ?? 0) * 100} sx={{ height: 8, borderRadius: 6 }} />
            </Box>
          ))}
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1}>
          <Typography variant="subtitle1" fontWeight={600}>
            系统
          </Typography>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1} alignItems={{ sm: 'center' }} sx={{ width: '100%' }}>
            <FormControl size="small" sx={{ width: { xs: '100%', sm: 220 } }}>
              <InputLabel>主题模式</InputLabel>
              <Select value={themeMode} label="主题模式" onChange={(event) => onThemeModeChange(event.target.value as 'system' | 'light' | 'dark')}>
                <MenuItem value="system">跟随系统</MenuItem>
                <MenuItem value="dark">暗色</MenuItem>
                <MenuItem value="light">明亮</MenuItem>
              </Select>
            </FormControl>
            <FormControlLabel
              control={
                <Switch
                  checked={contextMenuMode === 'custom'}
                  onChange={(event) => onContextMenuModeChange(event.target.checked ? 'custom' : 'native')}
                />
              }
              label="自绘右键菜单"
            />
            <Box sx={{ flexGrow: 1, display: { xs: 'none', sm: 'block' } }} />
            <Stack direction="row" spacing={1} sx={{ width: { xs: '100%', sm: 'auto' }, justifyContent: 'flex-end' }}>
              <Button variant="contained" color="primary" startIcon={<MsIcon name="restart_alt" size={18} />} onClick={onRequestBackendRestart}>
                重启后台服务
              </Button>
            </Stack>
          </Stack>
        </Stack>
      </Paper>
    </Stack>
  )
}
