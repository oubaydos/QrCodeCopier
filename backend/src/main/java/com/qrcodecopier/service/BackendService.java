package com.qrcodecopier.service;

import com.qrcodecopier.model.Message;
import com.qrcodecopier.model.MessageType;
import io.micronaut.http.HttpResponse;
import io.micronaut.websocket.WebSocketBroadcaster;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.qrcodecopier.utils.Utils.serializeToJson;
import static com.qrcodecopier.utils.Utils.verifySessionIdHash;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class BackendService {
    private final WebSocketBroadcaster broadcaster;

    public HttpResponse<?> copyUrl(String token, String url) {
        validateToken(token);
        Message message = new Message(MessageType.REDIRECTION_URL, url);
        broadcaster.broadcastAsync(serializeToJson(message), session -> verifySessionIdHash(token, session.getId()));
        return HttpResponse.ok();
    }

    private void validateToken(String token) {
        if (Objects.isNull(token)) throw new IllegalArgumentException("token should not be null");
    }
}
