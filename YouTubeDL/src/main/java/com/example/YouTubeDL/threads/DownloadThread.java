package com.example.YouTubeDL.threads;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.example.YouTubeDL.Shell;
import com.example.YouTubeDL.VideoParams;
import com.example.YouTubeDL.Shell.LineHandler;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.AudioDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;

public class DownloadThread implements Callable<Void> {
    
    private VideoParams params;
    private String directory;
    private LineHandler handler;
    
    public DownloadThread(VideoParams params, String directory, LineHandler handler) {
        this.params = params;
        this.directory = directory;
        this.handler = handler;
    }

    @Override
    public Void call() throws VideoDownloadException, AudioDownloadException, IOException {

        String url = params.url();
        Integer res = params.res();

        switch ( params.type() ) {
            case Audio:
                Shell.downloadAudio(url, directory, handler);

                break;

            case Video:
                Shell.downloadVideo(url, res, directory, handler);

                break;
        }

        return null;
    }
}
