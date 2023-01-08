package com.qrcodecopier;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.function.Predicate;

import static com.qrcodecopier.Utils.hashSessionId;
import static com.qrcodecopier.Utils.verifySessionIdHash;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class WebSocketService {

    private final WebSocketBroadcaster broadcaster;

    Publisher<String> handleOpen(WebSocketSession session) {
        return broadcaster.broadcast(String.format("new session : %s", hashSessionId(session)), verifySessionId(session));
    }

    Publisher<String> handleMessage(String message, WebSocketSession session) {
        return broadcaster.broadcast(String.format("[%s] Message: %s", hashSessionId(session), message), verifySessionId(session));
    }

    Publisher<String> closeConnection(WebSocketSession session) {
        return broadcaster.broadcast(String.format("[%s] Leaving!", hashSessionId(session)), verifySessionId(session));
    }

    Publisher<String> handleError(WebSocketSession session, Throwable err) {
        return broadcaster.broadcast(String.format("[%s] Error: %s", hashSessionId(session), err.getMessage()), verifySessionId(session));
    }

    private Predicate<WebSocketSession> verifySessionId(WebSocketSession session) {
        return websocketsession -> verifySessionIdHash(hashSessionId(session), websocketsession.getId());
    }
}
