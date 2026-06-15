//
/**
 * ============================================
 * 📹 WebRTC Service - خدمة المكالمات الصوتية والفيديو
 * Real-time Voice & Video Communication
 * ============================================
 */

const { Server } = require('socket.io');
const logger = require('../utils/logger');

class WebRTCService {
  constructor(io) {
    this.io = io;
    this.rooms = new Map(); // roomId -> { users, callType }
    this.activeCalls = new Map(); // userId -> { roomId, peerId }
    
    this.initializeSocketHandlers();
  }

  initializeSocketHandlers() {
    this.io.on('connection', (socket) => {
      logger.debug(`WebRTC client connected: ${socket.id}`);

      // Join call room
      socket.on('join-call', (data) => {
        this.handleJoinCall(socket, data);
      });

      // Leave call room
      socket.on('leave-call', (data) => {
        this.handleLeaveCall(socket, data);
      });

      // WebRTC signaling
      socket.on('offer', (data) => {
        this.handleOffer(socket, data);
      });

      socket.on('answer', (data) => {
        this.handleAnswer(socket, data);
      });

      socket.on('ice-candidate', (data) => {
        this.handleIceCandidate(socket, data);
      });

      // Call controls
      socket.on('mute-audio', (data) => {
        this.handleMuteAudio(socket, data);
      });

      socket.on('mute-video', (data) => {
        this.handleMuteVideo(socket, data);
      });

      socket.on('screen-share', (data) => {
        this.handleScreenShare(socket, data);
      });

      // Call recording
      socket.on('start-recording', (data) => {
        this.handleStartRecording(socket, data);
      });

      socket.on('stop-recording', (data) => {
        this.handleStopRecording(socket, data);
      });

      // Disconnection
      socket.on('disconnect', () => {
        this.handleDisconnect(socket);
      });
    });
  }

  /**
   * Handle join call
   */
  handleJoinCall(socket, { roomId, userId, userName, callType, isVideo }) {
    try {
      socket.join(roomId);
      socket.userId = userId;
      socket.roomId = roomId;

      // Initialize room if not exists
      if (!this.rooms.has(roomId)) {
        this.rooms.set(roomId, {
          id: roomId,
          users: new Map(),
          callType: callType || 'audio',
          isVideo: isVideo || false,
          createdAt: new Date(),
          recording: null
        });
      }

      const room = this.rooms.get(roomId);
      
      // Add user to room
      room.users.set(userId, {
        id: userId,
        name: userName,
        socketId: socket.id,
        isAudioMuted: false,
        isVideoMuted: !isVideo,
        isScreenSharing: false,
        joinedAt: new Date()
      });

      // Track active call for user
      this.activeCalls.set(userId, { roomId, socketId: socket.id });

      // Notify user they joined
      socket.emit('joined-call', {
        roomId,
        users: Array.from(room.users.values()).filter(u => u.id !== userId),
        callType: room.callType
      });

      // Notify others in room
      socket.to(roomId).emit('user-joined', {
        userId,
        userName,
        socketId: socket.id
      });

      logger.info(`User ${userName} (${userId}) joined call ${roomId}`);

    } catch (error) {
      logger.error('Join call error:', error);
      socket.emit('call-error', { message: 'Failed to join call' });
    }
  }

  /**
   * Handle leave call
   */
  handleLeaveCall(socket, { roomId, userId }) {
    try {
      this.leaveCall(socket, roomId, userId);
    } catch (error) {
      logger.error('Leave call error:', error);
    }
  }

  /**
   * Handle offer (WebRTC signaling)
   */
  handleOffer(socket, { roomId, offer, targetUserId }) {
    try {
      socket.to(roomId).emit('offer', {
        offer,
        fromUserId: socket.userId,
        fromSocketId: socket.id
      });
    } catch (error) {
      logger.error('Offer error:', error);
    }
  }

  /**
   * Handle answer (WebRTC signaling)
   */
  handleAnswer(socket, { roomId, answer, targetSocketId }) {
    try {
      socket.to(targetSocketId).emit('answer', {
        answer,
        fromUserId: socket.userId
      });
    } catch (error) {
      logger.error('Answer error:', error);
    }
  }

  /**
   * Handle ICE candidate (WebRTC signaling)
   */
  handleIceCandidate(socket, { roomId, candidate, targetSocketId }) {
    try {
      if (targetSocketId) {
        socket.to(targetSocketId).emit('ice-candidate', {
          candidate,
          fromUserId: socket.userId
        });
      } else {
        socket.to(roomId).emit('ice-candidate', {
          candidate,
          fromUserId: socket.userId
        });
      }
    } catch (error) {
      logger.error('ICE candidate error:', error);
    }
  }

  /**
   * Handle mute audio
   */
  handleMuteAudio(socket, { roomId, userId, isMuted }) {
    try {
      const room = this.rooms.get(roomId);
      if (room && room.users.has(userId)) {
        room.users.get(userId).isAudioMuted = isMuted;
        
        socket.to(roomId).emit('user-muted', {
          userId,
          type: 'audio',
          isMuted
        });
      }
    } catch (error) {
      logger.error('Mute audio error:', error);
    }
  }

  /**
   * Handle mute video
   */
  handleMuteVideo(socket, { roomId, userId, isMuted }) {
    try {
      const room = this.rooms.get(roomId);
      if (room && room.users.has(userId)) {
        room.users.get(userId).isVideoMuted = isMuted;
        
        socket.to(roomId).emit('user-muted', {
          userId,
          type: 'video',
          isMuted
        });
      }
    } catch (error) {
      logger.error('Mute video error:', error);
    }
  }

