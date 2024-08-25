/** @type {import('tailwindcss').Config} */
module.exports = {
  mode: 'jit',
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#143AA2',
        secondary: '#D29D2B',
        tertiary: '#ffffff',
        quaternary: '#052461',
        history: '#E0F6D9FF',
        borderColor: 'rgba(34, 34, 34, 0.2)',
        headerBorderColor: 'rgba(3, 102, 214, 0.1)',
        backDropColor: 'rgb(231,245,250)',
        icons: '#BAD0FB',
        iconHover: '#315BAE',
        classificationGreen: '#007a33' // This is used in the snackbar, classification banner, and confirmation items
      }
    },
  },
  plugins: [],
}
