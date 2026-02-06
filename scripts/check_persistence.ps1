$ErrorActionPreference = "Stop"
$BaseUrl = "http://localhost:8080/api"

<<<<<<< HEAD
function Log-Msg { param($Msg, $Color="White") Write-Host $Msg -ForegroundColor $Color }
=======
function Log-Msg { param($Msg, $Color = "White") Write-Host $Msg -ForegroundColor $Color }
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274

Log-Msg "--- Checking Data Persistence ---" "Yellow"

# 1. Login (Checks User DB)
Log-Msg "1. Attempting Login (Admin)..."
try {
<<<<<<< HEAD
    $resp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/usuarios/login" -Body (@{login="admin"; password="Admin123!"} | ConvertTo-Json) -ContentType "application/json"
    $token = $resp.token.Trim()
    Log-Msg "   [PASS] Login successful. Users DB seems alive (or re-seeded)." "Green"
} catch {
=======
    $loginResponse = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/usuarios/login" -Body (@{login = "admin"; password = $env:ADMIN_PASSWORD } | ConvertTo-Json) -ContentType "application/json"
    $token = $loginResponse.token.Trim()
    Log-Msg "   [PASS] Login successful. Users DB seems alive (or re-seeded)." "Green"
}
catch {
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    Log-Msg "   [FAIL] Login failed. Users DB might be empty." "Red"
    Log-Msg "   Error: $($_.Exception.Message)" "Red" 
    exit
}

<<<<<<< HEAD
$headers = @{Authorization="Bearer $token"}
=======
$headers = @{Authorization = "Bearer $token" }
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274

# 2. Check Inventory (Checks MySQL Data retention)
Log-Msg "`n2. Checking for Test Product (TESTP001)..."
try {
    # This was created by simple_setup.ps1
    $prod = Invoke-RestMethod -Method Get -Uri "$BaseUrl/inventario/productos/codigo/TESTP001" -Headers $headers
    if ($prod) {
<<<<<<< HEAD
         Log-Msg "   [PASS] Product TESTP001 found. Inventory DB persisted." "Green"
    }
} catch {
    if ($_.Exception.Response.StatusCode -eq 404 -or $_.Exception.Response.StatusCode -eq 500) {
         Log-Msg "   [FAIL] Product TESTP001 NOT found. Inventory DB likely lost data." "Red"
    } else {
         Log-Msg "   [FAIL] Error checking product: $($_.Exception.Message)" "Red"
=======
        Log-Msg "   [PASS] Product TESTP001 found. Inventory DB persisted." "Green"
    }
}
catch {
    if ($_.Exception.Response.StatusCode -eq 404 -or $_.Exception.Response.StatusCode -eq 500) {
        Log-Msg "   [FAIL] Product TESTP001 NOT found. Inventory DB likely lost data." "Red"
    }
    else {
        Log-Msg "   [FAIL] Error checking product: $($_.Exception.Message)" "Red"
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    }
}

# 3. Check Sales (Checks Postgres Data retention)
Log-Msg "`n3. Checking Sales History..."
try {
    $sales = Invoke-RestMethod -Method Get -Uri "$BaseUrl/ventas/ventas" -Headers $headers
    if ($sales -and $sales.Count -gt 0) {
        Log-Msg "   [PASS] Found $($sales.Count) sales in history. Sales DB persisted." "Green"
<<<<<<< HEAD
    } else {
        Log-Msg "   [INFO] No sales found (or empty). If you made sales before, they are gone." "Yellow"
    }
} catch {
=======
    }
    else {
        Log-Msg "   [INFO] No sales found (or empty). If you made sales before, they are gone." "Yellow"
    }
}
catch {
>>>>>>> 07cacaa80ccf220cb65c64c3522d1888c2bef274
    Log-Msg "   [FAIL] Could not fetch sales." "Red"
}
