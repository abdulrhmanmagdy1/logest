import React from "react";
import { motion } from "framer-motion";
import {
  Package, Navigation, Truck, User, MapPin,
} from "lucide-react";
import StatusBadge from "./StatusBadge";

const ShipmentCard = ({ shipment, onClick }) => {
  const cargoTypeLabels = {
    refrigerated: "مبرد",
    dry: "جاف",
    liquid: "سائل",
    hazardous: "خطير",
    fragile: "قابل للكسر",
  };

  const priorityColors = {
    urgent: "border-red-700/50",
    high:   "border-orange-700/50",
    normal: "border-gray-700",
    low:    "border-blue-700/50",
  };

  return (
    <motion.div
      whileHover={{ y: -2 }}
      onClick={() => onClick?.(shipment)}
      className={`card cursor-pointer border-r-4
                  ${priorityColors[shipment.priority] || priorityColors.normal}`}
    >
      {/* Header */}
      <div className="flex items-start justify-between mb-3">
        <div>
          <span className="font-mono text-red-400 text-sm font-bold">
            {shipment.trackingNumber}
          </span>
          <div className="flex items-center gap-2 mt-1">
            <StatusBadge status={shipment.status} type="shipment" />
            {shipment.priority !== "normal" && (
              <span className={`badge text-xs ${
                shipment.priority === "urgent" ? "badge-danger" :
                shipment.priority === "high"   ? "badge-warning" : "badge-gray"
              }`}>
                {{urgent: "عاجل", high: "أولوية عالية", low: "منخفض"}[shipment.priority]}
              </span>
            )}
          </div>
        </div>
        <span className="text-xs text-gray-500">
          {new Date(shipment.createdAt).toLocaleDateString("ar-EG")}
        </span>
      </div>

      {/* Route */}
      <div className="flex items-center gap-2 mb-3">
        <div className="flex items-center gap-1.5 flex-1 min-w-0">
          <div className="w-2 h-2 bg-green-400 rounded-full flex-shrink-0" />
          <span className="text-sm text-white truncate">
            {shipment.pickup?.city || shipment.pickup?.address || "—"}
          </span>
        </div>
        <Navigation className="w-3 h-3 text-gray-600 flex-shrink-0 rotate-90" />
        <div className="flex items-center gap-1.5 flex-1 min-w-0">
          <div className="w-2 h-2 bg-red-400 rounded-full flex-shrink-0" />
          <span className="text-sm text-white truncate">
            {shipment.delivery?.city || shipment.delivery?.address || "—"}
          </span>
        </div>
      </div>

      {/* Details */}
      <div className="grid grid-cols-2 gap-2 text-xs">
        <div className="flex items-center gap-1.5 text-gray-400">
          <Package className="w-3 h-3" />
          <span>{cargoTypeLabels[shipment.cargoType] || shipment.cargoType}</span>
        </div>
        <div className="flex items-center gap-1.5 text-gray-400">
          <span>{shipment.weight?.toLocaleString()} {shipment.weightUnit || "كجم"}</span>
        </div>
      </div>

      {/* Driver & Truck */}
      {(shipment.assignedDriver || shipment.assignedTruck) && (
        <div className="mt-3 pt-3 border-t border-gray-700 flex items-center gap-3 text-xs">
          {shipment.assignedDriver && (
            <div className="flex items-center gap-1.5 text-gray-400">
              <div className="w-5 h-5 bg-red-600/20 rounded-full flex items-center justify-center">
                <span className="text-red-400 font-bold text-xs">
                  {shipment.assignedDriver.name?.charAt(0)}
                </span>
              </div>
              <span className="text-gray-300">{shipment.assignedDriver.name}</span>
            </div>
          )}
          {shipment.assignedTruck && (
            <div className="flex items-center gap-1.5 text-gray-400">
              <Truck className="w-3 h-3" />
              <span>{shipment.assignedTruck.plateNumber}</span>
            </div>
          )}
        </div>
      )}
    </motion.div>
  );
};

export default ShipmentCard;
