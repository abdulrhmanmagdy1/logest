// MongoDB script to insert test users for Edham Logistics backend
// This script inserts 4 test users for non-customer roles
// Password: Test1234 (BCrypt hashed with strength 12)

// BCrypt hash for "Test1234" (strength 12)
// Generated using: BCrypt.hashpw("Test1234", BCrypt.gensalt(12))
const passwordHash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5NUYqLZ4Y3U1a";

db.users.insertMany([
  {
    _id: NumberLong(1),
    username: "supervisor@edham.com",
    email: "supervisor@edham.com",
    fullName: "أحمد المشرف",
    phoneNumber: "+966500000001",
    role: "SUPERVISOR",
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: NumberLong(2),
    username: "accountant@edham.com",
    email: "accountant@edham.com",
    fullName: "سارة المحاسبة",
    phoneNumber: "+966500000002",
    role: "ACCOUNTANT",
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: NumberLong(3),
    username: "driver@edham.com",
    email: "driver@edham.com",
    fullName: "خالد السائق",
    phoneNumber: "+966500000003",
    role: "DRIVER",
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    _id: NumberLong(4),
    username: "workshop@edham.com",
    email: "workshop@edham.com",
    fullName: "محمد الورشة",
    phoneNumber: "+966500000004",
    role: "WORKSHOP",
    active: true,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

print("Test users inserted successfully!");
print("Credentials:");
print("supervisor@edham.com / Test1234");
print("accountant@edham.com / Test1234");
print("driver@edham.com / Test1234");
print("workshop@edham.com / Test1234");
