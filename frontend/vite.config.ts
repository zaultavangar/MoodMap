/// <reference types="vitest" />

import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import { ViteAliases } from "vite-aliases";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), ViteAliases()],
  test: {
    include: ["tests/unit/*.test.ts"],
    environment: "jsdom",
  },
});
