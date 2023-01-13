import {redirect} from "./utils";

// const onMessage = (webSocket: WebSocket, numberOfMessagesReceived: number) => function handleUrlMessage(msg: { data: string }) {
//     numberOfMessagesReceived++;
//     if (numberOfMessagesReceived === 1) // FIXME THIS IS NOT THE WAY
//         showToken(msg.data)
//     else {
//         webSocket.close()
//         showUrlToRedirectTo(msg.data)
//         setTimeout(() => redirect(msg.data), 1000);
//     }
// }