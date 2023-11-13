/// <reference types="vite/client" />
interface ImportMetaEnv {
  readonly VITE_MAPBOX_API_TOKEN: string;
  readonly VITE_COLOR_MODE: "light" | "dark";
  // more env variables...
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
