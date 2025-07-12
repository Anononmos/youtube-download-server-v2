package com.example.YouTubeDL;

import java.io.IOException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoDownloadException;
import com.example.YouTubeDL.exceptions.DownloaderExceptions.VideoInfoException;

public interface VideoService {

    @Transactional
    public int createVideo(Video video);

    public int createVideoFromFile(VideoParams params, String filename) throws IOException, VideoInfoException;

    public int deleteVideo(String videoID);

    public int deleteVideoAndFile(String videoID);

    public Video download(VideoParams params, SseEmitter emitter) throws VideoDownloadException, VideoInfoException;

    public void testDownload(DownloadType type, Integer res, SseEmitter emitter);

    public void emitProgressData(SseEmitter emitter, String progressString);
}
