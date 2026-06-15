import React from "react";
import { MapPin } from "lucide-react";
import { getCitiesOptions, getCityName } from "../data/cities";

const CitySelect = ({ value, onChange, label, placeholder = "اختر المدينة", required = false }) => {
  const cities = getCitiesOptions();

  return (
    <div className="input-group">
      {label && (
        <label className="input-label">
          {label}
          {required && <span className="text-red-400 mr-1">*</span>}
        </label>
      )}
      <div className="relative">
        <MapPin className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500" />
        <select
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="input-field pr-10"
          required={required}
        >
          <option value="">{placeholder}</option>
          {cities.map((city) => (
            <option key={city.value} value={city.value}>
              {city.label}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
};

export default CitySelect;
