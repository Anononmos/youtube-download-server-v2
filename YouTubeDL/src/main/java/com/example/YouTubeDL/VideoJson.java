package com.example.YouTubeDL;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.postgresql.util.PGInterval;
import org.springframework.format.datetime.DateFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoJson {
    private static final String dateFormat = "yyyyMMdd";

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("webpage_url")
    private String url;

    @JsonProperty("channel_url")
    private String channelUrl;

    @JsonProperty("upload_date")
    private String uploaded;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("ext")
    private String ext;

    // Getters

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String channel() {
        return channel;
    }

    public String channelId() {
        return channelId;
    }

    public String url() {
        return url;
    }

    public String channelUrl() {
        return channelUrl;
    }

    public LocalDate uploaded() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(dateFormat);

        return LocalDate.parse(uploaded, format);
    }

    public PGInterval duration() throws SQLException {
        Integer seconds = duration % 60;
        Integer minutes = (duration / 60) % 60;
        Integer hours = duration / 3600;

        String date = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return new PGInterval(date);
    }

    public String ext() {
        return ext;
    }
}
