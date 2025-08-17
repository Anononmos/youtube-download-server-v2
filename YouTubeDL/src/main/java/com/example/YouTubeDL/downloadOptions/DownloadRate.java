package com.example.YouTubeDL.downloadOptions;

public enum DownloadRate {
    SLOW ("1M"), MEDIUM ("2M"), FAST("5M");

    public final String speed;

    DownloadRate(String speed) {
        this.speed = speed;
    }

    public String toString() {
        return speed;
    }
}
