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
import com.example.YouTubeDL.Video;
import com.example.YouTubeDL.VideoDataFormatter;
import com.example.YouTubeDL.VideoJson;
import com.example.YouTubeDL.VideoParams;
import com.example.YouTubeDL.exceptions.InternalServerException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.AudioDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.DownloaderException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Downloader {

    private final VideoParams params;
    private final String directory;

    private DataFormatter progressFormatter;
    private DataFormatter videoInfoFormatter;

    @FunctionalInterface
    public interface LineHandler {
        void handle(String line, Integer index);
    }

    @FunctionalInterface
    public interface CompletionHandler<V> {
        V handle(String output);
    }

    /**
     * 
     * @param params
     * @param directory
     */
    public Downloader(final VideoParams params, final String directory) {
        this.params = params;
        this.directory = directory;

        this.progressFormatter = new ProgressDataFormatter();
        this.videoInfoFormatter = new VideoDataFormatter();
    }

    public String getProgressFormat() {
        return progressFormatter.getFormat();
    }

    public String getInfoFormat() {
        return videoInfoFormatter.getFormat();
    }
    
    /**
     * 
     * @param <V>
     * @param stdout
     * @param onComplete
     * @return
     */
    private <V> CompletableFuture<V> handleShellOutput(InputStream stdout, CompletionHandler<V> onComplete) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        CompletableFuture<V> future = CompletableFuture.supplyAsync(() -> {
            String lines = "";

            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stdout)
                )
            ) {
                String line;
                Integer i = 0;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);

                    lines += line + '\n';
                    i++;
                }
            }
            catch (IOException e) {
                e.printStackTrace();

                throw new InternalServerException(e);
            }

            return lines;

        }, service).handleAsync((String output, Throwable ex) -> {
            if (ex != null) {
                ex.printStackTrace();

                throw new InternalServerException(ex);
            }

            V value = onComplete.handle(output);

            return value;

        }, service);

        return future;
    }

    /**
     * 
     * @param stdout
     * @param onMessage
     * @return
     */
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

                    cleanedLine = line.replaceAll("\\p{Cc}\\[K", "");
                    onMessage.handle(cleanedLine, i);

                    i++;
                }
            }
            catch (IOException e) {
                e.printStackTrace();

                throw new InternalServerException(e);
            }

            return null;

        }, service);

        return future;
    }

    /**
     * 
     * @param <E>
     * @param stderr
     * @param cls
     * @param params
     * @return
     */
    private <E extends DownloaderException> CompletableFuture<E> handleShellError(InputStream stderr, Class<E> cls, Object ...params) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        CompletableFuture<E> future = CompletableFuture.supplyAsync(() -> {
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

        }, service).handleAsync((E result, Throwable ex) -> {

            if (ex != null) {
                throw new CompletionException(ex);
            }

            if ( result.hasError() ) {
                throw new CompletionException(ex);
            }

            return result;
        });

        return future;
    }

    /**
     * 
     * @param onMessage
     * @return
     * @throws IOException
     */
    public ShellOutput<Void, VideoDownloadException> downloadVideo(LineHandler onMessage) {
        String format = progressFormatter.getFormat();
        
        String url = params.url();
        Integer res = params.res();

        String[] cmd = "yt-dlp -q --progress --progress-template \"%s\" --no-color -P \"%s\" -S res:%d \"%s\" --windows-filenames"
        .formatted(format, directory, res, url)
        .split(" ");
        
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process;
        
        try {
            process = builder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            VideoDownloadException exception = new VideoDownloadException(url, res);

            exception.addError("ERROR: Video download process failed to start.");

            return new ShellOutput<>(
                CompletableFuture.completedFuture(null), 
                CompletableFuture.failedFuture(exception)
            );
        }

        System.out.println("Downloading video from URL [%s] in %dp.".formatted(url, res));

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        CompletableFuture<Void> futureOutput = handleShellOutput(stdout, onMessage);
        CompletableFuture<VideoDownloadException> futureError = handleShellError(stderr, VideoDownloadException.class, url, res);

        return new ShellOutput<>(futureOutput, futureError);
    }

    /**
     * 
     * @param onMessage
     * @return
     * @throws IOException
     */
    public ShellOutput<Void, AudioDownloadException> downloadAudio(LineHandler onMessage) {
        final String format = progressFormatter.getFormat();
        final String url = params.url();
        
        String[] cmd = "yt-dlp -q --progress --progress-template \"%s\" --no-color -P \"%s\" -x \"%s\" --windows-filenames"
        .formatted(format, directory, url)
        .split(" ");
        
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process;

        try {
            process = builder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            AudioDownloadException exception = new AudioDownloadException(url);
            exception.addError("ERROR: Audio download process failed to start.");

            return new ShellOutput<>(
                CompletableFuture.completedFuture(null),
                CompletableFuture.failedFuture(exception) 
            );
        }

        System.out.println("Downloading audio from URL [%s].".formatted(url));
        
        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        CompletableFuture<Void> futureOutput = handleShellOutput(stdout, onMessage);  
        CompletableFuture<AudioDownloadException> futureError = handleShellError(stderr, AudioDownloadException.class, url);

        return new ShellOutput<>(futureOutput, futureError);
    }

    /**
     * 
     * @return
     * @throws IOException
     */
    public ShellOutput<Video, VideoInfoException> getVideoInfo() {
        String format = videoInfoFormatter.getFormat(); 
        String url = params.url();

        String[] cmd = "yt-dlp --print \"%s\" --no-color \"%s\" --windows-filenames"
        .formatted(format, url)
        .split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process;

        try {
            process = builder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            VideoInfoException exception = new VideoInfoException(url, format);
            exception.addError("ERROR: Video information extraction process failed to start.");

            return new ShellOutput<>(
                CompletableFuture.completedFuture(null), 
                CompletableFuture.failedFuture(exception)
            );
        }

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        System.out.println("Retrieving the following information [%s] from URL [%s].".formatted(format, url));

        CompletableFuture<Video> futureVideo = handleShellOutput(stdout, (output) -> {
            ObjectMapper mapper = new ObjectMapper();

            try {
                VideoJson json = mapper.readValue(output, VideoJson.class);

                return new Video(params, json);
                
            } catch (Exception e) {
                e.printStackTrace();

                throw new IllegalArgumentException(e);
            }
        });

        CompletableFuture<VideoInfoException> futureError = handleShellError(stderr, VideoInfoException.class, url, format);

        return new ShellOutput<>(futureVideo, futureError);
    }
}
