# نظام إدهام - Backend Documentation
# Edham Logistics - Backend Documentation

## 🚀 Quick Start

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Run tests
npm test

# Start simulator
npm run simulate
```

## 📁 Project Structure

```
d:/logest/
├── 📁 controllers/          # 16 Controller files
│   ├── index.js             # Export all controllers
│   ├── baseController.js    # Base CRUD class
│   └── [15 entity controllers]
├── 📁 routes/                 # 15 Route files
│   └── [all routes use controllers]
├── 📁 middleware/             # 6 Middleware files
│   ├── index.js             # Export all middleware
│   ├── auth.js              # Authentication
│   ├── security.js          # Security headers
│   ├── performance.js       # Performance monitoring
│   ├── validate.js          # Input validation
│   ├── upload.js            # File upload
│   └── databaseSecurity.js  # DB security
├── 📁 models/                 # 13 Model files
│   ├── index.js             # Export all models
│   └── [12 entity models]
├── 📁 utils/                  # 5 Utility files
│   ├── index.js             # Export all utils
│   ├── logger.js            # Professional logging
│   ├── helpers.js           # Helper functions
│   ├── distance.js          # Distance calculation
│   └── reports.js           # Report generation
├── 📁 config/
│   └── constants.js         # Centralized constants
├── 📁 sockets/
│   └── index.js             # Socket.io handlers
├── 📁 tests/                  # 4 Test files
│   └── [auth, shipments, trucks, test]
├── 📄 server.js               # Main server entry
├── 📄 index.js                # Root export file
└── 📄 swagger.js              # API documentation
```

## 🔧 Usage

### Importing Components

```javascript
// Method 1: Import from root index.js
const { controllers, models, middleware, utils } = require('./index');
const { ShipmentController } = require('./index').controllers;
const { logger, ROLES, HTTP_STATUS } = require('./index');

// Method 2: Import directly
const { ShipmentController } = require('./controllers');
const { auth, authorize } = require('./middleware');
const { logger } = require('./utils');
const { ROLES, HTTP_STATUS, MESSAGES } = require('./config/constants');
const { Shipment, User } = require('./models');
```

### Using Logger

```javascript
const logger = require('./utils/logger');

logger.info('Information message');
logger.success('Success message', { id: 123 });
logger.warning('Warning message');
logger.error('Error message', error);
```

### Using Constants

```javascript
const { ROLES, HTTP_STATUS, MESSAGES } = require('./config/constants');

// Check role
if (user.role === ROLES.ADMIN) { }

// Return status
res.status(HTTP_STATUS.NOT_FOUND).json({
  message: MESSAGES.NOT_FOUND
});
```

## 🔒 Environment Variables

Create `.env` file:

```env
NODE_ENV=development
PORT=5000
MONGODB_URI=mongodb://localhost:27017/edham
JWT_SECRET=your_secret_key
STRIPE_SECRET_KEY=sk_test_...
```

## 📊 API Endpoints

| Endpoint | Method | Auth Required | Description |
|----------|--------|---------------|-------------|
| `/api/auth/register` | POST | No | Register new user |
| `/api/auth/login` | POST | No | Login user |
| `/api/shipments` | GET | Yes | List shipments |
| `/api/shipments` | POST | Yes | Create shipment |
| `/api/trucks` | GET | Yes | List trucks |
| `/api/users` | GET | Yes | List users |

See `API_STRUCTURE.md` for complete documentation.

## 🧪 Testing

```bash
# Run all tests
npm test

# Run specific test
npm test -- auth.test.js
```

## 📝 Features

- ✅ **16 Professional Controllers** - All using BaseController pattern
- ✅ **15 Organized Routes** - Clean route definitions
- ✅ **6 Security Middleware** - Auth, validation, rate limiting
- ✅ **Professional Logger** - Colored logging with levels
- ✅ **Centralized Constants** - ROLES, HTTP_STATUS, MESSAGES
- ✅ **Pagination Support** - Built into all list endpoints
- ✅ **Soft Delete** - Data retention support
- ✅ **Input Validation** - Joi-based validation
- ✅ **Error Handling** - Unified error responses
- ✅ **API Documentation** - Swagger/OpenAPI
- ✅ **WebSocket Support** - Real-time updates
- ✅ **File Upload** - Multer integration
- ✅ **Comprehensive Tests** - Jest test suite

## 🛠️ Technologies

- Node.js
- Express.js
- MongoDB & Mongoose
- Socket.io
- JWT Authentication
- Stripe Payments
- Jest Testing

## 📞 Support

For support, email info@edham.com

---
**نظام إدهام للنقل المبرد | Edham Logistics**
