$ErrorActionPreference = "Continue"

# --- CONFIGURATION ---
$BaseUrl = "http://localhost:8080/api"
$TestProductCode = "PREMERGE_$(Get-Random)"
$LogFile = "verification_report.txt"

# --- HELPER FUNCTIONS ---
function Log-Msg {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
    Add-Content -Path $LogFile -Value $Message
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Uri,
        [hashtable]$Body = @{},
        [hashtable]$Headers = @{},
        [switch]$ReturnFullResponse
    )
    try {
        $params = @{
            Method      = $Method
            Uri         = $Uri
            ContentType = "application/json"
            Headers     = $Headers
        }
        if ($Body.Count -gt 0) { $params.Body = ($Body | ConvertTo-Json -Depth 5) }
        
        $response = Invoke-RestMethod @params
        return $response
    }
    catch {
        if ($ReturnFullResponse) { return $_.Exception.Response }
        Log-Msg "API Error: $($_.Exception.Message)" "Red"
        if ($_.Exception.Response) {
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                Log-Msg "Body: $($reader.ReadToEnd())" "Red"
            }
            catch {}
        }
        return $null
    }
}

function Assert-Test {
    param(
        [string]$Name,
        [scriptblock]$TestLogic
    )
    Log-Msg "`n[TEST] Verifying: $Name" "Cyan"
    try {
        $result = & $TestLogic
        if ($result) {
            Log-Msg "   [PASS] $Name" "Green"
            return $true
        }
        else {
            Log-Msg "   [FAIL] $Name" "Red"
            return $false
        }
    }
    catch {
        Log-Msg "   [CRITICAL FAIL] $Name - Exception: $_" "Red"
        return $false
    }
}

# --- INIT ---
"Verification Report - $(Get-Date)" | Out-File $LogFile
Log-Msg "Starting Pre-Merge Verification..." "Yellow"

# 1. SETUP & AUTH
$script:Token = $null
$script:Headers = @{}

Assert-Test "Admin Login" {
    $resp = Invoke-Api -Method Post -Uri "$BaseUrl/usuarios/login" -Body @{login = "admin"; password = $env:ADMIN_PASSWORD }
    if ($resp.token) {
        $script:Token = $resp.token.Trim()
        $script:Headers = @{Authorization = "Bearer $($script:Token)" }
        Log-Msg "DEBUG: Headers = $($script:Headers | ConvertTo-Json -Depth 1)" "Gray"
        return $true
    }
    return $false
} | Out-Null

if (-not $script:Token) { Log-Msg "Auth failed. Aborting." "Red"; exit }

# 2. DATA SETUP (Product)
$script:ProdId = $null
Assert-Test "Create Test Product & Stock" {
    # Create Product
    $pBody = @{
        codigoInterno   = $TestProductCode
        nombreComercial = "PreMerge Test Product"
        categoriaId     = 1
        laboratorioId   = 1
        precioVentaBase = 1000
        stockMinimo     = 5
    }
    $prod = Invoke-Api -Method Post -Uri "$BaseUrl/inventario/productos" -Body $pBody -Headers $script:Headers
    if (-not $prod) { return $false }
    $script:ProdId = $prod.id

    # Add Stock (100 units)
    $sBody = @{
        productoId       = $script:ProdId
        numeroLote       = "LOTE_TEST_$(Get-Random)"
        cantidad         = 100
        costoCompra      = 500
        fechaVencimiento = "2030-01-01"
        observaciones    = "Pre-Merge Test"
    }
    $entry = Invoke-Api -Method Post -Uri "$BaseUrl/inventario/entrada" -Body $sBody -Headers $script:Headers
    return ($entry -ne $null)
} | Out-Null

if (-not $script:ProdId) { Log-Msg "Setup failed. Aborting." "Red"; exit }

