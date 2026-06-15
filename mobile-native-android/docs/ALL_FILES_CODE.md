# Edham Logistics Android Application - Complete File Structure

## Project Root Directory: `d:\logest\mobile-native-android`

### Main Application Files:

#### Configuration Files:
- **build.gradle.kts** - `d:\logest\mobile-native-android\build.gradle.kts`
- **settings.gradle.kts** - `d:\logest\mobile-native-android\settings.gradle.kts`
- **gradlew** - `d:\logest\mobile-native-android\gradlew`
- **gradlew.bat** - `d:\logest\mobile-native-android\gradlew.bat`
- **.gitignore** - `d:\logest\mobile-native-android\.gitignore`

#### Documentation Files:
- **README.md** - `d:\logest\mobile-native-android\README.md`
- **APP_STRUCTURE.md** - `d:\logest\mobile-native-android\APP_STRUCTURE.md`
- **BUILD_APK.md** - `d:\logest\mobile-native-android\BUILD_APK.md`
- **CHANGELOG.md** - `d:\logest\mobile-native-android\CHANGELOG.md`
- **COMPLETE_GUIDE.md** - `d:\logest\mobile-native-android\COMPLETE_GUIDE.md`
- **CONTRIBUTING.md** - `d:\logest\mobile-native-android\CONTRIBUTING.md`
- **FINAL_SUMMARY.md** - `d:\logest\mobile-native-android\FINAL_SUMMARY.md`
- **LICENSE** - `d:\logest\mobile-native-android\LICENSE`

#### Application Source Code (`app/` directory):
- **MainActivity.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\main\MainActivity.kt`
- **Theme.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\theme\Theme.kt`
- **Color.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\theme\Color.kt`
- **Type.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\theme\Type.kt`
- **LoginScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\auth\LoginScreen.kt`
- **RoleSelectionScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\roleselection\RoleSelectionScreen.kt`
- **ClientDashboardScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\client\ClientDashboardScreen.kt`
- **SupervisorDashboardScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\supervisor\SupervisorDashboardScreen.kt`
- **DriverDashboardScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\driver\DriverDashboardScreen.kt`
- **SupervisorMapScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\supervisor\SupervisorMapScreen.kt`
- **DriverDocumentationScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\driver\DriverDocumentationScreen.kt`
- **ShipmentDetailsScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\shipment\ShipmentDetailsScreen.kt`
- **SplashScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\splash\SplashScreen.kt`
- **TrackingMapScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\tracking\TrackingMapScreen.kt`
- **BaseViewModel.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\base\BaseViewModel.kt`
- **UiState.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\base\UiState.kt`
- **ShipmentAdapter.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\client\adapter\ShipmentAdapter.kt`

#### Additional Application Files:
- **EdhamApplication.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\EdhamApplication.kt`
- **EdhamLogisticsApp.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\EdhamLogisticsApp.kt`
- **MainActivity.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\MainActivity.kt`

#### Data Layer Files:
- **PreferencesManager.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\local\PreferencesManager.kt`
- **AuthPreferences.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\local\prefs\AuthPreferences.kt`
- **Driver.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\model\Driver.kt`
- **Shipment.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\model\Shipment.kt`
- **User.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\model\User.kt`
- **UserRole.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\model\UserRole.kt`
- **ApiService.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\network\ApiService.kt`
- **AuthInterceptor.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\network\AuthInterceptor.kt`
- **ApiService.kt** (Remote) - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\remote\ApiService.kt`
- **AuthApiService.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\remote\api\AuthApiService.kt`
- **ShipmentApiService.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\remote\api\ShipmentApiService.kt`
- **ApiResponse.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\remote\dto\ApiResponse.kt`
- **InvoiceDto.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\remote\dto\InvoiceDto.kt`
- **ShipmentDto.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\remote\dto\ShipmentDto.kt`
- **UserDto.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\remote\dto\UserDto.kt`
- **AuthInterceptor.kt** (Remote) - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\remote\interceptor\AuthInterceptor.kt`
- **AuthRepository.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\repository\AuthRepository.kt`
- **DriverRepository.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\repository\DriverRepository.kt`
- **RoleRepository.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\repository\RoleRepository.kt`
- **ShipmentRepository.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\data\repository\ShipmentRepository.kt`

#### Dependency Injection Files:
- **AppModule.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\di\AppModule.kt`
- **NetworkModule.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\di\NetworkModule.kt`

#### Navigation Files:
- **AppNavigation.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\navigation\AppNavigation.kt`
- **EdhamNavigation.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\navigation\EdhamNavigation.kt`

#### Presentation Layer Files:
- **AuthViewModel.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\presentation\auth\AuthViewModel.kt`
- **DashboardScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\presentation\dashboard\DashboardScreen.kt`
- **DashboardViewModel.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\presentation\dashboard\DashboardViewModel.kt`
- **ShipmentsScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\presentation\shipments\ShipmentsScreen.kt`
- **TrackingScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\presentation\tracking\TrackingScreen.kt`

#### Service Files:
- **LocationTrackingService.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\service\LocationTrackingService.kt`

#### UI Additional Files:
- **Animations.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\animation\Animations.kt`
- **AuthViewModel.kt** (UI) - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\auth\AuthViewModel.kt`
- **RegisterScreen.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\auth\RegisterScreen.kt`
- **LoginViewModel.kt** - `d:\logest\mobile-native-android\app\src\main\java\com\edham\logistics\ui\auth\viewmodel\LoginViewModel.kt`

#### Test Files:
- **ExampleInstrumentedTest.kt** - `d:\logest\mobile-native-android\app\src\androidTest\java\com\edham\logistics\ExampleInstrumentedTest.kt`

### Key Features:
- Premium dark theme with Edham branding
- Multi-role support (Client, Supervisor, Driver)
- Real-time shipment tracking
- Document management system
- Modern Material Design 3 UI

### Technology Stack:
- Kotlin programming language
- Jetpack Compose UI
- Material Design 3
- Android Architecture Components
- MVVM architecture pattern

### Development Environment:
- Android Studio
- Gradle build system
- Git version control

This file serves as a comprehensive index of all files in the Edham Logistics Android application project.
