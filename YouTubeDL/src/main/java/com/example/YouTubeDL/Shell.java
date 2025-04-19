package com.example.YouTubeDL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Shell {

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

    private static void printError(InputStream errorStream, String message, Exception err) throws Exception {
        System.out.println(message);

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(errorStream)
            )
        ) {
            if (reader.ready()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                // Option to only log errors
                if (err != null) {
                    throw (err);
                }
            }

            System.out.println("Process completed with no errors.");
        }
    }
    
    public static void updateYTdlp() throws Exception {
        String[] cmd = "pip install \"yt-dlp[default]\"".split(" ");

        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();

        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        printStream(stdout, "Updating yt-dlp");
        printError(stderr, "Error updating yt-dlp", null);
    }

    public static void download() throws Exception {
        
    }
}
