/// <reference types="vite/client" />
interface ImportMetaEnv {
  readonly VITE_MAPBOX_API_TOKEN: string;
  readonly VITE_COLOR_MODE: "light" | "dark";
  readonly VITE_API_BASE_URL: string;
  // more env variables...
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
