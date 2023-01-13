package com.qrcodecopier.service;

import com.qrcodecopier.model.Message;
import com.qrcodecopier.model.MessageType;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import java.util.function.Predicate;

import static com.qrcodecopier.utils.Utils.*;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class WebSocketService {

    private final WebSocketBroadcaster broadcaster;

    public Publisher<String> handleOpen(WebSocketSession session) {
        Message typedMessage = new Message(MessageType.SESSION_ID, hashSessionId(session));
        return broadcast(typedMessage, session);
    }

    public Publisher<String> handleMessage(String message, WebSocketSession session) {
        Message typedMessage = new Message(MessageType.REDIRECTION_URL, message);
        return broadcast(typedMessage, session);
    }

    public Publisher<String> closeConnection(WebSocketSession session) {
        Message typedMessage = new Message(MessageType.CLOSING, hashSessionId(session));
        return broadcast(typedMessage, session);
    }

    public Publisher<String> handleError(WebSocketSession session, Throwable err) {
        Message typedMessage = new Message(MessageType.CLOSING, String.format("[%s] Error: %s", hashSessionId(session), err.getMessage()));
        return broadcast(typedMessage, session);
    }

    private Predicate<WebSocketSession> verifySessionId(WebSocketSession session) {
        return websocketsession -> verifySessionIdHash(hashSessionId(session), websocketsession.getId());
    }

    private Publisher<String> broadcast(Message typedMessage, WebSocketSession session) {
        return broadcaster.broadcast(serializeToJson(typedMessage), verifySessionId(session));
    }
}
