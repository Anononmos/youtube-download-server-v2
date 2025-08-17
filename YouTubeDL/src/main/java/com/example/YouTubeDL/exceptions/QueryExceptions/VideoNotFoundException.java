package com.example.YouTubeDL.exceptions.QueryExceptions;

public class VideoNotFoundException extends Exception {
    private String url;

    public VideoNotFoundException(String url) {
        super( "ERROR: Video with url [%s] does not exist.".formatted(url) );
        
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
