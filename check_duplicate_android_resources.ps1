$base = 'mobile-native-android/app/src/main/res/values'
$files = Get-ChildItem -Path $base -Filter '*.xml'
$resourceIndex = @{}
$tags = 'dimen|string|style|color|array|integer|item|bool|plurals'
foreach ($file in $files) {
    $content = Get-Content -Raw $file.FullName
    $matches = [regex]::Matches($content, '<(' + $tags + ')[^>]*name="([^"]+)"')
    foreach ($m in $matches) {
        $name = $m.Groups[2].Value
        if (-not $resourceIndex.ContainsKey($name)) { $resourceIndex[$name] = @() }
        $resourceIndex[$name] += $file.Name
    }
}
$duplicates = $resourceIndex.GetEnumerator() | Where-Object { $_.Value.Count -gt 1 } | Sort-Object Name
if ($duplicates.Count -eq 0) {
    Write-Host 'No duplicate resource names found.'
} else {
    Write-Host "Duplicate resource names found: $($duplicates.Count)"
    foreach ($entry in $duplicates) {
        Write-Host "Resource: $($entry.Name) -> Files: $([string]::Join(', ', ($entry.Value | Sort-Object -Unique)))"
    }
}
