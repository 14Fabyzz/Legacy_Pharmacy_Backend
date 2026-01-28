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
    } catch {
        Write-Host "Error: $($_.Exception.Message)"
        if ($_.Exception.Response) {
             $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
             Write-Host "Body: $($reader.ReadToEnd())"
        }
        return $null
    }
}

# 1. Login
$loginResponse = Invoke-Api -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body @{login="admin"; password="Admin123!"}
$token = $loginResponse.token
Write-Host "Token obtained."

# 2. Check Product 101
Write-Host "Checking Product 101..."
$prod = Invoke-Api -Method Get -Uri "http://localhost:8080/api/inventario/productos/101" -Headers @{Authorization="Bearer $token"}

if ($prod) {
    Write-Host "Product Found: $($prod | ConvertTo-Json)"
} else {
    Write-Host "Product 101 NOT FOUND."
}

# 3. List First 5 Products (to find a valid ID)
Write-Host "`nListing valid products..."
$list = Invoke-Api -Method Get -Uri "http://localhost:8080/api/inventario/productos?page=0&size=5" -Headers @{Authorization="Bearer $token"}
if ($list) {
   Write-Host "Products available: $($list.content.Count)"
   $list.content | ForEach-Object { Write-Host "ID: $($_.id) - Name: $($_.nombre)" }
}
