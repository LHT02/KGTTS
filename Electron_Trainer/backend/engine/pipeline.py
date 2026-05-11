import json
import os
import shutil
import subprocess
from pathlib import Path
from typing import Any, Optional

from . import packager, training
from .audio_validation import is_audio_file_usable, split_usable_audio_entries
from .config import DistillOptions, PipelineResult, ProgressCallback, ProjectPaths, TrainingOptions, VoxCpmDistillOptions
from .project_state import (
    archive_input_audio,
    archive_voicepack_avatar,
    archive_voxcpm_reference,
    distill_options_from_dict,
    load_project_config,
    read_saved_texts_jsonl,
    read_metadata_entries,
    save_project_config,
    training_options_from_dict,
    voxcpm_options_from_dict,
    write_metadata_entries,
)
from .text_normalization import DEFAULT_SENTENCE_PERIOD, ensure_sentence_ending
from .utils import find_executable
from .runtime_manager import describe_piper_runtime


def _ensure_dirs(paths: ProjectPaths) -> None:
    paths.work_dir.mkdir(parents=True, exist_ok=True)
    paths.segments_dir.mkdir(parents=True, exist_ok=True)
    paths.export_dir.mkdir(parents=True, exist_ok=True)


PATH_OPTION_FIELDS = {
    "asr_model_zip",
    "piper_base_checkpoint",
    "phonemizer_dict",
    "piper_config",
    "voicepack_avatar",
}


def _serialize_training_options(opts: TrainingOptions) -> dict[str, Any]:
    payload: dict[str, Any] = {}
    for name in TrainingOptions.__dataclass_fields__:
        value = getattr(opts, name)
        if name in PATH_OPTION_FIELDS:
            payload[name] = str(value) if value else None
        else:
            payload[name] = value
    return payload


def _tail_text(path: Path, *, max_chars: int = 4000) -> str:
    try:
        text = path.read_text(encoding="utf-8", errors="replace")
    except Exception:
        return ""
    text = text.strip()
    if len(text) > max_chars:
        text = text[-max_chars:]
    return text


def _run_piper_standard_prepare(
    paths: ProjectPaths,
    opts: TrainingOptions,
    progress: Optional[ProgressCallback] = None,
) -> None:
    status = describe_piper_runtime()
    if not status.get("available"):
        message = str(status.get("message") or "Piper 基础运行时不可用。")
        raise RuntimeError(f"{message} 请先在“依赖准备”中安装或重新安装 Piper 基础运行时。")

    piper_python = training._find_piper_python(prefer_cuda=False)  # type: ignore[attr-defined]
    if not piper_python:
        raise RuntimeError("Piper 基础运行时未安装。请先在“依赖准备”中安装 Piper 基础运行时。")

    backend_root = Path(__file__).resolve().parents[1]
    helper = backend_root / "tools" / "piper_standard_prepare.py"
    if not helper.exists():
        raise RuntimeError("普通模式素材准备组件缺失，请重新安装 KIGTTS Trainer 后再试。")

    request_path = paths.work_dir / "piper_standard_prepare_request.json"
    log_path = paths.work_dir / "prepare.log"
    request = {
        "paths": {
            "project_root": str(paths.project_root),
            "input_audio": [str(path) for path in paths.input_audio],
        },
        "training_options": _serialize_training_options(opts),
    }
    request_path.write_text(json.dumps(request, ensure_ascii=False, indent=2), encoding="utf-8")

    env = training._piper_env(piper_python)  # type: ignore[attr-defined]
    env["PYTHONPATH"] = str(backend_root)
    env["PYTHONUNBUFFERED"] = "1"
    env["PYTHONIOENCODING"] = "utf-8"
    env["PYTHONUTF8"] = "1"

    creationflags = subprocess.CREATE_NO_WINDOW if os.name == "nt" else 0
    if progress:
        progress("preprocess", 0.0, "正在准备录音素材...")
    proc = subprocess.Popen(
        [str(piper_python), "-u", str(helper), str(request_path)],
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        stdin=subprocess.DEVNULL,
        text=True,
        encoding="utf-8",
        errors="replace",
        env=env,
        cwd=str(backend_root),
        creationflags=creationflags,
        close_fds=True,
    )
    error_message = ""
    with log_path.open("w", encoding="utf-8") as log_file:
        log_file.write(f"piper_python={piper_python}\n")
        log_file.write(f"helper={helper}\n")
        log_file.write(f"pid={proc.pid}\n")
        log_file.flush()
        if proc.stdout is not None:
            for raw_line in proc.stdout:
                log_file.write(raw_line)
                log_file.flush()
                line = raw_line.strip()
                if not line:
                    continue
                try:
                    event = json.loads(line)
                except Exception:
                    continue
                event_type = event.get("type")
                if event_type == "progress" and progress:
                    progress(
                        str(event.get("stage") or "preprocess"),
                        float(event.get("value") or 0.0),
                        str(event.get("message") or ""),
                    )
                elif event_type == "done" and progress:
                    progress(
                        str(event.get("stage") or "asr"),
                        float(event.get("value") or 1.0),
                        str(event.get("message") or "素材准备完成"),
                    )
                elif event_type == "error":
                    error_message = str(event.get("message") or "")
                    traceback_text = str(event.get("traceback") or "")
                    if traceback_text:
                        log_file.write(traceback_text)
                        log_file.flush()

    return_code = proc.wait()
    if return_code != 0:
        detail = error_message or _tail_text(log_path)
        raise RuntimeError(f"普通模式素材准备失败，详见 {log_path}\n{detail}".strip())
    if not paths.training_manifest.exists():
        raise RuntimeError(f"素材准备完成但没有生成训练清单：{paths.training_manifest}")


