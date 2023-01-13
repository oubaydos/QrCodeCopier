package com.qrcodecopier.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.server.exceptions.InternalServerException;
import io.micronaut.websocket.WebSocketSession;
import org.apache.commons.codec.digest.DigestUtils;

public class Utils {
    private static final ObjectWriter OBJECT_WRITER = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public static String hashSessionId(@NonNull String sessionId) {
        return DigestUtils.sha256Hex(sessionId);
    }

    public static String hashSessionId(@NonNull WebSocketSession session) {
        return hashSessionId(session.getId());
    }

    public static boolean verifySessionIdHash(@NonNull String hashedSessionId, @NonNull String sessionId) {
        return hashSessionId(sessionId).equals(hashedSessionId);
    }

    public static String serializeToJson(Object object) {
        try {
            return OBJECT_WRITER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // TODO change
            throw new InternalServerException(e.getMessage(), e);
        }
    }
}
