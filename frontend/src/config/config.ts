import config from "./config.json";

export const BACKEND_SOCKET_URL: string = `ws://${config.HOSTNAME}:${config.PORT}/${config.WEBSOCKET_PATH}`
