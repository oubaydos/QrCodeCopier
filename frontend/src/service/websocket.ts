import {redirect, showUrlToRedirectTo} from "./utils";
import {Message, MessageType} from "./messageType";
import {Dispatch, SetStateAction} from "react";

export const handleMessage =
    (webSocket: WebSocket, setData: Dispatch<SetStateAction<string[]>>, setSessionId: Dispatch<SetStateAction<string>>) =>
        function handleUrlMessage(msg: { data: string }) {
            const message: Message = JSON.parse(msg.data);
            if (message.type === MessageType.SESSION_ID)
                setSessionId(message.message)
            else if (message.type === MessageType.REDIRECTION_URL) {
                webSocket.close()
                showUrlToRedirectTo(message.message, setData)
                setTimeout(() => redirect(message.message), 1000);
            } else {
                webSocket.close();
                alert(message.type)
                alert(message.message)
            }
        }