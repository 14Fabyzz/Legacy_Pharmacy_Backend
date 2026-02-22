# Plan para Integrar PostgreSQL (Microservicio de Ventas)

Este documento guarda la planificación futura para conectar el agente a una segunda base de datos (PostgreSQL), permitiéndole atender consultas de los módulos de Inventario (MySQL) y Ventas simultáneamente.

## 1. Módulos y Herramientas Necesarias

### [NUEVO] `tools/postgres_tool.py`
Se creará una herramienta equivalente a `MySQLTool` pero diseñada específicamente para conectarse a PostgreSQL.
*   **Dependencia:** Necesitaremos instalar `psycopg2-binary` (el conector de Python para PostgreSQL).
*   **Funcionalidad:** Métodos `_connect()`, `execute()`, y una adaptación de `get_schema()` específicamente escrita para leer el catálogo de tablas de PostgreSQL (`information_schema`).

### [MODIFICAR] `config.py`
Se añadirán las variables de entorno y el diccionario de configuración para la nueva base de datos PostgreSQL.
*   Añadir `POSTGRES_CONFIG = {...}` (Host, User, Password, Database, Port).
*   Cambiar `DATABASE_TYPE` para que pueda soportar un modo combinado, por ejemplo: `DATABASE_TYPE = 'microservices'`.

## 2. Lógica del Agente (El "Cerebro")

### [MODIFICAR] `agent.py`
Esta es la parte más crítica. El agente debe ser capaz de "enrutar" la pregunta del usuario hacia la base de datos correcta.

*   **Paso 2.1: Inicialización Multi-Herramienta:** 
    Modificar el `__init__` para que, si el modo es `microservices`, inicialice *tanto* `MySQLTool` como `PostgresTool` y las guarde en el diccionario `self.tools` (ej. `self.tools['inventory_db']` y `self.tools['sales_db']`).

*   **Paso 2.2: Enrutador Inteligente (Router LLM):**
    Añadiremos un paso previo muy rápido usando Gemini:
    *   *Prompt Router:* "Tienes dos bases de datos: 1. MySQL (Tiene productos, stock). 2. Postgres (Tiene ventas). Responde solo con 'MYSQL' o 'POSTGRES' dependiendo de qué base de datos tiene la información para responder esta pregunta: [Pregunta]".

*   **Paso 2.3: Generación de SQL Dinámica:**
    *   Si el router dice `MYSQL`, inyectamos el esquema de MySQL.
    *   Si el router dice `POSTGRES`, inyectamos el esquema de Postgres (recibido posteriormente) y le pedimos SQL (dialecto PostgreSQL).

---

## ⏳ Requisitos Pendientes para Iniciar:
- [ ] Proveer al Agente el script SQL de la estructura de tablas de Ventas (PostgreSQL).