  /**
   * Handle screen share
   */
  handleScreenShare(socket, { roomId, userId, isSharing }) {
    try {
      const room = this.rooms.get(roomId);
      if (room && room.users.has(userId)) {
        room.users.get(userId).isScreenSharing = isSharing;
        
        socket.to(roomId).emit('screen-share-changed', {
          userId,
          isSharing
        });
      }
    } catch (error) {
      logger.error('Screen share error:', error);
    }
  }

  /**
   * Handle start recording
   */
  async handleStartRecording(socket, { roomId, userId }) {
    try {
      const room = this.rooms.get(roomId);
      if (!room) return;

      // Initialize recording
      room.recording = {
        startedAt: new Date(),
        startedBy: userId,
        participants: Array.from(room.users.keys())
      };

      // Notify all participants
      this.io.to(roomId).emit('recording-started', {
        startedBy: userId,
        timestamp: room.recording.startedAt
      });

      logger.info(`Recording started for room ${roomId}`);

    } catch (error) {
      logger.error('Start recording error:', error);
    }
  }

  /**
   * Handle stop recording
   */
  async handleStopRecording(socket, { roomId, userId }) {
    try {
      const room = this.rooms.get(roomId);
      if (!room || !room.recording) return;

      const recordingData = {
        ...room.recording,
        endedAt: new Date(),
        endedBy: userId,
        duration: new Date() - room.recording.startedAt
      };

      // Save recording metadata
      await this.saveRecordingMetadata(roomId, recordingData);

      // Clear recording
      room.recording = null;

      // Notify all participants
      this.io.to(roomId).emit('recording-stopped', {
        stoppedBy: userId,
        duration: recordingData.duration
      });

      logger.info(`Recording stopped for room ${roomId}`);

    } catch (error) {
      logger.error('Stop recording error:', error);
    }
  }

  /**
   * Handle disconnect
   */
  handleDisconnect(socket) {
    try {
      if (socket.roomId && socket.userId) {
        this.leaveCall(socket, socket.roomId, socket.userId);
      }
      
      logger.debug(`WebRTC client disconnected: ${socket.id}`);
    } catch (error) {
      logger.error('Disconnect error:', error);
    }
  }

  /**
   * Leave call helper
   */
  leaveCall(socket, roomId, userId) {
    const room = this.rooms.get(roomId);
    
    if (room) {
      // Remove user from room
      room.users.delete(userId);
      
      // Notify others
      socket.to(roomId).emit('user-left', {
        userId,
        socketId: socket.id
      });

      // Clean up room if empty
      if (room.users.size === 0) {
        // If recording was active, stop it
        if (room.recording) {
          this.saveRecordingMetadata(roomId, {
            ...room.recording,
            endedAt: new Date(),
            autoStopped: true
          });
        }
        
        this.rooms.delete(roomId);
        logger.info(`Room ${roomId} closed (no users)`);
      }
    }

    // Remove from active calls
    this.activeCalls.delete(userId);
    
    // Leave socket room
    socket.leave(roomId);
    
    logger.info(`User ${userId} left call ${roomId}`);
  }

  /**
   * Save recording metadata
   */
  async saveRecordingMetadata(roomId, recordingData) {
    try {
      const CallRecording = require('../models/CallRecording');
      
      await CallRecording.create({
        roomId,
        ...recordingData,
        createdAt: new Date()
      });

    } catch (error) {
      logger.error('Save recording metadata error:', error);
    }
  }

  /**
   * Initiate call (for server-initiated calls)
   */
  async initiateCall({ callerId, calleeId, callType = 'audio' }) {
    try {
      // Check if callee is online
      const calleeSocket = this.getSocketByUserId(calleeId);
      
      if (!calleeSocket) {
        return {
          success: false,
          message: 'User is offline'
        };
      }

      // Generate room ID
      const roomId = `call_${Date.now()}_${callerId}_${calleeId}`;

      // Notify callee
      calleeSocket.emit('incoming-call', {
        roomId,
        callerId,
        callType,
        timestamp: new Date()
      });

      return {
        success: true,
        roomId,
        message: 'Call initiated'
      };

    } catch (error) {
      logger.error('Initiate call error:', error);
      return {
        success: false,
        message: 'Failed to initiate call'
      };
    }
  }

  /**
   * Get socket by user ID
   */
  getSocketByUserId(userId) {
    const activeCall = this.activeCalls.get(userId);
    if (activeCall) {
      return this.io.sockets.sockets.get(activeCall.socketId);
    }
    return null;
  }

  /**
   * Get active rooms
   */
  getActiveRooms() {
    return Array.from(this.rooms.values()).map(room => ({
      id: room.id,
      userCount: room.users.size,
      callType: room.callType,
      isRecording: !!room.recording,
      createdAt: room.createdAt,
      users: Array.from(room.users.values()).map(u => ({
        id: u.id,
        name: u.name,
        isAudioMuted: u.isAudioMuted,
        isVideoMuted: u.isVideoMuted,
        isScreenSharing: u.isScreenSharing
      }))
    }));
  }

  /**
   * Get user call status
   */
  getUserCallStatus(userId) {
    const activeCall = this.activeCalls.get(userId);
    if (!activeCall) {
      return { inCall: false };
    }

    const room = this.rooms.get(activeCall.roomId);
    return {
      inCall: true,
      roomId: activeCall.roomId,
      callType: room?.callType || 'unknown',
      participants: room ? Array.from(room.users.values()) : []
    };
  }
}

module.exports = WebRTCService;
