/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{js,jsx,ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        edham: {
          primary: '#0ea5e9',
          surface: '#0b1123',
          surfaceSoft: '#111c40',
          accent: '#38bdf8',
        },
      },
      boxShadow: {
        edham: '0 24px 80px rgba(15, 23, 42, 0.32)',
      },
      borderRadius: {
        xl: '1.5rem',
      },
    },
  },
  plugins: [],
};
