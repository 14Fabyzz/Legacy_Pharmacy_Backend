$ErrorActionPreference = "Stop"

Write-Host "1. Login..."
$headers = @{ "Content-Type" = "application/json" }
<<<<<<< HEAD
$loginBody = @{ login="admin"; password="Admin123!" } | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body $loginBody -Headers $headers
=======
$loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body (@{login = "admin"; password = $env:ADMIN_PASSWORD } | ConvertTo-Json) -ContentType "application/json"
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
$token = $loginResponse.token.Trim()
$headers["Authorization"] = "Bearer $token"
Write-Host "Token: $token"

Write-Host "`n2. Creating Product..."
$prodBody = @{
<<<<<<< HEAD
    codigoInterno = "TESTP001"
    nombreComercial = "Acetaminofen Test"
    categoriaId = 1
    laboratorioId = 1
    precioVentaBase = 500
    stockMinimo = 10
=======
    codigoInterno   = "TESTP001"
    nombreComercial = "Acetaminofen Test"
    categoriaId     = 1
    laboratorioId   = 1
    precioVentaBase = 500
    stockMinimo     = 10
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
} | ConvertTo-Json

try {
    $newProd = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/inventario/productos" -Body $prodBody -Headers $headers
    $prodId = $newProd.id
    Write-Host "Created Product ID: $prodId"
<<<<<<< HEAD
} catch {
=======
}
catch {
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    Write-Host "Creation failed, maybe it exists?"
    Write-Host $_.Exception.Message
    # Try to find it
    try {
        $existing = Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/inventario/productos/buscar?nombre=Acetaminofen" -Headers $headers
        if ($existing -and $existing.Count -gt 0) {
<<<<<<< HEAD
             $prodId = $existing[0].id
             Write-Host "Found existing Product ID: $prodId"
        }
    } catch {
=======
            $prodId = $existing[0].id
            Write-Host "Found existing Product ID: $prodId"
        }
    }
    catch {
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
        Write-Host "Could not find existing product either."
    }
}

if ($prodId) {
    Write-Host "`n3. Adding Stock..."
    $stockBody = @{
<<<<<<< HEAD
        productoId = $prodId
        numeroLote = "LOTE$(Get-Random)"
        cantidad = 100
        costoCompra = 200
        fechaVencimiento = "2028-12-31"
        observaciones = "Test Stock"
=======
        productoId       = $prodId
        numeroLote       = "LOTE$(Get-Random)"
        cantidad         = 100
        costoCompra      = 200
        fechaVencimiento = "2028-12-31"
        observaciones    = "Test Stock"
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    } | ConvertTo-Json
    
    try {
        $entry = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/inventario/entrada" -Body $stockBody -Headers $headers
        Write-Host "Stock added."
<<<<<<< HEAD
    } catch {
        Write-Host "Stock add failed: $($_.Exception.Message)"
        if ($_.Exception.Response) {
             $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
             Write-Host "Body: $($reader.ReadToEnd())"
=======
    }
    catch {
        Write-Host "Stock add failed: $($_.Exception.Message)"
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            Write-Host "Body: $($reader.ReadToEnd())"
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
        }
    }
    
    Write-Host "SETUP_PRODUCT_ID:$prodId"
}
