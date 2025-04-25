package com.example.YouTubeDL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class VideoController {

    @Value("${application.resolutions}")
    private String resolutions;
    
    @PostMapping(value = "/offload", consumes = "application/json", produces = "application/json")
    public @ResponseBody String offload(@Valid @RequestBody DownloadRequest body) {

        return "Hello";
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
