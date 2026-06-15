const express = require("express");
const router = express.Router();
const AnalyticsController = require("../controllers/analyticsController");
const { auth, authorizeCapability } = require("../middleware/auth");
const { CAPABILITIES } = require("../config/permissions");

// Enterprise dashboard facade, delegated to existing analytics controllers.
router.get(
  "/overview",
  auth,
  authorizeCapability(CAPABILITIES.DASHBOARD),
  AnalyticsController.getDashboardMetrics
);

router.get(
  "/kpis",
  auth,
  authorizeCapability(CAPABILITIES.DASHBOARD),
  AnalyticsController.getMonthlyReport
);

router.get(
  "/operations",
  auth,
  authorizeCapability(CAPABILITIES.DASHBOARD),
  AnalyticsController.getDriverPerformance
);

module.exports = router;
