/**
 * ============================================
 * 🚨 Error Boundary Component - نظام إدهام
 * Edham Logistics - Error Boundary
 * ============================================
 */

import React, { Component } from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({
      error: error,
      errorInfo: errorInfo
    });
    
    // Log error to console
    console.error('Error caught by ErrorBoundary:', error, errorInfo);
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null, errorInfo: null });
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-gray-900 flex items-center justify-center p-4">
          <div className="bg-gray-800 rounded-lg p-8 max-w-md w-full text-center">
            <div className="text-red-500 mb-4 flex justify-center">
              <AlertTriangle className="w-16 h-16" />
            </div>
            <h1 className="text-2xl font-bold text-white mb-2">حدث خطأ غير متوقع</h1>
            <p className="text-gray-400 mb-6">
              نأسف لحدوث خطأ. يرجى المحاولة مرة أخرى أو الاتصال بالدعم الفني إذا استمرت المشكلة.
            </p>
            
            {process.env.NODE_ENV === 'development' && this.state.error && (
              <div className="bg-gray-700 rounded p-4 mb-6 text-right">
                <p className="text-red-400 text-sm font-mono">
                  {this.state.error.toString()}
                </p>
                {this.state.errorInfo && (
                  <pre className="text-gray-400 text-xs mt-2 overflow-auto">
                    {this.state.errorInfo.componentStack}
                  </pre>
                )}
              </div>
            )}
            
            <button
              onClick={this.handleReset}
              className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded flex items-center gap-2 mx-auto transition"
            >
              <RefreshCw className="w-4 h-4" />
              إعادة المحاولة
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
