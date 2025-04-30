package com.example.YouTubeDL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;

public final class Shell {

    /**
     * 
     * @param inputStream
     * @param message
     * @throws IOException
     */
    private static void printStream(InputStream inputStream, String message) throws IOException {
        System.out.println(message);

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream)
            )
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    /**
     * 
     * @param errorStream
     * @param message
     * @param err
     * @throws Exception
     */
    private static void printError(InputStream errorStream, String message, Exception err) throws Exception {
        System.err.println(message);

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(errorStream)
            )
        ) {
            if (reader.ready()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }

                // Option to only log errors
                if (err != null) {
                    throw (err);
                }
            }

            System.err.println("Process completed with no errors.");
        }
    }

    /**
     * 
     * @param url
     */
    private static void downloadAudio(String url) {
        
    } 
    
    /**
     * 
     * @throws Exception
     */
    public static void updateYTdlp() throws Exception {
        String[] cmd = "pip install \"yt-dlp[default]\"".split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        printStream(stdout, "Updating yt-dlp");
        printError(stderr, "Error updating yt-dlp", null);
    }

    public static void download(String url, Integer resolution, DownloadType type, String directory) throws Exception {
        switch (type) {
            case DownloadType.Audio:
                downloadAudio(url);

                break;
        
            case DownloadType.Video:
                break;
        }
    }
}
