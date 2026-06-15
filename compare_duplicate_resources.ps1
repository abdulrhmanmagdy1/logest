$base = 'mobile-native-android/app/src/main/res/values'
function Get-Resource-Names($file) {
    $content = Get-Content -Raw $file
    $matches = [regex]::Matches($content, '<\w+[^>]*name="([^"]+)"')
    return $matches | ForEach-Object { $_.Groups[1].Value }
}
$systemSpacing = Get-Resource-Names "$base/design_system_spacing.xml"
$tokensSpacing = Get-Resource-Names "$base/design_tokens_spacing.xml"
Write-Host 'spacing unique in design_system_spacing.xml:'
($systemSpacing | Where-Object { $_ -notin $tokensSpacing }) | Sort | ForEach-Object { Write-Host $_ }
Write-Host 'spacing unique in design_tokens_spacing.xml:'
($tokensSpacing | Where-Object { $_ -notin $systemSpacing }) | Sort | ForEach-Object { Write-Host $_ }
$systemTypography = Get-Resource-Names "$base/design_system_typography.xml"
$tokensTypography = Get-Resource-Names "$base/design_tokens_typography.xml"
Write-Host 'typography unique in design_system_typography.xml:'
($systemTypography | Where-Object { $_ -notin $tokensTypography }) | Sort | ForEach-Object { Write-Host $_ }
Write-Host 'typography unique in design_tokens_typography.xml:'
($tokensTypography | Where-Object { $_ -notin $systemTypography }) | Sort | ForEach-Object { Write-Host $_ }
