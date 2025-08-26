package com.example.YouTubeDL;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.YouTubeDL.downloadOptions.DownloadRate;
import com.example.YouTubeDL.downloadOptions.DownloadType;
import com.example.YouTubeDL.exceptions.FileExceptions.InvalidFileException;
import com.example.YouTubeDL.exceptions.QueryExceptions.VideoNotFoundException;

public interface VideoService {

    @Transactional
    public Video createVideo(final Video video);

    @Transactional
    public Video createVideoFromFile(final String url, final String filename) throws FileNotFoundException, InvalidFileException;

    public Video getVideo(final String url) throws VideoNotFoundException;

    public List<Video> getVideos(final int limit, final int page);

    public Path getFile(final String url) throws VideoNotFoundException;

    public Channel getChannel(final String channelID);

    public String getIDFromURL(final String url) throws VideoNotFoundException;

    @Transactional
    public int deleteVideo(final String videoID) throws VideoNotFoundException;
    
    public int deleteVideoAndFile(String videoID);

    public void download(VideoParams params, SseEmitter emitter);

    public void testDownload(DownloadType type, DownloadRate rate, Integer res, SseEmitter emitter);

    public void emitProgressData(SseEmitter emitter, String line, Integer lineIndex);
}
