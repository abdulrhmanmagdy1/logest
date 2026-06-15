# 📚 Training Guide - نظام إدهام

## Edham Logistics Training Documentation

### Overview
This guide provides comprehensive training materials for users, administrators, and developers working with the Edham Logistics system.

---

## 1. User Training

### For Drivers

#### Dashboard Navigation
- **Login Process:**
  1. Open the mobile app
  2. Enter email and password
  3. Tap "Login"
  
- **Main Dashboard:**
  - View assigned shipments
  - Check truck status
  - View upcoming trips
  - Update location

#### Shipment Management
- **Accepting Shipments:**
  1. Go to "Shipments" tab
  2. Tap on assigned shipment
  3. Review details
  4. Tap "Accept"

- **Updating Status:**
  1. Open shipment details
  2. Tap "Update Status"
  3. Select new status
  4. Add notes if needed
  5. Tap "Submit"

- **Location Updates:**
  - Automatic GPS tracking (enabled by default)
  - Manual update if GPS fails
  - Update every 5 minutes during transit

#### Trip Management
- **Starting Trip:**
  1. Navigate to assigned shipment
  2. Tap "Start Trip"
  3. Confirm truck inspection
  4. Begin journey

- **Completing Trip:**
  1. Arrive at delivery location
  2. Tap "Complete Trip"
  3. Upload delivery photos
  4. Get recipient signature
  5. Submit completion

---

### For Managers

#### Admin Dashboard
- **Overview:**
  - Total shipments count
  - Revenue metrics
  - Fleet utilization
  - Delivery performance

- **Shipment Management:**
  - Create new shipments
  - Assign to trucks/drivers
  - Track in real-time
  - Manage status changes

- **Fleet Management:**
  - View all trucks
  - Monitor truck status
  - Schedule maintenance
  - Track fuel consumption

- **Driver Management:**
  - View driver profiles
  - Assign shipments
  - Monitor performance
  - Manage schedules

#### Analytics & Reports
- **Dashboard Metrics:**
  - Filter by date range
  - Export to CSV/PDF
  - Compare periods
  - Drill down details

- **Monthly Reports:**
  - Revenue breakdown
  - Shipment statistics
  - Driver performance
  - Fleet efficiency

- **Custom Reports:**
  - Create report templates
  - Schedule automated reports
  - Share with team
  - Archive historical data

---

### For Clients

#### Order Placement
- **Creating Shipment:**
  1. Click "New Shipment"
  2. Enter pickup details
  3. Enter delivery details
  4. Specify weight/quantity
  5. Add special instructions
  6. Submit request

#### Tracking Shipments
- **Real-time Tracking:**
  - View on map
  - Check estimated arrival
  - Contact driver
  - Receive notifications

#### Invoice Management
- **Viewing Invoices:**
  - Access from dashboard
  - Download PDF
  - View payment history
  - Check balance

- **Making Payments:**
  - Online payment options
  - Bank transfer details
  - Payment scheduling
  - Receipt generation

---

## 2. Administrator Training

### System Configuration

#### User Management
- **Adding Users:**
  1. Go to Settings > Users
  2. Click "Add User"
  3. Enter user details
  4. Assign role
  5. Send invitation

- **Managing Roles:**
  - Admin: Full system access
  - Manager: Operations access
  - Driver: Mobile app access
  - Client: Order tracking

#### System Settings
- **Company Information:**
  - Update company details
  - Configure contact info
  - Set business hours
  - Add logo

- **Notification Settings:**
  - Configure email alerts
  - Set SMS notifications
  - Manage push notifications
  - Configure alert thresholds

### Security Management

#### Access Control
- **Role Permissions:**
  - Define role capabilities
  - Grant/revoke permissions
  - Audit access logs
  - Review user activity

#### Security Policies
- **Password Requirements:**
  - Minimum 8 characters
  - Complex password rules
  - Expiration policy
  - Reset procedures

- **Two-Factor Authentication:**
  - Enable for admins
  - Configure methods
  - Recovery options
  - User guidance

---

## 3. Developer Training

### Getting Started

#### Environment Setup
```bash
# Clone repository
git clone https://github.com/edham/logistics.git

# Install backend dependencies
cd logistics
npm install

# Install frontend dependencies
cd client
npm install

# Set up environment variables
cp .env.example .env
# Edit .env with your configuration

# Start database
mongod

# Start backend server
npm run dev

# Start frontend (new terminal)
cd client
npm start
```

#### Project Structure
```
logistics/
├── config/           # Configuration files
├── controllers/      # Business logic
├── middleware/       # Express middleware
├── models/          # Mongoose models
├── routes/          # API routes
├── sockets/         # Socket.IO handlers
├── utils/           # Utility functions
├── client/          # React frontend
└── docs/            # Documentation
```

### API Development

