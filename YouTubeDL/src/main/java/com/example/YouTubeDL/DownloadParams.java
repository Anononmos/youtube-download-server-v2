package com.example.YouTubeDL;

import java.net.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DownloadParams {

    @NotNull(message="400 Query parameter \"url\" is not provided in request body.")
    @NotBlank(message="400 Query parameter \"url\" is not provided in request body.")
    @YouTubeURLValidation
    public String url; 

    public Integer res;

    public DownloadParams(String url, Integer res) {
        this.url = url;
        this.res = (res != null) ? res : 1080;  // Default resolution set to 1080p.
    }
}