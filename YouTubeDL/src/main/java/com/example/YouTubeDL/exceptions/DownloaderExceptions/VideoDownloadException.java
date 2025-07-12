package com.example.YouTubeDL.exceptions.DownloaderExceptions;

public class VideoDownloadException extends DownloaderException {

    private String url;
    private Integer res;

    public VideoDownloadException(String url, Integer res) {
        super( String.format("Failed to download video from url [%s], with resolution [%d].", url, res) );

        this.url = url;
        this.res = res;
    }

    public VideoDownloadException(VideoDownloadException ex) {
        super(ex);

        this.url = ex.getUrl();
        this.res = ex.getRes();
    }

    public String getUrl() {
        return url;
    }

    public Integer getRes() {
        return res;
    }
}
