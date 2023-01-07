package com.qrcodecopier;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.Arrays;

@ServerWebSocket("/ws/{token}")
@Slf4j
@RequiredArgsConstructor
public class BackendWebSocket {
    private final WebSocketBroadcaster broadcaster;

    @OnOpen
    public Publisher<String> openSession(String token) {
        return broadcaster.broadcast(String.format("[%s] Opened", token));
    }


    @OnMessage
    public Publisher<String> handleMessage(
            String token, String message) {

        return broadcaster.broadcast(String.format("[%s] Message: %s", token, message));
    }

    @OnClose
    public Publisher<String> closeConnection(
            String token) {

        return broadcaster.broadcast(String.format("[%s] Leaving!", token));
    }

    @OnError
    public Publisher<String> handleError(
            String token,
            Throwable err) {

        log.error("{}", Arrays.toString(err.getStackTrace()));
        return broadcaster.broadcast(String.format("[%s] Error: %s", token, err.getMessage()));
    }


}
