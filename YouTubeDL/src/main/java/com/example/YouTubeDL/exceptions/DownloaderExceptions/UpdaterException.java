package com.example.YouTubeDL.exceptions.DownloaderExceptions;

public class UpdaterException extends DownloaderException {

    public UpdaterException() {
        super("Failed to update yt-dlp.");
    }

    public UpdaterException(UpdaterException ex) {
        super(ex);
    }
}
