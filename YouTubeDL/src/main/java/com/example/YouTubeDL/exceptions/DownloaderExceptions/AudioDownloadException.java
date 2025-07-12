package com.example.YouTubeDL.exceptions.DownloaderExceptions;

public class AudioDownloadException extends DownloaderException {

    private String url;

    public AudioDownloadException(String url) {
        super( String.format("Failed to download audio from URL [%s].", url) );

        this.url = url;
    }

    public AudioDownloadException(AudioDownloadException ex) {
        super(ex);

        this.url = ex.getUrl();
    }

    public String getUrl() {
        return url;
    }
}
