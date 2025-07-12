package com.example.YouTubeDL;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.YouTubeDL.exceptions.DownloaderExceptions.AudioDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.DownloaderException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.UpdaterException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Shell {

    public interface LineHandler {
        void handle(String line, int numLine);
    }

    public record TestParams(String inDirectory, String outDirectory, String rate) {}

    /**
     * 
     * @param stdout
     * @param handler
     * @return
     * @throws IOException
     */
    public static String handleShellOutput(InputStream stdout, LineHandler handler) throws IOException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        String output = "";

        Future<String> futureOutput = service.submit( () -> {
            String lines = "";

            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stdout)
                )
            ) {
                String line;
                Integer i = 0;

                while ( (line = reader.readLine()) != null ) {
                    handler.handle(line, i);

                    lines += line + '\n';
                    i++;
                }
            }
            catch (IOException e) {
                e.printStackTrace();

                throw e;
            }

            return lines;
        });

        try {
            output = futureOutput.get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();

            throw new IOException(e);
        }
        catch (InterruptedException e) {
            e.printStackTrace();

            throw new IOException(e);
        }
        finally {
            service.shutdown();
        }

        return output;
    }


    /**
     * Reads each line from stderr and throws custom exception.
     * @param <E>
     * @param stderr
     * @param cls
     * @param params
     * @throws IOException
     * @throws E
     */
    public static <E extends DownloaderException> void handleShellError(InputStream stderr, Class<E> cls, Object ...params) throws IOException, E {

        ExecutorService service = Executors.newSingleThreadExecutor();
        
        Future<E> futureException = service.submit( () -> {

            Class<?> paramTypes[] = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = params[i].getClass();
            }

            E exception = cls.getConstructor(paramTypes).newInstance(params);

            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stderr)
                )
            ) {
                String line;

                while ((line = reader.readLine()) != null) {

                    System.err.println(line);

                    if ( line.startsWith("Error:") ) {
                        exception.addError(line);
                    }

                    else if ( line.startsWith("Warning:") ) {
                        exception.addWarning(line);
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();

                throw e;
            }

            return exception;
        });

        E exception;

        try {
            exception = futureException.get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();

            throw new IOException(e);
        }
        catch (InterruptedException e) {
            e.printStackTrace();

            throw new IOException(e);
        }
        finally {
            service.shutdown();
        }

        if ( exception.hasError() ) {
            exception.printStackTrace();
            exception.printWarnings();
            exception.printErrors();

            throw exception;
        }
    }


    /**
     * 
     * @param params
     * @param res
     * @param handler
     * @throws IOException
     * @throws VideoDownloadException
     */
    public static boolean testDownloadVideo(TestParams params, Integer res, LineHandler handler) {
        // TODO: Run "python -m http.server -d" in test videos directory
        // Run yt-dlp off of localhost url

        System.out.println("Testing video download.");

        String progressFormat = new ProgressDataFormatter().getFormat();

        String inDirectory = params.inDirectory();
        String outDirectory = params.outDirectory();
        String rate = params.rate();

        String serverTemplate = "python -m http.server -d %s";
        String serverCmd[] = String.format(serverTemplate, inDirectory).split(" ");

        ProcessBuilder fileServerBuilder = new ProcessBuilder(serverCmd);
        Process fileServer;

        try {
            fileServer = fileServerBuilder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            return false; 
        }

        String serverUrl = "http://localhost:8000/test-video.webm";

        System.out.println( String.format("Started test server with url [%s] with PID [%d].", serverUrl, fileServer.pid()) );

        String cmdTemplate = "yt-dlp -q --progress --progress-template \"%s\" --no-color -P \"%s\" -S res:%d -r \"%s\" \"%s\" --windows-filenames";
        String cmd[] = String.format(cmdTemplate, progressFormat, outDirectory, res, rate, serverUrl).split(" ");

        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            Process process = builder.start();

            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            handleShellOutput(stdout, handler);
            handleShellError(stderr, VideoDownloadException.class, serverUrl, res);
           
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
        catch (VideoDownloadException e) {
            e.printStackTrace();
            e.printWarnings();
            e.printErrors();

            return false;
        }
        finally {
            // Kill fileserver process in both cases

            fileServer.destroy();
        }

        File dir = new File(outDirectory);
        File[] children = dir.listFiles();  
        
        if (children.length < 1) {
            System.err.println("Error: Test video file did not download.");

            return false;
        }

        for (File file : children) {
            if ( !file.isDirectory() ) {
                file.delete();
            }
        }

        return true;
    }


    /**
     * 
     * @param url
     * @param res
     * @param directory
     * @param handler
     * @throws IOException
     * @throws VideoDownloadException
     */
    public static void downloadVideo(String url, Integer res, String directory, LineHandler handler) throws IOException, VideoDownloadException {
        String progressFormat = new ProgressDataFormatter().getFormat();

        String cmdTemplate = "yt-dlp -q --progress --progress-template \"%s\" --no-color -P \"%s\" -S res:%d \"%s\" --windows-filenames";
        String cmd[] = String.format(cmdTemplate, progressFormat, directory, res, url).split(" ");

        System.out.println("CMD: " + Arrays.toString(cmd));

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        // Read standard output and emit each line. When the process ends, check the error stream for errors
        
        System.out.println( String.format("Downloading video from url [%s] with resolution [%d].", url, res) );

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        handleShellOutput(stdout, handler);
        handleShellError(stderr, VideoDownloadException.class, url, res);

        return;
    }

    
    public static boolean testDownloadAudio(TestParams params, LineHandler handler) {
        // TODO: Run `python -m http.server $PORT` in directory containing test videos
        // Run download command on localhost url

        System.out.println("Testing audio download.");

        String progressFormat = new ProgressDataFormatter().getFormat();

        String inDirectory = params.inDirectory();
        String outDirectory = params.outDirectory();
        String rate = params.rate();

        String serverTemplate = "python -m http.server -d %s";
        String serverCmd[] = String.format(serverTemplate, inDirectory).split(" ");

        ProcessBuilder fileServerBuilder = new ProcessBuilder(serverCmd);
        Process fileServer;

        try {
            fileServer = fileServerBuilder.start();
        }
        catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        String serverUrl = "http://localhost:8000/test-video.webm";

        System.out.println( String.format("Started test server with url [%s] with PID [%d].", serverUrl, fileServer.pid()) );

        String cmdTemplate = "yt-dlp -q --progress --progress-template \"%s\" --no-color -P \"%s\" -r \"%s\" -x \"%s\" --windows-filenames";
        String cmd[] = String.format(cmdTemplate, progressFormat, outDirectory, rate, serverUrl).split(" ");

        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            Process process = builder.start();

            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            handleShellOutput(stdout, handler);
            handleShellError(stderr, AudioDownloadException.class, serverUrl);
           
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
        catch (AudioDownloadException e) {
            e.printStackTrace();
            e.printWarnings();
            e.printErrors();

            return false;
        }
        finally {
            // Kill fileserver process in both cases

            fileServer.destroy();
        }

        File dir = new File(outDirectory);
        File[] children = dir.listFiles();

        if (children.length < 1) {
            System.err.println("Error: Test audio file failed to download.");

            return false;
        }

        for (File file : children) {
            if ( !file.isDirectory() ) {
                file.delete();
            }
        }

        return true;
    }

    
    /**
     * 
     * @param url
     * @param directory
     * @param handler
     * @throws IOException
     * @throws AudioDownloadException
     */
    public static void downloadAudio(String url, String directory, LineHandler handler) throws IOException, AudioDownloadException {
        String progressFormat = new ProgressDataFormatter().getFormat();

        String cmdTemplate = "yt-dlp -q --progress --progress-template \"%s\" --no-color -P \"%s\" -x \"%s\" --windows-filenames";
        String[] cmd = String.format(cmdTemplate, progressFormat, directory, url).split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        System.out.println( String.format("Downloading audio from url [%s].", url) );

        handleShellOutput(stdout, handler);
        handleShellError(stderr, AudioDownloadException.class, url);

        return;
    } 
    
    
    /**
     * 
     * @throws IOException
     * @throws UpdaterException
     */
    public static void updateYTdlp() throws IOException, UpdaterException {
        String[] cmd = "pip install \"yt-dlp[default]\"".split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        System.out.println("Updating yt-dlp.");

        handleShellOutput(stdout, (line, _) -> { System.out.println(line); });
        handleShellError(stderr, UpdaterException.class);
    }

    /**
     * 
     * @param params
     * @param formatter
     * @return
     * @throws VideoInfoException
     * @throws IOException
     */
    public static VideoJson getVideoInfo(VideoParams params, DataFormatter formatter) throws IOException, VideoInfoException {
        String url = params.url();
        String format = formatter.getFormat();

        String[] cmd = String.format("yt-dlp --print \"%s\" \"%s\"", format, url).split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        System.out.println( String.format("Getting information from url [%s] and format [%s].", url, format) );

        String output = handleShellOutput(stdout, (line, _) -> { System.out.println(line); });

        handleShellError(stderr, VideoInfoException.class, url, format);

        ObjectMapper mapper = new ObjectMapper();
        VideoJson json = mapper.readValue(output, VideoJson.class);

        return json;
    }
}
