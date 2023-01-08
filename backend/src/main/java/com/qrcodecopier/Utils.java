package com.qrcodecopier;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.websocket.WebSocketSession;
import org.apache.commons.codec.digest.DigestUtils;

public class Utils {
    public static String hashSessionId(@NonNull String sessionId) {
        return DigestUtils.sha256Hex(sessionId);
    }

    public static String hashSessionId(@NonNull WebSocketSession session) {
        return hashSessionId(session.getId());
    }

    public static boolean verifySessionIdHash(@NonNull String hashedSessionId, @NonNull String sessionId) {
        return hashSessionId(sessionId).equals(hashedSessionId);
    }
}