def _expected_generated_entries(paths: ProjectPaths, mode: str, texts: list[str]) -> list[tuple[Path, str]]:
    if mode == "gsv_distill":
        wav_dir = paths.work_dir / "distill_corpus" / "wavs"
    elif mode == "voxcpm_distill":
        wav_dir = paths.work_dir / "voxcpm_corpus" / "wavs"
    else:
        return []
    return [(wav_dir / f"{index:05d}.wav", text) for index, text in enumerate(texts, start=1)]


def _load_saved_expected_texts(paths: ProjectPaths, mode: str, config: dict[str, object]) -> list[str]:
    raw_expected = config.get("metadata_texts") or []
    if isinstance(raw_expected, list):
        texts = [str(item).strip() for item in raw_expected if str(item).strip()]
        if texts:
            return texts
    if mode == "gsv_distill":
        return read_saved_texts_jsonl(paths.work_dir / "distill_corpus" / "texts.jsonl")
    if mode == "voxcpm_distill":
        return read_saved_texts_jsonl(paths.work_dir / "voxcpm_corpus" / "texts.jsonl")
    return []


def synth_preview(model_dir: Path, opts: TrainingOptions) -> Optional[Path]:
    """Generate preview.wav using piper CLI if available."""
    piper_bin: Optional[str] = None
    base_dir = os.environ.get("KGTTS_BASE_DIR")
    candidate_paths = []
    if base_dir:
        base = Path(base_dir)
        candidate_paths.extend(
            [
                base / "piper_env" / "Scripts" / "piper.exe",
                base / "resources_pack" / "piper_env" / "Scripts" / "piper.exe",
            ]
        )
    for cand in candidate_paths:
        if cand.exists():
            piper_bin = str(cand)
            break
    # Do not use global PATH piper to avoid matching unrelated binaries.
    preview_path = model_dir / "preview.wav"
    if not piper_bin:
        return None
    cmd = [
        piper_bin,
        "--model", str(model_dir / "model.onnx"),
        "--config", str(model_dir / "model.onnx.json"),
        "--output_file", str(preview_path),
        "--text", opts.text_sample,
    ]
    try:
        proc = subprocess.run(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            timeout=30,
        )
    except subprocess.TimeoutExpired as exc:
        log = model_dir / "preview.log"
        log.write_text(str(exc), encoding="utf-8")
        return None
    if proc.returncode != 0:
        log = model_dir / "preview.log"
        log.write_text(proc.stdout, encoding="utf-8")
        return None
    return preview_path


def _train_export_package(
    paths: ProjectPaths,
    opts: TrainingOptions,
    progress: Optional[ProgressCallback] = None,
) -> PipelineResult:
    training.write_preview_text(opts.text_sample, paths.work_dir / "preview.txt")
    ckpt = training.run_piper_training(paths.training_manifest, paths.work_dir, opts, progress)
    if progress:
        progress("export", 0.0, "训练完成，准备导出 ONNX")
    model_dir = paths.work_dir / "onnx"
    model_dir.mkdir(exist_ok=True, parents=True)
    training.export_onnx(ckpt, model_dir, opts, progress)
    if progress:
        progress("export", 0.7, "ONNX 导出完成，准备生成预览")
    preview_path = synth_preview(model_dir, opts)
    if progress:
        progress("export", 0.85, "预览处理完成，正在打包语音包")

    voicepack_zip = packager.package_voicepack(
        model_dir=model_dir,
        out_zip=paths.voicepack_path,
        opts=opts,
        preview=preview_path,
        phonemizer_dict=opts.phonemizer_dict,
    )
    if progress:
        progress("export", 1.0, "导出与打包完成")
    return PipelineResult(
        manifest_path=model_dir / "manifest.json",
        voicepack_path=voicepack_zip,
        preview_path=preview_path,
        training_log=paths.work_dir / "training.log",
    )


