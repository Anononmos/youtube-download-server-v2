package com.example.YouTubeDL.exceptions;

import java.util.LinkedList;
import java.util.List;

public class ErrorResponse {
    public final int status;
    public final String reason;
    private List<String> errors;

    public ErrorResponse(int status, String reason) {
        this.status = status;
        this.reason = reason;
        this.errors = new LinkedList<>();
    }

    public void addError(String error) {
        errors.add(error);
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
