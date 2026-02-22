"""
Programa principal - Agente MCP
Se conecta directamente a la base de datos real especificada en config.py
"""
from agent import MCPAgent
from config import (
    GEMINI_API_KEY, 
    GEMINI_MODEL, 
    MYSQL_CONFIG,
    DATABASE_TYPE
)

def main():
    """Función principal"""
    
    print("=" * 80)
    print("🤖 AGENTE MCP - CONECTANDO A BASE DE DATOS REAL")
    print("=" * 80)
    
    if DATABASE_TYPE != 'mysql':
        print(f"❌ Error: Este agente está configurado para usar SOLO MySQL.")
        print(f"Asegúrate que DATABASE_TYPE en config.py sea 'mysql'")
        return
        
    print(f"\n📊 Conectando a: {MYSQL_CONFIG['database']} en {MYSQL_CONFIG['host']}...")
    
    try:
        # Crear agente
        # La conexión a MySQL se intenta automáticamente al crear el agente
        agente = MCPAgent(
            api_key=GEMINI_API_KEY,
            model_name=GEMINI_MODEL,
            db_type='mysql',
            mysql_config=MYSQL_CONFIG
        )
        
    except Exception as e:
        print(f"\n❌❌ ERROR CRÍTICO AL CONECTAR CON MYSQL ❌❌")
        print(f"Error: {e}")
        print("\n💡 Revisa que:")
        print(f"   1. El servidor MySQL esté corriendo en '{MYSQL_CONFIG['host']}:{MYSQL_CONFIG['port']}'.")
        print(f"   2. El usuario '{MYSQL_CONFIG['user']}' y la contraseña sean correctos.")
        print(f"   3. La base de datos '{MYSQL_CONFIG['database']}' exista.")
        return

    print("=" * 80)
    print("✅ AGENTE CONECTADO Y LISTO")
    print("=" * 80)
    
    # Modo interactivo
    print("\n💬 Modo interactivo (escribe 'salir' para terminar)")
    print("   (Ahora puedes hacer preguntas sobre tus tablas reales de 'drogueria4')\n")
    
    while True:
        try:
            pregunta = input("👤 Tú: ").strip()
            
            if not pregunta:
                continue
            
            if pregunta.lower() == 'salir':
                print("\n👋 ¡Hasta luego!")
                break
            
            if pregunta.lower() == 'contexto':
                print(f"\n📊 {agente.get_context_summary()}\n")
                continue
            
            if pregunta.lower() == 'limpiar':
                agente.clear_context()
                continue
            
            # Preguntar al agente
            respuesta = agente.ask(pregunta)
            print(f"\n🤖 Agente: {respuesta}\n")
            print("-" * 80)
            
        except KeyboardInterrupt:
            print("\n\n👋 ¡Hasta luego!")
            break
        except Exception as e:
            print(f"\n❌ Error inesperado: {e}\n")
    
    # Cerrar
    agente.close()


if __name__ == "__main__":
    main()