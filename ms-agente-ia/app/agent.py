"""
Agente MCP - Model Context Protocol
Soporta SQLite, MySQL y Multi-Datasource (MySQL + PostgreSQL)
"""
from typing import List, Dict, Any, Optional, Tuple
from app.models.openai_model import OpenAIModel
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
    """Codificador JSON para Decimal y Date/Datetime."""
    def default(self, obj):
        if isinstance(obj, Decimal):
            return float(obj)
        if isinstance(obj, (date, datetime)):
            return obj.isoformat()
        return super().default(obj)


# Palabras que indican que la pregunta depende del contexto conversacional → no cachear
_CONTEXT_REFS = re.compile(
    r'\b(ese|esa|esos|esas|ese producto|esa venta|el mismo|la misma|'
    r'el anterior|la anterior|mencionado|mencionada|dicho|dicha|'
    r'ese medicamento|esa categor[íi]a|cu[áa]nto queda|quedan|'
    r'y (el|la|los|las)|tambi[ée]n|adem[áa]s|el que mencionaste|'
    r'ese laboratorio|ese proveedor)\b',
    re.IGNORECASE
)


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
        self.model = OpenAIModel(api_key, model_name)
        self.tools = {}
        self.db_type = db_type

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

        self.context: List[Dict[str, str]] = []
        self.max_context = 10

        self.query_cache: Dict[str, Dict[str, Any]] = {}
        self.cache_ttl_minutes = 5

        self.product_names: List[str] = []
        self._load_product_names()

    def _load_product_names(self):
        """Carga nombres de productos para corrección ortográfica (RAG)."""
        print("🧠 Cargando catálogo de productos para Corrección Ortográfica (RAG)...")
        try:
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

    # ─────────────────────────────────────────────
    # Método principal
    # ─────────────────────────────────────────────

    def ask(self, question: str) -> str:
        """Pregunta principal del agente."""
        print(f"\n🤔 Pregunta: {question}")
        self._add_to_context("user", question)

        normalized_q = " ".join(question.lower().split())

        # CAPA 1: Zero-latency — saludos simples sin llamar al LLM
        greetings_pattern = r'^(hola|buenos d[íi]as|buenas|buenas tardes|buenas noches|gracias|muchas gracias|adi[óo]s|chau|hasta luego|qu[ée] tal)$'
        if re.match(greetings_pattern, normalized_q.replace("?", "").replace("!", "").replace(",", "").strip()):
            print("⚡ [ZERO-LATENCY] Saludo detectado. Omitiendo LLM.")
            response_text = "¡Hola! ¿Cómo estás? Soy FarmaChat, el asistente inteligente de Regen Salud POS. ¿En qué te puedo ayudar hoy con tus datos de Ventas o Inventario?"
            self._add_to_context("assistant", response_text)
            return response_text

        # CAPA 2: Caché — omitir si la pregunta depende del contexto conversacional
        is_context_dependent = bool(_CONTEXT_REFS.search(normalized_q))
        q_hash = hashlib.md5(normalized_q.encode('utf-8')).hexdigest()

        if not is_context_dependent and q_hash in self.query_cache:
            cached_data = self.query_cache[q_hash]
            if datetime.now() - cached_data['timestamp'] < timedelta(minutes=self.cache_ttl_minutes):
                print("⚡ [CACHÉ HIT] Respuesta obtenida de memoria local.")
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

            elif "=== MULTI-STEP ===" in sql:
                response_text = self._handle_multi_step(question, sql)

            else:
                print(f"📊 SQL (Intento 1): {sql}")
                selected_tool, clean_sql = self._resolve_tool_and_sql(sql)
                results = selected_tool.execute(clean_sql)

                # Auto-corrección si el primer intento falla
                if results and "error" in results[0]:
                    original_error = results[0]['error']
                    print(f"⚠️ Error en SQL (Intento 1): {original_error}")
                    print("⚙️  Generando consulta SQL (Intento 2: Corrección)...")

                    correction_prompt = self._generate_sql_correction_prompt(question, clean_sql, original_error)
                    corrected_raw = self.model.ask(correction_prompt, self.context)
                    corrected_raw = self._clean_sql_markdown(corrected_raw)

                    if corrected_raw == "NO_QUERY":
                        response_text = f"Intenté corregir el error pero no encontré una consulta válida. ({original_error})"
                    else:
                        selected_tool, corrected_sql = self._resolve_tool_and_sql(corrected_raw)
                        print(f"📊 SQL (Intento 2): {corrected_sql}")
                        results = selected_tool.execute(corrected_sql)
                        clean_sql = corrected_sql

                        if results and "error" in results[0]:
                            final_error = results[0]['error']
                            print(f"❌ Error en SQL (Intento 2): {final_error}")
                            response_text = f"Error al ejecutar la consulta corregida: {final_error}"

                if not response_text:
                    print(f"✅ Resultados: {len(results)} filas")
                    response_text = self._generate_response(question, clean_sql, results)

        except Exception as e:
            print(f"❌ Excepción inesperada en 'ask': {e}")
            response_text = "Lo siento, ocurrió un error interno al procesar tu solicitud."

        # Limpiar JSON envuelto en markdown si el LLM lo devolvió así
        if response_text.strip().startswith("```json"):
            response_text = response_text.strip().replace("```json", "").replace("```", "").strip()

        # Guardar en caché (solo respuestas exitosas y no dependientes de contexto)
        is_error = "ocurrió un error interno" in response_text or "No puedo responder" in response_text
        is_confirm = "Confirmación Requerida" in response_text
        if not is_error and not is_confirm and not is_context_dependent:
            self.query_cache[q_hash] = {'response': response_text, 'timestamp': datetime.now()}

        self._add_to_context("assistant", response_text)
        return response_text

    # ─────────────────────────────────────────────
    # Helpers de SQL
    # ─────────────────────────────────────────────

    def _clean_sql_markdown(self, sql: str) -> str:
        """Limpia bloques de código markdown del SQL generado por el LLM."""
        sql = sql.strip()
        if sql.startswith("```sql"):
            sql = sql.replace("```sql", "").replace("```", "").strip()
        elif sql.startswith("```"):
            sql = sql.replace("```", "").strip()
        return sql

    def _resolve_tool_and_sql(self, sql: str) -> Tuple[Any, str]:
        """
        Determina qué herramienta de BD usar según el prefijo del SQL
        y devuelve la herramienta + el SQL limpio.
        """
        if self.db_type != 'multi':
            return self.tools.get("database"), self._clean_sql_markdown(sql)

        if "=== DB2 ===" in sql or sql.lstrip().startswith("```db2"):
            tool = self.tools["ventas_db"]
            clean = sql.replace("=== DB2 ===", "").replace("```db2", "").replace("```", "").strip()
        else:
            tool = self.tools["inventario_db"]
            clean = sql.replace("=== DB1 ===", "").replace("```db1", "").replace("```", "").strip()

        return tool, self._clean_sql_markdown(clean)

    def _handle_multi_step(self, question: str, raw_sql: str) -> str:
        """
        Ejecuta consultas multi-paso que necesitan datos de ambas bases de datos.

        Formato esperado del LLM:
            === MULTI-STEP ===
            === DB2 ===
            SELECT producto_id ... ;
            ---SEPARADOR---
            === DB1 ===
            SELECT nombre_comercial ... WHERE id = {STEP_RESULT};

        {STEP_RESULT} se reemplaza con el primer valor de la primera fila del paso anterior.
        """
        print("🔀 [MULTI-STEP] Ejecutando consulta multi-base de datos...")

        steps_block = raw_sql.replace("=== MULTI-STEP ===", "").strip()
        steps = [s.strip() for s in steps_block.split("---SEPARADOR---") if s.strip()]

        all_results: List[Dict] = []
        last_result: List[Dict] = []

        for i, step_sql in enumerate(steps):
            # Sustituir {STEP_RESULT} con el primer valor del paso anterior
            if "{STEP_RESULT}" in step_sql and last_result:
                first_val = list(last_result[0].values())[0]
                step_sql = step_sql.replace("{STEP_RESULT}", str(first_val))

            # Sustituir {STEP_RESULT_LIST} con lista de IDs del paso anterior (ej: 1,2,3)
            if "{STEP_RESULT_LIST}" in step_sql and last_result:
                id_list = ",".join(str(list(row.values())[0]) for row in last_result)
                step_sql = step_sql.replace("{STEP_RESULT_LIST}", id_list)

            tool, clean_sql = self._resolve_tool_and_sql(step_sql)
            print(f"📊 MULTI-STEP Paso {i + 1}: {clean_sql}")

            results = tool.execute(clean_sql)

            if results and "error" in results[0]:
                err = results[0]['error']
                print(f"⚠️ Error en MULTI-STEP paso {i + 1}: {err}")
                return f"Error en la consulta multi-paso (paso {i + 1}): {err}"

            last_result = results
            if results:
                all_results.extend(results)

        if not all_results:
            return "No se encontraron resultados para tu consulta."

        print(f"✅ MULTI-STEP completado: {len(all_results)} filas en total")
        return self._generate_response(question, "MULTI-STEP", all_results)

    # ─────────────────────────────────────────────
    # Generación de SQL (prompt al LLM)
    # ─────────────────────────────────────────────

    def _generate_sql(self, question: str) -> str:
        """Genera una consulta SQL a partir de la pregunta en lenguaje natural."""
        today = date.today()
        today_str = today.strftime("%Y-%m-%d")
        current_month_num = today.month
        current_year = today.year

        if self.db_type == 'multi':
            schema = ("=== BASES DE DATOS DE REGEN SALUD POS ===\n\n" +
                      self.tools["inventario_db"].get_schema() + "\n" +
                      self.tools["ventas_db"].get_schema())
        else:
            schema = self.tools["database"].get_schema()

        db_hint = "MULTI-DATASOURCE (MySQL + PostgreSQL)" if self.db_type == 'multi' else "MySQL"

        system_instruction = f"""
Eres un asistente experto en BI y SQL para "Regen Salud POS" usando {db_hint}.
Fecha actual del sistema: {today_str} (mes: {current_month_num}, año: {current_year}).

{schema}

════════════════════════════════════════════════════════════
ENRUTAMIENTO DE BASE DE DATOS (obligatorio en modo MULTI)
════════════════════════════════════════════════════════════

**DB1 → MySQL (Inventario/Productos)**
  Contiene: productos, stock, lotes, vencimientos, categorías, proveedores.
  Prefijo obligatorio: === DB1 ===
  Funciones de fecha MySQL: CURDATE(), NOW(), DATE_FORMAT(), MONTH(), YEAR(),
    DATE_SUB(), DATE_ADD(), DATEDIFF(), STR_TO_DATE()

**DB2 → PostgreSQL (Ventas/Facturación)**
  Contiene: ventas, detalle_ventas, facturas, cajas, cierres de caja.
  Prefijo obligatorio: === DB2 ===
  Funciones de fecha PostgreSQL: CURRENT_DATE, NOW(), DATE_TRUNC(), EXTRACT(),
    TO_CHAR(), AGE(), interval

════════════════════════════════════════════════════════════
MANEJO DE FECHAS RELATIVAS (usa la fecha actual: {today_str})
════════════════════════════════════════════════════════════
| Expresión         | MySQL (DB1)                                              | PostgreSQL (DB2)                                      |
|-------------------|----------------------------------------------------------|-------------------------------------------------------|
| "hoy"             | WHERE DATE(campo) = CURDATE()                            | WHERE campo::date = CURRENT_DATE                      |
| "ayer"            | WHERE DATE(campo) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)  | WHERE campo::date = CURRENT_DATE - 1                  |
| "esta semana"     | WHERE YEARWEEK(campo) = YEARWEEK(CURDATE())              | WHERE DATE_TRUNC('week', campo) = DATE_TRUNC('week', CURRENT_DATE) |
| "este mes"        | WHERE YEAR(campo)={current_year} AND MONTH(campo)={current_month_num}        | WHERE DATE_TRUNC('month', campo) = DATE_TRUNC('month', CURRENT_DATE) |
| "este año"        | WHERE YEAR(campo) = {current_year}                               | WHERE EXTRACT(YEAR FROM campo) = {current_year}               |
| "últimos 7 días"  | WHERE campo >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)       | WHERE campo >= CURRENT_DATE - 7                       |
| "últimos 30 días" | WHERE campo >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)      | WHERE campo >= CURRENT_DATE - 30                      |
| "último mes"      | WHERE campo >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)     | WHERE campo >= CURRENT_DATE - interval '1 month'      |
| "próximos N días" | WHERE campo BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL N DAY) | WHERE campo BETWEEN CURRENT_DATE AND CURRENT_DATE + N |

════════════════════════════════════════════════════════════
CONSULTAS MULTI-PASO (para preguntas cross-database)
════════════════════════════════════════════════════════════
Cuando la respuesta requiere datos de AMBAS bases (ej: "¿cuál es el más vendido y cuánto stock tiene?"):

Usa este formato EXACTO:
=== MULTI-STEP ===
=== DB2 ===
SELECT producto_id, SUM(cantidad) as total_vendido FROM detalle_ventas GROUP BY producto_id ORDER BY total_vendido DESC LIMIT 1;
---SEPARADOR---
=== DB1 ===
SELECT nombre_comercial, stock_actual FROM v_stock_productos WHERE id = {{STEP_RESULT}};

Regla: {{STEP_RESULT}} se sustituye con el PRIMER VALOR de la PRIMERA FILA del paso anterior.
Úsalo cuando necesites el ID de un resultado de DB2 para buscarlo en DB1 (o viceversa).

════════════════════════════════════════════════════════════
REGLA CRÍTICA: NUNCA MOSTRAR IDs AL USUARIO
════════════════════════════════════════════════════════════
NUNCA devuelvas columnas como producto_id, id, cliente_id, venta_id u otros IDs numéricos
como resultado final visible. Usa siempre el nombre legible disponible.

La tabla detalle_ventas (DB2) ya tiene la columna producto_nombre.
Para consultas de productos más vendidos, top productos, ventas por producto, etc.,
USA SIEMPRE producto_nombre en lugar de producto_id.

Ejemplo correcto para "productos más vendidos":
=== DB2 ===
SELECT dv.producto_nombre, SUM(dv.cantidad) AS total_vendido
FROM detalle_ventas dv
JOIN ventas v ON dv.venta_id = v.id
WHERE v.estado = 'COMPLETADA'
GROUP BY dv.producto_nombre
ORDER BY total_vendido DESC
LIMIT 10;

════════════════════════════════════════════════════════════
PREGUNTAS DE SEGUIMIENTO (contexto conversacional)
════════════════════════════════════════════════════════════
Si el usuario dice "ese", "esa", "el mismo", "ese producto", "cuánto queda", etc.,
revisa el HISTORIAL DE CONVERSACIÓN, extrae el nombre o ID mencionado previamente
y úsalo directamente en la cláusula WHERE de tu SQL.

════════════════════════════════════════════════════════════
TIPOS DE PREGUNTAS SOPORTADAS
════════════════════════════════════════════════════════════
Inventario (DB1):
  - Stock disponible por producto, categoría o laboratorio
  - Productos con bajo stock (bajo un umbral)
  - Lotes próximos a vencer (en N días)
  - Valor total del inventario
  - Productos por proveedor/categoría
  - Historial de entradas/salidas

Ventas (DB2):
  - Total de ventas (cantidad o monto) por período
  - Ventas por día, semana, mes, año
  - Detalle de una venta específica
  - Productos más vendidos
  - Ingresos totales o por período
  - Estado de cajas y cierres

Cross-database (MULTI-STEP):
  - Producto más vendido + su stock actual
  - Comparar stock vs ventas de un período
  - Productos vendidos que están próximos a agotarse

════════════════════════════════════════════════════════════
REGLAS OBLIGATORIAS PARA CONSULTAS DE VENTAS (DB2 - PostgreSQL)
════════════════════════════════════════════════════════════
SIEMPRE aplica estos filtros en consultas sobre la tabla "ventas":

1. FILTRAR SOLO VENTAS COMPLETADAS:
   WHERE v.estado = 'COMPLETADA'
   (Excluye ventas con estado 'ANULADA')

2. DESCONTAR DEVOLUCIONES (cuando el usuario pregunte por totales de dinero):
   La tabla "devoluciones" tiene: venta_id (FK → ventas.id), total_devuelto, estado
   Las devoluciones aceptadas tienen estado = 'COMPLETADA'.
   Para obtener el monto neto real:
   SUM(v.total) - COALESCE((SELECT SUM(d.total_devuelto) FROM devoluciones d
     WHERE d.venta_id = v.id AND d.estado = 'COMPLETADA'), 0)
   O con subquery agrupado para un período:
   (SELECT COALESCE(SUM(total_devuelto), 0) FROM devoluciones
    WHERE estado = 'COMPLETADA' AND fecha BETWEEN ... AND ...)

   Ejemplo completo para "total de ventas de marzo":
   === DB2 ===
   SELECT
     COALESCE(SUM(v.total), 0) - COALESCE(
       (SELECT SUM(d.total_devuelto) FROM devoluciones d
        JOIN ventas vd ON d.venta_id = vd.id
        WHERE d.estado = 'COMPLETADA'
          AND EXTRACT(MONTH FROM d.fecha) = 3
          AND EXTRACT(YEAR FROM d.fecha) = 2026), 0
     ) AS total_neto
   FROM ventas v
   WHERE v.estado = 'COMPLETADA'
     AND EXTRACT(MONTH FROM v."fechaVenta") = 3
     AND EXTRACT(YEAR FROM v."fechaVenta") = 2026;

   NOTA: La columna se llama "fechaVenta" (camelCase) → siempre con comillas dobles en PostgreSQL.

════════════════════════════════════════════════════════════
SEGURIDAD Y BUENAS PRÁCTICAS
════════════════════════════════════════════════════════════
1. SOLO consultas SELECT. Nunca INSERT/UPDATE/DELETE/DROP.
2. NUNCA selecciones campos: password, contraseña, token, jwt, hash, pin.
3. Agrega LIMIT 50 si la consulta no usa agregaciones (COUNT, SUM, AVG, MAX, MIN).
4. Usa COALESCE(SUM(campo), 0) para evitar retornar NULL cuando no hay datos.
5. Usa ABS() para cantidades almacenadas como negativas (salidas de stock).
6. NO hagas JOINs entre tablas de distintas bases de datos.
7. En MySQL usa backticks para nombres reservados; en PostgreSQL usa comillas dobles.

════════════════════════════════════════════════════════════
CONVERSACIÓN CASUAL (FAST-ROUTING)
════════════════════════════════════════════════════════════
Si la pregunta NO necesita datos de BD (capacidades, agradecimientos, explicaciones):
Responde ESTRICTAMENTE comenzando con: CHAT: <tu respuesta>

Pregunta del usuario: {question}

Genera SOLO el SQL (sin explicaciones, sin markdown). Si no puedes responder: NO_QUERY
"""

        # RAG: fuzzy matching de nombres de productos en la pregunta
        words_in_question = question.replace('?', '').replace(',', '').split()
        potential_matches = set()
        for word in words_in_question:
            if len(word) > 4:
                matches = difflib.get_close_matches(
                    word.lower(),
                    [p.lower() for p in self.product_names],
                    n=2, cutoff=0.7
                )
                for match in matches:
                    for original_name in self.product_names:
                        if original_name.lower() == match:
                            potential_matches.add(original_name)
                            break

        if potential_matches:
            matches_str = "', '".join(potential_matches)
            system_instruction += f"\n\n🚨 NOMBRES REALES EN CATÁLOGO (usa estos exactos en WHERE LIKE): '{matches_str}'"

        sql = self.model.ask(system_instruction, self.context)
        return self._clean_sql_markdown(sql)

    def _generate_sql_correction_prompt(self, question: str, bad_sql: str, error: str) -> str:
        """Genera un prompt para que el LLM corrija una consulta SQL fallida."""
        if self.db_type == 'multi':
            schema = ("=== BASES DE DATOS DE REGEN SALUD POS ===\n\n" +
                      self.tools["inventario_db"].get_schema() + "\n" +
                      self.tools["ventas_db"].get_schema())
        else:
            schema = self.tools["database"].get_schema()

        db_hint = "MULTI-DATASOURCE (MySQL + PostgreSQL)" if self.db_type == 'multi' else "MySQL"

        return f"""Eres un experto en SQL para {db_hint} - Regen Salud POS.

{schema}

El usuario preguntó: {question}

Se intentó ejecutar:
{bad_sql}

Error obtenido:
{error}

Corrige la consulta. Devuelve SOLO el SQL corregido (sin explicaciones ni markdown).
Recuerda usar el prefijo === DB1 === para MySQL (inventario) o === DB2 === para PostgreSQL (ventas).

REGLAS IMPORTANTES PARA VENTAS (DB2):
- Siempre filtra WHERE estado = 'COMPLETADA' en la tabla ventas.
- La columna de fecha se llama "fechaVenta" (camelCase) → usa comillas dobles en PostgreSQL.
- Para totales de dinero, descuenta devoluciones con estado = 'COMPLETADA' de la tabla devoluciones.

Si no es posible corregir, devuelve: NO_QUERY
"""

    # ─────────────────────────────────────────────
    # Generación de respuesta en lenguaje natural
    # ─────────────────────────────────────────────

    def _generate_response(self, question: str, sql: str, results: List[Dict]) -> str:
        """
        Genera una respuesta en lenguaje natural a partir de los resultados SQL.
        Usa heurística local (sin LLM) para maximizar velocidad.
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

        q_lower = question.lower()

        # ── Caso 1: Un solo resultado con un solo valor → Texto contextualizado ──
        if len(results) == 1 and len(results[0]) == 1:
            key, val = list(results[0].items())[0]

            if val is None:
                return "No se encontró información sobre ese elemento en el sistema."
            if val == 0 or val == Decimal('0'):
                return "El resultado es 0 — no hay datos que coincidan con tu consulta."

            val_fmt = f"{float(val):,.2f}" if isinstance(val, Decimal) else str(val)

            if any(w in q_lower for w in ['cuánto dinero', 'total vendido', 'recaudado', 'ingresos', 'monto', 'valor total', 'suma', 'suma de ventas', 'ventas del mes', 'ventas de']):
                return f"El total fue de ${val_fmt}."
            if any(w in q_lower for w in ['cuántas ventas', 'número de ventas', 'total de ventas', 'ventas registradas', 'ventas hubo']):
                return f"Se han registrado {val_fmt} ventas."
            if any(w in q_lower for w in ['cuántos productos', 'total de productos', 'cuántos artículos']):
                return f"Hay {val_fmt} productos en el sistema."
            if any(w in q_lower for w in ['cuántas unidades', 'stock', 'unidades disponibles', 'en inventario']):
                return f"Hay {val_fmt} unidades disponibles."
            if any(w in q_lower for w in ['cuántos clientes', 'número de clientes']):
                return f"Hay {val_fmt} clientes registrados."
            if any(w in q_lower for w in ['promedio', 'precio promedio', 'precio medio']):
                return f"El precio promedio es ${val_fmt}."
            if any(w in q_lower for w in ['cuántos lotes', 'número de lotes']):
                return f"Hay {val_fmt} lotes registrados."
            if any(w in q_lower for w in ['vencen', 'vencimiento', 'próximos a vencer']):
                return f"Hay {val_fmt} producto(s) próximos a vencer."

            return f"El resultado es: {val_fmt}."

        # ── Detectar columnas para gráfico ──
        time_keys = {
            'fecha', 'mes', 'dia', 'semana', 'año', 'anio', 'periodo', 'date',
            'month', 'day', 'hora', 'tipo', 'estado', 'categoria', 'categoria_nombre',
            'nombre_comercial', 'nombre', 'producto', 'laboratorio', 'proveedor',
            'nombre_producto', 'nombre_categoria'
        }
        keys = list(results[0].keys()) if results else []
        label_key = next((k for k in keys if k.lower() in time_keys), None)
        numeric_key = next(
            (k for k in keys if k != label_key and isinstance(results[0][k], (int, float, Decimal))),
            None
        )

        # ── Caso 2: Múltiples filas con eje categórico/temporal y un valor numérico → Gráfico ──
        is_chart_question = any(w in q_lower for w in [
            'reporte', 'análisis', 'analisis', 'ventas por', 'por mes', 'por día', 'por dia',
            'histórico', 'historico', 'tendencia', 'distribución', 'distribucion',
            'gráfico', 'grafico', 'comparar', 'comparativa', 'ranking', 'top',
            'evolución', 'evolucion', 'por categoría', 'por categoria',
            'por laboratorio', 'por proveedor'
        ])
        if len(results) > 1 and label_key and numeric_key and is_chart_question:
            chart_data = {
                "type": "chart",
                "chart_type": "bar",
                "title": f"Reporte: {question[:60]}",
                "content": json.loads(json.dumps(results, cls=CustomDecimalEncoder)),
                "label_key": label_key,
                "data_key": numeric_key
            }
            return json.dumps(chart_data)

        # ── Caso 3: Múltiples filas o fila con múltiples columnas → Tabla ──
        if len(results) > 1 or (len(results) == 1 and len(results[0]) > 1):
            table_data = {
                "type": "table",
                "title": f"Resultados: {question[:60]}",
                "content": json.loads(json.dumps(results, cls=CustomDecimalEncoder))
            }
            return json.dumps(table_data)

        # ── Fallback: texto plano ──
        results_str = json.dumps(results, cls=CustomDecimalEncoder, ensure_ascii=False, indent=2)
        return f"Aquí están los resultados:\n{results_str}"

    # ─────────────────────────────────────────────
    # Gestión de contexto y ciclo de vida
    # ─────────────────────────────────────────────

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
