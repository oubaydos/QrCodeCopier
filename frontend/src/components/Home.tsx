import React, {useEffect, useState} from "react";
import "../style/Home.css"
import {BACKEND_SOCKET_URL} from "../config/config";
import {handleMessage} from "../service/websocket";


function Home() {
    const [data, setData] = useState<Array<string>>([]);
    useEffect(() => {
        const webSocket = new WebSocket(BACKEND_SOCKET_URL);
        webSocket.onmessage = handleMessage(webSocket, setData)
    }, [])


    return (
        <div>
            {data.map(value => <h1 key={value}>{value}</h1>)}
        </div>
    );
}

export default Home;