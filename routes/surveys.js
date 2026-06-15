/**
 * ============================================
 * 📝 Surveys Routes - نظام إدهام
 * Edham Logistics - Driver Survey API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const SurveyController = require('../controllers/surveyController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Get all surveys
router.get('/', auth, SurveyController.getAll);

// Get survey statistics
router.get('/statistics', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), SurveyController.getStatistics);

// Create new survey (Driver only)
router.post('/', auth, SurveyController.create);

// Get surveys by driver
router.get('/driver/:driverId', auth, SurveyController.getByDriver);

// Get single survey
router.get('/:id', auth, SurveyController.getById);

// Delete survey
router.delete('/:id', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), SurveyController.delete);

module.exports = router;
