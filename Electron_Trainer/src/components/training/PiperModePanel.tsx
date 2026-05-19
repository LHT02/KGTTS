import {
  Box,
  IconButton,
  List,
  ListItem,
  ListItemText,
  Paper,
  Stack,
  Tooltip,
  Typography,
} from '@mui/material'
import type { SxProps, Theme } from '@mui/material'
import type { DragEvent as ReactDragEvent } from 'react'
import { MsIcon } from '../MsIcon'

export type PiperModePanelProps = {
  cardPaperSx: SxProps<Theme>
  audioFiles: string[]
  audioDragActive: boolean
  onAudioDragActiveChange: (active: boolean) => void
  onPickAudioFiles: () => void
  onClearAudioFiles: () => void
  onAudioDrop: (event: ReactDragEvent<HTMLDivElement>) => void
  onRemoveAudioFile: (index: number) => void
}

export function PiperModePanel({
  cardPaperSx,
  audioFiles,
  audioDragActive,
  onAudioDragActiveChange,
  onPickAudioFiles,
  onClearAudioFiles,
  onAudioDrop,
  onRemoveAudioFile,
}: PiperModePanelProps) {
  return (
    <Paper sx={cardPaperSx}>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="subtitle1" fontWeight={600}>
          音频导入
        </Typography>
        <Stack direction="row" spacing={1}>
          <Tooltip title="添加音频" arrow>
            <IconButton size="small" onClick={onPickAudioFiles}>
              <MsIcon name="add" size={20} />
            </IconButton>
          </Tooltip>
          <Tooltip title="清空音频" arrow>
            <IconButton size="small" onClick={onClearAudioFiles}>
              <MsIcon name="delete" size={20} />
            </IconButton>
          </Tooltip>
        </Stack>
      </Stack>
      <Box
        sx={{
          mt: 1,
          border: '1px dashed',
          borderColor: audioDragActive ? 'primary.main' : 'transparent',
          borderRadius: 1,
          p: 1,
          transition: 'border-color 0.15s ease',
        }}
        onDragOver={(event) => {
          event.preventDefault()
          if (event.dataTransfer) {
            event.dataTransfer.dropEffect = 'copy'
          }
          onAudioDragActiveChange(true)
        }}
        onDragLeave={() => onAudioDragActiveChange(false)}
        onDrop={onAudioDrop}
      >
        <List dense sx={{ maxHeight: 240, overflow: 'auto' }}>
          {audioFiles.length === 0 && (
            <ListItem>
              <ListItemText primary="暂无音频文件" secondary="点击添加或拖拽导入" />
            </ListItem>
          )}
          {audioFiles.map((file, index) => (
            <ListItem
              key={`${file}-${index}`}
              divider
              secondaryAction={
                <IconButton edge="end" size="small" onClick={() => onRemoveAudioFile(index)}>
                  <MsIcon name="close" size={18} />
                </IconButton>
              }
            >
              <ListItemText primary={file} />
            </ListItem>
          ))}
        </List>
      </Box>
    </Paper>
  )
}
