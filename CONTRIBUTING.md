# Contributing to Edham Logistics

Thank you for your interest in contributing to Edham Logistics! This document provides guidelines and instructions for contributing.

## 🚀 Getting Started

### Prerequisites

- Node.js 18+
- MongoDB 6+
- npm or yarn

### Setup

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/your-username/edham-logistics.git
   cd edham-logistics
   ```

3. Install dependencies:
   ```bash
   npm install
   cd client && npm install
   ```

4. Copy environment variables:
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

5. Start development server:
   ```bash
   npm run dev
   ```

## 📁 Project Structure

```
edham-logistics/
├── controllers/     # Business logic controllers
├── middleware/      # Express middleware
├── models/          # Mongoose models
├── routes/          # API routes
├── utils/           # Utility functions
├── config/          # Configuration files
├── tests/           # Test files
├── client/          # React frontend
└── sockets/         # Socket.io handlers
```

## 📝 Coding Standards

### JavaScript Style Guide

- Use ES6+ features
- Use async/await for asynchronous operations
- Use meaningful variable names
- Add JSDoc comments for functions

### Example

```javascript
/**
 * Calculate delivery time estimate
 * @param {Number} distance - Distance in km
 * @param {Number} avgSpeed - Average speed km/h
 * @returns {Number} Estimated hours
 */
const calculateDeliveryTime = (distance, avgSpeed = 60) => {
  return distance / avgSpeed;
};
```

### Naming Conventions

- **Controllers**: `PascalCase` (e.g., `ShipmentController.js`)
- **Middleware**: `camelCase` (e.g., `authMiddleware.js`)
- **Utils**: `camelCase` (e.g., `helpers.js`)
- **Constants**: `UPPER_SNAKE_CASE`

## 🧪 Testing

### Running Tests

```bash
# Run all tests
npm test

# Run with coverage
npm run test:coverage

# Run specific test file
npm test -- auth.test.js
```

### Writing Tests

- Place tests in `tests/` directory
- Use descriptive test names
- Follow AAA pattern: Arrange, Act, Assert

```javascript
describe('AuthController', () => {
  describe('register', () => {
    it('should create new user with valid data', async () => {
      // Arrange
      const userData = { name: 'Test', email: 'test@test.com', password: '123456' };
      
      // Act
      const result = await AuthController.register(userData);
      
      // Assert
      expect(result.success).toBe(true);
    });
  });
});
```

## 🔒 Security Guidelines

- Never commit `.env` files
- Sanitize all user inputs
- Use parameterized queries
- Implement proper authentication checks
- Validate file uploads

## 🐛 Bug Reports

When reporting bugs, please include:

1. Clear description of the issue
2. Steps to reproduce
3. Expected vs actual behavior
4. Environment details (Node version, OS)
5. Error messages or screenshots

## 💡 Feature Requests

Feature requests are welcome! Please:

1. Check if the feature already exists
2. Describe the feature and its use case
3. Explain why it would be valuable

## 🔄 Pull Request Process

1. Create a new branch: `git checkout -b feature/your-feature-name`
2. Make your changes
3. Add tests if applicable
4. Run tests: `npm test`
5. Commit with clear messages
6. Push to your fork
7. Submit a Pull Request

### PR Guidelines

- Keep changes focused and small
- Update documentation if needed
- Ensure all tests pass
- Request review from maintainers
- Respond to feedback promptly

## 🌐 Code Review

All submissions require review. We look for:

- Code quality and readability
- Test coverage
- Security considerations
- Performance impact
- Documentation completeness

## 📞 Questions?

Feel free to open an issue for:
- Questions about the codebase
- Clarification on guidelines
- Help with contributions

## 🙏 Recognition

Contributors will be:
- Listed in CONTRIBUTORS.md
- Mentioned in release notes
- Credited in documentation

Thank you for contributing to Edham Logistics!