# 3. HAPPY PATH: SALES FLOW
Assert-Test "Full Sales Flow (Box Open -> Sale -> Stock Check)" {
    # Ensure Box Open
    $box = Invoke-Api -Method Get -Uri "$BaseUrl/ventas/caja/estado" -Headers $script:Headers
    if (-not $box) {
        Invoke-Api -Method Post -Uri "$BaseUrl/ventas/caja/abrir" -Body @{saldoInicial = 1000 } -Headers $script:Headers
    }

    # Verify Initial Stock
    $stockPre = Invoke-Api -Method Get -Uri "$BaseUrl/inventario/productos/$script:ProdId/stock" -Headers $script:Headers
    if ($stockPre.cantidadDisponible -ne 100) { Log-Msg "Warning: Pre-stock mismatch ($($stockPre.cantidadDisponible))" "Yellow" }
    
    # Create Sale (Sell 10 units)
    $saleBody = @{
        clienteId     = 1 # Guest/Generic
        metodoPago    = "EFECTIVO"
        montoRecibido = 20000
        items         = @( @{ productoId = $script:ProdId; cantidad = 10; precioUnitario = 1000 } )
    }
    $sale = Invoke-Api -Method Post -Uri "$BaseUrl/ventas/ventas" -Body $saleBody -Headers $script:Headers
    
    if (-not $sale.numeroFactura) { return $false }

    # Verify Stock Update
    $stockPost = Invoke-Api -Method Get -Uri "$BaseUrl/inventario/productos/$script:ProdId/stock" -Headers $script:Headers
    
    $expected = $stockPre.cantidadDisponible - 10
    if ($stockPost.cantidadDisponible -eq $expected) { return $true }
    
    Log-Msg "Stock update failed! Expected $expected, got $($stockPost.cantidadDisponible)" "Red"
    return $false
} | Out-Null

# 4. EDGE CASE: INSUFFICIENT STOCK
Assert-Test "Reject Insufficient Stock" {
    # Try to sell 200 units (only 90 left)
    $failBody = @{
        clienteId     = 1
        metodoPago    = "EFECTIVO"
        montoRecibido = 500000
        items         = @( @{ productoId = $script:ProdId; cantidad = 200; precioUnitario = 1000 } )
    }
    
    # We expect a 400/500/Error response, not a success object
    try {
        $params = @{
            Method      = "Post"
            Uri         = "$BaseUrl/ventas/ventas"
            ContentType = "application/json"
            Headers     = $script:Headers
            Body        = ($failBody | ConvertTo-Json -Depth 5)
        }
        Invoke-RestMethod @params
        return $false # Should not reach here
    }
    catch {
        # Check if it was a "bad request" type error, which is good
        # Log-Msg "Caught expected error: $($_.Exception.Message)" "Gray"
        return $true
    }
} | Out-Null

# 5. EDGE CASE: PRODUCT NOT FOUND
Assert-Test "Reject Non-Existent Product" {
    $failBody = @{
        clienteId     = 1
        metodoPago    = "EFECTIVO"
        montoRecibido = 500000
        items         = @( @{ productoId = 999999; cantidad = 1; precioUnitario = 1000 } )
    }
    try {
        $params = @{
            Method = "Post"; Uri = "$BaseUrl/ventas/ventas"; ContentType = "application/json"; Headers = $script:Headers; Body = ($failBody | ConvertTo-Json)
        }
        Invoke-RestMethod @params
        return $false
    }
    catch { return $true }
} | Out-Null

# 6. SECURITY: INVALID TOKEN
Assert-Test "Reject Invalid Token" {
    $BadHeaders = @{Authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.INVALID_TOKEN_SIGNATURE" }
    try {
        $params = @{ Method = "Get"; Uri = "$BaseUrl/ventas/caja/estado"; Headers = $BadHeaders }
        Invoke-RestMethod @params
        return $false # Should NOT succeed
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 403 -or $_.Exception.Response.StatusCode -eq 401) {
            return $true
        }
        Log-Msg "Unexpected status code: $($_.Exception.Response.StatusCode)" "Red"
        return $false
    }
}

Log-Msg "`n----------------------------------------"
Log-Msg "VERIFICATION COMPLETE. See $LogFile for details." "Yellow"
