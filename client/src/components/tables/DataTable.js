import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const DataTable = ({ 
  columns = [], 
  data = [], 
  title,
  page = 1,
  totalPages = 1,
  onPageChange,
  loading = false
}) => {
  return (
    <div className="card overflow-hidden">
      {title && <h3 className="text-xl font-bold text-edham-white mb-6">{title}</h3>}
      
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b border-white/10">
              {columns.map((col) => (
                <th 
                  key={col.key} 
                  className="text-right py-4 px-4 text-edham-white/60 font-medium text-sm"
                >
                  {col.label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={columns.length} className="py-12 text-center text-edham-white/50">
                  جار التحميل...
                </td>
              </tr>
            ) : data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="py-12 text-center text-edham-white/50">
                  لا توجد بيانات
                </td>
              </tr>
            ) : (
              data.map((row, idx) => (
                <tr key={idx} className="border-b border-white/5 hover:bg-white/5 transition-colors">
                  {columns.map((col) => (
                    <td key={col.key} className="py-4 px-4 text-edham-white">
                      {col.render ? col.render(row[col.key], row) : row[col.key]}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="flex items-center justify-between mt-6 pt-6 border-t border-white/10">
          <button
            onClick={() => onPageChange(page - 1)}
            disabled={page <= 1}
            className="flex items-center gap-2 px-4 py-2 rounded-lg text-sm disabled:opacity-50 hover:bg-white/5 transition-colors"
          >
            <ChevronRight className="w-4 h-4" />
            السابق
          </button>
          <span className="text-edham-white/60 text-sm">
            صفحة {page} من {totalPages}
          </span>
          <button
            onClick={() => onPageChange(page + 1)}
            disabled={page >= totalPages}
            className="flex items-center gap-2 px-4 py-2 rounded-lg text-sm disabled:opacity-50 hover:bg-white/5 transition-colors"
          >
            التالي
            <ChevronLeft className="w-4 h-4" />
          </button>
        </div>
      )}
    </div>
  );
};

export default DataTable;
