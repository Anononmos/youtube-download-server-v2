package com.example.YouTubeDL;

import com.example.YouTubeDL.validation.YouTubeURLValidation.YouTubeURLValidation;
import com.example.YouTubeDL.validation.resolutionValidation.ResolutionValidation;
import com.example.YouTubeDL.validation.typeValidation.TypeValidation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DownloadRequest {

    @TypeValidation()
    private String type;

    @NotNull(message = "YouTube URL is missing from request.")
    @NotBlank(message = "YouTube URL provided is blank.")
    @YouTubeURLValidation()
    private String url; 

    @ResolutionValidation()
    private Integer res;

    public DownloadRequest(String type, String url, Integer res) {
        this.url = url;
        this.res = (res != null) ? res : 1080;          // Default resolution set to 1080p.
        this.type = (type != null) ? type : "video";    // Default value is video
    }

    public String getUrl() {
        return url;
    }

    public Integer getResolution() {
        return res;
    }

    public String getType() {
        return type;
    }
}