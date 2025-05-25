package com.example.YouTubeDL.exceptions;

import java.util.List;

public class DownloaderException extends Exception {
    private String[] errors;

    public DownloaderException() {

    }

    private DownloaderException(String errors) {
        this.errors = errors.split("\n");
    }

    public void setErrors(String errors) {
        this.errors = errors.split("\n");
    }

    public String[] getErrors() {
        return errors;
    }
}
