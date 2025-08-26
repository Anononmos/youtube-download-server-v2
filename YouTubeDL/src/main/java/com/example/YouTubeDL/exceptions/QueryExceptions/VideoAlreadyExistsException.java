package com.example.YouTubeDL.exceptions.QueryExceptions;

public class VideoAlreadyExistsException extends RuntimeException {
    final String url;

    public VideoAlreadyExistsException(final String url) {
        super( "ERROR: Video with URL [%s] already exists in the database.".formatted(url) );

        this.url = url;
    }
}
