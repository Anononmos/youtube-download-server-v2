package com.example.YouTubeDL.shells;

import java.util.concurrent.CompletableFuture;

import com.example.YouTubeDL.exceptions.DownloaderExceptions.DownloaderException;

public class ShellOutput<R, E extends DownloaderException> {
    public final CompletableFuture<R> result;
    public final CompletableFuture<E> error;

    public ShellOutput(CompletableFuture<R> result, CompletableFuture<E> error) {
        this.result = result;
        this.error = error;
    }

    public boolean isDone() {
        return result.isDone() && error.isDone();
    }
}