#### Creating a New Endpoint
```javascript
// 1. Create controller
// controllers/shipmentController.js
static async create(req, res) {
  try {
    const shipment = await Shipment.create(req.body)
    res.status(201).json({ success: true, data: shipment })
  } catch (error) {
    res.status(500).json({ success: false, message: error.message })
  }
}

// 2. Create route
// routes/shipments.js
router.post('/', auth, validateShipment, create)

// 3. Add to server
// server.js
app.use('/api/shipments', require('./routes/shipments'))
```

#### Best Practices
- Use async/await for async operations
- Implement proper error handling
- Validate input data
- Sanitize user input
- Use environment variables
- Write unit tests
- Document API endpoints

### Frontend Development

#### Component Structure
```jsx
// Example component
import React, { useState, useEffect } from 'react'
import api from '../services/api'

export default function ShipmentList() {
  const [shipments, setShipments] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchShipments()
  }, [])

  const fetchShipments = async () => {
    try {
      const response = await api.get('/shipments')
      setShipments(response.data)
    } catch (error) {
      console.error('Error fetching shipments:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div>Loading...</div>

  return (
    <div>
      {shipments.map(shipment => (
        <ShipmentCard key={shipment._id} shipment={shipment} />
      ))}
    </div>
  )
}
```

#### State Management
- Use React hooks for local state
- Context API for global state
- Consider Redux for complex state
- Implement proper data fetching

### Database Operations

#### MongoDB Queries
```javascript
// Find documents
const shipments = await Shipment.find({ status: 'pending' })

// Find with projection
const shipments = await Shipment.find({}, { description: 1, status: 1 })

// Find with pagination
const shipments = await Shipment.find()
  .skip((page - 1) * limit)
  .limit(limit)

// Aggregate
const stats = await Shipment.aggregate([
  { $match: { status: 'delivered' } },
  { $group: { _id: '$clientId', total: { $sum: 1 } } }
])
```

---

## 4. Troubleshooting Guide

### Common Issues

#### Login Problems
**Issue:** Cannot login
**Solutions:**
- Check email/password
- Verify account is active
- Reset password if needed
- Check internet connection
- Clear browser cache

#### Shipment Not Updating
**Issue:** Shipment status not changing
**Solutions:**
- Refresh the page
- Check permissions
- Verify network connection
- Check server status
- Contact support

#### Location Not Tracking
**Issue:** GPS not working
**Solutions:**
- Enable location services
- Grant app permissions
- Check GPS signal
- Restart the app
- Update app version

### Error Messages

#### Common Error Codes
- `401 Unauthorized` - Login required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource doesn't exist
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error

#### Reporting Issues
1. Note the error message
2. Record steps to reproduce
3. Take screenshot if possible
4. Contact support team
5. Provide system details

---

## 5. Best Practices

### For Users
- Keep passwords secure
- Log out after use
- Report issues promptly
- Follow company policies
- Update app regularly

### For Administrators
- Review logs regularly
- Update system frequently
- Backup data daily
- Monitor performance
- Train new users

### For Developers
- Write clean code
- Document changes
- Test thoroughly
- Follow security guidelines
- Collaborate with team

---

## 6. Support Resources

### Documentation
- API Documentation: `/docs/API_DOCUMENTATION.md`
- Security Guide: `/docs/SECURITY.md`
- Performance Guide: `/docs/PERFORMANCE.md`
- Color Palette: `/docs/COLOR_PALETTE.md`

### Contact Information
- **Technical Support:** support@edham.com
- **Emergency Hotline:** +966-XXX-XXXX
- **Email:** help@edham.com

### Training Sessions
- **New User Training:** Weekly (Wednesdays 10 AM)
- **Admin Training:** Monthly (First Monday)
- **Developer Training:** Quarterly
- **Refresher Courses:** As needed

---

## 7. Assessment

### User Knowledge Check
- [ ] Can login successfully
- [ ] Can navigate dashboard
- [ ] Can create/track shipments
- [ ] Can update status
- [ ] Can report issues

### Administrator Knowledge Check
- [ ] Can manage users
- [ ] Can configure settings
- [ ] Can review logs
- [ ] Can handle security
- [ ] Can generate reports

### Developer Knowledge Check
- [ ] Can set up environment
- [ ] Can create API endpoints
- [ ] Can write React components
- [ ] Can debug issues
- [ ] Can follow best practices

---

## 8. Continuous Learning

### Resources
- **Node.js Documentation:** https://nodejs.org/docs/
- **React Documentation:** https://react.dev/
- **MongoDB Documentation:** https://www.mongodb.com/docs/
- **Express Documentation:** https://expressjs.com/

### Courses
- Full-stack JavaScript
- Database Design
- API Development
- Security Best Practices
- Performance Optimization

### Community
- GitHub Repository
- Discussion Forums
- Slack Channel
- Monthly Meetups
- Annual Conference

---

**Last Updated:** April 2026
**Version:** 1.0.0
