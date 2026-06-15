/**
 * ============================================
 * 🚛 Edham Logistics - Unified Super App
 * نظام إدهام - التطبيق الموحد
 * ============================================
 */

import React from 'react';
import { ThemeProvider } from './contexts/ThemeContext';
import UnifiedAppPage from './pages/UnifiedAppPage';
import './styles/Theme.css';

function App() {
  return (
    <ThemeProvider>
      <div className="app">
        <UnifiedAppPage />
      </div>
    </ThemeProvider>
  );
}

export default App;
