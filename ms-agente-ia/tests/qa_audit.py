"""
Auditoría QA - Prueba 1 + 2 combinadas
Escribe resultados en JSON para evitar problemas de encoding en consola Windows.
"""
import sys, os, json
from decimal import Decimal
from datetime import date, datetime

sys.stdout.reconfigure(encoding='utf-8') if hasattr(sys.stdout, 'reconfigure') else None

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from dotenv import load_dotenv
load_dotenv()

from app.config import MYSQL_CONFIG, POSTGRES_CONFIG
from app.tools.mysql_tool import MySQLTool
from app.tools.postgres_tool import PostgresTool


class DecEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, Decimal): return float(o)
        if isinstance(o, (date, datetime)): return o.isoformat()
        return super().default(o)


output = {}

# ============================================================
# PRUEBA 1: Total ventas COMPLETADAS (PostgreSQL)
# ============================================================
pg = PostgresTool(**POSTGRES_CONFIG)
sql_p1 = "SELECT SUM(total) AS total_vendido, COUNT(*) AS num_facturas FROM ventas WHERE estado = 'COMPLETADA'"
resultado_p1 = pg.execute(sql_p1)
output["prueba1"] = {
    "db": "DB2_VENTAS (PostgreSQL)",
    "sql": sql_p1,
    "resultado": resultado_p1
}

# ============================================================
# PRUEBA 2-A: Buscar Acetaminofen en MySQL
# ============================================================
m = MySQLTool(**MYSQL_CONFIG)
sql_m1 = "SELECT id, nombre_comercial FROM productos WHERE nombre_comercial LIKE '%Acetaminof%' LIMIT 10"
productos = m.execute(sql_m1)
m.close()

output["prueba2_paso1"] = {
    "db": "DB1_INVENTARIO (MySQL)",
    "sql": sql_m1,
    "resultado": productos
}

# ============================================================
# PRUEBA 2-B: Usar IDs en PostgreSQL
# ============================================================
ids = [str(p["id"]) for p in productos if "id" in p and "error" not in p]
ids_str = ",".join(ids)

sql_p2 = f"SELECT SUM(cantidad) AS total_unidades_vendidas, COUNT(dv.id) AS num_lineas_venta FROM detalle_ventas dv WHERE dv.producto_id IN ({ids_str})"
resultado_p2 = pg.execute(sql_p2) if ids else [{"error": "No se encontraron IDs de producto en MySQL"}]
pg.close()

output["prueba2_paso2"] = {
    "db": "DB2_VENTAS (PostgreSQL)",
    "ids_puente": ids,
    "sql": sql_p2,
    "resultado": resultado_p2
}

# Escribir resultado a fichero JSON
output_path = os.path.join(os.path.dirname(__file__), "qa_resultados.json")
with open(output_path, "w", encoding="utf-8") as f:
    json.dump(output, f, cls=DecEncoder, ensure_ascii=False, indent=2)

print(f"OK - Resultados escritos en {output_path}")
