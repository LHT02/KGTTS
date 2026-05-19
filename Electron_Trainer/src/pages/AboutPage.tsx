import { Avatar, Box, Button, IconButton, Link, Paper, Stack, Tooltip, Typography } from '@mui/material'
import type { SxProps, Theme } from '@mui/material'
import logoBlack from '../../../ARTS/LOGOBlack.svg'
import logoWhite from '../../../ARTS/LOGOWhite.svg'
import avatarHuajiang from '../../../ARTS/Avatar/huajiang.jpg'
import avatarLht from '../../../ARTS/Avatar/LHT.jpg'
import avatarYuiLu from '../../../ARTS/Avatar/YuiLu.jpg'
import { MsIcon } from '../components/MsIcon'

type AboutCreator = {
  name: string
  homepage: string
  avatar: string
}

const ABOUT_CREATORS: AboutCreator[] = [
  {
    name: 'LHT',
    homepage: 'https://space.bilibili.com/87244951',
    avatar: avatarLht,
  },
  {
    name: 'Yui Lu',
    homepage: 'https://space.bilibili.com/23208863',
    avatar: avatarYuiLu,
  },
  {
    name: '花酱',
    homepage: 'https://space.bilibili.com/573842321',
    avatar: avatarHuajiang,
  },
]

export type AboutPageProps = {
  appVersion: string
  cardPaperSx: SxProps<Theme>
  resolvedThemeMode: 'light' | 'dark'
  onOpenExternal: (url: string, label: string) => void | Promise<void>
  onOpenLegal: (kind: 'openSource' | 'privacy') => void
}

export function AboutPage({
  appVersion,
  cardPaperSx,
  resolvedThemeMode,
  onOpenExternal,
  onOpenLegal,
}: AboutPageProps) {
  return (
    <Stack spacing={2}>
      <Paper
        sx={{
          ...cardPaperSx,
          p: { xs: 3, md: 4 },
          background:
            resolvedThemeMode === 'dark'
              ? 'linear-gradient(135deg, rgba(3,131,135,0.18), rgba(11,15,20,0.92))'
              : 'linear-gradient(135deg, rgba(3,131,135,0.12), rgba(255,255,255,0.98))',
        }}
      >
        <Stack spacing={2} alignItems="center" textAlign="center">
          <Box
            component="span"
            role="img"
            aria-label="KIGTTS"
            sx={{
              position: 'relative',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              height: { xs: 48, md: 62 },
              width: { xs: 220, md: 280 },
              maxWidth: '100%',
            }}
          >
            <Box
              component="img"
              src={logoBlack}
              alt=""
              aria-hidden
              sx={{
                position: 'absolute',
                inset: 0,
                width: '100%',
                height: '100%',
                objectFit: 'contain',
                opacity: resolvedThemeMode === 'dark' ? 0 : 1,
                filter: resolvedThemeMode === 'dark' ? 'blur(1px)' : 'blur(0px)',
                transition: 'opacity 220ms ease, filter 220ms ease',
              }}
            />
            <Box
              component="img"
              src={logoWhite}
              alt=""
              aria-hidden
              sx={{
                position: 'absolute',
                inset: 0,
                width: '100%',
                height: '100%',
                objectFit: 'contain',
                opacity: resolvedThemeMode === 'dark' ? 1 : 0,
                filter: resolvedThemeMode === 'dark' ? 'blur(0px)' : 'blur(1px)',
                transition: 'opacity 220ms ease, filter 220ms ease',
              }}
            />
          </Box>
          <Typography variant="body2" sx={{ opacity: 0.72, letterSpacing: 0.5 }}>
            Version {appVersion}
          </Typography>
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1.5}>
          <Typography variant="subtitle1" fontWeight={600}>
            软件制作
          </Typography>
          <Box
            sx={{
              display: 'grid',
              gap: 1.5,
              gridTemplateColumns: { xs: '1fr', md: 'repeat(3, minmax(0, 1fr))' },
            }}
          >
            {ABOUT_CREATORS.map((creator) => (
              <Box
                key={creator.name}
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 1.5,
                  minWidth: 0,
                  py: 0.5,
                }}
              >
                <Avatar src={creator.avatar} alt={creator.name} sx={{ width: 64, height: 64 }} />
                <Stack spacing={0.35} sx={{ minWidth: 0, flex: 1 }}>
                  <Typography variant="body1" fontWeight={600} noWrap>
                    {creator.name}
                  </Typography>
                  <Stack direction="row" spacing={0.5} alignItems="center" sx={{ minWidth: 0 }}>
                    <Link
                      href={creator.homepage}
                      underline="none"
                      color="primary"
                      sx={{
                        flex: 1,
                        minWidth: 0,
                        px: 0.5,
                        py: 0.2,
                        borderRadius: 1,
                        fontSize: 12,
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap',
                        transition: 'background-color 120ms ease',
                        '&:hover': {
                          bgcolor: 'action.hover',
                          textDecoration: 'none',
                        },
                      }}
                      onClick={(event) => {
                        event.preventDefault()
                        void onOpenExternal(creator.homepage, `${creator.name} 主页`)
                      }}
                    >
                      {creator.homepage}
                    </Link>
                    <Tooltip title={`打开 ${creator.name} 主页`} arrow>
                      <IconButton
                        size="small"
                        sx={{ flexShrink: 0 }}
                        onClick={() => {
                          void onOpenExternal(creator.homepage, `${creator.name} 主页`)
                        }}
                      >
                        <MsIcon name="open_in_new" size={18} />
                      </IconButton>
                    </Tooltip>
                  </Stack>
                </Stack>
              </Box>
            ))}
          </Box>
        </Stack>
      </Paper>

      <Paper sx={cardPaperSx}>
        <Stack spacing={1.5}>
          <Typography variant="subtitle1" fontWeight={600}>
            关于
          </Typography>
          <Stack spacing={0.25} alignItems="flex-start">
            <Button
              variant="text"
              sx={{
                px: 0.5,
                py: 0.25,
                minWidth: 0,
                justifyContent: 'flex-start',
                borderRadius: 1,
                '&:hover': { bgcolor: 'action.hover' },
              }}
              onClick={() => onOpenLegal('openSource')}
            >
              开源许可证
            </Button>
            <Button
              variant="text"
              sx={{
                px: 0.5,
                py: 0.25,
                minWidth: 0,
                justifyContent: 'flex-start',
                borderRadius: 1,
                '&:hover': { bgcolor: 'action.hover' },
              }}
              onClick={() => onOpenLegal('privacy')}
            >
              隐私政策
            </Button>
          </Stack>
        </Stack>
      </Paper>
    </Stack>
  )
}
