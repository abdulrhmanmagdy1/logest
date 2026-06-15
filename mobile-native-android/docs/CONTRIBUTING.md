# 🤝 Contributing to EDHAM Logistics

First off, thank you for considering contributing to EDHAM Logistics! It's people like you that make this project a great tool for the logistics community.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Process](#development-process)
- [Style Guidelines](#style-guidelines)
- [Commit Messages](#commit-messages)
- [Testing](#testing)
- [Documentation](#documentation)

## 📜 Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

### Our Standards

- Be respectful and inclusive
- Welcome newcomers
- Accept constructive criticism gracefully
- Focus on what's best for the community

## 🚀 Getting Started

### Setting Up Development Environment

1. **Fork and Clone**
```bash
git clone https://github.com/YOUR_USERNAME/mobile-native-android.git
cd mobile-native-android
```

2. **Open in Android Studio**
- File → Open → Select project folder
- Wait for Gradle sync

3. **Create Branch**
```bash
git checkout -b feature/your-feature-name
```

## 💡 How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Use a clear descriptive title**
- **Describe the exact steps to reproduce**
- **Provide specific examples**
- **Describe the behavior you observed**
- **Explain which behavior you expected**
- **Include screenshots if possible**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. Create an issue and provide:

- **Use a clear descriptive title**
- **Provide a step-by-step description**
- **Provide specific examples**
- **Explain why this enhancement would be useful**

### Pull Requests

1. Fork the repo and create your branch from `main`
2. If you've added code, add tests
3. Ensure the test suite passes
4. Make sure your code follows our style guidelines
5. Issue that pull request!

## 🔄 Development Process

### Branch Naming

- `feature/feature-name` - New features
- `bugfix/bug-description` - Bug fixes
- `hotfix/critical-fix` - Critical fixes
- `docs/documentation-update` - Documentation

### Workflow

1. **Create Issue** - Describe what you want to do
2. **Get Approval** - Wait for maintainer approval
3. **Create Branch** - From latest `main`
4. **Develop** - Write code following guidelines
5. **Test** - Ensure all tests pass
6. **Document** - Update relevant documentation
7. **PR** - Create pull request with description
8. **Review** - Address review comments
9. **Merge** - After approval

## 🎨 Style Guidelines

### Kotlin Style Guide

Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// ✅ Good
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUser(id: String): Result<User> {
        return try {
            val response = apiService.getUser(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ❌ Bad
class userRepository{
    suspend fun getuser(id:String):Result<User>{
        return try{
            val response=apiService.getUser(id)
            if(response.isSuccessful){
                Result.success(response.body()!!)
            }else{
                Result.failure(Exception(response.errorBody()?.string()))
            }
        }catch(e:Exception){
            Result.failure(e)
        }
    }
}
```

### Compose UI Guidelines

```kotlin
// ✅ Good - Clear naming, proper structure
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Content
    }
}

// ❌ Bad - Unclear, messy
@Composable
fun loginscreen(){
    val vm=viewmodel<AuthViewModel>()
    val state=vm.uiState.collectAsState().value
    Column(Modifier.fillMaxSize().padding(16.dp)){
        // content
    }
}
```

### File Organization

```
📁 feature/
├── 📄 FeatureScreen.kt       // Main screen
├── 📄 FeatureViewModel.kt    // ViewModel
├── 📄 FeatureComponents.kt   // UI components
└── 📄 FeatureUiState.kt      // UI State
```

## 📝 Commit Messages

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting, no code change
- `refactor`: Code refactoring
- `test`: Tests
- `chore`: Build, dependencies

### Examples

```bash
feat(auth): add login with biometric

fix(shipment): resolve tracking update delay

docs(readme): update installation instructions

refactor(network): migrate to coroutines

test(dashboard): add unit tests for ViewModel
```

## 🧪 Testing

### Unit Tests

```kotlin
@Test
fun `login with valid credentials returns success`() = runTest {
    // Given
    val email = "test@example.com"
    val password = "password123"
    
    // When
    val result = authRepository.login(email, password)
    
    // Then
    assertTrue(result.isSuccess)
}
```

### UI Tests

```kotlin
@Test
fun loginScreen_showsError_whenCredentialsInvalid() {
    composeTestRule.setContent {
        LoginScreen()
    }
    
    composeTestRule.onNodeWithText("Email").performTextInput("invalid")
    composeTestRule.onNodeWithText("Password").performTextInput("123")
    composeTestRule.onNodeWithText("Login").performClick()
    
    composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
}
```

### Test Coverage

- Minimum 80% code coverage
- Critical paths must be tested
- Edge cases should be covered

## 📚 Documentation

### Code Documentation

```kotlin
/**
 * Repository for managing user authentication.
 * 
 * @property apiService The API service for network calls
 * @property preferencesManager Local storage manager
 * 
 * @since 1.0.0
 */
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    /**
     * Attempts to login with provided credentials.
     * 
     * @param email User's email address
     * @param password User's password
     * @return Result containing AuthResponse on success
     * 
     * @throws IllegalArgumentException if email or password is blank
     */
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        // Implementation
    }
}
```

### README Updates

When adding features:
- Update feature list
- Add screenshots if UI changes
- Update API documentation if needed

## 🔍 Code Review Process

### What We Look For

- ✅ Code follows style guidelines
- ✅ Tests are included
- ✅ Documentation is updated
- ✅ No breaking changes (or properly handled)
- ✅ Performance considerations
- ✅ Security best practices

### Review Timeline

- Initial review: 1-2 days
- Follow-up reviews: Within 24 hours
- Merge after approval: Within 48 hours

## 🐛 Issue Labels

| Label | Description |
|-------|-------------|
| `bug` | Something isn't working |
| `enhancement` | New feature request |
| `documentation` | Documentation improvement |
| `good first issue` | Good for newcomers |
| `help wanted` | Extra attention needed |
| `priority: high` | High priority issue |

## 🙏 Recognition

Contributors will be:
- Listed in CONTRIBUTORS.md
- Mentioned in release notes
- Added to our Hall of Fame

## 📞 Questions?

- 💬 Join our [Discord](https://discord.gg/edham)
- 📧 Email: dev@edham-logistics.com
- 🐦 Twitter: [@EdhamLogistics](https://twitter.com/EdhamLogistics)

---

Thank you for contributing! 🎉

**The EDHAM Logistics Team**
