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
import re


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
            # En modo 'multi' la tool de inventario se llama 'inventario_db'; en otros modos 'database'
            inventory_tool = self.tools.get("inventario_db") or self.tools.get("database")
            if not inventory_tool:
                print("⚠️ No se encontró herramienta de inventario para cargar el catálogo.")
                return
            results = inventory_tool.execute("SELECT DISTINCT nombre_comercial FROM v_stock_productos")
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

        # --- CAPA 1: ZERO-LATENCY GREETINGS ---
        greetings_pattern = r'^(hola|buenos d[íi]as|buenas|buenas tardes|buenas noches|gracias|muchas gracias|adi[óo]s|chau|hasta luego|qu[ée] tal)$'
        if re.match(greetings_pattern, normalized_q.replace("?", "").replace("!", "").replace(",", "").strip()):
            print("⚡ [ZERO-LATENCY] Saludo conversacional detectado. Omitiendo LLM.")
            response_text = "¡Hola! ¿Cómo estás? Soy FarmaChat, el asistente inteligente de Regen Salud POS. ¿En qué te puedo ayudar hoy con tus datos de Ventas o Inventario?"
            self._add_to_context("assistant", response_text)
            return response_text

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
            elif sql.startswith("CHAT:"):
                print("💬 [FAST-ROUTING] Respuesta conversacional generada por el LLM.")
                response_text = sql.replace("CHAT:", "").strip()
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
            schema = ("=== BASES DE DATOS DE REGEN SALUD POS ===\n\n" +
                      self.tools["inventario_db"].get_schema() + "\n" +
                      self.tools["ventas_db"].get_schema())
        else:
            schema = self.tools["database"].get_schema()

        db_hint = "MULTI-DATASOURCE (MySQL + PostgreSQL)" if self.db_type == 'multi' else "MySQL"

        system_instruction = f"""
Eres un asistente experto en BI y SQL para "Regen Salud POS" usando {db_hint}.
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

8. **CONVERSACIÓN CASUAL (FAST-ROUTING):**
   - Si el usuario hace una pregunta conversacional ("cómo estás", "¿qué sabes hacer?"), te saluda o agradece, y la pregunta NO requiere extraer datos de las bases de datos:
   - NO intentes hacer una consulta SQL forzada.
   - En su lugar, responde comenzando ESTRICTAMENTE con la etiqueta `CHAT:` seguida de tu respuesta conversacional amigable.
   - Ejemplo salida: `CHAT: ¡Hola! Soy FarmaChat, el asistente de Regen Salud POS. Puedo ayudarte consultando el stock interno y las ventas.`
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

REGLA ANTI-NULL (MUY IMPORTANTE):
- Si la consulta busca stock o cantidad de un producto con WHERE/LIKE y el producto puede no existir, usa COALESCE para evitar NULL.
- Ejemplo correcto: SELECT COALESCE(SUM(stock_actual), 0) as stock_total FROM ...
- Si el resultado es 0 o la tabla está vacía, el código Python mostrará un mensaje amigable.
- Nunca hagas que la consulta devuelva una fila con valor NULL cuando la intención es contar o sumar.

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
        Genera respuesta usando heurística local en Python (sin llamar a Gemini).
        Esto elimina el segundo round-trip a la IA ahorrando 2-4s por consulta.
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

        if not results:
            return "No se encontraron resultados para tu consulta."

        # --- HEURÍSTICA LOCAL DE FORMATO (0ms, sin Gemini) ---
        q_lower = question.lower()

        # Caso 1: Un solo resultado con un solo valor numérico → Texto plano
        if len(results) == 1 and len(results[0]) == 1:
            key, val = list(results[0].items())[0]
            if val is None:
                return "No se encontró información sobre ese producto en el sistema. Es posible que no exista en el inventario o no haya registros disponibles."
            if val == 0 or val == Decimal('0'):
                return "No hay existencias disponibles de ese producto. El inventario actual es de **0 unidades**."
            return f"El resultado de tu consulta es: **{val}**"

        # Detectar si hay columnas de tipo temporal/categórico y numérico para gráfico
        time_keys = {'fecha', 'mes', 'dia', 'semana', 'año', 'anio', 'periodo', 'date', 'month', 'day', 'hora', 'tipo', 'estado', 'categoria', 'categoria_nombre', 'nombre_comercial'}
        keys = list(results[0].keys()) if results else []
        label_key = next((k for k in keys if k.lower() in time_keys), None)
        numeric_key = next(
            (k for k in keys if k != label_key and isinstance(results[0][k], (int, float, Decimal))),
            None
        )

        # Caso 2: Múltiples filas con eje categórico/temporal y un valor numérico → Gráfico de barras
        is_chart_question = any(w in q_lower for w in ['reporte', 'análisis', 'analisis', 'ventas por', 'por mes', 'por día', 'por dia', 'histórico', 'historico', 'tendencia', 'distribución', 'distribucion', 'grafico', 'gráfico'])
        if len(results) > 1 and label_key and numeric_key and is_chart_question:
            title = f"Reporte: {question[:60]}"
            chart_data = {
                "type": "chart",
                "chart_type": "bar",
                "title": title,
                "content": json.loads(json.dumps(results, cls=CustomDecimalEncoder)),
                "label_key": label_key,
                "data_key": numeric_key
            }
            return json.dumps(chart_data)

        # Caso 3: Múltiples filas con múltiples columnas → Tabla
        if len(results) > 1 or (len(results) == 1 and len(results[0]) > 1):
            title = f"Resultados: {question[:60]}"
            table_data = {
                "type": "table",
                "title": title,
                "content": json.loads(json.dumps(results, cls=CustomDecimalEncoder))
            }
            return json.dumps(table_data)

        # Caso 4: Fallback → Texto plano con los datos
        results_str = json.dumps(results, cls=CustomDecimalEncoder, ensure_ascii=False, indent=2)
        return f"Aquí están los resultados:\n{results_str}"

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
