/** @type {import('tailwindcss').Config} */
export default {
  // 关键：指定Tailwind要扫描的文件路径（包含所有Vue/JS/TS文件）
  content: [
    "./index.html", // 根目录的index.html
    "./src/**/*.{vue,js,ts,jsx,tsx}", // src目录下所有.vue/.js/.ts等文件
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}