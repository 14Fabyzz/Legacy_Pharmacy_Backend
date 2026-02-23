# Guía de Migración: Agente IA como Microservicio

Este documento detalla los pasos y conceptos necesarios para mudar el Agente de Python (FastAPI) a tu ecosistema de microservicios existente, prepararlo para Docker, conectarlo a tu API Gateway y consumirlo desde tu frontend en Angular.

## 1. Estructura de Carpetas (El "Mover")

Cuando muevas esta carpeta (`Agente009`) a tu repositorio de microservicios, la estructura ideal debería verse algo así:

```text
tu-monorepo-backend/
├── ms-usuarios/           # (Tu microservicio existente 1)
├── ms-inventario/         # (Tu microservicio existente 2)
├── api-gateway/           # (Tu enrutador principal, ej. Spring Cloud Gateway)
└── ms-agente-ia/          # <--- (ESTA ES LA CARPETA Agente009 RENOMBRADA)
    ├── agent.py
    ├── api.py             # Archivo principal de FastAPI
    ├── config.py
    ├── requirements.txt
    ├── Dockerfile         # (Por crear)
    └── .env               # (No subir al repo, inyectar en Azure)
```

**⚠️ CRÍTICO AL MOVER:** NO copies la carpeta `venv/` (el entorno virtual de tu computadora local) ni la carpeta `__pycache__/`. Solo copia los archivos de código fuente.

---

## 2. Archivo Dockerfile (Para Azure / Contenedores)

Para que tu agente corra en cualquier lugar sin importar si tiene Python instalado, crearemos un contenedor. Este archivo vivirá dentro de la carpeta `ms-agente-ia/` (junto a `api.py`):

```dockerfile
# Usa una imagen oficial de Python ligera
FROM python:3.11-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de requerimientos primero (para aprovechar caché de Docker)
COPY requirements.txt .

# Instala las dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Copia el resto del código del agente
COPY . .

# Expone el puerto que usa FastAPI internamente
EXPOSE 8000

# Comando para iniciar el servidor cuando el contenedor arranque
CMD ["uvicorn", "api:app", "--host", "0.0.0.0", "--port", "8000"]
```

---

## 3. Configuración del API Gateway

Tu frontend (Angular) no debe hablar directo con Python (puerto 8000). Angular debe hablar con el Gateway (ej. puerto 8080), y el Gateway redirige el tráfico. 

Si usas **Spring Cloud Gateway** (muy común en Java), tendrás que agregar una ruta en su `application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: agente-ia-service
          uri: http://ms-agente-ia:8000  # <--- Nombre del contenedor Docker y puerto de FastAPI
          predicates:
            - Path=/api/v1/chatbot/**    # <--- La ruta que el Frontend llamará
          filters:
            - RewritePath=/api/v1/chatbot/(?<segment>.*), /$\{segment} # Borra el prefijo y envía solo "/ask" a Python
```

*Con esta configuración, si Angular hace un POST a `http://localhost:8080/api/v1/chatbot/ask`, el Gateway se lo enviará al contenedor de Python a `http://ms-agente-ia:8000/ask`.*

---

## 4. Consumo en Angular (Frontend)

Una vez que el Gateway está configurado, crearás un servicio en Angular genérico:

```typescript
// chatbot.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  // Ahora apuntas al Gateway, no al puerto de Python directo
  private apiUrl = 'http://localhost:8080/api/v1/chatbot/ask'; 

  constructor(private http: HttpClient) { }

  askQuestion(question: string): Observable<any> {
    return this.http.post<any>(this.apiUrl, { question: question });
  }
}
```

Luego, construirás un Componente Global (`chatbot.component.ts`) que contenga el HTML del botón flotante y el historial de chat, usando este servicio para enviar y recibir los mensajes que genera la IA.

---

## Próximos Pasos (Checklist)

1. Cierra tu entorno aquí.
2. Mueve los archivos físicos a tu otro proyecto.
3. Abre tu otro proyecto ("el monorepo") en VS Code o tu editor.
4. Llámame en esa nueva ventana de chat.
5. Iniciaremos creando el `Dockerfile`, configurando el Gateway que uses, y adaptando el código de `api.py` si es estrictamente necesario para ajustarse a las políticas de tu red interna.
