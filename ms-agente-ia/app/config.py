"""
Configuración simple del agente con soporte MySQL
"""
import os
from pathlib import Path
from dotenv import load_dotenv

# Cargar variables de entorno desde el archivo .env
load_dotenv()

# Directorios
BASE_DIR = Path(__file__).parent.parent  # raíz del proyecto
DATA_DIR = BASE_DIR / "data"
DATA_DIR.mkdir(exist_ok=True)

# API Keys
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
GEMINI_MODEL = "gemini-2.5-flash"

# ========== CONFIGURACIÓN MySQL (INVENTARIO) ==========
MYSQL_CONFIG = {
    'host': os.getenv('MYSQL_HOST', 'localhost'),
    'user': os.getenv('MYSQL_USER', 'root'),
    'password': os.getenv('MYSQL_PASSWORD'),
    'database': os.getenv('MYSQL_DATABASE', 'defaultdb'),
    'port': int(os.getenv('MYSQL_PORT', '3306'))
}

# ========== CONFIGURACIÓN POSTGRESQL (VENTAS) ==========
POSTGRES_CONFIG = {
    'host': os.getenv('POSTGRES_HOST', 'localhost'),
    'user': os.getenv('POSTGRES_USER', 'postgres'),
    'password': os.getenv('POSTGRES_PASSWORD'),
    'database': os.getenv('POSTGRES_DATABASE', 'defaultdb'),
    'port': int(os.getenv('POSTGRES_PORT', '5432'))
}

# Tipo de base de datos a usar: 'sqlite' | 'mysql' | 'multi'
DATABASE_TYPE = os.getenv('DATABASE_TYPE', 'multi')
