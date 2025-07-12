package com.example.YouTubeDL;

public class ProgressDataFormatter implements DataFormatter {

    private String[] fields = {
        "status", 
        "_percent_str",
        "_downloaded_bytes_str",
        "_total_bytes_str", 
        "_total_bytes_estimate_str",
        "fragment_index", 
        "fragment_count",  
        "_speed_str", 
        "_elapsed_str",   
        "_eta_str"
    };

    @Override
    public String getFormat() {
        return String.format("%%(progress.{%s})j", String.join(",", fields));
    }
}
