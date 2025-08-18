package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Common
    INTERNAL_ERROR("Internal server error"),
    INVALID_REQUEST("Invalid request"),
    VALIDATION_FAILED("Validation failed"),
    RESOURCE_CONFLICT("Resource conflict"),

    // Auth
    UNAUTHORIZED("Unauthorized"),
    FORBIDDEN("Forbidden"),
    AUTHENTICATION_FAILED("Invalid credentials"),

    // User
    USER_NOT_FOUND("User not found"),
    DUPLICATE_USER("User already exists"),
    DUPLICATE_EMAIL("Email already in use"),
    DUPLICATE_USERNAME("Username already in use"),

    // Channel
    CHANNEL_NOT_FOUND("Channel not found"),
    PRIVATE_CHANNEL_UPDATE("Private channel cannot be updated"),
    INSUFFICIENT_PARTICIPANTS("need more participants"),

    // Message
    MESSAGE_NOT_FOUND("Message not found"),

    // Binary
    BINARY_FILENAME_BLANK("fileName must not be blank"),
    BINARY_BYTES_EMPTY("bytes must not be empty"),
    BINARY_CONTENT_TYPE_BLANK("contentType must not be blank"),

    BINARY_CONTENT_NOT_FOUND("Binary content not found");



    private final String message;
    ErrorCode(String message) { this.message = message; }
    public String getMessage() { return message; }
    public String getCode() { return name(); }
    public String format(Object... args) { return String.format(message, args); }
}
