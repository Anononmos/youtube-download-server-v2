package com.example.YouTubeDL.exceptions.DownloaderExceptions;

public class VideoInfoException extends DownloaderException {
    private String url;
    private String format;

    public VideoInfoException(String url, String format) {
        super( 
            String.format( "Failed to extract from video with url [%s] and format [%s].", url, format ) 
        );

        this.url = url;
        this.format = format;
    }

    public VideoInfoException(VideoInfoException ex) {
        super(ex);

        this.url = ex.getUrl();
        this.format = ex.getFormat();
    }

    public String getUrl() {
        return url;
    }

    public String getFormat() {
        return format;
    }
}
