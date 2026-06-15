const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());

// Health check
app.get('/api/actuator/health', (req, res) => {
  res.json({ status: 'UP' });
});

// Login endpoint
app.post('/api/v1/auth/login', (req, res) => {
  const { email, password } = req.body;
  
  // Mock login - accept any credentials
  res.json({
    success: true,
    message: 'Login successful',
    data: {
      token: 'mock-jwt-token-1234567890',
      user: {
        id: 1,
        email: email || 'user@example.com',
        name: 'Test User',
        role: 'USER'
      }
    }
  });
});

// Start server
app.listen(8080, '0.0.0.0', () => {
  console.log('Mock server running on http://localhost:8080');
  console.log('Emulator can access via http://10.0.2.2:8080');
});
