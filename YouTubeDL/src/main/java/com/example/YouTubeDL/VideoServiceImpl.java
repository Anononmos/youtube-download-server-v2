package com.example.YouTubeDL;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${application.download.directory}")
    private String directory;

    @Override
    public void download(VideoParams params) {
        DataFormatter formatter = new VideoDataFormatter();

        DownloadType type = params.type();
        
        switch (type) {
            case Audio:
                // TODO: Use two threads, one for download and one for info

                break;
        
            case Video:

                break;
        }

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'downloadVideo'");
    }
        
}
