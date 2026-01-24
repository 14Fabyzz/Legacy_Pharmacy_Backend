$ErrorActionPreference = "Stop"

Write-Host "1. Login..."
$headers = @{ "Content-Type" = "application/json" }
$loginBody = @{ login="admin"; password="Admin123!" } | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body $loginBody -Headers $headers
$token = $loginResponse.token
$headers["Authorization"] = "Bearer $token"
Write-Host "Token: $token"

Write-Host "`n2. Creating Product..."
$prodBody = @{
    codigoInterno = "TESTP001"
    nombreComercial = "Acetaminofen Test"
    categoriaId = 1
    laboratorioId = 1
    precioVentaBase = 500
    stockMinimo = 10
} | ConvertTo-Json

try {
    $newProd = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/inventario/productos" -Body $prodBody -Headers $headers
    $prodId = $newProd.id
    Write-Host "Created Product ID: $prodId"
} catch {
    Write-Host "Creation failed, maybe it exists?"
    Write-Host $_.Exception.Message
    # Try to find it
    try {
        $existing = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/inventario/productos/buscar?nombre=Acetaminofen" -Headers $headers
        if ($existing -and $existing.Count -gt 0) {
             $prodId = $existing[0].id
             Write-Host "Found existing Product ID: $prodId"
        }
    } catch {
        Write-Host "Could not find existing product either."
    }
}

if ($prodId) {
    Write-Host "`n3. Adding Stock..."
    $stockBody = @{
        productoId = $prodId
        numeroLote = "LOTE$(Get-Random)"
        cantidad = 100
        costoCompra = 200
        fechaVencimiento = "2028-12-31"
        observaciones = "Test Stock"
    } | ConvertTo-Json
    
    try {
        $entry = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/inventario/entrada" -Body $stockBody -Headers $headers
        Write-Host "Stock added."
    } catch {
        Write-Host "Stock add failed: $($_.Exception.Message)"
        if ($_.Exception.Response) {
             $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
             Write-Host "Body: $($reader.ReadToEnd())"
        }
    }
    
    Write-Host "SETUP_PRODUCT_ID:$prodId"
}
