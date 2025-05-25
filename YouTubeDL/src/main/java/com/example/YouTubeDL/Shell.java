package com.example.YouTubeDL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;

import com.example.YouTubeDL.exceptions.DownloaderException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Shell {

    /**
     * 
     * @param inputStream
     * @param message
     * @throws IOException
     */
    private static String printStream(InputStream inputStream, String message) throws IOException {
        System.out.println(message);

        String body = "";

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream)
            )
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                body += line + '\n';
            }
        }

        return body;
    }

    /**
     * 
     * @param errorStream
     * @param message
     * @param err
     * @throws Exception
     */
    private static String printError(InputStream errorStream, String message, DownloaderException ex) throws DownloaderException, IOException {
        System.err.println(message);

        String errors = "";
        String warnings = "";

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(errorStream)
            )
        ) {
            if (reader.ready()) {
                String line;

                while ((line = reader.readLine()) != null) {
                    System.err.println(line);

                    if ( line.startsWith("WARNING:") ) {
                        warnings += line + '\n';
                    }

                    if ( line.startsWith("ERROR:") ) {
                        errors += line + '\n';
                    }
                }
            }

            if ( !errors.isBlank() && ex != null) {
                ex.setErrors(errors);

                throw ex;
            }

            System.err.println("Process completed with no errors.");

            return errors + "\n\n" + warnings;
        }
    }

    /**
     * 
     * @param url
     */
    public static void downloadAudio(String url, String directory) throws Exception, IOException {
        String[] cmd = String.format("yt-dlp -P \"%s\" -x \"%s\"", directory, url).split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        printStream(stdout, String.format("", url) );
        printStream(stderr, "Error downloading audio.");
    } 
    
    /**
     * 
     * @throws Exception
     */
    public static void updateYTdlp() throws Exception, IOException {
        String[] cmd = "pip install \"yt-dlp[default]\"".split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        printStream(stdout, "Updating yt-dlp");
        printError(stderr, "Error updating yt-dlp", null);
    }

    /**
     * 
     * @param url
     * @return
     */
    public static VideoJson getVideoInfo(VideoParams params, DataFormatter formatter) throws Exception, IOException {
        String url = params.url();
        String format = formatter.getFormat();

        String[] cmd = String.format("yt-dlp --print \"%s\" \"%s\"", format, url).split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        String output = printStream( stdout, String.format("Getting video info with the following format: \n%s", format) );
        String errors = printError( stderr, "Errors when accessing video info.", null);

        ObjectMapper mapper = new ObjectMapper();
        VideoJson json = mapper.readValue(output, VideoJson.class);

        return json;
    }
}
