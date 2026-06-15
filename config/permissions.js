const CAPABILITIES = {
  AUTHENTICATION: "authentication",
  ROLES_PERMISSIONS: "roles_permissions",
  DASHBOARD: "dashboard",
  SHIPMENT_FLOW: "shipment_flow",
  DRIVER_TRACKING: "driver_tracking",
  NOTIFICATIONS: "notifications",
  BILLING: "billing",
  MAINTENANCE: "maintenance",
};

const CAPABILITY_ROLES = {
  [CAPABILITIES.AUTHENTICATION]: ["client", "driver", "employee", "maintenance", "accountant", "supervisor", "admin", "super_admin"],
  [CAPABILITIES.ROLES_PERMISSIONS]: ["supervisor", "admin", "super_admin"],
  [CAPABILITIES.DASHBOARD]: ["client", "driver", "employee", "maintenance", "accountant", "supervisor", "admin", "super_admin"],
  [CAPABILITIES.SHIPMENT_FLOW]: ["client", "driver", "supervisor", "admin", "super_admin"],
  [CAPABILITIES.DRIVER_TRACKING]: ["driver", "supervisor", "client", "admin", "super_admin"],
  [CAPABILITIES.NOTIFICATIONS]: ["client", "driver", "employee", "maintenance", "accountant", "supervisor", "admin", "super_admin"],
  [CAPABILITIES.BILLING]: ["client", "accountant", "supervisor", "admin", "super_admin"],
  [CAPABILITIES.MAINTENANCE]: ["maintenance", "supervisor", "admin", "super_admin"],
};

module.exports = {
  CAPABILITIES,
  CAPABILITY_ROLES,
};
