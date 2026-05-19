import { Box, Chip, IconButton, Paper, Stack, Tooltip, Typography } from '@mui/material'
import type { SxProps, Theme } from '@mui/material'
import { MsIcon } from '../components/MsIcon'

export type LogsPageProps = {
  cardPaperSx: SxProps<Theme>
  connected: boolean
  logs: string[]
  onExportLogs: () => void
}

export function LogsPage({ cardPaperSx, connected, logs, onExportLogs }: LogsPageProps) {
  return (
    <Paper sx={{ ...cardPaperSx, flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column' }}>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="subtitle1" fontWeight={600}>
          日志
        </Typography>
        <Stack direction="row" alignItems="center" spacing={0.5}>
          <Chip
            label={connected ? '后台服务已连接' : '后台服务未连接'}
            color={connected ? 'success' : 'default'}
            variant="outlined"
            size="small"
          />
          <Tooltip title="导出日志" arrow>
            <IconButton size="small" onClick={onExportLogs}>
              <MsIcon name="download" size={18} />
            </IconButton>
          </Tooltip>
        </Stack>
      </Stack>
      <Box
        className="allow-text-select"
        sx={{
          mt: 1,
          flex: 1,
          minHeight: 0,
          overflow: 'auto',
          fontFamily: 'Menlo, Consolas, monospace',
          fontSize: 12,
          whiteSpace: 'pre-wrap',
        }}
      >
        {logs.length === 0 ? (
          <Typography variant="caption" sx={{ opacity: 0.6 }}>
            暂无日志
          </Typography>
        ) : (
          logs.map((line, idx) => <Box key={`${idx}-${line}`}>{line}</Box>)
        )}
      </Box>
    </Paper>
  )
}
