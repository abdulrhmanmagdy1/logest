# 🚛 Edham Logistics Backend API

## نظام إدهام للنقل المبرد - Backend API

## 📋 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Installation](#-installation)
- [Environment Variables](#-environment-variables)
- [API Documentation](#-api-documentation)
- [Running the App](#-running-the-app)
- [Docker Setup](#-docker-setup)
- [API Endpoints](#-api-endpoints)

## ✨ Features

- 🔐 **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (RBAC)
  - Password reset functionality
  - Rate limiting for security

- 📦 **Shipment Management**
  - Create, update, track shipments
  - Temperature-controlled cargo support
  - Status tracking with history
  - Route optimization

- 🚛 **Fleet Management**
  - Truck assignment and tracking
  - Maintenance scheduling
  - Driver management
  - GPS tracking integration

- 💰 **Billing & Invoicing**
  - Automatic invoice generation
  - Payment processing
  - Overdue invoice reminders
  - Financial reports

- 📍 **Real-time Tracking**
  - WebSocket-based live tracking
  - Driver location updates
  - Route visualization
  - ETA calculations

- 🔔 **Notifications**
  - Email notifications
  - Push notifications
  - SMS alerts
  - In-app notifications

- 📊 **Analytics & Reports**
  - Dashboard statistics
  - Revenue reports
  - Driver performance metrics
  - Client activity reports

## 🛠️ Tech Stack

- **Runtime:** Node.js 18+
- **Framework:** Express.js
- **Database:** MongoDB (Mongoose)
- **Cache:** Redis
- **Real-time:** Socket.io
- **Authentication:** JWT
- **Documentation:** Swagger/OpenAPI
- **Email:** Nodemailer
- **File Upload:** Multer
- **Validation:** Express Validator
- **Logging:** Winston

## 📦 Installation

```bash
# Clone the repository
git clone https://github.com/edham/logistics-backend.git
cd backend

# Install dependencies
npm install

# Setup environment variables
cp .env.example .env
# Edit .env with your configuration

# Start development server
npm run dev
```

## 🔧 Environment Variables

```env
# Server Configuration
NODE_ENV=development
PORT=5000

# Database
MONGODB_URI=mongodb://localhost:27017/logest

# JWT
JWT_SECRET=your-super-secret-key
JWT_EXPIRE=30d

# Client URL
CLIENT_URL=http://localhost:3000

# Email (SMTP)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-app-password

# Redis
REDIS_URL=redis://localhost:6379
```

## 📚 API Documentation

Access the Swagger UI at:
```
http://localhost:5000/api-docs
```

## 🚀 Running the App

### Development Mode
```bash
npm run dev
```

### Production Mode
```bash
npm start
```

### With Docker
```bash
# Start all services
docker-compose -f docker-compose.backend.yml up -d

# Stop services
docker-compose -f docker-compose.backend.yml down
```

## 🐳 Docker Setup

The application is containerized with:
- **API Server:** Node.js 18 Alpine
- **Database:** MongoDB 6
- **Cache:** Redis 7
- **Admin UI:** MongoDB Express

## 🔌 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login user |
| GET | `/api/v1/auth/me` | Get current user |
| PUT | `/api/v1/auth/update-password` | Update password |

### Shipments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/shipments` | Get all shipments |
| POST | `/api/v1/shipments` | Create shipment |
| GET | `/api/v1/shipments/:id` | Get shipment details |
| PUT | `/api/v1/shipments/:id/status` | Update shipment status |

### Trucks
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/trucks` | Get all trucks |
| POST | `/api/v1/trucks` | Add new truck |
| PUT | `/api/v1/trucks/:id/assign` | Assign driver |

### Invoices
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/invoices` | Get all invoices |
| POST | `/api/v1/invoices` | Create invoice |
| POST | `/api/v1/invoices/:id/payment` | Add payment |

### Tracking
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/tracking/public/:number` | Public tracking |
| POST | `/api/v1/tracking/location` | Update location |
| GET | `/api/v1/tracking/fleet` | Fleet tracking |

### Reports
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/reports/dashboard` | Dashboard stats |
| GET | `/api/v1/reports/shipments` | Shipment reports |
| GET | `/api/v1/reports/revenue` | Revenue reports |

## 🧪 Testing

```bash
# Run tests
npm test

# Run with coverage
npm run test:coverage
```

## 📁 Project Structure

```
backend/
├── config/          # Configuration files
├── controllers/     # Route controllers
├── jobs/           # Cron jobs
├── middleware/     # Express middleware
├── models/         # Mongoose models
├── routes/         # API routes
├── services/       # Business logic services
├── uploads/        # Uploaded files
├── utils/          # Utility functions
├── server.js       # Entry point
└── package.json
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is proprietary and confidential.

## 📞 Support

For support, email support@edham.com or visit https://edham.com

---

© 2024 Edham Logistics. All rights reserved.
