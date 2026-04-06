"""
Modelo Gemini con retry automático ante rate-limit (429).
"""
import time
import re
import google.generativeai as genai
from typing import List, Dict


class GeminiModel:
    """Wrapper para Gemini con backoff automático en 429."""

    def __init__(self, api_key: str, model_name: str):
        genai.configure(api_key=api_key)
        self.model = genai.GenerativeModel(
            model_name,
            generation_config=genai.GenerationConfig(
                candidate_count=1,
            )
        )
        self._request_options = {"timeout": 30}

    def ask(self, prompt: str, context: List[Dict] = None) -> str:
        """
        Pregunta al modelo. Reintenta automáticamente si la API devuelve 429,
        respetando el retry_delay que indica la respuesta de Google.
        """
        if context:
            full_prompt = self._build_with_context(prompt, context)
        else:
            full_prompt = prompt

        max_retries = 4
        for attempt in range(max_retries):
            try:
                response = self.model.generate_content(
                    full_prompt,
                    request_options=self._request_options
                )
                return response.text.strip()

            except Exception as e:
                error_str = str(e)

                # Detectar 429 (rate limit) y extraer el tiempo de espera sugerido
                if "429" in error_str or "quota" in error_str.lower():
                    # Si la cuota es 0 (key sin free tier), no tiene sentido reintentar
                    if "limit: 0" in error_str:
                        raise RuntimeError(
                            "La API key de Gemini no tiene cuota disponible (limit: 0). "
                            "Genera una nueva key en aistudio.google.com."
                        )
                    wait_seconds = self._parse_retry_delay(error_str)
                    # Cap de 15s para no bloquear demasiado tiempo
                    wait_seconds = min(wait_seconds, 15.0)
                    if attempt < max_retries - 1:
                        print(f"⏳ Rate limit alcanzado. Esperando {wait_seconds}s antes de reintentar "
                              f"(intento {attempt + 1}/{max_retries - 1})...")
                        time.sleep(wait_seconds)
                        continue

                raise  # Re-lanzar si no es 429 o agotamos reintentos

        raise RuntimeError("Se agotaron los reintentos por rate limit de la API de Gemini.")

    def _parse_retry_delay(self, error_str: str) -> float:
        """Extrae el tiempo de espera sugerido del mensaje de error de Google."""
        match = re.search(r'retry_delay\s*\{\s*seconds:\s*(\d+)', error_str)
        if match:
            return float(match.group(1)) + 2  # +2s de margen
        return 35.0  # fallback conservador

    def _build_with_context(self, prompt: str, context: List[Dict]) -> str:
        """Construye el prompt incluyendo el historial de conversación."""
        history = "\n".join([
            f"{'Usuario' if m['role'] == 'user' else 'Asistente'}: {m['content']}"
            for m in context
        ])
        return f"""HISTORIAL DE LA CONVERSACIÓN PREVIA:
{history}

TAREA ACTUAL (basada en la conversación previa y la última pregunta del usuario):
{prompt}
"""
