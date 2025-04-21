$server = 'http://localhost:8080'

$postParams = @{url=""} | ConvertTo-Json
$result = Invoke-WebRequest -Uri "$($server)/offload" -Method Post -Body $postParams -ContentType "application/json" | ConvertTo-Json -Depth 10

Write-Output $result