import { Box, Button, Paper, Stack, Tooltip, Typography } from '@mui/material'
import type { ComponentType } from 'react'
import type { SxProps, Theme } from '@mui/material'
import { MsIcon } from '../MsIcon'
import type { PathFieldLikeProps } from './types'

export type ProjectOutputCardProps = {
  cardPaperSx: SxProps<Theme>
  PathFieldComponent: ComponentType<PathFieldLikeProps>
  outputDir: string
  onOutputDirChange: (value: string) => void
  onPickOutputDir: () => unknown | Promise<unknown>
  onOutputDrop: (value: string) => void
  onOpenOutputDirectory: () => unknown | Promise<unknown>
  onClearWorkCache: () => unknown | Promise<unknown>
}

export function ProjectOutputCard({
  cardPaperSx,
  PathFieldComponent,
  outputDir,
  onOutputDirChange,
  onPickOutputDir,
  onOutputDrop,
  onOpenOutputDirectory,
  onClearWorkCache,
}: ProjectOutputCardProps) {
  return (
    <Paper sx={cardPaperSx}>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="subtitle1" fontWeight={600}>
          项目与输出
        </Typography>
      </Stack>
      <Box sx={{ mt: 2 }}>
        <PathFieldComponent
          label="输出目录"
          value={outputDir}
          onChange={onOutputDirChange}
          onPick={() => {
            void onPickOutputDir()
          }}
          onDropPath={onOutputDrop}
        />
      </Box>
      <Stack
        direction={{ xs: 'column', md: 'row' }}
        spacing={1}
        alignItems={{ xs: 'flex-start', md: 'center' }}
        sx={{ mt: 1 }}
      >
        <Button
          variant="contained"
          startIcon={<MsIcon name="folder_open" size={18} />}
          onClick={() => {
            void onOpenOutputDirectory()
          }}
        >
          打开输出目录
        </Button>
        <Tooltip title="缓存目录为 <输出目录>/work" arrow>
          <Button
            variant="contained"
            startIcon={<MsIcon name="delete" size={18} />}
            onClick={() => {
              void onClearWorkCache()
            }}
          >
            清除工作缓存
          </Button>
        </Tooltip>
      </Stack>
    </Paper>
  )
}
