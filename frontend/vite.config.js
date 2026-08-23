import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const backendUrl = 'http://localhost:5151'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: backendUrl,
        changeOrigin: true,
      },
      '/auth': {
        target: backendUrl,
        changeOrigin: true,
      },
    },
  },
})
