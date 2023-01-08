package com.qrcodecopier;

import io.micronaut.http.HttpResponse;
import io.micronaut.websocket.WebSocketBroadcaster;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.qrcodecopier.Utils.verifySessionIdHash;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class BackendService {
    private final WebSocketBroadcaster broadcaster;

    public HttpResponse<?> copyUrl(String token, String url) {
        validateToken(token);
        broadcaster.broadcastAsync(url, session -> verifySessionIdHash(token, session.getId()));
        return HttpResponse.ok();
    }

    private void validateToken(String token) {
        if (Objects.isNull(token)) throw new IllegalArgumentException("token should not be null");
    }
}
