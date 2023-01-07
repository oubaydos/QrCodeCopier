package com.qrcodecopier;

import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.websocket.WebSocketClient;
import io.micronaut.websocket.annotation.ClientWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.awaitility.Awaitility.await;

@MicronautTest
@Slf4j
class BackendControllerTest {
    @Inject
    BeanContext beanContext;

    @Inject
    WebSocketClient client;
    @Inject
    EmbeddedServer embeddedServer;

    @ClientWebSocket
    static abstract class TestWebSocketClient implements AutoCloseable {

        private final Deque<String> messageHistory = new ConcurrentLinkedDeque<>();

        public String getLatestMessage() {
            return messageHistory.peekLast();
        }

        public List<String> getMessagesChronologically() {
            return new ArrayList<>(messageHistory);
        }

        @OnMessage
        void handleMessageFromServer(String message) {
            messageHistory.add(message);
        }

        abstract void send(@NonNull @NotBlank String message);
    }

    private TestWebSocketClient createWebSocketClient(String token) {
        WebSocketClient webSocketClient = beanContext.getBean(WebSocketClient.class);
        URI uri = UriBuilder.of("ws://localhost")
                .port(embeddedServer.getPort())
                .path("ws")
                .path(token)
                .build();
        Publisher<TestWebSocketClient> client = webSocketClient.connect(TestWebSocketClient.class, uri);
        return Flux.from(client).blockFirst();
    }

    @Test
    void shouldOpenWebSocketSession() {
        //Given
        TestWebSocketClient webSocketClientTest = createWebSocketClient("webSocketClientTest");
        await().until(() -> !webSocketClientTest.getMessagesChronologically().isEmpty());

        log.info("webSocketClientTest {}", webSocketClientTest.getLatestMessage());

    }

}
