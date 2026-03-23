"""
Herramienta para conectarse a la base de datos de Ventas (PostgreSQL) de Regen Salud POS.
"""
import psycopg2
from psycopg2.extras import RealDictCursor
import re
from typing import List, Dict, Any


class PostgresTool:
    """Herramienta para consultar Ventas (PostgreSQL) con reglas estrictas READ-ONLY."""

    def __init__(self, host: str, user: str, password: str, database: str, port: int = 5432):
        self.connection_params = {
            'host': host,
            'user': user,
            'password': password,
            'dbname': database,
            'port': port,
            'sslmode': 'require'
        }
        self.conn = None
        self.cursor = None
        self._cached_schema: str = ""  # <-- Schema Cache en memoria

        # Filtro de Seguridad Anti-Hackeo (DML/DDL Block)
        self.forbidden_words = re.compile(
            r"\b(INSERT|UPDATE|DELETE|DROP|ALTER|TRUNCATE|GRANT|REVOKE|COMMIT|ROLLBACK|EXEC)\b",
            re.IGNORECASE
        )

        self._connect()

    def _connect(self):
        """Establece la conexión con PostgreSQL."""
        try:
            self.conn = psycopg2.connect(**self.connection_params)
            self.conn.autocommit = False
            self.cursor = self.conn.cursor(cursor_factory=RealDictCursor)
            print(f"✅ Conectado a PostgreSQL (VENTAS): {self.connection_params['dbname']}")
        except psycopg2.Error as e:
            print(f"❌ Error al conectar a PostgreSQL: {e}")
            raise

    def _ensure_connection(self):
        """Verifica que la conexión a PostgreSQL esté activa."""
        try:
            if self.conn is None or self.conn.closed != 0:
                print("⚠️ Conexión de PostgreSQL perdida. Reconectando...")
                self._connect()
            else:
                self.conn.poll()
        except psycopg2.Error:
            print("⚠️ Falla de conexión. Forzando reconexión a PostgreSQL...")
            self._connect()

    def get_schema(self) -> str:
        """Obtiene el esquema de las tablas necesarias. Usa caché en memoria para evitar consultas repetitivas a Aiven."""
        if self._cached_schema:
            return self._cached_schema
        try:
            self._ensure_connection()
            self.cursor.execute("SELECT table_name FROM information_schema.tables WHERE table_schema='public';")
            tables = [row['table_name'] for row in self.cursor.fetchall()]

            schema = "=== ESQUEMA DE VENTAS (PostgreSQL) ===\n\n"
            sensitive_columns = {'password', 'contraseña', 'hashtoken', 'pin'}

            for table in tables:
                self._ensure_connection()
                self.cursor.execute(
                    f"SELECT column_name, data_type, is_nullable "
                    f"FROM information_schema.columns "
                    f"WHERE table_schema='public' AND table_name='{table}';"
                )
                columns = self.cursor.fetchall()

                schema += f"Tabla Ventas: {table}\n"
                for col in columns:
                    field = col['column_name']
                    if field.lower() in sensitive_columns:
                        continue
                    col_type = col['data_type']
                    null_str = "NOT NULL" if col['is_nullable'] == 'NO' else "NULL"
                    schema += f"  - {field}: {col_type} ({null_str})\n"

                self.cursor.execute(f"SELECT COUNT(*) as count FROM {table}")
                count = self.cursor.fetchone()['count']
                schema += f"  Total estim. registros: {count}\n\n"

            self._cached_schema = schema  # Almacenar en caché
            return schema
        except psycopg2.Error as e:
            return f"Error al obtener esquema PostgreSQL: {e}"

    def consultar_base_datos_ventas(self, sql: str) -> List[Dict[str, Any]]:
        """
        AI TOOL PRINCIPAL - Ejecuta SQL de Solo Lectura.
        Esta es la función que será consumida por el LLM.
        """
        clean_sql = sql.strip()
        sql_upper = clean_sql.upper()

        if self.forbidden_words.search(clean_sql):
            print("🛑 BLOQUEO DE SEGURIDAD: Se detectó una query mutacional en Ventas.")
            return [{"error": "Operación denegada: Solo lectura permitida. No puedes usar INSERT, UPDATE, DELETE, DROP, ALTER, TRUNCATE, etc."}]

        if not sql_upper.startswith('SELECT') and not sql_upper.startswith('WITH'):
            return [{"error": "Operación denegada: La consulta debe comenzar de forma estricta con SELECT o WITH en bases PostgreSQL."}]

        try:
            self._ensure_connection()
            if "LIMIT" not in sql_upper and "COUNT(" not in sql_upper and "SUM(" not in sql_upper:
                clean_sql = clean_sql.rstrip(";") + " LIMIT 50"

            self.cursor.execute(clean_sql)
            results = self.cursor.fetchall()
            return [dict(row) for row in results] if results else []

        except psycopg2.Error as e:
            self.conn.rollback()
            return [{"error": f"Error SQL PostgreSQL: {str(e)}"}]
        except Exception as e:
            return [{"error": f"Error General de Tool: {str(e)}"}]

    def execute(self, sql: str) -> List[Dict[str, Any]]:
        """Alias para mantener compatibilidad de API interna."""
        return self.consultar_base_datos_ventas(sql)

    def close(self):
        """Cierra la conexión."""
        if self.cursor:
            self.cursor.close()
        if self.conn:
            self.conn.close()
            print("🔌 Conexión PostgreSQL cerrada")
