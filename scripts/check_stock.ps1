$ErrorActionPreference = "Stop"

Write-Host "1. Login..."
$headers = @{ "Content-Type" = "application/json" }
<<<<<<< HEAD
$loginBody = @{ login="admin"; password="Admin123!" } | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body $loginBody -Headers $headers
=======
$loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body (@{login = "admin"; password = $env:ADMIN_PASSWORD } | ConvertTo-Json) -ContentType "application/json"
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
$token = $loginResponse.token
$headers["Authorization"] = "Bearer $token"

Write-Host "`n2. Checking Stock for Product 5..."
try {
    $stock = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/inventario/productos/5/stock" -Headers $headers
    Write-Host "Stock Response:"
    Write-Host "Cantidad Disponible: $($stock.cantidadDisponible)"
    Write-Host "Nombre: $($stock.nombreProducto)"
    Write-Host "Precio: $($stock.precioVenta)"
<<<<<<< HEAD
} catch {
=======
}
catch {
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    Write-Host "Failed to get stock."
    Write-Host $_.Exception.Message
}
