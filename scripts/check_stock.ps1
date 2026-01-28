$ErrorActionPreference = "Stop"

Write-Host "1. Login..."
$headers = @{ "Content-Type" = "application/json" }
$loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body (@{login = "admin"; password = $env:ADMIN_PASSWORD } | ConvertTo-Json) -ContentType "application/json"
$token = $loginResponse.token
$headers["Authorization"] = "Bearer $token"

Write-Host "`n2. Checking Stock for Product 5..."
try {
    $stock = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/inventario/productos/5/stock" -Headers $headers
    Write-Host "Stock Response:"
    Write-Host "Cantidad Disponible: $($stock.cantidadDisponible)"
    Write-Host "Nombre: $($stock.nombreProducto)"
    Write-Host "Precio: $($stock.precioVenta)"
}
catch {
    Write-Host "Failed to get stock."
    Write-Host $_.Exception.Message
}