def run_pipeline(
    paths: ProjectPaths,
    opts: TrainingOptions,
    progress: Optional[ProgressCallback] = None,
) -> PipelineResult:
    _ensure_dirs(paths)
    archive_input_audio(paths)
    archive_voicepack_avatar(paths, opts)
    save_project_config(paths, "piper", opts)

    _run_piper_standard_prepare(paths, opts, progress)
    save_project_config(paths, "piper", opts)
    return _train_export_package(paths, opts, progress)


def run_distill_pipeline(
    paths: ProjectPaths,
    opts: TrainingOptions,
    distill_opts: DistillOptions,
    progress: Optional[ProgressCallback] = None,
) -> PipelineResult:
    from . import gsv_distill

    _ensure_dirs(paths)
    archive_voicepack_avatar(paths, opts)
    texts = gsv_distill.collect_distill_texts(
        distill_opts,
        paths.work_dir,
        progress,
        getattr(opts, "normalize_text_append_period", True),
        getattr(opts, "text_normalization_period", DEFAULT_SENTENCE_PERIOD) or DEFAULT_SENTENCE_PERIOD,
    )
    save_project_config(paths, "gsv_distill", opts, distill_opts=distill_opts, metadata_texts=texts)
    gsv_distill.build_distill_corpus(paths, distill_opts, progress, texts=texts)
    return _train_export_package(paths, opts, progress)


def run_voxcpm_distill_pipeline(
    paths: ProjectPaths,
    opts: TrainingOptions,
    voxcpm_opts: VoxCpmDistillOptions,
    progress: Optional[ProgressCallback] = None,
) -> PipelineResult:
    from . import gsv_distill, voxcpm_distill

    _ensure_dirs(paths)

    archive_voicepack_avatar(paths, opts)
    archive_voxcpm_reference(paths, voxcpm_opts)
    texts = gsv_distill.collect_distill_texts(  # type: ignore[arg-type]
        voxcpm_opts,
        paths.work_dir,
        progress,
        getattr(opts, "normalize_text_append_period", True),
        getattr(opts, "text_normalization_period", DEFAULT_SENTENCE_PERIOD) or DEFAULT_SENTENCE_PERIOD,
    )
    save_project_config(paths, "voxcpm_distill", opts, voxcpm_opts=voxcpm_opts, metadata_texts=texts)
    voxcpm_distill.build_voxcpm_corpus(paths, voxcpm_opts, opts, progress, texts=texts)
    return _train_export_package(paths, opts, progress)


