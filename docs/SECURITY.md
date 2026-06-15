# 🔒 Security Guide - نظام إدهام

## Edham Logistics Security Documentation

### Overview
This document outlines the security measures implemented in the Edham Logistics system to protect data, prevent attacks, and ensure compliance with security best practices.

---

## 1. Authentication & Authorization

### JWT Token Management
- **Token Type:** JSON Web Tokens (JWT)
- **Algorithm:** HS256
- **Expiration:** 
  - Access Token: 15 minutes
  - Refresh Token: 7 days
- **Storage:** HttpOnly cookies (recommended) or localStorage

### Password Security
- **Minimum Length:** 8 characters
- **Requirements:** 
  - At least one uppercase letter
  - At least one number
  - At least one special character
- **Hashing:** bcrypt with salt rounds of 12
- **Reset Tokens:** 1 hour expiration

### Role-Based Access Control (RBAC)
- **Roles:** Admin, Manager, Driver, Client
- **Permissions:** Defined per role for each endpoint
- **Middleware:** `authorize('role')` for route protection

---

## 2. API Security

### Rate Limiting
- **General API:** 100 requests per 15 minutes
- **Auth Endpoints:** 5 requests per 15 minutes
- **Upload Endpoints:** 10 requests per hour
- **Implementation:** express-rate-limit

### Input Validation
- **Library:** express-validator
- **Validation Rules:** Defined in `middleware/validation.js`
- **Sanitization:** 
  - NoSQL injection prevention (express-mongo-sanitize)
  - XSS protection (xss-clean)

### CORS Configuration
```javascript
cors({
  origin: process.env.CORS_ORIGIN || "*",
  credentials: true,
  methods: ["GET", "POST", "PUT", "PATCH", "DELETE"]
})
```

### Security Headers (Helmet)
- Content Security Policy
- X-Frame-Options
- X-Content-Type-Options
- Strict-Transport-Security (in production)
- Referrer-Policy

---

## 3. Data Protection

### Database Security
- **Connection:** MongoDB with authentication
- **Environment Variables:** All credentials stored in .env
- **Backup:** Regular automated backups
- **Encryption:** Sensitive data encrypted at rest (optional)

### File Upload Security
- **Allowed Types:** Images (jpg, png, jpeg, gif), PDF
- **Max Size:** 5MB per file
- **Storage:** Separate upload directory
- **Validation:** File type and size validation before upload
- **Sanitization:** File name sanitization

### Sensitive Data Handling
- **PII:** Personal Identifiable Information protected
- **Payment Data:** Processed through Stripe (PCI compliant)
- **Location Data:** Encrypted transmission
- **Audit Logs:** All sensitive actions logged

---

## 4. WebSocket Security

### Socket.IO Security
- **CORS:** Configured for trusted origins
- **Authentication:** JWT verification on connection
- **Room-based Access:** Users can only join rooms they're authorized for
- **Rate Limiting:** Per-socket rate limiting

### Location Updates
- **Validation:** Coordinates validated before processing
- **Rate Limiting:** Maximum 1 update per second per truck
- **Authorization:** Only drivers can update their truck location

---

## 5. Error Handling

### Error Messages
- **Development:** Detailed error messages
- **Production:** Generic error messages
- **Logging:** All errors logged with context
- **Monitoring:** Error tracking and alerting

### 404 Handling
- Custom not found handler
- Consistent error response format
- No sensitive information in errors

---

## 6. Monitoring & Logging

### Audit Logging
- **Actions:** Login, CRUD operations, status changes
- **Data:** User ID, action, timestamp, IP address
- **Storage:** MongoDB audit logs collection
- **Retention:** 90 days (configurable)

### Security Events
- **Failed Login Attempts:** Logged and monitored
- **Suspicious Activity:** Detected and flagged
- **Intrusion Detection:** Basic pattern matching
- **Alerting:** Email notifications for critical events

---

## 7. Environment Configuration

