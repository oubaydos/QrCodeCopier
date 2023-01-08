package com.qrcodecopier;

import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.Arrays;

@ServerWebSocket()
@Slf4j
@RequiredArgsConstructor
public class BackendWebSocket {

    private final WebSocketService webSocketService;

    @OnOpen
    public Publisher<String> openSession(WebSocketSession session) {
        log.info("open sessions {}", session.getOpenSessions());
        return webSocketService.handleOpen(session);
    }


    @OnMessage
    public Publisher<String> handleMessage(String message, WebSocketSession session) {
        return webSocketService.handleMessage(message, session);
    }

    @OnClose
    public Publisher<String> closeConnection(
            WebSocketSession session) {

        return webSocketService.closeConnection(session);
    }


    @OnError
    public Publisher<String> handleError(
            WebSocketSession session,
            Throwable err) {

        log.error("{}", Arrays.toString(err.getStackTrace()));
        return webSocketService.handleError(session, err);
    }


}
