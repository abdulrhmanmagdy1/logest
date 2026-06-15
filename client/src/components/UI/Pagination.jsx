/**
 * ============================================
 * 📄 Pagination Component - نظام إدهام
 * Edham Logistics - Pagination
 * ============================================
 */

import React from 'react';

export default function Pagination({ currentPage, totalPages, onPageChange }) {
  const getPageNumbers = () => {
    const pages = [];
    const maxVisible = 5;

    if (totalPages <= maxVisible) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (currentPage <= 3) {
        for (let i = 1; i <= 4; i++) pages.push(i);
        pages.push('...');
        pages.push(totalPages);
      } else if (currentPage >= totalPages - 2) {
        pages.push(1);
        pages.push('...');
        for (let i = totalPages - 3; i <= totalPages; i++) pages.push(i);
      } else {
        pages.push(1);
        pages.push('...');
        for (let i = currentPage - 1; i <= currentPage + 1; i++) pages.push(i);
        pages.push('...');
        pages.push(totalPages);
      }
    }

    return pages;
  };

  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-center gap-2">
      <button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
        className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded disabled:opacity-50 disabled:cursor-not-allowed transition"
      >
        السابق
      </button>

      {getPageNumbers().map((page, index) => (
        page === '...' ? (
          <span key={index} className="text-gray-400 px-2">
            ...
          </span>
        ) : (
          <button
            key={page}
            onClick={() => onPageChange(page)}
            className={`px-4 py-2 rounded transition ${
              currentPage === page
                ? 'bg-blue-600 text-white'
                : 'bg-gray-700 hover:bg-gray-600 text-white'
            }`}
          >
            {page}
          </button>
        )
      ))}

      <button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
        className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded disabled:opacity-50 disabled:cursor-not-allowed transition"
      >
        التالي
      </button>
    </div>
  );
}
