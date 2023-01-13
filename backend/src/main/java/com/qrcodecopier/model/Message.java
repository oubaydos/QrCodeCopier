package com.qrcodecopier.model;

public record Message(
        MessageType type,
        String message
) {
}
