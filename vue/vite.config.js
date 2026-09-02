import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig(({ command }) => ({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9999',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
      '/ws': {
        target: 'ws://localhost:9999',
        ws: true,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/ws/, ''),
      },
    },
  },
  plugins: [
    vue(),
    AutoImport({
      resolvers:[ElementPlusResolver()],
      dts: command === 'serve' ? "src/auto-imports.d.ts" : false, // 仅开发时生成类型声明
      }),
    Components({
        resolvers:[ElementPlusResolver({importStyle:"sass"})],
        dts: command === 'serve' ? "src/components.d.ts" : false, // 仅开发时生成组件类型声明
      }),
  ],
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/css/index.scss" as *;`,
      },
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
}))
