import React, {useEffect, useState} from "react";
import config from '../config.json';
import {redirect, showToken, showUrlToRedirectTo} from "../service/utils";
import QrCode from "react-qr-code"
import "../style/Home.css"

const BACKEND_SOCKET_URL: string = `ws://${config.hostname}:${config.port}/ws`


export default function () {
    const [data, setData] = useState<Array<string>>([]);
    useEffect(() => {
        let numberOfMessagesReceived = 0;
        const webSocket = new WebSocket(BACKEND_SOCKET_URL);
        webSocket.onmessage = function handleUrlMessage(msg: { data: string }) {
            numberOfMessagesReceived++;
            if (numberOfMessagesReceived === 1) // FIXME THIS IS NOT THE WAY
                showToken(msg.data, setData)
            else {
                webSocket.close()
                showUrlToRedirectTo(msg.data, setData)
                setTimeout(() => redirect(msg.data), 1000);
            }
        }

    }, [])


    return (
        <div>
            {data.map(value => <h1 key={value}>${value}</h1>)}
            {data.length === 1 && <QrCode value={data[0]}/>}
        </div>
    );
}
