"""
Script de pruebas para la arquitectura Multi-Datasource (MySQL + PostgreSQL).
No requiere API Key de Gemini. Prueba directamente la capa de datos.

Ejecutar desde la raíz del proyecto:
    python -m tests.test_multi_datasource
"""
import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from dotenv import load_dotenv
load_dotenv()

from app.config import MYSQL_CONFIG, POSTGRES_CONFIG
from app.tools.mysql_tool import MySQLTool
from app.tools.postgres_tool import PostgresTool

# ============================================================
# COLORES PARA LA CONSOLA
# ============================================================
GREEN  = "\033[92m"
RED    = "\033[91m"
YELLOW = "\033[93m"
CYAN   = "\033[96m"
RESET  = "\033[0m"
BOLD   = "\033[1m"

passed = 0
failed = 0


def run_test(name: str, test_fn):
    global passed, failed
    print(f"\n{CYAN}{BOLD}  🔬 TEST: {name}{RESET}")
    try:
        result = test_fn()
        if result:
            print(f"  {GREEN}✅ PASSED{RESET}")
            passed += 1
        else:
            print(f"  {RED}❌ FAILED (retornó False){RESET}")
            failed += 1
    except Exception as e:
        print(f"  {RED}❌ FAILED con excepción: {e}{RESET}")
        failed += 1


# ============================================================
# BLOQUE 1: TESTS DE MYSQL (INVENTARIO)
# ============================================================
print(f"\n{BOLD}{YELLOW}{'='*60}")
print(f"  BLOQUE 1: DB1 - INVENTARIO (MySQL - Aiven)")
print(f"{'='*60}{RESET}")

mysql_tool = None
try:
    mysql_tool = MySQLTool(**MYSQL_CONFIG)
except Exception as e:
    print(f"{RED}❌ FALLA CRÍTICA: No se pudo conectar a MySQL. Abortando bloque 1.\n   Error: {e}{RESET}")

if mysql_tool:
    def test_mysql_schema():
        schema = mysql_tool.get_schema()
        print(f"     => Primeros 300 chars del schema:\n{YELLOW}     {schema[:300]}...{RESET}")
        return "Error" not in schema and len(schema) > 50

    def test_mysql_query_productos():
        results = mysql_tool.execute("SELECT id, nombre_comercial FROM productos LIMIT 5")
        print(f"     => {len(results)} productos obtenidos: {[r.get('nombre_comercial') for r in results]}")
        return len(results) > 0 and "error" not in results[0]

    def test_mysql_security_block():
        results = mysql_tool.execute("DELETE FROM productos WHERE 1=1")
        print(f"     => Respuesta del firewall: {results}")
        return "error" in results[0]

    run_test("Obtener schema de MySQL", test_mysql_schema)
    run_test("Consultar productos (SELECT válido)", test_mysql_query_productos)
    run_test("Bloquear DELETE en MySQL (Firewall)", test_mysql_security_block)

    mysql_tool.close()

# ============================================================
# BLOQUE 2: TESTS DE POSTGRESQL (VENTAS)
# ============================================================
print(f"\n{BOLD}{YELLOW}{'='*60}")
print(f"  BLOQUE 2: DB2 - VENTAS (PostgreSQL - Aiven)")
print(f"{'='*60}{RESET}")

pg_tool = None
try:
    pg_tool = PostgresTool(**POSTGRES_CONFIG)
except Exception as e:
    print(f"{RED}❌ FALLA CRÍTICA: No se pudo conectar a PostgreSQL. Abortando bloque 2.\n   Error: {e}{RESET}")

if pg_tool:
    def test_pg_schema():
        schema = pg_tool.get_schema()
        print(f"     => Primeros 300 chars del schema:\n{YELLOW}     {schema[:300]}...{RESET}")
        return "Error" not in schema and len(schema) > 50

    def test_pg_query_ventas():
        results = pg_tool.execute("SELECT id, numero_factura, total FROM ventas LIMIT 5")
        print(f"     => {len(results)} ventas obtenidas: {[r.get('numero_factura') for r in results]}")
        return len(results) >= 0 and (not results or "error" not in results[0])

    def test_pg_security_regex_delete():
        results = pg_tool.execute("DELETE FROM ventas WHERE 1=1")
        print(f"     => Bloqueo DELETE: {results[0]}")
        return "error" in results[0] and "Solo lectura" in results[0]["error"]

    def test_pg_security_regex_drop():
        results = pg_tool.execute("DROP TABLE ventas")
        print(f"     => Bloqueo DROP: {results[0]}")
        return "error" in results[0] and "Solo lectura" in results[0]["error"]

    def test_pg_security_regex_update():
        results = pg_tool.execute("UPDATE ventas SET total=0 WHERE 1=1")
        print(f"     => Bloqueo UPDATE: {results[0]}")
        return "error" in results[0] and "Solo lectura" in results[0]["error"]

    def test_pg_security_must_select():
        results = pg_tool.execute("EXEC sp_hack()")
        print(f"     => Bloqueo NON-SELECT: {results[0]}")
        return "error" in results[0]

    def test_pg_auto_limit():
        results = pg_tool.execute("SELECT id FROM ventas")
        print(f"     => Filas devueltas (auto-LIMIT): {len(results)} (debería ser ≤ 50)")
        return len(results) <= 50

    run_test("Obtener schema de PostgreSQL", test_pg_schema)
    run_test("Consultar ventas (SELECT válido)", test_pg_query_ventas)
    run_test("Bloquear DELETE en PostgreSQL (Firewall Regex)", test_pg_security_regex_delete)
    run_test("Bloquear DROP en PostgreSQL (Firewall Regex)", test_pg_security_regex_drop)
    run_test("Bloquear UPDATE en PostgreSQL (Firewall Regex)", test_pg_security_regex_update)
    run_test("Bloquear EXEC no SELECT (Firewall Directo)", test_pg_security_must_select)
    run_test("Auto-LIMIT de protección (sin LIMIT explícito)", test_pg_auto_limit)

    pg_tool.close()

# ============================================================
# RESUMEN FINAL
# ============================================================
total = passed + failed
print(f"\n{BOLD}{'='*60}")
print(f"  RESUMEN FINAL: {passed}/{total} tests PASSED")
print(f"{'='*60}{RESET}")
if failed == 0:
    print(f"{GREEN}{BOLD}  🎉 ¡TODOS LOS TESTS PASARON! Arquitectura Multi-Datasource OPERATIVA.{RESET}\n")
else:
    print(f"{RED}{BOLD}  ⚠️  {failed} test(s) fallaron. Revisar Output arriba.{RESET}\n")
