/**
 * ============================================
 * 🎮 Driver Simulator - نظام إدهام
 * Edham Logistics - Driver Location Simulator
 * ============================================
 */

const { io } = require("socket.io-client");
const logger = require("./utils/logger");

const socket = io("http://localhost:5000");

// مسار من الرياض إلى جدة - المملكة العربية السعودية
const route = [
  { lat: 24.7136, lng: 46.6753 },  // الرياض
  { lat: 24.5000, lng: 46.0000 },  // نقطة وسطى 1
  { lat: 24.2000, lng: 45.0000 },  // نقطة وسطى 2
  { lat: 23.8859, lng: 43.0000 },  // نقطة وسطى 3
  { lat: 23.5000, lng: 42.0000 },  // نقطة وسطى 4
  { lat: 23.0000, lng: 41.0000 },  // نقطة وسطى 5
  { lat: 22.5000, lng: 40.0000 },  // نقطة وسطى 6
  { lat: 21.5435, lng: 39.1728 }   // جدة
];

let currentIndex = 0;
let progress = 0;
const speed = 0.02; // سرعة الحركة

socket.on("connect", () => {
  logger.success("Driver simulator connected");
  socket.emit("joinShipment", { shipmentId: "S1" });
});

setInterval(() => {
  if (currentIndex >= route.length - 1) {
    logger.success("Route completed");
    currentIndex = 0;
    progress = 0;
  }

  const current = route[currentIndex];
  const next = route[currentIndex + 1];

  if (next) {
    // Interpolate between points
    const lat = current.lat + (next.lat - current.lat) * progress;
    const lng = current.lng + (next.lng - current.lng) * progress;

    socket.emit("driverLocation", {
      driverId: "D1",
      shipmentId: "S1",
      lat,
      lng
    });

    logger.info("Sending location", { lat: lat.toFixed(6), lng: lng.toFixed(6) });

    progress += speed;

    if (progress >= 1) {
      currentIndex++;
      progress = 0;
    }
  }
}, 2000);