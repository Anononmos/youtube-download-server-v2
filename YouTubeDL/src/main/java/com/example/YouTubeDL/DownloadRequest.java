package com.example.YouTubeDL;

import com.example.YouTubeDL.YouTubeURLValidation.DownloadValidationErrorType;
import com.example.YouTubeDL.YouTubeURLValidation.YouTubeURLValidation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DownloadRequest {

    @NotNull(
        message = "Parameter \"url\" is missing from request.", 
        payload = DownloadValidationErrorType.MissingURL.class
    )
    @NotBlank(
        message = "Parameter \"url\" is missing from request.",
        payload = DownloadValidationErrorType.MissingURL.class
    )
    @YouTubeURLValidation(payload = DownloadValidationErrorType.InvalidURL.class)
    private String url; 
    private Integer res;

    public DownloadRequest(String url, Integer res) {
        this.url = url;
        this.res = (res != null) ? res : 1080;  // Default resolution set to 1080p.
    }

    public String getUrl() {
        return url;
    }

    public Integer getResolution() {
        return res;
    }
}