$ErrorActionPreference = "Stop"

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Uri,
        [hashtable]$Body = @{},
        [hashtable]$Headers = @{}
    )
    try {
        $params = @{
            Method      = $Method
            Uri         = $Uri
            ContentType = "application/json"
        }
        if ($Body.Count -gt 0) {
            $params.Body = ($Body | ConvertTo-Json -Depth 5)
        }
        if ($Headers.Count -gt 0) {
            $params.Headers = $Headers
        }
        $response = Invoke-RestMethod @params
        return $response
    }
    catch {
        Write-Host "Error calling $Uri" -ForegroundColor Red
        Write-Host "Message: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            Write-Host "Body: $($reader.ReadToEnd())" -ForegroundColor Yellow
        }

        return @{ ErrorMessage = $_.Exception.Message; StatusCode = $_.Exception.Response.StatusCode }
    }
}

# 1. Login
Write-Host "--- Helper: Logging In ---"
$loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body (@{login = "admin"; password = $env:ADMIN_PASSWORD } | ConvertTo-Json) -ContentType "application/json"
$token = $loginResponse.token
$headers = @{Authorization = "Bearer $token" }

# 2. Check if product exists
$code = "TESTP001"
Write-Host "--- Helper: Checking Product $code ---"
$existing = Invoke-Api -Method Get -Uri "http://localhost:8080/api/inventario/productos/codigo/$code" -Headers $headers

if ($existing -and -not $existing.ErrorMessage) {
    Write-Host "Product already exists: ID $($existing.id)"
    $prodId = $existing.id
}
else {
    Write-Host "Product not found (or error 500/404). Proceeding to create."
    # 3. Create Product
    Write-Host "--- Helper: Creating Product ---"
    $prodBody = @{
        codigoInterno   = $code
        nombreComercial = "Acetaminofen Test"
        categoriaId     = 1
        laboratorioId   = 1
        precioVentaBase = 500
        stockMinimo     = 10
    }
    $newProd = Invoke-Api -Method Post -Uri "http://localhost:8080/api/inventario/productos" -Body $prodBody -Headers $headers
    
    if (-not $newProd -or $newProd.ErrorMessage) { 
        Write-Host "Failed to create product. Exiting." -ForegroundColor Red
        if ($newProd) { Write-Host $newProd.ErrorMessage }
        exit 
    }
    $prodId = $newProd.id
    Write-Host "Created Product ID: $prodId"
}

# 4. Add Stock
Write-Host "--- Helper: Adding Stock ---"
$stockBody = @{
    productoId       = $prodId
    numeroLote       = "LOTE$(Get-Random)"
    cantidad         = 100
    costoCompra      = 200
    fechaVencimiento = "2028-12-31"
    observaciones    = "Initial Test Stock"
}

$entry = Invoke-Api -Method Post -Uri "http://localhost:8080/api/inventario/entrada" -Body $stockBody -Headers $headers

if ($entry) {
    Write-Host "Stock added successfully."
}

# OUTPUT THE PRODUCT ID
Write-Host "SETUP_PRODUCT_ID:$prodId"
