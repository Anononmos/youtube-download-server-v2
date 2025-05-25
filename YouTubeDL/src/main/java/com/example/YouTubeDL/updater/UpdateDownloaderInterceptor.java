package com.example.YouTubeDL.updater;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.web.servlet.HandlerInterceptor;

import com.example.YouTubeDL.Shell;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UpdateDownloaderInterceptor implements HandlerInterceptor {

    private LastUpdateRepository lastUpdateRepository;

    public UpdateDownloaderInterceptor(LastUpdateRepository lastUpdateRepository) {
        this.lastUpdateRepository = lastUpdateRepository;
    }
    
    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

        if ( !lastUpdateRepository.canUpdate() ) {
            System.out.println("Did not update yt-dlp.");

            return true;
        }

        try {
            Shell.updateYTdlp();
        }
        catch (IOException e) {
            System.out.println("Failed to update yt-dlp.");

            e.printStackTrace();

            return false;
        }
        
        // Updates to today
        lastUpdateRepository.updateDate();

        System.out.println("Did update yt-dlp.");

        return true;
    }
}
