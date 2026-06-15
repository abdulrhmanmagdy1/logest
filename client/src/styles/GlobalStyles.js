/**
 * ============================================
 * 🎨 Global Styles & Animation Configuration
 * Premium enterprise logistics styling
 * ============================================
 */

import { createGlobalStyle } from 'styled-components';

const GlobalStyles = createGlobalStyle`
  /* Root variables */
  :root {
    /* Colors */
    --color-primary: #3b82f6;
    --color-primary-dark: #1e40af;
    --color-primary-light: #93c5fd;
    --color-secondary: #06b6d4;
    --color-accent: #8b5cf6;
    
    /* Neutral */
    --color-white: #ffffff;
    --color-gray-50: #f9fafb;
    --color-gray-100: #f3f4f6;
    --color-gray-200: #e5e7eb;
    --color-gray-300: #d1d5db;
    --color-gray-400: #9ca3af;
    --color-gray-500: #6b7280;
    --color-gray-600: #4b5563;
    --color-gray-700: #374151;
    --color-gray-800: #1f2937;
    --color-gray-900: #111827;
    --color-gray-950: #03040c;
    
    /* Status */
    --color-success: #10b981;
    --color-warning: #f59e0b;
    --color-error: #ef4444;
    --color-info: #3b82f6;
    
    /* Shadows */
    --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
    --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
    --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
    
    /* Typography */
    --font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
      'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
    --font-mono: 'SFMono-Regular', 'Consolas', 'Liberation Mono', 'Menlo', 'Courier', monospace;
  }

  /* Global resets */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  html {
    scroll-behavior: smooth;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  body {
    font-family: var(--font-family);
    background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
    color: var(--color-gray-900);
    line-height: 1.6;
    transition: background-color 0.3s ease;
  }

  body.dark-mode {
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
    color: var(--color-white);
  }

  /* Typography */
  h1, h2, h3, h4, h5, h6 {
    font-weight: 600;
    line-height: 1.2;
    margin-bottom: 0.5rem;
  }

  h1 {
    font-size: 2.25rem;
  }

  h2 {
    font-size: 1.875rem;
  }

  h3 {
    font-size: 1.5rem;
  }

  /* Links */
  a {
    color: var(--color-primary);
    text-decoration: none;
    transition: color 0.2s ease;

    &:hover {
      color: var(--color-primary-dark);
    }
  }

  /* Input elements */
  input, textarea, select {
    font-family: inherit;
    font-size: 1rem;
  }

  input[type="text"],
  input[type="email"],
  input[type="password"],
  textarea,
  select {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid var(--color-gray-300);
    border-radius: 0.5rem;
    background: var(--color-white);
    color: var(--color-gray-900);
    transition: all 0.2s ease;

    &:focus {
      outline: none;
      border-color: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }

    &:disabled {
      background: var(--color-gray-100);
      cursor: not-allowed;
    }
  }

  body.dark-mode {
    input[type="text"],
    input[type="email"],
    input[type="password"],
    textarea,
    select {
      background: var(--color-gray-800);
      color: var(--color-white);
      border-color: var(--color-gray-700);

      &:focus {
        border-color: var(--color-primary);
        box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
      }
    }
  }

  /* Buttons */
  button {
    font-family: inherit;
    cursor: pointer;
    transition: all 0.2s ease;

    &:disabled {
      cursor: not-allowed;
      opacity: 0.5;
    }
  }

  /* Scrollbar styling */
  ::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  ::-webkit-scrollbar-track {
    background: var(--color-gray-100);
  }

  body.dark-mode ::-webkit-scrollbar-track {
    background: var(--color-gray-800);
  }

  ::-webkit-scrollbar-thumb {
    background: var(--color-gray-400);
    border-radius: 4px;

    &:hover {
      background: var(--color-gray-500);
    }
  }

  body.dark-mode ::-webkit-scrollbar-thumb {
    background: var(--color-gray-600);

    &:hover {
      background: var(--color-gray-500);
    }
  }

  /* Loading spinner */
  @keyframes spin {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }

  .spinner {
    animation: spin 1s linear infinite;
  }

  /* Fade in animation */
  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  /* Slide up animation */
  @keyframes slideUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  /* Pulse animation */
  @keyframes pulse {
    0%, 100% {
      opacity: 1;
    }
    50% {
      opacity: 0.5;
    }
  }

  /* Gradient animation */
  @keyframes gradient {
    0% {
      background-position: 0% 50%;
    }
    50% {
      background-position: 100% 50%;
    }
    100% {
      background-position: 0% 50%;
    }
  }

  /* Selection */
  ::selection {
    background: var(--color-primary);
    color: var(--color-white);
  }

  /* Focus visible */
  :focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: 2px;
  }

  /* Utility classes */
  .animate-spin {
    animation: spin 1s linear infinite;
  }

  .animate-fade-in {
    animation: fadeIn 0.3s ease-out;
  }

  .animate-slide-up {
    animation: slideUp 0.3s ease-out;
  }

  .animate-pulse {
    animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
  }

  .gradient-animation {
    background-size: 200% 200%;
    animation: gradient 3s ease infinite;
  }

  /* Responsive */
  @media (max-width: 768px) {
    h1 {
      font-size: 1.875rem;
    }

    h2 {
      font-size: 1.5rem;
    }

    h3 {
      font-size: 1.25rem;
    }
  }

  /* Print styles */
  @media print {
    body {
      background: var(--color-white);
    }

    button, a {
      display: none;
    }
  }
`;

// Framer Motion animation presets
export const animationPresets = {
  // Page transitions
  pageEnter: {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0 },
    exit: { opacity: 0, y: -20 },
    transition: { duration: 0.3, ease: 'easeOut' }
  },

  // Fade in
  fadeIn: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
    transition: { duration: 0.3 }
  },

  // Scale up
  scaleIn: {
    initial: { opacity: 0, scale: 0.9 },
    animate: { opacity: 1, scale: 1 },
    transition: { duration: 0.3, ease: 'easeOut' }
  },

  // Stagger container
  staggerContainer: {
    animate: {
      transition: {
        staggerChildren: 0.1,
        delayChildren: 0.2
      }
    }
  },

  // Stagger item
  staggerItem: {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0 },
    transition: { duration: 0.4 }
  },

  // Slide from left
  slideInLeft: {
    initial: { opacity: 0, x: -50 },
    animate: { opacity: 1, x: 0 },
    transition: { duration: 0.5, ease: 'easeOut' }
  },

  // Slide from right
  slideInRight: {
    initial: { opacity: 0, x: 50 },
    animate: { opacity: 1, x: 0 },
    transition: { duration: 0.5, ease: 'easeOut' }
  },

  // Bounce
  bounce: {
    animate: {
      y: [0, -10, 0],
      transition: {
        duration: 0.6,
        repeat: Infinity,
        ease: 'easeInOut'
      }
    }
  }
};

// Tailwind configuration for premium design
export const tailwindConfig = {
  theme: {
    extend: {
      colors: {
        primary: 'var(--color-primary)',
        'primary-dark': 'var(--color-primary-dark)',
        'primary-light': 'var(--color-primary-light)',
        secondary: 'var(--color-secondary)',
        accent: 'var(--color-accent)',
      },
      boxShadow: {
        sm: 'var(--shadow-sm)',
        md: 'var(--shadow-md)',
        lg: 'var(--shadow-lg)',
        xl: 'var(--shadow-xl)',
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-out',
        'slide-up': 'slideUp 0.3s ease-out',
      }
    }
  }
};

export default GlobalStyles;
