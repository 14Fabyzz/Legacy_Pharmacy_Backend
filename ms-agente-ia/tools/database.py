"""
Herramienta para trabajar con bases de datos
"""
import sqlite3
from typing import List, Dict, Any


class DatabaseTool:
    """Herramienta para consultar bases de datos"""
    
    def __init__(self, db_path: str):
        self.db_path = db_path
        self.conn = sqlite3.connect(db_path)
        self.cursor = self.conn.cursor()
    
    def get_schema(self) -> str:
        """Obtiene el esquema de la BD"""
        self.cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
        tables = self.cursor.fetchall()
        
        schema = "ESQUEMA DE LA BASE DE DATOS:\n\n"
        for (table,) in tables:
            self.cursor.execute(f"PRAGMA table_info({table});")
            columns = self.cursor.fetchall()
            schema += f"Tabla: {table}\n"
            for col in columns:
                schema += f"  - {col[1]} ({col[2]})\n"
            schema += "\n"
        
        return schema
    
    def execute(self, sql: str) -> List[Dict[str, Any]]:
        """
        Ejecuta SQL y retorna resultados
        
        Args:
            sql: Consulta SQL
        
        Returns:
            Lista de diccionarios con los resultados
        """
        try:
            self.cursor.execute(sql)
            columns = [desc[0] for desc in self.cursor.description]
            rows = self.cursor.fetchall()
            
            return [dict(zip(columns, row)) for row in rows]
        except Exception as e:
            return [{"error": str(e)}]
    
    def close(self):
        """Cierra la conexión"""
        self.conn.close()