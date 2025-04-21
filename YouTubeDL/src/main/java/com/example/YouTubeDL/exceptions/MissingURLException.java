package com.example.YouTubeDL.exceptions;

import java.util.List;

public class MissingURLException {
    private List<String> errors;

    public MissingURLException(List<String> errors) {
        this.errors = errors;
    }
}
