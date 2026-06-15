/**
 * ============================================
 * 🎨 Tailwind Configuration - نظام إدهام
 * Edham Logistics - Modern Dark Orange Theme
 * ============================================
 * Primary Orange: #F97316 (برتقالي)
 * Secondary Orange: #FB923C (برتقالي فاتح)
 * Dark Background: #0A0A0A (خلفية داكنة)
 * ============================================
 */

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Edham Brand Colors - Orange Theme from images
        edham: {
          // Primary Orange (برتقالي)
          primary: {
            DEFAULT: '#F97316',
            50: '#FFF7ED',
            100: '#FFEDD5',
            200: '#FED7AA',
            300: '#FDBA74',
            400: '#FB923C',
            500: '#F97316',
            600: '#EA580C',
            700: '#C2410C',
            800: '#9A3412',
            900: '#7C2D12',
          },
          // Secondary Orange (برتقالي فاتح)
          secondary: {
            DEFAULT: '#FB923C',
            50: '#FFF7ED',
            100: '#FFEDD5',
            200: '#FED7AA',
            300: '#FDBA74',
            400: '#FB923C',
            500: '#F97316',
            600: '#EA580C',
            700: '#C2410C',
            800: '#9A3412',
            900: '#7C2D12',
          },
          // Accent Orange
          gold: {
            DEFAULT: '#F97316',
            50: '#FFF7ED',
            100: '#FFEDD5',
            200: '#FED7AA',
            300: '#FDBA74',
            400: '#FB923C',
            500: '#F97316',
            600: '#EA580C',
            700: '#C2410C',
            800: '#9A3412',
            900: '#7C2D12',
          },
          // Background Colors - Dark Theme
          dark: {
            DEFAULT: '#0A0A0A',
            50: '#1C1C1E',
            100: '#2C2C2E',
            200: '#3A3A3C',
            300: '#48484A',
          },
          // Text Colors
          white: '#FFFFFF',
          text: {
            primary: '#FFFFFF',
            secondary: '#A3A3A3',
            muted: '#737373',
          },
          // Status Colors
          success: '#10B981',
          warning: '#F97316',
          error: '#EF4444',
          info: '#FB923C',
        },
      },
      fontFamily: {
        cairo: ['Cairo', 'sans-serif'],
        inter: ['Inter', 'sans-serif'],
      },
      boxShadow: {
        'edham': '0 4px 6px -1px rgba(249, 115, 22, 0.1)',
        'edham-orange': '0 10px 15px -3px rgba(249, 115, 22, 0.2)',
        'edham-lg': '0 20px 25px -5px rgba(249, 115, 22, 0.3)',
      },
      borderRadius: {
        'edham': '8px',
        'edham-lg': '16px',
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'slide-down': 'slideDown 0.3s ease-out',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        slideDown: {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
    },
  },
  plugins: [],
}
