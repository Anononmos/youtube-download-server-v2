package com.example.YouTubeDL;

public class VideoDataFormatter implements DataFormatter {
    private String[] fields = {
        "id", 
        "title", 
        "channel", 
        "channel_id", 
        "webpage_url", 
        "channel_url", 
        "upload_date", 
        "duration", 
        "ext"
    };

    public VideoDataFormatter() {
    }

    public String getFormat() {
        String objectTraversal = String.join(",", fields);

        return String.format("%%(.{%s})j", objectTraversal);
    }
}
