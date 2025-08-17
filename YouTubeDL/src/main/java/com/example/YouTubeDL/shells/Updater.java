package com.example.YouTubeDL.shells;

import java.io.IOException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.UpdaterException;

public class Updater {

    public void updateYTdlp() throws UpdaterException {
        String[] cmd = "pip install --upgrade yt-dlp".split(" ");

        System.out.println("Updating yt-dlp.");
        
        ProcessBuilder builder = new ProcessBuilder(cmd).inheritIO();
        Process process;
        
        try {
            process = builder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                UpdaterException exception = new UpdaterException();
                exception.addError("ERROR: Update process returned error.");

                throw exception;
            }
        }
        catch (IOException e) {
            e.printStackTrace();

            UpdaterException exception = new UpdaterException();
            exception.addError("ERROR: Update process failed to start.");

            throw exception;
        }
        catch (InterruptedException e) {
            e.printStackTrace();

            UpdaterException exception = new UpdaterException();
            exception.addError("ERROR: Update process was interrupted.");

            throw exception;
        } 
        catch (UpdaterException e) {
            throw e;
        }
    }
}
