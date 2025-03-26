package com.example.YouTubeDL;

import java.io.IOException;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UpdateDownloaderInterceptor implements HandlerInterceptor {
    
    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

        try {
            Shell.updateYTdlp();
        }
        catch (IOException e) {
            System.out.println("Failed to update yt-dlp.");

            e.printStackTrace();
        }

        return true;
    }
}
