# 🤖 Documentación: Agente de Inteligencia de Datos (Legacy Pharmacy)

## 1. ¿Cómo funciona el Agente?

Este proyecto utiliza el patrón arquitectónico conocido como **Text-to-SQL (Texto a SQL)** potenciado por Modelos de Lenguaje Grande (LLMs) –específicamente el modelo Gemini de Google– y el protocolo de interacción MCP (Model Context Protocol).

El flujo de trabajo exacto del agente ocurre en **tres etapas (pasos)** cada vez que el usuario hace una pregunta:

1.  **Interpretación y Generación (Paso 1):**
    *   El usuario escribe una pregunta en español (ej: *"¿Cuántos productos hay?"*).
    *   El agente toma esta pregunta y se la envía a la IA (Gemini).
    *   Junto con la pregunta, el agente le inyecta a la IA un *"System Prompt"* muy detallado. Este prompt contiene el **esquema completo de la base de datos** (todas las tablas, vistas y columnas disponibles) y las reglas de negocio de la droguería.
    *   La IA analiza la petición y devuelve estrictamente una **consulta SQL** válida para obtener ese dato.

2.  **Ejecución y Autocorrección (Paso 2):**
    *   El agente toma esa consulta SQL y usa su *"Herramienta"* (`mysql_tool.py`) para conectarse directamente a la base de datos MySQL (ya sea en localhost o en Aiven Cloud) y ejecutar el código.
    *   **Mecanismo de Autocorrección:** Si el SQL generado tiene un error de sintaxis o de lógica, la base de datos devuelve un fallo. El agente atrapa este error, se lo reenvía a la IA diciendo *"Tu SQL falló por este motivo, por favor corrígelo"* y vuelve a intentar la ejecución automáticamente de forma transparente para el usuario.
    *   También previene interacciones peligrosas: Si la IA intenta hacer un `INSERT`, `UPDATE` o `DELETE`, el agente interceptará esa acción y pedirá confirmación humana (ya configurado en `api.py`).

3.  **Presentación (Paso 3):**
    *   La base de datos devuelve los datos crudos (ej: un número, o cientos de filas JSON).
    *   El agente le envía de nuevo a la IA la pregunta original + el SQL generado + los datos obtenidos y le dice: *"Analiza esto y decide la mejor forma de mostrárselo al usuario"*.
    *   La IA clasifica la respuesta y devuelve el dato formateado ya sea como texto plano (para conteos simples), como datos para una tabla gráfica (para enumerar listas cortas) o como un gráfico estadístico (para comparar ventas, fechas, etc.), lo cual luego renderiza el Frontend web en el Dashboard.

---

## 2. ¿Por qué es innovador?

Las razones por las que este agente destaca frente a un desarrollo tradicional (software monolito clásico) son:

*   **Democratización de la Data (Cero Código para el usuario final):** Los administradores, gerentes o farmaceutas ya no necesitan pedirle al equipo de desarrollo que programe un "Reporte Nuevo" ni aprender SQL. Simplemente le "hablan" al sistema.
*   **Conocimiento del Negocio Embebido:** No es un ChatGPT genérico. Gracias al contexto inyectado en `agent.py`, el agente "sabe" qué es un lote, entiende la diferencia entre *v_stock_productos* y *v_semaforo_vencimientos*, y maneja lógicas complejas de la farmacia (como el control por fraccionamiento, ivas o utilidades).
*   **Decisión Dinámica de Interfaz:** En el desarrollo de software tradicional, una pantalla siempre muestra un gráfico o siempre muestra una tabla; las vistas son estáticas. Este agente **decide en tiempo real** qué componente de interfaz de usuario es óptimo para la data solicitada y lo renderiza de manera dinámica a través de la API y el JS embebido.
*   **Auto-reparación (Self-healing):** Al poseer un ciclo de corrección de excepciones SQL automático, la tasa de éxito de la traducción Text-to-SQL se incrementa muchísimo en comparación a herramientas estándar. Ahorra caídas repentinas o reportes vacíos.

---

## 3. ¿Qué se le puede mejorar para hacerlo mucho más de "Clase Mundial"?

A pesar de ser muy funcional, el agente aún tiene oportunidades de volverse una herramienta mucho más robusta para producción. Aquí hay algunas mejoras técnicas clave:

**A. Mejoras de Inteligencia Artificial (Prompting y RAG)**
*   **RAG de Catálogos (Retrieval-Augmented Generation):** Actualmente, si el usuario hace una falta de ortografía (ej: pregunta por "doles" en vez de "dolex"), la base de datos MySQL arrojará cero resultados. Si se incluye un pequeño motor de búsqueda semántica primero (RAG o una búsqueda *Fuzzy*), el agente corregiría el nombre del producto *antes* de armar la consulta SQL.
*   **Análisis Predictivo Integral:** Hoy realiza análisis descriptivo (qué pasó), pero el agente podría entrenarse para tener **Análisis Prescriptivo y Predictivo**. Por ejemplo, predecir cuándo se quedará sin inventario con base a tendencias de datos del último mes en movimientos (Kardex).

**B. Mejoras de Arquitectura y Rendimiento**
*   **Caché de Consultas SQL (Redis/Memcached):** Interacciones comunes, como "¿Cuál es el producto más vendido del mes?", siempre van a generar el mismo código SQL y forzar a la BD. Es recomendable introducir una capa de almacenamiento en caché para las consultas textuales muy repetidas para evitar gastar *tokens* ("dinero") de la API de Google y tiempo de ejecución.
*   **Limitación y Paginación Fuerte (Defensa de Memoria):** Cuando un usuario pida un *"Listado completo de todas las ventas desde inicio de año"*, la base de datos podría devolver millones de registros, saturando el servidor Python o matando el navegador del usuario al intentar inyectar la tabla. El agente de IA debería estar estrictamente configurado para forzar bloques *`LIMIT 100 OFFSET...`* al final de cada consulta y crear paginación.

**C. Mejoras en la Seguridad**
*   **RBAC Dinámico (Control Basado en Rol Cero-Confianza):** El sistema actualmente permite que el agente intente leer cualquier tabla basándose en lo que pida el humano. Debería mejorarse el *Contexto Inicial*, enviándole a la API quién es el Usuario en línea y que el Agente mismo rechace preguntas financieras si detecta que la persona conectada al chat no es un "ADMIN", incluso antes de intentar armar el SQL.
*   **Modo Estricto de "Solo Lectura":** Restringir el usuario `avnadmin` o `root` en la configuración real de base de datos MySQL por un **usuario específico de solo-lectura** que tenga permisos bloqueados de fábrica a sentencias UPDATE/INSERT/DELETE/DROP en el lado del motor de BD (como salvaguarda paralela a la prevención hecha hoy en el código Python).

**D. Experiencia de Usuario (UI/UX)**
*   **Sugerencias de Prompting "Zero-cold-start":** Al entrar al sistema, se podrían mostrar unas etiquetas pre-cargadas y actualizadas como "Consultar stock bajo" o "Revisar vencimientos a 30 días" (inspirado en la UI nativa del dashboard que implementaste) para quitarle al humano la carga de tener que inventar comandos útiles desde cero.
