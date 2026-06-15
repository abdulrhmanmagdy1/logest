# 🧭 Central Navigation Layer - Documentation

## 📋 Overview

The Central Navigation Layer provides a unified, secure, and trackable navigation system for the Edham Logistics Android application. It enforces role-based access control, prevents direct navigation from fragments, and provides consistent animations across all screens.

## 🏗️ Architecture

### Core Components

```
Central Navigation Layer
├── AppNavigationController
│   ├── Navigation Management
│   ├── Role-Based Access Control
│   ├── Animation Configuration
│   ├── State Tracking
│   └── History Management
├── NavigationInterceptor
│   ├── Direct Navigation Prevention
│   ├── Fragment Extensions
│   └── Access Validation
├── NavigationLintRules
│   ├── Direct Navigation Detection
│   ├── Code Quality Enforcement
│   └── Build-time Validation
└── Navigation Animations
    ├── Slide Animations
    ├── Fade Animations
    └── Transition Configuration
```

## 🚀 Key Features

### 1. **Centralized Navigation Controller**
- **Single Source of Truth**: All navigation goes through `AppNavigationController`
- **Role-Based Access**: Automatic validation based on user roles
- **State Management**: Complete tracking of navigation state and history
- **Error Handling**: Comprehensive error handling and recovery

### 2. **Direct Navigation Prevention**
- **NavigationInterceptor**: Prevents direct `NavController.navigate()` calls
- **Fragment Extensions**: Safe navigation methods for fragments
- **Lint Rules**: Build-time detection of direct navigation usage
- **Access Validation**: Runtime validation of navigation permissions

### 3. **Unified Animations**
- **Slide Animations**: Smooth slide transitions for normal navigation
- **Fade Animations**: Modal dialogs with fade effects
- **Consistent Experience**: Same animations across all screens
- **Performance Optimized**: Efficient animation implementations

### 4. **Navigation State Tracking**
- **Real-time State**: Live tracking of current destination
- **Navigation History**: Complete history with size limits
- **Statistics**: Navigation analytics and metrics
- **State Observers**: LiveData for reactive UI updates

## 📱 Usage Examples

### Basic Navigation

```kotlin
// Navigate to a destination
appNavigationController.navigateToDestination(
    NavigationDestination.CUSTOMER_DASHBOARD
)

// Navigate with arguments
val args = Bundle().apply {
    putString("shipment_id", "12345")
}
appNavigationController.navigateToDestination(
    NavigationDestination.SHIPMENT_DETAILS,
    arguments = args
)

// Navigate as modal
appNavigationController.navigateToDestination(
    NavigationDestination.CREATE_SHIPMENT,
    isModal = true
)
```

### Fragment Navigation

```kotlin
class CustomerDashboardFragment : Fragment() {
    
    private fun onShipmentClicked(shipmentId: String) {
        // Safe navigation through extension
        navigateTo(
            NavigationDestination.SHIPMENT_DETAILS,
            args = Bundle().apply { putString("shipment_id", shipmentId) }
        )
    }
    
    private fun onBackClicked() {
        // Safe back navigation
        navigateBack()
    }
    
    private fun canNavigateToAdmin(): Boolean {
        // Check access before showing button
        return canAccess(NavigationDestination.ADMIN_DASHBOARD)
    }
}
```

### Role-Based Navigation

```kotlin
// Navigate to user's dashboard
appNavigationController.navigateToDashboard()

// Check if user can access destination
if (appNavigationController.canAccessDestination(NavigationDestination.ADMIN_DASHBOARD)) {
    // Show admin button
}
```

## 🎯 Navigation Destinations

### Dashboard Destinations
- `CUSTOMER_DASHBOARD` - Customer main dashboard
- `DRIVER_DASHBOARD` - Driver main dashboard  
- `SUPERVISOR_DASHBOARD` - Supervisor main dashboard
- `ACCOUNTANT_DASHBOARD` - Accountant main dashboard
- `WORKSHOP_DASHBOARD` - Workshop main dashboard
- `ADMIN_DASHBOARD` - Admin main dashboard

### Feature Destinations
- `CREATE_SHIPMENT` - Create new shipment
- `SHIPMENT_DETAILS` - View shipment details
- `SHIPMENT_TRACKING` - Track shipment location
- `DRIVER_TASKS` - Driver task management
- `DRIVER_EARNINGS` - Driver earnings view
- `INVOICES` - Financial invoices
- `PAYMENTS` - Payment management
- `PROFILE` - User profile
- `SETTINGS` - Application settings

## 🔐 Security Features

### Role-Based Access Control

```kotlin
// Automatic role validation
private fun isNavigationAllowed(destination: NavigationDestination): Boolean {
    val userRole = getCurrentUserRole() ?: return false
    
    return when (destination) {
        NavigationDestination.CUSTOMER_DASHBOARD -> userRole == UserRole.CUSTOMER
        NavigationDestination.DRIVER_DASHBOARD -> userRole == UserRole.DRIVER
        NavigationDestination.ADMIN_DASHBOARD -> userRole == UserRole.ADMIN
        // ... other destinations
        else -> false
    }
}
```

### Navigation Interception

```kotlin
// Prevents direct navigation calls
fun navigateToFragment(
    fragment: Fragment,
    destinationId: Int,
    args: Bundle? = null,
    isModal: Boolean = false
) {
    // Validate through centralized controller
    appNavigationController.navigateToFragment(destinationId, args, isModal)
}
```

## 🎨 Animation Configuration

