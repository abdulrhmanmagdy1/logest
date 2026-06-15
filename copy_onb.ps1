$src = "C:\Users\ABD\Downloads"
$dst = "D:\logest\mobile-native-android\app\src\main\res\drawable"
$map = @{
    "download (1).jpg" = "onb_hero_1.jpg"
    "download.jpg" = "onb_hero_2.jpg"
    "Easy Electric Van Booking Online for Businesses.jpg" = "onb_hero_3.jpg"
    "logistic.jpg" = "onb_hero_4.jpg"
    "Кто оплачивает внутреннюю доставку автомобилей из США_.jpg" = "onb_hero_5.jpg"
    "Как избежать штрафов за перевозку_ 7 секретов для бизнеса.jpg" = "onb_hero_6.jpg"
    "авиа.jpg" = "onb_hero_7.jpg"
}
foreach ($k in $map.Keys) {
    Copy-Item "$src\$k" "$dst\$($map[$k])" -Force
    Write-Host "Copied $k -> $($map[$k])"
}
