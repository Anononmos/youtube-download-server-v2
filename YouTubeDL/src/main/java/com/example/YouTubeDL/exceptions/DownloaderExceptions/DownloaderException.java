package com.example.YouTubeDL.exceptions.DownloaderExceptions;

import java.util.LinkedList;
import java.util.List;

public class DownloaderException extends Exception {

    private String message;
    private List<String> errors;
    private List<String> warnings;

    protected DownloaderException(String message) {
        this.message = message;
        this.warnings = new LinkedList<>();
        this.errors = new LinkedList<>();
    }

    /**
     * Copy Constructor
     * @param ex
     */
    protected DownloaderException(DownloaderException ex) {
        super(ex);

        this.message = ex.message;
        this.errors = new LinkedList<>();
        this.warnings = new LinkedList<>();

        this.errors.addAll( ex.getErrors() );
        this.warnings.addAll( ex.getWarnings() );
    }

    public String getMessage() {
        return message;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public boolean hasWarning() {
        return !warnings.isEmpty();
    }

    public void printErrors() {
        errors.stream().forEach(System.err::println);
    }

    public void printWarnings() {
        warnings.stream().forEach(System.err::println);
    }
}
