package com.qrcodecopier;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

//@ServerWebSocket("/websocket/{token}")
@Controller("/api")
@Slf4j
@RequiredArgsConstructor
public class BackendController {
    private final BackendService backendService;

    @Post(consumes = APPLICATION_JSON)
    public HttpResponse<?> copyUrl(String token, String url) {
        log.info("copyUrl POST request with token {} and url {}", token, url);
        return backendService.copyUrl(token,url);
    }
}