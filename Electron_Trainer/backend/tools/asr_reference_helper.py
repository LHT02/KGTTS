import json
import sys
import traceback
from pathlib import Path
from typing import Any


BACKEND_ROOT = Path(__file__).resolve().parents[1]
if str(BACKEND_ROOT) not in sys.path:
    sys.path.insert(0, str(BACKEND_ROOT))

from engine import asr  # noqa: E402


def _emit(payload: dict[str, Any]) -> None:
    print(json.dumps(payload, ensure_ascii=False), flush=True)


def main(argv: list[str]) -> int:
    if len(argv) != 2:
        _emit({"type": "error", "message": "参考音频转写参数不完整。"})
        return 2
    try:
        import numpy as np
        import soundfile as sf

        request = json.loads(Path(argv[1]).read_text(encoding="utf-8"))
        audio_path = Path(str(request["reference_audio"]))
        model_zip = Path(str(request["asr_model_zip"]))
        if not audio_path.exists():
            raise RuntimeError(f"参考音频不存在: {audio_path}")
        if not model_zip.exists():
            raise RuntimeError(f"语音识别模型不存在: {model_zip}")

        engine = asr.OfflineASR(model_zip, device="cpu")
        audio, sample_rate = sf.read(audio_path, always_2d=False)
        if getattr(audio, "ndim", 1) > 1:
            audio = np.asarray(audio).mean(axis=1)
        text, score = engine.transcribe(np.asarray(audio, dtype=np.float32), int(sample_rate))
        text = text.strip()
        if not text:
            raise RuntimeError("参考音频 ASR 未识别到有效文本，请手动填写参考文本。")
        _emit({"type": "done", "text": text, "score": float(score)})
        return 0
    except Exception as exc:
        _emit({"type": "error", "message": str(exc) or exc.__class__.__name__, "traceback": traceback.format_exc()})
        return 1


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
