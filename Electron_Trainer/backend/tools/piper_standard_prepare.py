import json
import sys
import traceback
from dataclasses import fields
from pathlib import Path
from typing import Any


BACKEND_ROOT = Path(__file__).resolve().parents[1]
if str(BACKEND_ROOT) not in sys.path:
    sys.path.insert(0, str(BACKEND_ROOT))

from engine import asr, preprocess, training, vad  # noqa: E402
from engine.config import ProjectPaths, TrainingOptions  # noqa: E402
from engine.text_normalization import DEFAULT_SENTENCE_PERIOD, ensure_sentence_ending  # noqa: E402


PATH_OPTION_FIELDS = {
    "asr_model_zip",
    "piper_base_checkpoint",
    "phonemizer_dict",
    "piper_config",
    "voicepack_avatar",
}


def _emit(payload: dict[str, Any]) -> None:
    print(json.dumps(payload, ensure_ascii=False), flush=True)


def _progress(stage: str, value: float, message: str) -> None:
    _emit({"type": "progress", "stage": stage, "value": value, "message": message})


def _training_options_from_payload(raw: dict[str, Any]) -> TrainingOptions:
    defaults = TrainingOptions()
    kwargs: dict[str, Any] = {}
    for field_info in fields(TrainingOptions):
        value = raw.get(field_info.name, getattr(defaults, field_info.name))
        if field_info.name in PATH_OPTION_FIELDS:
            kwargs[field_info.name] = Path(value) if value else None
        else:
            kwargs[field_info.name] = value
    return TrainingOptions(**kwargs)


def _project_paths_from_payload(raw: dict[str, Any]) -> ProjectPaths:
    project_root = Path(str(raw["project_root"]))
    input_audio = [Path(str(item)) for item in raw.get("input_audio", [])]
    return ProjectPaths(project_root=project_root, input_audio=input_audio)


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        _emit({"type": "error", "message": "素材准备参数不完整。"})
        return 2

    request_path = Path(argv[1])
    try:
        request = json.loads(request_path.read_text(encoding="utf-8"))
        paths = _project_paths_from_payload(request["paths"])
        opts = _training_options_from_payload(request["training_options"])

        processed = preprocess.preprocess_audios(
            paths.input_audio,
            paths.work_dir / "processed",
            opts,
            _progress,
        )
        segments = vad.vad_split(processed, paths.segments_dir, opts, _progress)
        transcripts = asr.transcribe_segments(segments, opts, _progress)
        if getattr(opts, "normalize_text_append_period", True):
            period = getattr(opts, "text_normalization_period", DEFAULT_SENTENCE_PERIOD) or DEFAULT_SENTENCE_PERIOD
            for item in transcripts:
                item.text = ensure_sentence_ending(item.text, period)

        training.write_metadata(transcripts, paths.training_manifest)
        _emit(
            {
                "type": "done",
                "stage": "asr",
                "value": 1.0,
                "message": f"素材准备完成，共 {len(transcripts)} 条",
                "metadata_path": str(paths.training_manifest),
                "count": len(transcripts),
            }
        )
        return 0
    except Exception as exc:
        _emit(
            {
                "type": "error",
                "message": str(exc) or exc.__class__.__name__,
                "traceback": traceback.format_exc(),
            }
        )
        return 1


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
