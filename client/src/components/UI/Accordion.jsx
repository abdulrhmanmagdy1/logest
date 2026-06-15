/**
 * ============================================
 * 📎 Accordion Component - نظام إدهام
 * Edham Logistics - Accordion
 * ============================================
 */

import React, { useState } from 'react';
import { ChevronDown, ChevronUp } from 'lucide-react';

export default function Accordion({ items, allowMultiple = false }) {
  const [openItems, setOpenItems] = useState([]);

  const toggleItem = (index) => {
    if (allowMultiple) {
      setOpenItems(prev =>
        prev.includes(index) ? prev.filter(i => i !== index) : [...prev, index]
      );
    } else {
      setOpenItems(prev => (prev.includes(index) ? [] : [index]));
    }
  };

  return (
    <div className="space-y-2">
      {items.map((item, index) => (
        <AccordionItem
          key={index}
          title={item.title}
          content={item.content}
          isOpen={openItems.includes(index)}
          onToggle={() => toggleItem(index)}
        />
      ))}
    </div>
  );
}

function AccordionItem({ title, content, isOpen, onToggle }) {
  return (
    <div className="bg-gray-800 rounded-lg overflow-hidden">
      <button
        onClick={onToggle}
        className="w-full px-4 py-3 flex items-center justify-between text-white hover:bg-gray-700 transition"
      >
        <span className="font-semibold">{title}</span>
        {isOpen ? <ChevronUp className="w-5 h-5" /> : <ChevronDown className="w-5 h-5" />}
      </button>
      {isOpen && (
        <div className="px-4 py-3 border-t border-gray-700 text-gray-300">
          {content}
        </div>
      )}
    </div>
  );
}
