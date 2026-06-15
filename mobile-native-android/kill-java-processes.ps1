# Kill Java processes that might lock build files
# Run this script before gradlew clean to prevent file locking issues

Write-Host "Stopping Java processes that might lock build files..." -ForegroundColor Yellow

# Kill VS Code/Windsurf Java Language Server processes
$javaProcesses = Get-Process | Where-Object {$_.CommandLine -like "*redhat.java*"} -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Write-Host "Found $($javaProcesses.Count) VS Code Java Language Server processes" -ForegroundColor Cyan
    $javaProcesses | Stop-Process -Force
    Write-Host "Killed VS Code Java Language Server processes" -ForegroundColor Green
} else {
    Write-Host "No VS Code Java Language Server processes found" -ForegroundColor Gray
}

# Kill any Gradle daemon Java processes
$gradleProcesses = Get-Process | Where-Object {$_.CommandLine -like "*gradle*" -and $_.ProcessName -eq "java"} -ErrorAction SilentlyContinue
if ($gradleProcesses) {
    Write-Host "Found $($gradleProcesses.Count) Gradle Java processes" -ForegroundColor Cyan
    $gradleProcesses | Stop-Process -Force
    Write-Host "Killed Gradle Java processes" -ForegroundColor Green
} else {
    Write-Host "No Gradle Java processes found" -ForegroundColor Gray
}

# Stop Gradle daemon
Write-Host "Stopping Gradle daemon..." -ForegroundColor Cyan
& ".\gradlew" --stop

Write-Host "Done. You can now run gradlew clean." -ForegroundColor Green
