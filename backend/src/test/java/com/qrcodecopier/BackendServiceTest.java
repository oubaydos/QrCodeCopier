package com.qrcodecopier;

import io.micronaut.http.HttpResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
class BackendServiceTest {

    @Inject
    BackendService backendService;

    @Test
    void copyUrlShouldReturnOk() {
        // Given
        String token = "temptoken";
        String url = "www.google.com";
        // When
        HttpResponse<?> res = backendService.copyUrl(token, url);
        // Then
        Assertions.assertNotNull(res);
        Assertions.assertEquals(res.code(), HttpResponse.ok().code());
    }

    @Test
    void copyUrlShouldReturnError() {
        // Given
        String token = null;
        String url = "www.google.com";
        // When Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> backendService.copyUrl(token, url), "token should not be null");
    }


}
