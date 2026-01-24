$ErrorActionPreference = "Continue"
$BaseUrl = "http://localhost:8080/api"

function Log-Msg { param($Msg, $Color="White") Write-Host $Msg -ForegroundColor $Color }

# 1. AUTH
Log-Msg "1. Login as Admin..."
$token = (Invoke-RestMethod -Method Post -Uri "$BaseUrl/usuarios/login" -Body (@{login="admin"; password="Admin123!"} | ConvertTo-Json) -ContentType "application/json").token.Trim()
$headers = @{Authorization="Bearer $token"}

# 2. EDGE CASE: BOX CLOSED
# To test this, we must ensure the box is closed. 
# Attempting to close box first (if open).
Log-Msg "`n2. Testing 'Sale with Closed Box'..."

try {
    # Try closing. If already closed, it might error or succeed.
    Invoke-RestMethod -Method Post -Uri "$BaseUrl/ventas/caja/cerrar" -Headers $headers -Body "{}" -ContentType "application/json" | Out-Null
    Log-Msg "   [INFO] Box closed forcefully for testing." "Gray"
} catch {}

# Now try to sell
$saleBody = @{
    clienteId = 1
    metodoPago = "EFECTIVO"
    montoRecibido = 50000
    items = @( @{ productoId = 1; cantidad = 1; precioUnitario = 1000 } )
}

try {
    Invoke-RestMethod -Method Post -Uri "$BaseUrl/ventas/ventas" -Headers $headers -Body ($saleBody | ConvertTo-Json) -ContentType "application/json"
    Log-Msg "   [FAIL] Sale succeeded even though Box should be closed!" "Red"
} catch {
    if ($_.Exception.Response.StatusCode -eq 409 -or $_.Exception.Response.StatusCode -eq 400) {
        Log-Msg "   [PASS] Sale rejected because Box is Closed ($($_.Exception.Response.StatusCode))." "Green"
    } else {
        Log-Msg "   [FAIL?] Unexpected error code: $($_.Exception.Response.StatusCode)" "Yellow"
        # Log response body
    }
}

# 3. RE-OPEN BOX (To leave system usable)
Log-Msg "`n3. Restoring System State (Opening Box)..."
try {
    Invoke-RestMethod -Method Post -Uri "$BaseUrl/ventas/caja/abrir" -Headers $headers -Body (@{saldoInicial=50000}|ConvertTo-Json) -ContentType "application/json" | Out-Null
    Log-Msg "   [INFO] Box re-opened." "Gray"
} catch {
    Log-Msg "   [WARN] Could not re-open box." "Yellow"
}

# 4. SECURITY CHECK (Manual Log)
Log-Msg "`n4. RBAC Check"
Log-Msg "   Note: Source code analysis showed no @PreAuthorize annotations in MS-Ventas." "Cyan"
Log-Msg "   This means currently Roles are NOT enforced at Method level (only Authentication)." "Cyan"