### Default Slide Animations
- **Enter**: `slide_in_right.xml` - Slide from right
- **Exit**: `slide_out_left.xml` - Slide to left
- **Pop Enter**: `slide_in_left.xml` - Slide from left
- **Pop Exit**: `slide_out_right.xml` - Slide to right

### Modal Fade Animations
- **Enter**: `fade_in.xml` - Fade in
- **Exit**: `fade_out.xml` - Fade out

### Animation Usage

```kotlin
// Normal navigation with slide animations
appNavigationController.navigateToDestination(destination, isModal = false)

// Modal navigation with fade animations
appNavigationController.navigateToDestination(destination, isModal = true)
```

## 📊 State Tracking

### Navigation State

```kotlin
sealed class NavigationState {
    object Idle : NavigationState()
    data class Success(val destination: NavigationDestination) : NavigationState()
    data class Error(val message: String) : NavigationState()
    data class Loading(val destination: NavigationDestination) : NavigationState()
}
```

### Navigation History

```kotlin
// Get navigation history
val history = appNavigationController.getNavigationHistory()

// Clear history (useful for logout)
appNavigationController.clearNavigationHistory()

// Get current destination
val current = appNavigationController.currentDestination.value
```

### Navigation Statistics

```kotlin
// Get navigation statistics
val stats = appNavigationController.getNavigationStatistics()
println("Current: ${stats.currentDestination}")
println("History size: ${stats.historySize}")
println("Can go back: ${stats.canGoBack}")
```

## 🧪 Testing

### Unit Tests

```kotlin
@Test
fun `test navigateToDestination with valid role`() {
    // Given
    val destination = NavigationDestination.CUSTOMER_DASHBOARD
    every { mockAuthenticationManager.currentUser.value } returns 
        UserEntity(role = UserRole.CUSTOMER)
    
    // When
    appNavigationController.navigateToDestination(destination)
    
    // Then
    verify { mockNavController.navigate(destination.fragmentId, any(), any()) }
    assertEquals(NavigationState.Success(destination), appNavigationController.getCurrentNavigationState())
}
```

### Integration Tests

```kotlin
@Test
fun `test navigation flow from login to dashboard`() {
    // Test complete navigation flow
    appNavigationController.navigateToLogin()
    // Simulate login
    appNavigationController.navigateToDashboard()
    
    // Verify navigation state and history
    val history = appNavigationController.getNavigationHistory()
    assertEquals(2, history.size)
    assertEquals(NavigationDestination.CUSTOMER_DASHBOARD, history.last())
}
```

## 🔧 Implementation Details

### Initialization in MainActivity

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize navigation controller
    appNavigationController.initialize(navController, supportFragmentManager)
    
    // Initialize navigation interceptor
    NavigationInterceptor.initialize(appNavigationController)
}
```

### Dependency Injection

```kotlin
@ActivityScoped
class AppNavigationController @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val authenticationManager: AuthenticationManager
) {
    // Implementation
}
```

## 📈 Performance Considerations

### Memory Management
- **History Limit**: Maximum 50 navigation entries
- **State Cleanup**: Automatic cleanup on logout
- **Efficient Data Structures**: Use of optimized collections

### Animation Performance
- **Hardware Acceleration**: Animations use GPU acceleration
- **Duration Optimization**: 300ms for slides, 250ms for fades
- **Interpolator Selection**: Decelerate/accelerate for smooth motion

## 🚨 Error Handling

### Navigation Errors
- **Access Denied**: Clear error messages for permission issues
- **Invalid Destinations**: Graceful handling of unknown destinations
- **Navigation Failures**: Recovery mechanisms and fallback options

### State Recovery
- **Error States**: Clear error state reporting
- **Recovery Options**: Automatic retry or user intervention
- **Logging**: Comprehensive error logging for debugging

## 🎯 Best Practices

### For Developers
1. **Always use AppNavigationController** - Never call NavController.navigate() directly
2. **Check permissions before navigation** - Use canAccessDestination() method
3. **Handle navigation state** - Observe navigation state LiveData
4. **Use fragment extensions** - Prefer navigateTo() extension methods
5. **Test navigation flows** - Include navigation tests in unit tests

### For Navigation Design
1. **Consistent animations** - Use same animation types for similar flows
2. **Role-based design** - Design navigation with user roles in mind
3. **Error states** - Plan for navigation failures and access denied
4. **Performance** - Consider animation performance and memory usage

## 🔮 Future Enhancements

### Planned Features
- **Deep Linking**: Support for deep link navigation
- **Custom Animations**: Configurable animation sets
- **Navigation Analytics**: Advanced navigation usage analytics
- **Gesture Navigation**: Support for swipe gestures
- **Voice Navigation**: Voice-activated navigation commands

### Extensibility
- **Plugin System**: Support for custom navigation providers
- **Animation Library**: Extensible animation system
- **Role Extensions**: Support for dynamic role definitions
- **State Persistence**: Navigation state persistence across app restarts

---

## 📝 Summary

The Central Navigation Layer provides:

✅ **Unified Navigation** - Single controller for all navigation operations
✅ **Role-Based Security** - Automatic access control based on user roles  
✅ **Direct Navigation Prevention** - Build-time and runtime enforcement
✅ **Consistent Animations** - Unified transition effects across all screens
✅ **State Tracking** - Complete navigation history and state management
✅ **Error Handling** - Comprehensive error handling and recovery
✅ **Testing Support** - Full test coverage for navigation flows
✅ **Performance Optimization** - Efficient memory usage and smooth animations

This system ensures a secure, maintainable, and consistent navigation experience throughout the Edham Logistics application.
