import React from "react";
import { motion } from "framer-motion";
import {
  Truck, User, Thermometer, AlertTriangle,
  CheckCircle, Clock, Gauge,
} from "lucide-react";
import StatusBadge from "./StatusBadge";

const TruckCard = ({ truck, onClick, showDriver = true }) => {
  const statusColors = {
    active:         "border-green-700/50",
    maintenance:    "border-yellow-700/50",
    idle:           "border-gray-700",
    out_of_service: "border-red-700/50",
  };

  const maintenanceWarning = truck.maintenanceStatus === "expired" ||
                             truck.maintenanceStatus === "due_soon";

  return (
    <motion.div
      whileHover={{ y: -2, scale: 1.01 }}
      onClick={() => onClick?.(truck)}
      className={`card cursor-pointer border ${
        statusColors[truck.status] || statusColors.idle
      } transition-all`}
    >
      {/* Header */}
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-2">
          <div className="w-10 h-10 bg-gray-900 rounded-xl flex items-center justify-center">
            <Truck className="w-5 h-5 text-red-400" />
          </div>
          <div>
            <p className="font-bold text-white text-sm">{truck.plateNumber}</p>
            <p className="text-gray-500 text-xs">{truck.brand} {truck.model}</p>
          </div>
        </div>
        <div className="flex flex-col items-end gap-1">
          <StatusBadge status={truck.status} type="truck" />
          {maintenanceWarning && (
            <div className="flex items-center gap-1 text-yellow-400 text-xs">
              <AlertTriangle className="w-3 h-3" />
              <span>يحتاج تجديد</span>
            </div>
          )}
        </div>
      </div>

      {/* Info Grid */}
      <div className="grid grid-cols-2 gap-2 mb-3">
        <div className="p-2 bg-gray-900 rounded-lg">
          <p className="text-gray-500 text-xs mb-0.5">الطاقة</p>
          <p className="text-white text-sm font-medium">
            {truck.capacity} {truck.capacityUnit}
          </p>
        </div>
        <div className="p-2 bg-gray-900 rounded-lg">
          <p className="text-gray-500 text-xs mb-0.5">الكيلومترات</p>
          <p className="text-white text-sm font-medium">
            {truck.currentKm?.toLocaleString()} كم
          </p>
        </div>
      </div>

      {/* نوع الشاحنة */}
      <div className="flex items-center gap-2 text-xs text-gray-400 mb-2">
        <Gauge className="w-3 h-3" />
        <span>{{
          refrigerated: "مبردة",
          standard:     "عادية",
          flatbed:      "مسطحة",
          tanker:       "صهريج",
        }[truck.truckType] || truck.truckType}</span>
        {truck.year && (
          <span className="text-gray-600">· {truck.year}</span>
        )}
      </div>

      {/* وحدة التبريد */}
      {truck.truckType === "refrigerated" && truck.refrigerationUnit?.brand && (
        <div className="flex items-center gap-1.5 text-xs text-blue-400 mb-2">
          <Thermometer className="w-3 h-3" />
          <span>{truck.refrigerationUnit.brand} | {truck.refrigerationUnit.minTemp}°C → {truck.refrigerationUnit.maxTemp}°C</span>
        </div>
      )}

      {/* السائق الحالي */}
      {showDriver && truck.currentDriver && (
        <div className="mt-2 pt-2 border-t border-gray-700 flex items-center gap-2">
          <div className="w-6 h-6 bg-red-600/20 rounded-full flex items-center
                          justify-center flex-shrink-0">
            <span className="text-red-400 text-xs font-bold">
              {truck.currentDriver.name?.charAt(0)}
            </span>
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-gray-300 text-xs truncate">{truck.currentDriver.name}</p>
          </div>
          <User className="w-3 h-3 text-gray-600" />
        </div>
      )}

      {/* الكفرات تحتاج تبديل */}
      {truck.tires?.some((t) => t.treadDepth < 3.0) && (
        <div className="mt-2 flex items-center gap-1.5 text-yellow-400 text-xs
                        bg-yellow-900/20 rounded-lg px-2 py-1">
          <AlertTriangle className="w-3 h-3 flex-shrink-0" />
          <span>
            {truck.tires.filter((t) => t.treadDepth < 3.0).length} كفر تحتاج استبدال
          </span>
        </div>
      )}
    </motion.div>
  );
};

export default TruckCard;
