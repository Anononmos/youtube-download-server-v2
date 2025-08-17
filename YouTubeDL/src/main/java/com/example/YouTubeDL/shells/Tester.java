package com.example.YouTubeDL.shells;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.text.StringEscapeUtils;
import com.example.YouTubeDL.DataFormatter;
import com.example.YouTubeDL.ProgressDataFormatter;
import com.example.YouTubeDL.downloadOptions.DownloadRate;
import com.example.YouTubeDL.exceptions.InternalServerException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.AudioDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.DownloaderException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;

public class Tester {
    
    private final String inDirectory;
    private final String outDirectory;
    private DownloadRate rate;

    private DataFormatter progressFormatter;

    @FunctionalInterface
    public interface LineHandler {
        void handle(String line, Integer lineIndex);
    }

    public Tester(String inDirectory, String outDirectory, DownloadRate rate) {
        this.inDirectory = inDirectory;
        this.outDirectory = outDirectory;

        this.rate = rate;
        this.progressFormatter = new ProgressDataFormatter();
    }


    private CompletableFuture<Void> handleShellOutput(InputStream stdout, LineHandler onMessage) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            
            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stdout)
                )
            ) {
                String line, cleanedLine;
                Integer i = 0;

                while ((line = reader.readLine()) != null) {
                    System.out.println( StringEscapeUtils.escapeJava(line) );

                    cleanedLine = line.replaceAll("\\p{Cc}\\K[", "");
                    onMessage.handle(cleanedLine, i);

                    i++;
                }
            }
            catch (IOException e) {
                e.printStackTrace();

                throw new RuntimeException(e);
            }

            return null;

        }, service);

        return future;
    }


    private <E extends DownloaderException> CompletableFuture<E> handleShellError(InputStream stderr, Class<E> cls, Object ...params) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        CompletableFuture<E> future = CompletableFuture.supplyAsync( () -> {
            Class<?> paramTypes[] = new Class[params.length];

            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = params[i].getClass();
            }

            E exception;

            try {
                exception = cls.getConstructor(paramTypes).newInstance(params);
            } 
            catch (Exception e) {
                e.printStackTrace();

                throw new IllegalArgumentException(e);
            }

            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stderr)
                )
            ) {
                String line;

                while ( (line = reader.readLine()) != null ) {
                    System.err.println( StringEscapeUtils.escapeJava(line) );

                    if ( line.contains("ERROR: ") ) {
                        exception.addError(line);
                    }

                    else if ( line.contains("WARNING: ") ) {
                        exception.addWarning(line);
                    }
                }
            } 
            catch (IOException e) {
                e.printStackTrace();

                throw new InternalServerException(e);
            }

            return exception;

        }, service).handle( (E result, Throwable ex) -> {
            if (ex != null) {
                throw new CompletionException(ex);
            }

            if ( result.hasError() ) {
                throw new CompletionException(result);
            }

            return result;
        });

        return future;
    }


    public ShellOutput<Void, VideoDownloadException> downloadVideo(final Integer res, LineHandler onMessage) {
        System.out.println("Testing video download.");

        final String format = progressFormatter.getFormat();
        final String serverUrl = "http://localhost:8000/test-video.webm";

        String[] serverCmd = "python -m http.server -d %s"
        .formatted(inDirectory)
        .split(" ");

        ProcessBuilder fileServerBuilder = new ProcessBuilder(serverCmd);
        Process fileServer;
        
        try {
            fileServer = fileServerBuilder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            VideoDownloadException exception = new VideoDownloadException(serverUrl, res);
            exception.addError("ERROR! Failed to start the test download server.");

            return new ShellOutput<>(
                CompletableFuture.completedFuture(null), 
                CompletableFuture.failedFuture(exception)
            );
        }

        System.out.println("Started test server with URL [%s] with PID [%d].".formatted( serverUrl, fileServer.pid() ));

        String[] cmd = "yt-dlp -q --progress --progress-template \"%s\" --no-color -P \"%s\" -S res:%d -r \"%s\" \"%s\" --windows-filenames"
        .formatted(format, outDirectory, res, rate.speed, serverUrl)
        .split(" ");
        
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process; 
        
        try {
            process = builder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            fileServer.destroy();

            VideoDownloadException exception = new VideoDownloadException(serverUrl, res);
            exception.addError("ERROR: Video download process from test server failed to start.");

            return new ShellOutput<>(
                CompletableFuture.completedFuture(null), 
                CompletableFuture.failedFuture(exception)
            );
        }

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        CompletableFuture<Void> futureOutput = handleShellOutput(stdout, onMessage);
        CompletableFuture<VideoDownloadException> futureError = handleShellError(stderr, VideoDownloadException.class, serverUrl, res);

        CompletableFuture.allOf(futureOutput, futureError)
        .whenCompleteAsync( (_, _) -> {
            fileServer.destroy();
        });

        return new ShellOutput<>(futureOutput, futureError);
    }


    public ShellOutput<Void, AudioDownloadException> downloadAudio(LineHandler onMessage) {
        System.out.println("Testing Audio Download.");

        final String format = progressFormatter.getFormat();
        final String serverUrl = "http://localhost:8000/test-video.webm";
        
        String[] serverCmd = "python -m http.server -d %s"
        .formatted(inDirectory)
        .split(" ");

        ProcessBuilder fileServerBuilder = new ProcessBuilder(serverCmd);
        Process fileServer; 
        
        try {
            fileServer = fileServerBuilder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            AudioDownloadException exception = new AudioDownloadException(serverUrl);
            exception.addError("ERROR: Failed to start the test download server.");

            return new ShellOutput<>(
                CompletableFuture.completedFuture(null), 
                CompletableFuture.failedFuture(exception)
            );
        }

        System.out.println("Started test server with URL [%s] with PID [%d].".formatted( serverUrl, fileServer.pid() ));

        String[] cmd = "yt-dlp -q --progress --progress-template \"%s\" --no-color -P \"%s\" -r \"%s\" -x \"%s\" --windows-filenames"
        .formatted(format, outDirectory, rate.speed, serverUrl)
        .split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process;

        try {
            process = builder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            AudioDownloadException exception = new AudioDownloadException(serverUrl);
            exception.addError("ERROR: Audio download process from test server failed to start.");

            return new ShellOutput<>(
                CompletableFuture.completedFuture(null), 
                CompletableFuture.failedFuture(exception)
            );
        }

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        CompletableFuture<Void> futureOutput = handleShellOutput(stdout, onMessage);
        CompletableFuture<AudioDownloadException> futureError = handleShellError(stderr, AudioDownloadException.class, serverUrl);

        CompletableFuture.allOf(futureOutput, futureError)
        .whenComplete( (_, _) -> {
            fileServer.destroy();
        });

        return new ShellOutput<>(futureOutput, futureError);
    }
}
