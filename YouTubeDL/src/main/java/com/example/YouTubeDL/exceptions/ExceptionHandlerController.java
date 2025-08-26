package com.example.YouTubeDL.exceptions;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.YouTubeDL.exceptions.ErrorResponse.ErrorType;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.AudioDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.UpdaterException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;
import com.example.YouTubeDL.exceptions.FileExceptions.InvalidFileException;
import com.example.YouTubeDL.exceptions.QueryExceptions.VideoAlreadyExistsException;
import com.example.YouTubeDL.exceptions.QueryExceptions.VideoNotFoundException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity< List<ErrorResponse> > handle(MethodArgumentNotValidException e) {
        List<ErrorResponse> errors = new LinkedList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            ErrorResponse response = new ErrorResponse(ErrorType.Validation, error.getDefaultMessage());
            
            errors.add(response);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VideoDownloadException.class)
    public ResponseEntity<ErrorResponse> handle(VideoDownloadException e) {
        e.printErrors();
        e.printWarnings();

        ErrorResponse response = new ErrorResponse(ErrorType.Video, e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AudioDownloadException.class)
    public ResponseEntity<ErrorResponse> handle(AudioDownloadException e) {
        e.printErrors();
        e.printWarnings();

        ErrorResponse response = new ErrorResponse(ErrorType.Audio, e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VideoInfoException.class)
    public ResponseEntity<ErrorResponse> handle(VideoInfoException e) {
        e.printErrors();
        e.printWarnings();

        ErrorResponse response = new ErrorResponse(ErrorType.Info, e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UpdaterException.class)
    public ResponseEntity<ErrorResponse> handle(UpdaterException e) {
        e.printErrors();
        e.printWarnings();

        ErrorResponse response = new ErrorResponse(ErrorType.Updater, e.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handle(InternalServerException e) {
        e.printStackTrace();

        return ResponseEntity.internalServerError().body( new ErrorResponse(ErrorType.Server, "Internal server error.") );
    }

    @ExceptionHandler(VideoAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(VideoAlreadyExistsException e) {
        e.printStackTrace();

        return ResponseEntity.internalServerError().body( new ErrorResponse(ErrorType.Query, e.getMessage()) );
    }

    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(VideoNotFoundException e) {
        e.printStackTrace();

        return ResponseEntity.internalServerError().body( new ErrorResponse(ErrorType.Query, e.getMessage()) );
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidFileException e) {
        e.printStackTrace();

        ErrorResponse response = new ErrorResponse(ErrorType.Migration, e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(FileNotFoundException e) {
        e.printStackTrace();

        ErrorResponse response = new ErrorResponse(ErrorType.Migration, e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
