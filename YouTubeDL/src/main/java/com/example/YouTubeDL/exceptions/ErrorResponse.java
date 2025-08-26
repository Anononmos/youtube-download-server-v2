package com.example.YouTubeDL.exceptions;

public class ErrorResponse {
    public enum ErrorType {
        Validation, Video, Audio, Info, Updater, Server, Query, Migration
    }
    
    private final ErrorType type;
    private final String message;

    public ErrorResponse(ErrorType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
