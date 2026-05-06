import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

import tailwindcss from '@tailwindcss/vite'

const isProd = process.env.BUILD_TARGET === "server";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  build: {
		target: "esnext",
		outDir: isProd
            ? "dist"
            : "../backend/src/main/resources/static",
		emptyOutDir: true,
	},
  server: {
		proxy: {
			"/api": "http://localhost:8080"
		}
	}
})
