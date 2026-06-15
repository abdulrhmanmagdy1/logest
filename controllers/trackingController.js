/**
 * ============================================
 * 📍 Tracking Controller - نظام إدهام
 * Edham Logistics - Driver Tracking Controller
 * ============================================
 */

const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');
const Location = require('../models/Location');
const Driver = require('../models/Driver');
const Shipment = require('../models/Shipment');
const TrackingSession = require('../models/TrackingSession');

class TrackingController {
  /**
   * Get driver current location
   */
  static async getCurrentLocation(req, res) {
    try {
      const driverId = req.user.id;

      const latestLocation = await Location.findOne({ 
        driverId,
        isActive: true 
      })
        .sort({ timestamp: -1 });

      if (!latestLocation) {
        return res.json({
          success: true,
          location: null,
          message: 'لا يوجد موقع محدد حالياً'
        });
      }

      res.json({
        success: true,
        location: {
          latitude: latestLocation.latitude,
          longitude: latestLocation.longitude,
          address: latestLocation.address,
          timestamp: latestLocation.timestamp,
          accuracy: latestLocation.accuracy,
          speed: latestLocation.speed,
          heading: latestLocation.heading
        }
      });
    } catch (error) {
      logger.error('Error getting current location:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Update driver location (automatic GPS)
   */
  static async updateLocation(req, res) {
    try {
      const driverId = req.user.id;
      const {
        latitude,
        longitude,
        accuracy,
        speed,
        heading,
        altitude,
        timestamp = new Date()
      } = req.body;

      // Validate coordinates
      if (!latitude || !longitude) {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'إحداثيات GPS مطلوبة'
        });
      }

      // Get address from coordinates (geocoding)
      let address = null;
      try {
        address = await this.geocodeCoordinates(latitude, longitude);
      } catch (geocodeError) {
        logger.warn('Geocoding failed:', geocodeError);
      }

      // Deactivate previous locations
      await Location.updateMany(
        { driverId, isActive: true },
        { isActive: false }
      );

      // Create new location record
      const location = new Location({
        driverId,
        latitude,
        longitude,
        address,
        accuracy,
        speed,
        heading,
        altitude,
        timestamp: new Date(timestamp),
        source: 'gps',
        isActive: true
      });

      await location.save();

      // Update driver's last known location
      await Driver.findByIdAndUpdate(driverId, {
        lastLocation: {
          latitude,
          longitude,
          address,
          timestamp: location.timestamp
        },
        lastLocationUpdate: new Date()
      });

      // Check if driver is in active tracking session
      const activeSession = await TrackingSession.findOne({
        driverId,
        status: 'active'
      });

      if (activeSession) {
        // Update session with new location
        activeSession.locations.push(location._id);
        activeSession.lastLocationUpdate = new Date();
        
        // Calculate distance traveled
        if (activeSession.locations.length > 1) {
          const distance = await this.calculateDistance(
            activeSession.locations[activeSession.locations.length - 2],
            location._id
          );
          activeSession.totalDistance += distance;
        }
        
        await activeSession.save();
      }

      // Emit real-time location update via WebSocket
      if (req.io) {
        req.io.emit('driver_location_update', {
          driverId,
          location: {
            latitude,
            longitude,
            address,
            timestamp: location.timestamp
          }
        });
      }

      res.json({
        success: true,
        message: 'تم تحديث الموقع بنجاح',
        location: {
          id: location._id,
          latitude,
          longitude,
          address,
          timestamp: location.timestamp
        }
      });

    } catch (error) {
      logger.error('Error updating location:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Add manual location entry
   */
  static async addManualLocation(req, res) {
    try {
      const driverId = req.user.id;
      const {
        latitude,
        longitude,
        address,
        description,
        timestamp = new Date()
      } = req.body;

      if (!latitude || !longitude) {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'إحداثيات الموقع مطلوبة'
        });
      }

      // Deactivate previous locations
      await Location.updateMany(
        { driverId, isActive: true },
        { isActive: false }
      );

      // Create manual location record
      const location = new Location({
        driverId,
        latitude,
        longitude,
        address: address || await this.geocodeCoordinates(latitude, longitude),
        description,
        timestamp: new Date(timestamp),
        source: 'manual',
        isActive: true
      });

      await location.save();

      // Update driver's last known location
      await Driver.findByIdAndUpdate(driverId, {
        lastLocation: {
          latitude,
          longitude,
          address: location.address,
          timestamp: location.timestamp
        },
        lastLocationUpdate: new Date()
      });

      logger.info('Manual location added', {
        driverId,
        latitude,
        longitude,
        address: location.address
      });

      res.json({
        success: true,
        message: 'تم إدخال الموقع يدوياً بنجاح',
        location: {
          id: location._id,
          latitude,
          longitude,
          address: location.address,
          description,
          timestamp: location.timestamp
        }
      });

    } catch (error) {
      logger.error('Error adding manual location:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get driver location history
   */
  static async getLocationHistory(req, res) {
    try {
      const { driverId } = req.params;
      const { 
        startDate, 
        endDate, 
        page = 1, 
        limit = 50 
      } = req.query;

      // Check permissions
      if (req.user.role !== 'admin' && req.user.role !== 'supervisor' && 
          req.user.id !== driverId) {
        return res.status(HTTP_STATUS.FORBIDDEN).json({
          success: false,
          message: 'غير مصرح بالوصول إلى سجل الموقع'
        });
      }

      const query = { driverId };
      if (startDate || endDate) {
        query.timestamp = {};
        if (startDate) query.timestamp.$gte = new Date(startDate);
        if (endDate) query.timestamp.$lte = new Date(endDate);
      }

      const skip = (page - 1) * limit;

      const [locations, total] = await Promise.all([
        Location.find(query)
          .sort({ timestamp: -1 })
          .skip(skip)
          .limit(limit),
        Location.countDocuments(query)
      ]);

      res.json({
        success: true,
        locations,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });

    } catch (error) {
      logger.error('Error getting location history:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get live tracking for shipment
   */
  static async getLiveTracking(req, res) {
    try {
      const { shipmentId } = req.params;

      const shipment = await Shipment.findById(shipmentId)
        .populate('assignedDriverId', 'name phone vehicleInfo');

      if (!shipment) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'الشحنة غير موجودة'
        });
      }

      // Check permissions
      if (req.user.role === 'client' && shipment.clientId.toString() !== req.user.id) {
        return res.status(HTTP_STATUS.FORBIDDEN).json({
          success: false,
          message: 'غير مصرح بالوصول إلى تتبع هذه الشحنة'
        });
      }

      let driverLocation = null;
      if (shipment.assignedDriverId) {
        driverLocation = await Location.findOne({
          driverId: shipment.assignedDriverId._id,
          isActive: true
        }).sort({ timestamp: -1 });
      }

      // Get tracking session if exists
      const trackingSession = await TrackingSession.findOne({
        shipmentId,
        status: 'active'
      }).populate('locations');

      res.json({
        success: true,
        shipment: {
          id: shipment._id,
          trackingNumber: shipment.trackingNumber,
          status: shipment.status,
          origin: shipment.origin,
          destination: shipment.destination,
          estimatedDelivery: shipment.estimatedDelivery
        },
        driver: shipment.assignedDriverId ? {
          id: shipment.assignedDriverId._id,
          name: shipment.assignedDriverId.name,
          phone: shipment.assignedDriverId.phone,
          vehicleInfo: shipment.assignedDriverId.vehicleInfo
        } : null,
        currentLocation: driverLocation ? {
          latitude: driverLocation.latitude,
          longitude: driverLocation.longitude,
          address: driverLocation.address,
          timestamp: driverLocation.timestamp,
          accuracy: driverLocation.accuracy
        } : null,
        trackingSession: trackingSession ? {
          id: trackingSession._id,
          startTime: trackingSession.startTime,
          totalDistance: trackingSession.totalDistance,
          estimatedArrival: trackingSession.estimatedArrival,
          locations: trackingSession.locations.slice(-10) // Last 10 locations
        } : null
      });

    } catch (error) {
      logger.error('Error getting live tracking:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get all active drivers locations (for supervisor)
   */
  static async getActiveDrivers(req, res) {
    try {
      const activeLocations = await Location.aggregate([
        { $match: { isActive: true } },
        {
          $lookup: {
            from: 'drivers',
            localField: 'driverId',
            foreignField: 'userId',
            as: 'driver'
          }
        },
        {
          $lookup: {
            from: 'users',
            localField: 'driverId',
            foreignField: '_id',
            as: 'user'
          }
        },
        { $unwind: '$driver' },
        { $unwind: '$user' },
        {
          $lookup: {
            from: 'shipments',
            localField: 'driver.currentShipmentId',
            foreignField: '_id',
            as: 'currentShipment'
          }
        },
        { $unwind: { path: '$currentShipment', preserveNullAndEmptyArrays: true } },
        {
          $project: {
            driverId: '$driverId',
            driverName: '$user.name',
            driverPhone: '$user.phone',
            vehicleInfo: '$driver.vehicleInfo',
            latitude: '$latitude',
            longitude: '$longitude',
            address: '$address',
            timestamp: '$timestamp',
            accuracy: '$accuracy',
            speed: '$speed',
            currentShipment: {
              id: '$currentShipment._id',
              trackingNumber: '$currentShipment.trackingNumber',
              status: '$currentShipment.status',
              origin: '$currentShipment.origin',
              destination: '$currentShipment.destination'
            }
          }
        }
      ]);

      res.json({
        success: true,
        drivers: activeLocations
      });

    } catch (error) {
      logger.error('Error getting active drivers:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Toggle tracking session
   */
  static async toggleTrackingSession(req, res) {
    try {
      const driverId = req.user.id;
      const { action, shipmentId } = req.body; // action: 'start' or 'stop'

      if (action === 'start') {
        // Check if there's already an active session
        const existingSession = await TrackingSession.findOne({
          driverId,
          status: 'active'
        });

        if (existingSession) {
          return res.status(HTTP_STATUS.BAD_REQUEST).json({
            success: false,
            message: 'يوجد جلسة تتبع نشطة بالفعل'
          });
        }

        // Create new tracking session
        const session = new TrackingSession({
          driverId,
          shipmentId,
          status: 'active',
          startTime: new Date()
        });

        await session.save();

        res.json({
          success: true,
          message: 'تم بدء جلسة التتبع',
          sessionId: session._id
        });

      } else if (action === 'stop') {
        const activeSession = await TrackingSession.findOne({
          driverId,
          status: 'active'
        });

        if (!activeSession) {
          return res.status(HTTP_STATUS.NOT_FOUND).json({
            success: false,
            message: 'لا توجد جلسة تتبع نشطة'
          });
        }

        activeSession.status = 'completed';
        activeSession.endTime = new Date();
        await activeSession.save();

        res.json({
          success: true,
          message: 'تم إنهاء جلسة التتبع',
          sessionId: activeSession._id
        });

      } else {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'الإجراء غير صحيح'
        });
      }

    } catch (error) {
      logger.error('Error toggling tracking session:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get tracking statistics
   */
  static async getStatistics(req, res) {
    try {
      const { startDate, endDate } = req.query;

      const matchQuery = {};
      if (startDate || endDate) {
        matchQuery.timestamp = {};
        if (startDate) matchQuery.timestamp.$gte = new Date(startDate);
        if (endDate) matchQuery.timestamp.$lte = new Date(endDate);
      }

      const [
        totalLocations,
        activeDrivers,
        totalSessions,
        averageAccuracy,
        dailyStats
      ] = await Promise.all([
        Location.countDocuments(matchQuery),
        Location.distinct('driverId', matchQuery).then(drivers => drivers.length),
        TrackingSession.countDocuments(matchQuery),
        Location.aggregate([
          { $match: matchQuery },
          { $group: { _id: null, avgAccuracy: { $avg: '$accuracy' } } }
        ]),
        Location.aggregate([
          { $match: matchQuery },
          {
            $group: {
              _id: {
                year: { $year: '$timestamp' },
                month: { $month: '$timestamp' },
                day: { $dayOfMonth: '$timestamp' }
              },
              count: { $sum: 1 },
              drivers: { $addToSet: '$driverId' }
            }
          },
          {
            $project: {
              date: '$_id',
              count: 1,
              uniqueDrivers: { $size: '$drivers' }
            }
          },
          { $sort: { '_id.year': 1, '_id.month': 1, '_id.day': 1 } }
        ])
      ]);

      res.json({
        success: true,
        statistics: {
          totalLocations,
          activeDrivers,
          totalSessions,
          averageAccuracy: averageAccuracy[0]?.avgAccuracy || 0,
          dailyStats
        }
      });

    } catch (error) {
      logger.error('Error getting tracking statistics:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get route history for driver
   */
  static async getRouteHistory(req, res) {
    try {
      const { driverId } = req.params;
      const { page = 1, limit = 20 } = req.query;

      // Check permissions
      if (req.user.role !== 'admin' && req.user.role !== 'supervisor' && 
          req.user.id !== driverId) {
        return res.status(HTTP_STATUS.FORBIDDEN).json({
          success: false,
          message: 'غير مصرح بالوصول إلى سجل المسارات'
        });
      }

      const skip = (page - 1) * limit;

      const [sessions, total] = await Promise.all([
        TrackingSession.find({ driverId })
          .populate('shipmentId', 'trackingNumber origin destination')
          .sort({ startTime: -1 })
          .skip(skip)
          .limit(limit),
        TrackingSession.countDocuments({ driverId })
      ]);

      res.json({
        success: true,
        sessions,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });

    } catch (error) {
      logger.error('Error getting route history:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Export tracking data
   */
  static async exportTrackingData(req, res) {
    try {
      const { format } = req.params;
      const { startDate, endDate, driverId } = req.query;

      const query = {};
      if (driverId) query.driverId = driverId;
      if (startDate || endDate) {
        query.timestamp = {};
        if (startDate) query.timestamp.$gte = new Date(startDate);
        if (endDate) query.timestamp.$lte = new Date(endDate);
      }

      const locations = await Location.find(query)
        .populate('driverId', 'name email')
        .sort({ timestamp: -1 });

      if (format === 'csv') {
        const csv = [
          'Driver Name,Email,Latitude,Longitude,Address,Timestamp,Source,Accuracy,Speed',
          ...locations.map(loc => 
            `"${loc.driverId?.name || 'N/A'}","${loc.driverId?.email || 'N/A'}",` +
            `${loc.latitude},${loc.longitude},"${loc.address || ''}",` +
            `${loc.timestamp},"${loc.source}",${loc.accuracy || 0},${loc.speed || 0}`
          )
        ].join('\n');

        res.setHeader('Content-Type', 'text/csv');
        res.setHeader('Content-Disposition', `attachment; filename="tracking-${Date.now()}.csv"`);
        res.send(csv);

      } else if (format === 'json') {
        res.setHeader('Content-Type', 'application/json');
        res.setHeader('Content-Disposition', `attachment; filename="tracking-${Date.now()}.json"`);
        res.json({ locations });

      } else {
        res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'تنسيق التصدير غير مدعوم'
        });
      }

    } catch (error) {
      logger.error('Error exporting tracking data:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Helper: Geocode coordinates to address
   */
  static async geocodeCoordinates(latitude, longitude) {
    // This would integrate with a geocoding service like Google Maps API
    // For now, return a formatted coordinate string
    return `${latitude.toFixed(6)}, ${longitude.toFixed(6)}`;
  }

  /**
   * Helper: Calculate distance between two locations
   */
  static async calculateDistance(location1Id, location2Id) {
    try {
      const [loc1, loc2] = await Promise.all([
        Location.findById(location1Id),
        Location.findById(location2Id)
      ]);

      if (!loc1 || !loc2) return 0;

      // Haversine formula for distance calculation
      const R = 6371; // Earth's radius in kilometers
      const dLat = (loc2.latitude - loc1.latitude) * Math.PI / 180;
      const dLon = (loc2.longitude - loc1.longitude) * Math.PI / 180;
      const a = 
        Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(loc1.latitude * Math.PI / 180) * Math.cos(loc2.latitude * Math.PI / 180) *
        Math.sin(dLon/2) * Math.sin(dLon/2);
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
      const distance = R * c;

      return distance;
    } catch (error) {
      logger.error('Error calculating distance:', error);
      return 0;
    }
  }
}

module.exports = TrackingController;
