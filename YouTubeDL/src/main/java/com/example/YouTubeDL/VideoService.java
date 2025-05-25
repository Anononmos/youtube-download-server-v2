package com.example.YouTubeDL;

import java.util.Collection;

public interface VideoService {

    /**
     * 
     * @param params
     * @param format
     * @return
     */
    // public abstract String getVideoInfo(VideoParams params, String format);

    /**
     * 
     * @param type
     * @param url
     * @param res
     */
    public abstract void download(VideoParams params);
}
