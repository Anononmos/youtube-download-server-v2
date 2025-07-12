package com.example.YouTubeDL;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProgressJson {

    @JsonProperty("status")
    private String status;

    private Double percent;

    private String downloadedBytes;
    private String totalBytes;
    private String estimatedBytes;
    
    private Integer fragment;

    @JsonProperty("fragment_count")
    private Integer numFragments;

    private String speed;
    private String elapsed;
    private String eta;

    @JsonProperty("_percent_str")
    public void setPercent(String percentString) {
        // Remove last character

        String number = percentString.replaceFirst(".$", "").trim();
        this.percent = Double.parseDouble(number) / 100;
    }

    @JsonProperty("percent")
    public Double getPercent() {
        return this.percent;
    }

    @JsonProperty("_downloaded_bytes_str")
    public void setDownloadedBytes(String downloadedBytes) {
        this.downloadedBytes = downloadedBytes.trim();
    }

    @JsonProperty("downloaded_bytes")
    public String getDownloadedBytes() {
        return this.downloadedBytes;
    }

    @JsonProperty("_total_bytes_str")
    public void setTotalBytes(String totalBytes) {
        this.totalBytes = totalBytes.trim();
    }

    @JsonProperty("total_bytes")
    public String getTotalBytes() {
        return this.totalBytes;
    }

    @JsonProperty("_total_bytes_estimate_str")
    public void setEstimatedBytes(String estimatedBytes) {
        this.estimatedBytes = estimatedBytes.trim();
    }

    @JsonProperty("estimated_bytes")
    public String getEstimatedBytes() {
        return this.estimatedBytes;
    }

    @JsonProperty("fragment_index")
    public void setFragment(Integer fragment_index) {
        this.fragment = fragment_index;
    }

    @JsonProperty("fragment")
    public Integer getFragment() {
        return this.fragment;
    }

    @JsonProperty("_speed_str")
    public void setSpeed(String speed) {
        this.speed = speed.trim();
    }

    @JsonProperty("speed") 
    public String getSpeed() {
        return this.speed;
    }

    @JsonProperty("_elapsed_str") 
    public void setElapsed(String elapsed) {
        this.elapsed = elapsed.trim();
    }

    @JsonProperty("elapsed")
    public String getElapsed() {
        return this.elapsed;
    }

    @JsonProperty("_eta_str") 
    public void setEta(String eta) {
        this.eta = eta.trim();
    }

    @JsonProperty("eta")
    public String getEta() {
        return this.eta;
    }
}
