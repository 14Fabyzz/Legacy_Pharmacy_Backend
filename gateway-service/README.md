# 🚀 Legacy Pharmacy - API Gateway

Gateway centralizado para la arquitectura de microservicios de Legacy Pharmacy.

## 📋 Descripción

Este API Gateway actúa como punto de entrada único para todos los microservicios del sistema Legacy Pharmacy,
proporcionando:

- ✅ **Enrutamiento inteligente** hacia microservicios
- 🔐 **Autenticación JWT centralizada**
- 🛡️ **CORS configurado** para frontend
- 📊 **Circuit Breaker** para resiliencia
- 📝 **Logging de auditoría**
- ⚡ **Rate Limiting** (opcional con Redis)

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (Angular)                       │
│                   http://localhost:4200                      │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   API GATEWAY (Puerto 8080)                  │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  • Autenticación JWT                                 │   │
│  │  • Enrutamiento                                      │   │
│  │  • Circuit Breaker                                   │   │
│  │  • CORS                                              │   │
│  │  • Logging                                           │   │
│  └──────────────────────────────────────────────────────┘   │
└───┬────────────┬────────────┬────────────┬─────────────────┘
    │            │            │            │
    ▼            ▼            ▼            ▼
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│Usuarios │ │Inventar │ │ Ventas  │ │Reportes │
│  8082   │ │  8081   │ │  8083   │ │  8084   │
│ (MySQL) │ │ (MySQL) │ │(Postgre)│ │         │
└─────────┘ └─────────┘ └─────────┘ └─────────┘
```

## 🎯 Microservicios Conectados

| Microservicio  | Puerto | Base de Datos | Rutas en Gateway                     |
|----------------|--------|---------------|--------------------------------------|
| **Usuarios**   | 8082   | MySQL         | `/api/usuarios/**`, `/api/auth/**`   |
| **Inventario** | 8081   | MySQL         | `/api/inventario/**`                 |
| **Ventas**     | 8083   | PostgreSQL    | `/api/ventas/**`, `/api/facturas/**` |
| **Reportes**   | 8084   | -             | `/api/reportes/**`                   |

## 🚦 Rutas Disponibles

### 🔓 Rutas Públicas (Sin autenticación)

```
POST   /api/auth/login       → Login de usuario
POST   /api/auth/registro    → Registro de nuevo usuario
```

### 🔐 Rutas Protegidas (Requieren JWT)

#### Usuarios

```
GET    /api/usuarios              → Listar usuarios
GET    /api/usuarios/{id}         → Obtener usuario por ID
PUT    /api/usuarios/{id}         → Actualizar usuario
DELETE /api/usuarios/{id}         → Eliminar usuario
```

#### Inventario

```
GET    /api/inventario/productos           → Listar productos
POST   /api/inventario/productos           → Crear producto
GET    /api/inventario/productos/{id}      → Obtener producto
PUT    /api/inventario/productos/{id}      → Actualizar producto
DELETE /api/inventario/productos/{id}      → Eliminar producto

GET    /api/inventario/lotes               → Listar lotes
POST   /api/inventario/lotes               → Crear lote

GET    /api/inventario/categorias          → Listar categorías
POST   /api/inventario/entrada             → Entrada de mercancía
```

#### Ventas

```
GET    /api/ventas                → Listar ventas
POST   /api/ventas                → Crear venta
GET    /api/ventas/{id}           → Obtener venta
GET    /api/facturas/{id}         → Obtener factura
```

#### Reportes

```
GET    /api/reportes/ventas/{periodo}      → Reporte de ventas
GET    /api/reportes/inventario            → Reporte de inventario
GET    /api/reportes/financieros           → Reporte financiero
```

## 🔧 Instalación

### Prerrequisitos

- Java 17+
- Maven 3.8+
- Git

### Pasos

1. **Clonar el repositorio**

```bash
git clone https://github.com/14Fabyzz/Legacy_Pharmacy_Backend.git
cd gateway-service
```

2. **Configurar JWT Secret**

Editar `src/main/resources/application.yml` o usar variable de entorno:

```bash
export JWT_SECRET="tu-clave-secreta-muy-segura-minimo-256-bits"
```

⚠️ **IMPORTANTE**: El JWT_SECRET debe ser el **mismo** en todos los microservicios.

3. **Compilar**

```bash
mvn clean install
```

4. **Ejecutar**

```bash
mvn spring-boot:run
```

El Gateway estará disponible en: `http://localhost:8080`

## 📊 Monitoreo

### Actuator Endpoints

- **Health Check**: `http://localhost:8080/actuator/health`
- **Rutas Configuradas**: `http://localhost:8080/actuator/gateway/routes`
- **Métricas**: `http://localhost:8080/actuator/metrics`

### Ejemplo de respuesta Health Check

```json
{
  "status": "UP",
  "components": {
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

## 🔐 Autenticación JWT

### Flujo de Autenticación

1. **Login** (Ruta pública)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "your_password"
  }'
```

Respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "rol": "ADMIN"
}
```

2. **Usar el token** en peticiones protegidas

```bash
curl -X GET http://localhost:8080/api/inventario/productos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Headers Añadidos a Microservicios

El Gateway automáticamente añade estos headers a las peticiones autenticadas:

- `X-User-Id`: ID del usuario
- `X-Username`: Nombre de usuario
- `X-User-Role`: Rol del usuario

Los microservicios pueden usar estos headers sin necesidad de validar el JWT nuevamente.

## 🛡️ Circuit Breaker

Si un microservicio está caído o falla, el Gateway retorna:

```json
{
  "timestamp": "2024-12-16T10:30:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "El servicio solicitado no está disponible temporalmente.",
  "suggestion": "Si el problema persiste, contacte al administrador."
}
```

### Configuración del Circuit Breaker

```yaml
resilience4j:
  circuitbreaker:
    instances:
      defaultCircuitBreaker:
        sliding-window-size: 10          # Últimas 10 llamadas
        failure-rate-threshold: 50       # 50% de fallos
        wait-duration-in-open-state: 30s # Esperar 30s antes de reintentar
```

## 🌐 CORS

Configurado para permitir peticiones desde:

- `http://localhost:4200` (Angular)
- `http://localhost:3000` (React)

Modificar en `application.yml` según necesidad.

## 📝 Logging

Todos los requests se registran con:

- Timestamp
- Método HTTP
- Ruta
- IP del cliente
- Código de respuesta
- Tiempo de procesamiento

Logs se guardan en: `logs/gateway.log`

Ejemplo:

```
╔═══════════════════════════════════════════════════════
║ REQUEST  → GET /api/inventario/productos
║ IP       → 192.168.1.100
║ Time     → 2024-12-16T10:30:00
║ Agent    → Mozilla/5.0...
╚═══════════════════════════════════════════════════════
╔═══════════════════════════════════════════════════════
║ RESPONSE ← GET /api/inventario/productos
║ Status   → 200
║ Duration → 45 ms
╚═══════════════════════════════════════════════════════
```

## ⚡ Rate Limiting (Opcional)

Para habilitar rate limiting con Redis:

1. **Instalar Redis**

```bash
docker run -d -p 6379:6379 redis:alpine
```

2. **Descomentar en application.yml**

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

3. **Añadir filtro de rate limiting** (implementación personalizada según necesidad)

## 🔧 Variables de Entorno

| Variable         | Descripción                  | Valor por Defecto     |
|------------------|------------------------------|-----------------------|
| `SERVER_PORT`    | Puerto del Gateway           | 8080                  |
| `JWT_SECRET`     | Clave secreta JWT            | (ver application.yml) |
| `USUARIOS_URL`   | URL microservicio usuarios   | http://localhost:8082 |
| `INVENTARIO_URL` | URL microservicio inventario | http://localhost:8081 |
| `VENTAS_URL`     | URL microservicio ventas     | http://localhost:8083 |
| `REPORTES_URL`   | URL microservicio reportes   | http://localhost:8084 |

## 📦 Estructura del Proyecto

```
gateway-service/
├── src/
│   └── main/
│       ├── java/com/legacy/pharmacy/gateway/
│       │   ├── GatewayServiceApplication.java    # Clase principal
│       │   ├── config/
│       │   │   ├── CorsConfig.java               # Configuración CORS
│       │   │   └── JwtUtil.java                  # Utilidad JWT
│       │   ├── filter/
│       │   │   ├── AuthenticationFilter.java     # Filtro autenticación
│       │   │   └── LoggingFilter.java            # Filtro logging
│       │   └── controller/
│       │       └── FallbackController.java       # Fallback circuit breaker
│       └── resources/
│           ├── application.yml                   # Configuración principal
│           └── application.properties            # Alternativa properties
├── pom.xml                                       # Dependencias Maven
└── README.md                                     # Este archivo
```

## 🐛 Troubleshooting

### Error: "No se encontró el header de autorización"

- Asegúrate de enviar el header `Authorization: Bearer <token>`

### Error: "Token expirado o inválido"

- Verifica que el JWT_SECRET sea el mismo en Gateway y microservicio de usuarios
- El token expira en 24 horas por defecto

### Error: "Service Unavailable"

- Verifica que los microservicios estén corriendo
- Revisa los puertos configurados
- Consulta los logs: `logs/gateway.log`

### Microservicio no responde

- Verifica conectividad: `curl http://localhost:8082/actuator/health`
- Revisa logs del microservicio específico
- Verifica que las rutas en `application.yml` sean correctas

## 📚 Tecnologías Utilizadas

- **Spring Boot 3.2.0**
- **Spring Cloud Gateway**
- **JWT (jjwt 0.12.3)**
- **Resilience4j** (Circuit Breaker)
- **Spring Boot Actuator** (Monitoreo)
- **Lombok** (Reducción de boilerplate)

## 👥 Autores

Legacy Pharmacy Development Team

## 📄 Licencia

Este proyecto es parte del sistema Legacy Pharmacy.

---

Para más información sobre los microservicios individuales, consulta sus respectivos READMEs.
