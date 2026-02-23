import os
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_community.utilities import SQLDatabase
from config import GEMINI_API_KEY, MYSQL_CONFIG, GEMINI_MODEL

# 1. Configuración de Entorno
# LangChain busca la API Key en las variables de entorno por defecto,
# pero también podemos pasarla directo.
os.environ["GOOGLE_API_KEY"] = GEMINI_API_KEY

class LangChainAgent:
    def __init__(self):
        print("🔗 Inicializando LangChain Agent...")
        
        # --- A. CONEXIÓN A BASE DE DATOS (La Magia de LangChain) ---
        # LangChain usa SQLAlchemy. La cadena de conexión para MySQL es:
        # mysql+pymysql://usuario:password@host:port/nombre_db
        
        user = MYSQL_CONFIG["user"]
        password = MYSQL_CONFIG["password"]
        host = MYSQL_CONFIG["host"]
        port = MYSQL_CONFIG["port"]
        database = MYSQL_CONFIG["database"]
        
        uri = f"mysql+pymysql://{user}:{password}@{host}:{port}/{database}"
        
        print(f"📊 Conectando a BD: {database} en {host}...")
        
        # SQLDatabase es un wrapper inteligente. 
        # 'sample_rows_in_table_info=0' evita que lea datos reales al escanear tablas (por privacidad/seguridad)
        self.db = SQLDatabase.from_uri(uri, sample_rows_in_table_info=0)
        
        # --- B. EL CEREBRO (Gemini) ---
        # Usamos la clase oficial. 'temperature=0' es vital para SQL (queremos precisión, no creatividad).
        self.llm = ChatGoogleGenerativeAI(
            model=GEMINI_MODEL,
            temperature=0,
            convert_system_message_to_human=True # Ayuda a veces con modelos de Google
        )
        
        print("✅ Agente LangChain base inicializado correctamente.")

    def test_connection(self):
        """Prueba rápida para ver si LangChain puede ver tus tablas"""
        try:
            # .get_usable_table_names() es un método mágico de LangChain
            tablas = self.db.get_usable_table_names()
            print(f"\nTablas detectadas automáticamente por LangChain: {tablas}")
            return True
        except Exception as e:
            print(f"❌ Error conectando: {e}")
            return False

# --- BLOQUE DE PRUEBA ---
if __name__ == "__main__":
    agent = LangChainAgent()
    agent.test_connection()