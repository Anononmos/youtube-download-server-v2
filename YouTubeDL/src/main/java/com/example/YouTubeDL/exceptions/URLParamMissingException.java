package com.example.YouTubeDL.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="400 Param \"url\" is not included in request.")
public class URLParamMissingException extends Exception {
    public URLParamMissingException(String message) {
        super(message);
    }
}
