//
/**
 * ============================================
 * 🚛 Fleet Management Routes - إدارة الأسطول
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Vehicle, Trip, Depot } = require('../models/Fleet');
const logger = require('../utils/logger');

// @route   GET /api/v1/fleet/vehicles
// @desc    Get all vehicles
// @access  Private
router.get('/vehicles', protect, async (req, res) => {
  try {
    const { status, type, category } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;
    if (category) query.category = category;

    const vehicles = await Vehicle.find(query)
      .populate('assignment.driver', 'firstName lastName phone')
      .populate('assignment.depot', 'name code')
      .sort({ vehicleNumber: 1 });

    res.json({
      success: true,
      count: vehicles.length,
      data: vehicles
    });

  } catch (error) {
    logger.error('Get vehicles error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/fleet/vehicles
// @desc    Add vehicle
// @access  Private (Fleet Manager, Admin)
router.post('/vehicles', protect, authorize(['admin', 'supervisor', 'fleet_manager']), async (req, res) => {
  try {
    const vehicleNumber = `VEH-${Date.now()}`;
    
    const vehicle = await Vehicle.create({
      ...req.body,
      vehicleNumber,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: vehicle
    });

  } catch (error) {
    logger.error('Create vehicle error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/fleet/vehicles/:id
// @desc    Get vehicle details
// @access  Private
router.get('/vehicles/:id', protect, async (req, res) => {
  try {
    const vehicle = await Vehicle.findById(req.params.id)
      .populate('assignment.driver', 'firstName lastName phone licenseNumber')
      .populate('assignment.depot', 'name location');

    if (!vehicle) {
      return res.status(404).json({ success: false, message: 'Vehicle not found' });
    }

    // Get recent trips
    const recentTrips = await Trip.find({ vehicle: vehicle._id })
      .sort({ 'schedule.actualStart': -1 })
      .limit(5);

    res.json({
      success: true,
      data: {
        ...vehicle.toObject(),
        recentTrips
      }
    });

  } catch (error) {
    logger.error('Get vehicle error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/fleet/vehicles/:id/assign
// @desc    Assign vehicle to driver
// @access  Private (Fleet Manager, Admin)
router.put('/vehicles/:id/assign', protect, authorize(['admin', 'supervisor', 'fleet_manager']), async (req, res) => {
  try {
    const { driverId, depotId, route } = req.body;

    const vehicle = await Vehicle.findByIdAndUpdate(
      req.params.id,
      {
        assignment: {
          driver: driverId,
          depot: depotId,
          route,
          assignedSince: new Date()
        },
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!vehicle) {
      return res.status(404).json({ success: false, message: 'Vehicle not found' });
    }

    res.json({
      success: true,
      message: 'Vehicle assigned successfully',
      data: vehicle
    });

  } catch (error) {
    logger.error('Assign vehicle error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/fleet/vehicles/:id/odometer
// @desc    Update odometer reading
// @access  Private (Driver, Fleet Manager)
router.put('/vehicles/:id/odometer', protect, async (req, res) => {
  try {
    const { reading } = req.body;

    const vehicle = await Vehicle.findById(req.params.id);
    if (!vehicle) {
      return res.status(404).json({ success: false, message: 'Vehicle not found' });
    }

    vehicle.odometer.current = reading;
    vehicle.odometer.lastUpdated = new Date();
    await vehicle.save();

    res.json({
      success: true,
      message: 'Odometer updated',
      data: vehicle.odometer
    });

  } catch (error) {
    logger.error('Update odometer error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/fleet/vehicles/:id/fuel
// @desc    Record fuel refill
// @access  Private (Driver, Fleet Manager)
router.put('/vehicles/:id/fuel', protect, async (req, res) => {
  try {
    const { amount, cost, location, odometer } = req.body;

    const vehicle = await Vehicle.findById(req.params.id);
    if (!vehicle) {
      return res.status(404).json({ success: false, message: 'Vehicle not found' });
    }

    vehicle.fuel.lastRefuel = {
      date: new Date(),
      amount,
      cost,
      location,
      odometer
    };
    vehicle.fuel.currentLevel = vehicle.fuel.tankCapacity;

    // Update costs
    vehicle.costs.totalFuel += cost;
    await vehicle.save();

    res.json({
      success: true,
      message: 'Fuel recorded',
      data: vehicle.fuel.lastRefuel
    });

  } catch (error) {
    logger.error('Record fuel error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Trips
// @route   GET /api/v1/fleet/trips
// @desc    Get trips
// @access  Private
router.get('/trips', protect, async (req, res) => {
  try {
    const { vehicle, driver, status, from, to } = req.query;
    
    let query = { company: req.user.company };
    if (vehicle) query.vehicle = vehicle;
    if (driver) query.driver = driver;
    if (status) query.status = status;
    if (from && to) {
      query['schedule.plannedStart'] = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const trips = await Trip.find(query)
      .populate('vehicle', 'vehicleNumber make model')
      .populate('driver', 'firstName lastName')
      .populate('shipments', 'trackingNumber status')
      .sort({ 'schedule.plannedStart': -1 });

    res.json({
      success: true,
      count: trips.length,
      data: trips
    });

  } catch (error) {
    logger.error('Get trips error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/fleet/trips
// @desc    Create trip
// @access  Private (Dispatcher, Fleet Manager)
router.post('/trips', protect, authorize(['admin', 'supervisor', 'fleet_manager', 'dispatcher']), async (req, res) => {
  try {
    const tripNumber = `TRIP-${Date.now()}`;
    
    const trip = await Trip.create({
      ...req.body,
      tripNumber,
      company: req.user.company,
      status: 'planned'
    });

    res.status(201).json({
      success: true,
      data: trip
    });

  } catch (error) {
    logger.error('Create trip error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/fleet/trips/:id/start
// @desc    Start trip
// @access  Private (Driver)
router.put('/trips/:id/start', protect, async (req, res) => {
  try {
    const { odometer, location } = req.body;

    const trip = await Trip.findByIdAndUpdate(
      req.params.id,
      {
        status: 'in_progress',
        'schedule.actualStart': new Date(),
        $push: {
          events: {
            type: 'departure',
            timestamp: new Date(),
            location,
            odometer
          }
        }
      },
      { new: true }
    );

    if (!trip) {
      return res.status(404).json({ success: false, message: 'Trip not found' });
    }

    res.json({
      success: true,
      message: 'Trip started',
      data: trip
    });

  } catch (error) {
    logger.error('Start trip error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/fleet/trips/:id/complete
// @desc    Complete trip
// @access  Private (Driver, Fleet Manager)
router.put('/trips/:id/complete', protect, async (req, res) => {
  try {
    const { odometer, location, fuelConsumed } = req.body;

    const trip = await Trip.findById(req.params.id);
    if (!trip) {
      return res.status(404).json({ success: false, message: 'Trip not found' });
    }

    trip.status = 'completed';
    trip.schedule.actualEnd = new Date();
    trip.metrics.fuel.actual = fuelConsumed;
    trip.metrics.distance.actual = odometer - (trip.events[0]?.odometer || 0);
    trip.metrics.time.driving = (trip.schedule.actualEnd - trip.schedule.actualStart) / (1000 * 60 * 60);
    trip.metrics.efficiency = (trip.metrics.distance.planned / trip.metrics.distance.actual) * 100;

    trip.events.push({
      type: 'arrival',
      timestamp: new Date(),
      location,
      odometer
    });

    await trip.save();

    res.json({
      success: true,
      message: 'Trip completed',
      data: trip
    });

  } catch (error) {
    logger.error('Complete trip error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/fleet/dashboard
// @desc    Fleet dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const stats = await Promise.all([
      // Vehicle counts by status
      Vehicle.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),
      
      // Vehicle counts by type
      Vehicle.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$type', count: { $sum: 1 } } }
      ]),
      
      // Active trips
      Trip.countDocuments({
        company: req.user.company,
        status: 'in_progress'
      }),
      
      // Today's trips
      Trip.countDocuments({
        company: req.user.company,
        'schedule.plannedStart': {
          $gte: new Date(new Date().setHours(0, 0, 0, 0)),
          $lt: new Date(new Date().setHours(23, 59, 59, 999))
        }
      }),
      
      // Fleet utilization
      Vehicle.aggregate([
        { $match: { company: req.user.company._id, status: 'active' } },
        {
          $group: {
            _id: null,
            totalVehicles: { $sum: 1 },
            assignedVehicles: {
              $sum: { $cond: [{ $ifNull: ['$assignment.driver', false] }, 1, 0] }
            }
          }
        }
      ]),
      
      // Total fleet cost this month
      Vehicle.aggregate([
        { $match: { company: req.user.company._id } },
        {
          $group: {
            _id: null,
            totalFuel: { $sum: '$costs.totalFuel' },
            totalMaintenance: { $sum: '$costs.totalMaintenance' },
            totalInsurance: { $sum: '$costs.totalInsurance' }
          }
        }
      ])
    ]);

    res.json({
      success: true,
      data: {
        byStatus: stats[0],
        byType: stats[1],
        activeTrips: stats[2],
        todayTrips: stats[3],
        utilization: stats[4][0] || { totalVehicles: 0, assignedVehicles: 0 },
        costs: stats[5][0] || { totalFuel: 0, totalMaintenance: 0, totalInsurance: 0 }
      }
    });

  } catch (error) {
    logger.error('Fleet dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Live tracking
// @route   GET /api/v1/fleet/live-tracking
// @desc    Get live vehicle positions
// @access  Private
router.get('/live-tracking', protect, async (req, res) => {
  try {
    const vehicles = await Vehicle.find({
      company: req.user.company,
      status: 'active',
      'telematics.status': 'active'
    }).select('vehicleNumber telematics assignment');

    const trackingData = vehicles.map(v => ({
      vehicleId: v._id,
      vehicleNumber: v.vehicleNumber,
      driver: v.assignment?.driver,
      location: v.telematics?.location,
      speed: v.telematics?.speed,
      ignition: v.telematics?.ignition,
      lastPing: v.telematics?.lastPing
    }));

    res.json({
      success: true,
      count: trackingData.length,
      data: trackingData
    });

  } catch (error) {
    logger.error('Live tracking error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
