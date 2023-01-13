import React, {useEffect, useState} from "react";
import "../style/Home.css"
import {BACKEND_SOCKET_URL} from "../config/config";
import {handleMessage} from "../service/websocket";
import QRCode from "react-qr-code";


function Home() {
    const [data, setData] = useState<Array<string>>([]);
    const [sessionId, setSessionId] = useState<string>("")
    useEffect(() => {
        const webSocket = new WebSocket(BACKEND_SOCKET_URL);
        webSocket.onmessage = handleMessage(webSocket, setData, setSessionId)
    }, [])


    return (
        <div>
            {sessionId && <QRCode value={sessionId} style={{margin: "20px"}}/>}
            {data.map(value => <h1 key={value}>{value}</h1>)}
        </div>
    );
}

export default Home;