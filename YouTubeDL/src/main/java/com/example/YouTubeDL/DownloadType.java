package com.example.YouTubeDL;

public enum DownloadType {
    Video ("video"), 
    Audio ("audio");

    public final String label;

    private DownloadType(String label) {
        this.label = (label != null) ? label : "video";
    }

    public static DownloadType valueOfLabel(String label) {

        for (DownloadType type : values()) {

            if ( type.label.equals(label) ) {
                return type;
            }
        }

        return null;
    }
}
