package com.qrcodecopier;

import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.websocket.WebSocketClient;
import io.micronaut.websocket.annotation.ClientWebSocket;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

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
    @Getter
    static abstract class TestWebSocketClient implements AutoCloseable {

        private final Deque<String> messageHistory = new ConcurrentLinkedDeque<>();
        private Boolean isOpen = false;

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

        @OnOpen
        void handleOpen() {
            isOpen = true;
        }

        @OnClose
        void handleClose() {
            isOpen = false;
        }

        abstract void send(@NonNull @NotBlank String message);
    }

    private TestWebSocketClient createWebSocketClient() {
        WebSocketClient webSocketClient = beanContext.getBean(WebSocketClient.class);
        URI uri = UriBuilder.of("ws://localhost").port(embeddedServer.getPort()).path("ws").build();
        Publisher<TestWebSocketClient> client = webSocketClient.connect(TestWebSocketClient.class, uri);
        return Flux.from(client).blockFirst();
    }

    @Test
    void shouldOpenWebSocketSession() {
        //Given
        //When
        TestWebSocketClient webSocketClientTest = createWebSocketClient();
        //Then
        Assertions.assertDoesNotThrow(() -> await().catchUncaughtExceptions().atMost(5, TimeUnit.SECONDS).until(webSocketClientTest::getIsOpen));
        log.debug("shouldOpenWebSocketSession test got these messages: {}", webSocketClientTest.getMessagesChronologically());
    }

    @Test
    void shouldNotBroadcastMessageToOtherSessionsWhenNewMessageIsSent() {
        //Given
        //When
        TestWebSocketClient webSocketClientTest = createWebSocketClient();
        TestWebSocketClient webSocketClientTest1 = createWebSocketClient();

        //Then
        Assertions.assertDoesNotThrow(() -> awaitWithTimeout().until(() -> webSocketClientTest.getMessagesChronologically().size() == 1));
        Assertions.assertDoesNotThrow(() -> awaitWithTimeout().until(() -> webSocketClientTest1.getMessagesChronologically().size() == 1));
        webSocketClientTest.send("hello");
        Assertions.assertDoesNotThrow(() -> awaitWithTimeout().until(() -> webSocketClientTest.getMessagesChronologically().size() == 2));
        Assertions.assertDoesNotThrow(() -> awaitWithTimeout().until(() -> webSocketClientTest1.getMessagesChronologically().size() == 1));

        log.debug("shouldOpenWebSocketSession test got these messages: {}", webSocketClientTest.getMessagesChronologically());
        log.debug("shouldOpenWebSocketSession test1 got these messages: {}", webSocketClientTest1.getMessagesChronologically());
    }

    private org.awaitility.core.ConditionFactory awaitWithTimeout() {
        return await().catchUncaughtExceptions().atMost(5, TimeUnit.SECONDS);
    }

}
