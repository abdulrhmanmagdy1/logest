/**
 * ============================================
 * 📍 Location Handler - نظام إدهام
 * Edham Logistics - Real-time Location Updates
 * ============================================
 */

const Trip = require('../../models/Trip');
const Shipment = require('../../models/Shipment');
const Location = require('../../models/Location');
const logger = require('../../utils/logger');

class LocationHandler {
  /**
   * معالج تحديث الموقع
   */
  static async handleLocationUpdate(socket, io, data) {
    try {
      const { driverId, latitude, longitude, address, shipmentId, speed } = data;

      // Save location to database
      const location = new Location({
        driver: driverId,
        shipment: shipmentId,
        latitude,
        longitude,
        address: address || 'Unknown',
        speed
      });

      await location.save();

      // Update shipment real-time location
      if (shipmentId) {
        await Shipment.findByIdAndUpdate(shipmentId, {
          currentLocation: { latitude, longitude },
          lastLocationUpdate: new Date()
        });
      }

      // Broadcast to all connected clients
      io.emit('locationUpdated', {
        driverId,
        shipmentId,
        latitude,
        longitude,
        address,
        speed,
        timestamp: new Date()
      });

      logger.success('Location updated', { driverId, shipmentId });
    } catch (error) {
      logger.error('Location handler error', error);
      socket.emit('error', { message: 'خطأ في تحديث الموقع' });
    }
  }

  /**
   * معالج تحديث حالة الرحلة
   */
  static async handleTripStatusUpdate(socket, io, data) {
    try {
      const { tripId, status } = data;

      const trip = await Trip.findByIdAndUpdate(tripId, {
        status
      }, { new: true }).populate('employee', 'name');

      if (!trip) {
        return socket.emit('error', { message: 'الرحلة غير موجودة' });
      }

      // Broadcast to supervisors
      io.emit('tripStatusChanged', {
        tripId,
        status,
        tripNumber: trip.tripNumber,
        employeeName: trip.employee?.name,
        timestamp: new Date()
      });

      logger.success('Trip status updated', { tripNumber: trip.tripNumber, status });
    } catch (error) {
      logger.error('Trip status handler error', error);
      socket.emit('error', { message: 'خطأ في تحديث حالة الرحلة' });
    }
  }

  /**
   * معالج تحديثات الشحنة
   */
  static async handleShipmentUpdate(socket, io, data) {
    try {
      const { shipmentId, status, location } = data;

      const shipment = await Shipment.findByIdAndUpdate(shipmentId, {
        status,
        currentLocation: location,
        lastLocationUpdate: new Date()
      }, { new: true }).populate(['client', 'driver', 'truck']);

      if (!shipment) {
        return socket.emit('error', { message: 'الشحنة غير موجودة' });
      }

      // Broadcast to relevant users
      io.emit('shipmentUpdated', {
        shipmentId,
        shipmentNumber: shipment.shipmentNumber,
        status,
        location,
        driver: shipment.driver?.name,
        timestamp: new Date()
      });

      logger.success('Shipment updated', { shipmentNumber: shipment.shipmentNumber });
    } catch (error) {
      logger.error('Shipment update handler error', error);
      socket.emit('error', { message: 'خطأ في تحديث الشحنة' });
    }
  }
}

module.exports = LocationHandler;
