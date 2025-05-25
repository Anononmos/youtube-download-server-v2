package com.example.YouTubeDL;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.postgresql.util.PGInterval;

public record Video(
    DownloadType media, 
    String id, 
    String title, 
    String channel, 
    String channelId,

    // 
    String url, 
    String channelUrl, 
    
    // 
    PGInterval duration, 
    Integer resolution, 
    LocalDate uploaded, 
    LocalDateTime downloaded, 
    String file_path
) {
    public Video(VideoParams params, VideoJson json) throws SQLException {


        this( 
            params.type(), 
            json.id(), 
            json.title(), 
            json.channel(), 
            json.channelId(), 
            json.url(), 
            json.channelUrl(), 
            json.duration(), 
            params.res(), 
            json.uploaded(), 
            LocalDateTime.now(), 
            "file_path" );
    }
}
