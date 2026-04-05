"""
Modelo OpenAI con retry automático ante rate-limit (429).
"""
import time
from openai import OpenAI, RateLimitError, APIError
from typing import List, Dict


class OpenAIModel:
    """Wrapper para OpenAI con backoff automático en 429."""

    def __init__(self, api_key: str, model_name: str):
        self.client = OpenAI(api_key=api_key)
        self.model_name = model_name
        self.timeout = 30

    def ask(self, prompt: str, context: List[Dict] = None) -> str:
        """
        Envía el prompt al modelo. El prompt principal va como mensaje 'system'
        y el contexto conversacional previo se adjunta como historial.
        """
        messages = [{"role": "system", "content": prompt}]

        if context:
            for msg in context:
                role = msg.get("role", "user")
                # OpenAI solo acepta 'user' o 'assistant'
                if role not in ("user", "assistant"):
                    role = "user"
                messages.append({"role": role, "content": msg["content"]})

        max_retries = 4
        for attempt in range(max_retries):
            try:
                response = self.client.chat.completions.create(
                    model=self.model_name,
                    messages=messages,
                    timeout=self.timeout,
                )
                return response.choices[0].message.content.strip()

            except RateLimitError as e:
                wait_seconds = min(30.0, (attempt + 1) * 10)
                if attempt < max_retries - 1:
                    print(f"⏳ Rate limit alcanzado. Esperando {wait_seconds}s "
                          f"(intento {attempt + 1}/{max_retries - 1})...")
                    time.sleep(wait_seconds)
                    continue
                raise

            except APIError as e:
                raise

        raise RuntimeError("Se agotaron los reintentos por rate limit de la API de OpenAI.")
