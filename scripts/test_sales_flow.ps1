$ErrorActionPreference = "Stop"
Start-Transcript -Path "test_run_final_2.log" -Force

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

        Write-Host "[$Method] $Uri"
        $response = Invoke-RestMethod @params
        return $response
    }
    catch {
        Write-Host "Error calling $Uri" -ForegroundColor Red
        Write-Host "Message: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
<<<<<<< HEAD
             Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
             
             try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $responseBody = $reader.ReadToEnd()
                Write-Host "Response Body: $responseBody" -ForegroundColor Yellow
             } catch {
                Write-Host "Could not read response body." -ForegroundColor DarkGray
             }
=======
            Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
             
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $responseBody = $reader.ReadToEnd()
                Write-Host "Response Body: $responseBody" -ForegroundColor Yellow
            }
            catch {
                Write-Host "Could not read response body." -ForegroundColor DarkGray
            }
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
        }
        return $null
    }
}

# 1. Login as Admin
Write-Host "`n--- 1. Logging In as Admin ---" -ForegroundColor Cyan
$loginBody = @{
    login    = "admin"
<<<<<<< HEAD
    password = "Admin123!"
=======
    password = $env:ADMIN_PASSWORD
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
}
$loginResponse = Invoke-Api -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body $loginBody

if (-not $loginResponse -or -not $loginResponse.token) {
    Write-Error "Login failed. Cannot proceed without token."
}

$token = $loginResponse.token
$headers = @{
    Authorization = "Bearer $token"
}
Write-Host "Token obtained." -ForegroundColor Green

# 2. Check Box State
Write-Host "`n--- 2. Checking Box State ---" -ForegroundColor Cyan
try {
    $boxState = Invoke-Api -Method Get -Uri "http://localhost:8080/api/ventas/caja/estado" -Headers $headers
    if ($boxState) {
        Write-Host "Box State: $($boxState | ConvertTo-Json -Depth 2)" -ForegroundColor Gray
    }
<<<<<<< HEAD
} catch {
=======
}
catch {
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    Write-Host "Box is likely closed (404/Empty)." -ForegroundColor Yellow
    $boxState = $null
}

# 3. Create Client
Write-Host "`n--- 3. Creating Client ---" -ForegroundColor Cyan
$clientBody = @{
    nombre               = "Cliente"
    apellido             = "Prueba"
    numeroIdentificacion = "CLI$(Get-Random)"
    email                = "cliente$(Get-Random)@test.com"
}
$client = Invoke-Api -Method Post -Uri "http://localhost:8080/api/ventas/clientes" -Body $clientBody -Headers $headers

if (-not $client) {
    Write-Host "Failed to create client. Creating sale might fail." -ForegroundColor Red
<<<<<<< HEAD
} else {
=======
}
else {
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    Write-Host "Client created with ID: $($client.id)" -ForegroundColor Green
}

# 4. Open Box (if closed)
# We assume if boxState is null, we need to open it.
if (-not $boxState) {
    Write-Host "`n--- 4. Opening Box ---" -ForegroundColor Cyan
    $openBoxBody = @{
        saldoInicial = 50000
        sucursalId   = 1
    }
    $boxOpen = Invoke-Api -Method Post -Uri "http://localhost:8080/api/ventas/caja/abrir" -Body $openBoxBody -Headers $headers
    if ($boxOpen) {
<<<<<<< HEAD
         Write-Host "Box Opened: $($boxOpen | ConvertTo-Json -Depth 2)" -ForegroundColor Green
    }
} else {
=======
        Write-Host "Box Opened: $($boxOpen | ConvertTo-Json -Depth 2)" -ForegroundColor Green
    }
}
else {
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    Write-Host "Box already open." -ForegroundColor Yellow
}

# 5. Create Sale
if ($client) {
    Write-Host "`n--- 5. Creating Sale ---" -ForegroundColor Cyan
    $saleBody = @{
<<<<<<< HEAD
        clienteId      = $client.id
        items          = @(
=======
        clienteId     = $client.id
        items         = @(
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
            @{
                productoId     = 5
                cantidad       = 2
                precioUnitario = 500
            }
        )
<<<<<<< HEAD
        metodoPago     = "EFECTIVO"
        montoRecibido  = 20000
=======
        metodoPago    = "EFECTIVO"
        montoRecibido = 20000
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    }
    $sale = Invoke-Api -Method Post -Uri "http://localhost:8080/api/ventas/ventas" -Body $saleBody -Headers $headers
    if ($sale) {
        Write-Host "Sale Created Successfully!" -ForegroundColor Green
        Write-Host ($sale | ConvertTo-Json -Depth 5)
    }
}
