"""
Auditoría QA - Prueba 2: Cruce Maestro MySQL + PostgreSQL
Paso 1: Buscar ID del producto en MySQL
Paso 2: Usar ese ID para sumar unidades vendidas en PostgreSQL
"""
import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from dotenv import load_dotenv
load_dotenv()

from app.config import MYSQL_CONFIG, POSTGRES_CONFIG
from app.tools.mysql_tool import MySQLTool
from app.tools.postgres_tool import PostgresTool
import json
from decimal import Decimal


class DecEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, Decimal): return float(o)
        return super().default(o)


# --------- PASO 1: DB1 - MySQL (Inventario) ---------
print("\n=== PASO 1: DB1_INVENTARIO (MySQL) ===")
m = MySQLTool(**MYSQL_CONFIG)

sql_mysql = "SELECT id, nombre_comercial FROM productos WHERE nombre_comercial LIKE '%Acetaminof%' LIMIT 10"
print(f"SQL MySQL => {sql_mysql}")
productos = m.execute(sql_mysql)
print(f"RESULTADO MySQL => {json.dumps(productos, cls=DecEncoder, ensure_ascii=False, indent=2)}")
m.close()

if not productos or "error" in productos[0]:
    print("No se encontró el producto en MySQL. Abortando Paso 2.")
    exit(1)

ids = [str(p["id"]) for p in productos]
ids_str = ",".join(ids)
print(f"\n=> IDs del producto extraídos del Inventario: [{ids_str}]")

# --------- PASO 2: DB2 - PostgreSQL (Ventas) ---------
print("\n=== PASO 2: DB2_VENTAS (PostgreSQL) ===")
pg = PostgresTool(**POSTGRES_CONFIG)

sql_pg = f"SELECT SUM(cantidad) AS total_unidades_vendidas, COUNT(dv.id) AS num_lineas_venta FROM detalle_ventas dv WHERE dv.producto_id IN ({ids_str})"
print(f"SQL PostgreSQL => {sql_pg}")
resultado = pg.execute(sql_pg)
print(f"RESULTADO PostgreSQL => {json.dumps(resultado, cls=DecEncoder, ensure_ascii=False, indent=2)}")
pg.close()
