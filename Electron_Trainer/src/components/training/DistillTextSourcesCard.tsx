import {
  Box,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Paper,
  Popover,
  Stack,
  Tooltip,
  Typography,
} from '@mui/material'
import type { DragEvent as ReactDragEvent } from 'react'
import type { SxProps, Theme } from '@mui/material'
import { MsIcon } from '../MsIcon'

export type DistillTextSourcesCardProps = {
  cardPaperSx: SxProps<Theme>
  sources: DistillTextSource[]
  dragActive: boolean
  emptyHint: string
  addAnchorEl: HTMLElement | null
  onAddAnchorChange: (anchor: HTMLElement | null) => void
  onClearSources: () => void
  onDragActiveChange: (active: boolean) => void
  onDrop: (event: ReactDragEvent<HTMLDivElement>) => void
  onOpenPresetDialog: () => void
  onPickTextFiles: () => unknown | Promise<unknown>
  onRemoveSource: (source: DistillTextSource) => void
  getSourcePrimaryText: (source: DistillTextSource) => string
  getSourceSecondaryText: (source: DistillTextSource) => string
}

export function DistillTextSourcesCard({
  cardPaperSx,
  sources,
  dragActive,
  emptyHint,
  addAnchorEl,
  onAddAnchorChange,
  onClearSources,
  onDragActiveChange,
  onDrop,
  onOpenPresetDialog,
  onPickTextFiles,
  onRemoveSource,
  getSourcePrimaryText,
  getSourceSecondaryText,
}: DistillTextSourcesCardProps) {
  return (
    <Paper sx={cardPaperSx}>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="subtitle1" fontWeight={600}>
          文本来源
        </Typography>
        <Stack direction="row" spacing={1}>
          <Tooltip title="添加文本来源" arrow>
            <IconButton size="small" onClick={(event) => onAddAnchorChange(event.currentTarget)}>
              <MsIcon name="add" size={20} />
            </IconButton>
          </Tooltip>
          <Tooltip title="清空文本来源" arrow>
            <IconButton size="small" onClick={onClearSources}>
              <MsIcon name="delete" size={20} />
            </IconButton>
          </Tooltip>
        </Stack>
      </Stack>
      <Box
        sx={{
          mt: 1,
          border: '1px dashed',
          borderColor: dragActive ? 'primary.main' : 'transparent',
          borderRadius: 1,
          p: 1,
          transition: 'border-color 0.15s ease',
        }}
        onDragOver={(event) => {
          event.preventDefault()
          if (event.dataTransfer) {
            event.dataTransfer.dropEffect = 'copy'
          }
          onDragActiveChange(true)
        }}
        onDragLeave={() => onDragActiveChange(false)}
        onDrop={onDrop}
      >
        <List dense sx={{ maxHeight: 240, overflow: 'auto' }}>
          {sources.length === 0 && (
            <ListItem>
              <ListItemText primary="暂无文本来源" secondary={emptyHint} />
            </ListItem>
          )}
          {sources.map((item) => (
            <ListItem
              key={`${item.kind}:${item.path}`}
              divider
              secondaryAction={
                <IconButton edge="end" size="small" onClick={() => onRemoveSource(item)}>
                  <MsIcon name="close" size={18} />
                </IconButton>
              }
            >
              <ListItemIcon sx={{ minWidth: 28 }}>
                <MsIcon name={item.kind === 'project_dir' ? 'folder' : 'article'} size={18} />
              </ListItemIcon>
              <ListItemText primary={getSourcePrimaryText(item)} secondary={getSourceSecondaryText(item)} />
            </ListItem>
          ))}
        </List>
      </Box>
      <Popover
        open={Boolean(addAnchorEl)}
        anchorEl={addAnchorEl}
        onClose={() => onAddAnchorChange(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <List dense sx={{ py: 0.5, minWidth: 180 }}>
          <ListItemButton onClick={onOpenPresetDialog}>
            <ListItemIcon sx={{ minWidth: 32 }}>
              <MsIcon name="library_books" size={18} />
            </ListItemIcon>
            <ListItemText primary="添加预设文件" secondary="内置 5 万 / 10 万 / 15 万字版本" />
          </ListItemButton>
          <ListItemButton
            onClick={() => {
              onAddAnchorChange(null)
              void onPickTextFiles()
            }}
          >
            <ListItemIcon sx={{ minWidth: 32 }}>
              <MsIcon name="article" size={18} />
            </ListItemIcon>
            <ListItemText primary="导入自定义文本文件" secondary=".txt / .csv / .jsonl" />
          </ListItemButton>
        </List>
      </Popover>
    </Paper>
  )
}
