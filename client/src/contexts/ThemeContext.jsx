import React, { createContext, useContext, useState, useEffect } from 'react';

// Create the theme context
const ThemeContext = createContext();

// Theme configurations
const themes = {
  green: {
    name: 'Saska Green',
    colors: {
      primary: '#10b981',
      primaryDark: '#059669',
      secondary: '#6b7280',
      background: '#111827',
      surface: '#1f2937',
      text: '#ffffff',
      textSecondary: '#9ca3af',
      border: '#374151',
      success: '#10b981',
      error: '#ef4444',
      warning: '#f59e0b',
      info: '#3b82f6'
    }
  },
  orange: {
    name: 'LoadSphere Orange',
    colors: {
      primary: '#f97316',
      primaryDark: '#ea580c',
      secondary: '#6b7280',
      background: '#000000',
      surface: '#111827',
      text: '#ffffff',
      textSecondary: '#9ca3af',
      border: '#374151',
      success: '#10b981',
      error: '#ef4444',
      warning: '#f59e0b',
      info: '#3b82f6'
    }
  }
};

// Theme provider component
export const ThemeProvider = ({ children }) => {
  const [currentTheme, setCurrentTheme] = useState('green');
  const [theme, setTheme] = useState(themes.green);

  // Update theme when currentTheme changes
  useEffect(() => {
    setTheme(themes[currentTheme]);
  }, [currentTheme]);

  // Toggle between themes
  const toggleTheme = () => {
    setCurrentTheme(prev => prev === 'green' ? 'orange' : 'green');
  };

  // Set specific theme
  const setThemeByName = (themeName) => {
    if (themes[themeName]) {
      setCurrentTheme(themeName);
    }
  };

  const value = {
    theme,
    currentTheme,
    themes,
    toggleTheme,
    setThemeByName
  };

  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  );
};

// Custom hook to use the theme context
export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

export default ThemeContext;
