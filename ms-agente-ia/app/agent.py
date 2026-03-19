"""
Agente MCP - Model Context Protocol
Soporta SQLite, MySQL y Multi-Datasource (MySQL + PostgreSQL)
"""
from typing import List, Dict, Any, Optional
from app.models.gemini import GeminiModel
from app.tools.database import DatabaseTool
from app.tools.mysql_tool import MySQLTool
from app.tools.postgres_tool import PostgresTool
import json
from decimal import Decimal
from datetime import date, datetime, timedelta
import hashlib
import difflib


class CustomDecimalEncoder(json.JSONEncoder):
    """
    Codificador de JSON personalizado para manejar objetos Decimal y Date/Datetime.
    """
    def default(self, obj):
        if isinstance(obj, Decimal):
            return float(obj)
        if isinstance(obj, (date, datetime)):
            return obj.isoformat()
        return super().default(obj)


class MCPAgent:
    """
    Agente con arquitectura MCP simplificada.
    Soporta SQLite, MySQL y Multi-Datasource (MySQL + PostgreSQL).
    """

    def __init__(
        self,
        api_key: str,
        model_name: str,
        db_type: str = 'sqlite',
        db_path: Optional[str] = None,
        mysql_config: Optional[Dict] = None,
        postgres_config: Optional[Dict] = None
    ):
        # Modelo de IA
        self.model = GeminiModel(api_key, model_name)

        # Herramientas disponibles
        self.tools = {}
        self.db_type = db_type

        # Configurar la herramienta de base de datos según el tipo
        if db_type == 'sqlite':
            if not db_path:
                raise ValueError("db_path es requerido para SQLite")
            self.tools["database"] = DatabaseTool(db_path)
            print(f"📊 Usando SQLite: {db_path}")

        elif db_type == 'mysql':
            if not mysql_config:
                raise ValueError("mysql_config es requerido para MySQL")
            self.tools["database"] = MySQLTool(**mysql_config)
            print(f"📊 Usando MySQL: {mysql_config['database']}")

        elif db_type == 'multi':
            if not mysql_config or not postgres_config:
                raise ValueError("mysql_config y postgres_config son obligatorios para multi-datasource")
            self.tools["inventario_db"] = MySQLTool(**mysql_config)
            self.tools["ventas_db"] = PostgresTool(**postgres_config)
            print("📊 Usando MULTI-DATASOURCE (Inventario en MySQL + Ventas en PostgreSQL)")

        else:
            raise ValueError(f"Tipo de BD no soportado: {db_type}")

        # Contexto de la conversación
        self.context: List[Dict[str, str]] = []
        self.max_context = 10

        # Opciones de Caché para ahorrar Tokens
        self.query_cache: Dict[str, Dict[str, Any]] = {}
        self.cache_ttl_minutes = 5

        # --- RAG: Conocimiento Previo de Nombres de Productos ---
        self.product_names: List[str] = []
        self._load_product_names()

    def _load_product_names(self):
        """Carga los nombres de productos al iniciar para hacer búsquedas difusas rápidas."""
        print("🧠 Cargando catálogo de productos para Corrección Ortográfica (RAG)...")
        try:
            results = self.tools["database"].execute("SELECT DISTINCT nombre_comercial FROM v_stock_productos")
            if results and "error" not in results[0]:
                self.product_names = [row['nombre_comercial'] for row in results if row['nombre_comercial']]
                print(f"✅ Catálogo cargado: {len(self.product_names)} productos en memoria.")
            else:
                print("⚠️ No se pudo cargar el catálogo de productos.")
        except Exception as e:
            print(f"⚠️ Error cargando catálogo: {e}")

    def ask(self, question: str) -> str:
        """
        Pregunta principal del agente (con auto-corrección y manejo de errores)
        """
        print(f"\n🤔 Pregunta: {question}")
        self._add_to_context("user", question)

        # --- VERIFICAR CACHÉ PRIMERO ---
        normalized_q = " ".join(question.lower().split())
        q_hash = hashlib.md5(normalized_q.encode('utf-8')).hexdigest()

        if q_hash in self.query_cache:
            cached_data = self.query_cache[q_hash]
            time_diff = datetime.now() - cached_data['timestamp']

            if time_diff < timedelta(minutes=self.cache_ttl_minutes):
                print(f"⚡ [CACHÉ HIT] Respuesta obtenida de memoria local (ahorrando tokens).")
                response_text = cached_data['response']
                self._add_to_context("assistant", response_text)
                return response_text
            else:
                del self.query_cache[q_hash]

        response_text = ""

        try:
            print("⚙️  Generando consulta SQL (Intento 1)...")
            sql = self._generate_sql(question)

            if sql == "NO_QUERY":
                response_text = "No puedo responder esa pregunta con los datos disponibles."
            else:
                print(f"📊 SQL (Intento 1): {sql}")

                # --- LÓGICA DE MULTI-DATASOURCE EXECUTION ---
                selected_tool = self.tools.get("database")
                if self.db_type == 'multi':
                    if "=== DB2 ===" in sql or sql.lstrip().startswith("```db2"):
                        selected_tool = self.tools["ventas_db"]
                        sql = sql.replace("=== DB2 ===", "").replace("```db2", "").replace("```", "").strip()
                    else:
                        selected_tool = self.tools["inventario_db"]
                        sql = sql.replace("=== DB1 ===", "").replace("```db1", "").replace("```", "").strip()

                results = selected_tool.execute(sql)

                # --- Lógica de Auto-Corrección ---
                if results and "error" in results[0]:
                    original_error = results[0]['error']
                    print(f"⚠️ Error en SQL (Intento 1): {original_error}")
                    print("⚙️  Generando consulta SQL (Intento 2: Corrección)...")

                    correction_prompt = self._generate_sql_correction_prompt(question, sql, original_error)
                    corrected_sql = self.model.ask(correction_prompt, self.context)

                    if corrected_sql.startswith("```sql"):
                        corrected_sql = corrected_sql.replace("```sql", "").replace("```", "").strip()
                    elif corrected_sql.startswith("```"):
                        corrected_sql = corrected_sql.replace("```", "").strip()

                    if corrected_sql == "NO_QUERY":
                        response_text = f"Intenté corregir un error, pero no pude encontrar una respuesta ({original_error})."
                    else:
                        print(f"📊 SQL (Intento 2): {corrected_sql}")
                        if self.db_type == 'multi':
                            if "=== DB2 ===" in corrected_sql or corrected_sql.lstrip().startswith("```db2"):
                                selected_tool = self.tools["ventas_db"]
                                corrected_sql = corrected_sql.replace("=== DB2 ===", "").replace("```db2", "").replace("```", "").strip()
                            else:
                                selected_tool = self.tools["inventario_db"]
                                corrected_sql = corrected_sql.replace("=== DB1 ===", "").replace("```db1", "").replace("```", "").strip()

                        results = selected_tool.execute(corrected_sql)
                        sql = corrected_sql

                        if results and "error" in results[0]:
                            final_error = results[0]['error']
                            print(f"❌ Error en SQL (Intento 2): {final_error}")
                            response_text = f"Error al ejecutar la consulta corregida: {final_error}"

                # --- Generar Respuesta Natural (si no hubo error) ---
                if not response_text:
                    print(f"✅ Resultados: {len(results)} filas")
                    response_text = self._generate_response(question, sql, results)

        except Exception as e:
            print(f"❌ Ocurrió una excepción inesperada en 'ask': {e}")
            response_text = "Lo siento, ocurrió un error interno al procesar tu solicitud."

        # --- Limpieza y Contexto ---
        if response_text.strip().startswith("```json"):
            print("Limpiando JSON envuelto en markdown...")
            response_text = response_text.strip().replace("```json", "").replace("```", "").strip()

        # --- GUARDAR EN CACHÉ ANTES DE RETORNAR ---
        is_error = "ocurrió un error interno" in response_text or "No puedo responder" in response_text
        is_confirm = "Confirmación Requerida" in response_text
        if not is_error and not is_confirm:
            self.query_cache[q_hash] = {
                'response': response_text,
                'timestamp': datetime.now()
            }

        self._add_to_context("assistant", response_text)
        return response_text

    def _generate_sql(self, question: str) -> str:
        """Genera una consulta SQL a partir de la pregunta del usuario."""
        if self.db_type == 'multi':
            schema = ("=== BASES DE DATOS DE FARMASYNC ===\n\n" +
                      self.tools["inventario_db"].get_schema() + "\n" +
                      self.tools["ventas_db"].get_schema())
        else:
            schema = self.tools["database"].get_schema()

        db_hint = "MULTI-DATASOURCE (MySQL + PostgreSQL)" if self.db_type == 'multi' else "MySQL"

        system_instruction = f"""
Eres un asistente experto en BI y SQL para "Farmasync POS" usando {db_hint}.
Tu trabajo es generar consultas SQL precisas para responder preguntas.
Tienes acceso a DOS BASES DE DATOS FÍSICAMENTE SEPARADAS. No puedes hacer `JOIN` cruzados.

{schema}

REGLAS EXPERTAS (PRIORIDAD ALTA):
1. **INVENTARIO (DB1 - MySQL):** Todo sobre stock, lotes, vencimientos, categorías locales (vista: `v_stock_productos`, etc).
2. **VENTAS (DB2 - PostgreSQL):** Todo sobre facturas (`ventas`), cobros (`detalle_ventas`), cajas y cierres.

3. **EL PUENTE lógico:** `DB1(productos.id)` equivale a `DB2(detalle_ventas.producto_id)`.
   Si el usuario pregunta "Cuánto vendió el laboratorio Bayer":
   - Genera un SQL para la DB2 respondiendo explícitamente `=== DB2 ===` o `=== DB1 ===` PREVIO a tu sentencia SQL.
   - Ejemplo:
     === DB2 ===
     SELECT SUM(subtotal) FROM detalle_ventas WHERE tipo_venta = 'UNIDAD';

4. **SEGURIDAD:** SOLAMENTE CONSULTAS `SELECT`.

5. **LÍMITE DE PROTECCIÓN (CRÍTICO):**
   - Siempre incluye `LIMIT 50` si la consulta no usa agregaciones.

6. **VALORES POSITIVOS EN GRÁFICOS Y REPORTES:**
   - Las salidas se registran con cantidades NEGATIVAS. Usa `ABS()` para resultados positivos.

7. **SEGURIDAD Y PRIVACIDAD DE DATOS (CRÍTICO):**
   - NUNCA selecciones contraseñas, hashes, tokens JWT, o pines de seguridad.
   - Si el usuario pide "toda la información de los usuarios", NO uses `SELECT *`.
"""

        # --- LÓGICA RAG: Búsqueda Difusa de Productos ---
        words_in_question = question.replace('?', '').replace(',', '').split()
        potential_matches = set()

        for word in words_in_question:
            if len(word) > 4:
                matches = difflib.get_close_matches(word.lower(), [p.lower() for p in self.product_names], n=2, cutoff=0.7)
                if matches:
                    for match in matches:
                        for original_name in self.product_names:
                            if original_name.lower() == match:
                                potential_matches.add(original_name)
                                break

        if potential_matches:
            matches_str = "', '".join(potential_matches)
            system_instruction += f"\n\n🚨 **AYUDA DE CONTEXTO:** Nombres de productos reales que suenan parecido: '{matches_str}'. Usa ESTOS nombres exactos en WHERE LIKE."

        system_instruction += f"""
Pregunta del usuario: {question}

Genera SOLO la consulta SQL (sin explicaciones ni formato markdown).
Si no se puede responder, devuelve: NO_QUERY
"""

        sql = self.model.ask(system_instruction, self.context)

        if sql.startswith("```sql"):
            sql = sql.replace("```sql", "").replace("```", "").strip()
        elif sql.startswith("```"):
            sql = sql.replace("```", "").strip()

        return sql

    def _generate_sql_correction_prompt(self, question: str, bad_sql: str, error: str) -> str:
        """Genera un prompt de corrección de SQL."""
        if self.db_type == 'multi':
            schema = ("=== BASES DE DATOS DE FARMASYNC ===\n\n" +
                      self.tools["inventario_db"].get_schema() + "\n" +
                      self.tools["ventas_db"].get_schema())
        else:
            schema = self.tools["database"].get_schema()
        db_hint = "MULTI-DATASOURCE" if self.db_type == 'multi' else "MySQL"

        return f"""Eres un experto en SQL para {db_hint}.
{schema}

El usuario preguntó: {question}

Se intentó ejecutar la siguiente consulta:
{bad_sql}

Pero falló con este error:
{error}

Por favor, corrige la consulta SQL. Genera SOLO la consulta SQL corregida (sin explicaciones).
Si no se puede responder, devuelve: NO_QUERY
"""

    def _generate_response(self, question: str, sql: str, results: List[Dict]) -> str:
        """
        Genera respuesta. Intercepta INSERT/UPDATE para pedir confirmación.
        Decide si la respuesta es texto, tabla o gráfico.
        """
        sql_upper = sql.strip().upper()
        if sql_upper.startswith("INSERT") or sql_upper.startswith("UPDATE"):
            confirm_data = {
                "type": "confirm",
                "title": "Confirmación Requerida",
                "message": "Estoy a punto de realizar la siguiente operación en la base de datos:",
                "sql_query": sql
            }
            return json.dumps(confirm_data)

        results_str = json.dumps(results, cls=CustomDecimalEncoder)

        if len(results_str) > 3000:
            results_str = results_str[:3000] + "... (resultados truncados)"

        prompt = f"""El usuario preguntó: {question}
Se ejecutó: {sql}
Resultados: {results_str}

Eres un asistente de análisis de datos. Analiza la PREGUNTA y los RESULTADOS y decide la mejor forma de presentarlos.

REGLAS DE DECISIÓN:

1. **RESPUESTA TIPO 'chart' (Gráfico):**
   * Cuándo usarlo: Si la PREGUNTA pide "reporte", "análisis", etc., y los RESULTADOS son una agregación.
   * Formato: {{"type": "chart", "chart_type": "bar", "title": "...", "content": [resultados], "label_key": "columna_X", "data_key": "columna_Y"}}

2. **RESPUESTA TIPO 'table' (Tabla):**
   * Cuándo usarlo: Si la PREGUNTA pide "listar", "mostrar todos", etc.
   * Formato: {{"type": "table", "title": "...", "content": [resultados]}}

3. **RESPUESTA TIPO 'text' (Texto Plano):**
   * Cuándo usarlo: Para todo lo demás (datos únicos, conteos totales, sin resultados).

INSTRUCCIÓN FINAL: Responde SOLAMENTE con el formato JSON (para 'chart' o 'table') o con el texto plano (para 'text').
"""

        return self.model.ask(prompt, self.context)

    def _add_to_context(self, role: str, content: str):
        """Añade un mensaje al historial de conversación."""
        self.context.append({"role": role, "content": content})

        if len(self.context) > self.max_context:
            self.context = self.context[-self.max_context:]

    def add_tool(self, name: str, tool: Any):
        """Añade una herramienta personalizada al agente."""
        self.tools[name] = tool
        print(f"✅ Herramienta '{name}' agregada")

    def get_context_summary(self) -> Dict:
        """Devuelve un resumen del estado actual del agente."""
        return {
            "messages": len(self.context),
            "database_type": self.db_type,
            "tools": list(self.tools.keys())
        }

    def clear_context(self):
        """Limpia el historial de conversación."""
        self.context = []
        print("🧹 Contexto limpiado")

    def close(self):
        """Cierra todas las conexiones de base de datos."""
        for tool in self.tools.values():
            if hasattr(tool, 'close'):
                tool.close()
