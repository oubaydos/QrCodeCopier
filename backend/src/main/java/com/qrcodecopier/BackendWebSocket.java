package com.qrcodecopier;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.Arrays;

@ServerWebSocket("/socket/{token}")
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
            String token) {

        return broadcaster.broadcast(String.format("[%s] Opened", token));
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

        log.error("errrrrrrrrr {}", Arrays.toString(err.getStackTrace()));
        return broadcaster.broadcast(String.format("[%s] %s", token, err.getMessage()));
    }

}
