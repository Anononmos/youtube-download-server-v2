package com.example.YouTubeDL.exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.YouTubeDL.exceptions.DownloaderExceptions.AudioDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.UpdaterException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Input validation error.");

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            response.addError( error.getDefaultMessage() );
        }

        return new ResponseEntity<ErrorResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to download video.")
    @ExceptionHandler(VideoDownloadException.class)
    public void handle(VideoDownloadException e) {
        e.printErrors();
        e.printWarnings();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to download audio.")
    @ExceptionHandler(AudioDownloadException.class)
    public void handle(AudioDownloadException e) {
        e.printErrors();
        e.printWarnings();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to extract YouTube video's information.")
    @ExceptionHandler(VideoInfoException.class)
    public void handle(VideoInfoException e) {
        e.printErrors();
        e.printWarnings();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to update yt-dlp.")
    @ExceptionHandler(UpdaterException.class)
    public void handle(UpdaterException e) {
        e.printErrors();
        e.printWarnings();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal server error.")
    @ExceptionHandler(InternalServerException.class)
    public void handle(InternalServerException e) {
        
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Encountered IO error.")
    @ExceptionHandler(IOException.class)
    public void handle(IOException e) {
        e.printStackTrace();
    }
}
