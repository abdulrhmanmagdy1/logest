/**
 * ============================================
 * 📊 Table Component - نظام إدهام
 * Professional data table component
 * ============================================
 */

import React from 'react';
import { ChevronDown, ChevronUp, ChevronsUpDown } from 'lucide-react';

const Table = ({
  columns,
  data,
  keyExtractor,
  isLoading = false,
  emptyMessage = 'لا توجد بيانات',
  sortColumn,
  sortDirection,
  onSort,
  isStriped = true,
  isHoverable = true,
}) => {
  const handleSort = (columnKey) => {
    if (onSort && columnKey) {
      onSort(columnKey);
    }
  };

  const renderSortIcon = (column) => {
    if (!column.sortable) return null;
    
    if (sortColumn !== column.key) {
      return <ChevronsUpDown className="w-4 h-4 mr-1 text-gray-400" />;
    }
    
    return sortDirection === 'asc' 
      ? <ChevronUp className="w-4 h-4 mr-1 text-blue-500" />
      : <ChevronDown className="w-4 h-4 mr-1 text-blue-500" />;
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-gray-500">
        <p>{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            {columns.map((column) => (
              <th
                key={column.key}
                scope="col"
                className={`px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider ${
                  column.sortable ? 'cursor-pointer hover:bg-gray-100' : ''
                } ${column.className || ''}`}
                onClick={() => handleSort(column.key)}
              >
                <div className="flex items-center">
                  {renderSortIcon(column)}
                  {column.title}
                </div>
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {data.map((row, index) => (
            <tr
              key={keyExtractor ? keyExtractor(row) : index}
              className={`${isStriped && index % 2 === 1 ? 'bg-gray-50' : ''} ${
                isHoverable ? 'hover:bg-gray-100' : ''
              }`}
            >
              {columns.map((column) => (
                <td
                  key={`${keyExtractor ? keyExtractor(row) : index}-${column.key}`}
                  className={`px-6 py-4 whitespace-nowrap text-sm text-gray-900 ${
                    column.className || ''
                  }`}
                >
                  {column.render
                    ? column.render(row[column.key], row)
                    : row[column.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Table;
