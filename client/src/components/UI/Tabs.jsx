/**
 * ============================================
 * 📑 Tabs Component - نظام إدهام
 * Edham Logistics - Tabs
 * ============================================
 */

import React, { useState } from 'react';

export default function Tabs({ tabs, defaultTab = 0 }) {
  const [activeTab, setActiveTab] = useState(defaultTab);

  return (
    <div>
      <div className="flex border-b border-gray-700">
        {tabs.map((tab, index) => (
          <button
            key={index}
            onClick={() => setActiveTab(index)}
            className={`px-4 py-2 text-sm font-medium transition ${
              activeTab === index
                ? 'text-blue-500 border-b-2 border-blue-500'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>
      <div className="mt-4">
        {tabs[activeTab]?.content}
      </div>
    </div>
  );
}

export function TabPanel({ children, active }) {
  return active ? <div>{children}</div> : null;
}
