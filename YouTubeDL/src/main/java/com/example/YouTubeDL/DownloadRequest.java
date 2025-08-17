package com.example.YouTubeDL;

import java.util.List;

import com.example.YouTubeDL.downloadOptions.DownloadType;
import com.example.YouTubeDL.validation.YouTubeURLValidation.YouTubeURLValidation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DownloadRequest {
    
    // @Value("#{'${application.download.resolutions}'.split(', ')}")
    // private List<Integer> resolutions;

    @NotNull(message = "Download media type is missing from request.")
    public final String type;

    @NotNull(message = "YouTube URL is missing from request.")
    @NotBlank(message = "YouTube URL provided is blank.")
    @YouTubeURLValidation()
    public final String url; 

    public final Integer res;

    /**
     * 
     * @param type
     * @param url
     * @param res
     */
    public DownloadRequest(String type, String url, Integer res) {
        this.type = type;
        this.url = url;
        this.res = res;
    }

    public VideoParams toVideoParams(List<Integer> resolutions) {
        DownloadType media = switch( type.toLowerCase() ) {
            case "video" -> {
                yield DownloadType.Video;
            }
            case "audio" -> {
                yield DownloadType.Audio;
            }
            default -> {
                yield DownloadType.Video;
            }
        };

        Integer resolution = ( resolutions.contains(res) || (media == DownloadType.Audio && res == null) ) ? res : 1080;

        return new VideoParams(media, url, resolution);
    }
}