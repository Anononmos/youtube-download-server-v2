package com.example.YouTubeDL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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


    public Video(VideoParams params, VideoJson json, String filePath) throws SQLException, IOException {
        this(
            params.type(), 
            json.id(),
            json.title(), 
            json.channel(), 
            json.channelID(), 
            json.duration(), 
            params.res(), 
            json.uploaded(), 
            getCreationDate(filePath), 
            filePath
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

    private static LocalDateTime getCreationDate(String file_path) throws IOException {
        File file = new File(file_path);
        
        if ( !file.exists() || file.isDirectory() ) {
            throw new FileNotFoundException(String.format("Error: File with path [%s] does not exist.", file_path));
        }

        FileTime fileTime = (FileTime) Files.getAttribute(file.toPath(), "creationTime");

        return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
    }
}
