/**
 * ============================================
 * 🔔 Notification Context - نظام إدهام
 * Edham Logistics - Real-time Notifications
 * ============================================
 */

import React, { createContext, useContext, useState, useEffect } from "react";
import { io } from "socket.io-client";
import logger from "../utils/logger";

const API_URL = process.env.REACT_APP_SOCKET_URL || process.env.REACT_APP_API_URL?.replace('/api/v1', '') || "http://localhost:5000";

const NotificationContext = createContext(null);

export const NotificationProvider = ({ children }) => {
  const [socket, setSocket] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) return;

    const newSocket = io(API_URL, {
      auth: { token },
      transports: ["websocket"],
    });

    setSocket(newSocket);

    newSocket.on("connect", () => {
      setIsConnected(true);
      logger.success("Connected to notification server");
    });

    newSocket.on("disconnect", () => {
      setIsConnected(false);
    });

    newSocket.on("notification", (notification) => {
      setNotifications((prev) => [
        { ...notification, isRead: false, receivedAt: new Date() },
        ...prev,
      ]);
      setUnreadCount((prev) => prev + 1);

      // Browser notification
      if (Notification.permission === "granted") {
        new Notification(notification.title, {
          body: notification.message,
          icon: "/edham-logo.png",
        });
      }
    });

    // Request permission
    if (Notification.permission === "default") {
      Notification.requestPermission();
    }

    return () => {
      newSocket.disconnect();
    };
  }, []);

  const markAsRead = (notificationId) => {
    setNotifications((prev) =>
      prev.map((notif) =>
        notif.id === notificationId ? { ...notif, isRead: true } : notif
      )
    );
    setUnreadCount((prev) => Math.max(0, prev - 1));
  };

  const markAllAsRead = () => {
    setNotifications((prev) => prev.map((notif) => ({ ...notif, isRead: true })));
    setUnreadCount(0);
  };

  return (
    <NotificationContext.Provider
      value={{
        socket,
        notifications,
        unreadCount,
        isConnected,
        markAsRead,
        markAllAsRead,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
};

export const useNotification = () => useContext(NotificationContext);
