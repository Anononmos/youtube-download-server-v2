package com.example.YouTubeDL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class VideoController {

    @Autowired
    VideoService videoService;

    @Value("${application.resolutions}")
    private String resolutions;
    
    @PostMapping(value = "/offload", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> offload(@Valid @RequestBody DownloadRequest body) {

        VideoParams params = body.toVideoParams();
        

        return null;
    }
    

    @GetMapping(value = "/")
    public String index() {
        return String.format("[%s]", resolutions);
    }
    
    
    @GetMapping(value = "/download")
    public String getDownload( @RequestParam(value = "url", required = true) String url ) {
        return new String("Hello World");
    }
    
}
