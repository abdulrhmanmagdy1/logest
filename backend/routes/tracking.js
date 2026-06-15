/**
 * ============================================
 * 📍 Tracking Routes - نظام إدهام
 * Real-time tracking endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const { protect } = require('../middleware/auth');
const Shipment = require('../models/Shipment');
const Truck = require('../models/Truck');
const logger = require('../utils/logger');
const { io } = require('../server');

// Validation helper
const handleValidationErrors = (req, res, next) => {
  const errors = require('express-validator').validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// @route   GET /api/v1/tracking/public/:trackingNumber
// @desc    Get public tracking info (no auth required)
// @access  Public
router.get('/public/:trackingNumber', async (req, res) => {
  try {
    const shipment = await Shipment.findOne({ 
      trackingNumber: req.params.trackingNumber.toUpperCase() 
    })
      .select('trackingNumber status statusHistory pickup delivery route estimatedDelivery')
      .populate('driver', 'firstName lastName driverInfo.rating');

    if (!shipment) {
      return res.status(404).json({ success: false, message: 'Tracking number not found' });
    }

    res.json({
      success: true,
      data: {
        trackingNumber: shipment.trackingNumber,
        status: shipment.status,
        statusHistory: shipment.statusHistory,
        pickup: shipment.pickup,
        delivery: shipment.delivery,
        route: shipment.route,
        driver: shipment.driver ? {
          name: `${shipment.driver.firstName} ${shipment.driver.lastName}`,
          rating: shipment.driver.driverInfo?.rating
        } : null,
        estimatedDelivery: shipment.estimatedDelivery
      }
    });
  } catch (error) {
    logger.error('Public tracking error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/tracking/location
// @desc    Update driver location
// @access  Private (Driver)
router.post('/location', protect, [
  body('shipmentId').optional().isMongoId(),
  body('lat').isFloat({ min: -90, max: 90 }),
  body('lng').isFloat({ min: -180, max: 180 })
], handleValidationErrors, async (req, res) => {
  try {
    if (req.user.role !== 'driver') {
      return res.status(403).json({ success: false, message: 'Only drivers can update location' });
    }

    const { shipmentId, lat, lng } = req.body;

    // Update driver's current location
    await User.findByIdAndUpdate(req.user.id, {
      'driverInfo.currentLocation': {
        lat,
        lng,
        updatedAt: new Date()
      }
    });

    // Update truck location if assigned
    const truck = await Truck.findOne({ assignedDriver: req.user.id });
    if (truck) {
      truck.currentLocation = { lat, lng, updatedAt: new Date() };
      await truck.save();
    }

    // Emit location update
    if (shipmentId) {
      const shipment = await Shipment.findById(shipmentId);
      if (shipment && shipment.client) {
        io.to(`shipment:${shipmentId}`).emit('location_update', {
          driverId: req.user.id,
          shipmentId,
          location: { lat, lng },
          timestamp: new Date().toISOString()
        });

        // Add tracking update to shipment
        shipment.trackingUpdates.push({
          timestamp: new Date(),
          location: { lat, lng },
          message: 'Driver location updated'
        });
        await shipment.save();
      }
    }

    res.json({ success: true, message: 'Location updated' });
  } catch (error) {
    logger.error('Update location error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/tracking/live/:shipmentId
// @desc    Subscribe to live tracking
// @access  Private
router.get('/live/:shipmentId', protect, async (req, res) => {
  try {
    const shipment = await Shipment.findById(req.params.shipmentId)
      .populate('driver', 'firstName lastName driverInfo.currentLocation')
      .populate('truck', 'currentLocation');

    if (!shipment) {
      return res.status(404).json({ success: false, message: 'Shipment not found' });
    }

    // Check authorization
    const isAuthorized = 
      req.user.role === 'admin' || req.user.role === 'supervisor' ||
      shipment.client.toString() === req.user.id ||
      shipment.driver?._id.toString() === req.user.id;

    if (!isAuthorized) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    res.json({
      success: true,
      data: {
        shipmentId: shipment._id,
        trackingNumber: shipment.trackingNumber,
        status: shipment.status,
        driverLocation: shipment.driver?.driverInfo?.currentLocation,
        truckLocation: shipment.truck?.currentLocation,
        lastUpdate: shipment.trackingUpdates[shipment.trackingUpdates.length - 1]
      }
    });
  } catch (error) {
    logger.error('Get live tracking error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/tracking/fleet
// @desc    Get all active fleet locations
// @access  Private (Admin, Supervisor)
router.get('/fleet', protect, async (req, res) => {
  try {
    if (!['admin', 'supervisor'].includes(req.user.role)) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    const trucks = await Truck.find({
      status: { $in: ['active', 'on_trip'] },
      'currentLocation.lat': { $exists: true }
    })
      .populate('assignedDriver', 'firstName lastName phone driverInfo.rating')
      .populate('currentShipment', 'trackingNumber status pickup delivery');

    const fleetData = trucks.map(truck => ({
      truckId: truck._id,
      plateNumber: truck.plateNumber,
      type: truck.type,
      location: truck.currentLocation,
      driver: truck.assignedDriver ? {
        id: truck.assignedDriver._id,
        name: `${truck.assignedDriver.firstName} ${truck.assignedDriver.lastName}`,
        phone: truck.assignedDriver.phone,
        rating: truck.assignedDriver.driverInfo?.rating
      } : null,
      shipment: truck.currentShipment ? {
        id: truck.currentShipment._id,
        trackingNumber: truck.currentShipment.trackingNumber,
        status: truck.currentShipment.status,
        pickup: truck.currentShipment.pickup,
        delivery: truck.currentShipment.delivery
      } : null
    }));

    res.json({
      success: true,
      count: fleetData.length,
      data: fleetData
    });
  } catch (error) {
    logger.error('Get fleet tracking error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/tracking/route
// @desc    Calculate route
// @access  Private
router.post('/route', protect, [
  body('origin.lat').isFloat(),
  body('origin.lng').isFloat(),
  body('destination.lat').isFloat(),
  body('destination.lng').isFloat()
], handleValidationErrors, async (req, res) => {
  try {
    const { origin, destination, waypoints } = req.body;

    // Calculate distance using Haversine formula
    const R = 6371; // Earth's radius in km
    const dLat = (destination.lat - origin.lat) * Math.PI / 180;
    const dLng = (destination.lng - origin.lng) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(origin.lat * Math.PI / 180) * Math.cos(destination.lat * Math.PI / 180) *
              Math.sin(dLng/2) * Math.sin(dLng/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    const distance = R * c;

    // Estimate duration (assuming average 60 km/h for trucks)
    const duration = Math.round((distance / 60) * 60); // in minutes

    // Generate simple route (in production, use Google Maps or similar)
    const route = {
      distance: Math.round(distance * 10) / 10,
      duration,
      origin,
      destination,
      waypoints: waypoints || [],
      polyline: null // Would be generated by mapping service
    };

    res.json({
      success: true,
      data: route
    });
  } catch (error) {
    logger.error('Calculate route error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
