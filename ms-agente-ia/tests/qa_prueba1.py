"""
Auditoría QA - Prueba 1: Total facturado con estado COMPLETADA (PostgreSQL)
"""
import sys
import os

# Apunta la raíz del proyecto para que los imports de `app.*` funcionen
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from dotenv import load_dotenv
load_dotenv()

from app.config import POSTGRES_CONFIG
from app.tools.postgres_tool import PostgresTool
import json
from decimal import Decimal


class DecEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, Decimal): return float(o)
        return super().default(o)


p = PostgresTool(**POSTGRES_CONFIG)

sql = "SELECT SUM(total) AS total_vendido, COUNT(*) AS num_facturas FROM ventas WHERE estado = 'COMPLETADA'"
print(f"SQL => {sql}")
r = p.execute(sql)
print(f"RESULTADO => {json.dumps(r, cls=DecEncoder, ensure_ascii=False, indent=2)}")
p.close()
