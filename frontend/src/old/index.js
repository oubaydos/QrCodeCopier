let numberOfMessagesReceived = 0
const webSocket = new WebSocket(`ws://${config.hostname}:${config.port}/ws`);


function showToken(token) {
    insertMessage("received token: " + token);

}

function showUrlToRedirectTo(url) {
    insertMessage("redirecting to: " + url);
}

webSocket.onmessage = function handleUrlMessage(msg) {
    numberOfMessagesReceived++;
    if (numberOfMessagesReceived === 1) // FIXME THIS IS NOT THE WAY
        showToken(msg.data)
    else {
        webSocket.close()
        showUrlToRedirectTo(msg.data)
        setTimeout(() => redirect(msg.data), 1000);
    }
}

