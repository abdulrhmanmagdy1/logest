import { GoogleMap, LoadScript, Marker, Polyline } from "@react-google-maps/api";
import { useEffect, useState } from "react";
import { io } from "socket.io-client";

export default function LiveMap() {

  const [socket, setSocket] = useState(null);
  const [drivers, setDrivers] = useState({});
  const [paths, setPaths] = useState({});
  const [error, setError] = useState(null);

  const target = { lat: 30.05, lng: 31.24 };
  const MAX_PATH_POINTS = 50; // Limit path points to prevent memory issues

  const distance = (a, b) => {
    const R = 6371;
    const dLat = (b.lat - a.lat) * Math.PI / 180;
    const dLng = (b.lng - a.lng) * Math.PI / 180;

    const x =
      Math.sin(dLat / 2) ** 2 +
      Math.cos(a.lat * Math.PI / 180) *
      Math.cos(b.lat * Math.PI / 180) *
      Math.sin(dLng / 2) ** 2;

    return 2 * R * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));
  };

  // ================= SOCKET INIT =================
  useEffect(() => {
    const newSocket = io("http://localhost:5000");
    setSocket(newSocket);

    newSocket.on("connect", () => {
      console.log("✅ Connected to server");
      setError(null);
    });

    newSocket.on("connect_error", (err) => {
      console.error("❌ Connection error:", err);
      setError("فشل الاتصال بالخادم");
    });

    // Join shipment room
    newSocket.emit("joinShipment", { shipmentId: "S1" });

    newSocket.on("locationUpdate", (data) => {
      const { driverId, lat, lng } = data;

      setDrivers((prev) => ({
        ...prev,
        [driverId]: { lat, lng }
      }));

      setPaths((prev) => {
        const currentPath = prev[driverId] || [];
        const newPath = [...currentPath, { lat, lng }];

        // Limit path size
        if (newPath.length > MAX_PATH_POINTS) {
          newPath.shift();
        }

        return {
          ...prev,
          [driverId]: newPath
        };
      });

      // 🚨 arrival check
      const dist = distance({ lat, lng }, target);

      if (dist < 0.2) {
        alert(`🚚 Driver ${driverId} وصل`);
        new Audio("/notify.mp3").play().catch(() => console.log("Audio play failed"));
      }
    });

    return () => {
      newSocket.disconnect();
    };
  }, []);
  // ================= MAP =================
  return (
    <LoadScript googleMapsApiKey="YOUR_API_KEY">
      <GoogleMap
        mapContainerStyle={{ width: "100%", height: "600px" }}
        zoom={13}
        center={target}
      >

        {/* 🎯 Destination */}
        <Marker position={target} label="DEST" />

        {/* 🚚 Drivers */}
        {Object.entries(drivers).map(([id, pos]) => (
          <Marker
            key={id}
            position={pos}
            label={`🚚 ${id}`}
          />
        ))}

        {/* 🛣️ Routes */}
        {Object.entries(paths).map(([id, path]) => (
          <Polyline
            key={id}
            path={path}
            options={{
              strokeColor: "#0d9488",
              strokeWeight: 4
            }}
          />
        ))}

      </GoogleMap>
    </LoadScript>
  );
}