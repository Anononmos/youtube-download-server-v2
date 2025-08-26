package com.example.YouTubeDL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.postgresql.util.PGInterval;
import com.example.YouTubeDL.downloadOptions.DownloadType;

public record Video(
    DownloadType media, 
    String id, 
    String title, 
    String channel, 
    String channelID,
    
    // 
    PGInterval duration, 
    Integer resolution, 
    LocalDate uploaded, 
    LocalDateTime downloaded, 
    String filePath
) {
    public Video(VideoParams params, VideoJson json) throws SQLException {

        this( 
            params.type(), 
            json.id(), 
            json.title(), 
            json.channel(), 
            json.channelID(),  
            json.duration(), 
            params.res(), 
            json.uploaded(), 
            LocalDateTime.now(), 
            json.filename() 
        );
    }

    public Video(VideoParams params, VideoJson json, LocalDateTime downloaded, String filename) throws SQLException {
        this(
            params.type(), 
            json.id(), 
            json.title(), 
            json.channel(), 
            json.channelID(), 
            json.duration(), 
            params.res(), 
            json.uploaded(), 
            downloaded, 
            filename
        );
    }

    public Video(ResultSet rs) throws SQLException {
        this(
            DownloadType.valueOf( rs.getString("media") ), 
            rs.getString("id"), 
            rs.getString("title"), 
            rs.getString("channel_name"), 
            rs.getString("channel"), 
            (PGInterval) rs.getObject("duration"), 
            rs.getInt("resolution"), 
            rs.getDate("uploaded").toLocalDate(), 
            rs.getTimestamp("downloaded").toLocalDateTime(), 
            rs.getString("file_path")
        );
    }
}
