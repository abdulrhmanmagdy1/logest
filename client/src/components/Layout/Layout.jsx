/**
 * ============================================
 * 📐 Layout Component - نظام إدهام
 * Edham Logistics - Main Layout
 * ============================================
 */

import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import Footer from './Footer';

export default function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const closeSidebar = () => {
    setSidebarOpen(false);
  };

  return (
    <div className="min-h-screen bg-gray-900 flex flex-col">
      <Navbar toggleSidebar={toggleSidebar} sidebarOpen={sidebarOpen} />
      <div className="flex flex-1">
        <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} />
        <main className="flex-1 p-6 md:mr-64">
          <Outlet />
        </main>
      </div>
      <Footer />
    </div>
  );
}
