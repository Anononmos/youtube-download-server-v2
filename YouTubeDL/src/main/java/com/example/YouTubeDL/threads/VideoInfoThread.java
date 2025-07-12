package com.example.YouTubeDL.threads;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import com.example.YouTubeDL.Shell;
import com.example.YouTubeDL.Video;
import com.example.YouTubeDL.VideoDataFormatter;
import com.example.YouTubeDL.VideoJson;
import com.example.YouTubeDL.VideoParams;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;

public class VideoInfoThread implements Callable<Video> {
    
    private VideoParams params;

    public VideoInfoThread(VideoParams params) {
        this.params = params;
    }

    @Override
    public Video call() throws VideoInfoException, IOException, SQLException {
        VideoDataFormatter formatter = new VideoDataFormatter();

        VideoJson json = Shell.getVideoInfo(params, formatter);

        return new Video(params, json);
    }
}
