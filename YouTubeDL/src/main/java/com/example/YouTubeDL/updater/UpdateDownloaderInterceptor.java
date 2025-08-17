package com.example.YouTubeDL.updater;

import org.springframework.web.servlet.HandlerInterceptor;

import com.example.YouTubeDL.exceptions.DownloaderExceptions.UpdaterException;
import com.example.YouTubeDL.shells.Updater;

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
            Updater updater = new Updater();

            updater.updateYTdlp();
        }
        catch (UpdaterException e) {
            e.printStackTrace();

            return false;
        }
        
        // Updates to today
        lastUpdateRepository.updateDate();

        System.out.println("Did update yt-dlp.");

        return true;
    }
}
