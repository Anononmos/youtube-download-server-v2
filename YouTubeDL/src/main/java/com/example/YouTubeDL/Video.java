package com.example.YouTubeDL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import org.postgresql.util.PGInterval;

public record Video(
    DownloadType media, 
    String id, 
    String title, 
    String channel, 
    String channelId,
    
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
            json.duration(), 
            params.res(), 
            json.uploaded(), 
            LocalDateTime.now(), 
            json.filename() 
            );
    }


    public Video(VideoParams params, VideoJson json, String file_path) throws SQLException, IOException {
        this(
            params.type(), 
            json.id(),
            json.title(), 
            json.channel(), 
            json.channelId(), 
            json.duration(), 
            params.res(), 
            json.uploaded(), 
            getCreationDate(file_path), 
            file_path
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


    public Object[] toObjectArray() {
        return new Object[] { media, id, title, channel, channelId, duration, resolution, file_path };
    }


    public String getParamsTemplate() {
        Object[] values = toObjectArray();
        String[] params = new String[values.length];

        Arrays.fill(params, "?");        

        return String.join(", ", params);
    }
}
