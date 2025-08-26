package com.example.YouTubeDL;

import com.example.YouTubeDL.validation.YouTubeURLValidation.YouTubeURLValidation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UploadRequest {

    @NotNull(message = "YouTube URL is missing from request.")
    @NotBlank(message = "YouTube URL provided is blank.")
    @YouTubeURLValidation
    public final String url;

    @NotNull(message = "Filename is missing from request.")
    @NotBlank(message = "Filename provided is blank.")
    public final String filename;

    public UploadRequest(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }
}
