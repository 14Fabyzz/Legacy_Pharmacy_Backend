#!/bin/bash

# Colores para salida
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo "=================================================="
echo "   INICIANDO TEST GLOBAL DE MICROSERVICIOS"
echo "=================================================="

# 1. Health Checks (Directos)
echo -e "\n[1] Verificando Health Check Directos..."

# Gateway (8080)
HTTP_GW=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
if [ "$HTTP_GW" == "200" ]; then echo -e "GATEWAY (8080):      ${GREEN}[✅ PASS]${NC}"; else echo -e "GATEWAY (8080):      ${RED}[❌ FAIL] $HTTP_GW${NC}"; fi

# MS-Inventario (8081)
HTTP_INV=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/actuator/health)
if [ "$HTTP_INV" == "200" ]; then echo -e "INVENTARIO (8081):   ${GREEN}[✅ PASS]${NC}"; else echo -e "INVENTARIO (8081):   ${RED}[❌ FAIL] $HTTP_INV${NC}"; fi

# MS-Usuarios (8082) - Endpoint custom /api/auth/health
HTTP_USR=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/api/auth/health)
if [ "$HTTP_USR" == "200" ]; then echo -e "USUARIOS (8082):     ${GREEN}[✅ PASS]${NC}"; else echo -e "USUARIOS (8082):     ${RED}[❌ FAIL] $HTTP_USR${NC}"; fi

# MS-Ventas (8083) - Asumiendo actuator health disponible
HTTP_VEN=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8083/actuator/health)
if [ "$HTTP_VEN" == "200" ]; then echo -e "VENTAS (8083):       ${GREEN}[✅ PASS]${NC}"; else echo -e "VENTAS (8083):       ${RED}[❌ FAIL] $HTTP_VEN${NC}"; fi


# 2. Autenticación a través del Gateway
echo -e "\n[2] Probando Login via Gateway..."
USER="admin"
PASS="${ADMIN_PASSWORD:-Admin123!}"

# La ruta en gateway es /api/usuarios/login que reescribe a /api/auth/login
RESPONSE=$(curl -s -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d "{\"login\": \"$USER\", \"password\": \"$PASS\"}")

# Extraer token
if command -v jq &> /dev/null; then
    TOKEN=$(echo $RESPONSE | jq -r '.token')
else
    TOKEN=$(echo $RESPONSE | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
fi

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    echo -e "Login Gateway: ${GREEN}[✅ PASS]${NC}"
    echo "Token capturado."
else
    echo -e "Login Gateway: ${RED}[❌ FAIL]${NC}"
    echo "Respuesta: $RESPONSE"
    exit 1
fi

# 3. Pruebas de Rutas Protegidas via Gateway
echo -e "\n[3] Probando Rutas Protegidas via Gateway..."

# Inventario via Gateway
# Ruta: /api/inventario/dashboard/cards -> /api/v1/inventario/dashboard/cards
URL_INV="http://localhost:8080/api/dashboard/cards"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$URL_INV" -H "Authorization: Bearer $TOKEN")
if [ "$HTTP_CODE" == "200" ]; then echo -e "Inventario (Gateway): ${GREEN}[✅ PASS]${NC}"; else echo -e "Inventario (Gateway): ${RED}[❌ FAIL] $HTTP_CODE ($URL_INV)${NC}"; fi


echo -e "\n=================================================="
echo "   FIN DEL TEST GLOBAL"
echo "=================================================="
