/**
 * ============================================
 * 🔽 Dropdown Component - نظام إدهام
 * Edham Logistics - Dropdown
 * ============================================
 */

import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';

export default function Dropdown({ trigger, items, align = 'right' }) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const alignClasses = {
    right: 'right-0',
    left: 'left-0',
    center: 'left-1/2 transform -translate-x-1/2'
  };

  return (
    <div className="relative" ref={dropdownRef}>
      <div onClick={() => setIsOpen(!isOpen)}>
        {trigger}
      </div>

      {isOpen && (
        <div className={`absolute top-full mt-2 bg-gray-700 rounded-lg shadow-xl z-50 min-w-48 ${alignClasses[align]}`}>
          {items.map((item, index) => (
            <button
              key={index}
              onClick={() => {
                item.onClick();
                setIsOpen(false);
              }}
              className="w-full text-right px-4 py-2 text-white hover:bg-gray-600 transition flex items-center gap-2 first:rounded-t-lg last:rounded-b-lg"
              disabled={item.disabled}
            >
              {item.icon && <span className="text-gray-400">{item.icon}</span>}
              <span>{item.label}</span>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
