package com.qrcodecopier;

import io.micronaut.http.HttpResponse;
import io.micronaut.websocket.WebSocketBroadcaster;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class BackendService {
    private final WebSocketBroadcaster broadcaster;

    public HttpResponse<?> copyUrl(String token, String url) {
        validateToken(token);
        // todo token should not be sessionId, you should hash it
        broadcaster.broadcastAsync(url, session -> session.getId().equals(token));
        return HttpResponse.ok();
    }

    private void validateToken(String token) {
        if (Objects.isNull(token)) throw new IllegalArgumentException("token should not be null");
    }
}
