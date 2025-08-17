package com.example.YouTubeDL;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class ProgressJson {

    @JsonProperty("status")
    private String status;

    private Double percent;

    @JsonSetter("_downloaded_bytes_str")
    private String downloadedBytes;

    @JsonSetter("_total_bytes_str")
    private String totalBytes;

    @JsonSetter("_total_bytes_estimate_str")
    private String estimatedBytes;

    @JsonSetter("fragment_index")
    @JsonFormat(shape = Shape.NUMBER_INT)
    private Integer fragment;

    @JsonSetter("fragment_count")
    @JsonFormat(shape = Shape.NUMBER_INT)
    private Integer numFragments;

    @JsonSetter("_speed_str")
    private String speed;

    @JsonSetter("_elapsed_str")
    private String elapsed;

    @JsonSetter("_eta_str")
    private String eta;

    // Setters

    @JsonSetter("_percent_str")
    private void setPercent(String percentStr) {
        this.percent = Optional.ofNullable(percentStr)
                .map(str -> str.replaceAll("%", ""))
                .map(str -> Double.parseDouble(str) / 100)
                .orElse(0.0);
    }

    @JsonIgnore()
    public Boolean isFinished() {
        return status.equals("finished");
    }

    @JsonIgnore()
    public Boolean isDownloading() {
        return status.equals("downloading");
    }

    // Getters

    public String status() {
        return this.status;
    }

    @JsonGetter("percent")
    public Double percent() {
        return this.percent;
    }

    @JsonGetter("downloaded_bytes")
    public String downloadedBytes() {
        return this.downloadedBytes;
    }

    @JsonGetter("total_bytes")
    public String totalBytes() {
        return this.totalBytes;
    }

    @JsonGetter("estimated_bytes")
    public String estimatedBytes() {
        return this.estimatedBytes;
    }

    @JsonGetter("fragment_index")
    public Integer fragment() {
        return this.fragment;
    }

    @JsonGetter("fragment_count")
    public Integer numFragments() {
        return this.numFragments;
    }
    
    @JsonGetter("speed")
    public String speed() {
        return this.speed;
    }

    @JsonGetter("elapsed")
    public String elapsed() {
        return this.elapsed;
    }

    @JsonGetter("eta")
    public String eta() {
        return this.eta;
    }
}
