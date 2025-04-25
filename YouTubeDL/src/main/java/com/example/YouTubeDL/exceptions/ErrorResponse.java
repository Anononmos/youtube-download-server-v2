package com.example.YouTubeDL.exceptions;

import java.util.LinkedList;
import java.util.List;

public class ErrorResponse {

    private int statusCode;
    private List<String> fields;
    private List<String> errors;

    public ErrorResponse(int code) {
        statusCode = code;
        fields = new LinkedList<>();
        errors = new LinkedList<>();
    }

    public void addError(String message) {
        errors.add(message);
    }

    public void addField(String field) {

        if ( !fields.contains(field) ) {
            fields.add(field);
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getErrors() {
        return errors;
    }
}
