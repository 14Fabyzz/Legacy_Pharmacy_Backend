# 📘 Guía de Inicio - Agente 009

¡Hola! Si has recibido este proyecto comprimido, sigue estos pasos para configurarlo y ejecutarlo en tu computadora.

## 📋 1. Requisitos Previos

Antes de empezar, asegúrate de tener instalado:
1.  **Python 3.10 o superior**: [Descargar Python](https://www.python.org/downloads/) (Al instalar, asegúrate de marcar la casilla **"Add Python to PATH"**).
2.  **MySQL Server**: Debes tener tu servidor de base de datos activo (XAMPP, WAMP, Workbench, etc.).

## ⚙️ 2. Configuración Inicial

1.  **Descomprimir**: Extrae todos los archivos del `.zip` o `.rar` en una carpeta.
2.  **Abrir Terminal**: Entra a la carpeta del proyecto, haz clic derecho en un espacio vacío y selecciona "Abrir en Terminal" (o usa CMD/PowerShell navegando a la carpeta).

## 📦 3. Instalación de Librerías

Ejecuta el siguiente comando en la terminal para instalar todas las herramientas necesarias (Inteligencia Artificial, Conexión a Base de Datos y Servidor Web):

```bash
pip install google-generativeai mysql-connector-python fastapi uvicorn
```

## 🔑 4. Configuración de Claves (Archivo `config.py`)

El proyecto necesita saber cómo conectarse a tu base de datos y a la IA de Google.

1.  Busca el archivo llamado `config.py` en la carpeta del proyecto.
2.  Ábrelo con el Bloc de Notas o tu editor de código favorito.
3.  **Modifica los siguientes valores:**

    *   `GEMINI_API_KEY`: Reemplaza el valor actual con tu propia API Key de Google.
    *   `MYSQL_CONFIG`: Ajusta los datos para que coincidan con tu base de datos local:
        *   `'user'`: Tu usuario (ej. 'root').
        *   `'password'`: Tu contraseña de MySQL.
        *   `'database'`: El nombre exacto de tu base de datos (ej. 'drogueria4' o 'legacy03').
        *   `'port'`: Generalmente 3306 (o 3307 si usas MariaDB/Docker).

4.  Guarda los cambios en el archivo.

## 🚀 5. Cómo Ejecutar

Tienes dos modos de uso disponibles:

### Opción A: Chat en Consola (Prueba Rápida)
Usa esto para verificar que el agente se conecta correctamente a la base de datos y responde preguntas.

Ejecuta:
```bash
python main.py
```

### Opción B: Servidor API (Backend)
Usa esto para levantar el servidor que alimentará la interfaz web (Dashboard).

Ejecuta:
```bash
python api.py
```
*(El servidor iniciará en http://127.0.0.1:8000)*

---
**Nota:** Si encuentras errores de conexión, verifica primero que tu servidor MySQL esté encendido y que los datos en `config.py` sean correctos.
