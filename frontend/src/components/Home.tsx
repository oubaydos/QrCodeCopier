import React, {useEffect, useState} from "react";
import config from '../config.json';
import {redirect} from "../service/utils";

const BACKEND_SOCKET_URL: string = `ws://${config.hostname}:${config.port}/ws`


export default function () {
    const [isConnected, setIsConnected] = useState<boolean>(false);
    const [data, setData] = useState<Array<string>>([]);
    useEffect(() => {
        let numberOfMessagesReceived = 0;
        const webSocket = new WebSocket(`ws://${config.hostname}:${config.port}/ws`);
        webSocket.onmessage = function handleUrlMessage(msg: { data: string }) {
            numberOfMessagesReceived++;
            if (numberOfMessagesReceived === 1) // FIXME THIS IS NOT THE WAY
                showToken(msg.data)
            else {
                webSocket.close()
                showUrlToRedirectTo(msg.data)
                setTimeout(() => redirect(msg.data), 1000);
            }
        }

    }, [])


    function showToken(token: string) {
        setData(data => [...data, "received token: " + token]);

    }

    function showUrlToRedirectTo(url: string) {
        setData(data => [...data, "redirecting to: " + url]);
    }

    useEffect(() => {
        const webSocket: WebSocket = new WebSocket(BACKEND_SOCKET_URL);
        setIsConnected(Boolean(webSocket.OPEN))
        // console.log("useEffect");
        // socket.on('connect', () => {
        //     setIsConnected(true);
        // });

        // return () => {
        //     console.log("haha ", socket.connected)
        //
        //     socket.off('connect');
        // };

    }, []);

    return (
        <div>
            <h1>Connected: {'' + isConnected}</h1>
            {data.map(value => <h1 key={value}>${value}</h1>)}
        </div>
    );
}
