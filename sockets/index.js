/**
 * ============================================
 * 📡 Socket.IO Handlers - نظام إدهام
 * Edham Logistics - Real-time Socket Events
 * ============================================
 */

const LocationHandler = require('./handlers/locationHandler');
const logger = require('../utils/logger');

module.exports = (io) => {
  io.on('connection', (socket) => {
    logger.info('User connected', { socketId: socket.id });

    // Location Updates
    socket.on('updateLocation', (data) => {
      LocationHandler.handleLocationUpdate(socket, io, data);
    });

    // Trip Status Updates
    socket.on('updateTripStatus', (data) => {
      LocationHandler.handleTripStatusUpdate(socket, io, data);
    });

    // Shipment Updates
    socket.on('updateShipment', (data) => {
      LocationHandler.handleShipmentUpdate(socket, io, data);
    });

    // Notifications
    socket.on('sendNotification', (data) => {
      io.emit('notification', {
        ...data,
        timestamp: new Date()
      });
      logger.info('Notification sent', data);
    });

    // Join room by role
    socket.on('joinRoom', (room) => {
      socket.join(room);
      logger.info('User joined room', { socketId: socket.id, room });
    });

    // Leave room
    socket.on('leaveRoom', (room) => {
      socket.leave(room);
      logger.info('User left room', { socketId: socket.id, room });
    });

    // Disconnect
    socket.on('disconnect', () => {
      logger.info('User disconnected', { socketId: socket.id });
    });

    // Error handling
    socket.on('error', (error) => {
      logger.error('Socket error', error);
    });
  });
};