def run_resume_project_pipeline(
    paths: ProjectPaths,
    progress: Optional[ProgressCallback] = None,
) -> PipelineResult:
    _ensure_dirs(paths)
    config = load_project_config(paths.project_root)
    mode = str(config.get("mode") or "").strip()
    opts = training_options_from_dict(config.get("training_options") or {})
    archive_voicepack_avatar(paths, opts)

    def save_resume_snapshot() -> None:
        try:
            if mode == "gsv_distill":
                save_project_config(
                    paths,
                    mode,
                    opts,
                    distill_opts=distill_options_from_dict(config.get("distill_options") or {}),
                )
            elif mode == "voxcpm_distill":
                save_project_config(
                    paths,
                    mode,
                    opts,
                    voxcpm_opts=voxcpm_options_from_dict(config.get("voxcpm_options") or {}),
                )
            elif mode == "piper":
                save_project_config(paths, mode, opts)
        except Exception:
            # Snapshot refresh is best-effort; continuing an existing project
            # should not fail only because an optional saved config is stale.
            pass

    expected_texts = _load_saved_expected_texts(paths, mode, config)
    try:
        entries = read_metadata_entries(paths.training_manifest)
        metadata_error = ""
    except Exception as exc:
        entries = []
        metadata_error = str(exc)
    metadata_texts = [text for _audio, text in entries]
    metadata_inconsistent = bool(expected_texts and expected_texts != metadata_texts)
    if metadata_inconsistent and progress:
        progress("collect", 0.0, "项目中的文本记录与上次保存的设置不一致，将按当前文本记录继续。")

    if mode in {"gsv_distill", "voxcpm_distill"} and expected_texts:
        generated_entries = _expected_generated_entries(paths, mode, expected_texts)
        if not entries or metadata_inconsistent or len(entries) != len(expected_texts):
            entries = generated_entries
            metadata_texts = [text for _audio, text in entries]
            metadata_inconsistent = False

    existing_entries, missing_entries = split_usable_audio_entries(entries)
    if entries and not metadata_inconsistent and not missing_entries and len(existing_entries) == len(entries):
        if progress:
            progress("collect", 1.0, f"旧项目音频完整，直接进入训练，共 {len(entries)} 条")
        save_resume_snapshot()
        return _train_export_package(paths, opts, progress)

    if not entries and mode != "piper":
        raise RuntimeError(metadata_error or "旧项目没有可用于恢复的训练文本。")

    if progress:
        progress("collect", 0.5, f"旧项目检测到 {len(missing_entries)} 条音频缺失或损坏")

    if mode == "voxcpm_distill":
        from . import voxcpm_distill

        voxcpm_opts = voxcpm_options_from_dict(config.get("voxcpm_options") or {})
        voxcpm_distill.generate_voxcpm_entries(paths, voxcpm_opts, opts, missing_entries, progress)
        still_missing = [(audio_path, text) for audio_path, text in entries if not is_audio_file_usable(audio_path)]
        if still_missing:
            raise RuntimeError(f"VoxCPM2 补生成后仍有 {len(still_missing)} 条音频缺失或损坏，无法继续训练。")
        if progress:
            progress("collect", 1.0, f"旧项目音频已补齐，共 {len(entries)} 条")
        save_resume_snapshot()
        return _train_export_package(paths, opts, progress)

    if mode == "gsv_distill":
        from . import gsv_distill

        distill_opts = distill_options_from_dict(config.get("distill_options") or {})
        try:
            gsv_distill.generate_distill_entries(paths, distill_opts, missing_entries, progress)
        except Exception as exc:
            if not existing_entries:
                raise RuntimeError(f"GPT-SoVITS 音频完全缺失，且无法按项目配置补生成：{exc}") from exc
            write_metadata_entries(existing_entries, paths.training_manifest)
            if progress:
                progress(
                    "collect",
                    1.0,
                    f"GPT-SoVITS 模型不可用或补生成失败，已移除 {len(missing_entries)} 条缺失或损坏文本，继续训练 {len(existing_entries)} 条。",
                )
            save_project_config(paths, mode, opts, distill_opts=distill_opts)
            return _train_export_package(paths, opts, progress)
        still_missing = [(audio_path, text) for audio_path, text in entries if not is_audio_file_usable(audio_path)]
        if still_missing:
            existing_entries = [(audio_path, text) for audio_path, text in entries if is_audio_file_usable(audio_path)]
            if not existing_entries:
                raise RuntimeError("GPT-SoVITS 音频完全缺失，且补生成后仍没有可训练音频。")
            write_metadata_entries(existing_entries, paths.training_manifest)
            if progress:
                progress("collect", 1.0, f"已移除 {len(still_missing)} 条仍缺失或损坏文本，继续训练 {len(existing_entries)} 条。")
        else:
            if progress:
                progress("collect", 1.0, f"旧项目音频已补齐，共 {len(entries)} 条")
        save_project_config(paths, mode, opts, distill_opts=distill_opts)
        return _train_export_package(paths, opts, progress)

    if mode == "piper":
        needs_rerun = bool(metadata_error or metadata_inconsistent or missing_entries or not entries)
        if needs_rerun:
            stored_audio = [Path(str(item)) for item in (config.get("input_audio") or []) if str(item).strip()]
            stored_audio = [path for path in stored_audio if path.exists()]
            if not stored_audio:
                reason = metadata_error or f"缺失或损坏 {len(missing_entries)} 条音频"
                raise RuntimeError(f"标准训练旧项目需要重新处理，但项目配置中没有可用原始音频：{reason}")
            if progress:
                progress("collect", 0.0, "旧项目素材不完整，将使用项目保存的原始录音重新准备训练素材。")
            paths.input_audio = stored_audio
            return run_pipeline(paths, opts, progress)
        return _train_export_package(paths, opts, progress)

    raise RuntimeError(f"不支持的旧项目训练模式: {mode or '未知'}")
