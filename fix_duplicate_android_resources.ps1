$base = 'mobile-native-android/app/src/main/res/values'
function Get-Names($file, $tags) {
    $content = Get-Content -Raw $file
    $names = @()
    foreach ($tag in $tags) {
        $pattern = '<' + $tag + '[^>]*name="([^"]+)"'
        $matches = [regex]::Matches($content, $pattern)
        foreach ($m in $matches) { $names += $m.Groups[1].Value }
    }
    return $names | Sort-Object -Unique
}
function Remove-Definitions($file, $names) {
    $content = Get-Content -Raw $file
    foreach ($name in $names) {
        $escaped = [regex]::Escape($name)
        $content = [regex]::Replace($content, '<dimen[^>]*name="' + $escaped + '"[^>]*>.*?</dimen>\s*', '', 'Singleline')
        $content = [regex]::Replace($content, '<string[^>]*name="' + $escaped + '"[^>]*>.*?</string>\s*', '', 'Singleline')
        $content = [regex]::Replace($content, '<style[^>]*name="' + $escaped + '"[^>]*>.*?</style>\s*', '', 'Singleline')
        $content = [regex]::Replace($content, '<color[^>]*name="' + $escaped + '"[^>]*>.*?</color>\s*', '', 'Singleline')
    }
    Set-Content -Path $file -Value $content -Encoding UTF8
}
# Remove duplicate spacing definitions from tokens spacing file based on system spacing names
$spacingNames = Get-Names "$base/design_system_spacing.xml" @('dimen')
Remove-Definitions "$base/design_tokens_spacing.xml" $spacingNames
# Remove duplicate typography definitions from tokens typography file based on system typography names
$typographyNames = Get-Names "$base/design_system_typography.xml" @('string','style')
Remove-Definitions "$base/design_tokens_typography.xml" $typographyNames
# Remove duplicate theme definition file
$themesFile = Join-Path $base 'themes.xml'
if (Test-Path $themesFile) { Remove-Item $themesFile -Force }
# Remove duplicate route and marker alias definitions from status file
$statusAliases = @('route_primary','route_alternative','route_optimal','route_traffic','route_blocked','marker_pickup','marker_delivery','marker_current','marker_driver','marker_waypoint','marker_destination')
Remove-Definitions "$base/design_tokens_status.xml" $statusAliases
Write-Host 'Duplicate cleanup script completed.'
