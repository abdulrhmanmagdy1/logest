const { safeRequire } = require("../core/safeRequire");

const routeModules = [
  // 1) Authentication
  { basePath: "/api/auth", router: safeRequire("../../routes/auth") },

  // 2) Roles & permissions
  { basePath: "/api/users", router: safeRequire("../../routes/users") },
  { basePath: "/api/admin", router: safeRequire("../../routes/admin") },

  // 3) Dashboard
  { basePath: "/api/dashboard", router: safeRequire("../../routes/dashboard") },
  { basePath: "/api/analytics", router: safeRequire("../../routes/analytics") },

  // 4) Shipment flow
  { basePath: "/api/shipments", router: safeRequire("../../routes/shipments") },
  { basePath: "/api/orders", router: safeRequire("../../routes/orders") },
  { basePath: "/api/trips", router: safeRequire("../../routes/trips") },
  { basePath: "/api/locations", router: safeRequire("../../routes/locations") },

  // 5) Driver tracking
  { basePath: "/api/drivers", router: safeRequire("../../routes/drivers") },
  { basePath: "/api/tracking", router: safeRequire("../../routes/tracking") },

  // 6) Notifications
  // Notifications route is currently unavailable in the root app.
  { basePath: "/api/mobile", router: safeRequire("../../routes/mobile") },

  // 7) Billing
  { basePath: "/api/payments", router: safeRequire("../../routes/payments") },
  { basePath: "/api/invoices", router: safeRequire("../../routes/invoices") },
  { basePath: "/api/vouchers", router: safeRequire("../../routes/vouchers") },

  // 8) Maintenance
  { basePath: "/api/maintenance", router: safeRequire("../../routes/maintenance") },
  { basePath: "/api/oil-schedule", router: safeRequire("../../routes/oilSchedule") },
  { basePath: "/api/spare-parts", router: safeRequire("../../routes/spareParts") },
  { basePath: "/api/employee-vehicles", router: safeRequire("../../routes/employeeVehicles") },
  { basePath: "/api/trucks", router: safeRequire("../../routes/trucks") },

  // Supporting domains
  { basePath: "/api/audit-logs", router: safeRequire("../../routes/auditLogs") },
  { basePath: "/api/surveys", router: safeRequire("../../routes/surveys") },
];

module.exports = routeModules;
