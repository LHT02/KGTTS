import { Box } from '@mui/material'

export type MsIconProps = {
  name: string
  size?: number
  fill?: 0 | 1
  wght?: number
  grad?: number
  opsz?: number
}

export const MsIcon = ({
  name,
  size = 20,
  fill = 0,
  wght = 500,
  grad = 0,
  opsz = 24,
}: MsIconProps) => (
  <Box
    component="span"
    className="material-symbols-sharp"
    sx={{
      fontSize: size,
      lineHeight: 1,
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      userSelect: 'none',
      fontVariationSettings: `'FILL' ${fill}, 'wght' ${wght}, 'GRAD' ${grad}, 'opsz' ${opsz}`,
    }}
    aria-hidden
  >
    {name}
  </Box>
)
