$server = 'http://localhost:8080'

$postParams = @{url="https://www.youtube.com/watch?v=Algf_CXH9Ko"} | ConvertTo-Json
Invoke-WebRequest -Uri "$($server)/offload" -Method Post -Body $postParams -ContentType "application/json"