### Required Environment Variables
```env
# Database
MONGODB_URI=mongodb://localhost:27017/edham

# JWT
JWT_SECRET=your_very_secure_secret_key
JWT_EXPIRE=15m
JWT_REFRESH_SECRET=your_refresh_secret
JWT_REFRESH_EXPIRE=7d

# Server
PORT=5000
NODE_ENV=production
CORS_ORIGIN=https://yourdomain.com

# Rate Limiting
RATE_LIMIT_WINDOW=900000
RATE_LIMIT_MAX=100

# File Upload
UPLOAD_DIR=./uploads
MAX_FILE_SIZE=5242880

# Email (Optional)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your_email
SMTP_PASS=your_password
```

### Security Best Practices
- Never commit .env files
- Use strong, unique secrets
- Rotate secrets regularly
- Use different secrets for development/production

---

## 8. Common Vulnerabilities & Mitigations

### OWASP Top 10

1. **Injection (SQL/NoSQL)**
   - Mitigation: Parameterized queries, input sanitization

2. **Broken Authentication**
   - Mitigation: Strong passwords, JWT with expiration, rate limiting

3. **Sensitive Data Exposure**
   - Mitigation: HTTPS in production, encrypted storage, secure headers

4. **XML External Entities (XXE)**
   - Mitigation: Not applicable (JSON-based API)

5. **Broken Access Control**
   - Mitigation: RBAC, middleware authorization checks

6. **Security Misconfiguration**
   - Mitigation: Security headers, error handling, audit logging

7. **Cross-Site Scripting (XSS)**
   - Mitigation: Input sanitization, output encoding, CSP headers

8. **Insecure Deserialization**
   - Mitigation: Validate and sanitize serialized data

9. **Using Components with Known Vulnerabilities**
   - Mitigation: Regular dependency updates, npm audit

10. **Insufficient Logging & Monitoring**
    - Mitigation: Comprehensive audit logging, error tracking

---

## 9. Deployment Security

### Production Checklist
- [ ] HTTPS enabled (SSL/TLS certificate)
- [ ] Firewall configured
- [ ] Database access restricted
- [ ] Environment variables secured
- [ ] Security headers enabled
- [ ] Rate limiting active
- [ ] Monitoring and logging enabled
- [ ] Backup strategy in place
- [ ] Incident response plan documented

### Docker Security
- Use official base images
- Run containers as non-root user
- Scan images for vulnerabilities
- Keep images updated
- Use secrets management for sensitive data

---

## 10. Incident Response

### Security Incident Steps
1. **Identify:** Detect the incident
2. **Contain:** Limit the damage
3. **Eradicate:** Remove the threat
4. **Recover:** Restore systems
5. **Learn:** Document and improve

### Contact Information
- **Security Team:** security@edham.com
- **Emergency Hotline:** +966-XXX-XXXX

---

## 11. Compliance

### Data Privacy
- GDPR compliant (if applicable)
- Data retention policies
- User consent management
- Right to deletion

### Industry Standards
- ISO 27001 (information security)
- PCI DSS (payment processing)
- SOC 2 Type II (optional)

---

## 12. Regular Security Tasks

### Daily
- Monitor error logs
- Review failed login attempts
- Check system alerts

### Weekly
- Review audit logs
- Check for security updates
- Monitor unusual activity

### Monthly
- Update dependencies
- Review access logs
- Test backup recovery
- Security audit

### Quarterly
- Penetration testing
- Security training
- Policy review
- Disaster recovery test

---

## 13. Resources

### Tools
- OWASP ZAP - Security testing
- npm audit - Dependency vulnerabilities
- Snyk - Security scanning
- Log monitoring - ELK Stack

### Documentation
- OWASP Top 10: https://owasp.org/www-project-top-ten/
- Node.js Security: https://nodejs.org/en/docs/guides/security/
- MongoDB Security: https://www.mongodb.com/docs/manual/administration/security/

---

**Last Updated:** April 2026
**Version:** 1.0.0
