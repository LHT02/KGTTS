export type PathFieldLikeProps = {
  label: string
  value: string
  onChange: (value: string) => void
  onPick: () => void
  onDropPath?: (value: string) => void
  onDropFiles?: (files: File[]) => Promise<string>
  helperText?: string
  placeholder?: string
}
