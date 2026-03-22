"""
Herramienta para trabajar con bases de datos MySQL
"""
import mysql.connector
from mysql.connector import errorcode
from typing import List, Dict, Any, Optional


class MySQLTool:
    """Herramienta para consultar bases de datos MySQL con reconexión automática."""

    def __init__(
        self,
        host: str,
        user: str,
        password: str,
        database: str,
        port: int = 3306
    ):
        self.connection_params = {
            'host': host,
            'user': user,
            'password': password,
            'database': database,
            'port': port,
            # --- Parámetros requeridos por Aiven Cloud MySQL ---
            'ssl_disabled': False,
            'connection_timeout': 30,
            'connect_timeout': 30,
        }
        self.conn = None
        self.cursor = None
        self._cached_schema: str = ""  # <-- Schema Cache en memoria
        self._connect()

    def _connect(self):
        """Establece la conexión con MySQL."""
        try:
            self.conn = mysql.connector.connect(**self.connection_params)
            self.conn.autocommit = False
            self.cursor = self.conn.cursor(dictionary=True)
            print(f"✅ Conectado a MySQL: {self.connection_params['database']}")
        except mysql.connector.Error as e:
            print(f"❌ Error al conectar a MySQL: {e}")
            raise

    def _ensure_connection(self):
        """
        Verifica que la conexión siga activa.
        Si se perdió (ej. timeout de Aiven), la restablece automáticamente.
        """
        try:
            if self.conn is None or not self.conn.is_connected():
                print("⚠️  Conexión perdida. Reconectando a MySQL...")
                self._connect()
            else:
                self.conn.ping(reconnect=True, attempts=3, delay=2)
                self.cursor = self.conn.cursor(dictionary=True)
        except mysql.connector.Error:
            print("⚠️  Ping fallido. Forzando reconexión...")
            self._connect()

    def get_schema(self) -> str:
        """Obtiene el esquema de todas las tablas. Usa caché en memoria para evitar consultas repetitivas a Aiven."""
        if self._cached_schema:
            return self._cached_schema
        try:
            self._ensure_connection()
            self.cursor.execute("SHOW TABLES")
            tables = [list(row.values())[0] for row in self.cursor.fetchall()]

            schema = "ESQUEMA DE LA BASE DE DATOS MySQL:\n\n"

            # --- PROTECCIÓN: Columnas que NO se enviarán al agente IA ---
            sensitive_columns = {'password', 'contraseña', 'contrasena', 'jwt', 'token', 'hash', 'secret'}

            for table in tables:
                self._ensure_connection()
                self.cursor.execute(f"DESCRIBE {table}")
                columns = self.cursor.fetchall()

                schema += f"Tabla: {table}\n"
                for col in columns:
                    field = col['Field']

                    if field.lower() in sensitive_columns:
                        continue

                    col_type = col['Type']
                    null = col['Null']
                    key = col['Key']
                    extra = col['Extra']

                    constraints = []
                    if key == 'PRI':
                        constraints.append('PRIMARY KEY')
                    if null == 'NO':
                        constraints.append('NOT NULL')
                    if extra:
                        constraints.append(extra)

                    constraint_str = f" ({', '.join(constraints)})" if constraints else ""
                    schema += f"  - {field}: {col_type}{constraint_str}\n"

                self.cursor.execute(f"SELECT COUNT(*) as count FROM {table}")
                count = self.cursor.fetchone()['count']
                schema += f"  Total de registros: {count}\n\n"

            self._cached_schema = schema  # Almacenar en caché
            return schema
        except mysql.connector.Error as e:
            return f"Error al obtener esquema: {e}"

    def execute(self, sql: str) -> List[Dict[str, Any]]:
        """
        Ejecuta una consulta SQL SELECT y retorna los resultados.
        Reintenta una vez si la conexión se perdió.
        """
        sql_upper = sql.strip().upper()
        if not sql_upper.startswith('SELECT'):
            return [{"error": "Solo se permiten consultas SELECT"}]

        try:
            self._ensure_connection()
            self.cursor.execute(sql)
            results = self.cursor.fetchall()
            return results if results else []

        except mysql.connector.Error as e:
            if e.errno in (2006, 2013, 2055):
                print(f"⚠️  Conexión perdida durante la consulta. Reintentando...")
                try:
                    self._connect()
                    self.cursor.execute(sql)
                    results = self.cursor.fetchall()
                    return results if results else []
                except mysql.connector.Error as retry_error:
                    return [{"error": str(retry_error)}]
            return [{"error": str(e)}]

    def execute_write(self, sql: str) -> Dict[str, Any]:
        """
        Ejecuta una consulta de ESCRITURA (INSERT, UPDATE).
        """
        sql_upper = sql.strip().upper()
        if sql_upper.startswith('SELECT'):
            return {"error": "Esta función es solo para INSERT, UPDATE o DELETE."}

        try:
            self._ensure_connection()
            self.cursor.execute(sql)
            self.conn.commit()

            rows_affected = self.cursor.rowcount

            if sql_upper.startswith('INSERT'):
                return {"success": True, "message": f"Inserción completada. {rows_affected} fila(s) afectada(s)."}
            elif sql_upper.startswith('UPDATE'):
                return {"success": True, "message": f"Actualización completada. {rows_affected} fila(s) afectada(s)."}
            elif sql_upper.startswith('DELETE'):
                return {"success": True, "message": f"Eliminación completada. {rows_affected} fila(s) afectada(s)."}
            else:
                return {"success": True, "message": f"Operación completada. {rows_affected} fila(s) afectada(s)."}

        except mysql.connector.Error as e:
            try:
                self.conn.rollback()
            except Exception:
                pass
            return {"error": str(e)}

    def test_connection(self) -> bool:
        """Prueba la conexión a la base de datos."""
        try:
            self._ensure_connection()
            self.cursor.execute("SELECT 1")
            return True
        except mysql.connector.Error:
            return False

    def close(self):
        """Cierra la conexión."""
        if self.cursor:
            self.cursor.close()
        if self.conn:
            self.conn.close()
        print("🔌 Conexión MySQL cerrada")
